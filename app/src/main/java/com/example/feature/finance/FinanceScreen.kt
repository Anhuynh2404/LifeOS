package com.example.feature.finance

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.core.ai.LifeOsAiService
import com.example.core.audio.ToneGenerator
import com.example.core.util.Formatters
import com.example.data.entity.*
import com.example.designsystem.components.*
import com.example.designsystem.theme.LifeTheme
import com.example.ui.components.*
import com.example.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FinanceScreen(
    wallets: List<WalletEntity>,
    categories: List<CategoryEntity>,
    transactions: List<TransactionEntity>,
    budgets: List<BudgetEntity>,
    savingsFunds: List<SavingsFundEntity>,
    impulseItems: List<ImpulseItemEntity>,
    footballCards: List<FootballCardEntity>,
    userLevel: Int,
    userCoins: Int,
    bankLoans: List<BankLoanEntity> = emptyList(),
    loanRepayments: List<LoanRepaymentEntity> = emptyList(),
    onAddWallet: (WalletEntity) -> Unit,
    onDeleteWallet: (WalletEntity) -> Unit,
    onAddCategory: (CategoryEntity) -> Unit,
    onUpdateCategory: (CategoryEntity) -> Unit = {},
    onDeleteCategory: (CategoryEntity) -> Unit = {},
    onAddTransaction: (TransactionEntity) -> Unit,
    onAddSavingsFund: (SavingsFundEntity) -> Unit,
    onDeleteSavingsFund: (SavingsFundEntity) -> Unit,
    onDepositFund: (SavingsFundEntity, amount: Long) -> Unit,
    onWithdrawFund: (SavingsFundEntity, amount: Long) -> Unit,
    onAddImpulseItem: (ImpulseItemEntity) -> Unit,
    onResolveImpulseItem: (ImpulseItemEntity, rejectAndSave: Boolean) -> Unit,
    onUnlockFootballCard: (FootballCardEntity) -> Unit,
    onOpenFifaPack: (pack: FifaPackType, wonCard: FootballCardEntity, isDuplicate: Boolean, coinsRefunded: Int) -> Unit = { _, _, _, _ -> },
    onClaimClubReward: (clubName: String) -> Unit = {},
    onAddBankLoan: (BankLoanEntity) -> Unit = {},
    onUpdateBankLoan: (BankLoanEntity) -> Unit = {},
    onDeleteBankLoan: (BankLoanEntity) -> Unit = {},
    onPayLoan: (loan: BankLoanEntity, principal: Long, interest: Long, walletId: Long?, note: String) -> Unit = { _, _, _, _, _ -> },
    onAddFootballCard: (FootballCardEntity) -> Unit = {},
    onImportFootballCards: (List<FootballCardEntity>) -> Unit = {},
    initialTab: Int = 0,
    modifier: Modifier = Modifier
) {
    var selectedTab by remember { mutableIntStateOf(initialTab) } // 0: Tổng quan, 1: Nợ ngân hàng, 2: Heo Đất MoMo, 3: Phòng chờ, 4: FIFA Cards
    var showScanInvoiceDialog by remember { mutableStateOf(false) }
    var showAddTransactionDialog by remember { mutableStateOf(false) }
    var showAddWalletDialog by remember { mutableStateOf(false) }
    var showAddCategoryDialog by remember { mutableStateOf(false) }
    var showManageCategoriesDialog by remember { mutableStateOf(false) }
    var editingCategory by remember { mutableStateOf<CategoryEntity?>(null) }
    var showAddFundDialog by remember { mutableStateOf(false) }
    var showAddImpulseDialog by remember { mutableStateOf(false) }
    var showAddEditLoanDialog by remember { mutableStateOf(false) }
    var editingLoan by remember { mutableStateOf<BankLoanEntity?>(null) }
    var payingLoan by remember { mutableStateOf<BankLoanEntity?>(null) }
    var viewingHistoryLoan by remember { mutableStateOf<BankLoanEntity?>(null) }

    // FIFA Pack Opening & Collection States
    val context = LocalContext.current
    var fifaSubTab by remember { mutableIntStateOf(0) } // 0: Mở Gói Thẻ, 1: Bộ Sưu Tập
    var selectedLeague by remember { mutableStateOf("Tất cả") }
    var selectedClub by remember { mutableStateOf("Tất cả") }
    var statusFilter by remember { mutableStateOf("Tất cả") } // "Tất cả", "Đã có", "Chưa có"
    var selectedRarity by remember { mutableStateOf("Tất cả") }
    var packOpeningResult by remember { mutableStateOf<Triple<FootballCardEntity, Boolean, Int>?>(null) }
    var lastOpenedPack by remember { mutableStateOf<FifaPackType?>(null) }
    var showCreateCardDialog by remember { mutableStateOf(false) }
    var showImportExportCardDialog by remember { mutableStateOf(false) }

    fun executeOpenPack(pack: FifaPackType) {
        if (userCoins < pack.costCoins) return
        val currentPity = PityManager.getPityCount(context)
        val isPityTriggered = (currentPity >= PityManager.MAX_PITY) && (pack.id in listOf("PACK_GOLD", "PACK_MEGA", "PACK_ULTIMATE"))

        val eligiblePool = if (isPityTriggered) {
            footballCards.filter { it.rarity == "ICON" }
        } else {
            when (pack.id) {
                "PACK_ULTIMATE" -> {
                    val roll = (1..100).random()
                    if (roll <= 40) footballCards.filter { it.rarity == "ICON" }
                    else footballCards.filter { it.rarity == "HERO" }
                }
                "PACK_MEGA" -> {
                    val roll = (1..100).random()
                    if (roll <= 3) footballCards.filter { it.rarity == "ICON" }
                    else if (roll <= 15) footballCards.filter { it.rarity == "HERO" }
                    else if (roll <= 55) footballCards.filter { it.rarity == "TOTW" }
                    else footballCards.filter { it.rarity == "GOLD" && it.ovr >= 85 }
                }
                "PACK_GOLD" -> {
                    val roll = (1..100).random()
                    if (roll <= 1) footballCards.filter { it.rarity == "ICON" }
                    else if (roll <= 7) footballCards.filter { it.rarity == "HERO" }
                    else if (roll <= 25) footballCards.filter { it.rarity == "TOTW" }
                    else footballCards.filter { it.rarity == "GOLD" }
                }
                "PACK_SILVER" -> {
                    val roll = (1..100).random()
                    if (roll <= 1) footballCards.filter { it.rarity == "TOTW" }
                    else if (roll <= 24) footballCards.filter { it.rarity == "GOLD" && it.ovr <= 84 }
                    else footballCards.filter { it.rarity == "SILVER" }
                }
                else -> { // PACK_BRONZE
                    val roll = (1..100).random()
                    if (roll <= 20) footballCards.filter { it.rarity == "SILVER" }
                    else footballCards.filter { it.rarity == "BRONZE" }
                }
            }
        }.ifEmpty { footballCards }

        val wonCard = eligiblePool.random()
        val isDuplicate = wonCard.isOwned
        val coinsRefunded = if (isDuplicate) (pack.costCoins * 0.35f).toInt().coerceAtLeast(20) else 0

        if (wonCard.rarity == "ICON") {
            PityManager.resetPity(context)
        } else if (pack.id in listOf("PACK_GOLD", "PACK_MEGA", "PACK_ULTIMATE")) {
            PityManager.incrementPity(context)
        }

        lastOpenedPack = pack
        packOpeningResult = Triple(wonCard, isDuplicate, coinsRefunded)
        onOpenFifaPack(pack, wonCard, isDuplicate, coinsRefunded)
    }

    val totalBalance = wallets.sumOf { it.balance }
    val totalSavingsInFunds = savingsFunds.sumOf { it.currentAmount }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            LifeTopBar(
                title = "Tài Chính Chánh Niệm",
                actions = {
                    if (selectedTab == 0) {
                        IconButton(
                            onClick = { showScanInvoiceDialog = true },
                            modifier = Modifier.testTag("scan_invoice_button")
                        ) {
                            Icon(Icons.Default.ReceiptLong, contentDescription = "Quét hóa đơn", tint = LifeTheme.colors.financeStart)
                        }
                    }
                }
            )
        },
        floatingActionButton = {
            LifeFab(
                onClick = {
                    when (selectedTab) {
                        0 -> showAddTransactionDialog = true
                        1 -> {
                            editingLoan = null
                            showAddEditLoanDialog = true
                        }
                        2 -> showAddFundDialog = true
                        3 -> showAddImpulseDialog = true
                        4 -> fifaSubTab = 0
                    }
                },
                icon = if (selectedTab == 4) Icons.Default.CardGiftcard else if (selectedTab == 1) Icons.Default.AccountBalance else Icons.Default.Add,
                customGradient = LifeTheme.colors.financeGradient,
                testTag = "finance_fab",
                modifier = Modifier.padding(bottom = 72.dp)
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            LifeSegmentedControl(
                items = listOf("Tổng Quan", "Nợ Ngân Hàng", "Quỹ Tiết Kiệm", "Phòng Chờ (48h)", "⚽ Thẻ FIFA"),
                selectedIndex = selectedTab,
                onItemSelected = { selectedTab = it },
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
            )

            when (selectedTab) {
                0 -> {
                    // Overview: Wallets, Budgets, Charts, Transactions
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(horizontal = 16.dp),
                        contentPadding = PaddingValues(top = 16.dp, bottom = 90.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        // Total Balance Card
                        item {
                            LifeCard(
                                variant = LifeCardVariant.GRADIENT,
                                gradient = LifeTheme.colors.financeGradient,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Column(modifier = Modifier.padding(18.dp)) {
                                    Text(
                                        "Tổng Số Dư Khả Dụng Trong Các Ví",
                                        style = LifeTheme.typography.labelMedium,
                                        color = Color.White.copy(alpha = 0.85f)
                                    )
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        text = Formatters.formatVnd(totalBalance),
                                        style = LifeTheme.typography.displayLarge,
                                        fontWeight = FontWeight.Bold,
                                        color = Color.White
                                    )
                                    Spacer(modifier = Modifier.height(14.dp))

                                    // Wallets Breakdown Row with Add Wallet button
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(
                                            "Danh Sách Ví Của Bạn",
                                            fontWeight = FontWeight.Bold,
                                            style = LifeTheme.typography.labelMedium,
                                            color = Color.White
                                        )
                                        TextButton(onClick = { showAddWalletDialog = true }) {
                                            Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp), tint = Color.White)
                                            Spacer(modifier = Modifier.width(4.dp))
                                            Text("Tạo Ví Mới", color = Color.White, style = LifeTheme.typography.labelSmall)
                                        }
                                    }

                                    LazyRow(
                                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                                        modifier = Modifier.fillMaxWidth()
                                    ) {
                                        items(wallets) { w ->
                                            Surface(
                                                shape = LifeTheme.shapes.medium,
                                                color = Color.White.copy(alpha = 0.2f),
                                                border = androidx.compose.foundation.BorderStroke(1.dp, Color.White.copy(alpha = 0.35f))
                                            ) {
                                                Column(modifier = Modifier.padding(10.dp)) {
                                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                                        Icon(
                                                            imageVector = when (w.type) {
                                                                "BANK" -> Icons.Default.AccountBalance
                                                                "EWALLET" -> Icons.Default.PhoneAndroid
                                                                else -> Icons.Default.Payments
                                                            },
                                                            contentDescription = null,
                                                            tint = Color.White,
                                                            modifier = Modifier.size(16.dp)
                                                        )
                                                        Spacer(modifier = Modifier.width(6.dp))
                                                        Text(w.name, style = LifeTheme.typography.labelMedium, fontWeight = FontWeight.SemiBold, color = Color.White)
                                                    }
                                                    Spacer(modifier = Modifier.height(4.dp))
                                                    Text(Formatters.formatVnd(w.balance), fontWeight = FontWeight.Bold, style = LifeTheme.typography.bodyMedium, color = Color.White)
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }

                        // Categories quick bar
                        item {
                            LifeCard(
                                variant = LifeCardVariant.SURFACE,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(
                                            "Danh Mục Thu Chi",
                                            fontWeight = FontWeight.Bold,
                                            style = LifeTheme.typography.titleMedium,
                                            maxLines = 1,
                                            overflow = TextOverflow.Ellipsis
                                        )
                                        Text(
                                            "${categories.size} danh mục",
                                            style = LifeTheme.typography.labelSmall,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Row(
                                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Surface(
                                            shape = RoundedCornerShape(8.dp),
                                            color = LifeTheme.colors.brandPrimary.copy(alpha = 0.12f),
                                            modifier = Modifier.clickable { showManageCategoriesDialog = true }
                                        ) {
                                            Row(
                                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp),
                                                verticalAlignment = Alignment.CenterVertically
                                            ) {
                                                Icon(
                                                    Icons.Default.Tune,
                                                    contentDescription = null,
                                                    modifier = Modifier.size(14.dp),
                                                    tint = LifeTheme.colors.brandPrimary
                                                )
                                                Spacer(modifier = Modifier.width(4.dp))
                                                Text(
                                                    "Quản Lý",
                                                    color = LifeTheme.colors.brandPrimary,
                                                    style = LifeTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold)
                                                )
                                            }
                                        }
                                        Surface(
                                            shape = RoundedCornerShape(8.dp),
                                            color = LifeTheme.colors.financeStart.copy(alpha = 0.12f),
                                            modifier = Modifier.clickable { showAddCategoryDialog = true }
                                        ) {
                                            Row(
                                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp),
                                                verticalAlignment = Alignment.CenterVertically
                                            ) {
                                                Icon(
                                                    Icons.Default.Add,
                                                    contentDescription = null,
                                                    modifier = Modifier.size(14.dp),
                                                    tint = LifeTheme.colors.financeStart
                                                )
                                                Spacer(modifier = Modifier.width(3.dp))
                                                Text(
                                                    "Thêm",
                                                    color = LifeTheme.colors.financeStart,
                                                    style = LifeTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold)
                                                )
                                            }
                                        }
                                    }
                                }
                                if (categories.isNotEmpty()) {
                                    Spacer(modifier = Modifier.height(10.dp))
                                    LazyRow(
                                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                                        modifier = Modifier.fillMaxWidth()
                                    ) {
                                        items(categories) { cat ->
                                            Surface(
                                                shape = RoundedCornerShape(8.dp),
                                                color = if (cat.type == "EXPENSE") LifeTheme.colors.error.copy(alpha = 0.12f) else LifeTheme.colors.success.copy(alpha = 0.12f),
                                                border = androidx.compose.foundation.BorderStroke(
                                                    1.dp,
                                                    if (cat.type == "EXPENSE") LifeTheme.colors.error.copy(alpha = 0.3f) else LifeTheme.colors.success.copy(alpha = 0.3f)
                                                ),
                                                modifier = Modifier.clickable { editingCategory = cat }
                                            ) {
                                                Row(
                                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                                                    verticalAlignment = Alignment.CenterVertically
                                                ) {
                                                    Text(if (cat.type == "EXPENSE") "🔻" else "🔺", fontSize = 11.sp)
                                                    Spacer(modifier = Modifier.width(4.dp))
                                                    Text(
                                                        cat.name,
                                                        style = LifeTheme.typography.labelSmall.copy(fontWeight = FontWeight.SemiBold),
                                                        color = if (cat.type == "EXPENSE") LifeTheme.colors.error else LifeTheme.colors.success
                                                    )
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }

                        // Bank Loans Quick Summary Card in Overview
                        val activeBankLoans = bankLoans.filter { !it.isPaidOff && it.remainingAmount > 0 }
                        val totalBankDebtRemaining = activeBankLoans.sumOf { it.remainingAmount }
                        val dueSoonBankLoansCount = activeBankLoans.count { BankLoanHelper.isDueSoon(it) }

                        if (bankLoans.isNotEmpty()) {
                            item {
                                LifeCard(
                                    variant = LifeCardVariant.SURFACE,
                                    onClick = { selectedTab = 1 },
                                    modifier = Modifier.fillMaxWidth().testTag("overview_bank_loans_card")
                                ) {
                                    Column(modifier = Modifier.padding(14.dp)) {
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Row(verticalAlignment = Alignment.CenterVertically) {
                                                Box(
                                                    modifier = Modifier
                                                        .size(36.dp)
                                                        .clip(RoundedCornerShape(8.dp))
                                                        .background(LifeTheme.colors.brandPrimary.copy(alpha = 0.12f)),
                                                    contentAlignment = Alignment.Center
                                                ) {
                                                    Icon(
                                                        Icons.Default.AccountBalance,
                                                        contentDescription = null,
                                                        tint = LifeTheme.colors.brandPrimary,
                                                        modifier = Modifier.size(20.dp)
                                                    )
                                                }
                                                Spacer(modifier = Modifier.width(10.dp))
                                                Column {
                                                    Text("Quản Lý Khoản Nợ Ngân Hàng", fontWeight = FontWeight.Bold, style = LifeTheme.typography.titleSmall)
                                                    Text(
                                                        "${activeBankLoans.size} khoản vay đang trả nợ",
                                                        style = LifeTheme.typography.bodySmall,
                                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                                    )
                                                }
                                            }
                                            if (dueSoonBankLoansCount > 0) {
                                                LifeBadge(text = "⚠️ $dueSoonBankLoansCount sắp đến hạn", style = LifeBadgeStyle.WARNING)
                                            } else {
                                                LifeBadge(text = "Xem chi tiết →", style = LifeBadgeStyle.NEUTRAL)
                                            }
                                        }
                                        Spacer(modifier = Modifier.height(10.dp))
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Text(
                                                "Tổng dư nợ còn lại:",
                                                style = LifeTheme.typography.bodySmall,
                                                color = MaterialTheme.colorScheme.onSurfaceVariant
                                            )
                                            Text(
                                                Formatters.formatVnd(totalBankDebtRemaining),
                                                style = LifeTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                                                color = if (totalBankDebtRemaining > 0) LifeTheme.colors.error else LifeTheme.colors.success
                                            )
                                        }
                                    }
                                }
                            }
                        }

                        // Budgets Section with Dynamic Spending Calculation
                        item {
                            Text("Ngân Sách Tháng & Cảnh Báo", fontWeight = FontWeight.Bold, style = LifeTheme.typography.titleMedium)
                        }

                        items(budgets) { budget ->
                            val pct = (budget.spentAmount.toFloat() / budget.monthlyLimit).coerceIn(0f, 1.5f)
                            val isWarning = pct >= 0.8f && pct < 1.0f
                            val isExceeded = pct >= 1.0f

                            val statusColor = when {
                                isExceeded -> LifeTheme.colors.error
                                isWarning -> LifeTheme.colors.warning
                                else -> LifeTheme.colors.financeStart
                            }

                            LifeCard(
                                variant = LifeCardVariant.ELEVATED,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Column(modifier = Modifier.padding(14.dp)) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(budget.category, fontWeight = FontWeight.SemiBold, style = LifeTheme.typography.titleMedium)
                                        LifeBadge(
                                            text = if (isExceeded) "⚠️ Vượt hạn mức!" else if (isWarning) "⚡ Đạt ${(pct * 100).toInt()}%" else "An toàn",
                                            style = if (isExceeded) LifeBadgeStyle.ERROR else if (isWarning) LifeBadgeStyle.WARNING else LifeBadgeStyle.SUCCESS
                                        )
                                    }
                                    Spacer(modifier = Modifier.height(10.dp))
                                    LifeProgressBar(
                                        progress = pct.coerceAtMost(1f),
                                        customGradient = Brush.horizontalGradient(listOf(statusColor, statusColor)),
                                        trackColor = statusColor.copy(alpha = 0.2f),
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .height(8.dp)
                                    )
                                    Spacer(modifier = Modifier.height(8.dp))
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Text("Đã chi: ${Formatters.formatVnd(budget.spentAmount)}", style = LifeTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                        Text("Hạn mức: ${Formatters.formatVnd(budget.monthlyLimit)}", style = LifeTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                    }
                                }
                            }
                        }

                        // Recent Transactions
                        item {
                            Text("Lịch Sử Giao Dịch Gần Đây", fontWeight = FontWeight.Bold, style = LifeTheme.typography.titleMedium)
                        }

                        if (transactions.isEmpty()) {
                            item {
                                LifeEmptyState(
                                    title = "Chưa có giao dịch",
                                    description = "Bấm nút + bên dưới để ghi chép khoản thu chi đầu tiên của bạn.",
                                    icon = Icons.Default.ReceiptLong
                                )
                            }
                        } else {
                            items(transactions.take(8)) { tx ->
                                LifeCard(
                                    variant = LifeCardVariant.SURFACE,
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(14.dp),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Column {
                                            Text(tx.category, fontWeight = FontWeight.SemiBold, style = LifeTheme.typography.bodyMedium)
                                            Text(tx.note.ifBlank { "Không có ghi chú" }, style = LifeTheme.typography.bodySmall.copy(fontSize = 11.sp), color = MaterialTheme.colorScheme.onSurfaceVariant)
                                        }
                                        Text(
                                            text = "${if (tx.type == "INCOME") "+" else "-"}${Formatters.formatVnd(tx.amount)}",
                                            fontWeight = FontWeight.Bold,
                                            style = LifeTheme.typography.titleMedium,
                                            color = if (tx.type == "INCOME") LifeTheme.colors.financeStart else if (tx.type == "PLANNED_EXPENSE") LifeTheme.colors.alarmStart else LifeTheme.colors.error
                                        )
                                    }
                                }
                            }
                        }
                    }
                }

                1 -> {
                    BankLoansTabContent(
                        bankLoans = bankLoans,
                        loanRepayments = loanRepayments,
                        wallets = wallets,
                        onAddLoanClick = {
                            editingLoan = null
                            showAddEditLoanDialog = true
                        },
                        onEditLoanClick = { loan ->
                            editingLoan = loan
                            showAddEditLoanDialog = true
                        },
                        onDeleteLoanClick = onDeleteBankLoan,
                        onPayLoanClick = { loan ->
                            payingLoan = loan
                        },
                        onViewHistoryClick = { loan ->
                            viewingHistoryLoan = loan
                        },
                        onToggleReminder = { loan, enabled ->
                            onUpdateBankLoan(loan.copy(isReminderEnabled = enabled))
                        }
                    )
                }

                2 -> {
                    // MoMo-style Heo Đất & Savings Funds
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(horizontal = 16.dp),
                        contentPadding = PaddingValues(top = 16.dp, bottom = 90.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        // MoMo Heo Đất Hero Card
                        item {
                            LifeCard(
                                variant = LifeCardVariant.ELEVATED,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Column(modifier = Modifier.padding(18.dp)) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Surface(
                                                shape = CircleShape,
                                                color = Color(0xFFEC4899).copy(alpha = 0.15f),
                                                modifier = Modifier.size(46.dp)
                                            ) {
                                                Box(contentAlignment = Alignment.Center) {
                                                    Icon(Icons.Default.Savings, contentDescription = null, tint = Color(0xFFBE185D), modifier = Modifier.size(26.dp))
                                                }
                                            }
                                            Spacer(modifier = Modifier.width(12.dp))
                                            Column {
                                                Text("Heo Đất & Quỹ Tiết Kiệm", fontWeight = FontWeight.Bold, color = Color(0xFFBE185D), style = LifeTheme.typography.titleMedium)
                                                Text("Tích lũy đều đặn từng đồng nhỏ", style = LifeTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                            }
                                        }
                                        LifeButton(
                                            text = "Tạo Quỹ",
                                            onClick = { showAddFundDialog = true },
                                            leadingIcon = { Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp)) },
                                            size = LifeButtonSize.SMALL
                                        )
                                    }

                                    Spacer(modifier = Modifier.height(14.dp))
                                    Text("Tổng Tiền Đang Tích Lũy:", style = LifeTheme.typography.labelSmall, color = Color(0xFFBE185D))
                                    Text(
                                        text = Formatters.formatVnd(totalSavingsInFunds),
                                        style = LifeTheme.typography.displayMedium,
                                        fontWeight = FontWeight.ExtraBold,
                                        color = Color(0xFFBE185D)
                                    )
                                }
                            }
                        }

                        items(savingsFunds) { fund ->
                            MomoSavingsCard(
                                fund = fund,
                                onQuickDeposit = { onDepositFund(fund, 100000) },
                                onWithdraw = { onWithdrawFund(fund, 50000) },
                                onDelete = { onDeleteSavingsFund(fund) }
                            )
                        }
                    }
                }

                3 -> {
                    // Impulse Control Room (48/72h Lock)
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(horizontal = 16.dp),
                        contentPadding = PaddingValues(top = 16.dp, bottom = 90.dp),
                        verticalArrangement = Arrangement.spacedBy(14.dp)
                    ) {
                        item {
                            LifeCard(
                                variant = LifeCardVariant.GRADIENT,
                                gradient = LifeTheme.colors.alarmGradient,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(16.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(Icons.Default.HourglassBottom, contentDescription = null, tint = Color.White, modifier = Modifier.size(32.dp))
                                    Spacer(modifier = Modifier.width(12.dp))
                                    Column {
                                        Text("Phòng Chờ Mua Sắm Bốc Đồng", fontWeight = FontWeight.Bold, color = Color.White, style = LifeTheme.typography.titleMedium)
                                        Text(
                                            "Khóa 48-72 giờ trước khi mua đồ không thiết yếu. Nếu từ chối mua, tiền sẽ được cộng vào quỹ tiết kiệm và thưởng Xu để mở thẻ cầu thủ FIFA!",
                                            style = LifeTheme.typography.bodySmall.copy(fontSize = 11.sp),
                                            color = Color.White.copy(alpha = 0.85f)
                                        )
                                    }
                                }
                            }
                        }

                        if (impulseItems.isEmpty()) {
                            item {
                                LifeEmptyState(
                                    title = "Phòng chờ trống",
                                    description = "Khi có món đồ muốn mua bốc đồng, hãy bấm nút + để đưa vào thử thách hạ nhiệt 48h.",
                                    icon = Icons.Default.HourglassEmpty
                                )
                            }
                        } else {
                            items(impulseItems) { item ->
                                ImpulseItemCard(
                                    item = item,
                                    onRejectAndSave = { onResolveImpulseItem(item, true) },
                                    onApproveAndBuy = { onResolveImpulseItem(item, false) }
                                )
                            }
                        }
                    }
                }

                4 -> {
                    // FIFA Football Cards Collection & Pack Opening
                    val allLeagues = listOf("Tất cả", "Premier League", "La Liga", "Serie A", "Bundesliga", "Ligue 1", "Icons", "Heroes")
                    val availableClubs = remember(footballCards, selectedLeague) {
                        val base = if (selectedLeague == "Tất cả") footballCards else footballCards.filter { it.leagueName == selectedLeague }
                        listOf("Tất cả") + base.map { it.clubName }.distinct()
                    }

                    val filteredCards = remember(footballCards, selectedLeague, selectedClub, statusFilter, selectedRarity) {
                        footballCards.filter { card ->
                            val matchLeague = selectedLeague == "Tất cả" || card.leagueName == selectedLeague
                            val matchClub = selectedClub == "Tất cả" || card.clubName == selectedClub
                            val matchRarity = selectedRarity == "Tất cả" || card.rarity.equals(selectedRarity, ignoreCase = true)
                            val matchStatus = when (statusFilter) {
                                "Đã có" -> card.isOwned
                                "Chưa có" -> !card.isOwned
                                else -> true
                            }
                            matchLeague && matchClub && matchRarity && matchStatus
                        }
                    }

                    val totalOwned = footballCards.count { it.isOwned }
                    val totalCards = footballCards.size

                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(horizontal = 16.dp),
                        contentPadding = PaddingValues(top = 16.dp, bottom = 90.dp),
                        verticalArrangement = Arrangement.spacedBy(14.dp)
                    ) {
                        // Header Banner
                        item {
                            LifeCard(
                                variant = LifeCardVariant.SURFACE,
                                shape = LifeTheme.shapes.large,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Column(modifier = Modifier.padding(18.dp)) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Column(modifier = Modifier.weight(1f)) {
                                            Text("⚽ FIFA ULTIMATE PACKS & CARDS", fontWeight = FontWeight.ExtraBold, color = GoldCoin, fontSize = 15.sp)
                                            Text("5 Giải đấu hàng đầu châu Âu & Icons Huyền Thoại", style = LifeTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                        }
                                        Surface(
                                            shape = LifeTheme.shapes.medium,
                                            color = GoldCoin.copy(alpha = 0.25f)
                                        ) {
                                            Row(
                                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                                                verticalAlignment = Alignment.CenterVertically
                                            ) {
                                                Icon(Icons.Default.MonetizationOn, contentDescription = null, tint = GoldCoin, modifier = Modifier.size(18.dp))
                                                Spacer(modifier = Modifier.width(4.dp))
                                                Text("$userCoins Xu", fontWeight = FontWeight.Bold, color = GoldCoin)
                                            }
                                        }
                                    }

                                    Spacer(modifier = Modifier.height(14.dp))

                                    // Switch between Pack Store & Collection
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .clip(RoundedCornerShape(12.dp))
                                            .background(LifeTheme.colors.surfaceHighlight)
                                            .padding(4.dp)
                                    ) {
                                        Box(
                                            modifier = Modifier
                                                .weight(1f)
                                                .clip(RoundedCornerShape(8.dp))
                                                .background(if (fifaSubTab == 0) GoldCoin else Color.Transparent)
                                                .clickable { fifaSubTab = 0 }
                                                .padding(vertical = 8.dp),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Text(
                                                "🏪 Cửa Hàng Gói Thẻ",
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 12.sp,
                                                color = if (fifaSubTab == 0) Color(0xFF451A03) else MaterialTheme.colorScheme.onSurface
                                            )
                                        }
                                        Box(
                                            modifier = Modifier
                                                .weight(1f)
                                                .clip(RoundedCornerShape(8.dp))
                                                .background(if (fifaSubTab == 1) GoldCoin else Color.Transparent)
                                                .clickable { fifaSubTab = 1 }
                                                .padding(vertical = 8.dp),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Text(
                                                "📖 Bộ Sưu Tập ($totalOwned/$totalCards)",
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 12.sp,
                                                color = if (fifaSubTab == 1) Color(0xFF451A03) else MaterialTheme.colorScheme.onSurface
                                            )
                                        }
                                    }
                                }
                            }
                        }

                        if (fifaSubTab == 0) {
                            // SubTab 0: FIFA Pack Store
                            // Pity System Card
                            item {
                                val pityCount = remember(packOpeningResult) { PityManager.getPityCount(context) }
                                Surface(
                                    shape = LifeTheme.shapes.medium,
                                    color = LifeTheme.colors.surfaceHighlight,
                                    border = CardDefaults.outlinedCardBorder().copy(brush = Brush.horizontalGradient(listOf(Color(0xFFF59E0B), Color(0xFF6366F1)))),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Row(
                                        modifier = Modifier.padding(14.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Box(
                                            modifier = Modifier
                                                .size(42.dp)
                                                .clip(CircleShape)
                                                .background(Color(0xFFF59E0B).copy(alpha = 0.15f)),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Icon(Icons.Default.Shield, contentDescription = null, tint = Color(0xFFF59E0B), modifier = Modifier.size(24.dp))
                                        }
                                        Spacer(modifier = Modifier.width(12.dp))
                                        Column(modifier = Modifier.weight(1f)) {
                                            Row(verticalAlignment = Alignment.CenterVertically) {
                                                Text("🛡️ Bảo Hiểm Huyền Thoại (Pity)", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = LifeTheme.colors.textPrimary)
                                                Spacer(modifier = Modifier.weight(1f))
                                                Text("$pityCount/${PityManager.MAX_PITY}", fontWeight = FontWeight.Black, fontSize = 13.sp, color = Color(0xFFF59E0B))
                                            }
                                            Spacer(modifier = Modifier.height(2.dp))
                                            Text(
                                                if (pityCount >= PityManager.MAX_PITY) "🔥 ĐÃ ĐẠT BẢO HIỂM! Gói cao cấp tiếp theo chắc chắn nhận ICON!"
                                                else "Mở ${PityManager.MAX_PITY - pityCount} gói cao cấp nữa mà chưa có Icon sẽ chắc chắn nhận 1 Icon!",
                                                style = LifeTheme.typography.bodySmall,
                                                color = LifeTheme.colors.textSecondary
                                            )
                                        }
                                    }
                                }
                            }

                            item {
                                Text(
                                    "CÁC GÓI THẺ ĐANG MỞ BÁN",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 13.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }

                            items(FIFA_PACKS) { pack ->
                                LifeCard(
                                    variant = LifeCardVariant.ELEVATED,
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Column(modifier = Modifier.padding(16.dp)) {
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            verticalAlignment = Alignment.Top
                                        ) {
                                            Row(verticalAlignment = Alignment.CenterVertically) {
                                                Surface(
                                                    shape = CircleShape,
                                                    color = pack.iconColor.copy(alpha = 0.15f),
                                                    modifier = Modifier.size(46.dp)
                                                ) {
                                                    Box(contentAlignment = Alignment.Center) {
                                                        Icon(Icons.Default.CardGiftcard, contentDescription = null, tint = pack.iconColor, modifier = Modifier.size(24.dp))
                                                    }
                                                }
                                                Spacer(modifier = Modifier.width(12.dp))
                                                Column {
                                                    Text(pack.name, fontWeight = FontWeight.Bold, style = LifeTheme.typography.titleMedium)
                                                    Text(pack.description, style = LifeTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                                }
                                            }
                                        }

                                        Spacer(modifier = Modifier.height(10.dp))

                                        // Drop rate badge
                                        Surface(
                                            shape = LifeTheme.shapes.small,
                                            color = pack.iconColor.copy(alpha = 0.1f),
                                            modifier = Modifier.fillMaxWidth()
                                        ) {
                                            Text(
                                                "🎯 Tỷ lệ trúng: ${pack.dropRateText}",
                                                style = LifeTheme.typography.labelSmall,
                                                fontWeight = FontWeight.SemiBold,
                                                color = pack.iconColor,
                                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                                            )
                                        }

                                        Spacer(modifier = Modifier.height(14.dp))

                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Row(verticalAlignment = Alignment.CenterVertically) {
                                                Icon(Icons.Default.MonetizationOn, contentDescription = null, tint = GoldCoin, modifier = Modifier.size(20.dp))
                                                Spacer(modifier = Modifier.width(4.dp))
                                                Text("${pack.costCoins} Xu", fontWeight = FontWeight.ExtraBold, fontSize = 16.sp, color = MaterialTheme.colorScheme.onSurface)
                                            }

                                            LifeButton(
                                                text = if (userCoins >= pack.costCoins) "Mở Gói Ngay" else "Thiếu Xu",
                                                onClick = { executeOpenPack(pack) },
                                                enabled = userCoins >= pack.costCoins,
                                                leadingIcon = { Icon(Icons.Default.AutoFixHigh, contentDescription = null, modifier = Modifier.size(16.dp)) },
                                                size = LifeButtonSize.MEDIUM
                                            )
                                        }
                                    }
                                }
                            }
                        } else {
                            // SubTab 1: Card Collection & Binder
                            item {
                                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                                    // Custom Card & CSV Importer buttons
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                                    ) {
                                        LifeButton(
                                            text = "⭐ Tự Tạo Thẻ",
                                            onClick = { showCreateCardDialog = true },
                                            variant = LifeButtonVariant.PRIMARY,
                                            size = LifeButtonSize.SMALL,
                                            modifier = Modifier.weight(1f)
                                        )
                                        LifeButton(
                                            text = "📁 Nhập/Xuất CSV",
                                            onClick = { showImportExportCardDialog = true },
                                            variant = LifeButtonVariant.OUTLINE,
                                            size = LifeButtonSize.SMALL,
                                            modifier = Modifier.weight(1f)
                                        )
                                    }

                                    // League Selector
                                    Text("Chọn Giải Đấu:", fontWeight = FontWeight.Bold, style = LifeTheme.typography.labelMedium)
                                    LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                        items(allLeagues) { league ->
                                            val countInLeague = if (league == "Tất cả") totalCards else footballCards.count { it.leagueName == league }
                                            val ownedInLeague = if (league == "Tất cả") totalOwned else footballCards.count { it.leagueName == league && it.isOwned }
                                            LifeChip(
                                                text = "$league ($ownedInLeague/$countInLeague)",
                                                selected = selectedLeague == league,
                                                onClick = {
                                                    selectedLeague = league
                                                    selectedClub = "Tất cả"
                                                }
                                            )
                                        }
                                    }

                                    // Club Selector
                                    Text("Chọn Câu Lạc Bộ:", fontWeight = FontWeight.Bold, style = LifeTheme.typography.labelMedium)
                                    LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                        items(availableClubs) { club ->
                                            val clubCards = if (club == "Tất cả") filteredCards else footballCards.filter { it.clubName == club }
                                            val clubOwned = clubCards.count { it.isOwned }
                                            LifeChip(
                                                text = if (club == "Tất cả") "Tất cả CLB" else "$club ($clubOwned/${clubCards.size})",
                                                selected = selectedClub == club,
                                                onClick = { selectedClub = club }
                                            )
                                        }
                                    }

                                    // Status & Rarity filter chips
                                    Text("Lọc Trạng Thái & Bậc Thẻ:", fontWeight = FontWeight.Bold, style = LifeTheme.typography.labelMedium)
                                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                        listOf("Tất cả", "Đã có", "Chưa có").forEach { status ->
                                            LifeChip(
                                                text = status,
                                                selected = statusFilter == status,
                                                onClick = { statusFilter = status }
                                            )
                                        }
                                    }

                                    LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                        items(listOf("Tất cả", "BRONZE", "SILVER", "GOLD", "TOTW", "HERO", "ICON")) { r ->
                                            LifeChip(
                                                text = if (r == "Tất cả") "Tất cả bậc" else r,
                                                selected = selectedRarity == r,
                                                onClick = { selectedRarity = r }
                                            )
                                        }
                                    }

                                    // Club completion celebration banner
                                    if (selectedClub != "Tất cả") {
                                        val thisClubCards = footballCards.filter { it.clubName == selectedClub }
                                        val isComplete = thisClubCards.isNotEmpty() && thisClubCards.all { it.isOwned }
                                        if (isComplete) {
                                            Surface(
                                                shape = LifeTheme.shapes.medium,
                                                color = Color(0xFFFEF3C7),
                                                border = CardDefaults.outlinedCardBorder().copy(brush = Brush.horizontalGradient(listOf(Color(0xFFF59E0B), Color(0xFFD97706)))),
                                                modifier = Modifier.fillMaxWidth()
                                            ) {
                                                Column(modifier = Modifier.padding(14.dp)) {
                                                    Text("🏆 HOÀN THÀNH TRỌN BỘ CLB $selectedClub!", fontWeight = FontWeight.Black, color = Color(0xFFB45309))
                                                    Text("Bạn đã sở hữu đầy đủ toàn bộ thẻ cầu thủ của $selectedClub!", style = LifeTheme.typography.bodySmall, color = Color(0xFF78350F))
                                                    Spacer(modifier = Modifier.height(10.dp))
                                                    LifeButton(
                                                        text = "Nhận Thưởng +500 XP & +1.000 Xu 🎁",
                                                        onClick = { onClaimClubReward(selectedClub) },
                                                        size = LifeButtonSize.MEDIUM
                                                    )
                                                }
                                            }
                                        }
                                    }
                                }
                            }

                            if (filteredCards.isEmpty()) {
                                item {
                                    Box(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(32.dp),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text("Không tìm thấy thẻ cầu thủ phù hợp", color = MaterialTheme.colorScheme.onSurfaceVariant)
                                    }
                                }
                            }

                            // 2-column Grid of FIFA Cards
                            items(filteredCards.chunked(2)) { pair ->
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                                ) {
                                    pair.forEach { card ->
                                        Box(modifier = Modifier.weight(1f)) {
                                            FifaPlayerCard(
                                                card = card,
                                                canUnlock = userCoins >= card.costCoins,
                                                onUnlock = { onUnlockFootballCard(card) },
                                                modifier = Modifier.fillMaxWidth()
                                            )
                                        }
                                    }
                                    if (pair.size == 1) {
                                        Spacer(modifier = Modifier.weight(1f))
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        // Dialogs
        if (showScanInvoiceDialog) {
            ScanInvoiceDialog(
                onDismiss = { showScanInvoiceDialog = false },
                onConfirmInvoice = { store, amount, category ->
                    val defaultWallet = wallets.firstOrNull()?.id ?: 1L
                    onAddTransaction(
                        TransactionEntity(
                            walletId = defaultWallet,
                            amount = amount,
                            type = "EXPENSE",
                            category = category,
                            note = "Hóa đơn quét: $store",
                            isConfirmed = true
                        )
                    )
                    showScanInvoiceDialog = false
                }
            )
        }

        if (showAddTransactionDialog) {
            AddTransactionDialog(
                wallets = wallets,
                categories = categories,
                onDismiss = { showAddTransactionDialog = false },
                onConfirm = { tx ->
                    onAddTransaction(tx)
                    showAddTransactionDialog = false
                }
            )
        }

        if (showAddWalletDialog) {
            AddWalletDialog(
                onDismiss = { showAddWalletDialog = false },
                onConfirm = { wallet ->
                    onAddWallet(wallet)
                    showAddWalletDialog = false
                }
            )
        }

        if (showAddCategoryDialog) {
            AddCategoryDialog(
                onDismiss = { showAddCategoryDialog = false },
                onConfirm = { cat ->
                    onAddCategory(cat)
                    showAddCategoryDialog = false
                }
            )
        }

        if (showManageCategoriesDialog) {
            ManageCategoriesDialog(
                categories = categories,
                onDismiss = { showManageCategoriesDialog = false },
                onAddNew = {
                    showManageCategoriesDialog = false
                    showAddCategoryDialog = true
                },
                onEditCategory = { cat ->
                    editingCategory = cat
                },
                onDeleteCategory = { cat ->
                    onDeleteCategory(cat)
                }
            )
        }

        editingCategory?.let { cat ->
            EditCategoryDialog(
                category = cat,
                onDismiss = { editingCategory = null },
                onConfirm = { updated ->
                    onUpdateCategory(updated)
                    editingCategory = null
                },
                onDelete = {
                    onDeleteCategory(cat)
                    editingCategory = null
                }
            )
        }

        if (showAddFundDialog) {
            AddSavingsFundDialog(
                onDismiss = { showAddFundDialog = false },
                onConfirm = { fund ->
                    onAddSavingsFund(fund)
                    showAddFundDialog = false
                }
            )
        }

        if (showAddImpulseDialog) {
            AddImpulseDialog(
                onDismiss = { showAddImpulseDialog = false },
                onConfirm = { item ->
                    onAddImpulseItem(item)
                    showAddImpulseDialog = false
                }
            )
        }

        // Bank Loan Dialogs
        if (showAddEditLoanDialog) {
            AddEditLoanDialog(
                initialLoan = editingLoan,
                onDismiss = {
                    showAddEditLoanDialog = false
                    editingLoan = null
                },
                onConfirm = { loan ->
                    if (editingLoan == null) {
                        onAddBankLoan(loan)
                    } else {
                        onUpdateBankLoan(loan)
                    }
                    showAddEditLoanDialog = false
                    editingLoan = null
                }
            )
        }

        payingLoan?.let { loan ->
            PayLoanDialog(
                loan = loan,
                wallets = wallets,
                onDismiss = { payingLoan = null },
                onConfirmPayment = { principal, interest, walletId, note ->
                    onPayLoan(loan, principal, interest, walletId, note)
                    payingLoan = null
                }
            )
        }

        viewingHistoryLoan?.let { loan ->
            LoanRepaymentsHistoryDialog(
                loan = loan,
                repayments = loanRepayments.filter { it.loanId == loan.id },
                onDismiss = { viewingHistoryLoan = null }
            )
        }

        // FIFA Pack Opening Reveal Suspense Dialog
        packOpeningResult?.let { (wonCard, isDuplicate, coinsRefunded) ->
            PackOpeningRevealDialog(
                openedCard = wonCard,
                isDuplicate = isDuplicate,
                coinsRefunded = coinsRefunded,
                onDismiss = { packOpeningResult = null },
                onOpenAnother = {
                    val currentPack = lastOpenedPack
                    packOpeningResult = null
                    currentPack?.let { executeOpenPack(it) }
                }
            )
        }

        if (showCreateCardDialog) {
            CreateCustomCardDialog(
                onDismiss = { showCreateCardDialog = false },
                onSave = { newCard ->
                    onAddFootballCard(newCard)
                    ToneGenerator.playSuccessChime()
                }
            )
        }

        if (showImportExportCardDialog) {
            ImportExportCardDialog(
                allCards = footballCards,
                onImportCards = { importedList ->
                    onImportFootballCards(importedList)
                    ToneGenerator.playSuccessChime()
                },
                onDismiss = { showImportExportCardDialog = false }
            )
        }
    }
}

