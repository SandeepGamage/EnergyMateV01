package com.example.energymatev01

import android.os.Bundle
import android.widget.ImageView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment

class NavBar : AppCompatActivity() {

    private lateinit var homeIcon: ImageView
    private lateinit var moodIcon: ImageView
    private lateinit var dropIcon: ImageView
    private lateinit var profileIcon: ImageView
    private var currentSelectedIcon: ImageView? = null

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
        }
    }

    private fun initializeViews() {
        homeIcon = findViewById(R.id.homeIcon)
        moodIcon = findViewById(R.id.moodIcon)
        dropIcon = findViewById(R.id.waterDropIcon)
        profileIcon = findViewById(R.id.profileIcon)
    }

    private fun setupBottomNavigation() {
        homeIcon.setOnClickListener {
            loadFragment(HomeFragment(), "HOME_FRAGMENT")
            setSelectedIcon(homeIcon)
        }

        moodIcon.setOnClickListener {
            loadFragment(MoodJournalFragment(), "MOOD_FRAGMENT")
            setSelectedIcon(moodIcon)
        }

        dropIcon.setOnClickListener {
            loadFragment(HydrationFragment(), "HYDRATION_FRAGMENT")
            setSelectedIcon(dropIcon)
        }

        profileIcon.setOnClickListener {
            // Check if ProfileFragment exists before loading
            try {
                loadFragment(ProfileFragment(), "PROFILE_FRAGMENT")
                setSelectedIcon(profileIcon)
            } catch (e: Exception) {
                // Fallback to home if profile doesn't exist
                loadFragment(HomeFragment(), "HOME_FRAGMENT")
                setSelectedIcon(homeIcon)
            }
        }
    }

    private fun setSelectedIcon(selectedIcon: ImageView) {
        // Reset all icons to default color
        homeIcon.setColorFilter(ContextCompat.getColor(this, android.R.color.darker_gray))
        moodIcon.setColorFilter(ContextCompat.getColor(this, android.R.color.darker_gray))
        dropIcon.setColorFilter(ContextCompat.getColor(this, android.R.color.darker_gray))
        profileIcon.setColorFilter(ContextCompat.getColor(this, android.R.color.darker_gray))

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