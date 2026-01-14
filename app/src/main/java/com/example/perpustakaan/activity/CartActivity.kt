package com.example.perpustakaan.activity

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.perpustakaan.databinding.ActivityCartBinding
import com.example.perpustakaan.adapter.CartItemAdapter
import com.example.perpustakaan.network.CartManager

class CartActivity : BaseActivity(), CartManager.CartUpdateListener {

    private lateinit var binding: ActivityCartBinding
    private lateinit var cartAdapter: CartItemAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCartBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupToolbar()
        setupRecyclerView()
        setupListeners()
        
        CartManager.addListener(this)
        updateUI()
    }

    override fun onDestroy() {
        super.onDestroy()
        CartManager.removeListener(this)
    }

    private fun setupToolbar() {
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)
        binding.toolbar.setNavigationOnClickListener {
            finish()
        }
    }

    private fun setupRecyclerView() {
        cartAdapter = CartItemAdapter()
        binding.rvCartItems.apply {
            layoutManager = LinearLayoutManager(this@CartActivity)
            adapter = cartAdapter
        }
    }

    private fun setupListeners() {
        binding.btnCheckout.setOnClickListener {
            if (CartManager.getTotalItems() > 0) {
                // Cek apakah sudah ada siswa login di session
                if (com.example.perpustakaan.util.SessionManager.hasSiswaSession(this)) {
                    // Langsung ke checkout dengan data siswa dari session
                    val intent = Intent(this, CheckoutActivity::class.java)
                    intent.putExtra("SISWA_ID", com.example.perpustakaan.util.SessionManager.getSiswaId(this))
                    intent.putExtra("SISWA_NAME", com.example.perpustakaan.util.SessionManager.getSiswaName(this))
                    intent.putExtra("SISWA_KELAS", com.example.perpustakaan.util.SessionManager.getSiswaKelas(this))
                    intent.putExtra("SISWA_NISN", com.example.perpustakaan.util.SessionManager.getSiswaNisn(this))
                    startActivity(intent)
                } else {
                    // Jika belum login, kembali ke dashboard untuk pilih metode
                    android.widget.Toast.makeText(
                        this,
                        "Silakan login dengan QR Code atau input manual terlebih dahulu",
                        android.widget.Toast.LENGTH_SHORT
                    ).show()
                    
                    val intent = Intent(this, DashboardActivity::class.java)
                    startActivity(intent)
                }
            }
        }

        binding.btnBrowseBooks.setOnClickListener {
            finish() // Return to book list
        }
    }

    private fun updateUI() {
        val cartItems = CartManager.getCartItems()
        val totalItems = CartManager.getTotalItems()
        val totalBooks = CartManager.getTotalBooksCount()

        if (cartItems.isEmpty()) {
            binding.layoutEmptyState.visibility = View.VISIBLE
            binding.layoutCartContent.visibility = View.GONE
        } else {
            binding.layoutEmptyState.visibility = View.GONE
            binding.layoutCartContent.visibility = View.VISIBLE
            
            cartAdapter.updateItems(cartItems)
            binding.tvTotalBooks.text = totalBooks.toString()
            binding.tvTotalItems.text = totalItems.toString()
        }
    }

    override fun onCartUpdated() {
        updateUI()
    }
}
