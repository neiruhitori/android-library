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
    val tahunTerbit: Int,
    
    @SerializedName("description")
    val description: String,
    
    @SerializedName("foto")
    val foto: String?,
    
    @SerializedName("foto_url")
    val fotoUrl: String?,
    
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
