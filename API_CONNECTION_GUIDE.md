# 🔌 Panduan Koneksi API Laravel dengan Android

## 📋 Ringkasan
Aplikasi Android perpustakaan sudah siap terhubung dengan API Laravel yang baru saja dibuat!

---

## ✅ Yang Sudah Dibuat

### 1. **Laravel API Controller** ✅
File: `app/Http/Controllers/Api/BukuApiController.php`

**Endpoints yang tersedia:**
- ✅ `GET /api/buku` - Mendapatkan semua buku
- ✅ `GET /api/buku/{id}` - Mendapatkan detail buku
- ✅ `GET /api/buku/search?q=keyword` - Mencari buku
- ✅ `POST /api/buku` - Tambah buku (admin)
- ✅ `PUT /api/buku/{id}` - Update buku (admin)
- ✅ `DELETE /api/buku/{id}` - Hapus buku (admin)

### 2. **API Routes** ✅
File: `routes/api.php`

Semua routes sudah terdaftar di `/api/buku/*`

### 3. **Android App Updated** ✅
- Model Buku sudah update dengan field `foto_url`
- Adapter sudah menggunakan URL foto dari API
- Detail Activity sudah menggunakan URL foto dari API

---

## 🚀 Langkah-langkah Setup

### **STEP 1: Setup Laravel (Backend)**

#### 1.1. Pastikan Database Aktif
```bash
# Start MySQL dari Laragon atau XAMPP
# Klik tombol "Start All" di Laragon
```

#### 1.2. Cek Konfigurasi Database
Edit file `.env` di `c:\laragon\www\sistem-library\.env`:
```env
DB_CONNECTION=mysql
DB_HOST=127.0.0.1
DB_PORT=3306
DB_DATABASE=sistem_library
DB_USERNAME=root
DB_PASSWORD=
```

#### 1.3. Test Koneksi Database
```bash
cd c:\laragon\www\sistem-library
php artisan migrate:status
```

#### 1.4. Jalankan Laravel Server
```bash
cd c:\laragon\www\sistem-library
php artisan serve
```

Server akan berjalan di: **http://localhost:8000**

#### 1.5. Test API
Buka browser atau Postman, test endpoint:
```
http://localhost:8000/api/buku
```

Response yang diharapkan:
```json
{
  "success": true,
  "message": "Data buku berhasil diambil",
  "data": [...]
}
```

---

### **STEP 2: Setup Android (Mobile App)**

#### 2.1. Update BASE_URL
Edit file: `d:\AndoidStudio\Perpustakaan\app\src\main\java\com\example\perpustakaan\network\RetrofitClient.kt`

**Untuk Emulator Android Studio:**
```kotlin
private const val BASE_URL = "http://10.0.2.2:8000/"
```

**Untuk Device Fisik:**
```kotlin
// Ganti dengan IP komputer Anda
private const val BASE_URL = "http://192.168.1.XXX:8000/"
```

**Cara cari IP Komputer:**
```bash
# Windows
ipconfig

# Cari "IPv4 Address" di bagian WiFi/Ethernet
# Contoh: 192.168.1.5
```

#### 2.2. Build & Run Android App
1. Buka Android Studio
2. Sync Gradle (jika belum)
3. Run app (Shift + F10)

---

## 🧪 Testing Koneksi

### Test 1: Check Laravel API
```bash
# Di browser atau Postman
http://localhost:8000/api/buku
```

Jika berhasil, akan muncul JSON response dengan data buku.

### Test 2: Check dari Android Emulator
1. Pastikan Laravel server running (`php artisan serve`)
2. Run Android app
3. Lihat Logcat di Android Studio
4. Jika ada error koneksi, cek BASE_URL

### Test 3: Check dari Device Fisik
1. Pastikan device & komputer di WiFi yang sama
2. Update BASE_URL dengan IP komputer
3. Run app di device
4. Test fitur search dan detail buku

---

## 🔍 Format Response API

### GET /api/buku (List Buku)
```json
{
  "success": true,
  "message": "Data buku berhasil diambil",
  "data": [
    {
      "id": 1,
      "judul": "Laskar Pelangi",
      "penulis": "Andrea Hirata",
      "tipe": "tahunan",
      "tahun_terbit": 2005,
      "description": "Novel tentang kehidupan anak Melayu Belitong",
      "foto": "buku/laskar-pelangi.jpg",
      "foto_url": "http://localhost:8000/storage/buku/laskar-pelangi.jpg",
      "created_at": "2024-01-01T00:00:00.000000Z",
      "updated_at": "2024-01-01T00:00:00.000000Z"
    }
  ]
}
```

### GET /api/buku/{id} (Detail Buku)
```json
{
  "success": true,
  "message": "Detail buku berhasil diambil",
  "data": {
    "id": 1,
    "judul": "Laskar Pelangi",
    "penulis": "Andrea Hirata",
    "tipe": "tahunan",
    "tahun_terbit": 2005,
    "description": "Novel tentang kehidupan anak Melayu Belitong yang bersekolah di SD Muhammadiyah",
    "foto": "buku/laskar-pelangi.jpg",
    "foto_url": "http://localhost:8000/storage/buku/laskar-pelangi.jpg",
    "created_at": "2024-01-01T00:00:00.000000Z",
    "updated_at": "2024-01-01T00:00:00.000000Z"
  }
}
```

