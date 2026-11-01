package com.example.feature.alarm

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.core.audio.ToneGenerator
import com.example.data.entity.TaskEntity
import com.example.designsystem.components.*
import com.example.designsystem.theme.LifeTheme
import kotlinx.coroutines.delay

@Composable
fun MorningRoutineScreen(
    top3Tasks: List<TaskEntity>,
    onStartNewDay: () -> Unit
) {
    val quotes = remember {
        listOf(
            "Mỗi buổi sáng là một trang giấy trắng. Bạn chính là tác giả của câu chuyện hôm nay.",
            "Kỷ luật là cầu nối giữa mục tiêu và thành tựu rực rỡ.",
            "Thức dậy sớm không chỉ mang lại thời gian, mà mang lại cả sự chủ động cho cuộc đời bạn.",
            "Chiến thắng buổi sáng là bạn đã chiến thắng cả ngày dài.",
            "Hành trình vạn dặm bắt đầu từ một bước chân tỉnh táo ngay lúc này.",
            "Đừng chờ đợi động lực. Hãy tạo ra động lực bằng hành động đầu tiên trong ngày.",
            "Sức mạnh không đến từ những gì bạn có thể làm. Nó đến từ việc vượt qua những gì bạn từng nghĩ mình không thể.",
            "Bạn mạnh mẽ hơn sự lười biếng của bản thân rất nhiều."
        )
    }

    var selectedQuote by remember { mutableStateOf(quotes.random()) }
    var isMeditationPlaying by remember { mutableStateOf(false) }
    var meditationSecondsRemaining by remember { mutableIntStateOf(120) }
    val colors = LifeTheme.colors

    DisposableEffect(Unit) {
        onDispose {
            ToneGenerator.stop()
        }
    }

    LaunchedEffect(isMeditationPlaying) {
        if (isMeditationPlaying) {
            ToneGenerator.playMeditationBell()
            while (isMeditationPlaying && meditationSecondsRemaining > 0) {
                delay(1000)
                meditationSecondsRemaining--
                if (meditationSecondsRemaining % 30 == 0 && meditationSecondsRemaining > 0) {
                    ToneGenerator.playMeditationBell()
                }
            }
            if (meditationSecondsRemaining <= 0) {
                isMeditationPlaying = false
                ToneGenerator.stop()
            }
        } else {
            ToneGenerator.stop()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(colors.background)
            .statusBarsPadding()
            .padding(LifeTheme.spacing.screenMargin)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Header
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(top = 16.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(64.dp)
                    .clip(CircleShape)
                    .background(colors.alarmStart.copy(alpha = 0.2f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.WbSunny,
                    contentDescription = null,
                    tint = colors.alarmEnd,
                    modifier = Modifier.size(36.dp)
                )
            }
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = "Nghi Thức Buổi Sáng",
                style = LifeTheme.typography.titleLarge,
                color = colors.textPrimary
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Khởi động tâm trí và thiết lập năng lượng đỉnh cao",
                style = LifeTheme.typography.bodySmall,
                color = colors.textSecondary
            )
        }

        // 1. Inspirational Quote Card
        LifeCard(
            variant = LifeCardVariant.SURFACE,
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Icon(
                    imageVector = Icons.Default.FormatQuote,
                    contentDescription = null,
                    tint = colors.brandPrimary,
                    modifier = Modifier.size(32.dp)
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "\"$selectedQuote\"",
                    style = LifeTheme.typography.bodyLarge.copy(fontStyle = FontStyle.Italic, lineHeight = 24.sp),
                    textAlign = TextAlign.Center,
                    color = colors.textPrimary
                )
                Spacer(modifier = Modifier.height(12.dp))
                LifeButton(
                    text = "Đổi câu truyền cảm hứng",
                    onClick = { selectedQuote = quotes.random() },
                    variant = LifeButtonVariant.TEXT,
                    size = LifeButtonSize.SMALL,
                    leadingIcon = { Icon(Icons.Default.Refresh, contentDescription = null, modifier = Modifier.size(16.dp)) }
                )
            }
        }

        // 2. Meditation Chime Player Card
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
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(colors.scheduleStart.copy(alpha = 0.15f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Default.SelfImprovement, contentDescription = null, tint = colors.scheduleStart, modifier = Modifier.size(22.dp))
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Text("Chuông Thiền Tĩnh Tâm", style = LifeTheme.typography.titleSmall, color = colors.textPrimary)
                }
                LifeBadge(
                    text = String.format("%02d:%02d", meditationSecondsRemaining / 60, meditationSecondsRemaining % 60),
                    style = LifeBadgeStyle.BRAND
                )
            }

            Spacer(modifier = Modifier.height(10.dp))
            Text(
                text = "Âm thanh sóng hài hòa 432Hz giúp não bộ chuyển từ ngái ngủ sang tập trung minh triết.",
                style = LifeTheme.typography.bodySmall,
                color = colors.textSecondary
            )
            Spacer(modifier = Modifier.height(14.dp))

            LifeButton(
                text = if (isMeditationPlaying) "Tạm dừng chuông thiền" else "Bắt đầu 2 phút tĩnh tâm",
                onClick = { isMeditationPlaying = !isMeditationPlaying },
                variant = if (isMeditationPlaying) LifeButtonVariant.DESTRUCTIVE else LifeButtonVariant.PRIMARY,
                customGradient = if (isMeditationPlaying) null else colors.scheduleGradient,
                leadingIcon = {
                    Icon(
                        imageVector = if (isMeditationPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                        contentDescription = null,
                        tint = if (isMeditationPlaying) colors.error else Color.White
                    )
                },
                modifier = Modifier.fillMaxWidth(),
                testTag = "meditation_toggle_button"
            )
        }

        // 3. Top 3 Priorities from Calendar Schedule
        LifeCard(
            variant = LifeCardVariant.SURFACE,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = "📋 3 Việc Trọng Tâm Hôm Nay",
                style = LifeTheme.typography.titleSmall,
                color = colors.textPrimary
            )
            Spacer(modifier = Modifier.height(12.dp))

            if (top3Tasks.isEmpty()) {
                Text(
                    text = "Hôm nay chưa có task nào. Hãy thêm vào lịch biểu!",
                    style = LifeTheme.typography.bodySmall,
                    color = colors.textSecondary
                )
            } else {
                top3Tasks.forEachIndexed { idx, task ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(24.dp)
                                .clip(CircleShape)
                                .background(colors.brandPrimary.copy(alpha = 0.15f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("${idx + 1}", style = LifeTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold), color = colors.brandPrimary)
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(task.title, style = LifeTheme.typography.bodySmall.copy(fontWeight = FontWeight.SemiBold), color = colors.textPrimary)
                            Text("${task.estimatedMinutes} phút • Độ khó: ${task.difficulty}", style = LifeTheme.typography.labelSmall, color = colors.textSecondary)
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Big Start Day Button
        LifeButton(
            text = "Bắt Đầu Ngày Mới Rực Rỡ 🌅",
            onClick = onStartNewDay,
            variant = LifeButtonVariant.PRIMARY,
            size = LifeButtonSize.LARGE,
            leadingIcon = { Icon(Icons.Default.RocketLaunch, contentDescription = null, tint = Color.White) },
            modifier = Modifier.fillMaxWidth(),
            testTag = "start_day_button"
        )

        Spacer(modifier = Modifier.height(16.dp))
    }
}
