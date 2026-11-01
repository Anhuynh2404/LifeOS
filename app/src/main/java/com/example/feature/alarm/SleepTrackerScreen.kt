package com.example.feature.alarm

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bedtime
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.data.entity.SleepLogEntity
import com.example.designsystem.components.*
import com.example.designsystem.theme.LifeTheme
import com.example.ui.components.CanvasBarChart
import com.example.ui.components.CanvasLineChart

@Composable
fun SleepTrackerScreen(
    sleepLogs: List<SleepLogEntity>,
    onAddSleepLog: (SleepLogEntity) -> Unit,
    onBack: () -> Unit
) {
    var bedtimeReminderHour by remember { mutableIntStateOf(22) }
    var bedtimeReminderMinute by remember { mutableIntStateOf(30) }
    var isReminderEnabled by remember { mutableStateOf(true) }
    val colors = LifeTheme.colors

    val daysLabels = listOf("T2", "T3", "T4", "T5", "T6", "T7", "CN")
    val durationHours = sleepLogs.map { (it.durationMinutes / 60f) }.ifEmpty { listOf(7.5f, 7.2f, 8.0f, 6.8f, 7.8f, 8.2f, 7.6f) }
    val qualityScores = sleepLogs.map { it.qualityScore.toFloat() }.ifEmpty { listOf(85f, 80f, 92f, 78f, 88f, 95f, 90f) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(colors.background)
    ) {
        LifeTopBar(
            title = "Nhật Ký & Chu Kỳ Giấc Ngủ",
            subtitle = "Phân tích nhịp sinh học circadian",
            onBackClick = onBack
        )

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = LifeTheme.spacing.screenMargin),
            contentPadding = PaddingValues(top = 12.dp, bottom = 80.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Smart Wake Mechanism Card
            item {
                LifeCard(
                    variant = LifeCardVariant.SURFACE,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(32.dp)
                                .clip(CircleShape)
                                .background(colors.brandPrimary.copy(alpha = 0.12f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Default.Info, contentDescription = null, tint = colors.brandPrimary, modifier = Modifier.size(18.dp))
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Text("Cơ Chế Báo Thức Chu Kỳ Ngủ", style = LifeTheme.typography.titleSmall, color = colors.textPrimary)
                    }
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "LifeOS tính toán pha ngủ nông trong cửa sổ 15-30 phút trước giờ hẹn để đánh thức bạn tự nhiên, giảm thiểu sự mệt mỏi theo nhịp sinh học tự nhiên.",
                        style = LifeTheme.typography.bodySmall,
                        color = colors.textSecondary
                    )
                }
            }

            // 7-day Sleep Duration Chart
            item {
                LifeCard(
                    variant = LifeCardVariant.SURFACE,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Thời Lượng Ngủ 7 Ngày Qua (Giờ)", style = LifeTheme.typography.titleSmall, color = colors.textPrimary)
                    Spacer(modifier = Modifier.height(14.dp))
                    CanvasBarChart(
                        labels = daysLabels.takeLast(durationHours.size),
                        values = durationHours,
                        barColor = colors.scheduleStart
                    )
                }
            }

            // 7-day Quality Trend Line Chart
            item {
                LifeCard(
                    variant = LifeCardVariant.SURFACE,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Chỉ Số Phục Hồi & Chất Lượng (%)", style = LifeTheme.typography.titleSmall, color = colors.textPrimary)
                    Spacer(modifier = Modifier.height(14.dp))
                    CanvasLineChart(
                        labels = daysLabels.takeLast(qualityScores.size),
                        values = qualityScores,
                        lineColor = colors.alarmEnd
                    )
                }
            }

            // Bedtime Reminder Card
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
                            Text("Nhắc Nhở Giờ Đi Ngủ", style = LifeTheme.typography.titleSmall, color = colors.textPrimary)
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                "Nhắc bạn buông điện thoại lúc ${String.format("%02d:%02d", bedtimeReminderHour, bedtimeReminderMinute)} để ngủ đủ 8 tiếng",
                                style = LifeTheme.typography.bodySmall,
                                color = colors.textSecondary
                            )
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        LifeSwitch(
                            checked = isReminderEnabled,
                            onCheckedChange = { isReminderEnabled = it },
                            testTag = "bedtime_switch"
                        )
                    }
                }
            }

            // Recent Sleep Logs
            item {
                Text(
                    text = "Lịch Sử Giấc Ngủ Gần Đây",
                    style = LifeTheme.typography.titleMedium,
                    color = colors.textPrimary
                )
            }

            items(sleepLogs) { log ->
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
                                    .background(colors.brandSurface),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(Icons.Default.Bedtime, contentDescription = null, tint = colors.brandPrimary, modifier = Modifier.size(20.dp))
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(
                                    text = log.date,
                                    style = LifeTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold),
                                    color = colors.textPrimary
                                )
                                Text(
                                    text = "${log.durationMinutes / 60}h ${log.durationMinutes % 60}m ngủ",
                                    style = LifeTheme.typography.bodySmall,
                                    color = colors.textSecondary
                                )
                            }
                        }

                        LifeBadge(
                            text = "Chất lượng ${log.qualityScore}%",
                            style = if (log.qualityScore >= 80) LifeBadgeStyle.SUCCESS else LifeBadgeStyle.WARNING
                        )
                    }
                }
            }
        }
    }
}
