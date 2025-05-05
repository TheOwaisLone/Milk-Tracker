package com.owais.milktracker.ui.theme


import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.material3.lightColorScheme


@Composable
fun SplashScreenTheme(content: @Composable () -> Unit) {
    // Define a theme for the splash screen
    MaterialTheme(
        colorScheme = lightColorScheme(
            primary = Color.Transparent,  // Transparent background for splash screen
            surface = Color.Transparent,  // Transparent surface
            background = Color.White,    // White background for splash screen
        ),
        typography = Typography,  // Assuming Typography is defined in your Theme.kt
        content = content
    )
}
