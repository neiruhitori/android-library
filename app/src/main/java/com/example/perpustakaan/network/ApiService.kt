package com.example.perpustakaan.network

import com.example.perpustakaan.model.BukuDetailResponse
import com.example.perpustakaan.model.BukuResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

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
}
