package com.example.designsystem.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.entity.FootballCardEntity

enum class CardRarityTier {
    BRONZE, SILVER, GOLD, TOTW, HERO, ICON;

    companion object {
        fun fromString(rarity: String): CardRarityTier {
            return when (rarity.uppercase()) {
                "BRONZE" -> BRONZE
                "SILVER" -> SILVER
                "GOLD", "GOLD_COMMON", "GOLD_RARE" -> GOLD
                "TOTW", "WALKOUT" -> TOTW
                "HERO" -> HERO
                "ICON" -> ICON
                else -> GOLD
            }
        }
    }
}

/**
 * Premium FUT-style Player Card with 5:7 Aspect Ratio, 6 Rarity Tiers,
 * Holo/Shimmer effects, and 3D Card Flip to view bio & career stats.
 */
@Composable
fun PlayerCard(
    card: FootballCardEntity,
    modifier: Modifier = Modifier,
    canUnlock: Boolean = false,
    onUnlock: (() -> Unit)? = null,
    enableFlip: Boolean = true,
    compact: Boolean = false
) {
    var isFlipped by remember { mutableStateOf(false) }
    val rotation by animateFloatAsState(
        targetValue = if (isFlipped) 180f else 0f,
        animationSpec = tween(durationMillis = 500, easing = FastOutSlowInEasing),
        label = "card_flip_rotation"
    )

    val tier = remember(card.rarity) { CardRarityTier.fromString(card.rarity) }

    // Shimmer effect for TOTW, HERO, and ICON
    val infiniteTransition = rememberInfiniteTransition(label = "holo_shimmer")
    val shimmerOffset by infiniteTransition.animateFloat(
        initialValue = -200f,
        targetValue = 600f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 2400, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "shimmer_offset"
    )

    // Palette & Visual styling per tier
    val backgroundBrush = when (tier) {
        CardRarityTier.BRONZE -> Brush.verticalGradient(
            listOf(Color(0xFF5A2A18), Color(0xFF78350F), Color(0xFF92400E), Color(0xFF451A03))
        )
        CardRarityTier.SILVER -> Brush.verticalGradient(
            listOf(Color(0xFF475569), Color(0xFF64748B), Color(0xFF94A3B8), Color(0xFF334155))
        )
        CardRarityTier.GOLD -> Brush.verticalGradient(
            listOf(Color(0xFFFFFBEB), Color(0xFFFEF08A), Color(0xFFF59E0B), Color(0xFF78350F))
        )
        CardRarityTier.TOTW -> Brush.verticalGradient(
            listOf(Color(0xFF0F172A), Color(0xFF1E1B4B), Color(0xFF312E81), Color(0xFF0284C7))
        )
        CardRarityTier.HERO -> Brush.verticalGradient(
            listOf(Color(0xFF4C1D95), Color(0xFF7C3AED), Color(0xFFC084FC), Color(0xFFE11D48))
        )
        CardRarityTier.ICON -> Brush.verticalGradient(
            listOf(Color(0xFFFFFDF5), Color(0xFFFEF3C7), Color(0xFFFDE68A), Color(0xFFD97706), Color(0xFF78350F))
        )
    }

    val primaryTextColor = when (tier) {
        CardRarityTier.BRONZE -> Color(0xFFFEF3C7)
        CardRarityTier.SILVER -> Color(0xFFF8FAFC)
        CardRarityTier.GOLD -> Color(0xFF451A03)
        CardRarityTier.TOTW -> Color(0xFFFDE047)
        CardRarityTier.HERO -> Color(0xFFFFFFFF)
        CardRarityTier.ICON -> Color(0xFF78350F)
    }

    val secondaryTextColor = when (tier) {
        CardRarityTier.BRONZE -> Color(0xFFFDE68A)
        CardRarityTier.SILVER -> Color(0xFFE2E8F0)
        CardRarityTier.GOLD -> Color(0xFF78350F)
        CardRarityTier.TOTW -> Color(0xFFBAE6FD)
        CardRarityTier.HERO -> Color(0xFFFBCFE8)
        CardRarityTier.ICON -> Color(0xFF92400E)
    }

    val borderColor = when (tier) {
        CardRarityTier.BRONZE -> Color(0xFFD97706)
        CardRarityTier.SILVER -> Color(0xFFCBD5E1)
        CardRarityTier.GOLD -> Color(0xFFF59E0B)
        CardRarityTier.TOTW -> Color(0xFF38BDF8)
        CardRarityTier.HERO -> Color(0xFFF43F5E)
        CardRarityTier.ICON -> Color(0xFFF59E0B)
    }

    val cornerRadius = if (compact) 12.dp else 16.dp

    Box(
        modifier = modifier
            .aspectRatio(5f / 7f)
            .graphicsLayer {
                rotationY = rotation
                cameraDistance = 14f * density
            }
            .clip(RoundedCornerShape(cornerRadius))
            .shadow(if (tier == CardRarityTier.ICON || tier == CardRarityTier.HERO) 10.dp else 4.dp, RoundedCornerShape(cornerRadius))
            .border(if (tier == CardRarityTier.ICON || tier == CardRarityTier.HERO) 2.5.dp else 1.8.dp, borderColor, RoundedCornerShape(cornerRadius))
            .background(backgroundBrush)
            .clickable(enabled = enableFlip) { isFlipped = !isFlipped }
    ) {
        if (rotation <= 90f) {
            // FRONT OF CARD
            CardFrontContent(
                card = card,
                tier = tier,
                primaryTextColor = primaryTextColor,
                secondaryTextColor = secondaryTextColor,
                canUnlock = canUnlock,
                onUnlock = onUnlock,
                compact = compact
            )
        } else {
            // BACK OF CARD (Flipped)
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .graphicsLayer { rotationY = 180f }
            ) {
                CardBackContent(
                    card = card,
                    tier = tier,
                    primaryTextColor = primaryTextColor,
                    secondaryTextColor = secondaryTextColor,
                    compact = compact,
                    onFlipBack = { isFlipped = false }
                )
            }
        }
    }
}

