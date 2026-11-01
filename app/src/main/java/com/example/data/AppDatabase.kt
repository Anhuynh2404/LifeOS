package com.example.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.data.dao.*
import com.example.data.entity.*

@Database(
    entities = [
        AlarmEntity::class,
        SleepLogEntity::class,
        TaskEntity::class,
        GoalEntity::class,
        SkillNodeEntity::class,
        VisionBoardItemEntity::class,
        ChallengeEntity::class,
        PartnerEntity::class,
        WalletEntity::class,
        TransactionEntity::class,
        BudgetEntity::class,
        SavingsFundEntity::class,
        ImpulseItemEntity::class,
        VirtualBaseItemEntity::class,
        RewardEntity::class,
        SpinHistoryEntity::class,
        EventLogEntity::class,
        CategoryEntity::class,
        FootballCardEntity::class,
        BankLoanEntity::class,
        LoanRepaymentEntity::class,
        ReminderEntity::class
    ],
    version = 6,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun alarmDao(): AlarmDao
    abstract fun taskDao(): TaskDao
    abstract fun goalDao(): GoalDao
    abstract fun skillDao(): SkillDao
    abstract fun challengeDao(): ChallengeDao
    abstract fun financeDao(): FinanceDao
    abstract fun eventLogDao(): EventLogDao
    abstract fun reminderDao(): ReminderDao


    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "lifeos_database.db"
                )
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
