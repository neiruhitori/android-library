package com.example.perpustakaan.model

import com.google.gson.annotations.SerializedName

data class Buku(
    @SerializedName("id")
    val id: Int,
    
    @SerializedName("judul")
    val judul: String,
    
    @SerializedName("penulis")
    val penulis: String,
    
    @SerializedName("tipe")
    val tipe: String, // enum: harian, tahunan
    
    @SerializedName("tahun_terbit")
    val tahunTerbit: String?,  // Changed to String? karena dari API bisa null atau string
    
    @SerializedName("isbn")
    val isbn: String?,
    
    @SerializedName("kota_cetak")
    val kotaCetak: String?,
    
    @SerializedName("description")
    val description: String,
    
    @SerializedName("foto")
    val foto: String?,
    
    @SerializedName("foto_url")
    val fotoUrl: String?,
    
    @SerializedName("stok")
    val stok: Int = 0,
    
    @SerializedName("stok_tersedia")
    val stokTersedia: Int = 0,
    
    @SerializedName("sedang_dipinjam")
    val sedangDipinjam: Int = 0,
    
    @SerializedName("total_peminjaman")
    val totalPeminjaman: Int = 0,
    
    @SerializedName("created_at")
    val createdAt: String?,
    
    @SerializedName("updated_at")
    val updatedAt: String?
)

data class BukuResponse(
    @SerializedName("success")
    val success: Boolean,
    
    @SerializedName("message")
    val message: String?,
    
    @SerializedName("data")
    val data: List<Buku>
)

data class BukuDetailResponse(
    @SerializedName("success")
    val success: Boolean,
    
    @SerializedName("message")
    val message: String?,
    
    @SerializedName("data")
    val data: Buku
)
