package com.owais.milktracker.alarm

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import com.owais.milktracker.utils.NotificationUtils

class MilkReminderReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent?) {
        NotificationUtils.showReminderNotification(context)
        Log.d("MilkReminderReceiver", "Alarm received, showing notification")

    }
}
