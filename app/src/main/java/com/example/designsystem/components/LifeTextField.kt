package com.example.designsystem.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsFocusedAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.designsystem.theme.LifeTheme
import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.util.Locale

@Composable
fun LifeTextField(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    label: String? = null,
    placeholder: String? = null,
    leadingIcon: (@Composable () -> Unit)? = null,
    trailingIcon: (@Composable () -> Unit)? = null,
    isError: Boolean = false,
    errorMessage: String? = null,
    singleLine: Boolean = true,
    maxLines: Int = 1,
    enabled: Boolean = true,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    keyboardActions: KeyboardActions = KeyboardActions.Default,
    visualTransformation: VisualTransformation = VisualTransformation.None,
    testTag: String = "life_text_field"
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isFocused by interactionSource.collectIsFocusedAsState()
    val colors = LifeTheme.colors
    val shapes = LifeTheme.shapes

    val borderColor by animateColorAsState(
        targetValue = when {
            isError -> colors.error
            isFocused -> colors.brandPrimary
            else -> colors.border
        },
        label = "field_border"
    )

    val bgColor = if (isFocused) colors.surface else colors.surfaceHighlight

    Column(modifier = modifier) {
        if (!label.isNullOrEmpty()) {
            Text(
                text = label,
                style = LifeTheme.typography.labelMedium.copy(fontWeight = FontWeight.SemiBold),
                color = if (isError) colors.error else if (isFocused) colors.brandPrimary else colors.textSecondary,
                modifier = Modifier.padding(bottom = 6.dp)
            )
        }

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .defaultMinSize(minHeight = 52.dp)
                .clip(shapes.medium)
                .background(bgColor)
                .border(
                    width = if (isFocused || isError) 1.5.dp else 1.dp,
                    color = borderColor,
                    shape = shapes.medium
                )
                .padding(horizontal = 16.dp, vertical = 12.dp),
            contentAlignment = Alignment.CenterStart
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                if (leadingIcon != null) {
                    leadingIcon()
                    Spacer(modifier = Modifier.width(12.dp))
                }

                Box(modifier = Modifier.weight(1f)) {
                    if (value.isEmpty() && placeholder != null) {
                        Text(
                            text = placeholder,
                            style = LifeTheme.typography.bodyMedium,
                            color = colors.textTertiary
                        )
                    }

                    BasicTextField(
                        value = value,
                        onValueChange = onValueChange,
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag(testTag),
                        enabled = enabled,
                        singleLine = singleLine,
                        maxLines = maxLines,
                        textStyle = LifeTheme.typography.bodyLarge.copy(color = colors.textPrimary),
                        cursorBrush = SolidColor(colors.brandPrimary),
                        keyboardOptions = keyboardOptions,
                        keyboardActions = keyboardActions,
                        interactionSource = interactionSource,
                        visualTransformation = visualTransformation
                    )
                }

                if (value.isNotEmpty() && enabled && trailingIcon == null) {
                    IconButton(
                        onClick = { onValueChange("") },
                        modifier = Modifier.size(28.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Clear,
                            contentDescription = "Xóa",
                            tint = colors.textTertiary,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                } else if (trailingIcon != null) {
                    Spacer(modifier = Modifier.width(8.dp))
                    trailingIcon()
                }
            }
        }

        AnimatedVisibility(visible = isError && !errorMessage.isNullOrEmpty()) {
            Text(
                text = errorMessage ?: "",
                style = LifeTheme.typography.bodySmall,
                color = colors.error,
                modifier = Modifier.padding(top = 4.dp, start = 4.dp)
            )
        }
    }
}

@Composable
fun LifeMoneyTextField(
    rawAmount: Long,
    onAmountChange: (Long) -> Unit,
    modifier: Modifier = Modifier,
    label: String = "Số tiền (VND)",
    placeholder: String = "0",
    isError: Boolean = false,
    errorMessage: String? = null,
    testTag: String = "life_money_field"
) {
    val formatter = remember {
        val symbols = DecimalFormatSymbols(Locale.GERMANY).apply {
            groupingSeparator = '.'
        }
        DecimalFormat("#,###", symbols)
    }

    val displayString = if (rawAmount > 0) formatter.format(rawAmount) else ""

    LifeTextField(
        value = displayString,
        onValueChange = { input ->
            val digitsOnly = input.filter { it.isDigit() }
            val parsed = digitsOnly.toLongOrNull() ?: 0L
            onAmountChange(parsed)
        },
        modifier = modifier,
        label = label,
        placeholder = placeholder,
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
        trailingIcon = {
            Text(
                text = "đ",
                style = LifeTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                color = LifeTheme.colors.brandPrimary
            )
        },
        isError = isError,
        errorMessage = errorMessage,
        testTag = testTag
    )
}
