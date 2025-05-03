package com.owais.milktracker.worker

import android.content.Context
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.owais.milktracker.utils.NotificationUtils

class MilkReminderWorker(context: Context, workerParams: WorkerParameters) :
    Worker(context, workerParams) {

    override fun doWork(): Result {
        NotificationUtils.showReminderNotification(applicationContext)
        return Result.success()
    }
}
