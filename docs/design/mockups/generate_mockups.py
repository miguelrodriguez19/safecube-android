#!/usr/bin/env python3
"""Generate the deterministic SCDK-M135 pilot mockups."""

from html import escape
from pathlib import Path
import re


ROOT = Path(__file__).resolve().parent
WIDTH = 390
HEIGHT = 844


def load_logo_group(variant):
    source = (ROOT.parent / "assets" / "logo" / variant).read_text(encoding="utf-8")
    match = re.search(r"(<g\b.*</g>)", source, re.DOTALL)
    if match is None:
        raise ValueError(f"Logo variant has no SVG group: {variant}")
    return match.group(1)


PRIMARY_LOGO = load_logo_group("primary.svg")
INVERTED_LOGO = load_logo_group("inverted.svg")

LIGHT = {
    "background": "#F5FAF9",
    "surface": "#F5FAF9",
    "surface_variant": "#DAE5E4",
    "on_surface": "#171D1D",
    "muted": "#3F4948",
    "primary": "#006A6A",
    "on_primary": "#FFFFFF",
    "primary_container": "#9CF1EF",
    "on_primary_container": "#002020",
    "tertiary": "#8B4A20",
    "tertiary_container": "#FFDCC7",
    "on_tertiary_container": "#321200",
    "error": "#BA1A1A",
    "on_error": "#FFFFFF",
    "outline": "#6F7978",
}

DARK = {
    "background": "#0E1515",
    "surface": "#0E1515",
    "surface_variant": "#3F4948",
    "on_surface": "#DEE4E3",
    "muted": "#BEC9C8",
    "primary": "#80D5D3",
    "on_primary": "#003737",
    "primary_container": "#00504F",
    "on_primary_container": "#9CF1EF",
    "tertiary": "#FFB68C",
    "tertiary_container": "#6D330A",
    "on_tertiary_container": "#FFDCC7",
    "error": "#FFB4AB",
    "on_error": "#690005",
    "outline": "#899392",
}


def text(x, y, value, size, color, weight=400, anchor="start"):
    return (
        f'<text x="{x}" y="{y}" font-size="{size}" fill="{color}" '
        f'font-weight="{weight}" text-anchor="{anchor}">{escape(value)}</text>'
    )


def rect(x, y, width, height, fill, radius=0, stroke="none", stroke_width=0):
    return (
        f'<rect x="{x}" y="{y}" width="{width}" height="{height}" rx="{radius}" '
        f'fill="{fill}" stroke="{stroke}" stroke-width="{stroke_width}"/>'
    )


def line(x1, y1, x2, y2, stroke, width=2):
    return f'<line x1="{x1}" y1="{y1}" x2="{x2}" y2="{y2}" stroke="{stroke}" stroke-width="{width}" stroke-linecap="round"/>'


def diamond(cx, cy, color, size=5):
    return f'<path d="M {cx} {cy - size} L {cx + size} {cy} L {cx} {cy + size} L {cx - size} {cy} Z" fill="{color}"/>'


def check(cx, cy, color):
    return f'<path d="M {cx - 5} {cy} L {cx - 1} {cy + 4} L {cx + 6} {cy - 5}" fill="none" stroke="{color}" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>'


def plus(cx, cy, color):
    return line(cx - 6, cy, cx + 6, cy, color, 3) + line(cx, cy - 6, cx, cy + 6, color, 3)


def settings_icon(cx, cy, color):
    return "".join([
        f'<circle cx="{cx}" cy="{cy}" r="5" fill="none" stroke="{color}" stroke-width="1.8"/>',
        f'<circle cx="{cx}" cy="{cy}" r="1.8" fill="{color}"/>',
        line(cx, cy - 8, cx, cy - 6, color, 1.8),
        line(cx, cy + 6, cx, cy + 8, color, 1.8),
        line(cx - 8, cy, cx - 6, cy, color, 1.8),
        line(cx + 6, cy, cx + 8, cy, color, 1.8),
    ])


