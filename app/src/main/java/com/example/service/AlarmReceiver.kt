package com.example.service

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.content.ContextCompat
import com.example.MainActivity
import com.example.data.AppDatabase
import com.example.data.entity.AlarmEntity
import com.example.feature.alarm.AlarmRingingActivity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.util.Calendar

class AlarmReceiver : BroadcastReceiver() {

    companion object {
        const val ACTION_ALARM_TRIGGER = "com.example.lifeos.ACTION_ALARM_TRIGGER"
        const val ACTION_BEDTIME_REMINDER = "com.example.lifeos.ACTION_BEDTIME_REMINDER"

        fun scheduleAlarm(context: Context, alarm: AlarmEntity) {
            val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

            // Intent to trigger alarm when time arrives
            val intent = Intent(context, AlarmReceiver::class.java).apply {
                action = ACTION_ALARM_TRIGGER
                putExtra("alarm_id", alarm.id)
                putExtra("alarm_title", alarm.title)
                putExtra("mission_type", alarm.missionType)
                putExtra("ringtone_name", alarm.ringtoneName)
            }

            val operationPendingIntent = PendingIntent.getBroadcast(
                context,
                alarm.id.toInt(),
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )

            // Intent to show when user taps the alarm icon in status bar / lockscreen
            val showIntent = Intent(context, AlarmRingingActivity::class.java).apply {
                putExtra("alarm_id", alarm.id)
                putExtra("alarm_title", alarm.title)
                putExtra("mission_type", alarm.missionType)
            }
            val showPendingIntent = PendingIntent.getActivity(
                context,
                (alarm.id + 10000).toInt(),
                showIntent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )

            val calendar = Calendar.getInstance().apply {
                set(Calendar.HOUR_OF_DAY, alarm.hour)
                set(Calendar.MINUTE, alarm.minute)
                set(Calendar.SECOND, 0)
                set(Calendar.MILLISECOND, 0)
                if (timeInMillis <= System.currentTimeMillis()) {
                    add(Calendar.DAY_OF_YEAR, 1)
                }
            }

            // Android 12+ check for exact alarm capability
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                if (!alarmManager.canScheduleExactAlarms()) {
                    // Fallback to standard window alarm if permission not yet granted
                    alarmManager.set(
                        AlarmManager.RTC_WAKEUP,
                        calendar.timeInMillis,
                        operationPendingIntent
                    )
                    return
                }
            }

            // CRITICAL: setAlarmClock provides highest OS priority, pierces Doze, and displays clock icon on lockscreen
            val clockInfo = AlarmManager.AlarmClockInfo(calendar.timeInMillis, showPendingIntent)
            alarmManager.setAlarmClock(clockInfo, operationPendingIntent)
        }

        fun scheduleTestAlarm(context: Context, secondsFromNow: Int = 60) {
            val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

            val intent = Intent(context, AlarmReceiver::class.java).apply {
                action = ACTION_ALARM_TRIGGER
                putExtra("alarm_id", 99999L)
                putExtra("alarm_title", "Báo thức thử nghiệm 1 phút")
                putExtra("mission_type", "MATH")
            }

            val operationPendingIntent = PendingIntent.getBroadcast(
                context,
                99999,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )

            val showIntent = Intent(context, AlarmRingingActivity::class.java).apply {
                putExtra("alarm_id", 99999L)
                putExtra("alarm_title", "Báo thức thử nghiệm 1 phút")
                putExtra("mission_type", "MATH")
            }
            val showPendingIntent = PendingIntent.getActivity(
                context,
                99999,
                showIntent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )

            val triggerTime = System.currentTimeMillis() + (secondsFromNow * 1000L)

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S && !alarmManager.canScheduleExactAlarms()) {
                alarmManager.set(AlarmManager.RTC_WAKEUP, triggerTime, operationPendingIntent)
            } else {
                val clockInfo = AlarmManager.AlarmClockInfo(triggerTime, showPendingIntent)
                alarmManager.setAlarmClock(clockInfo, operationPendingIntent)
            }
        }

        fun cancelAlarm(context: Context, alarmId: Long) {
            val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
            val intent = Intent(context, AlarmReceiver::class.java).apply {
                action = ACTION_ALARM_TRIGGER
            }
            val pendingIntent = PendingIntent.getBroadcast(
                context,
                alarmId.toInt(),
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
            alarmManager.cancel(pendingIntent)
        }
    }

    override fun onReceive(context: Context, intent: Intent) {
        val alarmId = intent.getLongExtra("alarm_id", 0L)
        val alarmTitle = intent.getStringExtra("alarm_title") ?: "Báo thức LifeOS"
        val missionType = intent.getStringExtra("mission_type") ?: "MATH"
        val ringtoneName = intent.getStringExtra("ringtone_name") ?: "Tiếng chuông bình minh"

        val serviceIntent = Intent(context, AlarmService::class.java).apply {
            putExtra("alarm_id", alarmId)
            putExtra("alarm_title", alarmTitle)
            putExtra("mission_type", missionType)
            putExtra("ringtone_name", ringtoneName)
        }

        ContextCompat.startForegroundService(context, serviceIntent)
    }
}

class BootReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val action = intent.action ?: return
        if (action == Intent.ACTION_BOOT_COMPLETED ||
            action == Intent.ACTION_LOCKED_BOOT_COMPLETED ||
            action == Intent.ACTION_MY_PACKAGE_REPLACED ||
            action == Intent.ACTION_TIME_CHANGED ||
            action == Intent.ACTION_TIMEZONE_CHANGED
        ) {
            val scope = CoroutineScope(Dispatchers.IO)
            scope.launch {
                try {
                    val db = AppDatabase.getDatabase(context)
                    val enabledAlarms = db.alarmDao().getEnabledAlarms().first()
                    for (alarm in enabledAlarms) {
                        AlarmReceiver.scheduleAlarm(context, alarm)
                    }

                    // Check if an alarm was ringing or missed recently
                    val prefs = context.getSharedPreferences(AlarmService.PREFS_ALARM_STATE, Context.MODE_PRIVATE)
                    val wasRinging = prefs.getBoolean(AlarmService.KEY_IS_RINGING, false)
                    val ringingSince = prefs.getLong("ringing_since", 0L)
                    val now = System.currentTimeMillis()

                    if (wasRinging && (now - ringingSince) < 15 * 60 * 1000L) {
                        // Restore ringing if within 15 minutes
                        val alarmId = prefs.getLong(AlarmService.KEY_CURRENT_ALARM_ID, 0L)
                        val serviceIntent = Intent(context, AlarmService::class.java).apply {
                            putExtra("alarm_id", alarmId)
                            putExtra("alarm_title", "Báo thức LifeOS (Tiếp tục)")
                            putExtra("mission_type", "MATH")
                        }
                        ContextCompat.startForegroundService(context, serviceIntent)
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }
    }
}
