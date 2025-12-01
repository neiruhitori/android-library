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
                // Navigate to student selection (QR or manual)
                val intent = Intent(this, StudentSelectionActivity::class.java)
                startActivity(intent)
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
