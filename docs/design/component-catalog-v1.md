# SafeCube component catalog v1

Task: `SCDK-M139`. Normative sources: [SPEC-UI-V1](../specs/features/ui-brand-motion-v1.md),
[ADR-0004](../architecture/adr/ADR-0004-DESIGN-SYSTEM-APPEARANCE.md),
[ADR-0003](../architecture/adr/ADR-0003-SENSITIVE-DATA-SURFACES.md) and
[brand guide](brand-guide-v1.md).

## Ownership and composition

Public components live in `core:ui`, package
`com.miguelrodriguez19.safecube.core.ui.component`. They receive state and callbacks; callers
own business validation, navigation, operation outcomes and lifecycle cleanup of secret values.
Components never access repositories, ViewModels, network, storage or feature types. Callers
resolve functional messages from localized resources. All built-in labels have EN/ES parity.

Localized text is grouped in one `strings.xml` per module, source set and language: English in
`values` and Spanish in `values-es`. Production and debug catalog resources remain separate.
Keep existing resource names when reorganizing files; grouping by component family does not
require separate XML files. Locale parity checks inspect resource keys rather than filenames.

Use `SafeCubeTheme` once at the root. Components consume its Material color roles and the
SafeCube spacing, shapes, typography and elevation tokens, including when dynamic color is on.
No component defines a feature-specific palette or domain state machine.

## Supported families and states

| Components | Supported states | Accessibility and actions |
|---|---|---|
| `SafeCubeButton` | Primary, secondary, text, destructive; default, pressed, focused, disabled, loading | Button role, visible label, minimum 48dp, loading announcement; disabled/loading suppress callbacks |
| `SafeCubeIconButton` | Default, pressed, focused, disabled, loading | Required localized accessible label, minimum 48dp |
| `SafeCubeTextField` | Default, focused, disabled, error; supporting text | Persistent label, editable semantics, keyboard/IME actions, contextual error |
| `SecretOutlinedTextField` | Masked by default; optional explicit reveal; focused, disabled, error | Localized show/hide action and visibility state; hidden plaintext absent from semantics |
| `SafeCubeTopAppBar` | Title, optional navigation action, action slot | Heading title; named navigation/action controls |
| `SafeCubeBottomNavigation` | Selected/unselected, focused, pressed, disabled entries | Tab role, labels, selection and callback; no destination resolution |
| `SafeCubeScaffold` (M140) | Top/bottom bars, snackbar and content slots | Single-owner safe insets and 16dp content margins; state and navigation remain with callers |
| `SafeCubeCard`, `SafeCubeItemRow` | Static or actionable; enabled/disabled; interaction feedback; optional row status | Static content has no click action; actionable content has 48dp target; row status is textual and announced |
| `SafeCubeBanner` | Info, warning, error, offline, draft, conflict, success; optional enabled/loading action | Text and icon plus localized state description, polite live region, contextual action |
| `SafeCubeConfirmationDialog` | Normal/destructive, confirming/loading | Heading, confirm/dismiss callbacks; loading blocks duplicate confirmation and dismissal |
| `SafeCubeProgress` | Indeterminate or explicitly determined | Message and progress semantics |
| `SafeCubeEmptyState` | Empty, optional action | Heading, explanation and optional named action |
| `SafeCubeRetryableError` | Retry available or retrying | Context and adjacent retry; retrying blocks duplicates |
| `SafeCubeTerminalError` | Terminal, optional corrective action | Context with no retry API; caller supplies only an authorized corrective action |

Error is `N/A` for navigation and action-only buttons: validation belongs to the associated field
or feedback surface. Press/focus are `N/A` for static cards, banners without actions, progress and
static state messages. Loading is represented by progress or the contained action, rather than a
second unrelated state flag on every container. Pressed/focused visuals use real Material/Compose
interactions and an injectable interaction source where supported.
For editable fields, pressing requests focus rather than introducing a separate persistent state.

## Coverage matrix

The debug catalog has eight review families. Each family has four IDE previews (`Light`/`Dark` ×
`EN`/`ES`). Those previews make the component contract easy to inspect but are not automatic
goldens. The screenshot source set keeps one `EN` light/dark pair per family, then adds only
targets that cover a distinct adaptation or state risk.

| Family or target | IDE previews | Current component goldens | Distinct visual risk | Safeguard |
|---|---:|---:|---|---|
| Actions | 4 | 2 (`EN` Light/Dark) | Variant contrast, minimum targets, pressed/focused/disabled/loading action treatment | Semantic role/label tests, callback suppression, token-based colors and 48dp targets |
| Fields | 5 | 4 (`EN` Light/Dark + Spanish Light + 200% English Light) | Label/supporting/error layout, localization length, secret masking and reveal affordance | Spanish and 200% goldens, secret lifecycle/semantics tests, synthetic values only |
| Navigation | 5 | 3 (`EN` Light/Dark + landscape Spanish Dark) | Selection, localized labels, row/card insets and landscape layout | Landscape target, selection/label semantics, explicit navigation callbacks |
| Feedback Status | 4 | 2 (`EN` Light/Dark) | Info/warning/error hierarchy and contextual action contrast | Text/icon/state-description contract, contrast regression and localized resources |
| Feedback Resolution | 4 | 2 (`EN` Light/Dark) | Draft/conflict/success differentiation and contextual actions | Semantic action labels, text/icons and theme roles |
| Feedback Availability | 5 | 3 (`EN` Light/Dark + landscape Spanish Light) | Long availability/status content and action placement in landscape | Landscape target, explicit state text/icon and localized action |
| Feedback Recovery | 4 | 2 (`EN` Light/Dark) | Retryable versus terminal affordances and duplicate-action prevention | Separate retry/terminal APIs, guarded callbacks and state-description assertions |
| Confirmation | 6 | 4 (`EN` Light/Dark + 200% Spanish Dark + loading English Light) | Large text reflow, destructive emphasis and visible loading/disabled treatment | 200% and loading targets, heading/action semantics and duplicate-confirmation guard |
| **Component total** | **37: 32 family + 5 targeted** | **22** | — | Eight family pairs, one Spanish form and five targeted adaptation/state cases |

