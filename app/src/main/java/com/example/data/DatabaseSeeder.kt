package com.example.data

import com.example.data.entity.*
import kotlinx.coroutines.flow.first

object DatabaseSeeder {

    suspend fun seedIfEmpty(database: AppDatabase) {
        val categories = database.financeDao().getAllCategories().first()
        if (categories.isNotEmpty()) return

        seedStarterData(database)
    }

    suspend fun resetToProductionClean(database: AppDatabase) {
        // Clear all personal user records and simulations
        database.alarmDao().deleteAllAlarms()
        database.alarmDao().deleteAllSleepLogs()
        database.taskDao().deleteAllTasks()
        database.goalDao().deleteAllGoals()
        database.goalDao().deleteAllVisionBoardItems()
        database.challengeDao().deleteAllChallenges()
        database.challengeDao().deleteAllPartners()
        database.challengeDao().deleteAllSpinHistory()
        database.financeDao().deleteAllWallets()
        database.financeDao().deleteAllTransactions()
        database.financeDao().deleteAllBudgets()
        database.financeDao().deleteAllSavingsFunds()
        database.financeDao().deleteAllImpulseItems()
        database.financeDao().deleteAllBankLoans()
        database.financeDao().deleteAllLoanRepayments()
        database.financeDao().deleteAllFootballCards()
        database.eventLogDao().deleteAllEvents()
        database.reminderDao().deleteAllReminders()

        seedStarterData(database)
    }

