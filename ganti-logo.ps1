# Script untuk copy logo SMPN2 ke project
# Jalankan script ini setelah menaruh file logo di folder yang sama dengan script

param(
    [string]$LogoPath = ""
)

Write-Host "========================================" -ForegroundColor Cyan
Write-Host "  Script Copy Logo SMPN 2 Klakah       " -ForegroundColor Cyan
Write-Host "========================================" -ForegroundColor Cyan
Write-Host ""

$ProjectPath = "D:\AndoidStudio\Perpustakaan\app\src\main\res"

# Jika logo path tidak diberikan, minta input
if ([string]::IsNullOrEmpty($LogoPath)) {
    Write-Host "Masukkan path lengkap file logo (contoh: C:\Downloads\logo_smpn2.png):" -ForegroundColor Yellow
    $LogoPath = Read-Host
}

# Cek apakah file ada
if (-not (Test-Path $LogoPath)) {
    Write-Host "❌ File tidak ditemukan: $LogoPath" -ForegroundColor Red
    exit 1
}

Write-Host "✅ File logo ditemukan!" -ForegroundColor Green
Write-Host ""

# Buat folder drawable jika belum ada
$DrawablePath = Join-Path $ProjectPath "drawable"
if (-not (Test-Path $DrawablePath)) {
    New-Item -ItemType Directory -Path $DrawablePath -Force | Out-Null
    Write-Host "✅ Folder drawable dibuat" -ForegroundColor Green
}

# Copy file logo
$DestinationPath = Join-Path $DrawablePath "logo_smpn2.png"
Copy-Item -Path $LogoPath -Destination $DestinationPath -Force

Write-Host "✅ Logo berhasil dicopy ke: $DestinationPath" -ForegroundColor Green
Write-Host ""

# Update layout files
Write-Host "📝 Updating layout files..." -ForegroundColor Yellow

# Update activity_dashboard.xml
$DashboardLayoutPath = Join-Path $ProjectPath "layout\activity_dashboard.xml"
if (Test-Path $DashboardLayoutPath) {
    $content = Get-Content $DashboardLayoutPath -Raw
    $content = $content -replace '@mipmap/ic_launcher', '@drawable/logo_smpn2'
    $content | Set-Content $DashboardLayoutPath -Encoding UTF8
    Write-Host "✅ Dashboard layout updated" -ForegroundColor Green
}

# Update activity_splash.xml (untuk logo dan background)
$SplashLayoutPath = Join-Path $ProjectPath "layout\activity_splash.xml"
if (Test-Path $SplashLayoutPath) {
    $content = Get-Content $SplashLayoutPath -Raw
    # Replace all occurrences
    $content = $content -replace '@mipmap/ic_launcher', '@drawable/logo_smpn2'
    $content | Set-Content $SplashLayoutPath -Encoding UTF8
    Write-Host "✅ Splash layout updated (logo + background)" -ForegroundColor Green
}

Write-Host ""
Write-Host "========================================" -ForegroundColor Cyan
Write-Host "✅ Logo berhasil diganti!" -ForegroundColor Green
Write-Host "========================================" -ForegroundColor Cyan
Write-Host ""
Write-Host "Langkah selanjutnya:" -ForegroundColor Yellow
Write-Host "1. Buka terminal di folder project" -ForegroundColor White
Write-Host "2. Jalankan: .\gradlew clean assembleDebug installDebug" -ForegroundColor White
Write-Host ""
Write-Host "Tekan Enter untuk keluar..."
Read-Host
