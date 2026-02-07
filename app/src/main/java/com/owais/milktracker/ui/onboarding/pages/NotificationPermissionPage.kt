package com.owais.milktracker.ui.onboarding.pages

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AccessAlarm
import androidx.compose.material.icons.outlined.DarkMode
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.owais.milktracker.ui.onboarding.OnboardingScaffold
import com.owais.milktracker.viewmodel.SettingsViewModel

@Composable
fun NotificationPermissionPage(
    onBack: () -> Unit,
    onRequestPermission: ((Boolean) -> Unit) -> Unit,
    onGranted: () -> Unit
) {
    var granted by remember { mutableStateOf(false) }

    OnboardingScaffold(
        icon = { Icon(Icons.Outlined.Notifications, null, Modifier.size(72.dp)) },
        title = "Notifications",
        description = "Allow notifications",
        showBack = true,
        onBack = onBack,
        primaryButton = if (granted) "Next" else "Allow",
        onPrimaryClick = {
            if (!granted) {
                onRequestPermission { granted = it }
            } else {
                onGranted()
            }
        }
    )
}
