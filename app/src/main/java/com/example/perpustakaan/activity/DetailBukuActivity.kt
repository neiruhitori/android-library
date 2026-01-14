package com.example.perpustakaan.activity

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.example.perpustakaan.R
import com.example.perpustakaan.databinding.ActivityDetailBukuBinding
import com.example.perpustakaan.network.RetrofitClient
import kotlinx.coroutines.launch

class DetailBukuActivity : BaseActivity() {
    
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
                        binding.tvBukuTahun.text = buku.tahunTerbit ?: "-"
                        binding.tvBukuTipe.text = buku.tipe.replaceFirstChar { it.uppercase() }
                        binding.tvBukuDescription.text = buku.description
                        
                        // Set Kelas untuk buku tahunan
                        if (buku.tipe == "tahunan" && !buku.kelas.isNullOrEmpty()) {
                            binding.tvBukuKelas.text = "Kelas ${buku.kelas}"
                            binding.layoutKelas.visibility = View.VISIBLE
                            binding.dividerKelas.visibility = View.VISIBLE
                        } else {
                            binding.layoutKelas.visibility = View.GONE
                            binding.dividerKelas.visibility = View.GONE
                        }
                        
                        // Set ISBN if available
                        if (!buku.isbn.isNullOrEmpty()) {
                            binding.tvBukuIsbn.text = buku.isbn
                            binding.layoutIsbn.visibility = View.VISIBLE
                            binding.dividerIsbn.visibility = View.VISIBLE
                        } else {
                            binding.layoutIsbn.visibility = View.GONE
                            binding.dividerIsbn.visibility = View.GONE
                        }
                        
                        // Set Kota Cetak if available
                        if (!buku.kotaCetak.isNullOrEmpty()) {
                            binding.tvBukuKotaCetak.text = buku.kotaCetak
                            binding.layoutKotaCetak.visibility = View.VISIBLE
                            binding.dividerKotaCetak.visibility = View.VISIBLE
                        } else {
                            binding.layoutKotaCetak.visibility = View.GONE
                            binding.dividerKotaCetak.visibility = View.GONE
                        }
                        
                        // Set stock information
                        binding.tvTotalStok.text = "${buku.stok} buku"
                        binding.tvSedangDipinjam.text = "${buku.sedangDipinjam} buku"
                        
                        // Set stock status badge
                        when {
                            buku.stokTersedia <= 0 -> {
                                binding.tvStokStatus.text = "Stok Habis"
                                binding.tvStokStatus.setTextColor(
                                    ContextCompat.getColor(this@DetailBukuActivity, android.R.color.white)
                                )
                                binding.layoutStokStatus.setBackgroundResource(R.drawable.bg_badge_unavailable)
                            }
                            else -> {
                                binding.tvStokStatus.text = "${buku.stokTersedia} Buku Tersisa"
                                binding.tvStokStatus.setTextColor(
                                    ContextCompat.getColor(this@DetailBukuActivity, android.R.color.white)
                                )
                                binding.layoutStokStatus.setBackgroundResource(R.drawable.bg_badge_limited)
                            }
                        }
                        
                        // Set total peminjaman
                        binding.tvTotalPeminjaman.text = "Total dipinjam: ${buku.totalPeminjaman} kali"
                        
                        // Show/hide statistics card
                        binding.cvStatistics.visibility = if (buku.totalPeminjaman > 0) {
                            View.VISIBLE
                        } else {
                            View.GONE
                        }
                        
                        // Load image
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
