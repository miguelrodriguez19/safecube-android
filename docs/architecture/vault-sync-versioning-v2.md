# Vault Sync Versioning v2

| Spec ID              | Status     | Owner   | Last reviewed | Supersedes           | Related ADRs |
|----------------------|------------|---------|---------------|----------------------|--------------|
| `SPEC-VAULT-SYNC-V2` | `APPROVED` | `vault` | `2026-07-29`  | `SPEC-VAULT-SYNC-V1` | `N/A`        |

- Status: Accepted
- Date: 2026-07-28
- Scope: Android, backend, PostgreSQL, and OpenAPI
- Supersedes: [Vault Sync v1](./historical/vault-sync-v1.md) and
  [Vault Sync Conflict Draft Resolution](./historical/vault-sync-conflict-draft-resolution.md)

## Context

The previous protocol used one version-like value for encryption identity, optimistic concurrency,
and incremental synchronization. That made a server-side revision capable of invalidating payload
authentication because `payloadVersion` is part of the authenticated encryption context.

The protocol also used timestamps as cursors and fetched summaries and details separately. Those
requests could observe different revisions or skip changes that shared a timestamp.

## Decision

The protocol separates three independent values:

| Field            | Authority | Purpose                                                  |
|------------------|-----------|----------------------------------------------------------|
| `payloadVersion` | Client    | Identifies the encrypted payload generation used in AAD. |
| `itemRevision`   | Server    | Per-item compare-and-set concurrency control.            |
| `changeSequence` | Server    | Per-account pull cursor ordered by transaction commit.   |

Mandatory invariants:

- The backend stores `payloadVersion` unchanged.
- `itemRevision` and `changeSequence` never participate in AAD.
- Every accepted update or delete must match the current `itemRevision`.
- Replaying an accepted `mutationId` returns its original response without another mutation.
- A draft is removed only after the resulting official item is validated and persisted atomically.
- A conflict keeps the remote official item and the local proposal at the same time.
- `updatedAt` is server-generated and informational only.

## Account-Scoped Change Cursor

`changeSequence` is an account-scoped, transactional, commit-ordered change cursor. It is not a
global sequence.

The backend allocates it through the account row in `vault_item_change_cursors`. The allocation
holds that row until the item mutation and idempotency ledger entry commit or roll back. Therefore:

- mutations for one account are serialized at cursor allocation
- mutations for different accounts remain independent
- a later committed mutation cannot receive a cursor before an earlier in-flight mutation
- `(account_id, change_sequence)` is unique
- clients must never compare sequences from different accounts
- clients require strictly increasing sequences, not contiguous values

Android stores `lastAppliedChangeSequence` by account and applies a page plus its new cursor in one
Room transaction.

## Remote Contract

Mutations use:

- `POST /vault/items`: `Idempotency-Key`
- `PUT /vault/items/{itemId}`: `Idempotency-Key` and `If-Match`
- `DELETE /vault/items/{itemId}`: `Idempotency-Key` and `If-Match`

Mutation responses expose `ETag` and return `mutationId`, `payloadVersion`, `itemRevision`, and
`changeSequence`. Create and update return `updatedAt`; delete returns `deletedAt`.

Protocol errors:

| Status | Meaning                                                        |
|--------|----------------------------------------------------------------|
| `400`  | Invalid request or missing `Idempotency-Key`.                  |
| `409`  | The idempotency key was reused with different request content. |
| `412`  | `If-Match` references a stale revision.                        |
| `428`  | A required `If-Match` header is missing.                       |

`GET /vault/items/changes?after=<changeSequence>&limit=<n>` returns ordered complete snapshots and
tombstones with `nextCursor` and `hasMore`. Sync does not use `updatedAfter` and does not perform a
summary-plus-detail hydration pass.

## Android Synchronization

The synchronization cycle is:

1. Pull all pages after the account checkpoint.
2. Compare drafts against the downloaded official `itemRevision`.
3. Push only `READY_TO_SYNC` drafts.
4. Pull again to observe concurrent mutations and the accepted remote result.

`400`, `409`, and `428` are protocol integrity failures. Android preserves the draft and stops that
push instead of retrying indefinitely. `412` enters the editable conflict flow. Transport errors
and `5xx` remain retryable.

Conflict rules:

- Update versus update: store the remote as official and retain the local update as `CONFLICT`.
- Publish local update: decrypt, encrypt a new payload generation, rebase on the current
  `itemRevision`, and create a new `mutationId`.
- Delete versus remote update: retain the delete draft as `CONFLICT`; publishing rebases and
  retries.
- Update versus remote delete: never resurrect the same remote item. "Save as new" creates a new
  logical item and a CREATE draft with `payloadVersion = 1`.
- Delete against an already deleted remote item: resolve as semantic success.
- Idempotency conflict: treat as client integrity failure, not a user-editable content conflict.

## Local Account Isolation

Android supports one local account at a time. It does not add `accountId` to official or draft item
rows.

Before activating a fresh session, Android removes:

- the in-memory KEK
- persisted vault key material
- all official items
- all drafts
- all sync checkpoints

Officials, drafts, and checkpoints are deleted in one Room transaction. New tokens are stored only
after that transaction succeeds. Manual and forced logout use the same lifecycle; a remote logout
failure never prevents local logout.

If Room cleanup fails during logout, keys and tokens still disappear. A later login retries cleanup
and remains blocked until it succeeds.

## OpenAPI Source

The checked-in contract is refreshed manually from a running backend:

```bash
curl -fsS \
  http://localhost:8080/safecube/v3/api-docs \
  -o ../safecube-android/core/network/openapi/OpenAPI.json
```

`VaultSyncOpenApiContractTest` validates the checked-in snapshot. Normal builds do not fetch a
contract over the network.

## Consequences

- Encryption no longer predicts a server revision.
- CAS provides deterministic multi-device conflict detection.
- Idempotency makes response-loss retries safe.
- Cursor pagination cannot skip changes because of timestamp equality.
- Mutations for one account are deliberately serialized during cursor allocation.
- Logout intentionally discards unpublished local drafts after explicit user confirmation.
