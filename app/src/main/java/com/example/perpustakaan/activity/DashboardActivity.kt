package com.example.perpustakaan.activity

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.perpustakaan.adapter.BukuAdapter
import com.example.perpustakaan.adapter.Category
import com.example.perpustakaan.adapter.CategoryAdapter
import com.example.perpustakaan.databinding.ActivityDashboardBinding
import com.example.perpustakaan.model.Buku
import com.example.perpustakaan.network.RetrofitClient
import kotlinx.coroutines.launch

class DashboardActivity : BaseActivity() {
    
    private lateinit var binding: ActivityDashboardBinding
    private lateinit var bukuHarianAdapter: BukuAdapter
    private lateinit var bukuTahunanAdapter: BukuAdapter
    private lateinit var bukuPopulerAdapter: BukuAdapter
    private lateinit var categoryAdapter: CategoryAdapter
    
    private var allBukuList = listOf<Buku>()
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDashboardBinding.inflate(layoutInflater)
        setContentView(binding.root)
        
        // No action bar for custom header
        supportActionBar?.hide()
        
        setupRecyclerViews()
        setupSearchView()
        setupSwipeRefresh()
        setupCartButton()
        loadBuku()
    }
    
    private fun setupSwipeRefresh() {
        binding.swipeRefreshLayout.setColorSchemeColors(
            resources.getColor(android.R.color.holo_blue_bright, null),
            resources.getColor(android.R.color.holo_green_light, null),
            resources.getColor(android.R.color.holo_orange_light, null),
            resources.getColor(android.R.color.holo_red_light, null)
        )
        
        binding.swipeRefreshLayout.setOnRefreshListener {
            loadBuku()
        }
    }
    
    private fun setupRecyclerViews() {
        // Setup Buku Harian dengan Grid Layout (2 columns)
        bukuHarianAdapter = BukuAdapter(
            onItemClick = { buku -> navigateToDetail(buku) },
            showPopularBadge = false
        )
        binding.rvBukuHarian.apply {
            layoutManager = androidx.recyclerview.widget.GridLayoutManager(this@DashboardActivity, 2)
            adapter = bukuHarianAdapter
        }
        
        // Setup Buku Tahunan dengan Grid Layout (2 columns)
        bukuTahunanAdapter = BukuAdapter(
            onItemClick = { buku -> navigateToDetail(buku) },
            showPopularBadge = false
        )
        binding.rvBukuTahunan.apply {
            layoutManager = androidx.recyclerview.widget.GridLayoutManager(this@DashboardActivity, 2)
            adapter = bukuTahunanAdapter
        }
        
        // Setup Buku Populer dengan Grid Layout (2 columns)
        bukuPopulerAdapter = BukuAdapter(
            onItemClick = { buku -> navigateToDetail(buku) },
            showPopularBadge = true
        )
        binding.rvBukuPopuler.apply {
            layoutManager = androidx.recyclerview.widget.GridLayoutManager(this@DashboardActivity, 2)
            adapter = bukuPopulerAdapter
        }
        
        // Setup Categories (horizontal)
        binding.rvCategories.apply {
            layoutManager = LinearLayoutManager(
                this@DashboardActivity,
                LinearLayoutManager.HORIZONTAL,
                false
            )
        }
        
        // Setup button Lihat Semua
        binding.btnLihatSemuaHarian.setOnClickListener {
            val intent = Intent(this@DashboardActivity, BukuKategoriActivity::class.java)
            intent.putExtra("KATEGORI", "harian")
            startActivity(intent)
        }
        
        binding.btnLihatSemuaTahunan.setOnClickListener {
            val intent = Intent(this@DashboardActivity, BukuKategoriActivity::class.java)
            intent.putExtra("KATEGORI", "tahunan")
            startActivity(intent)
        }
        
        binding.btnLihatSemuaPopuler.setOnClickListener {
            val intent = Intent(this@DashboardActivity, BukuKategoriActivity::class.java)
            intent.putExtra("IS_POPULAR", true)
            startActivity(intent)
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
                        allBukuList = bukuResponse.data
                        displayBukuByCategory(allBukuList)
                        showEmptyState(allBukuList.isEmpty())
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
                binding.swipeRefreshLayout.isRefreshing = false
            }
        }
    }
    
    private fun displayBukuByCategory(bukuList: List<Buku>) {
        // Filter buku harian
        val bukuHarian = bukuList.filter { it.tipe.equals("harian", ignoreCase = true) }
        bukuHarianAdapter.submitList(bukuHarian)
        
        // Filter buku tahunan
        val bukuTahunan = bukuList.filter { it.tipe.equals("tahunan", ignoreCase = true) }
        bukuTahunanAdapter.submitList(bukuTahunan)
        
        // Filter buku populer (sort by total peminjaman)
        val bukuPopuler = bukuList.sortedByDescending { it.totalPeminjaman }.take(10)
        bukuPopulerAdapter.submitList(bukuPopuler)
        
        // Setup categories
        val categories = listOf(
            Category("Buku Harian", bukuHarian.size, "harian"),
            Category("Buku Tahunan", bukuTahunan.size, "tahunan"),
            Category("Populer", bukuPopuler.size, "populer")
        )
        
        categoryAdapter = CategoryAdapter(categories) { category ->
            // Scroll to specific section
            when (category.type) {
                "harian" -> scrollToSection(binding.sectionBukuHarian)
                "tahunan" -> scrollToSection(binding.sectionBukuTahunan)
                "populer" -> scrollToSection(binding.sectionBukuPopuler)
            }
        }
        binding.rvCategories.adapter = categoryAdapter
        
        // Show/hide sections based on data
        binding.sectionBukuHarian.visibility = if (bukuHarian.isEmpty()) View.GONE else View.VISIBLE
        binding.sectionBukuTahunan.visibility = if (bukuTahunan.isEmpty()) View.GONE else View.VISIBLE
        binding.sectionBukuPopuler.visibility = if (bukuPopuler.isEmpty()) View.GONE else View.VISIBLE
    }
    
    private fun scrollToSection(view: View) {
        view.parent.requestChildFocus(view, view)
    }
    
    private fun filterBuku(query: String) {
        if (query.isEmpty()) {
            displayBukuByCategory(allBukuList)
            return
        }
        
        val filteredList = allBukuList.filter { buku ->
            buku.judul.contains(query, ignoreCase = true) ||
            buku.penulis.contains(query, ignoreCase = true) ||
            buku.description.contains(query, ignoreCase = true)
        }
        
        displayBukuByCategory(filteredList)
        showEmptyState(filteredList.isEmpty())
    }
    
    private fun navigateToDetail(buku: Buku) {
        val intent = Intent(this, DetailBukuActivity::class.java)
        intent.putExtra("BUKU_ID", buku.id)
        startActivity(intent)
    }
    
    private fun showLoading(isLoading: Boolean) {
        binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
    }
    
    private fun showEmptyState(isEmpty: Boolean) {
        binding.emptyStateLayout.visibility = if (isEmpty) View.VISIBLE else View.GONE
    }
    
    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
    
    private fun setupCartButton() {
        // Set click listener untuk FAB cart
        binding.fabCart.setOnClickListener {
            val intent = Intent(this, CartActivity::class.java)
            startActivity(intent)
        }
        
        // Update cart badge
        updateCartBadge()
    }
    
    override fun onResume() {
        super.onResume()
        // Update cart badge setiap kali activity resume
        updateCartBadge()
    }
    
    private fun updateCartBadge() {
        val totalItems = com.example.perpustakaan.network.CartManager.getTotalItems()
        if (totalItems > 0) {
            binding.tvCartBadge.visibility = View.VISIBLE
            binding.tvCartBadge.text = if (totalItems > 99) "99+" else totalItems.toString()
        } else {
            binding.tvCartBadge.visibility = View.GONE
        }
    }
}
