# Account entry — SCDK-M141

Normative sources: [SPEC-UI-V1](../specs/features/ui-brand-motion-v1.md),
[SPEC-HARDENING-V1](../specs/features/hardening-resilience-v1.md),
[auth contract](../architecture/openapi-auth-contract-integration.md),
[ADR-0003](../architecture/adr/ADR-0003-SENSITIVE-DATA-SURFACES.md) and
[ADR-0004](../architecture/adr/ADR-0004-DESIGN-SYSTEM-APPEARANCE.md).

## Composition and ownership

Welcome, Login and Signup consume the public `core:ui` design system and resolved root theme.
The [approved brand references](mockups/README.md) guide hierarchy rather than fixed coordinates.
The auth-specific layout is scrollable, handles IME insets and constrains medium-window content
to a 560dp column. This local composition limit does not redefine a global design token.
Critical actions grow with text and remain reachable by scrolling at 200% font scale.

Welcome explains the product and links to the two existing account routes. Login and Signup name
the account password explicitly and distinguish it from the vault passphrase. There are no new
routes, account methods, session policies, domain validations or backend integrations.
New auth copy belongs to the feature's single EN/ES resource file per locale.

## Input and feedback

Fields follow their visual order. Entry does not automatically open the keyboard; email is the
first input in keyboard traversal. IME Next advances between fields, and Done shares the guarded
submit path. Errors are associated with their fields; global feedback is reserved for additional
operation context. Validation returns focus to the first invalid field. Invalid credentials return
focus to password. Focus effects react to outcomes, not ordinary recomposition.

Only account fields declare credential autofill semantics. Login uses an existing password;
Signup uses a new password, including confirmation. Secret fields remain masked by default and
use the canonical opt-in reveal policy. No password, confirmation or reveal value is saved in
Bundle, navigation arguments or saved state; only a non-sensitive field identifier may be restored.

The existing ViewModels own all operation state, field values and cleanup. Their Phase 7 tests
remain functional evidence. Loading blocks duplicate input, submit and route-switch callbacks.
Retry is explicit and appears once next to its context. Signup's retry after successful register
continues to retry login only. Terminal feedback does not offer retry; corrected input follows the
existing ViewModel transition back to idle. Messages come from sanitized resource identifiers.

## Verification and review

Debug-only bridges expose the same production content to previews and semantic tests without
instantiating production services. Visual fixtures contain only reserved-domain email, empty
passwords or inert masking glyphs. They never contact a backend, Room or Keystore.

Selected goldens cover Welcome, Login idle/loading/retryable/terminal, Signup validation and
distinct 200%/landscape adaptations. They pin API 36, locale, theme, dimensions and font scale,
with dynamic color disabled. Broader IDE previews do not automatically become goldens.
Welcome, Login and Signup also declare parameterless `@Preview` entry points in their own screen
files, so Android Studio can discover each screen directly. Each renders EN light and ES dark
through `SafeCubeTheme`; account forms receive empty injected state with focus effects disabled.
They do not instantiate Hilt ViewModels and do not add golden cases.
Follow the [local update and human review workflow](../testing/visual-regression-testing.md).

Manual TalkBack acceptance remains required; automated semantic assertions do not attest spoken
feedback or screen-reader traversal. Use an approved test account/backend or an interactive host
with fake dependencies, never a production account. Traverse the heading,
email, password and labeled reveal action; submit, hear and correct validation; trigger a retryable
failure, invoke its single retry, then complete success. Repeat the input/action traversal with ES,
large font and the keyboard visible. Check that no raw secret is spoken while masked and no focus
is lost after an error. Do not capture HTTP logs, credentials or a production account as evidence.

Execution results and remaining review belong to the
[M141 agent report](../sdd/agent-reports/SCDK-M141.md).
