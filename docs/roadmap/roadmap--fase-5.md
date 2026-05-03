# FASE 5 - Sync Incremental Multi-Device

## Objetivo de la fase

Implementar la sincronización incremental real de `SecureItem` entre el almacenamiento local (
`Room`) y el backend, manteniendo `Room` como source of truth visible para la UI y preservando el
modelo Zero-Knowledge.

Incluye:

- pull incremental remoto
- push de cambios locales pendientes
- propagación de soft delete
- manejo simple y explícito de conflictos
- checkpoint de sync por cuenta
- estado mínimo de sync visible en UI

Resultado esperado:

> Lo creado, editado o borrado en un dispositivo termina reflejándose en otro sin romper el modelo
> offline-first.

---

## Estado actual

Según el estado actual del proyecto:

- `core:network` ya tiene stack propio de red, `AuthInterceptor`, `TokenRefreshAuthenticator`,
  `NetworkClientFactory` y OpenAPI generado con `VaultControllerApi`.
- `core:storage` ya tiene `AppDatabase`, `SecureItemEntity`, `SecureItemDao`, migraciones y
  `SecureItemLocalStorage`.
- `core:vault` ya tiene crypto de items, repositorios, cache de vault key material, unlock y casos
  de uso de CRUD local.
- `feature:vault` ya tiene `VaultScreen`, `NoteEditorScreen`, `PasswordEditorScreen` y sus
  `ViewModel`, pero sin capa de sync.
- El proyecto ya mantiene testing docs y el comando de verificación de cobertura.

Esto significa que Fase 5 no necesita rediseñar crypto ni rehacer el CRUD local: necesita añadir la
capa de sincronización sobre una base ya estable.

---

## Contrato backend relevante para esta fase

El contrato efectivo actual de `vault/items` es:

- `GET /vault/items`
- `GET /vault/items/{itemId}`
- `POST /vault/items`
- `PUT /vault/items/{itemId}`
- `DELETE /vault/items/{itemId}`

Detalles observables del OpenAPI:

### `GET /vault/items`

Soporta:

- `createdAfter`
- `updatedAfter`
- `type`
- `labels`
- `includeDeleted`
- `limit`
- `order`

Devuelve `ListSecureItemsResponse`, es decir, **summaries** sin payload.

### `GET /vault/items/{itemId}`

Devuelve `SecureItemResponse` con:

- `itemId`
- `itemType`
- `schemaVersion`
- `displayHint`
- `payload`
- `payloadVersion`
- `updatedAt`
- `deletedAt`

### `POST /vault/items`

Recibe:

- `itemType`
- `schemaVersion`
- `displayHint`
- `payload`

Devuelve:

- `itemId`
- `createdAt`

### `PUT /vault/items/{itemId}`

Recibe:

- `itemType`
- `schemaVersion`
- `displayHint`
- `payload`

Devuelve:

- `itemId`
- `payloadVersion`
- `updatedAt`

Puede devolver `409 Conflict`.

Nota de alineación contractual:

- `409` se trata como comportamiento observable del backend.
- este roadmap no asume el mecanismo interno exacto que dispara el conflicto.

### `DELETE /vault/items/{itemId}`

Devuelve:

- `itemId`
- `deletedAt`

Puede devolver `409 Conflict`.

---

## Decisiones MVP (cerradas para esta fase)

### 1) `Room` sigue siendo la única verdad visible para UI

Se mantiene la decisión cerrada en Fase 4:

- la UI observa siempre estado persistido local
- no existe `remote-first`
- la red solo alimenta y reconcilia el estado local

### 2) Sync en Fase 5 afecta solo a `SecureItem`

Fuera de esta fase:

- `user/profile`
- carpetas reales
- settings complejos
- rotación de KEK
- recovery UX avanzada

### 3) No habrá background sync ni `WorkManager` en esta fase

Para mantener el MVP claro y testeable:

- el sync será foreground/app-driven
- habrá sync manual y hooks simples desde la app cuando tenga sentido
- no se implementa periodic sync
- no se implementan jobs en segundo plano

### 4) El “since” del roadmap alto nivel se adapta al contrato real actual

El roadmap alto nivel habla de `since`, pero el OpenAPI actual expone:

