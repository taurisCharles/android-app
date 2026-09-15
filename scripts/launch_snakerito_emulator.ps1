$ErrorActionPreference = "Stop"

$emulator = Join-Path $env:LOCALAPPDATA "Android\Sdk\emulator\emulator.exe"
Start-Process -FilePath $emulator -ArgumentList @("-avd", "Snakerito_API_35")
