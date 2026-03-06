# 🟢 FASE 3 — Crypto Real + Vault Unlock (Argon2id)

## Objetivo de la fase

Implementar criptografía real en cliente (Zero-Knowledge) y el flujo de **Vault Unlock**:

- KDF **Argon2id** (derivación de `MASTER_KEY` en cliente)
- Jerarquía de claves (cliente):
    - `MASTER_KEY` (KDF desde passphrase; nunca sale del cliente)
    - `KEK` (Vault Key aleatoria por cuenta; solo en RAM cuando unlock)
    - `RECOVERY_KEY` (clave de recuperación; se muestra una sola vez)
- Key wrapping (envolver/desenvolver `KEK` con `MASTER_KEY` y `RECOVERY_KEY`)
- Memory-only key handling (keys solo en RAM cuando el vault está unlocked)
- Recovery key generation (MVP: generar + mostrar una sola vez)
- VaultKeyMaterial remoto + cache offline-first

**Resultado esperado**
> Usuario autenticado puede **inicializar** su vault (si no existe), **desbloquearlo** (passphrase o
> recovery key), y el cliente puede **cifrar/descifrar localmente** (sin CRUD real de items aún).

---

## Estado actual (punto de partida del repo)

Según `package_structure.md`:

- ✅ `core:crypto` existe con interfaces (`CryptoEngine`, `KdfEngine`, `KeyWrapping`) y un
  `FakeCryptoEngine`.
- ✅ `core:auth` tiene sesión y un `FakeVaultSessionManager`/estado de vault.
- ✅ `core:network` ya tiene stack OkHttp/Retrofit propio + interceptor/refresh.
- ✅ `feature:vault` tiene pantallas (`CreateVaultScreen`, `RecoveryKeyScreen`, etc.) pero sin wiring
  real crypto/vault.
- ✅ OpenAPI generado ya incluye `VaultKeyMaterialControllerApi` + modelos.

---

## Contrato backend usado en esta fase (VaultKeyMaterial)

### Endpoints (OpenAPI)

- `GET /vault/keys` → `VaultKeyMaterialResponse`
- `POST /vault/keys` → `InitVaultKeyMaterialRequest`
- `PUT /vault/keys/master` → `UpdateMasterWrappedKekRequest` (solo “plumbing”; UX de rotación fuera
  de fase)

### Campos clave (backend almacena opaco)

`VaultKeyMaterial` incluye (mínimo):

- `kekEncMaster` (bytes opacos)
- `kekEncRecovery` (bytes opacos)
- `kdfAlgorithm` (ej. `ARGON2ID`)
- `kdfSalt`
- `kdfMemoryKib`, `kdfIterations`, `kdfParallelism`, `kdfOutputLen`
- `cryptoVersion`

El backend **no deriva** ni **descifra** nada: almacena blobs + params.

---

## Decisiones MVP (cerradas para esta fase)

### 1) Login ≠ Vault Unlock

- Login (fase 2) es autenticación contra backend.
- Vault unlock es operación local; puede necesitar descargar `VaultKeyMaterial` si no está cacheado.
- Si cuenta no está `ACTIVE` el backend puede bloquear el flujo (futuro); para MVP se asume
  `ACTIVE`.

### 2) KDF obligatorio: Argon2id

- `kdfAlgorithm = "ARGON2ID"`
- Los parámetros se guardan en backend y se cachean localmente, porque el cliente debe reproducir el
  KDF igual en cada dispositivo.

**Params MVP recomendados (mobile)**

- `kdfMemoryKib = 65536` (64 MiB)
- `kdfIterations = 3`
- `kdfParallelism = 1`
- `kdfOutputLen = 32`
- `kdfSaltLen = 16` (no via contrato, pero el cliente la genera)

> Si un dispositivo no soporta esos params, el cliente falla el unlock (no downgrade silencioso).

### 3) AEAD v1 (MVP)

Backend recomienda `XCHACHA20_POLY1305` preferido y `AES_256_GCM` como alternativa.
Para MVP Android-first:

- AEAD: `AES_256_GCM`
- `nonceLen = 12` bytes (GCM)
- Keys: 32 bytes

