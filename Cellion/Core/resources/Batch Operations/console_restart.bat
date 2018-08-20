@echo off
@title Restart REXION
Color 4F

cd "%~dp0"

echo Restarting up the REXION realm...
start "cd" console_stop.bat
start "cd" console_start.bat
timeout /t 1 >nul

Taskkill /FI "WINDOWTITLE eq Stop_Rexion"
Taskkill /FI "WINDOWTITLE eq Start_Rexion"
Taskkill /FI "WINDOWTITLE eq Select Nox: Rexion"


