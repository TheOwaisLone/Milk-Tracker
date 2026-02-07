package com.owais.milktracker.ui.onboarding

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import com.owais.milktracker.ui.onboarding.pages.*
import com.owais.milktracker.utils.XiaomiAutoStart
import com.owais.milktracker.utils.BatteryOptimizationUtils.openBatteryOptimizationSettings
import com.owais.milktracker.viewmodel.SettingsViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun OnboardingPager(
    viewModel: SettingsViewModel,
    onRequestNotificationPermission: ((Boolean) -> Unit) -> Unit,
    onFinish: () -> Unit
) {
    val pagerState = rememberPagerState { 9 }
    val scope = rememberCoroutineScope()
    val context = LocalContext.current
    val isXiaomi = remember { XiaomiAutoStart.isXiaomiDevice() }

    fun goTo(page: Int) {
        scope.launch { pagerState.animateScrollToPage(page) }
    }

    HorizontalPager(
        state = pagerState,
        modifier = Modifier.fillMaxSize(),
        userScrollEnabled = false
    ) { page ->

        val reminderEnabled by viewModel.reminderEnabled.collectAsState(initial = true)

        when (page) {
            0 -> WelcomePage { goTo(1) }

            1 -> ThemePage(
                viewModel = viewModel,
                onBack = { goTo(0) },
                onNext = { goTo(2) }
            )

            2 -> ReminderTogglePage(
                viewModel = viewModel,
                onBack = { goTo(1) },
                onNext = {
                    goTo(if (reminderEnabled) 3 else 4)
                }
            )

            3 -> ReminderTimePage(
                viewModel = viewModel,
                onBack = { goTo(2) },
                onNext = { goTo(4) }
            )

            4 -> MilkPricePage(
                viewModel = viewModel,
                reminderEnabled = reminderEnabled,
                onBack = { goTo(if (reminderEnabled) 3 else 2) },
                onNext = { goTo(5) }
            )

            5 -> NotificationPermissionPage(
                onBack = { goTo(4) },
                onRequestPermission = onRequestNotificationPermission,
                onGranted = {
                    goTo(if (isXiaomi) 6 else 7)
                }
            )

            6 -> XiaomiAutoStartPage(
                onBack = { goTo(5) },
                onNext = { goTo(7) },
                openAutoStartSettings = {
                    XiaomiAutoStart.open(context)
                }
            )

            7 -> BatteryOptimizationPage(
                onBack = { goTo(if (isXiaomi) 6 else 5) },
                onNext = { goTo(8) },
                openBatterySettings = {
                    openBatteryOptimizationSettings(context)
                }
            )

            8 -> FinishPage(
                onBack = { goTo(7) },
                onFinish = onFinish,
                isXiaomi = isXiaomi
            )
        }
    }
}
