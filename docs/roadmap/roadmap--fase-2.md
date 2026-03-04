Perfecto. Te dejo la **FASE 2 rehecha** y ya **alineada con OpenAPI Generator**, sustituyendo las tareas obsoletas (`DTOs` + `AuthApi` manuales) y metiendo las nuevas tareas en el punto correcto del flujo. Está basada en tu roadmap actual de Fase 2 , el roadmap alto nivel (Fase 2 = Auth Flow Completo) , el contrato `auth` de OpenAPI (`/auth/register|login|refresh|logout`) , la estrategia de tokens/rotación/logout del slice `auth` , la validación HTTP homogénea (`ErrorResponse`)  y la separación **Login ≠ Vault unlock** .

---

# FASE 2 — Roadmap (Auth Flow Completo) [Actualizado OpenAPI Generator]

## Objetivo de la fase

Construir el flujo real de **Register/Login/Token storage/Refresh/Logout**, dejando al usuario en estado **LoggedInVaultLocked** (autenticado pero con vault todavía bloqueado). Esto mantiene la separación estricta **“Login != Vault unlock”** definida en ADR-004.

## Contrato backend que vamos a implementar en esta fase

Endpoints del slice `auth` (OpenAPI):

* `POST /auth/register`
* `POST /auth/login`
* `POST /auth/refresh`
* `POST /auth/logout`

Reglas clave del backend (`auth`):

* Access token = JWT corto
* Refresh token = opaco
* **Rotación obligatoria** en refresh
* Logout revoca refresh tokens (familia/sesiones activas)

## Decisiones MVP (cerradas para esta fase)

* **Errores HTTP homogéneos**: mapear formato tipo `ErrorResponse` (`error`, `fields`) a errores de dominio.
* **OpenAPI Generator = contrato**, no arquitectura.
* **Usar solo** clases generadas de:

    * `generated...api/*`
    * `generated...model/*`
* **No usar**:

    * `generated...infrastructure/ApiClient`
    * `generated...auth/HttpBearerAuth`
* **Authorization** la gestiona `AuthInterceptor` (propio).
* **Refresh automático** lo gestiona `TokenRefreshAuthenticator` (propio).
* **OkHttpClient/Retrofit** siguen siendo tuyos (via `NetworkClientFactory` / `NetworkModule`), coherente con la base de Fase 1 (`TokenProvider`, `AuthInterceptor`, `NetworkClientFactory`).

---

# Tasks — Fase 2 (actualizadas)

# Configure OpenAPI client integration strategy (contract-only usage)

## Main Story (How, I Want, To)

Como developer, quiero definir una estrategia explícita de integración del código generado por OpenAPI, para usar el contrato sin romper la arquitectura de `core:network` y `core:auth`.

## Context, Functional Description & Goal

Ya existe infraestructura de red propia (`NetworkClientFactory`, `AuthInterceptor`, `TokenProvider`) y Fase 2 necesita integrar el contrato `auth` generado por OpenAPI sin delegar auth/session en el cliente generado.
Esta tarea cierra la decisión arquitectónica para evitar implementaciones inconsistentes.

## Steps/Scope

### In Scope

* Documentar y aplicar la decisión de integración:

    * **Use** `generated...api.AuthControllerApi`
    * **Use** `generated...model.*` necesarios para auth
    * **Do not use** `generated...infrastructure.ApiClient`
    * **Do not use** `generated...auth.HttpBearerAuth`
* Definir ownership de responsabilidades:

    * `Authorization` header -> `AuthInterceptor`
    * `401 -> refresh -> retry` -> `TokenRefreshAuthenticator`
    * construcción de `OkHttpClient`/`Retrofit` -> `core:network`
* Dejar explícito el punto de creación de `AuthControllerApi` con `Retrofit.Builder(...).client(customOkHttpClient)`.

### Out of Scope (if applies)

* Implementación de `RemoteAuthDataSource`.
* Wiring de DI completo.
* Refresh automático real.

## Additional Information and Configuration

### API Contract and Expected Behavior (if applies)

El contrato `auth` está en OpenAPI y expone los endpoints de register/login/refresh/logout.

### Acceptance Criteria (ACs)

* Existe una decisión técnica explícita y aplicada en el roadmap/código (no ambigua).
* Queda cerrado que el código generado se usa como **contrato**, no como stack HTTP principal.

---

# Restrict generated API usage (Auth-only in Phase 2)

