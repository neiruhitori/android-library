package com.example.perpustakaan.activity

import android.content.Intent
import android.os.Bundle
import android.view.WindowManager
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import androidx.appcompat.app.AppCompatActivity
import com.example.perpustakaan.R
import com.example.perpustakaan.databinding.ActivityIdleSplashBinding

class IdleSplashActivity : AppCompatActivity() {
    
    private lateinit var binding: ActivityIdleSplashBinding
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityIdleSplashBinding.inflate(layoutInflater)
        setContentView(binding.root)
        
        // Keep screen awake - layar tidak sleep
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        
        // Hide action bar
        supportActionBar?.hide()
        
        // Ketuk di mana saja untuk kembali ke Dashboard
        binding.root.setOnClickListener {
            navigateToDashboard()
        }
    }
    
    override fun onResume() {
        super.onResume()
        // Start animations saat activity terlihat
        startAnimations()
    }
    
    override fun onPause() {
        super.onPause()
        // Stop semua animasi saat activity tidak terlihat
        stopAnimations()
    }
    
    override fun onDestroy() {
        super.onDestroy()
        // Clear screen awake flag
        window.clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
    }
    
    private fun startAnimations() {
        // Load animations
        val pulseAnim = AnimationUtils.loadAnimation(this, R.anim.logo_pulse).apply {
            repeatCount = Animation.INFINITE
            repeatMode = Animation.REVERSE
        }
        
        val logoScaleAnim = AnimationUtils.loadAnimation(this, R.anim.logo_scale_up)
        val titleAnim = AnimationUtils.loadAnimation(this, R.anim.fade_in)
        val schoolAnim = AnimationUtils.loadAnimation(this, R.anim.fade_in_delayed)
        
        val blinkAnim = AnimationUtils.loadAnimation(this, R.anim.blink_text).apply {
            repeatCount = Animation.INFINITE
            repeatMode = Animation.REVERSE
        }
        
        // Background animasi loop terus menerus (pulse)
        binding.ivBackground.startAnimation(pulseAnim)
        
        // Logo tengah dengan scale up lalu statis
        binding.ivLogo.startAnimation(logoScaleAnim)
        
        // Text elements dengan fade in
        binding.tvAppName.startAnimation(titleAnim)
        binding.layoutSchoolInfo.startAnimation(schoolAnim)
        
        // Text dan icon "Ketuk..." berkedip terus
        binding.tvTapToContinue.startAnimation(blinkAnim)
        binding.ivTouchIcon.startAnimation(blinkAnim)
    }
    
    private fun stopAnimations() {
        // Clear semua animasi untuk hemat resource
        binding.ivBackground.clearAnimation()
        binding.ivLogo.clearAnimation()
        binding.tvAppName.clearAnimation()
        binding.layoutSchoolInfo.clearAnimation()
        binding.tvTapToContinue.clearAnimation()
        binding.ivTouchIcon.clearAnimation()
    }
    
    private fun navigateToDashboard() {
        // Stop animasi sebelum pindah
        stopAnimations()
        
        // Kembali ke dashboard dan clear semua activity di atasnya
        val intent = Intent(this, DashboardActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
    }
    
    @Suppress("DEPRECATION")
    override fun onBackPressed() {
        // Disable back button - harus tap untuk lanjut
        // Do nothing
    }
}
