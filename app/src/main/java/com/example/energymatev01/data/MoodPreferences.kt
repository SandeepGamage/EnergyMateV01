package com.example.energymatev01.data

import android.content.Context
import android.content.SharedPreferences
import org.json.JSONArray
import org.json.JSONObject
import java.util.Calendar
import java.util.Date
import kotlin.compareTo

class MoodPreferences (context: Context) {
    private val pref : SharedPreferences = context.getSharedPreferences("mood_data", Context.MODE_PRIVATE)

    fun addEntry(mood: String, note: String){
        val array = JSONArray(pref.getString("entries", "[]"))
        val obj = JSONObject()
            .put("mood", mood)
            .put("note", note)
            .put("timestamp", System.currentTimeMillis())
        array.put(obj)
        pref.edit().putString("entries", array.toString()).apply()
    }

    fun deleteEntry(timestamp: Long){
        val array = JSONArray(pref.getString("entries", "[]"))
        val newArray = JSONArray()
        for(i in 0 until array.length()){
            val obj = array.getJSONObject(i)
            if(obj.optLong("timestamp") != timestamp) newArray.put(obj)
        }
        pref.edit().putString("entries", newArray.toString()).apply()
    }

    fun updateEntry(timestamp: Long, mood: String, note: String){
        val array = JSONArray(pref.getString("entries", "[]"))
        for(i in 0 until array.length()){
            val obj = array.getJSONObject(i)
            if(obj.optLong("timestamp") == timestamp){
                obj.put("mood", mood)
                obj.put("note", note)
                break
            }
        }
        pref.edit().putString("entries", array.toString()).apply()
    }

    fun getEntries(): List<Mood> {
        val json = pref.getString("entries", "[]") ?: "[]"
        val array = JSONArray(json)
        val list = ArrayList<Mood>(array.length())
        for (i in 0 until array.length()) {
            val o = array.getJSONObject(i)
            list.add(
                Mood(
                    mood = o.optString("mood"),
                    note = o.optString("note"),
                    timestamp = o.optLong("timestamp")
                )
            )
        }
        return list.sortedByDescending { it.timestamp }
    }

    fun getTodaysMoodCount(): Int {
        val cal = Calendar.getInstance()
        cal.time = Date()
        // Move to start of today
        cal.set(Calendar.HOUR_OF_DAY, 0)
        cal.set(Calendar.MINUTE, 0)
        cal.set(Calendar.SECOND, 0)
        cal.set(Calendar.MILLISECOND, 0)
        val todayStart = cal.timeInMillis

        // Move to end of today
        cal.add(Calendar.DAY_OF_MONTH, 1)
        val tomorrowStart = cal.timeInMillis

        // Count entries from today
        return getEntries().count { entry ->
            entry.timestamp >= todayStart && entry.timestamp < tomorrowStart
        }
    }

    fun clearAll() { pref.edit().clear().apply() }

    // Filter methods for day-wise filtering
    fun getEntriesForToday(): List<Mood> {
        val cal = Calendar.getInstance()
        cal.time = Date()
        cal.set(Calendar.HOUR_OF_DAY, 0)
        cal.set(Calendar.MINUTE, 0)
        cal.set(Calendar.SECOND, 0)
        cal.set(Calendar.MILLISECOND, 0)
        val todayStart = cal.timeInMillis

        cal.add(Calendar.DAY_OF_MONTH, 1)
        val tomorrowStart = cal.timeInMillis

        return getEntries().filter { entry ->
            entry.timestamp >= todayStart && entry.timestamp < tomorrowStart
        }
    }

    fun getEntriesForThisWeek(): List<Mood> {
        val cal = Calendar.getInstance()
        cal.time = Date()
        cal.set(Calendar.HOUR_OF_DAY, 0)
        cal.set(Calendar.MINUTE, 0)
        cal.set(Calendar.SECOND, 0)
        cal.set(Calendar.MILLISECOND, 0)
        
        // Get start of week (Monday)
        val dayOfWeek = cal.get(Calendar.DAY_OF_WEEK)
        val daysFromMonday = if (dayOfWeek == Calendar.SUNDAY) 6 else dayOfWeek - Calendar.MONDAY
        cal.add(Calendar.DAY_OF_MONTH, -daysFromMonday)
        val weekStart = cal.timeInMillis

        // Get end of week (Sunday)
        cal.add(Calendar.DAY_OF_MONTH, 7)
        val weekEnd = cal.timeInMillis

        return getEntries().filter { entry ->
            entry.timestamp >= weekStart && entry.timestamp < weekEnd
        }
    }

    fun getEntriesForThisMonth(): List<Mood> {
        val cal = Calendar.getInstance()
        cal.time = Date()
        cal.set(Calendar.HOUR_OF_DAY, 0)
        cal.set(Calendar.MINUTE, 0)
        cal.set(Calendar.SECOND, 0)
        cal.set(Calendar.MILLISECOND, 0)
        cal.set(Calendar.DAY_OF_MONTH, 1)
        val monthStart = cal.timeInMillis

        cal.add(Calendar.MONTH, 1)
        val nextMonthStart = cal.timeInMillis

        return getEntries().filter { entry ->
            entry.timestamp >= monthStart && entry.timestamp < nextMonthStart
        }
    }

    // Filter methods for mood type filtering
    fun getEntriesByMoodType(moodType: String): List<Mood> {
        return getEntries().filter { entry ->
            entry.mood.equals(moodType, ignoreCase = true)
        }
    }

    // Combined filtering methods
    fun getFilteredEntries(dayFilter: String?, moodFilter: String?): List<Mood> {
        var filteredEntries = getEntries()

        // Apply day filter
        when (dayFilter) {
            "Today" -> filteredEntries = getEntriesForToday()
            "This Week" -> filteredEntries = getEntriesForThisWeek()
            "This Month" -> filteredEntries = getEntriesForThisMonth()
        }

        // Apply mood filter
        if (!moodFilter.isNullOrEmpty() && moodFilter != "All") {
            filteredEntries = filteredEntries.filter { entry ->
                entry.mood.equals(moodFilter, ignoreCase = true)
            }
        }

        return filteredEntries
    }

}