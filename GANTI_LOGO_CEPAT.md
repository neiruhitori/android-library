# QUICK START: Ganti Logo dalam 3 Langkah

## 🚀 Cara Tercepat (5 Menit)

### 1️⃣ Copy Logo
```
Lokasi: D:\AndoidStudio\Perpustakaan\app\src\main\res\drawable\
File: Paste logo Anda dan rename jadi "logo_smpn2.png"
```

### 2️⃣ Edit 2 File (Ctrl+F di Android Studio)

**File: activity_dashboard.xml**
```xml
Cari: android:src="@mipmap/ic_launcher"
Ganti: android:src="@drawable/logo_smpn2"
(1 tempat)
```

**File: activity_splash.xml**
```xml
Cari: android:src="@mipmap/ic_launcher"
Ganti: android:src="@drawable/logo_smpn2"
(2 tempat - ivLogo DAN ivBackground)
```

### 3️⃣ Build & Run
```powershell
cd D:\AndoidStudio\Perpustakaan
.\gradlew clean assembleDebug installDebug
```

---

## 📍 Lokasi File Logo:

```
D:\AndoidStudio\Perpustakaan\
└── app\
    └── src\
        └── main\
            └── res\
                └── drawable\          ← TARUH LOGO DI SINI
                    └── logo_smpn2.png ← NAMA FILE HARUS INI
```

---

## ⚙️ Atau Gunakan Script Otomatis:

```powershell
cd D:\AndoidStudio\Perpustakaan
.\ganti-logo.ps1
# Ketik path logo saat diminta
```

---

## ✅ Hasil:

1. **Dashboard**: Logo SMPN2 di header kiri
2. **Splash Screen**: 
   - Logo SMPN2 di tengah (statis)
   - Background logo dengan animasi pulse
3. **Badge "Tersisa"**: Warna orange tua (#FF9800) lebih kontras

---

## 🎬 Animasi Splash Screen:

- ✅ **Background** → Scale up + Pulse (berulang)
- ✅ **Logo tengah** → Statis (tidak animasi)
- ✅ **Text** → Fade in
- ✅ **"Ketuk..."** → Blink

---

Baca `CARA_GANTI_LOGO.md` untuk detail lengkap!
