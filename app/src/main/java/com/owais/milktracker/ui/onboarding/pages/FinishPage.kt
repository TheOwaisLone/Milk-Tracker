package com.owais.milktracker.ui.onboarding.pages

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AccessAlarm
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material.icons.outlined.DarkMode
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.owais.milktracker.ui.onboarding.OnboardingScaffold
import com.owais.milktracker.viewmodel.SettingsViewModel

@Composable
fun FinishPage(
    isXiaomi: Boolean,
    onBack: () -> Unit,
    onFinish: () -> Unit
) {
    OnboardingScaffold(
        icon = { Icon(Icons.Outlined.CheckCircle, null, Modifier.size(72.dp)) },
        title = "All Set!",
        description = "You're ready to start tracking your milk consumption and expenses. " +
                "Tap the Start button below to begin using the app." +
                if (isXiaomi) "\n\nNote: If you have a Xiaomi device, please make sure to allow autostart for this app in your device settings to ensure that reminders work properly." else "",
        showBack = true,
        onBack = onBack,
        primaryButton = "Start",
        onPrimaryClick = onFinish
    )
}
