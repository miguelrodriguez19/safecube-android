#!/usr/bin/env python3
"""Verify the SCDK-M135 palette roles and documented contrast table."""

from pathlib import Path
import re
import sys


GUIDE = Path(__file__).resolve().parents[1] / "brand-guide-v1.md"
MOCKUP_GENERATOR = Path(__file__).resolve().parents[1] / "mockups" / "generate_mockups.py"

EXPECTED_ROLES = {
    "Light": {
        "primary": "#006A6A", "onPrimary": "#FFFFFF",
        "primaryContainer": "#9CF1EF", "onPrimaryContainer": "#002020",
        "secondary": "#4A6363", "onSecondary": "#FFFFFF",
        "secondaryContainer": "#CCE8E7", "onSecondaryContainer": "#051F1F",
        "tertiary": "#8B4A20", "onTertiary": "#FFFFFF",
        "tertiaryContainer": "#FFDCC7", "onTertiaryContainer": "#321200",
        "error": "#BA1A1A", "onError": "#FFFFFF",
        "background": "#F5FAF9", "onBackground": "#171D1D",
        "surface": "#F5FAF9", "onSurface": "#171D1D",
        "surfaceVariant": "#DAE5E4", "onSurfaceVariant": "#3F4948",
        "outline": "#6F7978",
    },
    "Dark": {
        "primary": "#80D5D3", "onPrimary": "#003737",
        "primaryContainer": "#00504F", "onPrimaryContainer": "#9CF1EF",
        "secondary": "#B1CCCC", "onSecondary": "#1C3535",
        "secondaryContainer": "#334B4B", "onSecondaryContainer": "#CCE8E7",
        "tertiary": "#FFB68C", "onTertiary": "#512400",
        "tertiaryContainer": "#6D330A", "onTertiaryContainer": "#FFDCC7",
        "error": "#FFB4AB", "onError": "#690005",
        "background": "#0E1515", "onBackground": "#DEE4E3",
        "surface": "#0E1515", "onSurface": "#DEE4E3",
        "surfaceVariant": "#3F4948", "onSurfaceVariant": "#BEC9C8",
        "outline": "#899392",
    },
}


def channel(value: int) -> float:
    normalized = value / 255
    return normalized / 12.92 if normalized <= 0.04045 else ((normalized + 0.055) / 1.055) ** 2.4


def luminance(value: str) -> float:
    red, green, blue = (int(value[index:index + 2], 16) for index in (1, 3, 5))
    return 0.2126 * channel(red) + 0.7152 * channel(green) + 0.0722 * channel(blue)


def contrast(first: str, second: str) -> float:
    lighter, darker = sorted((luminance(first), luminance(second)), reverse=True)
    return (lighter + 0.05) / (darker + 0.05)


def parse_role_table(markdown: str, heading: str, next_heading: str) -> dict[str, str]:
    section = markdown.split(heading, 1)[1].split(next_heading, 1)[0]
    rows = re.findall(r"^\| `([^`]+)` \| `(#[0-9A-F]{6})` \|", section, re.MULTILINE)
    roles: dict[str, str] = {}
    for role, value in rows:
        if role in roles:
            raise ValueError(f"duplicate {heading} role: {role}")
        roles[role] = value
    return roles


def main() -> int:
    markdown = GUIDE.read_text(encoding="utf-8")
    observed = {
        "Light": parse_role_table(markdown, "### 5.1 Tema claro", "### 5.2 Tema oscuro"),
        "Dark": parse_role_table(markdown, "### 5.2 Tema oscuro", "### 5.3 Tabla WCAG"),
    }
    if observed != EXPECTED_ROLES:
        print("Palette role mismatch", file=sys.stderr)
        return 1

    contrast_rows = re.findall(
        r"^\| (Light|Dark) \| `[^`]+ (#[0-9A-F]{6})` \| `[^`]+ (#[0-9A-F]{6})` "
        r"\| `([0-9]+\.[0-9]{2}):1` \| (AA texto|AA gráfico; no body text) \|$",
        markdown,
        re.MULTILINE,
    )
    if len(contrast_rows) != 38:
        print(f"Expected 38 contrast rows, observed {len(contrast_rows)}", file=sys.stderr)
        return 1

    for theme, foreground, background, documented, gate in contrast_rows:
        actual = contrast(foreground, background)
        threshold = 3.0 if gate.startswith("AA gráfico") else 4.5
        if round(actual, 2) != float(documented):
            print(
                f"{theme} {foreground}/{background}: documented {documented}, actual {actual:.2f}",
                file=sys.stderr,
            )
            return 1
        if actual < threshold:
            print(f"{theme} {foreground}/{background}: {actual:.2f} below {threshold:.2f}", file=sys.stderr)
            return 1

    generator = MOCKUP_GENERATOR.read_text(encoding="utf-8")
    allowed_values = {value for theme in EXPECTED_ROLES.values() for value in theme.values()}
    generator_values = set(re.findall(r"#[0-9A-F]{6}", generator))
    unexpected = generator_values - allowed_values
    if unexpected:
        print(f"Mockup generator uses colors outside the normative palette: {sorted(unexpected)}", file=sys.stderr)
        return 1
    forbidden_glyphs = {glyph for glyph in ("◆", "⚙", "✓") if glyph in generator}
    if forbidden_glyphs:
        print(f"Mockup generator uses fallback-dependent icon glyphs: {sorted(forbidden_glyphs)}", file=sys.stderr)
        return 1

    print(
        "Brand palette verification passed: 42 unique roles, 38 contrast pairs and mockup color/icon contracts valid."
    )
    return 0


if __name__ == "__main__":
    raise SystemExit(main())
