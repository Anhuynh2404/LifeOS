package com.example.service

import android.app.AlarmManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import com.example.MainActivity
import com.example.R
import com.example.data.AppDatabase
import com.example.data.entity.ReminderEntity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.Calendar

object ReminderScheduler {

    const val CHANNEL_SCHEDULE = "lifeos_schedule_channel"
    const val CHANNEL_FINANCE = "lifeos_finance_channel"
    const val CHANNEL_FOCUS = "lifeos_focus_channel"
    const val CHANNEL_IMPULSE = "lifeos_impulse_channel"
    const val CHANNEL_CHALLENGE = "lifeos_challenge_channel"

    fun initChannels(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val manager = context.getSystemService(NotificationManager::class.java)

            val channels = listOf(
                NotificationChannel(
                    CHANNEL_SCHEDULE,
                    "Lịch trình & Thói quen",
                    NotificationManager.IMPORTANCE_HIGH
                ).apply {
                    description = "Nhắc nhở sự kiện, thói quen và nhiệm vụ hàng ngày"
                    enableVibration(true)
                },
                NotificationChannel(
                    CHANNEL_FINANCE,
                    "Nhắc nợ & Tài chính",
                    NotificationManager.IMPORTANCE_HIGH
                ).apply {
                    description = "Cảnh báo kỳ hạn trả nợ ngân hàng và quản lý tài chính"
                    enableVibration(true)
                },
                NotificationChannel(
                    CHANNEL_FOCUS,
                    "Focus & Pomodoro",
                    NotificationManager.IMPORTANCE_DEFAULT
                ).apply {
                    description = "Thông báo kết thúc phiên tập trung và giờ nghỉ"
                },
                NotificationChannel(
                    CHANNEL_IMPULSE,
                    "Hộp hạ nhiệt mua sắm",
                    NotificationManager.IMPORTANCE_DEFAULT
                ).apply {
                    description = "Nhắc nhở kiểm tra lại mong muốn mua sắm sau 48h - 72h"
                },
                NotificationChannel(
                    CHANNEL_CHALLENGE,
                    "Kỷ luật & Thử thách",
                    NotificationManager.IMPORTANCE_HIGH
                ).apply {
                    description = "Cảnh báo duy trì chuỗi Streak và thử thách kỷ luật"
                    enableVibration(true)
                }
            )

            channels.forEach { manager.createNotificationChannel(it) }
        }
    }

    fun scheduleReminder(context: Context, reminder: ReminderEntity) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

        val intent = Intent(context, ReminderActionReceiver::class.java).apply {
            action = ReminderActionReceiver.ACTION_REMINDER_TRIGGER
            putExtra("reminder_id", reminder.id)
            putExtra("reminder_type", reminder.type)
            putExtra("target_id", reminder.targetId)
            putExtra("title", reminder.title)
            putExtra("message", reminder.message)
            putExtra("channel_type", reminder.channelType)
            putExtra("extra_data", reminder.extraData)
        }

        val pendingIntent = PendingIntent.getBroadcast(
            context,
            reminder.id.toInt(),
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S && !alarmManager.canScheduleExactAlarms()) {
            alarmManager.set(AlarmManager.RTC_WAKEUP, reminder.triggerTime, pendingIntent)
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, reminder.triggerTime, pendingIntent)
        } else {
            alarmManager.setExact(AlarmManager.RTC_WAKEUP, reminder.triggerTime, pendingIntent)
        }
    }

    fun scheduleDebtPaymentReminder(
        context: Context,
        loanId: Long,
        bankName: String,
        amount: Long,
        dueDay: Int,
        daysBefore: Int
    ) {
        val scope = CoroutineScope(Dispatchers.IO)
        scope.launch {
            val calendar = Calendar.getInstance().apply {
                set(Calendar.DAY_OF_MONTH, dueDay)
                set(Calendar.HOUR_OF_DAY, 9)
                set(Calendar.MINUTE, 0)
                set(Calendar.SECOND, 0)
                add(Calendar.DAY_OF_MONTH, -daysBefore)
                if (timeInMillis <= System.currentTimeMillis()) {
                    add(Calendar.MONTH, 1)
                }
            }

            val formattedAmount = java.text.NumberFormat.getCurrencyInstance(java.util.Locale("vi", "VN")).format(amount)
            val reminder = ReminderEntity(
                type = "DEBT",
                targetId = loanId,
                title = "💳 Nhắc hẹn trả nợ: $bankName",
                message = "Đến kỳ hạn thanh toán khoản vay $formattedAmount. Vui lòng kiểm tra tài khoản!",
                triggerTime = calendar.timeInMillis,
                channelType = "FINANCE",
                extraData = "$amount"
            )

            val db = AppDatabase.getDatabase(context)
            val id = db.reminderDao().insertReminder(reminder)
            scheduleReminder(context, reminder.copy(id = id))
        }
    }

    fun scheduleImpulseReminder(
        context: Context,
        itemId: Long,
        itemName: String,
        price: Long,
        coolingHours: Int = 48
    ) {
        val scope = CoroutineScope(Dispatchers.IO)
        scope.launch {
            val triggerTime = System.currentTimeMillis() + (coolingHours * 3600 * 1000L)
            val formattedPrice = java.text.NumberFormat.getCurrencyInstance(java.util.Locale("vi", "VN")).format(price)
            val reminder = ReminderEntity(
                type = "IMPULSE",
                targetId = itemId,
                title = "🧊 Hộp hạ nhiệt mua sắm",
                message = "Đã qua $coolingHours giờ hạ nhiệt! Bạn còn thực sự muốn mua món '$itemName' ($formattedPrice) không?",
                triggerTime = triggerTime,
                channelType = "IMPULSE",
                extraData = "$price"
            )

            val db = AppDatabase.getDatabase(context)
            val id = db.reminderDao().insertReminder(reminder)
            scheduleReminder(context, reminder.copy(id = id))
        }
    }

    fun triggerImmediateNotification(context: Context, reminder: ReminderEntity) {
        val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        initChannels(context)

        val channelId = when (reminder.channelType) {
            "FINANCE" -> CHANNEL_FINANCE
            "FOCUS" -> CHANNEL_FOCUS
            "IMPULSE" -> CHANNEL_IMPULSE
            "CHALLENGE" -> CHANNEL_CHALLENGE
            else -> CHANNEL_SCHEDULE
        }

        val openAppIntent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
        }
        val openAppPendingIntent = PendingIntent.getActivity(
            context,
            reminder.id.toInt(),
            openAppIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val builder = NotificationCompat.Builder(context, channelId)
            .setSmallIcon(R.mipmap.ic_launcher)
            .setContentTitle(reminder.title)
            .setContentText(reminder.message)
            .setStyle(NotificationCompat.BigTextStyle().bigText(reminder.message))
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setContentIntent(openAppPendingIntent)
            .setAutoCancel(true)

        // Add Quick Action Buttons based on reminder type
        when (reminder.type) {
            "DEBT" -> {
                val paidIntent = Intent(context, ReminderActionReceiver::class.java).apply {
                    action = ReminderActionReceiver.ACTION_DEBT_PAID
                    putExtra("reminder_id", reminder.id)
                    putExtra("loan_id", reminder.targetId)
                }
                val paidPendingIntent = PendingIntent.getBroadcast(
                    context,
                    (reminder.id + 20000).toInt(),
                    paidIntent,
                    PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
                )

                val snoozeIntent = Intent(context, ReminderActionReceiver::class.java).apply {
                    action = ReminderActionReceiver.ACTION_SNOOZE_10MIN
                    putExtra("reminder_id", reminder.id)
                    putExtra("title", reminder.title)
                    putExtra("message", reminder.message)
                    putExtra("channel_type", reminder.channelType)
                }
                val snoozePendingIntent = PendingIntent.getBroadcast(
                    context,
                    (reminder.id + 30000).toInt(),
                    snoozeIntent,
                    PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
                )

                builder.addAction(R.mipmap.ic_launcher, "✅ Đã trả nợ", paidPendingIntent)
                builder.addAction(R.mipmap.ic_launcher, "⏳ Hoãn 10 phút", snoozePendingIntent)
            }

            "TASK" -> {
                val doneIntent = Intent(context, ReminderActionReceiver::class.java).apply {
                    action = ReminderActionReceiver.ACTION_TASK_COMPLETE
                    putExtra("reminder_id", reminder.id)
                    putExtra("task_id", reminder.targetId)
                }
                val donePendingIntent = PendingIntent.getBroadcast(
                    context,
                    (reminder.id + 40000).toInt(),
                    doneIntent,
                    PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
                )
                builder.addAction(R.mipmap.ic_launcher, "✅ Hoàn thành", donePendingIntent)
            }
        }

        manager.notify(reminder.id.toInt(), builder.build())
    }
}
