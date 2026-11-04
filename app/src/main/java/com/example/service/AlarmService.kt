package com.example.service

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.media.AudioAttributes
import android.os.Build
import android.os.IBinder
import android.os.PowerManager
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import android.provider.Settings
import androidx.core.app.NotificationCompat
import com.example.R
import com.example.core.audio.ToneGenerator
import com.example.feature.alarm.AlarmRingingActivity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class AlarmService : Service() {

    companion object {
        const val CHANNEL_ID = "lifeos_alarm_channel"
        const val NOTIFICATION_ID = 1001
        const val ACTION_STOP_ALARM = "com.example.lifeos.ACTION_STOP_ALARM"
        const val PREFS_ALARM_STATE = "lifeos_alarm_state"
        const val KEY_IS_RINGING = "is_ringing"
        const val KEY_CURRENT_ALARM_ID = "current_alarm_id"
    }

    private var wakeLock: PowerManager.WakeLock? = null
    private var vibrator: Vibrator? = null
    private val serviceScope = CoroutineScope(Dispatchers.Main + Job())

    override fun onCreate() {
        super.onCreate()
        createNotificationChannel()

        val powerManager = getSystemService(Context.POWER_SERVICE) as PowerManager
        wakeLock = powerManager.newWakeLock(
            PowerManager.PARTIAL_WAKE_LOCK,
            "lifeos:AlarmServiceWakeLock"
        ).apply {
            setReferenceCounted(false)
            acquire(10 * 60 * 1000L) // 10 minutes max safe timeout
        }

        vibrator = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val vibratorManager = getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as VibratorManager
            vibratorManager.defaultVibrator
        } else {
            @Suppress("DEPRECATION")
            getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
        }
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        if (intent?.action == ACTION_STOP_ALARM) {
            stopAlarmInternal()
            stopForeground(STOP_FOREGROUND_REMOVE)
            stopSelf()
            return START_NOT_STICKY
        }

        val alarmTitle = intent?.getStringExtra("alarm_title") ?: "Báo thức LifeOS"
        val alarmId = intent?.getLongExtra("alarm_id", 0L) ?: 0L
        val missionType = intent?.getStringExtra("mission_type") ?: "MATH"
        val ringtoneName = intent?.getStringExtra("ringtone_name") ?: "Tiếng chuông bình minh"

        // Mark ringing state in persistent storage
        val prefs = getSharedPreferences(PREFS_ALARM_STATE, Context.MODE_PRIVATE)
        prefs.edit()
            .putBoolean(KEY_IS_RINGING, true)
            .putLong(KEY_CURRENT_ALARM_ID, alarmId)
            .putString("alarm_title", alarmTitle)
            .putString("mission_type", missionType)
            .putString("ringtone_name", ringtoneName)
            .putLong("ringing_since", System.currentTimeMillis())
            .apply()

        // 1. Prepare Full-Screen Intent targeting AlarmRingingActivity
        val ringingIntent = Intent(this, AlarmRingingActivity::class.java).apply {
            this.flags = Intent.FLAG_ACTIVITY_NEW_TASK or
                    Intent.FLAG_ACTIVITY_CLEAR_TOP or
                    Intent.FLAG_ACTIVITY_NO_USER_ACTION
            putExtra("alarm_id", alarmId)
            putExtra("alarm_title", alarmTitle)
            putExtra("mission_type", missionType)
        }

        val pendingRingingIntent = PendingIntent.getActivity(
            this,
            alarmId.toInt(),
            ringingIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        // 2. Build ongoing, non-dismissible notification with fullScreenIntent (cannot be swiped away!)
        val notification = NotificationCompat.Builder(this, CHANNEL_ID)
            .setSmallIcon(R.mipmap.ic_launcher)
            .setContentTitle("⏰ $alarmTitle")
            .setContentText("Chuông đang reo! Chạm để giải nhiệm vụ tắt chuông.")
            .setPriority(NotificationCompat.PRIORITY_MAX)
            .setCategory(NotificationCompat.CATEGORY_ALARM)
            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
            .setFullScreenIntent(pendingRingingIntent, true)
            .setContentIntent(pendingRingingIntent)
            .setOngoing(true)
            .setAutoCancel(false)
            .addAction(
                R.mipmap.ic_launcher,
                "🚨 GIẢI NHIỆM VỤ ĐỂ TẮT",
                pendingRingingIntent
            )
            .build()
        notification.flags = notification.flags or Notification.FLAG_ONGOING_EVENT or Notification.FLAG_NO_CLEAR

        startForeground(NOTIFICATION_ID, notification)

        // 3. Try to launch AlarmRingingActivity directly if overlay permission is granted or device is active
        try {
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q || Settings.canDrawOverlays(this)) {
                startActivity(ringingIntent)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

        // 4. Start audio with selected ringtone and intermittent vibration
        ToneGenerator.playAlarmWithRingtone(this, ringtoneName, onLoop = true)
        startVibration()

        return START_STICKY
    }

    private fun startVibration() {
        vibrator?.let { vib ->
            if (vib.hasVibrator()) {
                val pattern = longArrayOf(0, 800, 400, 800, 400)
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    val effect = VibrationEffect.createWaveform(pattern, 0)
                    val attributes = AudioAttributes.Builder()
                        .setUsage(AudioAttributes.USAGE_ALARM)
                        .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                        .build()
                    vib.vibrate(effect, attributes)
                } else {
                    @Suppress("DEPRECATION")
                    vib.vibrate(pattern, 0)
                }
            }
        }
    }

    private fun stopAlarmInternal() {
        ToneGenerator.stop()
        vibrator?.cancel()
        val prefs = getSharedPreferences(PREFS_ALARM_STATE, Context.MODE_PRIVATE)
        prefs.edit().putBoolean(KEY_IS_RINGING, false).apply()
        wakeLock?.let {
            if (it.isHeld) {
                try {
                    it.release()
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }
    }

    override fun onDestroy() {
        stopAlarmInternal()
        super.onDestroy()
    }

    override fun onBind(intent: Intent?): IBinder? = null

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                "Chuông báo thức thức dậy",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Kênh phát chuông báo thức thông minh LifeOS ưu tiên cao nhất"
                enableVibration(true)
                vibrationPattern = longArrayOf(0, 800, 400, 800, 400)
                lockscreenVisibility = Notification.VISIBILITY_PUBLIC
                setBypassDnd(true)
            }
            val manager = getSystemService(NotificationManager::class.java)
            manager.createNotificationChannel(channel)
        }
    }
}
