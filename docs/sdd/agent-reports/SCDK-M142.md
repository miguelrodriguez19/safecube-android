# Agent Report — SCDK-M142

## Status

`PARTIAL` — implementation and automated verification complete; human TalkBack/visual
acceptance pending.

## Context

| Campo | Valor |
| --- | --- |
| Task ID | SCDK-M142 |
| Spec | SPEC-UI-V1, SPEC-HARDENING-V1, SPEC-CRYPTO-V1 |
| ADRs | ADR-0001, ADR-0002, ADR-0003, ADR-0004 |
| Agent | Codex coordinator; GPT6 Luna XHIGH implementation subagents |
| Fecha | 2026-11-23 |

## Summary

Vault access redesign on `feat/SCDK-M142--Vault-access-redesign`, from updated origin/main
`641cbf6` (M141 merged). No separate worktree. Owner decisions: recovery initially hidden;
gate retains existing Retry even for terminal-classified failures.

## Changed files

- `feature/vault/.../component/VaultAccessLayout.kt` — canonical accessible scrollable shell.
- `feature/vault` — Create/Recovery/Unlock, EN/ES resources, screen-local previews, debug
  bridges and preview dependencies; existing ViewModels/actions/events retained.
- `feature/auth/.../gate/ui/PostLoginGateScreen.kt` — canonical gate states and previews.
- `core/ui/.../SafeCubeRecoveryKey.kt` and resources — explicit ephemeral recovery reveal.
- `core/ui/.../SafeCubeFeedback.kt` — compatible dialog options and scrollable long messages.
- `app/src/androidTest` — access/secret/dialog/legacy recovery regressions.
- `app/src/debug`, `app/src/screenshotTest`, reference PNGs — 16 selected safe new cases.
- `version.properties` — rc.7 / 28; testing/design/traceability documentation.
- [Presentation contract](../../design/vault-access-v1.md).

## Tests and quality gates

| Command | Result |
| --- | --- |
| `:feature:auth:testDebugUnitTest` | PASS after correcting gate viewport receiver |
| `:core:ui:testDebugUnitTest` | PASS with the sensitive recovery component and dialog additions |
| `:app:pixel2Api30DebugAndroidTest` excluding credential-isolated tests | PASS, 93 tests, 0 skipped/failures/errors, 3m12s |
| `:app:pixel2Api30CredentialDebugAndroidTest` selecting isolation annotation | PASS, 5 tests, 0 skipped/failures/errors, 59s |
| `:app:updateDebugScreenshotTest` local-only | PASS, 16 new references, 57 total |
| `validateVersionBump` against main version file | PASS |
| `ciVerify` | PASS, 1m42s; 795 JVM tests, lint, Kover, 57 visual references and release |

Integration results: `:feature:vault:testDebugUnitTest` PASS (72), `:app:testDebugUnitTest`
PASS (86), `:app:compileDebugAndroidTestKotlin` PASS. Initial compile fixes: preview dependency
missing from feature:vault, enum import and test member-import/exhaustiveness errors.
Privacy run: 4 recovery privacy + 4 secret field + 2 resilience UI tests PASS. The initial
dialog system-back regression failed due to Espresso's unfocused base-window root picker;
switched the test to UiDevice back. Final access/dialog run PASS (24 tests, API 36).
Added a dedicated Spanish 200-percent recovery warning/actions scroll case for final execution.
That Spanish recovery case PASS. Extended the shared dialog message slot with vertical scroll
after visual inspection found clipping in the compact 200-percent reconciliation preview.
The overflow regression PASS with guaranteed long safe fixture text; acknowledgement remains
fixed. New screenshot adapters provide a RESUMED synthetic lifecycle so reveal controls represent
an active screen while all fixture secret values remain hidden. Gate titles no longer repeat body.
Core-vault JVM tests PASS (421); version bump validation PASS. Initial 16 new references generated;
all 41 previous reference hashes unchanged, including after final refinement. Final references:
57 total, 16 new; no prior reference changed or deleted. Critical frames inspected for hidden
content, warnings, adaptive layout and dialog actions. Debug APK installed with `adb install -r`
on emulator-5554 (Success), without changing physical-device data. `git diff --check` PASS.

## Acceptance Criteria

- [x] Design-system adoption and localized distinct concepts.
- [x] Hidden secrets absent from semantics and screenshots.
- [x] Existing cancellation, retry, fallback, lock and remote notices preserved.
- [ ] Real TalkBack passphrase Unlock walkthrough.
- [x] Supported windows, themes, EN/ES and 200 percent font scale.
- [x] Safe goldens, semantic/instrumented and existing functional/security tests; final CI PASS.

## Decisions and assumptions

### Owner-selected compact recovery refinement

The owner selected proposal 3 and excluded the visible "Hidden" / "Demonstration only" captions.
Recovery now uses an outlined compact header with key icon, title, Show/Hide and chevron. The
entire header is a single accessible button; large text moves controls to a second row. Explicit
reveal inserts a monospaced value panel; lifecycle hiding removes it immediately. Visibility
remains in semantic state descriptions only. Safety copy is concise and below the card; the
checkbox and confirmation/deletion contract remain unchanged. No production demonstration text
is added. A shared inline warning keeps the feature independent of icon-library dependencies.