### 4) Envelope binario versionado para blobs envueltos

Para `kekEncMaster` y `kekEncRecovery`:

`wrappedBlob = [version:1][nonce:12][ciphertext+tag:N]`

`cryptoVersion = "v1"`

### 5) AAD (anti-swap)

Para key wrapping de `KEK`:

- `aad = "accountId:<uuid>|purpose:kek".utf8`

Para items (fase 4+): recomendado `accountId + itemId + payloadVersion` (se deja preparado).

### 6) Memory-only y zeroize best-effort

- `MASTER_KEY`, `KEK`, `RECOVERY_KEY` nunca se persisten en claro.
- Al bloquear vault / logout:
    - zeroize best-effort `ByteArray.fill(0)` en buffers temporales.
- Cache local solo guarda blobs opacos + params (nunca keys en claro).

### 7) Recovery Key (MVP)

- Se genera al inicializar vault.
- Se muestra **una sola vez**.
- Nunca se envía al backend.
- El backend solo almacena `kekEncRecovery`.

---

## Fuera de alcance (para no mezclar fases)

- CRUD real de `SecureItem` (fase 4).
- Sync incremental (fase 5).
- Rotación UX de passphrase (`PUT /vault/keys/master`) (fase 6/hardening).
- XChaCha20-Poly1305 (hardening posterior si se quiere).

---

# Tasks — Fase 3

---

# 0) Add and configure Kover test coverage for the project

## Main Story (How, I Want, To)

Como developer, quiero añadir y configurar cobertura de tests usando Kover para medir la cobertura real del código, generar reportes HTML y establecer un baseline antes de continuar con el desarrollo de crypto y vault.

## Context, Functional Description & Goal

El proyecto ya tiene tests unitarios en varios módulos (`core:auth`, `core:network`, `core:storage`) pero actualmente no existe una herramienta que mida la cobertura.
Antes de implementar lógica criptográfica y vault unlock es importante conocer qué partes del código están cubiertas por tests y cuáles no.

Kover es el plugin oficial de JetBrains para cobertura en proyectos Kotlin y funciona bien con:

* proyectos multi-module
* Gradle Kotlin DSL
* Android

El objetivo de esta tarea es:

* integrar Kover
* generar reportes de cobertura
* establecer baseline de cobertura del proyecto

## Steps/Scope

### In Scope

#### 1. Añadir plugin Kover en el proyecto

En el `build.gradle.kts` raíz:

```kotlin
plugins {
    id("org.jetbrains.kotlinx.kover") version "0.7.6"
}
```

#### 2. Aplicar Kover a módulos relevantes

Aplicar el plugin en módulos donde queremos medir cobertura.

Inicialmente:

* `core:auth`
* `core:network`
* `core:crypto`
* `core:storage`

Ejemplo:

```kotlin
plugins {
    id("org.jetbrains.kotlinx.kover")
}
```

#### 3. Configurar agregación de cobertura multi-module

En el `build.gradle.kts` raíz:

```kotlin
kover {
    isDisabled.set(false)
}
```

Esto permitirá generar reportes agregados del proyecto.

#### 4. Configurar exclusiones para Android/Kotlin

Excluir clases generadas o irrelevantes para cobertura:

```kotlin
kover {
    filters {
        excludes {
            classes(
                "*.BuildConfig",
                "*_Factory",
                "*_MembersInjector",
                "*_HiltModules*",
                "*_Impl",
                "*Dao_Impl",
                "*Database_Impl",
                "*ComposableSingletons*"
            )
            packages(
                "*.di.*",
                "*.generated.*"
            )
        }
    }
}
```

Esto evita que Hilt, Room o clases generadas distorsionen la cobertura.

#### 5. Generar reporte HTML de cobertura

Ejecutar:

```
./gradlew koverHtmlReport
```

El reporte se genera en:

```
build/reports/kover/html/index.html
```

y permite ver:

* cobertura por módulo
* cobertura por archivo
* líneas cubiertas y no cubiertas.

#### 6. Generar reporte XML (opcional para CI)

Ejecutar:

```
./gradlew koverXmlReport
```

