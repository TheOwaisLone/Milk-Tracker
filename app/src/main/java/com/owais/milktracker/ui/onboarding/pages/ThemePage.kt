package com.owais.milktracker.ui.onboarding.pages

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.DarkMode
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.owais.milktracker.ui.onboarding.OnboardingScaffold
import com.owais.milktracker.viewmodel.SettingsViewModel

@Composable
fun ThemePage(
    viewModel: SettingsViewModel,
    onBack: () -> Unit,
    onNext: () -> Unit
) {
    val isDark by viewModel.isDarkMode.collectAsState(false)

    OnboardingScaffold(
        icon = {
            Icon(Icons.Outlined.DarkMode, null, Modifier.size(72.dp))
        },
        title = "Theme",
        description = "Light or Dark mode",
        showBack = true,
        onBack = onBack,
        content = {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Light")
                Switch(
                    checked = isDark,
                    onCheckedChange = { viewModel.updateDarkMode(it) }
                )
                Text("Dark")
            }
        },
        primaryButton = "Next",
        onPrimaryClick = onNext
    )
}
