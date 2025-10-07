package com.example.energymatev01

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat

class HydrationReminderReceiver : BroadcastReceiver() {

    companion object {
        const val CHANNEL_ID = "hydration_channel"
        const val NOTIFICATION_ID = 1001
    }

    override fun onReceive(context: Context, intent: Intent) {
        // Check if reminders are still enabled
        val prefs = context.getSharedPreferences("HydrationSettings", Context.MODE_PRIVATE)
        val isReminderEnabled = prefs.getBoolean("isReminderEnabled", false)
        
        if (!isReminderEnabled) {
            return // Don't show notification if reminders are disabled
        }

        createNotificationChannel(context)
        showHydrationReminder(context)
    }

    private fun createNotificationChannel(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = "Hydration Reminders"
            val descriptionText = "Channel for hydration reminder notifications"
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(CHANNEL_ID, name, importance).apply {
                description = descriptionText
            }
            val notificationManager: NotificationManager =
                context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    private fun showHydrationReminder(context: Context) {
        val builder = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.baseline_notifications_24)
            .setContentTitle("💧 Hydration Reminder")
            .setContentText("Time to drink some water! Stay hydrated and healthy.")
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setAutoCancel(true)
            .setDefaults(NotificationCompat.DEFAULT_ALL)

        with(NotificationManagerCompat.from(context)) {
            try {
                notify(NOTIFICATION_ID, builder.build())
            } catch (e: SecurityException) {
                // Handle case where notification permission is not granted
                e.printStackTrace()
            }
        }
    }
}
