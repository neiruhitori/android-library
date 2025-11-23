package com.example.perpustakaan.activity

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.example.perpustakaan.R
import com.example.perpustakaan.databinding.ActivityDetailBukuBinding
import com.example.perpustakaan.network.RetrofitClient
import kotlinx.coroutines.launch

class DetailBukuActivity : AppCompatActivity() {
    
    private lateinit var binding: ActivityDetailBukuBinding
    private var bukuId: Int = 0
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailBukuBinding.inflate(layoutInflater)
        setContentView(binding.root)
        
        // Hide action bar
        supportActionBar?.hide()
        
        // Get buku ID from intent
        bukuId = intent.getIntExtra("BUKU_ID", 0)
        
        setupViews()
        loadDetailBuku()
    }
    
    private fun setupViews() {
        binding.btnBack.setOnClickListener {
            finish()
        }
    }
    
    private fun loadDetailBuku() {
        if (bukuId == 0) {
            showToast("ID Buku tidak valid")
            finish()
            return
        }
        
        lifecycleScope.launch {
            try {
                val response = RetrofitClient.apiService.getBukuById(bukuId)
                
                if (response.isSuccessful && response.body() != null) {
                    val bukuResponse = response.body()!!
                    
                    if (bukuResponse.success) {
                        val buku = bukuResponse.data
                        
                        // Set data to views
                        binding.tvBukuJudul.text = buku.judul
                        binding.tvBukuPenulis.text = buku.penulis
                        binding.tvBukuTahun.text = buku.tahunTerbit.toString()
                        binding.tvBukuTipe.text = buku.tipe.capitalize()
                        binding.tvBukuDescription.text = buku.description
                        
                        // Load image - gunakan foto_url dari API
                        val imageUrl = buku.fotoUrl ?: buku.foto
                        Glide.with(this@DetailBukuActivity)
                            .load(imageUrl)
                            .placeholder(R.drawable.ic_book_placeholder)
                            .error(R.drawable.ic_book_placeholder)
                            .into(binding.ivBukuCover)
                            
                    } else {
                        showToast(bukuResponse.message ?: "Gagal memuat detail buku")
                        finish()
                    }
                } else {
                    showToast("Error: ${response.code()}")
                    finish()
                }
                
            } catch (e: Exception) {
                showToast("Error: ${e.message}")
                finish()
            }
        }
    }
    
    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
}
