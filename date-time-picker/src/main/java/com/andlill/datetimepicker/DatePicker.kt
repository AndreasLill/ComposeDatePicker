package com.andlill.datetimepicker

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.ArrowDropUp
import androidx.compose.material.icons.filled.NavigateBefore
import androidx.compose.material.icons.filled.NavigateNext
import androidx.compose.material.icons.outlined.CalendarToday
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.andlill.datetimepicker.TimeUtils.getDatePickerMonthInfo
import com.andlill.datetimepicker.TimeUtils.toDateString
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.PagerState
import com.google.accompanist.pager.rememberPagerState
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException
import java.time.format.TextStyle
import java.time.temporal.WeekFields
import java.util.Locale

@Composable
fun DatePickerDialog(
    state: MutableState<Boolean>,
    yearRange: IntRange = IntRange(1900, 2100),
    initialDate: LocalDate = LocalDate.now(),
    locale: Locale = Locale.getDefault(),
    titleDatePattern: String = "EEE, MMM d",
    yearPickerDatePattern: String = "MMMM yyyy",
    textFieldDatePattern: String = "yyyy-MM-dd",
    textFieldLabelText: String = "Date",
    textFieldErrorText: String = "Invalid format.\nUse: yyyy-mm-dd",
    onSelectDate: (LocalDate) -> Unit
) {
    if (state.value) {

        var dateSelected by remember { mutableStateOf(initialDate) }
        var showPicker by remember { mutableStateOf(true) }

        Dialog(
            onDismissRequest = { state.value = false },
            content = {
                Surface(
                    modifier = Modifier
                        .wrapContentSize()
                        .animateContentSize(),
                    color = MaterialTheme.colorScheme.surface,
                    shape = RoundedCornerShape(28.dp)
                ) {
                    Column(modifier = Modifier.padding(24.dp)) {
                        Box(modifier = Modifier.fillMaxWidth()) {
                            Text(
                                modifier = Modifier
                                    .padding(start = 8.dp)
                                    .align(Alignment.CenterStart),
                                text = dateSelected.toDateString(titleDatePattern, locale),
                                fontSize = 22.sp,
                                fontWeight = FontWeight.Normal,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            IconButton(
                                modifier = Modifier.align(Alignment.CenterEnd),
                                onClick = {
                                    showPicker = !showPicker
                                },
                                content = {
                                    Icon(
                                        imageVector = if (showPicker) Icons.Outlined.Edit else Icons.Outlined.CalendarToday,
                                        contentDescription = null,
                                        tint = MaterialTheme.colorScheme.onSurface.copy(0.8f)
                                    )
                                }
                            )
                        }
                        Divider()
                        Column(modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 8.dp)) {
                            if (showPicker) {
                                DatePickerCalendar(
                                    dateSelected = dateSelected,
                                    locale = locale,
                                    yearPickerDatePattern = yearPickerDatePattern,
                                    yearRange = yearRange,
                                    onSelectDate = {
                                        dateSelected = it
                                    }
                                )
                            }
                            else {
                                DatePickerTextField(
                                    dateSelected = dateSelected,
                                    locale = locale,
                                    datePattern = textFieldDatePattern,
                                    labelText = textFieldLabelText,
                                    errorText = textFieldErrorText,
                                    onSelectDate = {
                                        dateSelected = it
                                    }
                                )
                            }
                        }
                        Box(modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 16.dp)) {
                            Row(modifier = Modifier.align(Alignment.CenterEnd)) {
                                TextButton(
                                    onClick = {
                                        state.value = false
                                    },
                                    content = {
                                        Text(text = "Cancel")
                                    }
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                TextButton(
                                    onClick = {
                                        onSelectDate(dateSelected)
                                        state.value = false
                                    },
                                    content = {
                                        Text(text = "OK")
                                    }
                                )
                            }
                        }
                    }
                }
            }
        )
    }
}

