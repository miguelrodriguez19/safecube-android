# Vault Sync v1 (Client Strategy)

Estado: `historical`
Version: `v1`

> Superseded by [Vault Sync Versioning v2](../vault-sync-versioning-v2.md). This document records the
> previous timestamp-based protocol and is not the current implementation contract.

## 1. Objetivo

Definir la estrategia cliente de sync incremental para `SecureItem` en Fase 5 sin inventar
endpoints ni contratos no existentes.

Este documento fija:

- checkpoint local de pull
- ciclo recomendado de sync
- adaptacion del concepto `since` al OpenAPI actual
- reglas de push y pull
- politica MVP de conflictos
- reglas de aplicacion local sobre `Room`

Este documento complementa:

- [secure-item-payload-v1.md](../secure-item-payload-v1.md)
- [crypto-v1.md](../crypto-v1.md)
- [storage_decision.md](../storage_decision.md)

## 2. Contrato observable actual

En v1, el cliente solo puede apoyarse en estos endpoints de `/vault/items`:

- `GET /vault/items`
- `GET /vault/items/{itemId}`
- `POST /vault/items`
- `PUT /vault/items/{itemId}`
- `DELETE /vault/items/{itemId}`

Limitaciones relevantes del contrato actual:

- `GET /vault/items` devuelve `SecureItemSummaryResponse`, no `payload`.
- `GET /vault/items/{itemId}` devuelve `SecureItemResponse` con `payload`.
- el incremental remoto no usa `since`; usa `createdAfter` y `updatedAfter`.
- no existe cursor de paginacion ni token de delta.
- `PUT /vault/items/{itemId}` no exige `updatedAt` en request.
- `409` existe como respuesta HTTP observable y en v1 se trata como conflicto explicito.

Consecuencia:

- el pull activo es necesariamente de dos pasos:

  1. listar summaries
  2. hidratar detalle solo cuando haga falta payload

## 3. Lenguaje ubicuo

### 3.1 Identidades

- `logicalItemId`: identidad estable de cliente. Vive en `payload` envelope v1 y sigue siendo la PK
  local.
- `remoteItemId`: `itemId` remoto del backend. Solo existe cuando el item ya tiene representacion
  remota.

### 3.2 Checkpoint

- `lastPulledAt`: checkpoint local por cuenta para pull incremental.
- se persiste por `accountId`, no de forma global.

### 3.3 Sync metadata local

`syncState` es metadata local de sincronizacion. No forma parte del contrato backend ni del
protocolo crypto.

Estados minimos v1:

- `SYNCED`
- `PENDING_CREATE`
- `PENDING_UPDATE`
- `PENDING_DELETE`
- `CONFLICT`

Semantica:

- `SYNCED`: la fila local no tiene cambios pendientes respecto al ultimo estado remoto aplicado.
- `PENDING_CREATE`: item creado localmente y aun no subido. Requiere `remoteItemId == null`.
- `PENDING_UPDATE`: item remoto ya conocido, modificado localmente y pendiente de `PUT`.
- `PENDING_DELETE`: tombstone local pendiente de `DELETE` remoto.
- `CONFLICT`: el cliente detecto conflicto remoto y requiere resolucion explicita. No hay merge
  automatico.

## 4. Adaptacion de `since` al contrato real

El roadmap alto nivel habla de `since`, pero el OpenAPI actual expone:

- `createdAfter`
- `updatedAfter`

Decision canonica v1:

- el cliente persiste un unico checkpoint `lastPulledAt`
- el mismo valor se envia en ambos parametros:

```text
GET /vault/items?createdAfter=<lastPulledAt>&updatedAfter=<lastPulledAt>&includeDeleted=true
```

Razon:

- no existe un endpoint `since`
- no existe un cursor especifico de cambios
- el uso dual de `createdAfter` y `updatedAfter` permite approximar un delta incremental sin
  inventar contrato nuevo

Reglas:

- el cliente debe deduplicar summaries por `remoteItemId` / `itemId`, porque un mismo item puede
  entrar en ambas ventanas.
- `includeDeleted = true` es obligatorio en pull incremental v1.
- `type`, `labels` y filtros funcionales no forman parte del pull incremental base.

## 5. Checkpoint `lastPulledAt`

### 5.1 Definicion

`lastPulledAt` representa la watermark remota mas reciente que el cliente ya aplico de forma
consistente a `Room`.

En v1 no representa:

- hora local de inicio del pull
- hora local de fin del pull
- ultimo intento de sync

### 5.2 Regla de actualizacion

Tras un pull exitoso:

- `lastPulledAt` se actualiza al maximo `updatedAt` observado en los summaries procesados.
- si no llegaron summaries, `lastPulledAt` no cambia.
- si el pull no pudo completarse de forma consistente, `lastPulledAt` no cambia.

Motivo:

- evita depender del reloj local del dispositivo
- evita saltar cambios remotos ocurridos durante un pull incompleto
- se apoya solo en timestamps que ya entrega el backend

### 5.3 Consistencia requerida

Se considera que un pull fue consistente solo si:

- la lista de summaries se obtuvo correctamente
- cada detalle necesario se pudo obtener
- las escrituras locales necesarias se aplicaron correctamente

Si cualquiera de esos pasos falla:

- no se avanza `lastPulledAt`

## 6. Regla `list summaries -> get detail`

`GET /vault/items` no devuelve `payload`, asi que el cliente debe separar:

- descubrimiento del delta
- hidratacion del payload

Flujo canonico:

1. `GET /vault/items(createdAfter, updatedAfter, includeDeleted = true)`
2. deduplicar summaries por `itemId`
3. para cada summary:
   - si `deletedAt != null`, no pedir detalle
   - si `deletedAt == null`, pedir `GET /vault/items/{itemId}`

Regla explicita v1:

- el cliente no debe asumir que `GET /vault/items` contiene informacion suficiente para reconstruir
  una fila activa completa.

## 7. Ciclo recomendado de sync

El ciclo recomendado v1 es:

```text
push -> pull
```

Orden canonico:

1. subir cambios locales pendientes
2. traer delta remoto

Razon:

- minimiza la ventana en la que el cliente opera sobre estado remoto desactualizado
- reduce conflictos evitables cuando el mismo dispositivo tiene cambios pendientes
- mantiene la regla de que la UI sigue leyendo solo `Room`

El pull puede ejecutarse aunque:

- algun item haya quedado pendiente por red
- algun item haya quedado en `CONFLICT`

Pero en ese caso:

- el pull nunca pisa filas `dirty`
- el pull nunca pisa filas `CONFLICT`

## 8. Push local canonico

### 8.1 Casos de salida

Reglas canonicas:

- `PENDING_CREATE` + `remoteItemId == null` -> `POST /vault/items`
- `PENDING_UPDATE` + `remoteItemId != null` -> `PUT /vault/items/{itemId}`
- `PENDING_DELETE` + `remoteItemId != null` -> `DELETE /vault/items/{itemId}`
- `PENDING_DELETE` + `remoteItemId == null` -> resolver solo localmente; no hay llamada remota

### 8.2 Campos usados por push

`POST /vault/items` usa:

- `itemType`
- `schemaVersion`
- `displayHint`
- `payload`

`PUT /vault/items/{itemId}` usa:

- `itemType`
- `schemaVersion`
- `displayHint`
- `payload`

Notas:

- `logicalItemId` no viaja como campo propio.
- la identidad local viaja implicita dentro del `payload` envelope v1.
- `payloadVersion` remoto se acepta desde respuesta remota; no se inventa desde sync.

### 8.3 Resultado de push exitoso

Tras `POST` exitoso:

- se enlaza `remoteItemId = result.itemId`
- `syncState = SYNCED`
- el `logicalItemId` no cambia

Tras `PUT` exitoso:

- se actualizan `payloadVersion` y `updatedAt` con los valores remotos devueltos
- `syncState = SYNCED`

Tras `DELETE` exitoso:

- se mantiene la tombstone local
- se marca `syncState = SYNCED`
- `deletedAt` pasa a ser el valor remoto devuelto

### 8.4 Errores de push

Politica MVP:

- `409` en `PUT` o `DELETE` -> `CONFLICT`
- `404` en `PUT` -> `CONFLICT`
- `404` en `DELETE` -> se trata como remoto ya ausente y se resuelve localmente de forma
  consistente
- error de red / timeout -> mantener estado pendiente
- no hay merge automatico
- no hay retry avanzado dentro de este contrato

## 9. Pull incremental canonico

### 9.1 Resumen del flujo

Flujo recomendado:

1. leer `lastPulledAt` de la cuenta actual
2. llamar `GET /vault/items(createdAfter = lastPulledAt, updatedAfter = lastPulledAt, includeDeleted = true)`
3. deduplicar summaries por `itemId`
4. aplicar tombstones remotos sin pedir detalle
5. pedir detalle solo para summaries activos que realmente puedan aplicarse
6. escribir cambios en `Room`
7. actualizar `lastPulledAt` si el pull fue consistente

### 9.2 Regla de deduplicacion

La deduplicacion se hace por `itemId` remoto.

Si el mismo `itemId` aparece varias veces en el batch:

- se conserva una sola entrada para aplicacion local
- el cliente debe tratar el batch como set de cambios por identidad remota

### 9.3 Regla de tombstones

Si un summary remoto llega con `deletedAt != null`:

- no se llama `GET /vault/items/{itemId}`
- se intenta localizar fila local por `remoteItemId`
- si existe y la fila esta `SYNCED`, se aplica tombstone local
- si existe y la fila esta `PENDING_*` o `CONFLICT`, no se pisa silenciosamente
- si no existe fila local, en v1 se ignora ese tombstone remoto

Motivo:

- el summary remoto no trae `payload`
- sin `payload` no puede recuperarse `logicalItemId`
- el modelo local sigue usando `logicalItemId` como PK real

### 9.4 Regla de hidratacion de detalle

Si un summary remoto llega activo:

- el cliente pide `GET /vault/items/{itemId}`
- el `payload` recibido sigue tratandose como blob opaco
- el cliente puede parsear el envelope v1 para extraer `logicalItemId`
- el cliente no necesita descifrar el contenido para aplicar sync local

Regla importante:

- `logicalItemId` de un item remoto se recupera desde el `payload` envelope, no se genera uno
  nuevo de forma arbitraria

### 9.5 Regla de insercion/upsert local

Para un item remoto activo:

1. buscar fila local por `remoteItemId`
2. si no existe, parsear `logicalItemId` desde el envelope
3. intentar resolver fila local por `logicalItemId`

Aplicacion:

- si existe fila por `remoteItemId` y esta `SYNCED`, actualizarla
- si no existe fila por `remoteItemId` pero existe por `logicalItemId` y esta `SYNCED`, enlazar
  `remoteItemId` y actualizarla
- si no existe ninguna, insertar nueva fila local usando el `logicalItemId` recuperado del payload
- si `remoteItemId` y `logicalItemId` apuntan a filas distintas, no fusionar silenciosamente; tratar
  como inconsistencia y dejar fuera de aplicacion automatica

## 10. Regla de no pisar items dirty

En v1 se consideran `dirty`:

- `PENDING_CREATE`
- `PENDING_UPDATE`
- `PENDING_DELETE`
- `CONFLICT`

Regla canonica:

- el pull nunca sobreescribe una fila local `dirty`

Esto aplica a:

- `displayHint`
- `payload`
- `payloadVersion`
- `updatedAt`
- `deletedAt`
- `remoteItemId` cuando introducirlo alteraria una fila dirty incompatible

Consecuencia:

- una fila dirty puede convivir temporalmente con cambios remotos ya existentes
- la convergencia final depende de push exitoso o resolucion explicita de conflicto

## 11. Politica MVP de conflictos

La politica v1 es deliberadamente simple:

- `409` remoto en `PUT` o `DELETE` significa `CONFLICT`
- no se asume el mecanismo interno exacto del backend para ese `409`
- no hay merge de payloads
- no hay `last-writer-wins` silencioso
- no se reescribe el payload local con el remoto mientras el item este en `CONFLICT`

`CONFLICT` significa:

- el cliente detecto que no puede afirmar que su fila local represente el estado remoto vigente
- el item requiere accion posterior explicita

Fuera de alcance v1:

- UI de resolucion
- merge semantico de secretos
- dual-write local + remote shadow sofisticado

## 12. Source of truth local vs remoto

### 12.1 Regla principal

`Room` sigue siendo la unica verdad visible para UI.

La UI:

- no lee SQL remoto
- no consume responses HTTP directas
- no interpreta conflictos remotos fuera de la metadata local

### 12.2 Truth local

Son source of truth local en v1:

- `logicalItemId`
- `createdAt` local
- `syncState`
- cualquier metadata local de sync como `lastPulledAt`, `lastSyncedAt` o `lastSyncError`

Razon:

- esas piezas no tienen un equivalente completo y estable en el contrato remoto actual

Nota sobre `createdAt`:

- el backend devuelve `createdAt` en `POST`, pero `GET /vault/items` y `GET /vault/items/{itemId}`
  no lo exponen despues
- por tanto `createdAt` no puede tratarse como campo remoto reconciliable en v1

### 12.3 Truth remota

Para filas `SYNCED` con `remoteItemId` conocido, son source of truth remota:

- `remoteItemId`
- `itemType`
- `schemaVersion`
- `displayHint`
- `payload`
- `payloadVersion`
- `updatedAt`
- `deletedAt`

Siempre bajo esta condicion:

- solo despues de push exitoso o de pull aplicado sobre una fila no dirty

### 12.4 Truth local temporal

Para filas `PENDING_*` o `CONFLICT`, la fila local sigue siendo la verdad operativa del dispositivo
hasta nueva decision de sync.

## 13. Relacion entre `logicalItemId` y `remoteItemId`

Reglas canonicas:

- `logicalItemId` nunca se regenera por sync
- `remoteItemId` puede aparecer mas tarde
- la asignacion de `remoteItemId` no cambia AAD ni envelope
- el payload sigue ligado al `logicalItemId`

Escenarios:

- create offline:
  - `logicalItemId` existe desde el principio
  - `remoteItemId = null`
- push create success:
  - mismo `logicalItemId`
  - se asigna `remoteItemId`
- pull de item creado en otro dispositivo:
  - `remoteItemId` viene del summary/detalle remoto
  - `logicalItemId` se recupera del payload envelope

## 14. Supuestos y limites explicitos de v1

Para no inventar contrato:

- no hay cursor remoto
- no hay endpoint bulk detail
- no hay merge remoto/local
- no hay restore de conflictos
- no hay background sync

Limitacion explicita del contrato actual:

- la estrategia incremental v1 asume que el delta relevante puede procesarse de forma consistente
  con una sola lista de summaries mas N detalles

Consecuencia:

- si en el futuro el backend necesita paginacion fuerte para deltas, v1 debera evolucionar con un
  contrato remoto especifico de cursor o watermark estable

## 15. Resumen operativo

Reglas finales v1:

- usar `lastPulledAt` por cuenta
- mapear `since` a `createdAfter + updatedAfter`
- ejecutar sync como `push -> pull`
- tratar `GET /vault/items` como lista de summaries, nunca como fuente de payload
- hidratar activos con `GET /vault/items/{itemId}`
- no pisar filas `dirty`
- traducir `409` a `CONFLICT`
- mantener `logicalItemId` como identidad local estable
- tratar `Room` como unica verdad visible para UI
