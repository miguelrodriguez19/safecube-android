# Manrope typography assets

Versioned typography package for SafeCube visual identity work (`SCDK-M135`). These files are
the offline source of truth for the selected static Manrope weights. Runtime integration belongs
to a later task and must not download fonts or depend on an external account, URL, or build-time
network access.

## Provenance

- Source archive: `Manrope.zip` supplied by the owner.
- Source archive SHA-256:
  `e96bb520b50404587cbc037e40ef03101d0ece450c7b64e0a41e4650058de926`
- Upstream family: Manrope.
- License: SIL Open Font License 1.1; the complete text is versioned in [`OFL.txt`](OFL.txt).
- Upstream documentation is preserved verbatim in [`README.upstream.txt`](README.upstream.txt).

## Selected static files

| File | Weight | SHA-256 |
| --- | ---: | --- |
| `Manrope-Regular.ttf` | 400 | `6383bd9f81e56d61139884d8e42cb7b2146a11dde4efde55c8bff1e4c2c0bbe8` |
| `Manrope-Medium.ttf` | 500 | `88d3f8ef004b53483a202772d09acaab17ca99dd41905d9b4ab07ac635632378` |
| `Manrope-SemiBold.ttf` | 600 | `abbefa1f58c7355b663c19f29ffe4cd7fc8c93a9e5f8b68f08d1e9ba2bc4ba0d` |
| `Manrope-Bold.ttf` | 700 | `4aed5d180a4f41ed21f07e678486f889bb40eb0ddf5f473769b6302f507d1e36` |

Versioned provenance files:

| File | SHA-256 |
| --- | --- |
| `OFL.txt` | `58d49f25b2cacdfe83739d557ac9319c48bf3ed3e9e33b6678ddb972b475ce7c` |
| `README.upstream.txt` | `a7a60d923e3177d7bed10e602ac1c57c428586dce956cbb31a0893092be28f3c` |

The package intentionally excludes `ExtraLight`, `Light`, `ExtraBold`, and
`Manrope-VariableFont_wght.ttf`. Do not add unselected weights or a variable font without a
versioned design decision and updated verification evidence.

## Usage contract

- Bundle the selected TTF files locally with the application; no downloadable-font provider is
  required or permitted for the v1 typography contract.
- Map the files to weights 400, 500, 600, and 700 in the design-system typography layer.
- Use `sans-serif` as the platform fallback when a selected asset cannot be loaded. The fallback
  must preserve readable text, accessible scaling, and the same semantic hierarchy.
- Keep the four files unchanged. Any derived font must be treated as a modified version and
  verified independently before use.

## Offline redistribution and license constraints

The SIL OFL 1.1 permits the unmodified fonts to be bundled, embedded, and redistributed with the
SafeCube repository and APK, provided that each copy retains the copyright notice and the complete
license. The font software must not be sold by itself and must remain under the OFL; this README
does not replace [`OFL.txt`](OFL.txt).

If a modified font is ever produced, it must retain the OFL and must not use any Reserved Font
Name for the modified version's primary user-facing font name unless the copyright holder grants
written permission. Do not rename, rebrand, or imply endorsement of a modified font using
"Manrope" or another reserved name. Preserve the copyright and attribution notices, and document
the modification and its independent hash before redistribution.

## Integrity check

From this directory, verify the selected assets with:

```sh
shasum -a 256 Manrope-Regular.ttf Manrope-Medium.ttf Manrope-SemiBold.ttf Manrope-Bold.ttf
```

The resulting digests must match the table above. Changes to these binaries require review under
`SPEC-UI-V1` and must update this manifest and its evidence.
