
# FASE 4 - Vault CRUD Offline-First

## Objetivo de la fase

Implementar el CRUD real de items del vault en local, con cifrado por item y persistencia en
`Room`, sin sincronización remota todavía.

Incluye:

- crear item seguro
- editar item seguro
- borrado lógico
- listado local
- descifrado local cuando el vault está unlocked
- persistencia offline-first en `Room`

Resultado esperado:

> Usuario autenticado y con vault unlocked puede crear, leer, editar y borrar items cifrados sin
> depender de red.

---

## Estado actual

Según el estado actual del código:

- `core:crypto` ya tiene implementaciones reales para `Argon2id`, `AES-256-GCM` y `KeyWrapping`.
- `core:vault` ya resuelve `VaultInitializeUseCase`, `VaultUnlockUseCase` y mantiene la `KEK` solo
  en memoria vía `VaultSessionManagerImpl`.
- `core:storage` existe, pero `SecureItemEntity` y `SecureItemDao` siguen siendo esqueletos.
- `feature:vault` ya tiene el flujo `CreateVault -> RecoveryKey -> UnlockVault -> Vault`, pero la
  pantalla `VaultScreen` sigue siendo placeholder.
- OpenAPI ya expone el contrato remoto de `/vault/items`, pero en esta fase no vamos a usarlo en
  runtime.

Gap detectado importante:

- `VaultKeyMaterialResponse` del backend ya incluye `accountId`, pero el modelo/cache local de
  `core:vault` no lo conserva aún. Fase 4 lo necesita para el AAD de items.

---

## Contrato backend relevante para fases 4-5

Aunque en Fase 4 no haremos sync real, el modelo local debe nacer alineado con el contrato de
`vault/items`.

### Endpoints existentes

- `GET /vault/items`
- `GET /vault/items/{itemId}`
- `POST /vault/items`
- `PUT /vault/items/{itemId}`
- `DELETE /vault/items/{itemId}`

### Campos relevantes del contrato

Requests:

- `itemType`
- `schemaVersion`
- `displayHint`
- `payload`
- `updatedAt` en update

Responses:

- `itemId`
- `payloadVersion`
- `updatedAt`
- `deletedAt`

Interpretación para Fase 4:

- `displayHint` es metadato no sensible y vive fuera del payload cifrado.
- `payload` es blob opaco, producido y entendido solo por el cliente.
- `payloadVersion` y `deletedAt` deben existir ya en el modelo local para no rediseñar en Fase 5.

---

## Decisiones MVP (cerradas para esta fase)

### 1) Fase 4 = CRUD local real, sin `vault/items` remoto

- El source of truth de items en esta fase es `Room`.
- No se implementa `RemoteSecureItemDataSource`.
- No se hace `push`, `pull`, ni merge de conflictos.
- El contrato remoto solo se usa como referencia de compatibilidad futura.

### 2) Recovery / passphrase reset no rota la `KEK`

Se mantiene la decisión cerrada en brainstorming:

- recovery key o cambio de passphrase solo hacen `rewrap` de la `KEK`
- no se generan `DEK` nuevas
- no se reenvuelven items uno a uno
- los `wrappedDEK` existentes siguen siendo válidos

Consecuencia:

- Fase 4 no necesita `kekEpoch`
- una futura rotación real de `KEK` será otro flujo distinto (`vault rekey` / `security reset`)

### 3) Cifrado por item con `DEK` única por item

Cada item tendrá:

- una `DEK` aleatoria de 32 bytes
- payload cifrado con `DEK`
- `DEK` envuelta con la `KEK` actualmente unlocked

Jerarquía efectiva:

```text
Passphrase / Recovery Key
   -> unwrap KEK
   -> unwrap wrappedDEK
   -> decrypt item payload
```

La `MASTER_KEY` no cifra items directamente.

### 4) El cliente define un `logicalItemId` propio

No podemos usar el `itemId` del backend como identidad criptográfica del item porque:

- el backend lo genera en `POST /vault/items`
- en Fase 4 el item nace offline
- el AAD debe poder reconstruirse en todos los clientes

Decisión:

- cada item tendrá un `logicalItemId` UUID generado por cliente
- `logicalItemId` será la identidad estable del item en cliente y en el payload envelope
- el futuro `itemId` del backend se modela aparte como `remoteItemId`

Esto evita acoplar el cifrado local a un id remoto que aún no existe.

### 5) AAD de items usa `accountId + logicalItemId + payloadVersion`

Fase 3 ya dejó recomendado usar AAD para items. Cerramos el formato MVP:

