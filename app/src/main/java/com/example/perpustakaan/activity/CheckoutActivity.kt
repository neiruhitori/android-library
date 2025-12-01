package com.example.perpustakaan.activity

import android.app.DatePickerDialog
import android.app.Dialog
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.perpustakaan.R
import com.example.perpustakaan.databinding.ActivityCheckoutBinding
import com.example.perpustakaan.databinding.DialogBookCodeSelectionBinding
import com.example.perpustakaan.adapter.BookCodeAdapter
import com.example.perpustakaan.adapter.CheckoutBookAdapter
import com.example.perpustakaan.model.CartItem
import com.example.perpustakaan.model.PeminjamanRequest
import com.example.perpustakaan.network.CartManager
import com.example.perpustakaan.network.RetrofitClient
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

class CheckoutActivity : BaseActivity() {

    private lateinit var binding: ActivityCheckoutBinding
    private lateinit var checkoutAdapter: CheckoutBookAdapter
    
    private var siswaId: Int = 0
    private var siswaName: String = ""
    private var siswaKelas: String = ""
    private var siswaNisn: String = ""
    
    private var tanggalPinjam: String = ""
    private var tanggalKembali: String = ""
    
    private val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
    private val displayFormat = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCheckoutBinding.inflate(layoutInflater)
        setContentView(binding.root)

