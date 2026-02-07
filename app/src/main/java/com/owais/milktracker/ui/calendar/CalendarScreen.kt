package com.owais.milktracker.ui.calendar

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.automirrored.filled.TrendingDown
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.*
import androidx.lifecycle.viewmodel.compose.viewModel
import com.owais.milktracker.R
import com.owais.milktracker.ui.components.EntryDialog
import com.owais.milktracker.utils.SettingsPreferences
import com.owais.milktracker.viewmodel.MilkViewModel
import com.owais.milktracker.viewmodel.MilkViewModelFactory
import kotlinx.coroutines.flow.map
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.TextStyle
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun CalendarScreen(
    openEntryForToday: Boolean = false,
    onSettingsClick: () -> Unit
) {
    val context = LocalContext.current
    val milkPrice by SettingsPreferences.getMilkPrice(context).collectAsState(initial = 35.0f)
    val viewModel: MilkViewModel = viewModel(factory = MilkViewModelFactory(context))

    val today = LocalDate.now()
    var currentMonth by remember { mutableStateOf(YearMonth.now()) }
    var showDialog by remember { mutableStateOf(false) }
    var selectedDate by remember { mutableStateOf(today) }

    val allEntries by viewModel.entries.collectAsState(initial = emptyMap())

    val currentMonthEntries = remember(currentMonth, allEntries) {
        allEntries.filterKeys {
            it.month == currentMonth.month && it.year == currentMonth.year
        }.values
    }

    val totalBorrowed = currentMonthEntries.filter { it.isBorrowed }.sumOf { it.quantity }
    val totalSold = currentMonthEntries.filter { !it.isBorrowed }.sumOf { it.quantity }

    val amountToPay = (totalBorrowed * milkPrice).toInt()
    val amountToReceive = (totalSold * milkPrice).toInt()
    val netBalance = amountToReceive - amountToPay

    val entry by viewModel.entries.map { it[selectedDate] }.collectAsState(initial = null)
    val days = remember(currentMonth) { generateMonthDates(currentMonth) }

    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()

    LaunchedEffect(openEntryForToday) {
        if (openEntryForToday) {
            selectedDate = today
            showDialog = true
        }
    }

    Scaffold(
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Milk Tracker",
                        style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.primary
                    )
                },
                navigationIcon = {
//                    Icon(
//                        painter = painterResource(id = R.drawable.logo),
//                        contentDescription = "App Logo",
//                        modifier = Modifier
//                            .padding(2.dp)
//                            .size(40.dp)
//
//                    )
//                    The Icon looks of blank tint.

                    Image(
                        painter = painterResource(id = R.drawable.logo),
                        contentDescription = "Milk Tracker Logo",
                        modifier = Modifier
                            .size(40.dp), // You can reduce this if needed
                        contentScale = ContentScale.Fit
                    )
                },
                actions = {
                    IconButton(onClick = onSettingsClick) {
                        Icon(Icons.Default.Settings, contentDescription = "Settings")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surfaceContainer
                )
            )
        },
        content = { innerPadding ->
            Column(
                modifier = Modifier
                    .padding(innerPadding)
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {

                // Month Navigation
                ElevatedCard(modifier = Modifier.fillMaxWidth()) {
                    Row(
                        modifier = Modifier
                            .padding(16.dp)
                            .fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        IconButton(onClick = { currentMonth = currentMonth.minusMonths(1) }) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Previous")
                        }
                        Text(
                            "${currentMonth.month.getDisplayName(TextStyle.FULL, Locale.getDefault())} ${currentMonth.year}",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Medium)
                        )
                        IconButton(onClick = { currentMonth = currentMonth.plusMonths(1) }) {
                            Icon(Icons.AutoMirrored.Filled.ArrowForward, contentDescription = "Next")
                        }
                    }
                }

                // Weekdays Header
                Row(modifier = Modifier.fillMaxWidth()) {
                    listOf("Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun").forEach {
                        Text(
                            text = it,
                            modifier = Modifier.weight(1f),
                            textAlign = TextAlign.Center,
                            fontWeight = FontWeight.Bold,
                            style = MaterialTheme.typography.labelLarge
                        )
                    }
                }

                // Calendar Days Grid
                LazyVerticalGrid(
                    columns = GridCells.Fixed(7),
                    verticalArrangement = Arrangement.spacedBy(6.dp),
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    modifier = Modifier
                        .fillMaxHeight()
                        .weight(1f)
                ) {
                    items(days.size) { index ->
                        val day = days[index]
                        if (day != null) {
                            CalendarDay(day, viewModel) {
                                selectedDate = it
                                showDialog = true
                            }
                        } else {
                            Spacer(modifier = Modifier.aspectRatio(1f))
                        }
                    }
                }

                // Net Balance Display
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                ) {
                    Text(
                        text = when {
                            netBalance > 0 -> "Net Balance: ₹$netBalance to Receive"
                            netBalance < 0 -> "Net Balance: ₹${-netBalance} to Pay"
                            else -> "Net Balance: ₹0 (Settled)"
                        },
                        style = MaterialTheme.typography.bodyLarge,
                        color = when {
                            netBalance > 0 -> Color(0xFF1E88E5)
                            netBalance < 0 -> Color(0xFFD32F2F)
                            else -> Color.Gray
                        },
                        fontWeight = FontWeight.SemiBold,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        textAlign = TextAlign.Center
                    )
                }

                // Monthly Summary
                ElevatedCard(modifier = Modifier.fillMaxWidth()) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text("Monthly Summary", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.height(8.dp))

                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Column {
                                Text("Borrowed: $totalBorrowed L", color = Color.Red)
                                Text("To Pay: ₹$amountToPay", color = Color.Red)
                            }
                            Column(horizontalAlignment = Alignment.End) {
                                Text("Sold: $totalSold L", color = Color.Blue)
                                Text("To Receive: ₹$amountToReceive", color = Color.Blue)
                            }
                        }
                    }
                }
            }
        }
    )

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
fun generateMonthDates(yearMonth: YearMonth): List<LocalDate?> {
    val firstDay = yearMonth.atDay(1)
    val lastDay = yearMonth.atEndOfMonth()
    val firstWeekday = firstDay.dayOfWeek.value % 7
    val calendar = mutableListOf<LocalDate?>()
    for (i in 1..firstWeekday) calendar.add(null)
    for (day in 1..lastDay.dayOfMonth) calendar.add(yearMonth.atDay(day))
    return calendar
}
