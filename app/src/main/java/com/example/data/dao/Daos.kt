package com.example.data.dao

import androidx.room.*
import com.example.data.entity.*
import kotlinx.coroutines.flow.Flow

@Dao
interface AlarmDao {
    @Query("SELECT * FROM alarms ORDER BY hour, minute ASC")
    fun getAllAlarms(): Flow<List<AlarmEntity>>

    @Query("SELECT * FROM alarms WHERE isEnabled = 1 ORDER BY hour, minute ASC")
    fun getEnabledAlarms(): Flow<List<AlarmEntity>>

    @Query("SELECT * FROM alarms WHERE id = :id")
    suspend fun getAlarmById(id: Long): AlarmEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAlarm(alarm: AlarmEntity): Long

    @Update
    suspend fun updateAlarm(alarm: AlarmEntity)

    @Delete
    suspend fun deleteAlarm(alarm: AlarmEntity)

    @Query("SELECT * FROM sleep_logs ORDER BY date DESC LIMIT 7")
    fun getRecentSleepLogs(): Flow<List<SleepLogEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSleepLog(log: SleepLogEntity): Long

    @Query("DELETE FROM alarms")
    suspend fun deleteAllAlarms()

    @Query("DELETE FROM sleep_logs")
    suspend fun deleteAllSleepLogs()
}

@Dao
interface TaskDao {
    @Query("SELECT * FROM tasks ORDER BY scheduledTimeHour, scheduledTimeMinute ASC")
    fun getAllTasks(): Flow<List<TaskEntity>>

    @Query("SELECT * FROM tasks WHERE deadlineDate = :date OR scheduledTimeHour >= 0 ORDER BY isCompleted ASC, difficulty DESC")
    fun getTasksForDate(date: String): Flow<List<TaskEntity>>

    @Query("SELECT * FROM tasks WHERE isCompleted = 0 ORDER BY CASE difficulty WHEN 'HARD' THEN 1 WHEN 'MEDIUM' THEN 2 ELSE 3 END ASC LIMIT 3")
    fun getTop3PriorityTasks(): Flow<List<TaskEntity>>

    @Query("SELECT * FROM tasks WHERE isCompleted = 0")
    suspend fun getIncompleteTasksList(): List<TaskEntity>

    @Query("SELECT * FROM tasks WHERE id = :id")
    suspend fun getTaskById(id: Long): TaskEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTask(task: TaskEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTasks(tasks: List<TaskEntity>)

    @Update
    suspend fun updateTask(task: TaskEntity)

    @Delete
    suspend fun deleteTask(task: TaskEntity)

    @Query("DELETE FROM tasks")
    suspend fun deleteAllTasks()
}

@Dao
interface GoalDao {
    @Query("SELECT * FROM goals ORDER BY targetDate ASC")
    fun getAllGoals(): Flow<List<GoalEntity>>

    @Query("SELECT * FROM goals WHERE id = :id")
    suspend fun getGoalById(id: Long): GoalEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertGoal(goal: GoalEntity): Long

    @Update
    suspend fun updateGoal(goal: GoalEntity)

    @Delete
    suspend fun deleteGoal(goal: GoalEntity)

    @Query("DELETE FROM goals")
    suspend fun deleteAllGoals()

    @Query("SELECT * FROM vision_board ORDER BY dateAdded DESC")
    fun getVisionBoardItems(): Flow<List<VisionBoardItemEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertVisionBoardItem(item: VisionBoardItemEntity): Long

    @Delete
    suspend fun deleteVisionBoardItem(item: VisionBoardItemEntity)

    @Query("DELETE FROM vision_board")
    suspend fun deleteAllVisionBoardItems()
}

@Dao
interface SkillDao {
    @Query("SELECT * FROM skill_nodes ORDER BY branchName, level ASC")
    fun getAllSkills(): Flow<List<SkillNodeEntity>>

    @Query("SELECT * FROM skill_nodes WHERE id = :id")
    suspend fun getSkillById(id: Long): SkillNodeEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSkill(skill: SkillNodeEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSkills(skills: List<SkillNodeEntity>)

    @Update
    suspend fun updateSkill(skill: SkillNodeEntity)
}

@Dao
interface ChallengeDao {
    @Query("SELECT * FROM challenges ORDER BY status ASC, currentStreak DESC")
    fun getAllChallenges(): Flow<List<ChallengeEntity>>

