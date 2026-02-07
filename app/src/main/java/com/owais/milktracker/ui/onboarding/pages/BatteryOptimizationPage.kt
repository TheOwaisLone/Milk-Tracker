package com.owais.milktracker.ui.onboarding.pages

import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.BatterySaver
import androidx.compose.material3.Icon
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.owais.milktracker.ui.onboarding.OnboardingScaffold

@Composable
fun BatteryOptimizationPage(
    onBack: () -> Unit,
    onNext: () -> Unit,
    openBatterySettings: () -> Unit
) {
    var disabled by remember { mutableStateOf(false) }

    OnboardingScaffold(
        icon = {
            Icon(
                imageVector = Icons.Outlined.BatterySaver,
                contentDescription = null,
                modifier = Modifier.size(72.dp)
            )
        },
        title = "Disable Battery Optimization",
        description = if (!disabled) {
            "Disable battery optimization so reminders work even when the app is closed."
        } else {
            "Battery optimization disabled."
        },
        showBack = true,
        onBack = onBack,
        primaryButton = if (disabled) "Next" else "Open Settings",
        onPrimaryClick = {
            if (!disabled) {
                openBatterySettings()
                disabled = true
            } else {
                onNext()
            }
        }
    )
}
