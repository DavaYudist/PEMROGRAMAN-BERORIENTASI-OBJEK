# Script Peluncuran Aplikasi Desktop Native FleetSumenep (JavaFX WebView)
# Mahasiswa: M. Davana Yudista Firdausy (3125522002) - Kelas A D3 TI-SM

Write-Host "==================================================================" -ForegroundColor Green
Write-Host " FleetSumenep DESKTOP • Rental Car Fleet Monitoring MVP" -ForegroundColor Green
Write-Host " Native Window (JavaFX WebView + Leaflet Map + Parking Lot View)" -ForegroundColor Green
Write-Host "==================================================================" -ForegroundColor Green

# 1. Tentukan path direktori
$scriptDir = Split-Path -Parent $MyInvocation.MyCommand.Path
if (-not $scriptDir) { $scriptDir = (Get-Location).Path }
if (-not (Test-Path "$scriptDir/SourceCode")) {
    $scriptDir = "$scriptDir/P10_3125522002_M Davana Yudista Firdausy"
}

$fxLib = "$scriptDir/lib/javafx-sdk-17.0.12/lib"
$fxBin = "$scriptDir/lib/javafx-sdk-17.0.12/bin"
$binDir = "$scriptDir/SourceCode/bin"
$mysqlJar = "$scriptDir/lib/mysql-connector-j-8.3.0.jar"

if (-not (Test-Path $binDir)) {
    New-Item -ItemType Directory -Force -Path $binDir | Out-Null
}

# 2. Kompilasi semua file .java
Write-Host "Mengompilasi domain model dan antarmuka desktop..." -ForegroundColor Cyan
$sourceFiles = (Get-ChildItem -Path "$scriptDir/SourceCode" -Filter "*.java" -Recurse).FullName
javac -encoding UTF-8 -d $binDir -cp "$mysqlJar;." --module-path $fxLib --add-modules javafx.controls,javafx.web $sourceFiles

if ($LASTEXITCODE -ne 0) {
    Write-Host "Kompilasi Gagal dengan kode: $LASTEXITCODE" -ForegroundColor Red
    exit $LASTEXITCODE
}

Write-Host "Kompilasi Berhasil (0 Error)!" -ForegroundColor Green
Write-Host "Membuka jendela aplikasi native desktop (Software Prism -Dprism.order=sw)..." -ForegroundColor Yellow

# 3. Jalankan aplikasi desktop native dengan software rasterizer stabil dan MySQL driver
java "-Djava.library.path=$fxBin" "-Dprism.order=sw" "-Dprism.verbose=false" --module-path $fxLib --add-modules javafx.controls,javafx.web -cp "$binDir;$mysqlJar;." SourceCode.Launcher
