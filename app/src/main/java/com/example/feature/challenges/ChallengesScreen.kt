package com.example.feature.challenges

import android.content.Intent
import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.core.audio.ToneGenerator
import com.example.core.util.Formatters
import com.example.data.entity.ChallengeEntity
import com.example.data.entity.RewardEntity
import com.example.data.entity.SpinHistoryEntity
import com.example.designsystem.components.*
import com.example.designsystem.theme.LifeTheme
import com.example.ui.components.CanvasRewardWheel

@Composable
fun ChallengesScreen(
    challenges: List<ChallengeEntity>,
    spinTickets: Int,
    rewards: List<RewardEntity>,
    spinHistory: List<SpinHistoryEntity>,
    onCheckInChallenge: (ChallengeEntity) -> Unit,
    onAddChallenge: (ChallengeEntity) -> Unit,
    onSpinWheelResult: (RewardEntity) -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedTabIndex by remember { mutableIntStateOf(0) } // 0: Challenges, 1: Social Battles, 2: Reward Wheel
    var isSpinning by remember { mutableStateOf(false) }
    var wonReward by remember { mutableStateOf<RewardEntity?>(null) }
    var showAddChallengeDialog by remember { mutableStateOf(false) }
    val context = LocalContext.current
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
                title = "Thử Thách & Cam Kết",
                subtitle = "Pledge, Wager & Vòng quay may mắn"
            )

            LifeSegmentedControl(
                items = listOf("Thử Thách", "Đấu Nhóm", "Vòng Quay ($spinTickets)"),
                selectedIndex = selectedTabIndex,
                onItemSelected = { selectedTabIndex = it }
            )

            Spacer(modifier = Modifier.height(14.dp))

            when (selectedTabIndex) {
                0 -> {
                    // Challenges List
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(top = 4.dp, bottom = 96.dp),
                        verticalArrangement = Arrangement.spacedBy(14.dp)
                    ) {
                        // Pledge & Wager Disclaimer
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
                                            .size(36.dp)
                                            .clip(CircleShape)
                                            .background(colors.challengesStart.copy(alpha = 0.15f)),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(Icons.Default.Balance, contentDescription = null, tint = colors.challengesStart, modifier = Modifier.size(20.dp))
                                    }
                                    Spacer(modifier = Modifier.width(12.dp))
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text("Cơ Chế Cam Kết (Pledge & Wager)", style = LifeTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold), color = colors.challengesStart)
                                        Spacer(modifier = Modifier.height(2.dp))
                                        Text(
                                            "Đặt cược điểm uy tín để duy trì thói quen. Nếu thất bại, điểm cược sẽ tự động chuyển vào Quỹ từ thiện.",
                                            style = LifeTheme.typography.bodySmall.copy(fontSize = 11.sp),
                                            color = colors.textSecondary
                                        )
                                    }
                                }
                            }
                        }

                        if (challenges.isEmpty()) {
                            item {
                                LifeEmptyState(
                                    title = "Chưa có thử thách nào",
                                    description = "Cam kết với bản thân một thói quen tích cực trong 21 ngày.",
                                    icon = Icons.Default.EmojiEvents,
                                    actionButtonText = "Tạo thử thách",
                                    onActionClick = { showAddChallengeDialog = true }
                                )
                            }
                        } else {
                            items(challenges) { challenge ->
                                ChallengeCard(
                                    challenge = challenge,
                                    onCheckIn = { onCheckInChallenge(challenge) },
                                    onShareWithPartner = {
                                        val shareIntent = Intent(Intent.ACTION_SEND).apply {
                                            type = "text/plain"
                                            putExtra(
                                                Intent.EXTRA_TEXT,
                                                "🔥 [LifeOS Kỷ Luật] Hôm nay tôi đã duy trì chuỗi ${challenge.currentStreak} ngày cho thử thách: '${challenge.title}'! Hãy tiếp tục giám sát tôi nhé!"
                                            )
                                        }
                                        context.startActivity(Intent.createChooser(shareIntent, "Báo cáo bạn đồng hành"))
                                    }
                                )
                            }
                        }
                    }
                }

                1 -> {
                    // Social Battles Screen
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(top = 4.dp, bottom = 96.dp),
                        verticalArrangement = Arrangement.spacedBy(14.dp)
                    ) {
                        item {
                            LifeCard(
                                variant = LifeCardVariant.SURFACE,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Default.Groups, contentDescription = null, tint = colors.challengesStart, modifier = Modifier.size(24.dp))
                                    Spacer(modifier = Modifier.width(12.dp))
                                    Column {
                                        Text("Phòng Thi Đấu & Đồng Đội", style = LifeTheme.typography.titleMedium, color = colors.textPrimary)
                                        Text("Cùng bạn bè thi đua chuỗi kỷ luật thức dậy & thói quen mỗi ngày", style = LifeTheme.typography.bodySmall, color = colors.textSecondary)
                                    }
                                }
                            }
                        }

                        item {
                            LifeCard(
                                variant = LifeCardVariant.SURFACE,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Column(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 12.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    verticalArrangement = Arrangement.spacedBy(10.dp)
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(56.dp)
                                            .clip(CircleShape)
                                            .background(colors.challengesStart.copy(alpha = 0.12f)),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(Icons.Default.EmojiEvents, contentDescription = null, tint = colors.challengesStart, modifier = Modifier.size(30.dp))
                                    }
                                    Text("Chưa Có Đối Thủ Thi Đấu", style = LifeTheme.typography.titleSmall, color = colors.textPrimary)
                                    Text(
                                        "Chia sẻ lời mời thi đấu với bạn bè hoặc người đồng hành để cùng nhau tạo chuỗi kỷ luật và tranh ngôi đầu bảng!",
                                        style = LifeTheme.typography.bodySmall,
                                        color = colors.textSecondary,
                                        modifier = Modifier.padding(horizontal = 16.dp),
                                        textAlign = androidx.compose.ui.text.style.TextAlign.Center
                                    )
                                    Spacer(modifier = Modifier.height(6.dp))
                                    LifeButton(
                                        text = "Chia sẻ lời mời thi đấu",
                                        onClick = {
                                            val shareIntent = Intent(Intent.ACTION_SEND).apply {
                                                type = "text/plain"
                                                putExtra(Intent.EXTRA_SUBJECT, "Lời mời thử thách kỷ luật LifeOS")
                                                putExtra(Intent.EXTRA_TEXT, "Tham gia thử thách kỷ luật cùng mình trên LifeOS! Hãy cùng nhau dậy sớm và xây dựng thói quen tốt mỗi ngày.")
                                            }
                                            context.startActivity(Intent.createChooser(shareIntent, "Mời bạn bè cùng thi đấu"))
                                        },
                                        variant = LifeButtonVariant.PRIMARY,
                                        size = LifeButtonSize.MEDIUM,
                                        leadingIcon = { Icon(Icons.Default.Share, contentDescription = null, tint = Color.White, modifier = Modifier.size(18.dp)) },
                                        customGradient = colors.challengesGradient
                                    )
                                }
                            }
                        }
                    }
                }

                2 -> {
                    // Reward Wheel Screen
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(bottom = 96.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = "🎡 Vòng Quay Phần Thưởng Tự Thân",
                                style = LifeTheme.typography.titleLarge,
                                color = colors.challengesStart
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "Bạn đang có $spinTickets lượt quay may mắn từ việc duy trì streak!",
                                style = LifeTheme.typography.bodySmall,
                                color = colors.textSecondary
                            )
                        }

                        // Wheel Canvas
                        CanvasRewardWheel(
                            rewards = rewards,
                            isSpinning = isSpinning,
                            onSpinFinished = { reward ->
                                isSpinning = false
                                wonReward = reward
                                ToneGenerator.playSuccessChime()
                                onSpinWheelResult(reward)
                            }
                        )

                        // Spin Button
                        LifeButton(
                            text = if (spinTickets > 0) "Quay Thưởng (Dùng 1 vé) 🎡" else "Cần thêm vé quay (Hoàn thành streak)",
                            onClick = {
                                if (!isSpinning && spinTickets > 0) {
                                    isSpinning = true
                                    wonReward = null
                                }
                            },
                            enabled = !isSpinning && spinTickets > 0,
                            variant = LifeButtonVariant.PRIMARY,
                            size = LifeButtonSize.LARGE,
                            customGradient = colors.challengesGradient,
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("spin_wheel_button")
                        )
                    }
                }
            }
        }

        // FAB
        if (selectedTabIndex == 0) {
            LifeFab(
                onClick = { showAddChallengeDialog = true },
                icon = Icons.Default.Add,
                customGradient = colors.challengesGradient,
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(24.dp)
                    .testTag("challenges_fab")
            )
        }

        // Add Challenge Dialog
        if (showAddChallengeDialog) {
            AddChallengeDialog(
                onDismiss = { showAddChallengeDialog = false },
                onConfirm = { challenge ->
                    onAddChallenge(challenge)
                    showAddChallengeDialog = false
                }
            )
        }

        // Won Reward Celebration Dialog
        wonReward?.let { reward ->
            LifeDialog(
                onDismissRequest = { wonReward = null },
                title = "Chúc Mừng Bạn Đã Trúng Thưởng!",
                icon = { Icon(Icons.Default.Celebration, contentDescription = null, tint = colors.warning, modifier = Modifier.size(32.dp)) },
                confirmButtonText = "Nhận Thưởng 🎁",
                onConfirm = { wonReward = null }
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        text = reward.title,
                        style = LifeTheme.typography.titleMedium,
                        color = colors.challengesStart
                    )
                    if (reward.type == "MONETARY_ENTERTAINMENT") {
                        Text(
                            text = "🎉 Phần thưởng này trị giá ${Formatters.formatVnd(reward.valueAmount)} và đã tự động được chuẩn bị từ Quỹ Giải Trí của bạn!",
                            style = LifeTheme.typography.bodySmall,
                            color = colors.textSecondary
                        )
                    } else {
                        Text(
                            text = "Phần thưởng đã được cộng trực tiếp vào hồ sơ của bạn. Tiếp tục phát huy nhé!",
                            style = LifeTheme.typography.bodySmall,
                            color = colors.textSecondary
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun ChallengeCard(
    challenge: ChallengeEntity,
    onCheckIn: () -> Unit,
    onShareWithPartner: () -> Unit
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
            Column(modifier = Modifier.weight(1f)) {
                Text(challenge.title, style = LifeTheme.typography.titleSmall, color = colors.textPrimary)
                Spacer(modifier = Modifier.height(2.dp))
                Text(challenge.description, style = LifeTheme.typography.bodySmall, color = colors.textSecondary)
            }
            LifeBadge(
                text = "${challenge.currentStreak} ngày",
                style = LifeBadgeStyle.WARNING,
                icon = { Icon(Icons.Default.LocalFireDepartment, contentDescription = null, tint = colors.warning, modifier = Modifier.size(14.dp)) }
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Progress bar to target
        val progress = (challenge.currentStreak.toFloat() / challenge.targetDays).coerceIn(0f, 1f)
        LifeProgressBar(progress = progress, customGradient = colors.challengesGradient)

        Spacer(modifier = Modifier.height(10.dp))

        if (challenge.habitRuleText.isNotBlank()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(LifeTheme.shapes.small)
                    .background(colors.surfaceHighlight)
                    .padding(8.dp)
            ) {
                Text(
                    text = "⚡ Quy tắc: ${challenge.habitRuleText}",
                    style = LifeTheme.typography.labelSmall,
                    color = colors.textSecondary
                )
            }
            Spacer(modifier = Modifier.height(10.dp))
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onShareWithPartner) {
                Icon(Icons.Default.Share, contentDescription = "Báo cáo bạn đồng hành", tint = colors.textSecondary)
            }

            LifeButton(
                text = "Điểm Danh Hôm Nay",
                onClick = onCheckIn,
                variant = LifeButtonVariant.PRIMARY,
                size = LifeButtonSize.SMALL,
                customGradient = colors.challengesGradient,
                leadingIcon = { Icon(Icons.Default.Check, contentDescription = null, tint = Color.White, modifier = Modifier.size(16.dp)) },
                testTag = "check_in_challenge_${challenge.id}"
            )
        }
    }
}

@Composable
fun AddChallengeDialog(
    onDismiss: () -> Unit,
    onConfirm: (ChallengeEntity) -> Unit
) {
    var title by remember { mutableStateOf("Không sử dụng điện thoại sau 22h") }
    var description by remember { mutableStateOf("Tập trung đọc sách và đi ngủ đúng giờ để phục hồi") }
    var targetDays by remember { mutableIntStateOf(21) }
    var wagerAmount by remember { mutableStateOf("100") }
    val colors = LifeTheme.colors

    LifeDialog(
        onDismissRequest = onDismiss,
        title = "Tạo Thử Thách & Cam Kết",
        icon = { Icon(Icons.Default.EmojiEvents, contentDescription = null, tint = colors.challengesStart) },
        confirmButtonText = "Cam Kết Tham Gia 🔥",
        onConfirm = {
            if (title.isNotBlank()) {
                onConfirm(
                    ChallengeEntity(
                        title = title,
                        description = description,
                        category = "Kỷ luật",
                        targetDays = targetDays,
                        currentStreak = 0,
                        bestStreak = 0,
                        wagerAmount = wagerAmount.toLongOrNull() ?: 100L,
                        wagerType = "POINTS"
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
                label = "Tên thử thách",
                modifier = Modifier.fillMaxWidth()
            )
            LifeTextField(
                value = description,
                onValueChange = { description = it },
                label = "Mô tả / Động lực",
                modifier = Modifier.fillMaxWidth()
            )
            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                listOf(7, 14, 21, 30).forEach { days ->
                    LifeChip(
                        text = "$days ngày",
                        selected = targetDays == days,
                        onClick = { targetDays = days }
                    )
                }
            }
            LifeTextField(
                value = wagerAmount,
                onValueChange = { wagerAmount = it },
                label = "Khoản đặt cược (Điểm uy tín)",
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}
