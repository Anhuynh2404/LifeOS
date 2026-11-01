package com.example.service

import android.content.Context
import androidx.work.*
import com.example.data.AppDatabase
import kotlinx.coroutines.flow.first
import java.util.concurrent.TimeUnit

class AlarmWatchdogWorker(
    private val context: Context,
    workerParams: WorkerParameters
) : CoroutineWorker(context, workerParams) {

    companion object {
        private const val WORK_NAME = "lifeos_alarm_watchdog"

        fun enqueuePeriodicWatchdog(context: Context) {
            val constraints = Constraints.Builder().build()
            val periodicWork = PeriodicWorkRequestBuilder<AlarmWatchdogWorker>(
                15, TimeUnit.MINUTES,
                5, TimeUnit.MINUTES
            )
                .setConstraints(constraints)
                .build()

            WorkManager.getInstance(context).enqueueUniquePeriodicWork(
                WORK_NAME,
                ExistingPeriodicWorkPolicy.KEEP,
                periodicWork
            )
        }
    }

    override suspend fun doWork(): Result {
        return try {
            val db = AppDatabase.getDatabase(context)
            val enabledAlarms = db.alarmDao().getEnabledAlarms().first()

            for (alarm in enabledAlarms) {
                AlarmReceiver.scheduleAlarm(context, alarm)
            }

            // Also check if any pending reminders were missed
            val now = System.currentTimeMillis()
            val dueReminders = db.reminderDao().getDueReminders(now)
            for (reminder in dueReminders) {
                ReminderScheduler.triggerImmediateNotification(context, reminder)
                db.reminderDao().markCompleted(reminder.id)
            }

            Result.success()
        } catch (e: Exception) {
            e.printStackTrace()
            Result.retry()
        }
    }
}
