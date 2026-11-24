# Simple Budget

A Fudget-style personal budget app for Android.

## Features

- Simple list of income and expense entries with a running balance at the top
- Green `+` button adds income, red `−` button adds an expense
- Income total / expense total shown under the balance
- Long-press any entry to edit or delete it
- Overflow menu (⋮) in the top bar: "Delete all"
- Entries persist on the device (SharedPreferences), no accounts, no internet
- Material 3 UI, dark-text light theme

## Tech

- Kotlin, ViewBinding, RecyclerView
- Package: `com.example.simplebudget`
- minSdk 24 (Android 7.0), targetSdk 34

## Build

1. Open the `SimpleBudget` folder in Android Studio (Hedgehog or newer).
2. If Studio reports a missing Gradle wrapper, let it fix it (or run `gradle wrapper` inside the folder).
3. Let Gradle sync, then press Run with a device/emulator on Android 7+.
4. Build release APK: **Build > Build Bundle(s) / APK(s) > Build APK(s)**.

Output APK: `app/build/outputs/apk/debug/app-debug.apk` (or `release` variant). Copy it to your phone and install it (allow "install unknown apps" if prompted).

## Project layout

- `app/src/main/java/com/example/simplebudget/MainActivity.kt` — UI logic
- `app/src/main/java/com/example/simplebudget/EntryAdapter.kt` — list adapter
- `app/src/main/java/com/example/simplebudget/Entry.kt` — data model
- `app/src/main/java/com/example/simplebudget/EntryStore.kt` — persistence
- `app/src/main/res/layout/` — main screen, list item, edit dialog

## Git

The project has no `.gitignore` yet; before committing, add one ignoring `.gradle/`, `build/`, `local.properties`, and `.idea/`.
