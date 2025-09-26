package com.example.energymatev01

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    private lateinit var sharedPreferences : SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        sharedPreferences = getSharedPreferences("user_data", MODE_PRIVATE)
        val isLoggedIn = sharedPreferences.getBoolean("isLoggedIn", false)
        lifecycleScope.launch {

            if(isLoggedIn){
                kotlinx.coroutines.delay(4000) // 50 seconds = 50,000 ms
                val intent = Intent(this@MainActivity, NavBar::class.java)
                startActivity(intent)
                finish()
            }else{
                kotlinx.coroutines.delay(4000) // 50 seconds = 50,000 ms
                val intent = Intent(this@MainActivity, onboarding_screen01::class.java)
                startActivity(intent)
                finish()
            }

        }

    }
}