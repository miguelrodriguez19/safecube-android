# SafeCube shell, navigation and motion v1

Task: `SCDK-M140`. Normative sources:
[SPEC-UI-V1](../specs/features/ui-brand-motion-v1.md),
[ADR-0004](../architecture/adr/ADR-0004-DESIGN-SYSTEM-APPEARANCE.md),
[ADR-0003](../architecture/adr/ADR-0003-SENSITIVE-DATA-SURFACES.md) and
[ADR-0001](../architecture/adr/ADR-0001-VAULT-AUTO-LOCK.md).
The routing, restoration and back contracts of M117, M125 and M129 remain in force.

## Ownership and composition

`SafeCubeScaffold` in `core:ui` accepts composable top-bar, bottom-bar, snackbar and content slots.
It owns the background, safe-drawing content insets, consumption of scaffold padding and 16dp
content margins. The top-bar host applies top/horizontal safe insets; canonical bottom navigation
owns its bottom/horizontal safe insets. Callers do not apply those insets or outer margins again.
The scaffold never resolves routes, ViewModels, repositories or domain state.

Bóveda and Ajustes use the scaffold, canonical app bar and canonical bottom navigation.
Their content and operations remain owned by `feature:vault`; M143/M145 own their full redesigns.
The feature tab adapter accepts a list, selected tab and one selection callback. The public
component accepts arbitrary stable item IDs; a third synthetic item is a test case, not a v1 route.
Product destinations remain exactly Bóveda and Ajustes.

The app applies its root surface and system-bar icon brightness only after appearance resolves.
Brightness follows the resolved theme surface, including explicit light/dark and dynamic color.
The native splash stays installed during appearance loading; `FLAG_SECURE` is retained.
The Compose session gate uses the existing static brand mark and localized accessible progress,
without navigation controls or an artificial minimum display time.

## Navigation and Back

`NavigationController` performs the existing list operations: add, pop, replace current, set root
and Settings Back to the nearest Vault entry (or Vault fallback when restored without one).
Selecting the current tab remains a no-op through its existing caller callback. Selecting Vault
from Settings retains the previous add-route semantics; this task does not introduce multi-stack
navigation or remove historical entries as an unrelated navigation change.

Back from Vault first displays a localized native `Toast.LENGTH_SHORT` without actions. A second
press confirms while the toast is visible; `Toast.Callback` visibility is authoritative, including
system animation and accessibility extensions. An immediate second press also confirms while
delivery is pending. If Android suppresses the toast and sends no callbacks, that pending window
expires at the accessibility-recommended short duration (AOSP base: 2,000ms), with an exclusive
deadline. Leaving Vault, losing access, pausing or disposing the host cancels the notice and clears
the confirmation. This state is never saved or restored. This owner correction, approved on
2026-11-09, replaces the former 1,500ms/snackbar behavior; it is a narrow SPEC-UI-V1 exception for
the informational exit notice.

Navigation3 only handles Back when a previous entry exists. The host additionally handles Back
for a single Vault root, so a freshly unlocked stack and a stack returning from Settings invoke
the same exit policy. The root handler is registered before NavDisplay to preserve descendant
screen handler priority. Other routes retain their existing Back behavior.

Navigation3 remains the rendering engine with the existing saveable-state and ViewModel-store
entry decorators. The state-injected `SafeCubeNavigationHost` is also used by deterministic tests.

## Motion and access boundaries

| Role | Existing token | Duration | Behavior |
|---|---|---:|---|
| Enter | Standard | 250ms | Subtle fade with enter easing |
| Exit | Short | 150ms | Subtle fade with exit easing |
| State change | Short | 150ms | Semantic duration for state feedback |
| Feedback | Short | 150ms | Semantic duration for feedback; Material owns its native control animations |

Ordinary authorized tab/editor pushes and pops use the fade policy, including Navigation3's
predictive-pop transition hook. Splash, authentication, bootstrap, recovery and unlock boundaries,
root replacement and current-entry replacement are instant. There is no slide or size animation.
Sync uses the canonical loading icon button instead of a manually repeating animation coroutine.

Compose applies the system animation duration scale once. Zero removes animation waiting;
navigation, selection, focus traversal and callbacks do not wait for animation completion.
No new persistence or animation-scale preference is introduced.

The host first applies the existing restored-navigation guard. Each rendered entry additionally
reads live session/vault state through a remembered state reference and applies that same policy.
This check includes entries retained as outgoing animation content, even when the current route
has already become safe. Revocation removes protected composition independently of fade duration.
Existing session/vault coordinators still choose the safe destination and replace the stack.
Recovery retains its existing session-only access contract.

## Evidence

JVM navigation/controller tests protect exact stack operations, instant boundaries and the Back
window, toast callback lifecycle and stale callback isolation. Compose tests exercise the real
Back dispatcher for both a single Vault root and a return from Settings, and cover shell
semantics/insets, a third synthetic tab, adaptation,
motion scales and removal of outgoing protected content during revocation. Appearance and
Phase 7 restoration/security regressions remain part of verification.

Development previews remain independent of the deliberate six-case golden selection described in
[visual regression testing](../testing/visual-regression-testing.md). Fixtures contain only
synthetic labels, never vault contents, account data or usable credentials.
Executed commands, results and acceptance status are recorded in the
[M140 report](../sdd/agent-reports/SCDK-M140.md).
