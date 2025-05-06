package com.owais.milktracker.ui.settings

import android.app.TimePickerDialog
import android.content.Context
import android.widget.TimePicker
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.owais.milktracker.alarm.ReminderManager
import com.owais.milktracker.data.SettingsDataStore
import com.owais.milktracker.utils.SettingsPreferences
import com.owais.milktracker.viewmodel.SettingsViewModel
import com.owais.milktracker.viewmodel.SettingsViewModelFactory
import kotlinx.coroutines.launch
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(onBack: () -> Unit) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val viewModel: SettingsViewModel = viewModel(factory = SettingsViewModelFactory(context))

    val reminderEnabled by viewModel.reminderEnabled.collectAsState(initial = true)
    val hour by viewModel.reminderHour.collectAsState(initial = 20)
    val minute by viewModel.reminderMinute.collectAsState(initial = 0)
    val milkPrice by viewModel.milkPrice.collectAsState()

    var isReminderOn by remember { mutableStateOf(reminderEnabled) }
    var reminderTime by remember { mutableStateOf("") }

    LaunchedEffect(hour, minute) {
        reminderTime = formatTime(hour, minute)
    }
    var milkPriceInput by remember { mutableStateOf(milkPrice.toString()) }
    val snackbarHostState = remember { SnackbarHostState() }


    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Settings",
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.Bold
                        )
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surfaceContainer
                )
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp),
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.Start
        ) {
            Text("App Preferences", style = MaterialTheme.typography.headlineSmall)
            Spacer(modifier = Modifier.height(24.dp))

            // Reminder Toggle
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Daily Reminder", modifier = Modifier.weight(1f))
                Switch(
                    checked = isReminderOn,
                    onCheckedChange = {
                        isReminderOn = it

                        val (h, m) = parseTime(reminderTime)

                        scope.launch {
                            viewModel.updateReminder(it, h, m)

                            if (it) {
                                ReminderManager.scheduleDailyReminder(context, h, m)
                                snackbarHostState.showSnackbar("Reminder enabled for $reminderTime")
                            } else {
                                ReminderManager.cancelReminder(context)
                                snackbarHostState.showSnackbar("Reminder disabled")
                            }
                        }
                    }
                )

            }

            Spacer(modifier = Modifier.height(16.dp))

            // Time Picker (Updated to auto-save)
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Reminder Time: $reminderTime", modifier = Modifier.weight(1f))
                Button(onClick = {
                    showTimePickerDialog(context) { hour, minute ->
                        val formatted = formatTime(hour, minute)
                        reminderTime = formatted

                        scope.launch {
                            // Save to SettingsDataStore (UI display purpose)
                            SettingsDataStore.setReminderTime(context, formatted)

                            // Save to SettingsPreferences (actual alarm scheduling)
                            SettingsPreferences.saveReminder(context, isReminderOn, hour, minute)

                            if (isReminderOn) {
                                ReminderManager.scheduleDailyReminder(context, hour, minute)
                            } else {
                                ReminderManager.cancelReminder(context)
                            }

                            // Show confirmation
                            snackbarHostState.showSnackbar("Reminder time updated to $formatted")
                        }
                    }
                }) {
                    Text("Change")
                }
            }


            Spacer(modifier = Modifier.height(16.dp))


            Row(
                modifier = Modifier
                    .fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Milk Price Input
                OutlinedTextField(
                    value = milkPriceInput,
                    onValueChange = { milkPriceInput = it },
                    label = { Text("Milk Price (per litre)") },
                    modifier = Modifier.weight(1f),
                    singleLine = true
                )

                Button(onClick = {
                    val (h, m) = parseTime(reminderTime)
                    val price = milkPriceInput.toFloatOrNull() ?: 35.0f

                    scope.launch {
                        viewModel.updateReminder(isReminderOn, h, m)
                        viewModel.updateMilkPrice(price)

                        if (isReminderOn) {
                            ReminderManager.scheduleDailyReminder(context, h, m)
                        } else {
                            ReminderManager.cancelReminder(context)
                        }

                        snackbarHostState.showSnackbar("Milk price updated to ₹${"%.2f".format(price)}")
                    }
                }) {
                    Text("Save")
                }

            }



        }
    }
}


fun showTimePickerDialog(context: Context, onTimeSelected: (Int, Int) -> Unit) {
    val calendar = Calendar.getInstance()
    val hour = calendar.get(Calendar.HOUR_OF_DAY)
    val minute = calendar.get(Calendar.MINUTE)

    TimePickerDialog(
        context,
        { _: TimePicker, selectedHour: Int, selectedMinute: Int ->
            onTimeSelected(selectedHour, selectedMinute)
        },
        hour,
        minute,
        false
    ).show()
}

fun formatTime(hour: Int, minute: Int): String {
    val isPM = hour >= 12
    val formattedHour = if (hour % 12 == 0) 12 else hour % 12
    val formattedMinute = minute.toString().padStart(2, '0')
    val amPm = if (isPM) "PM" else "AM"
    return "$formattedHour:$formattedMinute $amPm"
}

fun parseTime(time: String): Pair<Int, Int> {
    val parts = time.trim().split(" ", ":")
    var hour = parts[0].toInt()
    val minute = parts[1].toInt()
    val isPM = parts[2] == "PM"

    if (isPM && hour != 12) hour += 12
    if (!isPM && hour == 12) hour = 0
    return hour to minute
}
