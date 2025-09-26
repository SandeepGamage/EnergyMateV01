package com.example.energymatev01

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity

class login_page : AppCompatActivity() {

    private lateinit var viewRegister: TextView
    private lateinit var email: EditText
    private lateinit var password: EditText
    private lateinit var submit: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_login_page)

        // Initialize all views
        viewRegister = findViewById(R.id.register)
        email = findViewById(R.id.emailInput)
        password = findViewById(R.id.passwordInput)
        submit = findViewById(R.id.loginSubmit)

        // Set click listener for register text
        viewRegister.setOnClickListener {
            val intent = Intent(this, register_page::class.java)
            startActivity(intent)
        }

        // Set click listener for login button
        submit.setOnClickListener {
            // Get current values from input fields when button is clicked
            val emailInput = email.text.toString().trim()
            val passwordInput = password.text.toString().trim()

            validateLogin(emailInput, passwordInput)
        }
    }

    private fun validateLogin(emailInput: String, passwordInput: String) {
        // Validate that fields are not empty
        if (emailInput.isEmpty()) {
            email.error = "Please enter your email"
            return
        } else {
            email.error = null
        }

        if (passwordInput.isEmpty()) {
            password.error = "Please enter your password"
            return
        } else {
            password.error = null
        }

        // Validate email format
        if (!isValidEmail(emailInput)) {
            email.error = "Please enter a valid email address"
            return
        }

        // Get saved user data from SharedPreferences
        val sharedPreferences = getSharedPreferences("user_data", MODE_PRIVATE)
        val savedEmail = sharedPreferences.getString("email", "")
        val savedPassword = sharedPreferences.getString("password", "")

        // Check if credentials match
        if (emailInput == savedEmail && passwordInput == savedPassword) {
            Toast.makeText(this, "Login successful!", Toast.LENGTH_SHORT).show()

            // Save login status
            val editor = sharedPreferences.edit()
            editor.putBoolean("isLoggedIn", true)
            editor.apply()

            // Navigate to main activity
            val intent = Intent(this, NavBar::class.java)
            startActivity(intent)
            finish()
        } else {
            Toast.makeText(this, "Invalid email or password", Toast.LENGTH_SHORT).show()
        }
    }

    private fun isValidEmail(email: String): Boolean {
        val emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+"
        return email.matches(emailPattern.toRegex())
    }

    override fun onStart() {
        super.onStart()

        // Optional: Check if user is already logged in
        val sharedPreferences = getSharedPreferences("user_data", MODE_PRIVATE)
        val isLoggedIn = sharedPreferences.getBoolean("isLoggedIn", false)

        if (isLoggedIn) {
            // User is already logged in, redirect to main activity
            val intent = Intent(this, NavBar::class.java)
            startActivity(intent)
            finish()
        }
    }
}