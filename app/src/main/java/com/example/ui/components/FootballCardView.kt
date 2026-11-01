package com.example.ui.components

import android.content.Context
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.core.audio.ToneGenerator
import com.example.data.entity.FootballCardEntity
import com.example.designsystem.components.*
import com.example.designsystem.theme.LifeTheme
import kotlinx.coroutines.delay
import kotlin.math.cos
import kotlin.math.sin

// Definition of Pack Types in FIFA Pack Store
data class FifaPackType(
    val id: String,
    val name: String,
    val costCoins: Int,
    val description: String,
    val minOvr: Int,
    val iconColor: Color,
    val dropRateText: String,
    val tag: String
)

val FIFA_PACKS = listOf(
    FifaPackType(
        id = "PACK_BRONZE",
        name = "Gói Tân Thủ Đồng (Bronze)",
        costCoins = 50,
        description = "Chứa 1 cầu thủ Bronze hoặc Silver tiềm năng.",
        minOvr = 60,
        iconColor = Color(0xFFB45309),
        dropRateText = "Bronze 60-70: 80% • Silver 71-79: 20%",
        tag = "TIẾT KIỆM"
    ),
    FifaPackType(
        id = "PACK_SILVER",
        name = "Gói Tiêu Chuẩn Bạc (Silver)",
        costCoins = 100,
        description = "Chứa các tài năng trẻ và cầu thủ chất lượng.",
        minOvr = 70,
        iconColor = Color(0xFF64748B),
        dropRateText = "Silver 70-79: 75% • Gold 80-84: 24% • TOTW: 1%",
        tag = "CƠ BẢN"
    ),
    FifaPackType(
        id = "PACK_GOLD",
        name = "Gói Vàng Cao Cấp (Gold Premium)",
        costCoins = 250,
        description = "Đảm bảo cầu thủ Vàng chất lượng cao của 5 giải đấu châu Âu.",
        minOvr = 82,
        iconColor = Color(0xFFD97706),
        dropRateText = "Gold 82-87: 75% • TOTW 88+: 18% • Hero: 6% • Icon: 1%",
        tag = "PHỔ BIẾN"
    ),
    FifaPackType(
        id = "PACK_MEGA",
        name = "Gói Ngôi Sao Đặc Biệt (Mega Stars)",
        costCoins = 500,
        description = "Tỷ lệ cực cao trúng siêu sao TOTW và Huyền thoại thế giới.",
        minOvr = 85,
        iconColor = Color(0xFF6366F1),
        dropRateText = "Gold 85+: 45% • TOTW: 40% • Hero: 12% • Icon: 3%",
        tag = "HOT"
    ),
    FifaPackType(
        id = "PACK_ULTIMATE",
        name = "Gói Huyền Thoại Tối Thượng (Ultimate Legends)",
        costCoins = 1200,
        description = "100% ĐẢM BẢO trúng 1 Huyền thoại CLB (Hero) hoặc Huyền thoại Thế giới (Icon)!",
        minOvr = 87,
        iconColor = Color(0xFFEAB308),
        dropRateText = "100% HUYỀN THOẠI (Hero 60% • Icon 40%)",
        tag = "SIÊU VIP"
    )
)

object PityManager {
    private const val PREFS_NAME = "lifeos_fifa_pity"
    private const val KEY_PITY_COUNT = "pity_icon_count"
    const val MAX_PITY = 10

    fun getPityCount(context: Context): Int {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        return prefs.getInt(KEY_PITY_COUNT, 0)
    }

    fun incrementPity(context: Context): Int {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val current = prefs.getInt(KEY_PITY_COUNT, 0) + 1
        prefs.edit().putInt(KEY_PITY_COUNT, current).apply()
        return current
    }

    fun resetPity(context: Context) {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        prefs.edit().putInt(KEY_PITY_COUNT, 0).apply()
    }
}

/**
 * Backward compatible wrapper forwarding to the unified PlayerCard
 */
@Composable
fun FifaPlayerCard(
    card: FootballCardEntity,
    canUnlock: Boolean = false,
    onUnlock: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    PlayerCard(
        card = card,
        canUnlock = canUnlock,
        onUnlock = onUnlock,
        compact = true,
        modifier = modifier
    )
}

/**
 * Dramatic, suspenseful FUT pack opening reveal with 5 stages:
 * Stage 0: Pack shake & rattle
 * Stage 1: Explosion & particle bursts
 * Stage 2: Mystery card slides up facedown
 * Stage 3: Walkout suspense (Nationality -> Position -> Club) if high rating
 * Stage 4: 3D Card Flip reveal with sound chime & celebration
 */
