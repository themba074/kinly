# Kinly Android

Feature 0B established a native Kotlin and Jetpack Compose shell using the
Ocean Sky design direction and a non-sensitive backend readiness state. Feature
0D adds a debug-only native UI showcase and viewing workflow. Feature UI1 adds
a consent-aware Home preview inside that showcase. UI2 adds adult-owned
sharing-mode states, and UI3 adds circle and adult-membership states. All remain
synthetic and debug-only.
UI4.1 adds synthetic Places states without maps, coordinates, permissions,
notifications, or saved place behaviour.

## Prerequisites

- JDK 17 or newer;
- Android SDK Platform 36 and Build Tools 35.0.0; and
- the backend and local services described in `../backend/README.md` when
  testing the available state.

The approved minimum Android version is API 26. The support policy and required
physical-device matrix are recorded in
`../docs/decisions/0001-supported-platforms-and-device-matrix.md`.

## Build and test

From `android/` on Windows:

```powershell
$env:ANDROID_HOME = "$env:LOCALAPPDATA\Android\Sdk"
.\gradlew.bat testDebugUnitTest lintDebug assembleDebug
```

Run connected Compose tests when an emulator or device is available:

```powershell
.\gradlew.bat connectedDebugAndroidTest --no-configuration-cache
```

The test runtime pins Espresso 3.7.0 because the Compose test artifact otherwise
selects an older transitive version that is incompatible with Android 16 input
injection.

The debug app calls
`http://10.0.2.2:8080/actuator/health/readiness`, which reaches the development
machine from the standard Android emulator. Override it for another development
host without editing source:

```powershell
.\gradlew.bat assembleDebug -PKINLY_BACKEND_READINESS_URL=http://192.0.2.10:8080/actuator/health/readiness
```

Debug builds permit cleartext traffic only to support local development.
Release builds disable cleartext traffic and use a deliberately invalid HTTPS
placeholder until a deployed environment is separately approved.

## View the app and UI showcase

The debug APK is written to
`app/build/outputs/apk/debug/app-debug.apk`. Install it on an attached Android
device or emulator from `android/`:

```powershell
$env:ANDROID_HOME = "$env:LOCALAPPDATA\Android\Sdk"
.\gradlew.bat assembleDebug
& "$env:ANDROID_HOME\platform-tools\adb.exe" install -r `
  ".\app\build\outputs\apk\debug\app-debug.apk"
```

The debug build installs two launcher entries:

- **Kinly** opens the walking-skeleton app and its real backend readiness state.
- **Kinly UI Showcase** opens synthetic Ocean Sky components, state cards, and
  navigation without performing product actions. Select **Open Home preview**
  to view no-circle, circle, and unavailable Home states. In the circle state,
  the cards demonstrate paused, precise/fresh, approximate, and stale/offline
  sharing language.
- Select **Open sharing controls preview** to compare paused, approximate,
  precise, timed, and unavailable consent states. The controls change only
  synthetic preview state and never save a setting.
- Select **Open circle and membership preview** to compare no-circle, private
  circle, pending/expired invitation, and unavailable states. It performs no
  circle, invitation, role, or membership action.
- Select **Open Places preview** to compare loading, no-places, overview,
  uncertain/delayed, offline, and unavailable states. It uses no map, address,
  coordinate, location permission, network service, or alert delivery.

The showcase can also be opened directly:

```powershell
& "$env:ANDROID_HOME\platform-tools\adb.exe" shell am start `
  -n com.kinly.app/.ui.UiShowcaseActivity
```

For a physical phone, enable developer options and USB debugging before using
ADB, or transfer the APK and approve installation from that file source. The
default readiness URL is emulator-specific, so the main Kinly entry may
truthfully show `Unavailable` on a phone. The UI Showcase does not need a
backend or network connection.

The showcase activity, Home, sharing, circle, and Places previews, and their
synthetic fixtures exist only in debug sources. Release manifests do not
contain a showcase launcher or activity.

## Feature boundary

This app does not authenticate, request sensitive permissions, collect or show
location, register for push, store member data, or implement safety behaviour.
The non-Home tabs explicitly state that they are unavailable.
