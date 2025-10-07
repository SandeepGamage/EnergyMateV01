package com.example.energymatev01.fragment

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.energymatev01.R
import com.example.energymatev01.data.Mood
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlin.let

/**
 * RecyclerView Adapter for displaying mood journal entries
 * Uses ListAdapter for efficient list updates with automatic diffing
 */
class MoodAdapter : ListAdapter<Mood, MoodAdapter.VH>(DIFF) {

    // Callback functions for handling user interactions on mood entries
    // These are nullable and will be set by the fragment/activity using this adapter
    var onEdit: ((Mood) -> Unit)? = null     // Lambda function called when user taps edit
    var onDelete: ((Mood) -> Unit)? = null   // Lambda function called when user taps delete

    companion object {
        /**
         * DiffUtil callback that helps RecyclerView efficiently update only changed items
         * Instead of refreshing the entire list, it compares old and new data
         */
        private val DIFF = object : DiffUtil.ItemCallback<Mood>() {
            /**
             * Determines if two items represent the same mood entry
             * Uses timestamp as unique identifier since each mood entry has unique creation time
             */
            override fun areItemsTheSame(oldItem: Mood, newItem: Mood): Boolean =
                oldItem.timestamp == newItem.timestamp

            /**
             * Determines if the contents of two items are identical
             * If false, the item will be updated with new data
             */
            override fun areContentsTheSame(oldItem: Mood, newItem: Mood): Boolean =
                oldItem == newItem
        }
    }

    /**
     * Creates new ViewHolder instances when RecyclerView needs them
     * This is called when a new item comes into view and no existing ViewHolder can be reused
     */
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        // Inflate the XML layout for a single mood entry item
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_mood_entry, parent, false)
        return VH(view) // Return new ViewHolder containing the inflated view
    }

    /**
     * Binds data to an existing ViewHolder when RecyclerView scrolls
     * This is called frequently as user scrolls through the list
     */
    override fun onBindViewHolder(holder: VH, position: Int) {
        // Get the mood entry at current position and bind it to the ViewHolder
        holder.bind(getItem(position))
    }

    /**
     * ViewHolder class - holds references to UI elements for each mood entry
     * Reduces findViewById calls by caching view references
     */
    class VH(itemView: View) : RecyclerView.ViewHolder(itemView) {

        // Cache references to all UI elements in the mood entry layout
        private val moodIcon: ImageView = itemView.findViewById(R.id.moodIcon)        // Emoji icon
        private val moodTitle: TextView = itemView.findViewById(R.id.moodTitle)       // Mood name
        private val moodTime: TextView = itemView.findViewById(R.id.moodTime)         // Timestamp
        private val moodNote: TextView = itemView.findViewById(R.id.moodNote)         // User note
        private val editAction: TextView = itemView.findViewById(R.id.editAction)     // Edit button
        private val deleteAction: TextView = itemView.findViewById(R.id.deleteAction) // Delete button

        // Date formatter for displaying timestamp in readable format
        // Example output: "Oct 2, 7:24 PM"
        private val formatter = SimpleDateFormat("MMM d, h:mm a", Locale.getDefault())

        /**
         * Binds mood entry data to the UI elements
         * Called every time this ViewHolder is recycled for a new mood entry
         */
        fun bind(entry: Mood) {
            // Set basic mood information
            moodTitle.text = entry.mood                           // Display mood name (e.g., "Excellent")
            moodNote.text = entry.note                            // Display user's journal note
            moodTime.text = formatter.format(Date(entry.timestamp)) // Convert timestamp to readable date

            // Select appropriate emoji icon based on mood type
            // This creates visual consistency for EnergyMate's mood tracking
            val iconRes = when (entry.mood) {
                "Excellent" -> R.drawable.excellent_emoji      // Happy/excellent emoji
                "Good" -> R.drawable.good_emoji               // Good/positive emoji
                "Okay" -> R.drawable.okay_emoji               // Neutral emoji
                "Not Great" -> R.drawable.not_great_emoji     // Sad/concerned emoji
                "Bad" -> R.drawable.bad_emoji                 // Very sad/negative emoji
                else -> R.drawable.emoji_neutral              // Default fallback emoji
            }
            moodIcon.setImageResource(iconRes) // Apply the selected emoji to ImageView

            /**
             * Set up click listeners for edit and delete actions
             * These use a safe navigation pattern to find the parent adapter
             */
            editAction.setOnClickListener {
                // Navigate up view hierarchy: TextView -> ItemView -> RecyclerView -> Adapter
                (itemView.parent as? RecyclerView)?.adapter?.let { adapter ->
                    // Safely cast to MoodAdapter and invoke edit callback
                    if (adapter is MoodAdapter) adapter.onEdit?.invoke(entry)
                }
            }

            deleteAction.setOnClickListener {
                // Same pattern as edit - safely find adapter and invoke delete callback
                (itemView.parent as? RecyclerView)?.adapter?.let { adapter ->
                    if (adapter is MoodAdapter) adapter.onDelete?.invoke(entry)
                }
            }
        }
    }
}



