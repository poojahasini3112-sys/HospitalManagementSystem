@echo off
echo ============================================================
echo   MediTrack Hospital Web Application
echo   Backend: Java 17+ ^| Database: MySQL ^| Middleware: JDBC
echo ============================================================
set JAVA_EXE="C:\Program Files\Java\jdk-26.0.2.1\bin\java.exe"
if not exist %JAVA_EXE% set JAVA_EXE=java

echo Compiling Java Web Server...
dir /s /b src\main\java\*.java > sources.txt
"C:\Program Files\Java\jdk-26.0.2.1\bin\javac.exe" -encoding UTF-8 -cp "lib\mysql-connector-j-8.3.0.jar" -d "target\classes" @sources.txt
del sources.txt

echo Starting Java HTTP Server on http://localhost:8080 ...
start "" http://localhost:8080
%JAVA_EXE% -cp "target\classes;lib\mysql-connector-j-8.3.0.jar" com.hospital.web.HospitalWebServer
pause
