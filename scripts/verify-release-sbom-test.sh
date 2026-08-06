#!/usr/bin/env bash

set -euo pipefail

script_dir="$(cd -- "$(dirname -- "${BASH_SOURCE[0]}")" && pwd)"
verifier="$script_dir/verify-release-sbom.sh"
temporary_directory="$(mktemp -d)"
trap 'rm -rf "$temporary_directory"' EXIT

valid_sbom="$temporary_directory/valid.cdx.json"
jq -n '{
  bomFormat: "CycloneDX",
  specVersion: "1.6",
  version: 1,
  metadata: {
    component: {
      type: "application",
      "bom-ref": "pkg:maven/com.miguelrodriguez19.safecube/safecube-android@0.1.7-rc.3",
      name: "safecube-android",
      version: "0.1.7-rc.3"
    }
  },
  components: [{
    type: "library",
    "bom-ref": "pkg:maven/example/library@1.2.3",
    name: "library",
    version: "1.2.3"
  }],
  dependencies: [{
    ref: "pkg:maven/com.miguelrodriguez19.safecube/safecube-android@0.1.7-rc.3",
    dependsOn: ["pkg:maven/example/library@1.2.3"]
  }]
}' > "$valid_sbom"

"$verifier" "$valid_sbom" "0.1.7-rc.3" >/dev/null

expect_rejected() {
  local fixture_path="$1"
  local expected_marker="$2"
  local output_path="$temporary_directory/output.txt"

  if "$verifier" "$fixture_path" "0.1.7-rc.3" >"$output_path" 2>&1; then
    echo "The verifier accepted an invalid SBOM fixture: $fixture_path" >&2
    exit 1
  fi
  if grep -Fq "$expected_marker" "$output_path"; then
    echo "The verifier exposed a rejected fixture value in its output." >&2
    exit 1
  fi
}

local_path_fixture="$temporary_directory/local-path.cdx.json"
jq '.components[0].description = "/Users/example/secret-project"' \
  "$valid_sbom" > "$local_path_fixture"
expect_rejected "$local_path_fixture" "/Users/example/secret-project"

credential_url_fixture="$temporary_directory/credential-url.cdx.json"
jq '.components[0].externalReferences = [{type: "distribution", url: "https://fixture-user:fixture-password@example.test/library"}]' \
  "$valid_sbom" > "$credential_url_fixture"
expect_rejected "$credential_url_fixture" "fixture-password"

sensitive_field_fixture="$temporary_directory/sensitive-field.cdx.json"
jq '.metadata.component.password = "fixture-password"' \
  "$valid_sbom" > "$sensitive_field_fixture"
expect_rejected "$sensitive_field_fixture" "fixture-password"

wrong_version_fixture="$temporary_directory/wrong-version.cdx.json"
jq '.metadata.component.version = "0.1.7-rc.2"' \
  "$valid_sbom" > "$wrong_version_fixture"
expect_rejected "$wrong_version_fixture" "0.1.7-rc.2"

dangling_reference_fixture="$temporary_directory/dangling-reference.cdx.json"
jq '.dependencies[0].dependsOn = ["pkg:maven/example/missing@9.9.9"]' \
  "$valid_sbom" > "$dangling_reference_fixture"
expect_rejected "$dangling_reference_fixture" "pkg:maven/example/missing@9.9.9"

echo "Release SBOM verifier scenarios passed."