def sync_icon(cx, cy, color):
    return "".join([
        f'<path d="M {cx - 8} {cy - 1} A 9 9 0 0 1 {cx + 5} {cy - 7}" fill="none" stroke="{color}" stroke-width="2" stroke-linecap="round"/>',
        f'<path d="M {cx + 4} {cy - 11} L {cx + 8} {cy - 7} L {cx + 3} {cy - 5}" fill="none" stroke="{color}" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>',
        f'<path d="M {cx + 8} {cy + 1} A 9 9 0 0 1 {cx - 5} {cy + 7}" fill="none" stroke="{color}" stroke-width="2" stroke-linecap="round"/>',
        f'<path d="M {cx - 4} {cy + 11} L {cx - 8} {cy + 7} L {cx - 3} {cy + 5}" fill="none" stroke="{color}" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>',
    ])


def button(y, label, c, kind="primary", width=326):
    x = (WIDTH - width) // 2
    if kind == "primary":
        fill, foreground, stroke = c["primary"], c["on_primary"], "none"
    elif kind == "tonal":
        fill, foreground, stroke = c["primary_container"], c["on_primary_container"], "none"
    else:
        fill, foreground, stroke = "none", c["primary"], c["outline"]
    return "".join([
        rect(x, y, width, 52, fill, 16, stroke, 1 if stroke != "none" else 0),
        text(WIDTH / 2, y + 33, label, 15, foreground, 700, "middle"),
    ])


def field(y, label, value, c, secret=False, error=None):
    body = [text(40, y, label, 12, c["muted"], 600)]
    border = c["error"] if error else c["outline"]
    body.append(rect(32, y + 10, 326, 58, "none", 14, border, 1.5))
    body.append(text(48, y + 47, "••••••••••" if secret else value, 15, c["on_surface"], 500))
    if secret:
        body.append(text(330, y + 47, "Show", 12, c["primary"], 700, "end"))
    if error:
        body.append(text(40, y + 88, error, 12, c["error"], 500))
    return "".join(body)


def status_bar(c):
    return "".join([
        text(24, 28, "9:41", 12, c["on_surface"], 700),
        line(326, 20, 340, 20, c["on_surface"], 2),
        line(346, 18, 346, 23, c["on_surface"], 2),
        rect(354, 16, 18, 9, "none", 2, c["on_surface"], 1.5),
        rect(356, 18, 13, 5, c["on_surface"], 1),
    ])


def brand(y, c, inverted=False):
    logo = INVERTED_LOGO if inverted else PRIMARY_LOGO
    return "".join([
        f'<svg x="157" y="{y}" width="76" height="76" viewBox="0 0 1024 1024" aria-hidden="true">{logo}</svg>',
        text(WIDTH / 2, y + 100, "SafeCube", 24, c["on_surface"], 700, "middle"),
    ])


def shell(title, subtitle, c, content, theme, state):
    style = """
      @font-face { font-family: Manrope; src: url('../assets/type/manrope/Manrope-Regular.ttf'); font-weight: 400; }
      @font-face { font-family: Manrope; src: url('../assets/type/manrope/Manrope-Medium.ttf'); font-weight: 500; }
      @font-face { font-family: Manrope; src: url('../assets/type/manrope/Manrope-SemiBold.ttf'); font-weight: 600; }
      @font-face { font-family: Manrope; src: url('../assets/type/manrope/Manrope-Bold.ttf'); font-weight: 700; }
      text { font-family: Manrope, sans-serif; }
    """
    return f'''<svg xmlns="http://www.w3.org/2000/svg" width="{WIDTH}" height="{HEIGHT}" viewBox="0 0 {WIDTH} {HEIGHT}" role="img" aria-labelledby="title desc">
  <title id="title">{escape(title)} — {theme}</title>
  <desc id="desc">{escape(subtitle)}. Representative state: {escape(state)}.</desc>
  <style>{style}</style>
  {rect(0, 0, WIDTH, HEIGHT, c["background"])}
  {status_bar(c)}
  {content}
  {text(24, 824, f'{theme} · {state}', 10, c["muted"], 600)}
</svg>
'''


