package com.andlill.datepicker

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
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.andlill.datepicker.TimeUtils.getDatePickerMonthInfo
import com.andlill.datepicker.TimeUtils.toDateString
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
import java.util.*

@Composable
fun DatePickerDialog(
    state: MutableState<Boolean>,
    properties: DialogProperties = DialogProperties(),
    yearRange: IntRange = IntRange(1900, 2100),
    initialDate: LocalDate = LocalDate.now(),
    locale: Locale = Locale.getDefault(),
    colors: DatePickerColors = DatePickerDefaults.colors(),
    strings: DatePickerStrings = DatePickerDefaults.strings(),
    onSelectDate: (LocalDate) -> Unit
) {
    if (state.value) {
        val config = LocalConfiguration.current
        val screenSize = remember(config.orientation) {
            Pair(config.screenWidthDp, config.screenHeightDp)
        }
        var dateSelected by remember { mutableStateOf(initialDate) }
        var showPicker by remember { mutableStateOf(true) }
        Dialog(
            properties = properties,
            onDismissRequest = { state.value = false },
            content = {
                Surface(
                    modifier = Modifier
                        .wrapContentSize()
                        .animateContentSize(),
                    color = colors.background,
                    shape = RoundedCornerShape(28.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        if (screenSize.second >= 560) {
                            Box(modifier = Modifier.fillMaxWidth()) {
                                Text(
                                    modifier = Modifier
                                        .padding(start = 8.dp)
                                        .align(Alignment.CenterStart),
                                    text = dateSelected.toDateString(strings.titleDatePattern, locale),
                                    fontSize = 22.sp,
                                    fontWeight = FontWeight.Normal,
                                    color = colors.title
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
                                            tint = colors.icon
                                        )
                                    }
                                )
                            }
                            Divider(
                                modifier = Modifier.padding(bottom = 8.dp),
                                color = colors.divider
                            )
                        }
                        Column(modifier = Modifier.fillMaxWidth()) {
                            if (showPicker) {
                                DatePickerCalendar(
                                    screenSize = screenSize,
                                    dateSelected = dateSelected,
                                    locale = locale,
                                    colors = colors,
                                    strings = strings,
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
                                    colors = colors,
                                    strings = strings,
                                    onSelectDate = {
                                        dateSelected = it
                                    }
                                )
                            }
                        }
                        Box(modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 8.dp)) {
                            Row(modifier = Modifier.align(Alignment.CenterEnd)) {
                                TextButton(
                                    colors = colors.negativeButton,
                                    onClick = {
                                        state.value = false
                                    },
                                    content = {
                                        Text(strings.negativeButtonText)
                                    }
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                TextButton(
                                    colors = colors.positiveButton,
                                    onClick = {
                                        onSelectDate(dateSelected)
                                        state.value = false
                                    },
                                    content = {
                                        Text(strings.positiveButtonText)
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
    screenSize: Pair<Int, Int>,
    dateSelected: LocalDate,
    locale: Locale,
    colors: DatePickerColors,
    strings: DatePickerStrings,
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
        colors = colors,
        strings = strings,
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
            screenSize = screenSize,
            yearRange = yearRange,
            colors = colors,
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
            screenSize = screenSize,
            state = pagerState,
            pageCount = pageCount,
            startYear = yearRange.first,
            dateSelected = dateSelected,
            locale = locale,
            colors = colors,
            onClick = onSelectDate
        )
    }
}

@Composable
internal fun DatePickerCalendarHeader(
    dateViewed: LocalDate,
    locale: Locale,
    colors: DatePickerColors,
    strings: DatePickerStrings,
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
                    text = dateViewed.toDateString(strings.yearPickerDatePattern, locale),
                    fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = colors.calendarYearPicker
                )
                Spacer(modifier = Modifier.width(4.dp))
                Icon(
                    modifier = Modifier.size(20.dp),
                    imageVector = if (isShowYearPicker) Icons.Filled.ArrowDropUp else Icons.Filled.ArrowDropDown,
                    contentDescription = null,
                    tint = colors.calendarYearPicker
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
                        tint = colors.icon.copy(if (isShowYearPicker) 0.5f else 1f)
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
                        tint = colors.icon.copy(if (isShowYearPicker) 0.5f else 1f)
                    )
                }
            )
        }
    }
}