@OptIn(ExperimentalPagerApi::class)
@Composable
internal fun DatePickerCalendar(
    dateSelected: LocalDate,
    locale: Locale,
    yearPickerDatePattern: String,
    yearRange: IntRange,
    onSelectDate: (LocalDate) -> Unit
) {
    var showYearPicker by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()
    val pagerState = rememberPagerState(
        initialPage = (dateSelected.year - yearRange.first) * 12 + dateSelected.monthValue - 1
    )
    val pageCount = remember {
        (yearRange.last - yearRange.first + 1) * 12
    }
    val dateViewed = remember(pagerState.currentPage) {
        LocalDate.of(
            yearRange.first + pagerState.currentPage / 12,
            pagerState.currentPage % 12 + 1,
            1
        )
    }

    DatePickerCalendarHeader(
        dateViewed = dateViewed,
        locale = locale,
        datePattern = yearPickerDatePattern,
        isShowYearPicker = showYearPicker,
        onPrevious = {
            scope.launch {
                if (pagerState.currentPage > 0)
                    pagerState.animateScrollToPage(pagerState.currentPage - 1)
            }
        },
        onNext = {
            scope.launch {
                if (pagerState.currentPage <= pagerState.pageCount)
                    pagerState.animateScrollToPage(pagerState.currentPage + 1)
            }
        },
        onToggleYearPicker = {
            showYearPicker = !showYearPicker
        }
    )
    if (showYearPicker) {
        DatePickerCalendarYearPicker(
            yearRange = yearRange,
            dateSelected = dateSelected,
            onSelectYear = { year ->
                showYearPicker = false
                scope.launch {
                    pagerState.scrollToPage((year - yearRange.first) * 12 + dateSelected.monthValue - 1)
                }
            }
        )
    }
    else {
        DatePickerCalendarBody(
            state = pagerState,
            pageCount = pageCount,
            startYear = yearRange.first,
            dateSelected = dateSelected,
            locale = locale,
            onClick = onSelectDate
        )
    }
}

@Composable
internal fun DatePickerCalendarHeader(
    dateViewed: LocalDate,
    locale: Locale,
    datePattern: String,
    isShowYearPicker: Boolean,
    onPrevious: () -> Unit,
    onNext: () -> Unit,
    onToggleYearPicker: () -> Unit
) {
    Box(modifier = Modifier.fillMaxWidth()) {
        Row(modifier = Modifier.align(Alignment.CenterStart), verticalAlignment = Alignment.CenterVertically) {
            Button(
                colors = ButtonDefaults.textButtonColors(),
                onClick = onToggleYearPicker
            ) {
                Text(
                    text = dateViewed.toDateString(datePattern, locale),
                    fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.width(4.dp))
                Icon(
                    modifier = Modifier.size(20.dp),
                    imageVector = if (isShowYearPicker) Icons.Filled.ArrowDropUp else Icons.Filled.ArrowDropDown,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSurface
                )
            }
        }
        Row(modifier = Modifier.align(Alignment.CenterEnd)) {
            IconButton(
                enabled = !isShowYearPicker,
                onClick = onPrevious,
                content = {
                    Icon(
                        imageVector = Icons.Filled.NavigateBefore,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onSurface.copy(if (isShowYearPicker) 0.5f else 1f)
                    )
                }
            )
            IconButton(
                enabled = !isShowYearPicker,
                onClick = onNext,
                content = {
                    Icon(
                        imageVector = Icons.Filled.NavigateNext,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onSurface.copy(if (isShowYearPicker) 0.5f else 1f)
                    )
                }
            )
        }
    }
}

