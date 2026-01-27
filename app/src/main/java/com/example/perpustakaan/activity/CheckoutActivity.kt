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
    private var activeDialog: Dialog? = null // Track active dialog
    
    private var siswaId: Int = 0
    private var siswaName: String = ""
    private var siswaKelas: String = ""
    private var siswaNisn: String = ""
    private var siswaAbsen: String = ""
    private var siswaJenisKelamin: String = ""
    private var siswaAgama: String = ""
    
    private var tanggalPinjam: String = ""
    private var tanggalKembali: String = ""
    private var bookCategory: String = "" // harian atau tahunan
    
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
        fetchSiswaFullData()
        updateUI()
    }
    
    override fun onDestroy() {
        super.onDestroy()
        // Clear dialog reference saat activity destroyed
        try {
            activeDialog?.dismiss()
        } catch (e: Exception) {
            android.util.Log.e("CheckoutActivity", "Error dismissing dialog", e)
        }
        activeDialog = null
    }

    private fun getSiswaData() {
        siswaId = intent.getIntExtra("SISWA_ID", 0)
    }
    
    private fun fetchSiswaFullData() {
        lifecycleScope.launch {
            try {
                val response = RetrofitClient.apiService.getSiswaById(siswaId)
                
                android.util.Log.d("CheckoutActivity", "API Response: ${response.code()}")
                
                if (response.isSuccessful && response.body() != null) {
                    val siswaResponse = response.body()!!
                    
                    android.util.Log.d("CheckoutActivity", "Siswa Response: $siswaResponse")
                    
                    if (siswaResponse.success) {
                        val siswa = siswaResponse.data
                        siswaName = siswa.name
                        siswaKelas = siswa.kelas
                        siswaNisn = siswa.nisn
                        siswaAbsen = siswa.absen ?: "-"
                        siswaJenisKelamin = when(siswa.jenisKelamin) {
                            "L" -> "Laki-laki"
                            "P" -> "Perempuan"
                            else -> "-"
                        }
                        siswaAgama = siswa.agama ?: "-"
                        
                        android.util.Log.d("CheckoutActivity", "Siswa data: absen=$siswaAbsen, jk=$siswaJenisKelamin, agama=$siswaAgama")
                        
                        updateStudentInfo()
                    }
                }
            } catch (e: Exception) {
                android.util.Log.e("CheckoutActivity", "Error fetching siswa data", e)
                // Silent fail, use default data
            }
        }
    }
    
    private fun updateStudentInfo() {
        binding.tvStudentName.text = siswaName
        binding.tvStudentInfo.text = "$siswaKelas • NISN: $siswaNisn"
        binding.tvStudentAbsen.text = "Absen: $siswaAbsen"
        binding.tvStudentJenisKelamin.text = siswaJenisKelamin
        binding.tvStudentAgama.text = "Agama: $siswaAgama"
    }
    
    override fun onPause() {
        super.onPause()
        // Dismiss dialog saat activity pause untuk mencegah crash
        activeDialog?.dismiss()
        activeDialog = null
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
        // Deteksi kategori buku dari cart (ambil dari item pertama)
        val cartItems = CartManager.getCartItems()
        if (cartItems.isNotEmpty()) {
            bookCategory = cartItems.first().buku.tipe
        }
        
        val calendar = Calendar.getInstance()
        
        // Set default tanggal pinjam = hari ini (untuk semua kategori)
        tanggalPinjam = dateFormat.format(calendar.time)
        binding.etTanggalPinjam.setText(displayFormat.format(calendar.time))
        
        // Set tanggal kembali berdasarkan kategori
        if (bookCategory == "harian") {
            // Harian: Otomatis 1 minggu dari sekarang
            calendar.add(Calendar.DAY_OF_YEAR, 7)
            tanggalKembali = dateFormat.format(calendar.time)
            binding.etTanggalKembali.setText(displayFormat.format(calendar.time))
        } else {
            // Tahunan: Kosong, user harus input sendiri (hint sudah di layout XML)
            tanggalKembali = ""
            binding.etTanggalKembali.setText("")
        }

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
        val cartItems = CartManager.getCartItems()
        checkoutAdapter.updateItems(cartItems)
        
        val totalBooks = cartItems.sumOf { it.quantity }
        binding.tvTotalBooks.text = totalBooks.toString()
    }

    private fun showBookCodeSelectionDialog(cartItem: CartItem) {
        // Check if activity is finishing or destroyed
        if (isFinishing || isDestroyed) {
            return
        }
        
        // Dismiss previous dialog jika masih ada
        activeDialog?.dismiss()
        
        val dialog = Dialog(this)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        val dialogBinding = DialogBookCodeSelectionBinding.inflate(layoutInflater)
        dialog.setContentView(dialogBinding.root)
        
        // Set dialog size
        val displayMetrics = resources.displayMetrics
        val dialogWidth = (displayMetrics.widthPixels * 0.9).toInt()
        
        dialog.window?.setLayout(
            dialogWidth,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        
        // Prevent dialog from being dismissed by back button atau touch outside
        dialog.setCancelable(false)
        dialog.setCanceledOnTouchOutside(false)
        
        // Store dialog reference
        activeDialog = dialog

        val bookCodeAdapter = BookCodeAdapter(cartItem.quantity)
        dialogBinding.rvBookCodes.apply {
            layoutManager = LinearLayoutManager(this@CheckoutActivity)
            adapter = bookCodeAdapter
        }

        dialogBinding.tvBookInfo.text = 
            "Pilih ${cartItem.quantity} kode buku untuk \"${cartItem.buku.judul}\""

        // Set previously selected codes
        bookCodeAdapter.setSelectedCodes(CartManager.getSelectedKodeBuku(cartItem.buku.id))

        // Setup search functionality
        dialogBinding.etSearchCode.addTextChangedListener(object : android.text.TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: android.text.Editable?) {
                bookCodeAdapter.filter(s.toString())
            }
        })

        // Load book codes from API
        lifecycleScope.launch {
            try {
                val response = RetrofitClient.apiService.getAvailableBookCodes(cartItem.buku.id)
                
                dialogBinding.progressBar.visibility = View.GONE
                
                if (response.isSuccessful && response.body() != null) {
                    val kodeBukuResponse = response.body()!!
                    
                    if (kodeBukuResponse.success && kodeBukuResponse.data.isNotEmpty()) {
                        android.util.Log.d("CheckoutActivity", "Setting RecyclerView and buttons VISIBLE for buku ${cartItem.buku.id}, codes count: ${kodeBukuResponse.data.size}")
                        dialogBinding.searchLayout.visibility = View.VISIBLE
                        dialogBinding.rvBookCodes.visibility = View.VISIBLE
                        dialogBinding.layoutButtons.visibility = View.VISIBLE
                        bookCodeAdapter.updateBookCodes(kodeBukuResponse.data)
                        
                        // Force layout refresh
                        dialogBinding.root.post {
                            android.util.Log.d("CheckoutActivity", "Layout buttons visibility after post: ${dialogBinding.layoutButtons.visibility}")
                        }
                    } else {
                        dialogBinding.tvEmptyState.visibility = View.VISIBLE
                        dialogBinding.tvEmptyState.text = "Tidak ada kode buku tersedia untuk buku ini"
                    }
                } else {
                    dialogBinding.tvEmptyState.visibility = View.VISIBLE
                    dialogBinding.tvEmptyState.text = "Gagal memuat kode buku (Error: ${response.code()})"
                    Toast.makeText(
                        this@CheckoutActivity,
                        "Error ${response.code()}: ${response.message()}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
                
            } catch (e: Exception) {
                dialogBinding.progressBar.visibility = View.GONE
                dialogBinding.tvEmptyState.visibility = View.VISIBLE
                dialogBinding.tvEmptyState.text = "Error: ${e.message}"
                Toast.makeText(
                    this@CheckoutActivity,
                    "Error: ${e.message}",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }

        dialogBinding.btnCancel.setOnClickListener {
            dialog.dismiss()
            activeDialog = null
        }

        dialogBinding.btnConfirm.setOnClickListener {
            try {
                val selectedCodes = bookCodeAdapter.getSelectedCodes()
                android.util.Log.d("CheckoutActivity", "Selected codes: ${selectedCodes.size} out of ${cartItem.quantity}")
                
                if (selectedCodes.size == cartItem.quantity) {
                    CartManager.setSelectedKodeBuku(cartItem.buku.id, selectedCodes)
                    updateUI()
                    dialog.dismiss()
                    activeDialog = null
                    
                    Toast.makeText(
                        this,
                        "${selectedCodes.size} kode buku dipilih",
                        Toast.LENGTH_SHORT
                    ).show()
                } else {
                    Toast.makeText(
                        this,
                        "Pilih ${cartItem.quantity} kode buku (saat ini: ${selectedCodes.size})",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            } catch (e: Exception) {
                android.util.Log.e("CheckoutActivity", "Error confirming codes", e)
                Toast.makeText(
                    this,
                    "Error: ${e.message}",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
        
        // Listener untuk handle jika dialog di-dismiss dari luar
        dialog.setOnDismissListener {
            activeDialog = null
        }

        // Show dialog hanya jika activity masih valid
        if (!isFinishing && !isDestroyed) {
            try {
                dialog.show()
            } catch (e: Exception) {
                android.util.Log.e("CheckoutActivity", "Error showing dialog", e)
            }
        }
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
                        
                        // Clear session untuk logout siswa
                        com.example.perpustakaan.util.SessionManager.clearSiswaSession(this@CheckoutActivity)

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
