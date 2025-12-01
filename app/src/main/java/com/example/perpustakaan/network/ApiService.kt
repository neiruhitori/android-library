package com.example.perpustakaan.network

import com.example.perpustakaan.model.*
import retrofit2.Response
import retrofit2.http.*

interface ApiService {
    
    // Get all books
    @GET("api/buku")
    suspend fun getAllBuku(): Response<BukuResponse>
    
    // Get book by ID
    @GET("api/buku/{id}")
    suspend fun getBukuById(@Path("id") id: Int): Response<BukuDetailResponse>
    
    // Search books
    @GET("api/buku/search")
    suspend fun searchBuku(@Query("q") query: String): Response<BukuResponse>
    
    // Get available book codes by book ID
    @GET("api/buku/{id}/kode-buku")
    suspend fun getAvailableBookCodes(@Path("id") bukuId: Int): Response<KodeBukuResponse>
    
    // Get siswa by ID
    @GET("api/siswa/{id}")
    suspend fun getSiswaById(@Path("id") id: Int): Response<SiswaResponse>
    
    // Search siswa
    @GET("api/siswa/search/query")
    suspend fun searchSiswa(@Query("q") query: String): Response<SiswaListResponse>
    
    // Create peminjaman
    @POST("api/peminjaman/store")
    suspend fun createPeminjaman(@Body request: PeminjamanRequest): Response<PeminjamanResponse>
}
