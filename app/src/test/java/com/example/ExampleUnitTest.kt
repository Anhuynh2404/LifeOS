package com.example

import com.example.core.util.Formatters
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class ExampleUnitTest {
    @Test
    fun testVietnameseVndFormatting() {
        val formatted = Formatters.formatVnd(1250000L)
        assertEquals("1.250.000đ", formatted)
    }

    @Test
    fun testHourMinuteFormatting() {
        val time = Formatters.formatHourMinute(6, 30)
        assertEquals("06:30", time)
    }

    @Test
    fun testGreetingNotEmpty() {
        val greeting = Formatters.getGreeting()
        assertTrue(greeting.isNotBlank())
    }
}
