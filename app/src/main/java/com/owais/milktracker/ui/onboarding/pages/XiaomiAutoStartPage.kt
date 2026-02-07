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
            "Xiaomi devices require AutoStart permission for reminders to work reliably."
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
