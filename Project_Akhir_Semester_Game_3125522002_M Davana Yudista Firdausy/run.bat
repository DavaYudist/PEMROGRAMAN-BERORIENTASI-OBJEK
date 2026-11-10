@echo off
setlocal enabledelayedexpansion
title FleetSumenep Tycoon - Indonesian Car Rental Management Simulation

cd /d "%~dp0"

echo ==================================================================
echo  FleetSumenep TYCOON SIMULATION (OOP P1 - P15 FULL INTEGRATION)
echo  Indonesian Car Rental Tycoon Web Simulation
echo  Mahasiswa: M. Davana Yudista Firdausy (3125522002) - A D3 TI-SM
echo ==================================================================
echo.

set "FX_LIB=lib\javafx-sdk-17.0.12\lib"
set "FX_BIN=lib\javafx-sdk-17.0.12\bin"
set "BIN_DIR=SourceCode\bin"
set "MYSQL_JAR=lib\mysql-connector-j-8.3.0.jar"

if not exist "%BIN_DIR%" mkdir "%BIN_DIR%"

echo Mengompilasi seluruh domain model, simulasi game, dan launcher...
javac -encoding UTF-8 -d "%BIN_DIR%" -cp "%MYSQL_JAR%;." --module-path "%FX_LIB%" --add-modules javafx.controls,javafx.web SourceCode\model\exception\*.java SourceCode\model\*.java SourceCode\*.java
if errorlevel 1 goto compile_error

echo.
echo Kompilasi Berhasil (0 Error)!
echo.
echo Pilih Mode Eksekusi:
echo   [1] Buka Antarmuka Game Tycoon (Desktop WebKit - Green White)
echo   [2] Jalankan Test Regression Otomatis (16 Skenario P1-P15)
echo.

set "MODE=1"
set /p "MODE=Masukkan pilihan [1/2, default 1]: "

if "%MODE%"=="2" goto run_test
goto run_gui

:run_gui
echo.
echo Membuka antarmuka web game simulasi (-Dprism.order=sw)...
java "-Djava.library.path=%FX_BIN%" -Dprism.order=sw -Dprism.verbose=false --module-path "%FX_LIB%" --add-modules javafx.controls,javafx.web -cp "%BIN_DIR%;%MYSQL_JAR%;." SourceCode.Launcher
goto end

:run_test
echo.
echo Menjalankan Suite Uji Regresi Otomatis (16 Kasus Uji P1-P15)...
java -cp "%BIN_DIR%;%MYSQL_JAR%;." SourceCode.TestGameSimulationRunner
goto end

:compile_error
echo.
echo [ERROR] Kompilasi GAGAL! Periksa kode sumber Java.
goto end

:end
echo.
pause