- `createdAfter`
- `updatedAfter`

Decisión MVP:

- persistir un único checkpoint local `lastPulledAt` por cuenta
- usar ese mismo instante para `createdAfter` y `updatedAfter`
- deduplicar por `remoteItemId` / `itemId` al aplicar el delta

Esto permite implementar sync incremental sin inventar un endpoint nuevo.

### 5) Pull remoto de items activos será de dos pasos

Como `GET /vault/items` devuelve summaries sin payload:

1. listar cambios con `GET /vault/items`
2. para cada item no borrado, pedir detalle con `GET /vault/items/{itemId}`

Esto introduce N+1 requests, pero es aceptable para MVP y respeta el contrato real.

### 6) Se añade metadata explícita de sync al storage local

El modelo local necesita distinguir estado funcional del item y estado de sincronización.

Se añade al modelo local, como mínimo:

- `syncState`
- `lastSyncedAt`
- `lastSyncError` opcional si ayuda a UX mínima

Estados mínimos recomendados:

- `SYNCED`
- `PENDING_CREATE`
- `PENDING_UPDATE`
- `PENDING_DELETE`
- `CONFLICT`

### 7) `logicalItemId` sigue siendo la identidad local estable

Se mantiene la decisión de Fase 4:

- `logicalItemId` = identidad cliente / payload / AAD
- `remoteItemId` = identidad backend

El sync solo enlaza ambos mundos; no cambia el protocolo crypto local.

### 8) No hay merge automático de payloads

El manejo de conflictos en esta fase será deliberadamente simple:

- `409` remoto se traduce a conflicto explícito
- no hay merge de secretos
- no hay last-writer-wins silencioso
- no se sobreescribe un item local dirty con datos remotos sin decisión explícita

### 9) Pull remoto nunca pisa cambios locales pendientes

Si existe un item local en:

- `PENDING_CREATE`
- `PENDING_UPDATE`
- `PENDING_DELETE`
- `CONFLICT`

el pull no debe sobreescribirlo silenciosamente.

### 10) Delete local de item nunca sincronizado no llama al backend

Caso MVP importante:

- item creado offline (`remoteItemId == null`)
- luego borrado antes de sincronizar

Resultado:

- se elimina localmente
- no se hace `POST`
- no se hace `DELETE`

### 11) El sync sigue siendo opaque-payload

El backend sigue recibiendo y devolviendo:

- `displayHint`
- metadata mínima
- `payload` opaco

Nunca se envía contenido en claro ni material criptográfico sensible.

### 12) UI/UX enriquecida de verdad sigue perteneciendo a Fase 6

En esta fase solo entra la UX mínima necesaria para que sync sea usable:

- estado syncing
- error/retry mínimo
- badge o marcador de conflicto / pendiente
- feedback básico de último sync

Lo visualmente más rico, polished y consistente debe mantenerse en Fase 6.

---

## Fuera de alcance

- rediseño visual grande
- design system completo
- background sync periódico
- `WorkManager`
- sync de `UserProfile`
- conflict resolution UI avanzada
- merge semántico de payloads
- search
- folders reales
- adjuntos
- hard delete
- telemetry/analytics avanzada de sync

---

# Tasks - Fase 5

# 0) Documentar la estrategia cliente de sync incremental v1

## Main Story (How, I Want, To)

Como developer, quiero un documento canónico de sync cliente, para implementar Fase 5 sin
ambigüedades y sin inventar contratos no existentes.

## Context, Functional Description & Goal

El roadmap alto nivel habla de `since`, pero el OpenAPI actual expone `createdAfter` y
`updatedAfter`. Además, `GET /vault/items` devuelve summaries y no payload completo.

Antes de tocar storage y red hay que cerrar la estrategia exacta de:

- checkpoint
- pull
- push
- conflictos
- reglas de aplicación local

## Steps/Scope

### In Scope

Crear un documento nuevo, por ejemplo:

```text
docs/architecture/vault-sync-v1.md
```

Definir explícitamente:

- adaptación de `since` -> `createdAfter` + `updatedAfter`
- uso de `lastPulledAt`
- regla `list summaries -> get detail`
- ciclo recomendado `push -> pull`
- política de conflicto MVP (`CONFLICT`, sin merge automático)
- tratamiento de `409` como contrato observable (sin asumir `request.updatedAt`)
- regla de no pisar items dirty
- relación entre `logicalItemId` y `remoteItemId`
- qué significa `syncState`
- qué datos son source of truth local vs remoto

### Out of Scope (if applies)

- implementación de código
- UI de resolución de conflictos

## Additional Information and Configuration

### API Contract and Expected Behavior (if applies)

Basado en el OpenAPI actual de `/vault/items` y en las decisiones ya cerradas de Fase 4.

### Acceptance Criteria (ACs)

- existe un documento canónico de sync cliente
- la estrategia de Fase 5 queda cerrada sin inventar endpoints
- queda documentado que `GET /vault/items` no devuelve payload y requiere
  `GET /vault/items/{itemId}` para hidratar cambios activos
- Pasar `./gradlew clean verifyCoverage`.
- Los tests están basados en `@TESTING_STANDARD.md`.
- Se hace uso de la clean architecture, principios SOLID y clean code.

---

# 1) Permitir el uso del contrato OpenAPI de `VaultControllerApi` en Fase 5

## Main Story (How, I Want, To)

Como developer, quiero habilitar explícitamente el uso del contrato OpenAPI de `vault/items`, para
consumir el backend real sin romper la arquitectura actual.

## Context, Functional Description & Goal

Las fases previas ya cerraron que OpenAPI Generator se usa como contrato y no como stack HTTP
principal. Fase 5 necesita aplicar la misma regla a `VaultControllerApi`.

## Steps/Scope

### In Scope

Crear documento nuevo, por ejemplo:

```text
docs/architecture/openapi-vault-items-contract-integration.md
```

Permitir uso de:

```text
generated.api.VaultControllerApi
generated.model.* relacionados con vault items
```

Mantener prohibido:

```text
generated.infrastructure.*
generated.auth.*
```

Definir además:

- `feature/*` no importa `generated.*` directamente
- el consumo queda encapsulado en `core:vault` y/o `core:network`

### Out of Scope (if applies)

- implementación del data source
- refactor de módulos grande

## Additional Information and Configuration

### API Contract and Expected Behavior (if applies)

Se usarán los endpoints reales de `/vault/items` definidos en OpenAPI.

### Acceptance Criteria (ACs)

- existe una regla explícita de integración para `VaultControllerApi`
- queda cerrado que OpenAPI se usa como contrato, no como cliente HTTP principal
- `feature/*` no depende directamente de modelos/clases generadas
- Pasar `./gradlew clean verifyCoverage`.
- Los tests están basados en `@TESTING_STANDARD.md`.
- Se hace uso de la clean architecture, principios SOLID y clean code.

---

# 2) Expandir el storage local con metadata de sync y checkpoint por cuenta

## Main Story (How, I Want, To)

Como developer, quiero ampliar el modelo local para representar estado de sincronización y
checkpoint incremental, para soportar sync real sin sacrificar offline-first.

## Context, Functional Description & Goal

El CRUD local ya existe, pero el storage actual todavía no distingue entre:

- item limpio
- item con cambios pendientes
- item en conflicto
- último pull remoto aplicado

Sin esa capa, Fase 5 no puede implementar push/pull fiable.

## Steps/Scope

### In Scope

Ampliar el schema local con, como mínimo:

- `syncState`
- `lastSyncedAt: Instant?`
- `lastSyncError: String?` si aporta valor mínimo a la UX de sync

Añadir almacenamiento de checkpoint incremental por cuenta, por ejemplo con:

- tabla o entidad específica de sync checkpoint
- `accountId`
- `lastPulledAt`

Actualizar:

- `AppDatabase`
- migraciones
- `SecureItemEntity`
- `SecureItemDao`
- `SecureItemLocalStorage`

### Out of Scope (if applies)

- implementación remota
- política de conflicto completa

## Additional Information and Configuration

### API Contract and Expected Behavior (if applies)

El checkpoint se adaptará al contrato actual `createdAfter` + `updatedAfter`.

### Acceptance Criteria (ACs)

