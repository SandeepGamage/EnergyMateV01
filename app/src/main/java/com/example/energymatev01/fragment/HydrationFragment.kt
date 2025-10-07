package com.example.energymatev01.fragment

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageButton
import android.widget.Switch
import android.widget.TextView
import com.example.energymatev01.HydrationReminderSettings
import com.example.energymatev01.HydrationReminderScheduler
import com.example.energymatev01.R
import com.example.energymatev01.data.HydrationPreferences

class HydrationFragment : Fragment() {

    private lateinit var prefs: HydrationPreferences
    private lateinit var reminderDetailsText: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        prefs = HydrationPreferences(requireContext())
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_hydration, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val settingsBtn: ImageButton = view.findViewById(R.id.settingsBtn)
        val switch: Switch = view.findViewById(R.id.reminderSwitch)
        val addGlassBtn: Button = view.findViewById(R.id.addGlassBtn)
        val minusBtn: ImageButton = view.findViewById(R.id.minusBtn)
        val resetBtn: ImageButton = view.findViewById(R.id.resetBtn)
        val dailyMinusBtn: ImageButton = view.findViewById(R.id.dailyMinusBtn)
        val dailyPlusBtn: ImageButton = view.findViewById(R.id.dailyPlusBtn)
        val waterPercent: TextView = view.findViewById(R.id.waterPercent)
        val waterTotal: TextView = view.findViewById(R.id.waterTotal)
        val glassesLeft: TextView = view.findViewById(R.id.glassesLeft)
        val dailyGlassGoal: TextView = view.findViewById(R.id.dailyGlassGoal)
        reminderDetailsText = view.findViewById(R.id.reminderDetailsText)

        var count = prefs.getGlassesCount()
        var goal = prefs.getDailyGoal()

        settingsBtn.setOnClickListener {
            val intent = Intent(requireContext(), HydrationReminderSettings::class.java)
            startActivity(intent)
        }

        switch.isChecked = prefs.isReminderEnabled()

        switch.setOnCheckedChangeListener { _, isChecked ->
            prefs.setReminderEnabled(isChecked)
            
            // Schedule or cancel reminders based on toggle state
            val scheduler = HydrationReminderScheduler(requireContext())
            if (isChecked) {
                scheduler.scheduleReminders()
            } else {
                scheduler.cancelReminders()
            }
            
            updateReminderDetails()
        }

        fun updateUi() {
            val clampedCount = if (count < 0) 0 else count
            val clampedGoal = if (goal < 1) 1 else goal
            val percent = (clampedCount * 100 / clampedGoal).coerceAtMost(100)
            waterPercent.text = "$percent%"
            waterTotal.text = "$clampedCount / $clampedGoal glasses"
            val left = (clampedGoal - clampedCount).coerceAtLeast(0)
            glassesLeft.text = "$left more to go"
            dailyGlassGoal.text = "$clampedGoal glasses"
        }

        addGlassBtn.setOnClickListener {
            count += 1
            prefs.setGlassesCount(count)
            updateUi()
        }

        minusBtn.setOnClickListener {
            if (count > 0) {
                count -= 1
                prefs.setGlassesCount(count)
                updateUi()
            }
        }

        resetBtn.setOnClickListener {
            count = 0
            prefs.setGlassesCount(count)
            updateUi()
        }

        dailyMinusBtn.setOnClickListener {
            if (goal > 1) {
                goal -= 1
                prefs.setDailyGoal(goal)
                updateUi()
            }
        }

        dailyPlusBtn.setOnClickListener {
            goal += 1
            prefs.setDailyGoal(goal)
            updateUi()
        }

        updateUi()
        updateReminderDetails()
    }

    override fun onResume() {
        super.onResume()
        updateReminderDetails()
    }

    private fun updateReminderDetails() {
        if (prefs.isReminderEnabled()) {
            val frequency = prefs.getReminderFrequency()
            val frequencyInMinutes = frequency/(60*1000)
            val startTime = prefs.getReminderStartTime()
            val endTime = prefs.getReminderEndTime()
            reminderDetailsText.text = "Every $frequencyInMinutes from $startTime to $endTime"
        } else {
            reminderDetailsText.text = "Reminder not set"
        }
    }
}
