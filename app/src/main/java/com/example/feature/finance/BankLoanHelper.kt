package com.example.feature.finance

import androidx.compose.ui.graphics.Color
import com.example.data.entity.BankLoanEntity
import java.util.Calendar

object BankLoanHelper {

    val POPULAR_BANKS = listOf(
        "Vietcombank",
        "Techcombank",
        "MB Bank",
        "BIDV",
        "VPBank",
        "TPBank",
        "ACB",
        "Shinhan Bank",
        "Agribank",
        "VIB",
        "Sacombank",
        "Khác"
    )

    val LOAN_PURPOSES = listOf(
        "Vay mua nhà căn hộ",
        "Vay mua ô tô",
        "Vay tiêu dùng / Tín chấp",
        "Thẻ tín dụng",
        "Vay kinh doanh",
        "Vay thấu chi",
        "Vay học tập / Khác"
    )

    fun getBankColor(bankName: String): Color {
        val lower = bankName.lowercase()
        return when {
            "vietcom" in lower || "vcb" in lower -> Color(0xFF008853)
            "techcom" in lower || "tcb" in lower -> Color(0xFFE31837)
            "mb" in lower || "quân đội" in lower -> Color(0xFF0033A0)
            "bidv" in lower -> Color(0xFF00707B)
            "vp" in lower -> Color(0xFF00B14F)
            "tp" in lower || "tiên phong" in lower -> Color(0xFF682C91)
            "acb" in lower -> Color(0xFF005696)
            "shinhan" in lower -> Color(0xFF004687)
            "agri" in lower -> Color(0xFF8B1B1B)
            "vib" in lower -> Color(0xFF004F9F)
            "sacom" in lower -> Color(0xFF005AAB)
            else -> Color(0xFF4F46E5)
        }
    }

    fun getBankInitials(bankName: String): String {
        val upper = bankName.uppercase().trim()
        return when {
            "VIETCOM" in upper -> "VCB"
            "TECHCOM" in upper -> "TCB"
            "MB" in upper -> "MB"
            "BIDV" in upper -> "BIDV"
            "VP" in upper -> "VPB"
            "TP" in upper -> "TPB"
            "ACB" in upper -> "ACB"
            "SHINHAN" in upper -> "SHB"
            "AGRI" in upper -> "VBA"
            "VIB" in upper -> "VIB"
            "SACOM" in upper -> "STB"
            upper.length <= 4 -> upper
            else -> upper.split(" ").filter { it.isNotEmpty() }.take(3).map { it.first() }.joinToString("")
        }
    }

    /**
     * Calculates the next monthly payment date and days remaining until due date.
     * Returns Pair(formattedDateString "dd/MM/yyyy", daysRemaining).
     */
    fun calculateNextDueDate(loan: BankLoanEntity): Pair<String, Int> {
        if (loan.isPaidOff || loan.remainingAmount <= 0) {
            return Pair("Đã tất toán", 9999)
        }

        val todayCal = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }

        val currentDay = todayCal.get(Calendar.DAY_OF_MONTH)
        val currentMonth = todayCal.get(Calendar.MONTH)
        val currentYear = todayCal.get(Calendar.YEAR)

        val targetCal = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }

        val maxDaysThisMonth = targetCal.getActualMaximum(Calendar.DAY_OF_MONTH)
        val clampedDayThisMonth = minOf(loan.duePaymentDay, maxDaysThisMonth)

        if (currentDay <= clampedDayThisMonth) {
            // Due date is in current month
            targetCal.set(Calendar.DAY_OF_MONTH, clampedDayThisMonth)
        } else {
            // Due date is in next month
            targetCal.add(Calendar.MONTH, 1)
            val maxDaysNextMonth = targetCal.getActualMaximum(Calendar.DAY_OF_MONTH)
            targetCal.set(Calendar.DAY_OF_MONTH, minOf(loan.duePaymentDay, maxDaysNextMonth))
        }

        val diffMillis = targetCal.timeInMillis - todayCal.timeInMillis
        val daysRemaining = (diffMillis / (24 * 3600 * 1000L)).toInt()

        val formattedDate = String.format(
            "%02d/%02d/%04d",
            targetCal.get(Calendar.DAY_OF_MONTH),
            targetCal.get(Calendar.MONTH) + 1,
            targetCal.get(Calendar.YEAR)
        )

        return Pair(formattedDate, daysRemaining)
    }

    fun calculateMonthlyEstimatedPayment(
        originalAmount: Long,
        remainingAmount: Long,
        interestRatePercent: Float,
        termMonths: Int,
        interestMethod: String
    ): Long {
        if (termMonths <= 0 || originalAmount <= 0) return 0L
        val principalPerMonth = originalAmount / termMonths.toLong()
        val baseForInterest = if (interestMethod == "REDUCING_BALANCE") remainingAmount else originalAmount
        val monthlyInterest = (baseForInterest * (interestRatePercent / 100.0 / 12.0)).toLong()
        return principalPerMonth + monthlyInterest
    }

    fun isDueSoon(loan: BankLoanEntity): Boolean {
        if (!loan.isReminderEnabled || loan.isPaidOff || loan.remainingAmount <= 0) return false
        val days = calculateNextDueDate(loan).second
        return days in 0..loan.reminderDaysBefore
    }
}
