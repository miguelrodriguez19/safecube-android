# OpenAPI Vault Key Material Contract Integration Strategy

| Spec ID                           | Status     | Owner           | Last reviewed | Supersedes | Related ADRs |
|-----------------------------------|------------|-----------------|---------------|------------|--------------|
| `SPEC-OPENAPI-VAULT-KEY-MATERIAL` | `APPROVED` | `network/vault` | `2026-08-27`  | `N/A`      | `ADR-0002-PASSPHRASE-REWRAP`, `ADR-0001-VAULT-AUTO-LOCK` |

- Status: Accepted
- Date: 2026-08-27 (CAS review from `SCDK-B55`; original contract decision: 2026-03-09)
- Scope: `core:*`

## Context

Phase 2 allowed OpenAPI contract usage only for `AuthControllerApi`.
Phase 3 needs vault key material endpoints to support key bootstrap and master-wrap rotation flows:

- `GET /vault/keys`
- `POST /vault/keys`
- `PUT /vault/keys/master`

The generated OpenAPI layer must remain contract-only. Runtime HTTP/auth stack ownership stays in
app modules (`OkHttp`, interceptors, authenticators, Retrofit wiring).

## Decision

OpenAPI generated code is allowed as **contract-only** for vault key material in Phase 3.

Allowed:

- `com.miguelrodriguez19.safecube.core.network.generated.api.VaultKeyMaterialControllerApi`
- `com.miguelrodriguez19.safecube.core.network.generated.model.*`

Explicitly forbidden:

- `com.miguelrodriguez19.safecube.core.network.generated.infrastructure.*`
- `com.miguelrodriguez19.safecube.core.network.generated.auth.*`

## Accepted concurrency contract — SCDK-B55 / SCDK-M132

The following semantics are part of the approved vault key-material contract for
`PUT /vault/keys/master`:

1. Every passphrase change starts with a fresh `GET /vault/keys`. A valid `200` response must
   provide the key material, a strong, non-empty `ETag`, including its quotation marks, and
   `Cache-Control: no-store`. The client treats the ETag as opaque and must not parse, reconstruct,
   normalize, weaken or combine it.
   `materialBase`, `wrapperBase = materialBase.kekEncMaster` and `etagBase` are one versioned
   snapshot; an ETag from an older cache is not a valid PUT precondition.
2. The PUT body remains `{ "newKekEncMaster": "..." }`. The request contains exactly one
   `If-Match` header whose value is the literal ETag from that fresh GET. Weak ETags, `*`, lists,
   multiple header lines or a missing header are invalid. The candidate wrapper is not cached
   before confirmation; an authoritative remote base may first replace a stale local wrapper after
   the submitted current credential is verified against it.
3. A successful PUT `200` has an empty body and returns a new strong ETag plus
   `Cache-Control: no-store`. The client validates the ETag as confirmation and persists only the
   confirmed master wrapper; a later mutation always obtains a new ETag from a fresh GET.
   The backend applies one atomic compare-and-set conditioned on the authenticated account and the
   supplied ETag. Two concurrent PUTs based on the same ETag produce exactly one `200` and one
   `412 Precondition Failed`.
4. `412` means that the base revision is stale. It is a typed concurrency conflict, not a generic
   success or a blind retry permission. The client must perform a fresh GET and reconcile while
   retaining `wrapperBase`, `wrapperCandidato` and `etagBase`. A `400` malformed precondition and
   `428` missing precondition are contract errors; `409` is not the revision-conflict status for
   this endpoint. A `404` during the change cannot be reported as success. `401` follows the
   existing authentication flow, `403` is terminal, and `408`, `429`, `5xx` or transport failures
   are potentially uncertain and require reconciliation before any decision.
5. After `412`, timeout, interrupted transport or an ambiguous response, reconciliation compares
   the remote wrapper exactly: candidate means applied and may confirm success; base means not
   applied and does not authorize an automatic PUT; a third wrapper means another device won and
   requires fail-closed invalidation/lock. If reconciliation fails or material is invalid, the
   result remains indeterminate and the client invalidates master-wrapper authority, zeroizes the
   active KEK through lock and preserves session, recovery material, encrypted items, drafts and
   checkpoints.
6. A confirmed update changes only the master wrapper and its server version metadata. It does not
   change `kekEncRecovery`, KDF parameters, `cryptoVersion`, SecureItems, wrapped DEKs, payloads,
   drafts or checkpoints. No endpoint response or error body is required for the client decision
   in `400`, `404`, `412` or `428`; the HTTP status is authoritative.

The ETag requirement above applies to the versioned snapshot used by a passphrase change and its
reconciliation. The post-authentication bootstrap path uses `getKeyMaterial()` and may accept a
successful response with valid key material when `ETag` is absent or unusable; it does not retain or
send a version precondition. Only `getVersionedKeyMaterial()` may supply the base snapshot for
`PUT /vault/keys/master`.

The backend delivery is recorded as `SCDK-B55`. `SCDK-M132` imports the delivered contract into
the canonical `OpenAPI.json`, regenerates the Retrofit signature and implements the versioned GET,
literal `If-Match`, typed `412`/`428` mapping and response-ETag validation in `core:vault`.

## Usage Boundaries

Import boundary rule:

- Direct usage of `generated.*` is limited to `core:*` modules only.
- `feature/*` modules and `app` must not import `generated.*` directly.

Phase 3 guardrail:

- Only vault key material contract is enabled from generated APIs.
- This decision does not enable other generated controllers beyond already approved scope.

## Out Of Scope

- Generated infrastructure/auth runtime ownership outside the approved contract-only layer.
- Persisting an ETag as a reusable future precondition; every mutation obtains a fresh one.
