package com.example.geographyexplorer

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.appcompat.app.AppCompatActivity

@SuppressLint("CustomSplashScreen")
class SplashActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        // Hide the action bar for a clean splash screen
        supportActionBar?.hide()



        // Use a Handler to delay the transition to the next screen
        Handler(Looper.getMainLooper()).postDelayed({
            // Create an Intent to start LoginActivity
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)

            // Finish this activity so the user can't navigate back to it
            finish()
        }, 2500) // 2500 milliseconds = 2.5 seconds
    }
}