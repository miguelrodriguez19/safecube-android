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

## GitHub Actions CI

The [`CI` workflow](../../.github/workflows/ci.yml) runs for every pull request, every push to
`main`, and manual `workflow_dispatch` executions. It exposes three independent status checks:

```text
CI / version-guard
CI / verify
CI / instrumented-smoke
```

`version-guard` compares the current `version.properties` with the base version and requires a
strictly greater SemVer `VERSION_NAME` and `VERSION_CODE`. `verify` and `instrumented-smoke` start
only after that guard passes. The `verify` job runs `./gradlew --no-daemon ciVerify` on Ubuntu with
Temurin JDK 21. The workflow declares only `contents: read`, does not consume repository secrets,
and is safe for pull requests from forks. Newer executions cancel obsolete runs for the same pull
request or Git ref.

Test XML/HTML, Android Lint and Kover coverage reports are uploaded with `if: always()`, including
when a gate fails. The unsigned release APK built by `ciVerify` is deliberately excluded from CI
artifacts because it is not publicable.

The separate `instrumented-smoke` job has a 30-minute timeout and runs only
`MainActivitySmokeTest` on a clean Gradle Managed Device named `pixel2Api30` (Pixel 2, API 30,
`aosp-atd`, x86_64). It explicitly enables KVM and uses SwiftShader because GitHub Actions runners
do not offer hardware rendering. `testOptions.animationsDisabled` keeps animations disabled. On
failure, CI retains managed-device test results, the available logcat output and a screenshot when
the failed device remains available to `adb`.

### Required protection for `main`

Configure a branch ruleset or branch protection rule targeting `main`, and make it active with:

1. Require a pull request before merging; direct pushes to `main` must not be allowed.
2. Require at least one approval, dismiss stale approvals after new commits, and require review
   from Code Owners when a `CODEOWNERS` file is introduced.
3. Require status checks before merging, add the exact checks `CI / version-guard`, `CI / verify`,
   `CI / instrumented-smoke`, `Dependency Review / dependency-review` and
   `CodeQL / Analyze (java-kotlin)`, and require the branch
   to be up to date before merging.
4. Require all review conversations to be resolved.
5. Block force pushes and branch deletion.
6. Apply the rule to administrators and do not grant routine bypass access. Keep emergency bypass
   limited to a named owner and audit every use.

The `push` execution on `main` validates the resulting merge commit but does not replace the
required pull-request check. Release and publication workflows may run after merge, but they must
remain separate from this unprivileged CI workflow.

## Dependency updates and review

[Dependabot](../../.github/dependabot.yml) checks Gradle and GitHub Actions dependencies weekly.
Each ecosystem is limited to five open version-update pull requests. Compatible minor and patch
updates are grouped; major updates are always opened separately for explicit review. Dependabot
does not auto-merge any pull request: every update must satisfy the same CI checks and normal
branch-protection reviews without repository secrets.

The [`Dependency Review` workflow](../../.github/workflows/dependency-review.yml) runs on pull
requests to `main` and exposes this required check:

```text
Dependency Review / dependency-review
```

It declares only `contents: read` and fails when a dependency change introduces a vulnerability
with `high` or `critical` severity. License policy and automatic merging remain out of scope.

## CodeQL static security analysis

The [`CodeQL` workflow](../../.github/workflows/codeql.yml) runs for pull requests to `main`,
pushes to `main`, every Monday at 03:23 UTC, and manual dispatches. Its required check is:

```text
CodeQL / Analyze (java-kotlin)
```

It initializes `java-kotlin` in manual build mode and runs `:app:assembleDebug`, so CodeQL
observes the Kotlin sources compiled through the Android Gradle build. The workflow grants only
`contents: read` and `security-events: write`, and cancels obsolete analyses for the same pull
request or Git ref. Results are published in **Security → Code scanning alerts**; a configuration
or build failure fails the check instead of publishing an incomplete analysis.

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

Run the SafeCube smoke suite on a connected API 30 emulator:

```bash
./gradlew :app:connectedDebugAndroidTest
```

Requirements:

- An API 30 emulator must be running and visible through `adb devices`.
- The suite uses Android Test Orchestrator and clears application data between tests.
- The smoke tests start `MainActivity`, assert Welcome, and navigate to Login without submitting
  credentials or contacting the backend.

## Current baseline

- `app/src/test/.../ExampleUnitTest.kt` provides a JVM smoke test.
- `app/src/androidTest/.../MainActivitySmokeTest.kt` validates real startup and Welcome → Login navigation on API 30.
- `core/network/src/test/.../NetworkClientFactoryTest.kt` validates HTTP layer with `MockWebServer`.
