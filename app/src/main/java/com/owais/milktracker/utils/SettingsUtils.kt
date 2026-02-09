package com.owais.milktracker.utils

import android.app.TimePickerDialog
import android.content.Context
import android.widget.TimePicker
import java.util.Calendar

object SettingsUtils {

    fun showTimePickerDialog(context: Context, onTimeSelected: (Int, Int) -> Unit) {
        val calendar = Calendar.getInstance()
        TimePickerDialog(
            context,
            { _: TimePicker, h: Int, m: Int -> onTimeSelected(h, m) },
            calendar.get(Calendar.HOUR_OF_DAY),
            calendar.get(Calendar.MINUTE),
            false
        ).show()
    }

    fun formatTime(hour: Int, minute: Int): String {
        val isPM = hour >= 12
        val h = if (hour % 12 == 0) 12 else hour % 12
        return "$h:${minute.toString().padStart(2, '0')} ${if (isPM) "PM" else "AM"}"
    }

    fun parseTime(time: String): Pair<Int, Int> {
        if (time.isBlank()) return 20 to 0
        val parts = time.trim().split(" ", ":")
        if (parts.size != 3) return 20 to 0

        var hour = parts[0].toIntOrNull() ?: return 20 to 0
        val minute = parts[1].toIntOrNull() ?: return 20 to 0
        val isPM = parts[2].uppercase() == "PM"

        if (isPM && hour != 12) hour += 12
        if (!isPM && hour == 12) hour = 0

        return hour to minute
    }

}