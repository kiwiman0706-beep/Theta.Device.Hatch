# Equipment Ledger / 機器台帳

Native Java Android app. Android 11+, compile/target SDK 35, JDK17, Gradle8.13, AGP8.13.1.

See docs/使い方.md for user instructions.

## Build

1. Install Android SDK platform 35, build-tools, and JDK17.
2. Set ANDROID_HOME or create local.properties with sdk.dir.
3. Download TEPRA-Print SDK for Android 1.4.0 from the official KING JIM page after reading its terms. Place TepraPrint.jar in app/libs/ and the original ABI-specific libTepraPrint.so files in app/src/main/jniLibs/{ABI}/. Those proprietary SDK files are intentionally excluded from the source archive; they are integrated into the APK under the SDK distribution terms.
4. Run `./gradlew testDebugUnitTest assembleRelease`.

This private archive contains release-signing.jks and signing.properties so future updates can retain the same app identity. Keep the archive private. Do not publish the signing files. A changed signing certificate requires uninstalling the old app and will erase local data unless backed up first.

Core classes: MainActivity (native UI/camera/OCR), Store (encrypted SQLite records/photos/portable backups), Crypto (AES-GCM/PBKDF2), Extract (confirmed OCR candidates), Labels (raster layout/QR), Printer (official SDK network printing).

The basic offline ledger/OCR requires no external LLM API. Optional Gemini recognition uses a user-entered API key, encrypted on-device and excluded from backups. No API key is bundled in APK or source. Gemini.java implements HTTPS REST recognition; GeminiUi.java handles selected-photo consent, settings, and candidate review. Third-party ML Kit may carry its own SDK diagnostics. Device photo/print/auth flows require real-device validation.
