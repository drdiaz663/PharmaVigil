package com.lyra.infirmeriestock

import android.app.Application
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.lyra.infirmeriestock.worker.NotificationHelper
import com.lyra.infirmeriestock.worker.StockAlertWorker
import java.util.concurrent.TimeUnit

class StockApp : Application() {
    override fun onCreate() {
        super.onCreate()
        NotificationHelper.createChannels(this)
        scheduleStockAlerts()
    }

    private fun scheduleStockAlerts() {
        val request = PeriodicWorkRequestBuilder<StockAlertWorker>(6, TimeUnit.HOURS)
            .build()
        WorkManager.getInstance(this).enqueueUniquePeriodicWork(
            "stock_alerts",
            ExistingPeriodicWorkPolicy.UPDATE,
            request
        )
    }
}
