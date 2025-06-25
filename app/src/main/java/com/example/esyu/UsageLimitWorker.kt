package com.example.esyu

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import kotlinx.coroutines.flow.firstOrNull

class UsageLimitWorker(
    context: Context,
    workerParams: WorkerParameters
) : CoroutineWorker(context, workerParams) {
    override suspend fun doWork(): Result {
        val usageStats = getUsagesForDay(applicationContext)
        for ((packageName, appName, timeMs) in usageStats) {
            val limitFlow = getLimit(applicationContext, packageName)
            val limitMinutes = limitFlow.firstOrNull()
            val usedMinutes = (timeMs / 1000 / 60).toInt()
            if (limitMinutes != null && usedMinutes > limitMinutes) {
                sendUsageLimitNotification(applicationContext, appName, usedMinutes)
            }
        }
        return Result.success()
    }
}