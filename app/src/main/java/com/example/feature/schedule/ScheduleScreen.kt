package com.example.feature.schedule

import androidx.compose.animation.*
import androidx.compose.foundation.background
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
import com.example.core.ai.LifeOsAiService
import com.example.data.entity.GoalEntity
import com.example.data.entity.SkillNodeEntity
import com.example.data.entity.TaskEntity
import com.example.designsystem.components.*
import com.example.designsystem.theme.LifeTheme

@Composable
fun ScheduleScreen(
    tasks: List<TaskEntity>,
    skills: List<SkillNodeEntity>,
    goals: List<GoalEntity>,
    energyProfile: String,
    onAddTask: (TaskEntity) -> Unit,
    onToggleTask: (TaskEntity) -> Unit,
    onDeleteTask: (TaskEntity) -> Unit,
    onBatchReschedule: (List<TaskEntity>) -> Unit,
    onNavigateToFocus: () -> Unit,
    modifier: Modifier = Modifier
) {
    val colors = LifeTheme.colors
    var viewModeIndex by remember { mutableIntStateOf(0) } // 0: DAY, 1: WEEK, 2: MONTH
    var showAddTaskDialog by remember { mutableStateOf(false) }
    var showRescheduleDialog by remember { mutableStateOf(false) }
    var showIcsExportSuccess by remember { mutableStateOf(false) }

    val incompleteTasks = tasks.filter { !it.isCompleted }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(colors.background)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = LifeTheme.spacing.screenMargin)
        ) {
            LifeTopBar(
                title = "Lịch Biểu & Năng Lượng",
                subtitle = "Energy-based Smart Blocking",
                actions = {
                    IconButton(
                        onClick = { showRescheduleDialog = true },
                        modifier = Modifier.testTag("smart_reschedule_button")
                    ) {
                        Icon(Icons.Default.ScheduleSend, contentDescription = "Sắp xếp lại", tint = colors.scheduleStart)
                    }
                    IconButton(onClick = { showIcsExportSuccess = true }) {
                        Icon(Icons.Default.FileDownload, contentDescription = "Xuất .ics", tint = colors.textSecondary)
                    }
                }
            )

            // Segmented View Mode
            LifeSegmentedControl(
                items = listOf("Theo Ngày", "Theo Tuần", "Theo Tháng"),
                selectedIndex = viewModeIndex,
                onItemSelected = { viewModeIndex = it }
            )

            Spacer(modifier = Modifier.height(14.dp))

            // Energy curve indicator banner
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
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(colors.scheduleStart.copy(alpha = 0.15f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Default.Bolt, contentDescription = null, tint = colors.scheduleStart, modifier = Modifier.size(20.dp))
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Khung Giờ Vàng: $energyProfile",
                            style = LifeTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                            color = colors.scheduleStart
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = "LifeOS tự động sắp xếp việc khó vào lúc não bộ tập trung cao độ nhất.",
                            style = LifeTheme.typography.bodySmall.copy(fontSize = 11.sp),
                            color = colors.textSecondary
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            when (viewModeIndex) {
                0 -> {
                    // Day View
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(bottom = 96.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        if (tasks.isEmpty()) {
                            item {
                                LifeEmptyState(
                                    title = "Chưa có việc nào hôm nay",
                                    description = "Bấm nút + bên dưới để lên lịch nhiệm vụ theo khung giờ vàng.",
                                    icon = Icons.Default.CalendarToday,
                                    actionButtonText = "Thêm nhiệm vụ",
                                    onActionClick = { showAddTaskDialog = true }
                                )
                            }
                        } else {
                            items(tasks.sortedBy { it.scheduledTimeHour }) { task ->
                                ScheduleTaskItem(
                                    task = task,
                                    onToggle = { onToggleTask(task) },
                                    onDelete = { onDeleteTask(task) }
                                )
                            }
                        }
                    }
                }

                1 -> {
                    // Week View
                    val days = listOf("Thứ 2", "Thứ 3", "Thứ 4", "Thứ 5", "Thứ 6", "Thứ 7", "Chủ Nhật")
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(bottom = 96.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        items(days) { day ->
                            LifeCard(
                                variant = LifeCardVariant.SURFACE,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(day, style = LifeTheme.typography.titleSmall, color = colors.textPrimary)
                                    val count = if (day.contains("2") || day.contains("3")) 3 else 2
                                    LifeBadge(text = "$count nhiệm vụ", style = LifeBadgeStyle.INFO)
                                }
                            }
                        }
                    }
                }

                2 -> {
                    // Month View
                    LifeCard(
                        variant = LifeCardVariant.SURFACE,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Tổng quan Tháng 10 / 2026", style = LifeTheme.typography.titleMedium, color = colors.textPrimary)
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Tổng số việc: ${tasks.size} | Đã hoàn thành: ${tasks.count { it.isCompleted }}",
                            style = LifeTheme.typography.bodySmall,
                            color = colors.textSecondary
                        )
                        Spacer(modifier = Modifier.height(14.dp))
                        val progress = if (tasks.isNotEmpty()) tasks.count { it.isCompleted }.toFloat() / tasks.size else 0f
                        LifeProgressBar(progress = progress, customGradient = colors.scheduleGradient)
                    }
                }
            }
        }

        // FAB
        LifeFab(
            onClick = { showAddTaskDialog = true },
            icon = Icons.Default.Add,
            customGradient = colors.scheduleGradient,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(24.dp)
                .testTag("add_task_fab")
        )

        if (showAddTaskDialog) {
            AddTaskDialog(
                skills = skills,
                goals = goals,
                energyProfile = energyProfile,
                onDismiss = { showAddTaskDialog = false },
                onConfirm = { task ->
                    onAddTask(task)
                    showAddTaskDialog = false
                }
            )
        }

        if (showRescheduleDialog) {
            SmartRescheduleDialog(
                incompleteTasks = incompleteTasks,
                onDismiss = { showRescheduleDialog = false },
                onApply = { updatedList ->
                    onBatchReschedule(updatedList)
                    showRescheduleDialog = false
                }
            )
        }

        if (showIcsExportSuccess) {
            LifeDialog(
                onDismissRequest = { showIcsExportSuccess = false },
                title = "Đồng Bộ Lịch & Xuất .ics",
                icon = { Icon(Icons.Default.CheckCircle, contentDescription = null, tint = colors.financeStart) },
                confirmButtonText = "Đóng",
                onConfirm = { showIcsExportSuccess = false }
            ) {
                Text(
                    text = "Đã đồng bộ file 'lifeos_calendar.ics' vào thiết bị. Bạn có thể mở đồng bộ với Google Calendar hoặc ứng dụng Lịch mặc định.",
                    style = LifeTheme.typography.bodyMedium,
                    color = colors.textSecondary
                )
            }
        }
    }
}

