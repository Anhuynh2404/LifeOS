package com.example.feature.finance

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.core.util.Formatters
import com.example.data.entity.BankLoanEntity
import com.example.data.entity.LoanRepaymentEntity
import com.example.data.entity.WalletEntity
import com.example.designsystem.components.*
import com.example.designsystem.theme.LifeTheme
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun BankLoansTabContent(
    bankLoans: List<BankLoanEntity>,
    loanRepayments: List<LoanRepaymentEntity>,
    wallets: List<WalletEntity>,
    onAddLoanClick: () -> Unit,
    onEditLoanClick: (BankLoanEntity) -> Unit,
    onDeleteLoanClick: (BankLoanEntity) -> Unit,
    onPayLoanClick: (BankLoanEntity) -> Unit,
    onViewHistoryClick: (BankLoanEntity) -> Unit,
    onToggleReminder: (BankLoanEntity, enabled: Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    val colors = LifeTheme.colors
    var selectedFilter by remember { mutableStateOf("Tất cả") }

    val activeLoans = bankLoans.filter { !it.isPaidOff && it.remainingAmount > 0 }
    val paidOffLoans = bankLoans.filter { it.isPaidOff || it.remainingAmount <= 0 }
    val dueSoonLoans = activeLoans.filter { BankLoanHelper.isDueSoon(it) }

    val filteredLoans = when (selectedFilter) {
        "Đang trả" -> activeLoans
        "Sắp đến hạn" -> dueSoonLoans
        "Đã tất toán" -> paidOffLoans
        else -> bankLoans
    }

    val totalOriginalDebt = bankLoans.sumOf { it.originalAmount }
    val totalRemainingDebt = activeLoans.sumOf { it.remainingAmount }
    val totalPaidPrincipal = (totalOriginalDebt - totalRemainingDebt).coerceAtLeast(0L)
    val overallProgress = if (totalOriginalDebt > 0) {
        (totalPaidPrincipal.toFloat() / totalOriginalDebt.toFloat()).coerceIn(0f, 1f)
    } else 0f
    val totalMonthlyPayment = activeLoans.sumOf { it.monthlyEstimatedPayment }

    val nearestDueLoan = activeLoans
        .map { it to BankLoanHelper.calculateNextDueDate(it) }
        .filter { it.second.second in 0..30 }
        .minByOrNull { it.second.second }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        contentPadding = PaddingValues(top = 16.dp, bottom = 100.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // 1. Debt Summary Dashboard Card
        item {
            LifeCard(
                variant = LifeCardVariant.GRADIENT,
                gradient = Brush.linearGradient(
                    listOf(
                        Color(0xFF1E1B4B),
                        Color(0xFF312E81),
                        Color(0xFF4338CA)
                    )
                ),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                "Tổng Dư Nợ Ngân Hàng",
                                style = LifeTheme.typography.labelSmall,
                                color = Color.White.copy(alpha = 0.8f)
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = Formatters.formatVnd(totalRemainingDebt),
                                style = LifeTheme.typography.displayMedium,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        }
                        Box(
                            modifier = Modifier
                                .size(46.dp)
                                .clip(CircleShape)
                                .background(Color.White.copy(alpha = 0.15f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                Icons.Default.AccountBalance,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(26.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Progress row
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "Đã trả: ${Formatters.formatVnd(totalPaidPrincipal)}",
                            style = LifeTheme.typography.bodySmall,
                            color = Color(0xFF6EE7B7)
                        )
                        Text(
                            text = "${(overallProgress * 100).toInt()}% tổng vốn",
                            style = LifeTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold),
                            color = Color.White
                        )
                    }
                    Spacer(modifier = Modifier.height(6.dp))
                    LifeProgressBar(
                        progress = overallProgress,
                        customGradient = Brush.horizontalGradient(listOf(Color(0xFF10B981), Color(0xFF34D399))),
                        trackColor = Color.White.copy(alpha = 0.2f),
                        height = 8.dp
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    // 2 Stats metrics: Total Monthly Estimated & Next Due Date
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        // Metric 1: Monthly payment
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(LifeTheme.shapes.medium)
                                .background(Color.White.copy(alpha = 0.1f))
                                .padding(10.dp)
                        ) {
                            Column {
                                Text(
                                    "Ước tính trả tháng này",
                                    style = LifeTheme.typography.labelSmall.copy(fontSize = 11.sp),
                                    color = Color.White.copy(alpha = 0.75f)
                                )
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    Formatters.formatVnd(totalMonthlyPayment),
                                    style = LifeTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                                    color = Color.White
                                )
                            }
                        }

                        // Metric 2: Nearest due alert
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(LifeTheme.shapes.medium)
                                .background(
                                    if (nearestDueLoan != null && nearestDueLoan.second.second <= 3) {
                                        Color(0xFFEF4444).copy(alpha = 0.25f)
                                    } else {
                                        Color.White.copy(alpha = 0.1f)
                                    }
                                )
                                .padding(10.dp)
                        ) {
                            Column {
                                Text(
                                    "Kỳ thanh toán kế tiếp",
                                    style = LifeTheme.typography.labelSmall.copy(fontSize = 11.sp),
                                    color = Color.White.copy(alpha = 0.75f)
                                )
                                Spacer(modifier = Modifier.height(2.dp))
                                if (nearestDueLoan != null) {
                                    val (dateStr, daysLeft) = nearestDueLoan.second
                                    val daysText = if (daysLeft == 0) "Hôm nay!" else "Còn $daysLeft ngày"
                                    Text(
                                        "$dateStr ($daysText)",
                                        style = LifeTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                                        color = if (daysLeft <= 3) Color(0xFFFCA5A5) else Color.White
                                    )
                                } else {
                                    Text(
                                        "Không có nợ đến hạn",
                                        style = LifeTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                                        color = Color.White
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        // 2. Action row & Filters
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "📋 Danh Sách Khoản Vay",
                    style = LifeTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                    color = colors.textPrimary
                )
                LifeButton(
                    text = "Thêm Khoản Vay",
                    onClick = onAddLoanClick,
                    variant = LifeButtonVariant.PRIMARY,
                    size = LifeButtonSize.SMALL,
                    leadingIcon = { Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp)) },
                    testTag = "add_bank_loan_button"
                )
            }
        }

        // Filter chips
        item {
            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                val filters = listOf(
                    "Tất cả" to bankLoans.size,
                    "Đang trả" to activeLoans.size,
                    "Sắp đến hạn" to dueSoonLoans.size,
                    "Đã tất toán" to paidOffLoans.size
                )
                items(filters) { (label, count) ->
                    LifeChip(
                        text = "$label ($count)",
                        selected = selectedFilter == label,
                        onClick = { selectedFilter = label },
                        testTag = "loan_filter_${label}"
                    )
                }
            }
        }

        // 3. Bank Loan Cards List
        if (filteredLoans.isEmpty()) {
            item {
                LifeCard(
                    variant = LifeCardVariant.SURFACE,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(32.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            Icons.Default.AccountBalance,
                            contentDescription = null,
                            tint = colors.textSecondary.copy(alpha = 0.5f),
                            modifier = Modifier.size(48.dp)
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            "Chưa có khoản nợ ngân hàng nào trong danh mục này",
                            style = LifeTheme.typography.bodyMedium,
                            color = colors.textSecondary
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        LifeButton(
                            text = "+ Thêm khoản vay đầu tiên",
                            onClick = onAddLoanClick,
                            variant = LifeButtonVariant.OUTLINE,
                            size = LifeButtonSize.MEDIUM,
                            testTag = "empty_add_loan_btn"
                        )
                    }
                }
            }
        } else {
            items(filteredLoans, key = { it.id }) { loan ->
                val loanRepaymentList = loanRepayments.filter { it.loanId == loan.id }
                BankLoanCard(
                    loan = loan,
                    repayments = loanRepaymentList,
                    onEditClick = { onEditLoanClick(loan) },
                    onDeleteClick = { onDeleteLoanClick(loan) },
                    onPayClick = { onPayLoanClick(loan) },
                    onViewHistoryClick = { onViewHistoryClick(loan) },
                    onToggleReminder = { enabled -> onToggleReminder(loan, enabled) }
                )
            }
        }
    }
}

