package com.example.designsystem.theme

import androidx.compose.runtime.Immutable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color

@Immutable
data class LifeColors(
    // Brand
    val brandPrimary: Color,
    val brandDark: Color,
    val brandLight: Color,
    val brandSurface: Color,

    // Subsystems
    val alarmStart: Color,
    val alarmEnd: Color,
    val financeStart: Color,
    val financeEnd: Color,
    val scheduleStart: Color,
    val scheduleEnd: Color,
    val goalsStart: Color,
    val goalsEnd: Color,
    val challengesStart: Color,
    val challengesEnd: Color,

    // Semantics
    val success: Color,
    val successSurface: Color,
    val warning: Color,
    val warningSurface: Color,
    val error: Color,
    val errorSurface: Color,
    val info: Color,
    val infoSurface: Color,

    // Background & Surfaces
    val background: Color,
    val surface: Color,
    val surfaceElevated: Color,
    val surfaceHighlight: Color,
    val textPrimary: Color,
    val textSecondary: Color,
    val textTertiary: Color,
    val border: Color,
    val borderLight: Color,
    val isDark: Boolean
) {
    val brandGradient: Brush get() = Brush.horizontalGradient(listOf(brandPrimary, brandLight))
    val alarmGradient: Brush get() = Brush.linearGradient(listOf(alarmStart, alarmEnd))
    val financeGradient: Brush get() = Brush.linearGradient(listOf(financeStart, financeEnd))
    val scheduleGradient: Brush get() = Brush.linearGradient(listOf(scheduleStart, scheduleEnd))
    val goalsGradient: Brush get() = Brush.linearGradient(listOf(goalsStart, goalsEnd))
    val challengesGradient: Brush get() = Brush.linearGradient(listOf(challengesStart, challengesEnd))
}

val LightLifeColors = LifeColors(
    brandPrimary = Color(0xFF5B5BD6),
    brandDark = Color(0xFF4338CA),
    brandLight = Color(0xFF818CF8),
    brandSurface = Color(0xFFEEF2FF),

    alarmStart = Color(0xFFFFB86B),
    alarmEnd = Color(0xFFFF7E5F),
    financeStart = Color(0xFF10B981),
    financeEnd = Color(0xFF34D399),
    scheduleStart = Color(0xFF3B82F6),
    scheduleEnd = Color(0xFF60A5FA),
    goalsStart = Color(0xFF8B5CF6),
    goalsEnd = Color(0xFFC4B5FD),
    challengesStart = Color(0xFFF43F5E),
    challengesEnd = Color(0xFFFB7185),

    success = Color(0xFF10B981),
    successSurface = Color(0xFFECFDF5),
    warning = Color(0xFFF59E0B),
    warningSurface = Color(0xFFFFFBEB),
    error = Color(0xFFEF4444),
    errorSurface = Color(0xFFFEF2F2),
    info = Color(0xFF38BDF8),
    infoSurface = Color(0xFFF0F9FF),

    background = Color(0xFFF6F7FB),
    surface = Color(0xFFFFFFFF),
    surfaceElevated = Color(0xFFFFFFFF),
    surfaceHighlight = Color(0xFFF1F3F9),
    textPrimary = Color(0xFF1B1B2F),
    textSecondary = Color(0xFF6B7090),
    textTertiary = Color(0xFF9E9EAC),
    border = Color(0xFFE4E6F0),
    borderLight = Color(0xFFF0F2F8),
    isDark = false
)

val DarkLifeColors = LifeColors(
    brandPrimary = Color(0xFF6366F1),
    brandDark = Color(0xFF4338CA),
    brandLight = Color(0xFFA5B4FC),
    brandSurface = Color(0xFF23254A),

    alarmStart = Color(0xFFF59E0B),
    alarmEnd = Color(0xFFEF4444),
    financeStart = Color(0xFF10B981),
    financeEnd = Color(0xFF059669),
    scheduleStart = Color(0xFF3B82F6),
    scheduleEnd = Color(0xFF2563EB),
    goalsStart = Color(0xFF8B5CF6),
    goalsEnd = Color(0xFF7C3AED),
    challengesStart = Color(0xFFF43F5E),
    challengesEnd = Color(0xFFBE123C),

    success = Color(0xFF22C55E),
    successSurface = Color(0xFF0F3926),
    warning = Color(0xFFF59E0B),
    warningSurface = Color(0xFF38270B),
    error = Color(0xFFEF4444),
    errorSurface = Color(0xFF381519),
    info = Color(0xFF38BDF8),
    infoSurface = Color(0xFF0E3247),

    background = Color(0xFF0F1020),
    surface = Color(0xFF17182B),
    surfaceElevated = Color(0xFF1F2138),
    surfaceHighlight = Color(0xFF292C48),
    textPrimary = Color(0xFFECEDF7),
    textSecondary = Color(0xFF9A9CC0),
    textTertiary = Color(0xFF656784),
    border = Color(0xFF2A2C48),
    borderLight = Color(0xFF20223A),
    isDark = true
)

val LocalLifeColors = staticCompositionLocalOf { LightLifeColors }
