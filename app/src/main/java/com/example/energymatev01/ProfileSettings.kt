package com.example.energymatev01

import android.content.SharedPreferences
import android.os.Bundle
import android.widget.ImageButton
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.energymatev01.data.UserPreferences
import com.google.android.material.textfield.TextInputEditText

class ProfileSettings : AppCompatActivity() {
    private lateinit var userPref : UserPreferences
    private lateinit var backBTN: ImageButton
    private lateinit var nameEditText: TextInputEditText
    private lateinit var emailEditText: TextInputEditText
    private lateinit var phoneEditText: TextInputEditText
    private lateinit var cancelButton: androidx.appcompat.widget.AppCompatButton
    private lateinit var saveButton: androidx.appcompat.widget.AppCompatButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_profile_settings)

        userPref = UserPreferences(this)

        backBTN = findViewById(R.id.profileBackBTN)
        nameEditText = findViewById(R.id.nameEditText)
        emailEditText = findViewById(R.id.emailEditText)
        phoneEditText = findViewById(R.id.phoneEditText)
        cancelButton = findViewById(R.id.cancelButton)
        saveButton = findViewById(R.id.saveButton)

        loadUserData()

        backBTN.setOnClickListener {
            finish()
        }

        cancelButton.setOnClickListener {
            loadUserData()
        }

        saveButton.setOnClickListener {
            updateUserData()
            finish()
        }

    }

    private fun loadUserData(){
        var storedName = userPref.getName()
        var storedEmail = userPref.getEmail()
        var storedPhone = userPref.getMobileNumber()

        nameEditText.setText(storedName ?: "")
        emailEditText.setText(storedEmail ?: "")
        phoneEditText.setText(storedPhone ?: "")
    }

    private fun updateUserData(){
        var newName = nameEditText.text.toString()
        var newEmail = emailEditText.text.toString()
        var newPhone = phoneEditText.text.toString()
        var storedPassword = userPref.getPassword()
        var storedAddress = userPref.getAddress()

        userPref.saveUser(newName, newEmail, storedPassword, storedAddress, newPhone)
    }

}