@Composable
private fun CardFrontContent(
    card: FootballCardEntity,
    tier: CardRarityTier,
    primaryTextColor: Color,
    secondaryTextColor: Color,
    canUnlock: Boolean,
    onUnlock: (() -> Unit)?,
    compact: Boolean
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = if (compact) 6.dp else 10.dp, vertical = if (compact) 6.dp else 8.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        // TOP BAR: OVR, Position, Nation, Club Badge
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.Top
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = "${card.ovr}",
                    fontWeight = FontWeight.Black,
                    fontSize = if (compact) 18.sp else 22.sp,
                    color = primaryTextColor,
                    lineHeight = if (compact) 18.sp else 22.sp
                )
                Text(
                    text = card.position,
                    fontWeight = FontWeight.Bold,
                    fontSize = if (compact) 10.sp else 12.sp,
                    color = secondaryTextColor
                )
                Spacer(modifier = Modifier.height(2.dp))
                // Nationality label
                Surface(
                    shape = RoundedCornerShape(3.dp),
                    color = Color.Black.copy(alpha = 0.35f)
                ) {
                    Text(
                        text = card.nationality.take(3).uppercase(),
                        fontSize = if (compact) 6.sp else 8.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        modifier = Modifier.padding(horizontal = 3.dp, vertical = 1.dp)
                    )
                }
            }

            Column(horizontalAlignment = Alignment.End) {
                // Tier Badge
                Surface(
                    shape = RoundedCornerShape(4.dp),
                    color = when (tier) {
                        CardRarityTier.ICON -> Color(0xFFD97706)
                        CardRarityTier.HERO -> Color(0xFF7C3AED)
                        CardRarityTier.TOTW -> Color(0xFF0284C7)
                        CardRarityTier.GOLD -> Color(0xFFB45309)
                        CardRarityTier.SILVER -> Color(0xFF475569)
                        CardRarityTier.BRONZE -> Color(0xFF78350F)
                    }
                ) {
                    Text(
                        text = tier.name,
                        fontSize = if (compact) 6.sp else 8.sp,
                        fontWeight = FontWeight.Black,
                        color = Color.White,
                        modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                    )
                }
                Spacer(modifier = Modifier.height(3.dp))
                // Club shield pill
                Surface(
                    shape = RoundedCornerShape(4.dp),
                    color = Color.Black.copy(alpha = 0.4f)
                ) {
                    Text(
                        text = card.clubName.take(8).uppercase(),
                        fontSize = if (compact) 6.sp else 7.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.padding(horizontal = 3.dp, vertical = 1.dp)
                    )
                }
            }
        }

        // CENTER: Player Avatar & Halo Icon
        Box(
            modifier = Modifier
                .size(if (compact) 52.dp else 68.dp)
                .clip(CircleShape)
                .background(Color.White.copy(alpha = 0.2f))
                .border(1.5.dp, Color.White.copy(alpha = 0.4f), CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = when (tier) {
                    CardRarityTier.ICON -> Icons.Default.MilitaryTech
                    CardRarityTier.HERO -> Icons.Default.Shield
                    CardRarityTier.TOTW -> Icons.Default.ElectricBolt
                    else -> Icons.Default.SportsSoccer
                },
                contentDescription = null,
                tint = if (tier == CardRarityTier.TOTW) Color(0xFFFDE047) else primaryTextColor,
                modifier = Modifier.size(if (compact) 32.dp else 42.dp)
            )
        }

        // PLAYER NAME BANNER
        Surface(
            shape = RoundedCornerShape(4.dp),
            color = when (tier) {
                CardRarityTier.TOTW -> Color(0xFF0F172A).copy(alpha = 0.9f)
                CardRarityTier.HERO -> Color(0xFF3B0764).copy(alpha = 0.9f)
                CardRarityTier.ICON -> Color(0xFF451A03).copy(alpha = 0.9f)
                else -> Color.Black.copy(alpha = 0.45f)
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = card.playerName.uppercase(),
                color = if (tier == CardRarityTier.TOTW) Color(0xFFFDE047) else Color.White,
                fontWeight = FontWeight.Black,
                fontSize = if (compact) 9.sp else 11.sp,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(vertical = 2.dp, horizontal = 4.dp)
            )
        }

        // STATS SECTION (PAC, SHO, PAS, DRI, DEF, PHY)
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceAround
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                StatItem(label = "PAC", value = card.pace, primaryColor = primaryTextColor, secondaryColor = secondaryTextColor, compact = compact)
                StatItem(label = "SHO", value = card.shooting, primaryColor = primaryTextColor, secondaryColor = secondaryTextColor, compact = compact)
            }
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                StatItem(label = "PAS", value = card.passing, primaryColor = primaryTextColor, secondaryColor = secondaryTextColor, compact = compact)
                StatItem(label = "DRI", value = card.dribbling, primaryColor = primaryTextColor, secondaryColor = secondaryTextColor, compact = compact)
            }
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                StatItem(label = "DEF", value = card.defending, primaryColor = primaryTextColor, secondaryColor = secondaryTextColor, compact = compact)
                StatItem(label = "PHY", value = card.physical, primaryColor = primaryTextColor, secondaryColor = secondaryTextColor, compact = compact)
            }
        }

        // BOTTOM STATUS OR UNLOCK ACTION
        if (card.isOwned) {
            Surface(
                shape = RoundedCornerShape(4.dp),
                color = Color(0xFF047857),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(vertical = 2.dp),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Default.Check, contentDescription = null, tint = Color.White, modifier = Modifier.size(if (compact) 10.dp else 12.dp))
                    Spacer(modifier = Modifier.width(3.dp))
                    Text(
                        text = "ĐÃ SỞ HỮU",
                        color = Color.White,
                        fontSize = if (compact) 8.sp else 9.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        } else if (onUnlock != null) {
            Button(
                onClick = onUnlock,
                enabled = canUnlock,
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFFD97706),
                    disabledContainerColor = Color.Gray.copy(alpha = 0.5f)
                ),
                contentPadding = PaddingValues(horizontal = 2.dp, vertical = 0.dp),
                shape = RoundedCornerShape(4.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(if (compact) 22.dp else 26.dp)
            ) {
                Text(
                    text = "Mở ${card.costCoins} Xu",
                    fontSize = if (compact) 8.sp else 10.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }
        } else {
            Text(
                text = "Chạm để xem tiểu sử ↻",
                fontSize = if (compact) 6.sp else 8.sp,
                color = secondaryTextColor.copy(alpha = 0.8f)
            )
        }
    }
}