Archivo generado:

```
build/reports/kover/report.xml
```

Compatible con herramientas externas de análisis.

#### 7. Analizar baseline de cobertura

Revisar cobertura actual en:

* `core:auth`
* `core:network`
* `core:crypto`
* `core:storage`

Identificar clases sin tests o con cobertura baja.

#### 8. Documentar uso en testing docs

Actualizar:

```
docs/testing.md
```

incluyendo:

* cómo ejecutar cobertura
* cómo abrir reportes
* módulos cubiertos por la medición.

### Out of Scope (if applies)

* integración obligatoria en CI
* bloqueo de builds por cobertura mínima
* análisis externo con SonarQube o Codecov
* cobertura de `androidTest`

Estas mejoras pueden añadirse en **Fase 6 — Hardening & QA**.

## Additional Information and Configuration

### API Contract and Expected Behavior (if applies)

No aplica.

## Acceptance Criteria (ACs)

* Kover está integrado en el proyecto
* se puede ejecutar

```
./gradlew koverHtmlReport
```

* se genera el reporte HTML de cobertura
* el reporte muestra cobertura por módulo
* las clases generadas por Hilt/Room no afectan la cobertura
* el uso queda documentado en `docs/testing.md`


---

# 1) Document Crypto v1 spec (Argon2id + AEAD + envelope)

## Main Story (How, I Want, To)

Como developer, quiero documentar el contrato criptográfico v1 del cliente, para asegurar que la
implementación crypto sea consistente con el backend y futura evolución del vault.

## Context, Functional Description & Goal

El backend ya define el modelo criptográfico del vault y jerarquía de llaves.
El cliente debe implementar exactamente ese contrato antes de escribir código crypto.

La jerarquía es:

```
Passphrase
   ↓ KDF
MASTER_KEY
   ↓ wrap
KEK
   ↓ wrap
DEK
   ↓ encrypt
Payload
```

El backend almacena solo blobs opacos (`kekEncMaster`, `kekEncRecovery`).

Esta tarea crea el documento **canónico de cliente**.

## Steps/Scope

### In Scope

Crear `docs/architecture/crypto-v1.md` con:

* KDF

    * `ARGON2ID`
    * `memoryKib`
    * `iterations`
    * `parallelism`
    * `outputLen = 32`

* AEAD

    * `AES-256-GCM`
    * nonce = 12 bytes

* envelope v1

```
[v1][nonce][ciphertext][tag]
```

* reglas de uso de AAD

AAD sugerido:

```
accountId:<uuid>|purpose:kek
```

* reglas de seguridad

    * nonce único
    * RNG criptográfico obligatorio
    * zeroize best-effort

### Out of Scope (if applies)

* Implementación crypto.
* UI o flujo de vault.

## Additional Information and Configuration

### API Contract and Expected Behavior (if applies)

Compatible con `Vault Crypto Strategy v1`.

### Acceptance Criteria (ACs)

* Documento existe en `docs/architecture`.
* Define parámetros crypto sin ambigüedad.
* Servirá como referencia para implementación.

---

# 2) Implement Argon2KdfEngine

## Main Story (How, I Want, To)

Como developer, quiero derivar una `MASTER_KEY` segura desde la passphrase usando Argon2id.

## Context, Functional Description & Goal

El backend define que el cliente derive la master key usando un KDF fuerte.

`core:crypto` ya tiene:

```
KdfEngine
KdfRequest
```

Esta tarea implementa el engine real.

## Steps/Scope

### In Scope

* Añadir dependencia Argon2 compatible Android.
* Crear:

```
Argon2KdfEngine : KdfEngine
```

* Implementar derivación:

```
passphrase + salt -> MASTER_KEY (32 bytes)
```

* Crear:

```
SaltGenerator
```

usando `SecureRandom`.

* Tests deterministas:

```
(passphrase + salt + params) -> output estable
```

### Out of Scope (if applies)

* Generación de KEK.
* Unlock de vault.

## Additional Information and Configuration

### API Contract and Expected Behavior (if applies)

Parámetros compatibles con `VaultKeyMaterial`.

