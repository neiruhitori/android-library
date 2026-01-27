package com.example.perpustakaan.activity

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.MotionEvent
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import com.example.perpustakaan.network.CartManager
import com.example.perpustakaan.util.SessionManager

open class BaseActivity : AppCompatActivity() {

    private val idleHandler = Handler(Looper.getMainLooper())
    private val idleTimeout = 3 * 60 * 1000L // 3 menit dalam milliseconds
    
    private val idleRunnable = Runnable {
        // Tampilkan idle splash screen
        showIdleSplashScreen()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // Keep screen awake - layar tidak sleep
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
    }

    override fun onResume() {
        super.onResume()
        // Reset timer saat activity resume
        resetIdleTimer()
    }

    override fun onPause() {
        super.onPause()
        // Stop timer saat activity pause
        stopIdleTimer()
    }

    override fun onDestroy() {
        super.onDestroy()
        // Clear screen awake flag
        window.clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        stopIdleTimer()
    }

    override fun dispatchTouchEvent(ev: MotionEvent?): Boolean {
        // Reset timer setiap ada touch event
        resetIdleTimer()
        return super.dispatchTouchEvent(ev)
    }

    private fun resetIdleTimer() {
        // Hapus callback sebelumnya
        idleHandler.removeCallbacks(idleRunnable)
        // Set timer baru
        idleHandler.postDelayed(idleRunnable, idleTimeout)
    }

    private fun stopIdleTimer() {
        idleHandler.removeCallbacks(idleRunnable)
    }

    private fun showIdleSplashScreen() {
        // Clear cart dan session sebelum ke idle splash
        CartManager.clearCart()
        SessionManager.clearSiswaSession(this)
        
        val intent = Intent(this, IdleSplashActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
        startActivity(intent)
    }
}