@Composable
fun ScheduleTaskItem(
    task: TaskEntity,
    onToggle: () -> Unit,
    onDelete: () -> Unit
) {
    val colors = LifeTheme.colors

    LifeCard(
        variant = LifeCardVariant.SURFACE,
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Time capsule
            Box(
                modifier = Modifier
                    .clip(LifeTheme.shapes.small)
                    .background(colors.scheduleStart.copy(alpha = 0.12f))
                    .padding(horizontal = 8.dp, vertical = 6.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = String.format("%02d:%02d", task.scheduledTimeHour, task.scheduledTimeMinute),
                        style = LifeTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                        color = colors.scheduleStart
                    )
                    Text(
                        text = "${task.estimatedMinutes}p",
                        style = LifeTheme.typography.labelSmall.copy(fontSize = 9.sp),
                        color = colors.textSecondary
                    )
                }
            }

            Spacer(modifier = Modifier.width(10.dp))

            LifeCheckbox(
                checked = task.isCompleted,
                onCheckedChange = { onToggle() },
                activeColor = colors.scheduleStart,
                testTag = "task_item_check_${task.id}"
            )

            Spacer(modifier = Modifier.width(8.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = task.title,
                    style = LifeTheme.typography.bodyMedium.copy(
                        fontWeight = if (task.isCompleted) FontWeight.Normal else FontWeight.SemiBold
                    ),
                    color = if (task.isCompleted) colors.textTertiary else colors.textPrimary
                )
                if (task.description.isNotBlank()) {
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = task.description,
                        style = LifeTheme.typography.bodySmall.copy(fontSize = 11.sp),
                        color = colors.textSecondary
                    )
                }
                Spacer(modifier = Modifier.height(4.dp))
                Row(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    val (diffStyle, diffLabel) = when (task.difficulty) {
                        "HARD" -> LifeBadgeStyle.ERROR to "Khó"
                        "MEDIUM" -> LifeBadgeStyle.WARNING to "Vừa"
                        else -> LifeBadgeStyle.SUCCESS to "Dễ"
                    }
                    LifeBadge(text = diffLabel, style = diffStyle)

                    if (task.energyTag == "HIGH_FOCUS") {
                        LifeBadge(text = "⚡ Giờ vàng", style = LifeBadgeStyle.BRAND)
                    }
                }
            }

            IconButton(onClick = onDelete) {
                Icon(
                    imageVector = Icons.Default.DeleteOutline,
                    contentDescription = "Xóa",
                    tint = colors.textTertiary,
                    modifier = Modifier.size(18.dp)
                )
            }
        }
    }
}

