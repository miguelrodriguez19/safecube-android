# Compose visual regression testing

| Campo | Valor |
|---|---|
| Task | `SCDK-M137` |
| Spec | `SPEC-UI-V1` / `NFR-UI-005`, `SEC-UI-001` |
| ADRs | `ADR-0003-SENSITIVE-DATA-SURFACES`, `ADR-0004-DESIGN-SYSTEM-APPEARANCE` |
| Owner | `quality/android-ui` |
| Estado | `IMPLEMENTED` |

Contratos normativos: [SPEC-UI-V1](../specs/features/ui-brand-motion-v1.md),
[ADR-0003](../architecture/adr/ADR-0003-SENSITIVE-DATA-SURFACES.md) y
[ADR-0004](../architecture/adr/ADR-0004-DESIGN-SYSTEM-APPEARANCE.md).

## Toolchain and isolation

SafeCube uses the official experimental Compose Preview Screenshot Testing plugin
`com.android.compose.screenshot` pinned to `0.0.1-alpha16`. The integration is isolated to `:app`
and its `screenshotTest` source set. The pinned project environment is AGP `9.0.1`, Kotlin
`2.3.10`, Compose BOM `2026.02.00`, JDK 21 and compile SDK 36.

### Experimental dependency boundary

`verifyVisualRegression` is SafeCube's stable project-level contract. Its current delegation to
`:app:validateDebugScreenshotTest` is an implementation detail confined to the root Gradle build.
The engine is alpha software: its plugin API, generated task names, reference layout and report
layout are not treated as stable SafeCube contracts. No production source, fixture model, semantic
test or product component may depend on those details.

The standalone `com.android.compose.screenshot` plugin is deprecated for the future AGP 9.5 test
suite model. SafeCube intentionally retains it as a pinned compatibility adapter while the project
uses stable AGP `9.0.1`; adopting AGP `9.5.0-alpha` only to replace one experimental integration
with another is outside M137. Migration is required when AGP-native screenshot test suites are
available on the project's stable AGP line, or earlier if the pinned adapter stops passing the
canonical gate. Such migration must preserve the public command, determinism/privacy rules and
human approval workflow. Renderer changes may regenerate pixels, but never justify accepting
references without visual review.

The implementation paths are:

- previews and multi-preview annotations:
  `app/src/screenshotTest/java/com/miguelrodriguez19/safecube/visual/`;
- approved references: `app/src/screenshotTestDebug/reference/`;
- deterministic fixtures and rendering surface:
  `app/src/debug/java/com/miguelrodriguez19/safecube/app/testsupport/visual/`;
- semantic assertions:
  `app/src/androidTest/java/com/miguelrodriguez19/safecube/ComposeVisualRegressionCanarySemanticsTest.kt`;
- difference report: `app/build/reports/screenshotTest/preview/debug/index.html`.

The screenshot source set renders injected state only. It must not instantiate a ViewModel, access
network, Room, Keystore, system credentials or a production account.

## Canonical commands

Validate the approved references without modifying them:

```bash
./gradlew verifyVisualRegression
```

The root task delegates only to `:app:validateDebugScreenshotTest`. It also rejects any GitHub
Actions workflow containing a screenshot update task. `ciVerify` depends on this read-only gate.

Run the semantic test in the canonical instrumented suite:

```bash
./gradlew :app:pixel2Api30DebugAndroidTest
```

Methods under `src/androidTest` use Java-compatible identifiers without backticks or spaces. This
keeps individual test selection portable across AndroidJUnitRunner, UTP and Android Test
Orchestrator; descriptive backtick names remain valid only for JVM test source sets.

`ComposeVisualRegressionCanarySemanticsTest` also stays lexically before
`QuickUnlockDeviceCredentialTest`. The latter changes and restores the emulator credential and is
the deliberate final class in the current API 30 Orchestrator suite. On that platform, starting a
new instrumentation process while `locksettings clear` is invalidating Keystore handles can leave
the runner waiting indefinitely for Orchestrator. This ordering is a compatibility guard, not a
dependency of the semantic test; isolating credential-mutating tests into their own managed-device
execution is the preferred future replacement.

Create or update references only through an explicit local invocation:

```bash
./gradlew :app:updateDebugScreenshotTest
```

The update command is never valid in CI. A successful update is not approval: the resulting PNG
changes remain ordinary reviewable repository changes.

## Golden review workflow

1. Run `./gradlew verifyVisualRegression` before changing references and retain the generated HTML
   report when diagnosing a mismatch.
2. Confirm that the UI change is required by an approved UI task. A rendering failure, blank
   image, accidental clipping or secret exposure must be fixed, never accepted as a new baseline.
3. Run `./gradlew :app:updateDebugScreenshotTest` locally and remove references whose preview no
   longer exists; the experimental tool does not delete stale files automatically.
4. Review every PNG under `app/src/screenshotTestDebug/reference/` and inspect `git diff --stat`.
   Verify theme, locale, dimensions, font scale, labels and absence of sensitive content.
5. Run `./gradlew verifyVisualRegression` again. The command must pass without changing any PNG.
6. Submit the source change and references together for human review. The reviewer approves the
   visual intent; CI only verifies that committed references match rendering.

On mismatch, the plugin fails the Gradle task and writes an inspectable report to
`app/build/reports/screenshotTest/preview/debug/index.html`. CI uploads the complete
`**/build/reports/screenshotTest/**` tree with the other quality reports.

## Determinism and privacy rules

- Every non-locale preview pins locale `en`; locale previews pin `en` or `es` explicitly.
- Every preview pins API 36, dimensions and selected font scale. Theme previews pin day/night mode.
- Dynamic color is disabled so host wallpaper cannot change a reference.
- Fixtures use only reserved domains and visibly synthetic titles. They contain no passwords,
  passphrases, recovery keys, tokens, account IDs, payloads or real user content.
- Fixtures contain no current time, timezone formatting, random UUID, network result, local file or
  environment-derived value.
- Rendering uses bundled Android/Compose fonts and resources; tests never download fonts or assets.
- Animations, asynchronous work and transient feedback are excluded from the initial infrastructure
  surface. Later previews must inject their terminal state directly.
- A report, diff or reference containing content that should be secret is a security failure under
  `ADR-0003`, not an acceptable baseline.

## Initial coverage

The infrastructure canary deliberately validates the pipeline rather than the final redesign:

| Multi-preview | Cases |
|---|---|
| Theme | light and dark, compact portrait, English |
| Locale | English and Spanish, compact portrait |
| Configuration | compact portrait, medium landscape and compact portrait at 200% font scale |

The shared state includes synthetic auth, vault, password/note items, synced/draft/conflict, sync
in progress and a retryable offline error. The semantic test verifies button roles, accessible
labels, enabled/disabled states, state descriptions and dispatched actions against the same state.
M138–M147 will add product surfaces without changing the update/verify contract.

## Upstream documentation

- [Compose Preview Screenshot Testing](https://developer.android.com/studio/preview/compose-screenshot-testing)
- [Compose Preview Screenshot Testing release notes](https://developer.android.com/studio/preview/compose-screenshot-testing-release-notes)
