package com.example.perpustakaan.activity

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.MotionEvent
import android.view.View
import com.example.perpustakaan.databinding.ActivityDashboardBinding
import com.example.perpustakaan.network.CartManager
import com.example.perpustakaan.util.SessionManager

class DashboardActivity : BaseActivity() {
    
    companion object {
        // Flag global untuk track apakah app baru dibuka atau hanya activity recreation
        private var appStartTime: Long = 0
        private const val SESSION_TIMEOUT = 5 * 60 * 1000L // 5 menit
    }
    
    private lateinit var binding: ActivityDashboardBinding
    private val idleHandler = Handler(Looper.getMainLooper())
    private val idleTimeoutMillis = 3 * 60 * 1000L // 3 menit
    
    private val idleRunnable = Runnable {
        if (SessionManager.hasSiswaSession(this)) {
            // Logout siswa DULU, baru update UI
            SessionManager.clearSiswaSession(this)
            android.widget.Toast.makeText(this, "Sesi habis, silakan login kembali", android.widget.Toast.LENGTH_SHORT).show()
            // Update UI untuk kembali ke halaman scan/manual
            checkSession()
        }
    }
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDashboardBinding.inflate(layoutInflater)
        setContentView(binding.root)
        
        // No action bar for custom header
        supportActionBar?.hide()
        
        // JANGAN clear session di onCreate - biarkan cart tetap ada
        // Clear session hanya di onResume jika app baru dibuka
        
        checkSession()
        setupCardButtons()
    }
    
    override fun onResume() {
        super.onResume()
        
        // Clear session hanya jika app benar-benar baru dibuka (cold start)
        // ATAU session sudah timeout (5 menit tidak ada activity)
        val currentTime = System.currentTimeMillis()
        if (appStartTime == 0L || (currentTime - appStartTime) > SESSION_TIMEOUT) {
            // App fresh start atau session timeout
            SessionManager.clearSiswaSession(this)
            appStartTime = currentTime
        }
        
        checkSession()
        resetIdleTimer()
    }
    
    override fun onPause() {
        super.onPause()
        stopIdleTimer()
    }
    
    override fun onUserInteraction() {
        super.onUserInteraction()
        resetIdleTimer()
    }
    
    override fun dispatchTouchEvent(ev: MotionEvent?): Boolean {
        resetIdleTimer()
        return super.dispatchTouchEvent(ev)
    }
    
    private fun resetIdleTimer() {
        idleHandler.removeCallbacks(idleRunnable)
        if (SessionManager.hasSiswaSession(this)) {
            idleHandler.postDelayed(idleRunnable, idleTimeoutMillis)
        }
    }
    
    private fun stopIdleTimer() {
        idleHandler.removeCallbacks(idleRunnable)
    }
    
    private fun checkSession() {
        // Cek apakah sudah ada siswa yang login
        if (SessionManager.hasSiswaSession(this)) {
            // Sudah login - tampilkan pilihan buku
            binding.layoutMetodePeminjaman.visibility = View.GONE
            binding.layoutPilihBuku.visibility = View.VISIBLE
            
            // Tampilkan info siswa yang login
            binding.tvSiswaInfo.text = "${SessionManager.getSiswaName(this)} - ${SessionManager.getSiswaKelas(this)}"
            binding.layoutSiswaInfo.visibility = View.VISIBLE
        } else {
            // Belum login - tampilkan pilihan metode peminjaman
            binding.layoutMetodePeminjaman.visibility = View.VISIBLE
            binding.layoutPilihBuku.visibility = View.GONE
            binding.layoutSiswaInfo.visibility = View.GONE
        }
    }
    
    private fun setupCardButtons() {
        // Metode Peminjaman - QR Scanner
        binding.cardQRScan.setOnClickListener {
            val intent = Intent(this, QRScannerActivity::class.java)
            startActivity(intent)
        }
        
        // Metode Peminjaman - Manual Input
        binding.cardManualInput.setOnClickListener {
            val intent = Intent(this, ManualStudentInputActivity::class.java)
            startActivity(intent)
        }
        
        // Pilih Buku - Harian
        binding.cardHarian.setOnClickListener {
            val intent = Intent(this, BukuKategoriActivity::class.java)
            intent.putExtra("KATEGORI", "harian")
            startActivity(intent)
        }
        
        // Pilih Buku - Tahunan
        binding.cardTahunan.setOnClickListener {
            val intent = Intent(this, BukuKategoriActivity::class.java)
            intent.putExtra("KATEGORI", "tahunan")
            startActivity(intent)
        }
        
        // Tombol logout siswa
        binding.btnLogoutSiswa.setOnClickListener {
            SessionManager.clearSiswaSession(this)
            CartManager.clearCart() // Clear cart saat logout
            checkSession()
        }
    }
}
