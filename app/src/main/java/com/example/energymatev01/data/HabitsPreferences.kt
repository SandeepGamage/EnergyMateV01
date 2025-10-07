package com.example.energymatev01.data

import android.content.Context
import android.content.SharedPreferences
import org.json.JSONArray
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.UUID
import kotlin.collections.filterNot
import kotlin.collections.forEach
import kotlin.collections.indexOfFirst
import kotlin.collections.map
import kotlin.collections.toMutableList
import kotlin.ranges.coerceAtLeast
import kotlin.ranges.coerceAtMost
import kotlin.ranges.until

class HabitsPreferences(context: Context) {

    private val prefs: SharedPreferences =
        context.getSharedPreferences("habits_data", Context.MODE_PRIVATE)

    private fun today(): String = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())

    fun setHabits(habits: List<Habit>) {
        val array = JSONArray()
        habits.forEach { h ->
            array.put(
                JSONObject()
                    .put("id", h.id)
                    .put("name", h.name)
                    .put("targetPerDay", h.targetPerDay)
                    .put("lastCompletedDate", h.lastCompletedDate)
                    .put("completedTodayCount", h.completedTodayCount)
            )
        }
        prefs.edit().putString("habits", array.toString()).apply()
    }

    fun addHabit(name: String, targetPerDay: Int): Habit {
        val id = UUID.randomUUID().toString()
        val h = Habit(id = id, name = name, targetPerDay = targetPerDay, lastCompletedDate = today(), completedTodayCount = 0)
        val list = getHabits().toMutableList()
        list.add(h)
        setHabits(list)
        return h
    }

    fun updateHabit(id: String, name: String, targetPerDay: Int) {
        val list = getHabits().toMutableList()
        val idx = list.indexOfFirst { it.id == id }
        if (idx >= 0) {
            val old = list[idx]
            list[idx] = old.copy(name = name, targetPerDay = targetPerDay)
            setHabits(list)
        }
    }

    fun deleteHabit(id: String) {
        val list = getHabits().filterNot { it.id == id }
        setHabits(list)
    }

    fun getHabits(): List<Habit> {
        val json = prefs.getString("habits", "[]") ?: "[]"
        val array = JSONArray(json)
        val list = kotlin.collections.ArrayList<Habit>(array.length())
        for (i in 0 until array.length()) {
            val o = array.getJSONObject(i)
            list.add(
                Habit(
                    id = o.optString("id"),
                    name = o.optString("name"),
                    targetPerDay = o.optInt("targetPerDay", 1),
                    lastCompletedDate = o.optString("lastCompletedDate"),
                    completedTodayCount = o.optInt("completedTodayCount", 0)
                )
            )
        }
        return list
    }

    fun incrementHabit(id: String) {
        val list = getHabits().toMutableList()
        val idx = list.indexOfFirst { it.id == id }
        if (idx >= 0) {
            val t = today()
            val h = list[idx]
            val newCount = if (h.lastCompletedDate == t) h.completedTodayCount + 1 else 1
            list[idx] = h.copy(lastCompletedDate = t, completedTodayCount = newCount)
            setHabits(list)
        }
    }

    fun decrementHabit(id: String) {
        val list = getHabits().toMutableList()
        val idx = list.indexOfFirst { it.id == id }
        if (idx >= 0) {
            val t = today()
            val h = list[idx]
            val base = if (h.lastCompletedDate == t) h.completedTodayCount else 0
            val newCount = (base - 1).coerceAtLeast(0)
            list[idx] = h.copy(lastCompletedDate = t, completedTodayCount = newCount)
            setHabits(list)
        }
    }

    fun clearAll() { prefs.edit().clear().apply() }

    fun resetTodayCountsIfNewDay() {
        val t = today()
        val list = getHabits().map { h ->
            if (h.lastCompletedDate != t) h.copy(lastCompletedDate = t, completedTodayCount = 0) else h
        }
        setHabits(list)
    }

    fun computeCompletionPercent(hydrationPercent: Int): Int {
        val t = today()
        val list = getHabits()
        if (list.isEmpty()) return hydrationPercent
        var habitPercentSum = 0
        list.forEach { h ->
            val countToday = if (h.lastCompletedDate == t) h.completedTodayCount else 0
            val per = (countToday * 100 / h.targetPerDay).coerceAtMost(100)
            habitPercentSum += per
        }
        val habitsAvg = habitPercentSum / list.size
        return ((hydrationPercent + habitsAvg) / 2)
    }
}


