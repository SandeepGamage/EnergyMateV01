package com.example.energymatev01.fragment

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.energymatev01.R
import com.example.energymatev01.data.Habit
import kotlin.ranges.coerceAtLeast

class HabitAdapter : ListAdapter<Habit, HabitAdapter.VH>(DIFF) {

    var onIncrement: ((Habit) -> Unit)? = null
    var onDecrement: ((Habit) -> Unit)? = null
    var onEdit: ((Habit) -> Unit)? = null
    var onDelete: ((Habit) -> Unit)? = null

    companion object {
        private val DIFF = object : DiffUtil.ItemCallback<Habit>() {
            override fun areItemsTheSame(oldItem: Habit, newItem: Habit): Boolean = oldItem.id == newItem.id
            override fun areContentsTheSame(oldItem: Habit, newItem: Habit): Boolean = oldItem == newItem
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_habit, parent, false)
        return VH(view)
    }

    override fun onBindViewHolder(holder: VH, position: Int) {
        holder.bind(getItem(position))
    }

    inner class VH(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val name: TextView = itemView.findViewById(R.id.habitName)
        private val progress: TextView = itemView.findViewById(R.id.habitProgress)
        private val dec: TextView = itemView.findViewById(R.id.decrementHabit)
        private val inc: TextView = itemView.findViewById(R.id.incrementHabit)
        private val edit: TextView = itemView.findViewById(R.id.editHabit)
        private val del: TextView = itemView.findViewById(R.id.deleteHabit)

        fun bind(habit: Habit) {
            name.text = habit.name
            val count = habit.completedTodayCount
            val target = habit.targetPerDay.coerceAtLeast(1)
            progress.text = "${count} / ${target} today"

            dec.setOnClickListener { onDecrement?.invoke(habit) }
            inc.setOnClickListener { onIncrement?.invoke(habit) }
            edit.setOnClickListener { onEdit?.invoke(habit) }
            del.setOnClickListener { onDelete?.invoke(habit) }
        }
    }
}