@Composable
fun PackOpeningRevealDialog(
    openedCard: FootballCardEntity,
    isDuplicate: Boolean,
    coinsRefunded: Int,
    onDismiss: () -> Unit,
    onOpenAnother: () -> Unit
) {
    var stage by remember { mutableIntStateOf(0) }
    val isWalkout = openedCard.ovr >= 86 || openedCard.rarity in listOf("ICON", "HERO", "TOTW")

    LaunchedEffect(Unit) {
        // Stage 0: Shaking the pack (0.8s)
        delay(800)
        stage = 1
        ToneGenerator.playSuccessChime()
        // Stage 1: Explosion burst (0.6s)
        delay(600)
        stage = 2
        // Stage 2: Mystery card slides out (0.8s)
        delay(800)
        if (isWalkout) {
            stage = 3 // Walkout Suspense
            delay(1300) // Nation
            stage = 4 // Position & Club
            delay(1300)
            stage = 5 // Grand Card Reveal!
            ToneGenerator.playSuccessChime()
        } else {
            stage = 5 // Direct Card Reveal
            ToneGenerator.playSuccessChime()
        }
    }

    // Shaking pack animation
    val infiniteTransition = rememberInfiniteTransition(label = "pack_shake")
    val shakeRotation by infiniteTransition.animateFloat(
        initialValue = -5f,
        targetValue = 5f,
        animationSpec = infiniteRepeatable(
            animation = tween(80, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "shake_rot"
    )

    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 0.95f,
        targetValue = 1.05f,
        animationSpec = infiniteRepeatable(
            animation = tween(600, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse_scale"
    )

    AlertDialog(
        onDismissRequest = { if (stage >= 5) onDismiss() },
        title = {
            Text(
                text = when {
                    stage < 5 -> "⚡ ĐANG MỞ GÓI THẺ..."
                    openedCard.rarity == "ICON" -> "👑 HUYỀN THOẠI THẾ GIỚI ICONS!!"
                    openedCard.rarity == "HERO" -> "🛡️ HUYỀN THOẠI CLB HEROES!!"
                    openedCard.rarity == "TOTW" -> "⚡ NGÔI SAO TRONG TUẦN TOTW!!"
                    openedCard.ovr >= 88 -> "🔥 SIÊU SAO WALKOUT ĐỈNH CAO!!"
                    else -> "✨ CHÚC MỪNG BẠN ĐÃ NHẬN THẺ!"
                },
                fontWeight = FontWeight.ExtraBold,
                textAlign = TextAlign.Center,
                fontSize = 17.sp,
                color = when {
                    openedCard.rarity == "ICON" -> Color(0xFFF59E0B)
                    openedCard.rarity == "HERO" -> Color(0xFFC084FC)
                    openedCard.rarity == "TOTW" -> Color(0xFF38BDF8)
                    else -> Color(0xFF10B981)
                },
                modifier = Modifier.fillMaxWidth()
            )
        },
        text = {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(340.dp),
                contentAlignment = Alignment.Center
            ) {
                when (stage) {
                    0 -> {
                        // Stage 0: Shaking Pack
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(130.dp, 180.dp)
                                    .rotate(shakeRotation)
                                    .clip(RoundedCornerShape(16.dp))
                                    .background(
                                        Brush.verticalGradient(
                                            listOf(Color(0xFF312E81), Color(0xFF4C1D95), Color(0xFFF59E0B))
                                        )
                                    )
                                    .border(2.dp, Color(0xFFFDE047), RoundedCornerShape(16.dp)),
                                contentAlignment = Alignment.Center
                            ) {
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Icon(Icons.Default.CardGiftcard, contentDescription = null, tint = Color.White, modifier = Modifier.size(48.dp))
                                    Spacer(modifier = Modifier.height(8.dp))
                                    Text("FUT PACK", fontWeight = FontWeight.Black, fontSize = 14.sp, color = Color.White)
                                }
                            }
                            Spacer(modifier = Modifier.height(16.dp))
                            Text("Đang xé bao bì...", style = LifeTheme.typography.bodyMedium, color = Color.Gray)
                        }
                    }
                    1 -> {
                        // Stage 1: Explosion & Particle burst
                        Canvas(modifier = Modifier.fillMaxSize()) {
                            val center = Offset(size.width / 2, size.height / 2)
                            val colors = listOf(Color(0xFFF59E0B), Color(0xFF38BDF8), Color(0xFFEC4899), Color(0xFF10B981))
                            for (i in 0 until 24) {
                                val angle = (i * 15) * (Math.PI / 180f)
                                val radius = (80..140).random().toFloat()
                                val x = center.x + (radius * cos(angle)).toFloat()
                                val y = center.y + (radius * sin(angle)).toFloat()
                                drawCircle(
                                    color = colors.random(),
                                    radius = (4..8).random().toFloat(),
                                    center = Offset(x, y)
                                )
                            }
                        }
                    }
                    2 -> {
                        // Stage 2: Mystery Card Face Down
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Box(
                                modifier = Modifier
                                    .scale(pulseScale)
                                    .size(140.dp, 196.dp)
                                    .clip(RoundedCornerShape(14.dp))
                                    .background(
                                        Brush.verticalGradient(
                                            listOf(Color(0xFF0F172A), Color(0xFF1E293B), Color(0xFF334155))
                                        )
                                    )
                                    .border(2.dp, Color(0xFF38BDF8), RoundedCornerShape(14.dp)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(Icons.Default.HelpOutline, contentDescription = null, tint = Color(0xFF38BDF8), modifier = Modifier.size(54.dp))
                            }
                            Spacer(modifier = Modifier.height(12.dp))
                            Text("Ai sẽ xuất hiện?!", fontWeight = FontWeight.Bold, color = Color.White)
                        }
                    }
                    3 -> {
                        // Stage 3: Suspense Walkout - Reveal Nationality
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Icon(Icons.Default.Public, contentDescription = null, tint = Color(0xFFF59E0B), modifier = Modifier.size(68.dp).scale(pulseScale))
                            Spacer(modifier = Modifier.height(16.dp))
                            Text("QUỐC TỊCH", style = LifeTheme.typography.labelMedium, color = Color.Gray)
                            Text(
                                openedCard.nationality.uppercase(),
                                fontWeight = FontWeight.Black,
                                fontSize = 28.sp,
                                color = Color(0xFFF59E0B)
                            )
                        }
                    }
                    4 -> {
                        // Stage 4: Suspense Walkout - Reveal Position & Club
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Surface(shape = CircleShape, color = Color(0xFF4338CA), modifier = Modifier.size(68.dp).scale(pulseScale)) {
                                Box(contentAlignment = Alignment.Center) {
                                    Text(openedCard.position, fontWeight = FontWeight.Black, fontSize = 26.sp, color = Color.White)
                                }
                            }
                            Spacer(modifier = Modifier.height(16.dp))
                            Text("CÂU LẠC BỘ", style = LifeTheme.typography.labelMedium, color = Color.Gray)
                            Text(
                                openedCard.clubName.uppercase(),
                                fontWeight = FontWeight.Black,
                                fontSize = 22.sp,
                                color = Color(0xFFFDE047),
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                    else -> {
                        // Stage 5: Grand Reveal: 3D Flip Card!
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Box(
                                modifier = Modifier
                                    .width(180.dp)
                                    .scale(pulseScale),
                                contentAlignment = Alignment.Center
                            ) {
                                PlayerCard(
                                    card = openedCard.copy(isOwned = true),
                                    compact = false
                                )
                            }
                            Spacer(modifier = Modifier.height(8.dp))
                            if (isDuplicate) {
                                Surface(
                                    shape = RoundedCornerShape(6.dp),
                                    color = Color(0xFFFEF3C7)
                                ) {
                                    Row(
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Icon(Icons.Default.MonetizationOn, contentDescription = null, tint = Color(0xFFD97706), modifier = Modifier.size(14.dp))
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text("Đã có trong BST! Hoàn lại +$coinsRefunded Xu", fontWeight = FontWeight.Bold, fontSize = 11.sp, color = Color(0xFFB45309))
                                    }
                                }
                            } else {
                                Surface(
                                    shape = RoundedCornerShape(6.dp),
                                    color = Color(0xFFDCFCE7)
                                ) {
                                    Text("🎉 THẺ MỚI TINH ĐÃ VÀO BỘ SƯU TẬP!", fontWeight = FontWeight.Bold, fontSize = 11.sp, color = Color(0xFF16A34A), modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp))
                                }
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {
            if (stage >= 5) {
                LifeButton(
                    text = "Thu Thập Thẻ",
                    onClick = onDismiss,
                    variant = LifeButtonVariant.PRIMARY,
                    size = LifeButtonSize.MEDIUM
                )
            }
        },
        dismissButton = {
            if (stage >= 5) {
                LifeButton(
                    text = "Mở Tiếp Gói Nữa",
                    onClick = onOpenAnother,
                    variant = LifeButtonVariant.OUTLINE,
                    size = LifeButtonSize.MEDIUM
                )
            }
        }
    )
}

/**
 * Dialog for creating a custom personalized Player Card
 */
@Composable
fun CreateCustomCardDialog(
    onDismiss: () -> Unit,
    onSave: (FootballCardEntity) -> Unit
) {
    var playerName by remember { mutableStateOf("") }
    var clubName by remember { mutableStateOf("") }
    var leagueName by remember { mutableStateOf("Premier League") }
    var position by remember { mutableStateOf("ST") }
    var nationality by remember { mutableStateOf("Việt Nam") }
    var rarity by remember { mutableStateOf("GOLD") }
    var ovr by remember { mutableIntStateOf(88) }
    var pace by remember { mutableIntStateOf(85) }
    var shooting by remember { mutableIntStateOf(85) }
    var passing by remember { mutableIntStateOf(82) }
    var dribbling by remember { mutableIntStateOf(86) }
    var defending by remember { mutableIntStateOf(65) }
    var physical by remember { mutableIntStateOf(78) }
    var bio by remember { mutableStateOf("") }
    var foot by remember { mutableStateOf("Phải") }
    var weakFoot by remember { mutableIntStateOf(4) }
    var skillMoves by remember { mutableIntStateOf(4) }

    val previewCard = remember(playerName, clubName, leagueName, position, nationality, rarity, ovr, pace, shooting, passing, dribbling, defending, physical, bio, foot, weakFoot, skillMoves) {
        FootballCardEntity(
            id = 0,
            clubName = if (clubName.isBlank()) "My Club" else clubName,
            leagueName = leagueName,
            playerName = if (playerName.isBlank()) "Cầu Thủ" else playerName,
            position = position,
            ovr = ovr,
            rarity = rarity,
            costCoins = 200,
            isOwned = true,
            nationality = if (nationality.isBlank()) "Việt Nam" else nationality,
            pace = pace,
            shooting = shooting,
            passing = passing,
            dribbling = dribbling,
            defending = defending,
            physical = physical,
            bio = if (bio.isBlank()) "Cầu thủ tùy chỉnh độc bản do người dùng tạo." else bio,
            foot = foot,
            weakFoot = weakFoot,
            skillMoves = skillMoves
        )
    }

    val scrollState = rememberScrollState()

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text("⭐ Tự Tạo Thẻ Cầu Thủ Tùy Chỉnh", style = LifeTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(scrollState),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // Live Card Preview
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    contentAlignment = Alignment.Center
                ) {
                    PlayerCard(
                        card = previewCard,
                        modifier = Modifier.width(160.dp),
                        compact = true
                    )
                }

                LifeTextField(
                    value = playerName,
                    onValueChange = { playerName = it },
                    label = "Tên cầu thủ (vd: Nguyễn Quang Hải)",
                    modifier = Modifier.fillMaxWidth()
                )

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    LifeTextField(
                        value = clubName,
                        onValueChange = { clubName = it },
                        label = "CLB (vd: Công An Hà Nội)",
                        modifier = Modifier.weight(1f)
                    )
                    LifeTextField(
                        value = nationality,
                        onValueChange = { nationality = it },
                        label = "Quốc tịch (vd: Việt Nam)",
                        modifier = Modifier.weight(1f)
                    )
                }

                // League selection chips
                Text("Giải đấu:", style = LifeTheme.typography.labelSmall, fontWeight = FontWeight.Bold)
                LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    items(listOf("Premier League", "La Liga", "Serie A", "Bundesliga", "Ligue 1", "V-League", "Icons", "Heroes")) { l ->
                        LifeChip(text = l, selected = leagueName == l, onClick = { leagueName = l })
                    }
                }

                // Position selection chips
                Text("Vị trí:", style = LifeTheme.typography.labelSmall, fontWeight = FontWeight.Bold)
                LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    items(listOf("ST", "CF", "RW", "LW", "CAM", "CM", "CDM", "CB", "LB", "RB", "GK")) { pos ->
                        LifeChip(text = pos, selected = position == pos, onClick = { position = pos })
                    }
                }

                // Rarity selection chips
                Text("Bậc thẻ:", style = LifeTheme.typography.labelSmall, fontWeight = FontWeight.Bold)
                LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    items(listOf("BRONZE", "SILVER", "GOLD", "TOTW", "HERO", "ICON")) { r ->
                        LifeChip(text = r, selected = rarity == r, onClick = { rarity = r })
                    }
                }

                // Overall Rating (OVR) slider
                Text("Chỉ số tổng (OVR): $ovr", style = LifeTheme.typography.labelSmall, fontWeight = FontWeight.Bold)
                Slider(
                    value = ovr.toFloat(),
                    onValueChange = { ovr = it.toInt() },
                    valueRange = 50f..99f,
                    steps = 49
                )

                // 6 Stats sliders
                Text("Chỉ số chi tiết:", style = LifeTheme.typography.labelSmall, fontWeight = FontWeight.Bold)
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text("PAC (Tốc độ): $pace", fontSize = 11.sp)
                        Slider(value = pace.toFloat(), onValueChange = { pace = it.toInt() }, valueRange = 40f..99f)
                        Text("PAS (Chuyền): $passing", fontSize = 11.sp)
                        Slider(value = passing.toFloat(), onValueChange = { passing = it.toInt() }, valueRange = 40f..99f)
                        Text("DEF (Phòng ngự): $defending", fontSize = 11.sp)
                        Slider(value = defending.toFloat(), onValueChange = { defending = it.toInt() }, valueRange = 30f..99f)
                    }
                    Column(modifier = Modifier.weight(1f)) {
                        Text("SHO (Dứt điểm): $shooting", fontSize = 11.sp)
                        Slider(value = shooting.toFloat(), onValueChange = { shooting = it.toInt() }, valueRange = 40f..99f)
                        Text("DRI (Rê bóng): $dribbling", fontSize = 11.sp)
                        Slider(value = dribbling.toFloat(), onValueChange = { dribbling = it.toInt() }, valueRange = 40f..99f)
                        Text("PHY (Thể lực): $physical", fontSize = 11.sp)
                        Slider(value = physical.toFloat(), onValueChange = { physical = it.toInt() }, valueRange = 40f..99f)
                    }
                }

                // Bio
                LifeTextField(
                    value = bio,
                    onValueChange = { bio = it },
                    label = "Tiểu sử / Thành tích cá nhân",
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            LifeButton(
                text = "Lưu Thẻ Vào Bộ Sưu Tập",
                onClick = {
                    onSave(previewCard)
                    onDismiss()
                },
                enabled = playerName.isNotBlank() && clubName.isNotBlank(),
                variant = LifeButtonVariant.PRIMARY,
                size = LifeButtonSize.MEDIUM
            )
        },
        dismissButton = {
            LifeButton(
                text = "Hủy",
                onClick = onDismiss,
                variant = LifeButtonVariant.OUTLINE,
                size = LifeButtonSize.MEDIUM
            )
        }
    )
}