def welcome(c, theme):
    inverted = theme == "Dark"
    body = "".join([
        brand(112, c, inverted),
        text(WIDTH / 2, 310, "Your private space,", 28, c["on_surface"], 700, "middle"),
        text(WIDTH / 2, 346, "always within reach", 28, c["on_surface"], 700, "middle"),
        text(WIDTH / 2, 398, "Protect passwords and notes with", 14, c["muted"], 400, "middle"),
        text(WIDTH / 2, 420, "encryption designed around your control.", 14, c["muted"], 400, "middle"),
        button(520, "Log in", c),
        button(588, "Create account", c, "outline"),
        rect(72, 692, 246, 54, c["primary_container"], 16),
        text(WIDTH / 2, 715, "Encrypted before sync", 12, c["on_primary_container"], 700, "middle"),
        text(WIDTH / 2, 735, "Your vault stays yours", 11, c["on_primary_container"], 400, "middle"),
    ])
    return shell("Welcome", "SafeCube entry screen", c, body, theme, "Content")


def login(c, theme, retryable=False):
    body = [
        text(32, 96, "Welcome back", 28, c["on_surface"], 700),
        text(32, 124, "Log in to continue to your vault.", 14, c["muted"]),
        field(184, "Email", "person@example.com", c),
        field(288, "Account password", "", c, secret=True),
    ]
    if retryable:
        body.extend([
            rect(32, 406, 326, 96, c["error"], 16),
            text(52, 436, "Couldn't connect", 14, c["on_error"], 700),
            text(52, 460, "Check your connection. Your vault data", 12, c["on_error"]),
            text(52, 480, "has not been changed.", 12, c["on_error"]),
            button(530, "Try again", c),
        ])
    else:
        body.extend([
            button(420, "Log in", c),
            text(WIDTH / 2, 502, "New to SafeCube? Create account", 13, c["primary"], 600, "middle"),
        ])
    return shell("Login", "Account authentication form", c, "".join(body), theme, "Retryable error" if retryable else "Idle")


def unlock(c, theme, quick_error=False):
    body = [
        text(32, 94, "Unlock your vault", 28, c["on_surface"], 700),
        text(32, 124, "Your account is active. The vault is locked.", 14, c["muted"]),
    ]
    if quick_error:
        body.extend([
            rect(32, 172, 326, 118, c["surface"], 18, c["outline"], 1),
            text(52, 207, "Quick unlock unavailable", 15, c["on_surface"], 700),
            text(52, 233, "Device verification was cancelled.", 12, c["muted"]),
            text(52, 256, "Use your vault passphrase instead.", 12, c["muted"]),
            field(338, "Vault passphrase", "", c, secret=True),
            button(458, "Unlock with passphrase", c),
            button(526, "Try quick unlock again", c, "outline"),
        ])
    else:
        body.extend([
            rect(32, 172, 326, 72, c["primary_container"], 18),
            text(52, 202, "Why is my vault locked?", 13, c["on_primary_container"], 700),
            text(52, 224, "Lock protects decrypted data on this device.", 11, c["on_primary_container"]),
            field(294, "Vault passphrase", "", c, secret=True),
            button(414, "Unlock vault", c),
        ])
    return shell("Unlock", "Locked vault with passphrase fallback", c, "".join(body), theme, "Quick unlock fallback" if quick_error else "Locked")


def bottom_nav(c, selected="Vault"):
    return "".join([
        rect(0, 758, WIDTH, 70, c["surface"], 0),
        line(0, 758, WIDTH, 758, c["surface_variant"], 1),
        diamond(112, 786, c["primary"] if selected == "Vault" else c["muted"], 5),
        text(112, 812, "Vault", 11, c["primary"] if selected == "Vault" else c["muted"], 700, "middle"),
        settings_icon(278, 786, c["muted"]),
        text(278, 812, "Settings", 11, c["muted"], 600, "middle"),
    ])


def item_card(y, title_value, item_type, c, badge=None):
    content = [rect(24, y, 342, 92, c["surface"], 16, c["surface_variant"], 1)]
    content.append(text(40, y + 34, title_value, 14, c["on_surface"], 700))
    content.append(text(40, y + 61, item_type, 12, c["muted"]))
    if badge:
        badge_width = 62 if badge == "Draft" else 76
        badge_x = 350 - badge_width
        content.append(rect(badge_x, y + 17, badge_width, 24, c["tertiary_container"], 12))
        content.append(text(badge_x + badge_width / 2, y + 33, badge, 10, c["tertiary"], 700, "middle"))
    return "".join(content)


def sync_button(c):
    return "".join([
        '<g role="img" aria-label="Sync vault">',
        rect(318, 62, 48, 48, c["primary"], 16),
        sync_icon(342, 86, c["on_primary"]),
        '</g>',
    ])