- el schema local representa estado de sync explícito
- existe almacenamiento persistente del checkpoint incremental por cuenta
- las migraciones son coherentes y testeadas
- no se rompe el CRUD offline existente
- Pasar `./gradlew clean verifyCoverage`.
- Los tests están basados en `@TESTING_STANDARD.md`.
- Se hace uso de la clean architecture, principios SOLID y clean code.

---

# 3) Implementar `RemoteSecureItemDataSource`

## Main Story (How, I Want, To)

Como developer, quiero una capa remota aislada para `vault/items`, para desacoplar el repositorio de
dominio del detalle HTTP/OpenAPI.

## Context, Functional Description & Goal

Fase 4 dejó el modelo local alineado con el contrato futuro. Ahora toca encapsular el acceso remoto
real usando `VaultControllerApi`.

## Steps/Scope

### In Scope

Crear:

```text
RemoteSecureItemDataSource
```

Métodos mínimos:

```text
listVaultItems(createdAfter, updatedAfter, includeDeleted, limit)
getVaultItem(remoteItemId)
createVaultItem(request)
updateVaultItem(remoteItemId, request)
deleteVaultItem(remoteItemId)
```

Devolver resultados tipados preservando:

- `httpCode`
- `body`
- `errorBody`
- fallos de transporte

Mapeo mínimo de errores:

- `401/403` -> auth/session issue
- `404` -> missing remote item
- `409` -> conflicto remoto explícito (sin asumir causa interna)
- fallback -> unknown remote failure

### Out of Scope (if applies)

- actualización del storage local
- lógica de checkpoint
- orquestación de sync

## Additional Information and Configuration

### API Contract and Expected Behavior (if applies)

Debe usar el contrato OpenAPI real de `/vault/items` sin exponer `generated.*` fuera de la capa
remota.

### Acceptance Criteria (ACs)

- existe un wrapper remoto mínimo y testeable para `vault/items`
- el data source soporta list/get/create/update/delete
- los errores HTTP se exponen sin perder información útil para la capa de sync
- Pasar `./gradlew clean verifyCoverage`.
- Los tests están basados en `@TESTING_STANDARD.md`.
- Se hace uso de la clean architecture, principios SOLID y clean code.

---

# 4) Añadir primitivas locales de sync en `SecureItemDao` / `SecureItemLocalStorage`

## Main Story (How, I Want, To)

Como developer, quiero operaciones locales específicas de sync, para que la orquestación no tenga
que hablar SQL ni conocer Room.

## Context, Functional Description & Goal

El CRUD local actual está orientado a UI y dominio funcional. Fase 5 necesita operaciones
adicionales para:

- sacar pendientes de push
- buscar por `remoteItemId`
- aplicar tombstones remotos
- marcar sync success o conflict

## Steps/Scope

### In Scope

Añadir primitivas mínimas, por ejemplo:

- `getPendingSyncItemsOrdered()`
- `findByRemoteItemId(remoteItemId)`
- `markPendingCreate(...)`
- `markPendingUpdate(...)`
- `markPendingDelete(...)`
- `markSynced(...)`
- `markConflict(...)`
- `applyRemoteUpsert(...)`
- `applyRemoteDelete(...)`
- `getSyncCheckpoint(accountId)`
- `updateSyncCheckpoint(accountId, lastPulledAt)`

Mantener encapsulación:

- SQL/Room en `core:storage`
- semántica de sync en `core:vault`

### Out of Scope (if applies)

- llamadas de red
- UI

## Additional Information and Configuration

### API Contract and Expected Behavior (if applies)

Las primitivas locales deben soportar tanto push como pull incremental y tombstones.

### Acceptance Criteria (ACs)

- la capa de sync puede operar sin SQL directo
- existe lookup por `remoteItemId`
- se puede persistir `CONFLICT` y otros estados de sync
- el checkpoint incremental puede leerse y actualizarse desde una API local clara
- Pasar `./gradlew clean verifyCoverage`.
- Los tests están basados en `@TESTING_STANDARD.md`.
- Se hace uso de la clean architecture, principios SOLID y clean code.

---

# 5) Implementar `PullVaultDeltaUseCase`

## Main Story (How, I Want, To)

Como developer, quiero descargar y aplicar el delta remoto de forma incremental, para reflejar en
local los cambios hechos en otros dispositivos.

