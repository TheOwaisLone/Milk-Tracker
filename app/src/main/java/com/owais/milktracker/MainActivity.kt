package com.owais.milktracker

import android.Manifest
import android.annotation.SuppressLint
import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import java.time.ZoneId
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import androidx.work.*
import com.owais.milktracker.alarm.MilkReminderReceiver
import com.owais.milktracker.ui.calendar.CalendarScreen
import com.owais.milktracker.utils.NotificationUtils
import com.owais.milktracker.worker.MilkReminderWorker
import java.time.LocalDateTime
import java.util.concurrent.TimeUnit

class MainActivity : ComponentActivity() {

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val openEntry = intent?.getBooleanExtra("open_entry_for_today", false) ?: false


        NotificationUtils.createNotificationChannel(this)
        requestNotificationPermission()

        // 🔔 Add this line
        scheduleExactAlarm() // Schedule 8 PM daily notification

        // Optional: remove WorkManager unless you want a backup reminder
        // scheduleDailyReminder()

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

        val triggerTime = getNext8PMTimeInMillis()

        // Schedule exact alarm
        alarmManager.setExactAndAllowWhileIdle(
            AlarmManager.RTC_WAKEUP,
            triggerTime,
            pendingIntent
        )
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun getNext8PMTimeInMillis(): Long {
        val now = LocalDateTime.now()
        val next8PM = if (now.hour < 20) {
            now.withHour(13).withMinute(54).withSecond(0).withNano(0)
        } else {
            now.plusDays(1).withHour(13).withMinute(54).withSecond(0).withNano(0)
        }
        return next8PM.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli()
    }




    @RequiresApi(Build.VERSION_CODES.O)
    private fun scheduleDailyReminder() {
        val workManager = WorkManager.getInstance(applicationContext)

        val workRequest = PeriodicWorkRequestBuilder<MilkReminderWorker>(
            1, TimeUnit.DAYS
        )
            .setInitialDelay(calculateInitialDelay(), TimeUnit.MILLISECONDS)
            .build()

        workManager.enqueueUniquePeriodicWork(
            "MilkReminderWork",
            ExistingPeriodicWorkPolicy.KEEP,
            workRequest
        )
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun calculateInitialDelay(): Long {
        val now = LocalDateTime.now()
        val targetTime = now.withHour(13).withMinute(45).withSecond(0).withNano(0)
        val delayDuration = if (now < targetTime) {
            java.time.Duration.between(now, targetTime)
        } else {
            java.time.Duration.between(now, targetTime.plusDays(1))
        }
        return delayDuration.seconds * 1000  // Use seconds * 1000 for milliseconds
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