Focused API-30 access/privacy/resilience suite PASS (20 tests). Added an assertion that hidden
status is not visible text. Three Recovery references updated; regeneration also reconciles three
Create references whose field/action spacing differed from current rendering, without modifying
Create production code. Final privacy repeat PASS (4 tests), including the absence of a visible
hidden-state caption; `ciVerify` PASS (combined invocation 36s). Full access/privacy/resilience
subset PASS (20 tests); 57 visual references validated. `git diff --check` PASS.
An unavailable standalone emulator was replaced by the repository's managed API-30 runner.
No new commit requested or performed for this refinement.

- Domain state machines, actions/events, crypto and persistence remain unchanged.
- Reveal is presentation-only; the subsequent owner-approved interaction guard below requires
  presentation before dispatching confirmation.
- Verification will be centralized to avoid concurrent Gradle artifact mutation.
- Shared dialog adds optional `showDismissAction` and `onDismissRequest` parameters, retaining
  all defaults. This preserves a single-action lock notice and separates explicit decline from
  system back/outside dismissal for the quick-unlock offer. Two regression tests added.
- Version increment: `0.5.0-rc.7` / `28`, above updated main's rc.6 / 27.

### Owner-approved reveal-before-confirmation follow-up

The owner replaced the earlier optional-reveal decision with a screen-local interaction guard.
`SafeCubeRecoveryKey` now retains its uncontrolled API and adds a controlled overload reporting
actual RESUMED presentation. `RecoveryKeyContent` keeps non-saveable expansion, presentation
history and renewed-confirmation state keyed by the available recovery value. Premature Continue
dispatches only `ConfirmationChanged(false)`, expands/scrolls to the card and adds a polite
EN/ES inline live-region instruction. A second tap with stale confirmed state cannot bypass the
renewed checkbox requirement. No banner, timer, required scrolling or keyboard request was added.
Manual reveal followed by hide permits confirmation; pause hides without reopening while
retaining history. Recreation, exit and key replacement reset it. No ViewModel, route, crypto or
pending-record deletion contract changed. Interaction does not prove reading or safe storage.

Changed files: shared recovery component; Recovery screen; EN/ES creation resources; recovery,
access and resilience instrumented tests; presentation contract and traceability. Current checks:
`:core:ui:testDebugUnitTest`, `:feature:vault:testDebugUnitTest` and
`:app:compileDebugAndroidTestKotlin` PASS. Initial KSP ZIP cache failure resolved by the same
narrow invocation with `--no-build-cache`; no source workaround or suppression was added.
`ciVerify` PASS (1m48s), including JVM tests, lint, Kover, release and all 57 visual references.
Its first invocation found two outdated Recovery references from the owner's existing warning
banner/padding changes; both were inspected and updated, with plaintext still absent. No other
reference changed. The first 25-test managed run exposed a lifecycle suppression bug preventing
explicit Show after foregrounding, plus one legacy test expecting Continue without reveal. Both
were corrected; regression coverage now also checks automatic first reveal after pause/resume
and saved-instance restoration. Final managed API-30 rerun PASS: 27 tests, zero failures or skips,
1m19s (`VaultRecoveryPrivacyTest`, `VaultAccessSemanticsTest`, `ResilienceUiStateTest`).
Evidence: `app/build/reports/androidTests/managedDevice/debug/allDevices/index.html` and
`app/build/reports/screenshotTest/preview/debug/index.html`. `git diff --check` PASS.
No commit was created for this follow-up. Manual TalkBack remains pending for overall M142.

## Risks

Recovery emphasis follow-up: escaped the EN/ES reconfirmation resource's HTML tags so Android
does not strip them before the existing `AnnotatedString.fromHtml` rendering in the dialog and
inline instruction. Wording and emphasized spans are preserved.

Subsequent owner request: premature Continue also opens the shared single-action reminder dialog
(EN/ES), with the existing reconfirmation instruction. Acknowledgement/back only dismisses the
reminder; confirmation, cleanup and navigation are unchanged. The prior 27-test/CI results above
precede this popup refinement; instrumented interactions must now dismiss the reminder before
accessing the underlying screen. Full instrumented/CI verification has not been repeated for it.

Human TalkBack and visual acceptance must be recorded separately from automated evidence.

## Gaps and follow-up tasks

Manual TalkBack walkthrough and owner visual review pending. No automated gate remains failing.

## Next action

Complete the manual Unlock TalkBack walkthrough: page introduction, passphrase field and reveal
labels, validation and correction without lost focus, IME submit, quick-unlock cancellation and
passphrase fallback. Record only sanitized outcomes, never the spoken/typed secret values.
Review the 16 new references before committing. No protected real-data captures.
Owner subsequently requested functional atomic commits. Changes were separated into shared
components, vault-access presentation, verification/visual references and documentation commits.
