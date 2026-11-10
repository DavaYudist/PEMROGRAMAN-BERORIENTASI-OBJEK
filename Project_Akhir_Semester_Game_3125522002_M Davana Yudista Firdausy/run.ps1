# FleetSumenep Tycoon - PowerShell Launcher Script
# Mahasiswa: M. Davana Yudista Firdausy (3125522002) - A D3 TI-SM

$ScriptDir = Split-Path -Parent $MyInvocation.MyCommand.Path
Set-Location $ScriptDir

Write-Host "==================================================================" -ForegroundColor Green
Write-Host " FleetSumenep TYCOON SIMULATION (OOP P1 - P15 FULL INTEGRATION)" -ForegroundColor Cyan
Write-Host " Indonesian Car Rental Tycoon Web Simulation" -ForegroundColor White
Write-Host " Mahasiswa: M. Davana Yudista Firdausy (3125522002) - A D3 TI-SM" -ForegroundColor White
Write-Host "==================================================================" -ForegroundColor Green
Write-Host ""

$FX_LIB = "lib\javafx-sdk-17.0.12\lib"
$FX_BIN = "lib\javafx-sdk-17.0.12\bin"
$BIN_DIR = "SourceCode\bin"
$MYSQL_JAR = "lib\mysql-connector-j-8.3.0.jar"

if (-not (Test-Path $BIN_DIR)) {
    New-Item -ItemType Directory -Path $BIN_DIR | Out-Null
}

Write-Host "Mengompilasi seluruh domain model, simulasi game, dan launcher..." -ForegroundColor Yellow
$javaSources = (Get-ChildItem -Path "SourceCode" -Filter "*.java" -Recurse).FullName
javac -encoding UTF-8 -d $BIN_DIR -cp "$MYSQL_JAR;." --module-path $FX_LIB --add-modules javafx.controls,javafx.web $javaSources

if ($LASTEXITCODE -ne 0) {
    Write-Host "[ERROR] Kompilasi GAGAL! Periksa kode sumber Java." -ForegroundColor Red
    pause
    exit $LASTEXITCODE
}

Write-Host "Kompilasi Berhasil (0 Error)!" -ForegroundColor Green
Write-Host ""
Write-Host "Pilih Mode Eksekusi:" -ForegroundColor Cyan
Write-Host "  [1] Buka Antarmuka Game Tycoon (Desktop WebKit - Green White)"
Write-Host "  [2] Jalankan Test Regression Otomatis (12 Skenario P1-P15)"
Write-Host ""

$mode = Read-Host "Masukkan pilihan [1/2, default 1]"
if ([string]::IsNullOrWhiteSpace($mode)) { $mode = "1" }

if ($mode -eq "2") {
    Write-Host "Menjalankan Suite Uji Regresi Otomatis (12 Kasus Uji)..." -ForegroundColor Yellow
    java -cp "$BIN_DIR;$MYSQL_JAR;." SourceCode.TestGameSimulationRunner
} else {
    Write-Host "Membuka antarmuka web game simulasi (-Dprism.order=sw)..." -ForegroundColor Green
    java "-Djava.library.path=$FX_BIN" -Dprism.order=sw -Dprism.verbose=false --module-path $FX_LIB --add-modules javafx.controls,javafx.web -cp "$BIN_DIR;$MYSQL_JAR;." SourceCode.Launcher
}

Write-Host ""
pause