// MoMo Style Savings Fund Card with Deposit & Withdraw
@Composable
fun MomoSavingsCard(
    fund: SavingsFundEntity,
    onQuickDeposit: () -> Unit,
    onWithdraw: () -> Unit,
    onDelete: () -> Unit
) {
    val progress = (fund.currentAmount.toFloat() / fund.targetAmount).coerceIn(0f, 1f)
    val pct = (progress * 100).toInt()

    LifeCard(
        variant = LifeCardVariant.ELEVATED,
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(42.dp)
                            .background(Color(0xFFFDF2F8), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Default.Savings, contentDescription = null, tint = Color(0xFFBE185D))
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(fund.name, fontWeight = FontWeight.Bold, style = LifeTheme.typography.titleMedium)
                        Text("Tiến độ: $pct% • Hạn: ${fund.deadlineDate}", style = LifeTheme.typography.labelSmall, color = Color(0xFFBE185D))
                    }
                }
                IconButton(onClick = onDelete) {
                    Icon(Icons.Default.DeleteOutline, contentDescription = "Xóa quỹ", tint = LifeTheme.colors.error)
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            LifeProgressBar(
                progress = progress,
                customGradient = Brush.horizontalGradient(listOf(Color(0xFFBE185D), Color(0xFFEC4899))),
                trackColor = Color(0xFFFDF2F8),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(8.dp)
            )

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Đã tích lũy: ${Formatters.formatVnd(fund.currentAmount)}",
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFFBE185D),
                    style = LifeTheme.typography.bodySmall
                )
                Text(
                    text = "Mục tiêu: ${Formatters.formatVnd(fund.targetAmount)}",
                    style = LifeTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Action row: Deposit +100k or Withdraw
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                LifeButton(
                    text = "Nạp +100k",
                    onClick = onQuickDeposit,
                    leadingIcon = { Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp)) },
                    size = LifeButtonSize.MEDIUM,
                    modifier = Modifier.weight(1f)
                )

                LifeButton(
                    text = "Rút -50k",
                    onClick = onWithdraw,
                    variant = LifeButtonVariant.OUTLINE,
                    size = LifeButtonSize.MEDIUM,
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}

