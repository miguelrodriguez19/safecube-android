# Vault access — SCDK-M142

Sources: [UI spec](../specs/features/ui-brand-motion-v1.md),
[hardening](../specs/features/hardening-resilience-v1.md),
[crypto](../architecture/crypto-v1.md), ADRs
[0001](../architecture/adr/ADR-0001-VAULT-AUTO-LOCK.md),
[0002](../architecture/adr/ADR-0002-PASSPHRASE-REWRAP.md),
[0003](../architecture/adr/ADR-0003-SENSITIVE-DATA-SURFACES.md) and
[0004](../architecture/adr/ADR-0004-DESIGN-SYSTEM-APPEARANCE.md).

## Ownership and invariants

The presentation consumes existing Create, Recovery, Unlock and PostLogin states/actions/events.
It does not change bootstrap routing, KDF, envelopes, Keystore, pending initialization persistence,
session priorities or Android prompt authentication. Account login, vault passphrase, recovery key
and local quick unlock use distinct localized descriptions.

`VaultAccessLayout` owns the scrollable, IME-aware, maximum-560dp presentation column within the
canonical scaffold. Pane titles and headings introduce the page before fields or submit actions.
Text and actions remain reachable at 200 percent font scale; no automatic keyboard on entry.
Supporting error slots remain structurally stable while editing to preserve focus and IME.

## Owner decisions

- Start from updated main on `feat/SCDK-M142--Vault-access-redesign`, without a new worktree.
- Recovery starts hidden; explicit Show/Hide is ephemeral, never restored and limited to RESUMED.
  Pause, exit or recreation hides it without an exit animation. Hidden content is absent from
  merged/unmerged accessibility semantics, not merely visually obscured.
- Confirmation requires the key to have been presented during this screen instance. Existing
  confirmation and verified deletion remain authoritative; no new clipboard, export, screenshot
  or persistence capability is added.
- PostLoginGate preserves Retry for all non-loading errors that currently offer it, including
  terminal-classified failures. Visual classification must not change recovery or route policies.
- The Android biometric/device-credential dialog remains system-owned. Presentation preserves
  lock-notice priority, enrollment offer, cancellation and passphrase fallback.

## Selected recovery presentation

The owner selected the compact expandable proposal. Its outlined header contains a decorative
key icon, the recovery-key title, Show/Hide and a decorative chevron. The entire header is one
labeled button with a visibility state for assistive technology. No visible "Hidden" status or
"Demonstration only" subtitle is rendered. Large text moves the action onto a second header row.
Manual reveal or an explicit premature Continue inserts the monospaced value panel; hiding or lifecycle removal unmounts it
immediately without an exit animation. The concise safety warning appears below the card, followed
by the existing confirmation and Continue action. Storage/confirmation behavior is unchanged.

If Continue is pressed with the checkbox checked before the key has actually been presented,
the screen sends only `ConfirmationChanged(false)`, expands and scrolls to the card, and announces
localized inline guidance beside the confirmation. The owner's subsequent refinement also opens
a localized reminder dialog, "Important: save your recovery key", with a single "Got it" action.
Dismissing it never confirms recovery or navigates; the checkbox must still be marked again.
It sends no `Continue`, so pending-record
cleanup and navigation cannot start. The user must check the confirmation again before proceeding.
There is no timer or required scrolling, and this interaction cannot prove reading or secure storage.

Presentation is recorded only when the nonempty value is composed while RESUMED. Manually hiding
the card does not discard that history. Pause immediately hides the key and does not reopen it
on resume, but retains the history. Exit, recreation or replacement of the available key resets
both presentation states; neither is saveable. Loading, retry and verified cleanup keep their
existing ViewModel contract. Automatic expansion does not request keyboard focus or change routes.

## Evidence policy

Previews and screenshot fixtures inject empty values or unmistakably invalid demo markers.
No generated recovery key, passphrase, account data or raw HTTP diagnostics belong in images,
reports or test output. No reveal state or secret is saved to Bundle or navigation.

Automated semantics tests support, but do not replace, the manual TalkBack Unlock walkthrough.
Lifecycle/lock removal of sensitive presentation is immediate and independent of motion settings.
State-machine and process-death tests from Phase 7 remain required functional evidence.
