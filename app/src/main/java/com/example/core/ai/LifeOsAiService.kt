package com.example.core.ai

import com.google.firebase.Firebase
import com.google.firebase.ai.ai
import com.google.firebase.ai.type.generationConfig
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.json.JSONObject

data class GeneratedTaskPlan(
    val title: String,
    val description: String,
    val difficulty: String,
    val estimatedMinutes: Int,
    val energyTag: String,
    val suggestedHour: Int
)

data class ParsedReceipt(
    val storeName: String,
    val totalAmount: Long,
    val dateStr: String,
    val itemsSummary: String
)

object LifeOsAiService {

    suspend fun breakdownGoal(goalTitle: String): List<GeneratedTaskPlan> = withContext(Dispatchers.IO) {
        try {
            val model = Firebase.ai.generativeModel(
                modelName = "gemini-2.5-flash",
                generationConfig = generationConfig {
                    responseMimeType = "application/json"
                }
            )
            val prompt = """
                Bạn là chuyên gia huấn luyện LifeOS. Hãy phân rã mục tiêu sau thành 4 task hành động cụ thể để đưa vào lịch biểu:
                Mục tiêu: "$goalTitle"
                Trả về JSON Array với format:
                [
                  {
                    "title": "Tên nhiệm vụ ngắn gọn",
                    "description": "Chi tiết hành động",
                    "difficulty": "EASY" | "MEDIUM" | "HARD",
                    "estimatedMinutes": 30,
                    "energyTag": "HIGH_FOCUS" | "CREATIVE" | "ROUTINE" | "RECOVERY",
                    "suggestedHour": 9
                  }
                ]
            """.trimIndent()

            val response = model.generateContent(prompt)
            val text = response.text ?: ""
            val jsonArray = JSONArray(text.trim())
            val list = mutableListOf<GeneratedTaskPlan>()
            for (i in 0 until jsonArray.length()) {
                val obj = jsonArray.getJSONObject(i)
                list.add(
                    GeneratedTaskPlan(
                        title = obj.getString("title"),
                        description = obj.optString("description", ""),
                        difficulty = obj.optString("difficulty", "MEDIUM"),
                        estimatedMinutes = obj.optInt("estimatedMinutes", 45),
                        energyTag = obj.optString("energyTag", "HIGH_FOCUS"),
                        suggestedHour = obj.optInt("suggestedHour", 9 + i * 2)
                    )
                )
            }
            if (list.isNotEmpty()) return@withContext list
        } catch (_: Exception) {
            // Graceful fallback to rich local rule-based heuristic templates
        }

        // Offline / Fallback heuristic breakdown
        fallbackGoalBreakdown(goalTitle)
    }