## Main Story (How, I Want, To)

Como developer, quiero restringir el uso del código generado a `auth` durante Fase 2, para evitar scope creep y mezclar trabajo de `user`/`vault` antes de tiempo.

## Context, Functional Description & Goal

El generator ya expone APIs de `auth`, `user`, `vault` y `vault-key-material`, pero el objetivo de Fase 2 es **solo auth flow completo**.
Esta tarea define guardrails de alcance y dependencias.

## Steps/Scope

### In Scope

* Declarar regla de fase:

    * Solo permitido usar `AuthControllerApi` y modelos auth asociados.
* Declarar explícitamente fuera de Fase 2:

    * `UserProfileControllerApi`
    * `VaultControllerApi`
    * `VaultKeyMaterialControllerApi`
    * modelos de vault/profile/key material
* Definir regla de imports:

    * `feature/*` no importa `generated.*` directamente
    * consumo del contrato generado queda encapsulado en `core:auth` y/o `core:network`

### Out of Scope (if applies)

* Refactor de módulos para enforce automático.
* Lint custom / static analysis rules.

## Additional Information and Configuration

### API Contract and Expected Behavior (if applies)

Aunque OpenAPI incluya endpoints de otros slices, esta fase implementa únicamente `/auth/*`.

### Acceptance Criteria (ACs)

* El roadmap y las tareas de Fase 2 no dependen de APIs de `user` o `vault`.
* Las implementaciones de Fase 2 usan solo contrato generado de `auth`.

---

# Add Auth error mapping (generated HTTP errors -> domain AuthError)

## Main Story (How, I Want, To)

Como developer, quiero mapear errores HTTP del contrato generado a errores de dominio estables (`AuthError`), para desacoplar UI y dominio del detalle del backend.

## Context, Functional Description & Goal

Antes se planteaba crear un `ErrorResponse` manual; ahora el contrato OpenAPI ya tipa respuestas 400 en varios endpoints (y el backend mantiene formato homogéneo `error` + `fields`).
La tarea pasa a ser de **mapping**, no de creación de DTO manual.

## Steps/Scope

### In Scope

* Crear mapper/helper de errores para `auth`:

    * `400` -> `AuthError.ValidationFailed(fields?)`
    * `401` -> `AuthError.InvalidCredentials` (o equivalente)
    * `403` -> `AuthError.Forbidden` / `AuthError.AccountNotActive` (MVP estable)
    * `409` -> `AuthError.AccountAlreadyExists` (register) / conflict genérico
    * fallback -> `AuthError.Unknown`
* Parsing “safe” del body de error (si no parsea, no crashea)
* Mantener el mapper encapsulado en `core:auth` o `core:network` (según diseño actual)

### Out of Scope (if applies)

* i18n de mensajes UI.
* Catálogo cross-feature (`user`, `vault`).

## Additional Information and Configuration

### API Contract and Expected Behavior (if applies)

OpenAPI define `400/401/403/409/500` en endpoints auth; backend usa formato homogéneo para errores de validación.

### Acceptance Criteria (ACs)

* El mapper transforma errores HTTP de auth a un modelo de dominio estable.
* Un body de error inválido/no esperado no rompe el flujo.

---

# Create RemoteAuthDataSource (thin network wrapper with generated AuthControllerApi)

## Main Story (How, I Want, To)

Como developer, quiero una capa remota mínima para auth usando `AuthControllerApi` generado, para aislar Retrofit del repositorio y facilitar tests.

## Context, Functional Description & Goal

El roadmap original hablaba de `AuthApi` manual; con OpenAPI Generator, esta tarea se adapta para encapsular `AuthControllerApi` generado y exponer resultados tipados al repositorio.

## Steps/Scope

### In Scope

* Crear `RemoteAuthDataSource` que dependa de `AuthControllerApi` (generado)
* Métodos:

    * `register(request)`
    * `login(request)`
    * `refresh(request)`
    * `logout()`
* Devolver `NetworkResult<T>` (o wrapper existente) preservando:

    * `httpCode`
    * `body`
    * `errorBody` (si aplica)

### Out of Scope (if applies)

* Persistencia de tokens.
* Session state.
* Mapping a errores de UI.

## Additional Information and Configuration

### API Contract and Expected Behavior (if applies)

El contrato auth proviene de OpenAPI y ya define requests/responses para register/login/refresh/logout.