```text
accountId:<uuid>|itemId:<logicalItemId>|payloadVersion:<long>
```

Reglas:

- mismo AAD exacto en encrypt/decrypt
- `accountId` sale del `VaultKeyMaterial` cacheado
- `payloadVersion` empieza en `1` y solo incrementa cuando cambia el payload cifrado

### 6) `displayHint` es no sensible y vive fuera del cifrado

Se alinea con OpenAPI:

- `displayHint` se persiste en claro en local
- se usa para listas y preview
- no debe contener secretos

Decisión de UX MVP:

- todos los editores MVP pedirán un `displayHint` no sensible
- el contenido secreto de cada item irá cifrado aparte

### 7) Envelope de payload v1 es cliente-propio y versionado

El blob `payload` del item será un envelope binario versionado, controlado por cliente.

Formato MVP propuesto:

```text
[envelopeVersionByte:1][logicalItemId:16][wrappedDekLength:2][wrappedDek:N][nonce:12][ciphertext||tag:M]
```

Donde:

- `wrappedDek` ya es un blob versionado producido por `KeyWrapping`
- `ciphertext||tag` contiene el JSON/bytes del contenido secreto cifrado con la `DEK`
- el backend sigue tratándolo como opaco

Nota:

- `schemaVersion` del item y versión del envelope no son lo mismo
- `schemaVersion` gobierna el formato del contenido en claro del item
- el byte de envelope gobierna el formato binario del blob `payload`

### 8) MVP de item types = `PASSWORD` + `NOTE`

SafeCube es ante todo un password manager, así que `PASSWORD` forma parte del MVP de esta fase
junto a `NOTE`.

Regla de alcance:

- el pipeline de dominio será extensible para `itemType`
- el contenido secreto irá cifrado completamente
- la UI y los casos de uso de Fase 4 soportan `PASSWORD` y `NOTE`
- `PASSWORD` es el caso principal del producto
- `schemaVersion = 1` para ambos tipos

Contenido secreto MVP de `PASSWORD`:

- `username`
- `password`
- `websiteUrl` opcional
- `notes` opcionales

Contenido secreto MVP de `NOTE`:

- `noteBody`

Metadata no sensible MVP para ambos:

- `displayHint`

### 9) `Room` es source of truth y el borrado es lógico

La tabla local debe conservar tombstones.

Reglas:

- crear item inserta fila nueva
- editar item actualiza `payload`, `updatedAt`, `payloadVersion`
- si solo cambia `displayHint`, cambia `updatedAt` pero no es obligatorio incrementar `payloadVersion`
- borrar item solo setea `deletedAt`
- los listados por defecto excluyen `deletedAt != null`
- no se hace hard delete salvo futura política de mantenimiento

### 10) La `KEK` no sale de `core:vault`

No queremos que `feature:vault`, `app` o `core:storage` manipulen claves crudas.

Decisión:

- el acceso a `KEK` quedará encapsulado en un servicio/caso de uso de `core:vault`
- el CRUD de items falla en cerrado si el vault está locked
- ninguna pantalla recibe `ByteArray` de claves

### 11) Fase 4 corrige un prerequisito de Fase 3

Antes de cifrar items hay que ampliar `VaultKeyMaterial` local para conservar al menos:

- `accountId`

Opcionalmente, si aporta orden al modelo:

- `createdAt`
- `updatedAt`

Sin `accountId` no podemos construir el AAD canónico de items.

### 12) Ownership modular de crypto de items

Para esta fase se cierra esta distribución de responsabilidades:

- `core:vault` puede depender de `core:crypto` y `core:storage`
- `core:vault` solo consume la API pública de `core:crypto`
- `core:crypto` expone primitivas reutilizables, pero no conoce `SecureItem`, `PASSWORD`, `NOTE`,
  `logicalItemId`, `payloadVersion` ni el protocolo del vault
- AAD, envelope `payload` v1, `logicalItemId`, `payloadVersion` y schemas `PASSWORD` / `NOTE`
  pertenecen a `core:vault`

Esto permite sustituir implementaciones crypto futuras sin mover el dominio del vault.

### 13) La UI del vault observa siempre estado local persistido

Aunque en fases futuras haya sync, en Fase 4 queda fijada esta regla:

- `Room` es la única fuente de verdad visible para la UI
- listados y detalle consumen `Flow` desde repositorio/DAO
- no existe escritura remota ni comportamiento `remote-first`
- la estrategia híbrida `online-preferred` queda para Fase 5

