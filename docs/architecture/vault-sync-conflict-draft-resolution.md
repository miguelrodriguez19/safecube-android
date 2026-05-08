# Vault Sync Conflict Draft Resolution

- Status: Accepted
- Date: 2026-05-05
- Scope: `core:vault`, `core:storage`, `feature:vault`, backend `/vault/items`

## Context

SafeCube treats the backend as the authoritative persisted source shared between devices.
`Room` remains the local source of truth visible to the UI, but its official item rows must converge
to the backend state during sync.

Offline-first editing still means a device can modify an item while its local official row is stale.
Example:

```text
Device A has official item v1
Device B updates the same item -> backend official item v2
Device A edits from v1 and tries to push
Backend rejects the stale update with 409
```

The previous MVP conflict behavior left the item in `CONFLICT`. That made the user able to re-save
locally, but not able to leave the loop if every push was still based on stale remote state.

SafeCube needs a long-term policy that:

- keeps backend data authoritative
- does not silently lose local user edits
- avoids a full semantic merge in Phase 5
- can evolve into a richer conflict UI in later phases
- keeps encrypted payloads opaque to the backend

## Current Backend Behavior

The current backend behavior matters because not every remote failure is a real multi-device
conflict.

Create:

- `POST /vault/items` always creates a new `itemId`.
- backend assigns `payloadVersion = 1`.
- backend initializes `createdAt` and `updatedAt` with server time.
- backend does not compare `displayHint`, item type, payload, or any external client identifier
  against existing items.
- expected create failures are validation, authentication/authorization, malformed payload, or
  transport failures.
- `409 Conflict` is not expected for create under the current functional rules.

Update:

- backend first looks up the item by `itemId` and `accountId`.
- if the item does not exist, or belongs to another account, backend returns `404 Not Found`.
- this intentionally avoids leaking cross-account existence.
- backend preserves `itemId`, `accountId`, and `createdAt`.
- backend replaces mutable item fields from the request, increments `payloadVersion`, and updates
  `updatedAt`.

Current conflict check:

- backend rejects an update when the timestamp used for the update is not after the stored
  `updatedAt`.
- in that case the use case returns `StaleUpdateRejected` and the controller maps it to
  `409 Conflict`.
- although the OpenAPI description suggests optimistic concurrency with a client-known `updatedAt`,
  the current HTTP request does not include `updatedAt` or `lastKnownUpdatedAt`.
- the controller uses server time (`clock.instant()`) directly.

Consequence:

- with the current contract, two clients updating the same item one after another generally behave
  like last-write-wins as long as server time advances.
- `409 Conflict` is still observable, especially in tests or timestamp resolution edge cases, but
  it does not fully represent the classic "two clients edited from the same version" conflict.
- the draft resolution model applies to conflicts/failures that are observable with the current
  contract. It does not claim that the current backend detects every possible concurrent edit.

## Decision

Use an **official remote row plus local draft** model for stale update conflicts against an active
remote item.

The backend remains the official source of truth. When a local push is rejected because the backend
has a newer active version, the client must:

1. keep the rejected local changes as a local draft
2. fetch the current remote item
3. apply the remote item to the official local row
4. show the user that a local draft exists and is not official yet

The user can then choose:

- `Publish draft`: try to make the local draft the new official backend version
- `Discard draft`: delete the draft and keep the backend version

This is not a full merge algorithm. It is a draft-based conflict resolution policy where:

- `secure_items` represents the latest official item known from backend
- `secure_items_draft` mirrors the secure item shape for local changes that have not been accepted
  by backend
- UI can render the draft as an overlay while still knowing the official row separately

If the backend official state is deleted, backend deletion wins. The client must not create a draft
that can restore or recreate the deleted item automatically. The local edit can be discarded only
with explicit user-visible feedback.

## Ubiquitous Language

- `official item`: the item state accepted by backend and mirrored in `secure_items`.
- `local draft`: a local user edit that has not been accepted by backend.
- `draft base`: the remote version metadata used when the draft was created or last rebased.
- `publish draft`: send the draft as the desired next backend version.
- `discard draft`: remove the local draft and expose only the official item.

## Conflict Creation Flow

For `PENDING_UPDATE` rejected with `409`:

```text
local item = user's edited value
remote item = current backend value

1. create/update local draft from the local item
2. GET /vault/items/{remoteItemId}
3. apply remote item into secure_items as SYNCED
4. mark the item as having a local draft
5. surface a non-blocking draft badge/banner in feature:vault
```

If the fetched remote item is deleted, do not create/keep the update draft. Apply the remote delete
flow instead.

For `PENDING_DELETE` rejected with `409`:

```text
1. create a delete draft/tombstone draft
2. GET /vault/items/{remoteItemId}
3. apply remote item into secure_items as SYNCED
4. surface a draft pending deletion state
```

For `PENDING_DELETE` where backend already deleted the item:

```text
1. treat remote delete as already applied
2. keep/apply the local tombstone in secure_items
3. mark the official row SYNCED
4. do not create a draft
```

This covers delete idempotency from the client point of view. Whether backend returns success or
`404`, the local delete intent has already reached the same official outcome.

For `PENDING_UPDATE` rejected with `404`:

```text
1. backend official state is "missing/deleted"
2. apply a local tombstone in secure_items
3. mark the official row SYNCED
4. discard the rejected local edit
5. show that the remote item was deleted before the local edit could be published
```

SafeCube must not restore or recreate backend-deleted items automatically from a draft. If the user
wants a similar item again, they must create it manually as a new item.

