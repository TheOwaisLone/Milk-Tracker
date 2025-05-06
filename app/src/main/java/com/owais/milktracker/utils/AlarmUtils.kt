package com.owais.milktracker.utils

import android.os.Build
import androidx.annotation.RequiresApi
import java.time.LocalDateTime
import java.time.ZoneId

object AlarmUtils {
    @RequiresApi(Build.VERSION_CODES.O)
    fun getNextTriggerTimeMillis(hour: Int, minute: Int): Long {
        val now = LocalDateTime.now()
        val nextTrigger = if (now.hour < hour || (now.hour == hour && now.minute < minute)) {
            now.withHour(hour).withMinute(minute).withSecond(0).withNano(0)
        } else {
            now.plusDays(1).withHour(hour).withMinute(minute).withSecond(0).withNano(0)
        }
        return nextTrigger.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli()
    }
}
