package com.example.service

import android.app.AlarmManager
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import android.widget.Toast
import com.example.data.AppDatabase
import com.example.data.entity.EventLogEntity
import com.example.data.entity.LoanRepaymentEntity
import com.example.data.entity.ReminderEntity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ReminderActionReceiver : BroadcastReceiver() {

    companion object {
        const val ACTION_REMINDER_TRIGGER = "com.example.lifeos.ACTION_REMINDER_TRIGGER"
        const val ACTION_DEBT_PAID = "com.example.lifeos.ACTION_DEBT_PAID"
        const val ACTION_SNOOZE_10MIN = "com.example.lifeos.ACTION_SNOOZE_10MIN"
        const val ACTION_TASK_COMPLETE = "com.example.lifeos.ACTION_TASK_COMPLETE"
    }

    override fun onReceive(context: Context, intent: Intent) {
        val action = intent.action ?: return
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        when (action) {
            ACTION_REMINDER_TRIGGER -> {
                val reminderId = intent.getLongExtra("reminder_id", 0L)
                val type = intent.getStringExtra("reminder_type") ?: "GENERAL"
                val targetId = intent.getLongExtra("target_id", 0L)
                val title = intent.getStringExtra("title") ?: "Thông báo LifeOS"
                val message = intent.getStringExtra("message") ?: ""
                val channelType = intent.getStringExtra("channel_type") ?: "SCHEDULE"
                val extraData = intent.getStringExtra("extra_data") ?: ""

                val reminder = ReminderEntity(
                    id = reminderId,
                    type = type,
                    targetId = targetId,
                    title = title,
                    message = message,
                    triggerTime = System.currentTimeMillis(),
                    channelType = channelType,
                    extraData = extraData
                )
                ReminderScheduler.triggerImmediateNotification(context, reminder)
            }

            ACTION_DEBT_PAID -> {
                val reminderId = intent.getLongExtra("reminder_id", 0L)
                val loanId = intent.getLongExtra("loan_id", 0L)
                notificationManager.cancel(reminderId.toInt())

                CoroutineScope(Dispatchers.IO).launch {
                    try {
                        val db = AppDatabase.getDatabase(context)
                        val loan = db.financeDao().getBankLoanById(loanId)
                        if (loan != null) {
                            val paymentAmount = loan.monthlyEstimatedPayment
                            db.financeDao().insertRepayment(
                                LoanRepaymentEntity(
                                    loanId = loanId,
                                    paymentDate = System.currentTimeMillis(),
                                    principalPaid = (paymentAmount * 0.7f).toLong(),
                                    interestPaid = (paymentAmount * 0.3f).toLong(),
                                    totalPaid = paymentAmount,
                                    note = "Thanh toán nhanh qua thông báo LifeOS"
                                )
                            )
                            db.eventLogDao().insertEvent(
                                EventLogEntity(
                                    eventType = "FINANCE_DEBT_PAID",
                                    description = "Thanh toán khoản nợ: ${loan.bankName}",
                                    xpGained = 30
                                )
                            )
                        }
                        db.reminderDao().markCompleted(reminderId)
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
                Toast.makeText(context, "✅ Đã ghi nhận thanh toán nợ kỳ này!", Toast.LENGTH_SHORT).show()
            }

            ACTION_SNOOZE_10MIN -> {
                val reminderId = intent.getLongExtra("reminder_id", 0L)
                val title = intent.getStringExtra("title") ?: "Nhắc nhở LifeOS"
                val message = intent.getStringExtra("message") ?: ""
                val channelType = intent.getStringExtra("channel_type") ?: "SCHEDULE"
                notificationManager.cancel(reminderId.toInt())

                val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
                val snoozeTime = System.currentTimeMillis() + (10 * 60 * 1000L)

                val snoozeIntent = Intent(context, ReminderActionReceiver::class.java).apply {
                    this.action = ACTION_REMINDER_TRIGGER
                    putExtra("reminder_id", reminderId)
                    putExtra("reminder_type", "SNOOZE")
                    putExtra("title", title)
                    putExtra("message", message)
                    putExtra("channel_type", channelType)
                }

                val pendingIntent = PendingIntent.getBroadcast(
                    context,
                    reminderId.toInt(),
                    snoozeIntent,
                    PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
                )

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, snoozeTime, pendingIntent)
                } else {
                    alarmManager.setExact(AlarmManager.RTC_WAKEUP, snoozeTime, pendingIntent)
                }

                Toast.makeText(context, "⏳ Đã hoãn nhắc nhở thêm 10 phút.", Toast.LENGTH_SHORT).show()
            }

            ACTION_TASK_COMPLETE -> {
                val reminderId = intent.getLongExtra("reminder_id", 0L)
                val taskId = intent.getLongExtra("task_id", 0L)
                notificationManager.cancel(reminderId.toInt())

                CoroutineScope(Dispatchers.IO).launch {
                    try {
                        val db = AppDatabase.getDatabase(context)
                        val task = db.taskDao().getTaskById(taskId)
                        if (task != null) {
                            db.taskDao().updateTask(task.copy(isCompleted = true))
                            db.eventLogDao().insertEvent(
                                EventLogEntity(
                                    eventType = "TASK_COMPLETED",
                                    description = "Hoàn thành: ${task.title}",
                                    xpGained = 25
                                )
                            )
                        }
                        db.reminderDao().markCompleted(reminderId)
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
                Toast.makeText(context, "🎉 Đã hoàn thành nhiệm vụ! +25 XP", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