    @Query("SELECT * FROM challenges WHERE id = :id")
    suspend fun getChallengeById(id: Long): ChallengeEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertChallenge(challenge: ChallengeEntity): Long

    @Update
    suspend fun updateChallenge(challenge: ChallengeEntity)

    @Delete
    suspend fun deleteChallenge(challenge: ChallengeEntity)

    @Query("SELECT * FROM partners")
    fun getAllPartners(): Flow<List<PartnerEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPartner(partner: PartnerEntity): Long

    @Query("SELECT * FROM wheel_rewards")
    fun getAllRewards(): Flow<List<RewardEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRewards(rewards: List<RewardEntity>)

    @Query("SELECT * FROM spin_history ORDER BY timestamp DESC LIMIT 20")
    fun getSpinHistory(): Flow<List<SpinHistoryEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSpinHistory(history: SpinHistoryEntity): Long

    @Query("DELETE FROM challenges")
    suspend fun deleteAllChallenges()

    @Query("DELETE FROM partners")
    suspend fun deleteAllPartners()

    @Query("DELETE FROM spin_history")
    suspend fun deleteAllSpinHistory()
}

@Dao
interface FinanceDao {
    @Query("SELECT * FROM wallets")
    fun getAllWallets(): Flow<List<WalletEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertWallet(wallet: WalletEntity): Long

    @Update
    suspend fun updateWallet(wallet: WalletEntity)

    @Query("SELECT * FROM transactions ORDER BY timestamp DESC")
    fun getAllTransactions(): Flow<List<TransactionEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTransaction(transaction: TransactionEntity): Long

    @Query("SELECT * FROM budgets")
    fun getAllBudgets(): Flow<List<BudgetEntity>>

    @Query("SELECT * FROM budgets WHERE monthYear = :monthYear")
    fun getBudgetsForMonth(monthYear: String): Flow<List<BudgetEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBudget(budget: BudgetEntity): Long

    @Update
    suspend fun updateBudget(budget: BudgetEntity)

    @Query("SELECT * FROM savings_funds")
    fun getAllSavingsFunds(): Flow<List<SavingsFundEntity>>

    @Query("SELECT * FROM savings_funds WHERE id = :id")
    suspend fun getSavingsFundById(id: Long): SavingsFundEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSavingsFund(fund: SavingsFundEntity): Long

    @Update
    suspend fun updateSavingsFund(fund: SavingsFundEntity)

