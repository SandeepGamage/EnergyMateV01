package com.example.energymatev01

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.example.energymatev01.databinding.ActivityHydrationReminderSettingsBinding

class HydrationReminderSettings : AppCompatActivity() {
    private lateinit var binding: ActivityHydrationReminderSettingsBinding
    private lateinit var sharedPreferences: SharedPreferences
    private var selectedFrequency: Int? = null
    private var selectedStartTime: String? = null
    private var selectedEndTime: String? = null

    private val frequencyButtons by lazy {
        listOf(binding.btn30min, binding.btn1hour, binding.btn15hours, binding.btn2hours, binding.btn3hours, binding.btn4hours)
    }

    private val startTimeButtons by lazy {
        listOf(binding.btn6am, binding.btn7am, binding.btn8am, binding.btn9am)
    }

    private val endTimeButtons by lazy {
        listOf(binding.btn8pm, binding.btn9pm, binding.btn10pm, binding.btn11pm)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        binding = ActivityHydrationReminderSettingsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.backBTN.setOnClickListener {
            finish()
        }

        sharedPreferences = getSharedPreferences("HydrationSettings", MODE_PRIVATE)
        loadSettings()

        setupClickListeners()
        createNotificationChannel()
    }

    private fun loadSettings() {
        selectedFrequency = sharedPreferences.getInt("frequency", -1).takeIf { it != -1 }
        selectedStartTime = sharedPreferences.getString("startTime", null)
        selectedEndTime = sharedPreferences.getString("endTime", null)

        updateButtonStates(frequencyButtons, selectedFrequency?.let { getFrequencyText(it) })
        updateButtonStates(startTimeButtons, selectedStartTime)
        updateButtonStates(endTimeButtons, selectedEndTime)
    }

    private fun setupClickListeners() {
        frequencyButtons.forEach { button ->
            button.setOnClickListener {
                selectedFrequency = getFrequencyInMillis(button.text.toString())
                updateButtonStates(frequencyButtons, button.text.toString())
            }
        }

        startTimeButtons.forEach { button ->
            button.setOnClickListener {
                selectedStartTime = button.text.toString()
                updateButtonStates(startTimeButtons, selectedStartTime)
            }
        }

        endTimeButtons.forEach { button ->
            button.setOnClickListener {
                selectedEndTime = button.text.toString()
                updateButtonStates(endTimeButtons, selectedEndTime)
            }
        }

        binding.btnTestNotification.setOnClickListener {
            showTestNotification()
        }

        binding.btnSave.setOnClickListener {
            saveReminder()
        }
    }

    private fun updateButtonStates(buttons: List<Button>, selectedText: String?) {
        buttons.forEach { button ->
            val isSelected = button.text.toString() == selectedText
            button.setBackgroundResource(if (isSelected) R.drawable.rounded_border_green else R.drawable.rounded_border_orange)
            button.setTextColor(if (isSelected) Color.BLACK else Color.BLACK)
        }
    }

    private fun getFrequencyInMillis(frequencyText: String): Int {
        return when (frequencyText) {
            "30 minutes" -> 30 * 60 * 1000
            "1 hour" -> 60 * 60 * 1000
            "1.5 hours" -> 90 * 60 * 1000
            "2 hours" -> 120 * 60 * 1000
            "3 hours" -> 180 * 60 * 1000
            "4 hours" -> 240 * 60 * 1000
            else -> 0
        }
    }

    private fun getFrequencyText(frequencyInMillis: Int): String {
        return when (frequencyInMillis) {
            30 * 60 * 1000 -> "30 minutes"
            60 * 60 * 1000 -> "1 hour"
            90 * 60 * 1000 -> "1.5 hours"
            120 * 60 * 1000 -> "2 hours"
            180 * 60 * 1000 -> "3 hours"
            240 * 60 * 1000 -> "4 hours"
            else -> ""
        }
    }

    private fun showTestNotification() {
        val builder = NotificationCompat.Builder(this, "hydration_channel")
            .setSmallIcon(R.drawable.baseline_notifications_24)
            .setContentTitle("Hydration Reminder")
            .setContentText("This is a test notification to check how reminders will look.")
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setAutoCancel(true)

        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(android.Manifest.permission.POST_NOTIFICATIONS), 101)
            return
        }

        with(NotificationManagerCompat.from(this)) {
            notify(1, builder.build())
        }
    }

    private fun saveReminder() {
        val editor = sharedPreferences.edit()
        selectedFrequency?.let { editor.putInt("frequency", it) }
        selectedStartTime?.let { editor.putString("startTime", it) }
        selectedEndTime?.let { editor.putString("endTime", it) }
        editor.putBoolean("isReminderEnabled", true) // Enable reminders when settings are saved
        editor.apply()

        // Schedule the actual reminders
        val scheduler = HydrationReminderScheduler(this)
        scheduler.scheduleReminders()

        Toast.makeText(this, "Reminder saved and scheduled successfully!", Toast.LENGTH_SHORT).show()
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = "Hydration Reminders"
            val descriptionText = "Channel for hydration reminder notifications"
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel("hydration_channel", name, importance).apply {
                description = descriptionText
            }
            val notificationManager: NotificationManager =
                getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }
}
