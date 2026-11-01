package com.example.designsystem.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Icon
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.designsystem.theme.LifeTheme

@Composable
fun LifeCheckbox(
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
    label: String? = null,
    enabled: Boolean = true,
    activeColor: Color? = null,
    testTag: String = "life_checkbox"
) {
    val haptic = LocalHapticFeedback.current
    val colors = LifeTheme.colors
    val resolvedColor = activeColor ?: colors.brandPrimary

    val bgAnim by animateColorAsState(
        targetValue = if (checked) resolvedColor else Color.Transparent,
        label = "cb_bg"
    )
    val borderAnim by animateColorAsState(
        targetValue = if (checked) resolvedColor else colors.border,
        label = "cb_border"
    )
    val scaleAnim by animateFloatAsState(
        targetValue = if (checked) 1f else 0.8f,
        animationSpec = spring(dampingRatio = 0.5f, stiffness = 500f),
        label = "cb_scale"
    )

    Row(
        modifier = modifier
            .testTag(testTag)
            .defaultMinSize(minHeight = 48.dp)
            .clickable(enabled = enabled) {
                haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                onCheckedChange(!checked)
            }
            .padding(vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(24.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(bgAnim)
                .border(2.dp, borderAnim, RoundedCornerShape(8.dp)),
            contentAlignment = Alignment.Center
        ) {
            if (checked) {
                Icon(
                    imageVector = Icons.Default.Check,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier
                        .size(16.dp)
                        .scale(scaleAnim)
                )
            }
        }

        if (!label.isNullOrEmpty()) {
            Spacer(modifier = Modifier.width(12.dp))
            Text(
                text = label,
                style = LifeTheme.typography.bodyMedium.copy(
                    textDecoration = if (checked) TextDecoration.LineThrough else TextDecoration.None
                ),
                color = if (checked) colors.textTertiary else colors.textPrimary
            )
        }
    }
}

@Composable
fun LifeSwitch(
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    activeColor: Color? = null,
    testTag: String = "life_switch"
) {
    val haptic = LocalHapticFeedback.current
    val colors = LifeTheme.colors
    val targetColor = activeColor ?: colors.brandPrimary

    val trackColor by animateColorAsState(
        targetValue = if (checked) targetColor else colors.border,
        label = "switch_track"
    )

    val thumbOffset by animateDpAsState(
        targetValue = if (checked) 24.dp else 2.dp,
        animationSpec = spring(dampingRatio = 0.65f, stiffness = 450f),
        label = "switch_thumb"
    )

    Box(
        modifier = modifier
            .testTag(testTag)
            .defaultMinSize(minHeight = 48.dp, minWidth = 48.dp)
            .clickable(
                enabled = enabled,
                indication = null,
                interactionSource = remember { MutableInteractionSource() }
            ) {
                haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                onCheckedChange(!checked)
            },
        contentAlignment = Alignment.Center
    ) {
        Box(
            modifier = Modifier
                .width(48.dp)
                .height(26.dp)
                .clip(RoundedCornerShape(13.dp))
                .background(trackColor)
                .padding(horizontal = 2.dp),
            contentAlignment = Alignment.CenterStart
        ) {
            Box(
                modifier = Modifier
                    .offset(x = thumbOffset)
                    .size(22.dp)
                    .clip(CircleShape)
                    .background(Color.White)
            )
        }
    }
}

@Composable
fun LifeRadio(
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    label: String? = null,
    enabled: Boolean = true,
    testTag: String = "life_radio"
) {
    val haptic = LocalHapticFeedback.current
    val colors = LifeTheme.colors

    val borderColor by animateColorAsState(
        targetValue = if (selected) colors.brandPrimary else colors.border,
        label = "radio_border"
    )

    val dotScale by animateFloatAsState(
        targetValue = if (selected) 1f else 0f,
        animationSpec = spring(dampingRatio = 0.6f, stiffness = 500f),
        label = "radio_dot"
    )

    Row(
        modifier = modifier
            .testTag(testTag)
            .defaultMinSize(minHeight = 48.dp)
            .clickable(enabled = enabled) {
                haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                onClick()
            }
            .padding(vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(22.dp)
                .clip(CircleShape)
                .border(2.dp, borderColor, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            if (dotScale > 0.05f) {
                Box(
                    modifier = Modifier
                        .size(12.dp)
                        .scale(dotScale)
                        .clip(CircleShape)
                        .background(colors.brandPrimary)
                )
            }
        }

        if (!label.isNullOrEmpty()) {
            Spacer(modifier = Modifier.width(12.dp))
            Text(
                text = label,
                style = LifeTheme.typography.bodyMedium,
                color = colors.textPrimary
            )
        }
    }
}

@Composable
fun LifeChip(
    text: String,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    icon: (@Composable () -> Unit)? = null,
    badgeText: String? = null,
    testTag: String = "life_chip"
) {
    val haptic = LocalHapticFeedback.current
    val colors = LifeTheme.colors
    val shapes = LifeTheme.shapes

    val bgModifier = if (selected) {
        Modifier.background(colors.brandPrimary)
    } else {
        Modifier
            .background(colors.surfaceHighlight)
            .border(1.dp, colors.borderLight, shapes.pill)
    }

    Row(
        modifier = modifier
            .testTag(testTag)
            .height(38.dp)
            .clip(shapes.pill)
            .then(bgModifier)
            .clickable {
                haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                onClick()
            }
            .padding(horizontal = 14.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    ) {
        if (icon != null) {
            icon()
            Spacer(modifier = Modifier.width(6.dp))
        }
        Text(
            text = text,
            style = LifeTheme.typography.labelMedium.copy(fontWeight = if (selected) FontWeight.Bold else FontWeight.Medium),
            color = if (selected) Color.White else colors.textPrimary
        )
        if (badgeText != null) {
            Spacer(modifier = Modifier.width(6.dp))
            Box(
                modifier = Modifier
                    .clip(CircleShape)
                    .background(if (selected) Color.White.copy(alpha = 0.25f) else colors.brandSurface)
                    .padding(horizontal = 6.dp, vertical = 2.dp)
            ) {
                Text(
                    text = badgeText,
                    style = LifeTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                    color = if (selected) Color.White else colors.brandPrimary
                )
            }
        }
    }
}

@Composable
fun LifeSegmentedControl(
    items: List<String>,
    selectedIndex: Int,
    onItemSelected: (Int) -> Unit,
    modifier: Modifier = Modifier,
    testTag: String = "life_segmented_control"
) {
    val colors = LifeTheme.colors
    val shapes = LifeTheme.shapes
    val haptic = LocalHapticFeedback.current

    Box(
        modifier = modifier
            .testTag(testTag)
            .fillMaxWidth()
            .height(44.dp)
            .clip(shapes.medium)
            .background(colors.surfaceHighlight)
            .border(1.dp, colors.borderLight, shapes.medium)
            .padding(4.dp)
    ) {
        Row(modifier = Modifier.fillMaxSize()) {
            items.forEachIndexed { index, title ->
                val isSelected = index == selectedIndex
                val bg = if (isSelected) colors.surface else Color.Transparent
                val borderModifier = if (isSelected) Modifier.border(1.dp, colors.borderLight, shapes.small) else Modifier

                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight()
                        .clip(shapes.small)
                        .background(bg)
                        .then(borderModifier)
                        .clickable {
                            haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                            onItemSelected(index)
                        },
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = title,
                        style = LifeTheme.typography.labelMedium.copy(
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                        ),
                        color = if (isSelected) colors.brandPrimary else colors.textSecondary
                    )
                }
            }
        }
    }
}

@Composable
fun LifeSlider(
    value: Float,
    onValueChange: (Float) -> Unit,
    modifier: Modifier = Modifier,
    valueRange: ClosedFloatingPointRange<Float> = 0f..1f,
    steps: Int = 0,
    activeColor: Color? = null,
    testTag: String = "life_slider"
) {
    val colors = LifeTheme.colors
    val color = activeColor ?: colors.brandPrimary

    Slider(
        value = value,
        onValueChange = onValueChange,
        valueRange = valueRange,
        steps = steps,
        modifier = modifier.testTag(testTag),
        colors = SliderDefaults.colors(
            thumbColor = color,
            activeTrackColor = color,
            inactiveTrackColor = colors.border
        )
    )
}

@Composable
fun LifeTimePicker(
    hour: Int,
    minute: Int,
    onTimeChange: (hour: Int, minute: Int) -> Unit,
    modifier: Modifier = Modifier,
    is24Hour: Boolean = true,
    testTag: String = "life_time_picker"
) {
    val colors = LifeTheme.colors
    val shapes = LifeTheme.shapes
    val haptic = LocalHapticFeedback.current

    var is24hMode by remember { mutableStateOf(is24Hour) }
    var isPm by remember(hour) { mutableStateOf(hour >= 12) }

    // Direct text editing state
    var hourString by remember(hour) { mutableStateOf(String.format("%02d", if (is24hMode) hour else (if (hour % 12 == 0) 12 else hour % 12))) }
    var minuteString by remember(minute) { mutableStateOf(String.format("%02d", minute)) }

    Column(
        modifier = modifier
            .testTag(testTag)
            .fillMaxWidth()
            .clip(shapes.large)
            .background(colors.surfaceHighlight)
            .border(1.dp, colors.borderLight, shapes.large)
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Mode toggle 12h / 24h
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Thời gian đã chọn: ${String.format("%02d:%02d", hour, minute)}",
                style = LifeTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                color = colors.textPrimary
            )
            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                LifeChip(
                    text = "24h",
                    selected = is24hMode,
                    onClick = { is24hMode = true }
                )
                LifeChip(
                    text = "12h (AM/PM)",
                    selected = !is24hMode,
                    onClick = { is24hMode = false }
                )
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Main Digits & Steppers Row
        Row(
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            // Hour Column
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                // Increment Hour
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .clip(CircleShape)
                        .background(colors.surface)
                        .border(1.dp, colors.border, CircleShape)
                        .clickable {
                            haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                            val nextHour = (hour + 1) % 24
                            onTimeChange(nextHour, minute)
                        },
                    contentAlignment = Alignment.Center
                ) {
                    Text("+", style = LifeTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = colors.textPrimary)
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Hour Display Box
                Box(
                    modifier = Modifier
                        .widthIn(min = 72.dp)
                        .heightIn(min = 60.dp)
                        .clip(shapes.medium)
                        .background(colors.surface)
                        .border(1.5.dp, colors.brandPrimary.copy(alpha = 0.6f), shapes.medium)
                        .padding(horizontal = 8.dp, vertical = 6.dp),
                    contentAlignment = Alignment.Center
                ) {
                    androidx.compose.foundation.text.BasicTextField(
                        value = hourString,
                        onValueChange = { input ->
                            val clean = input.filter { it.isDigit() }.take(2)
                            hourString = clean
                            val num = clean.toIntOrNull()
                            if (num != null) {
                                val resolvedHour = if (is24hMode) {
                                    num.coerceIn(0, 23)
                                } else {
                                    val clamped12 = num.coerceIn(1, 12)
                                    if (isPm) {
                                        if (clamped12 == 12) 12 else clamped12 + 12
                                    } else {
                                        if (clamped12 == 12) 0 else clamped12
                                    }
                                }
                                onTimeChange(resolvedHour, minute)
                            }
                        },
                        keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(
                            keyboardType = androidx.compose.ui.text.input.KeyboardType.Number
                        ),
                        singleLine = true,
                        textStyle = LifeTheme.typography.tabularNumberLarge.copy(
                            fontSize = 32.sp,
                            fontWeight = FontWeight.Bold,
                            color = colors.brandPrimary,
                            textAlign = androidx.compose.ui.text.style.TextAlign.Center
                        ),
                        modifier = Modifier.width(64.dp)
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Decrement Hour
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .clip(CircleShape)
                        .background(colors.surface)
                        .border(1.dp, colors.border, CircleShape)
                        .clickable {
                            haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                            val prevHour = if (hour <= 0) 23 else hour - 1
                            onTimeChange(prevHour, minute)
                        },
                    contentAlignment = Alignment.Center
                ) {
                    Text("-", style = LifeTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = colors.textPrimary)
                }
            }

            // Colon separator
            Text(
                text = ":",
                style = LifeTheme.typography.tabularNumberLarge.copy(fontSize = 36.sp, fontWeight = FontWeight.Bold),
                color = colors.brandPrimary,
                modifier = Modifier.padding(horizontal = 14.dp)
            )

            // Minute Column
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                // Increment Minute
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .clip(CircleShape)
                        .background(colors.surface)
                        .border(1.dp, colors.border, CircleShape)
                        .clickable {
                            haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                            val nextMin = (minute + 5) % 60
                            onTimeChange(hour, nextMin)
                        },
                    contentAlignment = Alignment.Center
                ) {
                    Text("+5", style = LifeTheme.typography.labelMedium, fontWeight = FontWeight.Bold, color = colors.textPrimary)
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Minute Display Box
                Box(
                    modifier = Modifier
                        .widthIn(min = 72.dp)
                        .heightIn(min = 60.dp)
                        .clip(shapes.medium)
                        .background(colors.surface)
                        .border(1.5.dp, colors.brandPrimary.copy(alpha = 0.6f), shapes.medium)
                        .padding(horizontal = 8.dp, vertical = 6.dp),
                    contentAlignment = Alignment.Center
                ) {
                    androidx.compose.foundation.text.BasicTextField(
                        value = minuteString,
                        onValueChange = { input ->
                            val clean = input.filter { it.isDigit() }.take(2)
                            minuteString = clean
                            val num = clean.toIntOrNull()
                            if (num != null) {
                                onTimeChange(hour, num.coerceIn(0, 59))
                            }
                        },
                        keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(
                            keyboardType = androidx.compose.ui.text.input.KeyboardType.Number
                        ),
                        singleLine = true,
                        textStyle = LifeTheme.typography.tabularNumberLarge.copy(
                            fontSize = 32.sp,
                            fontWeight = FontWeight.Bold,
                            color = colors.brandPrimary,
                            textAlign = androidx.compose.ui.text.style.TextAlign.Center
                        ),
                        modifier = Modifier.width(64.dp)
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Decrement Minute
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .clip(CircleShape)
                        .background(colors.surface)
                        .border(1.dp, colors.border, CircleShape)
                        .clickable {
                            haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                            val prevMin = if (minute < 5) 55 else minute - 5
                            onTimeChange(hour, prevMin)
                        },
                    contentAlignment = Alignment.Center
                ) {
                    Text("-5", style = LifeTheme.typography.labelMedium, fontWeight = FontWeight.Bold, color = colors.textPrimary)
                }
            }

            // 12-Hour AM/PM Selector
            if (!is24hMode) {
                Spacer(modifier = Modifier.width(14.dp))
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    LifeChip(
                        text = "AM (Sáng)",
                        selected = !isPm,
                        onClick = {
                            isPm = false
                            if (hour >= 12) onTimeChange(hour - 12, minute)
                        }
                    )
                    LifeChip(
                        text = "PM (Tối)",
                        selected = isPm,
                        onClick = {
                            isPm = true
                            if (hour < 12) onTimeChange(hour + 12, minute)
                        }
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Quick Preset Chips (Morning, Noon, Evening, Bedtime)
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            listOf(
                "06:00" to (6 to 0),
                "06:30" to (6 to 30),
                "07:00" to (7 to 0),
                "22:30" to (22 to 30),
                "23:00" to (23 to 0)
            ).forEach { (label, pair) ->
                LifeChip(
                    text = label,
                    selected = hour == pair.first && minute == pair.second,
                    onClick = {
                        onTimeChange(pair.first, pair.second)
                    }
                )
            }
        }
    }
}

