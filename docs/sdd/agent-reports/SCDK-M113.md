# Agent Report — SCDK-M113

## Status

`DONE`

## Context

| Campo | Valor |
| --- | --- |
| Task ID | SCDK-M113 |
| Spec | [SPEC-HARDENING-V1](../../specs/features/hardening-resilience-v1.md), requisitos NFR-RESILIENCE-001/002 |
| ADRs | N/A; usa la política ACCEPTED de [SPEC-HARDENING-V1](../../specs/features/hardening-resilience-v1.md) |
| Agent | Codex |
| Fecha | 2026-08-12 |

## Summary

Se implementó en `core:network` la clasificación agnóstica de fallos con `NetworkFailureKind` y
la decisión explícita `Retryable`/`Terminal`. Transporte, timeout, 408, 429 y 5xx son retryables;
validación, credenciales, conflictos, protocolo, respuestas malformadas y causas desconocidas son
terminales. `CancellationException` se relanza sin convertirse en error de negocio.

Auth y vault propagan únicamente la clasificación, el código HTTP seguro y los nombres de campos
de validación. Se preservan el conflicto de idempotencia 409, CAS 412, protocolo 428,
unauthorized/forbidden y los errores de respuesta malformada sin conservar bodies HTTP,
mensajes de excepción ni `Throwable` en modelos de resultado.

## Changed files

- `core/network/.../NetworkFailure.kt` — clasificación canónica y decisión de retry.
- `core/network/.../NetworkFailureClassifierTest.kt` — matriz de códigos, excepciones y cancelación.
- `core/auth` — resultados remotos, mapper, repository, refresh handler y modelo `AuthError` sanitizados.
- `core/vault` — errores remotos, key material, secure items, bootstrap, sync y limpieza local sanitizados.
- `app` y `feature/auth` — adaptación a resultados de cleanup sin excepciones transportadas.
- `docs/specs/features/hardening-resilience-v1.md` — evidencia de M113 en la trazabilidad normativa.
- `docs/sdd/traceability-matrix.md` — código, tests, estado parcial y tareas pendientes.
- `docs/sdd/agent-reports/SCDK-M113.md` — este informe.

## Tests and quality gates

| Comando | Resultado | Evidencia |
| --- | --- | --- |
| `./gradlew :core:network:testDebugUnitTest` | `PASS` | Incluye la matriz exhaustiva de `NetworkFailureClassifierTest`. |
| `./gradlew :core:auth:testDebugUnitTest` | `PASS` | Mapeos de auth, refresh, cancelación y ausencia de body/mensaje crudo. |
| `./gradlew :core:vault:testDebugUnitTest` | `PASS` | Key material, secure items, sync, crypto, bootstrap y cancelación. |
| `./gradlew :core:network:compileDebugKotlin :core:auth:compileDebugKotlin :core:vault:compileDebugKotlin` | `PASS` | Compilación de los tres módulos esperados. |
| `./gradlew ciVerify` | `PASS` | 761 tareas completadas; incluye compilación release, lint, tests unitarios y verificación de cobertura. |

## Acceptance Criteria

- [x] Todos los códigos y fallos de la matriz tienen clasificación determinista.
- [x] Ningún modelo de fallo expuesto a UI contiene body HTTP, token, URL o mensaje crudo de excepción.
- [x] `CancellationException` nunca se convierte en error de negocio.
- [x] Tests unitarios cubren transporte, timeout, 408, 429, 4xx relevantes y 5xx.
- [x] `./gradlew ciVerify` pasa; 761 tareas completadas el 2026-08-12.
- [x] Trazabilidad y agent report están actualizados.

## Decisions and assumptions

- Decisión: 404 se conserva como `Unknown` terminal en el clasificador agnóstico y cada dominio
  lo representa con su error seguro de not-found cuando corresponde.
- Decisión: los campos de validación se reducen a nombres; valores y mensajes del body se descartan.
- Decisión: 409, 412 y 428 son terminales en la clasificación común; sync mantiene su resolución
  específica para idempotencia, CAS y precondición.
- Supuesto: las tareas posteriores implementarán la ejecución de retry y la state machine sin
  cambiar esta clasificación canónica.

## Risks

- La ejecución de retry todavía no existe; los consumidores deben usar `RetryDecision` antes de
  añadir reintentos en tareas posteriores.

## Gaps and follow-up tasks

- State machine de estados observables y retry seguro → SCDK-M117–SCDK-M121.
- Reconciliación de bootstrap y respuesta perdida → SCDK-M116/SCDK-M119.

## Next action

Continuar con las tareas que consumen `RetryDecision` sin duplicar la matriz ni reintroducir
bodies HTTP o excepciones en modelos de UI.
