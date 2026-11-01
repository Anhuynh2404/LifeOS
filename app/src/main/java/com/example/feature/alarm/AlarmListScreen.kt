package com.example.feature.alarm

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
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.core.util.Formatters
import com.example.data.entity.AlarmEntity
import com.example.designsystem.components.*
import com.example.designsystem.theme.LifeTheme

@Composable
fun AlarmListScreen(
    alarms: List<AlarmEntity>,
    onAddAlarm: (AlarmEntity) -> Unit,
    onToggleAlarm: (AlarmEntity) -> Unit,
    onDeleteAlarm: (AlarmEntity) -> Unit,
    onTriggerAlarm: (AlarmEntity) -> Unit,
    onNavigateToSleepLog: () -> Unit,
    onBack: () -> Unit
) {
    val colors = LifeTheme.colors
    var showAddDialog by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(colors.background)
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            LifeTopBar(
                title = "Báo Thức Thông Minh",
                subtitle = "Chu kỳ giấc ngủ & Đánh thức năng lượng",
                onBackClick = onBack,
                actions = {
                    IconButton(onClick = onNavigateToSleepLog) {
                        Icon(Icons.Default.Bedtime, contentDescription = "Nhật ký giấc ngủ", tint = colors.alarmStart)
                    }
                }
            )

            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = LifeTheme.spacing.screenMargin),
                contentPadding = PaddingValues(top = 12.dp, bottom = 96.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                item {
                    LifeCard(
                        variant = LifeCardVariant.SURFACE,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(40.dp)
                                    .clip(CircleShape)
                                    .background(colors.alarmStart.copy(alpha = 0.15f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(Icons.Default.AutoMode, contentDescription = null, tint = colors.alarmEnd, modifier = Modifier.size(24.dp))
                            }
                            Spacer(modifier = Modifier.width(14.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text("Báo Thức Theo Chu Kỳ Ngủ", style = LifeTheme.typography.titleSmall, color = colors.textPrimary)
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    "Đánh thức trong pha ngủ nông (cửa sổ 15-30 phút) giúp cơ thể tỉnh táo, không mệt mỏi.",
                                    style = LifeTheme.typography.bodySmall,
                                    color = colors.textSecondary
                                )
                            }
                        }
                    }
                }

                if (alarms.isEmpty()) {
                    item {
                        LifeEmptyState(
                            title = "Chưa có báo thức nào",
                            description = "Nhấn nút + bên dưới để tạo báo thức thông minh đầu tiên của bạn.",
                            icon = Icons.Default.Alarm,
                            actionButtonText = "Thêm báo thức",
                            onActionClick = { showAddDialog = true }
                        )
                    }
                } else {
                    items(alarms) { alarm ->
                        AlarmCard(
                            alarm = alarm,
                            onToggle = { onToggleAlarm(alarm) },
                            onDelete = { onDeleteAlarm(alarm) },
                            onTestTrigger = { onTriggerAlarm(alarm) }
                        )
                    }
                }
            }
        }

        // Floating action button
        LifeFab(
            onClick = { showAddDialog = true },
            icon = Icons.Default.Add,
            customGradient = colors.alarmGradient,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(24.dp)
                .testTag("add_alarm_fab")
        )

        if (showAddDialog) {
            AddAlarmDialog(
                onDismiss = { showAddDialog = false },
                onConfirm = { newAlarm ->
                    onAddAlarm(newAlarm)
                    showAddDialog = false
                }
            )
        }
    }
}

