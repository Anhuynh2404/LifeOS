package com.example.feature.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.core.audio.ToneGenerator
import com.example.core.qr.LaptopQrHelper
import com.example.data.preferences.UserPreferences
import com.example.designsystem.components.*
import com.example.designsystem.theme.LifeTheme

@Composable
fun SettingsScreen(
    userPrefs: UserPreferences,
    onSaveProfile: (name: String, wakeUp: String, energy: String) -> Unit,
    onTestTriggerAlarm: () -> Unit,
    onFastForward48h: () -> Unit,
    onAddSpinTicket: () -> Unit,
    onAddBonusXpAndCoins: () -> Unit,
    onResetDemoData: () -> Unit,
    onNavigateToGallery: () -> Unit = {},
    onNavigateToPermissions: () -> Unit = {},
    onTestRealAlarm1Min: () -> Unit = {},
    onBack: () -> Unit
) {

    var name by remember { mutableStateOf(userPrefs.userName) }
    var wakeUpTime by remember { mutableStateOf(userPrefs.wakeUpTime) }
    var energyProfile by remember { mutableStateOf(userPrefs.energyProfile) }
    var showBackupDialog by remember { mutableStateOf(false) }
    var showLaptopQrDialog by remember { mutableStateOf(false) }
    var showResetConfirmDialog by remember { mutableStateOf(false) }
    val colors = LifeTheme.colors


    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(colors.background)
    ) {
        LifeTopBar(
            title = "Cài Đặt & Quản Trị",
            subtitle = "Tùy biến hồ sơ & Thiết lập hệ thống",
            onBackClick = onBack
        )

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = LifeTheme.spacing.screenMargin),
            contentPadding = PaddingValues(top = 12.dp, bottom = 90.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // 0. Design System Gallery Entry
            item {
                LifeCard(
                    variant = LifeCardVariant.SURFACE,
                    onClick = onNavigateToGallery,
                    modifier = Modifier.fillMaxWidth().testTag("open_gallery_card")
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(44.dp)
                                    .clip(CircleShape)
                                    .background(colors.brandPrimary.copy(alpha = 0.15f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(Icons.Default.Palette, contentDescription = null, tint = colors.brandPrimary, modifier = Modifier.size(24.dp))
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text("🎨 Thư Viện Giao Diện", style = LifeTheme.typography.titleSmall, color = colors.textPrimary)
                                Spacer(modifier = Modifier.height(2.dp))
                                Text("Trưng bày mọi Design Tokens & Life Components", style = LifeTheme.typography.bodySmall, color = colors.textSecondary)
                            }
                        }
                        Icon(Icons.Default.ChevronRight, contentDescription = null, tint = colors.textTertiary)
                    }
                }
            }

            // Permission Health Center Entry
            item {
                LifeCard(
                    variant = LifeCardVariant.SURFACE,
                    onClick = onNavigateToPermissions,
                    modifier = Modifier.fillMaxWidth().testTag("open_permissions_card")
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(44.dp)
                                    .clip(CircleShape)
                                    .background(colors.warning.copy(alpha = 0.15f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(Icons.Default.Security, contentDescription = null, tint = colors.warning, modifier = Modifier.size(24.dp))
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text("🛡️ Thiết Lập Sức Mạnh Hệ Thống", style = LifeTheme.typography.titleSmall, color = colors.textPrimary)
                                Spacer(modifier = Modifier.height(2.dp))
                                Text("Kiểm tra 6 quyền chạy ngầm & Tự khởi chạy máy", style = LifeTheme.typography.bodySmall, color = colors.textSecondary)
                            }
                        }
                        Icon(Icons.Default.ChevronRight, contentDescription = null, tint = colors.textTertiary)
                    }
                }
            }

            // Laptop QR Setup Entry
            item {
                LifeCard(
                    variant = LifeCardVariant.SURFACE,
                    onClick = { showLaptopQrDialog = true },
                    modifier = Modifier.fillMaxWidth().testTag("open_laptop_qr_card")
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(44.dp)
                                    .clip(CircleShape)
                                    .background(colors.brandPrimary.copy(alpha = 0.15f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(Icons.Default.QrCodeScanner, contentDescription = null, tint = colors.brandPrimary, modifier = Modifier.size(24.dp))
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text("💻 Thiết Lập Quét QR Laptop", style = LifeTheme.typography.titleSmall, color = colors.textPrimary)
                                Spacer(modifier = Modifier.height(2.dp))
                                Text("Nhiệm vụ buộc bạn phải rời giường quét mã trên máy tính", style = LifeTheme.typography.bodySmall, color = colors.textSecondary)
                            }
                        }
                        Icon(Icons.Default.ChevronRight, contentDescription = null, tint = colors.textTertiary)
                    }
                }
            }

            // 1. User Profile Section
            item {
                LifeCard(
                    variant = LifeCardVariant.SURFACE,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        Text("👤 Hồ Sơ & Nhịp Sinh Học", style = LifeTheme.typography.titleMedium, color = colors.textPrimary)
                        LifeTextField(
                            value = name,
                            onValueChange = { name = it },
                            label = "Tên hiển thị",
                            modifier = Modifier.fillMaxWidth()
                        )
                        LifeTextField(
                            value = wakeUpTime,
                            onValueChange = { wakeUpTime = it },
                            label = "Giờ thức dậy mục tiêu",
                            modifier = Modifier.fillMaxWidth()
                        )
                        LifeTextField(
                            value = energyProfile,
                            onValueChange = { energyProfile = it },
                            label = "Hồ sơ năng lượng sinh học",
                            modifier = Modifier.fillMaxWidth()
                        )
                        LifeButton(
                            text = "Lưu Thay Đổi",
                            onClick = {
                                onSaveProfile(name, wakeUpTime, energyProfile)
                                ToneGenerator.playSuccessChime()
                            },
                            variant = LifeButtonVariant.PRIMARY,
                            size = LifeButtonSize.MEDIUM,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }
            }

            // 2. Alarm Diagnostics & Maintenance
            item {
                LifeCard(
                    variant = LifeCardVariant.SURFACE,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(36.dp)
                                    .clip(CircleShape)
                                    .background(colors.alarmStart.copy(alpha = 0.15f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(Icons.Default.Alarm, contentDescription = null, tint = colors.alarmEnd, modifier = Modifier.size(20.dp))
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                            Text("🔔 Kiểm Tra Hoạt Động Báo Thức", style = LifeTheme.typography.titleMedium, color = colors.textPrimary)
                        }
                        Text(
                            text = "Kiểm tra chuông báo thức thật xuyên Doze mode để đảm bảo báo thức hoạt động chính xác trên thiết bị:",
                            style = LifeTheme.typography.bodySmall,
                            color = colors.textSecondary
                        )

                        LifeButton(
                            text = "🔥 Thử báo thức thật sau 1 phút (Tắt app để test)",
                            onClick = onTestRealAlarm1Min,
                            variant = LifeButtonVariant.PRIMARY,
                            size = LifeButtonSize.MEDIUM,
                            customGradient = colors.alarmGradient,
                            modifier = Modifier.fillMaxWidth(),
                            testTag = "test_real_alarm_1min_button"
                        )

                        LifeButton(
                            text = "⏰ Kích hoạt chuông báo thức tức thì",
                            onClick = onTestTriggerAlarm,
                            variant = LifeButtonVariant.SECONDARY,
                            size = LifeButtonSize.MEDIUM,
                            modifier = Modifier.fillMaxWidth(),
                            testTag = "trigger_alarm_now_button"
                        )
                    }
                }
            }

            // 3. Clean Data Reset
            item {
                LifeCard(
                    variant = LifeCardVariant.SURFACE,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(36.dp)
                                    .clip(CircleShape)
                                    .background(colors.error.copy(alpha = 0.15f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(Icons.Default.DeleteSweep, contentDescription = null, tint = colors.error, modifier = Modifier.size(20.dp))
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                            Text("🗑️ Quản Trị Dữ Liệu Ứng Dụng", style = LifeTheme.typography.titleMedium, color = colors.textPrimary)
                        }
                        Text(
                            text = "Đưa toàn bộ cơ sở dữ liệu về trạng thái nguyên bản sạch sẽ (xóa hết báo thức, nhiệm vụ, giao dịch, nợ đã tạo và đưa cấp độ về 1).",
                            style = LifeTheme.typography.bodySmall,
                            color = colors.textSecondary
                        )

                        LifeButton(
                            text = "Xóa sạch dữ liệu & Bắt đầu lại từ đầu",
                            onClick = { showResetConfirmDialog = true },
                            variant = LifeButtonVariant.DESTRUCTIVE,
                            size = LifeButtonSize.MEDIUM,
                            modifier = Modifier.fillMaxWidth(),
                            testTag = "reset_clean_data_button"
                        )
                    }
                }
            }

            // 4. App Info Card
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
                        Column {
                            Text("LifeOS", style = LifeTheme.typography.titleMedium, color = colors.brandPrimary, fontWeight = FontWeight.Bold)
                            Spacer(modifier = Modifier.height(2.dp))
                            Text("Phiên bản 1.0.0 (Bản Chính Thức)", style = LifeTheme.typography.bodySmall, color = colors.textSecondary)
                            Text("Hệ điều hành kỷ luật & lối sống cá nhân", style = LifeTheme.typography.bodySmall, color = colors.textTertiary)
                        }
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .clip(CircleShape)
                                .background(colors.brandPrimary.copy(alpha = 0.12f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Default.Verified, contentDescription = null, tint = colors.brandPrimary, modifier = Modifier.size(22.dp))
                        }
                    }
                }
            }

            // 3. Backup and Export JSON Section
            item {
                LifeCard(
                    variant = LifeCardVariant.SURFACE,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        Text("💾 Sao Lưu & Khôi Phục Dữ Liệu", style = LifeTheme.typography.titleMedium, color = colors.textPrimary)
                        Text(
                            text = "Xuất toàn bộ cơ sở dữ liệu Room (Nhiệm vụ, Báo thức, Kỹ năng, Tài chính) ra file JSON an toàn.",
                            style = LifeTheme.typography.bodySmall,
                            color = colors.textSecondary
                        )
                        LifeButton(
                            text = "Xuất File JSON Sao Lưu",
                            onClick = { showBackupDialog = true },
                            variant = LifeButtonVariant.OUTLINE,
                            size = LifeButtonSize.MEDIUM,
                            leadingIcon = { Icon(Icons.Default.Backup, contentDescription = null, tint = colors.brandPrimary, modifier = Modifier.size(18.dp)) },
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }
            }
        }

        if (showBackupDialog) {
            LifeDialog(
                onDismissRequest = { showBackupDialog = false },
                title = "Sao Lưu Thành Công",
                icon = { Icon(Icons.Default.CheckCircle, contentDescription = null, tint = colors.financeStart) },
                confirmButtonText = "Xác nhận",
                onConfirm = { showBackupDialog = false }
            ) {
                Text(
                    text = "Đã tạo bản sao lưu 'lifeos_backup.json' chứa toàn bộ 5 phân hệ dữ liệu. Bạn có thể khôi phục bất cứ lúc nào.",
                    style = LifeTheme.typography.bodyMedium,
                    color = colors.textSecondary
                )
            }
        }

        if (showLaptopQrDialog) {
            val context = LocalContext.current
            val clipboardManager = LocalClipboardManager.current
            val deviceSecret = remember { LaptopQrHelper.getOrCreateDeviceSecret(context) }
            var copied by remember { mutableStateOf(false) }

            LifeDialog(
                onDismissRequest = { showLaptopQrDialog = false },
                title = "Thiết Lập Quét QR Laptop",
                icon = { Icon(Icons.Default.LaptopMac, contentDescription = null, tint = colors.brandPrimary) },
                confirmButtonText = "Đã hiểu",
                onConfirm = { showLaptopQrDialog = false }
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text(
                        text = "Để tắt chuông báo thức, bạn buộc phải ra khỏi giường và dùng camera app quét mã QR đang xoay trên màn hình laptop:",
                        style = LifeTheme.typography.bodySmall,
                        color = colors.textSecondary
                    )

                    LifeCard(
                        variant = LifeCardVariant.SURFACE,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                            Text("Bước 1: Mở trang web trên Laptop", style = LifeTheme.typography.labelMedium, color = colors.brandPrimary)
                            Text("Mở file 'LifeOS-Laptop-QR.html' đính kèm trên trình duyệt máy tính của bạn (hoặc bookmark lại). Hoạt động hoàn toàn Offline!", style = LifeTheme.typography.bodySmall, color = colors.textSecondary)
                        }
                    }

                    LifeCard(
                        variant = LifeCardVariant.SURFACE,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                            Text("Bước 2: Mã Bí Mật Thiết Bị Của Bạn", style = LifeTheme.typography.labelMedium, color = colors.brandPrimary)
                            Text(
                                text = deviceSecret,
                                style = LifeTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold, letterSpacing = 2.sp),
                                color = colors.alarmEnd
                            )
                            LifeButton(
                                text = if (copied) "✓ Đã sao chép mã" else "📋 Sao chép mã bí mật",
                                onClick = {
                                    clipboardManager.setText(AnnotatedString(deviceSecret))
                                    copied = true
                                },
                                variant = LifeButtonVariant.OUTLINE,
                                size = LifeButtonSize.SMALL,
                                modifier = Modifier.fillMaxWidth()
                            )
                        }
                    }

                    LifeCard(
                        variant = LifeCardVariant.SURFACE,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                            Text("Bước 3: Nhập mã vào Laptop & Bắt đầu", style = LifeTheme.typography.labelMedium, color = colors.brandPrimary)
                            Text("Dán mã trên vào trang web laptop. Mã QR động xoay đổi mỗi 30 giây (HMAC-SHA256). Sáng mai chỉ cần rời giường quét mã là tỉnh ngủ!", style = LifeTheme.typography.bodySmall, color = colors.textSecondary)
                        }
                    }
                }
            }
        }

        if (showResetConfirmDialog) {
            LifeDialog(
                onDismissRequest = { showResetConfirmDialog = false },
                title = "Xác Nhận Khởi Tạo Lại",
                icon = { Icon(Icons.Default.Warning, contentDescription = null, tint = colors.error) },
                confirmButtonText = "Xóa & Bắt đầu lại",
                dismissButtonText = "Hủy",
                onDismiss = { showResetConfirmDialog = false },
                onConfirm = {
                    showResetConfirmDialog = false
                    onResetDemoData()
                }
            ) {
                Text(
                    text = "Toàn bộ dữ liệu cá nhân (nhiệm vụ, báo thức, lịch sử chi tiêu, quỹ, nợ...) sẽ được làm sạch để bạn bắt đầu sử dụng app hoàn toàn mới với các thiết lập chuẩn.",
                    style = LifeTheme.typography.bodyMedium,
                    color = colors.textSecondary
                )
            }
        }
    }
}
