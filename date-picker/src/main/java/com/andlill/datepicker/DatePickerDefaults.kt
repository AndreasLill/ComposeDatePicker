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
        icon: Color = MaterialTheme.colorScheme.onSurface,
        divider: Color = DividerDefaults.color,
        calendarYearPicker: Color = MaterialTheme.colorScheme.onSurface,
        calendarWeekText: Color = MaterialTheme.colorScheme.onSurface,
        calendarText: Color = MaterialTheme.colorScheme.onSurface,
        calendarSelectedBackground: Color = MaterialTheme.colorScheme.primary,
        calendarSelectedText: Color = MaterialTheme.colorScheme.onPrimary,
        textFieldColors: TextFieldColors = TextFieldDefaults.outlinedTextFieldColors(),
        negativeButton: ButtonColors = ButtonDefaults.textButtonColors(),
        positiveButton: ButtonColors = ButtonDefaults.textButtonColors(),
    ): DatePickerColors = DatePickerColors(
        background = background,
        title = title,
        icon = icon,
        divider = divider,
        calendarYearPicker = calendarYearPicker,
        calendarWeekText = calendarWeekText,
        calendarText = calendarText,
        calendarSelectedBackground = calendarSelectedBackground,
        calendarSelectedText = calendarSelectedText,
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
    val icon: Color,
    val divider: Color,
    val calendarYearPicker: Color,
    val calendarWeekText: Color,
    val calendarText: Color,
    val calendarSelectedBackground: Color,
    val calendarSelectedText: Color,
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