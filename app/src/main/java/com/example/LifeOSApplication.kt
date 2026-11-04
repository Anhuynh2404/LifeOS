package com.example

import android.app.Application
import com.example.core.engine.RuleEngine
import com.example.core.event.LifeEventBus
import com.example.data.AppDatabase
import com.example.data.DatabaseSeeder
import com.example.data.preferences.UserPreferencesRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class LifeOSApplication : Application() {
    lateinit var database: AppDatabase
        private set

    lateinit var userPreferences: UserPreferencesRepository
        private set

    lateinit var ruleEngine: RuleEngine
        private set

    lateinit var eventBus: LifeEventBus
        private set

    override fun onCreate() {
        super.onCreate()
        database = AppDatabase.getDatabase(this)
        userPreferences = UserPreferencesRepository(this)
        ruleEngine = RuleEngine(database, userPreferences)
        eventBus = LifeEventBus(ruleEngine)

        // Seed realistic starter data
        CoroutineScope(Dispatchers.IO).launch {
            DatabaseSeeder.seedIfEmpty(database)
            try {
                com.example.service.PersistentStatusService.startService(this@LifeOSApplication)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}
