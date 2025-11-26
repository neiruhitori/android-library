# 🔧 Troubleshooting API Connection

## Masalah yang Sudah Diperbaiki

### ✅ Backend Laravel - Error 500
**Masalah**: API `/api/buku` return error 500
**Penyebab**: Relasi database yang tidak ada atau null menyebabkan crash
**Solusi**: Menambahkan try-catch untuk setiap operasi dan set default value

### ✅ Android - URL Connection
**Masalah**: Aplikasi tidak bisa connect ke Laravel server
**Penyebab**: URL salah untuk emulator
**Solusi**: Ganti dari `http://172.20.10.3:8000/` ke `http://10.0.2.2:8000/`

---

## 🔍 Cara Test Koneksi

### 1. Test API Laravel (dari komputer)
```powershell
# Pastikan server running
cd c:\laragon\www\sistem-library
php artisan serve

# Di terminal lain, test API:
Invoke-WebRequest -Uri "http://localhost:8000/api/buku" -Method GET
```

**Expected Result:**
```json
{
  "success": true,
  "message": "Data buku berhasil diambil",
  "data": [...]
}
```

### 2. Cek Logcat Android
```powershell
# Jalankan logcat untuk melihat request/response
adb logcat | Select-String "OkHttp"
```

Atau gunakan Android Studio Logcat viewer.

---

## 📱 Konfigurasi URL untuk Berbagai Device

### Emulator Android (Default)
```kotlin
private const val BASE_URL = "http://10.0.2.2:8000/"
```
`10.0.2.2` = localhost komputer dari perspektif emulator

### Device Fisik (Connected via WiFi)
1. Cek IP komputer:
```powershell
ipconfig
# Cari "IPv4 Address" di adapter WiFi
# Contoh: 192.168.1.100
```

2. Update RetrofitClient:
```kotlin
private const val BASE_URL = "http://192.168.1.100:8000/"
```

3. Pastikan komputer dan device di WiFi yang sama!

### LDPlayer / Emulator Lain
Tergantung konfigurasi network emulator:
- LDPlayer: Biasanya `http://10.0.2.2:8000/`
- Beberapa emulator: `http://192.168.137.1:8000/`

---

## ⚠️ Checklist Sebelum Test

- [ ] Server Laravel running (`php artisan serve`)
- [ ] API test dari browser/Postman berhasil
- [ ] Database memiliki data buku
- [ ] RetrofitClient BASE_URL sudah benar
- [ ] App sudah di-rebuild setelah ganti URL
- [ ] Network permission di AndroidManifest.xml ada
- [ ] usesCleartextTraffic="true" sudah di AndroidManifest

---

## 🐛 Debug Steps

### Jika masih error 500 di Android:

1. **Cek Logcat**:
```bash
adb logcat -s "OkHttp"
```

2. **Lihat Response Body**:
   - Buka Android Studio
   - Window > Logcat
   - Filter: "OkHttp"
   - Cari response dari `/api/buku`

3. **Test Manual dari Browser**:
   - Emulator: Browse ke `http://10.0.2.2:8000/api/buku`
   - Harus muncul JSON response

### Jika masih "Tidak ada buku":

1. **Cek data di database**:
```powershell
cd c:\laragon\www\sistem-library
php artisan tinker
>>> App\Models\Buku::count()
>>> App\Models\Buku::all()
```

2. **Test API di Postman**:
   - GET `http://localhost:8000/api/buku`
   - Harus return array data

3. **Cek Response Android**:
   - Logcat akan show full JSON response
   - Pastikan `success: true` dan `data` ada isinya

---

## 💡 Tips

### Gunakan Logging Interceptor
RetrofitClient sudah include logging, jadi semua request/response akan tampil di Logcat:
```
D/OkHttp: --> GET http://10.0.2.2:8000/api/buku
D/OkHttp: <-- 200 OK (123ms)
D/OkHttp: {"success":true,"data":[...]}
```

### Server URL Cepat Ganti
Buat variable di `local.properties` (tidak di-commit):
```properties
api.base.url=http://10.0.2.2:8000/
```

Lalu di `build.gradle`:
```gradle
buildConfigField "String", "API_BASE_URL", "\"${apiBaseUrl}\""
```

---

## 📊 Status Saat Ini

✅ Laravel API berjalan normal
✅ API return data dengan field stok
✅ Android app berhasil compile
✅ URL sudah disesuaikan untuk emulator
✅ Error handling sudah ditambahkan

**Next**: Buka aplikasi di emulator dan test!

---

## 🆘 Jika Masih Bermasalah

Share screenshot/log dari:
1. Logcat Android (filter "OkHttp")
2. Response dari `http://localhost:8000/api/buku`
3. Error message di aplikasi (screenshot)
