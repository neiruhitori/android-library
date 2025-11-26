# 🚀 Panduan Build & Test Aplikasi

## Prasyarat

1. **Android Studio** sudah terinstall
2. **Laravel Server** berjalan di `http://localhost:8000` atau sesuai konfigurasi
3. **Emulator/Device** sudah siap

---

## 📦 Build Aplikasi

### 1. Sync Gradle
Buka Android Studio dan sync project:
```
File > Sync Project with Gradle Files
```

### 2. Clean Project
```powershell
cd d:\AndoidStudio\Perpustakaan
.\gradlew clean
```

### 3. Build APK Debug
```powershell
.\gradlew assembleDebug
```

APK akan tersedia di:
```
app\build\outputs\apk\debug\app-debug.apk
```

### 4. Install ke Device/Emulator
```powershell
.\gradlew installDebug
```

Atau install manual:
```powershell
adb install app\build\outputs\apk\debug\app-debug.apk
```

---

## 🌐 Setup Laravel Backend

### 1. Jalankan Server
```powershell
cd c:\laragon\www\sistem-library
php artisan serve
```

Server akan berjalan di: `http://localhost:8000`

### 2. Test API Endpoint
Buka browser atau Postman, test endpoint:

```
GET http://localhost:8000/api/buku
```

Response expected:
```json
{
  "success": true,
  "message": "Data buku berhasil diambil",
  "data": [
    {
      "id": 1,
      "judul": "Contoh Buku",
      "penulis": "Penulis",
      "tipe": "harian",
      "tahun_terbit": 2024,
      "description": "Deskripsi buku",
      "foto": null,
      "foto_url": null,
      "stok": 10,
      "stok_tersedia": 5,
      "sedang_dipinjam": 5,
      "total_peminjaman": 25,
      "created_at": "2024-01-01",
      "updated_at": "2024-01-01"
    }
  ]
}
```

---

## 🔧 Konfigurasi API URL

### Lokasi File
```
app/src/main/java/com/example/perpustakaan/network/RetrofitClient.kt
```

### Update Base URL
Sesuaikan dengan IP server Laravel:

```kotlin
private const val BASE_URL = "http://10.0.2.2:8000/" // Untuk emulator
// atau
private const val BASE_URL = "http://192.168.1.X:8000/" // Untuk device fisik
```

**Catatan:**
- `10.0.2.2` = localhost dari emulator Android
- `192.168.1.X` = IP komputer di network lokal untuk device fisik

---

## ✅ Checklist Testing

### Backend (Laravel)
- [ ] Server Laravel running
- [ ] Database memiliki data buku
- [ ] Tabel `kode_bukus` memiliki data
- [ ] API `/api/buku` return data dengan field `stok`, `stok_tersedia`, dll
- [ ] API `/api/buku/{id}` berfungsi

### Android App
- [ ] Aplikasi berhasil di-build tanpa error
- [ ] Aplikasi terinstall di device/emulator
- [ ] Splash screen muncul
- [ ] Dashboard menampilkan buku dalam 3 kategori
- [ ] Search berfungsi
- [ ] Badge stok muncul dengan warna yang sesuai
- [ ] Klik buku membuka halaman detail
- [ ] Detail buku menampilkan info stok lengkap

---

## 🐛 Troubleshooting

### Error: "Failed to connect"
**Solusi:**
1. Pastikan server Laravel berjalan
2. Cek base URL di RetrofitClient.kt
3. Untuk emulator, gunakan `10.0.2.2` bukan `localhost`
4. Untuk device fisik, pastikan di network yang sama

### Error: "Cleartext HTTP traffic not permitted"
**Solusi:**
File sudah include `network_security_config.xml` dan `usesCleartextTraffic="true"`

### Stok selalu 0
**Solusi:**
1. Pastikan tabel `kode_bukus` memiliki data
2. Check relasi di model Laravel `Buku.php`

### Gambar tidak muncul
**Solusi:**
1. Jalankan: `php artisan storage:link`
2. Pastikan folder `storage/app/public` accessible
3. Foto URL harus berupa URL lengkap atau path yang valid

---

## 📱 Test Scenarios

### Scenario 1: Lihat Daftar Buku
1. Buka aplikasi
2. Lihat splash screen
3. Dashboard muncul dengan 3 kategori
4. Scroll ke bawah untuk melihat semua kategori
5. Cek badge stok pada setiap buku

### Scenario 2: Pencarian Buku
1. Di dashboard, klik search box
2. Ketik judul/penulis buku
3. Hasil pencarian muncul real-time
4. Clear search, semua buku muncul kembali

### Scenario 3: Detail Buku
1. Klik salah satu buku
2. Halaman detail muncul
3. Cek informasi lengkap:
   - Cover buku
   - Badge stok (warna sesuai ketersediaan)
   - Info penulis, tahun, tipe
   - Total stok & sedang dipinjam
   - Deskripsi
   - Statistik peminjaman (jika ada)

### Scenario 4: Kategori Horizontal
1. Di dashboard, scroll kategori horizontal
2. Klik salah satu kategori
3. Halaman scroll ke section yang sesuai

---

## 🎨 Visual Testing

### Cek Tampilan:
- [ ] Header gradient biru terlihat smooth
- [ ] Card buku background putih
- [ ] Badge stok warna sesuai (hijau/orange/merah)
- [ ] Text readable (contrast bagus)
- [ ] Icon berwarna biru
- [ ] Shadow pada card terlihat halus
- [ ] Image loading dengan placeholder

---

## 📊 Performance Testing

### Monitor:
- Loading time data dari API
- Scroll smoothness
- Image loading speed (Glide cache)
- Memory usage

---

## 🔄 Re-build Setelah Perubahan

Jika ada perubahan code:

```powershell
# Clean + Build + Install
.\gradlew clean assembleDebug installDebug
```

Atau dari Android Studio:
```
Build > Clean Project
Build > Rebuild Project
Run > Run 'app'
```

---

## 📝 Notes

- **First Build** mungkin memakan waktu lebih lama
- **Gradle Download** dependencies saat pertama kali
- **API Response** sangat bergantung pada data di database Laravel
- **Image Caching** by Glide akan mempercepat loading setelah first load

---

Selamat testing! 🎉
