# Snakerito Android Runbook

Quick steps to build and run Snakerito on Android.

## 1. Open The Project

Open Android Studio, then choose:

```text
path\to\Snakerito
```

Let Gradle sync finish before running the app.

## 2. Use Android Studio's JDK

This project builds with the JDK bundled with Android Studio.

If building from Command Prompt or PowerShell:

```bat
set "JAVA_HOME=C:\Program Files\Android\Android Studio\jbr"
gradlew.bat assembleDebug
```

The debug APK will be created at:

```text
app\build\outputs\apk\debug\app-debug.apk
```

## 3. Create An Emulator

Current machine note:

This machine has the Android SDK command-line tools and emulator package installed, plus API 35 images. Two AVDs were created while testing:

```text
Snakerito_API_35   API 35 Google APIs arm64-v8a
Snakerito_x86_35   API 35 Google APIs x86_64
```

The local emulator is not currently booting on this machine:

- `arm64-v8a` image: rejected because the installed emulator runs as an x86_64 host process.
- `x86_64` image: requires hardware virtualization acceleration, which this machine reports as unavailable.

The practical run path on this machine is a physical Android device over USB.

In Android Studio:

1. Go to Tools > Device Manager.
2. Click Create Device.
3. Pick a phone, such as Pixel 6.
4. Choose an Android system image. API 35 is a good match for this project.
5. Download the image if prompted.
6. Finish setup and start the emulator.

Once the emulator is running, click the green Run button in Android Studio.

## 4. Run On A Physical Android Phone

On the phone:

1. Open Settings > About phone.
2. Tap Build number 7 times to enable Developer options.
3. Open Developer options.
4. Enable USB debugging.
5. Plug the phone into the computer.
6. Accept the USB debugging prompt on the phone.

Then run from Android Studio, or install the APK manually:

```bat
%LOCALAPPDATA%\Android\Sdk\platform-tools\adb.exe install -r app\build\outputs\apk\debug\app-debug.apk
```

## 5. Common Fixes

If Gradle says Java is missing, set `JAVA_HOME`:

```bat
set "JAVA_HOME=C:\Program Files\Android\Android Studio\jbr"
```

If no devices appear, check:

```bat
%LOCALAPPDATA%\Android\Sdk\platform-tools\adb.exe devices
```

If the emulator option is missing, install it in Android Studio:

```text
Tools > SDK Manager > SDK Tools > Android Emulator
```

Or use the setup scripts in this repo:

```bat
powershell -NoProfile -ExecutionPolicy Bypass -File scripts\setup_android_emulator.ps1
powershell -NoProfile -ExecutionPolicy Bypass -File scripts\create_snakerito_avd.ps1
```

If Gradle sync fails, try:

```bat
gradlew.bat --stop
gradlew.bat clean assembleDebug
```
