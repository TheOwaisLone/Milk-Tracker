package com.owais.milktracker

import android.Manifest
import android.annotation.SuppressLint
import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import com.owais.milktracker.alarm.MilkReminderReceiver
import com.owais.milktracker.ui.calendar.CalendarScreen
import com.owais.milktracker.utils.NotificationUtils
import java.time.LocalDateTime
import java.time.ZoneId

class MainActivity : ComponentActivity() {

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Check if the app was opened from a notification (deep-linking to entry)
        val openEntry = intent?.getBooleanExtra("open_entry_for_today", false) ?: false

        // Set up notification channel and permissions
        NotificationUtils.createNotificationChannel(this)
        requestNotificationPermission()

        // Schedule daily reminder at 8 PM
        scheduleExactAlarm()

        setContent {
            CalendarScreen(openEntryForToday = openEntry)
        }
    }

    @SuppressLint("ScheduleExactAlarm")
    @RequiresApi(Build.VERSION_CODES.O)
    private fun scheduleExactAlarm() {
        val alarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(this, MilkReminderReceiver::class.java)
        val pendingIntent = PendingIntent.getBroadcast(
            this, 0, intent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

        // Get the next 8:00 PM time in milliseconds
        val triggerTime = getNext8PMTimeInMillis()

        // Set exact alarm to trigger once a day at 8 PM
        alarmManager.setExactAndAllowWhileIdle(
            AlarmManager.RTC_WAKEUP,
            triggerTime,
            pendingIntent
        )
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun getNext8PMTimeInMillis(): Long {
        val now = LocalDateTime.now()

        // Calculate next 8:00 PM (20:00) based on the current time
        val next8PM = if (now.hour < 20) {
            now.withHour(20).withMinute(0).withSecond(0).withNano(0)
        } else {
            now.plusDays(1).withHour(20).withMinute(0).withSecond(0).withNano(0)
        }

        // Return the time in milliseconds
        return next8PM.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli()
    }

    private fun requestNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            val permission = Manifest.permission.POST_NOTIFICATIONS
            val isGranted = ContextCompat.checkSelfPermission(this, permission) ==
                    android.content.pm.PackageManager.PERMISSION_GRANTED

            if (!isGranted) {
                val requestPermissionLauncher =
                    registerForActivityResult(ActivityResultContracts.RequestPermission()) {}
                requestPermissionLauncher.launch(permission)
            }
        }
    }
}
