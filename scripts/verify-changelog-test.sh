#!/usr/bin/env bash

set -euo pipefail

SCRIPT_DIRECTORY="$(cd -- "$(dirname -- "${BASH_SOURCE[0]}")" && pwd)"
REPOSITORY_DIRECTORY="$(cd -- "$SCRIPT_DIRECTORY/.." && pwd)"
GENERATOR_SCRIPT="$REPOSITORY_DIRECTORY/scripts/generate-changelog.sh"
TEMP_DIRECTORY="$(mktemp -d "${RUNNER_TEMP:-/tmp}/safecube-changelog-test.XXXXXX")"

cleanup() {
  rm -rf "$TEMP_DIRECTORY"
}
trap cleanup EXIT

if ! command -v git-cliff >/dev/null 2>&1; then
  echo "git-cliff is required to verify changelog generation." >&2
  exit 1
fi

git -C "$TEMP_DIRECTORY" init --quiet
git -C "$TEMP_DIRECTORY" config user.name "SafeCube changelog test"
git -C "$TEMP_DIRECTORY" config user.email "changelog-test@safecube.invalid"
cp "$REPOSITORY_DIRECTORY/cliff.toml" "$TEMP_DIRECTORY/cliff.toml"
mkdir -p "$TEMP_DIRECTORY/scripts"
cp "$GENERATOR_SCRIPT" "$TEMP_DIRECTORY/scripts/generate-changelog.sh"
chmod +x "$TEMP_DIRECTORY/scripts/generate-changelog.sh"

commit() {
  local message="$1"
  printf '%s\n' "$message" > "$TEMP_DIRECTORY/change.txt"
  git -C "$TEMP_DIRECTORY" add change.txt
  git -C "$TEMP_DIRECTORY" commit --quiet -m "$message"
}

commit 'chore(SCDK-M105): create changelog test baseline'
git -C "$TEMP_DIRECTORY" tag v0.1.0
commit 'feat(SCDK-M105): add a deterministic feature'
commit 'fix(SCDK-M105): handle a deterministic failure'
git -C "$TEMP_DIRECTORY" tag v0.1.1
commit 'docs(SCDK-M105): add a commit outside the selected range'

first_output="$TEMP_DIRECTORY/notes-first.md"
second_output="$TEMP_DIRECTORY/notes-second.md"
(
  cd "$TEMP_DIRECTORY"
  ./scripts/generate-changelog.sh v0.1.0 v0.1.1 "$first_output"
  ./scripts/generate-changelog.sh v0.1.0 v0.1.1 "$second_output"
)

if ! cmp -s "$first_output" "$second_output"; then
  echo "The same tag range produced different changelog output." >&2
  exit 1
fi

grep -F -q 'Add a deterministic feature' "$first_output"
grep -F -q 'Handle a deterministic failure' "$first_output"
if grep -F -q 'Add a commit outside the selected range' "$first_output"; then
  echo "The changelog included a commit outside the selected tag range." >&2
  exit 1
fi

echo "Deterministic changelog range generation verified."
