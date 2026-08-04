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

Every GitHub Actions job that invokes Gradle checks out the repository and then uses the local
[`setup-android-gradle`](../../.github/actions/setup-android-gradle/action.yml) composite action.
That action pins the supported Temurin JDK and `gradle/actions/setup-gradle` versions in one place.
The Gradle action restores its own Gradle User Home cache; workflows must not add a competing
`actions/cache` entry for `~/.gradle` or enable the Gradle cache in `actions/setup-java`.

Project-wide task output caching is enabled with `org.gradle.caching=true`. Cacheable Android,
Kotlin, Java and test tasks may therefore reuse outputs restored by `setup-gradle`, but every gate
must remain correct and pass from an empty cache. Android SDK and managed-device state are not
cached by the repository; the instrumented smoke test must continue to start a clean device.

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

## GitHub Actions release train

[`Release Train`](../../.github/workflows/release.yml) is a protected workflow separate from pull
request CI. Every push to `main` runs `Release Train / create-candidate-tag`, which revalidates the
version bump and uses its scoped `contents: write` permission to create `v<versionName>` on the
exact merge SHA. It has no signing secrets and never moves an existing tag. Release Train runs are
serialized, and tag creation distinguishes an absent reference from an API failure before handling
a concurrent creation idempotently. Its local regression scenarios run with:

```bash
scripts/verify-create-immutable-release-tag.sh
```

`Release Train / publish` is always present after the tag job and references the GitHub Environment
`release`. It remains pending until a required reviewer approves it; only then can it access the
four signing secrets. The job checks out the exact tag, rejects an existing release, runs
`releaseVerify`, `verifyReleaseSigningConfiguration` and `:app:assembleRelease`, verifies the
unique APK with `apksigner`, and checks the generated SHA-256.

The signed APK and checksum are retained as a workflow artifact and attached to a GitHub Release.
`publish` receives `contents: write` for that publication, uses generated release notes with a
`sha256sum -c` command, and passes `--prerelease` when the SemVer core contains a prerelease suffix.
A manual `workflow_dispatch` is a recovery execution for the current `main` commit, not a dry-run:
it follows the same Environment approval and immutable-publication rules.

## GitHub Actions pull request quality

The [`Pull Request Quality` workflow](../../.github/workflows/pull-request-quality.yml) runs for
every pull request targeting `main` and calls the root-level
[`Kotlin CI reusable workflow`](../../.github/workflows/kotlin-ci-reusable.yml). It exposes three
independent status checks:

```text
Pull Request Quality / quality / version-guard
Pull Request Quality / quality / verify
Pull Request Quality / quality / instrumented-smoke
```

`version-guard` compares the current `version.properties` with the base version and requires a
strictly greater SemVer `VERSION_NAME` and `VERSION_CODE`. `verify` and `instrumented-smoke` start
only after that guard passes. The `verify` job runs `./gradlew --no-daemon ciVerify` on Ubuntu with
Temurin JDK 21. The caller and reusable workflow declare only `contents: read`, do not consume
repository secrets, and are safe for pull requests from forks. Newer executions cancel obsolete
runs for the same pull request.

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
3. Require status checks before merging, add the exact checks
   `Pull Request Quality / quality / version-guard`,
   `Pull Request Quality / quality / verify`,
   `Pull Request Quality / quality / instrumented-smoke`,
   `Dependency Review / dependency-review`, `CodeQL / Analyze (java-kotlin)` and
   `Secret scan / gitleaks`, and require the branch to be up to date before merging.
4. Require all review conversations to be resolved.
5. Block force pushes and branch deletion.
6. Apply the rule to administrators and do not grant routine bypass access. Keep emergency bypass
   limited to a named owner and audit every use.

After merge, `Release Train` trusts that protected `main` received the pull-request assessment and
only repeats the critical version-bump validation before creating the candidate tag. Administrators
must not routinely bypass the branch rule.

## Dependency updates and review

[Dependabot](../../.github/dependabot.yml) checks Gradle and GitHub Actions dependencies on days 1
and 16 of every month at 06:00 in the `Europe/Madrid` timezone. This predictable twice-monthly
schedule approximates a 15-day cadence despite variable month lengths. Each ecosystem is limited to
two open version-update pull requests. Compatible minor and patch updates are grouped; major
updates are always opened separately for explicit review.

Automatic rebasing is disabled. A push to `main` therefore does not synchronize open Dependabot
branches and retrigger the pull-request workflows merely because their target changed. Dependabot
still scans the current `main` on each scheduled run and opens grouped pull requests for newly
available versions. Pull requests created before this policy change may retain the previous rebase
behavior temporarily. Dependabot does not auto-merge any pull request: every update must satisfy
the same CI checks and normal branch-protection reviews without repository secrets.

The [`Dependency Review` workflow](../../.github/workflows/dependency-review.yml) runs on pull
requests to `main` and exposes this required check:

```text
Dependency Review / dependency-review
```

It declares only `contents: read` and fails when a dependency change introduces a vulnerability
with `high` or `critical` severity. License policy and automatic merging remain out of scope.

## CodeQL static security analysis

The [`CodeQL` workflow](../../.github/workflows/codeql.yml) runs for pull requests to `main`,
every Monday at 03:23 UTC, and manual dispatches. Its required check is:

```text
CodeQL / Analyze (java-kotlin)
```

It initializes `java-kotlin` in manual build mode and runs `:app:assembleDebug`, so CodeQL
observes the Kotlin sources compiled through the Android Gradle build. The workflow grants only
`contents: read` and `security-events: write`, and cancels obsolete analyses for the same pull
request or Git ref. Results are published in **Security → Code scanning alerts**; a configuration
or build failure fails the check instead of publishing an incomplete analysis.

## Secret scanning

The [`Secret scan` workflow](../../.github/workflows/secret-scan.yml) runs Gitleaks for pull
requests to `main` and manual dispatches. Pushes to `main` are intentionally excluded; branch
protection must prevent direct pushes and require this pull-request check. It exposes this required
check:

```text
Secret scan / gitleaks
```

The checkout has full history, and Gitleaks scans the relevant commit range with redaction. The
controlled synthetic-fixture test demonstrates that an unallowlisted pattern fails while its value
does not appear in its captured output. See the [secret-scanning runbook](../security/secret-scanning.md)
for the exact allowlist, GitHub Secret Scanning and Push Protection activation, and incident
response.

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
