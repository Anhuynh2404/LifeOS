package com.example.feature.dashboard

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.core.event.LifeEventBus
import com.example.core.util.Formatters
import androidx.compose.runtime.remember
import com.example.data.entity.AlarmEntity
import com.example.data.entity.BankLoanEntity
import com.example.data.entity.EventLogEntity
import com.example.data.entity.TaskEntity
import com.example.data.preferences.UserPreferences
import com.example.designsystem.components.*
import com.example.designsystem.theme.LifeTheme
import com.example.feature.finance.BankLoanHelper

@Composable
fun DashboardScreen(
    userPrefs: UserPreferences,
    nextAlarm: AlarmEntity?,
    top3Tasks: List<TaskEntity>,
    eventLogs: List<EventLogEntity>,
    weeklySavings: Long,
    eventBus: LifeEventBus,
    bankLoans: List<BankLoanEntity> = emptyList(),
    onNavigateToAlarm: () -> Unit,
    onTriggerTestAlarm: (AlarmEntity) -> Unit,
    onNavigateToFocus: () -> Unit,
    onNavigateToSettings: () -> Unit,
    onNavigateToPermissions: () -> Unit = {},
    onNavigateToFinance: () -> Unit = {},
    onTaskChecked: (TaskEntity) -> Unit,
    onCheckInStreak: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val context = androidx.compose.ui.platform.LocalContext.current
    val hasMissingPermissions = remember { com.example.core.permission.PermissionHelper.hasAnyCriticalMissing(context) }
    val colors = LifeTheme.colors


    // Bank Loan Due Date Reminder Alert List (If any loan is due soon)
    val imminentLoans = remember(bankLoans) {
        bankLoans.filter { !it.isPaidOff && it.remainingAmount > 0 && it.isReminderEnabled }
            .map { it to BankLoanHelper.calculateNextDueDate(it) }
            .filter { it.second.second <= it.first.reminderDaysBefore || it.second.second <= 7 }
            .sortedBy { it.second.second }
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .testTag("dashboard_screen")
            .padding(horizontal = LifeTheme.spacing.screenMargin),
        contentPadding = PaddingValues(top = 16.dp, bottom = 100.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // 1. Premium Header with Profile, Avatar, XP, Streak, Coins & Settings
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
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        LifeAvatar(
                            name = userPrefs.userName,
                            level = userPrefs.level,
                            size = 48.dp
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = Formatters.getGreeting(),
                                style = LifeTheme.typography.labelSmall,
                                color = colors.textSecondary
                            )
                            Text(
                                text = userPrefs.userName,
                                style = LifeTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                color = colors.textPrimary
                            )
                        }
                    }

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        IconButton(
                            onClick = onNavigateToAlarm,
                            modifier = Modifier.testTag("dashboard_alarm_button")
                        ) {
                            Icon(
                                imageVector = Icons.Default.NotificationsActive,
                                contentDescription = "Báo thức",
                                tint = colors.alarmStart
                            )
                        }
                        IconButton(
                            onClick = onNavigateToSettings,
                            modifier = Modifier.testTag("dashboard_settings_button")
                        ) {
                            Icon(
                                imageVector = Icons.Default.Settings,
                                contentDescription = "Cài đặt",
                                tint = colors.textSecondary
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // XP Progress Bar
                val neededXp = userPrefs.level * 100
                val xpProgress = (userPrefs.xp.toFloat() / neededXp).coerceIn(0f, 1f)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Cấp độ ${userPrefs.level}",
                        style = LifeTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                        color = colors.brandPrimary
                    )
                    Text(
                        text = "${userPrefs.xp}/$neededXp XP",
                        style = LifeTheme.typography.labelSmall,
                        color = colors.textSecondary
                    )
                }
                Spacer(modifier = Modifier.height(4.dp))
                LifeProgressBar(progress = xpProgress, height = 6.dp)

                Spacer(modifier = Modifier.height(14.dp))

                val todayStr = remember { java.text.SimpleDateFormat("yyyy-MM-dd", java.util.Locale.getDefault()).format(java.util.Date()) }
                val isCheckedInToday = userPrefs.lastCheckInDate == todayStr

                // Stats Badges: Streak & Coins
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    // Streak Pill (Clickable to Check-in)
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(LifeTheme.shapes.medium)
                            .background(if (isCheckedInToday) colors.challengesStart.copy(alpha = 0.15f) else colors.challengesStart.copy(alpha = 0.08f))
                            .border(1.dp, if (isCheckedInToday) colors.challengesStart else colors.challengesStart.copy(alpha = 0.3f), LifeTheme.shapes.medium)
                            .clickable { onCheckInStreak() }
                            .padding(horizontal = 12.dp, vertical = 10.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.LocalFireDepartment,
                                contentDescription = null,
                                tint = colors.challengesStart,
                                modifier = Modifier.size(24.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Column {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(
                                        text = "${userPrefs.activeStreakDays} ngày",
                                        style = LifeTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                                        color = colors.challengesStart
                                    )
                                    if (isCheckedInToday) {
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text("✓", color = colors.challengesStart, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                                    }
                                }
                                Text(
                                    text = if (isCheckedInToday) "Đã điểm danh" else "Chạm điểm danh!",
                                    style = LifeTheme.typography.labelSmall.copy(fontSize = 10.sp),
                                    color = if (isCheckedInToday) colors.challengesStart else colors.textSecondary
                                )
                            }
                        }
                    }

                    // Coins Pill
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(LifeTheme.shapes.medium)
                            .background(colors.warning.copy(alpha = 0.12f))
                            .border(1.dp, colors.warning.copy(alpha = 0.25f), LifeTheme.shapes.medium)
                            .padding(horizontal = 12.dp, vertical = 10.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.MonetizationOn,
                                contentDescription = null,
                                tint = colors.warning,
                                modifier = Modifier.size(24.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Column {
                                Text(
                                    text = "${userPrefs.coins}",
                                    style = LifeTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                                    color = colors.warning
                                )
                                Text(
                                    text = "Xu Căn Cứ",
                                    style = LifeTheme.typography.labelSmall.copy(fontSize = 10.sp),
                                    color = colors.textSecondary
                                )
                            }
                        }
                    }
                }

                if (!isCheckedInToday) {
                    Spacer(modifier = Modifier.height(10.dp))
                    Surface(
                        shape = LifeTheme.shapes.medium,
                        color = colors.challengesStart.copy(alpha = 0.12f),
                        border = androidx.compose.foundation.BorderStroke(1.dp, colors.challengesStart.copy(alpha = 0.35f)),
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onCheckInStreak() }
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text("🔥", fontSize = 18.sp)
                                Spacer(modifier = Modifier.width(10.dp))
                                Column {
                                    Text(
                                        "Điểm Danh Duy Trì Chuỗi Kỷ Luật",
                                        style = LifeTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                                        color = colors.challengesStart
                                    )
                                    Text(
                                        "Chạm để nhận +1 Ngày, +50 XP & +20 Xu",
                                        style = LifeTheme.typography.labelSmall.copy(fontSize = 11.sp),
                                        color = colors.textSecondary
                                    )
                                }
                            }
                            LifeBadge(text = "+Điểm danh", style = LifeBadgeStyle.WARNING)
                        }
                    }
                }
            }
        }

        // Permission Health Warning Banner (shows if critical alarm/background permissions are missing)
        if (hasMissingPermissions) {
            item {
                LifeCard(
                    variant = LifeCardVariant.ELEVATED,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .clip(CircleShape)
                                .background(colors.warning.copy(alpha = 0.15f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                Icons.Default.WarningAmber,
                                contentDescription = null,
                                tint = colors.warning,
                                modifier = Modifier.size(24.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "Cần cấp quyền chạy ngầm & Báo thức",
                                style = LifeTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold,
                                color = colors.textPrimary
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = "Để báo thức bật màn hình khi tắt app, vui lòng bật quyền cần thiết.",
                                style = LifeTheme.typography.bodySmall,
                                color = colors.textSecondary
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            LifeButton(
                                text = "Thiết lập quyền ngay",
                                onClick = onNavigateToPermissions,
                                variant = LifeButtonVariant.PRIMARY,
                                size = LifeButtonSize.SMALL,
                                customGradient = colors.alarmGradient
                            )
                        }
                    }
                }
            }
        }

        // 2. Next Alarm & Smart Wake Hero Card (Robust, responsive 2-column layout)
        item {
            LifeCard(
                variant = LifeCardVariant.SURFACE,
                onClick = onNavigateToAlarm,
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("dashboard_wake_card")
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    // Top Row: Category Label Badge + Action
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.WbSunny,
                                contentDescription = null,
                                tint = colors.alarmStart,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "BÁO THỨC TIẾP THEO",
                                style = LifeTheme.typography.labelSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    letterSpacing = 0.5.sp,
                                    fontSize = 11.sp
                                ),
                                color = colors.alarmEnd
                            )
                        }

                        if (nextAlarm != null) {
                            LifeButton(
                                text = "Thử chuông",
                                onClick = { onTriggerTestAlarm(nextAlarm) },
                                variant = LifeButtonVariant.PRIMARY,
                                size = LifeButtonSize.SMALL,
                                customGradient = colors.alarmGradient,
                                leadingIcon = {
                                    Icon(Icons.Default.PlayArrow, contentDescription = null, tint = Color.White, modifier = Modifier.size(14.dp))
                                },
                                testTag = "test_trigger_alarm_button"
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Content Row: Time on left, Details on right
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Left: Large Time Display
                        Column(
                            horizontalAlignment = Alignment.Start,
                            modifier = Modifier.padding(end = 16.dp)
                        ) {
                            Text(
                                text = if (nextAlarm != null) Formatters.formatHourMinute(nextAlarm.hour, nextAlarm.minute) else "--:--",
                                style = LifeTheme.typography.tabularNumberLarge.copy(
                                    fontSize = 30.sp,
                                    fontWeight = FontWeight.Bold
                                ),
                                color = colors.textPrimary,
                                maxLines = 1,
                                softWrap = false
                            )
                            Text(
                                text = if (nextAlarm != null && nextAlarm.isEnabled) "Đang kích hoạt" else "Đang tắt",
                                style = LifeTheme.typography.labelSmall.copy(fontSize = 11.sp),
                                color = if (nextAlarm?.isEnabled == true) colors.success else colors.textTertiary
                            )
                        }

                        // Subtle Vertical Divider
                        Box(
                            modifier = Modifier
                                .width(1.dp)
                                .height(44.dp)
                                .background(colors.border)
                        )

                        Spacer(modifier = Modifier.width(16.dp))

                        // Right: Title and Mission Tag
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = nextAlarm?.title ?: "Chưa thiết lập báo thức",
                                style = LifeTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold,
                                color = colors.textPrimary,
                                maxLines = 1,
                                overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            val missionLabel = when (nextAlarm?.missionType) {
                                "MATH" -> "🧠 Giải Toán"
                                "LAPTOP_QR" -> "💻 Quét QR Laptop"
                                "BARCODE" -> "📷 Quét mã vạch"
                                "SMILE" -> "😊 Nụ cười 5s"
                                "SHAKE" -> "📱 Lắc điện thoại"
                                else -> "🔔 Chuông thường"
                            }
                            LifeBadge(
                                text = missionLabel,
                                style = LifeBadgeStyle.BRAND
                            )
                        }
                    }
                }
            }
        }

        // Bank Loan Due Date Reminder Alert Card (If any loan is due soon)
        if (imminentLoans.isNotEmpty()) {
            item {
                val (nearestLoan, dueInfo) = imminentLoans.first()
                val (dueDateStr, daysLeft) = dueInfo
                val isUrgent = daysLeft <= 2

                LifeCard(
                    variant = LifeCardVariant.ELEVATED,
                    onClick = onNavigateToFinance,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("dashboard_loan_reminder_card")
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(
                                if (isUrgent) colors.error.copy(alpha = 0.08f)
                                else colors.warning.copy(alpha = 0.08f)
                            )
                            .border(
                                1.5.dp,
                                if (isUrgent) colors.error.copy(alpha = 0.4f)
                                else colors.warning.copy(alpha = 0.4f),
                                LifeTheme.shapes.medium
                            )
                            .padding(14.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(36.dp)
                                        .clip(CircleShape)
                                        .background(
                                            if (isUrgent) colors.error.copy(alpha = 0.2f)
                                            else colors.warning.copy(alpha = 0.2f)
                                        ),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = if (isUrgent) Icons.Default.Warning else Icons.Default.NotificationImportant,
                                        contentDescription = null,
                                        tint = if (isUrgent) colors.error else colors.warning,
                                        modifier = Modifier.size(20.dp)
                                    )
                                }
                                Spacer(modifier = Modifier.width(10.dp))
                                Column {
                                    Text(
                                        text = "Nhắc Hẹn Trả Nợ Ngân Hàng",
                                        style = LifeTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                        color = if (isUrgent) colors.error else colors.warning
                                    )
                                    Text(
                                        text = "${nearestLoan.bankName} - ${nearestLoan.loanName}",
                                        style = LifeTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                                        color = colors.textPrimary
                                    )
                                }
                            }

                            LifeBadge(
                                text = when {
                                    daysLeft < 0 -> "Quá hạn!"
                                    daysLeft == 0 -> "Hôm nay!"
                                    daysLeft == 1 -> "Ngày mai!"
                                    else -> "Còn $daysLeft ngày"
                                },
                                style = if (isUrgent) LifeBadgeStyle.ERROR else LifeBadgeStyle.WARNING
                            )
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(
                                    text = "Hạn thanh toán: $dueDateStr (Ngày ${nearestLoan.duePaymentDay} hàng tháng)",
                                    style = LifeTheme.typography.bodySmall.copy(fontSize = 11.sp),
                                    color = colors.textSecondary
                                )
                                Text(
                                    text = "Dự kiến trả kỳ này: ${Formatters.formatVnd(nearestLoan.monthlyEstimatedPayment)}",
                                    style = LifeTheme.typography.bodySmall.copy(fontSize = 11.sp, fontWeight = FontWeight.SemiBold),
                                    color = colors.textPrimary
                                )
                            }

                            LifeButton(
                                text = "Xem & Trả",
                                onClick = onNavigateToFinance,
                                variant = LifeButtonVariant.PRIMARY,
                                size = LifeButtonSize.SMALL,
                                customGradient = if (isUrgent) Brush.horizontalGradient(listOf(colors.error, Color(0xFFDC2626))) else colors.financeGradient,
                                testTag = "dashboard_pay_loan_button"
                            )
                        }
                    }
                }
            }
        }

        // 3. Quick Stats Bento: Focus Mode & Weekly Savings
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Focus Mode Card
                LifeCard(
                    variant = LifeCardVariant.SURFACE,
                    onClick = onNavigateToFocus,
                    modifier = Modifier
                        .weight(1f)
                        .testTag("dashboard_focus_card")
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(32.dp)
                                .clip(CircleShape)
                                .background(colors.scheduleStart.copy(alpha = 0.12f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Default.HourglassTop, contentDescription = null, tint = colors.scheduleStart, modifier = Modifier.size(18.dp))
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Tập Trung", style = LifeTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold), color = colors.scheduleStart)
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Pomodoro 25p",
                        style = LifeTheme.typography.titleSmall,
                        color = colors.textPrimary
                    )
                    Text(
                        text = "Chặn xao nhãng",
                        style = LifeTheme.typography.labelSmall.copy(fontSize = 11.sp),
                        color = colors.textSecondary
                    )
                }

                // Weekly Savings Card
                LifeCard(
                    variant = LifeCardVariant.SURFACE,
                    modifier = Modifier.weight(1f)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(32.dp)
                                .clip(CircleShape)
                                .background(colors.financeStart.copy(alpha = 0.12f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Default.Savings, contentDescription = null, tint = colors.financeStart, modifier = Modifier.size(18.dp))
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Tiết Kiệm Tuần", style = LifeTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold), color = colors.financeStart)
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = Formatters.formatVnd(weeklySavings),
                        style = LifeTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                        color = colors.textPrimary
                    )
                    Text(
                        text = "Từ chối bốc đồng",
                        style = LifeTheme.typography.labelSmall.copy(fontSize = 11.sp),
                        color = colors.textSecondary
                    )
                }
            }
        }

        // 4. Top 3 Priorities Today with LifeCheckbox & LifeCard
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "🎯 3 Việc Quan Trọng Nhất Hôm Nay",
                    style = LifeTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                    color = colors.textPrimary
                )
                LifeBadge(
                    text = "${top3Tasks.count { it.isCompleted }}/${top3Tasks.size} xong",
                    style = if (top3Tasks.all { it.isCompleted } && top3Tasks.isNotEmpty()) LifeBadgeStyle.SUCCESS else LifeBadgeStyle.NEUTRAL
                )
            }
        }

        if (top3Tasks.isEmpty()) {
            item {
                LifeEmptyState(
                    title = "Không còn việc tồn đọng",
                    description = "Bạn đã hoàn thành hết các việc ưu tiên hôm nay! Hãy nghỉ ngơi hoặc lên kế hoạch mới.",
                    icon = Icons.Default.CheckCircle
                )
            }
        } else {
            items(top3Tasks) { task ->
                LifeCard(
                    variant = LifeCardVariant.SURFACE,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        LifeCheckbox(
                            checked = task.isCompleted,
                            onCheckedChange = { onTaskChecked(task) },
                            testTag = "task_check_${task.id}"
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = task.title,
                                style = LifeTheme.typography.bodyMedium.copy(
                                    fontWeight = if (task.isCompleted) FontWeight.Normal else FontWeight.SemiBold
                                ),
                                color = if (task.isCompleted) colors.textTertiary else colors.textPrimary
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Row(
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "${task.estimatedMinutes} phút",
                                    style = LifeTheme.typography.labelSmall,
                                    color = colors.textSecondary
                                )
                                val (diffStyle, diffLabel) = when (task.difficulty) {
                                    "HARD" -> LifeBadgeStyle.ERROR to "Khó"
                                    "MEDIUM" -> LifeBadgeStyle.WARNING to "Vừa"
                                    else -> LifeBadgeStyle.SUCCESS to "Dễ"
                                }
                                LifeBadge(text = diffLabel, style = diffStyle)
                            }
                        }
                    }
                }
            }
        }

        // 5. System Event Timeline
        item {
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "⚡ Dòng Thời Gian Hệ Thống",
                    style = LifeTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                    color = colors.textPrimary
                )
                Text(
                    text = "Liên kết tự động",
                    style = LifeTheme.typography.labelSmall,
                    color = colors.brandPrimary
                )
            }
        }

        items(eventLogs.take(5)) { log ->
            LifeCard(
                variant = LifeCardVariant.SURFACE,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.Top
                ) {
                    val iconColor = when (log.eventType) {
                        "ALARM_DISMISSED" -> colors.alarmStart
                        "TASK_COMPLETED" -> colors.scheduleStart
                        "HABIT_TRANSFER", "IMPULSE_REJECTED" -> colors.financeStart
                        "WHEEL_REWARD" -> colors.warning
                        else -> colors.brandPrimary
                    }
                    Box(
                        modifier = Modifier
                            .size(10.dp)
                            .clip(CircleShape)
                            .background(iconColor)
                            .offset(y = 4.dp)
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = log.description,
                            style = LifeTheme.typography.bodySmall,
                            color = colors.textPrimary
                        )
                        if (log.xpGained > 0 || log.coinsGained > 0 || log.savedAmount > 0) {
                            Spacer(modifier = Modifier.height(4.dp))
                            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                if (log.xpGained > 0) {
                                    LifeBadge(text = "+${log.xpGained} XP", style = LifeBadgeStyle.BRAND)
                                }
                                if (log.coinsGained > 0) {
                                    LifeBadge(text = "+${log.coinsGained} Xu", style = LifeBadgeStyle.WARNING)
                                }
                                if (log.savedAmount > 0) {
                                    LifeBadge(text = "+${Formatters.formatVnd(log.savedAmount)} tiết kiệm", style = LifeBadgeStyle.SUCCESS)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