        getSiswaData()
        setupToolbar()
        setupRecyclerView()
        setupDatePickers()
        setupListeners()
        updateUI()
    }

    private fun getSiswaData() {
        siswaId = intent.getIntExtra("SISWA_ID", 0)
        siswaName = intent.getStringExtra("SISWA_NAME") ?: ""
        siswaKelas = intent.getStringExtra("SISWA_KELAS") ?: ""
        siswaNisn = intent.getStringExtra("SISWA_NISN") ?: ""
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
        checkoutAdapter = CheckoutBookAdapter { cartItem ->
            showBookCodeSelectionDialog(cartItem)
        }
        
        binding.rvCheckoutBooks.apply {
            layoutManager = LinearLayoutManager(this@CheckoutActivity)
            adapter = checkoutAdapter
        }
    }

    private fun setupDatePickers() {
        val calendar = Calendar.getInstance()
        
        // Set default tanggal pinjam = hari ini
        tanggalPinjam = dateFormat.format(calendar.time)
        binding.etTanggalPinjam.setText(displayFormat.format(calendar.time))
        
        // Set default tanggal kembali = 7 hari dari sekarang
        calendar.add(Calendar.DAY_OF_YEAR, 7)
        tanggalKembali = dateFormat.format(calendar.time)
        binding.etTanggalKembali.setText(displayFormat.format(calendar.time))

        binding.etTanggalPinjam.setOnClickListener {
            showDatePicker(true)
        }

        binding.etTanggalKembali.setOnClickListener {
            showDatePicker(false)
        }
    }

    private fun showDatePicker(isPinjam: Boolean) {
        val calendar = Calendar.getInstance()
        
        val datePickerDialog = DatePickerDialog(
            this,
            { _, year, month, dayOfMonth ->
                calendar.set(year, month, dayOfMonth)
                val dateString = dateFormat.format(calendar.time)
                val displayString = displayFormat.format(calendar.time)
                
                if (isPinjam) {
                    tanggalPinjam = dateString
                    binding.etTanggalPinjam.setText(displayString)
                } else {
                    tanggalKembali = dateString
                    binding.etTanggalKembali.setText(displayString)
                }
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        )
        
        // Set minimum date to today
        datePickerDialog.datePicker.minDate = System.currentTimeMillis()
        datePickerDialog.show()
    }

    private fun setupListeners() {
        binding.btnConfirmCheckout.setOnClickListener {
            validateAndCheckout()
        }
    }

    private fun updateUI() {
        binding.tvStudentName.text = siswaName
        binding.tvStudentInfo.text = "$siswaKelas • $siswaNisn"
        
        val cartItems = CartManager.getCartItems()
        checkoutAdapter.updateItems(cartItems)
        
        val totalBooks = cartItems.sumOf { it.quantity }
        binding.tvTotalBooks.text = totalBooks.toString()
    }

    private fun showBookCodeSelectionDialog(cartItem: CartItem) {
        val dialog = Dialog(this)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        val dialogBinding = DialogBookCodeSelectionBinding.inflate(layoutInflater)
        dialog.setContentView(dialogBinding.root)
        dialog.window?.setLayout(
            (resources.displayMetrics.widthPixels * 0.9).toInt(),
            ViewGroup.LayoutParams.WRAP_CONTENT
        )

        val bookCodeAdapter = BookCodeAdapter(cartItem.quantity)
        dialogBinding.rvBookCodes.apply {
            layoutManager = LinearLayoutManager(this@CheckoutActivity)
            adapter = bookCodeAdapter
        }

        dialogBinding.tvBookInfo.text = 
            "Pilih ${cartItem.quantity} kode buku untuk \"${cartItem.buku.judul}\""

        // Set previously selected codes
        bookCodeAdapter.setSelectedCodes(CartManager.getSelectedKodeBuku(cartItem.buku.id))

        // Load book codes from API
        lifecycleScope.launch {
            try {
                val response = RetrofitClient.apiService.getAvailableBookCodes(cartItem.buku.id)
                
                dialogBinding.progressBar.visibility = View.GONE
                
                if (response.isSuccessful && response.body() != null) {
                    val kodeBukuResponse = response.body()!!
                    
                    if (kodeBukuResponse.success && kodeBukuResponse.data.isNotEmpty()) {
                        dialogBinding.rvBookCodes.visibility = View.VISIBLE
                        dialogBinding.layoutButtons.visibility = View.VISIBLE
                        bookCodeAdapter.updateBookCodes(kodeBukuResponse.data)
                    } else {
                        dialogBinding.tvEmptyState.visibility = View.VISIBLE
                    }
                } else {
                    dialogBinding.tvEmptyState.visibility = View.VISIBLE
                    Toast.makeText(
                        this@CheckoutActivity,
                        "Gagal memuat kode buku",
                        Toast.LENGTH_SHORT
                    ).show()
                }
                
            } catch (e: Exception) {
                dialogBinding.progressBar.visibility = View.GONE
                dialogBinding.tvEmptyState.visibility = View.VISIBLE
                Toast.makeText(
                    this@CheckoutActivity,
                    "Error: ${e.message}",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }

        dialogBinding.btnCancel.setOnClickListener {
            dialog.dismiss()
        }

        dialogBinding.btnConfirm.setOnClickListener {
            val selectedCodes = bookCodeAdapter.getSelectedCodes()
            if (selectedCodes.size == cartItem.quantity) {
                CartManager.setSelectedKodeBuku(cartItem.buku.id, selectedCodes)
                updateUI()
                dialog.dismiss()
            } else {
                Toast.makeText(
                    this,
                    "Pilih ${cartItem.quantity} kode buku",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }

        dialog.show()
    }

    private fun validateAndCheckout() {
        // Validate all books have selected codes
        val cartItems = CartManager.getCartItems()
        var allCodesSelected = true
        
        for (item in cartItems) {
            val selectedCodes = CartManager.getSelectedKodeBuku(item.buku.id)
            if (selectedCodes.size != item.quantity) {
                allCodesSelected = false
                Toast.makeText(
                    this,
                    "Pilih kode buku untuk \"${item.buku.judul}\"",
                    Toast.LENGTH_SHORT
                ).show()
                return
            }
        }

        if (!allCodesSelected) return

        // Validate dates
        if (tanggalPinjam.isEmpty() || tanggalKembali.isEmpty()) {
            Toast.makeText(this, "Pilih tanggal peminjaman", Toast.LENGTH_SHORT).show()
            return
        }

        // Process checkout
        processCheckout()
    }

    private fun processCheckout() {
        binding.progressBar.visibility = View.VISIBLE
        binding.btnConfirmCheckout.isEnabled = false

        // Collect all selected kode buku IDs
        val allKodeBukuIds = mutableListOf<Int>()
        CartManager.getCartItems().forEach { item ->
            val selectedCodes = CartManager.getSelectedKodeBuku(item.buku.id)
            allKodeBukuIds.addAll(selectedCodes.map { it.id })
        }

        val request = PeminjamanRequest(
            siswasId = siswaId,
            tanggalPinjam = tanggalPinjam,
            tanggalKembali = tanggalKembali,
            kodeBukuIds = allKodeBukuIds
        )

        lifecycleScope.launch {
            try {
                val response = RetrofitClient.apiService.createPeminjaman(request)

                binding.progressBar.visibility = View.GONE
                binding.btnConfirmCheckout.isEnabled = true

                if (response.isSuccessful && response.body() != null) {
                    val peminjamanResponse = response.body()!!

                    if (peminjamanResponse.success) {
                        Toast.makeText(
                            this@CheckoutActivity,
                            "Peminjaman berhasil!",
                            Toast.LENGTH_LONG
                        ).show()

                        // Clear cart
                        CartManager.clearCart()

                        // Navigate back to dashboard
                        finishAffinity()
                        startActivity(android.content.Intent(this@CheckoutActivity, DashboardActivity::class.java))
                    } else {
                        Toast.makeText(
                            this@CheckoutActivity,
                            peminjamanResponse.message ?: "Gagal menyimpan peminjaman",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                } else {
                    Toast.makeText(
                        this@CheckoutActivity,
                        "Error: ${response.code()}",
                        Toast.LENGTH_SHORT
                    ).show()
                }

            } catch (e: Exception) {
                binding.progressBar.visibility = View.GONE
                binding.btnConfirmCheckout.isEnabled = true
                Toast.makeText(
                    this@CheckoutActivity,
                    "Error: ${e.message}",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }
}
