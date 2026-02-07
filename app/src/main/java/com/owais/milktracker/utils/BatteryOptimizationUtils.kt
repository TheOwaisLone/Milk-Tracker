package com.owais.milktracker.utils

import android.content.Context
import android.content.Intent
import android.provider.Settings

object BatteryOptimizationUtils {

    fun openBatteryOptimizationSettings(context: Context) {
        val intent = Intent(Settings.ACTION_IGNORE_BATTERY_OPTIMIZATION_SETTINGS).apply {
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        context.startActivity(intent)
    }
}
