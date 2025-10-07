//package com.example.energymatev01.fragment
//
//import android.os.Bundle
//import androidx.fragment.app.Fragment
//import android.view.LayoutInflater
//import android.view.View
//import android.view.ViewGroup
//import android.widget.Button
//import android.widget.EditText
//import android.widget.LinearLayout
//import android.widget.Toast
//import androidx.core.content.ContextCompat
//import androidx.recyclerview.widget.LinearLayoutManager
//import androidx.recyclerview.widget.RecyclerView
//import com.example.energymatev01.R
//import com.example.energymatev01.data.MoodPreferences
//
//class MoodJournalFragment : Fragment() {
//
//    private lateinit var prefs: MoodPreferences
//    private var selectedMood: String = ""
//    private var selectedMoodView: LinearLayout? = null
//
//    override fun onCreateView(
//        inflater: LayoutInflater,
//        container: ViewGroup?,
//        savedInstanceState: Bundle?
//    ): View? {
//        return inflater.inflate(R.layout.fragment_mood_journal, container, false)
//    }
//
//    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
//        super.onViewCreated(view, savedInstanceState)
//
//        val excellent: LinearLayout = view.findViewById(R.id.excellentMood)
//        val good: LinearLayout = view.findViewById(R.id.goodMood)
//        val okay: LinearLayout = view.findViewById(R.id.okayMood)
//        val notGreat: LinearLayout = view.findViewById(R.id.notGreatMood)
//        val bad: LinearLayout = view.findViewById(R.id.badMood)
//        val save: Button = view.findViewById(R.id.saveLogBTN)
//        val note: EditText = view.findViewById(R.id.noteContainer)
//        val recycler: RecyclerView = view.findViewById(R.id.recentEntriesRecyclerView)
//        recycler.layoutManager = LinearLayoutManager(requireContext())
//        val adapter = MoodAdapter()
//        recycler.adapter = adapter
//
//        resetAllMoodViews(excellent, good, okay, notGreat, bad)
//
//        adapter.onEdit = { entry ->
//            val dialog = android.app.AlertDialog.Builder(requireContext())
//            val input = EditText(requireContext())
//            input.setText(entry.note)
//            dialog.setTitle("Edit Mood Entry")
//            dialog.setView(input)
//            dialog.setPositiveButton("Save") { _, _ ->
//                prefs.updateEntry(entry.timestamp, entry.mood, input.text.toString())
//                adapter.submitList(prefs.getEntries())
//            }
//            dialog.setNegativeButton("Cancel") { d, _ ->
//                d.dismiss()
//            }
//            dialog.show()
//        }
//
//        adapter.onDelete = { entry ->
//            prefs.deleteEntry(entry.timestamp)
//            adapter.submitList(prefs.getEntries())
//        }
//
//        fun select(mood: String, moodView: LinearLayout) {
//            // Reset all mood views to unselected state
//            resetAllMoodViews(excellent, good, okay, notGreat, bad)
//
//            // Set selected mood and view
//            selectedMood = mood
//            selectedMoodView = moodView
//
//            // Update selected view appearance
//            moodView.background = ContextCompat.getDrawable(requireContext(), R.drawable.mood_selected_background)
//        }
//
//        excellent.setOnClickListener { select("Excellent", excellent) }
//        good.setOnClickListener { select("Good", good) }
//        okay.setOnClickListener { select("Okay", okay) }
//        notGreat.setOnClickListener { select("Not Great", notGreat) }
//        bad.setOnClickListener { select("Bad", bad) }
//
//        save.setOnClickListener {
//            val text = note.text.toString().trim()
//            if (selectedMood.isEmpty()) {
//                Toast.makeText(requireContext(), "Please select a mood", Toast.LENGTH_SHORT).show()
//                return@setOnClickListener
//            }
//            prefs.addEntry(selectedMood, text)
//            Toast.makeText(requireContext(), "Mood saved", Toast.LENGTH_SHORT).show()
//            note.setText("")
//            selectedMood = ""
//            selectedMoodView = null
//
//            // Reset all mood views to unselected state
//            resetAllMoodViews(excellent, good, okay, notGreat, bad)
//
//            adapter.submitList(prefs.getEntries())
//        }
//
//    }
//
//    //change the background color to default one
//    private fun resetAllMoodViews(vararg moodView: LinearLayout){
//        moodView.forEach { moodView ->
//            moodView.background = ContextCompat.getDrawable(requireContext(), R.drawable.mood_unselected_background)
//        }
//    }
//
//}

package com.example.energymatev01.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.Spinner
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.energymatev01.data.Mood
import com.example.energymatev01.R
import com.example.energymatev01.data.MoodPreferences
import com.example.energymatev01.fragment.MoodAdapter

class MoodJournalFragment : Fragment() {

    private lateinit var prefs: MoodPreferences
    private var selectedMood: String = ""
    private var selectedMoodView: LinearLayout? = null
    
    // Filter variables
    private var currentDayFilter: String? = null
    private var currentMoodFilter: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        prefs = MoodPreferences(requireContext())
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_mood_journal, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val excellent: LinearLayout = view.findViewById(R.id.excellentMood)
        val good: LinearLayout = view.findViewById(R.id.goodMood)
        val okay: LinearLayout = view.findViewById(R.id.okayMood)
        val notGreat: LinearLayout = view.findViewById(R.id.notGreatMood)
        val bad: LinearLayout = view.findViewById(R.id.badMood)
        val note: EditText = view.findViewById(R.id.noteContainer)
        val save: Button = view.findViewById(R.id.saveLogBTN)
        val recycler: RecyclerView = view.findViewById(R.id.recentEntriesRecyclerView)
        recycler.layoutManager = LinearLayoutManager(requireContext())
        val adapter = MoodAdapter()
        recycler.adapter = adapter
        adapter.submitList(prefs.getEntries())

