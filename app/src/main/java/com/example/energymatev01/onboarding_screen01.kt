package com.example.energymatev01

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class onboarding_screen01 : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_onboarding_screen01)

        val getStartedBTN : Button = findViewById(R.id.getStartedBTN)

        getStartedBTN.setOnClickListener {
            val intent = Intent(this, onboarding_screen02::class.java)
            startActivity(intent)
        }


    }
}