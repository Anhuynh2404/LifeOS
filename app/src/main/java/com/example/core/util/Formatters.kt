package com.example.core.util

import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object Formatters {
    fun formatVnd(amount: Long): String {
        val symbols = DecimalFormatSymbols(Locale.GERMAN).apply {
            groupingSeparator = '.'
        }
        val formatter = DecimalFormat("#,###", symbols)
        return "${formatter.format(amount)}đ"
    }

    fun formatHourMinute(hour: Int, minute: Int): String {
        return String.format("%02d:%02d", hour, minute)
    }

    fun formatCurrentDateVietnamese(): String {
        val sdf = SimpleDateFormat("EEEE, 'ngày' dd 'tháng' MM", Locale("vi", "VN"))
        val formatted = sdf.format(Date())
        return formatted.replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale("vi", "VN")) else it.toString() }
    }

    fun getGreeting(): String {
        val hour = java.util.Calendar.getInstance().get(java.util.Calendar.HOUR_OF_DAY)
        return when (hour) {
            in 4..10 -> "Chào buổi sáng rực rỡ"
            in 11..13 -> "Chào buổi trưa an lành"
            in 14..17 -> "Chào buổi chiều tập trung"
            else -> "Chào buổi tối thư thái"
        }
    }
}