    private suspend fun seedStarterData(database: AppDatabase) {
        // 1. Starter Skill Tree (Level 1, 0 XP, ready for real user growth)
        database.skillDao().insertSkills(
            listOf(
                SkillNodeEntity(id = 1, branchName = "Sức khỏe", title = "Thể lực & Vận động", description = "Rèn luyện sức bền tim mạch và thể chất dẻo dai", level = 1, currentXp = 0, requiredXp = 100, canvasX = 0.2f, canvasY = 0.25f, iconName = "directions_run"),
                SkillNodeEntity(id = 2, branchName = "Trí tuệ", title = "Ngoại ngữ & Kỹ năng", description = "Mở rộng ngôn ngữ và nâng cao tư duy học thuật", level = 1, currentXp = 0, requiredXp = 100, canvasX = 0.5f, canvasY = 0.2f, iconName = "translate"),
                SkillNodeEntity(id = 3, branchName = "Trí tuệ", title = "Đọc sách & Nghiên cứu", description = "Đọc, tích lũy và đúc kết tri thức mỗi tuần", level = 1, currentXp = 0, requiredXp = 100, canvasX = 0.75f, canvasY = 0.35f, iconName = "auto_stories"),
                SkillNodeEntity(id = 4, branchName = "Tài chính", title = "Tự chủ tài chính", description = "Kiểm soát dòng tiền, ngân sách và từ chối chi tiêu bốc đồng", level = 1, currentXp = 0, requiredXp = 100, canvasX = 0.3f, canvasY = 0.65f, iconName = "account_balance_wallet"),
                SkillNodeEntity(id = 5, branchName = "Tinh thần", title = "Kỷ luật & Thức dậy sớm", description = "Làm chủ buổi sáng, giữ vững sự tập trung và kiến tạo tương lai", level = 1, currentXp = 0, requiredXp = 100, canvasX = 0.6f, canvasY = 0.7f, iconName = "wb_sunny")
            )
        )

        // 2. Wheel Rewards Configuration (Official balance)
        database.challengeDao().insertRewards(
            listOf(
                RewardEntity(id = 1, title = "+100 XP Toàn Năng", probabilityWeight = 25, type = "XP", valueAmount = 100, colorHex = "#3B82F6"),
                RewardEntity(id = 2, title = "+60 Xu Xây Dựng", probabilityWeight = 25, type = "COINS", valueAmount = 60, colorHex = "#F59E0B"),
                RewardEntity(id = 3, title = "Mua sách mới (Trích Quỹ Thưởng)", probabilityWeight = 15, type = "MONETARY_ENTERTAINMENT", valueAmount = 150000, colorHex = "#10B981"),
                RewardEntity(id = 4, title = "Cà phê thư giãn cuối tuần", probabilityWeight = 15, type = "MONETARY_ENTERTAINMENT", valueAmount = 65000, colorHex = "#EC4899"),
                RewardEntity(id = 5, title = "Nghỉ ngơi 1 buổi tối không áp lực", probabilityWeight = 10, type = "XP", valueAmount = 50, colorHex = "#8B5CF6"),
                RewardEntity(id = 6, title = "+2 Vé Quay May Mắn", probabilityWeight = 10, type = "TICKET", valueAmount = 2, colorHex = "#06B6D4")
            )
        )

        // 3. Clean Starter Wallet (1 default cash wallet with 0 VND balance)
        database.financeDao().insertWallet(
            WalletEntity(
                id = 1,
                name = "Tiền mặt",
                type = "CASH",
                balance = 0L
            )
        )

        // 4. Standard Vietnamese Financial Categories
        database.financeDao().insertCategories(
            listOf(
                CategoryEntity(id = 1, name = "Ăn uống & Sinh hoạt", type = "EXPENSE", colorHex = "#10B981"),
                CategoryEntity(id = 2, name = "Mua sắm cá nhân", type = "EXPENSE", colorHex = "#F59E0B"),
                CategoryEntity(id = 3, name = "Phát triển bản thân", type = "EXPENSE", colorHex = "#6366F1"),
                CategoryEntity(id = 4, name = "Tiền nhà & Hóa đơn", type = "EXPENSE", colorHex = "#EF4444"),
                CategoryEntity(id = 5, name = "Di chuyển & Xăng xe", type = "EXPENSE", colorHex = "#0EA5E9"),
                CategoryEntity(id = 6, name = "Lương & Thưởng", type = "INCOME", colorHex = "#10B981"),
                CategoryEntity(id = 7, name = "Thu nhập kinh doanh", type = "INCOME", colorHex = "#F59E0B"),
                CategoryEntity(id = 8, name = "Đầu tư & Cổ tức", type = "INCOME", colorHex = "#8B5CF6")
            )
        )

        // 5. Virtual Base Items (Starter studio apartment unlocked, others unlockable with coins)
        database.financeDao().insertBaseItems(
            listOf(
                VirtualBaseItemEntity(id = 1, name = "Căn hộ Studio Khởi Đầu", type = "BUILDING", costCoins = 0, isUnlocked = true, isEquipped = true, tier = 1, description = "Nơi khởi nguồn kỷ luật"),
                VirtualBaseItemEntity(id = 2, name = "Chậu Cây Trí Tuệ", type = "NATURE", costCoins = 100, isUnlocked = false, isEquipped = false, tier = 1, description = "Biểu tượng của sự sinh trưởng mỗi ngày"),
                VirtualBaseItemEntity(id = 3, name = "Bàn Làm Việc Tối Giản", type = "DECORATION", costCoins = 200, isUnlocked = false, isEquipped = false, tier = 1, description = "Không gian sản sinh các ý tưởng lớn"),
                VirtualBaseItemEntity(id = 4, name = "Giá Sách Cổ Điển", type = "DECORATION", costCoins = 350, isUnlocked = false, isEquipped = false, tier = 2, description = "Chứa đựng tri thức vô tận"),
                VirtualBaseItemEntity(id = 5, name = "Vườn Thiền Ban Công", type = "NATURE", costCoins = 500, isUnlocked = false, isEquipped = false, tier = 3, description = "Khoảng trời tĩnh lặng tái tạo năng lượng")
            )
        )

        // 6. FIFA Football Cards Catalog (113 cards across 5 leagues + Icons, 0 owned)
        database.financeDao().insertFootballCards(FootballCardSeederData.ALL_CARDS)

        // 7. Initial Welcome Event Log
        database.eventLogDao().insertEvent(
            EventLogEntity(
                eventType = "SYSTEM_INIT",
                description = "Chào mừng bạn đến với LifeOS! Bắt đầu hành trình làm chủ kỷ luật và cuộc sống.",
                xpGained = 0,
                coinsGained = 100,
                savedAmount = 0
            )
        )
    }
}
