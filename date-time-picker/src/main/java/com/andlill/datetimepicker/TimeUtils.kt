package com.andlill.datetimepicker

import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter

object TimeUtils {

    fun LocalDate.toDateString(pattern: String): String {
        val formatter = DateTimeFormatter.ofPattern(pattern)
        return this.format(formatter)
    }

    fun LocalTime.toTimeString(pattern: String): String {
        val formatter = DateTimeFormatter.ofPattern(pattern)
        return this.format(formatter)
    }

    fun LocalDate.getDatePickerMonthInfo(): Pair<Int, Int> {
        val numberOfDays = this.month.length(this.isLeapYear)
        val firstDay = this.withDayOfMonth(1).dayOfWeek.value - 1
        return Pair(firstDay, numberOfDays)
    }
}