## Context, Functional Description & Goal

El contrato actual obliga a separar listado de cambios y recuperación de payload. Esta tarea
construye el pull incremental real respetando el storage local como source of truth visible.

## Steps/Scope

### In Scope

Implementar un caso de uso, por ejemplo:

```text
PullVaultDeltaUseCase
```

Flujo mínimo:

1. leer `lastPulledAt`
2. llamar
   `listVaultItems(createdAfter = lastPulledAt, updatedAfter = lastPulledAt, includeDeleted = true)`
3. deduplicar summaries por `itemId`
4. para cada item no borrado, pedir detalle con `getVaultItem(itemId)`
5. aplicar el delta en local
6. actualizar checkpoint si el pull fue consistente

Reglas:

- si el item local está dirty o en conflicto, no pisarlo silenciosamente
- si llega un tombstone remoto, aplicarlo localmente
- el pull no descifra payloads; solo sincroniza blobs opacos + metadata

### Out of Scope (if applies)

- push local
- merge semántico
- background sync

## Additional Information and Configuration

### API Contract and Expected Behavior (if applies)

`GET /vault/items` devuelve summaries; `GET /vault/items/{itemId}` devuelve payload completo.

### Acceptance Criteria (ACs)

- el pull incremental usa checkpoint persistido
- el caso de uso hidrata payload remoto solo cuando el item no está borrado
- el pull propaga soft delete remoto correctamente
- no se sobreescriben cambios locales pendientes de forma silenciosa
- Pasar `./gradlew clean verifyCoverage`.
- Los tests están basados en `@TESTING_STANDARD.md`.
- Se hace uso de la clean architecture, principios SOLID y clean code.

---

# 6) Implementar `PushLocalVaultChangesUseCase`

## Main Story (How, I Want, To)

Como developer, quiero subir cambios locales pendientes al backend, para que el resto de
dispositivos puedan verlos.

## Context, Functional Description & Goal

Tras Fase 4, el usuario ya puede crear/editar/borrar offline. Fase 5 necesita convertir esos cambios
en operaciones remotas reales.

## Steps/Scope

### In Scope

Implementar un caso de uso, por ejemplo:

```text
PushLocalVaultChangesUseCase
```

Flujo mínimo:

- `PENDING_CREATE` + `remoteItemId == null` -> `POST /vault/items`
- `PENDING_UPDATE` + `remoteItemId != null` -> `PUT /vault/items/{itemId}`
- `PENDING_DELETE` + `remoteItemId != null` -> `DELETE /vault/items/{itemId}`
- `PENDING_DELETE` + `remoteItemId == null` -> resolver solo localmente

Actualizar estado local en success:

- `remoteItemId`
- `payloadVersion` si aplica
- `updatedAt` / `deletedAt`
- `syncState = SYNCED`
- `lastSyncedAt`

Reglas mínimas de error:

- `409` en update/delete -> `CONFLICT`
- `404` en delete -> tratar como delete remoto ya aplicado o resolver de forma consistente
- `404` en update -> conflicto explícito
- fallos de red -> mantener pendiente

### Out of Scope (if applies)

- reintentos avanzados
- batching concurrente complejo
- merge de conflictos

## Additional Information and Configuration

### API Contract and Expected Behavior (if applies)

Debe usar únicamente el contrato observable actual y tratar `409` como conflicto explícito.

### Acceptance Criteria (ACs)

- el push soporta create/update/delete reales
- un item local nunca sincronizado y luego borrado no llama al backend
- el estado local queda consistente tras éxito o fallo remoto
- `409` no causa sobrescritura silenciosa de datos locales
- Pasar `./gradlew clean verifyCoverage`.
- Los tests están basados en `@TESTING_STANDARD.md`.
- Se hace uso de la clean architecture, principios SOLID y clean code.

---

# 7) Implementar `VaultSyncUseCase` / `VaultSyncCoordinator`

## Main Story (How, I Want, To)

Como developer, quiero una orquestación única de sync, para ejecutar la sincronización completa con
una política determinista y reusable desde UI/app.

## Context, Functional Description & Goal

Push y pull por separado no bastan. La app necesita una entrada única y serializada para sincronizar
el vault sin carreras.

