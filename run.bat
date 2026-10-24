@echo off
echo ========================================================
echo   Running Hospital Management System Test Suite
echo ========================================================
set JAVA_EXE="C:\Program Files\Java\jdk-26.0.2.1\bin\java.exe"
if not exist %JAVA_EXE% set JAVA_EXE=java

%JAVA_EXE% -cp "target\classes;lib\mysql-connector-j-8.3.0.jar" com.hospital.Main
pause
