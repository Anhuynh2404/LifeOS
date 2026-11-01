package com.example.feature.goals

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.core.ai.GeneratedTaskPlan
import com.example.core.ai.LifeOsAiService
import com.example.core.audio.ToneGenerator
import com.example.data.entity.GoalEntity
import com.example.data.entity.SkillNodeEntity
import com.example.data.entity.TaskEntity
import com.example.data.entity.VisionBoardItemEntity
import com.example.designsystem.components.*
import com.example.designsystem.theme.LifeTheme
import com.example.ui.components.CanvasSkillTree
import kotlinx.coroutines.launch

@Composable
fun GoalsScreen(
    goals: List<GoalEntity>,
    skills: List<SkillNodeEntity>,
    visionItems: List<VisionBoardItemEntity>,
    onAddGoal: (GoalEntity) -> Unit,
    onUpdateGoalKpi: (GoalEntity, newKpi: Float) -> Unit,
    onAddTasksFromAi: (List<TaskEntity>) -> Unit,
    onAddSkillXp: (SkillNodeEntity, addedXp: Int) -> Unit,
    onAddVisionItem: (VisionBoardItemEntity) -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedTabIndex by remember { mutableIntStateOf(0) } // 0: Goals, 1: Skill Tree, 2: Vision Board
    var showAiBreakdownDialog by remember { mutableStateOf(false) }
    var showAddGoalDialog by remember { mutableStateOf(false) }
    var showAddVisionDialog by remember { mutableStateOf(false) }
    var selectedSkillToInspect by remember { mutableStateOf<SkillNodeEntity?>(null) }
    val colors = LifeTheme.colors

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
                title = "Mục Tiêu & Cây Kỹ Năng",
                subtitle = "RPG Skill Tree & KPI Vision",
                actions = {
                    if (selectedTabIndex == 0) {
                        LifeButton(
                            text = "AI Phân Rã",
                            onClick = { showAiBreakdownDialog = true },
                            variant = LifeButtonVariant.PRIMARY,
                            size = LifeButtonSize.SMALL,
                            customGradient = colors.goalsGradient,
                            leadingIcon = { Icon(Icons.Default.AutoAwesome, contentDescription = null, tint = Color.White, modifier = Modifier.size(14.dp)) },
                            testTag = "ai_breakdown_button"
                        )
                    }
                }
            )

            LifeSegmentedControl(
                items = listOf("Mục Tiêu (KPI)", "Cây Kỹ Năng", "Bảng Tầm Nhìn"),
                selectedIndex = selectedTabIndex,
                onItemSelected = { selectedTabIndex = it }
            )

            Spacer(modifier = Modifier.height(14.dp))

            when (selectedTabIndex) {
                0 -> {
                    // Goals list
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(top = 4.dp, bottom = 96.dp),
                        verticalArrangement = Arrangement.spacedBy(14.dp)
                    ) {
                        if (goals.isEmpty()) {
                            item {
                                LifeEmptyState(
                                    title = "Chưa có mục tiêu nào",
                                    description = "Hãy đặt mục tiêu cuộc sống và dùng AI phân rã thành hành động hàng ngày.",
                                    icon = Icons.Default.Flag,
                                    actionButtonText = "Tạo mục tiêu mới",
                                    onActionClick = { showAddGoalDialog = true }
                                )
                            }
                        } else {
                            items(goals) { goal ->
                                GoalCard(
                                    goal = goal,
                                    onUpdateProgress = { newKpi -> onUpdateGoalKpi(goal, newKpi) }
                                )
                            }
                        }
                    }
                }

                1 -> {
                    // RPG Skill Tree (Canvas)
                    Box(modifier = Modifier.fillMaxSize()) {
                        CanvasSkillTree(
                            skills = skills,
                            onSkillClick = { selectedSkillToInspect = it }
                        )

                        // Top Overlay Hint
                        Box(
                            modifier = Modifier
                                .align(Alignment.TopCenter)
                                .padding(top = 10.dp)
                                .clip(LifeTheme.shapes.pill)
                                .background(Color.Black.copy(alpha = 0.7f))
                                .padding(horizontal = 14.dp, vertical = 6.dp)
                        ) {
                            Text(
                                text = "👆 Chạm vào kỹ năng để rèn luyện • Kéo để khám phá",
                                color = Color.White,
                                style = LifeTheme.typography.labelSmall
                            )
                        }
                    }
                }

                2 -> {
                    // Vision Board Grid
                    LazyVerticalGrid(
                        columns = GridCells.Fixed(2),
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(top = 4.dp, bottom = 96.dp),
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(visionItems) { item ->
                            LifeCard(
                                variant = LifeCardVariant.SURFACE,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(36.dp)
                                        .clip(CircleShape)
                                        .background(colors.goalsStart.copy(alpha = 0.15f)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Visibility,
                                        contentDescription = null,
                                        tint = colors.goalsStart,
                                        modifier = Modifier.size(20.dp)
                                    )
                                }
                                Spacer(modifier = Modifier.height(10.dp))
                                if (item.title.isNotBlank()) {
                                    Text(item.title, style = LifeTheme.typography.titleSmall, color = colors.textPrimary)
                                    Spacer(modifier = Modifier.height(4.dp))
                                }
                                Text(
                                    text = item.content,
                                    style = LifeTheme.typography.bodySmall.copy(fontStyle = FontStyle.Italic),
                                    color = colors.textSecondary
                                )
                            }
                        }
                    }
                }
            }
        }

        // FAB
        LifeFab(
            onClick = {
                when (selectedTabIndex) {
                    0 -> showAddGoalDialog = true
                    2 -> showAddVisionDialog = true
                    else -> showAiBreakdownDialog = true
                }
            },
            icon = Icons.Default.Add,
            customGradient = colors.goalsGradient,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(24.dp)
                .testTag("goals_fab")
        )

        // AI Goal Breakdown Dialog
        if (showAiBreakdownDialog) {
            AiGoalBreakdownDialog(
                skills = skills,
                onDismiss = { showAiBreakdownDialog = false },
                onGeneratedTasks = { tasks ->
                    onAddTasksFromAi(tasks)
                    showAiBreakdownDialog = false
                }
            )
        }

        // Add Goal Dialog
        if (showAddGoalDialog) {
            AddGoalDialog(
                onDismiss = { showAddGoalDialog = false },
                onConfirm = { goal ->
                    onAddGoal(goal)
                    showAddGoalDialog = false
                }
            )
        }

        // Add Vision Item Dialog
        if (showAddVisionDialog) {
            AddVisionDialog(
                onDismiss = { showAddVisionDialog = false },
                onConfirm = { item ->
                    onAddVisionItem(item)
                    showAddVisionDialog = false
                }
            )
        }

        // Skill Detail Dialog
        selectedSkillToInspect?.let { skill ->
            LifeDialog(
                onDismissRequest = { selectedSkillToInspect = null },
                title = skill.title,
                icon = { Icon(Icons.Default.WorkspacePremium, contentDescription = null, tint = colors.goalsStart) },
                confirmButtonText = "Rèn luyện (+30 XP)",
                onConfirm = {
                    onAddSkillXp(skill, 30)
                    ToneGenerator.playSuccessChime()
                    selectedSkillToInspect = null
                },
                dismissButtonText = "Đóng",
                onDismiss = { selectedSkillToInspect = null }
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text(skill.description, style = LifeTheme.typography.bodySmall, color = colors.textSecondary)
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(LifeTheme.shapes.small)
                            .background(colors.goalsStart.copy(alpha = 0.12f))
                            .padding(12.dp)
                    ) {
                        Column {
                            Text("Nhánh: ${skill.branchName}", style = LifeTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold), color = colors.goalsStart)
                            Spacer(modifier = Modifier.height(2.dp))
                            Text("Cấp độ: Lv.${skill.level}", style = LifeTheme.typography.bodySmall, color = colors.textPrimary)
                            Text("Kinh nghiệm: ${skill.currentXp} / ${skill.requiredXp} XP", style = LifeTheme.typography.bodySmall, color = colors.textSecondary)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun GoalCard(
    goal: GoalEntity,
    onUpdateProgress: (Float) -> Unit
) {
    val progress = (goal.currentKpi / goal.targetKpi).coerceIn(0f, 1f)
    val pct = (progress * 100).toInt()
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
            Column(modifier = Modifier.weight(1f)) {
                Text(goal.title, style = LifeTheme.typography.titleSmall, color = colors.textPrimary)
                if (goal.visionDescription.isNotBlank()) {
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(goal.visionDescription, style = LifeTheme.typography.bodySmall, color = colors.textSecondary)
                }
            }
            LifeBadge(text = "$pct%", style = LifeBadgeStyle.BRAND)
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Progress bar
        LifeProgressBar(progress = progress, customGradient = colors.goalsGradient)

        Spacer(modifier = Modifier.height(10.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "${goal.currentKpi} / ${goal.targetKpi} ${goal.kpiUnit}",
                style = LifeTheme.typography.labelSmall,
                color = colors.textSecondary
            )
            LifeButton(
                text = "+ Tiến độ",
                onClick = {
                    val step = (goal.targetKpi / 10f).coerceAtLeast(0.5f)
                    val next = (goal.currentKpi + step).coerceAtMost(goal.targetKpi)
                    onUpdateProgress(next)
                },
                variant = LifeButtonVariant.TEXT,
                size = LifeButtonSize.SMALL
            )
        }
    }
}

@Composable
fun AiGoalBreakdownDialog(
    skills: List<SkillNodeEntity>,
    onDismiss: () -> Unit,
    onGeneratedTasks: (List<TaskEntity>) -> Unit
) {
    var goalInput by remember { mutableStateOf("Chạy Marathon 42km trong 6 tháng") }
    var isLoading by remember { mutableStateOf(false) }
    var generatedPlans by remember { mutableStateOf<List<GeneratedTaskPlan>>(emptyList()) }
    val scope = rememberCoroutineScope()
    val colors = LifeTheme.colors

    LifeDialog(
        onDismissRequest = onDismiss,
        title = "AI Phân Rã Mục Tiêu Lớn",
        icon = { Icon(Icons.Default.AutoAwesome, contentDescription = null, tint = colors.goalsStart) },
        confirmButtonText = if (generatedPlans.isEmpty()) "Phân Rã Ngay ✨" else "Thêm Vào Lịch Biểu 🚀",
        onConfirm = {
            if (generatedPlans.isEmpty()) {
                isLoading = true
                scope.launch {
                    val results = LifeOsAiService.breakdownGoal(goalInput)
                    generatedPlans = results
                    isLoading = false
                }
            } else {
                val taskEntities = generatedPlans.map { plan ->
                    TaskEntity(
                        title = plan.title,
                        description = plan.description,
                        estimatedMinutes = plan.estimatedMinutes,
                        difficulty = plan.difficulty,
                        scheduledTimeHour = plan.suggestedHour,
                        scheduledTimeMinute = 0,
                        energyTag = plan.energyTag,
                        linkedSkillId = skills.firstOrNull()?.id
                    )
                }
                onGeneratedTasks(taskEntities)
            }
        },
        dismissButtonText = "Hủy",
        onDismiss = onDismiss,
        testTag = if (generatedPlans.isEmpty()) "ai_generate_plan_button" else "ai_add_to_schedule_button"
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Text(
                "Gemini AI tự động phân rã mục tiêu lớn thành các hành động chi tiết theo khung giờ năng lượng:",
                style = LifeTheme.typography.bodySmall,
                color = colors.textSecondary
            )
            LifeTextField(
                value = goalInput,
                onValueChange = { goalInput = it },
                label = "Mục tiêu lớn",
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("ai_goal_input")
            )

            // Preset chips
            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                LifeChip(
                    text = "Chạy 42km",
                    selected = goalInput.contains("Marathon"),
                    onClick = { goalInput = "Chạy Marathon 42km trong 6 tháng" }
                )
                LifeChip(
                    text = "IELTS 7.5",
                    selected = goalInput.contains("IELTS"),
                    onClick = { goalInput = "Chinh phục IELTS 7.5" }
                )
                LifeChip(
                    text = "Tài chính",
                    selected = goalInput.contains("Tài chính"),
                    onClick = { goalInput = "Tự do tài chính & Tiết kiệm" }
                )
            }

            if (isLoading) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 12.dp),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    CircularProgressIndicator(color = colors.goalsStart, modifier = Modifier.size(24.dp))
                    Spacer(modifier = Modifier.width(12.dp))
                    Text("Đang phân rã lộ trình...", style = LifeTheme.typography.bodySmall, color = colors.textSecondary)
                }
            }

            if (generatedPlans.isNotEmpty()) {
                Text("Lộ trình đề xuất (${generatedPlans.size} nhiệm vụ):", style = LifeTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold), color = colors.textPrimary)
                generatedPlans.forEach { plan ->
                    LifeCard(
                        variant = LifeCardVariant.SURFACE,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(plan.title, style = LifeTheme.typography.bodySmall.copy(fontWeight = FontWeight.SemiBold), color = colors.textPrimary)
                        Text(
                            "${plan.estimatedMinutes}p • Độ khó: ${plan.difficulty} • Giờ vàng: ${plan.suggestedHour}:00",
                            style = LifeTheme.typography.labelSmall.copy(fontSize = 10.sp),
                            color = colors.goalsStart
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun AddGoalDialog(
    onDismiss: () -> Unit,
    onConfirm: (GoalEntity) -> Unit
) {
    var title by remember { mutableStateOf("") }
    var vision by remember { mutableStateOf("") }
    var targetKpi by remember { mutableStateOf("100") }
    var kpiUnit by remember { mutableStateOf("Điểm") }
    val colors = LifeTheme.colors

    LifeDialog(
        onDismissRequest = onDismiss,
        title = "Tạo Mục Tiêu Mới",
        icon = { Icon(Icons.Default.Flag, contentDescription = null, tint = colors.goalsStart) },
        confirmButtonText = "Lưu Mục Tiêu",
        onConfirm = {
            if (title.isNotBlank()) {
                onConfirm(
                    GoalEntity(
                        title = title,
                        visionDescription = vision,
                        currentKpi = 0f,
                        targetKpi = targetKpi.toFloatOrNull() ?: 100f,
                        kpiUnit = kpiUnit,
                        targetDate = "2027-12-31"
                    )
                )
            }
        },
        dismissButtonText = "Hủy",
        onDismiss = onDismiss
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            LifeTextField(
                value = title,
                onValueChange = { title = it },
                label = "Tên mục tiêu",
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )
            LifeTextField(
                value = vision,
                onValueChange = { vision = it },
                label = "Tầm nhìn & lý do thực hiện",
                modifier = Modifier.fillMaxWidth()
            )
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                LifeTextField(
                    value = targetKpi,
                    onValueChange = { targetKpi = it },
                    label = "KPI mục tiêu",
                    modifier = Modifier.weight(1f)
                )
                LifeTextField(
                    value = kpiUnit,
                    onValueChange = { kpiUnit = it },
                    label = "Đơn vị",
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}

@Composable
fun AddVisionDialog(
    onDismiss: () -> Unit,
    onConfirm: (VisionBoardItemEntity) -> Unit
) {
    var quoteText by remember { mutableStateOf("") }
    var title by remember { mutableStateOf("Ước mơ tương lai") }
    val colors = LifeTheme.colors

    LifeDialog(
        onDismissRequest = onDismiss,
        title = "Thêm Vào Bảng Tầm Nhìn",
        icon = { Icon(Icons.Default.Visibility, contentDescription = null, tint = colors.goalsStart) },
        confirmButtonText = "Ghim Vào Bảng",
        onConfirm = {
            if (quoteText.isNotBlank()) {
                onConfirm(VisionBoardItemEntity(title = title, content = quoteText, type = "QUOTE"))
            }
        },
        dismissButtonText = "Hủy",
        onDismiss = onDismiss
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            LifeTextField(
                value = title,
                onValueChange = { title = it },
                label = "Tiêu đề",
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )
            LifeTextField(
                value = quoteText,
                onValueChange = { quoteText = it },
                label = "Nội dung trích dẫn / Hình ảnh tâm tưởng",
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}
