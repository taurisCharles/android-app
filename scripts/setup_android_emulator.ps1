$ErrorActionPreference = "Stop"

$sdk = Join-Path $env:LOCALAPPDATA "Android\Sdk"
$javaHome = "C:\Program Files\Android\Android Studio\jbr"
$cmdlineToolsUrl = "https://dl.google.com/android/repository/commandlinetools-win-15859902_latest.zip"
$cmdlineZip = Join-Path $env:TEMP "commandlinetools-win-15859902_latest.zip"
$extractDir = Join-Path $env:TEMP "android_cmdline_tools_extract"
$latestDir = Join-Path $sdk "cmdline-tools\latest"

$env:JAVA_HOME = $javaHome
$env:Path = "$javaHome\bin;$sdk\platform-tools;$latestDir\bin;$env:Path"

Write-Host "Installing Android command-line tools..."
Invoke-WebRequest -Uri $cmdlineToolsUrl -OutFile $cmdlineZip

New-Item -ItemType Directory -Force -Path (Join-Path $sdk "cmdline-tools") | Out-Null
Remove-Item -Recurse -Force $extractDir -ErrorAction SilentlyContinue
Expand-Archive -Force -Path $cmdlineZip -DestinationPath $extractDir
Remove-Item -Recurse -Force $latestDir -ErrorAction SilentlyContinue
New-Item -ItemType Directory -Force -Path $latestDir | Out-Null
Move-Item -Force (Join-Path $extractDir "cmdline-tools\*") $latestDir
Remove-Item -Recurse -Force $extractDir

$sdkManager = Join-Path $latestDir "bin\sdkmanager.bat"
$avdManager = Join-Path $latestDir "bin\avdmanager.bat"

Write-Host "Accepting Android SDK licenses..."
1..20 | ForEach-Object { "y" } | & $sdkManager --licenses --sdk_root=$sdk

Write-Host "Installing emulator and API 35 Google APIs x86_64 image..."
& $sdkManager --sdk_root=$sdk "emulator" "platform-tools" "platforms;android-35" "system-images;android-35;google_apis;x86_64"

Write-Host "Creating Snakerito_API_35 AVD..."
"no" | & $avdManager create avd `
    --force `
    --name "Snakerito_API_35" `
    --package "system-images;android-35;google_apis;x86_64" `
    --device "medium_phone"

Write-Host "Android emulator setup complete."
Write-Host "AVD name: Snakerito_API_35"