// Add Transaction Dialog with Real-Time Digit Formatting (300.000.000đ), Dropdown Categories, and Wallet Selector
@Composable
fun AddTransactionDialog(
    wallets: List<WalletEntity>,
    categories: List<CategoryEntity>,
    onDismiss: () -> Unit,
    onConfirm: (TransactionEntity) -> Unit
) {
    var rawAmount by remember { mutableLongStateOf(0L) }
    var selectedType by remember { mutableStateOf("EXPENSE") }
    var selectedWalletId by remember { mutableStateOf(wallets.firstOrNull()?.id ?: 1L) }
    var note by remember { mutableStateOf("") }

    val filteredCategories = categories.filter { it.type == selectedType }.ifEmpty { categories }
    var selectedCategoryName by remember { mutableStateOf(filteredCategories.firstOrNull()?.name ?: "Ăn uống & Sinh hoạt") }

    LifeDialog(
        title = "Thêm Giao Dịch Mới",
        icon = { Icon(Icons.Default.Payments, contentDescription = null, tint = LifeTheme.colors.brandPrimary) },
        onDismissRequest = onDismiss,
        confirmButtonText = "Lưu Giao Dịch",
        onConfirm = {
            if (rawAmount > 0) {
                onConfirm(
                    TransactionEntity(
                        walletId = selectedWalletId,
                        amount = rawAmount,
                        type = selectedType,
                        category = selectedCategoryName,
                        note = note,
                        isConfirmed = true
                    )
                )
            }
        },
        dismissButtonText = "Hủy",
        onDismiss = onDismiss
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            // Type selector: Thu / Chi
            LifeSegmentedControl(
                items = listOf("Khoản Chi", "Khoản Thu"),
                selectedIndex = if (selectedType == "EXPENSE") 0 else 1,
                onItemSelected = { idx ->
                    selectedType = if (idx == 0) "EXPENSE" else "INCOME"
                    selectedCategoryName = categories.firstOrNull { it.type == selectedType }?.name ?: if (selectedType == "EXPENSE") "Ăn uống & Sinh hoạt" else "Lương & Thưởng"
                }
            )

            // Amount input with live formatted currency display (300.000.000đ)
            LifeMoneyTextField(
                rawAmount = rawAmount,
                onAmountChange = { rawAmount = it },
                label = "Số tiền (VNĐ)"
            )

            // Category Selection Chips
            Text("Danh mục chi tiêu:", style = LifeTheme.typography.labelMedium, fontWeight = FontWeight.Bold)
            LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                items(filteredCategories) { cat ->
                    LifeChip(
                        text = cat.name,
                        selected = selectedCategoryName == cat.name,
                        onClick = { selectedCategoryName = cat.name }
                    )
                }
            }

            // Wallet Selection Chips
            Text("Chọn ví giao dịch:", style = LifeTheme.typography.labelMedium, fontWeight = FontWeight.Bold)
            LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                items(wallets) { w ->
                    LifeChip(
                        text = "${w.name} (${Formatters.formatVnd(w.balance)})",
                        selected = selectedWalletId == w.id,
                        onClick = { selectedWalletId = w.id }
                    )
                }
            }

            LifeTextField(
                value = note,
                onValueChange = { note = it },
                label = "Ghi chú chi tiết"
            )
        }
    }
}

