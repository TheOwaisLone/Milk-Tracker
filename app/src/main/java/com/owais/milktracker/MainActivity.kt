package com.owais.milktracker

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.owais.milktracker.ui.theme.MilkTrackerTheme
import com.owais.milktracker.ui.calendar.CalendarScreen

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MilkTrackerTheme {
                CalendarScreen()
            }
        }
    }
}
