# SafeCube logo assets

These files are the versioned logo source and deterministic exports for SCDK-M135. The owner
master retains its original DOCTYPE byte-for-byte; all derived exports are self-contained and do
not depend on an external document, account, font, or network resource.

## Canonical source

- `master.svg` is copied byte-for-byte from the owner-provided source.
- SHA-256: `79729c8e195797f6eac3708d6a1a65d953077252d3b13494fdaecacc3760c72a`
- Geometry: 37 ordered SVG path `d` attributes.
- Source viewBox: `0 0 1024.000000 1024.000000` (numerically equivalent to `0 0 1024 1024`).
- Every mark-bearing export preserves the master path `d` sequence exactly.
- The source wrapper transform `translate(0.000000,1024.000000) scale(0.100000,-0.100000)` is retained.
  Canvas/viewBox changes and wrapper transforms are the only permitted adaptation; path geometry is
  not edited.

## Exports

| File | Intended use | ViewBox | Artwork |
| --- | --- | --- | --- |
| `primary.svg` | Primary petroleum mark on transparent surfaces | `0 0 1024 1024` | `#006A6A` |
| `inverted.svg` | Inverted mark for dark/petroleum surfaces | `0 0 1024 1024` | `#FFFFFF` |
| `monochrome.svg` | Single-colour black mark | `0 0 1024 1024` | `#000000` |
| `adaptive-foreground.svg` | Adaptive launcher foreground | `0 0 1024 1024` | `#FFFFFF`, transparent |
| `adaptive-background.svg` | Adaptive launcher background | `0 0 1024 1024` | `#006A6A` |
| `round-preview.svg` | Round icon review/export preview | `0 0 1024 1024` | `#006A6A` + `#FFFFFF` |
| `monochrome-themed.svg` | Android themed-icon source | `0 0 1024 1024` | `#000000`, transparent |
| `splash-light.svg` | Light splash concept/export | `0 0 1024 1024` | `#F5FAF9` + `#006A6A` |
| `splash-dark.svg` | Dark splash concept/export | `0 0 1024 1024` | `#0E1515` + `#FFFFFF` |
| `in-app-mark.svg` | Transparent in-app mark | `0 0 1024 1024` | `#006A6A` |

The adaptive foreground and background are separate concept exports so M138 can map them to
Android adaptive-icon resources without reinterpreting the logo. The documented adaptive safe zone
is a 66% diameter centered region. The round file is a review preview, not an Android resource.

## Clearspace and minimum size

- Keep clearspace on every side equal to **1/8 of the rendered mark width**. No text, UI control,
  edge, mask, or other symbol may enter that area.
- Minimum size is **24dp for UI use** and **32dp for standalone, splash or reference use**. For a
  1× documentation export, those values correspond to 24px and 32px respectively. Below 24dp, use
  the distinct product icon defined by M138; do not shrink this logo to fit a smaller slot.
- Preserve the aspect ratio and the path order. Do not stretch, skew, redraw, crop, outline,
  trace, or apply unapproved effects.

## Verification

Run from the repository root:

```text
python3 docs/design/tools/verify-logo-geometry.py
```

The verifier uses only the Python standard library. It checks the master checksum, path count and
ordered path geometry, expected viewBoxes, expected fill colours, and the intentional path-free
adaptive background.
