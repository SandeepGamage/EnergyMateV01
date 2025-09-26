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
import com.example.energymatev01.databinding.ActivityRegisterPageBinding

class register_page : AppCompatActivity() {

    private lateinit var viewLogin: TextView
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var registerSubmit: Button
    private lateinit var emailInput: EditText
    private lateinit var nameInput: EditText
    private lateinit var passwordInput: EditText

    private lateinit var repasswordInput: EditText
    private lateinit var addressInput: EditText
    private lateinit var mobileNumberInput: EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_register_page)

        viewLogin = findViewById(R.id.login)

        viewLogin.setOnClickListener {
            intent = Intent(this, login_page::class.java)
            startActivity(intent)
        }

        // Initialize views using findViewById
        registerSubmit = findViewById(R.id.registerSubmit)
        emailInput = findViewById(R.id.emailInput)
        nameInput = findViewById(R.id.nameInput)
        passwordInput = findViewById(R.id.passwordInput)
        repasswordInput = findViewById(R.id.rePasswordInput)
        addressInput = findViewById(R.id.addressInput)
        mobileNumberInput = findViewById(R.id.mobileNumberInput)

        registerSubmit.setOnClickListener {
            val email = emailInput.text.toString().trim()
            val name = nameInput.text.toString().trim()
            val password = passwordInput.text.toString().trim()
            val repassword = repasswordInput.text.toString().trim()
            val address = addressInput.text.toString().trim()
            val mobileNumber = mobileNumberInput.text.toString().trim()

            // Clear previous errors
            clearErrors()

            // Validate all fields
            if (validateInputs(email, name, password, repassword, address, mobileNumber)) {
                // All fields are valid, proceed with registration
                performRegistration(email, name, password, address, mobileNumber)
            }
        }
    }

    private fun clearErrors() {
        emailInput.error = null
        nameInput.error = null
        passwordInput.error = null
        addressInput.error = null
        mobileNumberInput.error = null
    }

    private fun validateInputs(
        email: String,
        name: String,
        password: String,
        repassword: String,
        address: String,
        mobileNumber: String
    ): Boolean {
        var isValid = true

        if (password != repassword) {
            passwordInput.error = "Passwords do not match"
            repasswordInput.error = "Passwords do not match"
            isValid = false
        }

        if(password.length<8){
            passwordInput.error = "Password must be at least 8 characters"
            repasswordInput.error = "Password must be at least 8 characters"
            isValid = false
        }

        if (email.isEmpty()) {
            emailInput.error = "Please enter your email"
            isValid = false
        } else if (!isValidEmail(email)) {
            emailInput.error = "Please enter a valid email"
            isValid = false
        }

        if (name.isEmpty()) {
            nameInput.error = "Please enter your name"
            isValid = false
        }

        if (password.isEmpty()) {
            passwordInput.error = "Please enter your password"
            isValid = false
        } else if (password.length < 6) {
            passwordInput.error = "Password must be at least 6 characters"
            isValid = false
        }

        if (address.isEmpty()) {
            addressInput.error = "Please enter your address"
            isValid = false
        }

        if (mobileNumber.isEmpty()) {
            mobileNumberInput.error = "Please enter your mobile number"
            isValid = false
        } else if (!isValidMobileNumber(mobileNumber)) {
            mobileNumberInput.error = "Please enter a valid mobile number"
            isValid = false
        }

        return isValid
    }

    private fun isValidEmail(email: String): Boolean {
        val emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+"
        return email.matches(emailPattern.toRegex())
    }

    private fun isValidMobileNumber(mobileNumber: String): Boolean {
        // Basic mobile number validation (adjust pattern as needed)
        val mobilePattern = "^[0-9]{10,15}$"
        return mobileNumber.matches(mobilePattern.toRegex())
    }

    private fun performRegistration(
        email: String,
        name: String,
        password: String,
        address: String,
        mobileNumber: String
    ) {
        // TODO: Implement your registration logic here

        sharedPreferences = getSharedPreferences("user_data", MODE_PRIVATE)

        val editor = sharedPreferences.edit()
        editor.putString("email", email)
        editor.putString("name", name)
        editor.putString("password", password)
        editor.putString("address", address)
        editor.putString("mobileNumber", mobileNumber)
        editor.apply()

        Toast.makeText(this, "Registration successful!", Toast.LENGTH_SHORT).show()

        // Example: Navigate to login page after successful registration
         val intent = Intent(this, login_page::class.java)
         startActivity(intent)
         finish()
    }
}