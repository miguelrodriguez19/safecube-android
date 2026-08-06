#!/usr/bin/env bash

set -euo pipefail

if [ "$#" -ne 2 ]; then
  echo "Usage: $0 <cyclonedx-json> <version-name>" >&2
  exit 64
fi

sbom_path="$1"
version_name="$2"

if [ ! -s "$sbom_path" ]; then
  echo "Release SBOM is missing or empty." >&2
  exit 1
fi

if ! jq -e --arg version_name "$version_name" '
  .bomFormat == "CycloneDX" and
  .specVersion == "1.6" and
  (.version | type == "number") and
  (.metadata | type == "object") and
  (.metadata.component | type == "object") and
  .metadata.component.name == "safecube-android" and
  .metadata.component.version == $version_name and
  (.components | type == "array") and
  (.dependencies | type == "array") and
  (([.metadata.component["bom-ref"]] + [.components[]["bom-ref"]]) as $refs |
    ([.dependencies[].ref] + [.dependencies[].dependsOn[]]) |
    all(. as $ref | $refs | index($ref) != null))
' "$sbom_path" >/dev/null; then
  echo "Release SBOM does not satisfy the SafeCube CycloneDX contract." >&2
  exit 1
fi

if ! jq -e '
  [
    .. | objects | keys[] |
    select(test("(?i)^(password|passwd|secret|token|credential|keystore|api[_-]?key|authorization|private[_-]?key)(s|_.*)?$"))
  ] | length == 0
' "$sbom_path" >/dev/null; then
  echo "Release SBOM contains a forbidden sensitive field." >&2
  exit 1
fi

if ! jq -e '
  [
    .. | strings |
    select(
      test("/home/runner/|/Users/|/private/var/|file:/+|[A-Za-z]:\\\\") or
      test("^[A-Za-z][A-Za-z0-9+.-]*://[^/@[:space:]]+@") or
      test("SAFECUBE_RELEASE_|GITHUB_TOKEN|KEYSTORE_BASE64|-----BEGIN [A-Z ]*PRIVATE KEY-----")
    )
  ] | length == 0
' "$sbom_path" >/dev/null; then
  echo "Release SBOM contains a forbidden path or credential reference." >&2
  exit 1
fi

for forbidden_root in \
  "${GITHUB_WORKSPACE:-}" \
  "${RUNNER_TEMP:-}" \
  "${HOME:-}"; do
  if [ -n "$forbidden_root" ] && ! jq -e --arg forbidden_root "$forbidden_root" '
    [.. | strings | select(contains($forbidden_root))] | length == 0
  ' "$sbom_path" >/dev/null; then
    echo "Release SBOM contains runner-local data." >&2
    exit 1
  fi
done

echo "Release SBOM structure and disclosure checks passed."
