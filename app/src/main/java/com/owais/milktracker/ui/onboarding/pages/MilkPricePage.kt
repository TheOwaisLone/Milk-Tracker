package com.owais.milktracker.ui.onboarding.pages

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AccessAlarm
import androidx.compose.material.icons.outlined.CurrencyRupee
import androidx.compose.material.icons.outlined.DarkMode
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.owais.milktracker.ui.onboarding.OnboardingScaffold
import com.owais.milktracker.viewmodel.SettingsViewModel

@Composable
fun MilkPricePage(
    viewModel: SettingsViewModel,
    reminderEnabled: Boolean,
    onBack: () -> Unit,
    onNext: () -> Unit
) {
    val price by viewModel.milkPrice.collectAsState(35f)
    var input by remember { mutableStateOf(price.toString()) }

    OnboardingScaffold(
        icon = { Icon(Icons.Outlined.CurrencyRupee, null, Modifier.size(72.dp)) },
        title = "Milk Price",
        description = "Enter the current price of milk per litre. This will help you track your expenses and analyze your spending habits over time." +
                "\n\n You can update this price later in settings if it changes.",
        showBack = true,
        onBack = onBack,
        content = {
            OutlinedTextField(
                value = input,
                onValueChange = { input = it },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                label = { Text("₹ per litre") }
            )
        },
        primaryButton = "Next",
        onPrimaryClick = {
            viewModel.updateMilkPrice(input.toFloatOrNull() ?: 35f)
            onNext()
        }
    )
}