@Composable
fun BankLoanCard(
    loan: BankLoanEntity,
    repayments: List<LoanRepaymentEntity>,
    onEditClick: () -> Unit,
    onDeleteClick: () -> Unit,
    onPayClick: () -> Unit,
    onViewHistoryClick: () -> Unit,
    onToggleReminder: (Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    val colors = LifeTheme.colors
    val bankColor = BankLoanHelper.getBankColor(loan.bankName)
    val bankInitials = BankLoanHelper.getBankInitials(loan.bankName)
    val (dueDateStr, daysRemaining) = BankLoanHelper.calculateNextDueDate(loan)

    val paidAmount = (loan.originalAmount - loan.remainingAmount).coerceAtLeast(0L)
    val progress = if (loan.originalAmount > 0) {
        (paidAmount.toFloat() / loan.originalAmount.toFloat()).coerceIn(0f, 1f)
    } else 0f

    LifeCard(
        variant = LifeCardVariant.ELEVATED,
        modifier = modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Header: Bank badge, loan title & contract number, action buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                    // Bank Logo / Initials Avatar
                    Box(
                        modifier = Modifier
                            .size(42.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .background(bankColor),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = bankInitials,
                            style = LifeTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                            color = Color.White
                        )
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = loan.bankName,
                                style = LifeTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                                color = colors.textPrimary
                            )
                            if (loan.isPaidOff || loan.remainingAmount <= 0) {
                                Spacer(modifier = Modifier.width(6.dp))
                                LifeBadge(text = "Đã tất toán", style = LifeBadgeStyle.SUCCESS)
                            }
                        }
                        Text(
                            text = loan.loanName,
                            style = LifeTheme.typography.bodySmall,
                            color = colors.textSecondary
                        )
                        if (loan.contractNumber.isNotBlank()) {
                            Text(
                                text = "HĐ: ${loan.contractNumber}",
                                style = LifeTheme.typography.labelSmall.copy(fontSize = 11.sp),
                                color = colors.textSecondary.copy(alpha = 0.75f)
                            )
                        }
                    }
                }

                // Edit & Delete icons
                Row {
                    IconButton(onClick = onEditClick, modifier = Modifier.size(36.dp)) {
                        Icon(Icons.Default.Edit, contentDescription = "Sửa", tint = colors.textSecondary, modifier = Modifier.size(18.dp))
                    }
                    IconButton(onClick = onDeleteClick, modifier = Modifier.size(36.dp)) {
                        Icon(Icons.Default.DeleteOutline, contentDescription = "Xóa", tint = colors.error, modifier = Modifier.size(18.dp))
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Debt Amount & Progress
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Bottom
            ) {
                Column {
                    Text(
                        text = "Dư nợ gốc còn lại",
                        style = LifeTheme.typography.labelSmall,
                        color = colors.textSecondary
                    )
                    Text(
                        text = Formatters.formatVnd(loan.remainingAmount),
                        style = LifeTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                        color = if (loan.isPaidOff || loan.remainingAmount <= 0) colors.success else colors.textPrimary
                    )
                }
                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        text = "Vốn vay ban đầu",
                        style = LifeTheme.typography.labelSmall,
                        color = colors.textSecondary
                    )
                    Text(
                        text = Formatters.formatVnd(loan.originalAmount),
                        style = LifeTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold),
                        color = colors.textSecondary
                    )
                }
            }

            Spacer(modifier = Modifier.height(6.dp))
            LifeProgressBar(
                progress = progress,
                customGradient = Brush.horizontalGradient(listOf(bankColor, Color(0xFF10B981))),
                height = 8.dp
            )
            Spacer(modifier = Modifier.height(4.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Đã thanh toán: ${Formatters.formatVnd(paidAmount)}",
                    style = LifeTheme.typography.labelSmall.copy(fontSize = 11.sp),
                    color = colors.textSecondary
                )
                Text(
                    text = "${(progress * 100).toInt()}%",
                    style = LifeTheme.typography.labelSmall.copy(fontSize = 11.sp, fontWeight = FontWeight.Bold),
                    color = colors.textPrimary
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Grid Specs: Interest Rate, Monthly payment, Term, Calculation method
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(LifeTheme.shapes.small)
                    .background(colors.surfaceElevated.copy(alpha = 0.6f))
                    .border(1.dp, colors.border, LifeTheme.shapes.small)
                    .padding(10.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text("Lãi suất", style = LifeTheme.typography.labelSmall.copy(fontSize = 10.sp), color = colors.textSecondary)
                    Text("${loan.interestRatePercent}%/năm", style = LifeTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold), color = colors.textPrimary)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text("Thời hạn", style = LifeTheme.typography.labelSmall.copy(fontSize = 10.sp), color = colors.textSecondary)
                    Text("${loan.termMonths} tháng", style = LifeTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold), color = colors.textPrimary)
                }
                Column {
                    Text("Trả mỗi tháng (ước tính)", style = LifeTheme.typography.labelSmall.copy(fontSize = 10.sp), color = colors.textSecondary)
                    Text(
                        Formatters.formatVnd(loan.monthlyEstimatedPayment),
                        style = LifeTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                        color = colors.financeStart
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text("Phương thức tính lãi", style = LifeTheme.typography.labelSmall.copy(fontSize = 10.sp), color = colors.textSecondary)
                    Text(
                        if (loan.interestMethod == "REDUCING_BALANCE") "Dư nợ giảm dần" else "Gốc cố định",
                        style = LifeTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                        color = colors.textPrimary
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Reminder & Due Date Section
            if (!loan.isPaidOff && loan.remainingAmount > 0) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(LifeTheme.shapes.medium)
                        .background(
                            when {
                                daysRemaining <= 0 -> colors.error.copy(alpha = 0.12f)
                                daysRemaining <= loan.reminderDaysBefore -> colors.warning.copy(alpha = 0.12f)
                                else -> colors.brandPrimary.copy(alpha = 0.08f)
                            }
                        )
                        .border(
                            1.dp,
                            when {
                                daysRemaining <= 0 -> colors.error.copy(alpha = 0.3f)
                                daysRemaining <= loan.reminderDaysBefore -> colors.warning.copy(alpha = 0.3f)
                                else -> colors.brandPrimary.copy(alpha = 0.2f)
                            },
                            LifeTheme.shapes.medium
                        )
                        .padding(horizontal = 12.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                        Icon(
                            imageVector = when {
                                daysRemaining <= 0 -> Icons.Default.Warning
                                daysRemaining <= loan.reminderDaysBefore -> Icons.Default.NotificationImportant
                                else -> Icons.Default.Event
                            },
                            contentDescription = null,
                            tint = when {
                                daysRemaining <= 0 -> colors.error
                                daysRemaining <= loan.reminderDaysBefore -> colors.warning
                                else -> colors.brandPrimary
                            },
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Column {
                            val statusText = when {
                                daysRemaining < 0 -> "Đã quá hạn ${-daysRemaining} ngày!"
                                daysRemaining == 0 -> "Hôm nay đến hạn trả nợ!"
                                daysRemaining == 1 -> "Ngày mai đến hạn thanh toán!"
                                daysRemaining <= loan.reminderDaysBefore -> "Sắp đến hạn: Còn $daysRemaining ngày (ngày $dueDateStr)"
                                else -> "Đáo hạn kỳ tới: $dueDateStr (còn $daysRemaining ngày)"
                            }
                            Text(
                                text = statusText,
                                style = LifeTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                color = when {
                                    daysRemaining <= 0 -> colors.error
                                    daysRemaining <= loan.reminderDaysBefore -> colors.warning
                                    else -> colors.textPrimary
                                }
                            )
                            Text(
                                text = "Ngày ${loan.duePaymentDay} hàng tháng • Nhắc trước ${loan.reminderDaysBefore} ngày",
                                style = LifeTheme.typography.bodySmall.copy(fontSize = 11.sp),
                                color = colors.textSecondary
                            )
                        }
                    }

                    // Quick Switch toggle
                    LifeSwitch(
                        checked = loan.isReminderEnabled,
                        onCheckedChange = onToggleReminder,
                        testTag = "loan_reminder_switch_${loan.id}"
                    )
                }
            }

            // Optional note
            if (loan.note.isNotBlank()) {
                Spacer(modifier = Modifier.height(8.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        Icons.Default.Info,
                        contentDescription = null,
                        tint = colors.textSecondary,
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = loan.note,
                        style = LifeTheme.typography.bodySmall.copy(fontSize = 11.sp),
                        color = colors.textSecondary,
                        maxLines = 2
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Action Buttons: Pay installment & View History
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                if (!loan.isPaidOff && loan.remainingAmount > 0) {
                    LifeButton(
                        text = "Trả Nợ Kỳ Này",
                        onClick = onPayClick,
                        variant = LifeButtonVariant.PRIMARY,
                        size = LifeButtonSize.SMALL,
                        leadingIcon = { Icon(Icons.Default.Payment, contentDescription = null, modifier = Modifier.size(16.dp)) },
                        modifier = Modifier.weight(1f),
                        testTag = "pay_loan_button_${loan.id}"
                    )
                }
                LifeButton(
                    text = "Lịch Sử (${repayments.size})",
                    onClick = onViewHistoryClick,
                    variant = LifeButtonVariant.OUTLINE,
                    size = LifeButtonSize.SMALL,
                    leadingIcon = { Icon(Icons.Default.History, contentDescription = null, modifier = Modifier.size(16.dp)) },
                    modifier = if (loan.isPaidOff || loan.remainingAmount <= 0) Modifier.fillMaxWidth() else Modifier.weight(1f),
                    testTag = "loan_history_button_${loan.id}"
                )
            }
        }
    }
}

// Dialog: Add or Edit Bank Loan
@Composable
fun AddEditLoanDialog(
    initialLoan: BankLoanEntity? = null,
    onDismiss: () -> Unit,
    onConfirm: (BankLoanEntity) -> Unit
) {
    var bankName by remember { mutableStateOf(initialLoan?.bankName ?: "Vietcombank") }
    var loanName by remember { mutableStateOf(initialLoan?.loanName ?: "Vay mua nhà căn hộ") }
    var contractNumber by remember { mutableStateOf(initialLoan?.contractNumber ?: "") }
    var originalAmount by remember { mutableLongStateOf(initialLoan?.originalAmount ?: 500000000L) }
    var remainingAmount by remember { mutableLongStateOf(initialLoan?.remainingAmount ?: 500000000L) }
    var interestRatePercent by remember { mutableFloatStateOf(initialLoan?.interestRatePercent ?: 8.5f) }
    var interestMethod by remember { mutableStateOf(initialLoan?.interestMethod ?: "REDUCING_BALANCE") }
    var termMonths by remember { mutableIntStateOf(initialLoan?.termMonths ?: 36) }
    var duePaymentDay by remember { mutableIntStateOf(initialLoan?.duePaymentDay ?: 15) }
    var reminderDaysBefore by remember { mutableIntStateOf(initialLoan?.reminderDaysBefore ?: 3) }
    var isReminderEnabled by remember { mutableStateOf(initialLoan?.isReminderEnabled ?: true) }
    var note by remember { mutableStateOf(initialLoan?.note ?: "") }

    val colors = LifeTheme.colors

    val monthlyEstimate = remember(originalAmount, remainingAmount, interestRatePercent, termMonths, interestMethod) {
        BankLoanHelper.calculateMonthlyEstimatedPayment(
            originalAmount = originalAmount,
            remainingAmount = remainingAmount,
            interestRatePercent = interestRatePercent,
            termMonths = termMonths,
            interestMethod = interestMethod
        )
    }

    LifeDialog(
        title = if (initialLoan == null) "Thêm Khoản Vay Ngân Hàng" else "Chỉnh Sửa Khoản Vay",
        icon = { Icon(Icons.Default.AccountBalance, contentDescription = null, tint = colors.brandPrimary) },
        onDismissRequest = onDismiss,
        confirmButtonText = "Lưu khoản vay",
        onConfirm = {
            if (bankName.isNotBlank() && loanName.isNotBlank() && originalAmount > 0) {
                val finalizedLoan = (initialLoan ?: BankLoanEntity(
                    bankName = bankName,
                    loanName = loanName,
                    originalAmount = originalAmount,
                    remainingAmount = remainingAmount,
                    interestRatePercent = interestRatePercent,
                    startDate = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
                )).copy(
                    bankName = bankName,
                    loanName = loanName,
                    contractNumber = contractNumber,
                    originalAmount = originalAmount,
                    remainingAmount = remainingAmount,
                    interestRatePercent = interestRatePercent,
                    interestMethod = interestMethod,
                    termMonths = termMonths,
                    duePaymentDay = duePaymentDay,
                    monthlyEstimatedPayment = monthlyEstimate,
                    reminderDaysBefore = reminderDaysBefore,
                    isReminderEnabled = isReminderEnabled,
                    note = note,
                    isPaidOff = remainingAmount <= 0
                )
                onConfirm(finalizedLoan)
            }
        },
        dismissButtonText = "Hủy",
        onDismiss = onDismiss
    ) {
        LazyColumn(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item {
                Text("Chọn hoặc nhập ngân hàng:", style = LifeTheme.typography.labelSmall, fontWeight = FontWeight.Bold)
                LazyRow(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    items(BankLoanHelper.POPULAR_BANKS) { bank ->
                        LifeChip(
                            text = bank,
                            selected = bankName == bank,
                            onClick = { bankName = bank }
                        )
                    }
                }
                Spacer(modifier = Modifier.height(6.dp))
                LifeTextField(
                    value = bankName,
                    onValueChange = { bankName = it },
                    label = "Tên ngân hàng"
                )
            }

            item {
                Text("Mục đích vay:", style = LifeTheme.typography.labelSmall, fontWeight = FontWeight.Bold)
                LazyRow(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    items(BankLoanHelper.LOAN_PURPOSES) { purpose ->
                        LifeChip(
                            text = purpose,
                            selected = loanName == purpose,
                            onClick = { loanName = purpose }
                        )
                    }
                }
                Spacer(modifier = Modifier.height(6.dp))
                LifeTextField(
                    value = loanName,
                    onValueChange = { loanName = it },
                    label = "Tên gói vay / Mục đích"
                )
            }

            item {
                LifeTextField(
                    value = contractNumber,
                    onValueChange = { contractNumber = it },
                    label = "Mã số hợp đồng / Số tài khoản vay (tùy chọn)"
                )
            }

            item {
                LifeMoneyTextField(
                    rawAmount = originalAmount,
                    onAmountChange = {
                        originalAmount = it
                        if (initialLoan == null) remainingAmount = it
                    },
                    label = "Số tiền vay ban đầu"
                )
            }

            item {
                LifeMoneyTextField(
                    rawAmount = remainingAmount,
                    onAmountChange = { remainingAmount = it },
                    label = "Dư nợ gốc còn lại hiện tại"
                )
            }

            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    LifeTextField(
                        value = interestRatePercent.toString(),
                        onValueChange = { interestRatePercent = it.toFloatOrNull() ?: interestRatePercent },
                        label = "Lãi suất (%/năm)",
                        modifier = Modifier.weight(1f)
                    )
                    LifeTextField(
                        value = termMonths.toString(),
                        onValueChange = { termMonths = it.toIntOrNull() ?: termMonths },
                        label = "Thời hạn (tháng)",
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            item {
                Text("Phương thức tính lãi:", style = LifeTheme.typography.labelSmall, fontWeight = FontWeight.Bold)
                LifeSegmentedControl(
                    items = listOf("Dư nợ giảm dần", "Gốc cố định"),
                    selectedIndex = if (interestMethod == "REDUCING_BALANCE") 0 else 1,
                    onItemSelected = {
                        interestMethod = if (it == 0) "REDUCING_BALANCE" else "FIXED_BALANCE"
                    }
                )
            }

            item {
                Text("Ngày đến hạn thanh toán hàng tháng: Ngày $duePaymentDay", style = LifeTheme.typography.labelSmall, fontWeight = FontWeight.Bold)
                Slider(
                    value = duePaymentDay.toFloat(),
                    onValueChange = { duePaymentDay = it.toInt().coerceIn(1, 31) },
                    valueRange = 1f..31f,
                    steps = 29
                )
            }

            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text("Nhắc hẹn trả nợ tự động", style = LifeTheme.typography.labelMedium, fontWeight = FontWeight.Bold)
                        Text("Báo nhắc trước ngày thanh toán", style = LifeTheme.typography.bodySmall, color = colors.textSecondary)
                    }
                    LifeSwitch(
                        checked = isReminderEnabled,
                        onCheckedChange = { isReminderEnabled = it }
                    )
                }
                if (isReminderEnabled) {
                    Spacer(modifier = Modifier.height(6.dp))
                    Text("Nhắc trước ngày đến hạn:", style = LifeTheme.typography.labelSmall)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        listOf(1 to "1 ngày", 2 to "2 ngày", 3 to "3 ngày", 5 to "5 ngày", 7 to "7 ngày").forEach { (days, label) ->
                            LifeChip(
                                text = label,
                                selected = reminderDaysBefore == days,
                                onClick = { reminderDaysBefore = days }
                            )
                        }
                    }
                }
            }

            item {
                LifeTextField(
                    value = note,
                    onValueChange = { note = it },
                    label = "Ghi chú (Tài sản thế chấp, SĐT cán bộ tín dụng...)"
                )
            }

            // Real-time calculation preview card
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(LifeTheme.shapes.medium)
                        .background(colors.brandPrimary.copy(alpha = 0.1f))
                        .border(1.dp, colors.brandPrimary.copy(alpha = 0.25f), LifeTheme.shapes.medium)
                        .padding(12.dp)
                ) {
                    Column {
                        Text(
                            "💡 Dự tính trả nợ kỳ đầu / tháng:",
                            style = LifeTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                            color = colors.brandPrimary
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            Formatters.formatVnd(monthlyEstimate),
                            style = LifeTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                            color = colors.brandPrimary
                        )
                        val monthlyPrincipal = if (termMonths > 0) originalAmount / termMonths else 0L
                        val monthlyInterest = monthlyEstimate - monthlyPrincipal
                        Text(
                            "Gồm: Gốc ~${Formatters.formatVnd(monthlyPrincipal)} + Lãi ~${Formatters.formatVnd(monthlyInterest)}",
                            style = LifeTheme.typography.bodySmall.copy(fontSize = 11.sp),
                            color = colors.textSecondary
                        )
                    }
                }
            }
        }
    }
}

