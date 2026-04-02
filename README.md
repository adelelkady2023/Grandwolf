# Grand Wolf Security Android App

A modern Android app (Jetpack Compose) for **Grand Wolf Security & Guarding** to present company services and let users contact the team directly.

## Features
- Elegant mobile-first UI/UX with card-based layout.
- Company profile, mission, vision, and policy sections.
- Full services catalog.
- Direct **Contact Us** actions:
  - Open phone dialer.
  - Open email composer.
  - Open WhatsApp chat with a pre-filled message.
- **Service Request Form** to collect user requirements and:
  - send directly to `info@grandwolfeg.com` via email intent.
  - optionally submit JSON to your backend endpoint.
- Remote logo loading from:
  - `https://grandwolfeg.com/wp-content/uploads/2026/03/Grand-wolf-Final-2048x861.png`

## Backend integration
In `MainActivity.kt`, set:
- `BACKEND_ENDPOINT = "https://api.yourdomain.com/service-requests"`

If left empty, the app still sends the request through email.

## Project setup
1. Install Android Studio (latest stable).
2. Open this folder as a project.
3. Let Gradle sync.
4. Run on a physical Android device or emulator.

## Build release AAB for Google Play

### 1) Create signing key (one time)
```bash
keytool -genkeypair -v -keystore grandwolf-upload-key.jks -keyalg RSA -keysize 2048 -validity 10000 -alias grandwolf_upload
```

### 2) Configure signing in `app/build.gradle.kts`
Add your signing config under `android {}` (replace placeholders with your values).

### 3) Build AAB
```bash
./gradlew bundleRelease
```

Generated file:
- `app/build/outputs/bundle/release/app-release.aab`
