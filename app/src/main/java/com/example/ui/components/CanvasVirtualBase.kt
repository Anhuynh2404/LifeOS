package com.example.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.entity.VirtualBaseItemEntity
import com.example.ui.theme.GoldCoin
import com.example.ui.theme.IndigoPrimary

@Composable
fun CanvasVirtualBase(
    userLevel: Int,
    equippedItems: List<VirtualBaseItemEntity>,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(200.dp),
        contentAlignment = Alignment.BottomCenter
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val width = size.width
            val height = size.height

            // 1. Sky & Atmosphere gradient
            drawRect(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFF0F172A),
                        Color(0xFF1E1B4B),
                        Color(0xFF312E81)
                    )
                )
            )

            // 2. Stars / floating inspiration dust
            val starPositions = listOf(
                Offset(width * 0.15f, height * 0.18f),
                Offset(width * 0.42f, height * 0.12f),
                Offset(width * 0.78f, height * 0.22f),
                Offset(width * 0.88f, height * 0.15f),
                Offset(width * 0.62f, height * 0.32f)
            )
            starPositions.forEach { pos ->
                drawCircle(color = Color.White.copy(alpha = 0.8f), radius = 2.dp.toPx(), center = pos)
            }

            // 3. Rolling Hill terrain (Base ground)
            val hillPath = Path().apply {
                moveTo(0f, height * 0.65f)
                cubicTo(
                    width * 0.35f, height * 0.58f,
                    width * 0.65f, height * 0.72f,
                    width, height * 0.62f
                )
                lineTo(width, height)
                lineTo(0f, height)
                close()
            }
            drawPath(
                path = hillPath,
                brush = Brush.verticalGradient(
                    colors = listOf(Color(0xFF047857), Color(0xFF064E3B), Color(0xFF022C22))
                )
            )

            // 4. Main Headquarters building (grows with level)
            val buildingCenterX = width * 0.5f
            val buildingBaseY = height * 0.76f
            val buildingWidth = 80.dp.toPx() + (userLevel.coerceAtMost(5) * 6.dp.toPx())
            val buildingHeight = 70.dp.toPx() + (userLevel.coerceAtMost(5) * 8.dp.toPx())

            // Building body
            drawRoundRect(
                brush = Brush.verticalGradient(
                    colors = listOf(Color(0xFF6366F1), Color(0xFF4338CA), Color(0xFF312E81))
                ),
                topLeft = Offset(buildingCenterX - buildingWidth / 2, buildingBaseY - buildingHeight),
                size = Size(buildingWidth, buildingHeight),
                cornerRadius = CornerRadius(8.dp.toPx(), 8.dp.toPx())
            )

            // Roof spire
            val roofPath = Path().apply {
                moveTo(buildingCenterX - buildingWidth / 2 - 4.dp.toPx(), buildingBaseY - buildingHeight)
                lineTo(buildingCenterX + buildingWidth / 2 + 4.dp.toPx(), buildingBaseY - buildingHeight)
                lineTo(buildingCenterX, buildingBaseY - buildingHeight - 24.dp.toPx())
                close()
            }
            drawPath(path = roofPath, color = Color(0xFF818CF8))

            // Glowing Beacon / Vitality crystal on top of HQ
            drawCircle(
                color = Color(0xFFFBBF24),
                radius = 7.dp.toPx(),
                center = Offset(buildingCenterX, buildingBaseY - buildingHeight - 26.dp.toPx())
            )

            // Windows
            val winSize = 10.dp.toPx()
            for (row in 0..2) {
                for (col in -1..1) {
                    val wx = buildingCenterX + col * 18.dp.toPx() - winSize / 2
                    val wy = buildingBaseY - buildingHeight + 14.dp.toPx() + row * 16.dp.toPx()
                    drawRoundRect(
                        color = Color(0xFFFDE047),
                        topLeft = Offset(wx, wy),
                        size = Size(winSize, winSize),
                        cornerRadius = CornerRadius(2.dp.toPx(), 2.dp.toPx())
                    )
                }
            }

            // 5. Render Equipped decorations
            val hasTree = equippedItems.any { it.name.contains("Cây", ignoreCase = true) }
            val hasDeskOrLibrary = equippedItems.any { it.name.contains("Bàn", ignoreCase = true) || it.name.contains("Sách", ignoreCase = true) }
            val hasZenGarden = equippedItems.any { it.name.contains("Vườn", ignoreCase = true) }

            if (hasTree) {
                // Wisdom tree on left hill
                val treeX = width * 0.22f
                val treeY = height * 0.75f
                // Trunk
                drawRect(
                    color = Color(0xFF78350F),
                    topLeft = Offset(treeX - 4.dp.toPx(), treeY - 26.dp.toPx()),
                    size = Size(8.dp.toPx(), 26.dp.toPx())
                )
                // Foliage canopy
                drawCircle(color = Color(0xFF10B981), radius = 18.dp.toPx(), center = Offset(treeX, treeY - 32.dp.toPx()))
                drawCircle(color = Color(0xFF34D399), radius = 12.dp.toPx(), center = Offset(treeX - 6.dp.toPx(), treeY - 36.dp.toPx()))
            }

            if (hasDeskOrLibrary) {
                // Study pavilion on right hill
                val pavX = width * 0.78f
                val pavY = height * 0.78f
                drawRoundRect(
                    color = Color(0xFFD97706),
                    topLeft = Offset(pavX - 18.dp.toPx(), pavY - 22.dp.toPx()),
                    size = Size(36.dp.toPx(), 22.dp.toPx()),
                    cornerRadius = CornerRadius(4.dp.toPx(), 4.dp.toPx())
                )
                // Warm lamp glow
                drawCircle(color = Color(0xFFFEF08A), radius = 4.dp.toPx(), center = Offset(pavX, pavY - 14.dp.toPx()))
            }

            if (hasZenGarden) {
                // Zen bridge and stones
                val zenX = width * 0.36f
                val zenY = height * 0.88f
                drawCircle(color = Color(0xFF94A3B8), radius = 6.dp.toPx(), center = Offset(zenX, zenY))
                drawCircle(color = Color(0xFFCBD5E1), radius = 8.dp.toPx(), center = Offset(zenX + 14.dp.toPx(), zenY))
            }
        }

        // Overlay status badge
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                color = Color.Black.copy(alpha = 0.55f),
                shape = MaterialTheme.shapes.small
            ) {
                Text(
                    text = "🏰 Căn Cứ LifeOS • Cấp $userLevel",
                    color = Color.White,
                    style = MaterialTheme.typography.labelMedium,
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                )
            }
            Surface(
                color = GoldCoin.copy(alpha = 0.25f),
                shape = MaterialTheme.shapes.small
            ) {
                Text(
                    text = "✨ Đã trang bị ${equippedItems.size} công trình",
                    color = GoldCoin,
                    style = MaterialTheme.typography.labelSmall,
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                )
            }
        }
    }
}
