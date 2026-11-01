package com.example.designsystem.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.ui.graphics.Color

object LifeTheme {
    val colors: LifeColors
        @Composable
        @ReadOnlyComposable
        get() = LocalLifeColors.current

    val typography: LifeTypography
        @Composable
        @ReadOnlyComposable
        get() = LocalLifeTypography.current

    val spacing: LifeSpacing
        @Composable
        @ReadOnlyComposable
        get() = LocalLifeSpacing.current

    val shapes: LifeShapes
        @Composable
        @ReadOnlyComposable
        get() = LocalLifeShapes.current
}

@Composable
fun LifeOSTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val lifeColors = if (darkTheme) DarkLifeColors else LightLifeColors
    val lifeTypography = LifeTypography()
    val lifeSpacing = LifeSpacing()
    val lifeShapes = LifeShapes()

    val m3ColorScheme = if (darkTheme) {
        darkColorScheme(
            primary = lifeColors.brandPrimary,
            onPrimary = Color.White,
            primaryContainer = lifeColors.brandDark,
            onPrimaryContainer = lifeColors.brandLight,
            secondary = lifeColors.scheduleStart,
            background = lifeColors.background,
            onBackground = lifeColors.textPrimary,
            surface = lifeColors.surface,
            onSurface = lifeColors.textPrimary,
            surfaceVariant = lifeColors.surfaceElevated,
            onSurfaceVariant = lifeColors.textSecondary,
            outline = lifeColors.border
        )
    } else {
        lightColorScheme(
            primary = lifeColors.brandPrimary,
            onPrimary = Color.White,
            primaryContainer = lifeColors.brandSurface,
            onPrimaryContainer = lifeColors.brandPrimary,
            secondary = lifeColors.scheduleStart,
            background = lifeColors.background,
            onBackground = lifeColors.textPrimary,
            surface = lifeColors.surface,
            onSurface = lifeColors.textPrimary,
            surfaceVariant = lifeColors.surfaceHighlight,
            onSurfaceVariant = lifeColors.textSecondary,
            outline = lifeColors.border
        )
    }

    CompositionLocalProvider(
        LocalLifeColors provides lifeColors,
        LocalLifeTypography provides lifeTypography,
        LocalLifeSpacing provides lifeSpacing,
        LocalLifeShapes provides lifeShapes
    ) {
        MaterialTheme(
            colorScheme = m3ColorScheme,
            content = content
        )
    }
}
