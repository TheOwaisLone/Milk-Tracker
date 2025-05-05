package com.owais.milktracker.ui.settings

import android.app.TimePickerDialog
import android.content.Context
import android.widget.TimePicker
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.owais.milktracker.data.SettingsDataStore
import kotlinx.coroutines.launch
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(onBack: () -> Unit) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    // Load stored values
    val reminderEnabledFlow = SettingsDataStore.getReminderEnabled(context).collectAsState(initial = true)
    val reminderTimeFlow = SettingsDataStore.getReminderTime(context).collectAsState(initial = "08:00 PM")
    val milkPriceFlow = SettingsDataStore.getMilkPrice(context).collectAsState(initial = "0.0")

    var isReminderOn by remember { mutableStateOf(reminderEnabledFlow.value) }
    var reminderTime by remember { mutableStateOf(reminderTimeFlow.value) }
    var milkPrice by remember { mutableStateOf(milkPriceFlow.value) }

    // Update states when flows change
    LaunchedEffect(reminderEnabledFlow.value, reminderTimeFlow.value, milkPriceFlow.value) {
        isReminderOn = reminderEnabledFlow.value
        reminderTime = reminderTimeFlow.value
        milkPrice = milkPriceFlow.value
    }

    Scaffold(
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
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
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
                        scope.launch {
                            SettingsDataStore.setReminderEnabled(context, it)
                        }
                    }
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Time Picker
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
                            SettingsDataStore.setReminderTime(context, formatted)
                        }
                    }
                }) {
                    Text("Change")
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Milk Price Input
            OutlinedTextField(
                value = milkPrice,
                onValueChange = {
                    milkPrice = it
                    scope.launch {
                        SettingsDataStore.setMilkPrice(context, it)
                    }
                },
                label = { Text("Milk Price (per litre)") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )
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
