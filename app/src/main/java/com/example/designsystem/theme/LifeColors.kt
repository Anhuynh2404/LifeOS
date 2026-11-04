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
    brandPrimary = Color(0xFF4F46E5),
    brandDark = Color(0xFF3730A3),
    brandLight = Color(0xFF818CF8),
    brandSurface = Color(0xFFEEF2FF),

    alarmStart = Color(0xFFFFA000),
    alarmEnd = Color(0xFFFF5722),
    financeStart = Color(0xFF059669),
    financeEnd = Color(0xFF10B981),
    scheduleStart = Color(0xFF2563EB),
    scheduleEnd = Color(0xFF38BDF8),
    goalsStart = Color(0xFF7C3AED),
    goalsEnd = Color(0xFFA855F7),
    challengesStart = Color(0xFFE11D48),
    challengesEnd = Color(0xFFFB7185),

    success = Color(0xFF10B981),
    successSurface = Color(0xFFECFDF5),
    warning = Color(0xFFF59E0B),
    warningSurface = Color(0xFFFFFBEB),
    error = Color(0xFFEF4444),
    errorSurface = Color(0xFFFEF2F2),
    info = Color(0xFF0284C7),
    infoSurface = Color(0xFFF0F9FF),

    background = Color(0xFFFFFBF2),
    surface = Color(0xFFFFFFFF),
    surfaceElevated = Color(0xFFFFFFFF),
    surfaceHighlight = Color(0xFFF7F3E8),
    textPrimary = Color(0xFF1E1B18),
    textSecondary = Color(0xFF57534E),
    textTertiary = Color(0xFF8C867F),
    border = Color(0xFFDFD3C0),
    borderLight = Color(0xFFE8DECF),
    isDark = false
)

val DarkLifeColors = LifeColors(
    brandPrimary = Color(0xFF6366F1),
    brandDark = Color(0xFF4338CA),
    brandLight = Color(0xFFA5B4FC),
    brandSurface = Color(0xFF1E2142),

    alarmStart = Color(0xFFFFA000),
    alarmEnd = Color(0xFFFF5722),
    financeStart = Color(0xFF10B981),
    financeEnd = Color(0xFF34D399),
    scheduleStart = Color(0xFF3B82F6),
    scheduleEnd = Color(0xFF60A5FA),
    goalsStart = Color(0xFF8B5CF6),
    goalsEnd = Color(0xFFC084FC),
    challengesStart = Color(0xFFF43F5E),
    challengesEnd = Color(0xFFFB7185),

    success = Color(0xFF22C55E),
    successSurface = Color(0xFF133E2B),
    warning = Color(0xFFF59E0B),
    warningSurface = Color(0xFF3D2C10),
    error = Color(0xFFEF4444),
    errorSurface = Color(0xFF3D1A1F),
    info = Color(0xFF38BDF8),
    infoSurface = Color(0xFF13374D),

    background = Color(0xFF0B0F19),
    surface = Color(0xFF131B2E),
    surfaceElevated = Color(0xFF1A243D),
    surfaceHighlight = Color(0xFF223052),
    textPrimary = Color(0xFFF8FAFC),
    textSecondary = Color(0xFF94A3B8),
    textTertiary = Color(0xFF64748B),
    border = Color(0xFF1E293B),
    borderLight = Color(0xFF1A243D),
    isDark = true
)

val LocalLifeColors = staticCompositionLocalOf { LightLifeColors }
