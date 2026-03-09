# OpenAPI Vault Key Material Contract Integration Strategy

- Status: Accepted
- Date: 2026-03-09
- Scope: `core:*`

## Context

Phase 2 allowed OpenAPI contract usage only for `AuthControllerApi`.
Phase 3 needs vault key material endpoints to support key bootstrap and master-wrap rotation flows:

- `GET /vault/keys`
- `POST /vault/keys`
- `PUT /vault/keys/master`

The generated OpenAPI layer must remain contract-only. Runtime HTTP/auth stack ownership stays in app modules (`OkHttp`, interceptors, authenticators, Retrofit wiring).

## Decision

OpenAPI generated code is allowed as **contract-only** for vault key material in Phase 3.

Allowed:
- `com.miguelrodriguez19.safecube.core.network.generated.api.VaultKeyMaterialControllerApi`
- `com.miguelrodriguez19.safecube.core.network.generated.model.*`

Explicitly forbidden:
- `com.miguelrodriguez19.safecube.core.network.generated.infrastructure.*`
- `com.miguelrodriguez19.safecube.core.network.generated.auth.*`

## Usage Boundaries

Import boundary rule:
- Direct usage of `generated.*` is limited to `core:*` modules only.
- `feature/*` modules and `app` must not import `generated.*` directly.

Phase 3 guardrail:
- Only vault key material contract is enabled from generated APIs.
- This decision does not enable other generated controllers beyond already approved scope.

## Out Of Scope

- Data source implementation for vault key material.
