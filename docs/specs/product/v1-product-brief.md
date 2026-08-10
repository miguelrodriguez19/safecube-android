# SPEC-PRODUCT-V1 — SafeCube v1 Product Brief

## Metadata

| Campo              | Valor                                                         |
|--------------------|---------------------------------------------------------------|
| ID                 | `SPEC-PRODUCT-V1`                                             |
| Estado             | `APPROVED`                                                    |
| Owner              | Product/Maintainer                                            |
| Fecha              | `2026-07-29`                                                  |
| Última revisión    | `2026-08-10`                                                  |
| Reemplaza          | `N/A`                                                         |
| Dependencias       | `SPEC-CRYPTO-V1`, `SPEC-VAULT-SYNC-V2`, `SPEC-RELEASE-POLICY`, `SPEC-HARDENING-V1`, `ADR-0001` |
| Tasks relacionadas | `SCDK-M91`, `SCDK-M109`, `SCDK-M110`, `SCDK-M131`            |

## Visión

SafeCube es un gestor de secretos zero-knowledge, offline-first y centrado en cliente. El backend
proporciona identidad, sesión, almacenamiento opaco y sincronización, pero no debe conocer el
contenido descifrado del vault ni material de claves.

## Usuario objetivo

Una persona que necesita guardar passwords y notas sensibles, acceder a ellas desde más de un
dispositivo y conservar la disponibilidad local incluso cuando la red no está disponible.

## Propuesta de valor

El usuario puede crear, leer, editar, eliminar y sincronizar secretos cifrados manteniendo el
control criptográfico en el cliente y recibiendo feedback claro sobre lock, sync, conflictos y
errores.

## Alcance de la beta abierta

La beta abierta debe cubrir:

- registro y login;
- refresh y logout seguros;
- creación y desbloqueo del vault;
- desbloqueo rápido local opcional mediante biometría fuerte o credencial segura del dispositivo,
  protegido por Android Keystore y con passphrase como recuperación;
- recovery key;
- CRUD cifrado de passwords y notas;
- persistencia local offline-first;
- sync multi-device mediante el protocolo v2;
- drafts, conflictos y borrados lógicos;
- expiración de sesión y cierre seguro;
- release mediante APK firmado en GitHub Releases.

## No objetivos de v1

- Publicación en Google Play.
- Carpetas reales.
- Búsqueda avanzada.
- PIN propio de SafeCube distinto de la credencial segura del dispositivo.
- Background sync periódico con WorkManager.
- Merge semántico de secretos.
- Adjuntos o rich text.
- Soporte local simultáneo para varias cuentas.

## Principios

- Zero-knowledge por diseño.
- Offline-first con `Room` como source of truth visible para UI.
- No perder cambios locales ni ocultar conflictos.
- Separar login de unlock del vault.
- Tratar biometría y credencial segura del dispositivo como métodos locales alternativos de unlock,
  nunca como sustitutos del login de cuenta ni de la passphrase de recuperación.
- No exponer secretos en logs, telemetry, screenshots o errores.
- Contratos versionados y decisiones técnicas registradas.
- Cada release pública debe ser reproducible y trazable.

## Éxito de la beta

La beta es usable cuando una persona puede instalar el APK, registrarse, crear y desbloquear un
vault, crear una password y una nota, cerrar y volver a abrir la app, desbloquear de nuevo mediante
el método local disponible, sincronizar desde otro dispositivo y recuperarse de errores de red sin
perder datos. Un dispositivo sin biometría o bloqueo seguro debe conservar el unlock mediante
passphrase sin degradar la confidencialidad del vault.

Además:

- los flujos críticos tienen tests automatizados y evidencia manual definida;
- una release pública puede asociarse a commit, tag, `versionCode`, firma y checksum;
- los fallos operativos no exponen secretos ni requieren intervención sobre datos cifrados.

## Trazabilidad

- Crypto: `SPEC-CRYPTO-V1`.
- Secure items: `SPEC-SECURE-ITEM-PAYLOAD-V1`.
- Sync: `SPEC-VAULT-SYNC-V2`.
- Release: `SPEC-RELEASE-POLICY`.
- Hardening y quick unlock: `SPEC-HARDENING-V1`, `ADR-0001-VAULT-AUTO-LOCK`.
