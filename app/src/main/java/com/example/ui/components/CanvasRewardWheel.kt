package com.example.ui.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.EaseOutCubic
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.*
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.entity.RewardEntity
import kotlinx.coroutines.launch
import kotlin.math.cos
import kotlin.math.sin

@OptIn(ExperimentalTextApi::class)
@Composable
fun CanvasRewardWheel(
    rewards: List<RewardEntity>,
    isSpinning: Boolean,
    onSpinFinished: (RewardEntity) -> Unit,
    modifier: Modifier = Modifier
) {
    val rotationAnim = remember { Animatable(0f) }
    val scope = rememberCoroutineScope()
    val textMeasurer = rememberTextMeasurer()

    val wedgeAngle = if (rewards.isNotEmpty()) 360f / rewards.size else 60f

    LaunchedEffect(isSpinning) {
        if (isSpinning && rewards.isNotEmpty()) {
            val fullRotations = (5..8).random() * 360f
            val targetIndex = rewards.indices.random()
            val targetAngle = fullRotations + (360f - (targetIndex * wedgeAngle + wedgeAngle / 2f))

            rotationAnim.animateTo(
                targetValue = targetAngle,
                animationSpec = tween(durationMillis = 4000, easing = EaseOutCubic)
            )
            onSpinFinished(rewards[targetIndex])
        }
    }

    Box(
        modifier = modifier.size(300.dp),
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val center = Offset(size.width / 2, size.height / 2)
            val radius = size.minDimension / 2 - 16.dp.toPx()

            // Draw outer metallic rim
            drawCircle(
                color = Color(0xFFFFD166),
                radius = radius + 10.dp.toPx(),
                center = center,
                style = Stroke(width = 8.dp.toPx())
            )

            // Draw wedges
            rewards.forEachIndexed { index, reward ->
                val startAngle = rotationAnim.value + index * wedgeAngle
                val color = try {
                    Color(android.graphics.Color.parseColor(reward.colorHex))
                } catch (_: Exception) {
                    if (index % 2 == 0) Color(0xFF6366F1) else Color(0xFF10B981)
                }

                drawArc(
                    color = color,
                    startAngle = startAngle,
                    sweepAngle = wedgeAngle,
                    useCenter = true,
                    topLeft = Offset(center.x - radius, center.y - radius),
                    size = Size(radius * 2, radius * 2)
                )

                // Divider line
                val rad = Math.toRadians(startAngle.toDouble())
                val endX = center.x + radius * cos(rad).toFloat()
                val endY = center.y + radius * sin(rad).toFloat()
                drawLine(
                    color = Color.White.copy(alpha = 0.5f),
                    start = center,
                    end = Offset(endX, endY),
                    strokeWidth = 2.dp.toPx()
                )

                // Short text inside wedge
                val midAngleRad = Math.toRadians((startAngle + wedgeAngle / 2f).toDouble())
                val textDistance = radius * 0.65f
                val textX = center.x + textDistance * cos(midAngleRad).toFloat()
                val textY = center.y + textDistance * sin(midAngleRad).toFloat()

                val shortTitle = if (reward.title.length > 12) reward.title.take(10) + ".." else reward.title
                val layout = textMeasurer.measure(
                    text = AnnotatedString(shortTitle),
                    style = TextStyle(color = Color.White, fontSize = 10.sp, fontWeight = androidx.compose.ui.text.font.FontWeight.Bold)
                )
                drawText(
                    textMeasurer = textMeasurer,
                    text = shortTitle,
                    style = TextStyle(color = Color.White, fontSize = 10.sp, fontWeight = androidx.compose.ui.text.font.FontWeight.Bold),
                    topLeft = Offset(textX - layout.size.width / 2, textY - layout.size.height / 2)
                )
            }

            // Center hub
            drawCircle(
                color = Color.White,
                radius = 28.dp.toPx(),
                center = center
            )
            drawCircle(
                color = Color(0xFF4F46E5),
                radius = 22.dp.toPx(),
                center = center
            )

            // Top indicator pointer arrow pointing downward at top rim
            val pointerPath = Path().apply {
                moveTo(center.x - 14.dp.toPx(), 4.dp.toPx())
                lineTo(center.x + 14.dp.toPx(), 4.dp.toPx())
                lineTo(center.x, 28.dp.toPx())
                close()
            }
            drawPath(path = pointerPath, color = Color(0xFFE11D48))
        }
    }
}