### Acceptance Criteria (ACs)

* `RemoteAuthDataSource` compila y usa `AuthControllerApi` generado.
* Los errores HTTP se exponen sin pérdida de información útil.

---

# Implement AuthRepositoryImpl (core:auth) using RemoteAuthDataSource

## Main Story (How, I Want, To)

Como developer, quiero un `AuthRepository` real, para que `feature:auth` deje de depender de mocks/fakes y consuma auth backend de forma estable.

## Context, Functional Description & Goal

El backend define rotación obligatoria de refresh y logout con revocación de sesiones.
El repositorio se apoya en `RemoteAuthDataSource` + error mapper y no conoce Retrofit directamente (alineado con la base de Fase 1).

## Steps/Scope

### In Scope

* Implementar `AuthRepositoryImpl : AuthRepository`

    * `register(email, password)`
    * `login(email, password)`
    * `refresh(refreshToken)`
    * `logout()`
* Usar `RemoteAuthDataSource`
* Mapear errores HTTP a `AuthError` mediante el mapper de auth
* Mantener contrato consumible por `feature:auth` (sin exponer modelos generated)

### Out of Scope (if applies)

* Guardado de tokens en storage.
* Refresh automático (`Authenticator`).
* Vault unlock.

## Additional Information and Configuration

### API Contract and Expected Behavior (if applies)

* `/auth/refresh` rota refresh token y devuelve nuevo par.
* `/auth/logout` revoca sesiones activas (refresh tokens).

### Acceptance Criteria (ACs)

* `AuthRepositoryImpl` está cableado y usable desde `feature:auth`.
* Errores 400/401/403/409 se exponen como `AuthError` estable.

---

# Finalize EncryptedTokenStorage (MVP)

## Main Story (How, I Want, To)

Como developer, quiero persistir `accessToken` y `refreshToken` de forma segura, para soportar cold start sin re-login inmediato.

## Context, Functional Description & Goal

`core:auth` ya contiene `TokenStorage` y `EncryptedTokenStorage` en el package structure.
Esta tarea completa la implementación MVP y la deja testeable.

## Steps/Scope

### In Scope

* Implementar/terminar `EncryptedTokenStorage`

    * `saveTokens(accessToken, refreshToken, issuedAt)`
    * `getAccessToken()`
    * `getRefreshToken()`
    * `getIssuedAt()`
    * `clear()`
* Evitar logs con tokens/valores sensibles
* Asegurar comportamiento consistente en lecturas posteriores

### Out of Scope (if applies)

* Biometría
* Vault keys / crypto material
* Refresh automático

## Additional Information and Configuration

### API Contract and Expected Behavior (if applies)

Los tokens persistidos vienen del response de login/refresh (`AuthTokensResponse`).

### Acceptance Criteria (ACs)

* La persistencia funciona tras cerrar/abrir app.
* `clear()` elimina todo el estado persistido de tokens.

---

# Implement SessionManagerImpl (session state + TokenProvider)

## Main Story (How, I Want, To)

Como developer, quiero un `SessionManager` real que exponga estado por `Flow/StateFlow` y provea access token a network, para que navegación y requests usen un único source of truth.

## Context, Functional Description & Goal

La arquitectura de Fase 1 ya separaba `TokenProvider` de `AuthInterceptor`; esta tarea conecta `TokenStorage` con `SessionState` y mantiene el estado post-login en `LoggedInVaultLocked`.

## Steps/Scope

### In Scope

* Crear `SessionManagerImpl` usando `TokenStorage`
* Exponer `sessionState: StateFlow<SessionState>`
* Implementar `TokenProvider` para `AuthInterceptor`
* Implementar:

    * `onLoginSuccess(tokens)` (o equivalente)
    * `forceLogout()` (limpia storage + emite `LoggedOut`)
* Resolver estado inicial (cold start) leyendo storage

### Out of Scope (if applies)

* Vault unlock / derivación de claves
* Refresh automático (`Authenticator`)

## Additional Information and Configuration

### API Contract and Expected Behavior (if applies)

Login y unlock están separados; en Fase 2 el éxito de auth no desbloquea el vault.

### Acceptance Criteria (ACs)

* Si hay tokens al arrancar -> `LoggedInVaultLocked`
* Si no hay tokens -> `LoggedOut`
* `forceLogout()` limpia storage y actualiza estado

---

