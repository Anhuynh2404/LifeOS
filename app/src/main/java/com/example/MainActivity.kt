package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.*
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.core.audio.ToneGenerator
import com.example.core.event.LifeEvent
import com.example.core.util.Formatters
import com.example.data.DatabaseSeeder
import com.example.data.entity.*
import com.example.data.preferences.UserPreferences
import com.example.feature.alarm.*
import com.example.feature.challenges.ChallengesScreen
import com.example.feature.dashboard.DashboardScreen
import com.example.feature.finance.FinanceScreen
import com.example.feature.goals.GoalsScreen
import com.example.feature.onboarding.OnboardingScreen
import com.example.feature.schedule.FocusModeScreen
import com.example.feature.schedule.ScheduleScreen
import com.example.feature.settings.SettingsScreen
import com.example.designsystem.components.LifeBottomBar
import com.example.designsystem.components.LifeNavigationItem
import com.example.designsystem.gallery.ComponentGalleryScreen
import com.example.designsystem.theme.LifeOSTheme
import com.example.designsystem.theme.LifeTheme
import com.example.ui.components.*
import com.example.ui.theme.*
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

enum class MainTab(val title: String, val icon: ImageVector) {
    TODAY("Hôm nay", Icons.Default.Today),
    SCHEDULE("Lịch", Icons.Default.CalendarMonth),
    FINANCE("Tài chính", Icons.Default.AccountBalanceWallet),
    GOALS("Mục tiêu", Icons.Default.Flag),
    CHALLENGES("Thử thách", Icons.Default.EmojiEvents)
}

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val app = application as LifeOSApplication
        val database = app.database
        val userPrefsRepo = app.userPreferences
        val eventBus = app.eventBus

        com.example.service.ReminderScheduler.initChannels(this)
        com.example.service.AlarmWatchdogWorker.enqueuePeriodicWatchdog(this)

        setContent {

            LifeOSTheme {
                val scope = rememberCoroutineScope()

                // State collection
                val userPrefs by userPrefsRepo.userPreferencesFlow.collectAsState(
                    initial = UserPreferences(
                        userName = "Người Dùng",
                        wakeUpTime = "06:30",
                        energyProfile = "Buổi sáng (8:00 - 11:30)",
                        isOnboarded = true,
                        level = 1,
                        xp = 0,
                        coins = 100,
                        spinTickets = 1,
                        activeStreakDays = 0,
                        demoSimulateCamera = false,
                        demoSimulateSensors = false,
                        demoSimulateAppBlocker = false,
                        demoFastForwardHours = 0
                    )
                )

                val alarms by database.alarmDao().getAllAlarms().collectAsState(initial = emptyList())
                val tasks by database.taskDao().getAllTasks().collectAsState(initial = emptyList())
                val top3Tasks by database.taskDao().getTop3PriorityTasks().collectAsState(initial = emptyList())
                val goals by database.goalDao().getAllGoals().collectAsState(initial = emptyList())
                val skills by database.skillDao().getAllSkills().collectAsState(initial = emptyList())
                val visionItems by database.goalDao().getVisionBoardItems().collectAsState(initial = emptyList())
                val challenges by database.challengeDao().getAllChallenges().collectAsState(initial = emptyList())
                val rewards by database.challengeDao().getAllRewards().collectAsState(initial = emptyList())
                val spinHistory by database.challengeDao().getSpinHistory().collectAsState(initial = emptyList())
                val wallets by database.financeDao().getAllWallets().collectAsState(initial = emptyList())
                val categories by database.financeDao().getAllCategories().collectAsState(initial = emptyList())
                val transactions by database.financeDao().getAllTransactions().collectAsState(initial = emptyList())
                val budgets by database.financeDao().getAllBudgets().collectAsState(initial = emptyList())
                val savingsFunds by database.financeDao().getAllSavingsFunds().collectAsState(initial = emptyList())
                val impulseItems by database.financeDao().getAllImpulseItems().collectAsState(initial = emptyList())
                val footballCards by database.financeDao().getAllFootballCards().collectAsState(initial = emptyList())
                val virtualBaseItems by database.financeDao().getVirtualBaseItems().collectAsState(initial = emptyList())
                val bankLoans by database.financeDao().getAllBankLoans().collectAsState(initial = emptyList())
                val loanRepayments by database.financeDao().getAllRepayments().collectAsState(initial = emptyList())
                val eventLogs by database.eventLogDao().getRecentEvents().collectAsState(initial = emptyList())
                val sleepLogs by database.alarmDao().getRecentSleepLogs().collectAsState(initial = emptyList())

                // Savings this week calculation
                val weeklySavings = remember(eventLogs) {
                    val sevenDaysAgo = System.currentTimeMillis() - 7 * 24 * 3600 * 1000L
                    eventLogs.filter { it.timestamp >= sevenDaysAgo }.sumOf { it.savedAmount }
                }

                // Navigation State
                var currentTab by remember { mutableStateOf(MainTab.TODAY) }
                var activeSubScreen by remember { mutableStateOf<String?>(null) }
                // Sub-screens: "alarm_list", "alarm_trigger", "morning_routine", "sleep_tracker", "focus_mode", "settings"
                var triggeringAlarmTitle by remember { mutableStateOf("Thức dậy rực rỡ") }
                var triggeringAlarmMission by remember { mutableStateOf("MATH") }

                // Check intent extra on launch or test
                LaunchedEffect(intent) {
                    val navigateTo = intent.getStringExtra("navigate_to")
                    if (navigateTo == "alarm_trigger") {
                        triggeringAlarmTitle = intent.getStringExtra("alarm_title") ?: "Thức dậy rực rỡ"
                        triggeringAlarmMission = intent.getStringExtra("mission_type") ?: "MATH"
                        activeSubScreen = "alarm_trigger"
                    } else if (navigateTo == "focus_mode") {
                        activeSubScreen = "focus_mode"
                    }
                }

                if (!userPrefs.isOnboarded) {
                    OnboardingScreen(
                        onFinish = { name, wakeUp, energy ->
                            scope.launch {
                                userPrefsRepo.saveOnboarding(name, wakeUp, energy)
                            }
                        }
                    )
                } else {
                    Scaffold(
                        bottomBar = {
                            if (activeSubScreen == null) {
                                val colors = LifeTheme.colors
                                val navItems = listOf(
                                    LifeNavigationItem("today", "Hôm nay", Icons.Default.Today, colors.brandPrimary),
                                    LifeNavigationItem("schedule", "Lịch", Icons.Default.CalendarMonth, colors.scheduleStart),
                                    LifeNavigationItem("finance", "Tài chính", Icons.Default.AccountBalanceWallet, colors.financeStart),
                                    LifeNavigationItem("goals", "Mục tiêu", Icons.Default.Flag, colors.goalsStart),
                                    LifeNavigationItem("challenges", "Thử thách", Icons.Default.EmojiEvents, colors.challengesStart)
                                )
                                val selectedId = when (currentTab) {
                                    MainTab.TODAY -> "today"
                                    MainTab.SCHEDULE -> "schedule"
                                    MainTab.FINANCE -> "finance"
                                    MainTab.GOALS -> "goals"
                                    MainTab.CHALLENGES -> "challenges"
                                }
                                LifeBottomBar(
                                    items = navItems,
                                    selectedItemId = selectedId,
                                    onItemSelected = { id ->
                                        currentTab = when (id) {
                                            "today" -> MainTab.TODAY
                                            "schedule" -> MainTab.SCHEDULE
                                            "finance" -> MainTab.FINANCE
                                            "goals" -> MainTab.GOALS
                                            else -> MainTab.CHALLENGES
                                        }
                                    }
                                )
                            }
                        }
                    ) { innerPadding ->
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(innerPadding)
                        ) {
                            // Subscreen Rendering with BackHandler
                            if (activeSubScreen != null) {
                                BackHandler {
                                    activeSubScreen = null
                                }
                                when (activeSubScreen) {
                                    "alarm_list" -> {
                                        AlarmListScreen(
                                            alarms = alarms,
                                            onAddAlarm = { newAlarm ->
                                                scope.launch {
                                                    val id = database.alarmDao().insertAlarm(newAlarm)
                                                    if (newAlarm.isEnabled) {
                                                        com.example.service.AlarmReceiver.scheduleAlarm(this@MainActivity, newAlarm.copy(id = id))
                                                    }
                                                }
                                            },
                                            onToggleAlarm = { alarm ->
                                                scope.launch {
                                                    val updated = alarm.copy(isEnabled = !alarm.isEnabled)
                                                    database.alarmDao().updateAlarm(updated)
                                                    if (updated.isEnabled) {
                                                        com.example.service.AlarmReceiver.scheduleAlarm(this@MainActivity, updated)
                                                    } else {
                                                        com.example.service.AlarmReceiver.cancelAlarm(this@MainActivity, updated.id)
                                                    }
                                                }
                                            },
                                            onDeleteAlarm = { alarm ->
                                                scope.launch {
                                                    database.alarmDao().deleteAlarm(alarm)
                                                    com.example.service.AlarmReceiver.cancelAlarm(this@MainActivity, alarm.id)
                                                }
                                            },
                                            onTriggerAlarm = { alarm ->
                                                triggeringAlarmTitle = alarm.title
                                                triggeringAlarmMission = alarm.missionType
                                                ToneGenerator.playAlarm(onLoop = true)
                                                activeSubScreen = "alarm_trigger"
                                            },
                                            onNavigateToSleepLog = { activeSubScreen = "sleep_tracker" },
                                            onBack = { activeSubScreen = null }
                                        )
                                    }

                                    "alarm_trigger" -> {
                                        AlarmTriggerScreen(
                                            alarmTitle = triggeringAlarmTitle,
                                            missionType = triggeringAlarmMission,
                                            onMissionCompleted = { missionUsed ->
                                                ToneGenerator.stop()
                                                eventBus.postEvent(
                                                    LifeEvent.AlarmDismissed(
                                                        alarmId = 1L,
                                                        onTime = true,
                                                        missionUsed = missionUsed
                                                    )
                                                )
                                                // Transition smoothly to Morning Routine
                                                activeSubScreen = "morning_routine"
                                            }
                                        )
                                    }

                                    "morning_routine" -> {
                                        MorningRoutineScreen(
                                            top3Tasks = top3Tasks,
                                            onStartNewDay = {
                                                eventBus.postEvent(
                                                    LifeEvent.MorningRoutineCompleted(
                                                        quoteRead = true,
                                                        meditationDone = true
                                                    )
                                                )
                                                activeSubScreen = null
                                                currentTab = MainTab.TODAY
                                            }
                                        )
                                    }

                                    "sleep_tracker" -> {
                                        SleepTrackerScreen(
                                            sleepLogs = sleepLogs,
                                            onAddSleepLog = { log ->
                                                scope.launch { database.alarmDao().insertSleepLog(log) }
                                            },
                                            onBack = { activeSubScreen = "alarm_list" }
                                        )
                                    }

                                    "focus_mode" -> {
                                        FocusModeScreen(
                                            onFocusFinished = { duration, blocked ->
                                                eventBus.postEvent(
                                                    LifeEvent.FocusSessionCompleted(
                                                        durationMinutes = duration,
                                                        appDistractionsBlocked = blocked
                                                    )
                                                )
                                                activeSubScreen = null
                                            },
                                            onBack = { activeSubScreen = null }
                                        )
                                    }

                                    "settings" -> {
                                        SettingsScreen(
                                            userPrefs = userPrefs,
                                            onSaveProfile = { name, wakeUp, energy ->
                                                scope.launch {
                                                    userPrefsRepo.saveOnboarding(name, wakeUp, energy)
                                                }
                                            },
                                            onTestTriggerAlarm = {
                                                triggeringAlarmTitle = alarms.firstOrNull()?.title ?: "Báo thức thử nghiệm"
                                                triggeringAlarmMission = alarms.firstOrNull()?.missionType ?: "MATH"
                                                ToneGenerator.playAlarm(onLoop = true)
                                                activeSubScreen = "alarm_trigger"
                                            },
                                            onFastForward48h = {
                                                // Fast forward impulse items and add 48 hours to demo
                                                scope.launch {
                                                    userPrefsRepo.addFastForwardHours(48)
                                                    // Unlock impulse items if waiting
                                                    val waiting = impulseItems.filter { it.status == "WAITING" }
                                                    waiting.forEach {
                                                        // Item now ready to be resolved
                                                    }
                                                    ToneGenerator.playSuccessChime()
                                                }
                                            },
                                            onAddSpinTicket = {
                                                scope.launch {
                                                    userPrefsRepo.addSpinTickets(1)
                                                    ToneGenerator.playSuccessChime()
                                                }
                                            },
                                            onAddBonusXpAndCoins = {
                                                scope.launch {
                                                    userPrefsRepo.addXpAndCoins(100, 100)
                                                    ToneGenerator.playSuccessChime()
                                                }
                                            },
                                            onResetDemoData = {
                                                scope.launch {
                                                    DatabaseSeeder.resetToProductionClean(database)
                                                    userPrefsRepo.resetToCleanUserData()
                                                    android.widget.Toast.makeText(
                                                        this@MainActivity,
                                                        "✨ Đã làm sạch toàn bộ dữ liệu! Ứng dụng đã sẵn sàng cho bạn bắt đầu.",
                                                        android.widget.Toast.LENGTH_LONG
                                                    ).show()
                                                    ToneGenerator.playSuccessChime()
                                                }
                                            },
                                            onNavigateToGallery = { activeSubScreen = "component_gallery" },
                                            onNavigateToPermissions = { activeSubScreen = "permission_health" },
                                            onTestRealAlarm1Min = {
                                                com.example.service.AlarmReceiver.scheduleTestAlarm(this@MainActivity, 60)
                                                android.widget.Toast.makeText(
                                                    this@MainActivity,
                                                    "⏰ Đã đặt báo thức thật sau 60 giây! Hãy tắt app và khóa màn hình ngay để kiểm tra.",
                                                    android.widget.Toast.LENGTH_LONG
                                                ).show()
                                            },
                                            onBack = { activeSubScreen = null }
                                        )
                                    }

                                    "component_gallery" -> {
                                        ComponentGalleryScreen(
                                            onBack = { activeSubScreen = "settings" }
                                        )
                                    }

                                    "permission_health" -> {
                                        com.example.ui.screens.PermissionHealthCenterScreen(
                                            onNavigateBack = { activeSubScreen = "settings" }
                                        )
                                    }
                                }
                            } else {
                                // Main Tab Content Rendering
                                when (currentTab) {
                                    MainTab.TODAY -> {
                                        DashboardScreen(
                                            userPrefs = userPrefs,
                                            nextAlarm = alarms.firstOrNull { it.isEnabled } ?: alarms.firstOrNull(),
                                            top3Tasks = top3Tasks,
                                            eventLogs = eventLogs,
                                            weeklySavings = weeklySavings,
                                            eventBus = eventBus,
                                            bankLoans = bankLoans,
                                            onNavigateToAlarm = { activeSubScreen = "alarm_list" },
                                            onTriggerTestAlarm = { alarm ->
                                                triggeringAlarmTitle = alarm.title
                                                triggeringAlarmMission = alarm.missionType
                                                ToneGenerator.playAlarm(onLoop = true)
                                                activeSubScreen = "alarm_trigger"
                                            },
                                            onNavigateToFocus = { activeSubScreen = "focus_mode" },
                                            onNavigateToSettings = { activeSubScreen = "settings" },
                                            onNavigateToPermissions = { activeSubScreen = "permission_health" },
                                            onNavigateToFinance = { currentTab = MainTab.FINANCE },
                                            onTaskChecked = { task ->
                                                scope.launch {
                                                    val updated = task.copy(isCompleted = !task.isCompleted)
                                                    database.taskDao().updateTask(updated)
                                                    if (updated.isCompleted) {
                                                        ToneGenerator.playSuccessChime()
                                                        eventBus.postEvent(
                                                            LifeEvent.TaskCompleted(
                                                                taskId = task.id,
                                                                title = task.title,
                                                                skillId = task.linkedSkillId,
                                                                goalId = task.linkedGoalId,
                                                                challengeId = task.linkedChallengeId,
                                                                difficulty = task.difficulty
                                                            )
                                                        )
                                                    }
                                                }
                                            }
                                        )
                                    }

                                    MainTab.SCHEDULE -> {
                                        ScheduleScreen(
                                            tasks = tasks,
                                            skills = skills,
                                            goals = goals,
                                            energyProfile = userPrefs.energyProfile,
                                            onAddTask = { task ->
                                                scope.launch {
                                                    database.taskDao().insertTask(task)
                                                    ToneGenerator.playSuccessChime()
                                                }
                                            },
                                            onToggleTask = { task ->
                                                scope.launch {
                                                    val updated = task.copy(isCompleted = !task.isCompleted)
                                                    database.taskDao().updateTask(updated)
                                                    if (updated.isCompleted) {
                                                        ToneGenerator.playSuccessChime()
                                                        eventBus.postEvent(
                                                            LifeEvent.TaskCompleted(
                                                                taskId = task.id,
                                                                title = task.title,
                                                                skillId = task.linkedSkillId,
                                                                goalId = task.linkedGoalId,
                                                                challengeId = task.linkedChallengeId,
                                                                difficulty = task.difficulty
                                                            )
                                                        )
                                                    }
                                                }
                                            },
                                            onDeleteTask = { task ->
                                                scope.launch { database.taskDao().deleteTask(task) }
                                            },
                                            onBatchReschedule = { rescheduledTasks ->
                                                scope.launch {
                                                    database.taskDao().insertTasks(rescheduledTasks)
                                                    ToneGenerator.playSuccessChime()
                                                }
                                            },
                                            onNavigateToFocus = { activeSubScreen = "focus_mode" }
                                        )
                                    }

                                    MainTab.FINANCE -> {
                                        FinanceScreen(
                                            wallets = wallets,
                                            categories = categories,
                                            transactions = transactions,
                                            budgets = budgets,
                                            savingsFunds = savingsFunds,
                                            impulseItems = impulseItems,
                                            footballCards = footballCards,
                                            userLevel = userPrefs.level,
                                            userCoins = userPrefs.coins,
                                            onAddWallet = { wallet ->
                                                scope.launch {
                                                    database.financeDao().insertWallet(wallet)
                                                    ToneGenerator.playSuccessChime()
                                                }
                                            },
                                            onDeleteWallet = { wallet ->
                                                scope.launch {
                                                    database.financeDao().deleteWallet(wallet)
                                                }
                                            },
                                            onAddCategory = { cat ->
                                                scope.launch {
                                                    database.financeDao().insertCategory(cat)
                                                    ToneGenerator.playSuccessChime()
                                                }
                                            },
                                            onAddTransaction = { tx ->
                                                scope.launch {
                                                    database.financeDao().insertTransaction(tx)
                                                    // Update wallet balance
                                                    val wallet = database.financeDao().getAllWallets().first().find { it.id == tx.walletId }
                                                    if (wallet != null) {
                                                        val newBalance = if (tx.type == "INCOME") wallet.balance + tx.amount else wallet.balance - tx.amount
                                                        database.financeDao().updateWallet(wallet.copy(balance = newBalance))
                                                    }
                                                    // Auto-update or create budget for category if expense
                                                    if (tx.type == "EXPENSE") {
                                                        val currentMonth = java.text.SimpleDateFormat("yyyy-MM", java.util.Locale.getDefault()).format(java.util.Date())
                                                        val existingBudget = database.financeDao().getBudgetByCategory(tx.category, currentMonth)
                                                        if (existingBudget != null) {
                                                            database.financeDao().updateBudget(
                                                                existingBudget.copy(spentAmount = existingBudget.spentAmount + tx.amount)
                                                            )
                                                        } else {
                                                            database.financeDao().insertBudget(
                                                                BudgetEntity(
                                                                    category = tx.category,
                                                                    monthlyLimit = 3000000L,
                                                                    spentAmount = tx.amount,
                                                                    monthYear = currentMonth
                                                                )
                                                            )
                                                        }
                                                    }
                                                    ToneGenerator.playSuccessChime()
                                                }
                                            },
                                            onAddSavingsFund = { fund ->
                                                scope.launch {
                                                    database.financeDao().insertSavingsFund(fund)
                                                    ToneGenerator.playSuccessChime()
                                                }
                                            },
                                            onDeleteSavingsFund = { fund ->
                                                scope.launch {
                                                    database.financeDao().deleteSavingsFund(fund)
                                                }
                                            },
                                            onDepositFund = { fund, amount ->
                                                scope.launch {
                                                    database.financeDao().updateSavingsFund(fund.copy(currentAmount = fund.currentAmount + amount))
                                                    val primaryWallet = wallets.firstOrNull()
                                                    if (primaryWallet != null) {
                                                        database.financeDao().updateWallet(primaryWallet.copy(balance = maxOf(0L, primaryWallet.balance - amount)))
                                                    }
                                                    ToneGenerator.playSuccessChime()
                                                }
                                            },
                                            onWithdrawFund = { fund, amount ->
                                                scope.launch {
                                                    val withdrawAmt = minOf(amount, fund.currentAmount)
                                                    database.financeDao().updateSavingsFund(fund.copy(currentAmount = fund.currentAmount - withdrawAmt))
                                                    val primaryWallet = wallets.firstOrNull()
                                                    if (primaryWallet != null) {
                                                        database.financeDao().updateWallet(primaryWallet.copy(balance = primaryWallet.balance + withdrawAmt))
                                                    }
                                                    ToneGenerator.playSuccessChime()
                                                }
                                            },
                                            onAddImpulseItem = { item ->
                                                scope.launch {
                                                    database.financeDao().insertImpulseItem(item)
                                                    ToneGenerator.playSuccessChime()
                                                }
                                            },
                                            onResolveImpulseItem = { item, rejectAndSave ->
                                                scope.launch {
                                                    if (rejectAndSave) {
                                                        database.financeDao().updateImpulseItem(item.copy(status = "REJECTED_SAVED"))
                                                        eventBus.postEvent(
                                                            LifeEvent.ImpulseRejected(
                                                                itemId = item.id,
                                                                itemName = item.name,
                                                                savedPrice = item.price
                                                            )
                                                        )
                                                    } else {
                                                        database.financeDao().updateImpulseItem(item.copy(status = "APPROVED_BOUGHT"))
                                                        eventBus.postEvent(
                                                            LifeEvent.ImpulseApproved(
                                                                itemId = item.id,
                                                                itemName = item.name,
                                                                price = item.price
                                                            )
                                                        )
                                                    }
                                                    ToneGenerator.playSuccessChime()
                                                }
                                            },
                                            onUnlockFootballCard = { card ->
                                                scope.launch {
                                                    if (!card.isOwned && userPrefs.coins >= card.costCoins) {
                                                        val spent = userPrefsRepo.spendCoins(card.costCoins)
                                                        if (spent) {
                                                            database.financeDao().updateFootballCard(card.copy(isOwned = true))
                                                            ToneGenerator.playSuccessChime()
                                                            val clubCards = database.financeDao().getAllFootballCards().first().filter { it.clubName == card.clubName }
                                                            val allOwned = clubCards.all { it.isOwned || it.id == card.id }
                                                            if (allOwned) {
                                                                userPrefsRepo.addXpAndCoins(500, 1000)
                                                                ToneGenerator.playSuccessChime()
                                                            }
                                                        }
                                                    }
                                                }
                                            },
                                            onOpenFifaPack = { pack, wonCard, isDuplicate, coinsRefunded ->
                                                scope.launch {
                                                    userPrefsRepo.spendCoins(pack.costCoins)
                                                    if (isDuplicate) {
                                                        userPrefsRepo.addXpAndCoins(20, coinsRefunded)
                                                    } else {
                                                        database.financeDao().updateFootballCard(wonCard.copy(isOwned = true))
                                                        userPrefsRepo.addXpAndCoins(50, 0)
                                                    }
                                                    ToneGenerator.playSuccessChime()
                                                }
                                            },
                                            onClaimClubReward = { clubName ->
                                                scope.launch {
                                                    userPrefsRepo.addXpAndCoins(500, 1000)
                                                    ToneGenerator.playSuccessChime()
                                                }
                                            },
                                            onAddFootballCard = { card ->
                                                scope.launch {
                                                    database.financeDao().insertFootballCard(card)
                                                    ToneGenerator.playSuccessChime()
                                                }
                                            },
                                            onImportFootballCards = { cards ->
                                                scope.launch {
                                                    database.financeDao().insertFootballCards(cards)
                                                    ToneGenerator.playSuccessChime()
                                                }
                                            },
                                            bankLoans = bankLoans,
                                            loanRepayments = loanRepayments,
                                            onAddBankLoan = { loan ->
                                                scope.launch {
                                                    val id = database.financeDao().insertBankLoan(loan)
                                                    if (loan.isReminderEnabled) {
                                                        com.example.service.ReminderScheduler.scheduleDebtPaymentReminder(
                                                            this@MainActivity,
                                                            id,
                                                            loan.bankName,
                                                            loan.monthlyEstimatedPayment,
                                                            loan.duePaymentDay,
                                                            loan.reminderDaysBefore
                                                        )
                                                    }
                                                    ToneGenerator.playSuccessChime()
                                                }
                                            },
                                            onUpdateBankLoan = { loan ->
                                                scope.launch {
                                                    database.financeDao().updateBankLoan(loan)
                                                    if (loan.isReminderEnabled) {
                                                        com.example.service.ReminderScheduler.scheduleDebtPaymentReminder(
                                                            this@MainActivity,
                                                            loan.id,
                                                            loan.bankName,
                                                            loan.monthlyEstimatedPayment,
                                                            loan.duePaymentDay,
                                                            loan.reminderDaysBefore
                                                        )
                                                    }
                                                    ToneGenerator.playSuccessChime()
                                                }
                                            },
                                            onDeleteBankLoan = { loan ->
                                                scope.launch {
                                                    database.financeDao().deleteBankLoan(loan)
                                                }
                                            },
                                            onPayLoan = { loan, principal, interest, walletId, note ->
                                                scope.launch {
                                                    val totalPayment = principal + interest
                                                    val newRemaining = maxOf(0L, loan.remainingAmount - principal)
                                                    database.financeDao().updateBankLoan(
                                                        loan.copy(
                                                            remainingAmount = newRemaining,
                                                            isPaidOff = newRemaining <= 0
                                                        )
                                                    )
                                                    database.financeDao().insertRepayment(
                                                        LoanRepaymentEntity(
                                                            loanId = loan.id,
                                                            paymentDate = System.currentTimeMillis(),
                                                            principalPaid = principal,
                                                            interestPaid = interest,
                                                            totalPaid = totalPayment,
                                                            walletId = walletId,
                                                            note = note
                                                        )
                                                    )
                                                    if (walletId != null) {
                                                        val targetWallet = wallets.find { it.id == walletId }
                                                        if (targetWallet != null) {
                                                            val newBalance = maxOf(0L, targetWallet.balance - totalPayment)
                                                            database.financeDao().updateWallet(targetWallet.copy(balance = newBalance))
                                                            database.financeDao().insertTransaction(
                                                                TransactionEntity(
                                                                    walletId = targetWallet.id,
                                                                    amount = totalPayment,
                                                                    type = "EXPENSE",
                                                                    category = "Trả nợ ngân hàng",
                                                                    note = "Trả nợ: ${loan.bankName} - ${loan.loanName} ($note)",
                                                                    isConfirmed = true
                                                                )
                                                            )
                                                        }
                                                    }
                                                    database.eventLogDao().insertEvent(
                                                        EventLogEntity(
                                                            eventType = "LOAN_REPAYMENT",
                                                            description = "Đã thanh toán ${Formatters.formatVnd(totalPayment)} cho khoản vay ${loan.bankName}",
                                                            savedAmount = 0
                                                        )
                                                    )
                                                    ToneGenerator.playSuccessChime()
                                                }
                                            }
                                        )
                                    }

                                    MainTab.GOALS -> {
                                        GoalsScreen(
                                            goals = goals,
                                            skills = skills,
                                            visionItems = visionItems,
                                            onAddGoal = { goal ->
                                                scope.launch {
                                                    database.goalDao().insertGoal(goal)
                                                    ToneGenerator.playSuccessChime()
                                                }
                                            },
                                            onUpdateGoalKpi = { goal, newKpi ->
                                                scope.launch {
                                                    database.goalDao().updateGoal(goal.copy(currentKpi = newKpi))
                                                    eventBus.postEvent(
                                                        LifeEvent.GoalKpiUpdated(
                                                            goalId = goal.id,
                                                            newKpi = newKpi,
                                                            targetKpi = goal.targetKpi
                                                        )
                                                    )
                                                    ToneGenerator.playSuccessChime()
                                                }
                                            },
                                            onAddTasksFromAi = { aiTasks ->
                                                scope.launch {
                                                    database.taskDao().insertTasks(aiTasks)
                                                    ToneGenerator.playSuccessChime()
                                                }
                                            },
                                            onAddSkillXp = { skill, addedXp ->
                                                scope.launch {
                                                    val newXp = skill.currentXp + addedXp
                                                    if (newXp >= skill.requiredXp) {
                                                        database.skillDao().updateSkill(
                                                            skill.copy(
                                                                level = skill.level + 1,
                                                                currentXp = newXp - skill.requiredXp,
                                                                requiredXp = (skill.requiredXp * 1.5f).toInt()
                                                            )
                                                        )
                                                        eventBus.postEvent(LifeEvent.SkillLeveledUp(skill.id, skill.title, skill.level + 1))
                                                    } else {
                                                        database.skillDao().updateSkill(skill.copy(currentXp = newXp))
                                                    }
                                                    ToneGenerator.playSuccessChime()
                                                }
                                            },
                                            onAddVisionItem = { item ->
                                                scope.launch {
                                                    database.goalDao().insertVisionBoardItem(item)
                                                    ToneGenerator.playSuccessChime()
                                                }
                                            }
                                        )
                                    }

                                    MainTab.CHALLENGES -> {
                                        ChallengesScreen(
                                            challenges = challenges,
                                            spinTickets = userPrefs.spinTickets,
                                            rewards = rewards,
                                            spinHistory = spinHistory,
                                            onCheckInChallenge = { challenge ->
                                                scope.launch {
                                                    val newStreak = challenge.currentStreak + 1
                                                    val updated = challenge.copy(
                                                        currentStreak = newStreak,
                                                        bestStreak = maxOf(challenge.bestStreak, newStreak),
                                                        lastCheckInDate = java.text.SimpleDateFormat("yyyy-MM-dd", java.util.Locale.getDefault()).format(java.util.Date())
                                                    )
                                                    database.challengeDao().updateChallenge(updated)
                                                    ToneGenerator.playSuccessChime()

                                                    // If cooking challenge completed, trigger rule
                                                    if (challenge.title.contains("nấu ăn", ignoreCase = true)) {
                                                        eventBus.postEvent(LifeEvent.CookingChallengeCompleted(challenge.id))
                                                    } else {
                                                        eventBus.postEvent(LifeEvent.ChallengeStreakIncreased(challenge.id, challenge.title, newStreak))
                                                    }
                                                }
                                            },
                                            onAddChallenge = { challenge ->
                                                scope.launch {
                                                    database.challengeDao().insertChallenge(challenge)
                                                    ToneGenerator.playSuccessChime()
                                                }
                                            },
                                            onSpinWheelResult = { reward ->
                                                scope.launch {
                                                    userPrefsRepo.addSpinTickets(-1)
                                                    database.challengeDao().insertSpinHistory(
                                                        SpinHistoryEntity(
                                                            rewardTitle = reward.title,
                                                            rewardDetails = "Phần thưởng loại: ${reward.type}"
                                                        )
                                                    )
                                                    eventBus.postEvent(
                                                        LifeEvent.WheelRewardWon(
                                                            rewardTitle = reward.title,
                                                            isMonetary = reward.type == "MONETARY_ENTERTAINMENT",
                                                            amount = reward.valueAmount
                                                        )
                                                    )
                                                }
                                            }
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
