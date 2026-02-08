package com.owais.milktracker.ui.onboarding.pages

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AccessAlarm
import androidx.compose.material.icons.outlined.DarkMode
import androidx.compose.material.icons.outlined.Schedule
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.owais.milktracker.ui.onboarding.OnboardingScaffold
import com.owais.milktracker.ui.settings.formatTime
import com.owais.milktracker.ui.settings.showTimePickerDialog
import com.owais.milktracker.viewmodel.SettingsViewModel

@Composable
fun ReminderTimePage(
    viewModel: SettingsViewModel,
    onBack: () -> Unit,
    onNext: () -> Unit
) {
    val context = LocalContext.current
    val hour by viewModel.reminderHour.collectAsState(20)
    val minute by viewModel.reminderMinute.collectAsState(0)
    var time by remember { mutableStateOf(formatTime(hour, minute)) }

    OnboardingScaffold(
        icon = { Icon(Icons.Outlined.Schedule, null, Modifier.size(72.dp)) },
        title = "Reminder Time",
        description = "Set the time for your daily reminder. This will be the time you receive a notification to log your milk entry each day." +
                "\n\n You can change this time later in settings.",
        showBack = true,
        onBack = onBack,
        content = {
            Box(Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                OutlinedButton(
                    onClick = {
                        showTimePickerDialog(context) { h, m ->
                            time = formatTime(h, m)
                            viewModel.updateReminder(true, h, m)
                        }
                    }
                ) {
                    Text(time)
                }
            }
        },
        primaryButton = "Next",
        onPrimaryClick = onNext
    )
}


