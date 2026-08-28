# Agent Report — SCDK-M132

## Status

`DONE`

## Context

| Campo | Valor |
| --- | --- |
| Task ID | `SCDK-M132` |
| Backend | `SCDK-B55` |
| Specs | `SPEC-HARDENING-V1` / `SEC-CRYPTO-002`, `SPEC-OPENAPI-VAULT-KEY-MATERIAL` |
| ADRs | `ADR-0002-PASSPHRASE-REWRAP`; relación lifecycle con `ADR-0001-VAULT-AUTO-LOCK` |
| Agente | Codex, orquestador y validador de una flota de cuatro agentes |
| Fecha | `2026-08-27` |

## Summary

Se incorporó el contrato backend CAS con ETag para `PUT /vault/keys/master`. Cada cambio de
passphrase obtiene primero un snapshot remoto fresco con ETag fuerte y entre comillas, valida la
credencial contra ese wrapper y envía el valor literal como `If-Match`. El resultado `412` es un
conflicto tipado y ninguna respuesta incierta provoca un segundo PUT automático.

La reconciliación distingue wrapper candidato, base y tercero. El candidato confirma éxito; la
base informa que no se aplicó; un tercero sincroniza únicamente `kekEncMaster`, invalida la KEK en
memoria y bloquea en `ManualOnly`. Si la reconciliación falla o es incompatible, se elimina solo la
autoridad local del wrapper maestro y se bloquea. La sesión autenticada y los datos cifrados se
conservan.

## Changed files and behavior

- `core/network/openapi/OpenAPI.json` — copia canónica de `/safecube/v3/api-docs`, con ETag en GET
  y PUT, `If-Match` requerido y respuestas `412`/`428`.
- `core/network/src/test/.../VaultKeyMaterialOpenApiContractTest.kt` — test estructural del contrato
  CAS, headers obligatorios, estados y body mínimo.
- `core/vault/.../RemoteVaultKeyMaterialDataSource.kt` y modelos/repositorio remotos — snapshot
  versionado, confirmación con ETag, validación de ETag fuerte y errores tipados de conflicto,
  precondición ausente y violación contractual.
- `core/vault/.../ChangeVaultPassphraseUseCase.kt` — GET fresco, validación contra autoridad remota,
  CAS, reconciliación sin PUT ciego, persistencia exclusiva del wrapper maestro, zeroización y
  fail-closed.
- `feature/vault/.../ChangePassphraseViewModel.kt` y recursos EN/ES — el perdedor no muestra éxito,
  limpia campos y navega a Unlock con mensaje específico.
- Tests de data source, MockWebServer, dominio y ViewModel — incluyen revisión obsoleta, respuestas
  perdidas, errores terminales, contrato inválido, fallos de persistencia/reconciliación y carrera
  concurrente determinista de dos clientes con exactamente un ganador.
- `ADR-0002`, `SPEC-HARDENING-V1`, contrato de integración, registro de specs y matriz de
  trazabilidad — decisión B55 aceptada y evidencia M132.

No se modifican schemas de storage, KEK/recovery key, SecureItems, DEKs, payloads, drafts ni
checkpoints. La reconciliación local usa `updateMasterWrappedKek`; no reescribe `kekEncRecovery` ni
los parámetros KDF.

## Tests and quality gates

| Comando | Resultado | Evidencia |
| --- | --- | --- |
| Comparación `jq -S` OpenAPI local frente al descargado | `PASS` | Diferencia semántica vacía. |
| Tests focalizados de data source y caso de uso | `PASS` | Dominio, Retrofit y MockWebServer sin fallos. |
| `:core:network:testDebugUnitTest --tests ...VaultKeyMaterialOpenApiContractTest` | `PASS` | 3 casos contractuales. |
| `./gradlew verifyKover` | `PASS` | 357 tareas; ramas: 1650/1920 (85,94 %, mínimo 85 %). |
| `git diff --check` | `PASS` | Sin errores de whitespace. |
| `./gradlew ciVerify` | `PASS` | Ejecución única final: 768 tareas; tests, lint debug, Kover y `assembleRelease`. |

