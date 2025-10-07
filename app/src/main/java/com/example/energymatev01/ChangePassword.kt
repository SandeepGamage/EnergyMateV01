package com.example.energymatev01

import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.example.energymatev01.data.UserPreferences
import com.example.energymatev01.databinding.ActivityChangePasswordBinding
import com.google.android.material.textfield.TextInputEditText

class ChangePassword : AppCompatActivity() {

    private lateinit var storedPassword: String

    private lateinit var userPref : UserPreferences
    private lateinit var binding: ActivityChangePasswordBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        userPref = UserPreferences(this)

        storedPassword = userPref.getPassword()

        binding = ActivityChangePasswordBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupClickListeners()
    }

    private fun setupClickListeners() {
        binding.backButton.setOnClickListener {
            finish()
        }

        binding.cancelButton.setOnClickListener {
            finish()
        }

        binding.changePasswordButton.setOnClickListener {
            if (validateInputs()) {
                changePassword()
            }
        }
    }

    private fun validateInputs(): Boolean {
        val currentPassword = binding.currentPasswordEditText.text.toString()
        val newPassword = binding.newPasswordEditText.text.toString()
        val confirmPassword = binding.confirmPasswordEditText.text.toString()

        if (currentPassword.isEmpty()) {
            binding.currentPasswordInputLayout.error = "Current password is required"
            return false
        }

        if (newPassword.isEmpty()) {
            binding.newPasswordInputLayout.error = "New password is required"
            return false
        }

        if (newPassword.length < 8) {
            binding.newPasswordInputLayout.error = "Password must be at least 8 characters"
            return false
        }

        if (confirmPassword.isEmpty()) {
            binding.confirmPasswordInputLayout.error = "Please confirm your new password"
            return false
        }

        if (newPassword != confirmPassword) {
            binding.confirmPasswordInputLayout.error = "Passwords do not match"
            return false
        }

        if (currentPassword == newPassword) {
            binding.newPasswordInputLayout.error = "New password must be different from current password"
            return false
        }

        if (currentPassword != storedPassword){
            binding.currentPasswordInputLayout.error = "Current password is incorrect"
            return false
        }

        // Clear previous errors
        clearErrors()
        return true
    }

    private fun clearErrors() {
        binding.currentPasswordInputLayout.error = null
        binding.newPasswordInputLayout.error = null
        binding.confirmPasswordInputLayout.error = null
    }

    private fun changePassword() {
        // TODO: Implement actual password change logic
        // This would typically involve:
        // 1. Verify current password with server/database
        // 2. Hash the new password
        // 3. Update password in server/database
        // 4. Show success message

        userPref = UserPreferences(this)
        userPref.savePassword(binding.newPasswordEditText.text.toString())
        
        Toast.makeText(this, "Password changed successfully!", Toast.LENGTH_SHORT).show()
        finish()
    }
}