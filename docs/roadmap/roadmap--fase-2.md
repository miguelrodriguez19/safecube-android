# FASE 2 — Roadmap (Auth Flow Completo)

## Objetivo de la fase

Construir el flujo real de **Register/Login/Token storage/Refresh/Logout**, dejando al usuario en
estado **LoggedInVaultLocked** (autenticado pero con vault todavía bloqueado). Esto está alineado
con el roadmap móvil y con la separación estricta “Login != Vault unlock” .

## Contrato backend que vamos a implementar en esta fase

Endpoints del slice `auth`:

* `POST /auth/register`
* `POST /auth/login`
* `POST /auth/refresh`
* `POST /auth/logout`

Reglas clave: refresh token **opaco con rotación obligatoria**, access token JWT corto, logout
revoca refresh tokens .

## Decisiones MVP (para no sobre-ingenierizar)

* **Errores HTTP homogéneos**: parsear `ErrorResponse` cuando aplique (400 con `fields`, etc.).

---

# Tasks — Fase 2

# Add Auth DTOs (requests/responses)

## Main Story (How, I Want, To)

Como developer, quiero tener DTOs Kotlin alineados con OpenAPI, para serializar/deserializar auth
sin inventar campos.

## Context, Functional Description & Goal

Crear los modelos de request/response para `register/login/refresh` según el schema del backend.

## Steps/Scope

### In Scope

* Crear data classes:

    * `RegisterAccountRequest(email, password)`
    * `AuthenticateAccountRequest(email, password)`
    * `RefreshTokenRequest(refreshToken)`
    * `RegisterAccountResult(accountId, createdAt)`
    * `AuthTokensResponse(accessToken, refreshToken, issuedAt)`
* Asegurar anotaciones/adapter necesarios según el serializer ya configurado en `core:network`.

### Out of Scope (if applies)

* Implementar llamadas Retrofit/OkHttp.
* Persistencia de tokens.
* Lógica de UI/flows.

## Additional Information and Configuration

### API Contract and Expected Behavior (if applies)

Schemas en `components/schemas` y endpoints `/auth/*`.

### Acceptance Criteria (ACs)

* Los DTOs compilan y se pueden usar desde `core:network`/`core:auth`.
* Los campos coinciden con OpenAPI (nombres y tipos).

---

# Implement AuthApi (Retrofit interface)

## Main Story (How, I Want, To)

Como developer, quiero una interfaz Retrofit con los endpoints de auth, para poder ejecutar
register/login/refresh/logout contra backend.

## Context, Functional Description & Goal

Implementar `AuthApi` con las rutas y métodos definidos en OpenAPI.

## Steps/Scope

### In Scope

* Crear `AuthApi` con:

    * `POST /auth/register`
    * `POST /auth/login`
    * `POST /auth/refresh`
    * `POST /auth/logout`
* Tipar request/response usando DTOs del task #1.
* Asegurar base URL con el `servers[0].url` del spec (o el mecanismo existente en `NetworkConfig`).

### Out of Scope (if applies)

* Mapping de errores a UI.
* Refresh automático.
* Token storage.

## Additional Information and Configuration

### API Contract and Expected Behavior (if applies)

`/auth/register|login|refresh` devuelven `200` con body (según spec). `/auth/logout` `200` sin body.

### Acceptance Criteria (ACs)

* La interfaz `AuthApi` está disponible y conectada al `Retrofit` existente.
* Se puede instanciar desde DI (aunque aún no se use en UI).

---

# Add ErrorResponse model + parsing helper

## Main Story (How, I Want, To)

Como developer, quiero parsear errores HTTP de forma homogénea, para manejar validaciones y
credenciales inválidas sin acoplar la UI al backend.

## Context, Functional Description & Goal

El backend usa un formato uniforme `{ error, fields }` para validación web.
Necesitamos un modelo `ErrorResponse` y un helper para parsear `errorBody`.

## Steps/Scope

### In Scope

