package com.example.perpustakaan.activity

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import com.example.perpustakaan.databinding.ActivityQrScannerBinding
import com.example.perpustakaan.network.RetrofitClient
import com.google.zxing.ResultPoint
import com.journeyapps.barcodescanner.BarcodeCallback
import com.journeyapps.barcodescanner.BarcodeResult
import kotlinx.coroutines.launch
import org.json.JSONObject

class QRScannerActivity : BaseActivity() {

    private lateinit var binding: ActivityQrScannerBinding
    private var isScanning = true
    
    companion object {
        private const val CAMERA_PERMISSION_REQUEST_CODE = 100
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityQrScannerBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupListeners()
        checkCameraPermission()
    }
    
    private fun checkCameraPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
            == PackageManager.PERMISSION_GRANTED) {
            // Permission already granted
            setupScanner()
        } else {
            // Request permission
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.CAMERA),
                CAMERA_PERMISSION_REQUEST_CODE
            )
        }
    }
    
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        
        if (requestCode == CAMERA_PERMISSION_REQUEST_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission granted
                setupScanner()
            } else {
                // Permission denied
                Toast.makeText(
                    this,
                    "Izin kamera diperlukan untuk scan QR Code",
                    Toast.LENGTH_LONG
                ).show()
                finish()
            }
        }
    }

    private fun setupScanner() {
        binding.barcodeScanner.decodeContinuous(object : BarcodeCallback {
            override fun barcodeResult(result: BarcodeResult?) {
                if (!isScanning) return
                
                result?.text?.let { qrContent ->
                    isScanning = false
                    processQRCode(qrContent)
                }
            }

            override fun possibleResultPoints(resultPoints: MutableList<ResultPoint>?) {
                // Optional: handle result points
            }
        })
    }

    private fun setupListeners() {
        binding.btnBack.setOnClickListener {
            finish()
        }
    }

    private fun processQRCode(qrContent: String) {
        try {
            // QR Code berisi JSON dengan format: {"id": 123}
            val jsonObject = JSONObject(qrContent)
            val siswaId = jsonObject.getInt("id")
            
            // Fetch siswa data from API
            fetchSiswaData(siswaId)
            
        } catch (e: Exception) {
            Toast.makeText(this, "QR Code tidak valid", Toast.LENGTH_SHORT).show()
            isScanning = true
        }
    }

    private fun fetchSiswaData(siswaId: Int) {
        lifecycleScope.launch {
            try {
                val response = RetrofitClient.apiService.getSiswaById(siswaId)
                
                if (response.isSuccessful && response.body() != null) {
                    val siswaResponse = response.body()!!
                    
                    if (siswaResponse.success) {
                        val siswa = siswaResponse.data
                        
                        // Navigate to checkout with siswa data
                        val intent = Intent(this@QRScannerActivity, CheckoutActivity::class.java)
                        intent.putExtra("SISWA_ID", siswa.id)
                        intent.putExtra("SISWA_NAME", siswa.name)
                        intent.putExtra("SISWA_KELAS", siswa.kelas)
                        intent.putExtra("SISWA_NISN", siswa.nisn)
                        startActivity(intent)
                        finish()
                    } else {
                        Toast.makeText(
                            this@QRScannerActivity,
                            siswaResponse.message ?: "Siswa tidak ditemukan",
                            Toast.LENGTH_SHORT
                        ).show()
                        isScanning = true
                    }
                } else {
                    Toast.makeText(
                        this@QRScannerActivity,
                        "Error: ${response.code()}",
                        Toast.LENGTH_SHORT
                    ).show()
                    isScanning = true
                }
                
            } catch (e: Exception) {
                Toast.makeText(
                    this@QRScannerActivity,
                    "Error: ${e.message}",
                    Toast.LENGTH_SHORT
                ).show()
                isScanning = true
            }
        }
    }

    override fun onResume() {
        super.onResume()
        binding.barcodeScanner.resume()
    }

    override fun onPause() {
        super.onPause()
        binding.barcodeScanner.pause()
    }
}
