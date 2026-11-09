@echo off
setlocal
cd /d "%~dp0"

echo [1/2] Mengompilasi Modul 12...
if not exist "SourceCode\bin" mkdir "SourceCode\bin"
javac -encoding UTF-8 -d SourceCode\bin SourceCode\model\*.java SourceCode\repository\*.java SourceCode\*.java
if %ERRORLEVEL% NEQ 0 (
    echo [ERROR] Kompilasi gagal!
    pause
    exit /b %ERRORLEVEL%
)

echo [2/2] Menjalankan Test Runner P12...
java -cp SourceCode\bin SourceCode.TestP12Runner
pause
