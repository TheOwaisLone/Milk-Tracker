package com.owais.milktracker.ui.settings

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
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
import android.content.Intent
import android.app.Activity
import com.owais.milktracker.alarm.ReminderManager
import com.owais.milktracker.utils.SettingsUtils.formatTime
import com.owais.milktracker.utils.SettingsUtils.showTimePickerDialog
import com.owais.milktracker.utils.SettingsUtils.parseTime
import com.owais.milktracker.utils.DataImportExportUtils
import com.owais.milktracker.viewmodel.SettingsViewModel
import com.owais.milktracker.viewmodel.SettingsViewModelFactory
import com.owais.milktracker.viewmodel.MilkViewModel
import com.owais.milktracker.viewmodel.MilkViewModelFactory
import kotlinx.coroutines.launch



@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(onBack: () -> Unit) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val settingsViewModel: SettingsViewModel =
        viewModel(factory = SettingsViewModelFactory(context))
    val milkViewModel: MilkViewModel =
        viewModel(factory = MilkViewModelFactory(context))

    val reminderEnabled by settingsViewModel.reminderEnabled.collectAsState(initial = true)
    val hour by settingsViewModel.reminderHour.collectAsState(initial = 20)
    val minute by settingsViewModel.reminderMinute.collectAsState(initial = 0)
    val milkPrice by settingsViewModel.milkPrice.collectAsState()
    val isDarkMode by settingsViewModel.isDarkMode.collectAsState(initial = false)
    val entries by milkViewModel.entries.collectAsState()

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

            // Dark Mode
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

            // Reminder
            ElevatedCard(modifier = Modifier.fillMaxWidth()) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text("Daily Reminder", modifier = Modifier.weight(1f))
                        Switch(
                            checked = reminderEnabled,
                            onCheckedChange = { enabled ->
                                val (h, m) = parseTime(reminderTime)
                                scope.launch {
                                    viewModel.updateReminder(enabled, h, m)
                                    if (enabled) {
                                        ReminderManager.scheduleDailyReminder(context, h, m)
                                        snackbarHostState.showSnackbar(
                                            "Reminder enabled for $reminderTime"
                                        )
                                    } else {
                                        ReminderManager.cancelReminder(context)
                                        snackbarHostState.showSnackbar("Reminder disabled")
                                    }
                                }
                            }
                        )
                    }

                    HorizontalDivider()

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text("Reminder Time: $reminderTime", modifier = Modifier.weight(1f))
                        OutlinedButton(onClick = {
                            showTimePickerDialog(context) { h, m ->
                                reminderTime = formatTime(h, m)
                                scope.launch {
                                    viewModel.updateReminder(reminderEnabled, h, m)
                                    if (reminderEnabled) {
                                        ReminderManager.scheduleDailyReminder(context, h, m)
                                    }
                                    snackbarHostState.showSnackbar(
                                        "Reminder time updated to $reminderTime"
                                    )
                                }
                            }
                        }) {
                            Text("Change")
                        }
                    }
                }
            }

            // Milk Price
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
                            val price = milkPriceInput.toFloatOrNull() ?: 35f
                            scope.launch {
                                viewModel.updateMilkPrice(price)
                                snackbarHostState.showSnackbar(
                                    "Milk price updated to ₹${"%.2f".format(price)}"
                                )
                            }
                        },
                        modifier = Modifier.align(Alignment.End)
                    ) {
                        Text("Save")
                    }
                }
            }

            //Import/Export (Placeholder)
            ElevatedCard(modifier = Modifier.fillMaxWidth()) {
                Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Text("Data Management", style = MaterialTheme.typography.titleMedium)
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Button(
                                onClick = {/* importDataFromCSV(viewModel.) */},
                                modifier = Modifier.weight(1f)
                            ) {
                                Text("Import Data")
                            }

                            Spacer(modifier = Modifier.width(25.dp))

                            Button(
                                onClick = { /* exportDataToCSV(viewModel.getAllEntries)*/ },
                                modifier = Modifier.weight(1f)
                            ) {
                                Text("Export Data")
                            }
                        }
                    }
                }
        }
    }
}


