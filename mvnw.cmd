@echo off
setlocal

set "BASE_DIR=%~dp0"
set "MAVEN_VERSION=3.9.16"
set "MAVEN_HOME=%BASE_DIR%.mvn\apache-maven-%MAVEN_VERSION%"
set "MAVEN_BIN=%MAVEN_HOME%\bin\mvn.cmd"
set "MAVEN_ZIP=%BASE_DIR%.mvn\wrapper\apache-maven-%MAVEN_VERSION%-bin.zip"
set "MAVEN_URL=https://dlcdn.apache.org/maven/maven-3/%MAVEN_VERSION%/binaries/apache-maven-%MAVEN_VERSION%-bin.zip"

if exist "%ProgramFiles%\Java\latest\jdk-25\bin\javac.exe" (
    set "JAVA_HOME=%ProgramFiles%\Java\latest\jdk-25"
)

if exist "%ProgramFiles%\Java\jdk-25.0.3\bin\javac.exe" (
    set "JAVA_HOME=%ProgramFiles%\Java\jdk-25.0.3"
)

if not exist "%MAVEN_BIN%" (
    echo Downloading Apache Maven %MAVEN_VERSION%...
    powershell -NoProfile -ExecutionPolicy Bypass -Command ^
        "$ErrorActionPreference = 'Stop';" ^
        "$zip = '%MAVEN_ZIP%';" ^
        "$dest = '%BASE_DIR%.mvn';" ^
        "New-Item -ItemType Directory -Force -Path (Split-Path -Parent $zip) | Out-Null;" ^
        "Invoke-WebRequest -Uri '%MAVEN_URL%' -OutFile $zip;" ^
        "Expand-Archive -LiteralPath $zip -DestinationPath $dest -Force;"
    if errorlevel 1 exit /b 1
)

"%MAVEN_BIN%" %*
endlocal
