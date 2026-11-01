package com.example.designsystem.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.designsystem.theme.LifeTheme

enum class LifeCardVariant {
    SURFACE,
    ELEVATED,
    GRADIENT,
    GLASS
}

@Composable
fun LifeCard(
    modifier: Modifier = Modifier,
    variant: LifeCardVariant = LifeCardVariant.SURFACE,
    shape: Shape? = null,
    gradient: Brush? = null,
    onClick: (() -> Unit)? = null,
    testTag: String = "life_card",
    content: @Composable ColumnScope.() -> Unit
) {
    val colors = LifeTheme.colors
    val shapes = LifeTheme.shapes
    val resolvedShape = shape ?: shapes.large

    val baseModifier = modifier
        .testTag(testTag)
        .clip(resolvedShape)

    val styledModifier = when (variant) {
        LifeCardVariant.SURFACE -> {
            baseModifier
                .background(colors.surface)
                .border(1.dp, colors.border, resolvedShape)
        }
        LifeCardVariant.ELEVATED -> {
            Modifier
                .shadow(elevation = 6.dp, shape = resolvedShape, spotColor = Color.Black.copy(alpha = 0.08f))
                .then(baseModifier)
                .background(colors.surfaceElevated)
                .border(1.dp, colors.borderLight, resolvedShape)
        }
        LifeCardVariant.GRADIENT -> {
            val brush = gradient ?: colors.brandGradient
            baseModifier.background(brush)
        }
        LifeCardVariant.GLASS -> {
            baseModifier
                .background(colors.surface.copy(alpha = if (colors.isDark) 0.65f else 0.85f))
                .border(1.dp, colors.border.copy(alpha = 0.5f), resolvedShape)
        }
    }

    val finalModifier = if (onClick != null) {
        styledModifier.clickable { onClick() }
    } else {
        styledModifier
    }

    Column(
        modifier = finalModifier.padding(16.dp),
        content = content
    )
}

@Composable
fun LifeStatCard(
    title: String,
    value: String,
    modifier: Modifier = Modifier,
    subtitle: String? = null,
    icon: (@Composable () -> Unit)? = null,
    badgeText: String? = null,
    badgeColor: Color? = null,
    variant: LifeCardVariant = LifeCardVariant.SURFACE,
    gradient: Brush? = null,
    onClick: (() -> Unit)? = null,
    testTag: String = "life_stat_card"
) {
    val colors = LifeTheme.colors

    LifeCard(
        modifier = modifier,
        variant = variant,
        gradient = gradient,
        onClick = onClick,
        testTag = testTag
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = title,
                style = LifeTheme.typography.labelMedium,
                color = if (variant == LifeCardVariant.GRADIENT) Color.White.copy(alpha = 0.8f) else colors.textSecondary
            )
            if (icon != null) {
                icon()
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = value,
            style = LifeTheme.typography.tabularNumber,
            color = if (variant == LifeCardVariant.GRADIENT) Color.White else colors.textPrimary
        )

        if (subtitle != null || badgeText != null) {
            Spacer(modifier = Modifier.height(4.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                if (badgeText != null) {
                    val bg = badgeColor ?: colors.success
                    Box(
                        modifier = Modifier
                            .clip(LifeTheme.shapes.pill)
                            .background(bg.copy(alpha = 0.15f))
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = badgeText,
                            style = LifeTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                            color = bg
                        )
                    }
                    Spacer(modifier = Modifier.width(6.dp))
                }
                if (subtitle != null) {
                    Text(
                        text = subtitle,
                        style = LifeTheme.typography.bodySmall,
                        color = if (variant == LifeCardVariant.GRADIENT) Color.White.copy(alpha = 0.8f) else colors.textSecondary
                    )
                }
            }
        }
    }
}
