package com.owais.milktracker.ui.onboarding.pages

import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.PowerSettingsNew
import androidx.compose.material3.Icon
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.owais.milktracker.ui.onboarding.OnboardingScaffold
import com.owais.milktracker.utils.XiaomiAutoStart

@Composable
fun XiaomiAutoStartPage(
    onBack: () -> Unit,
    onNext: () -> Unit,
    openAutoStartSettings: () -> Unit
) {
    var enabled by remember { mutableStateOf(false) }

    OnboardingScaffold(
        icon = {
            Icon(
                imageVector = Icons.Outlined.PowerSettingsNew,
                contentDescription = null,
                modifier = Modifier.size(72.dp)
            )
        },
        title = "Allow AutoStart",
        description = if (!enabled) {
            "Xiaomi devices often kill background processes to save battery, which can prevent reminders from working. " +
                    "\n To ensure reliable reminders, please enable AutoStart for Milk Tracker in the settings." +
                    "\n Don’t worry, it’s a one-time setup and won’t affect your battery life." +
                    "\n Tap the button below to open the AutoStart settings and enable it for Milk Tracker." +
                    "\n Once enabled, you can proceed to the next step."
        } else {
            "AutoStart enabled. You’re good to go."
        },
        showBack = true,
        onBack = onBack,
        primaryButton = if (enabled) "Next" else "Open Settings",
        onPrimaryClick = {
            if (!enabled) {
                openAutoStartSettings()
                enabled = true
            } else {
                onNext()
            }
        }
    )
}