### Acceptance Criteria (ACs)

* `Argon2KdfEngine` compila.
* Tests deterministas pasan.
* No se loggea la passphrase.

---

# 3) Implement AesGcmCryptoEngine

## Main Story (How, I Want, To)

Como developer, quiero cifrar y descifrar datos con AEAD para garantizar confidencialidad e
integridad.

## Context, Functional Description & Goal

El backend recomienda AEAD (`AES-GCM` o `XChaCha20`).

Tu arquitectura ya define:

```
CryptoEngine
EncryptionRequest
DecryptionRequest
```

Esta tarea sustituye el `FakeCryptoEngine`.

## Steps/Scope

### In Scope

Crear:

```
AesGcmCryptoEngine : CryptoEngine
```

Implementar:

```
encrypt(request)
decrypt(request)
```

Características:

* nonce random (12 bytes)
* AES/GCM/NoPadding
* soporte AAD

Tests:

* encrypt -> decrypt roundtrip
* AAD incorrecto falla

### Out of Scope (if applies)

* Key wrapping.
* Vault unlock.

## Additional Information and Configuration

### Acceptance Criteria (ACs)

* `FakeCryptoEngine` ya no se usa.
* Tests AEAD pasan.

---

# 4) Implement AesGcmKeyWrapping

## Main Story (How, I Want, To)

Como developer, quiero envolver y desenvolver claves usando AES-GCM para proteger la KEK.

## Context, Functional Description & Goal

La KEK debe almacenarse envuelta:

```
kekEncMaster
kekEncRecovery
```

El backend guarda estos blobs opacos.

## Steps/Scope

### In Scope

Crear:

```
AesGcmKeyWrapping : KeyWrapping
```

Implementar:

```
wrapKey()
unwrapKey()
```

Formato envelope v1:

```
[v1][nonce][ciphertext+tag]
```

AAD:

```
accountId:<uuid>|purpose:kek
```

Tests:

* wrap -> unwrap
* unwrap con AAD incorrecto falla

### Out of Scope (if applies)

* Network.
* VaultSession.

## Acceptance Criteria (ACs)

* Key wrapping funcional.
* Envelope versionado.

---

# 5) Allow VaultKeyMaterial OpenAPI contract usage (Phase 3)

## Main Story (How, I Want, To)

Como developer, quiero permitir el uso del contrato OpenAPI de vault key material en Fase 3.

## Context, Functional Description & Goal

Fase 2 solo permitía `AuthControllerApi`.
Fase 3 necesita:

```
GET /vault/keys
POST /vault/keys
PUT /vault/keys/master
```

## Steps/Scope

### In Scope

Crear documento:

```
docs/architecture/openapi-vault-key-material-contract-integration.md
```

Permitir uso de:

```
generated.api.VaultKeyMaterialControllerApi
generated.model.*
```

Prohibido:

```
generated.infrastructure.*
generated.auth.*
```

### Out of Scope (if applies)

* Implementación de data source.

### Acceptance Criteria (ACs)

* Regla documentada.
* Uso del contrato limitado a `core`.

---

# 6) Implement RemoteVaultKeyMaterialDataSource

## Main Story (How, I Want, To)

Como developer, quiero acceder a `/vault/keys` desde una capa remota aislada.

## Context, Functional Description & Goal

El backend define:

```
GET /vault/keys
POST /vault/keys
PUT /vault/keys/master
```

## Steps/Scope

### In Scope

Crear:

```
RemoteVaultKeyMaterialDataSource
```

Métodos:

```
getKeyMaterial()
initKeyMaterial()
updateMasterWrappedKek()
```

Mapeo de errores:

```
404 -> VaultNotInitialized
409 -> VaultAlreadyInitialized
401/403 -> Unauthorized
```

### Acceptance Criteria (ACs)

* Tests con `MockWebServer`.
* Manejo correcto de errores.

---

# 7) Implement VaultKeyMaterialCache

## Main Story (How, I Want, To)

Como developer, quiero cachear el material del vault para permitir unlock offline.

## Context, Functional Description & Goal

El cliente debe poder desbloquear vault **sin red**.

Se cachea:

