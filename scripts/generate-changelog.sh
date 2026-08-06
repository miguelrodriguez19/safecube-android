#!/usr/bin/env bash

set -euo pipefail

SCRIPT_DIRECTORY="$(cd -- "$(dirname -- "${BASH_SOURCE[0]}")" && pwd)"
REPOSITORY_DIRECTORY="$(cd -- "$SCRIPT_DIRECTORY/.." && pwd)"
CONFIGURATION_FILE="$REPOSITORY_DIRECTORY/cliff.toml"

usage() {
  cat >&2 <<'EOF'
Usage:
  scripts/generate-changelog.sh
  scripts/generate-changelog.sh <previous-tag> <current-tag> [output-file]

Without tags, regenerates CHANGELOG.md from the repository history. With two
tags, generates only the commits reachable from current-tag and not previous-tag.
EOF
}

if ! command -v git-cliff >/dev/null 2>&1; then
  echo "git-cliff is required; install the pinned version documented in docs/release/release-policy.md." >&2
  exit 1
fi

cd "$REPOSITORY_DIRECTORY"

if [ "$#" -eq 0 ]; then
  git-cliff \
    --config "$CONFIGURATION_FILE" \
    --no-exec \
    --output "$REPOSITORY_DIRECTORY/CHANGELOG.md"
  exit 0
fi

if [ "$#" -lt 2 ] || [ "$#" -gt 3 ]; then
  usage
  exit 2
fi

previous_tag="$1"
current_tag="$2"
output_file="${3:-$REPOSITORY_DIRECTORY/CHANGELOG.md}"

for tag in "$previous_tag" "$current_tag"; do
  if ! git -C "$REPOSITORY_DIRECTORY" rev-parse --verify --quiet "refs/tags/$tag^{commit}" >/dev/null; then
    echo "Tag does not exist: $tag" >&2
    exit 1
  fi
done

if ! git -C "$REPOSITORY_DIRECTORY" merge-base --is-ancestor "$previous_tag" "$current_tag"; then
  echo "Previous tag is not an ancestor of current tag: $previous_tag..$current_tag" >&2
  exit 1
fi

git-cliff \
  --config "$CONFIGURATION_FILE" \
  --no-exec \
  --tag "$current_tag" \
  --output "$output_file" \
  "$previous_tag..$current_tag"
