# Testing setup

## Unit tests (JVM)

Run all unit tests:

```bash
./gradlew test
```

## Instrumented tests (device/emulator)

Run all connected Android tests:

```bash
./gradlew connectedAndroidTest
```

Requirements:
- Android emulator running or physical device connected.
- USB debugging enabled for physical devices.

## Current baseline

- `app/src/test/.../ExampleUnitTest.kt` provides a JVM smoke test.
- `app/src/androidTest/.../ExampleInstrumentedTest.kt` validates app context.
- `app/src/androidTest/.../MainActivityComposeTest.kt` validates Compose UI rendering.
- `core/network/src/test/.../NetworkClientFactoryTest.kt` validates HTTP layer with `MockWebServer`.