// Dialog: Pay installment
@Composable
fun PayLoanDialog(
    loan: BankLoanEntity,
    wallets: List<WalletEntity>,
    onDismiss: () -> Unit,
    onConfirmPayment: (principal: Long, interest: Long, walletId: Long?, note: String) -> Unit
) {
    val termMonthsSafe = if (loan.termMonths > 0) loan.termMonths else 1
    val defaultPrincipal = (loan.originalAmount / termMonthsSafe).coerceAtMost(loan.remainingAmount)
    val defaultInterest = (loan.remainingAmount * (loan.interestRatePercent / 100.0 / 12.0)).toLong()

    var principalPaid by remember { mutableLongStateOf(defaultPrincipal) }
    var interestPaid by remember { mutableLongStateOf(defaultInterest) }
    var selectedWalletId by remember { mutableStateOf<Long?>(wallets.firstOrNull()?.id) }
    var paymentNote by remember { mutableStateOf("Trả nợ kỳ ${SimpleDateFormat("MM/yyyy", Locale.getDefault()).format(Date())}") }

    val totalPayment = principalPaid + interestPaid
    val colors = LifeTheme.colors

    LifeDialog(
        title = "Ghi Nhận Trả Nợ Ngân Hàng",
        icon = { Icon(Icons.Default.Payment, contentDescription = null, tint = colors.financeStart) },
        onDismissRequest = onDismiss,
        confirmButtonText = "Xác nhận thanh toán",
        onConfirm = {
            if (totalPayment > 0) {
                onConfirmPayment(principalPaid, interestPaid, selectedWalletId, paymentNote)
            }
        },
        dismissButtonText = "Hủy",
        onDismiss = onDismiss
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            // Loan Info Banner
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(LifeTheme.shapes.small)
                    .background(colors.surfaceElevated)
                    .padding(10.dp)
            ) {
                Column {
                    Text(
                        "${loan.bankName} - ${loan.loanName}",
                        style = LifeTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                        color = colors.textPrimary
                    )
                    Text(
                        "Dư nợ còn: ${Formatters.formatVnd(loan.remainingAmount)} • Lãi: ${loan.interestRatePercent}%/năm",
                        style = LifeTheme.typography.bodySmall,
                        color = colors.textSecondary
                    )
                }
            }

            LifeMoneyTextField(
                rawAmount = principalPaid,
                onAmountChange = { principalPaid = it },
                label = "Tiền gốc trả kỳ này"
            )

            LifeMoneyTextField(
                rawAmount = interestPaid,
                onAmountChange = { interestPaid = it },
                label = "Tiền lãi trả kỳ này"
            )

            // Total banner
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(LifeTheme.shapes.medium)
                    .background(colors.financeStart.copy(alpha = 0.12f))
                    .padding(12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    "Tổng thanh toán:",
                    style = LifeTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                    color = colors.financeStart
                )
                Text(
                    Formatters.formatVnd(totalPayment),
                    style = LifeTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    color = colors.financeStart
                )
            }

            if (wallets.isNotEmpty()) {
                Text("Trích tiền từ ví:", style = LifeTheme.typography.labelSmall, fontWeight = FontWeight.Bold)
                LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(wallets) { wallet ->
                        LifeChip(
                            text = "${wallet.name} (${Formatters.formatVnd(wallet.balance)})",
                            selected = selectedWalletId == wallet.id,
                            onClick = { selectedWalletId = wallet.id }
                        )
                    }
                }
            }

            LifeTextField(
                value = paymentNote,
                onValueChange = { paymentNote = it },
                label = "Ghi chú đợt trả nợ"
            )
        }
    }
}

