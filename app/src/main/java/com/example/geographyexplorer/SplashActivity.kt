package com.example.geographyexplorer

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricManager.Authenticators.BIOMETRIC_STRONG
import androidx.biometric.BiometricPrompt
import androidx.core.content.ContextCompat
import com.google.firebase.auth.FirebaseAuth
import java.util.concurrent.Executor

@SuppressLint("CustomSplashScreen")
class SplashActivity : AppCompatActivity() {

    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var executor: Executor
    private lateinit var biometricPrompt: BiometricPrompt
    private lateinit var promptInfo: BiometricPrompt.PromptInfo

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        firebaseAuth = FirebaseAuth.getInstance()
        supportActionBar?.hide()

        // >>> DELAY RE-ADDED so splash image shows for 2.5 seconds
        Handler(Looper.getMainLooper()).postDelayed({

            // Check if user is logged in
            if (firebaseAuth.currentUser != null) {
                // User is logged in, now check if app lock is enabled
                val sharedPrefs = getSharedPreferences("AppLockPrefs", MODE_PRIVATE)
                val isAppLockEnabled = sharedPrefs.getBoolean("is_app_lock_enabled", false)

                if (isAppLockEnabled) {
                    // App lock is ON, show biometric prompt
                    showBiometricPrompt()
                } else {
                    // App lock is OFF, go straight to main
                    goToActivity(MainActivity::class.java)
                }
            } else {
                // User is not logged in, go to login
                goToActivity(LoginActivity::class.java)
            }

        }, 2500) // 2500 milliseconds = 2.5 seconds
    }

    private fun showBiometricPrompt() {
        executor = ContextCompat.getMainExecutor(this)
        biometricPrompt = BiometricPrompt(this, executor,
            object : BiometricPrompt.AuthenticationCallback() {
                override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                    super.onAuthenticationSucceeded(result)
                    // Success! Go to the main app
                    goToActivity(MainActivity::class.java)
                }
                override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {
                    super.onAuthenticationError(errorCode, errString)
                    // Error or user cancelled
                    Toast.makeText(applicationContext, "Authentication failed", Toast.LENGTH_SHORT).show()
                    finish() // Close the app
                }
                override fun onAuthenticationFailed() {
                    super.onAuthenticationFailed()
                    // Invalid fingerprint/face
                    Toast.makeText(applicationContext, "Authentication failed", Toast.LENGTH_SHORT).show()
                }
            })

        promptInfo = BiometricPrompt.PromptInfo.Builder()
            .setTitle("Geography Explorer Locked")
            .setSubtitle("Authenticate to open the app")
            .setNegativeButtonText("Cancel")
            .build()

        // Check if biometrics are available before showing the prompt
        val biometricManager = BiometricManager.from(this)
        if (biometricManager.canAuthenticate(BIOMETRIC_STRONG) == BiometricManager.BIOMETRIC_SUCCESS) {
            biometricPrompt.authenticate(promptInfo)
        } else {
            // No biometrics enrolled, or not available.
            // In a real app, you'd tell the user to enable it, but for simplicity, we'll just log them in.
            Toast.makeText(this, "No biometrics found. Disabling app lock.", Toast.LENGTH_SHORT).show()
            getSharedPreferences("AppLockPrefs", MODE_PRIVATE).edit().putBoolean("is_app_lock_enabled", false).apply()
            goToActivity(MainActivity::class.java)
        }
    }

    // Helper function to start an activity and finish this one
    private fun goToActivity(activityClass: Class<*>) {
        val intent = Intent(this, activityClass)
        startActivity(intent)
        finish()
    }
}