// Dialog Create New Wallet
@Composable
fun AddWalletDialog(
    onDismiss: () -> Unit,
    onConfirm: (WalletEntity) -> Unit
) {
    var name by remember { mutableStateOf("") }
    var balance by remember { mutableLongStateOf(0L) }
    var walletType by remember { mutableStateOf("BANK") }

    LifeDialog(
        title = "Tạo Ví Mới",
        icon = { Icon(Icons.Default.AccountBalanceWallet, contentDescription = null, tint = LifeTheme.colors.brandPrimary) },
        onDismissRequest = onDismiss,
        confirmButtonText = "Tạo Ví",
        onConfirm = {
            if (name.isNotBlank()) {
                onConfirm(
                    WalletEntity(
                        name = name,
                        type = walletType,
                        balance = balance
                    )
                )
            }
        },
        dismissButtonText = "Hủy",
        onDismiss = onDismiss
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            LifeTextField(
                value = name,
                onValueChange = { name = it },
                label = "Tên ví (VD: BIDV, ZaloPay, Tiết kiệm...)"
            )
            LifeMoneyTextField(
                rawAmount = balance,
                onAmountChange = { balance = it },
                label = "Số dư ban đầu"
            )
            Text("Loại ví:", style = LifeTheme.typography.labelMedium, fontWeight = FontWeight.Bold)
            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                listOf("CASH" to "Tiền mặt", "BANK" to "Ngân hàng", "EWALLET" to "Ví điện tử").forEach { (type, label) ->
                    LifeChip(
                        text = label,
                        selected = walletType == type,
                        onClick = { walletType = type }
                    )
                }
            }
        }
    }
}

