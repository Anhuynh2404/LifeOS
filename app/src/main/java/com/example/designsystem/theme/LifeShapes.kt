package com.example.designsystem.theme

import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.dp

@Immutable
data class LifeShapes(
    val extraSmall: Shape = RoundedCornerShape(8.dp),
    val small: Shape = RoundedCornerShape(12.dp),
    val medium: Shape = RoundedCornerShape(16.dp),
    val large: Shape = RoundedCornerShape(20.dp),
    val extraLarge: Shape = RoundedCornerShape(24.dp),
    val sheet: Shape = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp),
    val dialog: Shape = RoundedCornerShape(28.dp),
    val pill: Shape = RoundedCornerShape(999.dp),
    val circle: Shape = CircleShape
)

val LocalLifeShapes = staticCompositionLocalOf { LifeShapes() }
