# SecureItem Payload v1 (Client Contract)

Estado: `canonical`  
Version: `v1`

## 1. Objetivo

Definir el contrato cliente de `SecureItem` para que:

- `Room` persista una identidad estable del item.
- `core:vault` cifre y descifre siempre con el mismo protocolo.
- futura sync distinga claramente identidad local de identidad remota.

Este documento complementa [crypto-v1.md](./crypto-v1.md). `crypto-v1` define primitives y reglas
criptograficas base; este documento define como se aplican a `SecureItem`.

## 2. Contrato backend vs contrato cliente

El backend de `SecureItem` solo conoce:

- `itemType`
- `displayHint`
- `schemaVersion`
- `payload`
- `payloadVersion`
- `itemId` remoto

El cliente define adicionalmente:

- `logicalItemId`
- `remoteItemId`
- envelope binario del `payload`
- AAD canonico
- schema del contenido en claro para cada `itemType`

Regla:

- el backend sigue tratando `payload` como blob opaco Base64 sobre JSON.

## 3. Identidad del item

### 3.1 `logicalItemId`

`logicalItemId` es un UUID generado por cliente al crear el item.

Semantica:

- es la identidad estable del item en cliente.
- es la primary key logica para `Room`.
- participa en el `payload` envelope.
- participa en el AAD canonico.
- nunca depende de haber sincronizado con backend.

### 3.2 `remoteItemId`

`remoteItemId` es el UUID asignado por backend cuando exista representacion remota del item.

Semantica:

- puede ser `null` mientras el item exista solo offline.
- no participa en el AAD.
- no participa en el envelope criptografico.
- no reemplaza a `logicalItemId`.

### 3.3 Regla de coexistencia

Para un mismo item:

- `logicalItemId` se conserva estable durante toda la vida del item.
- `remoteItemId` puede aparecer mas tarde y se modela aparte.
- si en el futuro cambia la representacion remota, `logicalItemId` no cambia.

## 4. Modelo local canonico

El registro local canonico de `SecureItem` debe poder representar como minimo:

| Campo            | Tipo        | Regla                                       |
|------------------|-------------|---------------------------------------------|
| `logicalItemId`  | `UUID`      | obligatorio; identidad estable del cliente  |
| `remoteItemId`   | `UUID?`     | opcional; identidad backend                 |
| `itemType`       | `Enum`      | obligatorio; MVP: `PASSWORD`, `NOTE`        |
| `schemaVersion`  | `Int`       | obligatorio; version del contenido en claro |
| `displayHint`    | `String`    | obligatorio; no sensible                    |
| `payload`        | `ByteArray` | obligatorio; envelope binario v1            |
| `payloadVersion` | `Long`      | obligatorio; empieza en `1`                 |
| `updatedAt`      | `Instant`   | obligatorio                                 |
| `deletedAt`      | `Instant?`  | opcional; tombstone local                   |

Reglas:

- `displayHint` vive fuera del cifrado y no debe contener secretos.
- `payload` es el unico blob secreto persistido para el contenido del item.
- `schemaVersion` y `payloadVersion` no son equivalentes.

## 5. Envelope binario `payload` v1

`payload` usa un envelope cliente-propio por encima de las primitives definidas en
[crypto-v1.md](./crypto-v1.md).

Formato canonico:

```text
[payloadEnvelopeVersion:1][logicalItemId:16][wrappedDekLength:2][wrappedDek:N][nonce:12][ciphertext||tag:M]
```

Definicion:

- `payloadEnvelopeVersion`: 1 byte. En v1 debe ser `0x01`.
- `logicalItemId`: UUID binario canonico de 16 bytes.
- `wrappedDekLength`: entero unsigned big-endian de 2 bytes.
- `wrappedDek`: blob opaco generado por `KeyWrapping.wrapKey`, versionado por su propio contrato.
- `nonce`: 12 bytes aleatorios para `AES-256-GCM`.
- `ciphertext||tag`: salida directa de AEAD sobre el contenido en claro serializado.

Reglas:

- el envelope completo es binario.
- para transporte HTTP/JSON, `payload` se codifica en Base64.
- `wrappedDek` no se interpreta fuera de `core:vault`.
- el `logicalItemId` embebido en el envelope debe coincidir exactamente con el `logicalItemId`
  del registro local. Si no coincide, el payload es invalido.

Notas:

- `payloadEnvelopeVersion` versiona el contenedor binario.
- `schemaVersion` versiona el contenido en claro.
- `wrappedDek` puede tener su propio versionado interno sin cambiar `payloadEnvelopeVersion`.

## 6. AAD canonico del payload

El AAD v1 del `payload` es obligatorio y usa UTF-8.

Formato canonico:

```text
accountId:<uuid>|logicalItemId:<uuid>|payloadVersion:<long>
```

Reglas:

- el string debe construirse exactamente en ese orden.
- las claves literales son exactamente `accountId`, `logicalItemId`, `payloadVersion`.
- `accountId` sale del `VaultKeyMaterial` cacheado.
- `logicalItemId` sale de la identidad local del item, no del backend.
- `payloadVersion` sale del registro local actual.
- mismo AAD exacto en encrypt y decrypt.

Consecuencia:

- si se intercambia el `payload` entre dos filas con distinto `logicalItemId`, el decrypt debe
  fallar.

