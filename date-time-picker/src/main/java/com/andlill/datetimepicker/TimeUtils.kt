package com.andlill.datetimepicker

import java.time.DayOfWeek
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.time.temporal.WeekFields
import java.util.*

object TimeUtils {

    fun LocalDate.toDateString(pattern: String, locale: Locale): String {
        val formatter = DateTimeFormatter.ofPattern(pattern).withLocale(locale)
        return this.format(formatter)
    }

    fun LocalTime.toTimeString(pattern: String, locale: Locale): String {
        val formatter = DateTimeFormatter.ofPattern(pattern).withLocale(locale)
        return this.format(formatter)
    }

    fun LocalDate.getDatePickerMonthInfo(locale: Locale): Pair<Int, Int> {
        val numberOfDays = this.month.length(this.isLeapYear)
        var firstDay = this.withDayOfMonth(1).dayOfWeek.value

        // Add a day if first day of week is a Sunday.
        if (WeekFields.of(locale).firstDayOfWeek == DayOfWeek.MONDAY)
            firstDay--

        return Pair(firstDay, numberOfDays)
    }
}