// Dialog: View Repayments History
@Composable
fun LoanRepaymentsHistoryDialog(
    loan: BankLoanEntity,
    repayments: List<LoanRepaymentEntity>,
    onDismiss: () -> Unit
) {
    val colors = LifeTheme.colors
    val totalPrincipal = repayments.sumOf { it.principalPaid }
    val totalInterest = repayments.sumOf { it.interestPaid }
    val totalPaid = repayments.sumOf { it.totalPaid }
    val sdf = remember { SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault()) }

    LifeDialog(
        title = "Lịch Sử Trả Nợ: ${loan.bankName}",
        icon = { Icon(Icons.Default.History, contentDescription = null, tint = colors.brandPrimary) },
        onDismissRequest = onDismiss,
        confirmButtonText = "Đóng",
        onConfirm = onDismiss,
        dismissButtonText = null,
        onDismiss = null
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            // Summary row
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(LifeTheme.shapes.small)
                    .background(colors.surfaceElevated)
                    .padding(10.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text("Tổng gốc đã trả", style = LifeTheme.typography.labelSmall.copy(fontSize = 11.sp), color = colors.textSecondary)
                    Text(Formatters.formatVnd(totalPrincipal), style = LifeTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold), color = colors.success)
                }
                Column {
                    Text("Tổng lãi đã trả", style = LifeTheme.typography.labelSmall.copy(fontSize = 11.sp), color = colors.textSecondary)
                    Text(Formatters.formatVnd(totalInterest), style = LifeTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold), color = colors.warning)
                }
                Column(horizontalAlignment = Alignment.End) {
                    Text("Tổng đã trả", style = LifeTheme.typography.labelSmall.copy(fontSize = 11.sp), color = colors.textSecondary)
                    Text(Formatters.formatVnd(totalPaid), style = LifeTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold), color = colors.textPrimary)
                }
            }

            if (repayments.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        "Chưa có lịch sử thanh toán nào cho khoản vay này.",
                        style = LifeTheme.typography.bodySmall,
                        color = colors.textSecondary
                    )
                }
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(max = 300.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(repayments, key = { it.id }) { item ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(LifeTheme.shapes.small)
                                .background(colors.surface)
                                .border(1.dp, colors.border, LifeTheme.shapes.small)
                                .padding(10.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = if (item.note.isNotBlank()) item.note else "Thanh toán trả nợ",
                                    style = LifeTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                    color = colors.textPrimary
                                )
                                Text(
                                    text = sdf.format(Date(item.paymentDate)),
                                    style = LifeTheme.typography.bodySmall.copy(fontSize = 10.sp),
                                    color = colors.textSecondary
                                )
                                Text(
                                    text = "Gốc: ${Formatters.formatVnd(item.principalPaid)} • Lãi: ${Formatters.formatVnd(item.interestPaid)}",
                                    style = LifeTheme.typography.bodySmall.copy(fontSize = 10.sp),
                                    color = colors.textSecondary
                                )
                            }
                            Text(
                                text = Formatters.formatVnd(item.totalPaid),
                                style = LifeTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                                color = colors.financeStart
                            )
                        }
                    }
                }
            }
        }
    }
}
