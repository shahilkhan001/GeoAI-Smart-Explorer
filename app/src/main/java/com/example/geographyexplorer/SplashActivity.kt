package com.example.geographyexplorer

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.animation.AlphaAnimation
import android.view.animation.Animation
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricManager.Authenticators.BIOMETRIC_STRONG
import androidx.biometric.BiometricPrompt
import androidx.core.content.ContextCompat
import com.example.geographyexplorer.databinding.ActivitySplashBinding
import com.google.firebase.auth.FirebaseAuth
import java.util.concurrent.Executor

@SuppressLint("CustomSplashScreen")
class SplashActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySplashBinding
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var executor: Executor
    private lateinit var biometricPrompt: BiometricPrompt
    private lateinit var promptInfo: BiometricPrompt.PromptInfo

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivitySplashBinding.inflate(layoutInflater)
        setContentView(binding.root)

        firebaseAuth = FirebaseAuth.getInstance()
        supportActionBar?.hide()

        startFadeAnimation()

        Handler(Looper.getMainLooper()).postDelayed({

            if (firebaseAuth.currentUser != null) {

                val sharedPrefs = getSharedPreferences("AppLockPrefs", MODE_PRIVATE)
                val isAppLockEnabled = sharedPrefs.getBoolean("is_app_lock_enabled", false)

                if (isAppLockEnabled) {
                    showBiometricPrompt()
                } else {
                    goToActivity(MainActivity::class.java)
                }

            } else {
                goToActivity(LoginActivity::class.java)
            }

        }, 2500)
    }

    private fun startFadeAnimation() {

        // Logo fade animation
        val logoFade = AlphaAnimation(0f, 1f)
        logoFade.duration = 1200
        logoFade.fillAfter = true

        binding.imageViewLogo.startAnimation(logoFade)

        // Title slide + scale animation
        binding.textViewAppName.translationY = 80f
        binding.textViewAppName.scaleX = 0.8f
        binding.textViewAppName.scaleY = 0.8f
        binding.textViewAppName.alpha = 0f

        binding.textViewAppName.animate()
            .translationY(0f)
            .scaleX(1f)
            .scaleY(1f)
            .alpha(1f)
            .setDuration(1200)
            .setStartDelay(300)
            .start()

        // Tagline animation (slightly delayed)
        binding.textViewTagline.translationY = 80f
        binding.textViewTagline.alpha = 0f

        binding.textViewTagline.animate()
            .translationY(0f)
            .alpha(1f)
            .setDuration(1000)
            .setStartDelay(600)
            .start()
    }

    private fun showBiometricPrompt() {

        executor = ContextCompat.getMainExecutor(this)

        biometricPrompt = BiometricPrompt(
            this,
            executor,
            object : BiometricPrompt.AuthenticationCallback() {

                override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                    super.onAuthenticationSucceeded(result)
                    goToActivity(MainActivity::class.java)
                }

                override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {
                    super.onAuthenticationError(errorCode, errString)
                    Toast.makeText(applicationContext, "Authentication failed", Toast.LENGTH_SHORT).show()
                    finish()
                }

                override fun onAuthenticationFailed() {
                    super.onAuthenticationFailed()
                    Toast.makeText(applicationContext, "Authentication failed", Toast.LENGTH_SHORT).show()
                }
            }
        )

        promptInfo = BiometricPrompt.PromptInfo.Builder()
            .setTitle("Geography Explorer Locked")
            .setSubtitle("Authenticate to open the app")
            .setNegativeButtonText("Cancel")
            .build()

        val biometricManager = BiometricManager.from(this)

        if (biometricManager.canAuthenticate(BIOMETRIC_STRONG) ==
            BiometricManager.BIOMETRIC_SUCCESS
        ) {

            biometricPrompt.authenticate(promptInfo)

        } else {

            Toast.makeText(this, "No biometrics found. Disabling app lock.", Toast.LENGTH_SHORT).show()

            getSharedPreferences("AppLockPrefs", MODE_PRIVATE)
                .edit()
                .putBoolean("is_app_lock_enabled", false)
                .apply()

            goToActivity(MainActivity::class.java)
        }
    }

    private fun goToActivity(activityClass: Class<*>) {
        val intent = Intent(this, activityClass)
        startActivity(intent)
        finish()
    }
}