# Testing setup

| Spec ID | Status | Owner | Last reviewed | Supersedes | Related ADRs |
| --- | --- | --- | --- | --- | --- |
| `SPEC-TESTING` | `APPROVED` | `quality` | `2026-07-29` | `N/A` | `N/A` |

## Unit tests (JVM)

Run all unit tests:

```bash
./gradlew test
```

## Test coverage (Kover)

Run aggregated HTML coverage report:

```bash
./gradlew koverHtmlReport
```

HTML report location:

`build/reports/kover/html/index.html`

Run aggregated XML report (CI-friendly):

```bash
./gradlew koverXmlReport
```

Run full verification flow (equivalent to `mvn clean verify`):

```bash
./gradlew clean check
```

This runs tests, lint, generates Kover HTML/XML reports, and enforces Kover thresholds.

## Canonical CI and release gates

The root Gradle tasks below are the canonical commands for local validation and GitHub Actions.
Their dependency graph lives in `build.gradle.kts`; workflows and documentation must invoke these
tasks instead of maintaining a second list of checks.

Run the CI gate locally or in a pull request:

```bash
./gradlew ciVerify
```

`ciVerify` validates the version, runs the unit-test and Kover coverage gate, runs debug lint,
executes `VaultSyncOpenApiContractTest`, and assembles `:app:assembleRelease` without requiring
release-signing secrets. It does not publish an artifact and it does not verify the production
keystore.

Run the release-code gate before publishing:

```bash
./gradlew releaseVerify
```

`releaseVerify` runs `ciVerify` and release lint. It still only validates code and does not publish
or sign an APK. The protected publication workflow must verify the production signing configuration
separately, before invoking this gate:

```bash
./gradlew verifyReleaseSigningConfiguration releaseVerify
```

`verifyReleaseSigningConfiguration` is intentionally outside `ciVerify`, so pull requests can run
the CI gate without access to the release keystore or its credentials.

If you want a Maven-like verify flow focused on unit tests + coverage only:

```bash
./gradlew clean verifyCoverage
```

This runs tests, generates Kover HTML/XML reports, and enforces Kover thresholds (without lint).

You can also run explicit Kover verification:

```bash
./gradlew clean test koverHtmlReport koverXmlReport koverVerify
```

XML report location:

`build/reports/kover/report.xml`

Module report locations:

- `core/auth/build/reports/kover/html/index.html`
- `core/network/build/reports/kover/html/index.html`
- `core/crypto/build/reports/kover/html/index.html`
- `core/storage/build/reports/kover/html/index.html`

Current baseline modules included in coverage aggregation:

- `core:auth`
- `core:network`
- `core:crypto`
- `core:storage`

Notes:

- Generated classes/packages (Hilt, Room, OpenAPI generated code, etc.) are excluded from coverage metrics via Kover filters.
- Coverage uses JVM unit tests (`test` tasks). `androidTest` coverage is out of scope for now.

Coverage baseline snapshot (March 7, 2026):

- Aggregated (`:`): line `75.0%` (`261/348`)
- `core:auth`: line `77.4%` (`175/226`)
- `core:network`: line `96.6%` (`86/89`)
- `core:crypto`: line `0.0%` (`0/28`)
- `core:storage`: line `0.0%` (`0/5`)

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
