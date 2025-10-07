package com.example.energymatev01

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class onboarding_screen03 : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_onboarding_screen03)

        val nextBTN : Button = findViewById(R.id.nextBTN2)

        nextBTN.setOnClickListener {
            val intent = Intent(this, login_page::class.java)
            startActivity(intent)
        }

    }
}