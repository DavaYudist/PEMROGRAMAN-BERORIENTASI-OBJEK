# Script Peluncuran Aplikasi Capstone FleetSumenep (P1 - P15)
# Mahasiswa: M. Davana Yudista Firdausy (3125522002) - Kelas A D3 TI-SM

Write-Host "==================================================================" -ForegroundColor Green
Write-Host " FleetSumenep CAPSTONE PROJECT • OOP P1 - P15 FULL INTEGRATION" -ForegroundColor Green
Write-Host " Dual Launcher: Native JavaFX WebView & Interactive Console CLI" -ForegroundColor Green
Write-Host "==================================================================" -ForegroundColor Green

$scriptDir = Split-Path -Parent $MyInvocation.MyCommand.Path
if (-not $scriptDir) { $scriptDir = (Get-Location).Path }

$fxLib = "$scriptDir/lib/javafx-sdk-17.0.12/lib"
$fxBin = "$scriptDir/lib/javafx-sdk-17.0.12/bin"
$binDir = "$scriptDir/SourceCode/bin"
$mysqlJar = "$scriptDir/lib/mysql-connector-j-8.3.0.jar"

if (-not (Test-Path $binDir)) {
    New-Item -ItemType Directory -Force -Path $binDir | Out-Null
}

Write-Host "Mengompilasi seluruh domain model, persistensi, dan antarmuka..." -ForegroundColor Cyan
$sourceFiles = (Get-ChildItem -Path "$scriptDir/SourceCode" -Filter "*.java" -Recurse).FullName
javac -encoding UTF-8 -d $binDir -cp "$mysqlJar;." --module-path $fxLib --add-modules javafx.controls,javafx.web $sourceFiles

if ($LASTEXITCODE -ne 0) {
    Write-Host "Kompilasi Gagal dengan kode: $LASTEXITCODE" -ForegroundColor Red
    exit $LASTEXITCODE
}

Write-Host "Kompilasi Berhasil (0 Error)!" -ForegroundColor Green
Write-Host "Pilih Mode Eksekusi:" -ForegroundColor Yellow
Write-Host "  [1] Antarmuka GUI Hybrid Desktop (JavaFX WebView)" -ForegroundColor Yellow
Write-Host "  [2] Antarmuka CLI Menu Interaktif (P13 Console)" -ForegroundColor Yellow
Write-Host "  [3] Automated Regression Test Suite (12 Skenario)" -ForegroundColor Yellow
$pilihan = Read-Host "Masukkan pilihan [1/2/3, default 1]"

if ($pilihan -eq "2") {
    java -cp "$binDir;$mysqlJar;." SourceCode.CliMain
} elseif ($pilihan -eq "3") {
    java -cp "$binDir;$mysqlJar;." SourceCode.TestRegressionRunner
} else {
    Write-Host "Membuka jendela aplikasi native desktop (Software Prism -Dprism.order=sw)..." -ForegroundColor Yellow
    java "-Djava.library.path=$fxBin" "-Dprism.order=sw" "-Dprism.verbose=false" --module-path $fxLib --add-modules javafx.controls,javafx.web -cp "$binDir;$mysqlJar;." SourceCode.Launcher
}
