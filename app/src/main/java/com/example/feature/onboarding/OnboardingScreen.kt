package com.example.feature.onboarding

import androidx.compose.animation.*
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.designsystem.components.*
import com.example.designsystem.theme.LifeTheme

@Composable
fun OnboardingScreen(
    onFinish: (name: String, wakeUpTime: String, energyProfile: String) -> Unit
) {
    var step by remember { mutableIntStateOf(1) }
    var userName by remember { mutableStateOf("") }
    var wakeUpHour by remember { mutableIntStateOf(6) }
    var wakeUpMinute by remember { mutableIntStateOf(30) }
    var selectedEnergy by remember { mutableStateOf("Buổi sáng (9:00 - 11:30)") }
    val colors = LifeTheme.colors

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(colors.background)
            .statusBarsPadding()
            .navigationBarsPadding()
            .padding(LifeTheme.spacing.screenMargin)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        // Top Progress Bar
        Column(modifier = Modifier.fillMaxWidth()) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Thiết lập LifeOS",
                    style = LifeTheme.typography.titleMedium,
                    color = colors.brandPrimary
                )
                LifeBadge(text = "Bước $step / 4", style = LifeBadgeStyle.BRAND)
            }
            Spacer(modifier = Modifier.height(10.dp))
            LifeProgressBar(progress = step / 4f, height = 6.dp)
        }

        Spacer(modifier = Modifier.height(28.dp))

        // Step Content
        AnimatedContent(
            targetState = step,
            label = "onboarding_step"
        ) { currentStep ->
            when (currentStep) {
                1 -> {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Box(
                            modifier = Modifier
                                .size(72.dp)
                                .clip(CircleShape)
                                .background(colors.brandSurface),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Person,
                                contentDescription = null,
                                tint = colors.brandPrimary,
                                modifier = Modifier.size(40.dp)
                            )
                        }
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = "Xin chào! Bạn tên là gì?",
                            style = LifeTheme.typography.titleLarge,
                            color = colors.textPrimary,
                            textAlign = TextAlign.Center
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "LifeOS sẽ gọi tên bạn trong các lời chào và báo cáo mỗi ngày.",
                            style = LifeTheme.typography.bodyMedium,
                            color = colors.textSecondary,
                            textAlign = TextAlign.Center
                        )
                        Spacer(modifier = Modifier.height(24.dp))
                        LifeTextField(
                            value = userName,
                            onValueChange = { userName = it },
                            label = "Họ và tên của bạn",
                            singleLine = true,
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("onboarding_name_input")
                        )
                    }
                }
                2 -> {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Box(
                            modifier = Modifier
                                .size(72.dp)
                                .clip(CircleShape)
                                .background(colors.alarmStart.copy(alpha = 0.2f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Alarm,
                                contentDescription = null,
                                tint = colors.alarmEnd,
                                modifier = Modifier.size(40.dp)
                            )
                        }
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = "Giờ thức dậy mục tiêu?",
                            style = LifeTheme.typography.titleLarge,
                            color = colors.textPrimary,
                            textAlign = TextAlign.Center
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "LifeOS sẽ tối ưu hóa chu kỳ ngủ và đánh thức bạn ở trạng thái sảng khoái nhất.",
                            style = LifeTheme.typography.bodyMedium,
                            color = colors.textSecondary,
                            textAlign = TextAlign.Center
                        )
                        Spacer(modifier = Modifier.height(24.dp))
                        LifeCard(
                            variant = LifeCardVariant.SURFACE,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Box(
                                modifier = Modifier.fillMaxWidth(),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = String.format("%02d:%02d", wakeUpHour, wakeUpMinute),
                                    style = LifeTheme.typography.tabularNumberLarge.copy(fontSize = 42.sp),
                                    color = colors.alarmEnd
                                )
                            }
                        }
                        Spacer(modifier = Modifier.height(16.dp))
                        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                            listOf(Pair(5, 30), Pair(6, 0), Pair(6, 30)).forEach { (h, m) ->
                                val label = String.format("%02d:%02d", h, m)
                                LifeChip(
                                    text = label,
                                    selected = wakeUpHour == h && wakeUpMinute == m,
                                    onClick = { wakeUpHour = h; wakeUpMinute = m }
                                )
                            }
                        }
                    }
                }
                3 -> {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Box(
                            modifier = Modifier
                                .size(72.dp)
                                .clip(CircleShape)
                                .background(colors.scheduleStart.copy(alpha = 0.15f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Bolt,
                                contentDescription = null,
                                tint = colors.scheduleStart,
                                modifier = Modifier.size(40.dp)
                            )
                        }
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = "Hồ sơ năng lượng sinh học",
                            style = LifeTheme.typography.titleLarge,
                            color = colors.textPrimary,
                            textAlign = TextAlign.Center
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "Khi nào bạn cảm thấy não bộ tập trung và nhiều năng lượng nhất trong ngày?",
                            style = LifeTheme.typography.bodyMedium,
                            color = colors.textSecondary,
                            textAlign = TextAlign.Center
                        )
                        Spacer(modifier = Modifier.height(20.dp))
                        val options = listOf(
                            "Buổi sáng sớm (6:00 - 9:00)" to "Chim sớm - Thích giải quyết việc khó khi yên tĩnh",
                            "Buổi sáng (9:00 - 11:30)" to "Đỉnh cao cortisol tự nhiên - Tập trung logic cao độ",
                            "Buổi chiều (14:30 - 17:00)" to "Nhịp độ ổn định - Sáng tạo và giải quyết vấn đề",
                            "Buổi tối (20:00 - 23:00)" to "Cú đêm - Năng lượng phục hồi và không gian riêng tư"
                        )
                        options.forEach { (title, subtitle) ->
                            val isSelected = selectedEnergy == title
                            LifeCard(
                                variant = if (isSelected) LifeCardVariant.SURFACE else LifeCardVariant.SURFACE,
                                onClick = { selectedEnergy = title },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp)
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    LifeRadio(selected = isSelected, onClick = { selectedEnergy = title })
                                    Spacer(modifier = Modifier.width(10.dp))
                                    Column {
                                        Text(
                                            text = title,
                                            style = LifeTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold),
                                            color = if (isSelected) colors.scheduleStart else colors.textPrimary
                                        )
                                        Text(
                                            text = subtitle,
                                            style = LifeTheme.typography.bodySmall,
                                            color = colors.textSecondary
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
                4 -> {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Box(
                            modifier = Modifier
                                .size(72.dp)
                                .clip(CircleShape)
                                .background(colors.financeStart.copy(alpha = 0.15f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Security,
                                contentDescription = null,
                                tint = colors.financeStart,
                                modifier = Modifier.size(40.dp)
                            )
                        }
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = "Quyền hạn & An toàn dữ liệu",
                            style = LifeTheme.typography.titleLarge,
                            color = colors.textPrimary,
                            textAlign = TextAlign.Center
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "LifeOS hoạt động hoàn toàn Local-First trên máy của bạn. Dữ liệu của bạn thuộc về bạn.",
                            style = LifeTheme.typography.bodyMedium,
                            color = colors.textSecondary,
                            textAlign = TextAlign.Center
                        )
                        Spacer(modifier = Modifier.height(20.dp))
                        LifeCard(
                            variant = LifeCardVariant.SURFACE,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Default.Alarm, contentDescription = null, tint = colors.alarmStart, modifier = Modifier.size(20.dp))
                                    Spacer(modifier = Modifier.width(10.dp))
                                    Text("Báo thức chính xác (Exact Alarm)", style = LifeTheme.typography.bodySmall, color = colors.textPrimary)
                                }
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Default.Notifications, contentDescription = null, tint = colors.brandPrimary, modifier = Modifier.size(20.dp))
                                    Spacer(modifier = Modifier.width(10.dp))
                                    Text("Thông báo Pomodoro & nhắc giờ ngủ", style = LifeTheme.typography.bodySmall, color = colors.textPrimary)
                                }
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Default.CameraAlt, contentDescription = null, tint = colors.financeStart, modifier = Modifier.size(20.dp))
                                    Spacer(modifier = Modifier.width(10.dp))
                                    Text("Camera: quét hóa đơn & nhiệm vụ nụ cười", style = LifeTheme.typography.bodySmall, color = colors.textPrimary)
                                }
                            }
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(28.dp))

        // Navigation Buttons
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (step > 1) {
                LifeButton(
                    text = "Quay lại",
                    onClick = { step-- },
                    variant = LifeButtonVariant.OUTLINE,
                    size = LifeButtonSize.MEDIUM
                )
            } else {
                LifeButton(
                    text = "Bỏ qua",
                    onClick = {
                        val timeStr = String.format("%02d:%02d", wakeUpHour, wakeUpMinute)
                        onFinish(userName.ifBlank { "Người Dùng" }, timeStr, selectedEnergy)
                    },
                    variant = LifeButtonVariant.TEXT,
                    size = LifeButtonSize.MEDIUM
                )
            }

            LifeButton(
                text = if (step == 4) "Bắt đầu ngay 🚀" else "Tiếp tục",
                onClick = {
                    if (step < 4) {
                        step++
                    } else {
                        val timeStr = String.format("%02d:%02d", wakeUpHour, wakeUpMinute)
                        onFinish(userName.ifBlank { "Người Dùng" }, timeStr, selectedEnergy)
                    }
                },
                variant = LifeButtonVariant.PRIMARY,
                size = LifeButtonSize.MEDIUM,
                testTag = "onboarding_next_button"
            )
        }
    }
}