## Steps/Scope

### In Scope

Crear un coordinador o caso de uso, por ejemplo:

```text
VaultSyncUseCase
```

Responsabilidades:

- ejecutar el ciclo recomendado `push -> pull`
- evitar ejecuciones concurrentes (`Mutex` / lock)
- devolver resultado resumido:
    - items subidos
    - items descargados
    - conflictos
    - fallo global si aplica
- fallar en cerrado si no hay sesión usable o vault en contexto no válido para la operación

### Out of Scope (if applies)

- scheduling en background
- sync automático complejo por conectividad

## Additional Information and Configuration

### API Contract and Expected Behavior (if applies)

Orquesta los casos de uso ya adaptados al contrato real de `/vault/items`.

### Acceptance Criteria (ACs)

- existe un único punto de entrada para sync completo
- no se producen sync concurrentes solapados
- el ciclo completo puede ejecutarse desde UI sin conocimiento de detalle HTTP/storage
- el resultado del sync es observable y estable para la capa de presentación
- Pasar `./gradlew clean verifyCoverage`.
- Los tests están basados en `@TESTING_STANDARD.md`.
- Se hace uso de la clean architecture, principios SOLID y clean code.

---

# 8) Implementar `VaultSyncTrigger` para push oportunista

## Main Story (How, I Want, To)

Como developer, quiero disparar un intento de subida tras cada mutación local, para que los cambios
lleguen al backend cuanto antes sin romper offline-first.

## Context, Functional Description & Goal

Fase 5 mantiene `Room` como source of truth visible para UI. Tras crear, editar o borrar un item, el
cambio debe quedar persistido localmente y marcado como pendiente. Si hay sesión y red, la app puede
intentar subirlo de forma best-effort.

Esta tarea no sustituye al sync completo `push -> pull`. Añade un modo oportunista orientado a
mutaciones locales, mientras `VaultSyncUseCase` sigue siendo la entrada de sync completo para
desbloqueo, entrada al vault, refresh manual o vuelta a foreground.

## Steps/Scope

### In Scope

Crear un trigger/orquestador simple, por ejemplo:

```text
VaultSyncTrigger
```

Integrarlo tras mutaciones locales de vault:

- `CreateSecurePasswordUseCase`
- `UpdateSecurePasswordUseCase`
- `CreateSecureNoteUseCase`
- `UpdateSecureNoteUseCase`
- `SoftDeleteSecureItemUseCase`

Reglas:

- la operación funcional siempre escribe primero en `Room`
- la UI no espera a la red para considerar guardado el cambio local
- si hay sesión y red, se intenta subir el cambio pendiente de forma best-effort
- si el push falla por red, transporte o `5xx`, el item permanece pendiente
- si el backend devuelve `409`, el item pasa a `CONFLICT`
- si ya hay sync en curso, la petición se coalesce o se delega al coordinador existente
- no se ejecuta pull completo obligatorio tras cada mutación

### Out of Scope (if applies)

- `WorkManager`
- background sync periódico
- retry exponencial avanzado
- notificaciones del sistema
- pull remoto tras cada mutación local
- resolución visual o semántica avanzada de conflictos

## Additional Information and Configuration

### API Contract and Expected Behavior (if applies)

Usa `POST`, `PUT` y `DELETE` de `/vault/items` a través de la capa remota ya encapsulada. `409` se
trata como conflicto observable.

El trigger puede usar `PushLocalVaultChangesUseCase` completo para MVP. Si aparece coste o
contención innecesaria, se puede introducir después una variante targeted por item sin cambiar el
contrato de UI.

### Acceptance Criteria (ACs)

- crear un item intenta subirlo tras guardarlo localmente
- editar un item intenta subirlo tras guardarlo localmente
- borrar un item intenta sincronizar el tombstone si tiene `remoteItemId`
- si no hay red o sesión usable, el item queda pendiente sin romper el éxito local
- si hay sync concurrente, no se lanza una segunda ejecución solapada
- no se dispara pull completo obligatorio después de cada mutación
- Pasar `./gradlew clean verifyCoverage`.
- Los tests están basados en `@TESTING_STANDARD.md`.
- Se hace uso de la clean architecture, principios SOLID y clean code.

---