* blobs opacos
* parámetros KDF

## Steps/Scope

### In Scope

Crear:

```
VaultKeyMaterialCache
```

Persistir:

```
kekEncMaster
kekEncRecovery
kdfAlgorithm
kdfSalt
kdfMemoryKib
kdfIterations
kdfParallelism
kdfOutputLen
cryptoVersion
```

Storage:

```
EncryptedSharedPreferences
```

### Acceptance Criteria (ACs)

* Save / get / clear funcionan.
* Nunca se guarda una key en claro.

---

# 8) Implement VaultInitializeUseCase

## Main Story (How, I Want, To)

Como developer, quiero inicializar el vault cuando aún no existe.

## Context, Functional Description & Goal

Si:

```
GET /vault/keys -> 404
```

el cliente debe crear el vault.

## Steps/Scope

### In Scope

Proceso:

1️⃣ generar `kdfSalt`

2️⃣ derivar `MASTER_KEY`

3️⃣ generar `KEK`

4️⃣ generar `RECOVERY_KEY`

5️⃣ wrap KEK

```
kekEncMaster
kekEncRecovery
```

6️⃣ `POST /vault/keys`

7️⃣ guardar en cache

8️⃣ devolver recovery key

### Acceptance Criteria (ACs)

* Flujo init completo funciona.
* Recovery key nunca se envía al backend.

---

# 9) Implement VaultUnlockUseCase

## Main Story (How, I Want, To)

Como developer, quiero desbloquear la KEK desde passphrase o recovery key.

## Context, Functional Description & Goal

El unlock es completamente cliente.

## Steps/Scope

### In Scope

Unlock passphrase:

```
MASTER_KEY = KDF(passphrase)
KEK = unwrap(kekEncMaster)
```

Unlock recovery:

```
KEK = unwrap(kekEncRecovery)
```

Resultado:

```
UnlockedKeyring(KEK)
```

### Acceptance Criteria (ACs)

* Passphrase correcta -> unlock
* Recovery key -> unlock
* Incorrectas -> error estable

---

# 10) Replace FakeVaultSessionManager with real implementation

## Main Story (How, I Want, To)

Como developer, quiero gestionar el estado del vault y mantener la KEK solo en RAM.

## Context, Functional Description & Goal

Ahora existe:

```
FakeVaultSessionManager
```

Debe sustituirse por la implementación real.

## Steps/Scope

### In Scope

Implementar:

```
VaultSessionManagerImpl
```

con:

```
vaultState: StateFlow<VaultState>
```

Métodos:

```
unlockWithPassphrase()
unlockWithRecoveryKey()
lock()
onLogout()
```

### Acceptance Criteria (ACs)

* KEK vive solo en RAM.
* lock limpia memoria.

---

# 11) Wire vault create/unlock UI flow

## Main Story (How, I Want, To)

Como developer, quiero conectar la UI con el sistema real de vault.

## Context, Functional Description & Goal

Las pantallas ya existen:

```
CreateVaultScreen
UnlockVaultScreen
RecoveryKeyScreen
VaultScreen
```

## Steps/Scope

### In Scope

ViewModels:

```
CreateVaultViewModel
UnlockVaultViewModel
RecoveryKeyViewModel
```

Flujo:

```
Login
 ↓
PostLoginGate
 ↓
CreateVault OR UnlockVault
 ↓
VaultScreen
```

### Acceptance Criteria (ACs)

* Usuario puede crear vault.
* Usuario puede desbloquear vault.

---

# 12) Add crypto + vault tests

## Main Story (How, I Want, To)

Como developer, quiero tests para el núcleo crypto/vault.

## Context, Functional Description & Goal

Crypto es el corazón del sistema.

## Steps/Scope

### In Scope

Unit tests:

```
Argon2KdfEngineTest
AesGcmCryptoEngineTest
AesGcmKeyWrappingTest
```

Integration:

```
VaultInitializeUseCaseTest
VaultUnlockUseCaseTest
RemoteVaultKeyMaterialDataSourceTest
```

### Acceptance Criteria (ACs)

* Tests pasan en CI.
* No se loggea material sensible.

---