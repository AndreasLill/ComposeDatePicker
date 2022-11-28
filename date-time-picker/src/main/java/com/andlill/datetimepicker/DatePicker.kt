package com.andlill.datetimepicker

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.NavigateBefore
import androidx.compose.material.icons.filled.NavigateNext
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
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

@Composable
fun DatePickerDialog(
    state: MutableState<Boolean>,
    startDate: LocalDate = LocalDate.now(),
    onSelectDate: (LocalDate) -> Unit
) {
    if (state.value) {

        var dateSelected by remember { mutableStateOf(startDate) }
        val showPicker by remember { mutableStateOf(true) }

        Dialog(
            onDismissRequest = { state.value = false },
            content = {
                Surface(
                    color = MaterialTheme.colorScheme.surface,
                    shape = RoundedCornerShape(32.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Box(modifier = Modifier.fillMaxWidth()) {
                            Text(
                                modifier = Modifier
                                    .padding(start = 16.dp)
                                    .align(Alignment.CenterStart),
                                text = dateSelected.toDateString("E, MMM d"),
                                fontSize = 22.sp,
                                fontWeight = FontWeight.Normal,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            IconButton(
                                modifier = Modifier.align(Alignment.CenterEnd),
                                onClick = {},
                                content = {
                                    Icon(
                                        imageVector = Icons.Outlined.Edit,
                                        contentDescription = null,
                                        tint = MaterialTheme.colorScheme.onSurface.copy(0.8f)
                                    )
                                }
                            )
                        }
                        Divider(modifier = Modifier.padding(bottom = 8.dp))
                        Column(modifier = Modifier.fillMaxWidth()) {
                            if (showPicker) {
                                DatePickerPager(dateSelected) {
                                    dateSelected = it
                                }
                            }
                            else {
                                DatePickerTextField(dateSelected)
                            }
                        }
                        Box(modifier = Modifier.fillMaxWidth()) {
                            Row(modifier = Modifier.align(Alignment.CenterEnd)) {
                                TextButton(
                                    onClick = {
                                        state.value = false
                                    },
                                    content = {
                                        Text(text = "Cancel")
                                    }
                                )
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
internal fun DatePickerPager(dateSelected: LocalDate, onSelectDate: (LocalDate) -> Unit) {
    var showYearPicker by remember { mutableStateOf(false) }
    val yearRange = remember { IntRange(1900, 2100) }
    val pageScope = rememberCoroutineScope()
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

    DatePickerPagerHeader(
        dateViewed = dateViewed,
        onPrevious = {
            pageScope.launch {
                if (pagerState.currentPage > 0)
                    pagerState.animateScrollToPage(pagerState.currentPage - 1)
            }
        },
        onNext = {
            pageScope.launch {
                if (pagerState.currentPage <= pagerState.pageCount)
                    pagerState.animateScrollToPage(pagerState.currentPage + 1)
            }
        },
        onToggleYearPicker = {
            showYearPicker = !showYearPicker
        }
    )
    if (showYearPicker) {
        DatePickerPagerYearPicker(
            yearRange = yearRange,
            dateSelected = dateSelected,
            onSelectYear = { year ->
                showYearPicker = false
                onSelectDate(LocalDate.of(
                    year,
                    dateSelected.month,
                    dateSelected.dayOfMonth
                ))
                pageScope.launch {
                    pagerState.scrollToPage((year - yearRange.first) * 12 + dateSelected.monthValue - 1)
                }
            }
        )
    }
    else {
        DatePickerPagerBody(
            state = pagerState,
            pageCount = pageCount,
            startYear = yearRange.first,
            selectedDate = dateSelected,
            onClick = onSelectDate
        )
    }
}

@Composable
internal fun DatePickerPagerHeader(dateViewed: LocalDate, onPrevious: () -> Unit, onNext: () -> Unit, onToggleYearPicker: () -> Unit) {
    Box(modifier = Modifier.fillMaxWidth()) {
        Row(modifier = Modifier.align(Alignment.CenterStart), verticalAlignment = Alignment.CenterVertically) {
            Button(
                colors = ButtonDefaults.textButtonColors(),
                onClick = onToggleYearPicker
            ) {
                Text(
                    text = dateViewed.toDateString("MMMM yyyy"),
                    fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurface.copy(0.8f)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Icon(
                    modifier = Modifier.size(20.dp),
                    imageVector = Icons.Filled.ArrowDropDown,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSurface.copy(0.8f)
                )
            }
        }
        Row(modifier = Modifier.align(Alignment.CenterEnd)) {
            IconButton(
                onClick = onPrevious,
                content = {
                    Icon(
                        imageVector = Icons.Filled.NavigateBefore,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onSurface.copy(0.8f)
                    )
                }
            )
            IconButton(
                onClick = onNext,
                content = {
                    Icon(
                        imageVector = Icons.Filled.NavigateNext,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onSurface.copy(0.8f)
                    )
                }
            )
        }
    }
}

@Composable
internal fun DatePickerPagerYearPicker(yearRange: IntRange, dateSelected: LocalDate, onSelectYear: (Int) -> Unit) {
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
            items(count = yearRange.last - yearRange.first) { item ->
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
internal fun DatePickerPagerBody(state: PagerState, pageCount: Int, startYear: Int, selectedDate: LocalDate, onClick: (LocalDate) -> Unit) {
    val dayNames = remember { listOf("M", "T", "W", "T", "F", "S", "S") }
    val dateNow = remember { LocalDate.now() }

    Column(modifier = Modifier.fillMaxWidth()) {
        Row(modifier = Modifier.fillMaxWidth()) {
            dayNames.forEach { item ->
                Box(
                    modifier = Modifier
                        .weight(1F)
                        .size(40.dp),
                    contentAlignment = Alignment.Center) {
                    Text(
                        text = item,
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
                pageDate.getDatePickerMonthInfo()
            }
            LazyVerticalGrid(
                userScrollEnabled = false,
                columns = GridCells.Fixed(7),
                content = {
                    items(count = 42) { item ->
                        // TODO: Perhaps do 6 rows of items instead for performance?
                        if (item >= monthInfo.first && (item - monthInfo.first) < monthInfo.second) {
                            DatePickerItem(
                                enabled = true,
                                selected = (pageDate.year == selectedDate.year && pageDate.month == selectedDate.month && selectedDate.dayOfMonth == (item + 1 - monthInfo.first)),
                                today = (pageDate.year == dateNow.year && pageDate.month == dateNow.month && dateNow.dayOfMonth == (item + 1 - monthInfo.first)),
                                text = (item + 1 - monthInfo.first).toString(),
                                onClick = {
                                    onClick(LocalDate.of(pageDate.year, pageDate.month, (item + 1 - monthInfo.first)))
                                }
                            )
                        }
                        else {
                            Box(modifier = Modifier.size(40.dp))
                        }
                    }
                }
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun YearPickerItem(selected: Boolean = false, text: String, onClick: () -> Unit) {
    Surface(
        modifier = Modifier.height(40.dp),
        shape = RoundedCornerShape(32.dp),
        color = if (selected) MaterialTheme.colorScheme.primary else Color.Transparent,
        onClick = onClick,
        content = {
            Box(contentAlignment = Alignment.Center) {
                Text(
                    text = text,
                    fontSize = 13.sp,
                    color = if (selected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurface
                )
            }
        }
    )
}

@Composable
internal fun DatePickerItem(enabled: Boolean, selected: Boolean = false, today: Boolean = false, text: String, onClick: () -> Unit) {
    val interactionSource = remember { MutableInteractionSource() }
    Surface(
        modifier = Modifier
            .then(
                if (enabled) {
                    Modifier.clickable(
                        interactionSource = interactionSource,
                        indication = null,
                        onClick = onClick
                    )
                }
                else
                    Modifier
            ),
        shape = RoundedCornerShape(32.dp),
        border = if (today) BorderStroke(1.dp, MaterialTheme.colorScheme.primary) else BorderStroke(0.dp, Color.Transparent),
        color = if (selected) MaterialTheme.colorScheme.primary else Color.Transparent,
        content = {
            Box(modifier = Modifier.height(40.dp), contentAlignment = Alignment.Center) {
                Text(
                    text = text,
                    fontSize = 13.sp,
                    color = if (selected) MaterialTheme.colorScheme.onPrimary else if (today) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface
                )
            }
        }
    )
}

@Composable
internal fun DatePickerTextField(dateSelected: LocalDate) {
    Text(dateSelected.toString())
}