# Aplikasi Perpustakaan Android

Aplikasi Android untuk sistem perpustakaan yang terhubung dengan API Laravel.

## 🎨 Desain
- Tema: Dark Purple (seperti contoh yang diberikan)
- Warna Utama: Ungu gelap (#5B4E8B, #3D3557)
- Warna Aksen: Hijau neon (#4FFFB0)
- Background: Dark (#2A2438)

## ✨ Fitur
1. **Splash Screen** - Tampilan welcome saat pertama kali membuka aplikasi
2. **Dashboard** - Menampilkan daftar buku dengan fitur pencarian
3. **Detail Buku** - Menampilkan informasi lengkap tentang buku

## 📱 Screenshot Fitur
- Splash Screen: Tampilan pembuka dengan logo, ketuk untuk lanjut ke dashboard
- Dashboard: Daftar buku dengan search box dan card design modern
- Detail Buku: Informasi lengkap buku (cover, judul, penulis, tahun, tipe, deskripsi)

## 🔧 Teknologi
- **Language**: Kotlin
- **Architecture**: MVVM pattern ready
- **Networking**: Retrofit + OkHttp
- **Image Loading**: Glide
- **UI**: Material Design 3
- **Async**: Kotlin Coroutines

## 📦 Dependencies
```gradle
// Core
implementation("androidx.core:core-ktx:1.12.0")
implementation("androidx.appcompat:appcompat:1.6.1")
implementation("androidx.recyclerview:recyclerview:1.3.2")
implementation("com.google.android.material:material:1.11.0")
implementation("androidx.cardview:cardview:1.0.0")
implementation("androidx.constraintlayout:constraintlayout:2.1.4")

// Retrofit untuk API
implementation("com.squareup.retrofit2:retrofit:2.9.0")
implementation("com.squareup.retrofit2:converter-gson:2.9.0")
implementation("com.squareup.okhttp3:logging-interceptor:4.11.0")

// Glide untuk load image
implementation("com.github.bumptech.glide:glide:4.16.0")

// Coroutines untuk async
implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.7.3")
implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.6.2")
```

## 🚀 Setup

### 1. Konfigurasi API Laravel
Edit file `RetrofitClient.kt`:
```kotlin
// Untuk Emulator Android Studio
private const val BASE_URL = "http://10.0.2.2:8000/"

// Untuk Device Fisik (ganti dengan IP komputer Anda)
// private const val BASE_URL = "http://192.168.1.xxx:8000/"
```

### 2. Format Response API Laravel
Aplikasi mengharapkan response dengan format berikut:

**GET /api/buku** - Daftar semua buku
```json
{
  "success": true,
  "message": "Data buku berhasil diambil",
  "data": [
    {
      "id": 1,
      "judul": "Judul Buku",
      "penulis": "Nama Penulis",
      "tipe": "tahunan",
      "tahun_terbit": 2024,
      "description": "Deskripsi buku",
      "foto": "http://url-gambar.com/buku.jpg",
      "created_at": "2024-01-01 00:00:00",
      "updated_at": "2024-01-01 00:00:00"
    }
  ]
}
```

**GET /api/buku/{id}** - Detail buku
```json
{
  "success": true,
  "message": "Detail buku berhasil diambil",
  "data": {
    "id": 1,
    "judul": "Judul Buku",
    "penulis": "Nama Penulis",
    "tipe": "tahunan",
    "tahun_terbit": 2024,
    "description": "Deskripsi buku lengkap",
    "foto": "http://url-gambar.com/buku.jpg",
    "created_at": "2024-01-01 00:00:00",
    "updated_at": "2024-01-01 00:00:00"
  }
}
```

**GET /api/buku/search?q=keyword** - Pencarian buku (opsional)
```json
{
  "success": true,
  "message": "Hasil pencarian",
  "data": [ /* array buku */ ]
}
```

### 3. Struktur Database
Sesuai dengan diagram yang Anda berikan:

**Tabel: bukus**
- id (bigint, primary key)
- judul (varchar 255)
- penulis (varchar 255)
- tipe (enum: 'harian', 'tahunan')
- tahun_terbit (year)
- description (text)
- foto (varchar 255, nullable)
- created_at (timestamp)
- updated_at (timestamp)

## 📁 Struktur Project
```
app/src/main/java/com/example/perpustakaan/
├── activity/
│   ├── SplashActivity.kt          # Splash screen
│   ├── DashboardActivity.kt       # Dashboard dengan list buku
│   └── DetailBukuActivity.kt      # Detail buku
├── adapter/
│   └── BukuAdapter.kt             # Adapter untuk RecyclerView
├── model/
│   ├── Buku.kt                    # Model data buku
│   └── Siswa.kt                   # Model data siswa (untuk fitur future)
└── network/
    ├── ApiService.kt              # Interface API endpoints
    └── RetrofitClient.kt          # Retrofit configuration

app/src/main/res/
├── layout/
│   ├── activity_splash.xml        # Layout splash screen
│   ├── activity_dashboard.xml     # Layout dashboard
│   ├── activity_detail_buku.xml   # Layout detail buku
│   └── item_buku.xml              # Layout item list buku
├── drawable/
│   ├── bg_*.xml                   # Background shapes
│   └── ic_*.xml                   # Icons
└── values/
    ├── colors.xml                 # Warna tema
    ├── strings.xml                # String resources
    └── themes.xml                 # Tema aplikasi
```

## 🎯 Cara Menggunakan

### Build & Run
1. Buka project di Android Studio
2. Sync Gradle
3. Jalankan aplikasi (Shift + F10)

### Testing dengan Emulator
1. Pastikan Laravel API berjalan di `http://localhost:8000`
2. Gunakan `http://10.0.2.2:8000` sebagai BASE_URL di RetrofitClient
3. Test CRUD operations

### Testing dengan Device Fisik
1. Pastikan device dan komputer terhubung ke WiFi yang sama
2. Cari IP Address komputer (ipconfig/ifconfig)
3. Update BASE_URL di RetrofitClient dengan `http://[IP_KOMPUTER]:8000`
4. Install APK ke device

## 🔐 Permissions
```xml
<uses-permission android:name="android.permission.INTERNET" />
<uses-permission android:name="android.permission.ACCESS_NETWORK_STATE" />
```

## 📝 Catatan

### Fitur yang Sudah Dibuat:
✅ Splash Screen dengan tap to continue
✅ Dashboard dengan daftar buku
✅ Fitur pencarian (search) real-time
✅ Detail buku
✅ Dark theme dengan warna purple & green accent
✅ Card design modern
✅ Image loading dengan Glide
✅ Error handling

### Fitur untuk Pengembangan Selanjutnya:
- [ ] Fitur peminjaman buku
- [ ] Fitur pengembalian buku
- [ ] Histori peminjaman
- [ ] Filter berdasarkan kategori/tipe
- [ ] Sorting (A-Z, terbaru, dll)
- [ ] Bookmark/favorite buku
- [ ] QR Code scanner untuk buku
- [ ] Notifikasi jatuh tempo pengembalian

## 🐛 Troubleshooting

### Error: Unable to connect to API
- Periksa BASE_URL di RetrofitClient.kt
- Pastikan Laravel API berjalan
- Periksa firewall/antivirus tidak memblokir koneksi

### Error: Cleartext HTTP not permitted
- Sudah ditangani dengan `android:usesCleartextTraffic="true"` di AndroidManifest
- Untuk production, gunakan HTTPS

### Image tidak muncul
- Periksa URL image dari API
- Pastikan URL dapat diakses
- Periksa permission INTERNET sudah ada

## 👨‍💻 Developer Notes

### Customize API Endpoints
Edit `ApiService.kt` untuk menambah/mengubah endpoint:
```kotlin
interface ApiService {
    @GET("api/buku")
    suspend fun getAllBuku(): Response<BukuResponse>
    
    // Tambah endpoint baru di sini
}
```

### Customize Warna
Edit `colors.xml`:
```xml
<color name="purple_primary">#5B4E8B</color>
<color name="green_accent">#4FFFB0</color>
<!-- dst -->
```

## 📞 Support
Jika ada pertanyaan atau issue, silakan hubungi developer.

---
**Version**: 1.0.0
**Last Updated**: November 2024
