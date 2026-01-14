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
import com.journeyapps.barcodescanner.camera.CameraSettings
import kotlinx.coroutines.launch
import org.json.JSONObject

class QRScannerActivity : BaseActivity() {

    private lateinit var binding: ActivityQrScannerBinding
    private var isScanning = true
    private var isUsingFrontCamera = true // Default menggunakan kamera depan
    
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
        // Konfigurasi kamera
        val settings = CameraSettings()
        if (isUsingFrontCamera) {
            settings.requestedCameraId = 1 // Front camera
        } else {
            settings.requestedCameraId = 0 // Back camera
        }
        binding.barcodeScanner.cameraSettings = settings
        
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
        
        // Tombol untuk switch camera
        binding.btnSwitchCamera.setOnClickListener {
            switchCamera()
        }
    }
    
    private fun switchCamera() {
        isUsingFrontCamera = !isUsingFrontCamera
        
        // Pause scanner sebelum switch
        binding.barcodeScanner.pause()
        
        // Update camera settings
        val settings = CameraSettings()
        if (isUsingFrontCamera) {
            settings.requestedCameraId = 1 // Front camera
            Toast.makeText(this, "Beralih ke kamera depan", Toast.LENGTH_SHORT).show()
        } else {
            settings.requestedCameraId = 0 // Back camera
            Toast.makeText(this, "Beralih ke kamera belakang", Toast.LENGTH_SHORT).show()
        }
        binding.barcodeScanner.cameraSettings = settings
        
        // Resume scanner dengan kamera baru
        binding.barcodeScanner.resume()
    }

    private fun processQRCode(qrContent: String) {
        try {
            // Coba parse sebagai JSON dulu: {"id": 123}
            val jsonObject = JSONObject(qrContent)
            val siswaId = jsonObject.getInt("id")
            
            // Fetch siswa data from API
            fetchSiswaData(siswaId)
            
        } catch (jsonException: Exception) {
            // Jika bukan JSON, coba parse sebagai ID langsung (plain text)
            try {
                val siswaId = qrContent.trim().toInt()
                fetchSiswaData(siswaId)
            } catch (numberException: Exception) {
                Toast.makeText(this, "QR Code tidak valid: $qrContent", Toast.LENGTH_LONG).show()
                isScanning = true
            }
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
                        
                        // Simpan data siswa ke session
                        com.example.perpustakaan.util.SessionManager.saveSiswaSession(
                            this@QRScannerActivity,
                            siswa.id,
                            siswa.name,
                            siswa.kelas,
                            siswa.nisn
                        )
                        
                        Toast.makeText(
                            this@QRScannerActivity,
                            "Login berhasil! Selamat datang ${siswa.name}",
                            Toast.LENGTH_SHORT
                        ).show()
                        
                        // Kembali ke dashboard
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
