package com.owais.milktracker.ui.onboarding

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.owais.milktracker.ui.settings.formatTime
import com.owais.milktracker.ui.settings.showTimePickerDialog
import com.owais.milktracker.viewmodel.SettingsViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun OnboardingPager(
    viewModel: SettingsViewModel,
    onRequestNotificationPermission: ((Boolean) -> Unit) -> Unit,
    onFinish: () -> Unit
) {
    // ✅ 7 pages now
    val pagerState = rememberPagerState { 7 }
    val scope = rememberCoroutineScope()

    fun goTo(page: Int) {
        scope.launch { pagerState.animateScrollToPage(page) }
    }

    HorizontalPager(
        state = pagerState,
        modifier = Modifier.fillMaxSize(),
        userScrollEnabled = false
    ) { page ->

        when (page) {

            // 0 — Welcome
            0 -> OnboardingScaffold(
                icon = { Icon(Icons.Outlined.TrackChanges, null, Modifier.size(72.dp)) },
                title = "Welcome to MilkTracker",
                description = "Track milk expenses and never miss your daily reminder.",
                showBack = false,
                primaryButton = "Get Started",
                onPrimaryClick = { goTo(1) }
            )

            // 1 — Theme
            1 -> {
                val isDark by viewModel.isDarkMode.collectAsState(initial = false)

                OnboardingScaffold(
                    icon = { Icon(Icons.Outlined.DarkMode, null, Modifier.size(72.dp)) },
                    title = "Choose Theme",
                    description = "Pick the look that feels right to you.",
                    showBack = true,
                    onBack = { goTo(0) },
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
                    onPrimaryClick = { goTo(2) }
                )
            }

            // 2 — Reminder toggle
            2 -> {
                val enabled by viewModel.reminderEnabled.collectAsState(initial = true)
                val hour by viewModel.reminderHour.collectAsState(initial = 20)
                val minute by viewModel.reminderMinute.collectAsState(initial = 0)

                OnboardingScaffold(
                    icon = { Icon(Icons.Outlined.AccessAlarm, null, Modifier.size(72.dp)) },
                    title = "Daily Reminder",
                    description = "Enable reminders to log milk usage daily.",
                    showBack = true,
                    onBack = { goTo(1) },
                    content = {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("Off")
                            Switch(
                                checked = enabled,
                                onCheckedChange = {
                                    viewModel.updateReminder(it, hour, minute)
                                }
                            )
                            Text("On")
                        }
                    },
                    primaryButton = "Next",
                    onPrimaryClick = { if (enabled) goTo(3) else goTo(4)
                    }
                )
            }

            // 3 — Reminder time
            3 -> {
                val context = LocalContext.current
                val enabled by viewModel.reminderEnabled.collectAsState(initial = true)
                val hour by viewModel.reminderHour.collectAsState(initial = 20)
                val minute by viewModel.reminderMinute.collectAsState(initial = 0)

                var timeText by remember { mutableStateOf(formatTime(hour, minute)) }

                OnboardingScaffold(
                    icon = { Icon(Icons.Outlined.Schedule, null, Modifier.size(72.dp)) },
                    title = "Reminder Time",
                    description = "Choose when you want to be reminded.",
                    showBack = true,
                    onBack = { goTo(2) },
                    content = {
                        OutlinedButton(
                            enabled = enabled,
                            onClick = {
                                showTimePickerDialog(context) { h, m ->
                                    timeText = formatTime(h, m)
                                    viewModel.updateReminder(enabled, h, m)
                                }
                            }
                        ) {
                            Text(timeText)
                        }
                    },
                    primaryButton = "Next",
                    onPrimaryClick = { goTo(4) }
                )
            }

            // 4 — Milk Price
            4 -> {
                val price by viewModel.milkPrice.collectAsState(initial = 35f)
                val enabled by viewModel.reminderEnabled.collectAsState(initial = true)
                var input by remember { mutableStateOf(price.toString()) }

                OnboardingScaffold(
                    icon = { Icon(Icons.Outlined.CurrencyRupee, null, Modifier.size(72.dp)) },
                    title = "Milk Price",
                    description = "Set the price per litre to track expenses accurately.",
                    showBack = true,

                onBack = {
                    if (enabled) {
                        goTo(3)
                    } else {
                        goTo(2)
                    }
                },

                content = {
                        OutlinedTextField(
                            value = input,
                            onValueChange = { input = it },
                            singleLine = true,
                            label = { Text("Price per litre (₹)") },
                            keyboardOptions = KeyboardOptions(
                                keyboardType = KeyboardType.Number
                            ),
                            modifier = Modifier.fillMaxWidth()
                        )
                    },
                    primaryButton = "Next",
                    onPrimaryClick = {
                        val value = input.toFloatOrNull() ?: 35f
                        viewModel.updateMilkPrice(value)
                        goTo(5)
                    }
                )
            }

            // 5 — Notification Permission
            5 -> {
                var granted by remember { mutableStateOf(false) }

                OnboardingScaffold(
                    icon = { Icon(Icons.Outlined.Notifications, null, Modifier.size(72.dp)) },
                    title = "Enable Notifications",
                    description = if (!granted) {
                        "We need notification permission to remind you daily."
                    } else {
                        "Notifications enabled. You’re all set!"
                    },
                    showBack = true,
                    onBack = { goTo(4) },
                    primaryButton = if (granted) "Next" else "Allow",
                    onPrimaryClick = {
                        if (!granted) {
                            onRequestNotificationPermission { result ->
                                granted = result
                            }
                        } else {
                            goTo(6)
                        }
                    }
                )
            }

            // 6 — Finish
            6 -> OnboardingScaffold(
                icon = { Icon(Icons.Outlined.CheckCircle, null, Modifier.size(72.dp)) },
                title = "All Set!",
                description = "You’re ready to use MilkTracker.\n\nYou can change settings anytime.",
                showBack = true,
                onBack = { goTo(5) },
                primaryButton = "Start Using App",
                onPrimaryClick = onFinish
            )
        }
    }
}
