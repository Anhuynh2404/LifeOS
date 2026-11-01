package com.example.data.preferences

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "lifeos_user_prefs")

data class UserPreferences(
    val userName: String,
    val wakeUpTime: String,
    val energyProfile: String,
    val isOnboarded: Boolean,
    val level: Int,
    val xp: Int,
    val coins: Int,
    val spinTickets: Int,
    val activeStreakDays: Int,
    val demoSimulateCamera: Boolean,
    val demoSimulateSensors: Boolean,
    val demoSimulateAppBlocker: Boolean,
    val demoFastForwardHours: Int
)

class UserPreferencesRepository(private val context: Context) {
    companion object {
        val KEY_USER_NAME = stringPreferencesKey("user_name")
        val KEY_WAKEUP_TIME = stringPreferencesKey("wakeup_time")
        val KEY_ENERGY_PROFILE = stringPreferencesKey("energy_profile")
        val KEY_IS_ONBOARDED = booleanPreferencesKey("is_onboarded")
        val KEY_LEVEL = intPreferencesKey("user_level")
        val KEY_XP = intPreferencesKey("user_xp")
        val KEY_COINS = intPreferencesKey("user_coins")
        val KEY_SPIN_TICKETS = intPreferencesKey("spin_tickets")
        val KEY_STREAK_DAYS = intPreferencesKey("active_streak_days")
        val KEY_SIMULATE_CAMERA = booleanPreferencesKey("demo_simulate_camera")
        val KEY_SIMULATE_SENSORS = booleanPreferencesKey("demo_simulate_sensors")
        val KEY_SIMULATE_APP_BLOCKER = booleanPreferencesKey("demo_simulate_app_blocker")
        val KEY_FAST_FORWARD_HOURS = intPreferencesKey("demo_fast_forward_hours")
    }

    val userPreferencesFlow: Flow<UserPreferences> = context.dataStore.data.map { prefs ->
        UserPreferences(
            userName = prefs[KEY_USER_NAME] ?: "Người Dùng",
            wakeUpTime = prefs[KEY_WAKEUP_TIME] ?: "06:30",
            energyProfile = prefs[KEY_ENERGY_PROFILE] ?: "Buổi sáng (8:00 - 11:30)",
            isOnboarded = prefs[KEY_IS_ONBOARDED] ?: true,
            level = prefs[KEY_LEVEL] ?: 1,
            xp = prefs[KEY_XP] ?: 0,
            coins = prefs[KEY_COINS] ?: 100,
            spinTickets = prefs[KEY_SPIN_TICKETS] ?: 1,
            activeStreakDays = prefs[KEY_STREAK_DAYS] ?: 0,
            demoSimulateCamera = prefs[KEY_SIMULATE_CAMERA] ?: false,
            demoSimulateSensors = prefs[KEY_SIMULATE_SENSORS] ?: false,
            demoSimulateAppBlocker = prefs[KEY_SIMULATE_APP_BLOCKER] ?: false,
            demoFastForwardHours = prefs[KEY_FAST_FORWARD_HOURS] ?: 0
        )
    }

    suspend fun saveOnboarding(name: String, wakeUp: String, energy: String) {
        context.dataStore.edit { prefs ->
            prefs[KEY_USER_NAME] = name
            prefs[KEY_WAKEUP_TIME] = wakeUp
            prefs[KEY_ENERGY_PROFILE] = energy
            prefs[KEY_IS_ONBOARDED] = true
        }
    }

    suspend fun addXpAndCoins(xpGained: Int, coinsGained: Int) {
        context.dataStore.edit { prefs ->
            val currentXp = prefs[KEY_XP] ?: 0
            val currentLevel = prefs[KEY_LEVEL] ?: 1
            val currentCoins = prefs[KEY_COINS] ?: 100

            val newTotalXp = currentXp + xpGained
            val xpNeededForNext = currentLevel * 100
            if (newTotalXp >= xpNeededForNext) {
                prefs[KEY_LEVEL] = currentLevel + 1
                prefs[KEY_XP] = newTotalXp - xpNeededForNext
            } else {
                prefs[KEY_XP] = newTotalXp
            }
            prefs[KEY_COINS] = currentCoins + coinsGained
        }
    }

    suspend fun addSpinTickets(count: Int) {
        context.dataStore.edit { prefs ->
            val current = prefs[KEY_SPIN_TICKETS] ?: 1
            prefs[KEY_SPIN_TICKETS] = (current + count).coerceAtLeast(0)
        }
    }

    suspend fun spendCoins(amount: Int): Boolean {
        var success = false
        context.dataStore.edit { prefs ->
            val current = prefs[KEY_COINS] ?: 100
            if (current >= amount) {
                prefs[KEY_COINS] = current - amount
                success = true
            }
        }
        return success
    }

    suspend fun setDemoFlags(camera: Boolean, sensors: Boolean, appBlocker: Boolean) {
        context.dataStore.edit { prefs ->
            prefs[KEY_SIMULATE_CAMERA] = camera
            prefs[KEY_SIMULATE_SENSORS] = sensors
            prefs[KEY_SIMULATE_APP_BLOCKER] = appBlocker
        }
    }

    suspend fun addFastForwardHours(hours: Int) {
        context.dataStore.edit { prefs ->
            val current = prefs[KEY_FAST_FORWARD_HOURS] ?: 0
            prefs[KEY_FAST_FORWARD_HOURS] = current + hours
        }
    }

    suspend fun resetOnboarding() {
        context.dataStore.edit { prefs ->
            prefs[KEY_IS_ONBOARDED] = false
        }
    }

    suspend fun resetToCleanUserData() {
        context.dataStore.edit { prefs ->
            prefs[KEY_USER_NAME] = "Người Dùng"
            prefs[KEY_WAKEUP_TIME] = "06:30"
            prefs[KEY_ENERGY_PROFILE] = "Buổi sáng (8:00 - 11:30)"
            prefs[KEY_IS_ONBOARDED] = true
            prefs[KEY_LEVEL] = 1
            prefs[KEY_XP] = 0
            prefs[KEY_COINS] = 100
            prefs[KEY_SPIN_TICKETS] = 1
            prefs[KEY_STREAK_DAYS] = 0
            prefs[KEY_SIMULATE_CAMERA] = false
            prefs[KEY_SIMULATE_SENSORS] = false
            prefs[KEY_SIMULATE_APP_BLOCKER] = false
            prefs[KEY_FAST_FORWARD_HOURS] = 0
        }
    }
}
