package com.example.designsystem.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.designsystem.theme.LifeTheme

@Composable
fun LifeDialog(
    onDismissRequest: () -> Unit,
    title: String,
    modifier: Modifier = Modifier,
    icon: (@Composable () -> Unit)? = null,
    confirmButtonText: String = "Xác nhận",
    onConfirm: () -> Unit,
    dismissButtonText: String? = "Hủy",
    onDismiss: (() -> Unit)? = null,
    destructive: Boolean = false,
    testTag: String = "life_dialog",
    content: @Composable ColumnScope.() -> Unit
) {
    val colors = LifeTheme.colors
    val shapes = LifeTheme.shapes

    Dialog(
        onDismissRequest = onDismissRequest,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp)
                .clip(shapes.dialog)
                .background(colors.surface)
                .border(1.dp, colors.border, shapes.dialog)
                .testTag(testTag)
                .padding(24.dp)
        ) {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                if (icon != null) {
                    Box(
                        modifier = Modifier
                            .size(52.dp)
                            .clip(CircleShape)
                            .background(if (destructive) colors.errorSurface else colors.brandSurface),
                        contentAlignment = Alignment.Center
                    ) {
                        icon()
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                }

                Text(
                    text = title,
                    style = LifeTheme.typography.titleMedium,
                    color = colors.textPrimary,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(16.dp))

                content()

                Spacer(modifier = Modifier.height(24.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    if (dismissButtonText != null && onDismiss != null) {
                        LifeButton(
                            text = dismissButtonText,
                            onClick = onDismiss,
                            modifier = Modifier.weight(1f),
                            variant = LifeButtonVariant.SECONDARY,
                            size = LifeButtonSize.MEDIUM
                        )
                    }

                    LifeButton(
                        text = confirmButtonText,
                        onClick = onConfirm,
                        modifier = Modifier.weight(1f),
                        variant = if (destructive) LifeButtonVariant.DESTRUCTIVE else LifeButtonVariant.PRIMARY,
                        size = LifeButtonSize.MEDIUM
                    )
                }
            }
        }
    }
}

@Composable
fun LifeBottomSheetHeader(
    title: String,
    modifier: Modifier = Modifier,
    subtitle: String? = null
) {
    val colors = LifeTheme.colors

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(top = 12.dp, bottom = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Drag Handle
        Box(
            modifier = Modifier
                .width(40.dp)
                .height(4.dp)
                .clip(LifeTheme.shapes.pill)
                .background(colors.border)
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = title,
            style = LifeTheme.typography.titleMedium,
            color = colors.textPrimary,
            textAlign = TextAlign.Center
        )

        if (!subtitle.isNullOrEmpty()) {
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = subtitle,
                style = LifeTheme.typography.bodySmall,
                color = colors.textSecondary,
                textAlign = TextAlign.Center
            )
        }
    }
}