# 9) Exponer estado mínimo de sync en `feature:vault`

## Main Story (How, I Want, To)

Como usuario, quiero saber si mis cambios están pendientes, sincronizando o en conflicto, para
confiar en el comportamiento multi-dispositivo sin necesidad de una UI avanzada todavía.

## Context, Functional Description & Goal

El roadmap alto nivel reserva la UI/UX rica para Fase 6, pero Fase 5 necesita feedback mínimo para
que sync sea realmente usable.

## Steps/Scope

### In Scope

Actualizar, como mínimo:

- `VaultHomeViewModel`
- `NoteEditorViewModel`
- `PasswordEditorViewModel`

Añadir estado mínimo de presentación:

- `isSyncing`
- `lastSyncResult` / `lastSyncError`
- marcador de item pendiente o en conflicto
- acción manual `syncNow()`

UX mínima aceptada:

- feedback de sync en home
- retry manual
- conflictos visibles de forma simple
- sin rediseño visual grande

### Out of Scope (if applies)

- redesign completo de `VaultScreen`
- resolución visual avanzada de conflictos
- microinteracciones / motion elaboradas

## Additional Information and Configuration

### API Contract and Expected Behavior (if applies)

La presentación consume el resultado de `VaultSyncUseCase` y el estado persistido local.

### Acceptance Criteria (ACs)

- desde la home del vault se puede lanzar sync manual
- el usuario ve al menos estado pendiente / syncing / conflict
- la UI sigue observando estado local persistido, no respuestas remotas directas
- no se introduce una segunda fuente de verdad en presentación
- Pasar `./gradlew clean verifyCoverage`.
- Los tests están basados en `@TESTING_STANDARD.md`.
- Se hace uso de la clean architecture, principios SOLID y clean code.

---

# 10) Añadir tests de sync end-to-end a nivel de módulos

## Main Story (How, I Want, To)

Como developer, quiero tests deterministas de sync incremental, para introducir multi-device real
sin degradar la fiabilidad del vault.

## Context, Functional Description & Goal

Fase 5 toca una de las zonas con más riesgo de regresión: storage + red + conflictos + estados
intermedios.

## Steps/Scope

### In Scope

Cubrir como mínimo:

- migración del schema con metadata de sync
- checkpoint store
- `RemoteSecureItemDataSource` con `MockWebServer`
- `PullVaultDeltaUseCase`
- `PushLocalVaultChangesUseCase`
- `VaultSyncUseCase`
- `VaultSyncTrigger`
- conflicto `409`
- propagación de tombstones
- caso “create offline -> delete offline antes de sync”
- caso `list summaries -> get detail`
- estado de `VaultHomeViewModel` durante sync

Preferencias:

- tests unitarios para orquestación y mapping
- tests de storage con Room in-memory
- tests de red con `MockWebServer`

### Out of Scope (if applies)

- tests E2E multi-dispositivo reales
- tests de background sync
- tests de UX avanzada

## Additional Information and Configuration

### API Contract and Expected Behavior (if applies)

Basado en el OpenAPI actual de `vault/items` y en la estrategia documentada en `vault-sync-v1.md`.

### Acceptance Criteria (ACs)

- el flujo crítico `push -> pull` queda cubierto por tests
- tombstones y conflictos tienen tests explícitos
- la fase no reduce el rigor actual de `core:vault`, `core:storage` y `feature:vault`
- Pasar `./gradlew clean verifyCoverage`.
- Los tests están basados en `@TESTING_STANDARD.md`.
- Se hace uso de la clean architecture, principios SOLID y clean code.

---

## Resumen de salida esperada al cerrar Fase 5

La app debería poder:

1. crear, editar y borrar items offline como ya hace Fase 4
2. marcar cambios locales como pendientes de sync
3. subir esos cambios al backend cuando haya sesión y red
4. descargar cambios hechos desde otros dispositivos
5. propagar borrados lógicos entre dispositivos
6. mantener `Room` como verdad local visible para la UI
7. detectar conflictos remotos y marcarlos de forma explícita
8. seguir siendo usable aunque el sync falle temporalmente

Resultado:

> SafeCube pasa de “vault local offline-first” a “vault offline-first con sincronización
> multi-device real”.