* Crear `ErrorResponse(error: String?, fields: Map<String, String>?)`.
* Crear helper `ErrorResponseParser.parse(responseBody: String): ErrorResponse?`.
* Añadir mapping mínimo “safe” (si no parsea, devolver null y tratarlo como error genérico).

### Out of Scope (if applies)

* Catálogo completo de códigos de error por feature.
* i18n de mensajes.

## Additional Information and Configuration

### API Contract and Expected Behavior (if applies)

Estrategia canónica de validación y errores: `ErrorResponse` con `fields` opcional.

### Acceptance Criteria (ACs)

* Dado un JSON con `{ "error": "...", "fields": {...} }` el parser devuelve el modelo.
* Dado un JSON inválido/no esperado, el parser no crashea.

---

# Create RemoteAuthDataSource (thin network wrapper)

## Main Story (How, I Want, To)

Como developer, quiero una capa mínima remota para auth, para aislar Retrofit del repositorio y
facilitar tests.

## Context, Functional Description & Goal

Encapsular llamadas `AuthApi` y devolver resultados tipados (success/error) para que `core:auth` no
dependa de Retrofit directamente. (Arquitectura actual por módulos).

## Steps/Scope

### In Scope

* Crear `RemoteAuthDataSource` con métodos:

    * `register(request)`
    * `login(request)`
    * `refresh(request)`
    * `logout()`
* Devolver un resultado tipo `NetworkResult<T>` (o el wrapper existente).

### Out of Scope (if applies)

* Persistencia de tokens.
* Session state.
* UI.

## Additional Information and Configuration

### API Contract and Expected Behavior (if applies)

Las rutas y responses están definidas por OpenAPI.

### Acceptance Criteria (ACs)

* `RemoteAuthDataSource` compila y se puede usar desde `core:auth`.
* Los errores HTTP se exponen (code + body/errorBody) sin perder información.

---

# Implement AuthRepositoryImpl (core:auth)

## Main Story (How, I Want, To)

Como developer, quiero un `AuthRepository` real, para que `feature:auth` deje de depender de
mocks/fakes.

## Context, Functional Description & Goal

El contrato de sesión del backend define rotación obligatoria de refresh y logout revocando
sesiones.
Implementar `AuthRepositoryImpl` usando `RemoteAuthDataSource`.

## Steps/Scope

### In Scope

* Implementar `AuthRepositoryImpl : AuthRepository`:

    * `register(email, password)`
    * `login(email, password)`
    * `refresh(refreshToken)`
    * `logout()`
* Mapear errores a un modelo estable (p.ej. `AuthError.InvalidCredentials`,
  `AuthError.ValidationFailed(fields)`, etc.) usando `ErrorResponse`.

### Out of Scope (if applies)

* Guardar tokens en storage (eso va en tasks separados).
* Refresh automático (Authenticator).
* Vault unlock.

## Additional Information and Configuration

### API Contract and Expected Behavior (if applies)

* `/auth/refresh` siempre devuelve un nuevo par de tokens (rotación).
* `/auth/logout` revoca sesiones (refresh tokens).

### Acceptance Criteria (ACs)

* `AuthRepositoryImpl` está cableado y usable desde `feature:auth`.
* Errores 400 se exponen con `fields` si están presentes.

---

# Finalize EncryptedTokenStorage (MVP)

## Main Story (How, I Want, To)

Como developer, quiero persistir `accessToken` y `refreshToken` de forma segura, para soportar “cold
start” sin re-login inmediato.

## Context, Functional Description & Goal

En `core:auth` ya existe `TokenStorage` y una implementación `EncryptedTokenStorage`.
Esta tarea asegura que la implementación sea completa y testeable.

## Steps/Scope

### In Scope

* Implementar/terminar `EncryptedTokenStorage`:

    * `saveTokens(accessToken, refreshToken, issuedAt)`
    * `getAccessToken() / getRefreshToken() / getIssuedAt()`
    * `clear()`
* Evitar logs con tokens.

### Out of Scope (if applies)

* Biometría.
* Vault keys.
* Refresh automático.

## Additional Information and Configuration

### API Contract and Expected Behavior (if applies)

Los tokens son los del `AuthTokensResponse`.

