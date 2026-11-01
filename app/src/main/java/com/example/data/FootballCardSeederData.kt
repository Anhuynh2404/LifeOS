package com.example.data

import com.example.data.entity.FootballCardEntity

object FootballCardSeederData {
    val ALL_CARDS: List<FootballCardEntity> = listOf(
        // =========================================================================
        // PREMIER LEAGUE (ANH)
        // =========================================================================
        // Arsenal
        FootballCardEntity(
            id = 1, clubName = "Arsenal", leagueName = "Premier League", playerName = "Bukayo Saka",
            position = "RW", ovr = 87, rarity = "GOLD", costCoins = 180, isOwned = false, nationality = "Anh",
            pace = 88, shooting = 83, passing = 84, dribbling = 89, defending = 65, physical = 76,
            bio = "Ngôi sao sáng giá của Hale End, bùng nổ bên cánh phải Arsenal và Tam Sư.", foot = "Trái", weakFoot = 4, skillMoves = 4
        ),
        FootballCardEntity(
            id = 2, clubName = "Arsenal", leagueName = "Premier League", playerName = "Martin Ødegaard",
            position = "CAM", ovr = 89, rarity = "GOLD", costCoins = 220, isOwned = false, nationality = "Na Uy",
            pace = 76, shooting = 82, passing = 90, dribbling = 89, defending = 68, physical = 64,
            bio = "Đội trưởng tài ba, bộ não điều phối chiến thuật với nhãn quan sắc bén tại Emirates.", foot = "Trái", weakFoot = 3, skillMoves = 5
        ),
        FootballCardEntity(
            id = 3, clubName = "Arsenal", leagueName = "Premier League", playerName = "Declan Rice",
            position = "CDM", ovr = 88, rarity = "GOLD", costCoins = 200, isOwned = false, nationality = "Anh",
            pace = 75, shooting = 72, passing = 83, dribbling = 81, defending = 87, physical = 86,
            bio = "Bức tường thép trung tuyến, bậc thầy thu hồi bóng và bao quát không gian.", foot = "Phải", weakFoot = 3, skillMoves = 3
        ),
        FootballCardEntity(
            id = 4, clubName = "Arsenal", leagueName = "Premier League", playerName = "William Saliba",
            position = "CB", ovr = 87, rarity = "GOLD", costCoins = 190, isOwned = false, nationality = "Pháp",
            pace = 83, shooting = 40, passing = 73, dribbling = 74, defending = 88, physical = 83,
            bio = "Trung vệ điềm tĩnh, tốc độ đáng kinh ngạc và khả năng 1-đối-1 thượng hạng.", foot = "Phải", weakFoot = 3, skillMoves = 3
        ),
        FootballCardEntity(
            id = 5, clubName = "Arsenal", leagueName = "Premier League", playerName = "Kai Havertz",
            position = "ST", ovr = 85, rarity = "GOLD", costCoins = 150, isOwned = false, nationality = "Đức",
            pace = 82, shooting = 82, passing = 80, dribbling = 83, defending = 60, physical = 77,
            bio = "Số 9 ảo thông minh, luôn biết cách xuất hiện đúng lúc tại vòng cấm.", foot = "Trái", weakFoot = 4, skillMoves = 4
        ),
        FootballCardEntity(
            id = 6, clubName = "Arsenal", leagueName = "Premier League", playerName = "Ethan Nwaneri",
            position = "CAM", ovr = 71, rarity = "SILVER", costCoins = 70, isOwned = false, nationality = "Anh",
            pace = 77, shooting = 69, passing = 72, dribbling = 76, defending = 45, physical = 58,
            bio = "Thần đồng trẻ tuổi nhất từng ra sân tại Ngoại hạng Anh, tiềm năng vô hạn.", foot = "Trái", weakFoot = 3, skillMoves = 3
        ),
        FootballCardEntity(
            id = 7, clubName = "Arsenal", leagueName = "Premier League", playerName = "Myles Lewis-Skelly",
            position = "LB", ovr = 67, rarity = "SILVER", costCoins = 60, isOwned = false, nationality = "Anh",
            pace = 76, shooting = 55, passing = 68, dribbling = 70, defending = 65, physical = 68,
            bio = "Hậu vệ biên đa năng từ học viện London Colney.", foot = "Trái", weakFoot = 3, skillMoves = 3
        ),

        // Manchester City
        FootballCardEntity(
            id = 8, clubName = "Manchester City", leagueName = "Premier League", playerName = "Erling Haaland",
            position = "ST", ovr = 91, rarity = "GOLD", costCoins = 320, isOwned = false, nationality = "Na Uy",
            pace = 89, shooting = 93, passing = 70, dribbling = 81, defending = 45, physical = 88,
            bio = "Quái vật ghi bàn với sức mạnh càn lướt và bản năng săn bàn kinh hoàng.", foot = "Trái", weakFoot = 3, skillMoves = 3
        ),
        FootballCardEntity(
            id = 9, clubName = "Manchester City", leagueName = "Premier League", playerName = "Erling Haaland (TOTW)",
            position = "ST", ovr = 93, rarity = "TOTW", costCoins = 480, isOwned = false, nationality = "Na Uy",
            pace = 91, shooting = 95, passing = 74, dribbling = 84, defending = 48, physical = 90,
            bio = "Phiên bản In-form bùng nổ với cú hat-trick siêu đẳng.", foot = "Trái", weakFoot = 4, skillMoves = 3
        ),
        FootballCardEntity(
            id = 10, clubName = "Manchester City", leagueName = "Premier League", playerName = "Kevin De Bruyne",
            position = "CM", ovr = 91, rarity = "GOLD", costCoins = 300, isOwned = false, nationality = "Bỉ",
            pace = 72, shooting = 88, passing = 94, dribbling = 87, defending = 65, physical = 74,
            bio = "Nhạc trưởng vĩ đại của Pep Guardiola với những đường chọc khe hình học.", foot = "Phải", weakFoot = 5, skillMoves = 4
        ),
        FootballCardEntity(
            id = 11, clubName = "Manchester City", leagueName = "Premier League", playerName = "Rodri",
            position = "CDM", ovr = 91, rarity = "GOLD", costCoins = 310, isOwned = false, nationality = "Tây Ban Nha",
            pace = 66, shooting = 80, passing = 86, dribbling = 84, defending = 87, physical = 85,
            bio = "Chủ nhân Quả Bóng Vàng, ông chủ khu trung tuyến hoàn hảo.", foot = "Phải", weakFoot = 4, skillMoves = 3
        ),
        FootballCardEntity(
            id = 12, clubName = "Manchester City", leagueName = "Premier League", playerName = "Phil Foden",
            position = "RW", ovr = 88, rarity = "GOLD", costCoins = 210, isOwned = false, nationality = "Anh",
            pace = 86, shooting = 86, passing = 85, dribbling = 90, defending = 60, physical = 68,
            bio = "Cầu thủ xuất sắc nhất Premier League mùa giải, kỹ thuật chạm một điệu nghệ.", foot = "Trái", weakFoot = 4, skillMoves = 4
        ),
        FootballCardEntity(
            id = 13, clubName = "Manchester City", leagueName = "Premier League", playerName = "Rico Lewis",
            position = "RB", ovr = 74, rarity = "SILVER", costCoins = 80, isOwned = false, nationality = "Anh",
            pace = 79, shooting = 62, passing = 75, dribbling = 78, defending = 72, physical = 65,
            bio = "Hậu vệ biên đảo ngược thông minh được Pep Guardiola tôi luyện.", foot = "Phải", weakFoot = 3, skillMoves = 3
        ),

        // Liverpool
        FootballCardEntity(
            id = 14, clubName = "Liverpool", leagueName = "Premier League", playerName = "Mohamed Salah",
            position = "RW", ovr = 89, rarity = "GOLD", costCoins = 250, isOwned = false, nationality = "Ai Cập",
            pace = 89, shooting = 87, passing = 82, dribbling = 88, defending = 45, physical = 76,
            bio = "Vua Ai Cập tại Anfield, chân sút kỷ lục với những pha cứa lòng ma thuật.", foot = "Trái", weakFoot = 3, skillMoves = 4
        ),
        FootballCardEntity(
            id = 15, clubName = "Liverpool", leagueName = "Premier League", playerName = "Mohamed Salah (TOTW)",
            position = "RW", ovr = 91, rarity = "TOTW", costCoins = 420, isOwned = false, nationality = "Ai Cập",
            pace = 91, shooting = 89, passing = 85, dribbling = 90, defending = 47, physical = 78,
            bio = "Thẻ Ngôi Sao Trong Tuần phong độ đỉnh cao của Mo Salah.", foot = "Trái", weakFoot = 4, skillMoves = 4
        ),
        FootballCardEntity(
            id = 16, clubName = "Liverpool", leagueName = "Premier League", playerName = "Virgil van Dijk",
            position = "CB", ovr = 89, rarity = "GOLD", costCoins = 250, isOwned = false, nationality = "Hà Lan",
            pace = 78, shooting = 60, passing = 72, dribbling = 72, defending = 89, physical = 86,
            bio = "Thủ lĩnh hàng thủ The Kop, pháo đài không chiến và chỉ huy sắc đá.", foot = "Phải", weakFoot = 3, skillMoves = 2
        ),
        FootballCardEntity(
            id = 17, clubName = "Liverpool", leagueName = "Premier League", playerName = "Alexis Mac Allister",
            position = "CM", ovr = 86, rarity = "GOLD", costCoins = 170, isOwned = false, nationality = "Argentina",
            pace = 74, shooting = 82, passing = 86, dribbling = 85, defending = 78, physical = 75,
            bio = "Nhà vô địch thế giới với lối đá mềm mại, tư duy bóng đá đỉnh cao.", foot = "Phải", weakFoot = 3, skillMoves = 3
        ),
        FootballCardEntity(
            id = 18, clubName = "Liverpool", leagueName = "Premier League", playerName = "Conor Bradley",
            position = "RB", ovr = 73, rarity = "SILVER", costCoins = 75, isOwned = false, nationality = "Bắc Ireland",
            pace = 82, shooting = 61, passing = 70, dribbling = 74, defending = 70, physical = 71,
            bio = "Phát hiện trẻ rực sáng nơi hành lang cánh phải The Kop.", foot = "Phải", weakFoot = 3, skillMoves = 3
        ),

        // Manchester United
        FootballCardEntity(
            id = 19, clubName = "Manchester United", leagueName = "Premier League", playerName = "Bruno Fernandes",
            position = "CAM", ovr = 87, rarity = "GOLD", costCoins = 190, isOwned = false, nationality = "Bồ Đào Nha",
            pace = 74, shooting = 85, passing = 89, dribbling = 83, defending = 72, physical = 78,
            bio = "Đội trưởng Quỷ Đỏ, nguồn sáng tạo dồi dào với những đường chuyền quyết định.", foot = "Phải", weakFoot = 3, skillMoves = 4
        ),
        FootballCardEntity(
            id = 20, clubName = "Manchester United", leagueName = "Premier League", playerName = "Kobbie Mainoo",
            position = "CM", ovr = 80, rarity = "GOLD", costCoins = 120, isOwned = false, nationality = "Anh",
            pace = 75, shooting = 72, passing = 81, dribbling = 84, defending = 76, physical = 75,
            bio = "Tài năng thiên bẩm, kiểm soát nhịp độ trận đấu tự tin như cựu binh.", foot = "Phải", weakFoot = 3, skillMoves = 3
        ),
        FootballCardEntity(
            id = 21, clubName = "Manchester United", leagueName = "Premier League", playerName = "Alejandro Garnacho",
            position = "LW", ovr = 82, rarity = "GOLD", costCoins = 130, isOwned = false, nationality = "Argentina",
            pace = 88, shooting = 78, passing = 74, dribbling = 84, defending = 40, physical = 68,
            bio = "Cánh chim thần tốc với những siêu phẩm ngả bàn đèn để đời.", foot = "Phải", weakFoot = 4, skillMoves = 4
        ),
        FootballCardEntity(
            id = 22, clubName = "Manchester United", leagueName = "Premier League", playerName = "Daniel Gore",
            position = "CM", ovr = 63, rarity = "BRONZE", costCoins = 40, isOwned = false, nationality = "Anh",
            pace = 68, shooting = 56, passing = 65, dribbling = 66, defending = 58, physical = 60,
            bio = "Tiền vệ triển vọng từ lò Carrington giàu nhiệt huyết.", foot = "Phải", weakFoot = 3, skillMoves = 3
        ),

        // Chelsea
        FootballCardEntity(
            id = 23, clubName = "Chelsea", leagueName = "Premier League", playerName = "Cole Palmer",
            position = "CAM", ovr = 86, rarity = "GOLD", costCoins = 180, isOwned = false, nationality = "Anh",
            pace = 82, shooting = 84, passing = 85, dribbling = 87, defending = 55, physical = 70,
            bio = "'Cold Palmer' - phong thái băng giá, điềm tĩnh lạnh lùng trước khung thành.", foot = "Trái", weakFoot = 3, skillMoves = 4
        ),
        FootballCardEntity(
            id = 24, clubName = "Chelsea", leagueName = "Premier League", playerName = "Cole Palmer (TOTW)",
            position = "CAM", ovr = 88, rarity = "TOTW", costCoins = 330, isOwned = false, nationality = "Anh",
            pace = 84, shooting = 87, passing = 88, dribbling = 89, defending = 58, physical = 72,
            bio = "Màn trình diễn In-form 4 bàn thắng trong 20 phút chấn động Stamford Bridge.", foot = "Trái", weakFoot = 4, skillMoves = 4
        ),
        FootballCardEntity(
            id = 25, clubName = "Chelsea", leagueName = "Premier League", playerName = "Moisés Caicedo",
            position = "CDM", ovr = 83, rarity = "GOLD", costCoins = 140, isOwned = false, nationality = "Ecuador",
            pace = 76, shooting = 65, passing = 78, dribbling = 79, defending = 83, physical = 82,
            bio = "Cỗ máy quét năng nổ, nguồn thể lực dồi dào nơi tuyến giữa Chelsea.", foot = "Phải", weakFoot = 3, skillMoves = 3
        ),
        FootballCardEntity(
            id = 26, clubName = "Chelsea", leagueName = "Premier League", playerName = "Tyrique George",
            position = "LW", ovr = 64, rarity = "BRONZE", costCoins = 40, isOwned = false, nationality = "Anh",
            pace = 75, shooting = 62, passing = 60, dribbling = 68, defending = 35, physical = 56,
            bio = "Cầu thủ chạy cánh tài năng trẻ của học viện Cobham.", foot = "Phải", weakFoot = 3, skillMoves = 3
        ),

        // Tottenham, Aston Villa, Newcastle
        FootballCardEntity(
            id = 27, clubName = "Tottenham", leagueName = "Premier League", playerName = "Son Heung-min",
            position = "ST", ovr = 87, rarity = "GOLD", costCoins = 190, isOwned = false, nationality = "Hàn Quốc",
            pace = 87, shooting = 88, passing = 81, dribbling = 84, defending = 42, physical = 70,
            bio = "Niềm tự hào châu Á, tiền đạo toàn diện với khả năng dứt điểm 2 chân như một.", foot = "Phải", weakFoot = 5, skillMoves = 4
        ),
        FootballCardEntity(
            id = 28, clubName = "Aston Villa", leagueName = "Premier League", playerName = "Emiliano Martínez",
            position = "GK", ovr = 87, rarity = "GOLD", costCoins = 180, isOwned = false, nationality = "Argentina",
            pace = 85, shooting = 83, passing = 82, dribbling = 86, defending = 58, physical = 85,
            bio = "'Dibu' - Bậc thầy bắt phạt đền và thủ môn số một hành tinh hiện tại.", foot = "Phải", weakFoot = 3, skillMoves = 1
        ),
        FootballCardEntity(
            id = 29, clubName = "Newcastle", leagueName = "Premier League", playerName = "Alexander Isak",
            position = "ST", ovr = 85, rarity = "GOLD", costCoins = 160, isOwned = false, nationality = "Thụy Điển",
            pace = 88, shooting = 83, passing = 74, dribbling = 84, defending = 36, physical = 74,
            bio = "Chân sút thanh thoát với đôi chân dài ngoắt ngoéo và khả năng dứt điểm lạnh lùng.", foot = "Phải", weakFoot = 4, skillMoves = 4
        ),

        // =========================================================================
        // LA LIGA (TÂY BAN NHA)
        // =========================================================================
        // Real Madrid
        FootballCardEntity(
            id = 30, clubName = "Real Madrid", leagueName = "La Liga", playerName = "Kylian Mbappé",
            position = "ST", ovr = 91, rarity = "GOLD", costCoins = 320, isOwned = false, nationality = "Pháp",
            pace = 97, shooting = 90, passing = 80, dribbling = 92, defending = 36, physical = 78,
            bio = "Siêu tốc độ không thể ngăn cản, sát thủ hàng đầu cập bến Santiago Bernabéu.", foot = "Phải", weakFoot = 4, skillMoves = 5
        ),
        FootballCardEntity(
            id = 31, clubName = "Real Madrid", leagueName = "La Liga", playerName = "Vinícius Jr.",
            position = "LW", ovr = 90, rarity = "GOLD", costCoins = 300, isOwned = false, nationality = "Brazil",
            pace = 96, shooting = 84, passing = 81, dribbling = 91, defending = 35, physical = 70,
            bio = "Vũ công Samba với những pha rê bóng xé toang mọi hàng phòng ngự.", foot = "Phải", weakFoot = 4, skillMoves = 5
        ),
        FootballCardEntity(
            id = 32, clubName = "Real Madrid", leagueName = "La Liga", playerName = "Vinícius Jr. (TOTW)",
            position = "LW", ovr = 92, rarity = "TOTW", costCoins = 460, isOwned = false, nationality = "Brazil",
            pace = 97, shooting = 87, passing = 84, dribbling = 93, defending = 38, physical = 72,
            bio = "Màn trình diễn rực sáng với cú đúp hạ gục đối thủ tại El Clasico.", foot = "Phải", weakFoot = 4, skillMoves = 5
        ),
        FootballCardEntity(
            id = 33, clubName = "Real Madrid", leagueName = "La Liga", playerName = "Jude Bellingham",
            position = "CAM", ovr = 90, rarity = "GOLD", costCoins = 300, isOwned = false, nationality = "Anh",
            pace = 80, shooting = 86, passing = 85, dribbling = 88, defending = 78, physical = 84,
            bio = "'Hey Jude' - Chiến binh toàn năng, vị tướng trẻ làm say đắm người hâm mộ thành Madrid.", foot = "Phải", weakFoot = 4, skillMoves = 4
        ),
        FootballCardEntity(
            id = 34, clubName = "Real Madrid", leagueName = "La Liga", playerName = "Federico Valverde",
            position = "CM", ovr = 88, rarity = "GOLD", costCoins = 210, isOwned = false, nationality = "Uruguay",
            pace = 88, shooting = 83, passing = 84, dribbling = 84, defending = 81, physical = 85,
            bio = "'Chim ưng' với nguồn thể lực vô tận và những cú sút xa như búa bổ.", foot = "Phải", weakFoot = 4, skillMoves = 3
        ),
        FootballCardEntity(
            id = 35, clubName = "Real Madrid", leagueName = "La Liga", playerName = "Endrick",
            position = "ST", ovr = 77, rarity = "SILVER", costCoins = 90, isOwned = false, nationality = "Brazil",
            pace = 86, shooting = 78, passing = 65, dribbling = 80, defending = 35, physical = 76,
            bio = "Thần đồng Brazil với sức mạnh tỳ đè và cú sút sấm sét.", foot = "Trái", weakFoot = 3, skillMoves = 4
        ),

        // Barcelona
        FootballCardEntity(
            id = 36, clubName = "Barcelona", leagueName = "La Liga", playerName = "Lamine Yamal",
            position = "RW", ovr = 85, rarity = "GOLD", costCoins = 180, isOwned = false, nationality = "Tây Ban Nha",
            pace = 88, shooting = 80, passing = 83, dribbling = 88, defending = 42, physical = 62,
            bio = "Hiện tượng EURO, thần đồng La Masia với những pha cứa lòng ma ảo.", foot = "Trái", weakFoot = 3, skillMoves = 4
        ),
        FootballCardEntity(
            id = 37, clubName = "Barcelona", leagueName = "La Liga", playerName = "Lamine Yamal (TOTW)",
            position = "RW", ovr = 88, rarity = "TOTW", costCoins = 350, isOwned = false, nationality = "Tây Ban Nha",
            pace = 90, shooting = 84, passing = 86, dribbling = 91, defending = 45, physical = 65,
            bio = "Phong độ In-form xé toang cánh đối phương với pha bóng độc diễn.", foot = "Trái", weakFoot = 4, skillMoves = 5
        ),
        FootballCardEntity(
            id = 38, clubName = "Barcelona", leagueName = "La Liga", playerName = "Robert Lewandowski",
            position = "ST", ovr = 88, rarity = "GOLD", costCoins = 210, isOwned = false, nationality = "Ba Lan",
            pace = 75, shooting = 88, passing = 80, dribbling = 85, defending = 44, physical = 81,
            bio = "Vua phá lưới kỳ cựu với bản năng vòng cấm mẫu mực bậc nhất châu Âu.", foot = "Phải", weakFoot = 4, skillMoves = 4
        ),
        FootballCardEntity(
            id = 39, clubName = "Barcelona", leagueName = "La Liga", playerName = "Raphinha",
            position = "LW", ovr = 86, rarity = "GOLD", costCoins = 180, isOwned = false, nationality = "Brazil",
            pace = 89, shooting = 83, passing = 82, dribbling = 86, defending = 54, physical = 74,
            bio = "Đội trưởng mẫu mực, chạy không biết mệt mỏi với cú hat-trick vào lưới Bayern.", foot = "Trái", weakFoot = 4, skillMoves = 4
        ),
        FootballCardEntity(
            id = 40, clubName = "Barcelona", leagueName = "La Liga", playerName = "Pedri",
            position = "CM", ovr = 86, rarity = "GOLD", costCoins = 180, isOwned = false, nationality = "Tây Ban Nha",
            pace = 78, shooting = 72, passing = 86, dribbling = 89, defending = 70, physical = 70,
            bio = "Nghệ sĩ giữa sân với lối đá thanh thoát mang hình bóng Iniesta thuở nào.", foot = "Phải", weakFoot = 4, skillMoves = 4
        ),
        FootballCardEntity(
            id = 41, clubName = "Barcelona", leagueName = "La Liga", playerName = "Pau Cubarsí",
            position = "CB", ovr = 75, rarity = "SILVER", costCoins = 85, isOwned = false, nationality = "Tây Ban Nha",
            pace = 72, shooting = 35, passing = 78, dribbling = 72, defending = 77, physical = 72,
            bio = "Trung vệ trẻ chuyền bóng xuyên tuyến đỉnh cao của La Masia.", foot = "Phải", weakFoot = 3, skillMoves = 2
        ),

        // Atletico Madrid, Athletic Bilbao, Real Sociedad, Villarreal
        FootballCardEntity(
            id = 42, clubName = "Atletico Madrid", leagueName = "La Liga", playerName = "Antoine Griezmann",
            position = "ST", ovr = 88, rarity = "GOLD", costCoins = 210, isOwned = false, nationality = "Pháp",
            pace = 80, shooting = 88, passing = 88, dribbling = 88, defending = 60, physical = 74,
            bio = "Hoàng tử bé của Simeone, linh hồn tấn công và phòng ngự từ xa hoàn hảo.", foot = "Trái", weakFoot = 3, skillMoves = 4
        ),
        FootballCardEntity(
            id = 43, clubName = "Athletic Bilbao", leagueName = "La Liga", playerName = "Nico Williams",
            position = "LW", ovr = 85, rarity = "GOLD", costCoins = 170, isOwned = false, nationality = "Tây Ban Nha",
            pace = 93, shooting = 79, passing = 80, dribbling = 87, defending = 40, physical = 68,
            bio = "Nhà vô địch châu Âu với tốc độ kinh hoàng bên cánh trái xứ Basque.", foot = "Phải", weakFoot = 5, skillMoves = 4
        ),
        FootballCardEntity(
            id = 44, clubName = "Real Sociedad", leagueName = "La Liga", playerName = "Takefusa Kubo",
            position = "RW", ovr = 82, rarity = "GOLD", costCoins = 130, isOwned = false, nationality = "Nhật Bản",
            pace = 86, shooting = 78, passing = 80, dribbling = 86, defending = 45, physical = 58,
            bio = "Ngôi sao số một Samurai Xanh với những pha đổi hướng lắt léo.", foot = "Trái", weakFoot = 4, skillMoves = 4
        ),
        FootballCardEntity(
            id = 45, clubName = "Villarreal", leagueName = "La Liga", playerName = "Álex Baena",
            position = "CAM", ovr = 82, rarity = "GOLD", costCoins = 130, isOwned = false, nationality = "Tây Ban Nha",
            pace = 78, shooting = 77, passing = 85, dribbling = 82, defending = 55, physical = 66,
            bio = "Vua kiến tạo La Liga với những đường tạt chuẩn xác từng milimet.", foot = "Phải", weakFoot = 3, skillMoves = 3
        ),

        // =========================================================================
        // SERIE A (Ý)
        // =========================================================================
        // Inter Milan
        FootballCardEntity(
            id = 46, clubName = "Inter Milan", leagueName = "Serie A", playerName = "Lautaro Martínez",
            position = "ST", ovr = 89, rarity = "GOLD", costCoins = 240, isOwned = false, nationality = "Argentina",
            pace = 82, shooting = 88, passing = 78, dribbling = 86, defending = 48, physical = 84,
            bio = "'El Toro' - Đội trưởng Nerazzurri, vua phá lưới Serie A và cỗ máy chiến đấu quả cảm.", foot = "Phải", weakFoot = 4, skillMoves = 4
        ),
        FootballCardEntity(
            id = 47, clubName = "Inter Milan", leagueName = "Serie A", playerName = "Nicolò Barella",
            position = "CM", ovr = 87, rarity = "GOLD", costCoins = 190, isOwned = false, nationality = "Ý",
            pace = 79, shooting = 78, passing = 84, dribbling = 86, defending = 78, physical = 82,
            bio = "Trái tim hàng tiền vệ đội bóng áo sọc xanh đen, bền bỉ và giàu sáng tạo.", foot = "Phải", weakFoot = 3, skillMoves = 3
        ),
        FootballCardEntity(
            id = 48, clubName = "Inter Milan", leagueName = "Serie A", playerName = "Alessandro Bastoni",
            position = "CB", ovr = 87, rarity = "GOLD", costCoins = 190, isOwned = false, nationality = "Ý",
            pace = 76, shooting = 44, passing = 81, dribbling = 76, defending = 87, physical = 83,
            bio = "Trung vệ lệch trái hiện đại nhất Serie A với cái chân trái phát động cự phách.", foot = "Trái", weakFoot = 3, skillMoves = 3
        ),

        // AC Milan
        FootballCardEntity(
            id = 49, clubName = "AC Milan", leagueName = "Serie A", playerName = "Rafael Leão",
            position = "LW", ovr = 86, rarity = "GOLD", costCoins = 180, isOwned = false, nationality = "Bồ Đào Nha",
            pace = 93, shooting = 81, passing = 78, dribbling = 88, defending = 32, physical = 76,
            bio = "Nụ cười rạng rỡ, bước chạy xé gió và khả năng độc diễn thăng hoa tại San Siro.", foot = "Phải", weakFoot = 4, skillMoves = 5
        ),
        FootballCardEntity(
            id = 50, clubName = "AC Milan", leagueName = "Serie A", playerName = "Theo Hernández",
            position = "LB", ovr = 87, rarity = "GOLD", costCoins = 190, isOwned = false, nationality = "Pháp",
            pace = 95, shooting = 73, passing = 78, dribbling = 84, defending = 80, physical = 88,
            bio = "Đầu tàu tốc độ bên cánh trái, những pha bứt phá từ sân nhà không ai đuổi kịp.", foot = "Trái", weakFoot = 3, skillMoves = 4
        ),
        FootballCardEntity(
            id = 51, clubName = "AC Milan", leagueName = "Serie A", playerName = "Francesco Camarda",
            position = "ST", ovr = 65, rarity = "BRONZE", costCoins = 45, isOwned = false, nationality = "Ý",
            pace = 74, shooting = 66, passing = 55, dribbling = 68, defending = 30, physical = 62,
            bio = "Cầu thủ trẻ nhất lịch sử Serie A, cây săn bàn kỷ lục của Primavera Milan.", foot = "Phải", weakFoot = 3, skillMoves = 3
        ),

        // Juventus, Napoli, AS Roma, Atalanta
        FootballCardEntity(
            id = 52, clubName = "Juventus", leagueName = "Serie A", playerName = "Kenan Yıldız",
            position = "CAM", ovr = 79, rarity = "SILVER", costCoins = 95, isOwned = false, nationality = "Thổ Nhĩ Kỳ",
            pace = 83, shooting = 76, passing = 77, dribbling = 82, defending = 40, physical = 68,
            bio = "Số 10 mới của Bà Đầm Già, người kế thừa chiếc áo huyền thoại của Del Piero.", foot = "Phải", weakFoot = 4, skillMoves = 4
        ),
        FootballCardEntity(
            id = 53, clubName = "Napoli", leagueName = "Serie A", playerName = "Khvicha Kvaratskhelia",
            position = "LW", ovr = 85, rarity = "GOLD", costCoins = 170, isOwned = false, nationality = "Georgia",
            pace = 86, shooting = 82, passing = 82, dribbling = 88, defending = 45, physical = 75,
            bio = "'Kvaradona' - Ảo thuật gia làm mê hoặc người hâm mộ thành Naples.", foot = "Phải", weakFoot = 5, skillMoves = 5
        ),
        FootballCardEntity(
            id = 54, clubName = "AS Roma", leagueName = "Serie A", playerName = "Paulo Dybala",
            position = "CF", ovr = 86, rarity = "GOLD", costCoins = 170, isOwned = false, nationality = "Argentina",
            pace = 80, shooting = 86, passing = 86, dribbling = 90, defending = 40, physical = 62,
            bio = "'La Joya' - Viên ngọc quý thành Rome với cái chân trái ma thuật và mặt nạ ăn mừng biểu tượng.", foot = "Trái", weakFoot = 3, skillMoves = 4
        ),
        FootballCardEntity(
            id = 55, clubName = "Atalanta", leagueName = "Serie A", playerName = "Ademola Lookman",
            position = "ST", ovr = 83, rarity = "GOLD", costCoins = 140, isOwned = false, nationality = "Nigeria",
            pace = 88, shooting = 82, passing = 78, dribbling = 86, defending = 44, physical = 68,
            bio = "Người hùng lập hat-trick chung kết Europa League đưa Atalanta vào lịch sử.", foot = "Phải", weakFoot = 4, skillMoves = 4
        ),

        // =========================================================================
        // BUNDESLIGA (ĐỨC)
        // =========================================================================
        // Bayern Munich
        FootballCardEntity(
            id = 56, clubName = "Bayern Munich", leagueName = "Bundesliga", playerName = "Harry Kane",
            position = "ST", ovr = 90, rarity = "GOLD", costCoins = 300, isOwned = false, nationality = "Anh",
            pace = 72, shooting = 93, passing = 84, dribbling = 83, defending = 48, physical = 83,
            bio = "Cỗ máy dứt điểm thượng thừa, vua phá lưới chiếc giày vàng châu Âu tại Munich.", foot = "Phải", weakFoot = 5, skillMoves = 3
        ),
        FootballCardEntity(
            id = 57, clubName = "Bayern Munich", leagueName = "Bundesliga", playerName = "Harry Kane (TOTW)",
            position = "ST", ovr = 92, rarity = "TOTW", costCoins = 450, isOwned = false, nationality = "Anh",
            pace = 75, shooting = 95, passing = 86, dribbling = 85, defending = 50, physical = 85,
            bio = "Phiên bản In-form với 3 bàn thắng và 2 kiến tạo hủy diệt đối thủ tại Allianz Arena.", foot = "Phải", weakFoot = 5, skillMoves = 3
        ),
        FootballCardEntity(
            id = 58, clubName = "Bayern Munich", leagueName = "Bundesliga", playerName = "Jamal Musiala",
            position = "CAM", ovr = 87, rarity = "GOLD", costCoins = 190, isOwned = false, nationality = "Đức",
            pace = 84, shooting = 82, passing = 82, dribbling = 90, defending = 62, physical = 66,
            bio = "'Bambi' - Đôi chân uyển chuyển như múa ba lê giữa vòng vây đối phương.", foot = "Phải", weakFoot = 4, skillMoves = 5
        ),
        FootballCardEntity(
            id = 59, clubName = "Bayern Munich", leagueName = "Bundesliga", playerName = "Alphonso Davies",
            position = "LB", ovr = 84, rarity = "GOLD", costCoins = 160, isOwned = false, nationality = "Canada",
            pace = 95, shooting = 68, passing = 78, dribbling = 85, defending = 76, physical = 78,
            bio = "'Chiếc xe đua công thức 1' bên hành lang trái xứ Bavaria.", foot = "Trái", weakFoot = 4, skillMoves = 4
        ),
        FootballCardEntity(
            id = 60, clubName = "Bayern Munich", leagueName = "Bundesliga", playerName = "Mathys Tel",
            position = "ST", ovr = 77, rarity = "SILVER", costCoins = 85, isOwned = false, nationality = "Pháp",
            pace = 87, shooting = 77, passing = 69, dribbling = 80, defending = 32, physical = 74,
            bio = "Tiền đạo trẻ đầy năng lượng, siêu dự bị ghi bàn trong những phút bù giờ.", foot = "Phải", weakFoot = 3, skillMoves = 4
        ),

        // Bayer Leverkusen
        FootballCardEntity(
            id = 61, clubName = "Bayer Leverkusen", leagueName = "Bundesliga", playerName = "Florian Wirtz",
            position = "CAM", ovr = 88, rarity = "GOLD", costCoins = 220, isOwned = false, nationality = "Đức",
            pace = 82, shooting = 82, passing = 87, dribbling = 89, defending = 54, physical = 68,
            bio = "Thiên tài kiến thiết làm nên mùa giải bất bại lịch sử của Xabi Alonso.", foot = "Phải", weakFoot = 4, skillMoves = 4
        ),
        FootballCardEntity(
            id = 62, clubName = "Bayer Leverkusen", leagueName = "Bundesliga", playerName = "Granit Xhaka",
            position = "CM", ovr = 86, rarity = "GOLD", costCoins = 170, isOwned = false, nationality = "Thụy Sĩ",
            pace = 62, shooting = 78, passing = 86, dribbling = 76, defending = 80, physical = 82,
            bio = "Tướng lĩnh trung trường quả cảm, thủ lĩnh tinh thần của nhà vô địch Đức.", foot = "Trái", weakFoot = 3, skillMoves = 3
        ),
        FootballCardEntity(
            id = 63, clubName = "Bayer Leverkusen", leagueName = "Bundesliga", playerName = "Jeremie Frimpong",
            position = "RWB", ovr = 84, rarity = "GOLD", costCoins = 160, isOwned = false, nationality = "Hà Lan",
            pace = 94, shooting = 74, passing = 80, dribbling = 85, defending = 74, physical = 72,
            bio = "Mũi khoan phá hủy diệt bên cánh phải với tốc độ khủng khiếp.", foot = "Phải", weakFoot = 3, skillMoves = 3
        ),

        // Borussia Dortmund & RB Leipzig
        FootballCardEntity(
            id = 64, clubName = "Borussia Dortmund", leagueName = "Bundesliga", playerName = "Gregor Kobel",
            position = "GK", ovr = 88, rarity = "GOLD", costCoins = 200, isOwned = false, nationality = "Thụy Sĩ",
            pace = 86, shooting = 85, passing = 74, dribbling = 89, defending = 52, physical = 86,
            bio = "Thành trì vững chắc bảo vệ bức tường vàng Signal Iduna Park.", foot = "Phải", weakFoot = 3, skillMoves = 1
        ),
        FootballCardEntity(
            id = 65, clubName = "RB Leipzig", leagueName = "Bundesliga", playerName = "Xavi Simons",
            position = "CAM", ovr = 84, rarity = "GOLD", costCoins = 150, isOwned = false, nationality = "Hà Lan",
            pace = 86, shooting = 80, passing = 82, dribbling = 87, defending = 54, physical = 72,
            bio = "Năng lượng bùng nổ, sự sáng tạo và kỹ thuật xử lý bóng tốc độ cao.", foot = "Phải", weakFoot = 4, skillMoves = 4
        ),

        // =========================================================================
        // LIGUE 1 (PHÁP)
        // =========================================================================
        // Paris Saint-Germain
        FootballCardEntity(
            id = 66, clubName = "Paris Saint-Germain", leagueName = "Ligue 1", playerName = "Gianluigi Donnarumma",
            position = "GK", ovr = 89, rarity = "GOLD", costCoins = 240, isOwned = false, nationality = "Ý",
            pace = 87, shooting = 84, passing = 78, dribbling = 89, defending = 52, physical = 86,
            bio = "Thủ môn cao lớn với sải tay hộ pháp và phản xạ xuất thần dưới khung gỗ.", foot = "Phải", weakFoot = 3, skillMoves = 1
        ),
        FootballCardEntity(
            id = 67, clubName = "Paris Saint-Germain", leagueName = "Ligue 1", playerName = "Ousmane Dembélé",
            position = "RW", ovr = 86, rarity = "GOLD", costCoins = 180, isOwned = false, nationality = "Pháp",
            pace = 93, shooting = 78, passing = 82, dribbling = 89, defending = 36, physical = 58,
            bio = "Cầu thủ hai chân như một hiếm có, biến hóa khôn lường nơi hành lang cánh.", foot = "Phải", weakFoot = 5, skillMoves = 5
        ),
        FootballCardEntity(
            id = 68, clubName = "Paris Saint-Germain", leagueName = "Ligue 1", playerName = "Achraf Hakimi",
            position = "RB", ovr = 85, rarity = "GOLD", costCoins = 170, isOwned = false, nationality = "Morocco",
            pace = 92, shooting = 76, passing = 80, dribbling = 83, defending = 77, physical = 78,
            bio = "Hậu vệ biên tấn công hàng đầu thế giới với tốc độ và khả năng xuyên phá.", foot = "Phải", weakFoot = 4, skillMoves = 4
        ),
        FootballCardEntity(
            id = 69, clubName = "Paris Saint-Germain", leagueName = "Ligue 1", playerName = "Warren Zaïre-Emery",
            position = "CM", ovr = 80, rarity = "GOLD", costCoins = 120, isOwned = false, nationality = "Pháp",
            pace = 78, shooting = 72, passing = 79, dribbling = 81, defending = 77, physical = 79,
            bio = "Đội trưởng trẻ tuổi tài năng xuất chúng của lò đào tạo PSG.", foot = "Phải", weakFoot = 3, skillMoves = 3
        ),
        FootballCardEntity(
            id = 70, clubName = "Paris Saint-Germain", leagueName = "Ligue 1", playerName = "Senny Mayulu",
            position = "CAM", ovr = 66, rarity = "SILVER", costCoins = 55, isOwned = false, nationality = "Pháp",
            pace = 74, shooting = 62, passing = 67, dribbling = 70, defending = 42, physical = 60,
            bio = "Tài năng trẻ sáng giá được Luis Enrique trao cơ hội tỏa sáng.", foot = "Phải", weakFoot = 3, skillMoves = 3
        ),

        // Marseille, Monaco, Lyon
        FootballCardEntity(
            id = 71, clubName = "Marseille", leagueName = "Ligue 1", playerName = "Mason Greenwood",
            position = "RW", ovr = 82, rarity = "GOLD", costCoins = 130, isOwned = false, nationality = "Anh",
            pace = 86, shooting = 84, passing = 78, dribbling = 83, defending = 40, physical = 68,
            bio = "Khả năng vung chân 2 chân sút bóng cực hiểm hóc và chính xác.", foot = "Trái", weakFoot = 5, skillMoves = 4
        ),
        FootballCardEntity(
            id = 72, clubName = "Monaco", leagueName = "Ligue 1", playerName = "Aleksandr Golovin",
            position = "CAM", ovr = 81, rarity = "GOLD", costCoins = 120, isOwned = false, nationality = "Nga",
            pace = 78, shooting = 78, passing = 83, dribbling = 83, defending = 64, physical = 68,
            bio = "Ngôi sao dẫn dắt lối chơi của đội bóng Công quốc Monaco.", foot = "Phải", weakFoot = 4, skillMoves = 4
        ),
        FootballCardEntity(
            id = 73, clubName = "Lyon", leagueName = "Ligue 1", playerName = "Alexandre Lacazette",
            position = "ST", ovr = 82, rarity = "GOLD", costCoins = 130, isOwned = false, nationality = "Pháp",
            pace = 74, shooting = 83, passing = 75, dribbling = 80, defending = 42, physical = 74,
            bio = "Tướng lĩnh ghi bàn kỳ cựu trở về vực dậy đội bóng quê hương Lyon.", foot = "Phải", weakFoot = 4, skillMoves = 4
        ),

        // =========================================================================
        // HEROES (HUYỀN THOẠI CÂU LẠC BỘ)
        // =========================================================================
        FootballCardEntity(
            id = 74, clubName = "Villarreal & Atletico", leagueName = "Heroes", playerName = "Diego Forlán",
            position = "ST", ovr = 88, rarity = "HERO", costCoins = 350, isOwned = false, nationality = "Uruguay",
            pace = 86, shooting = 90, passing = 80, dribbling = 84, defending = 45, physical = 82,
            bio = "Huyền thoại sút xa với trái bóng Jabulani, Chiếc giày vàng World Cup 2010.", foot = "Phải", weakFoot = 5, skillMoves = 4
        ),
        FootballCardEntity(
            id = 75, clubName = "Newcastle & PSG", leagueName = "Heroes", playerName = "David Ginola",
            position = "LM", ovr = 89, rarity = "HERO", costCoins = 400, isOwned = false, nationality = "Pháp",
            pace = 90, shooting = 88, passing = 87, dribbling = 91, defending = 54, physical = 84,
            bio = "Lãng tử hào hoa với phong cách thi đấu đầy chất nghệ sĩ và thể lực phi phàm.", foot = "Phải", weakFoot = 5, skillMoves = 5
        ),
        FootballCardEntity(
            id = 76, clubName = "Manchester City", leagueName = "Heroes", playerName = "Yaya Touré",
            position = "CDM", ovr = 89, rarity = "HERO", costCoins = 420, isOwned = false, nationality = "Bờ Biển Ngà",
            pace = 82, shooting = 86, passing = 88, dribbling = 86, defending = 84, physical = 91,
            bio = "Quái vật tuyến giữa không thể bị xô đổ, linh hồn kỷ nguyên vàng Man City.", foot = "Phải", weakFoot = 4, skillMoves = 4
        ),
        FootballCardEntity(
            id = 77, clubName = "Juventus", leagueName = "Heroes", playerName = "Claudio Marchisio",
            position = "CM", ovr = 87, rarity = "HERO", costCoins = 340, isOwned = false, nationality = "Ý",
            pace = 81, shooting = 83, passing = 85, dribbling = 84, defending = 82, physical = 82,
            bio = "'Il Principino' - Hoàng tử nhỏ của thành Turin, tiền vệ con thoi mẫu mực.", foot = "Phải", weakFoot = 4, skillMoves = 4
        ),
        FootballCardEntity(
            id = 78, clubName = "Inter Milan & Bayern", leagueName = "Heroes", playerName = "Lúcio",
            position = "CB", ovr = 89, rarity = "HERO", costCoins = 400, isOwned = false, nationality = "Brazil",
            pace = 82, shooting = 68, passing = 74, dribbling = 72, defending = 91, physical = 90,
            bio = "Hòn đá tảng dũng mãnh, trung vệ vô địch World Cup và cú ăn ba thần thánh cùng Inter.", foot = "Phải", weakFoot = 3, skillMoves = 3
        ),
        FootballCardEntity(
            id = 79, clubName = "Udinese", leagueName = "Heroes", playerName = "Antonio Di Natale",
            position = "ST", ovr = 88, rarity = "HERO", costCoins = 360, isOwned = false, nationality = "Ý",
            pace = 89, shooting = 90, passing = 84, dribbling = 90, defending = 45, physical = 68,
            bio = "Huyền thoại trọn đời của Udinese, cỗ máy làm bàn với kỹ thuật dứt điểm siêu việt.", foot = "Phải", weakFoot = 4, skillMoves = 4
        ),
        FootballCardEntity(
            id = 80, clubName = "Lyon", leagueName = "Heroes", playerName = "Sidney Govou",
            position = "RW", ovr = 86, rarity = "HERO", costCoins = 300, isOwned = false, nationality = "Pháp",
            pace = 90, shooting = 84, passing = 80, dribbling = 86, defending = 50, physical = 78,
            bio = "Biểu tượng 7 chức vô địch Ligue 1 liên tiếp thống trị nước Pháp của Lyon.", foot = "Phải", weakFoot = 4, skillMoves = 4
        ),
        FootballCardEntity(
            id = 81, clubName = "Arsenal", leagueName = "Heroes", playerName = "Freddie Ljungberg",
            position = "RM", ovr = 86, rarity = "HERO", costCoins = 300, isOwned = false, nationality = "Thụy Điển",
            pace = 89, shooting = 82, passing = 80, dribbling = 86, defending = 62, physical = 76,
            bio = "Mảnh ghép thế hệ Bất Bại 'The Invincibles 2003-2004' lừng lẫy của Pháo Thủ.", foot = "Phải", weakFoot = 4, skillMoves = 4
        ),
        FootballCardEntity(
            id = 82, clubName = "Al Hilal", leagueName = "Heroes", playerName = "Saeed Al-Owairan",
            position = "CAM", ovr = 88, rarity = "HERO", costCoins = 380, isOwned = false, nationality = "Ả Rập Xê Út",
            pace = 94, shooting = 85, passing = 83, dribbling = 88, defending = 44, physical = 70,
            bio = "Tác giả pha độc diễn solo từ sân nhà tại World Cup 1994 chấn động lịch sử.", foot = "Trái", weakFoot = 4, skillMoves = 4
        ),
        FootballCardEntity(
            id = 83, clubName = "Chelsea & Real Madrid", leagueName = "Heroes", playerName = "Ricardo Carvalho",
            position = "CB", ovr = 88, rarity = "HERO", costCoins = 350, isOwned = false, nationality = "Bồ Đào Nha",
            pace = 81, shooting = 40, passing = 72, dribbling = 72, defending = 90, physical = 84,
            bio = "Trung vệ thông minh, bậc thầy đọc tình huống của triều đại Mourinho.", foot = "Phải", weakFoot = 3, skillMoves = 2
        ),
        FootballCardEntity(
            id = 84, clubName = "Real Madrid", leagueName = "Heroes", playerName = "Fernando Morientes",
            position = "ST", ovr = 87, rarity = "HERO", costCoins = 320, isOwned = false, nationality = "Tây Ban Nha",
            pace = 83, shooting = 89, passing = 76, dribbling = 82, defending = 46, physical = 84,
            bio = "Sát thủ không chiến, người hùng 3 lần vô địch Champions League cùng Kền Kền Trắng.", foot = "Phải", weakFoot = 4, skillMoves = 3
        ),
        FootballCardEntity(
            id = 85, clubName = "Liverpool", leagueName = "Heroes", playerName = "Dirk Kuyt",
            position = "RW", ovr = 86, rarity = "HERO", costCoins = 300, isOwned = false, nationality = "Hà Lan",
            pace = 84, shooting = 84, passing = 81, dribbling = 81, defending = 68, physical = 86,
            bio = "Người hùng không phổi tại Anfield với cú hat-trick lịch sử vào lưới Man United.", foot = "Phải", weakFoot = 3, skillMoves = 3
        ),
        FootballCardEntity(
            id = 86, clubName = "Manchester United", leagueName = "Heroes", playerName = "Park Ji-sung",
            position = "LM", ovr = 87, rarity = "HERO", costCoins = 360, isOwned = false, nationality = "Hàn Quốc",
            pace = 86, shooting = 79, passing = 83, dribbling = 84, defending = 78, physical = 88,
            bio = "'Người ba phổi' vĩ đại của Sir Alex Ferguson, khắc tinh của Andrea Pirlo.", foot = "Phải", weakFoot = 3, skillMoves = 3
        ),
        FootballCardEntity(
            id = 87, clubName = "Inter Milan", leagueName = "Heroes", playerName = "Diego Milito",
            position = "ST", ovr = 88, rarity = "HERO", costCoins = 360, isOwned = false, nationality = "Argentina",
            pace = 84, shooting = 90, passing = 78, dribbling = 86, defending = 45, physical = 80,
            bio = "'Hoàng tử' lập cú đúp lịch sử tại chung kết Champions League 2010 hoàn tất cú ăn ba.", foot = "Phải", weakFoot = 4, skillMoves = 4
        ),
        FootballCardEntity(
            id = 88, clubName = "Inter Milan", leagueName = "Heroes", playerName = "Iván Córdoba",
            position = "CB", ovr = 87, rarity = "HERO", costCoins = 350, isOwned = false, nationality = "Colombia",
            pace = 86, shooting = 40, passing = 65, dribbling = 68, defending = 89, physical = 86,
            bio = "Chiến binh thép với sức bật không tưởng và tốc độ xuất sắc hàng thủ Inter.", foot = "Phải", weakFoot = 3, skillMoves = 2
        ),

        // =========================================================================
        // ICONS (HUYỀN THOẠI THẾ GIỚI - TOP 25 LEGENDS)
        // =========================================================================
        FootballCardEntity(
            id = 89, clubName = "Huyền Thoại Thế Giới", leagueName = "Icons", playerName = "Pelé",
            position = "CAM", ovr = 95, rarity = "ICON", costCoins = 650, isOwned = false, nationality = "Brazil",
            pace = 95, shooting = 95, passing = 92, dribbling = 95, defending = 56, physical = 76,
            bio = "'Vua Bóng Đá' duy nhất 3 lần vô địch World Cup, người mở ra kỷ nguyên bóng đá đẹp.", foot = "Phải", weakFoot = 4, skillMoves = 5
        ),
        FootballCardEntity(
            id = 90, clubName = "Huyền Thoại Thế Giới", leagueName = "Icons", playerName = "Diego Maradona",
            position = "CAM", ovr = 95, rarity = "ICON", costCoins = 650, isOwned = false, nationality = "Argentina",
            pace = 90, shooting = 92, passing = 93, dribbling = 96, defending = 42, physical = 75,
            bio = "'Cậu bé vàng' vĩ đại với 'Bàn tay của Chúa' và bàn thắng thế kỷ tại World Cup 1986.", foot = "Trái", weakFoot = 3, skillMoves = 5
        ),
        FootballCardEntity(
            id = 91, clubName = "Huyền Thoại Thế Giới", leagueName = "Icons", playerName = "Zinedine Zidane",
            position = "CAM", ovr = 95, rarity = "ICON", costCoins = 600, isOwned = false, nationality = "Pháp",
            pace = 84, shooting = 91, passing = 96, dribbling = 95, defending = 75, physical = 84,
            bio = "'Zizou' - Bậc thầy điều khiển trái bóng, cú vô-lê chung kết Champions League 2002.", foot = "Phải", weakFoot = 5, skillMoves = 5
        ),
        FootballCardEntity(
            id = 92, clubName = "Huyền Thoại Thế Giới", leagueName = "Icons", playerName = "Ronaldo Nazário",
            position = "ST", ovr = 94, rarity = "ICON", costCoins = 550, isOwned = false, nationality = "Brazil",
            pace = 96, shooting = 94, passing = 80, dribbling = 94, defending = 44, physical = 80,
            bio = "'Người ngoài hành tinh' R9, cơn ác mộng của mọi thủ môn với kỹ thuật đảo chân ma quỷ.", foot = "Phải", weakFoot = 5, skillMoves = 5
        ),
        FootballCardEntity(
            id = 93, clubName = "Huyền Thoại Thế Giới", leagueName = "Icons", playerName = "Ronaldinho",
            position = "LW", ovr = 94, rarity = "ICON", costCoins = 550, isOwned = false, nationality = "Brazil",
            pace = 92, shooting = 90, passing = 91, dribbling = 96, defending = 40, physical = 80,
            bio = "Phù thủy mang nụ cười vào bóng đá, người từng khiến toàn bộ Santiago Bernabéu đứng dậy vỗ tay.", foot = "Phải", weakFoot = 4, skillMoves = 5
        ),
        FootballCardEntity(
            id = 94, clubName = "Huyền Thoại Thế Giới", leagueName = "Icons", playerName = "Johan Cruyff",
            position = "CF", ovr = 94, rarity = "ICON", costCoins = 540, isOwned = false, nationality = "Hà Lan",
            pace = 91, shooting = 92, passing = 91, dribbling = 94, defending = 44, physical = 74,
            bio = "Bộ óc vĩ đại khai sinh 'Bóng đá tổng lực', cú ngoặt bóng 'Cruyff Turn' bất tử.", foot = "Phải", weakFoot = 5, skillMoves = 5
        ),
        FootballCardEntity(
            id = 95, clubName = "Huyền Thoại Thế Giới", leagueName = "Icons", playerName = "Paolo Maldini",
            position = "CB", ovr = 94, rarity = "ICON", costCoins = 520, isOwned = false, nationality = "Ý",
            pace = 86, shooting = 56, passing = 76, dribbling = 70, defending = 96, physical = 85,
            bio = "Hậu vệ vĩ đại nhất lịch sử, biểu tượng trọn đời trung thành của AC Milan với 5 cúp C1.", foot = "Phải", weakFoot = 4, skillMoves = 2
        ),
        FootballCardEntity(
            id = 96, clubName = "Huyền Thoại Thế Giới", leagueName = "Icons", playerName = "Franz Beckenbauer",
            position = "CB", ovr = 94, rarity = "ICON", costCoins = 520, isOwned = false, nationality = "Đức",
            pace = 84, shooting = 74, passing = 88, dribbling = 85, defending = 95, physical = 86,
            bio = "'Hoàng đế' bóng đá Đức, người định nghĩa hoàn hảo vai trò Libero quét sau hàng thủ.", foot = "Phải", weakFoot = 4, skillMoves = 3
        ),
        FootballCardEntity(
            id = 97, clubName = "Huyền Thoại Thế Giới", leagueName = "Icons", playerName = "Thierry Henry",
            position = "ST", ovr = 93, rarity = "ICON", costCoins = 500, isOwned = false, nationality = "Pháp",
            pace = 94, shooting = 92, passing = 83, dribbling = 90, defending = 54, physical = 80,
            bio = "'Đứa con của thần gió', chân sút huyền thoại số 1 lịch sử Arsenal và tuyển Pháp.", foot = "Phải", weakFoot = 4, skillMoves = 4
        ),
        FootballCardEntity(
            id = 98, clubName = "Huyền Thoại Thế Giới", leagueName = "Icons", playerName = "Ruud Gullit",
            position = "CF", ovr = 93, rarity = "ICON", costCoins = 520, isOwned = false, nationality = "Hà Lan",
            pace = 86, shooting = 88, passing = 88, dribbling = 88, defending = 80, physical = 88,
            bio = "Cầu thủ toàn năng bậc nhất thế giới có thể chơi xuất sắc ở mọi vị trí trên sân.", foot = "Phải", weakFoot = 5, skillMoves = 4
        ),
        FootballCardEntity(
            id = 99, clubName = "Huyền Thoại Thế Giới", leagueName = "Icons", playerName = "Eric Cantona",
            position = "CF", ovr = 93, rarity = "ICON", costCoins = 480, isOwned = false, nationality = "Pháp",
            pace = 85, shooting = 92, passing = 88, dribbling = 89, defending = 55, physical = 88,
            bio = "'King Eric' - Chiếc cổ áo dựng ngược và khí phách vương giả thay đổi lịch sử Man United.", foot = "Phải", weakFoot = 4, skillMoves = 5
        ),
        FootballCardEntity(
            id = 100, clubName = "Huyền Thoại Thế Giới", leagueName = "Icons", playerName = "Steven Gerrard",
            position = "CM", ovr = 92, rarity = "ICON", costCoins = 460, isOwned = false, nationality = "Anh",
            pace = 83, shooting = 90, passing = 90, dribbling = 84, defending = 78, physical = 87,
            bio = "Đội trưởng vĩ đại của Liverpool, người hùng đêm kỳ tích Istanbul 2005.", foot = "Phải", weakFoot = 4, skillMoves = 3
        ),
        FootballCardEntity(
            id = 101, clubName = "Huyền Thoại Thế Giới", leagueName = "Icons", playerName = "Frank Lampard",
            position = "CM", ovr = 92, rarity = "ICON", costCoins = 460, isOwned = false, nationality = "Anh",
            pace = 80, shooting = 92, passing = 88, dribbling = 82, defending = 74, physical = 82,
            bio = "Tiền vệ ghi bàn số một lịch sử Chelsea với những pha băng lên dứt điểm kinh điển.", foot = "Phải", weakFoot = 4, skillMoves = 3
        ),
        FootballCardEntity(
            id = 102, clubName = "Huyền Thoại Thế Giới", leagueName = "Icons", playerName = "Xavi Hernández",
            position = "CM", ovr = 93, rarity = "ICON", costCoins = 480, isOwned = false, nationality = "Tây Ban Nha",
            pace = 78, shooting = 74, passing = 95, dribbling = 91, defending = 72, physical = 70,
            bio = "Bậc thầy chuyền bóng Tiki-taka, trái tim thế hệ vàng Barcelona và La Roja thống trị thế giới.", foot = "Phải", weakFoot = 3, skillMoves = 4
        ),
        FootballCardEntity(
            id = 103, clubName = "Huyền Thoại Thế Giới", leagueName = "Icons", playerName = "Andrés Iniesta",
            position = "CM", ovr = 93, rarity = "ICON", costCoins = 480, isOwned = false, nationality = "Tây Ban Nha",
            pace = 82, shooting = 80, passing = 93, dribbling = 94, defending = 66, physical = 64,
            bio = "Người hùng ghi bàn phút 116 mang cúp vàng World Cup 2010 về cho đất nước Tây Ban Nha.", foot = "Phải", weakFoot = 4, skillMoves = 4
        ),
        FootballCardEntity(
            id = 104, clubName = "Huyền Thoại Thế Giới", leagueName = "Icons", playerName = "Iker Casillas",
            position = "GK", ovr = 92, rarity = "ICON", costCoins = 440, isOwned = false, nationality = "Tây Ban Nha",
            pace = 92, shooting = 85, passing = 72, dribbling = 93, defending = 58, physical = 88,
            bio = "'Thánh Iker' - Thủ môn vĩ đại của Real Madrid với pha cản phá chân trước Robben năm 2010.", foot = "Trái", weakFoot = 2, skillMoves = 1
        ),
        FootballCardEntity(
            id = 105, clubName = "Huyền Thoại Thế Giới", leagueName = "Icons", playerName = "Gianluigi Buffon",
            position = "GK", ovr = 93, rarity = "ICON", costCoins = 460, isOwned = false, nationality = "Ý",
            pace = 91, shooting = 88, passing = 75, dribbling = 92, defending = 60, physical = 90,
            bio = "Huyền thoại bất tử trong khung gỗ của Juventus và đội tuyển Ý vô địch World Cup 2006.", foot = "Phải", weakFoot = 2, skillMoves = 1
        ),
        FootballCardEntity(
            id = 106, clubName = "Huyền Thoại Thế Giới", leagueName = "Icons", playerName = "Andrea Pirlo",
            position = "CM", ovr = 92, rarity = "ICON", costCoins = 450, isOwned = false, nationality = "Ý",
            pace = 74, shooting = 82, passing = 95, dribbling = 89, defending = 70, physical = 68,
            bio = "'Kiến trúc sư' hào hoa với cú sục bóng Panenka và những quả đá phạt vẽ nên cầu vồng.", foot = "Phải", weakFoot = 4, skillMoves = 5
        ),
        FootballCardEntity(
            id = 107, clubName = "Huyền Thoại Thế Giới", leagueName = "Icons", playerName = "Luís Figo",
            position = "RW", ovr = 92, rarity = "ICON", costCoins = 450, isOwned = false, nationality = "Bồ Đào Nha",
            pace = 90, shooting = 86, passing = 89, dribbling = 91, defending = 46, physical = 80,
            bio = "Chủ nhân Quả Bóng Vàng 2000, bậc thầy tạt bóng và rê dắt biên điệu nghệ.", foot = "Phải", weakFoot = 4, skillMoves = 4
        ),
        FootballCardEntity(
            id = 108, clubName = "Huyền Thoại Thế Giới", leagueName = "Icons", playerName = "Roberto Carlos",
            position = "LB", ovr = 92, rarity = "ICON", costCoins = 460, isOwned = false, nationality = "Brazil",
            pace = 93, shooting = 86, passing = 82, dribbling = 82, defending = 84, physical = 88,
            bio = "Cú sút phạt trái chuối hình viên đạn thách thức mọi định luật vật lý.", foot = "Trái", weakFoot = 2, skillMoves = 3
        ),
        FootballCardEntity(
            id = 109, clubName = "Huyền Thoại Thế Giới", leagueName = "Icons", playerName = "Cafu",
            position = "RB", ovr = 93, rarity = "ICON", costCoins = 470, isOwned = false, nationality = "Brazil",
            pace = 92, shooting = 74, passing = 84, dribbling = 86, defending = 89, physical = 88,
            bio = "Đội trưởng duy nhất góp mặt ở 3 trận chung kết World Cup liên tiếp.", foot = "Phải", weakFoot = 3, skillMoves = 3
        ),
        FootballCardEntity(
            id = 110, clubName = "Huyền Thoại Thế Giới", leagueName = "Icons", playerName = "Roberto Baggio",
            position = "CAM", ovr = 93, rarity = "ICON", costCoins = 480, isOwned = false, nationality = "Ý",
            pace = 86, shooting = 88, passing = 90, dribbling = 94, defending = 40, physical = 64,
            bio = "'Đuôi ngựa thần thánh' làm say đắm hàng triệu trái tim người hâm mộ túc cầu.", foot = "Phải", weakFoot = 4, skillMoves = 5
        ),
        FootballCardEntity(
            id = 111, clubName = "Huyền Thoại Thế Giới", leagueName = "Icons", playerName = "Dennis Bergkamp",
            position = "CF", ovr = 92, rarity = "ICON", costCoins = 450, isOwned = false, nationality = "Hà Lan",
            pace = 84, shooting = 88, passing = 87, dribbling = 90, defending = 38, physical = 76,
            bio = "'Người Hà Lan không bay' với bàn thắng quay người kỳ diệu trước Newcastle.", foot = "Phải", weakFoot = 4, skillMoves = 4
        ),
        FootballCardEntity(
            id = 112, clubName = "Huyền Thoại Thế Giới", leagueName = "Icons", playerName = "Wayne Rooney",
            position = "ST", ovr = 92, rarity = "ICON", costCoins = 460, isOwned = false, nationality = "Anh",
            pace = 87, shooting = 91, passing = 84, dribbling = 87, defending = 58, physical = 89,
            bio = "Chân sút số một lịch sử Manchester United, chiến binh rực lửa giàu đam mê.", foot = "Phải", weakFoot = 4, skillMoves = 4
        ),
        FootballCardEntity(
            id = 113, clubName = "Huyền Thoại Thế Giới", leagueName = "Icons", playerName = "Paul Scholes",
            position = "CM", ovr = 92, rarity = "ICON", costCoins = 450, isOwned = false, nationality = "Anh",
            pace = 75, shooting = 90, passing = 94, dribbling = 82, defending = 70, physical = 82,
            bio = "Bậc thầy chuyền dài chuẩn từng tấc đất được cả Zidane và Xavi ngưỡng mộ.", foot = "Phải", weakFoot = 3, skillMoves = 3
        )
    )
}
