package com.example.perpustakaan.activity

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.Toast
import androidx.recyclerview.widget.GridLayoutManager
import com.example.perpustakaan.adapter.BukuAdapter
import com.example.perpustakaan.databinding.ActivityBukuKategoriBinding
import com.example.perpustakaan.model.Buku
import com.example.perpustakaan.network.RetrofitClient
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class BukuKategoriActivity : BaseActivity() {

    private lateinit var binding: ActivityBukuKategoriBinding
    private lateinit var bukuAdapter: BukuAdapter
    private var allBuku = listOf<Buku>()
    private var filteredBuku = listOf<Buku>()
    private var kategori: String = "harian"
    
    // Pagination
    private var currentPage = 1
    private val itemsPerPage = 12
    private var totalPages = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityBukuKategoriBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Get kategori dari intent
        kategori = intent.getStringExtra("KATEGORI") ?: "harian"
        val isPopular = intent.getBooleanExtra("IS_POPULAR", false)

        // Setup title
        val title = when {
            isPopular -> "Buku Paling Banyak Dipinjam"
            kategori == "harian" -> "Buku Harian"
            kategori == "tahunan" -> "Buku Tahunan"
            else -> "Semua Buku"
        }
        binding.tvCategoryTitle.text = title

        // Setup back button
        binding.btnBack.setOnClickListener {
            finish()
        }

        setupRecyclerView()
        setupSearchBox()
        setupPagination()
        setupCartButton()
        loadBuku(isPopular)
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

    private fun setupRecyclerView() {
        bukuAdapter = BukuAdapter(
            onItemClick = { buku ->
                // Navigate to detail
                val intent = android.content.Intent(this, DetailBukuActivity::class.java)
                intent.putExtra("BUKU_ID", buku.id)
                startActivity(intent)
            },
            showPopularBadge = false
        )

        binding.rvBuku.apply {
            layoutManager = GridLayoutManager(this@BukuKategoriActivity, 2)
            adapter = bukuAdapter
        }
    }

    private fun setupSearchBox() {
        binding.etSearch.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                filterBuku(s.toString())
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })
    }

    private fun setupPagination() {
        binding.btnPrevPage.setOnClickListener {
            if (currentPage > 1) {
                currentPage--
                updatePage()
            }
        }

        binding.btnNextPage.setOnClickListener {
            if (currentPage < totalPages) {
                currentPage++
                updatePage()
            }
        }
    }

    private fun loadBuku(isPopular: Boolean) {
        binding.progressBar.visibility = View.VISIBLE
        binding.rvBuku.visibility = View.GONE

        CoroutineScope(Dispatchers.IO).launch {
            try {
                android.util.Log.d("BukuKategori", "Starting API call to getAllBuku()")
                val response = RetrofitClient.apiService.getAllBuku()
                
                android.util.Log.d("BukuKategori", "Response code: ${response.code()}")
                android.util.Log.d("BukuKategori", "Response message: ${response.message()}")
                
                withContext(Dispatchers.Main) {
                    binding.progressBar.visibility = View.GONE
                    
                    if (response.isSuccessful && response.body()?.success == true) {
                        val data = response.body()?.data ?: emptyList()
                        
                        android.util.Log.d("BukuKategori", "Data received: ${data.size} books")
                        
                        // Filter berdasarkan kategori atau popular
                        allBuku = if (isPopular) {
                            data.sortedByDescending { it.totalPeminjaman }
                        } else {
                            data.filter { it.tipe == kategori }
                        }
                        
                        filteredBuku = allBuku
                        updatePage()
                        
                        if (allBuku.isEmpty()) {
                            showEmptyState(true)
                        }
                    } else {
                        val errorBody = response.errorBody()?.string()
                        android.util.Log.e("BukuKategori", "API Error: $errorBody")
                        showError("Gagal memuat data: ${response.message()}")
                    }
                }
                
            } catch (e: java.net.UnknownHostException) {
                android.util.Log.e("BukuKategori", "UnknownHostException: ${e.message}", e)
                withContext(Dispatchers.Main) {
                    binding.progressBar.visibility = View.GONE
                    showError("❌ Tidak dapat terhubung ke server.\n\nPeriksa:\n• Koneksi internet\n• Domain dapat diakses\n• VPN aktif (jika diperlukan)")
                }
            } catch (e: java.net.ConnectException) {
                android.util.Log.e("BukuKategori", "ConnectException: ${e.message}", e)
                withContext(Dispatchers.Main) {
                    binding.progressBar.visibility = View.GONE
                    showError("❌ Koneksi ditolak.\n\nServer mungkin down atau alamat salah.")
                }
            } catch (e: javax.net.ssl.SSLHandshakeException) {
                android.util.Log.e("BukuKategori", "SSLHandshakeException: ${e.message}", e)
                withContext(Dispatchers.Main) {
                    binding.progressBar.visibility = View.GONE
                    showError("❌ SSL Error.\n\nSertifikat SSL bermasalah.")
                }
            } catch (e: java.net.SocketTimeoutException) {
                android.util.Log.e("BukuKategori", "SocketTimeoutException: ${e.message}", e)
                withContext(Dispatchers.Main) {
                    binding.progressBar.visibility = View.GONE
                    showError("❌ Timeout.\n\nServer terlalu lama merespons.")
                }
            } catch (e: Exception) {
                android.util.Log.e("BukuKategori", "Exception: ${e.message}", e)
                e.printStackTrace()
                withContext(Dispatchers.Main) {
                    binding.progressBar.visibility = View.GONE
                    showError("❌ ERROR: Failed to fetch\n\n${e.javaClass.simpleName}:\n${e.message}\n\nCek Logcat untuk detail.")
                }
            }
        }
    }

    private fun filterBuku(query: String) {
        filteredBuku = if (query.isEmpty()) {
            allBuku
        } else {
            allBuku.filter {
                it.judul.contains(query, ignoreCase = true) ||
                        it.penulis.contains(query, ignoreCase = true)
            }
        }
        
        currentPage = 1
        updatePage()
    }

    private fun updatePage() {
        totalPages = (filteredBuku.size + itemsPerPage - 1) / itemsPerPage
        
        if (totalPages == 0) totalPages = 1

        val startIndex = (currentPage - 1) * itemsPerPage
        val endIndex = minOf(startIndex + itemsPerPage, filteredBuku.size)
        
        val pageData = if (filteredBuku.isNotEmpty()) {
            filteredBuku.subList(startIndex, endIndex)
        } else {
            emptyList()
        }

        bukuAdapter.updateData(pageData)
        
        // Update UI
        binding.rvBuku.visibility = if (pageData.isNotEmpty()) View.VISIBLE else View.GONE
        binding.tvInfoHeader.text = "Menampilkan ${pageData.size} dari ${filteredBuku.size} buku"
        binding.tvPageInfo.text = "Halaman $currentPage dari $totalPages"
        
        // Update pagination buttons
        binding.btnPrevPage.isEnabled = currentPage > 1
        binding.btnNextPage.isEnabled = currentPage < totalPages
        
        // Show/hide pagination
        binding.paginationLayout.visibility = if (totalPages > 1) View.VISIBLE else View.GONE
        
        // Show empty state if no data
        showEmptyState(pageData.isEmpty())
        
        // Scroll to top
        binding.scrollView.smoothScrollTo(0, 0)
    }

    private fun showEmptyState(show: Boolean) {
        binding.emptyStateLayout.visibility = if (show) View.VISIBLE else View.GONE
        binding.rvBuku.visibility = if (show) View.GONE else View.VISIBLE
    }

    private fun showError(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
}
