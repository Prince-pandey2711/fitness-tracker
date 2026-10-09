@echo off
title FitTrack Pro - Online Fitness Tracking Application
echo ========================================================
echo        Starting FitTrack Pro Server (Spring Boot)
echo ========================================================
echo.
cd /d "%~dp0"

echo [1/2] Opening application in browser at http://localhost:8080...
start "" http://localhost:8080

echo [2/2] Starting backend server...
echo (Keep this window open while using the application)
echo.
call .\mvnw.cmd spring-boot:run

pause
