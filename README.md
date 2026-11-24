# Kill VPN

A minimal Android app that disconnects the active VPN with a tap on a Quick Settings tile.

## How it works

Android allows only one active VPN at a time. When an app establishes a new VPN interface, the previous one is disconnected automatically. Kill VPN creates a temporary VPN interface and closes it immediately, which leaves no VPN active.

## Features

- Quick Settings tile: tap once to arm, tap again within 3 seconds to disconnect (no accidental disconnects while swiping the panel down)
- Feedback after each run: "VPN disconnected", "No active VPN found", or a hint when the VPN could not be disconnected
- English and Persian (فارسی), selectable in the app, with full RTL support
- Light and dark theme
- Android 13+: add the tile to Quick Settings with one tap from inside the app
- Tiny: no third-party libraries, minified with R8
- Android 7.0 (API 24) and above

## Privacy

Kill VPN has no internet permission, collects no data and has no analytics. It only requests the VPN permission (needed to replace the current VPN) and network state access (to check whether a VPN is active).

## Install

Download the latest APK from the [Releases](../../releases) page, install it and open the app once.

## Usage

1. Open the app and tap "Grant VPN permission", then confirm.
2. Add the "Kill VPN" tile to Quick Settings (use the button in the app on Android 13+, or edit the panel manually).
3. Tap the tile twice to disconnect the active VPN.

## Build

Android Studio: open the project folder, then Build > Build APK(s).

Command line (JDK 17 and Android SDK with platform 34):

    gradle wrapper
    ./gradlew assembleRelease

GitHub Actions: every push builds a debug APK (Actions tab > run > Artifacts).

## Signed releases

Create a release keystore once and keep a backup of it. If you lose it, you can no longer update installed copies.

    keytool -genkeypair -v -keystore release.jks -alias killvpn -keyalg RSA -keysize 4096 -validity 10000
    base64 -w0 release.jks

In the repository go to Settings > Secrets and variables > Actions and add:

- `KEYSTORE_BASE64`: the output of the base64 command
- `KEYSTORE_PASSWORD`: the keystore password
- `KEY_ALIAS`: `killvpn`
- `KEY_PASSWORD`: the key password

Then publish a release by pushing a version tag:

    git tag v1.2.0
    git push origin v1.2.0

The workflow builds a signed, minified APK and attaches it, with a SHA-256 checksum, to a GitHub Release. For local signed builds, put `storeFile`, `storePassword`, `keyAlias` and `keyPassword` in a git-ignored `keystore.properties` file in the project root.

## Limitations

- If the current VPN is set to Always-on or Lockdown, Android will not allow it to be disconnected. Turn off "Always-on VPN" in the system settings first.
- Some custom or vendor ROMs may behave differently.

## License

MIT
