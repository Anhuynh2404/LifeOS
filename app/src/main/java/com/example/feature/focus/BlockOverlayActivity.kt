package com.example.feature.focus

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.SystemClock
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Block
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.MainActivity
import com.example.designsystem.components.*
import com.example.designsystem.theme.LifeTheme
import com.example.service.FocusService
import com.example.ui.theme.MyApplicationTheme
import kotlinx.coroutines.delay

class BlockOverlayActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val blockedAppName = intent.getStringExtra("blocked_app_name") ?: "Ứng dụng giải trí"

        setContent {
            MyApplicationTheme {
                BlockOverlayContent(
                    blockedAppName = blockedAppName,
                    onReturnToFocus = {
                        val mainIntent = Intent(this, MainActivity::class.java).apply {
                            flags = Intent.FLAG_ACTIVITY_NEW_TASK or
                                    Intent.FLAG_ACTIVITY_CLEAR_TOP or
                                    Intent.FLAG_ACTIVITY_SINGLE_TOP
                            putExtra("navigate_to", "focus_mode")
                        }
                        startActivity(mainIntent)
                        finish()
                    },
                    onSurrenderSession = {
                        val stopIntent = Intent(this, FocusService::class.java).apply {
                            action = FocusService.ACTION_STOP
                        }
                        startService(stopIntent)

                        val prefs = getSharedPreferences("lifeos_focus_state", Context.MODE_PRIVATE)
                        prefs.edit().putBoolean("is_focus_active", false).apply()

                        val mainIntent = Intent(this, MainActivity::class.java).apply {
                            flags = Intent.FLAG_ACTIVITY_NEW_TASK or
                                    Intent.FLAG_ACTIVITY_CLEAR_TOP or
                                    Intent.FLAG_ACTIVITY_SINGLE_TOP
                        }
                        startActivity(mainIntent)
                        finish()
                    }
                )
            }
        }
    }
}

@Composable
fun BlockOverlayContent(
    blockedAppName: String,
    onReturnToFocus: () -> Unit,
    onSurrenderSession: () -> Unit
) {
    val colors = LifeTheme.colors
    var showSurrenderDialog by remember { mutableStateOf(false) }
    var commitmentInput by remember { mutableStateOf("") }
    val requiredCommitment = "Tôi thừa nhận mình đã mất tập trung và chọn từ bỏ phiên làm việc này."

    // Track remaining seconds using system clock
    var remainingSeconds by remember { mutableLongStateOf(FocusService.getRemainingSecondsDirect()) }

    LaunchedEffect(Unit) {
        while (remainingSeconds > 0) {
            delay(1000)
            remainingSeconds = FocusService.getRemainingSecondsDirect()
        }
    }

    val minutes = remainingSeconds / 60
    val seconds = remainingSeconds % 60
    val timeFormatted = String.format("%02d:%02d", minutes, seconds)

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF0F172A))
            .padding(28.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.fillMaxWidth()
        ) {
            Box(
                modifier = Modifier
                    .size(96.dp)
                    .clip(CircleShape)
                    .background(Color(0xFFEF4444).copy(alpha = 0.2f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Block,
                    contentDescription = null,
                    tint = Color(0xFFEF4444),
                    modifier = Modifier.size(56.dp)
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            Text(
                text = "ĐÃ CHẶN $blockedAppName.UPPERCASE()!",
                style = LifeTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = Color.White,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = "Bạn đang trong phiên Pomodoro tập trung cao độ.\nThời gian còn lại: $timeFormatted",
                style = LifeTheme.typography.bodyMedium,
                fontWeight = FontWeight.SemiBold,
                color = Color(0xFF38BDF8),
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(14.dp))

            Text(
                text = "Mỗi lần từ chối cám dỗ, bạn đang rèn luyện ý chí của một chiến binh kỷ luật. Đừng để 1 phút lướt điện thoại phá hỏng cả ngày nỗ lực!",
                style = LifeTheme.typography.bodySmall,
                color = Color.White.copy(alpha = 0.8f),
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(32.dp))

            LifeButton(
                text = "🎯 Quay Lại Tập Trung Ngay",
                onClick = onReturnToFocus,
                variant = LifeButtonVariant.PRIMARY,
                size = LifeButtonSize.LARGE,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(14.dp))

            LifeButton(
                text = "Tôi muốn bỏ cuộc (Chế độ nghiêm khắc)",
                onClick = { showSurrenderDialog = true },
                variant = LifeButtonVariant.TEXT,
                size = LifeButtonSize.SMALL,
                modifier = Modifier.fillMaxWidth()
            )
        }

        if (showSurrenderDialog) {
            LifeDialog(
                onDismissRequest = { showSurrenderDialog = false },
                title = "Cam Kết Từ Bỏ Phiên",
                icon = { Icon(Icons.Default.Warning, contentDescription = null, tint = colors.warning) },
                confirmButtonText = "Xác nhận từ bỏ",
                onConfirm = {
                    if (commitmentInput.trim() == requiredCommitment) {
                        showSurrenderDialog = false
                        onSurrenderSession()
                    }
                },
                dismissButtonText = "Hủy bỏ",
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
                        text = "\"$requiredCommitment\"",
                        style = LifeTheme.typography.bodySmall,
                        fontWeight = FontWeight.Bold,
                        color = colors.warning
                    )
                    LifeTextField(
                        value = commitmentInput,
                        onValueChange = { commitmentInput = it },
                        label = "Gõ lại câu cam kết ở đây",
                        modifier = Modifier.fillMaxWidth()
                    )
                    if (commitmentInput.isNotBlank() && commitmentInput.trim() != requiredCommitment) {
                        Text(
                            text = "Chưa khớp từng ký tự!",
                            style = LifeTheme.typography.labelSmall,
                            color = colors.error
                        )
                    }
                }
            }
        }
    }
}
