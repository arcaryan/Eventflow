@echo off
title EventFlow automatic setup
pushd "%~dp0"
powershell.exe -NoLogo -NoProfile -ExecutionPolicy Bypass -File "%~dp0setup-and-run.ps1"
if errorlevel 1 (
    echo.
    echo EventFlow could not be started. Read the error above.
    pause
)
popd