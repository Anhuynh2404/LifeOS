package com.example.feature.alarm

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.core.audio.ToneGenerator
import com.example.ui.theme.ColorAlarm
import com.example.ui.theme.ColorAlarmDark
import com.example.ui.theme.IndigoPrimary
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun AlarmTriggerScreen(
    alarmTitle: String,
    missionType: String,
    onMissionCompleted: (missionUsed: String) -> Unit
) {
    val context = androidx.compose.ui.platform.LocalContext.current
    var currentMission by remember { mutableStateOf(missionType) }
    var isSolved by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()

    val deviceSecret = remember { com.example.core.qr.LaptopQrHelper.getOrCreateDeviceSecret(context) }
    var qrScanError by remember { mutableStateOf<String?>(null) }


    // Math Mission State
    var mathNum1 by remember { mutableIntStateOf(16) }
    var mathNum2 by remember { mutableIntStateOf(4) }
    var mathNum3 by remember { mutableIntStateOf(19) }
    var mathAnswerInput by remember { mutableStateOf("") }
    var mathError by remember { mutableStateOf(false) }
    val expectedAnswer = (mathNum1 * mathNum2) + mathNum3 // 16 * 4 + 19 = 83

    // Smile Mission State
    var smileSecondsRemaining by remember { mutableIntStateOf(5) }
    var isSmiling by remember { mutableStateOf(false) }

    // Shake Mission State
    var shakeCount by remember { mutableIntStateOf(0) }
    val targetShakes = 15

    // Barcode Mission State
    var isBarcodeScanned by remember { mutableStateOf(false) }

    LaunchedEffect(isSmiling) {
        if (isSmiling && smileSecondsRemaining > 0) {
            while (smileSecondsRemaining > 0) {
                delay(1000)
                smileSecondsRemaining--
            }
            isSolved = true
            ToneGenerator.playSuccessChime()
            delay(800)
            onMissionCompleted("Nụ cười 5 giây")
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(Color(0xFF1E1B4B), Color(0xFF0F172A))
                )
            ),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // Header
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.padding(top = 36.dp)
            ) {
                Surface(
                    shape = CircleShape,
                    color = ColorAlarm.copy(alpha = 0.2f),
                    modifier = Modifier.size(72.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = Icons.Default.Alarm,
                            contentDescription = null,
                            tint = ColorAlarm,
                            modifier = Modifier.size(40.dp)
                        )
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = alarmTitle,
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
                Text(
                    text = "Hoàn thành nhiệm vụ bên dưới để tắt chuông!",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.White.copy(alpha = 0.8f)
                )
            }

            // Mission Interactive Area
            Card(
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 16.dp)
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    when (currentMission) {
                        "MATH" -> {
                            Text(
                                text = "🧠 Nhiệm Vụ Giải Toán Trí Tuệ",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = IndigoPrimary
                            )
                            Text(
                                text = "Giải phép tính kích hoạt não bộ:",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Surface(
                                shape = RoundedCornerShape(12.dp),
                                color = IndigoPrimary.copy(alpha = 0.1f),
                                modifier = Modifier.padding(vertical = 8.dp)
                            ) {
                                Text(
                                    text = "$mathNum1 × $mathNum2 + $mathNum3 = ?",
                                    style = MaterialTheme.typography.headlineMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = IndigoPrimary,
                                    modifier = Modifier.padding(horizontal = 24.dp, vertical = 12.dp)
                                )
                            }
                            OutlinedTextField(
                                value = mathAnswerInput,
                                onValueChange = {
                                    mathAnswerInput = it
                                    mathError = false
                                },
                                label = { Text("Nhập kết quả của bạn") },
                                isError = mathError,
                                singleLine = true,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("math_answer_input")
                            )
                            if (mathError) {
                                Text("Chưa chính xác! Hãy tính lại nhé.", color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall)
                            }
                            Button(
                                onClick = {
                                    val ans = mathAnswerInput.trim().toIntOrNull()
                                    if (ans == expectedAnswer) {
                                        isSolved = true
                                        ToneGenerator.playSuccessChime()
                                        scope.launch {
                                            delay(600)
                                            onMissionCompleted("Giải toán")
                                        }
                                    } else {
                                        mathError = true
                                    }
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = IndigoPrimary),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("math_submit_button")
                            ) {
                                Text("Xác nhận & Tắt Chuông")
                            }
                            // Simulator helper button for emulator
                            FilledTonalButton(
                                onClick = {
                                    mathAnswerInput = expectedAnswer.toString()
                                    isSolved = true
                                    ToneGenerator.playSuccessChime()
                                    scope.launch {
                                        delay(600)
                                        onMissionCompleted("Giải toán")
                                    }
                                },
                                modifier = Modifier.testTag("math_auto_solve_button")
                            ) {
                                Text("🤖 Điền đáp án đúng (Mô phỏng)")
                            }
                        }

                        "SMILE" -> {
                            Text(
                                text = "😊 Nhiệm Vụ Nụ Cười Ban Mai",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = ColorAlarmDark
                            )
                            Text(
                                text = "Mỉm cười thật tươi liên tục trong 5 giây để đánh thức cảm xúc tích cực:",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Text(
                                text = if (isSmiling) "😄 Đang mỉm cười: $smileSecondsRemaining giây" else "🙂 Sẵn sàng mỉm cười",
                                style = MaterialTheme.typography.headlineSmall,
                                fontWeight = FontWeight.Bold,
                                color = ColorAlarm
                            )
                            LinearProgressIndicator(
                                progress = { (5 - smileSecondsRemaining) / 5f },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(10.dp)
                                    .clip(RoundedCornerShape(5.dp)),
                                color = ColorAlarm
                            )
                            Button(
                                onClick = { isSmiling = true },
                                colors = ButtonDefaults.buttonColors(containerColor = ColorAlarm),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("smile_start_button")
                            ) {
                                Icon(Icons.Default.SentimentVerySatisfied, contentDescription = null)
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("Bắt đầu mỉm cười (Giữ 5s)")
                            }
                        }

                        "BARCODE" -> {
                            Text(
                                text = "📷 Quét Mã Vạch Tuýp Kem Đánh Răng",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF10B981)
                            )
                            Text(
                                text = "Rời giường và quét mã vạch đã đăng ký trong phòng tắm:",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Card(
                                shape = RoundedCornerShape(12.dp),
                                colors = CardDefaults.cardColors(containerColor = Color.Black.copy(alpha = 0.05f)),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(110.dp)
                            ) {
                                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                                    Icon(Icons.Default.QrCodeScanner, contentDescription = null, tint = Color(0xFF10B981), modifier = Modifier.size(54.dp))
                                }
                            }
                            Button(
                                onClick = {
                                    isBarcodeScanned = true
                                    ToneGenerator.playSuccessChime()
                                    scope.launch {
                                        delay(600)
                                        onMissionCompleted("Quét mã vạch")
                                    }
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF10B981)),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("barcode_scan_success_button")
                            ) {
                                Text("Quét thành công mã vạch (Mô phỏng)")
                            }
                        }

                        "SHAKE" -> {
                            Text(
                                text = "📱 Lắc Điện Thoại Để Thức Dậy",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFFF43F5E)
                            )
                            Text(
                                text = "Lắc điện thoại $targetShakes lần để bơm máu lên não:",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Text(
                                text = "$shakeCount / $targetShakes lần",
                                style = MaterialTheme.typography.displaySmall,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFFF43F5E)
                            )
                            LinearProgressIndicator(
                                progress = { (shakeCount.toFloat() / targetShakes).coerceAtMost(1f) },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(10.dp)
                                    .clip(RoundedCornerShape(5.dp)),
                                color = Color(0xFFF43F5E)
                            )
                            Button(
                                onClick = {
                                    shakeCount += 5
                                    if (shakeCount >= targetShakes) {
                                        ToneGenerator.playSuccessChime()
                                        scope.launch {
                                            delay(600)
                                            onMissionCompleted("Lắc máy")
                                        }
                                    }
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFF43F5E)),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("shake_phone_button")
                            ) {
                                Icon(Icons.Default.Vibration, contentDescription = null)
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("Mô phỏng lắc máy (+5 lần)")
                            }
                        }

                        "LAPTOP_QR" -> {
                            Text(
                                text = "💻 Quét Mã QR Trên Màn Hình Laptop",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF38BDF8)
                            )
                            Text(
                                text = "Hãy rời giường, đến bàn làm việc và hướng camera vào mã QR đang chạy trên laptop:",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )

                            CameraQrScannerView(
                                onQrDetected = { qrCode ->
                                    val isValid = com.example.core.qr.LaptopQrHelper.validateQrPayload(qrCode, deviceSecret)
                                    if (isValid) {
                                        ToneGenerator.playSuccessChime()
                                        scope.launch {
                                            delay(500)
                                            onMissionCompleted("Quét mã QR Laptop")
                                        }
                                    } else {
                                        qrScanError = "Mã QR không khớp với thiết bị này!"
                                    }
                                },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(200.dp)
                            )

                            if (qrScanError != null) {
                                Text(
                                    text = qrScanError ?: "",
                                    color = MaterialTheme.colorScheme.error,
                                    style = MaterialTheme.typography.labelSmall
                                )
                            }

                            Text(
                                text = "Mã bí mật laptop: $deviceSecret",
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFFF59E0B)
                            )

                            // Simulator button
                            Button(
                                onClick = {
                                    ToneGenerator.playSuccessChime()
                                    scope.launch {
                                        delay(500)
                                        onMissionCompleted("Quét mã QR Laptop")
                                    }
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF38BDF8)),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("simulate_qr_success_button")
                            ) {
                                Text("🤖 Quét đúng mã QR Laptop (Mô phỏng)", color = Color.Black)
                            }

                            // Emergency Fail-Safe Fallback
                            OutlinedButton(
                                onClick = {
                                    currentMission = "MATH"
                                },
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text("⚠️ Không có laptop? Chuyển sang Giải toán khó")
                            }
                        }
                    }
                }
            }

            // Bottom Safe Exit hint
            Text(
                text = "💡 Hoàn thành nhiệm vụ giúp bạn tỉnh táo và tăng +50 XP Kỷ luật!",
                style = MaterialTheme.typography.labelSmall,
                color = Color.White.copy(alpha = 0.6f),
                modifier = Modifier.padding(bottom = 24.dp)
            )
        }
    }
}