@Composable
internal fun DatePickerCalendarYearPicker(
    yearRange: IntRange,
    dateSelected: LocalDate,
    onSelectYear: (Int) -> Unit
) {
    val state = rememberLazyGridState(
        initialFirstVisibleItemIndex = dateSelected.year - yearRange.first
    )
    LazyVerticalGrid(
        state = state,
        columns = GridCells.Fixed(3),
        contentPadding = PaddingValues(top = 16.dp, bottom = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        content = {
            items(count = yearRange.last - yearRange.first + 1) { item ->
                val value = remember(item) {
                    (item + yearRange.first)
                }
                YearPickerItem(
                    selected = (value == dateSelected.year),
                    text = value.toString(),
                    onClick = {
                        onSelectYear(value)
                    }
                )
            }
        }
    )
}

@OptIn(ExperimentalPagerApi::class)
@Composable
internal fun DatePickerCalendarBody(
    state: PagerState,
    pageCount: Int,
    startYear: Int,
    dateSelected: LocalDate,
    locale: Locale,
    onClick: (LocalDate) -> Unit
) {
    val dateNow = remember { LocalDate.now() }

    Column(modifier = Modifier.fillMaxWidth()) {
        Row(modifier = Modifier.fillMaxWidth()) {
            (0..6).forEach { day ->
                Box(
                    modifier = Modifier
                        .weight(1F)
                        .size(40.dp),
                    contentAlignment = Alignment.Center) {
                    Text(
                        text = WeekFields.of(locale).firstDayOfWeek.plus(day.toLong()).getDisplayName(TextStyle.NARROW, locale),
                        fontSize = 13.sp,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
            }
        }
        HorizontalPager(state = state, count = pageCount) { page ->
            val pageDate = remember(page) {
                LocalDate.of(
                    startYear + page / 12,
                    page % 12 + 1,
                    1
                )
            }
            val monthInfo = remember(pageDate) {
                pageDate.getDatePickerMonthInfo(locale)
            }
            LazyVerticalGrid(
                modifier = Modifier.height(240.dp),
                userScrollEnabled = false,
                columns = GridCells.Fixed(7),
                content = {
                    // Maximum amount of "dates" shown is 37.
                    // Largest count is starting on final day of week and 31 days. (ex. Jan 2023)
                    items(count = 37) { item ->
                        if (item >= monthInfo.first && (item - monthInfo.first) < monthInfo.second) {
                            DatePickerItem(
                                enabled = true,
                                selected = (pageDate.year == dateSelected.year && pageDate.month == dateSelected.month && dateSelected.dayOfMonth == (item + 1 - monthInfo.first)),
                                today = (pageDate.year == dateNow.year && pageDate.month == dateNow.month && dateNow.dayOfMonth == (item + 1 - monthInfo.first)),
                                text = (item + 1 - monthInfo.first).toString(),
                                onClick = {
                                    onClick(LocalDate.of(pageDate.year, pageDate.month, (item + 1 - monthInfo.first)))
                                }
                            )
                        }
                    }
                }
            )
        }
    }
}

@Composable
internal fun YearPickerItem(
    selected: Boolean = false,
    text: String,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .height(40.dp)
            .background(
                color = if (selected) MaterialTheme.colorScheme.primary else Color.Transparent,
                shape = RoundedCornerShape(40.dp),
            )
            .clip(
                shape = RoundedCornerShape(40.dp)
            )
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center,
        content = {
            Text(
                text = text,
                fontSize = 13.sp,
                color = if (selected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurface
            )
        }
    )
}

@Composable
internal fun DatePickerItem(
    enabled: Boolean,
    selected: Boolean = false,
    today: Boolean = false,
    text: String,
    onClick: () -> Unit
) {
    val interactionSource = remember { MutableInteractionSource() }
    Box(
        modifier = Modifier
            .height(40.dp)
            .background(
                color = if (selected) MaterialTheme.colorScheme.primary else Color.Transparent,
                shape = RoundedCornerShape(40.dp),
            )
            .border(
                border = if (today) BorderStroke(
                    1.dp,
                    MaterialTheme.colorScheme.primary
                ) else BorderStroke(0.dp, Color.Transparent),
                shape = RoundedCornerShape(40.dp),
            )
            .then(
                if (enabled) {
                    Modifier.clickable(
                        interactionSource = interactionSource,
                        indication = null,
                        onClick = onClick
                    )
                } else
                    Modifier
            ),
        contentAlignment = Alignment.Center,
        content = {
            Text(
                text = text,
                fontSize = 13.sp,
                color = if (selected) MaterialTheme.colorScheme.onPrimary else if (today) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface
            )
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun DatePickerTextField(
    dateSelected: LocalDate,
    locale: Locale,
    datePattern: String,
    labelText: String,
    errorText: String,
    onSelectDate: (LocalDate) -> Unit
) {
    val textFieldValue = remember {
        mutableStateOf(
            TextFieldValue(
                text = dateSelected.toDateString(datePattern, locale),
                selection = TextRange(Int.MAX_VALUE)
            )
        )
    }
    val parsedDate = remember(textFieldValue.value.text) {
        try {
            LocalDate.parse(textFieldValue.value.text, DateTimeFormatter.ofPattern(datePattern))
        }
        catch (_: DateTimeParseException) {
            null
        }
    }
    val isValidationError = remember(parsedDate) {
        parsedDate == null
    }

    val focusRequester = remember { FocusRequester() }

    LaunchedEffect(Unit) {
        focusRequester.requestFocus()
    }
    LaunchedEffect(parsedDate) {
        parsedDate?.let(onSelectDate)
    }

    OutlinedTextField(
        modifier = Modifier.focusRequester(focusRequester),
        label = {
            Text(labelText)
        },
        placeholder = {
            Text(datePattern)
        },
        isError = isValidationError,
        supportingText = {
            if (isValidationError)
                Text(errorText)
        },
        singleLine = true,
        maxLines = 1,
        value = textFieldValue.value,
        onValueChange = {
            textFieldValue.value = it
        }
    )
}