@Composable
internal fun DatePickerCalendarYearPicker(
    screenSize: Pair<Int, Int>,
    yearRange: IntRange,
    colors: DatePickerColors,
    dateSelected: LocalDate,
    onSelectYear: (Int) -> Unit
) {
    val state = rememberLazyGridState(
        initialFirstVisibleItemIndex = dateSelected.year - yearRange.first
    )
    LazyVerticalGrid(
        modifier = Modifier.height(if (screenSize.second >= 560) 280.dp else 210.dp),
        state = state,
        columns = GridCells.Fixed(3),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        content = {
            items(count = yearRange.last - yearRange.first + 1) { item ->
                val value = remember(item) {
                    (item + yearRange.first)
                }
                YearPickerItem(
                    colors = colors,
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
    screenSize: Pair<Int, Int>,
    state: PagerState,
    pageCount: Int,
    startYear: Int,
    dateSelected: LocalDate,
    locale: Locale,
    colors: DatePickerColors,
    onClick: (LocalDate) -> Unit
) {
    val dateNow = remember { LocalDate.now() }

    Column(modifier = Modifier.fillMaxWidth()) {
        Row(modifier = Modifier.fillMaxWidth()) {
            (0..6).forEach { day ->
                Box(
                    modifier = Modifier
                        .weight(1F)
                        .size(if (screenSize.second >= 560) 40.dp else 30.dp),
                    contentAlignment = Alignment.Center) {
                    Text(
                        text = WeekFields.of(locale).firstDayOfWeek.plus(day.toLong()).getDisplayName(TextStyle.NARROW, locale),
                        fontSize = 13.sp,
                        color = colors.calendarWeekText
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
                modifier = Modifier.height(if (screenSize.second >= 560) 240.dp else 180.dp),
                userScrollEnabled = false,
                columns = GridCells.Fixed(7),
                content = {
                    // Maximum amount of "dates" shown is 37.
                    // Largest count is starting on final day of week and 31 days. (ex. Jan 2023)
                    items(count = 37) { item ->
                        if (item >= monthInfo.first && (item - monthInfo.first) < monthInfo.second) {
                            CalendarItem(
                                colors = colors,
                                enabled = true,
                                selected = (pageDate.year == dateSelected.year && pageDate.month == dateSelected.month && dateSelected.dayOfMonth == (item + 1 - monthInfo.first)),
                                today = (pageDate.year == dateNow.year && pageDate.month == dateNow.month && dateNow.dayOfMonth == (item + 1 - monthInfo.first)),
                                text = (item + 1 - monthInfo.first).toString(),
                                size = if (screenSize.second >= 560) 40.dp else 30.dp,
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun DatePickerTextField(
    dateSelected: LocalDate,
    locale: Locale,
    colors: DatePickerColors,
    strings: DatePickerStrings,
    onSelectDate: (LocalDate) -> Unit
) {
    val textFieldValue = remember {
        mutableStateOf(
            TextFieldValue(
                text = dateSelected.toDateString(strings.textFieldDatePattern, locale),
                selection = TextRange(Int.MAX_VALUE)
            )
        )
    }
    val parsedDate = remember(textFieldValue.value.text) {
        try {
            LocalDate.parse(textFieldValue.value.text, DateTimeFormatter.ofPattern(strings.textFieldDatePattern))
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
        colors = colors.textFieldColors,
        label = {
            Text(strings.textFieldLabelText)
        },
        placeholder = {
            Text(strings.textFieldDatePattern)
        },
        isError = isValidationError,
        supportingText = {
            if (isValidationError)
                Text(strings.textFieldErrorText)
        },
        singleLine = true,
        maxLines = 1,
        value = textFieldValue.value,
        onValueChange = {
            textFieldValue.value = it
        }
    )
}

@Composable
internal fun YearPickerItem(
    colors: DatePickerColors,
    selected: Boolean = false,
    text: String,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .height(40.dp)
            .background(
                color = if (selected) colors.calendarSelectedBackground else Color.Transparent,
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
                color = if (selected) colors.calendarSelectedText else colors.calendarText
            )
        }
    )
}

@Composable
internal fun CalendarItem(
    colors: DatePickerColors,
    enabled: Boolean,
    selected: Boolean = false,
    today: Boolean = false,
    text: String,
    size: Dp,
    onClick: () -> Unit
) {
    val interactionSource = remember { MutableInteractionSource() }
    Box(contentAlignment = Alignment.Center) {
        Box(
            modifier = Modifier
                .size(size)
                .background(
                    color = if (selected) colors.calendarSelectedBackground else Color.Transparent,
                    shape = RoundedCornerShape(size),
                )
                .border(
                    border = if (today) BorderStroke(
                        1.dp,
                        colors.calendarSelectedText
                    ) else BorderStroke(0.dp, Color.Transparent),
                    shape = RoundedCornerShape(size),
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
                    color = if (selected) colors.calendarSelectedText else if (today) colors.calendarSelectedText else colors.calendarText
                )
            }
        )
    }
}