// Dialog Create New Category
@Composable
fun AddCategoryDialog(
    onDismiss: () -> Unit,
    onConfirm: (CategoryEntity) -> Unit
) {
    var name by remember { mutableStateOf("") }
    var type by remember { mutableStateOf("EXPENSE") }

    LifeDialog(
        title = "Tạo Danh Mục Thu/Chi Mới",
        icon = { Icon(Icons.Default.Category, contentDescription = null, tint = LifeTheme.colors.brandPrimary) },
        onDismissRequest = onDismiss,
        confirmButtonText = "Lưu Danh Mục",
        onConfirm = {
            if (name.isNotBlank()) {
                onConfirm(CategoryEntity(name = name, type = type))
            }
        },
        dismissButtonText = "Hủy",
        onDismiss = onDismiss
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            LifeTextField(
                value = name,
                onValueChange = { name = it },
                label = "Tên danh mục (VD: Cà phê, Du lịch, Thưởng...)"
            )
            LifeSegmentedControl(
                items = listOf("Chi Tiêu", "Thu Nhập"),
                selectedIndex = if (type == "EXPENSE") 0 else 1,
                onItemSelected = { idx -> type = if (idx == 0) "EXPENSE" else "INCOME" }
            )
        }
    }
}

@Composable
fun ManageCategoriesDialog(
    categories: List<CategoryEntity>,
    onDismiss: () -> Unit,
    onAddNew: () -> Unit,
    onEditCategory: (CategoryEntity) -> Unit,
    onDeleteCategory: (CategoryEntity) -> Unit
) {
    var filterType by remember { mutableStateOf("ALL") } // "ALL", "EXPENSE", "INCOME"

    val filtered = remember(categories, filterType) {
        when (filterType) {
            "EXPENSE" -> categories.filter { it.type == "EXPENSE" }
            "INCOME" -> categories.filter { it.type == "INCOME" }
            else -> categories
        }
    }

    LifeDialog(
        title = "Quản Lý Danh Mục Thu/Chi",
        icon = { Icon(Icons.Default.Tune, contentDescription = null, tint = LifeTheme.colors.brandPrimary) },
        onDismissRequest = onDismiss,
        confirmButtonText = "+ Thêm Mới",
        onConfirm = onAddNew,
        dismissButtonText = "Đóng",
        onDismiss = onDismiss
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            LifeSegmentedControl(
                items = listOf("Tất cả (${categories.size})", "Chi tiêu", "Thu nhập"),
                selectedIndex = when (filterType) {
                    "EXPENSE" -> 1
                    "INCOME" -> 2
                    else -> 0
                },
                onItemSelected = { idx ->
                    filterType = when (idx) {
                        1 -> "EXPENSE"
                        2 -> "INCOME"
                        else -> "ALL"
                    }
                }
            )

            if (filtered.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 24.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        "Chưa có danh mục nào trong nhóm này.",
                        style = LifeTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(max = 280.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    items(filtered, key = { it.id }) { cat ->
                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.45f),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 12.dp, vertical = 8.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Box(
                                        modifier = Modifier
                                            .size(32.dp)
                                            .clip(CircleShape)
                                            .background(
                                                if (cat.type == "EXPENSE") LifeTheme.colors.error.copy(alpha = 0.15f)
                                                else LifeTheme.colors.success.copy(alpha = 0.15f)
                                            ),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(
                                            if (cat.type == "EXPENSE") "🔻" else "🔺",
                                            fontSize = 12.sp
                                        )
                                    }
                                    Spacer(modifier = Modifier.width(10.dp))
                                    Column {
                                        Text(
                                            cat.name,
                                            style = LifeTheme.typography.labelMedium,
                                            fontWeight = FontWeight.SemiBold
                                        )
                                        Text(
                                            if (cat.type == "EXPENSE") "Khoản Chi" else "Khoản Thu",
                                            style = LifeTheme.typography.labelSmall.copy(fontSize = 10.sp),
                                            color = if (cat.type == "EXPENSE") LifeTheme.colors.error else LifeTheme.colors.success
                                        )
                                    }
                                }

                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    IconButton(
                                        onClick = { onEditCategory(cat) },
                                        modifier = Modifier.size(32.dp)
                                    ) {
                                        Icon(
                                            Icons.Default.Edit,
                                            contentDescription = "Sửa",
                                            tint = LifeTheme.colors.brandPrimary,
                                            modifier = Modifier.size(18.dp)
                                        )
                                    }
                                    IconButton(
                                        onClick = { onDeleteCategory(cat) },
                                        modifier = Modifier.size(32.dp)
                                    ) {
                                        Icon(
                                            Icons.Default.DeleteOutline,
                                            contentDescription = "Xóa",
                                            tint = LifeTheme.colors.error,
                                            modifier = Modifier.size(18.dp)
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

@Composable
fun EditCategoryDialog(
    category: CategoryEntity,
    onDismiss: () -> Unit,
    onConfirm: (CategoryEntity) -> Unit,
    onDelete: () -> Unit
) {
    var name by remember { mutableStateOf(category.name) }
    var type by remember { mutableStateOf(category.type) }

    LifeDialog(
        title = "Chỉnh Sửa Danh Mục",
        icon = { Icon(Icons.Default.Edit, contentDescription = null, tint = LifeTheme.colors.brandPrimary) },
        onDismissRequest = onDismiss,
        confirmButtonText = "Lưu Thay Đổi",
        onConfirm = {
            if (name.isNotBlank()) {
                onConfirm(category.copy(name = name.trim(), type = type))
            }
        },
        dismissButtonText = "Hủy",
        onDismiss = onDismiss
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            LifeTextField(
                value = name,
                onValueChange = { name = it },
                label = "Tên danh mục"
            )
            LifeSegmentedControl(
                items = listOf("Chi Tiêu", "Thu Nhập"),
                selectedIndex = if (type == "EXPENSE") 0 else 1,
                onItemSelected = { idx -> type = if (idx == 0) "EXPENSE" else "INCOME" }
            )
            Spacer(modifier = Modifier.height(4.dp))
            OutlinedButton(
                onClick = onDelete,
                colors = ButtonDefaults.outlinedButtonColors(contentColor = LifeTheme.colors.error),
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(Icons.Default.DeleteOutline, contentDescription = null, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text("Xóa danh mục này")
            }
        }
    }
}

// Dialog Create MoMo Savings Fund
@Composable
fun AddSavingsFundDialog(
    onDismiss: () -> Unit,
    onConfirm: (SavingsFundEntity) -> Unit
) {
    var name by remember { mutableStateOf("") }
    var targetAmount by remember { mutableLongStateOf(10000000L) }
    var deadline by remember { mutableStateOf("2027-12-31") }

    LifeDialog(
        title = "Mở Quỹ Tiết Kiệm",
        icon = { Icon(Icons.Default.Savings, contentDescription = null, tint = Color(0xFFBE185D)) },
        onDismissRequest = onDismiss,
        confirmButtonText = "Mở Quỹ Heo Đất 🐷",
        onConfirm = {
            if (name.isNotBlank()) {
                onConfirm(
                    SavingsFundEntity(
                        name = name,
                        targetAmount = targetAmount,
                        currentAmount = 0L,
                        deadlineDate = deadline
                    )
                )
            }
        },
        dismissButtonText = "Hủy",
        onDismiss = onDismiss
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            LifeTextField(
                value = name,
                onValueChange = { name = it },
                label = "Mục tiêu tiết kiệm (VD: Mua iPhone 16, Đi Nhật...)"
            )
            LifeMoneyTextField(
                rawAmount = targetAmount,
                onAmountChange = { targetAmount = it },
                label = "Số tiền mục tiêu"
            )
            LifeTextField(
                value = deadline,
                onValueChange = { deadline = it },
                label = "Ngày hoàn thành mục tiêu"
            )
        }
    }
}

// Card for items in Impulse Waiting Room
@Composable
fun ImpulseItemCard(
    item: ImpulseItemEntity,
    onRejectAndSave: () -> Unit,
    onApproveAndBuy: () -> Unit
) {
    val elapsedHours = ((System.currentTimeMillis() - item.createdAt) / (1000 * 3600)).toInt()
    val remainingHours = maxOf(0, item.lockDurationHours - elapsedHours)
    val isReady = remainingHours == 0 || item.status != "WAITING"

    LifeCard(
        variant = LifeCardVariant.ELEVATED,
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(item.name, fontWeight = FontWeight.Bold, style = LifeTheme.typography.titleMedium)
                    Text(Formatters.formatVnd(item.price), fontWeight = FontWeight.SemiBold, color = LifeTheme.colors.alarmStart, style = LifeTheme.typography.bodyMedium)
                }
                LifeBadge(
                    text = when (item.status) {
                        "REJECTED_SAVED" -> "Đã tiết kiệm"
                        "APPROVED_BOUGHT" -> "Đã mua"
                        else -> if (isReady) "Đã hết giờ chờ" else "Còn ${remainingHours}h"
                    },
                    style = when (item.status) {
                        "REJECTED_SAVED" -> LifeBadgeStyle.SUCCESS
                        "APPROVED_BOUGHT" -> LifeBadgeStyle.ERROR
                        else -> if (isReady) LifeBadgeStyle.WARNING else LifeBadgeStyle.NEUTRAL
                    }
                )
            }

            if (item.reason.isNotBlank()) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    "Lý do muốn mua: \"${item.reason}\"",
                    style = LifeTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            if (item.status == "WAITING") {
                Spacer(modifier = Modifier.height(14.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    LifeButton(
                        text = "Từ chối & Tiết kiệm",
                        onClick = onRejectAndSave,
                        leadingIcon = { Icon(Icons.Default.Savings, contentDescription = null, modifier = Modifier.size(16.dp)) },
                        size = LifeButtonSize.MEDIUM,
                        modifier = Modifier.weight(1f)
                    )
                    LifeButton(
                        text = if (isReady) "Vẫn muốn mua" else "Đang khóa (${remainingHours}h)",
                        onClick = onApproveAndBuy,
                        enabled = isReady,
                        variant = LifeButtonVariant.OUTLINE,
                        size = LifeButtonSize.MEDIUM,
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }
    }
}

// Dialog Add Item to Impulse Waiting Room
@Composable
fun AddImpulseDialog(
    onDismiss: () -> Unit,
    onConfirm: (ImpulseItemEntity) -> Unit
) {
    var name by remember { mutableStateOf("") }
    var price by remember { mutableLongStateOf(0L) }
    var reason by remember { mutableStateOf("") }
    var lockHours by remember { mutableIntStateOf(48) }

    LifeDialog(
        title = "Đưa Vào Phòng Chờ Bốc Đồng",
        icon = { Icon(Icons.Default.HourglassBottom, contentDescription = null, tint = LifeTheme.colors.alarmStart) },
        onDismissRequest = onDismiss,
        confirmButtonText = "Khóa Vào Phòng Chờ",
        onConfirm = {
            if (name.isNotBlank() && price > 0) {
                onConfirm(
                    ImpulseItemEntity(
                        name = name,
                        price = price,
                        reason = reason,
                        lockDurationHours = lockHours
                    )
                )
            }
        },
        dismissButtonText = "Hủy",
        onDismiss = onDismiss
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Text(
                "Quy tắc 48-72h giúp não bộ hạ nhiệt cảm xúc muốn mua sắm ngay lập tức.",
                style = LifeTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            LifeTextField(
                value = name,
                onValueChange = { name = it },
                label = "Món đồ muốn mua"
            )
            LifeMoneyTextField(
                rawAmount = price,
                onAmountChange = { price = it },
                label = "Giá dự kiến"
            )
            LifeTextField(
                value = reason,
                onValueChange = { reason = it },
                label = "Tại sao lại muốn mua món này?"
            )
            Text("Thời gian khóa hạ nhiệt:", style = LifeTheme.typography.labelMedium, fontWeight = FontWeight.Bold)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                listOf(24 to "24h", 48 to "48h (Chuẩn)", 72 to "72h (Lớn)").forEach { (hrs, label) ->
                    LifeChip(
                        text = label,
                        selected = lockHours == hrs,
                        onClick = { lockHours = hrs }
                    )
                }
            }
        }
    }
}

// Dialog Simulate / Scan Invoice
@Composable
fun ScanInvoiceDialog(
    onDismiss: () -> Unit,
    onConfirmInvoice: (store: String, amount: Long, category: String) -> Unit
) {
    var storeName by remember { mutableStateOf("WinMart Cầu Giấy") }
    var amount by remember { mutableLongStateOf(185000L) }
    var selectedCategory by remember { mutableStateOf("Ăn uống & Sinh hoạt") }

    LifeDialog(
        title = "Mô Phỏng Quét Hóa Đơn (OCR)",
        icon = { Icon(Icons.Default.ReceiptLong, contentDescription = null, tint = LifeTheme.colors.financeStart) },
        onDismissRequest = onDismiss,
        confirmButtonText = "Xác nhận & Thêm giao dịch",
        onConfirm = {
            if (storeName.isNotBlank() && amount > 0) {
                onConfirmInvoice(storeName, amount, selectedCategory)
            }
        },
        dismissButtonText = "Hủy",
        onDismiss = onDismiss
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Text(
                "ML Kit Text Recognition trích xuất tự động thông tin cửa hàng và tổng tiền từ hóa đơn.",
                style = LifeTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            LifeTextField(
                value = storeName,
                onValueChange = { storeName = it },
                label = "Cửa hàng / Đơn vị"
            )
            LifeMoneyTextField(
                rawAmount = amount,
                onAmountChange = { amount = it },
                label = "Tổng tiền hóa đơn"
            )
            LifeTextField(
                value = selectedCategory,
                onValueChange = { selectedCategory = it },
                label = "Phân loại chi tiêu"
            )
        }
    }
}
