@echo off
REM Install Gradle 8.7 for Android
set GRADLE_ZIP=%TEMP%\gradle-8.7-bin.zip
set GRADLE_HOME=d:\App Android\Family\.gradle\gradle-8.7
set GRADLE_URL=https://services.gradle.org/distributions/gradle-8.7-bin.zip
set GRADLE_EXTRACT=%TEMP%\gradle-87-extract

if not exist "%GRADLE_ZIP%" (
    echo Downloading Gradle 8.7...
    powershell -NoProfile -Command "
    [Net.ServicePointManager]::SecurityProtocol = [Net.SecurityProtocolType]::Tls12
    $ProgressPreference = 'SilentlyContinue'
    Invoke-WebRequest -Uri '%GRADLE_URL%' -OutFile '%GRADLE_ZIP%' -UseBasicParsing
    " || exit /b 1
)

if not exist "%GRADLE_HOME%" (
    echo Extracting Gradle 8.7...
    if exist "%GRADLE_EXTRACT%" rmdir /s /q "%GRADLE_EXTRACT%"
    mkdir "%GRADLE_EXTRACT%"
    powershell -NoProfile -Command "
    Expand-Archive -Path '%GRADLE_ZIP%' -DestinationPath '%GRADLE_EXTRACT%' -Force
    Move-Item '%GRADLE_EXTRACT%\gradle-8.7' '%GRADLE_HOME%' -Force
    " || exit /b 1
)

echo Gradle 8.7 ready
"%GRADLE_HOME%\bin\gradle.bat" --version
exit /b 0
