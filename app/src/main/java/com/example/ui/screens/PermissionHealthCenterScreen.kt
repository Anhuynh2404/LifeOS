package com.example.ui.screens

import android.os.Build
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.core.permission.PermissionHelper
import com.example.designsystem.components.*
import com.example.designsystem.theme.LifeTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PermissionHealthCenterScreen(
    onNavigateBack: () -> Unit
) {
    val context = LocalContext.current
    val colors = LifeTheme.colors
    var refreshTrigger by remember { mutableIntStateOf(0) }

    val hasNotif = remember(refreshTrigger) { PermissionHelper.isNotificationGranted(context) }
    val hasExactAlarm = remember(refreshTrigger) { PermissionHelper.isExactAlarmGranted(context) }
    val hasBattery = remember(refreshTrigger) { PermissionHelper.isBatteryOptimizationIgnored(context) }
    val hasOverlay = remember(refreshTrigger) { PermissionHelper.isOverlayGranted(context) }
    val hasAccessibility = remember(refreshTrigger) { PermissionHelper.isAccessibilityGranted(context) }
    val hasUsageStats = remember(refreshTrigger) { PermissionHelper.isUsageStatsGranted(context) }

    val totalGranted = listOf(hasNotif, hasExactAlarm, hasBattery, hasOverlay, hasAccessibility, hasUsageStats).count { it }
    val healthPercent = (totalGranted * 100) / 6

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text("Thiết Lập Sức Mạnh Hệ Thống", fontWeight = FontWeight.Bold, fontSize = 18.sp, color = colors.textPrimary)
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Quay lại", tint = colors.textPrimary)
                    }
                },
                actions = {
                    IconButton(onClick = { refreshTrigger++ }) {
                        Icon(Icons.Default.Refresh, contentDescription = "Làm mới", tint = colors.textPrimary)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = colors.background
                )
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .background(colors.background)
                .padding(padding)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            item {
                Spacer(modifier = Modifier.height(4.dp))
                // Health score banner
                LifeCard(
                    variant = LifeCardVariant.ELEVATED,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "Độ tin cậy chạy ngầm: $healthPercent%",
                                style = LifeTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = if (healthPercent >= 80) colors.success else colors.warning
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = if (healthPercent == 100) {
                                    "Tuyệt vời! Báo thức và bộ chặn xao nhãng sẽ hoạt động chính xác 100% kể cả khi tắt app."
                                } else {
                                    "Hãy cấp đủ các quyền bên dưới để đảm bảo báo thức kêu đúng giờ và chặn app xao nhãng hiệu quả."
                                },
                                style = LifeTheme.typography.bodySmall,
                                color = colors.textSecondary
                            )
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        LifeBadge(
                            text = "$totalGranted/6 Quyền",
                            style = if (healthPercent >= 80) LifeBadgeStyle.SUCCESS else LifeBadgeStyle.WARNING
                        )
                    }
                }
            }

            // Group 1: Báo thức & Chạy ngầm (Bắt buộc)
            item {
                Text(
                    text = "BÁO THỨC & THÔNG BÁO XUYÊN TẮT MÁY",
                    style = LifeTheme.typography.labelMedium,
                    fontWeight = FontWeight.Bold,
                    color = colors.brandPrimary,
                    modifier = Modifier.padding(top = 8.dp, bottom = 4.dp)
                )
            }

            item {
                PermissionItemCard(
                    icon = Icons.Default.NotificationsActive,
                    title = "Thông báo hệ thống",
                    description = "Cần thiết để phát chuông, nhắc việc, nhắc nợ và cảnh báo hạ nhiệt mua sắm.",
                    isGranted = hasNotif,
                    isRequired = true,
                    onClickAction = {
                        PermissionHelper.openNotificationSettings(context)
                    }
                )
            }

            item {
                PermissionItemCard(
                    icon = Icons.Default.Alarm,
                    title = "Báo thức chính xác (Exact Alarm)",
                    description = "Cho phép LifeOS đánh thức điện thoại chuẩn xác từng giây xuyên qua chế độ ngủ Doze.",
                    isGranted = hasExactAlarm,
                    isRequired = true,
                    onClickAction = {
                        PermissionHelper.openExactAlarmSettings(context)
                    }
                )
            }

            item {
                PermissionItemCard(
                    icon = Icons.Default.BatteryChargingFull,
                    title = "Bỏ qua tối ưu hóa Pin (Không giới hạn)",
                    description = "Ngăn Android tự ý đóng LifeOS khi điện thoại khóa màn hình hoặc qua đêm.",
                    isGranted = hasBattery,
                    isRequired = true,
                    onClickAction = {
                        PermissionHelper.openBatteryOptimizationSettings(context)
                    }
                )
            }

            // Group 2: Focus & Hiển thị đè
            item {
                Text(
                    text = "BỘ CHẶN XAO NHÃNG & MÀN HÌNH KHÓA",
                    style = LifeTheme.typography.labelMedium,
                    fontWeight = FontWeight.Bold,
                    color = colors.brandPrimary,
                    modifier = Modifier.padding(top = 12.dp, bottom = 4.dp)
                )
            }

            item {
                PermissionItemCard(
                    icon = Icons.Default.Layers,
                    title = "Hiển thị trên ứng dụng khác (Overlay)",
                    description = "Bật màn hình giải nhiệm vụ báo thức và che màn hình khi mở app xao nhãng.",
                    isGranted = hasOverlay,
                    isRequired = true,
                    onClickAction = {
                        PermissionHelper.openOverlaySettings(context)
                    }
                )
            }

            item {
                PermissionItemCard(
                    icon = Icons.Default.Accessibility,
                    title = "Dịch vụ Trợ năng (Accessibility Service)",
                    description = "Giúp LifeOS phát hiện ngay lập tức khi bạn mở TikTok/Facebook để tự động chặn lại.",
                    isGranted = hasAccessibility,
                    isRequired = false,
                    onClickAction = {
                        PermissionHelper.openAccessibilitySettings(context)
                    }
                )
            }

            item {
                PermissionItemCard(
                    icon = Icons.Default.QueryStats,
                    title = "Dữ liệu sử dụng ứng dụng (Usage Stats)",
                    description = "Cơ chế dự phòng kép để giám sát thời gian và ứng dụng gây xao nhãng.",
                    isGranted = hasUsageStats,
                    isRequired = false,
                    onClickAction = {
                        PermissionHelper.openUsageStatsSettings(context)
                    }
                )
            }

            // Group 3: Cài đặt riêng theo hãng sản xuất (OEM Auto-start)
            item {
                Text(
                    text = "CÀI ĐẶT RIÊNG CHO MÁY ${Build.MANUFACTURER.uppercase()}",
                    style = LifeTheme.typography.labelMedium,
                    fontWeight = FontWeight.Bold,
                    color = colors.brandPrimary,
                    modifier = Modifier.padding(top = 12.dp, bottom = 4.dp)
                )
            }

            item {
                LifeCard(
                    variant = LifeCardVariant.SURFACE,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                Icons.Default.PhoneAndroid,
                                contentDescription = null,
                                tint = colors.brandPrimary
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Tự khởi chạy & Khóa đa nhiệm (${Build.MANUFACTURER.replaceFirstChar { it.uppercase() }})",
                                style = LifeTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold,
                                color = colors.textPrimary
                            )
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "Các dòng máy Xiaomi, Oppo, Realme, Vivo, Samsung, Huawei thường có trình diệt app ngầm rất mạnh. Hãy bật 'Tự khởi chạy' và khóa ổ khóa đa nhiệm cho LifeOS.",
                            style = LifeTheme.typography.bodySmall,
                            color = colors.textSecondary
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        LifeButton(
                            text = "Mở cài đặt chạy ngầm của máy",
                            onClick = {
                                PermissionHelper.openOemAutoStartSettings(context)
                            },
                            variant = LifeButtonVariant.SECONDARY,
                            size = LifeButtonSize.MEDIUM,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }
            }

            item {
                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }
}

