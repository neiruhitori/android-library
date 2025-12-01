package com.example.perpustakaan.activity

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import com.example.perpustakaan.databinding.ActivityManualStudentInputBinding
import com.example.perpustakaan.model.Siswa
import com.example.perpustakaan.network.RetrofitClient
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class ManualStudentInputActivity : BaseActivity() {

    private lateinit var binding: ActivityManualStudentInputBinding
    private var allStudents = listOf<Siswa>()
    private var selectedSiswa: Siswa? = null
    private var searchJob: Job? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityManualStudentInputBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupToolbar()
        setupAutoComplete()
        setupListeners()
    }

    private fun setupToolbar() {
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)
        binding.toolbar.setNavigationOnClickListener {
            finish()
        }
    }

    private fun setupAutoComplete() {
        // Setup AutoCompleteTextView dengan live search
        binding.etSearch.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            
            override fun afterTextChanged(s: Editable?) {
                val query = s.toString().trim()
                
                // Cancel previous search job
                searchJob?.cancel()
                
                if (query.length >= 1) {
                    // Debounce search - tunggu 300ms setelah user berhenti mengetik
                    searchJob = lifecycleScope.launch {
                        delay(300)
                        searchStudents(query)
                    }
                }
            }
        })

        // Handle item selection dari dropdown
        binding.etSearch.setOnItemClickListener { parent, _, position, _ ->
            val selectedName = parent.getItemAtPosition(position).toString()
            val siswa = allStudents.find { it.name == selectedName }
            
            if (siswa != null) {
                selectedSiswa = siswa
                showSelectedStudent(siswa)
            } else {
                Toast.makeText(this, "Siswa tidak ditemukan", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun setupListeners() {
        binding.btnProceed.setOnClickListener {
            selectedSiswa?.let { siswa ->
                val intent = Intent(this, CheckoutActivity::class.java)
                intent.putExtra("SISWA_ID", siswa.id)
                intent.putExtra("SISWA_NAME", siswa.name)
                intent.putExtra("SISWA_KELAS", siswa.kelas)
                intent.putExtra("SISWA_NISN", siswa.nisn)
                startActivity(intent)
                finish()
            }
        }
    }

    private fun searchStudents(query: String) {
        lifecycleScope.launch {
            try {
                val response = RetrofitClient.apiService.searchSiswa(query)

                if (response.isSuccessful && response.body() != null) {
                    val siswaResponse = response.body()!!

                    if (siswaResponse.success && siswaResponse.data.isNotEmpty()) {
                        allStudents = siswaResponse.data
                        
                        // Update dropdown suggestions
                        val studentNames = allStudents.map { it.name }
                        val adapter = ArrayAdapter(
                            this@ManualStudentInputActivity,
                            android.R.layout.simple_dropdown_item_1line,
                            studentNames
                        )
                        binding.etSearch.setAdapter(adapter)
                        
                        // Show dropdown jika belum muncul
                        if (!binding.etSearch.isPopupShowing && query.isNotEmpty()) {
                            binding.etSearch.showDropDown()
                        }
                    } else {
                        // Tidak ada hasil
                        binding.etSearch.setAdapter(null)
                        if (query.length >= 2) {
                            Toast.makeText(
                                this@ManualStudentInputActivity,
                                "Nama '$query' belum terdaftar",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                }
            } catch (e: Exception) {
                Toast.makeText(
                    this@ManualStudentInputActivity,
                    "Error: ${e.message}",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    private fun showSelectedStudent(siswa: Siswa) {
        binding.cardSelectedStudent.visibility = View.VISIBLE
        binding.layoutEmptyState.visibility = View.GONE
        
        binding.tvSelectedName.text = siswa.name
        binding.tvSelectedInfo.text = "Kelas ${siswa.kelas} • NISN: ${siswa.nisn}"
    }
}
