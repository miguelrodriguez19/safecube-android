#!/usr/bin/env bash

#######################################################
#   This script don't run automatically.              #
#                                                     #
#   We are keeping it as a manual regression tool,    #
#   without adding it to any triggers or workflows.   #
#######################################################

set -euo pipefail

SCRIPT_DIRECTORY="$(cd "$(dirname "$0")" && pwd)"
SCRIPT_UNDER_TEST="${SCRIPT_DIRECTORY}/create-immutable-release-tag.sh"
TEMP_DIRECTORY="$(mktemp -d "${RUNNER_TEMP:-/tmp}/safecube-release-tag-test.XXXXXX")"

cleanup() {
  rm -rf "${TEMP_DIRECTORY}"
}
trap cleanup EXIT

FAKE_BIN="${TEMP_DIRECTORY}/bin"
mkdir -p "${FAKE_BIN}"

cat > "${FAKE_BIN}/gh" <<'FAKE_GH'
#!/usr/bin/env bash

set -euo pipefail

if [[ "${1:-}" != "api" ]]; then
  echo "Unexpected gh command." >&2
  exit 2
fi
shift

if [[ " $* " == *" --method POST "* ]]; then
  printf 'POST\n' >> "${FAKE_GH_ACTIONS}"
  if [[ "${FAKE_GH_SCENARIO}" == "concurrent" ]]; then
    printf 'created\n' > "${FAKE_GH_STATE}"
    printf '{"message":"Reference already exists","status":"422"}\n'
    echo 'gh: Reference already exists (HTTP 422)' >&2
    exit 1
  fi
  exit 0
fi

printf 'GET\n' >> "${FAKE_GH_ACTIONS}"
case "${FAKE_GH_SCENARIO}" in
  missing)
    printf '{"message":"Not Found","status":"404"}\n'
    echo 'gh: Not Found (HTTP 404)' >&2
    exit 1
    ;;
  same)
    printf '{"object":{"type":"commit","sha":"%s"}}\n' "${GITHUB_SHA}"
    ;;
  conflict)
    printf '{"object":{"type":"commit","sha":"1111111111111111111111111111111111111111"}}\n'
    ;;
  unexpected)
    printf '{"message":"Service unavailable","status":"503"}\n'
    echo 'gh: Service unavailable (HTTP 503)' >&2
    exit 1
    ;;
  concurrent)
    if [[ -s "${FAKE_GH_STATE}" ]]; then
      printf '{"object":{"type":"commit","sha":"%s"}}\n' "${GITHUB_SHA}"
    else
      printf '{"message":"Not Found","status":"404"}\n'
      echo 'gh: Not Found (HTTP 404)' >&2
      exit 1
    fi
    ;;
  *)
    echo "Unknown fake gh scenario: ${FAKE_GH_SCENARIO}." >&2
    exit 2
    ;;
esac
FAKE_GH
chmod +x "${FAKE_BIN}/gh"

run_case() {
  local scenario="$1"
  local expected_exit="$2"
  local expected_output="$3"
  local case_directory="${TEMP_DIRECTORY}/${scenario}"
  local actual_exit

  mkdir -p "${case_directory}"
  : > "${case_directory}/state"
  : > "${case_directory}/actions"

  set +e
  PATH="${FAKE_BIN}:${PATH}" \
    FAKE_GH_SCENARIO="${scenario}" \
    FAKE_GH_STATE="${case_directory}/state" \
    FAKE_GH_ACTIONS="${case_directory}/actions" \
    GITHUB_REPOSITORY="safecube/android" \
    GITHUB_SHA="2222222222222222222222222222222222222222" \
    RELEASE_TAG="v0.1.7-rc.2" \
    RUNNER_TEMP="${case_directory}" \
    "${SCRIPT_UNDER_TEST}" \
    > "${case_directory}/stdout" \
    2> "${case_directory}/stderr"
  actual_exit=$?
  set -e

  if [[ "${actual_exit}" -ne "${expected_exit}" ]]; then
    echo "Scenario ${scenario} exited ${actual_exit}; expected ${expected_exit}." >&2
    cat "${case_directory}/stdout" >&2
    cat "${case_directory}/stderr" >&2
    exit 1
  fi

  if ! grep -F -q -- "${expected_output}" \
    "${case_directory}/stdout" "${case_directory}/stderr"; then
    echo "Scenario ${scenario} did not emit: ${expected_output}" >&2
    cat "${case_directory}/stdout" >&2
    cat "${case_directory}/stderr" >&2
    exit 1
  fi
}

run_case missing 0 'Created immutable release tag'
run_case same 0 'already points to'
run_case conflict 1 'is immutable and conflicts with this candidate'
run_case unexpected 1 'Unable to inspect release tag'
run_case concurrent 0 'was created concurrently with the expected identity'

if [[ "$(cat "${TEMP_DIRECTORY}/missing/actions")" != $'GET\nPOST' ]]; then
  echo "The missing-tag scenario did not inspect before creating the tag." >&2
  exit 1
fi

echo "Immutable release-tag creation scenarios verified."
