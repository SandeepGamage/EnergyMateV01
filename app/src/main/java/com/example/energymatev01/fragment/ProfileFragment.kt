package com.example.energymatev01.fragment

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.example.energymatev01.ChangePassword
import com.example.energymatev01.MainActivity
import com.example.energymatev01.PrivacyPolicyPage
import com.example.energymatev01.ProfileSettings
import com.example.energymatev01.R
import com.example.energymatev01.data.UserPreferences
import com.example.energymatev01.data.HabitsPreferences
import com.example.energymatev01.data.HydrationPreferences
import com.example.energymatev01.data.MoodPreferences
import com.example.energymatev01.login_page
import com.example.energymatev01.onboarding_screen01

class ProfileFragment : Fragment() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_profile, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val userPrefs = UserPreferences(requireContext())
        val habitsPrefs = HabitsPreferences(requireContext())
        val hydrationPrefs = HydrationPreferences(requireContext())
        val moodPrefs = MoodPreferences(requireContext())
        val builder: AlertDialog.Builder

        // Update profile info
        val nameView: TextView = view.findViewById(R.id.profileName)
        val emailView: TextView = view.findViewById(R.id.profileEmail)
        val storedUserName = userPrefs.getName()
        nameView.text = storedUserName?.uppercase()
        val storedUserEmail = userPrefs.getEmail()
        emailView.text = storedUserEmail

        // Update wellness stats with real data
        val habits = habitsPrefs.getHabits()
        val todaysMoodCount = moodPrefs.getTodaysMoodCount()
        val waterCount = hydrationPrefs.getGlassesCount()
        val waterGoal = hydrationPrefs.getDailyGoal().coerceAtLeast(1)
        val hydrationPercent = (waterCount * 100 / waterGoal).coerceAtMost(100)
        val todaysProgress = habitsPrefs.computeCompletionPercent(hydrationPercent)

        view.findViewById<TextView>(R.id.profileActiveHabitsCount)?.text = habits.size.toString()
        view.findViewById<TextView>(R.id.profileMoodEntriesCount)?.text = todaysMoodCount.toString()
        view.findViewById<TextView>(R.id.profileTodaysProgress)?.text = "$todaysProgress%"
        view.findViewById<TextView>(R.id.profileWaterIntake)?.text = waterCount.toString()

        val editProfileView: View = view.findViewById(R.id.editProfileBTN)
        editProfileView.setOnClickListener {
            val intent = Intent(requireContext(), ProfileSettings::class.java)
            startActivity(intent)
        }

        val changePassword : View = view.findViewById(R.id.changePasswordBTN)
        changePassword.setOnClickListener {
            val intent = Intent(requireContext(), ChangePassword::class.java)
            startActivity(intent)
        }

        val privacyPolicyPage: View = view.findViewById(R.id.privacyPolicyBTN)
        privacyPolicyPage.setOnClickListener {
            val intent = Intent(requireContext(), PrivacyPolicyPage::class.java)
            startActivity(intent)
        }

        builder = AlertDialog.Builder(requireContext())
        val logoutView: View = view.findViewById(R.id.logoutBTN)
        logoutView.setOnClickListener {
            userPrefs.setLoggedIn(false)

            builder.setTitle("Logout")
                .setMessage("Are you sure you want to logout?")
                .setCancelable(true)
                .setPositiveButton("Yes") { dialog, which ->
                    val intent = Intent(requireContext(), login_page::class.java)
                    startActivity(intent)
                }
                .setNegativeButton("No") { dialog, which ->
                    dialog.cancel()
                }
                .setNeutralButton("Cancel") { dialog, which ->
                    Toast.makeText(
                        requireContext(),
                        "clicked cancel\noperation canceled",
                        Toast.LENGTH_LONG
                    ).show()
                }
                .show()

//            val intent = Intent(requireContext(), onboarding_screen01::class.java)
//            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
//            startActivity(intent)
        }

        val clearRow: View = view.findViewById(R.id.clearDataText)
        clearRow.setOnClickListener {
            HydrationPreferences(requireContext()).clearAll()
            MoodPreferences(requireContext()).clearAll()
            HabitsPreferences(requireContext()).clearAll()
            UserPreferences(requireContext()).clearAll()

            val intent = Intent(requireContext(), MainActivity::class.java)
            startActivity(intent)

            android.widget.Toast.makeText(requireContext(), "Cleared hydration and mood data", android.widget.Toast.LENGTH_SHORT).show()
        }
    }
}


