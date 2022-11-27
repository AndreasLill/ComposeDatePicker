package com.andlill.composedatetimepicker

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.andlill.composedatetimepicker.ui.theme.ComposeDateTimePickerTheme
import com.andlill.datetimepicker.DatePickerDialog
import java.time.LocalDate

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ComposeDateTimePickerTheme {
                val dateDialogState = remember { mutableStateOf(false) }
                val date = remember { mutableStateOf(LocalDate.now()) }
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background,
                    content = {
                        Box(contentAlignment = Alignment.Center) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(text = date.value.toString())
                                Button(onClick = { dateDialogState.value = true }) {
                                    Text("Pick a date")
                                }
                                DatePickerDialog(
                                    state = dateDialogState,
                                    startDate = date.value,
                                    onSelectDate = {
                                        date.value = it
                                    }
                                )
                            }
                        }
                    }
                )
            }
        }
    }
}