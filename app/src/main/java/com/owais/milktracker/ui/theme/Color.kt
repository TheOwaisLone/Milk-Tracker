package com.owais.milktracker.ui.theme

import androidx.compose.ui.graphics.Color
import androidx.compose.material3.lightColorScheme
import androidx.compose.material3.darkColorScheme

val md_theme_light_primary = Color(0xFF4CAF50)
val md_theme_light_onPrimary = Color.White
val md_theme_light_secondary = Color(0xFFFFC107)
val md_theme_light_onSecondary = Color.Black
val md_theme_light_background = Color(0xFFF5F5F5)
val md_theme_light_onBackground = Color.Black
val md_theme_light_surface = Color.White
val md_theme_light_onSurface = Color.Black
val md_theme_light_error = Color(0xFFB00020)
val md_theme_light_onError = Color.White

val md_theme_dark_primary = Color(0xFF81C784)
val md_theme_dark_onPrimary = Color.Black
val md_theme_dark_secondary = Color(0xFFFFD54F)
val md_theme_dark_onSecondary = Color.Black
val md_theme_dark_background = Color(0xFF121212)
val md_theme_dark_onBackground = Color.White
val md_theme_dark_surface = Color(0xFF1E1E1E)
val md_theme_dark_onSurface = Color.White
val md_theme_dark_error = Color(0xFFCF6679)
val md_theme_dark_onError = Color.Black

val LightColorScheme = lightColorScheme(
    primary = md_theme_light_primary,
    onPrimary = md_theme_light_onPrimary,
    secondary = md_theme_light_secondary,
    onSecondary = md_theme_light_onSecondary,
    background = md_theme_light_background,
    onBackground = md_theme_light_onBackground,
    surface = md_theme_light_surface,
    onSurface = md_theme_light_onSurface,
    error = md_theme_light_error,
    onError = md_theme_light_onError
)

val DarkColorScheme = darkColorScheme(
    primary = md_theme_dark_primary,
    onPrimary = md_theme_dark_onPrimary,
    secondary = md_theme_dark_secondary,
    onSecondary = md_theme_dark_onSecondary,
    background = md_theme_dark_background,
    onBackground = md_theme_dark_onBackground,
    surface = md_theme_dark_surface,
    onSurface = md_theme_dark_onSurface,
    error = md_theme_dark_error,
    onError = md_theme_dark_onError
)
