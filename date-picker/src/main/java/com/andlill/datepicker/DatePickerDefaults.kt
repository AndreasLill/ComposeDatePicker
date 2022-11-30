package com.andlill.datepicker

import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.ui.graphics.Color

object DatePickerDefaults {

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    fun colors(
        background: Color = MaterialTheme.colorScheme.surface,
        title: Color = MaterialTheme.colorScheme.onSurface,
        editIcon: Color = MaterialTheme.colorScheme.onSurface,
        divider: Color = DividerDefaults.color,
        yearPickerTitle: Color = MaterialTheme.colorScheme.onSurface,
        yearPickerText: Color = MaterialTheme.colorScheme.onSurface,
        yearPickerSelectedBackground: Color = MaterialTheme.colorScheme.primary,
        yearPickerSelectedText: Color = MaterialTheme.colorScheme.onPrimary,
        prevIcon: Color = MaterialTheme.colorScheme.onSurface,
        nextIcon: Color = MaterialTheme.colorScheme.onSurface,
        calendarWeekText: Color = MaterialTheme.colorScheme.onSurface,
        calendarDayText: Color = MaterialTheme.colorScheme.onSurface,
        calendarDaySelectedBackground: Color = MaterialTheme.colorScheme.primary,
        calendarDaySelectedText: Color = MaterialTheme.colorScheme.onPrimary,
        calendarDayTodayText: Color = MaterialTheme.colorScheme.primary,
        calendarDayTodayBorder: Color = MaterialTheme.colorScheme.primary,
        textFieldColors: TextFieldColors = TextFieldDefaults.outlinedTextFieldColors(),
        negativeButton: ButtonColors = ButtonDefaults.textButtonColors(),
        positiveButton: ButtonColors = ButtonDefaults.textButtonColors(),
    ): DatePickerColors = DatePickerColors(
        background = background,
        title = title,
        editIcon = editIcon,
        divider = divider,
        yearPickerTitle = yearPickerTitle,
        yearPickerText = yearPickerText,
        yearPickerSelectedBackground = yearPickerSelectedBackground,
        yearPickerSelectedText = yearPickerSelectedText,
        prevIcon = prevIcon,
        nextIcon = nextIcon,
        calendarWeekText = calendarWeekText,
        calendarDayText = calendarDayText,
        calendarDaySelectedBackground = calendarDaySelectedBackground,
        calendarDaySelectedText = calendarDaySelectedText,
        calendarDayTodayText = calendarDayTodayText,
        calendarDayTodayBorder = calendarDayTodayBorder,
        textFieldColors = textFieldColors,
        negativeButton = negativeButton,
        positiveButton = positiveButton,
    )

    @Composable
    fun strings(
        titleDatePattern: String = "EEE, MMM d",
        yearPickerDatePattern: String = "MMMM yyyy",
        textFieldDatePattern: String = "yyyy-MM-dd",
        textFieldLabelText: String = "Date",
        textFieldErrorText: String = "Invalid format.\nUse: yyyy-mm-dd",
        negativeButtonText: String = "Cancel",
        positiveButtonText: String = "OK",
    ): DatePickerStrings = DatePickerStrings(
        titleDatePattern = titleDatePattern,
        yearPickerDatePattern = yearPickerDatePattern,
        textFieldDatePattern = textFieldDatePattern,
        textFieldLabelText = textFieldLabelText,
        textFieldErrorText = textFieldErrorText,
        negativeButtonText = negativeButtonText,
        positiveButtonText = positiveButtonText,
    )
}

@Immutable
class DatePickerColors internal constructor(
    val background: Color,
    val title: Color,
    val editIcon: Color,
    val divider: Color,
    val yearPickerTitle: Color,
    val yearPickerText: Color,
    val yearPickerSelectedBackground: Color,
    val yearPickerSelectedText: Color,
    val prevIcon: Color,
    val nextIcon: Color,
    val calendarWeekText: Color,
    val calendarDayText: Color,
    val calendarDaySelectedBackground: Color,
    val calendarDaySelectedText: Color,
    val calendarDayTodayText: Color,
    val calendarDayTodayBorder: Color,
    val textFieldColors: TextFieldColors,
    val negativeButton: ButtonColors,
    val positiveButton: ButtonColors,
)

class DatePickerStrings internal constructor(
    val titleDatePattern: String,
    val yearPickerDatePattern: String,
    val textFieldDatePattern: String,
    val textFieldLabelText: String,
    val textFieldErrorText: String,
    val negativeButtonText: String,
    val positiveButtonText: String,
)