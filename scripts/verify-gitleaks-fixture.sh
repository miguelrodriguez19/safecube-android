#!/usr/bin/env bash

set -euo pipefail

SCRIPT_DIR="$(cd "$(dirname "$0")" && pwd)"
REPOSITORY_ROOT="$(cd "${SCRIPT_DIR}/.." && pwd)"
GITLEAKS_BIN="${GITLEAKS_BIN:-gitleaks}"
CONFIG_FILE="${REPOSITORY_ROOT}/.gitleaks.toml"
ALLOWED_FIXTURE="${REPOSITORY_ROOT}/tests/fixtures/gitleaks/allowed-synthetic-secret.txt"
TEMP_DIR="$(mktemp -d "${RUNNER_TEMP:-/tmp}/safecube-gitleaks.XXXXXX")"

cleanup() {
  rm -rf "${TEMP_DIR}"
}
trap cleanup EXIT

command -v "${GITLEAKS_BIN}" >/dev/null 2>&1 || {
  echo "Gitleaks is required to verify the synthetic fixture." >&2
  exit 1
}

"${GITLEAKS_BIN}" detect \
  --source "${ALLOWED_FIXTURE}" \
  --no-git \
  --config "${CONFIG_FILE}" \
  --redact \
  --no-banner \
  --exit-code=1 \
  > "${TEMP_DIR}/allowed.stdout" \
  2> "${TEMP_DIR}/allowed.stderr"

CONTROL_FIXTURE="${TEMP_DIR}/control-synthetic-secret.txt"
CONTROL_PREFIX="SAFE_CUBE_TEST_"
CONTROL_MARKER="SECRET_"
CONTROL_PART_ONE="A1B2C3D4"
CONTROL_PART_TWO="E5F60708"
CONTROL_PART_THREE="091A2B3C"
CONTROL_PART_FOUR="4D5E6F70"
CONTROL_VALUE="${CONTROL_PREFIX}${CONTROL_MARKER}${CONTROL_PART_ONE}${CONTROL_PART_TWO}${CONTROL_PART_THREE}${CONTROL_PART_FOUR}"
CONTROL_REPORT="${TEMP_DIR}/control-report.json"
printf '%s\n' "${CONTROL_VALUE}" > "${CONTROL_FIXTURE}"

set +e
"${GITLEAKS_BIN}" detect \
  --source "${CONTROL_FIXTURE}" \
  --no-git \
  --config "${CONFIG_FILE}" \
  --redact \
  --no-banner \
  --exit-code=1 \
  --report-format=json \
  --report-path="${CONTROL_REPORT}" \
  > "${TEMP_DIR}/control.stdout" \
  2> "${TEMP_DIR}/control.stderr"
SCAN_EXIT=$?
set -e

if [[ "${SCAN_EXIT}" -ne 1 ]]; then
  echo "The control fixture did not produce the expected Gitleaks finding." >&2
  exit 1
fi

if ! grep -F -q -- 'safecube-synthetic-secret' "${CONTROL_REPORT}"; then
  echo "The control fixture was not reported by the SafeCube synthetic rule." >&2
  exit 1
fi

if grep -F -q -- "${CONTROL_VALUE}" \
  "${TEMP_DIR}/control.stdout" \
  "${TEMP_DIR}/control.stderr" \
  "${CONTROL_REPORT}"; then
  echo "Gitleaks control output contained the synthetic secret instead of redacting it." >&2
  exit 1
fi

echo "Synthetic fixture detection and redaction verified."
