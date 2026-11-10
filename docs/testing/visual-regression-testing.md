# Compose visual regression testing

| Campo | Valor |
|---|---|
| Task | `SCDK-M137`; documentation continuation `SCDK-M139` |
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

- selected golden previews:
  `app/src/screenshotTest/java/com/miguelrodriguez19/safecube/visual/`;
- development previews and multi-preview annotations:
  `app/src/debug/java/com/miguelrodriguez19/safecube/app/testsupport/visual/`;
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

### IDE previews and golden policy

IDE previews are review and design-development surfaces; they are not automatic golden
references. Adding a golden requires a distinct visual risk, such as a new adaptation or a state
whose layout/contrast/affordance is not represented by an existing reference. Do not add a
Cartesian theme × locale × window × state matrix or duplicate references merely because an IDE
preview exists. Design source may be maintained in an external design tool, but Figma is optional
and no particular design tool is required. Goldens remain code-versioned repository artifacts and
still require human visual review.

M141 additionally declares screen-local `@Preview` functions in Welcome, Login and Signup for
direct Android Studio discovery. These render injected empty state under the canonical theme;
they do not instantiate Hilt or add screenshot-test cases. Their annotation dependency is available
to the feature, while the tooling renderer remains a debug dependency.

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

## Historical M137/M138 coverage

The original infrastructure baseline is historical context, not the current count. M137 used
seven canary references to exercise theme, locale, configuration and font-scale dimensions. M138
added two deterministic token snapshots while retaining the seven-reference infrastructure
coverage, for nine historical references in total. Those historical counts must not be combined
with the current component baseline.

The shared historical state used synthetic auth, vault, password/note items, synced/draft/conflict,
sync in progress and a retryable offline error. The semantic canary verified roles, labels,
enabled/disabled states, state descriptions and dispatched actions against the same state.

## M139 component and infrastructure coverage

The component/infrastructure subset contains **25 references**:

| Target | References | Risk covered |
|---|---:|---|
| Eight component families × `EN` Light/Dark | 16 | Baseline component appearance and theme roles for Actions, Fields, Navigation, Feedback Status, Feedback Resolution, Feedback Availability, Feedback Recovery and Confirmation |
| Explicit Fields Spanish Light | 1 | Locale-specific field labels/supporting text without duplicating the full locale matrix |
| Fields 200% font scale, `EN` Light | 1 | Text reflow and preservation of field actions at large font scale |
| Navigation landscape, Spanish Dark | 1 | Selection, labels and layout under medium-landscape width and localized text |
| Feedback Availability landscape, Spanish Light | 1 | Long availability/status content and action placement in landscape |
| Confirmation 200% font scale, Spanish Dark | 1 | Dialog reflow and destructive action access at large font scale |
| Confirmation loading, `EN` Light | 1 | Visible loading progress, retained labels and disabled action appearance; blocked dismissal/duplicate confirmation are tested semantically |
| Existing visual regression canary | 1 | Pipeline/configuration smoke coverage only; `visualRegressionCanaryConfigurationPreview` retains the existing Preview name `Compact portrait` and its settings |
| Existing token snapshots | 2 | Approved light/dark token roles and typography/spacing/shape/elevation/motion rendering; unchanged |
| **Total** | **25** | **22 component goldens + 1 canary + 2 token snapshots** |

The component catalog therefore has 22 screenshot goldens: 16 family baselines, one explicit
Fields Spanish Light case, and five existing targeted adaptation/state cases. Its debug-only IDE
catalog has 37 previews (32 family previews plus those five targeted cases); IDE preview count is
not golden count.

The five targeted cases are intentionally selected safeguards rather than a Cartesian expansion.
The canary is reduced to its existing Compact portrait case, and the two token snapshots are
unchanged. The source and reference inventory must match the cases discovered by the read-only
gate; stale references must be retired when their golden entry point is removed.

## Component catalog after SCDK-M139

