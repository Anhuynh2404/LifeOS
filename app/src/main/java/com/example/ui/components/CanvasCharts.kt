package com.example.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.ColorSchedule
import kotlin.math.cos
import kotlin.math.sin

data class PieSliceData(
    val label: String,
    val value: Float,
    val color: Color
)

@Composable
fun CanvasPieChart(
    slices: List<PieSliceData>,
    modifier: Modifier = Modifier,
    holeRatio: Float = 0.55f
) {
    val total = slices.sumOf { it.value.toDouble() }.toFloat().coerceAtLeast(1f)

    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceAround
    ) {
        Canvas(modifier = Modifier.size(160.dp)) {
            val strokeWidth = size.minDimension * (1f - holeRatio) / 2
            val radius = (size.minDimension - strokeWidth) / 2
            val center = Offset(size.width / 2, size.height / 2)

            var startAngle = -90f
            for (slice in slices) {
                val sweepAngle = (slice.value / total) * 360f
                drawArc(
                    color = slice.color,
                    startAngle = startAngle,
                    sweepAngle = sweepAngle - 2f, // subtle gap
                    useCenter = false,
                    style = Stroke(width = strokeWidth, cap = StrokeCap.Round),
                    topLeft = Offset(center.x - radius, center.y - radius),
                    size = Size(radius * 2, radius * 2)
                )
                startAngle += sweepAngle
            }
        }

        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
            slices.forEach { slice ->
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Canvas(modifier = Modifier.size(10.dp)) {
                        drawCircle(color = slice.color)
                    }
                    Spacer(modifier = Modifier.width(6.dp))
                    val pct = ((slice.value / total) * 100).toInt()
                    Text(
                        text = "${slice.label} ($pct%)",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
            }
        }
    }
}

@Composable
fun CanvasBarChart(
    labels: List<String>,
    values: List<Float>,
    barColor: Color = ColorSchedule,
    modifier: Modifier = Modifier
) {
    val maxVal = (values.maxOrNull() ?: 100f).coerceAtLeast(10f)

    Column(modifier = modifier) {
        Canvas(
            modifier = Modifier
                .fillMaxWidth()
                .height(130.dp)
        ) {
            val barCount = values.size
            if (barCount == 0) return@Canvas
            val spacing = size.width / (barCount * 2)
            val barWidth = spacing

            // Draw horizontal subtle grid lines
            for (i in 1..3) {
                val y = size.height * (1f - i * 0.25f)
                drawLine(
                    color = Color.LightGray.copy(alpha = 0.3f),
                    start = Offset(0f, y),
                    end = Offset(size.width, y),
                    strokeWidth = 1.dp.toPx()
                )
            }

            values.forEachIndexed { index, value ->
                val x = spacing + index * (barWidth + spacing)
                val barHeight = (value / maxVal) * (size.height * 0.85f)
                val y = size.height - barHeight

                drawRoundRect(
                    brush = Brush.verticalGradient(
                        colors = listOf(barColor.copy(alpha = 0.9f), barColor)
                    ),
                    topLeft = Offset(x, y),
                    size = Size(barWidth, barHeight),
                    cornerRadius = androidx.compose.ui.geometry.CornerRadius(6.dp.toPx())
                )
            }
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            labels.forEach { label ->
                Text(
                    text = label,
                    style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
fun CanvasLineChart(
    labels: List<String>,
    values: List<Float>,
    lineColor: Color = Color(0xFF10B981),
    modifier: Modifier = Modifier
) {
    val maxVal = (values.maxOrNull() ?: 100f).coerceAtLeast(10f)
    val minVal = (values.minOrNull() ?: 0f).coerceAtMost(maxVal - 1f)

    Column(modifier = modifier) {
        Canvas(
            modifier = Modifier
                .fillMaxWidth()
                .height(120.dp)
        ) {
            if (values.size < 2) return@Canvas
            val stepX = size.width / (values.size - 1)
            val range = (maxVal - minVal).coerceAtLeast(1f)

            val points = values.mapIndexed { index, v ->
                val x = index * stepX
                val normalized = (v - minVal) / range
                val y = size.height - (normalized * (size.height * 0.75f) + size.height * 0.1f)
                Offset(x, y)
            }

            val path = Path().apply {
                moveTo(points.first().x, points.first().y)
                for (i in 1 until points.size) {
                    val p0 = points[i - 1]
                    val p1 = points[i]
                    val cx = (p0.x + p1.x) / 2
                    cubicTo(cx, p0.y, cx, p1.y, p1.x, p1.y)
                }
            }

            val fillPath = Path().apply {
                addPath(path)
                lineTo(size.width, size.height)
                lineTo(0f, size.height)
                close()
            }

            drawPath(
                path = fillPath,
                brush = Brush.verticalGradient(
                    colors = listOf(lineColor.copy(alpha = 0.35f), Color.Transparent)
                )
            )

            drawPath(
                path = path,
                color = lineColor,
                style = Stroke(width = 3.dp.toPx(), cap = StrokeCap.Round)
            )

            // Draw point dots
            points.forEach { pt ->
                drawCircle(color = Color.White, radius = 5.dp.toPx(), center = pt)
                drawCircle(color = lineColor, radius = 3.dp.toPx(), center = pt)
            }
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            labels.forEach { label ->
                Text(
                    text = label,
                    style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}