Esto evita introducir dos verdades distintas en UI antes de tiempo.

---

## Fuera de alcance

- Sync incremental de items
- `VaultControllerApi` en runtime
- conflictos multi-device
- estrategia híbrida `online-preferred`
- cola de sync / estados `pendingSync`
- `kekEpoch`
- rotación real de `KEK`
- `itemType` más allá de `PASSWORD` y `NOTE`
- carpetas reales
- search
- biometría
- hard delete

---

# Tasks - Fase 4

# 0) Completar `VaultKeyMaterial` local con `accountId`

## Main Story (How, I Want, To)

Como developer, quiero conservar `accountId` dentro del material de vault cacheado para poder
construir el AAD de los items de forma determinista.

## Context, Functional Description & Goal

El backend ya devuelve `accountId` en `VaultKeyMaterialResponse`, pero el modelo local actual de
`core:vault` lo descarta. Fase 4 necesita ese dato antes de crear el primer item cifrado.

## Steps/Scope

### In Scope

- ampliar `VaultKeyMaterial`
- mapear `accountId` en `RemoteVaultKeyMaterialDataSource`
- persistir `accountId` en `VaultKeyMaterialCache`
- ajustar tests existentes de `core:vault`

### Out of Scope (if applies)

- cambios de contrato backend

### Acceptance Criteria (ACs)

- `accountId` queda disponible en cache offline
- unlock y create vault siguen funcionando
- no se rompe compatibilidad del flujo de Fase 3

---

# 1) Documentar el contrato `SecureItem` v1 (payload + identidad local)

## Main Story (How, I Want, To)

Como developer, quiero un contrato cliente claro para `SecureItem`, para que Room, crypto y futura
sync trabajen sobre el mismo modelo.

## Context, Functional Description & Goal

El backend solo ve `itemType`, `displayHint`, `schemaVersion` y `payload` opaco. El cliente debe
definir:

- identidad estable del item
- envelope cifrado
- AAD
- schema del contenido en claro

## Steps/Scope

### In Scope

Crear documento nuevo, por ejemplo:

```text
docs/architecture/secure-item-payload-v1.md
```

Definir:

- `logicalItemId` vs `remoteItemId`
- envelope binario v1 del payload
- AAD canónico
- `PASSWORD` v1 y `NOTE` v1 como primeros item types
- reglas de `payloadVersion`
- regla explicitando que recovery/passphrase reset no cambia `wrappedDEK`

### Out of Scope (if applies)

- sync real
- `itemType` más allá de `PASSWORD` y `NOTE`

### Acceptance Criteria (ACs)

- existe un spec cliente consistente con `crypto-v1.md`
- queda cerrada la diferencia entre identidad local y `itemId` remoto

---

# 2) Implementar `SecureItemContentCodec` para `PASSWORD` y `NOTE`

## Main Story (How, I Want, To)

Como developer, quiero separar el schema funcional del contenido secreto del pipeline crypto, para
poder evolucionar item types sin mezclar semántica de negocio y primitivas de cifrado.

## Context, Functional Description & Goal

`schemaVersion` del contenido y envelope binario del payload son dos preocupaciones distintas.
Fase 4 necesita una capa explícita que convierta `PASSWORD` y `NOTE` entre modelo de dominio y
`ByteArray`/JSON serializable antes del cifrado.

## Steps/Scope

### In Scope

- definir codec/serializer de contenido secreto en `core:vault`
- soportar `PASSWORD` v1
- soportar `NOTE` v1
- versionar por `schemaVersion`
- dejar claro que esta capa no cifra ni descifra, solo serializa/deserializa contenido

### Out of Scope (if applies)

- soporte a item types futuros
- migraciones entre múltiples `schemaVersion`

### Acceptance Criteria (ACs)

- `PASSWORD` y `NOTE` pueden serializarse y rehidratarse de forma determinista
- el pipeline crypto no conoce campos funcionales como `username` o `noteBody`
- queda separada la responsabilidad entre schema funcional y envelope cifrado

---

# 3) Expandir Room schema para `SecureItem`

## Main Story (How, I Want, To)

Como developer, quiero una tabla real de `SecureItem` en `Room`, para que el vault funcione offline
con datos estructurados y listables.

## Context, Functional Description & Goal

`core:storage` ya tiene el bootstrap, pero `SecureItemEntity` y `SecureItemDao` siguen vacíos.
Fase 4 necesita un schema real y preparado para evolucionar.

## Steps/Scope

### In Scope

Definir `SecureItemEntity` con, como mínimo:

