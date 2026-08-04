#!/usr/bin/env bash

set -euo pipefail

for required_name in GITHUB_REPOSITORY GITHUB_SHA RELEASE_TAG; do
  if [[ -z "${!required_name:-}" ]]; then
    echo "Required release-tag variable is unavailable: ${required_name}." >&2
    exit 1
  fi
done

command -v gh >/dev/null 2>&1 || {
  echo "GitHub CLI is required to create the release tag." >&2
  exit 1
}

command -v jq >/dev/null 2>&1 || {
  echo "jq is required to inspect the release tag." >&2
  exit 1
}

TEMP_DIRECTORY="$(mktemp -d "${RUNNER_TEMP:-/tmp}/safecube-release-tag.XXXXXX")"
TAG_RESPONSE="${TEMP_DIRECTORY}/tag-response.json"
TAG_ERROR="${TEMP_DIRECTORY}/tag-error.log"
CREATE_ERROR="${TEMP_DIRECTORY}/create-error.log"
TAG_REF_ENDPOINT="repos/${GITHUB_REPOSITORY}/git/ref/tags/${RELEASE_TAG}"

cleanup() {
  rm -rf "${TEMP_DIRECTORY}"
}
trap cleanup EXIT

read_tag_ref() {
  : > "${TAG_RESPONSE}"
  : > "${TAG_ERROR}"

  local api_exit
  if gh api "${TAG_REF_ENDPOINT}" > "${TAG_RESPONSE}" 2> "${TAG_ERROR}"; then
    return 0
  else
    api_exit=$?
  fi

  if grep -F -q -- '(HTTP 404)' "${TAG_ERROR}"; then
    return 4
  fi

  echo "Unable to inspect release tag ${RELEASE_TAG}." >&2
  cat "${TAG_ERROR}" >&2
  return "${api_exit}"
}

verify_existing_tag() {
  local existing_type
  local existing_sha

  existing_type="$(jq -r '.object.type // empty' "${TAG_RESPONSE}")"
  existing_sha="$(jq -r '.object.sha // empty' "${TAG_RESPONSE}")"

  if [[ -z "${existing_type}" || -z "${existing_sha}" ]]; then
    echo "GitHub returned an invalid reference for release tag ${RELEASE_TAG}." >&2
    return 1
  fi

  if [[ "${existing_type}" != "commit" || "${existing_sha}" != "${GITHUB_SHA}" ]]; then
    echo "Release tag ${RELEASE_TAG} is immutable and conflicts with this candidate." >&2
    echo "Expected: type=commit sha=${GITHUB_SHA}" >&2
    echo "Found: type=${existing_type} sha=${existing_sha}" >&2
    return 1
  fi

  echo "Release tag ${RELEASE_TAG} already points to ${GITHUB_SHA}; continuing the same candidate."
}

read_exit=0
if read_tag_ref; then
  verify_existing_tag
  exit 0
else
  read_exit=$?
fi

if [[ "${read_exit}" -ne 4 ]]; then
  exit "${read_exit}"
fi

create_exit=0
if gh api --method POST "repos/${GITHUB_REPOSITORY}/git/refs" \
  -f ref="refs/tags/${RELEASE_TAG}" \
  -f sha="${GITHUB_SHA}" \
  > /dev/null 2> "${CREATE_ERROR}"; then
  echo "Created immutable release tag ${RELEASE_TAG} at ${GITHUB_SHA}."
  exit 0
else
  create_exit=$?
fi

# A parallel run may have created the same reference after the initial 404.
if read_tag_ref; then
  verify_existing_tag
  echo "Release tag ${RELEASE_TAG} was created concurrently with the expected identity."
  exit 0
fi

echo "Unable to create immutable release tag ${RELEASE_TAG}." >&2
cat "${CREATE_ERROR}" >&2
exit "${create_exit}"
