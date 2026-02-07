package com.owais.milktracker.ui.onboarding.pages

import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.TrackChanges
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.owais.milktracker.ui.onboarding.OnboardingScaffold

@Composable
fun WelcomePage(
    onNext: () -> Unit
) {
    OnboardingScaffold(
        icon = {
            Icon(
                imageVector = Icons.Outlined.TrackChanges,
                contentDescription = null,
                modifier = Modifier.size(72.dp)
            )
        },
        title = "Welcome",
        description = "Track milk expenses and reminders.",
        showBack = false,
        primaryButton = "Get Started",
        onPrimaryClick = onNext
    )
}