/**
 * Bulk CSV / JSON Import & Export Dialog
 */
@Composable
fun ImportExportCardDialog(
    allCards: List<FootballCardEntity>,
    onImportCards: (List<FootballCardEntity>) -> Unit,
    onDismiss: () -> Unit
) {
    var csvText by remember { mutableStateOf("") }
    var importStatusMessage by remember { mutableStateOf<String?>(null) }
    val clipboardManager = LocalClipboardManager.current

    val sampleCsv = """
Tên,CLB,Giải Đấu,Vị Trí,OVR,Bậc,Quốc Tịch,PAC,SHO,PAS,DRI,DEF,PHY,Chân,Tiểu Sử
Quang Hải,CAHN,V-League,CAM,84,GOLD,Việt Nam,82,85,86,87,55,68,Trái,Nhạc trưởng hoa mỹ của bóng đá Việt Nam
Tiến Linh,Bình Dương,V-League,ST,82,GOLD,Việt Nam,80,84,72,76,42,82,Phải,Vua phá lưới với khả năng chớp thời cơ nhạy bén
Hoàng Đức,Thể Công,V-League,CM,83,GOLD,Việt Nam,78,79,85,84,72,77,Trái,Quả bóng vàng Việt Nam với lối chơi đĩnh đạc
""".trimIndent()

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text("📁 Nhập / Xuất Dữ Liệu Cầu Thủ (CSV)", style = LifeTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Text(
                    text = "Dán dữ liệu CSV cầu thủ theo định dạng: Tên,CLB,Giải Đấu,Vị Trí,OVR,Bậc,Quốc Tịch,PAC,SHO,PAS,DRI,DEF,PHY,Chân,Tiểu Sử",
                    style = LifeTheme.typography.bodySmall,
                    color = Color.Gray
                )

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    LifeButton(
                        text = "📋 Tải dữ liệu mẫu CSV",
                        onClick = { csvText = sampleCsv },
                        variant = LifeButtonVariant.OUTLINE,
                        size = LifeButtonSize.SMALL
                    )

                    LifeButton(
                        text = "📤 Xuất toàn bộ thẻ ra CSV",
                        onClick = {
                            val header = "Tên,CLB,Giải Đấu,Vị Trí,OVR,Bậc,Quốc Tịch,PAC,SHO,PAS,DRI,DEF,PHY,Chân,Tiểu Sử\n"
                            val body = allCards.joinToString("\n") {
                                "${it.playerName},${it.clubName},${it.leagueName},${it.position},${it.ovr},${it.rarity},${it.nationality},${it.pace},${it.shooting},${it.passing},${it.dribbling},${it.defending},${it.physical},${it.foot},${it.bio}"
                            }
                            clipboardManager.setText(AnnotatedString(header + body))
                            importStatusMessage = "Đã sao chép ${allCards.size} cầu thủ vào bộ nhớ tạm!"
                        },
                        variant = LifeButtonVariant.OUTLINE,
                        size = LifeButtonSize.SMALL
                    )
                }

                OutlinedTextField(
                    value = csvText,
                    onValueChange = { csvText = it },
                    label = { Text("Dán văn bản CSV tại đây") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(180.dp),
                    textStyle = LifeTheme.typography.bodySmall
                )

                if (importStatusMessage != null) {
                    Text(
                        text = importStatusMessage ?: "",
                        color = Color(0xFF10B981),
                        style = LifeTheme.typography.bodySmall,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        },
        confirmButton = {
            LifeButton(
                text = "Tiến Hành Nhập Thẻ",
                onClick = {
                    try {
                        val lines = csvText.lines().filter { it.isNotBlank() }
                        val imported = mutableListOf<FootballCardEntity>()
                        lines.forEachIndexed { index, line ->
                            if (index == 0 && (line.contains("Tên") || line.contains("Name"))) return@forEachIndexed
                            val parts = line.split(",").map { it.trim() }
                            if (parts.size >= 7) {
                                val name = parts[0]
                                val club = parts[1]
                                val league = parts[2]
                                val pos = parts[3]
                                val ovr = parts[4].toIntOrNull() ?: 80
                                val rarity = parts[5].uppercase()
                                val nation = parts[6]
                                val pac = parts.getOrNull(7)?.toIntOrNull() ?: 80
                                val sho = parts.getOrNull(8)?.toIntOrNull() ?: 80
                                val pas = parts.getOrNull(9)?.toIntOrNull() ?: 80
                                val dri = parts.getOrNull(10)?.toIntOrNull() ?: 80
                                val def = parts.getOrNull(11)?.toIntOrNull() ?: 60
                                val phy = parts.getOrNull(12)?.toIntOrNull() ?: 75
                                val foot = parts.getOrNull(13) ?: "Phải"
                                val bio = parts.getOrNull(14) ?: ""

                                imported.add(
                                    FootballCardEntity(
                                        clubName = club,
                                        leagueName = league,
                                        playerName = name,
                                        position = pos,
                                        ovr = ovr,
                                        rarity = rarity,
                                        costCoins = 180,
                                        isOwned = true,
                                        nationality = nation,
                                        pace = pac,
                                        shooting = sho,
                                        passing = pas,
                                        dribbling = dri,
                                        defending = def,
                                        physical = phy,
                                        bio = bio,
                                        foot = foot
                                    )
                                )
                            }
                        }
                        if (imported.isNotEmpty()) {
                            onImportCards(imported)
                            importStatusMessage = "Thành công nhập ${imported.size} cầu thủ vào cơ sở dữ liệu!"
                        } else {
                            importStatusMessage = "Không tìm thấy dữ liệu hợp lệ. Vui lòng kiểm tra định dạng."
                        }
                    } catch (e: Exception) {
                        importStatusMessage = "Lỗi khi nhập: ${e.message}"
                    }
                },
                enabled = csvText.isNotBlank(),
                variant = LifeButtonVariant.PRIMARY,
                size = LifeButtonSize.MEDIUM
            )
        },
        dismissButton = {
            LifeButton(
                text = "Đóng",
                onClick = onDismiss,
                variant = LifeButtonVariant.OUTLINE,
                size = LifeButtonSize.MEDIUM
            )
        }
    )
}