@Composable
private fun PermissionItemCard(
    icon: ImageVector,
    title: String,
    description: String,
    isGranted: Boolean,
    isRequired: Boolean,
    onClickAction: () -> Unit
) {
    val colors = LifeTheme.colors

    LifeCard(
        variant = if (isGranted) LifeCardVariant.SURFACE else LifeCardVariant.ELEVATED,
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    modifier = Modifier.weight(1f),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = if (isGranted) colors.success else colors.brandPrimary,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = title,
                        style = LifeTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        color = colors.textPrimary
                    )
                }

                LifeBadge(
                    text = if (isGranted) "ĐÃ CẤP" else if (isRequired) "CẦN THIẾT" else "KHUYÊN DÙNG",
                    style = if (isGranted) LifeBadgeStyle.SUCCESS else if (isRequired) LifeBadgeStyle.ERROR else LifeBadgeStyle.NEUTRAL
                )
            }

            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = description,
                style = LifeTheme.typography.bodySmall,
                color = colors.textSecondary
            )

            if (!isGranted) {
                Spacer(modifier = Modifier.height(10.dp))
                LifeButton(
                    text = "Cấp quyền ngay",
                    onClick = onClickAction,
                    variant = LifeButtonVariant.PRIMARY,
                    size = LifeButtonSize.SMALL,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}