@Composable
fun AlarmCard(
    alarm: AlarmEntity,
    onToggle: () -> Unit,
    onDelete: () -> Unit,
    onTestTrigger: () -> Unit
) {
    val colors = LifeTheme.colors

    LifeCard(
        variant = LifeCardVariant.SURFACE,
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = Formatters.formatHourMinute(alarm.hour, alarm.minute),
                    style = LifeTheme.typography.tabularNumberLarge,
                    color = if (alarm.isEnabled) colors.alarmEnd else colors.textTertiary
                )
                Text(
                    text = alarm.title,
                    style = LifeTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold),
                    color = colors.textPrimary
                )
            }

            LifeSwitch(
                checked = alarm.isEnabled,
                onCheckedChange = { onToggle() },
                activeColor = colors.alarmEnd,
                testTag = "alarm_switch_${alarm.id}"
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        val missionBadge = when (alarm.missionType) {
            "MATH" -> "🧠 Giải Toán học"
            "BARCODE" -> "📷 Quét mã vạch"
            "SMILE" -> "😊 Nụ cười 5s"
            "SHAKE" -> "📱 Lắc điện thoại"
            else -> "Chuông thường"
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                LifeBadge(text = missionBadge, style = LifeBadgeStyle.BRAND)
                if (alarm.isSmartWakeEnabled) {
                    LifeBadge(text = "Chu kỳ ${alarm.smartWakeWindowMinutes}p", style = LifeBadgeStyle.INFO)
                }
            }

            Row(verticalAlignment = Alignment.CenterVertically) {
                LifeButton(
                    text = "Thử kêu",
                    onClick = onTestTrigger,
                    variant = LifeButtonVariant.TEXT,
                    size = LifeButtonSize.SMALL
                )
                IconButton(onClick = onDelete) {
                    Icon(
                        imageVector = Icons.Default.DeleteOutline,
                        contentDescription = "Xóa",
                        tint = colors.error,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun AddAlarmDialog(
    onDismiss: () -> Unit,
    onConfirm: (AlarmEntity) -> Unit
) {
    var title by remember { mutableStateOf("Báo thức mục tiêu") }
    var hour by remember { mutableIntStateOf(6) }
    var minute by remember { mutableIntStateOf(30) }
    var selectedMission by remember { mutableStateOf("MATH") }
    var smartWake by remember { mutableStateOf(true) }
    val colors = LifeTheme.colors

    LifeDialog(
        onDismissRequest = onDismiss,
        title = "Tạo Báo Thức Mới",
        icon = { Icon(Icons.Default.Alarm, contentDescription = null, tint = colors.alarmEnd) },
        confirmButtonText = "Lưu Báo Thức",
        onConfirm = {
            onConfirm(
                AlarmEntity(
                    title = title.ifBlank { "Báo thức" },
                    hour = hour,
                    minute = minute,
                    daysOfWeek = "1,2,3,4,5",
                    isEnabled = true,
                    missionType = selectedMission,
                    isSmartWakeEnabled = smartWake
                )
            )
        },
        dismissButtonText = "Hủy",
        onDismiss = onDismiss
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
            LifeTextField(
                value = title,
                onValueChange = { title = it },
                label = "Tên báo thức",
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )

            Text("Giờ thức dậy:", style = LifeTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold), color = colors.textPrimary)

            LifeTimePicker(
                hour = hour,
                minute = minute,
                onTimeChange = { h, m ->
                    hour = h
                    minute = m
                }
            )


            Text("Nhiệm vụ tắt chuông:", style = LifeTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold), color = colors.textPrimary)
            val missions = listOf(
                "MATH" to "Giải toán",
                "LAPTOP_QR" to "💻 QR Laptop",
                "BARCODE" to "Mã vạch",
                "SMILE" to "Cười 5s",
                "SHAKE" to "Lắc máy"
            )
            androidx.compose.foundation.lazy.LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                items(missions) { (type, label) ->
                    LifeChip(
                        text = label,
                        selected = selectedMission == type,
                        onClick = { selectedMission = type }
                    )
                }
            }

            if (selectedMission == "LAPTOP_QR") {
                val context = androidx.compose.ui.platform.LocalContext.current
                val secret = remember { com.example.core.qr.LaptopQrHelper.getOrCreateDeviceSecret(context) }
                LifeCard(
                    variant = LifeCardVariant.SURFACE,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(10.dp)) {
                        Text("💻 Hướng Dẫn QR Laptop", style = LifeTheme.typography.titleSmall, fontWeight = FontWeight.Bold, color = colors.brandPrimary)
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            "Mở file LifeOS-Laptop-QR.html trên trình duyệt laptop và đảm bảo mã thiết bị là: $secret. Mã QR sẽ tự xoay vòng mỗi 30s.",
                            style = LifeTheme.typography.bodySmall,
                            color = colors.textSecondary
                        )
                    }
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Chu kỳ ngủ thông minh", style = LifeTheme.typography.bodySmall, color = colors.textPrimary)
                LifeSwitch(checked = smartWake, onCheckedChange = { smartWake = it })
            }
        }
    }
}
