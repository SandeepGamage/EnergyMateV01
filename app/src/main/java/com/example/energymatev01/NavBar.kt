package com.example.energymatev01

import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.example.energymatev01.fragment.HomeFragment
import com.example.energymatev01.fragment.HydrationFragment
import com.example.energymatev01.fragment.MoodJournalFragment
import com.example.energymatev01.fragment.ProfileFragment

class NavBar : AppCompatActivity() {

    private lateinit var homeIcon: ImageView
    private lateinit var moodIcon: ImageView
    private lateinit var dropIcon: ImageView
    private lateinit var profileIcon: ImageView
    private var currentSelectedIcon: ImageView? = null

    private lateinit var homeText: TextView
    private lateinit var moodText: TextView
    private lateinit var dropText: TextView
    private lateinit var profileText: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_nav_bar)

        initializeViews()
        setupBottomNavigation()

        // Load default fragment (Home) when activity starts
        if (savedInstanceState == null) {
            loadFragment(HomeFragment(), "HOME_FRAGMENT")
            setSelectedIcon(homeIcon)
            homeText.text = "Home"
        }
    }

    private fun initializeViews() {
        homeIcon = findViewById(R.id.homeIcon)
        moodIcon = findViewById(R.id.moodIcon)
        dropIcon = findViewById(R.id.waterDropIcon)
        profileIcon = findViewById(R.id.profileIcon)
        homeText = findViewById(R.id.homeText)
        moodText = findViewById(R.id.moodText)
        dropText = findViewById(R.id.waterDropText)
        profileText = findViewById(R.id.profileText)

    }

    private fun setupBottomNavigation() {
        homeIcon.setOnClickListener {
            loadFragment(HomeFragment(), "HOME_FRAGMENT")
            setSelectedIcon(homeIcon)
            homeText.text = "Home"
        }

        moodIcon.setOnClickListener {
            loadFragment(MoodJournalFragment(), "MOOD_FRAGMENT")
            setSelectedIcon(moodIcon)
            moodText.text = "Mood"
        }

        dropIcon.setOnClickListener {
            loadFragment(HydrationFragment(), "HYDRATION_FRAGMENT")
            setSelectedIcon(dropIcon)
            dropText.text = "Hydration"
        }

        profileIcon.setOnClickListener {
            // Check if ProfileFragment exists before loading
            try {
                loadFragment(ProfileFragment(), "PROFILE_FRAGMENT")
                setSelectedIcon(profileIcon)
                profileText.text = "Profile"
            } catch (e: Exception) {
                // Fallback to home if profile doesn't exist
                loadFragment(HomeFragment(), "HOME_FRAGMENT")
                setSelectedIcon(homeIcon)
                homeText.text = "Home"
            }
        }
    }

    private fun setSelectedIcon(selectedIcon: ImageView) {
        // Reset all icons to default color
        homeIcon.setColorFilter(ContextCompat.getColor(this, android.R.color.darker_gray))
        moodIcon.setColorFilter(ContextCompat.getColor(this, android.R.color.darker_gray))
        dropIcon.setColorFilter(ContextCompat.getColor(this, android.R.color.darker_gray))
        profileIcon.setColorFilter(ContextCompat.getColor(this, android.R.color.darker_gray))
        homeText.text = ""
        moodText.text = ""
        dropText.text = ""
        profileText.text = ""

        // Set selected icon to accent color
        selectedIcon.setColorFilter(ContextCompat.getColor(this, R.color.hm_orange))
        currentSelectedIcon = selectedIcon
    }

    private fun loadFragment(fragment: Fragment, tag: String) {
        val transaction = supportFragmentManager.beginTransaction()

        // Add slide animations
        transaction.setCustomAnimations(
            R.anim.slide_in_right,
            R.anim.slide_out_left,
            R.anim.slide_in_left,
            R.anim.slide_out_right
        )

        transaction.replace(R.id.fragmentContainer, fragment, tag)
        transaction.setReorderingAllowed(true)
        transaction.commit()
    }
}