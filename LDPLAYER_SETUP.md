# Setup untuk LDPlayer Emulator

## ⚠️ Masalah Koneksi LDPlayer

LDPlayer memiliki konfigurasi jaringan yang berbeda dengan emulator Android Studio standar.

## 🔧 Solusi Koneksi

### Cara 1: Gunakan `10.0.2.2` (Sudah di-set)
```kotlin
private const val BASE_URL = "http://10.0.2.2:8000/"
```

Ini sudah di-set di `RetrofitClient.kt`. Coba ini terlebih dahulu.

### Cara 2: Gunakan IP WiFi Komputer
Jika Cara 1 tidak berhasil, uncomment baris ini:
```kotlin
private const val BASE_URL = "http://172.20.10.3:8000/"
```

### Cara 3: Gunakan IP Local Area Connection
Jika LDPlayer menggunakan bridge mode:
```kotlin
private const val BASE_URL = "http://192.168.137.1:8000/"
```

## 🚀 Langkah-Langkah

### 1. Pastikan Laravel Server Running
Server sudah running di terminal dengan perintah:
```bash
php artisan serve --host=0.0.0.0 --port=8000
```

### 2. Cek Setting LDPlayer Network

#### Buka LDPlayer Settings:
1. Klik icon **gear/settings** di LDPlayer
2. Pilih **Network**
3. Cek mode yang digunakan:
   - **NAT** → gunakan `10.0.2.2`
   - **Bridge** → gunakan IP komputer (`172.20.10.3` atau `192.168.137.1`)

### 3. Test Koneksi dari LDPlayer

Buka **Browser** di dalam LDPlayer, lalu akses:
- `http://10.0.2.2:8000/api/buku`
- `http://172.20.10.3:8000/api/buku`
- `http://192.168.137.1:8000/api/buku`

Yang mana yang berhasil, gunakan IP tersebut di `RetrofitClient.kt`

### 4. Rebuild Aplikasi
Setelah mengubah BASE_URL:
```
Build → Clean Project
Build → Rebuild Project
```

### 5. Jalankan Aplikasi
Run aplikasi di LDPlayer dan cek Logcat untuk error detail.

## 🔍 Debugging

### Cek Logcat
Filter dengan:
- `OkHttp` - untuk melihat HTTP request/response
- `Retrofit` - untuk error Retrofit
- `BukuRepository` - untuk error repository

### Jika Masih Error

1. **Pastikan Firewall tidak memblokir**:
   - Buka Windows Firewall
   - Allow port 8000 untuk PHP

2. **Test dari browser PC**:
   ```
   http://localhost:8000/api/buku
   ```
   Jika ini tidak berhasil, masalah ada di Laravel server.

3. **Cek IP yang aktif**:
   ```powershell
   ipconfig
   ```
   Gunakan IPv4 Address yang sesuai dengan koneksi LDPlayer.

## 📝 Konfigurasi Saat Ini

- **Laravel Server**: Running di `0.0.0.0:8000`
- **IP WiFi**: `172.20.10.3`
- **IP Local**: `192.168.137.1`
- **APP_URL**: `http://0.0.0.0:8000` (sudah di-update di `.env`)

## ✅ Checklist

- [x] Laravel server running
- [x] `.env` updated untuk accept all connections
- [x] Multiple BASE_URL options di RetrofitClient
- [x] Cache Laravel cleared
- [ ] Test koneksi dari browser LDPlayer
- [ ] Update BASE_URL sesuai yang berhasil
- [ ] Rebuild aplikasi Android
- [ ] Test aplikasi di LDPlayer

## 💡 Tips

1. **Coba satu per satu** IP address di browser LDPlayer dulu
2. **Yang berhasil** di browser, gunakan di `RetrofitClient.kt`
3. **Selalu rebuild** setelah mengubah BASE_URL
4. **Jangan lupa** uncomment BASE_URL yang dipilih dan comment yang lain