## Acceptance Criteria

- [x] Contrato backend/OpenAPI acordado mediante `SCDK-B55`, importado y documentado.
- [x] Dos cambios con el mismo ETag producen exactamente un ganador remoto en el test concurrente.
- [x] El perdedor recibe conflicto tipado, no muestra éxito y no sobrescribe al ganador.
- [x] Una credencial comprobada solo contra caché obsoleta nunca alcanza un PUT aceptado.
- [x] Conflicto o tercer wrapper sincroniza solo el wrapper remoto, zeroiza y bloquea.
- [x] El ganador conserva el vault desbloqueado y la misma KEK efectiva.
- [x] Respuesta perdida distingue candidato, base y tercer wrapper.
- [x] `kekEncRecovery`, SecureItems, drafts y checkpoints no se mutan.
- [x] Hay tests deterministas de dos clientes, carrera, revisión obsoleta, conflicto, respuesta
  perdida y fallo de reconciliación.
- [x] ADR, spec, contrato, registro y trazabilidad están actualizados.
- [x] `./gradlew ciVerify` pasa en la ejecución final única.

## Decisions and assumptions

- El resumen backend adjunto `SCDK-B55` y el OpenAPI vivo entregado son la decisión contractual
  aceptada para implementar M132.
- El ETag se trata como opaco y literal. No se añade persistencia/versionado local: cada operación
  obtiene una revisión fresca, y el ETag del `200` solo confirma la mutación actual.
- Si el GET fresco demuestra que la caché está obsoleta y la passphrase remota es válida, se
  sincroniza primero el wrapper base autoritativo; el wrapper candidato nunca se guarda antes de
  confirmación o reconciliación.
- El tercer wrapper actualiza solo `kekEncMaster`; la igualdad byte-for-byte del resto del material
  se valida antes de hacerlo.
- La invalidación de quick unlock es local y best-effort; `ManualOnly` evita relanzar el prompt
  automáticamente y el lock siempre zeroiza la KEK en memoria.

## Risks and gaps

- Si el almacenamiento local y Android Keystore fallan simultáneamente al borrar un enrolamiento
  quick unlock, el lock sigue aplicado pero la eliminación persistente no puede garantizarse. No
  se amplió el schema de storage sin una nueva decisión normativa; este caso queda como riesgo
  residual explícito.
- La carrera se verifica de forma determinista con dos instancias reales del caso de uso y un CAS
  in-memory atómico; la reproducción manual con dos dispositivos físicos en `dev` sigue siendo una
  validación operacional recomendable, no un bloqueo de implementación.
- El contrato backend garantiza que el CAS solo cambia wrapper, revisión y `updatedAt`; Android no
  puede verificar internamente la implementación PostgreSQL y confía en la evidencia B55.

## Next action

Realizar opcionalmente la reproducción manual en dos dispositivos dev y registrar modelos/versiones
Android como evidencia operacional adicional.

### Corrección post-implementación — 2026-08-28

La revisión post-implementación detectó que el bootstrap compartía por error el contrato versionado
del cambio de passphrase. La corrección mantiene `getVersionedKeyMaterial()` estricto y hace que
`getKeyMaterial()` acepte el payload válido aunque falte o no sea utilizable el `ETag`.

Evidencia adicional:

- `core/vault/.../RemoteVaultKeyMaterialDataSourceTest` cubre bootstrap sin `ETag` y mantiene el
  rechazo versionado sin `ETag`.
- `core/vault/.../RemoteVaultKeyMaterialDataSourceIntegrationTest` cubre el mismo comportamiento a
  través de Retrofit/MockWebServer.
- `./gradlew :core:vault:testDebugUnitTest` pasa.

La reproducción manual en dos dispositivos dev sigue siendo una validación operacional opcional.
