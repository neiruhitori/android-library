package com.example.perpustakaan.model

import com.google.gson.annotations.SerializedName

data class Siswa(
    @SerializedName("id")
    val id: Int,
    
    @SerializedName("nisn")
    val nisn: String,
    
    @SerializedName("name")
    val name: String,
    
    @SerializedName("kelas")
    val kelas: String,
    
    @SerializedName("created_at")
    val createdAt: String?,
    
    @SerializedName("updated_at")
    val updatedAt: String?
)