def create_fab(c):
    return "".join([
        '<g role="img" aria-label="Create item">',
        rect(310, 682, 56, 56, c["primary"], 18),
        plus(338, 710, c["on_primary"]),
        '</g>',
    ])


def vault(c, theme, issue=False):
    body = [
        text(24, 88, "Your vault", 28, c["on_surface"], 700),
        text(24, 114, "Available securely on this device", 13, c["muted"]),
        sync_button(c),
    ]
    if issue:
        body.extend([
            rect(24, 142, 342, 82, c["tertiary_container"], 16),
            text(44, 172, "Sync paused", 14, c["tertiary"], 700),
            text(44, 198, "Your local items remain available.", 12, c["on_tertiary_container"]),
            item_card(248, "Example login", "Password", c, "Draft"),
            item_card(356, "Example note", "Secure note · Needs review", c, "Conflict"),
            item_card(464, "Synthetic record", "Password", c),
        ])
    else:
        body.extend([
            rect(24, 142, 342, 54, c["primary_container"], 14),
            check(50, 170, c["on_primary_container"]),
            text(62, 175, "Synced just now", 12, c["on_primary_container"], 700),
            item_card(220, "Example login", "Password", c),
            item_card(328, "Example note", "Secure note", c),
            item_card(436, "Synthetic record", "Password", c),
        ])
    body.append(create_fab(c))
    body.append(bottom_nav(c))
    return shell("Vault", "Local vault content and synchronization status", c, "".join(body), theme, "Offline, draft and conflict" if issue else "Content")


MOCKUPS = {
    "welcome-light.svg": welcome(LIGHT, "Light"),
    "welcome-dark.svg": welcome(DARK, "Dark"),
    "login-light.svg": login(LIGHT, "Light"),
    "login-dark.svg": login(DARK, "Dark", retryable=True),
    "unlock-light.svg": unlock(LIGHT, "Light"),
    "unlock-dark.svg": unlock(DARK, "Dark", quick_error=True),
    "vault-light.svg": vault(LIGHT, "Light"),
    "vault-dark.svg": vault(DARK, "Dark", issue=True),
}


def generate_index():
    cards = "\n".join(
        f'<figure><img src="{name}" alt="{name.removesuffix(".svg").replace("-", " ")}"><figcaption>{name}</figcaption></figure>'
        for name in MOCKUPS
    )
    return f'''<!doctype html>
<html lang="en">
<head>
  <meta charset="utf-8">
  <meta name="viewport" content="width=device-width,initial-scale=1">
  <title>SafeCube pilot mockups</title>
  <style>
    @font-face {{ font-family: Manrope; src: url('../assets/type/manrope/Manrope-Regular.ttf'); font-weight: 400; }}
    @font-face {{ font-family: Manrope; src: url('../assets/type/manrope/Manrope-Bold.ttf'); font-weight: 700; }}
    * {{ box-sizing: border-box; }}
    body {{ margin: 0; padding: 32px; background: #DAE5E4; color: #171D1D; font-family: Manrope, sans-serif; }}
    h1 {{ margin: 0 0 8px; }}
    p {{ margin: 0 0 28px; max-width: 72ch; }}
    main {{ display: grid; grid-template-columns: repeat(auto-fit, minmax(390px, 1fr)); gap: 28px; align-items: start; }}
    figure {{ margin: 0; display: grid; justify-content: center; gap: 8px; }}
    img {{ width: 390px; height: 844px; border-radius: 30px; box-shadow: 0 10px 28px #00202033; background: white; }}
    figcaption {{ text-align: center; font-size: 13px; font-weight: 700; }}
  </style>
</head>
<body>
  <h1>SafeCube pilot mockups</h1>
  <p>SCDK-M135 review sheet. All content is synthetic; these references do not implement runtime UI.</p>
  <main>{cards}</main>
</body>
</html>
'''


def main():
    for name, body in MOCKUPS.items():
        (ROOT / name).write_text(body, encoding="utf-8")
    (ROOT / "index.html").write_text(generate_index(), encoding="utf-8")
    print(f"Generated {len(MOCKUPS)} mockups and index.html in {ROOT}")


if __name__ == "__main__":
    main()