The [component catalog](../design/component-catalog-v1.md) extends the existing adapter with
`DesignSystemCatalogScreenshotTest` and debug-only rendering surfaces. It renders public
`core:ui` APIs directly, with explicit light/dark, EN/ES, API 36, dimensions and font scales.
There are 37 debug-only IDE previews, but only 22 component goldens: 16 family baselines, one
explicit Fields Spanish Light case and five targeted adaptation/state cases. Together with the
one existing Compact portrait canary and two unchanged token snapshots, this reference
subset is 25. Secret previews use empty values or inert masking glyphs; reveal is tested through
semantic interaction, not exposed PNGs. Static references verify resting visual states; transient
press and real input focus are verified through instrumented interactions rather than assumed from
effect-driven preview rendering.
Components receive injected state and callbacks and do not resolve any production dependencies.

`ComposeDesignSystemActionsTest`, `ComposeDesignSystemSecretTest`,
`ComposeDesignSystemStructureTest` and `ComposeDesignSystemFeedbackTest` are part of the ordinary
app instrumented suite. They do not mutate device credentials and therefore belong to the general
API 30 partition described in [testing.md](testing.md), excluding `DeviceCredentialIsolation`.
No new story-specific Gradle verification task is introduced. Local update, human review and
read-only verification use the same commands as M137. Exact results and remaining review are
recorded in [the M139 report](../sdd/agent-reports/SCDK-M139.md).

## Shell coverage after SCDK-M140

The [shell contract](../design/shell-navigation-motion-v1.md) adds exactly six selected references
to the existing 25, for **31 total**. Broad development previews remain in debug and do not
automatically become golden tests.

| Target | Configuration | Distinct visual risk |
|---|---|---|
| Session splash | Light EN, compact | Static brand mark, themed surface and accessible progress |
| Session splash | Dark EN, compact | Dark surface/brand contrast |
| Vault-selected shell | Light EN, compact | App-bar actions, content margins and selected Vault tab |
| Settings-selected shell | Dark ES, compact | Localized labels and selected Settings tab |
| Settings shell | Light ES, medium landscape | Horizontal layout and action placement |
| Vault shell | Dark ES, compact, 200% font | Reflow, heading/action access and tab labels |

All six pin API 36 and disable dynamic color. Shell fixtures contain only synthetic labels.
Motion scales, back operations and removal of outgoing protected content are tested semantically
and instrumentally, rather than represented by additional transient PNGs. Updates remain explicit
local operations with visual inspection; CI only validates. Execution evidence belongs to the
[M140 report](../sdd/agent-reports/SCDK-M140.md).

## Account entry coverage after SCDK-M141

The [account entry contract](../design/auth-entry-v1.md) adds ten deliberate references to the
31 existing references. `AuthScreenshotTest` renders the actual production content through
debug-only bridges, with no ViewModel or production services.

| Target | Configuration | Distinct visual risk |
|---|---|---|
| Welcome | Light EN / Dark ES | Brand hierarchy, entry actions and localized dark treatment |
| Welcome | Light ES, compact 200% | Large-type introduction and scrollable entry actions |
| Login idle | Light EN, compact | Account credentials versus vault terminology |
| Login loading | Light EN, compact | Progress, preserved context and disabled controls |
| Login retryable | Dark ES, compact | Contextual single retry action |
| Login terminal | Light EN, compact | Terminal context without retry |
| Login idle | Dark ES, medium landscape | Form width, reflow and localized action placement |
| Signup validation | Light ES, compact | Error-field association and confirmation |
| Signup | Dark ES, compact 200% | Long labels and action access through scrolling |

All cases pin API 36, locale, theme, dimensions and font scale, with dynamic color disabled.
Password fixture values are empty or inert masking glyphs. Focus/IME/autofill and real callbacks
are verified instrumentally, not inferred from static captures. Broader debug previews remain
independent of golden selection. Local generation, PNG inspection, human approval and read-only
verification follow the same workflow above. Execution results, including any pending manual
TalkBack or visual approval, belong to the [M141 report](../sdd/agent-reports/SCDK-M141.md).

## Upstream documentation

- [Compose Preview Screenshot Testing](https://developer.android.com/studio/preview/compose-screenshot-testing)
- [Compose Preview Screenshot Testing release notes](https://developer.android.com/studio/preview/compose-screenshot-testing-release-notes)
