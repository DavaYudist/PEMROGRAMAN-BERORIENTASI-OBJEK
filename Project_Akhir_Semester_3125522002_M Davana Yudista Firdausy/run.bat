@echo off
title FleetSumenep - Rental Car Fleet Monitoring & Interactive Bay Management
echo ==================================================================
echo  FleetSumenep CAPSTONE PROJECT (OOP P1 - P15 FULL INTEGRATION)
echo  Dual-Engine: Native JavaFX WebView Window & CLI Console Menu
echo  Mahasiswa: M. Davana Yudista Firdausy (3125522002) - A D3 TI-SM
echo ==================================================================

cd /d "%~dp0"
set FX_LIB=lib\javafx-sdk-17.0.12\lib
set FX_BIN=lib\javafx-sdk-17.0.12\bin
set BIN_DIR=SourceCode\bin
set MYSQL_JAR=lib\mysql-connector-j-8.3.0.jar

if not exist "%BIN_DIR%" mkdir "%BIN_DIR%"

echo.
echo Mengompilasi seluruh domain model, persistensi, dan antarmuka...
javac -encoding UTF-8 -d "%BIN_DIR%" -cp "%MYSQL_JAR%;." --module-path "%FX_LIB%" --add-modules javafx.controls,javafx.web SourceCode\model\exception\*.java SourceCode\model\*.java SourceCode\Main.java SourceCode\Launcher.java SourceCode\CliMain.java SourceCode\TestRegressionRunner.java SourceCode\TestMySQLPersistence.java SourceCode\TestP10Runner.java

if errorlevel 1 (
    echo.
    echo Kompilasi GAGAL!
    pause
    exit /b %errorlevel%
)

echo.
echo Kompilasi Berhasil (0 Error)!
echo Pilih Mode Eksekusi:
echo   [1] Buka Antarmuka GUI Hybrid Desktop (JavaFX WebView Leaflet)
echo   [2] Buka Antarmuka Konsol Interaktif (CLI Menu P13)
echo   [3] Jalankan Test Regression Otomatis (12 Skenario P1-P15)
set /p mode="Masukkan pilihan [1/2/3, default 1]: "

if "%mode%"=="2" (
    java -cp "%BIN_DIR%;%MYSQL_JAR%;." SourceCode.CliMain
) else if "%mode%"=="3" (
    java -cp "%BIN_DIR%;%MYSQL_JAR%;." SourceCode.TestRegressionRunner
) else (
    echo Membuka antarmuka desktop native (Software Prism Rasterizer -Dprism.order=sw)...
    java "-Djava.library.path=%FX_BIN%" -Dprism.order=sw -Dprism.verbose=false --module-path "%FX_LIB%" --add-modules javafx.controls,javafx.web -cp "%BIN_DIR%;%MYSQL_JAR%;." SourceCode.Launcher
)
pause