### Acceptance Criteria (ACs)

* Persistencia funciona tras cerrar/abrir app (lectura consistente).
* `clear()` elimina todo y deja el sistema en LoggedOut (cuando se conecte SessionManager).

---

# Implement SessionManagerImpl (session state + TokenProvider)

## Main Story (How, I Want, To)

Como developer, quiero un `SessionManager` real que exponga estado por `Flow/StateFlow` y provea
access token a network, para que navegación y requests se basen en un único source of truth.

## Context, Functional Description & Goal

La app tiene gates de navegación y `TokenProvider` en `core:network`.
Esta tarea conecta tokens persistidos con el estado de sesión, dejando al usuario en *
*LoggedInVaultLocked** (login ≠ unlock).

## Steps/Scope

### In Scope

* Crear `SessionManagerImpl` que use `TokenStorage`.
* Exponer `sessionState: StateFlow<SessionState>`.
* Implementar `TokenProvider` para `AuthInterceptor`.
* Añadir `forceLogout()` que limpie tokens y emita `LoggedOut`.

### Out of Scope (if applies)

* Vault unlock / derivación de keys.
* Refresh automático (Authenticator).

## Additional Information and Configuration

### API Contract and Expected Behavior (if applies)

Separación explícita login vs unlock (ADR-004).

### Acceptance Criteria (ACs)

* Si hay tokens en storage al arrancar, estado = `LoggedInVaultLocked`.
* Si no hay tokens, estado = `LoggedOut`.
* `forceLogout()` limpia storage y actualiza estado.

---

# Add OkHttp TokenRefreshAuthenticator (auto refresh)

## Main Story (How, I Want, To)

Como developer, quiero refresh automático de sesión cuando recibo `401`, para que la app sea usable
sin que el usuario tenga que re-loguearse constantemente.

## Context, Functional Description & Goal

El backend exige **rotación obligatoria** de refresh token; si refresh falla, se fuerza re-login.

## Steps/Scope

### In Scope

* Implementar `TokenRefreshAuthenticator : okhttp3.Authenticator`:

    * Ante `401`, llamar a `AuthRepository.refresh(refreshToken)`
    * Persistir nuevos tokens en `TokenStorage`
    * Reintentar la request con el nuevo `Authorization: Bearer ...`
* Prevenir carreras: lock/mutex para múltiples 401 simultáneos.
* Usar un cliente separado para el refresh (para evitar loops del authenticator).

### Out of Scope (if applies)

* “Refresh por tiempo” (decodificar JWT `exp`) en MVP.
* Retries genéricos no relacionados con auth.

## Additional Information and Configuration

### API Contract and Expected Behavior (if applies)

Refresh rota siempre y puede fallar con `InvalidCredentials`/401 ⇒ re-login.

### Acceptance Criteria (ACs)

* Caso 401 -> refresh 200 -> retry 200 funciona.
* Caso 401 -> refresh 401 -> `SessionManager.forceLogout()` se dispara.

---

# Wire DI for Auth stack (modules)

## Main Story (How, I Want, To)

Como developer, quiero que DI provea todas las piezas reales de auth, para poder usarlo desde UI sin
instanciación manual.

## Context, Functional Description & Goal

Ya existen módulos DI en `core:*`.
Esta tarea conecta: `AuthApi`, `RemoteAuthDataSource`, `AuthRepositoryImpl`, `TokenStorage`,
`SessionManagerImpl`, `TokenRefreshAuthenticator`.

## Steps/Scope

### In Scope

* Actualizar `NetworkModule` / `AuthModule`:

    * Provider de `AuthApi`
    * Provider de `AuthRepositoryImpl`
    * Provider de `EncryptedTokenStorage`
    * Provider de `SessionManagerImpl`
    * Configurar `OkHttpClient` con:

        * `AuthInterceptor(TokenProvider)`
        * `TokenRefreshAuthenticator`
* Mantener dependencias “hacia abajo” (app/feature -> core).

### Out of Scope (if applies)

* Refactor grande de módulos.
* Multi-base-url por flavors (si no existe ya).

