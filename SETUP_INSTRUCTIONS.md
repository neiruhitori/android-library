# Setup Instructions - Aplikasi Perpustakaan

## ⚠️ Persyaratan System

### Java Development Kit (JDK)
**PENTING**: Aplikasi ini memerlukan **Java 17** atau lebih tinggi.

#### Cara Check Java Version:
```bash
java -version
```

#### Jika Java version < 17, download JDK 17:
- Download dari: https://www.oracle.com/java/technologies/javase/jdk17-archive-downloads.html
- Atau gunakan OpenJDK: https://adoptium.net/

#### Set JAVA_HOME (Windows):
1. Buka "Environment Variables"
2. Tambah/edit JAVA_HOME: `C:\Program Files\Java\jdk-17`
3. Restart Android Studio

#### Alternative: Set di gradle.properties
Tambahkan di file `gradle.properties`:
```properties
org.gradle.java.home=C:\\Program Files\\Java\\jdk-17
```

---

## 🚀 Langkah Setup Project

### 1. Clone/Open Project
```bash
cd d:\AndoidStudio\Perpustakaan
```

### 2. Sync Gradle
- Buka Android Studio
- File → Open → Pilih folder Perpustakaan
- Tunggu Gradle sync selesai
- Jika ada error, periksa Java version

### 3. Konfigurasi API URL
Edit `app/src/main/java/com/example/perpustakaan/network/RetrofitClient.kt`:

```kotlin
// Untuk Android Emulator
private const val BASE_URL = "http://10.0.2.2:8000/"

// Untuk Device Fisik (ganti dengan IP komputer)
// private const val BASE_URL = "http://192.168.1.XXX:8000/"
```

### 4. Setup Laravel API
Lihat file `LARAVEL_API_EXAMPLE.md` untuk contoh implementasi API Laravel.

**Laravel harus berjalan di:**
- URL: http://localhost:8000
- Endpoints:
  - GET /api/buku (list semua buku)
  - GET /api/buku/{id} (detail buku)
  - GET /api/buku/search?q=keyword (search buku)

### 5. Build & Run
```bash
# Clean build
./gradlew clean

# Build project
./gradlew build

# Atau langsung run dari Android Studio (Shift + F10)
```

---

## 📱 Testing

### Dengan Emulator
1. Buka AVD Manager di Android Studio
2. Start emulator
3. Run aplikasi (Shift + F10)
4. Pastikan Laravel API running di http://localhost:8000

### Dengan Device Fisik
1. Enable Developer Options & USB Debugging
2. Connect device via USB
3. Cari IP address komputer (cmd → ipconfig)
4. Update BASE_URL dengan IP komputer
5. Pastikan device & komputer di WiFi yang sama
6. Run aplikasi

---

## 🎨 Struktur Design

### Color Palette
```xml
<!-- Primary Colors -->
Purple Primary: #5B4E8B
Purple Dark: #3D3557
Purple Light: #7B6BA8

<!-- Background -->
Background Dark: #2A2438
Card Background: #3D3557

<!-- Accent -->
Green Accent: #4FFFB0
Green Light: #6FFFC5

<!-- Text -->
Text Primary: #FFFFFF
Text Secondary: #B8B3C8
Text Hint: #8A8495
```

### Font Sizes
- Title: 24sp (bold)
- Subtitle: 20sp (bold)
- Body: 16sp
- Caption: 14sp
- Small: 12sp

---

## 🐛 Common Issues & Solutions

### Issue 1: Java Version Error
```
Error: Android Gradle plugin requires Java 17
```
**Solution**: 
- Install JDK 17
- Set JAVA_HOME ke JDK 17
- Restart Android Studio

### Issue 2: Cannot Connect to API
```
Error: Unable to resolve host / Connection refused
```
**Solution**:
- Periksa Laravel running: `php artisan serve`
- Periksa BASE_URL di RetrofitClient.kt
- Untuk emulator: gunakan `10.0.2.2` bukan `localhost`
- Untuk device: pastikan di WiFi yang sama

### Issue 3: Cleartext HTTP Traffic
```
Error: Cleartext HTTP traffic not permitted
```
**Solution**:
- Sudah ditangani di AndroidManifest.xml
- `android:usesCleartextTraffic="true"` sudah ada
- Untuk production, gunakan HTTPS

### Issue 4: Image Not Loading
```
Error: Failed to load image
```
**Solution**:
- Periksa URL image valid
- Pastikan INTERNET permission ada
- Check Glide dependency ter-install

### Issue 5: Gradle Sync Failed
```
Error: Failed to sync Gradle
```
**Solution**:
- File → Invalidate Caches → Invalidate and Restart
- Delete folder `.gradle` dan `build`
- Sync ulang

---

## 📂 File Penting

### Configuration Files
- `build.gradle.kts` - Dependencies & build config
- `AndroidManifest.xml` - App configuration & permissions
- `RetrofitClient.kt` - API URL configuration

### Layout Files
- `activity_splash.xml` - Splash screen design
- `activity_dashboard.xml` - Dashboard design
- `activity_detail_buku.xml` - Detail page design
- `item_buku.xml` - Book card design

### Resources
- `colors.xml` - Color palette
- `strings.xml` - String resources
- `themes.xml` - App themes

---

## 📦 Dependencies Version

```gradle
Android Gradle Plugin: 8.13.1
Kotlin: 2.0.21
Min SDK: 23 (Android 6.0)
Target SDK: 36 (Android 15)
Compile SDK: 36

Retrofit: 2.9.0
Glide: 4.16.0
Coroutines: 1.7.3
Material Design: 1.11.0
```

---

## ✅ Checklist Before Running

- [ ] Java 17+ installed
- [ ] Android Studio updated
- [ ] Gradle synced successfully
- [ ] Laravel API running
- [ ] BASE_URL configured correctly
- [ ] Internet permission added
- [ ] Emulator/Device ready

---

## 📞 Support

Jika masih ada error:
1. Check error message di Logcat
2. Baca file `README.md` untuk detail lengkap
3. Cek `LARAVEL_API_EXAMPLE.md` untuk setup API

---

## 🎯 Next Steps

Setelah aplikasi berjalan:
1. Test semua fitur (Splash → Dashboard → Detail)
2. Test search functionality
3. Test dengan data real dari Laravel API
4. Customize sesuai kebutuhan
5. Tambah fitur lanjutan (peminjaman, dll)

---

**Happy Coding! 🚀**