## 7. Contenido en claro y serializacion

Antes de cifrar, el contenido secreto del item se serializa a JSON UTF-8.

Reglas de serializacion v1:

- JSON object UTF-8 sin BOM.
- sin pretty print.
- sin campos extra.
- las keys deben emitirse en el orden canonico definido por este documento.
- los strings se conservan tal como los entrega dominio; no hay normalizacion adicional.
- `displayHint` no forma parte de este JSON.

## 8. `itemType` MVP

Los primeros `itemType` soportados por cliente son:

- `PASSWORD`
- `NOTE`

En v1:

- ambos usan `schemaVersion = 1`.
- cualquier otro `itemType` queda fuera de alcance de este contrato.

### 8.1 `PASSWORD` v1 cleartext schema

JSON canónico:

```json
{
  "username": "user",
  "email": "user@example.com",
  "password": "secret",
  "website": {
    "url": "https://example.com",
    "domain": "example.com"
  },
  "notes": "optional",
  "totp": {
    "secret": "BASE32SECRET",
    "issuer": "Example",
    "accountName": "user@example.com"
  }
}
```

Campos (Kotlin):

```kotlin
data class PasswordItemContentV1(
    val username: String?,
    val email: String?,
    val password: String,
    val website: PasswordWebsiteV1?,
    val notes: String?,
    val totp: PasswordTotpV1?
)

data class PasswordWebsiteV1(
    val url: String?,
    val domain: String?
)

data class PasswordTotpV1(
    val secret: String,
    val issuer: String?,
    val accountName: String?
)
```

Reglas:

- `password` es obligatorio y no puede estar vacío.
- al menos uno de `username` o `email` debe existir y no puede estar vacío.
- `website` es opcional.
- si `website` está presente, al menos uno de `url` o `domain` debe existir y no puede estar vacío.
- `notes` es opcional.
- `totp` es opcional.
- si `totp` está presente, `secret` es obligatorio y no puede estar vacío.
- `issuer` y `accountName` son opcionales dentro de `totp`.
- todos estos campos forman parte del contenido secreto del item y deben ir completamente cifrados.
- `displayHint` no forma parte de este schema; vive fuera del payload cifrado como metadata no
  sensible.
- el orden canónico de serialización es:

    1. `username`
    2. `email`
    3. `password`
    4. `website`
    5. `notes`
    6. `totp`
- dentro de `website`, el orden canónico es:

    1. `url`
    2. `domain`
- dentro de `totp`, el orden canónico es:

    1. `secret`
    2. `issuer`
    3. `accountName`

### 8.2 `NOTE` v1 cleartext schema

JSON canonico:

```json
{
  "noteBody": "private text"
}
```

Campos:

- `body: String`

Reglas:

- `body` es obligatorio.
- `body` soporta markdown, la UI ya se encargará de renderizarlo.

- el orden canonico de serializacion es: `body`.

## 9. Reglas de `payloadVersion`

`payloadVersion` modela la version del contenido cifrado del item.

Reglas v1:

- empieza en `1` al crear el item.
- solo incrementa cuando cambia el contenido secreto serializado y, por tanto, cambia el
  `payload` cifrado.
- cambiar solo `displayHint` no obliga a incrementar `payloadVersion`.
- cambiar solo `remoteItemId`, `updatedAt` o `deletedAt` no obliga a incrementar `payloadVersion`.
- un cambio de `schemaVersion` que implique reserializar y recifrar el contenido debe incrementar
  `payloadVersion`.

## 10. Regla de reset de passphrase o recovery

`wrappedDEK` no cambia por operaciones de reset de passphrase o recovery.

Razon:

- passphrase reset o recovery reset en v1 reenvuelve la `KEK`.
- `wrappedDEK` es la `DEK` envuelta con la `KEK`.
- si la `KEK` efectiva no rota, los items no necesitan rewrap ni reencriptado.

Regla canonica:

- operaciones sobre `kekEncMaster` o `kekEncRecovery` no deben modificar `wrappedDEK`,
  `payloadEnvelopeVersion`, `logicalItemId`, `payloadVersion` ni `ciphertext` de un `SecureItem`.

Consecuencia:

- los items existentes siguen siendo validos despues de un reset de passphrase o recovery en v1.

## 11. Compatibilidad con `crypto-v1`

Este contrato debe respetar siempre:

- `AES-256-GCM`
- `nonce` de 12 bytes
- `tag` de 16 bytes
- encoding UTF-8 para el AAD
- fallo en cerrado si la autenticacion AEAD falla

`crypto-v1` gobierna la primitive AEAD y el manejo de llaves; este documento gobierna el protocolo
de `SecureItem`.

## 12. Relación con la sincronización

Este documento define únicamente el envelope y el contenido cifrado de `SecureItem`. La
sincronización no debe inferir revisiones del payload ni modificar su semántica criptográfica.

La política vigente de sync y conflictos está definida en
[Vault Sync Versioning v2](./vault-sync-versioning-v2.md), que separa:

- `payloadVersion` como generación criptográfica del cliente.
- `itemRevision` como revisión CAS del backend.
- `changeSequence` como cursor de cambios por cuenta.

El alcance específico de este contrato sigue limitado a `PASSWORD` y `NOTE`; no define UI,
resolución semántica de secretos ni sincronización de otros dominios.
