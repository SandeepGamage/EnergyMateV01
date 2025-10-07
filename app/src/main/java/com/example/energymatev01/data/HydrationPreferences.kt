package com.example.energymatev01.data

import android.content.Context
import android.content.SharedPreferences

class HydrationPreferences(context: Context) {

    private val prefs: SharedPreferences = context.getSharedPreferences("hydration_data", Context.MODE_PRIVATE)
    private val hydrationSettingPref: SharedPreferences = context.getSharedPreferences("HydrationSettings", Context.MODE_PRIVATE)

    fun getGlassesCount(): Int = prefs.getInt("glasses", 0)
    fun setGlassesCount(count: Int) { prefs.edit().putInt("glasses", count).apply() }

    fun getDailyGoal(): Int = prefs.getInt("goal", 7)
    fun setDailyGoal(goal: Int) { prefs.edit().putInt("goal", goal).apply() }

    fun setReminderEnabled(enabled: Boolean) {
        hydrationSettingPref.edit().putBoolean("isReminderEnabled", enabled).apply()
    }
    fun isReminderEnabled(): Boolean {
        return hydrationSettingPref.getBoolean("isReminderEnabled", false)
    }

    fun getReminderFrequency(): Int {
        return hydrationSettingPref.getInt("frequency", 0)
    }

    fun getReminderStartTime(): String {
        return hydrationSettingPref.getString("startTime", "Not set") ?: "Not set"
    }

    fun getReminderEndTime(): String {
        return hydrationSettingPref.getString("endTime", "Not set") ?: "Not set"
    }

    fun clearAll() { prefs.edit().clear().apply() }
}
