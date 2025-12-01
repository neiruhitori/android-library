package com.example.perpustakaan.model

import com.google.gson.annotations.SerializedName

// Model untuk kode buku yang tersedia
data class KodeBuku(
    @SerializedName("id")
    val id: Int,
    
    @SerializedName("kode_buku")
    val kodeBuku: String,
    
    @SerializedName("status")
    val status: String,
    
    @SerializedName("buku_judul")
    val bukuJudul: String,
    
    @SerializedName("buku_tipe")
    val bukuTipe: String
)

// Response untuk list kode buku
data class KodeBukuResponse(
    @SerializedName("success")
    val success: Boolean,
    
    @SerializedName("message")
    val message: String?,
    
    @SerializedName("data")
    val data: List<KodeBuku>
)

// Response untuk siswa
data class SiswaResponse(
    @SerializedName("success")
    val success: Boolean,
    
    @SerializedName("message")
    val message: String?,
    
    @SerializedName("data")
    val data: Siswa
)

// Response untuk search siswa
data class SiswaListResponse(
    @SerializedName("success")
    val success: Boolean,
    
    @SerializedName("message")
    val message: String?,
    
    @SerializedName("data")
    val data: List<Siswa>
)

// Request untuk create peminjaman
data class PeminjamanRequest(
    @SerializedName("siswas_id")
    val siswasId: Int,
    
    @SerializedName("tanggal_pinjam")
    val tanggalPinjam: String,
    
    @SerializedName("tanggal_kembali")
    val tanggalKembali: String,
    
    @SerializedName("kode_buku_ids")
    val kodeBukuIds: List<Int>
)

// Response untuk create peminjaman
data class PeminjamanResponse(
    @SerializedName("success")
    val success: Boolean,
    
    @SerializedName("message")
    val message: String?,
    
    @SerializedName("data")
    val data: PeminjamanData?
)

data class PeminjamanData(
    @SerializedName("id")
    val id: Int,
    
    @SerializedName("siswa")
    val siswa: SiswaData,
    
    @SerializedName("tanggal_pinjam")
    val tanggalPinjam: String,
    
    @SerializedName("tanggal_kembali")
    val tanggalKembali: String,
    
    @SerializedName("status")
    val status: String,
    
    @SerializedName("buku_details")
    val bukuDetails: List<BukuDetail>
)

data class SiswaData(
    @SerializedName("id")
    val id: Int,
    
    @SerializedName("name")
    val name: String,
    
    @SerializedName("kelas")
    val kelas: String
)

data class BukuDetail(
    @SerializedName("kode_buku")
    val kodeBuku: String,
    
    @SerializedName("judul")
    val judul: String,
    
    @SerializedName("status")
    val status: String
)

// Item untuk cart peminjaman (local data)
data class CartItem(
    val buku: Buku,
    var quantity: Int = 1,
    var selectedKodeBuku: MutableList<KodeBuku> = mutableListOf()
)
