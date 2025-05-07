package com.owais.milktracker.ui.settings

import android.app.TimePickerDialog
import android.content.Context
import android.widget.TimePicker
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
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
import java.util.Calendar

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
    val isDarkMode by viewModel.isDarkMode.collectAsState(initial = false)

    var isReminderOn by remember { mutableStateOf(reminderEnabled) }
    var reminderTime by remember { mutableStateOf("") }
    var milkPriceInput by remember { mutableStateOf(milkPrice.toString()) }
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(hour, minute) {
        reminderTime = formatTime(hour, minute)
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Settings",
                        style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
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
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text("App Preferences", style = MaterialTheme.typography.headlineSmall)

            // Section: Dark Mode Toggle
            ElevatedCard(modifier = Modifier.fillMaxWidth()) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Dark Mode", modifier = Modifier.weight(1f))
                    Switch(
                        checked = isDarkMode,
                        onCheckedChange = { isDark ->
                            scope.launch {
                                viewModel.updateDarkMode(isDark)
                                snackbarHostState.showSnackbar(
                                    if (isDark) "Dark mode enabled" else "Dark mode disabled"
                                )
                            }
                        }
                    )
                }
            }

            // Section: Reminder Settings
            ElevatedCard(modifier = Modifier.fillMaxWidth()) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Daily Reminder", modifier = Modifier.weight(1f))
                        Switch(
                            checked = isReminderOn,
                            onCheckedChange = {
                                isReminderOn = it
                                if (reminderTime.isNotBlank()) {
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
                            }
                        )
                    }

                    HorizontalDivider()

                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Reminder Time: $reminderTime", modifier = Modifier.weight(1f))
                        OutlinedButton(onClick = {
                            showTimePickerDialog(context) { h, m ->
                                val formatted = formatTime(h, m)
                                reminderTime = formatted

                                scope.launch {
                                    SettingsDataStore.setReminderTime(context, formatted)
                                    SettingsPreferences.saveReminder(context, isReminderOn, h, m)

                                    if (isReminderOn) {
                                        ReminderManager.scheduleDailyReminder(context, h, m)
                                    } else {
                                        ReminderManager.cancelReminder(context)
                                    }

                                    snackbarHostState.showSnackbar("Reminder time updated to $formatted")
                                }
                            }
                        }) {
                            Text("Change")
                        }
                    }
                }
            }

            // Section: Milk Price
            ElevatedCard(modifier = Modifier.fillMaxWidth()) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text("Milk Price Settings", style = MaterialTheme.typography.titleMedium)

                    OutlinedTextField(
                        value = milkPriceInput,
                        onValueChange = { milkPriceInput = it },
                        label = { Text("Milk Price (per litre)") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )

                    Button(
                        onClick = {
                            val price = milkPriceInput.toFloatOrNull() ?: 35.0f
                            if (reminderTime.isNotBlank()) {
                                val (h, m) = parseTime(reminderTime)
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
                            }
                        },
                        modifier = Modifier.align(Alignment.End)
                    ) {
                        Text("Save")
                    }
                }
            }
        }
    }
}


// Time Picker Dialog
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

// Format time to "h:mm AM/PM"
fun formatTime(hour: Int, minute: Int): String {
    val isPM = hour >= 12
    val formattedHour = if (hour % 12 == 0) 12 else hour % 12
    val formattedMinute = minute.toString().padStart(2, '0')
    val amPm = if (isPM) "PM" else "AM"
    return "$formattedHour:$formattedMinute $amPm"
}

// Safe parser with fallback
fun parseTime(time: String): Pair<Int, Int> {
    if (time.isBlank()) return 20 to 0 // fallback default

    val parts = time.trim().split(" ", ":")
    if (parts.size != 3) return 20 to 0 // malformed input

    return try {
        var hour = parts[0].toInt()
        val minute = parts[1].toInt()
        val isPM = parts[2].uppercase() == "PM"

        if (isPM && hour != 12) hour += 12
        if (!isPM && hour == 12) hour = 0

        hour to minute
    } catch (e: NumberFormatException) {
        20 to 0 // fallback if parsing fails
    }
}