- `logicalItemId: UUID` como PK
- `remoteItemId: UUID?`
- `itemType: String`
- `schemaVersion: Int`
- `displayHint: String`
- `payload: ByteArray`
- `payloadVersion: Long`
- `createdAt: Instant`
- `updatedAt: Instant`
- `deletedAt: Instant?`

También:

- índices útiles (`remoteItemId`, `deletedAt`, `updatedAt`, `itemType`)
- `TypeConverter` para `UUID` / `Instant`
- export de schema de Room
- subida de versión de DB y migración correspondiente si aplica

### Out of Scope (if applies)

- tablas de folders
- tablas de sync queue

### Acceptance Criteria (ACs)

- la entidad ya representa CRUD offline real
- Room compila con schema exportado
- existe migración o estrategia explícita coherente con `storage_decision.md`

---

# 4) Implementar `SecureItemDao` y capa local de acceso a datos

## Main Story (How, I Want, To)

Como developer, quiero una capa local estable para leer y escribir items del vault sin acoplar la
UI a SQL ni a Room.

## Context, Functional Description & Goal

Fase 4 debe leer desde Room siempre. El acceso a datos debe quedar encapsulado para permitir que
Fase 5 añada sync sin reescribir `feature:vault`.

## Steps/Scope

### In Scope

DAO mínimo:

- `observeActiveItems()`
- `observeItem(logicalItemId)`
- `getItem(logicalItemId)`
- `insert(item)`
- `update(item)`
- `softDelete(logicalItemId, deletedAt, updatedAt)`

Arquitectura:

- `core:storage` expone DAO y data source/local storage implementation
- el repositorio de dominio vive en `core:vault`
- orden por defecto de listados: `updatedAt DESC`

### Out of Scope (if applies)

- repositorio remoto
- colas de sync

### Acceptance Criteria (ACs)

- la UI consume `Flow` y no SQL directo
- soft delete funciona sin perder la fila
- listados por defecto no muestran items borrados
- queda preservado que `Room` sea la única verdad visible para UI

---

# 5) Implementar el pipeline crypto de items

## Main Story (How, I Want, To)

Como developer, quiero cifrar y descifrar items con `DEK` por item y `KEK` en memoria, para que el
vault mantenga el modelo zero-knowledge también en el CRUD local.

## Context, Functional Description & Goal

La fase 3 ya deja lista la `KEK` en RAM y los engines crypto reales. Ahora falta aplicarlos a
items concretos sin filtrar detalles de implementación a la UI ni a storage.

## Steps/Scope

### In Scope

Crear servicio o componente de `core:vault`, por ejemplo:

- `SecureItemCryptoService`
- `VaultItemCipher`

Responsabilidades:

- generar `DEK`
- envolver/desenvolver `DEK` con `KEK`
- cifrar/descifrar contenido secreto
- construir/parsing del payload envelope v1
- construir AAD usando `accountId + logicalItemId + payloadVersion`

Reglas:

- si vault está locked, fallar en cerrado
- zeroize best-effort de buffers sensibles
- no exponer `KEK` fuera de `core:vault`
- consumir solo API pública de `core:crypto`

### Out of Scope (if applies)

- soporte para varios envelope versions
- biometría

### Acceptance Criteria (ACs)

- create/edit generan payloads descifrables
- un payload alterado falla al descifrar
- el componente no depende de UI
- la semántica de `SecureItem` sigue viviendo en `core:vault`

---

# 6) Implementar casos de uso de CRUD local

## Main Story (How, I Want, To)

Como developer, quiero casos de uso de alto nivel para crear, listar, leer, editar y borrar items,
para que `feature:vault` solo orqueste interacción de usuario.

## Context, Functional Description & Goal

El CRUD no debe quedar repartido entre ViewModels, DAO y servicios crypto. Necesita casos de uso
claros y testeables.

## Steps/Scope

### In Scope

Casos de uso MVP:

- `ObserveVaultItemSummariesUseCase`
- `ObserveSecureItemDetailUseCase`
- `CreateSecurePasswordUseCase`
- `UpdateSecurePasswordUseCase`
- `CreateSecureNoteUseCase`
- `UpdateSecureNoteUseCase`
- `SoftDeleteSecureItemUseCase`

Reglas de negocio:

- `payloadVersion = 1` en create
- `payloadVersion += 1` solo cuando cambia payload cifrado
- `updatedAt` cambia en create/update/delete
- `deletedAt` solo se setea en delete
- `displayHint` se trata en UX como campo no vacío y no sensible

