package com.owais.milktracker.ui.calendar

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.filled.TrendingDown
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
fun CalendarScreen(openEntryForToday: Boolean = false) {
    val context = LocalContext.current
    val viewModel: MilkViewModel = viewModel(factory = MilkViewModelFactory(context))

    val today = LocalDate.now()
    var currentMonth by remember { mutableStateOf(YearMonth.now()) }

    var showDialog by remember { mutableStateOf(false) }
    var selectedDate by remember { mutableStateOf(today) }

    // Automatically show entry dialog for today's date (used when coming from a FAB, etc.)
    LaunchedEffect(openEntryForToday) {
        if (openEntryForToday) {
            selectedDate = today
            showDialog = true
        }
    }

    // Observe entry for the selected date
    val entry by viewModel.entries
        .map { it[selectedDate] }
        .collectAsState(initial = null)

    // Generate all dates (including padding nulls) for the grid
    val days = remember(currentMonth) {
        generateMonthDates(currentMonth)
    }

    // ---------- UI Layout ----------

    Column(modifier = Modifier.padding(16.dp)) {

        // Header with Month Navigation
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

        // Weekday Labels
        val weekdays = listOf("Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun")
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
            weekdays.forEach {
                Text(text = it, modifier = Modifier.weight(1f), textAlign = TextAlign.Center)
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Calendar Grid Layout
        LazyVerticalGrid(
            columns = GridCells.Fixed(7),
            content = {
                items(days.size) { index ->
                    val day = days[index]
                    if (day != null) {
                        CalendarDay(day, viewModel) { clickedDate ->
                            selectedDate = clickedDate
                            showDialog = true
                        }
                    } else {
                        // Empty cell (padding at start of month)
                        Box(modifier = Modifier
                            .aspectRatio(1f)
                            .padding(4.dp))
                    }
                }
            }
        )
    }

    // Entry Dialog for adding/updating entries
    if (showDialog) {
        EntryDialog(
            date = selectedDate,
            initialEntry = entry,
            onDismiss = { showDialog = false },
            onSave = {
                viewModel.upsertEntry(it)
                showDialog = false
            },
            onDelete = {
                entry?.let { viewModel.deleteEntry(it) }
                showDialog = false
            }
        )
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun CalendarDay(
    date: LocalDate,
    viewModel: MilkViewModel,
    onClick: (LocalDate) -> Unit
) {
    // Observe this day's entry
    val entry by viewModel.entries
        .map { it[date] }
        .collectAsState(initial = null)

    // Background color for each day box
    val bgColor = when {
        entry?.isBorrowed == true -> Color.Red.copy(alpha = 0.1f)
        entry?.isBorrowed == false -> Color.Blue.copy(alpha = 0.1f)
        else -> Color.Transparent
    }

    Box(
        modifier = Modifier
            .aspectRatio(1f)
            .padding(4.dp)
            .background(bgColor, shape = MaterialTheme.shapes.medium)
            .clickable { onClick(date) },
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            // Date Number (e.g., 1, 2, 3...)
            Text(
                text = "${date.dayOfMonth}",
                style = MaterialTheme.typography.bodySmall
            )

            if (entry != null) {
                Spacer(modifier = Modifier.height(4.dp))

                // Milk Quantity (e.g., 1.5L)
                Text(
                    text = "${entry!!.quantity}L",
                    style = MaterialTheme.typography.labelSmall,
                    color = Color.Black
                )

                Spacer(modifier = Modifier.height(2.dp))

                // Status Icon: Borrowed or Sold
                Icon(
                    imageVector = if (entry!!.isBorrowed) Icons.Filled.TrendingDown else Icons.Filled.ShoppingCart,
                    contentDescription = if (entry!!.isBorrowed) "Borrowed" else "Sold",
                    tint = if (entry!!.isBorrowed) Color.Red else Color.Blue,
                    modifier = Modifier.size(16.dp)
                )

                // Small colored dot for status (optional aesthetic)
                Box(
                    modifier = Modifier
                        .padding(top = 2.dp)
                        .size(6.dp)
                        .background(
                            color = if (entry!!.isBorrowed) Color.Red else Color.Blue,
                            shape = CircleShape
                        )
                )
            }
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
fun generateMonthDates(yearMonth: YearMonth): List<LocalDate?> {
    val firstDay = yearMonth.atDay(1)
    val lastDay = yearMonth.atEndOfMonth()

    val firstWeekday = firstDay.dayOfWeek.value % 7 // Sunday = 0

    val totalDays = lastDay.dayOfMonth
    val calendar = mutableListOf<LocalDate?>()

    // Padding before 1st of the month
    for (i in 1..firstWeekday) calendar.add(null)

    // Add all valid dates
    for (day in 1..totalDays) {
        calendar.add(yearMonth.atDay(day))
    }

    return calendar
}
