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
    val viewModel: MilkViewModel = viewModel(factory = MilkViewModelFactory(context))

    val today = LocalDate.now()
    var currentMonth by remember { mutableStateOf(YearMonth.now()) }

    var showDialog by remember { mutableStateOf(false) }
    var selectedDate by remember { mutableStateOf(today) }

    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()

    // Automatically open dialog if triggered externally (e.g., via FAB)
    LaunchedEffect(openEntryForToday) {
        if (openEntryForToday) {
            selectedDate = today
            showDialog = true
        }
    }

    val allEntries by viewModel.entries.collectAsState(initial = emptyMap())

    val currentMonthEntries = remember(currentMonth, allEntries) {
        allEntries.filterKeys {
            it.month == currentMonth.month && it.year == currentMonth.year
        }.values
    }

    val totalBorrowed = currentMonthEntries.filter { it.isBorrowed }.sumOf { it.quantity }
    val totalSold = currentMonthEntries.filter { !it.isBorrowed }.sumOf { it.quantity }

    val ratePerLitre = 35
    val amountToPay = (totalBorrowed * ratePerLitre).toInt()
    val amountToReceive = (totalSold * ratePerLitre).toInt()
    val netBalance = amountToReceive - amountToPay

    val entry by viewModel.entries.map { it[selectedDate] }.collectAsState(initial = null)

    val days = remember(currentMonth) { generateMonthDates(currentMonth) }

    Scaffold(
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            TopAppBar(
                navigationIcon = {
                    Image(
                        painter = painterResource(id = R.drawable.logo),
                        contentDescription = "Milk Tracker Logo",
                        modifier = Modifier
                            .size(40.dp), // You can reduce this if needed
                        contentScale = ContentScale.Fit
                    )
                },
                title = {
                    Text(
                        text = "Milk Tracker",
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                    )
                },
                actions = {
                    IconButton(onClick = onSettingsClick) {
                        Icon(
                            imageVector = Icons.Default.Settings,
                            contentDescription = "Settings",
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }

                    IconButton(onClick = {
                        // Optional: Exit logic
                    }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ExitToApp,
                            contentDescription = "Exit App",
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surfaceContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary
                ),
                scrollBehavior = scrollBehavior
            )
        },
        content = { innerPadding ->

            Column(modifier = Modifier
                .padding(innerPadding)
                .padding(16.dp)) {

                // Header with Month & Navigation
                Surface(
                    tonalElevation = 5.dp,
                    shape = MaterialTheme.shapes.medium,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        IconButton(onClick = { currentMonth = currentMonth.minusMonths(1) }) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Previous Month")
                        }

                        Text(
                            text = "${currentMonth.month.getDisplayName(TextStyle.FULL, Locale.getDefault())} ${currentMonth.year}",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )

                        IconButton(onClick = { currentMonth = currentMonth.plusMonths(1) }) {
                            Icon(Icons.AutoMirrored.Filled.ArrowForward, contentDescription = "Next Month")
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Weekday headers
                val weekdays = listOf("Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun")
                Row(modifier = Modifier.fillMaxWidth()) {
                    weekdays.forEach {
                        Text(
                            text = it,
                            modifier = Modifier.weight(1f),
                            style = MaterialTheme.typography.labelMedium,
                            textAlign = TextAlign.Center,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                Spacer(modifier = Modifier.height(6.dp))

                // Calendar Grid
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
                            Box(modifier = Modifier
                                .aspectRatio(1f)
                                .padding(4.dp))
                        }
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = when {
                        netBalance > 0 -> "Net Balance: ₹$netBalance to Receive"
                        netBalance < 0 -> "Net Balance: ₹${-netBalance} to Pay"
                        else -> "Net Balance: ₹0 (Settled)"
                    },
                    color = when {
                        netBalance > 0 -> Color.Blue
                        netBalance < 0 -> Color.Red
                        else -> Color.Gray
                    },
                    fontWeight = FontWeight.Bold,
                    style = MaterialTheme.typography.bodyMedium,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(12.dp))

                Column(modifier = Modifier.fillMaxWidth().padding(top = 12.dp)) {
                    Text(
                        text = "Monthly Summary",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(6.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column(horizontalAlignment = Alignment.Start) {
                            Text("Borrowed: $totalBorrowed L", color = Color.Red, fontWeight = FontWeight.SemiBold)
                            Text("To Pay: ₹$amountToPay", color = Color.Red)
                        }

                        Column(horizontalAlignment = Alignment.End) {
                            Text("Sold: $totalSold L", color = Color.Blue, fontWeight = FontWeight.SemiBold)
                            Text("To Receive: ₹$amountToReceive", color = Color.Blue)
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
@Composable
fun CalendarDay(
    date: LocalDate,
    viewModel: MilkViewModel,
    onClick: (LocalDate) -> Unit
) {
    val entry by viewModel.entries
        .map { it[date] }
        .collectAsState(initial = null)

    val bgColor = when (entry?.isBorrowed) {
        true -> Color.Red.copy(alpha = 0.1f)
        false -> Color.Blue.copy(alpha = 0.1f)
        else -> Color.Transparent
    }

    Box(
        modifier = Modifier
            .aspectRatio(1f)
            .padding(0.dp)
            .background(bgColor, shape = MaterialTheme.shapes.medium)
            .clickable { onClick(date) },
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = "${date.dayOfMonth}",
                style = MaterialTheme.typography.bodySmall
            )

            if (entry != null) {
                Spacer(modifier = Modifier.height(1.dp))
                Text(
                    text = "${entry!!.quantity}L",
                    style = MaterialTheme.typography.labelSmall,
                    color = Color.Black,
                    fontWeight = FontWeight.SemiBold
                )
                Spacer(modifier = Modifier.height(1.dp))
                Icon(
                    imageVector = if (entry!!.isBorrowed) Icons.AutoMirrored.Filled.TrendingDown else Icons.Filled.ShoppingCart,
                    contentDescription = if (entry!!.isBorrowed) "Borrowed" else "Sold",
                    tint = if (entry!!.isBorrowed) Color.Red else Color.Blue,
                    modifier = Modifier.size(10.dp)
                )
                Box(
                    modifier = Modifier
                        .padding(top = 0.dp)
                        .size(25.dp)
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
    val firstWeekday = firstDay.dayOfWeek.value % 7
    val calendar = mutableListOf<LocalDate?>()
    for (i in 1..firstWeekday) calendar.add(null)
    for (day in 1..lastDay.dayOfMonth) calendar.add(yearMonth.atDay(day))
    return calendar
}
