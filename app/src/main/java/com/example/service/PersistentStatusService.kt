package com.example.service

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.IBinder
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import com.example.MainActivity
import com.example.R
import com.example.data.AppDatabase
import com.example.data.preferences.UserPreferencesRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class PersistentStatusService : Service() {

    companion object {
        const val CHANNEL_ID = "lifeos_persistent_status_channel"
        const val NOTIFICATION_ID = 9001
        const val ACTION_REFRESH = "com.example.lifeos.ACTION_REFRESH_STATUS"

        fun startService(context: Context) {
            val intent = Intent(context, PersistentStatusService::class.java)
            try {
                ContextCompat.startForegroundService(context, intent)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

        fun refresh(context: Context) {
            val intent = Intent(context, PersistentStatusService::class.java).apply {
                action = ACTION_REFRESH
            }
            try {
                ContextCompat.startForegroundService(context, intent)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    private val serviceScope = CoroutineScope(Dispatchers.IO)

    override fun onCreate() {
        super.onCreate()
        createNotificationChannel()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        updateNotificationContent()
        return START_STICKY
    }

    private fun updateNotificationContent() {
        serviceScope.launch {
            try {
                val db = AppDatabase.getDatabase(applicationContext)
                val userPrefsRepo = UserPreferencesRepository(applicationContext)
                val prefs = userPrefsRepo.userPreferencesFlow.first()

                val enabledAlarms = db.alarmDao().getEnabledAlarms().first()
                val nextAlarm = enabledAlarms.firstOrNull()
                val nextAlarmStr = if (nextAlarm != null) {
                    String.format("%02d:%02d", nextAlarm.hour, nextAlarm.minute)
                } else {
                    "Chưa đặt"
                }

                val allTasks = db.taskDao().getAllTasks().first()
                val remainingTasks = allTasks.count { !it.isCompleted }

                val title = "LifeOS • Kỷ Luật & Năng Lượng"
                val bodyText = "⏰ Báo thức: $nextAlarmStr • 🔥 Chuỗi: ${prefs.activeStreakDays} ngày • 📋 Còn $remainingTasks nhiệm vụ"

                // Open Main App Intent
                val openAppIntent = Intent(applicationContext, MainActivity::class.java).apply {
                    this.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
                }
                val pendingOpenApp = PendingIntent.getActivity(
                    applicationContext,
                    0,
                    openAppIntent,
                    PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
                )

                // Quick Check-in Intent
                val checkinIntent = Intent(applicationContext, ReminderActionReceiver::class.java).apply {
                    action = ReminderActionReceiver.ACTION_CHECKIN_STREAK
                }
                val pendingCheckin = PendingIntent.getBroadcast(
                    applicationContext,
                    9002,
                    checkinIntent,
                    PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
                )

                val notification = NotificationCompat.Builder(applicationContext, CHANNEL_ID)
                    .setSmallIcon(R.mipmap.ic_launcher)
                    .setContentTitle(title)
                    .setContentText(bodyText)
                    .setStyle(NotificationCompat.BigTextStyle().bigText(
                        "$bodyText\n🌟 Đạt mục tiêu mỗi ngày: Cấp độ ${prefs.level} • ${prefs.coins} Xu"
                    ))
                    .setPriority(NotificationCompat.PRIORITY_LOW)
                    .setCategory(NotificationCompat.CATEGORY_SERVICE)
                    .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                    .setContentIntent(pendingOpenApp)
                    .setOngoing(true)
                    .setAutoCancel(false)
                    .addAction(
                        R.mipmap.ic_launcher,
                        "🔥 ĐIỂM DANH",
                        pendingCheckin
                    )
                    .addAction(
                        R.mipmap.ic_launcher,
                        "MỞ LIFEOS",
                        pendingOpenApp
                    )
                    .build().apply {
                        flags = flags or Notification.FLAG_ONGOING_EVENT or Notification.FLAG_NO_CLEAR
                    }

                startForeground(NOTIFICATION_ID, notification)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    override fun onBind(intent: Intent?): IBinder? = null

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                "Bảng thông tin cố định LifeOS",
                NotificationManager.IMPORTANCE_LOW
            ).apply {
                description = "Hiển thị thông tin báo thức, chuỗi kỷ luật và nhiệm vụ quan trọng không thể bỏ lỡ"
                setShowBadge(false)
                lockscreenVisibility = Notification.VISIBILITY_PUBLIC
            }
            val manager = getSystemService(NotificationManager::class.java)
            manager.createNotificationChannel(channel)
        }
    }
}
