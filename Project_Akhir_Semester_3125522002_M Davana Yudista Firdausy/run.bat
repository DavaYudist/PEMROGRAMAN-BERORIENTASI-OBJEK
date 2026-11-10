@echo off
setlocal enabledelayedexpansion
title FleetSumenep - Rental Car Fleet Monitoring and Interactive Bay Management

cd /d "%~dp0"

echo ==================================================================
echo  FleetSumenep CAPSTONE PROJECT (OOP P1 - P15 FULL INTEGRATION)
echo  Dual-Engine: Native JavaFX WebView Window and CLI Console Menu
echo  Mahasiswa: M. Davana Yudista Firdausy (3125522002) - A D3 TI-SM
echo ==================================================================
echo.

set "FX_LIB=lib\javafx-sdk-17.0.12\lib"
set "FX_BIN=lib\javafx-sdk-17.0.12\bin"
set "BIN_DIR=SourceCode\bin"
set "MYSQL_JAR=lib\mysql-connector-j-8.3.0.jar"

if not exist "%BIN_DIR%" mkdir "%BIN_DIR%"

echo Mengompilasi seluruh domain model, persistensi, dan antarmuka...
javac -encoding UTF-8 -d "%BIN_DIR%" -cp "%MYSQL_JAR%;." --module-path "%FX_LIB%" --add-modules javafx.controls,javafx.web SourceCode\model\exception\*.java SourceCode\model\*.java SourceCode\*.java
if errorlevel 1 goto compile_error

echo.
echo Kompilasi Berhasil (0 Error)!
echo.
echo Pilih Mode Eksekusi:
echo   [1] Antarmuka GUI Hybrid Desktop (JavaFX WebView Leaflet)
echo   [2] Antarmuka Konsol Interaktif (CLI Menu P13)
echo   [3] Jalankan Test Regression Otomatis (12 Skenario P1-P15)
echo.

set "MODE=1"
set /p "MODE=Masukkan pilihan [1/2/3, default 1]: "

if "%MODE%"=="2" goto run_cli
if "%MODE%"=="3" goto run_test
goto run_gui

:run_gui
echo.
echo Menjalankan GUI Desktop (Software Prism Rasterizer -Dprism.order=sw)...
java "-Djava.library.path=%FX_BIN%" -Dprism.order=sw -Dprism.verbose=false --module-path "%FX_LIB%" --add-modules javafx.controls,javafx.web -cp "%BIN_DIR%;%MYSQL_JAR%;." SourceCode.Launcher
goto end

:run_cli
echo.
echo Menjalankan Antarmuka CLI Interaktif...
java -cp "%BIN_DIR%;%MYSQL_JAR%;." SourceCode.CliMain
goto end

:run_test
echo.
echo Menjalankan Suite Uji Regresi Otomatis (12 Kasus Uji)...
java -cp "%BIN_DIR%;%MYSQL_JAR%;." SourceCode.TestRegressionRunner
goto end

:compile_error
echo.
echo [ERROR] Kompilasi GAGAL! Periksa kode sumber Java.
goto end

:end
echo.
pause