### GET /api/buku/search?q=laskar (Search)
```json
{
  "success": true,
  "message": "Hasil pencarian buku",
  "data": [...]
}
```

---

## 📸 Upload Foto Buku

### Di Laravel Admin Panel
Jika Anda sudah punya form upload foto di Laravel:

1. Upload foto ke `storage/app/public/buku/`
2. Jalankan: `php artisan storage:link`
3. Foto bisa diakses via: `http://localhost:8000/storage/buku/namafile.jpg`

### Format Foto di Database
Simpan di kolom `foto` dengan format:
```
buku/laskar-pelangi.jpg
```

API Controller akan otomatis convert jadi URL lengkap:
```
http://localhost:8000/storage/buku/laskar-pelangi.jpg
```

---

## 🐛 Troubleshooting

### Problem 1: "No connection could be made"
**Di Laravel:**
```bash
# Pastikan MySQL running
# Cek di Laragon/XAMPP

# Test koneksi
php artisan migrate:status
```

### Problem 2: "Unable to resolve host" (Android)
**Solution:**
```kotlin
// Untuk Emulator, gunakan:
private const val BASE_URL = "http://10.0.2.2:8000/"

// BUKAN localhost atau 127.0.0.1
```

### Problem 3: CORS Error
**Di Laravel:**
Edit `config/cors.php`:
```php
'paths' => ['api/*'],
'allowed_origins' => ['*'],
'allowed_methods' => ['*'],
```

### Problem 4: 404 Not Found
**Check:**
```bash
# Lihat routes
php artisan route:list --path=api/buku

# Clear cache
php artisan route:clear
php artisan config:clear
php artisan cache:clear
```

### Problem 5: Foto tidak muncul
**Solution:**
```bash
# Buat symbolic link untuk storage
php artisan storage:link

# Pastikan folder exist
# storage/app/public/buku/
```

---

## 📝 Testing dengan Postman

### 1. Get All Books
```
Method: GET
URL: http://localhost:8000/api/buku
Headers: 
  Accept: application/json
```

### 2. Get Book Detail
```
Method: GET
URL: http://localhost:8000/api/buku/1
Headers: 
  Accept: application/json
```

### 3. Search Books
```
Method: GET
URL: http://localhost:8000/api/buku/search?q=laskar
Headers: 
  Accept: application/json
```

### 4. Create Book (Admin)
```
Method: POST
URL: http://localhost:8000/api/buku
Headers: 
  Content-Type: application/json
  Accept: application/json
Body (JSON):
{
  "judul": "Test Buku",
  "penulis": "Test Penulis",
  "tipe": "tahunan",
  "tahun_terbit": 2024,
  "description": "Deskripsi test",
  "foto": "buku/test.jpg"
}
```

---

## 🔐 Security (Untuk Production)

### Enable Authentication (Optional)
Jika ingin menambah authentication:

```php
// routes/api.php
Route::middleware('auth:sanctum')->group(function () {
    Route::post('/buku', [BukuApiController::class, 'store']);
    Route::put('/buku/{id}', [BukuApiController::class, 'update']);
    Route::delete('/buku/{id}', [BukuApiController::class, 'destroy']);
});

// Public routes (no auth)
Route::get('/buku', [BukuApiController::class, 'index']);
Route::get('/buku/search', [BukuApiController::class, 'search']);
Route::get('/buku/{id}', [BukuApiController::class, 'show']);
```

---

## ✨ Fitur API yang Sudah Dibuat

### ✅ Features:
- [x] Get all books with pagination ready
- [x] Get single book detail
- [x] Search books by keyword (judul, penulis, description)
- [x] Auto format foto URL
- [x] Proper error handling
- [x] JSON response format standard
- [x] HTTP status codes proper
- [x] CRUD operations ready

### 🔄 Response Format Konsisten:
```json
{
  "success": true/false,
  "message": "Pesan informasi",
  "data": {...} atau [...],
  "error": "Error message" (jika ada error)
}
```

---

## 📞 Quick Commands

### Laravel:
```bash
# Start server
php artisan serve

# Check routes
php artisan route:list --path=api

# Clear cache
php artisan optimize:clear

# Storage link
php artisan storage:link
```

### Android:
```bash
# Build
./gradlew build

# Clean
./gradlew clean
```

---

## 🎯 Next Steps

1. ✅ **Jalankan Laravel** - `php artisan serve`
2. ✅ **Test API** - Buka `http://localhost:8000/api/buku`
3. ✅ **Update BASE_URL** di Android
4. ✅ **Run Android App**
5. ✅ **Test Koneksi**

---

**Selamat! API Laravel dan Android App sudah siap terhubung! 🚀**

Jika ada pertanyaan atau error, lihat bagian Troubleshooting di atas.
