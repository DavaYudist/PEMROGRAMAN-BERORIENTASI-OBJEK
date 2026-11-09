# PowerShell Runner Script for Modul 12
$ErrorActionPreference = "Stop"
$ScriptDir = Split-Path -Parent $MyInvocation.MyCommand.Path
Set-Location $ScriptDir

Write-Host "=========================================" -ForegroundColor Cyan
Write-Host "   RUNNER MODUL 12 - PERSISTENSI FILE    " -ForegroundColor Cyan
Write-Host "=========================================" -ForegroundColor Cyan

$binDir = Join-Path $ScriptDir "SourceCode\bin"
if (-not (Test-Path $binDir)) {
    New-Item -ItemType Directory -Path $binDir | Out-Null
}

Write-Host "[1/2] Mengompilasi Kode Program Java..." -ForegroundColor Yellow
javac -encoding UTF-8 -d SourceCode\bin SourceCode\model\*.java SourceCode\repository\*.java SourceCode\*.java
Write-Host "[OK] Kompilasi berhasil." -ForegroundColor Green

Write-Host "`n[2/2] Menjalankan Pengujian Otomatis..." -ForegroundColor Yellow
java -cp SourceCode\bin SourceCode.TestP12Runner
