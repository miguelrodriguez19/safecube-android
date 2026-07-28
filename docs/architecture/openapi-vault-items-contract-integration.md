# OpenAPI Vault Items Contract Integration Strategy

- Status: Accepted
- Date: 2026-04-17
- Scope: `core:network`, `core:vault`

## Context

Phase 2 and Phase 3 already closed that OpenAPI Generator is used as contract-only.
Phase 5 now needs `/vault/items` integration to consume the real backend without replacing app-owned
HTTP/auth runtime architecture.

Runtime ownership remains in app modules:

- `OkHttp`
- `Retrofit`
- `AuthInterceptor`
- `TokenRefreshAuthenticator`

## Decision

OpenAPI generated code is allowed as **contract-only** for vault items in Phase 5.

Allowed:

- `com.miguelrodriguez19.safecube.core.network.generated.api.VaultControllerApi`
- `com.miguelrodriguez19.safecube.core.network.generated.model.*` related to `/vault/items` contract

Explicitly forbidden:

- `com.miguelrodriguez19.safecube.core.network.generated.infrastructure.*`
- `com.miguelrodriguez19.safecube.core.network.generated.auth.*`

## Usage Boundaries

Import boundary rule:

- Direct `generated.*` usage is limited to `core:*` modules.
- `feature/*` modules and `app` must not import `generated.*` directly.
- Generated contract consumption for `/vault/items` stays encapsulated in `core:vault` and/or
  `core:network`.

Phase 5 guardrail:

- This decision enables generated contract usage for `VaultControllerApi` only (vault items scope).
- This decision does not enable generated runtime HTTP/auth helpers.

## Contract Scope Enabled

This decision applies to the current `/vault/items` endpoints defined in OpenAPI:

- `GET /vault/items`
- `GET /vault/items/changes`
- `GET /vault/items/{itemId}`
- `POST /vault/items`
- `PUT /vault/items/{itemId}`
- `DELETE /vault/items/{itemId}`

## Contract Refresh

The checked-in snapshot is refreshed manually from a running backend:

```bash
curl -fsS \
  http://localhost:8080/safecube/v3/api-docs \
  -o ../safecube-android/core/network/openapi/OpenAPI.json
```

`VaultSyncOpenApiContractTest` validates the versioned snapshot. Gradle does not download OpenAPI
during a normal build.

The current synchronization semantics are defined by
[Vault Sync Versioning v2](./vault-sync-versioning-v2.md).