The five targeted component cases are deliberately narrow: Fields at 200% font scale in English
Light, Navigation in medium landscape Spanish Dark, Feedback Availability in medium landscape
Spanish Light, Confirmation at 200% font scale in Spanish Dark, and Confirmation loading in
English Light. They are adaptation/state safeguards, not a Cartesian expansion of every family by
every locale, theme, window and state.

IDE previews are not automatic goldens. A new golden must cover a distinct visual risk and must not
duplicate an existing reference or create a Cartesian matrix without a documented reason. Design
source may live in an external design tool, but no particular tool (including Figma) is mandated;
the rendered golden and its source code remain versioned in the repository.

## Interaction, focus and secret policy

- Minimum touch targets are 48dp; content can grow with text at 200%. Do not constrain components
  to fixed heights or truncate critical actions. Hosts provide scrolling when the window requires it.
- Normal traversal follows composition order. Fields accept keyboard/IME configuration; no field
  automatically steals focus. Dialog focus uses the modal Compose host and returns to its caller.
- Item rows expose status once through the parent's `stateDescription`. The matching visible
  status remains on screen, but its child semantics are cleared to avoid a duplicate announcement;
  title, supporting text and action semantics remain available.
- Errors preserve the label and contextual supporting text. Relevant feedback is available beyond
  a transient Toast and communicates severity through words/icons as well as approved color roles.
  If callers omit supporting text on an error field, the component supplies a localized visible
  error label, so even that state never relies only on a red border.
- `enabled=false` and `loading=true` block callbacks, including direct semantic invocations.
  Loading preserves the action context and supplies accessible state/progress feedback.
- Optional action label and callback must be supplied together. Components do not infer whether
  an operation is retryable; consumers select the appropriate error component.
- Textual buttons inherit the semantic surface foreground (`LocalContentColor`) so actions on
  error/warning containers retain their approved contrast. Cards own their standard 16dp insets
  and 8dp content spacing; callers need not recreate those styles.
- Secret reveal is opt-in; existing consumers remain permanently masked. Only an explicit action
  while the host is `RESUMED` reveals the value. `ON_PAUSE`, disablement and composition recreation
  hide it immediately, and returning does not restore reveal. Visibility is not saveable.
- Hiding does not clear user input or change vault auto-lock. Lock/cancel/flow cleanup remain the
  consumer's responsibility under ADR-0003. No clipboard operation or secret persistence is added.
- The compatible String/callback secret API uses a non-saveable native `TextFieldState` editing
  buffer, synchronized with caller values, because `OutlinedSecureTextField` supplies copy/cut/drag
  protections in both visibility modes. Neither this buffer nor reveal is restored after recreation.
- Masked semantic text is memoized for the current value using non-saveable `remember(value)`.
  The reveal icon uses Material's disabled content color without an additional alpha reduction.

## Evidence and adoption

The deterministic component catalog lives in `app/src/debug` and screenshot previews in
`app/src/screenshotTest`, reusing the [existing visual regression engine](../testing/visual-regression-testing.md).
It composes real public primitives, including a form, without feature style copies. The catalog has
37 IDE previews: 32 family previews plus the five targeted adaptation/state previews in the matrix
above. The screenshot source set has 22 component goldens: 16 family baselines, one explicit
Fields Spanish Light golden, and the five targeted cases. Secrets stay masked.
The non-empty secret preview contains only inert masking glyphs, never a credential fixture.
Press/focus interaction sources are supplied for interactive previews; the static renderer captures
resting pixels, while real focus and action behavior are asserted in instrumented tests.
Semantic tests under `app/src/androidTest` exercise the same APIs as an external consumer.

`DesignSystemContractTest` guards resource parity and forbidden imports. The exact executed
commands and M139 evidence are recorded in the
[agent report](../sdd/agent-reports/SCDK-M139.md). The component/infrastructure visual baseline is 25
references: 22 component goldens,
one existing Compact portrait canary, and the two existing token snapshots. The canary keeps the
`visualRegressionCanaryConfigurationPreview` function and its existing `Compact portrait` Preview
settings; the token snapshots remain unchanged.

M140 adds six targeted shell references using the scaffold and the same primitives, for 31 total.
Its composition, inset and motion contracts are documented in
[shell, navigation and motion v1](shell-navigation-motion-v1.md).

M140 owns shell adoption; M141–M145 own screen redesigns. This catalog adds neither production
destinations nor new domain messages, and does not mark the whole UI spec verified.
