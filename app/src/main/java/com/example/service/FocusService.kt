package com.example.service

import android.app.AppOpsManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.app.usage.UsageStatsManager
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.IBinder
import android.os.SystemClock
import androidx.core.app.NotificationCompat
import com.example.MainActivity
import com.example.R
import com.example.core.audio.ToneGenerator
import com.example.feature.focus.BlockOverlayActivity
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class FocusService : Service() {
    companion object {
        const val CHANNEL_ID = "lifeos_focus_channel"
        const val NOTIFICATION_ID = 2002
        const val ACTION_START = "ACTION_START_FOCUS"
        const val ACTION_STOP = "ACTION_STOP_FOCUS"
        const val PREFS_FOCUS = "lifeos_focus_state"

        private val _remainingSeconds = MutableStateFlow(25 * 60L)
        val remainingSeconds = _remainingSeconds.asStateFlow()

        private val _isFocusActive = MutableStateFlow(false)
        val isFocusActive = _isFocusActive.asStateFlow()

        private val _blockedAttempts = MutableStateFlow(0)
        val blockedAttempts = _blockedAttempts.asStateFlow()

        private val _lastBlockedApp = MutableStateFlow("")
        val lastBlockedApp = _lastBlockedApp.asStateFlow()

        val DEFAULT_BLOCKED_PACKAGES = setOf(
            "com.zhiliaoapp.musically",       // TikTok Global
            "com.ss.android.ugc.trill",       // TikTok Asian
            "com.facebook.katana",            // Facebook
            "com.facebook.lite",              // Facebook Lite
            "com.facebook.orca",              // Messenger
            "com.instagram.android",          // Instagram
            "com.google.android.youtube",     // YouTube
            "com.twitter.android",            // X / Twitter
            "com.snapchat.android",           // Snapchat
            "com.shopee.vn",                  // Shopee
            "com.reddit.frontpage",           // Reddit
            "com.netflix.mediaclient",        // Netflix
            "com.garena.game.kgvn",           // Liên Quân Mobile
            "com.dts.freefireth",             // Free Fire
            "com.vng.pubgmobile"              // PUBG Mobile
        )

        private var appPreferences: android.content.SharedPreferences? = null

        fun initPreferences(context: Context) {
            if (appPreferences == null) {
                appPreferences = context.getSharedPreferences(PREFS_FOCUS, Context.MODE_PRIVATE)
            }
        }

        fun getRemainingSecondsDirect(): Long {
            val prefs = appPreferences ?: return _remainingSeconds.value
            val isRunning = prefs.getBoolean("is_focus_active", false)
            if (!isRunning) return 0L

            val startElapsed = prefs.getLong("start_elapsed_realtime", 0L)
            val targetSec = prefs.getInt("target_seconds", 25 * 60)
            val currentElapsed = (SystemClock.elapsedRealtime() - startElapsed) / 1000L
            return maxOf(0L, targetSec - currentElapsed)
        }

        fun recordBlockedAttempt(appName: String = "TikTok") {
            _blockedAttempts.value = _blockedAttempts.value + 1
            _lastBlockedApp.value = appName
        }

        fun hasUsageStatsPermission(context: Context): Boolean {
            return try {
                val appOps = context.getSystemService(Context.APP_OPS_SERVICE) as AppOpsManager
                val mode = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    appOps.unsafeCheckOpNoThrow(
                        AppOpsManager.OPSTR_GET_USAGE_STATS,
                        android.os.Process.myUid(),
                        context.packageName
                    )
                } else {
                    @Suppress("DEPRECATION")
                    appOps.checkOpNoThrow(
                        AppOpsManager.OPSTR_GET_USAGE_STATS,
                        android.os.Process.myUid(),
                        context.packageName
                    )
                }
                mode == AppOpsManager.MODE_ALLOWED
            } catch (_: Exception) {
                false
            }
        }
    }

    private var timerJob: Job? = null
    private var monitorJob: Job? = null
    private val scope = CoroutineScope(Dispatchers.Default)

    override fun onCreate() {
        super.onCreate()
        initPreferences(applicationContext)
        createNotificationChannel()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        when (intent?.action) {
            ACTION_STOP -> {
                stopTimer()
                stopForeground(STOP_FOREGROUND_REMOVE)
                stopSelf()
                return START_NOT_STICKY
            }
            ACTION_START -> {
                val durationMinutes = intent.getIntExtra("duration_minutes", 25)
                startTimer(durationMinutes)
            }
        }
        return START_STICKY
    }

    private fun startTimer(durationMinutes: Int) {
        timerJob?.cancel()
        monitorJob?.cancel()

        val targetSeconds = durationMinutes * 60
        _remainingSeconds.value = targetSeconds.toLong()
        _isFocusActive.value = true
        _blockedAttempts.value = 0

        // Persist session with monotonic elapsedRealtime
        val prefs = getSharedPreferences(PREFS_FOCUS, Context.MODE_PRIVATE)
        prefs.edit()
            .putBoolean("is_focus_active", true)
            .putLong("start_elapsed_realtime", SystemClock.elapsedRealtime())
            .putInt("target_seconds", targetSeconds)
            .putInt("blocked_count", 0)
            .apply()

        updateNotification(targetSeconds.toLong())

        // 1. Timer countdown job using monotonic clock
        timerJob = scope.launch {
            while (isActive && _isFocusActive.value) {
                delay(1000)
                val remaining = getRemainingSecondsDirect()
                _remainingSeconds.value = remaining

                if (remaining <= 0) {
                    _isFocusActive.value = false
                    prefs.edit().putBoolean("is_focus_active", false).apply()
                    ToneGenerator.playSuccessChime()
                    stopForeground(STOP_FOREGROUND_REMOVE)
                    stopSelf()
                    break
                }

                if (remaining % 5 == 0L) {
                    updateNotification(remaining)
                }
            }
        }

        // 2. Dual Backup Background App Monitor (via UsageStatsManager if Accessibility is not active)
        monitorJob = scope.launch {
            var lastBlockedTime = 0L
            while (isActive && _isFocusActive.value) {
                delay(1200)

                // If accessibility service is already running, let it handle instant window change
                if (BlockerAccessibilityService.isServiceRunning) {
                    continue
                }

                try {
                    val usageStatsManager = getSystemService(Context.USAGE_STATS_SERVICE) as? UsageStatsManager
                    val endTime = System.currentTimeMillis()
                    val beginTime = endTime - 4000

                    val stats = usageStatsManager?.queryUsageStats(
                        UsageStatsManager.INTERVAL_BEST,
                        beginTime,
                        endTime
                    )

                    val topApp = stats?.maxByOrNull { it.lastTimeUsed }?.packageName
                    val blockedString = prefs.getString("blocked_packages", "") ?: ""
                    val blockedSet = if (blockedString.isNotBlank()) blockedString.split(",").toSet() else DEFAULT_BLOCKED_PACKAGES

                    if (topApp != null && blockedSet.contains(topApp) && topApp != packageName) {
                        val now = System.currentTimeMillis()
                        if (now - lastBlockedTime > 2500) {
                            lastBlockedTime = now
                            val readableName = when {
                                topApp.contains("musically") || topApp.contains("trill") -> "TikTok"
                                topApp.contains("facebook") -> "Facebook"
                                topApp.contains("instagram") -> "Instagram"
                                topApp.contains("youtube") -> "YouTube"
                                topApp.contains("twitter") -> "X / Twitter"
                                topApp.contains("shopee") -> "Shopee"
                                else -> "Ứng dụng xao nhãng"
                            }
                            recordBlockedAttempt(readableName)
                            ToneGenerator.playAlarm(onLoop = false)

                            val overlayIntent = Intent(applicationContext, BlockOverlayActivity::class.java).apply {
                                flags = Intent.FLAG_ACTIVITY_NEW_TASK or
                                        Intent.FLAG_ACTIVITY_CLEAR_TOP or
                                        Intent.FLAG_ACTIVITY_SINGLE_TOP
                                putExtra("blocked_app_name", readableName)
                            }
                            startActivity(overlayIntent)
                        }
                    }
                } catch (_: Exception) {
                }
            }
        }
    }

    private fun stopTimer() {
        timerJob?.cancel()
        timerJob = null
        monitorJob?.cancel()
        monitorJob = null
        _isFocusActive.value = false

        val prefs = getSharedPreferences(PREFS_FOCUS, Context.MODE_PRIVATE)
        prefs.edit().putBoolean("is_focus_active", false).apply()
    }

    private fun updateNotification(secondsLeft: Long) {
        val minutes = secondsLeft / 60
        val seconds = secondsLeft % 60
        val timeString = String.format("%02d:%02d", minutes, seconds)

        val openIntent = Intent(this, MainActivity::class.java).apply {
            putExtra("navigate_to", "focus_mode")
            flags = Intent.FLAG_ACTIVITY_SINGLE_TOP
        }
        val pendingIntent = PendingIntent.getActivity(
            this,
            1,
            openIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val notification = NotificationCompat.Builder(this, CHANNEL_ID)
            .setSmallIcon(R.mipmap.ic_launcher)
            .setContentTitle("🎯 Đang trong chế độ Tập trung LifeOS")
            .setContentText("Còn lại: $timeString | Đã chặn: ${_blockedAttempts.value} lần cám dỗ")
            .setOngoing(true)
            .setContentIntent(pendingIntent)
            .setOnlyAlertOnce(true)
            .build()

        startForeground(NOTIFICATION_ID, notification)
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                "Chế độ tập trung LifeOS",
                NotificationManager.IMPORTANCE_LOW
            )
            val manager = getSystemService(NotificationManager::class.java)
            manager.createNotificationChannel(channel)
        }
    }

    override fun onDestroy() {
        stopTimer()
        super.onDestroy()
    }

    override fun onBind(intent: Intent?): IBinder? = null
}
