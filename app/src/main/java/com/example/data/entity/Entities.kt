package com.example.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "alarms")
data class AlarmEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val title: String,
    val hour: Int,
    val minute: Int,
    val daysOfWeek: String, // e.g. "1,2,3,4,5" (Mon-Fri) or "0,1,2,3,4,5,6"
    val isEnabled: Boolean = true,
    val missionType: String = "MATH", // "MATH", "BARCODE", "SMILE", "SHAKE"
    val missionConfig: String = "NORMAL", // Difficulty or barcode string or target shake count
    val gradualVolume: Boolean = true,
    val vibrate: Boolean = true,
    val ringtoneName: String = "Tiếng chuông bình minh",
    val isSmartWakeEnabled: Boolean = true,
    val smartWakeWindowMinutes: Int = 20 // 15 or 30 min window
)

@Entity(tableName = "sleep_logs")
data class SleepLogEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val date: String, // YYYY-MM-DD
    val bedtime: Long, // timestamp
    val wakeTime: Long, // timestamp
    val durationMinutes: Int,
    val qualityScore: Int, // 0 - 100
    val movementEventsCount: Int = 12,
    val note: String = "Giấc ngủ phục hồi tốt"
)

@Entity(tableName = "tasks")
data class TaskEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val title: String,
    val description: String = "",
    val estimatedMinutes: Int = 45,
    val difficulty: String = "MEDIUM", // "EASY", "MEDIUM", "HARD"
    val deadlineDate: String = "", // YYYY-MM-DD
    val scheduledTimeHour: Int = 9,
    val scheduledTimeMinute: Int = 0,
    val recurrence: String = "NONE", // "NONE", "DAILY", "WEEKLY", "MONTHLY"
    val isCompleted: Boolean = false,
    val linkedSkillId: Long? = null,
    val linkedGoalId: Long? = null,
    val linkedChallengeId: Long? = null,
    val energyTag: String = "HIGH_FOCUS", // "HIGH_FOCUS", "CREATIVE", "ROUTINE", "RECOVERY"
    val isRescheduled: Boolean = false
)

@Entity(tableName = "goals")
data class GoalEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val title: String,
    val visionDescription: String,
    val timeframeType: String = "1_YEAR", // "1_YEAR", "5_YEAR"
    val targetDate: String,
    val currentKpi: Float,
    val targetKpi: Float,
    val kpiUnit: String, // e.g. "IELTS Band", "Triệu VNĐ", "Km"
    val milestonesJson: String = "[]",
    val linkedSkillBranch: String = "Trí tuệ"
)

@Entity(tableName = "skill_nodes")
data class SkillNodeEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val branchName: String, // "Sức khỏe", "Trí tuệ", "Sự nghiệp", "Tài chính", "Tinh thần"
    val title: String,
    val description: String,
    val level: Int = 1,
    val currentXp: Int = 0,
    val requiredXp: Int = 100,
    val isUnlocked: Boolean = true,
    val parentId: Long? = null,
    val iconName: String = "school",
    val canvasX: Float = 0f,
    val canvasY: Float = 0f
)

@Entity(tableName = "vision_board")
data class VisionBoardItemEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val type: String = "QUOTE", // "QUOTE", "IMAGE", "NOTE"
    val content: String,
    val title: String = "",
    val dateAdded: Long = System.currentTimeMillis()
)

@Entity(tableName = "challenges")
data class ChallengeEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val title: String,
    val description: String,
    val category: String, // "Sức khỏe", "Kỷ luật", "Học tập", "Tài chính"
    val targetDays: Int = 21,
    val currentStreak: Int = 0,
    val bestStreak: Int = 0,
    val lastCheckInDate: String = "", // YYYY-MM-DD
    val wagerAmount: Long = 0, // In points or virtual VND
    val wagerType: String = "POINTS", // "POINTS", "VIRTUAL_VND"
    val status: String = "ACTIVE", // "ACTIVE", "COMPLETED", "FAILED"
    val linkedFundId: Long? = null, // e.g. transfer 50k to fund when completed
    val habitRuleText: String = ""
)

@Entity(tableName = "partners")
data class PartnerEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val contactInfo: String,
    val relation: String = "Bạn thân"
)

@Entity(tableName = "categories")
data class CategoryEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val type: String = "EXPENSE", // "EXPENSE", "INCOME"
    val icon: String = "category",
    val colorHex: String = "#10B981"
)

@Entity(tableName = "wallets")
data class WalletEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val type: String, // "CASH", "BANK", "EWALLET", "SAVINGS"
    val balance: Long,
    val accountNumber: String = ""
)

@Entity(tableName = "transactions")
data class TransactionEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val walletId: Long,
    val amount: Long,
    val type: String, // "INCOME", "EXPENSE", "PLANNED_EXPENSE"
    val category: String,
    val note: String,
    val timestamp: Long = System.currentTimeMillis(),
    val isConfirmed: Boolean = true
)

@Entity(tableName = "budgets")
data class BudgetEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val category: String,
    val monthlyLimit: Long,
    val spentAmount: Long = 0,
    val monthYear: String // e.g. "2026-10"
)

@Entity(tableName = "savings_funds")
data class SavingsFundEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val targetAmount: Long,
    val currentAmount: Long = 0,
    val icon: String = "savings", // "piggy", "travel", "car", "house", "gadget"
    val colorHex: String = "#10B981",
    val deadlineDate: String = "2027-12-31",
    val linkedChallengeTrigger: String = ""
)

