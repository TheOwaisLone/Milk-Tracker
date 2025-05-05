package com.owais.milktracker

import android.Manifest
import android.annotation.SuppressLint
import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import androidx.core.net.toUri
import androidx.navigation.compose.rememberNavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.owais.milktracker.alarm.MilkReminderReceiver
import com.owais.milktracker.ui.calendar.CalendarScreen
import com.owais.milktracker.ui.settings.SettingsScreen
import com.owais.milktracker.utils.NotificationUtils
import java.time.LocalDateTime
import java.time.ZoneId

class MainActivity : ComponentActivity() {

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val openEntry = intent?.getBooleanExtra("open_entry_for_today", false) ?: false

        NotificationUtils.createNotificationChannel(this)
        requestNotificationPermission()
        checkAndRequestExactAlarmPermission()

        setContent {
            val navController = rememberNavController()

            NavHost(navController = navController, startDestination = "calendar") {
                composable("calendar") {
                    CalendarScreen(
                        openEntryForToday = openEntry,
                        onSettingsClick = {
                            navController.navigate("settings")
                        }
                    )
                }
                composable("settings") {
                    SettingsScreen(
                        onBack = {
                            navController.popBackStack()
                        }
                    )
                }

            }
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
            now.withHour(20).withMinute(0).withSecond(0).withNano(0)
        } else {
            now.plusDays(1).withHour(20).withMinute(0).withSecond(0).withNano(0)
        }
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

    private fun checkAndRequestExactAlarmPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val alarmManager = getSystemService(AlarmManager::class.java)
            if (alarmManager.canScheduleExactAlarms()) {
                scheduleExactAlarm()
            } else {
                val intent = Intent(Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM)
                intent.data = "package:$packageName".toUri()
                startActivity(intent)
            }
        } else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                scheduleExactAlarm()
            }
        }
    }
}
