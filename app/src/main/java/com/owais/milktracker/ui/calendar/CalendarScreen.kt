package com.owais.milktracker.ui.calendar

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.*
import androidx.lifecycle.viewmodel.compose.viewModel
import com.owais.milktracker.ui.components.EntryDialog
import com.owais.milktracker.viewmodel.MilkViewModel
import com.owais.milktracker.viewmodel.MilkViewModelFactory
import kotlinx.coroutines.flow.map
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.TextStyle
import java.util.*

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun CalendarScreen() {
    val context = LocalContext.current
    val viewModel: MilkViewModel = viewModel(factory = MilkViewModelFactory(context))
    val today = LocalDate.now()
    var currentMonth by remember { mutableStateOf(YearMonth.now()) }

    val days = remember(currentMonth) {
        generateMonthDates(currentMonth)
    }

    Column(modifier = Modifier.padding(16.dp)) {
        // Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            TextButton(onClick = { currentMonth = currentMonth.minusMonths(1) }) {
                Text("< Previous")
            }
            Text(
                text = "${currentMonth.month.getDisplayName(TextStyle.FULL, Locale.getDefault())} ${currentMonth.year}",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
            TextButton(onClick = { currentMonth = currentMonth.plusMonths(1) }) {
                Text("Next >")
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Weekdays
        val weekdays = listOf("Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun")
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
            weekdays.forEach {
                Text(text = it, modifier = Modifier.weight(1f), textAlign = TextAlign.Center)
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Calendar grid
        LazyVerticalGrid(
            columns = GridCells.Fixed(7),
            content = {
                items(days.size) { index ->
                    val day = days[index]
                    if (day != null) {
                        CalendarDay(day, viewModel)
                    } else {
                        Box(modifier = Modifier
                            .aspectRatio(1f)
                            .padding(4.dp))
                    }
                }

            }
        )
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun CalendarDay(date: LocalDate, viewModel: MilkViewModel) {
    var showDialog by remember { mutableStateOf(false) }

    val entry by viewModel.entries
        .map { it[date] }
        .collectAsState(initial = null)

    val bgColor = when {
        entry?.isBorrowed == true -> Color.Red.copy(alpha = 0.3f)
        entry?.isBorrowed == false -> Color.Blue.copy(alpha = 0.3f)
        else -> Color.Transparent
    }

    Box(
        modifier = Modifier
            .aspectRatio(1f)
            .padding(4.dp)
            .background(bgColor, shape = MaterialTheme.shapes.medium)
            .clickable { showDialog = true },
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = "${date.dayOfMonth}",
                style = MaterialTheme.typography.bodySmall
            )
            if (entry != null) {
                Text(
                    text = "${entry!!.quantity}L",
                    style = MaterialTheme.typography.labelSmall,
                    color = Color.Black
                )
            }
        }
    }

    if (showDialog) {
        EntryDialog(
            date = date,
            initialEntry = entry,
            onDismiss = { showDialog = false },
            onSave = {
                viewModel.upsertEntry(it)
            },
            onDelete = {
                entry?.let { viewModel.deleteEntry(it) }
            }
        )
    }
}

@RequiresApi(Build.VERSION_CODES.O)
fun generateMonthDates(yearMonth: YearMonth): List<LocalDate?> {
    val firstDay = yearMonth.atDay(1)
    val lastDay = yearMonth.atEndOfMonth()
    val firstWeekday = firstDay.dayOfWeek.value % 7 // Make Sunday = 0

    val totalDays = lastDay.dayOfMonth
    val calendar = mutableListOf<LocalDate?>()

    for (i in 1..firstWeekday) calendar.add(null)
    for (day in 1..totalDays) {
        calendar.add(yearMonth.atDay(day))
    }

    return calendar
}
