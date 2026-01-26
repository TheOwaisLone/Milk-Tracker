package com.owais.milktracker

import android.content.Intent
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.annotation.RequiresApi
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.owais.milktracker.ui.calendar.CalendarScreen
import com.owais.milktracker.ui.settings.SettingsScreen
import com.owais.milktracker.ui.theme.MilkTrackerTheme
import com.owais.milktracker.utils.NotificationUtils
import com.owais.milktracker.utils.SettingsPreferences
import com.owais.milktracker.viewmodel.SettingsViewModel
import com.owais.milktracker.viewmodel.SettingsViewModelFactory
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        lifecycleScope.launch {

            // 1️⃣ FIX legacy DataStore FIRST (before ViewModel)
            SettingsPreferences.cleanCorruptedMilkPrice(this@MainActivity)

            // 2️⃣ Check onboarding
            val onboardingDone =
                SettingsPreferences.isOnboardingDone(this@MainActivity)

            if (!onboardingDone) {
                startActivity(
                    Intent(this@MainActivity, OnboardingActivity::class.java)
                )
                finish()
                return@launch
            }

            // 3️⃣ Safe to continue
            NotificationUtils.createNotificationChannel(this@MainActivity)

            // 4️⃣ NOW compose UI
            setContent {
                val viewModel: SettingsViewModel = viewModel(
                    factory = SettingsViewModelFactory(applicationContext)
                )
                val isDarkMode by viewModel.isDarkMode.collectAsState(initial = false)
                val navController = rememberNavController()

                MilkTrackerTheme(darkTheme = isDarkMode) {
                    NavHost(
                        navController = navController,
                        startDestination = "calendar"
                    ) {
                        composable("calendar") {
                            CalendarScreen(
                                onSettingsClick = {
                                    navController.navigate("settings")
                                }
                            )
                        }
                        composable("settings") {
                            SettingsScreen(
                                onBack = { navController.popBackStack() }
                            )
                        }
                    }
                }
            }
        }
    }
}
