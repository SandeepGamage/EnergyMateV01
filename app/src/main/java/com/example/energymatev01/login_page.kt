package com.example.energymatev01

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.example.energymatev01.data.UserPreferences

class login_page : AppCompatActivity() {

    private lateinit var viewRegister: TextView
    private lateinit var email: EditText
    private lateinit var password: EditText
    private lateinit var submit: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_login_page)

        viewRegister = findViewById(R.id.register)
        email = findViewById(R.id.emailInput)
        password = findViewById(R.id.passwordInput)
        submit = findViewById(R.id.loginSubmit)

        viewRegister.setOnClickListener {
            val intent = Intent(this, register_page::class.java)
            startActivity(intent)
        }

        submit.setOnClickListener {
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

        val userPreferences = UserPreferences(this)
        val savedEmail = userPreferences.getEmail()
        val savedPassword = userPreferences.getPassword()

        if (emailInput == savedEmail && passwordInput == savedPassword) {
            Toast.makeText(this, "Login successful!", Toast.LENGTH_SHORT).show()

            userPreferences.setLoggedIn(true)

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