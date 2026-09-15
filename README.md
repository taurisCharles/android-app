# Snakerito

Snakerito is a small Android snake game built with Kotlin and Jetpack Compose. Swipe, or use the on-screen controls, to steer the burrito toward salsa without hitting the walls or yourself.

## Current Status

- Builds successfully with Gradle.
- Debug APK output: `app/build/outputs/apk/debug/app-debug.apk`
- Requires an Android emulator or USB-connected Android device to run.

## Build

From the project folder in Command Prompt or PowerShell:

```bat
set "JAVA_HOME=C:\Program Files\Android\Android Studio\jbr"
gradlew.bat assembleDebug
```

From WSL:

```sh
cmd.exe /C "set JAVA_HOME=C:\Program Files\Android\Android Studio\jbr&& gradlew.bat assembleDebug"
```

## Run

The easiest path is Android Studio:

1. Open the project folder.
2. Install an emulator from Tools > Device Manager if one is not already available.
3. Click Run.

If a device is already connected and authorized, install the debug APK with:

```bat
%LOCALAPPDATA%\Android\Sdk\platform-tools\adb.exe install -r app\build\outputs\apk\debug\app-debug.apk
```
