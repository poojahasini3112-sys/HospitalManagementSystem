@echo off
echo ========================================================
echo   Hospital Management System | Java Swing + MySQL
echo ========================================================
set JAVA_EXE="C:\Program Files\Java\jdk-26.0.2.1\bin\java.exe"
if not exist %JAVA_EXE% set JAVA_EXE=java

if "%1"=="test" (
    echo Running Backend Integration Test Suite...
    %JAVA_EXE% -cp "target\classes;lib\mysql-connector-j-8.3.0.jar" com.hospital.Main
) else (
    echo Launching Java Swing Graphical User Interface...
    start "" %JAVA_EXE% -cp "target\classes;lib\mysql-connector-j-8.3.0.jar" com.hospital.ui.HospitalDashboard
)