    @Query("SELECT * FROM categories ORDER BY name ASC")
    fun getAllCategories(): Flow<List<CategoryEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCategory(cat: CategoryEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCategories(cats: List<CategoryEntity>)

    @Delete
    suspend fun deleteCategory(cat: CategoryEntity)

    @Delete
    suspend fun deleteWallet(wallet: WalletEntity)

    @Query("SELECT * FROM budgets WHERE category = :category AND monthYear = :monthYear LIMIT 1")
    suspend fun getBudgetByCategory(category: String, monthYear: String): BudgetEntity?

    @Delete
    suspend fun deleteSavingsFund(fund: SavingsFundEntity)

    @Query("SELECT * FROM football_cards ORDER BY ovr DESC")
    fun getAllFootballCards(): Flow<List<FootballCardEntity>>

    @Query("SELECT * FROM football_cards WHERE clubName = :clubName ORDER BY ovr DESC")
    fun getFootballCardsByClub(clubName: String): Flow<List<FootballCardEntity>>

    @Query("SELECT * FROM football_cards WHERE leagueName = :leagueName ORDER BY ovr DESC")
    fun getFootballCardsByLeague(leagueName: String): Flow<List<FootballCardEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFootballCards(cards: List<FootballCardEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFootballCard(card: FootballCardEntity): Long

    @Update
    suspend fun updateFootballCard(card: FootballCardEntity)

    @Delete
    suspend fun deleteFootballCard(card: FootballCardEntity)

    @Query("DELETE FROM football_cards")
    suspend fun deleteAllFootballCards()

    @Query("SELECT * FROM impulse_items ORDER BY createdAt DESC")
    fun getAllImpulseItems(): Flow<List<ImpulseItemEntity>>

    @Query("SELECT * FROM impulse_items WHERE id = :id")
    suspend fun getImpulseItemById(id: Long): ImpulseItemEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertImpulseItem(item: ImpulseItemEntity): Long

    @Update
    suspend fun updateImpulseItem(item: ImpulseItemEntity)

    @Query("SELECT * FROM virtual_base_items")
    fun getVirtualBaseItems(): Flow<List<VirtualBaseItemEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBaseItems(items: List<VirtualBaseItemEntity>)

    @Update
    suspend fun updateBaseItem(item: VirtualBaseItemEntity)

    // Bank Loans & Repayments
    @Query("SELECT * FROM bank_loans ORDER BY isPaidOff ASC, duePaymentDay ASC")
    fun getAllBankLoans(): Flow<List<BankLoanEntity>>

    @Query("SELECT * FROM bank_loans WHERE id = :id")
    suspend fun getBankLoanById(id: Long): BankLoanEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBankLoan(loan: BankLoanEntity): Long

    @Update
    suspend fun updateBankLoan(loan: BankLoanEntity)

    @Delete
    suspend fun deleteBankLoan(loan: BankLoanEntity)

    @Query("SELECT * FROM loan_repayments ORDER BY paymentDate DESC")
    fun getAllRepayments(): Flow<List<LoanRepaymentEntity>>

    @Query("SELECT * FROM loan_repayments WHERE loanId = :loanId ORDER BY paymentDate DESC")
    fun getRepaymentsForLoan(loanId: Long): Flow<List<LoanRepaymentEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRepayment(repayment: LoanRepaymentEntity): Long

    @Delete
    suspend fun deleteRepayment(repayment: LoanRepaymentEntity)

    @Query("DELETE FROM wallets")
    suspend fun deleteAllWallets()

    @Query("DELETE FROM transactions")
    suspend fun deleteAllTransactions()

    @Query("DELETE FROM budgets")
    suspend fun deleteAllBudgets()

    @Query("DELETE FROM savings_funds")
    suspend fun deleteAllSavingsFunds()

    @Query("DELETE FROM impulse_items")
    suspend fun deleteAllImpulseItems()

    @Query("DELETE FROM bank_loans")
    suspend fun deleteAllBankLoans()

    @Query("DELETE FROM loan_repayments")
    suspend fun deleteAllLoanRepayments()
}

@Dao
interface EventLogDao {
    @Query("SELECT * FROM event_logs ORDER BY timestamp DESC LIMIT 50")
    fun getRecentEvents(): Flow<List<EventLogEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertEvent(event: EventLogEntity): Long

    @Query("SELECT SUM(savedAmount) FROM event_logs WHERE timestamp >= :sinceTimestamp")
    fun getSavingsSince(sinceTimestamp: Long): Flow<Long?>

    @Query("DELETE FROM event_logs")
    suspend fun deleteAllEvents()
}

@Dao
interface ReminderDao {
    @Query("SELECT * FROM reminders WHERE isCompleted = 0 ORDER BY triggerTime ASC")
    fun getPendingReminders(): Flow<List<ReminderEntity>>

    @Query("SELECT * FROM reminders WHERE isCompleted = 0 AND triggerTime <= :upToTime")
    suspend fun getDueReminders(upToTime: Long): List<ReminderEntity>

    @Query("SELECT * FROM reminders WHERE id = :id")
    suspend fun getReminderById(id: Long): ReminderEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertReminder(reminder: ReminderEntity): Long

    @Update
    suspend fun updateReminder(reminder: ReminderEntity)

    @Query("UPDATE reminders SET isCompleted = 1 WHERE id = :id")
    suspend fun markCompleted(id: Long)

    @Query("DELETE FROM reminders WHERE id = :id")
    suspend fun deleteReminderById(id: Long)

    @Query("DELETE FROM reminders WHERE type = :type AND targetId = :targetId")
    suspend fun deleteByTypeAndTarget(type: String, targetId: Long)

    @Query("DELETE FROM reminders")
    suspend fun deleteAllReminders()
}

