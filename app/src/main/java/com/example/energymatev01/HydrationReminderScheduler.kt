package com.example.energymatev01

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import java.util.*

class HydrationReminderScheduler(private val context: Context) {

    private val alarmManager: AlarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
    private val sharedPreferences: SharedPreferences = context.getSharedPreferences("HydrationSettings", Context.MODE_PRIVATE)

    companion object {
        const val REMINDER_REQUEST_CODE = 1001
    }

    fun scheduleReminders() {
        val frequency = sharedPreferences.getInt("frequency", 0)
        val startTime = sharedPreferences.getString("startTime", "8:00 AM")
        val endTime = sharedPreferences.getString("endTime", "10:00 PM")
        val isEnabled = sharedPreferences.getBoolean("isReminderEnabled", false)

        if (!isEnabled || frequency == 0 || startTime == null || endTime == null) {
            return
        }

        // Cancel existing alarms first
        cancelReminders()

        // Parse start and end times
        val startHour = parseTimeToHour(startTime)
        val endHour = parseTimeToHour(endTime)
        val frequencyMinutes = frequency / (60 * 1000) // Convert milliseconds to minutes

        // Calculate how many reminders we need between start and end time
        val totalMinutes = endHour - startHour
        val reminderCount = (totalMinutes / frequencyMinutes).toInt()

        // Schedule reminders
        for (i in 0 until reminderCount) {
            val reminderTime = startHour + (i * frequencyMinutes)
            scheduleSingleReminder(reminderTime, i)
        }
    }

    private fun scheduleSingleReminder(minutesFromMidnight: Int, reminderIndex: Int) {
        val calendar = Calendar.getInstance()
        calendar.set(Calendar.HOUR_OF_DAY, minutesFromMidnight / 60)
        calendar.set(Calendar.MINUTE, minutesFromMidnight % 60)
        calendar.set(Calendar.SECOND, 0)
        calendar.set(Calendar.MILLISECOND, 0)

        // If the time has already passed today, schedule for tomorrow
        if (calendar.timeInMillis <= System.currentTimeMillis()) {
            calendar.add(Calendar.DAY_OF_MONTH, 1)
        }

        val intent = Intent(context, HydrationReminderReceiver::class.java)
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            REMINDER_REQUEST_CODE + reminderIndex,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        // Schedule the alarm
        alarmManager.setRepeating(
            AlarmManager.RTC_WAKEUP,
            calendar.timeInMillis,
            AlarmManager.INTERVAL_DAY, // Repeat daily
            pendingIntent
        )
    }

    fun cancelReminders() {
        // Cancel all scheduled reminders
        for (i in 0..20) { // Cancel up to 20 reminders (should be enough)
            val intent = Intent(context, HydrationReminderReceiver::class.java)
            val pendingIntent = PendingIntent.getBroadcast(
                context,
                REMINDER_REQUEST_CODE + i,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
            alarmManager.cancel(pendingIntent)
        }
    }

    fun parseTimeToHour(timeString: String): Int {
        return try {
            val parts = timeString.split(":")
            val hour = parts[0].toInt()
            val minutePart = parts[1].split(" ")
            val minute = minutePart[0].toInt()
            val ampm = if (minutePart.size > 1) minutePart[1].uppercase() else ""

            var totalMinutes = hour * 60 + minute

            // Handle AM/PM
            if (ampm == "PM" && hour != 12) {
                totalMinutes += 12 * 60
            } else if (ampm == "AM" && hour == 12) {
                totalMinutes -= 12 * 60
            }

            totalMinutes
        } catch (e: Exception) {
            // Default to 8 AM if parsing fails
            8 * 60
        }
    }

    fun isReminderScheduled(): Boolean {
        val intent = Intent(context, HydrationReminderReceiver::class.java)
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            REMINDER_REQUEST_CODE,
            intent,
            PendingIntent.FLAG_NO_CREATE or PendingIntent.FLAG_IMMUTABLE
        )
        return pendingIntent != null
    }
}
