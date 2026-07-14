# Currant [Black] Atlas — Android App

A native Android wrapper for the Quantitative Visualization Atlas Fieldbook Edition.

## What is included

- Fully offline atlas content
- Persistent notes, bookmarks, learning paths, mastery, review queue, and fieldbook
- Android file picker support for Markdown and JSON fieldbook exports
- External source links open in the user's browser
- Adaptive and monochrome Currant [Black] launcher icons
- Android 12+ splash screen
- Edge-to-edge system-bar support
- Hardware Back closes the menu before leaving the app
- No analytics, ads, account, or cloud dependency

## Install through Android Studio

1. Open this folder in Android Studio.
2. Allow Gradle to sync and install any requested Android SDK components.
3. Enable **Developer options > USB debugging** on the Galaxy.
4. Connect the phone by USB and approve the debugging prompt.
5. Select the phone in Android Studio and press **Run**.

To create a shareable APK, use **Build > Build Bundle(s) / APK(s) > Build APK(s)**.
The debug APK will be under:

`app/build/outputs/apk/debug/app-debug.apk`

## Build automatically with GitHub Actions

Push the project to a GitHub repository. The included workflow builds a debug APK on
every push and can also be started manually from the **Actions** tab. Download the
`Currant-Black-Atlas-APK` artifact after the workflow finishes.

## App identity

- Package: `com.derobertis.currantblackatlas`
- Minimum Android: API 26
- Target Android: API 35
- Version: 1.0.0

## Updating the atlas later

Replace:

`app/src/main/assets/index.html`

with the newer app-patched HTML edition, then rebuild. The native shell does not need
to be rewritten when the educational content expands.