@Composable
private fun StatItem(
    label: String,
    value: Int,
    primaryColor: Color,
    secondaryColor: Color,
    compact: Boolean
) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Text(
            text = "$value",
            fontWeight = FontWeight.Bold,
            fontSize = if (compact) 8.sp else 10.sp,
            color = primaryColor
        )
        Spacer(modifier = Modifier.width(2.dp))
        Text(
            text = label,
            fontSize = if (compact) 7.sp else 8.sp,
            fontWeight = FontWeight.Medium,
            color = secondaryColor
        )
    }
}

@Composable
private fun CardBackContent(
    card: FootballCardEntity,
    tier: CardRarityTier,
    primaryTextColor: Color,
    secondaryTextColor: Color,
    compact: Boolean,
    onFlipBack: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = if (compact) 8.dp else 12.dp, vertical = if (compact) 6.dp else 10.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = card.playerName.uppercase(),
                fontWeight = FontWeight.Black,
                fontSize = if (compact) 10.sp else 13.sp,
                color = primaryTextColor,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Text(
                text = "${card.clubName} • ${card.leagueName}",
                fontSize = if (compact) 7.sp else 9.sp,
                color = secondaryTextColor,
                fontWeight = FontWeight.SemiBold
            )
        }

        // Career Bio Box
        Surface(
            shape = RoundedCornerShape(6.dp),
            color = Color.Black.copy(alpha = 0.35f),
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = if (card.bio.isNotBlank()) card.bio else "Ngôi sao xuất sắc với những cống hiến to lớn cho câu lạc bộ và đội tuyển quốc gia.",
                fontSize = if (compact) 7.sp else 9.sp,
                color = Color.White,
                lineHeight = if (compact) 10.sp else 13.sp,
                modifier = Modifier.padding(if (compact) 5.dp else 8.dp)
            )
        }

        // Special attributes: Foot, Weak Foot, Skill Moves
        Column(verticalArrangement = Arrangement.spacedBy(2.dp), modifier = Modifier.fillMaxWidth()) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("Chân thuận:", fontSize = if (compact) 7.sp else 8.sp, color = secondaryTextColor)
                Text(card.foot, fontSize = if (compact) 7.sp else 8.sp, fontWeight = FontWeight.Bold, color = primaryTextColor)
            }
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("Chân không thuận:", fontSize = if (compact) 7.sp else 8.sp, color = secondaryTextColor)
                Text("★".repeat(card.weakFoot) + "☆".repeat(5 - card.weakFoot), fontSize = if (compact) 7.sp else 8.sp, color = Color(0xFFF59E0B))
            }
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("Kỹ thuật qua người:", fontSize = if (compact) 7.sp else 8.sp, color = secondaryTextColor)
                Text("★".repeat(card.skillMoves) + "☆".repeat(5 - card.skillMoves), fontSize = if (compact) 7.sp else 8.sp, color = Color(0xFFF59E0B))
            }
        }

        // Flip back hint / action
        Surface(
            shape = RoundedCornerShape(4.dp),
            color = Color.Black.copy(alpha = 0.4f),
            modifier = Modifier
                .clickable { onFlipBack() }
                .padding(vertical = 2.dp)
        ) {
            Text(
                text = "↻ Quay lại mặt trước",
                fontSize = if (compact) 7.sp else 8.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White,
                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
            )
        }
    }
}
