package com.example.perpustakaan.activity

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.perpustakaan.adapter.BukuAdapter
import com.example.perpustakaan.databinding.ActivityDashboardBinding
import com.example.perpustakaan.model.Buku
import com.example.perpustakaan.network.RetrofitClient
import kotlinx.coroutines.launch

class DashboardActivity : AppCompatActivity() {
    
    private lateinit var binding: ActivityDashboardBinding
    private lateinit var bukuAdapter: BukuAdapter
    private var bukuList = listOf<Buku>()
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDashboardBinding.inflate(layoutInflater)
        setContentView(binding.root)
        
        // No action bar for custom header
        supportActionBar?.hide()
        
        setupRecyclerView()
        setupSearchView()
        loadBuku()
    }
    
    private fun setupRecyclerView() {
        bukuAdapter = BukuAdapter { buku ->
            navigateToDetail(buku)
        }
        
        binding.rvBuku.apply {
            layoutManager = LinearLayoutManager(this@DashboardActivity)
            adapter = bukuAdapter
        }
    }
    
    private fun setupSearchView() {
        binding.etSearch.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                filterBuku(s.toString())
            }
            
            override fun afterTextChanged(s: Editable?) {}
        })
    }
    
    private fun loadBuku() {
        showLoading(true)
        
        lifecycleScope.launch {
            try {
                val response = RetrofitClient.apiService.getAllBuku()
                
                if (response.isSuccessful && response.body() != null) {
                    val bukuResponse = response.body()!!
                    
                    if (bukuResponse.success) {
                        bukuList = bukuResponse.data
                        bukuAdapter.submitList(bukuList)
                        showEmptyState(bukuList.isEmpty())
                    } else {
                        showToast(bukuResponse.message ?: "Gagal memuat data")
                        showEmptyState(true)
                    }
                } else {
                    showToast("Error: ${response.code()}")
                    showEmptyState(true)
                }
                
            } catch (e: Exception) {
                showToast("Error: ${e.message}")
                showEmptyState(true)
            } finally {
                showLoading(false)
            }
        }
    }
    
    private fun filterBuku(query: String) {
        val filteredList = if (query.isEmpty()) {
            bukuList
        } else {
            bukuList.filter { buku ->
                buku.judul.contains(query, ignoreCase = true) ||
                buku.penulis.contains(query, ignoreCase = true) ||
                buku.description.contains(query, ignoreCase = true)
            }
        }
        
        bukuAdapter.submitList(filteredList)
        showEmptyState(filteredList.isEmpty())
    }
    
    private fun navigateToDetail(buku: Buku) {
        val intent = Intent(this, DetailBukuActivity::class.java)
        intent.putExtra("BUKU_ID", buku.id)
        startActivity(intent)
    }
    
    private fun showLoading(isLoading: Boolean) {
        binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
        binding.rvBuku.visibility = if (isLoading) View.GONE else View.VISIBLE
    }
    
    private fun showEmptyState(isEmpty: Boolean) {
        binding.emptyStateLayout.visibility = if (isEmpty) View.VISIBLE else View.GONE
        binding.rvBuku.visibility = if (isEmpty) View.GONE else View.VISIBLE
    }
    
    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
}
