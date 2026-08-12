# Agent Report — SCDK-M112

## Status

DONE

## Context

| Campo | Valor |
| --- | --- |
| Task ID | SCDK-M112 |
| Spec | [SPEC-HARDENING-V1](../../specs/features/hardening-resilience-v1.md), requisito SEC-PRIVACY-001 |
| ADR | [ADR-0003-SENSITIVE-DATA-SURFACES](../../architecture/adr/ADR-0003-SENSITIVE-DATA-SURFACES.md) ACCEPTED |
| Agent | Codex |
| Fecha | 2026-08-12 |

## Summary

Se definió la política de plataforma para backup, device transfer, screenshots, recents, logs,
clipboard, visualización de passwords/passphrases, estado restaurable y R8. El ADR aplica una
postura deny-by-default a todas las build types y delimita la única excepción: un registro
transitorio cifrado, ligado a dispositivo/cuenta y excluido de backup para recuperar una
inicialización pendiente tras process death.

El registro no permite plaintext, passphrase, MASTER_KEY, KEK ni DEK en claro; reutiliza el mismo
candidato al reconciliar y se elimina con verificación al confirmar la recovery key, cerrar sesión,
descartar el intento o detectar material remoto incompatible. No se modificó runtime, APIs, Room,
payloads, crypto de items ni sync.

## Changed files

- `docs/architecture/adr/ADR-0003-SENSITIVE-DATA-SURFACES.md` — decisión ACCEPTED sobre superficies
  sensibles, excepción transitoria cifrada y reglas R8.
- `docs/specs/features/hardening-resilience-v1.md` — enlace a ADR-0003 ACCEPTED y estado de
  SEC-PRIVACY-001.
- `docs/sdd/spec-registry.md` — ADR-0003 registrado como ACCEPTED.
- `docs/sdd/traceability-matrix.md` — task, ADR, evidencia y gap de aprobación enlazados.
- `docs/package-structure/package_structure.md` — inventario actualizado con ADR-0003 y este
  informe.
- `docs/sdd/agent-reports/SCDK-M112.md` — este informe.

## Tests and quality gates

| Comando | Resultado | Evidencia |
| --- | --- | --- |
| Lectura de AGENTS.md, manual SDD, specs, contratos, roadmap, template ADR y estado actual | PASS | Se confirmaron manifest `allowBackup`, reglas de backup de plantilla, logging HTTP BODY, ausencia de FLAG_SECURE y recovery key en saved state/navegación. |
| `git diff --check` | PASS | Sin errores de whitespace en los ficheros versionados modificados. |
| Tests de runtime / `./gradlew ciVerify` | SKIPPED | M112 solo define un ADR; la implementación corresponde a SCDK-M124 y SCDK-M126–SCDK-M128. |

## Acceptance Criteria

- [x] El ADR cubre backup, screenshots, logs, clipboard, saved state y R8.
- [x] La excepción transitoria para recovery está delimitada y tiene borrado verificable.
- [x] No se introduce almacenamiento de plaintext.
- [x] El owner humano ha marcado el ADR como `ACCEPTED` el 2026-08-12.
- [x] Spec, trazabilidad y agent report enlazan el ADR.

## Decisions and assumptions

- Decisión: `android:allowBackup=false` y exclusiones explícitas de cloud backup y device transfer
  se aplican a todas las variantes, sin includes de datos de la aplicación.
- Decisión: `FLAG_SECURE` se aplica a la Activity completa, sin opt-out por pantalla o build type.
- Decisión: no existe logging HTTP de headers/bodies ni escritura de secretos al clipboard; los
  logs permitidos son eventos estructurados y no identificables.
- Decisión: la recovery key no viaja por rutas ni saved state; el único estado superviviente es el
  registro cifrado de inicialización pendiente.
- Supuesto: el adapter de persistencia implementará la clave no exportable y el envelope AEAD v1
  sin introducir SQLCipher, manteniendo la decisión observable del ADR.

## Risks

- Una eliminación local no verificable puede dejar ciphertext cifrado; mitigación: bloquear la
  finalización y reintentar cleanup, sin continuar como si el registro hubiera desaparecido.

## Gaps and follow-up tasks

- Implementación y tests de bootstrap/transient record → SCDK-M113, SCDK-M116.
- Implementación y tests de superficies de plataforma, logging, saved state y R8 → SCDK-M124,
  SCDK-M126–SCDK-M128.

## Next action

Las tareas de runtime pueden implementar la configuración de plataforma y el registro transitorio
sin reinterpretar las prohibiciones de exposición ni su ciclo de borrado.
