package com.owais.milktracker.alarm

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.owais.milktracker.utils.NotificationUtils

class MilkReminderReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent?) {
        NotificationUtils.showReminderNotification(context)
    }
}
