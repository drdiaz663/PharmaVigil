package com.lyra.infirmeriestock.worker

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.lyra.infirmeriestock.R

object NotificationHelper {
    private const val CHANNEL_EXPIRY = "expiry_alerts"
    private const val CHANNEL_STOCK = "stock_alerts"

    fun createChannels(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            manager.createNotificationChannel(
                NotificationChannel(CHANNEL_EXPIRY, "Alertes pÃ©remption", NotificationManager.IMPORTANCE_HIGH)
            )
            manager.createNotificationChannel(
                NotificationChannel(CHANNEL_STOCK, "Alertes stock bas", NotificationManager.IMPORTANCE_HIGH)
            )
        }
    }

    fun postExpiryAlert(context: Context, productName: String, days: Int) {
        val notification = NotificationCompat.Builder(context, CHANNEL_EXPIRY)
            .setSmallIcon(R.drawable.ic_notification)
            .setContentTitle("âš ï¸ PÃ©remption proche")
            .setContentText(" expire dans  jour(s)")
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .build()

        NotificationManagerCompat.from(context).notify(
            productName.hashCode(),
            notification
        )
    }

    fun postLowStockAlert(context: Context, productName: String, quantity: Int, location: String) {
        val notification = NotificationCompat.Builder(context, CHANNEL_STOCK)
            .setSmallIcon(R.drawable.ic_notification)
            .setContentTitle("ðŸ“¦ Stock bas")
            .setContentText(" : reste  unitÃ©(s) ()")
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .build()

        NotificationManagerCompat.from(context).notify(
            "low_".hashCode(),
            notification
        )
    }
}
