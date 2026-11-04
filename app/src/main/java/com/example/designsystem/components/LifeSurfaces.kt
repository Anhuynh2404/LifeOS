package com.example.designsystem.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Inbox
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.designsystem.theme.LifeTheme

enum class LifeBadgeStyle {
    BRAND,
    SUCCESS,
    WARNING,
    ERROR,
    INFO,
    NEUTRAL
}

@Composable
fun LifeBadge(
    text: String,
    modifier: Modifier = Modifier,
    style: LifeBadgeStyle = LifeBadgeStyle.BRAND,
    icon: (@Composable () -> Unit)? = null,
    testTag: String = "life_badge"
) {
    val colors = LifeTheme.colors
    val (bgColor, textColor) = when (style) {
        LifeBadgeStyle.BRAND -> colors.brandSurface to colors.brandPrimary
        LifeBadgeStyle.SUCCESS -> colors.successSurface to colors.success
        LifeBadgeStyle.WARNING -> colors.warningSurface to colors.warning
        LifeBadgeStyle.ERROR -> colors.errorSurface to colors.error
        LifeBadgeStyle.INFO -> colors.infoSurface to colors.info
        LifeBadgeStyle.NEUTRAL -> colors.surfaceHighlight to colors.textSecondary
    }

    Row(
        modifier = modifier
            .testTag(testTag)
            .clip(LifeTheme.shapes.pill)
            .background(bgColor)
            .border(0.5.dp, textColor.copy(alpha = 0.25f), LifeTheme.shapes.pill)
            .padding(horizontal = 8.dp, vertical = 3.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (icon != null) {
            icon()
            Spacer(modifier = Modifier.width(4.dp))
        }
        Text(
            text = text,
            style = LifeTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
            color = textColor
        )
    }
}

@Composable
fun LifeListItem(
    title: String,
    modifier: Modifier = Modifier,
    subtitle: String? = null,
    leadingIcon: (@Composable () -> Unit)? = null,
    trailingContent: (@Composable () -> Unit)? = null,
    onClick: (() -> Unit)? = null,
    testTag: String = "life_list_item"
) {
    val colors = LifeTheme.colors
    val shapes = LifeTheme.shapes

    val baseMod = modifier
        .testTag(testTag)
        .fillMaxWidth()
        .clip(shapes.medium)
        .background(colors.surface)
        .border(1.dp, colors.border, shapes.medium)

    val clickableMod = if (onClick != null) {
        baseMod.clickable { onClick() }
    } else {
        baseMod
    }

    Row(
        modifier = clickableMod.padding(14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (leadingIcon != null) {
            leadingIcon()
            Spacer(modifier = Modifier.width(14.dp))
        }

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                style = LifeTheme.typography.titleSmall,
                color = colors.textPrimary,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            if (!subtitle.isNullOrEmpty()) {
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = subtitle,
                    style = LifeTheme.typography.bodySmall,
                    color = colors.textSecondary,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }

        if (trailingContent != null) {
            Spacer(modifier = Modifier.width(12.dp))
            trailingContent()
        }
    }
}

@Composable
fun LifeAvatar(
    name: String,
    level: Int,
    modifier: Modifier = Modifier,
    size: Dp = 48.dp,
    testTag: String = "life_avatar"
) {
    val colors = LifeTheme.colors
    val initial = name.firstOrNull()?.uppercaseChar()?.toString() ?: "U"

    Box(
        modifier = modifier
            .testTag(testTag)
            .size(size),
        contentAlignment = Alignment.Center
    ) {
        // Outer level ring
        Box(
            modifier = Modifier
                .fillMaxSize()
                .clip(CircleShape)
                .background(colors.brandGradient)
                .padding(2.5.dp)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .clip(CircleShape)
                    .background(colors.surfaceElevated),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = initial,
                    style = LifeTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    color = colors.brandPrimary
                )
            }
        }

        // Level pill badge
        Box(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .offset(x = 4.dp, y = 4.dp)
                .clip(LifeTheme.shapes.pill)
                .background(colors.warning)
                .padding(horizontal = 4.dp, vertical = 1.dp)
        ) {
            Text(
                text = "Lv$level",
                style = LifeTheme.typography.labelSmall.copy(fontWeight = FontWeight.ExtraBold, fontSize = 9.sp),
                color = Color.Black
            )
        }
    }
}

@Composable
fun LifeEmptyState(
    title: String,
    description: String,
    modifier: Modifier = Modifier,
    icon: ImageVector = Icons.Default.Inbox,
    actionButtonText: String? = null,
    onActionClick: (() -> Unit)? = null,
    testTag: String = "life_empty_state"
) {
    val colors = LifeTheme.colors

    Column(
        modifier = modifier
            .testTag(testTag)
            .fillMaxWidth()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Box(
            modifier = Modifier
                .size(72.dp)
                .clip(CircleShape)
                .background(colors.brandSurface),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = colors.brandPrimary,
                modifier = Modifier.size(36.dp)
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = title,
            style = LifeTheme.typography.titleMedium,
            color = colors.textPrimary,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(6.dp))

        Text(
            text = description,
            style = LifeTheme.typography.bodyMedium,
            color = colors.textSecondary,
            textAlign = TextAlign.Center
        )

        if (!actionButtonText.isNullOrEmpty() && onActionClick != null) {
            Spacer(modifier = Modifier.height(20.dp))
            LifeButton(
                text = actionButtonText,
                onClick = onActionClick,
                size = LifeButtonSize.MEDIUM,
                variant = LifeButtonVariant.PRIMARY
            )
        }
    }
}

@Composable
fun LifeSkeleton(
    modifier: Modifier = Modifier,
    height: Dp = 20.dp,
    shape: androidx.compose.ui.graphics.Shape? = null
) {
    val colors = LifeTheme.colors
    val shapes = LifeTheme.shapes
    val resolvedShape = shape ?: shapes.small

    val transition = rememberInfiniteTransition(label = "skeleton_shimmer")
    val translateAnim by transition.animateFloat(
        initialValue = 0f,
        targetValue = 1000f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1200, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "shimmer_anim"
    )

    val baseColor = colors.surfaceHighlight
    val highlightColor = colors.borderLight

    val brush = Brush.linearGradient(
        colors = listOf(baseColor, highlightColor, baseColor),
        start = Offset(translateAnim - 200f, translateAnim - 200f),
        end = Offset(translateAnim, translateAnim)
    )

    Box(
        modifier = modifier
            .height(height)
            .clip(resolvedShape)
            .background(brush)
    )
}
