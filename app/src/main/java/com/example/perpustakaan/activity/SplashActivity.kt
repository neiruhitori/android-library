package com.example.perpustakaan.activity

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.WindowManager
import android.view.animation.AnimationUtils
import androidx.appcompat.app.AppCompatActivity
import com.example.perpustakaan.R
import com.example.perpustakaan.databinding.ActivitySplashBinding

class SplashActivity : AppCompatActivity() {
    
    private lateinit var binding: ActivitySplashBinding
    private val splashDuration = 2800L // 2.8 seconds
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySplashBinding.inflate(layoutInflater)
        setContentView(binding.root)
        
        // Keep screen awake - layar tidak sleep
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        
        // Hide action bar
        supportActionBar?.hide()
        
        // Start animations
        startAnimations()
        
        // Auto navigate after splash duration
        Handler(Looper.getMainLooper()).postDelayed({
            navigateToDashboard()
        }, splashDuration)
    }
    
    private fun startAnimations() {
        // Load animations
        val logoAnim = AnimationUtils.loadAnimation(this, R.anim.logo_scale_up)
        val titleAnim = AnimationUtils.loadAnimation(this, R.anim.fade_in)
        val schoolAnim = AnimationUtils.loadAnimation(this, R.anim.fade_in_delayed)
        val progressAnim = AnimationUtils.loadAnimation(this, R.anim.fade_in_progress)
        
        // Apply animations dengan smooth sequence
        binding.ivLogo.startAnimation(logoAnim)
        binding.tvAppName.startAnimation(titleAnim)
        binding.layoutSchoolInfo.startAnimation(schoolAnim)
        binding.progressBar.startAnimation(progressAnim)
    }
    
    private fun navigateToDashboard() {
        val intent = Intent(this, DashboardActivity::class.java)
        startActivity(intent)
        finish()
    }
    
    override fun onDestroy() {
        super.onDestroy()
        // Clear screen awake flag
        window.clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
    }
    
    @Suppress("DEPRECATION")
    override fun onBackPressed() {
        // Disable back button on splash screen
        // Do nothing
    }
}
