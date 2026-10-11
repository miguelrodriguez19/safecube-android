#!/usr/bin/env python3
"""Verify SafeCube logo geometry and deterministic SVG export contracts."""

from __future__ import annotations

import hashlib
import sys
import xml.etree.ElementTree as ET
from pathlib import Path


ROOT = Path(__file__).resolve().parents[1] / "assets" / "logo"
MASTER_NAME = "master.svg"
MASTER_SHA256 = "79729c8e195797f6eac3708d6a1a65d953077252d3b13494fdaecacc3760c72a"
EXPECTED_PATH_COUNT = 37
EXPECTED_VIEWBOX = (0.0, 0.0, 1024.0, 1024.0)

EXPECTED = {
    "master.svg": {"fills": {"#000000"}, "paths": True},
    "primary.svg": {"fills": {"#006a6a"}, "paths": True},
    "inverted.svg": {"fills": {"#ffffff"}, "paths": True},
    "monochrome.svg": {"fills": {"#000000"}, "paths": True},
    "adaptive-foreground.svg": {"fills": {"#ffffff"}, "paths": True},
    "adaptive-background.svg": {"fills": {"#006a6a"}, "paths": False},
    "round-preview.svg": {"fills": {"#006a6a", "#ffffff"}, "paths": True},
    "monochrome-themed.svg": {"fills": {"#000000"}, "paths": True},
    "splash-light.svg": {"fills": {"#f5faf9", "#006a6a"}, "paths": True},
    "splash-dark.svg": {"fills": {"#0e1515", "#ffffff"}, "paths": True},
    "in-app-mark.svg": {"fills": {"#006a6a"}, "paths": True},
}


def fail(message: str) -> None:
    print(f"FAIL: {message}", file=sys.stderr)


def parse_number_tuple(value: str) -> tuple[float, ...]:
    return tuple(float(part) for part in value.replace(",", " ").split())


def parse_svg(path: Path) -> tuple[ET.Element, list[str], set[str]]:
    try:
        root = ET.fromstring(path.read_bytes())
    except (OSError, ET.ParseError) as error:
        raise ValueError(f"{path.name}: invalid SVG/XML: {error}") from error

    view_box = root.attrib.get("viewBox")
    if view_box is None or parse_number_tuple(view_box) != EXPECTED_VIEWBOX:
        raise ValueError(f"{path.name}: expected viewBox numerically equal to 0 0 1024 1024")

    paths = [
        element.attrib.get("d", "")
        for element in root.iter()
        if element.tag.rsplit("}", 1)[-1] == "path"
    ]
    fills = {
        element.attrib["fill"].lower()
        for element in root.iter()
        if "fill" in element.attrib
    }
    return root, paths, fills


def main() -> int:
    master_path = ROOT / MASTER_NAME
    if not master_path.is_file():
        fail(f"missing {master_path}")
        return 1

    digest = hashlib.sha256(master_path.read_bytes()).hexdigest()
    if digest != MASTER_SHA256:
        fail(f"{MASTER_NAME}: SHA-256 {digest} does not match owner source")
        return 1

    try:
        _, master_paths, master_fills = parse_svg(master_path)
    except ValueError as error:
        fail(str(error))
        return 1

    if len(master_paths) != EXPECTED_PATH_COUNT:
        fail(f"{MASTER_NAME}: expected {EXPECTED_PATH_COUNT} paths, found {len(master_paths)}")
        return 1

    if master_fills != EXPECTED[MASTER_NAME]["fills"]:
        fail(f"{MASTER_NAME}: unexpected fill set {sorted(master_fills)}")
        return 1

    failures = 0
    for name, contract in EXPECTED.items():
        path = ROOT / name
        if not path.is_file():
            fail(f"missing {path}")
            failures += 1
            continue
        try:
            _, paths, fills = parse_svg(path)
        except ValueError as error:
            fail(str(error))
            failures += 1
            continue

        if fills != contract["fills"]:
            fail(f"{name}: expected fills {sorted(contract['fills'])}, found {sorted(fills)}")
            failures += 1

        if contract["paths"]:
            if paths != master_paths:
                fail(f"{name}: ordered path d sequence differs from {MASTER_NAME}")
                failures += 1
            if len(paths) != EXPECTED_PATH_COUNT:
                fail(f"{name}: expected {EXPECTED_PATH_COUNT} paths, found {len(paths)}")
                failures += 1
        elif paths:
            fail(f"{name}: expected an intentional path-free export, found {len(paths)} paths")
            failures += 1

    if failures:
        print(f"Logo verification failed with {failures} issue(s).", file=sys.stderr)
        return 1

    print(
        f"Logo verification passed: {len(EXPECTED)} exports, "
        f"{EXPECTED_PATH_COUNT} master paths, checksum and colour/viewBox contracts valid."
    )
    return 0


if __name__ == "__main__":
    raise SystemExit(main())
