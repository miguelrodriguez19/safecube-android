# SafeCube pilot mockups

These eight deterministic `390×844` SVGs are the SCDK-M135 visual review set. They are brand
references, not Android resources or implementations of production screens.

| File | Theme | Representative state |
| --- | --- | --- |
| `welcome-light.svg` | Light | Welcome content and primary/secondary entry actions |
| `welcome-dark.svg` | Dark | Welcome content and inverted brand treatment |
| `login-light.svg` | Light | Idle account login with masked password |
| `login-dark.svg` | Dark | Retryable/offline login failure with contextual retry |
| `unlock-light.svg` | Light | Locked vault with passphrase fallback |
| `unlock-dark.svg` | Dark | Quick-unlock cancellation/error with passphrase fallback |
| `vault-light.svg` | Light | Manual sync success feedback, synced-by-default rows and lower create FAB |
| `vault-dark.svg` | Dark | Paused sync with visible local content, title-level draft/conflict and lower create FAB |

## Regeneration

From the repository root:

```text
python3 docs/design/mockups/generate_mockups.py
```

The generator uses only the Python standard library. Open `index.html` locally to review all eight
references. SVGs load the versioned Manrope files by repository-relative path and embed the exact
vector group from the versioned primary or inverted logo variant. They do not use a network
resource or account.

## Privacy and review contract

- All labels and records are synthetic; `person@example.com` uses the reserved example domain.
- Password and passphrase fields contain bullets only.
- No token, recovery key, UUID, payload, clipboard value, account data or production screenshot is
  present.
- A reviewer must inspect both themes, each recovery/error action, logo treatment, clipping and
  contrast before approving the M135 identity package.
- A change to the generator requires regenerating all eight SVGs and reviewing the resulting diff.
