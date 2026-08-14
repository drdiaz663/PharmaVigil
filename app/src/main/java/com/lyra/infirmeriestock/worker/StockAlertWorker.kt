package com.lyra.infirmeriestock.worker

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.lyra.infirmeriestock.data.Location
import com.lyra.infirmeriestock.data.StockRepository
import java.time.LocalDate
import java.time.ZoneId
import java.time.temporal.ChronoUnit

class StockAlertWorker(context: Context, params: WorkerParameters) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result {
        val repo = StockRepository()
        val products = repo.getProducts()
        val today = LocalDate.now()

        products.forEach { product ->
            product.expiryDate?.let { timestamp ->
                val expiry = timestamp.toDate().toInstant()
                    .atZone(ZoneId.systemDefault()).toLocalDate()
                val days = ChronoUnit.DAYS.between(today, expiry)
                if (days in 0..30) {
                    NotificationHelper.postExpiryAlert(applicationContext, product.name, days.toInt())
                }
            }

            if (product.quantity <= product.minStock) {
                NotificationHelper.postLowStockAlert(
                    context = applicationContext,
                    productName = product.name,
                    quantity = product.quantity,
                    location = Location.valueOf(product.location).displayName
                )
            }
        }
        return Result.success()
    }
}
