package com.owais.milktracker

import android.Manifest
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import androidx.work.*
import com.owais.milktracker.ui.calendar.CalendarScreen
import com.owais.milktracker.utils.NotificationUtils
import com.owais.milktracker.worker.MilkReminderWorker
import java.time.Duration
import java.time.LocalDateTime
import java.util.concurrent.TimeUnit

class MainActivity : ComponentActivity() {

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        NotificationUtils.createNotificationChannel(this)
        requestNotificationPermission()
        scheduleDailyReminder()

        setContent {
            CalendarScreen()
        }
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
        val targetTime = now.withHour(20).withMinute(0).withSecond(0) // 8:00 PM
        val delay = if (now < targetTime) {
            Duration.between(now, targetTime)
        } else {
            Duration.between(now, targetTime.plusDays(1))
        }
        return delay.toMillis()
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
