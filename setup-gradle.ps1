param([string]$Version = "8.4")

$gradle_url = "https://services.gradle.org/distributions/gradle-$Version-bin.zip"
$gradle_home = "$PSScriptRoot\.gradle\gradle-$Version"
$gradle_zip = "$env:TEMP\gradle-$Version-bin.zip"

Write-Host "Installing Gradle $Version..."

# Download
if (!(Test-Path $gradle_zip)) {
    Write-Host "Downloading Gradle..."
    [Net.ServicePointManager]::SecurityProtocol = [Net.SecurityProtocolType]::Tls12
    $ProgressPreference = 'SilentlyContinue'
    Invoke-WebRequest -Uri $gradle_url -OutFile $gradle_zip -UseBasicParsing
}

# Extract
if (!(Test-Path $gradle_home)) {
    Write-Host "Extracting Gradle..."
    $extract_temp = "$env:TEMP\gradle"
    if (Test-Path $extract_temp) { Remove-Item $extract_temp -Recurse -Force }
    Expand-Archive -Path $gradle_zip -DestinationPath $extract_temp -Force
    New-Item -ItemType Directory -Path (Split-Path $gradle_home) -Force | Out-Null
    Move-Item "$extract_temp\gradle-$Version" $gradle_home -Force
}

# Set environment
$env:GRADLE_HOME = $gradle_home
$env:PATH = "$gradle_home\bin;$env:PATH"

Write-Host "Gradle installed at: $gradle_home"
Write-Host "`nTesting Gradle..."
& "$gradle_home\bin\gradle.bat" --version