# Add OkHttp TokenRefreshAuthenticator (auto refresh with rotation support)

## Main Story (How, I Want, To)

Como developer, quiero refresh automático al recibir `401`, para que la app mantenga la sesión sin re-login manual cuando sea posible.

## Context, Functional Description & Goal

El backend exige refresh token opaco con **rotación obligatoria**; si refresh falla, se debe forzar re-login.
Esta lógica debe vivir en tu stack (`OkHttp Authenticator`), no en `HttpBearerAuth` generado.

## Steps/Scope

### In Scope

* Implementar `TokenRefreshAuthenticator : okhttp3.Authenticator`

    * detectar `401`
    * ejecutar `AuthRepository.refresh(refreshTokenActual)`
    * persistir nuevos tokens
    * reintentar request con nuevo `Authorization: Bearer ...`
* Prevenir carreras (mutex/lock) para múltiples `401`
* Usar cliente separado para refresh (sin loops de authenticator/interceptor)
* Si refresh falla (`401`/inválido) -> `SessionManager.forceLogout()`

### Out of Scope (if applies)

* Refresh proactivo por `exp` del JWT
* Retry policies no relacionadas con auth

## Additional Information and Configuration

### API Contract and Expected Behavior (if applies)

`/auth/refresh` emite nueva sesión y rota refresh token; errores de auth deben degradar a re-login.

### Acceptance Criteria (ACs)

* Caso `401 -> refresh 200 -> retry 200` funciona
* Caso `401 -> refresh 401 -> forceLogout()` funciona

---

# Wire DI for Auth stack (generated AuthControllerApi + custom OkHttp/Retrofit)

## Main Story (How, I Want, To)

Como developer, quiero que DI provea todas las piezas reales del stack de auth, para usarlo desde UI sin instanciación manual y sin mezclar el cliente generado completo.

## Context, Functional Description & Goal

Ya existen módulos DI en `core:*` y piezas de red propias (`NetworkModule`, `NetworkClientFactory`, `AuthInterceptor`, `TokenProvider`).
Con OpenAPI Generator, esta tarea debe instanciar `AuthControllerApi` generado usando **tu** `OkHttpClient`/`Retrofit`.

## Steps/Scope

### In Scope

* Actualizar `NetworkModule` / `AuthModule` para proveer:

    * `AuthControllerApi` (generado)
    * `RemoteAuthDataSource`
    * `AuthRepositoryImpl`
    * `EncryptedTokenStorage`
    * `SessionManagerImpl`
    * `TokenRefreshAuthenticator`
* Configurar `OkHttpClient` con:

    * `AuthInterceptor(TokenProvider)`
    * `TokenRefreshAuthenticator`
* Crear `AuthControllerApi` con `Retrofit` propio (no `ApiClient` generado)
* Mantener dependencias hacia abajo (`app/feature -> core`)

### Out of Scope (if applies)

* Refactor grande de módulos
* Multi-base-url por flavors (si no existe ya)
* Uso de `ApiClient` generado / `HttpBearerAuth` generado

## Additional Information and Configuration

### API Contract and Expected Behavior (if applies)

OpenAPI define `bearerAuth`, pero la inyección del header en mobile se resuelve con `AuthInterceptor` propio para soportar refresh automático y control centralizado.

### Acceptance Criteria (ACs)

* Desde `feature:auth` se puede inyectar `AuthRepository` y `SessionManager`
* Requests autenticadas usan `Authorization: Bearer <accessToken>` vía interceptor propio
* `AuthControllerApi` generado se instancia con `Retrofit` propio

---

# Implement Auth ViewModels (Login + Signup)

## Main Story (How, I Want, To)

Como developer, quiero ViewModels para login/signup con estados de UI, para conectar pantallas a auth real sin meter lógica en composables.

## Context, Functional Description & Goal

`feature:auth` ya tiene pantallas y Fase 2 debe completar register/login reales contra backend, dejando al usuario autenticado pero con vault bloqueado.

## Steps/Scope

### In Scope

* Crear `LoginViewModel` y `SignupViewModel` (o uno unificado)

    * `uiState` con loading/success/error
    * llamadas a `AuthRepository.login/register`
    * en success: persistencia de tokens a través de `SessionManager`/`TokenStorage` (según diseño)
* Mapear errores comunes para UI:

    * `401` -> credenciales inválidas
    * `409` -> cuenta ya existe (register)
    * `400` -> errores de campos (`fields`)
    * `403` -> estado de cuenta no apto / forbidded (mensaje MVP)

