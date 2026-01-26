package com.owais.milktracker

import android.Manifest
import android.content.Intent
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import com.owais.milktracker.alarm.ReminderManager
import com.owais.milktracker.data.SettingsDataStore
import com.owais.milktracker.ui.onboarding.OnboardingPager
import com.owais.milktracker.ui.theme.MilkTrackerTheme
import com.owais.milktracker.utils.SettingsPreferences
import com.owais.milktracker.utils.SettingsPreferences.getReminderSettingsOnce
import com.owais.milktracker.utils.requestExactAlarmPermission
import com.owais.milktracker.viewmodel.SettingsViewModel
import com.owais.milktracker.viewmodel.SettingsViewModelFactory
import kotlinx.coroutines.launch

class OnboardingActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // ✅ Enable edge-to-edge properly
        WindowCompat.setDecorFitsSystemWindows(window, false)

        setContent {
            val context = LocalContext.current
            val view = LocalView.current
            val scope = rememberCoroutineScope()

            // 🌗 Observe theme
            val isDarkMode by SettingsDataStore
                .getDarkMode(context)
                .collectAsState(initial = false)

            // ✅ FIX: Status bar icons visibility
            LaunchedEffect(isDarkMode) {
                val controller = WindowInsetsControllerCompat(window, view)
                // Light theme → dark icons
                controller.isAppearanceLightStatusBars = !isDarkMode
            }

            val viewModel: SettingsViewModel =
                viewModel(factory = SettingsViewModelFactory(context))

            // 🔑 Callback holder for permission result
            var permissionResultCallback by remember {
                mutableStateOf<(Boolean) -> Unit>({})
            }

            val notificationLauncher =
                rememberLauncherForActivityResult(
                    ActivityResultContracts.RequestPermission()
                ) { granted ->
                    permissionResultCallback(granted)
                }

            MilkTrackerTheme(darkTheme = isDarkMode) {
                Surface(modifier = Modifier.fillMaxSize()) {
                    OnboardingPager(
                        viewModel = viewModel,

                        onRequestNotificationPermission = { onResult ->
                            permissionResultCallback = onResult

                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                                notificationLauncher.launch(
                                    Manifest.permission.POST_NOTIFICATIONS
                                )
                            } else {
                                onResult(true) // Pre-Android 13
                            }
                        },

                        onFinish = {
                            scope.launch {
                                val (enabled, hour, minute) =
                                    getReminderSettingsOnce(context)

                                if (enabled) {
                                    ReminderManager.scheduleDailyReminder(
                                        context, hour, minute
                                    )
                                } else {
                                    ReminderManager.cancelReminder(context)
                                }

                                requestExactAlarmPermission(context)
                                SettingsPreferences.setOnboardingDone(context)

                                startActivity(
                                    Intent(context, MainActivity::class.java)
                                )
                                finish()
                            }
                        }
                    )
                }
            }
        }
    }
}
