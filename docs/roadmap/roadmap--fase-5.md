# FASE 5 - Sync Multi-Device v2

## Estado de la fase

La implementación Android del protocolo v2 está completada y registrada en commits
`SCDK-M90`. La referencia normativa es
[Vault Sync Versioning v2](../architecture/vault-sync-versioning-v2.md).

El roadmap v1, basado en timestamps, `PENDING_*` dentro del oficial y el patrón summary + detail,
se conserva en [historical/roadmap--fase-5-v1.md](./historical/roadmap--fase-5-v1.md).

## Objetivo

Sincronizar `SecureItem` entre dispositivos manteniendo `Room` como source of truth visible para la
UI, preservando el modelo Zero-Knowledge y separando correctamente:

- `payloadVersion`: generación criptográfica controlada por el cliente.
- `itemRevision`: control CAS por ítem controlado por el backend.
- `changeSequence`: cursor por cuenta ordenado por commit.

## Decisiones vigentes

- `secure_items` representa únicamente el oficial remoto conocido.
- `secure_items_draft` representa propuestas locales no oficiales.
- `payloadVersion` se almacena en backend sin modificación y forma parte del AAD.
- `itemRevision` y `changeSequence` nunca forman parte del AAD.
- `UPDATE` y `DELETE` requieren `If-Match` y se validan mediante CAS atómico.
- `Idempotency-Key` se conserva entre reintentos de la misma mutación.
- Un reintento idéntico reproduce la respuesta original sin crear otra revisión.
- `changeSequence` se persiste por cuenta y no tiene que ser global ni contiguo.
- El pull usa `GET /vault/items/changes` con snapshots completos y tombstones.
- Los timestamps son informativos y no funcionan como cursor.
- Al cambiar de cuenta se limpian oficiales, drafts, checkpoints y material criptográfico local.

## Implementación completada

### Contrato y red

- OpenAPI versionado con `/vault/items/changes`, `ETag`, `If-Match`, `Idempotency-Key`,
  `payloadVersion`, `itemRevision` y `changeSequence`.
- Test de contrato en `core:network` sobre el JSON versionado.
- Mapeo explícito de `400`, `409`, `412` y `428`.
- Los errores definitivos de protocolo conservan el draft y detienen el push.

### Storage y dominio

- Modelo draft-first con drafts CREATE, UPDATE y DELETE.
- `mutationId` persistente en drafts.
- Checkpoint `lastAppliedChangeSequence` por cuenta.
- Aplicación de páginas remotas y checkpoint en una única transacción Room.
- Oficialización y eliminación del draft en una única transacción después de validar el payload.
- Rebase explícito de drafts mediante `itemRevision`.
- Publicación y descarte de conflictos.
- “Guardar como nuevo” para una actualización local contra un tombstone remoto.

### Sesión y aislamiento local

- Coordinador único para login, signup, refresh, logout y expiración de sesión.
- Limpieza transaccional de oficiales, drafts y checkpoints.
- Eliminación de tokens y claves incluso si falla Room durante el logout.
- Confirmación antes de descartar drafts activos en logout manual.

### Verificación

- Tests unitarios y de storage para conflictos, rebase, idempotencia local, cursores y
  oficialización.
- Tests de contrato OpenAPI.
- Cobertura global mínima de 90% de líneas y 85% de ramas.
- Última validación local: gate Kover de `ciVerify` correcto.

## Ciclo de sincronización vigente

1. Pull de todas las páginas posteriores a `lastAppliedChangeSequence`.
2. Aplicación transaccional de snapshots, tombstones y checkpoint.
3. Comparación de drafts con `remote.itemRevision`.
4. Push exclusivo de drafts `READY_TO_SYNC`.
5. Pull final para observar cambios concurrentes y confirmar el estado remoto.

## Política de conflictos

- UPDATE contra UPDATE: el remoto pasa a oficial y la propuesta local queda como `CONFLICT`.
- Publicar UPDATE: nuevo cifrado, nuevo `payloadVersion`, nueva base y nuevo `mutationId`.
- DELETE contra actualización remota: el delete local permanece como conflicto.
- Publicar DELETE: se rebasa sobre la revisión remota y se reintenta el borrado.
- UPDATE contra delete remoto: no se resucita el ítem; se ofrece “Guardar como nuevo”.
- DELETE contra delete remoto: éxito semántico local.
- Conflicto de idempotencia: error de integridad del cliente, no conflicto editable.

## Trabajo restante

- Ejecutar el escenario concurrente completo contra una instancia real del backend v2:
  dos clientes sobre la misma revisión, respuesta perdida, reintento idempotente y pull final.
- Repetir la validación después de cada cambio del contrato backend o del snapshot OpenAPI.
- Mantener sincronizados este roadmap, el ADR v2 y el test de contrato.

## Fuera de alcance

- Merge semántico de secretos.
- Restauración automática de ítems eliminados remotamente.
- Sincronización de otros dominios.
- Background sync periódico o `WorkManager`.
- Soporte local simultáneo para varias cuentas.
- Compatibilidad con clientes anteriores al protocolo v2.