## Additional Information and Configuration

### API Contract and Expected Behavior (if applies)

Security global bearerAuth en el spec.

### Acceptance Criteria (ACs)

* Desde `feature:auth` se puede inyectar `AuthRepository` y `SessionManager`.
* Requests autenticadas incluyen `Authorization: Bearer <accessToken>` cuando hay sesión.

---

# Implement Auth ViewModels (Login + Signup)

## Main Story (How, I Want, To)

Como developer, quiero ViewModels para login/signup con estados de UI, para conectar pantallas a
auth real sin meter lógica en composables.

## Context, Functional Description & Goal

`feature:auth` ya tiene pantallas, pero falta el wiring real.
El objetivo es completar el flujo y dejar al usuario autenticado con vault bloqueado (no unlock).

## Steps/Scope

### In Scope

* Crear `LoginViewModel` y `SignupViewModel` (o uno unificado):

    * `uiState` con loading/success/error
    * calls a `AuthRepository.login/register`
    * en success: persistencia de tokens vía `SessionManager`/`TokenStorage` (según tu diseño
      actual)
* Mapear errores comunes:

    * 401 -> credenciales inválidas
    * 409 -> cuenta ya existe (register)
    * 400 -> `fields`

### Out of Scope (if applies)

* UX avanzada, animaciones, i18n.
* Vault unlock.

## Additional Information and Configuration

### API Contract and Expected Behavior (if applies)

Login/register endpoints y códigos.

### Acceptance Criteria (ACs)

* Los ViewModels ejecutan login/register reales y reflejan loading/error.
* Al éxito, el `SessionState` cambia a `LoggedInVaultLocked`.

---

# Wire Auth screens + navigation gates to real SessionState

## Main Story (How, I Want, To)

Como developer, quiero que la navegación dependa del estado real de sesión, para que la app enrute
correctamente entre LoggedOut y LoggedInVaultLocked.

## Context, Functional Description & Goal

En `app/navigation` ya existen gates (`NavigationGates.kt`).
Conectar gates a `SessionManager.sessionState`.

## Steps/Scope

### In Scope

* Actualizar `NavigationGates`/`NavigationWrapper` para observar `SessionState`.
* Asegurar flujo:

    * `LoggedOut` -> Welcome/Login/Signup
    * `LoggedInVaultLocked` -> `PostLoginGateScreen` (o route equivalente)
* Añadir acción de logout (mínima) que llame `AuthRepository.logout()` y luego
  `SessionManager.forceLogout()`.

### Out of Scope (if applies)

* Pantallas de vault unlock reales (Fase 3).
* Perfil (`/user/profile`) y vault endpoints.

## Additional Information and Configuration

### API Contract and Expected Behavior (if applies)

Logout revoca sesiones; el cliente debe limpiar tokens siempre.

### Acceptance Criteria (ACs)

* Tras login, se navega a flujo “vault locked”.
* Tras logout (o refresh fallido), se vuelve a Welcome/Login.

---

# Minimal tests: auth contract + refresh flow

## Main Story (How, I Want, To)

Como developer, quiero tests mínimos y deterministas, para validar que el stack de auth funciona y
no se rompe con refactors.

## Context, Functional Description & Goal

Backend valida de forma estricta y responde `ErrorResponse` homogéneo.
Además, refresh rota tokens obligatoriamente.

## Steps/Scope

### In Scope

* Tests con `MockWebServer`:

    * `AuthRepositoryImpl` happy-path login/refresh/logout.
    * Parser de `ErrorResponse`.
    * Secuencia `401 -> refresh -> retry`.
* Un test (instrumented o unit, según tu setup) para `EncryptedTokenStorage` (save/read/clear).

### Out of Scope (if applies)

* Tests E2E UI (Compose) completos.
* Tests de vault/crypto.

## Additional Information and Configuration

### API Contract and Expected Behavior (if applies)

Basado en OpenAPI endpoints y formato de error.

### Acceptance Criteria (ACs)

* Los tests pasan en CI/local.
* Hay cobertura del flujo crítico: refresh + fallback a logout.