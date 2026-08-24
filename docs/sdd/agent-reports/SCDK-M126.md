# Agent Report — SCDK-M126

## Status

`DONE`

## Context

| Campo | Valor |
| --- | --- |
| Task ID | `SCDK-M126` |
| Spec | `SPEC-HARDENING-V1`, `SPEC-STORAGE`, `SPEC-VAULT-SYNC-V2` |
| ADRs | `ADR-0003-SENSITIVE-DATA-SURFACES` |
| Agent | `Codex` |
| Fecha | `2026-08-24` |

## Summary

Se deshabilitó el backup de Android y se aplicaron exclusiones explícitas para legacy backup,
cloud backup y device transfer. El manifest release mergeado queda limitado a la launcher Activity
como único componente exportado; el receiver de profileinstaller aportado por una dependencia se
retira del merge de la aplicación.

Se documentó el inventario de Room, tokens, material de claves, auto-lock y recovery transitoria.
La limpieza de sesión mantiene el orden contractual: primero KEK/material/registro y datos Room;
solo después se eliminan tokens mediante `forceLogout`. No se cambió el schema Room ni se añadió
fallback destructivo.

## Changed files

- `app/src/main/AndroidManifest.xml` — `allowBackup=false` y retirada del receiver exportado auxiliar.
- `app/src/main/res/xml/backup_rules.xml` — exclusión raíz legacy explícita.
- `app/src/main/res/xml/data_extraction_rules.xml` — exclusión raíz para cloud y device-transfer.
- `app/build.gradle.kts` — verificación XML del manifest release, reglas y componentes exportados.
- `build.gradle.kts` — integración de la verificación en `releaseVerify`.
- `app/src/test/java/com/miguelrodriguez19/safecube/app/session/AccountSessionLifecycleImplTest.kt` — contrato de limpieza de sesión y tokens.
- `docs/sdd/local-storage-inventory.md` — inventario de persistencia, protección, ciclo de vida y borrado.
- `docs/roadmap/roadmap--fase-7.md` — criterios de aceptación de M126 marcados con evidencia.
- `docs/sdd/traceability-matrix.md` — trazabilidad de `SPEC-STORAGE`, `SPEC-VAULT-SYNC-V2` y `SEC-PRIVACY-001`.
- `docs/sdd/agent-reports/SCDK-M126.md` — este informe.

## Tests and quality gates

| Comando | Resultado | Evidencia |
| --- | --- | --- |
| `./gradlew :app:testDebugUnitTest :core:auth:testDebugUnitTest :core:storage:testDebugUnitTest :core:vault:testDebugUnitTest` | `PASS` | tests de sesión, tokens, cleaner, Room y vault |
| `./gradlew :app:verifyReleaseSecurityManifest` | `PASS` | manifest release mergeado, reglas explícitas y componentes exportados |
| `./gradlew :app:testDebugUnitTest` | `PASS` | comprobación final tras el ajuste de aserciones |
| `./gradlew releaseVerify` | `PASS` | cobertura, lint debug/release, `assembleRelease` y verificación de manifest |
| `git diff --check` | `PASS` | sin errores de whitespace |

## Acceptance Criteria

- [x] El manifest release efectivo declara backup deshabilitado y el task lo verifica.
- [x] Las reglas legacy/cloud/device-transfer tienen exclusiones explícitas y no contienen plantillas, `include` ni `TODO`.
- [x] El inventario identifica contenido, protección y borrado de cada superficie local.
- [x] No se introduce SQLCipher, cambio de schema ni fallback destructivo de Room.
- [x] La limpieza cubre KEK/material/registro, officials, drafts, checkpoints y tokens según el orden contractual.
- [x] `releaseVerify` pasa.
- [x] Trazabilidad y agent report están actualizados.

## Decisions and assumptions

- Decisión: retirar `androidx.profileinstaller.ProfileInstallReceiver` del manifest merge porque era
  el único componente no launcher con `exported=true`; se conserva `profileable` y el initializer.
- Decisión: mantener la preferencia de auto-lock en `SharedPreferences` normal porque solo contiene
  un enum no sensible; la exclusión global evita que se transfiera.
- Supuesto: el contrato de limpieza existente, distribuido entre `LocalVaultDataCleaner`,
  `SessionManager` y `SecureItemLocalStorage`, es la frontera correcta; no se fusionan auth y vault
  en una dependencia nueva.

## Risks

- `tools:ignore="MissingClass"` está limitado al nodo de eliminación del receiver de profileinstaller;
  el task de manifest verifica el resultado efectivo y evita que vuelva a aparecer una exposición exportada.
- La exclusión estática del manifest no prueba un servicio de backup del sistema operativo; la
  política efectiva queda cubierta por `allowBackup=false` y las tres reglas explícitas exigidas por ADR-0003.

## Gaps and follow-up tasks

- Screenshots, logs, clipboard y pruebas instrumentadas de las superficies restantes siguen fuera de
  M126 y pertenecen a M127/M128 según el roadmap.

## Next action

Revisar este checkpoint y continuar con la siguiente tarea de la fase 7 cuando el owner confirme el
alcance de las superficies de plataforma restantes.
