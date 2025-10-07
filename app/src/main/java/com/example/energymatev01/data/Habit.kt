package com.example.energymatev01.data

data class Habit(
    val id: String,
    val name: String,
    val targetPerDay: Int = 1,
    val lastCompletedDate: String = "",
    val completedTodayCount: Int = 0
)