### Out of Scope (if applies)

* UX avanzada / animaciones / i18n
* Vault unlock

## Additional Information and Configuration

### API Contract and Expected Behavior (if applies)

Login y register usan `/auth/login` y `/auth/register`; el backend puede devolver `400/401/403/409` según el caso.

### Acceptance Criteria (ACs)

* Los ViewModels ejecutan login/register reales y reflejan loading/error
* Al éxito, el `SessionState` pasa a `LoggedInVaultLocked`

---

# Wire Auth screens + navigation gates to real SessionState

## Main Story (How, I Want, To)

Como developer, quiero que la navegación dependa del estado real de sesión, para enrutar correctamente entre `LoggedOut` y `LoggedInVaultLocked`.

## Context, Functional Description & Goal

Fase 2 termina con usuario autenticado pero vault aún bloqueado; la navegación debe reflejar exactamente ese estado.

## Steps/Scope

### In Scope

* Actualizar `NavigationGates` / `NavigationWrapper` para observar `SessionManager.sessionState`
* Asegurar flujo:

    * `LoggedOut` -> Welcome/Login/Signup
    * `LoggedInVaultLocked` -> `PostLoginGateScreen` (o route equivalente)
* Añadir acción de logout mínima:

    * `AuthRepository.logout()`
    * luego `SessionManager.forceLogout()`

### Out of Scope (if applies)

* Pantallas reales de vault unlock (Fase 3)
* Endpoints `/user/profile` y `/vault/*`

## Additional Information and Configuration

### API Contract and Expected Behavior (if applies)

`/auth/logout` revoca sesiones; el cliente debe limpiar estado local siempre tras logout o refresh fallido.

### Acceptance Criteria (ACs)

* Tras login/register exitoso se entra al flujo `vault locked`
* Tras logout o refresh fallido se vuelve a `LoggedOut`

---

# Minimal tests: auth contract integration + refresh flow (generated APIs/models)

## Main Story (How, I Want, To)

Como developer, quiero tests mínimos y deterministas, para validar que el stack auth funciona con el contrato generado y no se rompe con refactors.

## Context, Functional Description & Goal

Fase 2 introduce contrato generado + refresh automático; hay que cubrir happy paths y degradación a logout cuando refresh falla. El backend además mantiene errores HTTP homogéneos.

## Steps/Scope

### In Scope

* Tests con `MockWebServer` para:

    * `RemoteAuthDataSource` usando `AuthControllerApi` generado
    * `AuthRepositoryImpl` happy-path (`login/refresh/logout`)
    * error mapper de auth (`400/401/403/409`)
    * secuencia `401 -> refresh -> retry`
* Test de `EncryptedTokenStorage` (`save/read/clear`)
* Test de `SessionManagerImpl` para estado inicial/cold start y `forceLogout()`

### Out of Scope (if applies)

* Tests E2E UI completos (Compose)
* Tests de vault/crypto
* Tests de `user`/`vault` generated APIs

## Additional Information and Configuration

### API Contract and Expected Behavior (if applies)

Basado en `/auth/*` de OpenAPI y estrategia de tokens/errores del backend.

### Acceptance Criteria (ACs)

* Tests pasan en local/CI
* Está cubierto el flujo crítico: refresh con rotación + fallback a logout
* Los tests usan contrato generado de auth (no DTOs/AuthApi manuales)

---

## Orden de ejecución recomendado (para minimizar bloqueos)

1. Configure OpenAPI client integration strategy
2. Restrict generated API usage (Auth-only in Phase 2)
3. Add Auth error mapping (generated HTTP errors -> domain AuthError)
4. Create RemoteAuthDataSource (thin network wrapper with generated AuthControllerApi)
5. Implement AuthRepositoryImpl (core:auth) using RemoteAuthDataSource
6. Finalize EncryptedTokenStorage (MVP)
7. Implement SessionManagerImpl (session state + TokenProvider)
8. Add OkHttp TokenRefreshAuthenticator (auto refresh with rotation support)
9. Wire DI for Auth stack (generated AuthControllerApi + custom OkHttp/Retrofit)
10. Implement Auth ViewModels (Login + Signup)
11. Wire Auth screens + navigation gates to real SessionState
12. Minimal tests: auth contract integration + refresh flow (generated APIs/models)