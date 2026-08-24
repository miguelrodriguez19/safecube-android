# Agent Report — SCDK-M127

## Status

`DONE`

## Context

| Campo | Valor |
| --- | --- |
| Task ID | `SCDK-M127` |
| Spec | `SPEC-HARDENING-V1` — `SEC-PRIVACY-001` |
| ADRs | `ADR-0003-SENSITIVE-DATA-SURFACES` |
| Agent | `Codex` |
| Fecha | `2026-08-24` |

## Summary

Se aplicó `FLAG_SECURE` a la ventana raíz de `MainActivity`, por lo que la protección cubre las
rutas de SafeCube y la miniatura de recents. Login y Signup ahora enmascaran password y
confirmación con `PasswordVisualTransformation`; Create Vault, Unlock Vault, Change Passphrase y
Password Editor ya cumplían el mismo contrato y quedaron cubiertos por una auditoría automática.

No se encontraron escrituras programáticas de secretos al clipboard en los módulos Android propios.
La auditoría se integró en `ciVerify` y recorre todos los módulos Android del repositorio. Las
pruebas instrumentadas usan únicamente datos sintéticos y no imprimen valores sensibles.

## Changed files

- `app/src/main/java/com/miguelrodriguez19/safecube/app/entrypoint/MainActivity.kt` — aplica `FLAG_SECURE` antes de renderizar el contenido.
- `app/src/androidTest/java/com/miguelrodriguez19/safecube/MainActivitySmokeTest.kt` — verifica el flag activo y las máscaras visuales de Login/Signup.
- `feature/auth/src/main/java/com/miguelrodriguez19/safecube/feature/auth/presentation/login/ui/LoginScreen.kt` — enmascara password.
- `feature/auth/src/main/java/com/miguelrodriguez19/safecube/feature/auth/presentation/signup/ui/SignupScreen.kt` — enmascara password y confirmación.
- `build.gradle.kts` — añade `verifySensitiveClipboardAudit` y `verifySensitiveFieldAudit` a `ciVerify`.
- `docs/roadmap/roadmap--fase-7.md` — marca los AC de M127 con evidencia.
- `docs/sdd/traceability-matrix.md` — actualiza la trazabilidad de `SEC-PRIVACY-001`.
- `docs/sdd/agent-reports/SCDK-M127.md` — este informe.

## Tests and quality gates

| Comando | Resultado | Evidencia |
| --- | --- | --- |
| `./gradlew verifySensitiveClipboardAudit verifySensitiveFieldAudit :app:compileDebugAndroidTestKotlin` | `PASS` | auditorías de clipboard/campos y compilación instrumentada |
| `./gradlew :app:connectedDebugAndroidTest` | `PASS` | 5 tests en `Pixel_8a`, incluyendo `FLAG_SECURE`, Login y Signup |
| `./gradlew ciVerify` | `PASS` | tests JVM, cobertura, lint, auditorías y `assembleRelease` |
| `git diff --check` | `PASS` | sin errores de whitespace |

## Acceptance Criteria

- [x] `FLAG_SECURE` está presente en la ventana activa y lo verifica `MainActivitySmokeTest`.
- [x] Todos los campos secretos enumerados están visualmente protegidos; los campos existentes de vault se auditan y Login/Signup se prueban en Compose.
- [x] No existen escrituras programáticas de secretos al clipboard; la auditoría cubre todos los módulos Android propios.
- [x] Tests y auditoría no imprimen valores sensibles.
- [x] `ciVerify` pasa.
- [x] Trazabilidad y agent report están actualizados.

## Decisions and assumptions

- Decisión: aplicar `FLAG_SECURE` en `MainActivity`, la única ventana de la aplicación, para cubrir todas las rutas sin acoplar la protección a pantallas concretas.
- Decisión: mantener `PasswordVisualTransformation` únicamente en los campos de password/passphrase; display hint, username, URL y notes no se transforman.
- Decisión: convertir la ausencia de APIs de clipboard en una comprobación de CI que incluya los módulos `app`, `core` y `feature`.
- Supuesto: la semántica accesible existente de los `OutlinedTextField` es suficiente porque el valor no se usa como `label` ni `contentDescription`; la transformación afecta solo a la representación visual.

## Risks

- La auditoría de campos requiere actualizar su inventario si se añade una nueva pantalla sensible; esto hace visible el cambio como fallo de CI.
- `FLAG_SECURE` protege la ventana de SafeCube, pero no puede controlar capturas realizadas fuera de la ventana o por herramientas del sistema con privilegios especiales.

## Gaps and follow-up tasks

- El logging sensible de tráfico HTTP y la auditoría R8 permanecen en el alcance de `SCDK-M128`.

## Next action

Revisar los cambios de M127 y, cuando el owner lo solicite, crear el commit convencional de la tarea.
