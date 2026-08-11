# Agent Report — SCDK-M111

## Status

DONE

## Context

| Campo | Valor |
| --- | --- |
| Task ID | SCDK-M111 |
| Spec | [SPEC-HARDENING-V1](../../specs/features/hardening-resilience-v1.md), requisito SEC-CRYPTO-002 |
| ADR | [ADR-0002-PASSPHRASE-REWRAP](../../architecture/adr/ADR-0002-PASSPHRASE-REWRAP.md) ACCEPTED |
| Agent | Codex |
| Fecha | 2026-08-11 |

## Summary

Se definió el contrato documental para cambiar la passphrase mediante rewrap de la misma KEK.
El flujo exige vault desbloqueado, verificación de la passphrase actual, nueva passphrase y
confirmación; conserva el salt y parámetros KDF v1; actualiza el servidor antes de la caché local;
y reconcilia una respuesta perdida mediante `GET /vault/keys`.

El ADR también cierra los resultados comparables de la reconciliación, la ruta fail-closed para un
resultado indeterminado, los únicos errores reintentables, los invariantes de items y drafts y el
zeroizado best-effort. No se modificó runtime, APIs, storage, crypto remoto, sync ni payloads.

## Changed files

- `docs/architecture/adr/ADR-0002-PASSPHRASE-REWRAP.md` — decisión ACCEPTED sobre rewrap,
  orden remoto/local, reconciliación y recuperación segura.
- `docs/specs/features/hardening-resilience-v1.md` — enlace a ADR-0002 ACCEPTED y trazabilidad
  del requisito SEC-CRYPTO-002.
- `docs/sdd/spec-registry.md` — ADR-0002 registrado como ACCEPTED para SPEC-HARDENING-V1.
- `docs/sdd/traceability-matrix.md` — task, ADR, evidencia y gap de aprobación enlazados.
- `docs/package-structure/package_structure.md` — inventario actualizado con el ADR.
- `docs/sdd/agent-reports/SCDK-M111.md` — este informe.

## Tests and quality gates

| Comando | Resultado | Evidencia |
| --- | --- | --- |
| Lectura de AGENTS.md, manual SDD, SPEC-HARDENING-V1, crypto, payload, OpenAPI y template ADR | PASS | Contratos canónicos y límites de M111 revisados antes de editar. |
| Revisión de estado y rama con Git | PASS | Se mantuvo `feat/SCDK-M109--define-hardening-resilience-contract`; no había cambios de usuario pendientes al iniciar. |
| `git diff --check` | PASS | Sin errores de whitespace en los ficheros versionados modificados. |
| Tests de runtime / `./gradlew ciVerify` | SKIPPED | M111 solo define un ADR; la implementación corresponde a SCDK-M123/SCDK-M124. |

## Acceptance Criteria

- [x] El ADR define el orden remoto/local y la recuperación de respuesta perdida.
- [x] Queda demostrado que no se modifican items ni DEKs.
- [x] Los casos de passphrase actual incorrecta y resultado remoto incierto están cerrados.
- [x] El owner humano ha marcado el ADR como `ACCEPTED` el 2026-08-11.
- [x] Spec, trazabilidad y agent report enlazan el ADR.

## Decisions and assumptions

- Decisión: el cambio de passphrase reenvuelve la misma KEK, con el mismo salt y parámetros KDF,
  un nonce nuevo y sin modificar `kekEncRecovery` ni datos de items.
- Decisión: una respuesta incierta se reconcilia primero con `GET /vault/keys`; no se repite el
  `PUT` a ciegas.
- Decisión: si la reconciliación no determina el wrapper remoto, se elimina la caché maestra, se
  zeroiza la KEK y se bloquea el vault, conservando datos cifrados y recovery.
- Supuesto: el endpoint existente puede confirmar el éxito por estado HTTP o mediante una lectura
  posterior que devuelva el wrapper exacto, sin ampliar el contrato API en M111.

## Risks

- La persistencia local puede fallar después de un éxito remoto; mitigación: persistencia atómica,
  reintento solo del wrapper confirmado y ruta de reconciliación sin regenerar material.

## Gaps and follow-up tasks

- Implementación y tests del rewrap → SCDK-M123/SCDK-M124, tras la aceptación del ADR.

## Next action

Las tareas SCDK-M123/SCDK-M124 pueden implementar el contrato y sus tests sin reinterpretar el
orden de persistencia ni la ruta de respuesta incierta.
