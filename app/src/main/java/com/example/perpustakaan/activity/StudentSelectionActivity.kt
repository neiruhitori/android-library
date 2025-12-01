package com.example.perpustakaan.activity

import android.content.Intent
import android.os.Bundle
import com.example.perpustakaan.databinding.ActivityStudentSelectionBinding

class StudentSelectionActivity : BaseActivity() {

    private lateinit var binding: ActivityStudentSelectionBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityStudentSelectionBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupToolbar()
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

    private fun setupListeners() {
        // QR Scan option
        binding.cardQRScan.setOnClickListener {
            val intent = Intent(this, QRScannerActivity::class.java)
            startActivity(intent)
        }

        // Manual Input option
        binding.cardManualInput.setOnClickListener {
            val intent = Intent(this, ManualStudentInputActivity::class.java)
            startActivity(intent)
        }
    }
}