        // Initialize filter UI
        setupFilterUI(view, adapter)

        // Initialize all mood views to unselected state
        resetAllMoodViews(excellent, good, okay, notGreat, bad)

        adapter.onEdit = { entry ->
            val dialog = android.app.AlertDialog.Builder(requireContext())
            val input = EditText(requireContext())
            input.setText(entry.note)
            dialog.setTitle("Edit Note")
            dialog.setView(input)
            dialog.setPositiveButton("Save") { _, _ ->
                prefs.updateEntry(entry.timestamp, entry.mood, input.text.toString())
                adapter.submitList(getFilteredEntries())
            }
            dialog.setNegativeButton("Cancel", null)
            dialog.show()
        }
        adapter.onDelete = { entry ->
            prefs.deleteEntry(entry.timestamp)
            adapter.submitList(getFilteredEntries())
        }

        fun select(mood: String, moodView: LinearLayout) {
            // Reset all mood views to unselected state
            resetAllMoodViews(excellent, good, okay, notGreat, bad)

            // Set selected mood and view
            selectedMood = mood
            selectedMoodView = moodView

            // Update selected view appearance
            moodView.background = ContextCompat.getDrawable(requireContext(), R.drawable.mood_selected_background)
        }

        excellent.setOnClickListener { select("Excellent", excellent) }
        good.setOnClickListener { select("Good", good) }
        okay.setOnClickListener { select("Okay", okay) }
        notGreat.setOnClickListener { select("Not Great", notGreat) }
        bad.setOnClickListener { select("Bad", bad) }

        save.setOnClickListener {
            val text = note.text.toString().trim()
            if (selectedMood.isEmpty()) {
                Toast.makeText(requireContext(), "Please select a mood", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            prefs.addEntry(selectedMood, text)
            Toast.makeText(requireContext(), "Mood saved", Toast.LENGTH_SHORT).show()
            note.setText("")
            selectedMood = ""
            selectedMoodView = null

            // Reset all mood views to unselected state
            resetAllMoodViews(excellent, good, okay, notGreat, bad)

            adapter.submitList(getFilteredEntries())
        }
    }

    private fun setupFilterUI(view: View, adapter: MoodAdapter) {
        val dayFilterSpinner: Spinner = view.findViewById(R.id.dayFilterSpinner)
        val moodTypeSpinner: Spinner = view.findViewById(R.id.moodTypeSpinner)

        //day filter spinner
        val dayFilters = listOf("All Time", "Today", "This Week", "This Month")
        val daySpinnerAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, dayFilters)
        daySpinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        dayFilterSpinner.adapter = daySpinnerAdapter

        //mood type spinner
        val moodTypes = listOf("All Moods", "Excellent", "Good", "Okay", "Not Great", "Bad")
        val moodSpinnerAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, moodTypes)
        moodSpinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        moodTypeSpinner.adapter = moodSpinnerAdapter

        //spinner listener
        dayFilterSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                currentDayFilter = if (position == 0) null else dayFilters[position]
                adapter.submitList(getFilteredEntries())
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }

        // Mood spinner listener
        moodTypeSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                currentMoodFilter = if (position == 0) null else moodTypes[position]
                adapter.submitList(getFilteredEntries())
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }

    }

    private fun getFilteredEntries(): List<Mood> {
        return prefs.getFilteredEntries(currentDayFilter, currentMoodFilter)
    }

    private fun resetAllMoodViews(vararg moodViews: LinearLayout) {
        moodViews.forEach { moodView ->
            moodView.background = ContextCompat.getDrawable(requireContext(), R.drawable.rounded_border_orange)
        }
    }

//    // Test method to verify filtering functionality
//    private fun testFiltering() {
//        // This method can be called to test the filtering functionality
//        // It adds some test entries and verifies the filters work correctly
//        val testEntries = listOf(
//            "Excellent", "Good", "Okay", "Not Great", "Bad"
//        )
//
//        // Add test entries with different timestamps
//        testEntries.forEachIndexed { index, mood ->
//            prefs.addEntry(mood, "Test note $index")
//            // Add some delay to ensure different timestamps
//            Thread.sleep(100)
//        }
//
//        // Test day filters
//        val todayEntries = prefs.getEntriesForToday()
//        val weekEntries = prefs.getEntriesForThisWeek()
//        val monthEntries = prefs.getEntriesForThisMonth()
//
//        // Test mood filters
//        val excellentEntries = prefs.getEntriesByMoodType("Excellent")
//        val goodEntries = prefs.getEntriesByMoodType("Good")
//
//        // Test combined filters
//        val filteredEntries = prefs.getFilteredEntries("Today", "Excellent")
//
//        // Log results for debugging
//        android.util.Log.d("MoodFilterTest", "Today entries: ${todayEntries.size}")
//        android.util.Log.d("MoodFilterTest", "Week entries: ${weekEntries.size}")
//        android.util.Log.d("MoodFilterTest", "Month entries: ${monthEntries.size}")
//        android.util.Log.d("MoodFilterTest", "Excellent entries: ${excellentEntries.size}")
//        android.util.Log.d("MoodFilterTest", "Good entries: ${goodEntries.size}")
//        android.util.Log.d("MoodFilterTest", "Filtered entries: ${filteredEntries.size}")
//    }
}


