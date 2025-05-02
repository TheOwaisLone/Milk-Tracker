package com.owais.milktracker.ui.theme

import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.core.os.BuildCompat

@Composable
fun MilkTrackerTheme(content: @Composable () -> Unit) {
    val colorScheme = if (BuildCompat.isAtLeastT()) {
        dynamicLightColorScheme(LocalContext.current)
    } else {
        lightColorScheme()
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography(),
        content = content
    )
}
