package com.example.energymatev01.ui.components
import android.content.Context
import android.graphics.Color
import androidx.core.content.ContextCompat
import com.example.energymatev01.R
import com.example.energymatev01.data.HabitsPreferences
import com.example.energymatev01.data.HydrationPreferences
import com.example.energymatev01.data.Mood
import com.example.energymatev01.data.MoodPreferences
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import java.text.SimpleDateFormat
import java.util.*

class MoodChartHelper(private val context: Context) {

    private fun mapMoodToScore(mood: String): Int {
        return when (mood.lowercase(Locale.getDefault())) {
            "excellent" -> 5
            "good" -> 4
            "okay" -> 3
            "not great" -> 2
            "bad" -> 1
            else -> 0
        }
    }

    fun setupMoodChart(
        lineChart: LineChart,
        moodPreferences: MoodPreferences
    ) {
        // Get mood entries
        val entries = moodPreferences.getEntries()

        // Calculate last 7 days data
        val cal = Calendar.getInstance()
        cal.time = Date()

        // Move to start of today
        cal.set(Calendar.HOUR_OF_DAY, 0)
        cal.set(Calendar.MINUTE, 0)
        cal.set(Calendar.SECOND, 0)
        cal.set(Calendar.MILLISECOND, 0)
        val todayStart = cal.timeInMillis

        // Generate 7 days data and labels
        val dayLabels = ArrayList<String>()
        val lineEntries = ArrayList<Entry>()

        for (i in 6 downTo 0) {
            val dayStart = todayStart - i * 24L * 60L * 60L * 1000L
            val dayEnd = dayStart + 24L * 60L * 60L * 1000L

            // Get entries for this day
            val dayEntries = entries.filter { it.timestamp in dayStart until dayEnd }

            // Calculate average mood score for the day
            val average = if (dayEntries.isEmpty()) {
                0f
            } else {
                dayEntries.map { mapMoodToScore(it.mood) }.average().toFloat()
            }

            lineEntries.add(Entry((6 - i).toFloat(), average))
            dayLabels.add(SimpleDateFormat("EEE", Locale.getDefault()).format(Date(dayStart)))
        }

        // Create LineDataSet
        val dataSet = LineDataSet(lineEntries, "Daily Mood Trend").apply {
            // EnergyMate color theme - Orange line
            color = ContextCompat.getColor(context, R.color.primary_orange)
            setCircleColor(ContextCompat.getColor(context, R.color.primary_orange))

            // Line appearance
            lineWidth = 3f
            circleRadius = 6f
            circleHoleRadius = 3f
            setDrawCircles(true)
            setDrawValues(true)

            // Value text styling
            valueTextColor = ContextCompat.getColor(context, R.color.black)
            valueTextSize = 12f

            // Smooth curve
            mode = LineDataSet.Mode.CUBIC_BEZIER

            // Fill area under line (optional)
            setDrawFilled(true)
            fillColor = ContextCompat.getColor(context, R.color.primary_orange)
            fillAlpha = 50
        }

        // Create LineData and set to chart
        val lineData = LineData(dataSet)
        lineChart.data = lineData

        // Customize chart appearance
        customizeLineChart(lineChart, dayLabels)
    }

    private fun customizeLineChart(lineChart: LineChart, dayLabels: ArrayList<String>) {
        lineChart.apply {
            // Chart description
            description.isEnabled = false

            // Chart background - White theme
            setBackgroundColor(ContextCompat.getColor(context, R.color.white))

            // Remove grid background for clean look
            setDrawGridBackground(false)

            // Animation - smooth entry effect
            animateY(1000)

            // Touch interactions
            setTouchEnabled(true)
            isDragEnabled = true
            setScaleEnabled(true)
            setPinchZoom(true)

            // Legend styling
            legend.apply {
                isEnabled = false
                textColor = ContextCompat.getColor(context, R.color.black)
                textSize = 12f
            }

            // X-axis configuration
            xAxis.apply {
                position = XAxis.XAxisPosition.BOTTOM
                textColor = ContextCompat.getColor(context, R.color.black)
                setDrawGridLines(false)
                granularity = 1f
                valueFormatter = IndexAxisValueFormatter(dayLabels)
                textSize = 10f
                setDrawAxisLine(true)
                axisLineColor = ContextCompat.getColor(context, R.color.black)
            }

            // Y-axis configuration (left)
            axisLeft.apply {
                textColor = ContextCompat.getColor(context, R.color.black)
                axisMinimum = 0f
                axisMaximum = 5f
                setDrawGridLines(true)
                gridColor = Color.LTGRAY
                gridLineWidth = 0.5f
                textSize = 10f
                setDrawAxisLine(true)
                axisLineColor = ContextCompat.getColor(context, R.color.black)
            }

            // Disable right Y-axis
            axisRight.isEnabled = false

            // Remove border
            setDrawBorders(false)

            // Refresh chart
            invalidate()
        }
    }

    fun refreshChart(
        chart: LineChart,
        moodPreferences: MoodPreferences
    ) {
        setupMoodChart(chart, moodPreferences)
    }

}
