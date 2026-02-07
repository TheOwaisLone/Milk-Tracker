package com.owais.milktracker.ui.onboarding.pages

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AccessAlarm
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.owais.milktracker.ui.onboarding.OnboardingScaffold
import com.owais.milktracker.viewmodel.SettingsViewModel

@Composable
fun ReminderTogglePage(
    viewModel: SettingsViewModel,
    onBack: () -> Unit,
    onNext: () -> Unit
) {
    val enabled by viewModel.reminderEnabled.collectAsState(true)
    val hour by viewModel.reminderHour.collectAsState(20)
    val minute by viewModel.reminderMinute.collectAsState(0)

    OnboardingScaffold(
        icon = { Icon(Icons.Outlined.AccessAlarm, null, Modifier.size(72.dp)) },
        title = "Daily Reminder",
        description = "Enable daily reminder",
        showBack = true,
        onBack = onBack,
        content = {
            Row(
                Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Off")
                Spacer(Modifier.width(12.dp))
                Switch(
                    checked = enabled,
                    onCheckedChange = {
                        viewModel.updateReminder(it, hour, minute)
                    }
                )
                Spacer(Modifier.width(12.dp))
                Text("On")
            }
        },
        primaryButton = "Next",
        onPrimaryClick = onNext
    )
}