    private fun fallbackGoalBreakdown(goalTitle: String): List<GeneratedTaskPlan> {
        val lower = goalTitle.lowercase()
        return when {
            lower.contains("chạy") || lower.contains("marathon") || lower.contains("thể thao") -> listOf(
                GeneratedTaskPlan("Kiểm tra chỉ số VO2Max & khởi động nhẹ", "Đo nhịp tim nghỉ ngơi và chạy bộ 3km Zone 2", "EASY", 30, "RECOVERY", 6),
                GeneratedTaskPlan("Chạy biến tốc Interval 400m x 6 hiệp", "Nâng cao ngưỡng chịu đựng axit lactic và sức bền", "HARD", 45, "HIGH_FOCUS", 6),
                GeneratedTaskPlan("Bài tập sức mạnh thân dưới & cơ lõi (Core)", "Plank, Squat và Lunge giúp chống chấn thương khớp", "MEDIUM", 40, "ROUTINE", 17),
                GeneratedTaskPlan("Chạy dài tích lũy Long Run cuối tuần 12km", "Bổ sung điện giải và giữ tốc độ ổn định", "HARD", 75, "HIGH_FOCUS", 6)
            )
            lower.contains("ielts") || lower.contains("tiếng anh") || lower.contains("ngoại ngữ") -> listOf(
                GeneratedTaskPlan("Luyện nghe chép chính tả TED Talks / IELTS Cam", "Nghe Section 3 & ghi lại 15 collocation học thuật", "MEDIUM", 45, "HIGH_FOCUS", 9),
                GeneratedTaskPlan("Phân tích bài đọc Academic & giải đề Reading", "Rèn kỹ năng Skimming & Scanning trong 20 phút", "HARD", 50, "HIGH_FOCUS", 10),
                GeneratedTaskPlan("Luyện nói Speaking Part 2 trước gương", "Ghi âm bài nói chủ đề Technology & phân tích lỗi phát âm", "MEDIUM", 30, "CREATIVE", 14),
                GeneratedTaskPlan("Viết Task 2 Essay & sửa lỗi ngữ pháp", "Viết bài luận 250 từ dạng Opinion và trau chuốt từ vựng", "HARD", 60, "HIGH_FOCUS", 15)
            )
            lower.contains("tiền") || lower.contains("tài chính") || lower.contains("tiết kiệm") -> listOf(
                GeneratedTaskPlan("Rà soát các khoản đăng ký định kỳ (Sub)", "Hủy các dịch vụ không còn sử dụng để giảm rò rỉ tiền", "EASY", 20, "ROUTINE", 11),
                GeneratedTaskPlan("Lập bảng cân đối thu chi & phân bổ 50/30/20", "Phân bổ dòng tiền vào Quỹ Dự phòng và Quỹ Đầu tư", "MEDIUM", 40, "HIGH_FOCUS", 14),
                GeneratedTaskPlan("Nghiên cứu danh mục quỹ chỉ số ETF uy tín", "Đọc báo cáo thường niên và phân tích chi phí quản lý", "HARD", 60, "HIGH_FOCUS", 9),
                GeneratedTaskPlan("Thiết lập lệnh trích tiền tự động ngày nhận lương", "Chuyển tiền tự động vào tài khoản tích lũy sinh lời", "EASY", 15, "ROUTINE", 16)
            )
            else -> listOf(
                GeneratedTaskPlan("Nghiên cứu kiến thức nền tảng & lập lộ trình", "Tổng hợp 3 tài liệu uy tín nhất về $goalTitle", "MEDIUM", 45, "HIGH_FOCUS", 9),
                GeneratedTaskPlan("Thực hành bước đi đầu tiên quan trọng", "Dành 1 giờ tập trung cao độ không xao nhãng", "HARD", 60, "HIGH_FOCUS", 10),
                GeneratedTaskPlan("Rà soát tiến độ & đúc kết bài học", "Đánh giá kết quả đạt được và ghi chép kinh nghiệm", "EASY", 25, "ROUTINE", 16),
                GeneratedTaskPlan("Luyện tập nâng cao và củng cố thói quen", "Thực hiện phiên thứ hai với độ khó tăng dần", "MEDIUM", 40, "CREATIVE", 14)
            )
        }
    }

    fun explainEnergyRecommendation(taskTitle: String, difficulty: String, slotHour: Int, profile: String): String {
        return when (difficulty) {
            "HARD" -> "Khung giờ ${slotHour}:00 là thời điểm $profile của bạn. Lượng hoóc-môn cortisol và độ tập trung não bộ đang ở mức tối ưu, giúp xử lý '$taskTitle' nhanh hơn 40% mà không bị mệt mỏi."
            "MEDIUM" -> "Khung giờ ${slotHour}:00 có mức năng lượng vừa phải, thích hợp cho việc giải quyết '$taskTitle' một cách nhịp nhàng và hiệu quả."
            else -> "Khung giờ ${slotHour}:00 lý tưởng cho các việc nhẹ nhàng như '$taskTitle', giúp bạn duy trì đà hoàn thành mà không tiêu tốn năng lượng sâu."
        }
    }

    fun parseReceiptSimulation(simulatedText: String = ""): ParsedReceipt {
        val total = if (simulatedText.contains("WinMart", ignoreCase = true)) 245000L
        else if (simulatedText.contains("Highland", ignoreCase = true)) 125000L
        else 185000L

        val store = if (simulatedText.contains("Highland", ignoreCase = true)) "Highlands Coffee"
        else if (simulatedText.contains("WinMart", ignoreCase = true)) "Siêu thị WinMart+"
        else "Nhà thuốc An Khang"

        return ParsedReceipt(
            storeName = store,
            totalAmount = total,
            dateStr = "Hôm nay",
            itemsSummary = "Hóa đơn mua sắm tiêu dùng thiết yếu"
        )
    }
}
