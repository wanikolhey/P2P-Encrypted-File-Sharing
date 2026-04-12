@echo off
REM P2P File Sharing - GUI Launch Script for Windows

echo P2P File Sharing - Launching GUI...
echo Building project...

REM Change to app directory
cd /d "%~dp0app"

REM Build the project
call gradle build -q

if errorlevel 1 (
    echo Build failed. Please check errors above.
    exit /b 1
)

echo Starting GUI application...

REM Run the GUI
call gradle runGui

echo GUI application closed.
pause
