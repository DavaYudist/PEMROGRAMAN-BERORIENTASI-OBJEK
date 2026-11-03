@echo off
title FleetSumenep DESKTOP - Native Hybrid Window
echo ==================================================================
echo  FleetSumenep DESKTOP - Rental Car Fleet Monitoring MVP
echo  Native Window (JavaFX WebView + Leaflet Map + Parking Lot View)
echo  Mahasiswa: M. Davana Yudista Firdausy (3125522002)
echo ==================================================================

cd /d "%~dp0"
set FX_LIB=lib\javafx-sdk-17.0.12\lib
set FX_BIN=lib\javafx-sdk-17.0.12\bin
set BIN_DIR=SourceCode\bin
set MYSQL_JAR=lib\mysql-connector-j-8.3.0.jar

if not exist "%BIN_DIR%" mkdir "%BIN_DIR%"

echo.
echo Mengompilasi domain model dan antarmuka desktop...
javac -encoding UTF-8 -d "%BIN_DIR%" -cp "%MYSQL_JAR%;." --module-path "%FX_LIB%" --add-modules javafx.controls,javafx.web SourceCode\model\exception\*.java SourceCode\model\*.java SourceCode\Main.java SourceCode\Launcher.java SourceCode\TestP10Runner.java

if errorlevel 1 (
    echo.
    echo Kompilasi GAGAL!
    pause
    exit /b %errorlevel%
)

echo.
echo Kompilasi Berhasil (0 Error)!
echo Membuka jendela aplikasi desktop native (Software Prism Rasterizer -Dprism.order=sw)...
echo.

java "-Djava.library.path=%FX_BIN%" -Dprism.order=sw -Dprism.verbose=false --module-path "%FX_LIB%" --add-modules javafx.controls,javafx.web -cp "%BIN_DIR%;%MYSQL_JAR%;." SourceCode.Launcher
pause
