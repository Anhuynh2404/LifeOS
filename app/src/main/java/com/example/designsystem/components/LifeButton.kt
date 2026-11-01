package com.example.designsystem.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.*
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.designsystem.theme.LifeTheme

enum class LifeButtonVariant {
    PRIMARY,
    SECONDARY,
    OUTLINE,
    TEXT,
    DESTRUCTIVE
}

enum class LifeButtonSize(val minHeight: Dp, val horizontalPadding: Dp) {
    LARGE(52.dp, 24.dp),
    MEDIUM(44.dp, 18.dp),
    SMALL(36.dp, 12.dp)
}

@Composable
fun LifeButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    variant: LifeButtonVariant = LifeButtonVariant.PRIMARY,
    size: LifeButtonSize = LifeButtonSize.LARGE,
    enabled: Boolean = true,
    loading: Boolean = false,
    customGradient: Brush? = null,
    leadingIcon: (@Composable () -> Unit)? = null,
    trailingIcon: (@Composable () -> Unit)? = null,
    testTag: String = "life_button"
) {
    val haptic = LocalHapticFeedback.current
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()

    val scale by animateFloatAsState(
        targetValue = if (isPressed && enabled && !loading) 0.97f else 1f,
        animationSpec = spring(dampingRatio = 0.6f, stiffness = 400f),
        label = "btn_press_scale"
    )

    val colors = LifeTheme.colors
    val shapes = LifeTheme.shapes

    val shape = when (size) {
        LifeButtonSize.LARGE -> shapes.medium
        LifeButtonSize.MEDIUM -> shapes.small
        LifeButtonSize.SMALL -> shapes.extraSmall
    }

    val (bgBrush, bgColor, contentColor, borderColor) = when (variant) {
        LifeButtonVariant.PRIMARY -> {
            val brush = customGradient ?: colors.brandGradient
            Quad(brush, Color.Unspecified, Color.White, Color.Transparent)
        }
        LifeButtonVariant.SECONDARY -> {
            Quad(null, colors.surfaceHighlight, colors.textPrimary, colors.borderLight)
        }
        LifeButtonVariant.OUTLINE -> {
            Quad(null, Color.Transparent, colors.brandPrimary, colors.border)
        }
        LifeButtonVariant.TEXT -> {
            Quad(null, Color.Transparent, colors.brandPrimary, Color.Transparent)
        }
        LifeButtonVariant.DESTRUCTIVE -> {
            Quad(null, colors.errorSurface, colors.error, colors.error.copy(alpha = 0.3f))
        }
    }

    val finalAlpha = if (enabled && !loading) 1f else 0.5f

    val baseModifier = modifier
        .testTag(testTag)
        .scale(scale)
        .defaultMinSize(minHeight = size.minHeight)
        .clip(shape)

    val styledModifier = when {
        bgBrush != null && enabled -> baseModifier.background(bgBrush)
        bgColor != Color.Unspecified -> baseModifier.background(bgColor.copy(alpha = if (enabled) 1f else 0.6f))
        else -> baseModifier
    }.then(
        if (borderColor != Color.Transparent) Modifier.border(1.dp, borderColor, shape) else Modifier
    ).then(
        if (variant == LifeButtonVariant.PRIMARY && enabled && !loading) {
            Modifier.shadow(elevation = 6.dp, shape = shape, spotColor = colors.brandPrimary.copy(alpha = 0.4f))
        } else Modifier
    ).clickable(
        interactionSource = interactionSource,
        indication = null,
        enabled = enabled && !loading
    ) {
        haptic.performHapticFeedback(HapticFeedbackType.LongPress)
        onClick()
    }

    Box(
        modifier = styledModifier.padding(horizontal = size.horizontalPadding, vertical = 8.dp),
        contentAlignment = Alignment.Center
    ) {
        if (loading) {
            CircularProgressIndicator(
                modifier = Modifier.size(20.dp),
                color = contentColor,
                strokeWidth = 2.dp
            )
        } else {
            Row(
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (leadingIcon != null) {
                    leadingIcon()
                    Spacer(modifier = Modifier.width(8.dp))
                }
                Text(
                    text = text,
                    style = when (size) {
                        LifeButtonSize.LARGE -> LifeTheme.typography.titleSmall
                        LifeButtonSize.MEDIUM -> LifeTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold)
                        LifeButtonSize.SMALL -> LifeTheme.typography.labelMedium.copy(fontWeight = FontWeight.SemiBold)
                    },
                    color = contentColor.copy(alpha = finalAlpha)
                )
                if (trailingIcon != null) {
                    Spacer(modifier = Modifier.width(8.dp))
                    trailingIcon()
                }
            }
        }
    }
}

private data class Quad<A, B, C, D>(val first: A, val second: B, val third: C, val fourth: D)
