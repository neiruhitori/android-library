package com.example.perpustakaan.activity

import android.content.Intent
import android.os.Bundle
import com.example.perpustakaan.databinding.ActivityDashboardBinding

class DashboardActivity : BaseActivity() {
    
    private lateinit var binding: ActivityDashboardBinding
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDashboardBinding.inflate(layoutInflater)
        setContentView(binding.root)
        
        // No action bar for custom header
        supportActionBar?.hide()
        
        setupCardButtons()
    }
    
    private fun setupCardButtons() {
        // Card Harian click listener
        binding.cardHarian.setOnClickListener {
            val intent = Intent(this, BukuKategoriActivity::class.java)
            intent.putExtra("KATEGORI", "harian")
            startActivity(intent)
        }
        
        // Card Tahunan click listener
        binding.cardTahunan.setOnClickListener {
            val intent = Intent(this, BukuKategoriActivity::class.java)
            intent.putExtra("KATEGORI", "tahunan")
            startActivity(intent)
        }
    }
}