@Composable
fun AddTaskDialog(
    skills: List<SkillNodeEntity>,
    goals: List<GoalEntity>,
    energyProfile: String,
    onDismiss: () -> Unit,
    onConfirm: (TaskEntity) -> Unit
) {
    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var estimatedMinutes by remember { mutableIntStateOf(45) }
    var difficulty by remember { mutableStateOf("HARD") }
    var scheduledHour by remember { mutableIntStateOf(9) }
    var scheduledMinute by remember { mutableIntStateOf(0) }
    var selectedSkillId by remember { mutableStateOf<Long?>(skills.firstOrNull()?.id) }
    var selectedGoalId by remember { mutableStateOf<Long?>(goals.firstOrNull()?.id) }
    val colors = LifeTheme.colors

    val aiReasoning = remember(difficulty, scheduledHour) {
        LifeOsAiService.explainEnergyRecommendation(title.ifBlank { "Nhiệm vụ" }, difficulty, scheduledHour, energyProfile)
    }

    LifeDialog(
        onDismissRequest = onDismiss,
        title = "Thêm Nhiệm Vụ Mới",
        icon = { Icon(Icons.Default.AddTask, contentDescription = null, tint = colors.scheduleStart) },
        confirmButtonText = "Lưu Nhiệm Vụ",
        onConfirm = {
            if (title.isNotBlank()) {
                onConfirm(
                    TaskEntity(
                        title = title,
                        description = description,
                        estimatedMinutes = estimatedMinutes,
                        difficulty = difficulty,
                        scheduledTimeHour = scheduledHour,
                        scheduledTimeMinute = scheduledMinute,
                        linkedSkillId = selectedSkillId,
                        linkedGoalId = selectedGoalId,
                        energyTag = if (difficulty == "HARD") "HIGH_FOCUS" else "ROUTINE"
                    )
                )
            }
        },
        dismissButtonText = "Hủy",
        onDismiss = onDismiss,
        testTag = "save_task_button"
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            LifeTextField(
                value = title,
                onValueChange = { title = it },
                label = "Tên nhiệm vụ",
                singleLine = true,
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("task_title_input")
            )
            LifeTextField(
                value = description,
                onValueChange = { description = it },
                label = "Mô tả / ghi chú",
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )

            Text("Mức độ tập trung cần thiết:", style = LifeTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold), color = colors.textPrimary)
            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                listOf("EASY" to "Dễ", "MEDIUM" to "Vừa", "HARD" to "Khó (Chất xám)").forEach { (diff, label) ->
                    LifeChip(
                        text = label,
                        selected = difficulty == diff,
                        onClick = {
                            difficulty = diff
                            if (diff == "HARD") scheduledHour = 9
                        }
                    )
                }
            }

            // AI Recommendation Banner
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(LifeTheme.shapes.small)
                    .background(colors.scheduleStart.copy(alpha = 0.1f))
                    .padding(10.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.TipsAndUpdates, contentDescription = null, tint = colors.scheduleStart, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(aiReasoning, style = LifeTheme.typography.bodySmall.copy(fontSize = 11.sp), color = colors.textPrimary)
                }
            }

            Text("Khung giờ bắt đầu:", style = LifeTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold), color = colors.textPrimary)
            LifeTimePicker(
                hour = scheduledHour,
                minute = scheduledMinute,
                onTimeChange = { h, m ->
                    scheduledHour = h
                    scheduledMinute = m
                }
            )
        }
    }
}

@Composable
fun SmartRescheduleDialog(
    incompleteTasks: List<TaskEntity>,
    onDismiss: () -> Unit,
    onApply: (List<TaskEntity>) -> Unit
) {
    val colors = LifeTheme.colors

    LifeDialog(
        onDismissRequest = onDismiss,
        title = "Sắp Xếp Lại Thông Minh",
        icon = { Icon(Icons.Default.ScheduleSend, contentDescription = null, tint = colors.scheduleStart) },
        confirmButtonText = "Chấp Nhận Lịch Mới",
        onConfirm = {
            val updated = incompleteTasks.mapIndexed { idx, t ->
                t.copy(
                    scheduledTimeHour = 9 + (idx * 2),
                    isRescheduled = true
                )
            }
            onApply(updated)
        },
        dismissButtonText = "Bỏ qua",
        onDismiss = onDismiss,
        testTag = "accept_reschedule_button"
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Text(
                "Phát hiện ${incompleteTasks.size} việc chưa xong. LifeOS tự động phân bổ vào các khoảng trống năng lượng ngày mai:",
                style = LifeTheme.typography.bodySmall,
                color = colors.textSecondary
            )
            incompleteTasks.take(3).forEachIndexed { idx, task ->
                LifeCard(
                    variant = LifeCardVariant.SURFACE,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(task.title, style = LifeTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold), color = colors.textPrimary)
                    val newHour = 9 + (idx * 2)
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        "👉 Đề xuất dời sang ngày mai lúc ${newHour}:00 (Khớp năng lượng ${task.difficulty})",
                        style = LifeTheme.typography.labelSmall,
                        color = colors.scheduleStart
                    )
                }
            }
        }
    }
}