Errores de dominio estables:

- `VaultLocked`
- `ItemNotFound`
- `ValidationError`
- `CorruptedPayload`

### Out of Scope (if applies)

- sync conflict resolution
- import/export

### Acceptance Criteria (ACs)

- la capa de dominio soporta CRUD completo offline
- create/update exigen vault unlocked
- delete es lógico y conserva tombstone local
- no existe restore UX en v1

---

# 7) Reemplazar `VaultScreen` placeholder por listado local real

## Main Story (How, I Want, To)

Como usuario, quiero ver mis items del vault en una lista real, para poder operar con mis secretos
locales cuando el vault está unlocked.

## Context, Functional Description & Goal

`VaultScreen` hoy solo muestra contenido dummy. Fase 4 necesita convertirlo en la home real del
vault.

## Steps/Scope

### In Scope

Implementar:

- listado de items activos desde Room
- empty state real
- CTA principal para crear nueva password entry
- CTA secundaria para crear nueva nota
- click en item para editarlo
- refresco reactivo vía `Flow`

La lista usa:

- `displayHint`
- `itemType`
- `updatedAt`

sin descifrar el payload completo para pintar la lista.

### Out of Scope (if applies)

- folders reales
- filtros avanzados

### Acceptance Criteria (ACs)

- `VaultScreen` deja de ser dummy
- crear/editar/borrar refleja cambios inmediatamente en lista
- items borrados no aparecen en vista por defecto
- la pantalla observa siempre estado local persistido

---

# 8) Añadir editores MVP para `PASSWORD` y `NOTE`

## Main Story (How, I Want, To)

Como usuario, quiero crear y editar passwords y notas seguras, para almacenar información sensible
dentro del vault sin depender de red.

## Context, Functional Description & Goal

Fase 4 necesita al menos un `itemType` usable extremo a extremo, pero al ser SafeCube un password
manager, `PASSWORD` debe estar en el MVP junto a `NOTE`.

## Steps/Scope

### In Scope

Añadir navegación y UI para:

- crear password entry
- editar password entry existente
- crear nota
- editar nota existente
- borrar nota
- borrar password entry

Campos MVP de `PASSWORD`:

- `displayHint` no sensible
- `username` cifrado
- `password` cifrada
- `websiteUrl` cifrada opcional
- `notes` cifradas opcionales

Campos MVP de `NOTE`:

- `displayHint` no sensible
- `noteBody` cifrado

También:

- ViewModels específicos
- mapeo de errores de dominio a UI
- rutas tipadas para abrir editor por `logicalItemId`

### Out of Scope (if applies)

- rich text
- adjuntos
- múltiples plantillas de item

### Acceptance Criteria (ACs)

- usuario puede crear una password entry cifrada
- usuario puede editarla y borrarla lógicamente
- usuario puede crear una nota cifrada
- usuario puede reabrirla, editarla y borrarla lógicamente

---

# 9) Añadir tests de storage + codec + vault item crypto + use cases

## Main Story (How, I Want, To)

Como developer, quiero tests del CRUD offline de vault, para que la fase 4 no introduzca deuda en
una de las zonas más sensibles de la app.

## Context, Functional Description & Goal

Esta fase toca persistencia, cifrado, session gating y UI orchestration. Sin tests, el riesgo de
regresión es alto.

## Steps/Scope

### In Scope

Cubrir como mínimo:

- serialización y deserialización de `PASSWORD` y `NOTE`
- parsing y construcción del payload envelope
- encrypt/decrypt de `PASSWORD` y `NOTE`
- fallo cuando el vault está locked
- `payloadVersion` en create/update
- DAO queries y soft delete
- casos de uso de create/update/delete/list
- ViewModels del flujo de listado/editor

Preferencias:

- tests unitarios para dominio/codec/crypto
- tests de DAO con Room in-memory

### Out of Scope (if applies)

- sync integration tests
- end-to-end multi-device

### Acceptance Criteria (ACs)

- el flujo offline principal queda cubierto por tests
- los casos de error más importantes tienen test
- la fase no reduce el nivel de rigor de `core:vault` / `core:storage`

---

## Resumen de salida esperada al cerrar Fase 4

La app debería poder:

1. autenticarse
2. crear o desbloquear vault
3. listar items locales reales
4. crear una password entry cifrada
5. leerla, editarla y borrarla lógicamente
6. crear una nota cifrada
7. leerla y editarla localmente
8. reiniciar la app y seguir viendo el vault local al volver a hacer unlock

Sin sync todavía.