`PENDING_CREATE` is not expected to create this kind of stale update conflict because it has no
`remoteItemId`. With the current backend rules, create does not produce functional conflicts.
If a future backend version returns `409` for create, it must be treated as a create-specific remote
failure and documented separately.

### Delete And Edit Cross-Device Cases

Remote delete while local edits exist:

```text
Device A has official item v1
Device B deletes item -> backend official tombstone v2 with deletedAt
Device A edits from v1 and tries PUT
Backend rejects or reports the item as missing/stale
```

Decision:

```text
1. discard Device A edit
2. apply backend tombstone to secure_items as the official state
3. mark the official row SYNCED
4. show that the item was deleted on another device before the local edit could be published
```

No `RESTORE` draft is created. The client must not reuse the deleted `remoteItemId`, and must not
create a replacement item automatically with `POST /vault/items`.

Local delete while remote update exists:

```text
Device A has official item v1
Device A deletes item locally -> PENDING_DELETE
Device B updates item -> backend official item v2
Device A tries DELETE
Backend rejects stale delete with 409
```

Decision:

```text
1. keep Device A delete intent as a local delete draft
2. fetch and apply backend v2 to secure_items as the official state
3. show the item as active official data with a pending local delete draft
4. allow:
   - Discard draft: keep backend v2
   - Publish delete draft: retry DELETE against the latest official base
```

Local delete followed by local edit on the same device should not normally happen through the UI,
because deleted items are removed from the active editor flow. If it is possible through stale
navigation, the latest explicit user action decides the local intent:

```text
1. cancel the local delete intent
2. keep the edited value as an UPDATE draft if the backend item still exists
3. apply the latest backend item to secure_items as the official state
4. show the draft over the active official item
```

If the backend item was deleted, the rule from "Remote delete while local edits exist" applies:
discard the edit, apply the tombstone, and do not create a restore draft.

## Publish Draft Flow

Publishing a draft means: "make my local draft the next official backend version".

Canonical flow:

```text
1. read official item from secure_items
2. read local draft
3. execute the draft operation:
   - UPDATE -> PUT /vault/items/{remoteItemId} with draft displayHint/payload/schema
   - DELETE -> DELETE /vault/items/{remoteItemId}
4. on success:
   - update secure_items with returned remote metadata
   - delete the draft
   - mark official item SYNCED
5. on 409:
   - fetch latest remote item
   - apply it to secure_items
   - if latest remote item is active:
     - keep the draft
     - update draft base metadata
     - keep UI in draft state
   - if latest remote item is deleted:
     - delete the draft
     - keep the remote tombstone as official
     - show that the draft cannot be published because the item was deleted remotely
```

`Publish draft` must not mutate the official row before the backend accepts the change.

## Discard Draft Flow

Discarding a draft means: "keep backend as truth and remove my unaccepted local edit".

Canonical flow:

```text
1. delete local draft
2. show secure_items official row
3. keep item SYNCED unless another pending operation exists
```

No remote call is required to discard a draft unless the client needs to refresh stale official data.

## Local Storage Shape

Do not store official state and draft state in the same columns.

Recommended storage:

```text
secure_items
- logicalItemId
- remoteItemId
- itemType
- schemaVersion
- displayHint
- payload
- payloadVersion
- createdAt
- updatedAt
- deletedAt
- syncState
- lastSyncedAt
- lastSyncError

secure_items_draft
- logicalItemId
- remoteItemId
- itemType
- schemaVersion
- displayHint
- payload
- payloadVersion
- createdAt
- updatedAt
- deletedAt
- lastSyncedAt
- lastSyncError
- draftType
- basePayloadVersion
- baseUpdatedAt
- lastPublishError
```

`secure_items` must stay the official backend mirror.
`secure_items_draft` must be a mirror-style table for the local proposal, with additional draft
metadata.

Draft types:

- `UPDATE`: user wants to publish edited fields/payload over the current official item.
- `DELETE`: user wants to delete the current official item.

The draft payload remains encrypted in the same client-owned format as official payloads.
The backend never needs to inspect draft content.

## UI Policy

Home:

- show a clear `Draft` marker when an item has a local draft
- avoid showing this as a permanent blocking `Conflict` state
- show sync errors separately from draft state

Editor:

- load official item and draft, if present
- if a draft exists, render the draft as the editable value
- show a banner such as:

```text
This item changed on another device. Your local changes are saved as a draft.
```

Actions:

- `Publish draft`
- `Discard draft`

The user must always be able to leave the state by discarding the draft.

## Consequences

Positive:

- local edits are preserved as drafts when the backend item still exists
- backend remains the authoritative multi-device source
- UI can escape conflict loops with `Discard draft`
- `Publish draft` is a clear user intent, not an implicit retry
- backend deletes are final unless the user manually creates a new item
- future Phase 6 conflict UX can build on the same storage model

Costs:

- requires a new local mirror table: `secure_items_draft`
- requires presentation state that can distinguish official values from draft values
- requires new use cases for publish/discard draft
- does not try to compensate for concurrent edits that the current backend contract does not expose
  as conflicts

## Non-Goals

This ADR does not introduce:

- semantic merge of decrypted note/password fields
- backend inspection of encrypted payloads
- automatic last-write-wins behavior
- automatic recreation of backend-deleted items from local drafts
- CRDT/event-log replication
- multi-version history UI

## Relationship With `syncState`

`syncState = CONFLICT` should not be the long-term user-facing terminal state for stale updates.

Preferred direction:

- use `syncState` for transport/sync queue state of official rows
- use draft existence to represent unaccepted local changes
- reserve `CONFLICT` for states that cannot be represented or resolved by draft publish/discard
