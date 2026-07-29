# OpenAPI Auth Contract Integration Strategy

| Spec ID                                   | Status     | Owner          | Last reviewed | Supersedes | Related ADRs |
|-------------------------------------------|------------|----------------|---------------|------------|--------------|
| `SPEC-AUTH-CONTRACT`, `SPEC-OPENAPI-AUTH` | `APPROVED` | `auth/network` | `2026-07-29`  | `N/A`      | `N/A`        |

- Status: Accepted
- Date: 2026-03-04
- Scope: `core:network`, `core:auth`

## Context

`core:network` already owns the HTTP stack (`OkHttp`, `Retrofit`, interceptors, DI).
OpenAPI generation is used to get backend contract types, but must not replace session/auth
architecture from app modules.

Phase 2 needs `/auth/*` contract integration without coupling to generated HTTP/auth helpers.

## Decision

OpenAPI generated code is used as **contract-only** for auth.

Allowed:

- `com.miguelrodriguez19.safecube.core.network.generated.api.AuthControllerApi`
- `com.miguelrodriguez19.safecube.core.network.generated.model.*` needed by auth

Not allowed as runtime HTTP stack:

- `generated.infrastructure.ApiClient`
- `generated.auth.HttpBearerAuth`

Phase 2 scope guardrail:

- Allowed generated API usage: `AuthControllerApi` only
- Explicitly out of Phase 2 usage:
    - `UserProfileControllerApi`
    - `VaultControllerApi`
    - `VaultKeyMaterialControllerApi`
    - Vault/profile/key-material generated models

## Ownership

- `Authorization` header: `AuthInterceptor`
- `401 -> refresh -> retry`: `TokenRefreshAuthenticator` (owner defined; no-op until refresh
  implementation)
- `OkHttpClient` and `Retrofit` construction: `core:network` (`NetworkClientFactory` +
  `NetworkModule`)

Import boundary rule:

- `feature/*` modules must not import `generated.*` directly.
- Generated contract consumption must stay encapsulated in `core:network` and/or `core:auth`.

## Applied In Code

- `AuthControllerApi` is created from app Retrofit, not generated ApiClient:
    - `core/network/src/main/java/com/miguelrodriguez19/safecube/core/network/di/NetworkModule.kt`
- HTTP stack remains app-owned:
    -
    `core/network/src/main/java/com/miguelrodriguez19/safecube/core/network/NetworkClientFactory.kt`
    - `core/network/src/main/java/com/miguelrodriguez19/safecube/core/network/AuthInterceptor.kt`
    -
    `core/network/src/main/java/com/miguelrodriguez19/safecube/core/network/TokenRefreshAuthenticator.kt`
- Import audit snapshot (2026-03-04):
    - `feature/*`: no direct `generated.*` imports
    - runtime wiring of generated APIs: only `AuthControllerApi`

## Explicit Creation Point

`AuthControllerApi` is provided with:

`Retrofit.Builder(...).client(customOkHttpClient).build().create(AuthControllerApi::class.java)`

Implementation detail:

- The builder lives in `NetworkClientFactory.createRetrofit(...)`.
- `NetworkModule` injects the custom `OkHttpClient` and uses that Retrofit instance to create
  `AuthControllerApi`.
