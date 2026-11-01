package com.example.feature.alarm

import android.app.KeyguardManager
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.WindowManager
import androidx.activity.ComponentActivity
import androidx.activity.OnBackPressedCallback
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import com.example.data.AppDatabase
import com.example.data.entity.EventLogEntity
import com.example.service.AlarmService
import com.example.ui.theme.MyApplicationTheme
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class AlarmRingingActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Ensure activity shows over lock screen and turns the screen on immediately
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O_MR1) {
            setShowWhenLocked(true)
            setTurnScreenOn(true)
            val keyguardManager = getSystemService(Context.KEYGUARD_SERVICE) as KeyguardManager
            keyguardManager.requestDismissKeyguard(this, null)
        }

        window.addFlags(
            WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON or
            WindowManager.LayoutParams.FLAG_ALLOW_LOCK_WHILE_SCREEN_ON or
            WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED or
            WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON or
            WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD
        )

        // Prevent bypassing alarm via back button - user must complete mission
        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                // Do not allow dismiss by back button
            }
        })

        val alarmId = intent.getLongExtra("alarm_id", 0L)
        val alarmTitle = intent.getStringExtra("alarm_title") ?: "Báo thức LifeOS"
        val missionType = intent.getStringExtra("mission_type") ?: "MATH"

        setContent {
            MyApplicationTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    AlarmTriggerScreen(
                        alarmTitle = alarmTitle,
                        missionType = missionType,
                        onMissionCompleted = { missionUsed ->
                            // 1. Stop Alarm Foreground Service & Audio
                            val stopIntent = Intent(this@AlarmRingingActivity, AlarmService::class.java).apply {
                                action = AlarmService.ACTION_STOP_ALARM
                            }
                            startService(stopIntent)

                            // 2. Clear ringing flag in preferences
                            val prefs = getSharedPreferences("lifeos_alarm_state", Context.MODE_PRIVATE)
                            prefs.edit().putBoolean("is_ringing", false).apply()

                            // 3. Log wake-up event
                            CoroutineScope(Dispatchers.IO).launch {
                                try {
                                    val db = AppDatabase.getDatabase(applicationContext)
                                    db.eventLogDao().insertEvent(
                                        EventLogEntity(
                                            eventType = "WAKE_UP_SUCCESS",
                                            description = "Thức dậy thành công: $alarmTitle ($missionUsed)",
                                            xpGained = 50
                                        )
                                    )
                                } catch (e: Exception) {
                                    e.printStackTrace()
                                }
                            }

                            // 4. Dismiss activity cleanly
                            finishAndRemoveTask()
                        }
                    )
                }
            }
        }
    }
}
