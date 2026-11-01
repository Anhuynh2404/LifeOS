package com.example.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.*
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.entity.SkillNodeEntity
import com.example.ui.theme.*

@OptIn(ExperimentalTextApi::class)
@Composable
fun CanvasSkillTree(
    skills: List<SkillNodeEntity>,
    onSkillClick: (SkillNodeEntity) -> Unit,
    modifier: Modifier = Modifier
) {
    var panOffsetX by remember { mutableFloatStateOf(0f) }
    var panOffsetY by remember { mutableFloatStateOf(0f) }
    val textMeasurer = rememberTextMeasurer()

    Box(
        modifier = modifier
            .fillMaxSize()
            .pointerInput(Unit) {
                detectDragGestures { change, dragAmount ->
                    change.consume()
                    panOffsetX += dragAmount.x
                    panOffsetY += dragAmount.y
                }
            }
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val width = size.width
            val height = size.height
            val centerX = width / 2 + panOffsetX
            val centerY = height / 2 + panOffsetY

            // Draw subtle cosmic grid background
            for (i in -4..4) {
                val lineX = centerX + i * 140f
                drawLine(
                    color = Color.White.copy(alpha = 0.05f),
                    start = Offset(lineX, 0f),
                    end = Offset(lineX, height),
                    strokeWidth = 1f
                )
                val lineY = centerY + i * 140f
                drawLine(
                    color = Color.White.copy(alpha = 0.05f),
                    start = Offset(0f, lineY),
                    end = Offset(width, lineY),
                    strokeWidth = 1f
                )
            }

            // Central root nexus: "Cội Nguồn LifeOS"
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(IndigoPrimaryLight, IndigoPrimary, Color.Transparent),
                    center = Offset(centerX, centerY),
                    radius = 55.dp.toPx()
                ),
                radius = 35.dp.toPx(),
                center = Offset(centerX, centerY)
            )
            drawCircle(
                color = Color.White,
                radius = 12.dp.toPx(),
                center = Offset(centerX, centerY)
            )

            // Draw connecting branch lines
            skills.forEach { skill ->
                val nodeX = centerX + (skill.canvasX - 0.5f) * width * 1.5f
                val nodeY = centerY + (skill.canvasY - 0.5f) * height * 1.5f

                val branchColor = when (skill.branchName) {
                    "Sức khỏe" -> ColorAlarm
                    "Trí tuệ" -> ColorGoals
                    "Tài chính" -> ColorFinance
                    "Tinh thần" -> ColorChallenges
                    else -> ColorSchedule
                }

                // Branch line from center to node
                drawLine(
                    brush = Brush.linearGradient(
                        colors = listOf(IndigoPrimary, branchColor)
                    ),
                    start = Offset(centerX, centerY),
                    end = Offset(nodeX, nodeY),
                    strokeWidth = 3.dp.toPx()
                )

                // Glow ring
                drawCircle(
                    color = branchColor.copy(alpha = 0.25f),
                    radius = 36.dp.toPx(),
                    center = Offset(nodeX, nodeY)
                )

                // Node circle
                drawCircle(
                    brush = Brush.radialGradient(
                        colors = listOf(branchColor.copy(alpha = 0.9f), branchColor),
                        center = Offset(nodeX, nodeY),
                        radius = 28.dp.toPx()
                    ),
                    radius = 24.dp.toPx(),
                    center = Offset(nodeX, nodeY)
                )

                // Level badge border
                drawCircle(
                    color = Color.White,
                    radius = 24.dp.toPx(),
                    center = Offset(nodeX, nodeY),
                    style = Stroke(width = 2.dp.toPx())
                )

                // Level text
                val levelText = "Lv.${skill.level}"
                val textLayoutResult = textMeasurer.measure(
                    text = AnnotatedString(levelText),
                    style = TextStyle(color = Color.White, fontSize = 11.sp, fontWeight = androidx.compose.ui.text.font.FontWeight.Bold)
                )
                drawText(
                    textMeasurer = textMeasurer,
                    text = levelText,
                    style = TextStyle(color = Color.White, fontSize = 11.sp, fontWeight = androidx.compose.ui.text.font.FontWeight.Bold),
                    topLeft = Offset(nodeX - textLayoutResult.size.width / 2, nodeY - textLayoutResult.size.height / 2)
                )

                // Title label below node
                val titleLayoutResult = textMeasurer.measure(
                    text = AnnotatedString(skill.title),
                    style = TextStyle(color = Color.White, fontSize = 11.sp, fontWeight = androidx.compose.ui.text.font.FontWeight.Medium)
                )
                drawText(
                    textMeasurer = textMeasurer,
                    text = skill.title,
                    style = TextStyle(color = Color.White, fontSize = 11.sp, fontWeight = androidx.compose.ui.text.font.FontWeight.Medium),
                    topLeft = Offset(nodeX - titleLayoutResult.size.width / 2, nodeY + 28.dp.toPx())
                )
            }
        }

        // Tap targets overlay for nodes
        skills.forEach { skill ->
            val branchColor = when (skill.branchName) {
                "Sức khỏe" -> ColorAlarm
                "Trí tuệ" -> ColorGoals
                "Tài chính" -> ColorFinance
                "Tinh thần" -> ColorChallenges
                else -> ColorSchedule
            }

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .wrapContentSize(Alignment.Center)
                    .offset(
                        x = ((skill.canvasX - 0.5f) * 360f + panOffsetX / 3).dp,
                        y = ((skill.canvasY - 0.5f) * 450f + panOffsetY / 3).dp
                    )
            ) {
                IconButton(
                    onClick = { onSkillClick(skill) },
                    modifier = Modifier.size(54.dp)
                ) {
                    Surface(
                        shape = CircleShape,
                        color = Color.Transparent,
                        modifier = Modifier.fillMaxSize()
                    ) {}
                }
            }
        }
    }
}
