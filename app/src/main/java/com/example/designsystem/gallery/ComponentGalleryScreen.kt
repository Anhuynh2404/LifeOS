package com.example.designsystem.gallery

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
import androidx.compose.ui.unit.dp
import com.example.designsystem.components.*
import com.example.designsystem.theme.LifeTheme

@Composable
fun ComponentGalleryScreen(
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val colors = LifeTheme.colors
    var textInput by remember { mutableStateOf("LifeOS Pro") }
    var moneyAmount by remember { mutableStateOf(1500000L) }
    var checkboxState by remember { mutableStateOf(true) }
    var switchState by remember { mutableStateOf(true) }
    var radioSelected by remember { mutableStateOf(0) }
    var chipSelected by remember { mutableStateOf(1) }
    var segmentIndex by remember { mutableStateOf(0) }
    var sliderValue by remember { mutableStateOf(0.65f) }
    var showDialog by remember { mutableStateOf(false) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(colors.background)
            .testTag("component_gallery_screen")
    ) {
        LifeTopBar(
            title = "Thư viện giao diện",
            subtitle = "Design System Showcase & Tokens",
            onBackClick = onBack
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            // Palette section
            Text(text = "1. Bảng màu & Gradients", style = LifeTheme.typography.titleMedium, color = colors.textPrimary)
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                ColorBox("Brand", colors.brandPrimary)
                ColorBox("Alarm", colors.alarmStart)
                ColorBox("Finance", colors.financeStart)
                ColorBox("Schedule", colors.scheduleStart)
                ColorBox("Goals", colors.goalsStart)
                ColorBox("Challenges", colors.challengesStart)
            }

            // Typography section
            Text(text = "2. Kiểu chữ Be Vietnam Pro", style = LifeTheme.typography.titleMedium, color = colors.textPrimary)
            LifeCard(variant = LifeCardVariant.SURFACE) {
                Text(text = "Display: LifeOS 2.0", style = LifeTheme.typography.displayMedium, color = colors.textPrimary)
                Text(text = "Title Large: Tối ưu cuộc sống", style = LifeTheme.typography.titleLarge, color = colors.brandPrimary)
                Text(text = "Body: Trải nghiệm mượt mà, chuẩn mực premium.", style = LifeTheme.typography.bodyMedium, color = colors.textSecondary)
                Spacer(modifier = Modifier.height(8.dp))
                Text(text = "Tabular figures: 1.250.000đ (+18.5%)", style = LifeTheme.typography.tabularNumber, color = colors.financeStart)
            }

            // Buttons section
            Text(text = "3. Nút bấm (LifeButton)", style = LifeTheme.typography.titleMedium, color = colors.textPrimary)
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                LifeButton(
                    text = "Nút chính (Primary Gradient)",
                    onClick = { showDialog = true },
                    variant = LifeButtonVariant.PRIMARY,
                    size = LifeButtonSize.LARGE,
                    leadingIcon = { Icon(Icons.Default.Star, contentDescription = null, tint = Color.White) },
                    modifier = Modifier.fillMaxWidth()
                )
                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    LifeButton(
                        text = "Secondary",
                        onClick = {},
                        variant = LifeButtonVariant.SECONDARY,
                        size = LifeButtonSize.MEDIUM,
                        modifier = Modifier.weight(1f)
                    )
                    LifeButton(
                        text = "Outline",
                        onClick = {},
                        variant = LifeButtonVariant.OUTLINE,
                        size = LifeButtonSize.MEDIUM,
                        modifier = Modifier.weight(1f)
                    )
                    LifeButton(
                        text = "Xóa",
                        onClick = {},
                        variant = LifeButtonVariant.DESTRUCTIVE,
                        size = LifeButtonSize.MEDIUM,
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            // Input fields section
            Text(text = "4. Ô nhập liệu (LifeTextField)", style = LifeTheme.typography.titleMedium, color = colors.textPrimary)
            LifeTextField(
                value = textInput,
                onValueChange = { textInput = it },
                label = "Tiêu đề mục tiêu",
                leadingIcon = { Icon(Icons.Default.Edit, contentDescription = null, tint = colors.brandPrimary) },
                modifier = Modifier.fillMaxWidth()
            )
            LifeMoneyTextField(
                rawAmount = moneyAmount,
                onAmountChange = { moneyAmount = it },
                label = "Số tiền tiết kiệm",
                modifier = Modifier.fillMaxWidth()
            )

            // Controls section
            Text(text = "5. Điều khiển (Controls)", style = LifeTheme.typography.titleMedium, color = colors.textPrimary)
            LifeCard(variant = LifeCardVariant.SURFACE) {
                LifeCheckbox(
                    checked = checkboxState,
                    onCheckedChange = { checkboxState = it },
                    label = "Hoàn thành 30 phút rèn luyện thể thao"
                )
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(text = "Chế độ tập trung sâu", style = LifeTheme.typography.bodyMedium, color = colors.textPrimary)
                    LifeSwitch(checked = switchState, onCheckedChange = { switchState = it })
                }
                Spacer(modifier = Modifier.height(8.dp))
                LifeSegmentedControl(
                    items = listOf("Ngày", "Tuần", "Tháng", "Năm"),
                    selectedIndex = segmentIndex,
                    onItemSelected = { segmentIndex = it }
                )
                Spacer(modifier = Modifier.height(12.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    LifeChip(text = "Tất cả", selected = chipSelected == 0, onClick = { chipSelected = 0 })
                    LifeChip(text = "Quan trọng", selected = chipSelected == 1, badgeText = "3", onClick = { chipSelected = 1 })
                    LifeChip(text = "Đã xong", selected = chipSelected == 2, onClick = { chipSelected = 2 })
                }
                Spacer(modifier = Modifier.height(12.dp))
                LifeSlider(value = sliderValue, onValueChange = { sliderValue = it })
            }

            // Progress & Cards section
            Text(text = "6. Tiến độ & Thẻ thông số", style = LifeTheme.typography.titleMedium, color = colors.textPrimary)
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                LifeStatCard(
                    title = "Tổng tiết kiệm",
                    value = "12.8M",
                    subtitle = "+2.4M tuần này",
                    badgeText = "Xuất sắc",
                    badgeColor = colors.financeStart,
                    modifier = Modifier.weight(1f)
                )
                LifeStatCard(
                    title = "Chuỗi ngày",
                    value = "15 ngày",
                    subtitle = "Kỷ lục mới",
                    badgeText = "Top 5%",
                    badgeColor = colors.challengesStart,
                    modifier = Modifier.weight(1f)
                )
            }

            LifeCard(variant = LifeCardVariant.SURFACE) {
                Text(text = "Tiến độ ngày hôm nay", style = LifeTheme.typography.labelMedium, color = colors.textSecondary)
                Spacer(modifier = Modifier.height(8.dp))
                LifeProgressBar(progress = sliderValue)
                Spacer(modifier = Modifier.height(16.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center
                ) {
                    LifeCircularProgress(progress = sliderValue)
                }
            }

            // Badges & Avatar
            Text(text = "7. Huy hiệu & Avatar", style = LifeTheme.typography.titleMedium, color = colors.textPrimary)
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.CenterVertically) {
                LifeAvatar(name = "Nguyễn Văn Hùng", level = 5)
                LifeBadge(text = "Hoàn thành", style = LifeBadgeStyle.SUCCESS)
                LifeBadge(text = "Cảnh báo", style = LifeBadgeStyle.WARNING)
                LifeBadge(text = "VIP", style = LifeBadgeStyle.BRAND)
            }

            Spacer(modifier = Modifier.height(24.dp))
        }

        if (showDialog) {
            LifeDialog(
                onDismissRequest = { showDialog = false },
                title = "Hộp thoại Design System",
                icon = { Icon(Icons.Default.CheckCircle, contentDescription = null, tint = colors.brandPrimary) },
                onConfirm = { showDialog = false },
                onDismiss = { showDialog = false },
                confirmButtonText = "Tuyệt vời",
                dismissButtonText = "Đóng"
            ) {
                Text(
                    text = "Mọi thành phần trong LifeOS đã được đồng bộ hóa hoàn toàn với Design System thống nhất.",
                    style = LifeTheme.typography.bodyMedium,
                    color = colors.textSecondary
                )
            }
        }
    }
}

@Composable
private fun RowScope.ColorBox(title: String, color: Color) {
    Column(
        modifier = Modifier.weight(1f),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(CircleShape)
                .background(color)
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(text = title, style = LifeTheme.typography.labelSmall, color = LifeTheme.colors.textSecondary)
    }
}
