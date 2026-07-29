# Crypto v1 (Client Contract)

| Spec ID          | Status     | Owner    | Last reviewed | Supersedes | Related ADRs |
|------------------|------------|----------|---------------|------------|--------------|
| `SPEC-CRYPTO-V1` | `APPROVED` | `crypto` | `2026-07-29`  | `N/A`      | `N/A`        |

Estado: `canonical`  
Versión: `v1`

## 1. Objetivo

Definir el contrato criptográfico v1 del cliente para implementación consistente con backend, con
enfoque en:

- KDF para derivación de `MASTER_KEY` desde `Passphrase / Master Password`.
- AEAD para cifrado autenticado.
- Envelope binario versionado.
- Reglas de AAD y seguridad operativa.

## 2. Jerarquía de llaves (cliente)

```text
Passphrase / Master Password
   ↓ KDF
MASTER_KEY
   ↓ unwrap
KEK
   ↓ unwrap
DEK
   ↓ encrypt/decrypt
Payload
```

Notas:

* El backend persiste solo blobs opacos de material envuelto (`kekEncMaster`, `kekEncRecovery`).
* El cliente reconstruye localmente la jerarquía de llaves.
* `kekEncMaster` y `kekEncRecovery` contienen la `KEK` envuelta; la `KEK` nunca se almacena ni
  transmite en claro.
* En SafeCube v1, `Vault Key (VK)` queda como término conceptual del backend. El término operativo y
  canónico en cliente es `KEK`.

## 3. Lenguaje ubicuo (canónico)

### 3.1 Términos canónicos para cliente

* `Passphrase / Master Password`: secreto memorizado por el usuario.
* `MASTER_KEY`: llave derivada localmente desde la passphrase mediante KDF.
* `KEK`: `Key Encryption Key`; clave simétrica generada en cliente y protegida mediante `MASTER_KEY`
  o `RECOVERY_KEY`.
* `RECOVERY_KEY`: clave aleatoria de recuperación, mostrada una sola vez al usuario.
* `DEK`: `Data Encryption Key`; clave única por item.
* `Payload`: contenido cifrado del secreto.

### 3.2 Relación con nombres del backend

* `kekEncMaster`: blob opaco que contiene la `KEK` envuelta con `MASTER_KEY`.
* `kekEncRecovery`: blob opaco que contiene la `KEK` envuelta con `RECOVERY_KEY`.
* `cryptoVersion`: versión del contrato criptográfico del cliente.
* `Vault Key (VK)`: término conceptual del backend; no se usa como término operativo en este
  contrato cliente.

## 4. KDF (obligatorio)

Parámetros v1:

* `kdfAlgorithm`: `ARGON2ID`
* `memoryKib`: entero positivo
* `iterations`: entero positivo
* `parallelism`: entero positivo
* `outputLen`: `32` bytes (fijo)
* `kdfSalt`: bytes aleatorios por cuenta

Salida:

* `MASTER_KEY` de 32 bytes, derivada localmente desde `Passphrase / Master Password`.

## 5. AEAD (obligatorio)

Algoritmo v1:

* `AES-256-GCM`

Parámetros:

* `keyLen`: 32 bytes
* `nonceLen`: 12 bytes (fijo)
* `tagLen`: 16 bytes (128-bit, estándar GCM)

Definición:

* El `tag` es un código de autenticación criptográfico generado por el algoritmo AEAD.
* El `tag` garantiza integridad y autenticidad del `ciphertext` y del `AAD`.
* Si el `tag` no coincide durante el descifrado, la operación debe fallar.

Regla:

* En GCM, reutilizar `nonce` con la misma clave está prohibido.

## 6. Envelope binario v1

Formato canónico:

```text
[v1][nonce][ciphertext||tag]
```

Definición:

* `v1`: marcador de versión de 1 byte (`0x01`)
* `nonce`: 12 bytes
* `ciphertext||tag`: resultado directo de la operación AEAD

Notas:

* Un byte permite hasta 256 versiones distintas de formato.
* En implementaciones JVM/Android con AES-GCM, el resultado de cifrado suele devolverse como
  `ciphertext||tag`.

Reglas:

* El envelope es binario.
* Para transporte HTTP/JSON se codifica como Base64.
* El backend almacena el blob como datos opacos sin interpretación.
* `cryptoVersion` backend debe ser `v1` para este contrato.

### 6.1 Parsing del envelope

Para descifrar un blob:

1. Leer byte `0` → `version`.
2. Validar que `version == 0x01`.
3. Extraer `nonce` en rango `[1..12]`.
4. El resto del blob es `ciphertext||tag`.
5. Ejecutar AEAD decrypt con:

    * `key`
    * `nonce`
    * `ciphertext||tag`
    * `AAD` (si aplica)

Si la autenticación falla, el descifrado debe fallar.

## 7. AAD (Associated Authenticated Data)

El `AAD` es obligatorio cuando se configure para un propósito y debe ser determinista.

Formato recomendado para envolver/desenvolver `KEK`:

```text
accountId:<uuid>|purpose:kek
```

Reglas:

* Mismo `AAD` exacto en cifrado y descifrado.
* Cambios de formato, orden o encoding invalidan autenticación.
* Encoding recomendado: UTF-8.

## 8. Contrato con backend (v1)

Campos de `VaultKeyMaterial` relevantes para cliente:

* `kekEncMaster`
* `kekEncRecovery`
* `kdfAlgorithm`
* `kdfSalt`
* `kdfMemoryKib`
* `kdfIterations`
* `kdfParallelism`
* `kdfOutputLen` (`32`)
* `cryptoVersion` (`v1`)

### 8.1 Semántica de recovery/passphrase change en v1

* La operación de actualización de material maestro (`PUT /vault/keys/master`) se interpreta como
  actualización de envoltorio (`rewrap`) del material de KEK.
* En v1 no implica re-encriptado masivo de items ni rotación obligatoria de DEK por sí sola.
* Si en el futuro se añade rotación real de KEK, debe introducirse versionado explícito de material
  de clave para coordinación multi-cliente.

## 9. Reglas de seguridad

* `nonce` único por operación de cifrado con una misma clave.
* RNG criptográfico obligatorio para `nonce`, `kdfSalt` y claves aleatorias.
* `zeroize` best-effort para material sensible en memoria:

    * `Passphrase / Master Password`
    * `MASTER_KEY`
    * `KEK`
    * `DEK`
    * plaintext
* Fallar en cerrado: si falla autenticación AEAD, no devolver plaintext parcial.
* Nunca enviar llaves en claro al backend.

## 10. Fuera de alcance

* Implementación crypto.
* UI y flujos de vault unlock/recovery.
* Integración CI/CD.
