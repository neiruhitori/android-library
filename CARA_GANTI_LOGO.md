# Cara Mengganti Logo Perpustakaan SMP Negeri 2

## 🎯 Lokasi Logo yang Perlu Diganti

Ada **3 tempat** yang menggunakan logo:

1. **Dashboard Header** - Logo di kiri atas
2. **Splash Screen Logo** - Logo di tengah (statis)
3. **Splash Screen Background** - Background dengan animasi

---

## 📋 Cara 1: Copy File Logo Langsung (PALING MUDAH)

### Langkah 1: Siapkan File Logo
- Pastikan Anda punya file logo SMP Negeri 2 dalam format **PNG** atau **JPG**
- Ukuran recommended: **512x512 px** atau lebih besar
- File sebaiknya background **transparan** (PNG)

### Langkah 2: Copy ke Folder Drawable

**Via File Explorer:**
1. Buka folder: `D:\AndoidStudio\Perpustakaan\app\src\main\res\drawable\`
2. Copy paste file logo Anda ke folder ini
3. Rename file menjadi: **`logo_smpn2.png`** (PENTING: huruf kecil semua, tanpa spasi)

**Via Android Studio:**
1. Buka Android Studio
2. Di panel kiri, expand: `app → res → drawable`
3. Klik kanan pada folder `drawable` → **Show in Explorer**
4. Copy paste logo ke sana
5. Rename menjadi: `logo_smpn2.png`

### Langkah 3: Update File Layout

**Edit 3 file berikut di Android Studio:**

#### File 1: `activity_dashboard.xml`
Lokasi: `app/src/main/res/layout/activity_dashboard.xml`

Cari baris:
```xml
android:src="@mipmap/ic_launcher"
```

Ganti menjadi:
```xml
android:src="@drawable/logo_smpn2"
```

#### File 2: `activity_splash.xml` - Logo Tengah
Lokasi: `app/src/main/res/layout/activity_splash.xml`

Cari baris dengan `android:id="@+id/ivLogo"`, di bawahnya ada:
```xml
android:src="@mipmap/ic_launcher"
```

Ganti menjadi:
```xml
android:src="@drawable/logo_smpn2"
```

#### File 3: `activity_splash.xml` - Background
Lokasi: SAMA seperti File 2

Cari baris dengan `android:id="@+id/ivBackground"`, di bawahnya ada:
```xml
android:src="@mipmap/ic_launcher"
```

Ganti menjadi:
```xml
android:src="@drawable/logo_smpn2"
```

### Langkah 4: Build & Install
Buka terminal di folder project, jalankan:
```powershell
cd D:\AndoidStudio\Perpustakaan
.\gradlew clean assembleDebug installDebug
```

---

## 📋 Cara 2: Menggunakan Script PowerShell (OTOMATIS)

### Langkah 1: Simpan Logo
Simpan file logo Anda di lokasi yang mudah diakses (contoh: Desktop)

### Langkah 2: Jalankan Script
Buka PowerShell di folder project:
```powershell
cd D:\AndoidStudio\Perpustakaan
.\ganti-logo.ps1
```

### Langkah 3: Input Path Logo
Ketik path lengkap file logo, contoh:
```
C:\Users\YourName\Desktop\logo_smpn2.png
```

Script akan otomatis:
- ✅ Copy logo ke folder drawable
- ✅ Rename menjadi logo_smpn2.png
- ✅ Update semua file layout
- ✅ Siap di-build!

### Langkah 4: Build
```powershell
.\gradlew clean assembleDebug installDebug
```

---

## 📋 Cara 3: Via Android Studio Image Asset (HIGH QUALITY)

### Langkah 1: Open Image Asset Studio
1. Buka Android Studio
2. Klik kanan folder `res` di panel kiri
3. Pilih: **New → Image Asset**

### Langkah 2: Configure Image
1. **Icon Type**: Pilih "Image"
2. **Name**: Ketik `logo_smpn2`
3. **Asset Type**: Pilih "Image"
4. **Path**: Klik folder icon, pilih logo Anda
5. **Trim**: Pilih "No" (agar logo tidak terpotong)
6. **Padding**: 0% (full size)
7. **Background**: Transparent

### Langkah 3: Generate
1. Klik "Next"
2. Klik "Finish"
3. Android Studio akan generate logo dalam berbagai ukuran otomatis

### Langkah 4: Update Layout Files
Ikuti **Langkah 3** dari Cara 1 di atas

### Langkah 5: Build
```powershell
.\gradlew clean assembleDebug installDebug
```

---

## 🎨 Yang Sudah Dikerjakan:

### ✅ Badge "Buku Tersisa" Warna Lebih Gelap
- Warna lama: `#FFF3E0` (terlalu terang)
- Warna baru: `#FF9800` (Orange tua)
- Text: Putih, lebih kontras dan mudah dibaca

### ✅ Animasi Splash Screen di Background
- **Background** yang animasi (scale up + pulse)
- **Logo tengah** STATIS (tidak animasi)
- Background semi-transparan dengan gradient overlay
- Text fade in + blink

### ✅ Layout Changes
- Icon lonceng dihapus
- Nama sekolah ditambahkan di header

---

## 📱 Preview Setelah Ganti Logo:

**Splash Screen:**
```
┌─────────────────────────┐
│  [Background Animasi]   │
│                         │
│      [Logo Statis]      │
│                         │
│     Perpustakaan        │
│  SMP Negeri 2 Klakah    │
│      Lumajang           │
│                         │
│ Ketuk untuk melanjutkan │
└─────────────────────────┘
```

**Dashboard Header:**
```
┌─────────────────────────────────┐
│ [Logo] Perpustakaan    SMPN2    │
│                      Klakah     │
│         [Search Box]             │
└─────────────────────────────────┘
```

---

## 🔧 Tips & Troubleshooting:

### Logo Tidak Muncul?
1. Pastikan nama file **huruf kecil semua**: `logo_smpn2.png`
2. Pastikan ada di folder: `app/src/main/res/drawable/`
3. Clean project: `.\gradlew clean`
4. Rebuild: `.\gradlew assembleDebug`

### Logo Terpotong/Tidak Proporsional?
Tambahkan padding di ImageView:
```xml
android:padding="8dp"
android:scaleType="fitCenter"
```

### Background Terlalu Gelap/Terang?
Edit di `activity_splash.xml`, ubah alpha:
```xml
<!-- Background image -->
android:alpha="0.3"  <!-- 0.1 = sangat transparan, 0.9 = hampir solid -->

<!-- Gradient overlay -->
android:alpha="0.7"  <!-- Adjust sesuai kebutuhan -->
```

### Animasi Terlalu Cepat/Lambat?
Edit file di `res/anim/`:
- `logo_scale_up.xml`: durasi 800ms → ubah sesuai selera
- `logo_pulse.xml`: durasi 1500ms → ubah sesuai selera

---

## 📝 Checklist:

- [ ] File logo sudah disiapkan (PNG/JPG)
- [ ] File logo sudah di-copy ke `res/drawable/`
- [ ] File logo sudah di-rename jadi `logo_smpn2.png`
- [ ] File `activity_dashboard.xml` sudah diedit (1 tempat)
- [ ] File `activity_splash.xml` sudah diedit (2 tempat)
- [ ] Sudah run: `.\gradlew clean assembleDebug installDebug`
- [ ] Aplikasi sudah di-test di emulator
- [ ] Logo muncul di dashboard ✅
- [ ] Logo muncul di splash screen ✅
- [ ] Background animasi bekerja ✅

---

Selamat! Logo SMP Negeri 2 Klakah Lumajang sudah terpasang! 🎉

