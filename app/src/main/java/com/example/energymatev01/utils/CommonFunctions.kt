package com.example.energymatev01.utils

import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class CommonFunctions {

    fun getTodayDate(): String {
        val calendar = Calendar.getInstance()
        val dateFormat = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault())
        return dateFormat.format(calendar.time)
    }

}