package com.example.energymatev01.fragment

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.energymatev01.R
import com.example.energymatev01.data.HabitsPreferences
import com.example.energymatev01.data.HydrationPreferences
import com.example.energymatev01.data.MoodPreferences
import com.example.energymatev01.data.UserPreferences
import com.example.energymatev01.ui.components.MoodChartHelper
import com.example.energymatev01.utils.CommonFunctions
import com.github.mikephil.charting.charts.LineChart

class HomeFragment : Fragment() {
    private lateinit var userName: TextView
    private lateinit var dateText: TextView
    private lateinit var completePercentage: TextView
    private lateinit var userPreferences: UserPreferences
    private lateinit var hydrationPreferences: HydrationPreferences
    private lateinit var habitsPreferences: HabitsPreferences
    private lateinit var moodPreferences: MoodPreferences
    private var moodSharedPrefs: SharedPreferences? = null
    private var moodPrefListener: SharedPreferences.OnSharedPreferenceChangeListener? = null
    private val commonFunctions = CommonFunctions()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_home, container, false)
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        userPreferences = UserPreferences(requireContext())
        hydrationPreferences = HydrationPreferences(requireContext())
        habitsPreferences = HabitsPreferences(requireContext())
        moodPreferences = MoodPreferences(requireContext())

        userName = view.findViewById(R.id.usernameText)
        dateText = view.findViewById(R.id.dateText)
        completePercentage = view.findViewById(R.id.circularProgressPercent)

        val storedUserName = userPreferences.getName()
        userName.text = storedUserName?.uppercase()

        //set today date to display
        dateText.text = commonFunctions.getTodayDate()

        val glassCount = hydrationPreferences.getGlassesCount()
        val goal = hydrationPreferences.getDailyGoal().coerceAtLeast(1)
        val hydrationPercent = (glassCount * 100 / goal).coerceAtMost(100)

        habitsPreferences.resetTodayCountsIfNewDay()
        val percentage = habitsPreferences.computeCompletionPercent(hydrationPercent)

        completePercentage.text = "$percentage%"

        val recycler: RecyclerView = view.findViewById(R.id.habitsRecycler)
        recycler.layoutManager = LinearLayoutManager(requireContext())
        val adapter = HabitAdapter()
        recycler.adapter = adapter

        //setup MPAndroidChart for moods
        val moodChart: LineChart = view.findViewById(R.id.MoodChart)
        val chartHelper = MoodChartHelper(requireContext())

        //assign chart with current mood data only
        chartHelper.setupMoodChart(moodChart, moodPreferences)

        fun refreshUI() {
            val habits = habitsPreferences.getHabits()
            adapter.submitList(habits)

            // Update stats with real data
            val activeHabitsCount = habits.size
            val todaysMoodCount = moodPreferences.getTodaysMoodCount()
            val waterGlassCount = hydrationPreferences.getGlassesCount()

            view.findViewById<TextView>(R.id.activeHabitsCount)?.text = activeHabitsCount.toString()
            view.findViewById<TextView>(R.id.dailyGoalsCount)?.text = todaysMoodCount.toString()
            view.findViewById<TextView>(R.id.waterIntakeCount)?.text = waterGlassCount.toString()

            // Update circular progress
            val c = hydrationPreferences.getGlassesCount()
            val g = hydrationPreferences.getDailyGoal().coerceAtLeast(1)
            val hp = (c * 100 / g).coerceAtMost(100)
            val p = habitsPreferences.computeCompletionPercent(hp)
            view.findViewById<TextView>(R.id.circularProgressPercent)?.text = "$p%"
        }

        fun refreshChartOnly() {
            chartHelper.refreshChart(moodChart, moodPreferences)
        }

        // Initial population
        refreshUI()
        refreshChartOnly()

        // Listen for mood preference changes; refresh chart only when mood entries change
        moodSharedPrefs = requireContext().getSharedPreferences("mood_data", Context.MODE_PRIVATE)
        moodPrefListener = SharedPreferences.OnSharedPreferenceChangeListener { _, key ->
            if (key == "entries") {
                // Only refresh the chart when mood entries change
                refreshChartOnly()
            }
        }
        moodSharedPrefs?.registerOnSharedPreferenceChangeListener(moodPrefListener)

        adapter.onIncrement = { habit ->
            habitsPreferences.incrementHabit(habit.id)
            refreshUI()
        }
        adapter.onDecrement = { habit ->
            habitsPreferences.decrementHabit(habit.id)
            refreshUI()
        }
        adapter.onDelete = { habit ->



            habitsPreferences.deleteHabit(habit.id)
            refreshUI()
        }
        adapter.onEdit = { habit ->
            val dialog = android.app.AlertDialog.Builder(requireContext())
            val container = android.widget.LinearLayout(requireContext())
            container.orientation = android.widget.LinearLayout.VERTICAL

            val nameInput = android.widget.EditText(requireContext())
            nameInput.setText(habit.name)
            nameInput.hint = "Habit name"

            val targetInput = android.widget.EditText(requireContext())
            targetInput.inputType = android.text.InputType.TYPE_CLASS_NUMBER
            targetInput.setText(habit.targetPerDay.toString())
            targetInput.hint = "Target per day"

            container.addView(nameInput)
            container.addView(targetInput)

            dialog.setTitle("Edit Habit")
            dialog.setView(container)
            dialog.setPositiveButton("Save") { _, _ ->
                val newName = nameInput.text.toString().trim()
                val newTarget = targetInput.text.toString().toIntOrNull() ?: habit.targetPerDay
                habitsPreferences.updateHabit(habit.id, newName, newTarget.coerceAtLeast(1))
                refreshUI()
            }
            dialog.setNegativeButton("Cancel", null)
            dialog.show()
        }

        val addBtn: View = view.findViewById(R.id.addHabitBtn)
        addBtn.setOnClickListener {
            val dialog = android.app.AlertDialog.Builder(requireContext())
            val container = android.widget.LinearLayout(requireContext())
            container.orientation = android.widget.LinearLayout.VERTICAL

            val nameInput = android.widget.EditText(requireContext())
            nameInput.hint = "Habit name"

            val targetInput = android.widget.EditText(requireContext())
            targetInput.inputType = android.text.InputType.TYPE_CLASS_NUMBER
            targetInput.hint = "Target per day"

            container.addView(nameInput)
            container.addView(targetInput)

            dialog.setTitle("Add Habit")
            dialog.setView(container)
            dialog.setPositiveButton("Add") { _, _ ->
                val name = nameInput.text.toString().trim()
                val target = targetInput.text.toString().toIntOrNull() ?: 1
                if (name.isNotEmpty()) {
                    habitsPreferences.addHabit(name, target.coerceAtLeast(1))
                    refreshUI()
                }
            }
            dialog.setNegativeButton("Cancel", null)
            dialog.show()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        // Unregister shared prefs listener to avoid leaks
        moodSharedPrefs?.unregisterOnSharedPreferenceChangeListener(moodPrefListener)
        moodPrefListener = null
        moodSharedPrefs = null
    }

}
