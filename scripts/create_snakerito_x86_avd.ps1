$ErrorActionPreference = "Stop"

$sdk = Join-Path $env:LOCALAPPDATA "Android\Sdk"
$javaHome = "C:\Program Files\Android\Android Studio\jbr"
$avdManager = Join-Path $sdk "cmdline-tools\latest\bin\avdmanager.bat"

$env:JAVA_HOME = $javaHome
$env:Path = "$javaHome\bin;$sdk\platform-tools;$sdk\emulator;$env:Path"

"no" | & $avdManager create avd `
    --force `
    --name "Snakerito_x86_35" `
    --package "system-images;android-35;google_apis;x86_64" `
    --device "medium_phone"
