
# Kill VPN

A minimal Android app that disconnects the active VPN with one tap from a Quick Settings tile.

## How it works

Android allows only one active VPN at a time. When an app establishes a new VPN interface, the previous one is disconnected automatically. Kill VPN creates a temporary VPN interface and closes it immediately, which leaves no VPN active.

## Features

- One-tap VPN disconnect from Quick Settings
- Minimal size, no third-party libraries
- Android 7.0 (API 24) and above

## Build

Option 1: Android Studio

1. Open the project folder in Android Studio.
2. Build → Build APK(s).

Option 2: Command line

```
gradle wrapper
./gradlew assembleRelease
```

Requires JDK 17 and Android SDK with platform 34.

Option 3: GitHub Actions

The included workflow (`.github/workflows/build.yml`) builds a debug APK on every push. Download it from the run's Artifacts section.

## Installation and usage

1. Install the APK and open the app.
2. Tap "Grant VPN permission" and confirm.
3. Open Quick Settings, tap edit, and add the "Kill VPN" tile.
4. Tap the tile to disconnect the active VPN.

## Limitations

- If the current VPN is set to Always-on or Lockdown, Android will not allow it to be disconnected.
- Some custom or vendor ROMs may behave differently.

## License

MIT