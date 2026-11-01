package com.example.feature.schedule

import android.content.Context
import android.content.Intent
import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import com.example.core.permission.PermissionHelper
import com.example.designsystem.components.*
import com.example.designsystem.theme.LifeTheme
import com.example.service.BlockerAccessibilityService
import com.example.service.FocusService

data class BlockedAppItem(
    val name: String,
    val packageName: String,
    val category: String,
    var isBlocked: Boolean = true
)

@Composable
fun FocusModeScreen(
    blockedAppNameFromIntent: String? = null,
    onFocusFinished: (durationMinutes: Int, distractionsBlocked: Int) -> Unit,
    onBack: () -> Unit
) {
    val context = LocalContext.current
    val colors = LifeTheme.colors

    val isFocusActive by FocusService.isFocusActive.collectAsState()
    val remainingSeconds by FocusService.remainingSeconds.collectAsState()
    val blockedAttempts by FocusService.blockedAttempts.collectAsState()

    var selectedMinutes by remember { mutableIntStateOf(25) }
    var isStrictMode by remember { mutableStateOf(true) }
    var showAppBlockerDialog by remember { mutableStateOf(false) }
    var showSurrenderDialog by remember { mutableStateOf(false) }
    var surrenderInput by remember { mutableStateOf("") }
    val requiredSurrender = "Tôi thừa nhận mình đã mất tập trung và chọn từ bỏ phiên làm việc này."

    val hasAccessibility = remember(isFocusActive) { PermissionHelper.isAccessibilityGranted(context) }
    val hasUsageStats = remember(isFocusActive) { PermissionHelper.isUsageStatsGranted(context) }

    // Load blocked apps from prefs
    val prefs = remember { context.getSharedPreferences("lifeos_focus_state", Context.MODE_PRIVATE) }
    var blockedAppList by remember {
        mutableStateOf(
            listOf(
                BlockedAppItem("TikTok Global", "com.zhiliaoapp.musically", "Mạng xã hội"),
                BlockedAppItem("TikTok Châu Á", "com.ss.android.ugc.trill", "Mạng xã hội"),
                BlockedAppItem("Facebook", "com.facebook.katana", "Mạng xã hội"),
                BlockedAppItem("Messenger", "com.facebook.orca", "Mạng xã hội"),
                BlockedAppItem("Instagram", "com.instagram.android", "Mạng xã hội"),
                BlockedAppItem("YouTube", "com.google.android.youtube", "Video"),
                BlockedAppItem("Shopee", "com.shopee.vn", "Mua sắm"),
                BlockedAppItem("X / Twitter", "com.twitter.android", "Mạng xã hội"),
                BlockedAppItem("Liên Quân Mobile", "com.garena.game.kgvn", "Trò chơi"),
                BlockedAppItem("Free Fire", "com.dts.freefireth", "Trò chơi"),
                BlockedAppItem("Netflix", "com.netflix.mediaclient", "Giải trí")
            )
        )
    }

    val minutes = remainingSeconds / 60
    val seconds = remainingSeconds % 60
    val timeFormatted = String.format("%02d:%02d", minutes, seconds)

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(colors.background)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = LifeTheme.spacing.screenMargin)
        ) {
            LifeTopBar(
                title = "Chế Độ Tập Trung",
                subtitle = "Pomodoro & Chặn xao nhãng",
                onBackClick = onBack,
                actions = {
                    IconButton(onClick = { showAppBlockerDialog = true }) {
                        Icon(Icons.Default.Shield, contentDescription = "Danh sách app bị chặn", tint = colors.brandPrimary)
                    }
                }
            )

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(vertical = 12.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                // Header Status & Permission warning
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = if (isFocusActive) "🎯 Đang trong phiên làm việc sâu" else "Thiết lập phiên Pomodoro",
                        style = LifeTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = colors.brandPrimary
                    )

                    if (!hasAccessibility && !hasUsageStats) {
                        LifeCard(
                            variant = LifeCardVariant.ELEVATED,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(14.dp)) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Default.Warning, contentDescription = null, tint = colors.warning, modifier = Modifier.size(20.dp))
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        "Cần bật quyền Trợ năng để chặn TikTok",
                                        style = LifeTheme.typography.titleSmall,
                                        fontWeight = FontWeight.Bold,
                                        color = colors.warning
                                    )
                                }
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    "Bật Dịch vụ Trợ năng LifeOS giúp hệ thống phát hiện và đóng ngay lập tức các ứng dụng xao nhãng khi bạn mở chúng.",
                                    style = LifeTheme.typography.bodySmall,
                                    color = colors.textSecondary
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                LifeButton(
                                    text = "Bật Quyền Trợ Năng Ngay 👉",
                                    onClick = {
                                        PermissionHelper.openAccessibilitySettings(context)
                                    },
                                    variant = LifeButtonVariant.PRIMARY,
                                    size = LifeButtonSize.SMALL,
                                    modifier = Modifier.fillMaxWidth()
                                )
                            }
                        }
                    } else {
                        LifeBadge(
                            text = if (hasAccessibility) "🛡️ Lá chắn Trợ năng & UsageStats đang kích hoạt" else "🛡️ Lá chắn ngầm đang kích hoạt",
                            style = LifeBadgeStyle.SUCCESS
                        )
                    }
                }

                // Circular Timer Display
                val totalSec = selectedMinutes * 60f
                val progress = if (totalSec > 0) (remainingSeconds / totalSec).coerceIn(0f, 1f) else 1f

                LifeCircularProgress(
                    progress = progress,
                    size = 220.dp,
                    strokeWidth = 12.dp,
                    customGradient = colors.scheduleGradient
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = timeFormatted,
                            style = LifeTheme.typography.tabularNumberLarge.copy(fontSize = 38.sp),
                            color = colors.textPrimary
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = if (isFocusActive) "Đã chặn $blockedAttempts cám dỗ" else "$selectedMinutes Phút",
                            style = LifeTheme.typography.labelSmall,
                            color = colors.textSecondary
                        )
                    }
                }

                // Duration Selector Chips (if not active)
                if (!isFocusActive) {
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        listOf(15, 25, 45, 60).forEach { mins ->
                            LifeChip(
                                text = "$mins phút",
                                selected = selectedMinutes == mins,
                                onClick = { selectedMinutes = mins }
                            )
                        }
                    }

                    // Strict Mode Toggle
                    LifeCard(
                        variant = LifeCardVariant.SURFACE,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text("Chế độ nghiêm khắc (Strict Mode)", style = LifeTheme.typography.titleSmall, fontWeight = FontWeight.Bold, color = colors.textPrimary)
                                Text("Không thể bỏ cuộc giữa chừng nếu không gõ cam kết", style = LifeTheme.typography.bodySmall, color = colors.textSecondary)
                            }
                            LifeSwitch(
                                checked = isStrictMode,
                                onCheckedChange = { isStrictMode = it }
                            )
                        }
                    }
                }

                // Blocker Test and Info Card
                LifeCard(
                    variant = LifeCardVariant.SURFACE,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text("Chặn ${blockedAppList.count { it.isBlocked }} App Xao Nhãng", style = LifeTheme.typography.titleSmall, fontWeight = FontWeight.Bold, color = colors.textPrimary)
                            Text("TikTok, FB, Insta, Shopee, Games...", style = LifeTheme.typography.bodySmall, color = colors.textSecondary)
                        }
                        LifeButton(
                            text = "Danh sách",
                            onClick = { showAppBlockerDialog = true },
                            variant = LifeButtonVariant.SECONDARY,
                            size = LifeButtonSize.SMALL,
                            testTag = "open_block_list_button"
                        )
                    }
                }

                // Action Buttons
                if (!isFocusActive) {
                    LifeButton(
                        text = "Bắt Đầu Tập Trung ($selectedMinutes Phút)",
                        onClick = {
                            val blockedPkgs = blockedAppList.filter { it.isBlocked }.map { it.packageName }.joinToString(",")
                            prefs.edit().putString("blocked_packages", blockedPkgs).apply()

                            val intent = Intent(context, FocusService::class.java).apply {
                                action = FocusService.ACTION_START
                                putExtra("duration_minutes", selectedMinutes)
                            }
                            ContextCompat.startForegroundService(context, intent)
                        },
                        variant = LifeButtonVariant.PRIMARY,
                        size = LifeButtonSize.LARGE,
                        customGradient = colors.scheduleGradient,
                        leadingIcon = { Icon(Icons.Default.PlayArrow, contentDescription = null, tint = Color.White) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("start_focus_button")
                    )
                } else {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        LifeButton(
                            text = "Dừng Sớm",
                            onClick = {
                                if (isStrictMode) {
                                    showSurrenderDialog = true
                                } else {
                                    val intent = Intent(context, FocusService::class.java).apply {
                                        action = FocusService.ACTION_STOP
                                    }
                                    context.startService(intent)
                                }
                            },
                            variant = LifeButtonVariant.OUTLINE,
                            size = LifeButtonSize.LARGE,
                            modifier = Modifier.weight(1f)
                        )
                        LifeButton(
                            text = "Hoàn Thành (+${selectedMinutes * 2} XP)",
                            onClick = {
                                val intent = Intent(context, FocusService::class.java).apply {
                                    action = FocusService.ACTION_STOP
                                }
                                context.startService(intent)
                                onFocusFinished(selectedMinutes, blockedAttempts)
                            },
                            variant = LifeButtonVariant.PRIMARY,
                            size = LifeButtonSize.LARGE,
                            modifier = Modifier
                                .weight(1.5f)
                                .testTag("complete_focus_button")
                        )
                    }
                }
            }
        }

        // Strict Mode Surrender Dialog
        if (showSurrenderDialog) {
            LifeDialog(
                onDismissRequest = { showSurrenderDialog = false },
                title = "Cam Kết Từ Bỏ Phiên",
                icon = { Icon(Icons.Default.Warning, contentDescription = null, tint = colors.warning) },
                confirmButtonText = "Xác nhận từ bỏ",
                onConfirm = {
                    if (surrenderInput.trim() == requiredSurrender) {
                        showSurrenderDialog = false
                        val intent = Intent(context, FocusService::class.java).apply {
                            action = FocusService.ACTION_STOP
                        }
                        context.startService(intent)
                    }
                },
                dismissButtonText = "Tiếp tục làm việc",
                onDismiss = { showSurrenderDialog = false },
                destructive = true
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text(
                        text = "Để dừng sớm ở chế độ nghiêm khắc, bạn phải gõ lại chính xác câu bên dưới:",
                        style = LifeTheme.typography.bodySmall,
                        color = colors.textSecondary
                    )
                    Text(
                        text = "\"$requiredSurrender\"",
                        style = LifeTheme.typography.bodySmall,
                        fontWeight = FontWeight.Bold,
                        color = colors.warning
                    )
                    LifeTextField(
                        value = surrenderInput,
                        onValueChange = { surrenderInput = it },
                        label = "Gõ lại câu cam kết ở đây",
                        modifier = Modifier.fillMaxWidth()
                    )
                    if (surrenderInput.isNotBlank() && surrenderInput.trim() != requiredSurrender) {
                        Text(
                            text = "Chưa khớp từng ký tự!",
                            style = LifeTheme.typography.labelSmall,
                            color = colors.error
                        )
                    }
                }
            }
        }

        // Blocker App List Dialog with Presets and Individual Toggles
        if (showAppBlockerDialog) {
            LifeDialog(
                onDismissRequest = { showAppBlockerDialog = false },
                title = "Danh Sách Ứng Dụng Bị Chặn",
                icon = { Icon(Icons.Default.Shield, contentDescription = null, tint = colors.brandPrimary) },
                confirmButtonText = "Lưu & Đóng",
                onConfirm = {
                    val blockedPkgs = blockedAppList.filter { it.isBlocked }.map { it.packageName }.joinToString(",")
                    prefs.edit().putString("blocked_packages", blockedPkgs).apply()
                    showAppBlockerDialog = false
                }
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    if (isFocusActive) {
                        Text(
                            "🔒 Phiên Pomodoro đang chạy! Danh sách chặn bị khóa để đảm bảo kỷ luật.",
                            style = LifeTheme.typography.bodySmall,
                            fontWeight = FontWeight.Bold,
                            color = colors.warning
                        )
                    } else {
                        Text(
                            "Chọn các ứng dụng xao nhãng cần chặn khi bắt đầu phiên:",
                            style = LifeTheme.typography.bodySmall,
                            color = colors.textSecondary
                        )
                        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            LifeChip(
                                text = "Chọn tất cả",
                                selected = blockedAppList.all { it.isBlocked },
                                onClick = {
                                    blockedAppList = blockedAppList.map { it.copy(isBlocked = true) }
                                }
                            )
                            LifeChip(
                                text = "Chỉ MXH",
                                selected = false,
                                onClick = {
                                    blockedAppList = blockedAppList.map {
                                        it.copy(isBlocked = it.category == "Mạng xã hội")
                                    }
                                }
                            )
                        }
                    }

                    LazyColumn(
                        modifier = Modifier
                            .fillMaxWidth()
                            .heightIn(max = 280.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(blockedAppList) { app ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column {
                                    Text(app.name, style = LifeTheme.typography.bodyMedium, fontWeight = FontWeight.SemiBold, color = colors.textPrimary)
                                    Text(app.category, style = LifeTheme.typography.labelSmall, color = colors.textSecondary)
                                }
                                LifeSwitch(
                                    checked = app.isBlocked,
                                    enabled = !isFocusActive,
                                    onCheckedChange = { checked ->
                                        blockedAppList = blockedAppList.map {
                                            if (it.packageName == app.packageName) it.copy(isBlocked = checked) else it
                                        }
                                    }
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