@Entity(tableName = "football_cards")
data class FootballCardEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val clubName: String,
    val leagueName: String = "Premier League", // "Premier League", "La Liga", "Serie A", "Bundesliga", "Ligue 1", "Icons", "Heroes"
    val playerName: String,
    val position: String, // "ST", "CF", "RW", "LW", "CAM", "CM", "CDM", "CB", "LB", "RB", "GK"
    val ovr: Int, // Overall rating: 50 - 99
    val rarity: String = "GOLD", // "BRONZE", "SILVER", "GOLD", "TOTW", "HERO", "ICON"
    val costCoins: Int = 150,
    val isOwned: Boolean = false,
    val nationality: String = "Anh",
    val pace: Int = 85,
    val shooting: Int = 82,
    val passing: Int = 84,
    val dribbling: Int = 86,
    val defending: Int = 70,
    val physical: Int = 75,
    val bio: String = "",
    val foot: String = "Phải",
    val weakFoot: Int = 4,
    val skillMoves: Int = 4,
    val avatarUrl: String = ""
)

@Entity(tableName = "impulse_items")
data class ImpulseItemEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val price: Long,
    val reason: String,
    val imageUrl: String = "",
    val createdAt: Long = System.currentTimeMillis(),
    val lockDurationHours: Int = 48,
    val status: String = "WAITING" // "WAITING", "REJECTED_SAVED", "APPROVED_BOUGHT"
)

@Entity(tableName = "virtual_base_items")
data class VirtualBaseItemEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val type: String, // "BUILDING", "DECORATION", "NATURE"
    val costCoins: Int,
    val isUnlocked: Boolean = false,
    val isEquipped: Boolean = false,
    val tier: Int = 1,
    val description: String = ""
)

@Entity(tableName = "wheel_rewards")
data class RewardEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val title: String,
    val probabilityWeight: Int = 10,
    val type: String = "XP", // "XP", "COINS", "MONETARY_ENTERTAINMENT", "TICKET"
    val valueAmount: Long = 50,
    val colorHex: String = "#6366F1"
)

@Entity(tableName = "spin_history")
data class SpinHistoryEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val rewardTitle: String,
    val rewardDetails: String,
    val timestamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "event_logs")
data class EventLogEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val eventType: String,
    val description: String,
    val timestamp: Long = System.currentTimeMillis(),
    val xpGained: Int = 0,
    val coinsGained: Int = 0,
    val savedAmount: Long = 0
)

@Entity(tableName = "bank_loans")
data class BankLoanEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val bankName: String, // e.g. "Vietcombank", "Techcombank", "MB Bank", "BIDV", "VPBank", "TPBank", "Shinhan Bank", "Khác"
    val loanName: String, // e.g. "Vay mua nhà căn hộ", "Vay mua ô tô", "Vay tín chấp tiêu dùng", "Thẻ tín dụng", "Vay kinh doanh"
    val contractNumber: String = "", // Mã hợp đồng / Số tài khoản vay
    val originalAmount: Long, // Tổng số tiền vay ban đầu (ví dụ: 600.000.000đ)
    val remainingAmount: Long, // Dư nợ gốc còn lại (ví dụ: 480.000.000đ)
    val interestRatePercent: Float, // Lãi suất %/năm (ví dụ: 8.5)
    val interestMethod: String = "REDUCING_BALANCE", // "REDUCING_BALANCE" (Dư nợ giảm dần) hoặc "FIXED_BALANCE" (Dư nợ gốc)
    val termMonths: Int = 36, // Thời hạn vay (ví dụ: 36 tháng)
    val startDate: String = "", // Ngày bắt đầu vay (YYYY-MM-DD)
    val duePaymentDay: Int = 15, // Ngày phải trả nợ hàng tháng (1-31)
    val monthlyEstimatedPayment: Long = 0L, // Số tiền gốc + lãi cần trả mỗi tháng
    val note: String = "", // Ghi chú chi tiết, tài sản bảo đảm, số điện thoại cán bộ tín dụng...
    val reminderDaysBefore: Int = 3, // Nhắc hẹn trước 1, 2, 3, 5, 7 ngày
    val isReminderEnabled: Boolean = true, // Bật/tắt nhắc nợ
    val isPaidOff: Boolean = false // Trạng thái đã tất toán
)

@Entity(tableName = "loan_repayments")
data class LoanRepaymentEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val loanId: Long,
    val paymentDate: Long = System.currentTimeMillis(),
    val principalPaid: Long, // Tiền gốc đã thanh toán
    val interestPaid: Long, // Tiền lãi đã thanh toán
    val totalPaid: Long, // Tổng tiền đã thanh toán
    val walletId: Long? = null, // Ví dùng thanh toán (nếu có)
    val note: String = "" // Ghi chú (Kỳ 1, Kỳ 2...)
)

@Entity(tableName = "reminders")
data class ReminderEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val type: String, // "EVENT", "TASK", "DEBT", "CHALLENGE", "IMPULSE", "BEDTIME", "POMODORO", "BUDGET"
    val targetId: Long = 0L,
    val title: String,
    val message: String,
    val triggerTime: Long, // timestamp
    val channelType: String = "DEFAULT", // "ALARM", "SCHEDULE", "FINANCE", "FOCUS", "IMPULSE", "CHALLENGE"
    val isCompleted: Boolean = false,
    val extraData: String = ""
)
