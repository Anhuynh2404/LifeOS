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
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.ui.platform.LocalContext
import com.example.core.audio.ToneGenerator
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
    val context = LocalContext.current
    var title by remember { mutableStateOf("Báo thức mục tiêu") }
    var hour by remember { mutableIntStateOf(6) }
    var minute by remember { mutableIntStateOf(30) }
    var selectedMission by remember { mutableStateOf("MATH") }
    var selectedRingtone by remember { mutableStateOf("Giai điệu Bình Minh") }
    var isPreviewingTone by remember { mutableStateOf(false) }
    var smartWake by remember { mutableStateOf(true) }
    val colors = LifeTheme.colors

    val audioPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        if (uri != null) {
            val fileName = uri.lastPathSegment?.substringAfterLast('/')?.substringAfterLast(':') ?: "Nhạc chuông tùy chọn"
            selectedRingtone = "custom:$uri|$fileName"
            ToneGenerator.stop()
            isPreviewingTone = false
        }
    }

    DisposableEffect(Unit) {
        onDispose {
            ToneGenerator.stop()
        }
    }

    val displayRingtoneName = remember(selectedRingtone) {
        if (selectedRingtone.startsWith("custom:")) {
            selectedRingtone.substringAfter('|')
        } else {
            selectedRingtone
        }
    }

    LifeDialog(
        onDismissRequest = {
            ToneGenerator.stop()
            onDismiss()
        },
        title = "Tạo Báo Thức Mới",
        icon = { Icon(Icons.Default.Alarm, contentDescription = null, tint = colors.alarmEnd) },
        confirmButtonText = "Lưu Báo Thức",
        onConfirm = {
            ToneGenerator.stop()
            onConfirm(
                AlarmEntity(
                    title = title.ifBlank { "Báo thức" },
                    hour = hour,
                    minute = minute,
                    daysOfWeek = "1,2,3,4,5",
                    isEnabled = true,
                    missionType = selectedMission,
                    ringtoneName = selectedRingtone,
                    isSmartWakeEnabled = smartWake
                )
            )
        },
        dismissButtonText = "Hủy",
        onDismiss = {
            ToneGenerator.stop()
            onDismiss()
        }
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
                val secret = remember { com.example.core.qr.LaptopQrHelper.getOrCreateDeviceSecret(context) }
                LifeCard(
                    variant = LifeCardVariant.SURFACE,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(10.dp)) {
                        Text("💻 Quét Mã QR Laptop", style = LifeTheme.typography.titleSmall, fontWeight = FontWeight.Bold, color = colors.brandPrimary)
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            "Mở file LifeOS-Laptop-QR.html trên laptop. Bạn có thể chọn chế độ '📌 QR Cố Định' để quét duy nhất 1 mã vĩnh viễn không bao giờ hết hạn!",
                            style = LifeTheme.typography.bodySmall,
                            color = colors.textSecondary
                        )
                    }
                }
            }

            // Ringtone section
            Text("Nhạc chuông báo thức:", style = LifeTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold), color = colors.textPrimary)
            val presetTones = listOf(
                "Giai điệu Bình Minh",
                "Hùng Tráng Kỷ Luật",
                "Chuông Báo Động Quân Đội",
                "Tiếng Chim Hót Thiên Nhiên",
                "Nhạc chuông mặc định máy"
            )
            androidx.compose.foundation.lazy.LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                items(presetTones) { tone ->
                    LifeChip(
                        text = tone,
                        selected = selectedRingtone == tone,
                        onClick = {
                            selectedRingtone = tone
                            ToneGenerator.stop()
                            isPreviewingTone = false
                        }
                    )
                }
            }

            // Custom phone audio picker and preview button
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                LifeButton(
                    text = "📁 Tải nhạc từ máy",
                    onClick = { audioPickerLauncher.launch("audio/*") },
                    variant = LifeButtonVariant.OUTLINE,
                    size = LifeButtonSize.SMALL,
                    leadingIcon = { Icon(Icons.Default.AudioFile, contentDescription = null, modifier = Modifier.size(16.dp)) }
                )

                LifeButton(
                    text = if (isPreviewingTone) "⏹ Dừng" else "▶ Nghe thử",
                    onClick = {
                        if (isPreviewingTone) {
                            ToneGenerator.stop()
                            isPreviewingTone = false
                        } else {
                            ToneGenerator.playAlarmWithRingtone(context, selectedRingtone, onLoop = false)
                            isPreviewingTone = true
                        }
                    },
                    variant = if (isPreviewingTone) LifeButtonVariant.SECONDARY else LifeButtonVariant.PRIMARY,
                    size = LifeButtonSize.SMALL
                )
            }

            Text(
                text = "Đang chọn: $displayRingtoneName",
                style = LifeTheme.typography.bodySmall,
                color = colors.brandPrimary
            )

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
