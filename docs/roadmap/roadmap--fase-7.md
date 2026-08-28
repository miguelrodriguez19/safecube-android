# FASE 7 — Hardening, Seguridad & Resiliencia

## Objetivo de la fase

Endurecer los flujos funcionales construidos en las fases 0-5 para que los fallos previsibles se
recuperen o se expliquen sin pérdida de datos, exposición de secretos ni degradación de los
contratos de crypto y sync.

Esta fase no rediseña visualmente la aplicación. Deja estabilizados los estados, errores, retries,
sesión, lifecycle y superficies sensibles sobre los que trabajará la Fase 8.

## Estado de partida verificado

- El refresh de tokens y el logout local existen, pero no distinguen de extremo a extremo una
  expiración definitiva de un fallo transitorio.
- Sync v2 conserva drafts, `mutationId` y checkpoints, pero la UI reduce sus fallos a mensajes
  genéricos.
- El gate del vault puede interpretar un estado desconocido como Unlock.
- No existen auto-lock ni cambio de passphrase.
- Una respuesta perdida durante `POST /vault/keys` puede dejar una recovery key aceptada por el
  backend sin posibilidad de volver a mostrarla.
- La recovery key pendiente se transporta mediante `rememberSaveable`.
- El manifest permite backup y conserva reglas Android de plantilla.
- El cliente HTTP usa logging `BODY` en builds debug.
- Carpetas, perfil y parte de Settings siguen siendo placeholders fuera del corte de v1.
- `feature:auth` no tiene tests JVM.

## Decisiones de planificación

- La fase comienza creando una spec normativa y tres ADRs. Las cards de implementación no empiezan
  hasta que esos documentos estén `APPROVED`/`ACCEPTED`.
- La sesión autenticada y el estado unlocked del vault son estados independientes.
- Los retries de mutaciones nunca crean una identidad, draft o generación criptográfica nueva.
- Process death siempre descarta la KEK y obliga a desbloquear de nuevo.
- El quick unlock local mediante biometría fuerte o credencial segura del dispositivo protegida por
  Android Keystore forma parte del MVP; la passphrase sigue siendo fallback y recuperación.
- SafeCube no añade un PIN propio: biometría y credencial del dispositivo son alternativas de una
  única operación Unlock, no una tercera sesión ni autenticación backend.
- La primera entrega pública no ofrece una opción de auto-lock `Never`.
- El cambio de passphrase reenvuelve la KEK; no rota la KEK ni recifra items.
- Payloads corruptos fallan en cerrado y no se eliminan automáticamente.
- Room continúa siendo la source of truth local y no se introduce SQLCipher sin una decisión
  posterior.
- El rediseño visual, búsqueda, carpetas y background sync siguen fuera de alcance.

La implementación de quick unlock se ejecuta en la card atómica `SCDK-M131`; debe completarse antes
de `SCDK-M130` y no se integra de forma oportunista en otra card.

La prueba manual multidispositivo de cambio de passphrase ha detectado una condición de carrera
entre clientes que comparten vault. `SCDK-M132` documenta el bug y queda como dependencia de cierre
de la fase hasta que el backend defina un contrato de concurrencia y el cliente pueda aplicarlo.

## Orden de implementación

1. Contratos normativos: `SCDK-M109`–`SCDK-M112`.
2. Clasificación y resiliencia de dominio: `SCDK-M113`–`SCDK-M117`.
3. Estados y recuperación de UI: `SCDK-M118`–`SCDK-M121`.
4. Controles de seguridad de usuario: `SCDK-M122`–`SCDK-M125`.
5. Hardening de plataforma y limpieza de alcance: `SCDK-M126`–`SCDK-M129`.
6. Quick unlock con Android Keystore: `SCDK-M131`.
7. Corrección de concurrencia del cambio de passphrase: `SCDK-M132`.
8. Verificación transversal y cierre: `SCDK-M130`.

---

# SCDK-M109. Definir el contrato de hardening, seguridad y resiliencia de SafeCube v1

## Main Story (How, I Want, To)

Como maintainer, quiero una especificación normativa de hardening para que los agentes implementen
la Fase 7 sin inventar políticas de errores, seguridad o recuperación.

## Context, Functional Description & Goal

La Fase 7 solo está descrita en el roadmap. Las specs actuales regulan producto, crypto, payload,
storage y sync, pero no definen una matriz transversal de errores, auto-lock, process death,
protección de superficies sensibles ni recuperación de operaciones inciertas.

El resultado único de esta tarea es una spec revisada y aprobada que convierta la Fase 7 en
requisitos observables.

## Steps/Scope

### In Scope

- Crear `docs/specs/features/hardening-resilience-v1.md` con ID `SPEC-HARDENING-V1`.
- Crear inicialmente la spec con estado `REVIEW`; solo el owner humano puede promoverla a
  `APPROVED`.
- Definir estos requisitos estables:
  - `NFR-RESILIENCE-001`: estados consistentes de carga, contenido, vacío, error y retry.
  - `NFR-RESILIENCE-002`: clasificación explícita de errores reintentables y definitivos.
  - `FR-AUTH-002`: expiración de sesión y refresh fallido.
  - `FR-VAULT-002`: bootstrap del vault, red lenta y respuesta perdida.
  - `SEC-SESSION-001`: auto-lock y eliminación de la KEK en memoria.
  - `SEC-CRYPTO-002`: cambio de passphrase mediante rewrap.
  - `SEC-PRIVACY-001`: backups, screenshots, logs, clipboard y estado transitorio.
  - `NFR-LIFECYCLE-001`: process death, cold start y restauración de navegación.
  - `FR-SCOPE-001`: retirada de placeholders fuera de v1.
- Definir el modelo observable mínimo de operación: `Idle`, `InitialLoading`, `Content`, `Empty`,
  `Mutating`, `RetryableError` y `TerminalError`.
- Establecer que un error de refresh o sync no oculta contenido local ya disponible.
- Establecer que ninguna mutación se reintenta automáticamente con una identidad nueva.
- Definir que errores de transporte, timeout, HTTP 408/429 y 5xx son reintentables.
- Definir como definitivos los errores de validación, protocolo, integridad criptográfica y payload
  corrupto, salvo que una acción explícita pueda reparar su causa.
- Definir que payloads corruptos fallan en cerrado, se conservan cifrados y no se eliminan
  automáticamente.
- Definir que process death siempre descarta la KEK y obliga a desbloquear de nuevo.
- Enlazar las specs de auth, crypto, payload, sync, storage y producto existentes.
- Registrar la spec en `docs/sdd/spec-registry.md` y sus requisitos en
  `docs/sdd/traceability-matrix.md`.

### Out of Scope (if applies)

- Modificar código de runtime.
- Elegir detalles técnicos reservados a los ADRs de las siguientes tareas.
- Rediseñar visualmente las pantallas.
- Añadir biometría, búsqueda, carpetas o background sync.

## Additional Information and Configuration

- Autoridad previa: `SPEC-PRODUCT-V1`, `SPEC-CRYPTO-V1`,
  `SPEC-SECURE-ITEM-PAYLOAD-V1`, `SPEC-VAULT-SYNC-V2` y `SPEC-STORAGE`.
- Dependencia: ninguna.
- La spec no puede quedar `APPROVED` con `TBD`, decisiones críticas abiertas o ACs no verificables.
- Crear `docs/sdd/agent-reports/SCDK-M109.md`.

### API Contract and Expected Behavior (if applies)

No cambia APIs. La spec será la fuente normativa de todas las cards posteriores de Fase 7.

### Acceptance Criteria (ACs)

- [ ] Existe `SPEC-HARDENING-V1` con todos los requisitos enumerados.
- [ ] La matriz de retry cubre auth, refresh, vault bootstrap, sync, storage y crypto.
- [ ] Cada requisito tiene criterios observables y estrategia de test.
- [ ] La spec enlaza los contratos canónicos sin duplicarlos ni contradecirlos.
- [ ] No quedan decisiones críticas marcadas como `TBD`.
- [ ] El owner humano ha promovido la spec a `APPROVED`.
- [ ] Registry, trazabilidad y agent report están actualizados.

---

# SCDK-M110. Decidir la política de auto-lock y ciclo de vida del vault

## Main Story (How, I Want, To)

Como security owner, quiero una política inequívoca de bloqueo por background para que la KEK no
permanezca en memoria más tiempo del autorizado.

## Context, Functional Description & Goal

`VaultSessionManager` puede bloquear y zeroizar la KEK, pero ninguna parte de la aplicación observa
el ciclo de vida del proceso. La política debe distinguir autenticación de cuenta y desbloqueo del
vault.

## Steps/Scope

### In Scope

- Crear `docs/architecture/adr/ADR-0001-VAULT-AUTO-LOCK.md` inicialmente como `PROPOSED`.
- Decidir que el auto-lock actúa sobre el vault, no sobre la sesión autenticada.
- Adoptar timeout por defecto `Immediately`.
- Admitir exclusivamente: inmediatamente, 30 segundos, 1 minuto, 5 minutos y 15 minutos.
- No ofrecer una opción `Never`.
- Medir el tiempo con reloj monotónico, nunca con fecha/hora de pared.
- Usar lifecycle de proceso para evitar bloqueos por simple recreación de Activity.
- Al llegar al timeout: zeroizar la KEK, cancelar operaciones protegidas, limpiar plaintext visible
  y establecer Unlock como raíz.
- Conservar tokens, items cifrados, drafts y checkpoints.
- Definir que process death equivale siempre a vault bloqueado.
- Definir una acción manual `Lock now`.
- Registrar el ADR en la spec y la matriz de trazabilidad.

### Out of Scope (if applies)

- Logout automático.
- Biometría.
- Timeout de sesión del backend.
- WorkManager o servicios en background.

## Additional Information and Configuration

- Spec: `SPEC-HARDENING-V1`, requisito `SEC-SESSION-001`.
- Dependencia: `SCDK-M109`.
- Crear `docs/sdd/agent-reports/SCDK-M110.md`.

### API Contract and Expected Behavior (if applies)

Una transición a background inicia el plazo configurado. Volver antes del plazo conserva el vault
abierto. Alcanzar el plazo o matar el proceso obliga a introducir de nuevo la passphrase.

### Acceptance Criteria (ACs)

- [ ] El ADR diferencia account session y vault session.
- [ ] Timeout por defecto, opciones y semántica temporal están cerrados.
- [ ] Queda prohibida la opción de mantener el vault desbloqueado indefinidamente.
- [ ] Config change, background real y process death están diferenciados.
- [ ] El owner humano ha marcado el ADR como `ACCEPTED`.
- [ ] Spec, trazabilidad y agent report enlazan el ADR.

---

# SCDK-M111. Decidir la consistencia del cambio de passphrase mediante rewrap

## Main Story (How, I Want, To)

Como usuario, quiero cambiar mi passphrase sin recifrar todos mis secretos ni arriesgarme a perder
acceso al vault.

## Context, Functional Description & Goal

`PUT /vault/keys/master` ya existe y `SPEC-CRYPTO-V1` establece que v1 reenvuelve la KEK. Falta
definir verificación de credenciales, orden de persistencia y recuperación cuando la respuesta de
red se pierde.

## Steps/Scope

### In Scope

- Crear `docs/architecture/adr/ADR-0002-PASSPHRASE-REWRAP.md` inicialmente como `PROPOSED`.
- Requerir vault desbloqueado, passphrase actual, nueva passphrase y confirmación.
- Mantener en v1 el mismo salt y parámetros KDF existentes.
- Verificar la passphrase actual desenvolviendo la KEK y comparándola con la KEK activa.
- Envolver la misma KEK con la nueva `MASTER_KEY` y un nonce nuevo.
- No cambiar `kekEncRecovery`, DEKs, payloads, `payloadVersion`, `itemRevision` ni drafts.
- Actualizar la caché local únicamente cuando el servidor confirme el cambio o una lectura posterior
  confirme exactamente el nuevo wrapper.
- Ante respuesta perdida, ejecutar `GET /vault/keys` antes de declarar éxito.
- Si el resultado no puede determinarse, borrar el material maestro cacheado, zeroizar la KEK,
  bloquear el vault y exigir reconciliación online.
- Zeroizar best-effort passphrases convertidas a bytes, MASTER_KEY y copias de la KEK.
- Registrar el ADR en spec y trazabilidad.

### Out of Scope (if applies)

- Rotar la KEK o recovery key.
- Recifrar items.
- Recuperar una passphrase olvidada.
- Añadir una política nueva de complejidad.

## Additional Information and Configuration

- Specs: `SPEC-HARDENING-V1`, `SPEC-CRYPTO-V1` y
  `SPEC-SECURE-ITEM-PAYLOAD-V1`.
- Contrato: `PUT /vault/keys/master`.
- Dependencia: `SCDK-M109`.
- Crear `docs/sdd/agent-reports/SCDK-M111.md`.

### API Contract and Expected Behavior (if applies)

El request contiene exclusivamente el nuevo `kekEncMaster`. Un éxito mantiene la KEK efectiva y
todos los payloads exactamente iguales.

### Acceptance Criteria (ACs)

- [ ] El ADR define el orden remoto/local y la recuperación de respuesta perdida.
- [ ] Queda demostrado que no se modifican items ni DEKs.
- [ ] Los casos de passphrase actual incorrecta y resultado remoto incierto están cerrados.
- [ ] El owner humano ha marcado el ADR como `ACCEPTED`.
- [ ] Spec, trazabilidad y agent report enlazan el ADR.

---

# SCDK-M112. Decidir la política de exposición y persistencia de datos sensibles

## Main Story (How, I Want, To)

Como security owner, quiero una política de plataforma única para impedir que secretos aparezcan en
backups, screenshots, logs, clipboard o estado restaurable.

## Context, Functional Description & Goal

Actualmente la aplicación permite backup, conserva reglas Android de plantilla, usa logging HTTP
BODY en debug y transporta la recovery key mediante `rememberSaveable`.

## Steps/Scope

### In Scope

- Crear `docs/architecture/adr/ADR-0003-SENSITIVE-DATA-SURFACES.md` inicialmente como `PROPOSED`.
- Decidir `android:allowBackup="false"` y exclusión explícita de cloud backup y device transfer.
- Aplicar protección de screenshots y recents mediante `FLAG_SECURE` a toda la Activity.
- Prohibir logs de headers, cuerpos HTTP, tokens, passphrases, recovery keys, payloads,
  `displayHint` e IDs de items en release/benchmark. La decisión de 2026-08-28 mantiene debug hasta
  el gate de retirada definitiva de Fase 9.
- Prohibir escrituras programáticas de secretos al clipboard durante v1.
- Exigir visual transformation para passwords y passphrases.
- Prohibir secretos en `Bundle`, rutas serializables, `SavedStateHandle` o `rememberSaveable`.
- Permitir un registro transitorio cifrado y excluido de backup para recuperar una inicialización de
  vault pendiente después de process death.
- Eliminar ese registro al confirmar que la recovery key fue guardada, al cerrar sesión o al
  descartar de forma segura el intento.
- Exigir reglas R8 mínimas y justificadas; no aceptar `-keep` globales.
- Registrar el ADR en spec y trazabilidad.

### Out of Scope (if applies)

- SQLCipher.
- Copy-to-clipboard de passwords.
- Telemetría y crash reporting.
- Publicación de mapping files.
- Rediseño visual.

## Additional Information and Configuration

- Specs: `SPEC-HARDENING-V1`, `SPEC-PRODUCT-V1`, `SPEC-CRYPTO-V1` y
  `SPEC-STORAGE`.
- Dependencia: `SCDK-M109`.
- Crear `docs/sdd/agent-reports/SCDK-M112.md`.

### API Contract and Expected Behavior (if applies)

La app no exporta datos mediante backup, no permite capturas de sus ventanas y no persiste secretos
en el estado de navegación. El estado transitorio de inicialización está cifrado y tiene ciclo de
vida explícito.

### Acceptance Criteria (ACs)

- [ ] El ADR cubre backup, screenshots, logs, clipboard, saved state y R8.
- [ ] La excepción transitoria para recovery está delimitada y tiene borrado verificable.
- [ ] No se introduce almacenamiento de plaintext.
- [ ] El owner humano ha marcado el ADR como `ACCEPTED`.
- [ ] Spec, trazabilidad y agent report enlazan el ADR.

---

# SCDK-M113. Implementar la clasificación canónica de fallos y retry

## Main Story (How, I Want, To)

Como usuario, quiero que la app distinga fallos recuperables de errores definitivos para recibir una
acción segura y comprensible.

## Context, Functional Description & Goal

Auth, key material y sync usan modelos de error distintos. Algunos conservan excepciones o cuerpos
HTTP y la UI termina mostrando errores genéricos sin saber si puede reintentar.

## Steps/Scope

### In Scope

- Añadir en `core:network` una clasificación agnóstica de contenido: `Connectivity`, `Timeout`,
  `RateLimited`, `ServerUnavailable`, `Unauthorized`, `Forbidden`, `Validation`, `Conflict`,
  `Protocol`, `MalformedResponse` y `Unknown`.
- Exponer una decisión explícita `Retryable` o `Terminal`.
- Clasificar como retryable transporte, timeout, 408, 429 y 5xx.
- Mantener 400, 409, 412 y 428 bajo la semántica específica de cada dominio.
- No almacenar bodies HTTP ni mensajes de excepción en modelos que alcancen la UI.
- Mapear la clasificación en `core:auth` y `core:vault` sin perder conflicto CAS 412, conflicto de
  idempotencia 409, validación/protocolo 400/428 y unauthorized.
- Preservar `CancellationException`.
- Añadir tests unitarios exhaustivos de la matriz.

### Out of Scope (if applies)

- Reintentar operaciones.
- Cambiar navegación.
- Diseñar componentes visuales.
- Registrar errores o telemetría.

## Additional Information and Configuration

- Spec: `SPEC-HARDENING-V1`, requisitos `NFR-RESILIENCE-001/002`.
- Dependencias: `SCDK-M109`, `SCDK-M112`.
- Módulos: `core:network`, `core:auth`, `core:vault`.
- Ejecutar tests de los tres módulos y `./gradlew ciVerify`.
- Actualizar trazabilidad y crear `docs/sdd/agent-reports/SCDK-M113.md`.

### API Contract and Expected Behavior (if applies)

La clasificación no modifica OpenAPI ni status codes. Convierte respuestas y excepciones en errores
de dominio sanitizados con una decisión de retry observable.

### Acceptance Criteria (ACs)

- [ ] Todos los códigos y fallos de la matriz tienen clasificación determinista.
- [ ] Ningún modelo expuesto a UI contiene body HTTP, token, URL o mensaje crudo de excepción.
- [ ] `CancellationException` nunca se convierte en error de negocio.
- [ ] Los tests cubren transporte, timeout, 408, 429, 4xx relevantes y 5xx.
- [ ] `./gradlew ciVerify` pasa.
- [ ] Trazabilidad y agent report están actualizados.

---

# SCDK-M114. Completar expiración de sesión y navegación segura a login

## Main Story (How, I Want, To)

Como usuario autenticado, quiero volver de forma segura a login cuando mi sesión expire sin perder
datos locales ni quedar en una pantalla protegida.

## Context, Functional Description & Goal

El authenticator ya serializa refreshes concurrentes y limita el retry, pero falta una razón de
logout observable y una política completa para distinguir credenciales expiradas de indisponibilidad
transitoria.

## Steps/Scope

### In Scope

- Modelar logout manual, sesión expirada, refresh rechazado e integridad local no recuperable.
- Mantener un único refresh para varias respuestas 401 concurrentes.
- Finalizar la sesión local cuando no exista refresh token o refresh responda 400, 401 o 403.
- Mantener la sesión y devolver error retryable si refresh falla por transporte, timeout, 408, 429 o
  5xx.
- No interpretar un 403 ordinario de una operación protegida como expiración automática.
- Zeroizar KEK, borrar tokens y ejecutar limpieza local según `SPEC-VAULT-SYNC-V2`.
- Garantizar logout local aunque falle el logout remoto o la limpieza de Room.
- Vaciar el back stack protegido y establecer Login como raíz cuando expire la sesión.
- Mostrar, después del cierre seguro, un diálogo modal no sensible y de un solo uso que explique
  sesión caducada, renovación rechazada o fallo de integridad local.
- Añadir tests de concurrencia, refresh definitivo/transitorio, limpieza fallida y navegación.

### Out of Scope (if applies)

- Cambiar TTLs del backend.
- Reautenticación biométrica.
- Reintentar indefinidamente refresh.
- Conservar drafts después de logout.

## Additional Information and Configuration

- Specs: `SPEC-HARDENING-V1`, `SPEC-AUTH-CONTRACT`, `SPEC-VAULT-SYNC-V2`.
- Dependencias: `SCDK-M110`, `SCDK-M113`.
- Módulos: `core:network`, `core:auth`, `app`.
- Ejecutar tests de auth/network/app y `./gradlew ciVerify`.
- Actualizar trazabilidad y crear `docs/sdd/agent-reports/SCDK-M114.md`.

### API Contract and Expected Behavior (if applies)

Una operación protegida recibe 401, intenta como máximo un refresh efectivo y reintenta una vez con
el token nuevo. Un refresh rechazado termina la sesión; un fallo transitorio no lo hace.

### Acceptance Criteria (ACs)

- [ ] Varias respuestas 401 concurrentes provocan un solo refresh.
- [ ] Refresh 400/401/403 elimina sesión y contenido local conforme a la spec.
- [ ] Un timeout o 5xx de refresh conserva la sesión y queda clasificado como retryable.
- [ ] Tras expiración no puede volverse a una ruta protegida con Back.
- [ ] La UI muestra una sola vez el motivo sanitizado del cierre forzado y no muestra bodies,
  tokens ni detalles internos.
- [ ] Tests y `ciVerify` pasan.
- [ ] Trazabilidad y agent report están actualizados.

---

# SCDK-M115. Endurecer sync frente a red lenta, offline y respuestas perdidas

## Main Story (How, I Want, To)

Como usuario, quiero reintentar una sincronización interrumpida sin duplicar mutaciones, perder
drafts ni degradar el protocolo draft-first.

## Context, Functional Description & Goal

Sync v2 ya conserva `mutationId`, drafts y checkpoints, pero los errores retryables no están
expuestos de forma explícita y faltan pruebas de respuesta perdida.

## Steps/Scope

### In Scope

- Aplicar la clasificación de `SCDK-M113` a pull y push.
- Conservar draft, payload, base revision y `mutationId` ante transporte, timeout, 408, 429 o 5xx.
- Reutilizar exactamente el mismo `mutationId` al reintentar una mutación incierta.
- No recifrar, incrementar `payloadVersion` ni crear otra draft durante un retry.
- Mantener 400, 409 y 428 como fallos de integridad/protocolo no reintentables.
- Mantener 412 como conflicto editable y delete ya remoto como éxito semántico.
- Impedir ejecuciones concurrentes de sync.
- Conservar el checkpoint anterior hasta aplicar una página completa en una transacción.
- Añadir integración con MockWebServer para conexión lenta, desconexión antes de respuesta, replay
  idempotente, 429, 5xx y conflicto de protocolo.
- No añadir loops automáticos ni sleeps.

### Out of Scope (if applies)

- WorkManager o backoff periódico.
- Cambiar OpenAPI o backend.
- Merge semántico de secretos.

## Additional Information and Configuration

- Specs: `SPEC-HARDENING-V1`, `SPEC-VAULT-SYNC-V2`,
  `SPEC-OPENAPI-VAULT-ITEMS`.
- Dependencia: `SCDK-M113`.
- Módulos: `core:vault`, `core:network`, `core:storage`.
- Ejecutar tests aplicables y `./gradlew ciVerify`.
- Actualizar trazabilidad y crear `docs/sdd/agent-reports/SCDK-M115.md`.

### API Contract and Expected Behavior (if applies)

El retry conserva `Idempotency-Key` e `If-Match`. Solo una respuesta aceptada y persistida
atómicamente elimina la draft.

### Acceptance Criteria (ACs)

- [ ] Una respuesta perdida no crea otra mutación ni otro payload.
- [ ] El retry usa el mismo `mutationId`.
- [ ] Los drafts sobreviven a todos los fallos retryables.
- [ ] El checkpoint no avanza ante página inválida o fallo local.
- [ ] Los errores de protocolo no entran en retry automático.
- [ ] Tests y `ciVerify` pasan.
- [ ] Trazabilidad y agent report están actualizados.

---

# SCDK-M116. Recuperar la creación del vault después de una respuesta perdida

## Main Story (How, I Want, To)

Como usuario que crea su vault, quiero recuperar la misma recovery key si la red o el proceso fallan
durante la inicialización.

## Context, Functional Description & Goal

Actualmente un fallo después de que el backend acepte `POST /vault/keys` puede zeroizar la recovery
key generada y convertir el siguiente intento en `AlreadyInitialized`.

## Steps/Scope

### In Scope

- Introducir un registro cifrado de inicialización pendiente conforme a `ADR-0003`.
- Guardar antes del POST el material envuelto candidato, parámetros KDF, recovery key y estado de la
  operación.
- No persistir KEK ni MASTER_KEY en claro.
- Reutilizar el mismo material candidato al reintentar; no regenerarlo.
- Ante respuesta perdida, ejecutar `GET /vault/keys`.
- Comparar de forma segura el material remoto con el candidato:
  - si coincide, finalizar como éxito y entregar la recovery key original;
  - si responde 404, permitir repetir el POST con el mismo candidato;
  - si existe material distinto, descartar el candidato y tratar el vault como ya existente;
  - si la lectura también falla, conservar el intento y mostrar estado incierto/retryable.
- Serializar intentos concurrentes de inicialización.
- Cachear únicamente material confirmado.
- Mantener la recovery key pendiente hasta la confirmación explícita del usuario.
- Añadir tests de respuesta perdida, 404 posterior, 409 posterior, material diferente e interrupción
  del proceso.

### Out of Scope (if applies)

- Cambiar el endpoint backend.
- Añadir idempotency key a OpenAPI.
- Regenerar o rotar una recovery key ya aceptada.
- Mostrar todavía la UI final.

## Additional Information and Configuration

- Specs: `SPEC-HARDENING-V1`, `SPEC-CRYPTO-V1`,
  `SPEC-OPENAPI-VAULT-KEY-MATERIAL`.
- ADR: `ADR-0003-SENSITIVE-DATA-SURFACES`.
- Dependencias: `SCDK-M112`, `SCDK-M113`.
- Módulo: `core:vault`.
- Ejecutar `./gradlew :core:vault:testDebugUnitTest` y `./gradlew ciVerify`.
- Actualizar trazabilidad y crear `docs/sdd/agent-reports/SCDK-M116.md`.

### API Contract and Expected Behavior (if applies)

Se mantienen `GET /vault/keys` y `POST /vault/keys`. Un retry siempre reutiliza el mismo request
candidato hasta que el estado remoto pueda reconciliarse.

### Acceptance Criteria (ACs)

- [ ] Una desconexión tras aceptación remota conserva la recovery key original.
- [ ] Ningún retry genera una segunda KEK o recovery key.
- [ ] Un material remoto diferente nunca se sobrescribe.
- [ ] No se persiste KEK ni passphrase en claro.
- [ ] Todos los escenarios de reconciliación tienen tests deterministas.
- [ ] `ciVerify` pasa.
- [ ] Trazabilidad y agent report están actualizados.

---

# SCDK-M117. Modelar el bootstrap del vault como una state machine explícita

## Main Story (How, I Want, To)

Como usuario autenticado, quiero que la app distinga entre vault inexistente, bloqueado y
temporalmente inaccesible para no enviarme a una pantalla incorrecta.

## Context, Functional Description & Goal

`PostLoginGateRoute` convierte estados desconocidos en Unlock. Sin material local y sin red no puede
saber si debe crear o desbloquear el vault.

## Steps/Scope

### In Scope

- Sustituir el fallback de navegación por estados explícitos: loading, not initialized, locked,
  unlocked, retryable remote failure, material local corrupto/no soportado y authentication
  required.
- Con caché válida y red no disponible, permitir unlock offline.
- Sin caché y con fallo de red, mostrar error con Retry; nunca navegar a Create o Unlock por
  suposición.
- Tratar 404 confirmado como `NotInitialized`.
- Delegar unauthorized al lifecycle de sesión.
- Permitir refrescar material local corrupto desde backend sin eliminar items ni drafts.
- Establecer cada destino como raíz para impedir volver al estado anterior mediante Back.
- Añadir tests de resolución de destino para todas las combinaciones.
- Evitar flashes de contenido protegido durante la resolución.

### Out of Scope (if applies)

- Rediseño visual del splash.
- Crear el vault.
- Auto-lock.
- Cambiar contratos remotos.

## Additional Information and Configuration

- Specs: `SPEC-HARDENING-V1`, `SPEC-CRYPTO-V1`.
- Dependencias: `SCDK-M113`, `SCDK-M114`, `SCDK-M116`.
- Módulos: `core:vault`, `app`.
- Ejecutar tests de vault/app y `./gradlew ciVerify`.
- Actualizar trazabilidad y crear `docs/sdd/agent-reports/SCDK-M117.md`.

### API Contract and Expected Behavior (if applies)

Solo un 404 remoto confirmado conduce a Create Vault. Un fallo transitorio sin material local
mantiene al usuario en un estado recuperable con Retry.

### Acceptance Criteria (ACs)

- [ ] No existe un branch `else` que trate un estado desconocido como Unlock.
- [ ] Offline con caché válida permite desbloquear.
- [ ] Offline sin caché no crea un vault nuevo.
- [ ] Unauthorized limpia la navegación protegida.
- [ ] Todos los estados y destinos tienen tests.
- [ ] `ciVerify` pasa.
- [ ] Trazabilidad y agent report están actualizados.

---

# SCDK-M118. Completar loading, error y retry en Login y Signup

## Main Story (How, I Want, To)

Como usuario, quiero entender si un acceso falló por mis credenciales o por conectividad y poder
reintentarlo sin duplicar peticiones.

## Context, Functional Description & Goal

Login y Signup tienen un booleano de loading y errores genéricos, pero `feature:auth` no dispone de
tests JVM ni de estados retryables explícitos.

## Steps/Scope

### In Scope

- Modelar estados de operación explícitos en Login y Signup.
- Diferenciar validación local, credenciales inválidas, cuenta existente/prohibida, offline/timeout,
  servicio no disponible y error terminal inesperado.
- Mostrar Retry únicamente para fallos retryables.
- Evitar submits duplicados mientras una petición está activa.
- Mantener email durante un retry.
- No persistir passwords ni confirmaciones en saved state.
- Limpiar passwords después de éxito o rechazo definitivo.
- Mantenerlos solo en memoria durante un fallo transitorio reintentable.
- Añadir tests completos para ambos ViewModels, eventos únicos y retry.
- Mantener paridad inglés/español.

### Out of Scope (if applies)

- Rediseño visual de auth.
- Cambiar validaciones del backend.
- Password manager/autofill.
- Recuperación de contraseña de cuenta.

## Additional Information and Configuration

- Specs: `SPEC-HARDENING-V1`, `SPEC-AUTH-CONTRACT`.
- Dependencias: `SCDK-M113`, `SCDK-M114`.
- Módulo: `feature:auth`.
- Ejecutar `./gradlew :feature:auth:testDebugUnitTest` y `./gradlew ciVerify`.
- Actualizar trazabilidad y crear `docs/sdd/agent-reports/SCDK-M118.md`.

### API Contract and Expected Behavior (if applies)

Retry repite como máximo la operación solicitada por el usuario. No existe retry automático de
register o login.

### Acceptance Criteria (ACs)

- [ ] Login y Signup distinguen errores retryables y definitivos.
- [ ] Un doble tap no genera dos requests.
- [ ] Passwords no aparecen en saved state ni logs.
- [ ] Existen tests JVM de ambos ViewModels.
- [ ] Recursos ingleses y españoles mantienen paridad.
- [ ] `ciVerify` pasa.
- [ ] Trazabilidad y agent report están actualizados.

---

# SCDK-M119. Completar los estados de creación, desbloqueo y recovery key

## Main Story (How, I Want, To)

Como usuario, quiero completar el acceso al vault con feedback inequívoco y sin perder mi recovery
key ante fallos recuperables.

## Context, Functional Description & Goal

Create y Unlock mapean casi todos los errores a un mensaje genérico. Recovery muestra la clave sin
modelar carga, reconciliación o confirmación de borrado del estado pendiente.

## Steps/Scope

### In Scope

- Aplicar estados explícitos de loading, success, retryable error y terminal error.
- Integrar Create Vault con la reconciliación de `SCDK-M116`.
- En estado remoto incierto, ofrecer Retry sin regenerar material.
- En Unlock distinguir passphrase inválida, material local no disponible, material corrupto y vault
  bloqueado durante la operación.
- Exigir confirmación explícita `I saved it` antes de eliminar la recovery key pendiente.
- Impedir continuar si la recovery key no está disponible.
- Mantener las acciones deshabilitadas durante operaciones activas.
- Añadir tests de ViewModels y eventos de navegación únicos.
- Mover todo el copy a recursos y mantener inglés/español.

### Out of Scope (if applies)

- Recuperación mediante recovery key.
- Rotación de recovery key.
- Rediseño visual.
- Biometría.

## Additional Information and Configuration

- Specs: `SPEC-HARDENING-V1`, `SPEC-CRYPTO-V1`.
- ADR: `ADR-0003-SENSITIVE-DATA-SURFACES`.
- Dependencias: `SCDK-M116`, `SCDK-M117`.
- Módulo: `feature:vault`.
- Ejecutar tests de feature vault y `./gradlew ciVerify`.
- Actualizar trazabilidad y crear `docs/sdd/agent-reports/SCDK-M119.md`.

### API Contract and Expected Behavior (if applies)

Confirmar la recovery key elimina el registro transitorio. Retry de inicialización reutiliza el
mismo candidato.

### Acceptance Criteria (ACs)

- [ ] Create, Unlock y Recovery tienen estados observables y testeados.
- [ ] Retry de creación no cambia la recovery key.
- [ ] Passphrase incorrecta se diferencia de material corrupto.
- [ ] No se puede omitir la confirmación de recovery key.
- [ ] No quedan strings hardcodeadas en estas pantallas.
- [ ] `ciVerify` pasa.
- [ ] Trazabilidad y agent report están actualizados.

---

# SCDK-M120. Completar estados de Vault Home y recuperación manual de sync

## Main Story (How, I Want, To)

Como usuario, quiero distinguir un vault vacío de uno todavía cargando y seguir viendo mis datos
locales cuando sync falle.

## Context, Functional Description & Goal

`VaultHomeUiState` comienza con una lista vacía, por lo que la UI puede mostrar un vacío falso. Los
errores de sync se reducen actualmente a Push Failed o Pull Failed.

## Steps/Scope

### In Scope

- Modelar carga inicial de Room por separado de lista vacía.
- Mostrar Empty únicamente después de la primera lectura local correcta.
- Modelar error de lectura local sin confundirlo con Empty.
- Mantener items locales visibles durante sync y después de un fallo remoto.
- Mostrar categorías sanitizadas: offline/timeout, servicio no disponible, sesión requerida,
  conflicto, protocolo/integridad y storage/crypto.
- Mostrar Retry solo para fallos retryables.
- Deshabilitar Sync mientras ya existe una ejecución.
- Conservar contadores parciales cuando una fase posterior falla.
- No iniciar loops de retry por recomposición.
- Añadir tests de ViewModel para loading, empty, content, error, retry y concurrencia.
- Mantener inglés/español.

### Out of Scope (if applies)

- Rediseño del listado.
- Background sync.
- Banners de observabilidad.
- Resolver conflictos automáticamente.

## Additional Information and Configuration

- Specs: `SPEC-HARDENING-V1`, `SPEC-VAULT-SYNC-V2`.
- Dependencia: `SCDK-M115`.
- Módulo: `feature:vault`.
- Ejecutar `./gradlew :feature:vault:testDebugUnitTest` y `./gradlew ciVerify`.
- Actualizar trazabilidad y crear `docs/sdd/agent-reports/SCDK-M120.md`.

### API Contract and Expected Behavior (if applies)

Room continúa siendo source of truth. Un error remoto modifica únicamente el estado de sync, no la
lista local visible.

### Acceptance Criteria (ACs)

- [ ] No se muestra Empty antes de recibir la primera emisión de Room.
- [ ] Un fallo de sync no borra ni oculta items locales.
- [ ] Retry solo aparece para errores clasificados como retryable.
- [ ] No pueden coexistir dos syncs.
- [ ] Tests de todos los estados pasan.
- [ ] `ciVerify` pasa.
- [ ] Trazabilidad y agent report están actualizados.

---

# SCDK-M121. Endurecer los editores ante vault bloqueado y payload corrupto

## Main Story (How, I Want, To)

Como usuario, quiero que un item corrupto o un cambio de estado del vault falle de forma segura sin
mostrar plaintext parcial ni destruir datos.

## Context, Functional Description & Goal

Los editores de password y note mezclan loading, contenido y error. Un payload corrupto termina en
un mensaje genérico y los campos pueden permanecer en memoria al bloquearse el vault.

## Steps/Scope

### In Scope

- Introducir estados explícitos compartidos: loading, editable content, saving, not found, vault
  locked, corrupted payload, estado official/draft inconsistente y local storage failure.
- Ante bloqueo, cancelar observaciones y mutaciones dependientes de la KEK, limpiar campos secretos,
  impedir save/delete/publish y navegar a Unlock.
- Ante payload corrupto, no devolver plaintext parcial, conservar official y draft cifrados,
  impedir sobrescritura o borrado accidental y permitir Back y Retry de lectura.
- No avanzar checkpoint si la corrupción procede de una página remota no aplicable.
- Evitar que una emisión tardía sobrescriba cambios locales no guardados.
- Aplicar el comportamiento a passwords y notes mediante abstracciones compartidas cuando resulte
  natural.
- Añadir tests de ambos ViewModels y de crypto/storage involucrado.

### Out of Scope (if applies)

- Reparar o migrar un payload corrupto.
- Eliminar automáticamente el item.
- Rediseñar los formularios.
- Merge semántico de conflictos.

## Additional Information and Configuration

- Specs: `SPEC-HARDENING-V1`, `SPEC-CRYPTO-V1`,
  `SPEC-SECURE-ITEM-PAYLOAD-V1`, `SPEC-VAULT-SYNC-V2`.
- Dependencias: `SCDK-M115`, `SCDK-M120`.
- Módulos: `core:vault`, `feature:vault`.
- Ejecutar tests de ambos módulos y `./gradlew ciVerify`.
- Actualizar trazabilidad y crear `docs/sdd/agent-reports/SCDK-M121.md`.

### API Contract and Expected Behavior (if applies)

Una autenticación AEAD fallida produce `CorruptedPayload`, nunca contenido parcial. El registro
cifrado permanece intacto.

### Acceptance Criteria (ACs)

- [ ] Los editores nunca renderizan plaintext tras bloquear el vault.
- [ ] Payload corrupto no puede sobrescribirse ni borrarse desde el estado de error.
- [ ] No se pierde una draft por un fallo de lectura.
- [ ] Password y Note cubren los mismos estados.
- [ ] Los tests incluyen bloqueo durante carga y durante save.
- [ ] `ciVerify` pasa.
- [ ] Trazabilidad y agent report están actualizados.

---

# SCDK-M122. Implementar auto-lock configurable y Lock now

## Main Story (How, I Want, To)

Como usuario, quiero que mi vault se bloquee tras el periodo configurado en background y poder
bloquearlo inmediatamente desde Settings.

## Context, Functional Description & Goal

`VaultSessionManager.lock()` ya zeroiza la KEK, pero no existe coordinador de lifecycle ni
preferencia de timeout.

## Steps/Scope

### In Scope

- Implementar la política exacta de `ADR-0001`.
- Crear un repositorio de preferencias para el timeout no sensible.
- Usar lifecycle de proceso y reloj monotónico inyectable.
- Programar/cancelar el bloqueo sin depender de una Activity concreta.
- Aplicar default `Immediately` y exponer las cinco opciones aprobadas en Settings.
- Añadir acción `Lock now`.
- Al bloquear, zeroizar KEK, actualizar `VaultState`, cancelar operaciones protegidas y establecer
  Unlock como raíz.
- No cerrar la sesión autenticada ni eliminar datos locales.
- Añadir tests deterministas con reloj y scheduler falsos.
- Añadir test de recreación de Activity que no active un bloqueo falso.

### Out of Scope (if applies)

- Biometría.
- Opción Never.
- Logout por inactividad.
- AlarmManager o WorkManager.

## Additional Information and Configuration

- Spec: `SPEC-HARDENING-V1`, requisito `SEC-SESSION-001`.
- ADR: `ADR-0001-VAULT-AUTO-LOCK`.
- Dependencias: `SCDK-M110`, `SCDK-M117`, `SCDK-M121`.
- Módulos: `app`, `core:vault`, `feature:vault`.
- Ejecutar tests aplicables y `./gradlew ciVerify`.
- Actualizar trazabilidad y crear `docs/sdd/agent-reports/SCDK-M122.md`.

### API Contract and Expected Behavior (if applies)

La preferencia almacena uno de los timeouts aprobados. Valores desconocidos vuelven de forma segura
a `Immediately`.

### Acceptance Criteria (ACs)

- [ ] La configuración por defecto bloquea al entrar en background.
- [ ] Cada timeout aprobado funciona con reloj monotónico.
- [ ] No existe opción Never.
- [ ] Lock now zeroiza la KEK y conserva la sesión.
- [ ] Config changes no se interpretan como inactividad.
- [ ] Tests y `ciVerify` pasan.
- [ ] Trazabilidad y agent report están actualizados.

---

# SCDK-M123. Implementar el cambio de passphrase mediante rewrap de KEK

## Main Story (How, I Want, To)

Como usuario con el vault desbloqueado, quiero cambiar mi passphrase conservando todos mis secretos
y mi recovery key.

## Context, Functional Description & Goal

El repositorio remoto ya expone `updateMasterWrappedKek`, pero no existe un caso de uso que
verifique la passphrase actual y coordine actualización remota y caché local.

## Steps/Scope

### In Scope

- Crear un caso de uso de cambio de passphrase en `core:vault`.
- Requerir vault desbloqueado y material local válido.
- Verificar la passphrase actual conforme a `ADR-0002`.
- Derivar la nueva MASTER_KEY con salt y parámetros KDF existentes.
- Reenvolver exactamente la misma KEK con nonce nuevo.
- Ejecutar `PUT /vault/keys/master`.
- Actualizar solo `kekEncMaster` en la caché confirmada.
- Reconciliar respuestas perdidas mediante `GET /vault/keys`.
- Si el resultado queda indeterminado, borrar material maestro cacheado, zeroizar KEK y claves
  derivadas, bloquear el vault y devolver error retryable que exige conexión.
- Definir resultados tipados y sanitizados.
- Verificar mediante mocks estrictos que no se accede a repositorios de items/drafts.
- Añadir tests de éxito, credencial actual inválida, 401, 5xx, respuesta perdida y reconciliación
  incierta.

### Out of Scope (if applies)

- UI.
- Rotación de KEK o recovery key.
- Recifrado de payloads.
- Cambio de parámetros Argon2.

## Additional Information and Configuration

- Specs: `SPEC-HARDENING-V1`, `SPEC-CRYPTO-V1`,
  `SPEC-SECURE-ITEM-PAYLOAD-V1`.
- ADR: `ADR-0002-PASSPHRASE-REWRAP`.
- Dependencias: `SCDK-M111`, `SCDK-M113`, `SCDK-M122`.
- Módulo: `core:vault`.
- Ejecutar tests de crypto/vault y `./gradlew ciVerify`.
- Actualizar trazabilidad y crear `docs/sdd/agent-reports/SCDK-M123.md`.

### API Contract and Expected Behavior (if applies)

Solo cambia `kekEncMaster`. `kekEncRecovery` y todo registro SecureItem permanecen byte-for-byte
idénticos.

### Acceptance Criteria (ACs)

- [ ] La passphrase actual incorrecta no produce request remoto.
- [ ] Un éxito cambia exclusivamente `kekEncMaster`.
- [ ] Una respuesta perdida se reconcilia antes de declarar éxito.
- [ ] Un estado incierto bloquea el vault sin borrar items.
- [ ] Tests verifican cero interacción con items y drafts.
- [ ] Material temporal se zeroiza best-effort.
- [ ] `ciVerify` pasa.
- [ ] Trazabilidad y agent report están actualizados.

---

# SCDK-M124. Añadir el flujo de cambio de passphrase en Settings

## Main Story (How, I Want, To)

Como usuario, quiero cambiar mi passphrase desde Settings con validación y feedback seguro.

## Context, Functional Description & Goal

Settings contiene actualmente texto dummy y no ofrece acceso al endpoint de rewrap.

## Steps/Scope

### In Scope

- Añadir acción `Change passphrase` en Settings.
- Crear pantalla o diálogo dedicado con passphrase actual, nueva passphrase y confirmación.
- Exigir campos no vacíos, coincidencia y que la nueva passphrase sea distinta.
- Integrar el caso de uso de `SCDK-M123`.
- Distinguir credencial actual inválida, error retryable, sesión requerida y resultado incierto.
- Deshabilitar submit durante la operación.
- Limpiar los tres campos después de éxito, cancelación o error definitivo.
- Ante éxito, mantener el vault desbloqueado porque la KEK no cambia.
- Ante resultado incierto, navegar a Unlock mediante el estado global.
- Añadir tests del ViewModel y navegación.
- Mantener recursos inglés/español.

### Out of Scope (if applies)

- Indicador avanzado de fortaleza.
- Recovery de passphrase olvidada.
- Rotación de recovery key.
- Rediseño integral de Settings.

## Additional Information and Configuration

- Spec: `SPEC-HARDENING-V1`, requisito `SEC-CRYPTO-002`.
- ADR: `ADR-0002-PASSPHRASE-REWRAP`.
- Dependencia: `SCDK-M123`.
- Módulos: `feature:vault`, `app`.
- Ejecutar tests aplicables y `./gradlew ciVerify`.
- Actualizar trazabilidad y crear `docs/sdd/agent-reports/SCDK-M124.md`.

### API Contract and Expected Behavior (if applies)

La pantalla no recibe ni muestra material de claves. Solo entrega passphrases al caso de uso y
consume un resultado tipado.

### Acceptance Criteria (ACs)

- [ ] El flujo solo está disponible con el vault desbloqueado.
- [ ] Validación local evita requests inválidos.
- [ ] Success mantiene acceso a los mismos items sin recifrarlos.
- [ ] El estado incierto termina en vault bloqueado.
- [ ] Los campos sensibles se limpian y no se guardan en saved state.
- [ ] Tests, recursos y `ciVerify` pasan.
- [ ] Trazabilidad y agent report están actualizados.

---

# SCDK-M125. Recuperar navegación y recovery key de forma segura tras process death

## Main Story (How, I Want, To)

Como usuario, quiero que un reinicio del proceso vuelva a un estado seguro sin perder una recovery
key pendiente ni abrir directamente contenido protegido.

## Context, Functional Description & Goal

La recovery key está actualmente en `rememberSaveable` y el back stack puede restaurar rutas del
vault aunque la KEK ya no exista.

## Steps/Scope

### In Scope

- Eliminar la recovery key de `rememberSaveable`, Bundle, rutas y parámetros serializables.
- Consumir el registro transitorio cifrado creado en `SCDK-M116`.
- Después de process death, considerar siempre el vault bloqueado, resolver primero sesión,
  inicialización pendiente y material de keys e ignorar rutas incompatibles.
- Si existe recovery key pendiente confirmada, volver a Recovery antes de Unlock.
- Eliminar el registro después de la confirmación del usuario o logout.
- No persistir plaintext de editores; documentar que cambios no guardados en pantalla se descartan
  tras process death.
- Mantener drafts ya guardadas en Room.
- Añadir tests de Activity recreation, process-state recreation y back stack protegido.
- Verificar que cold start no muestra un frame de Vault antes de Unlock.

### Out of Scope (if applies)

- Restaurar formularios con plaintext no guardado.
- Background sync.
- Rotar una recovery key perdida antes de esta mejora.
- Cambiar Navigation3.

## Additional Information and Configuration

- Specs: `SPEC-HARDENING-V1`, `SPEC-CRYPTO-V1`.
- ADRs: `ADR-0001-VAULT-AUTO-LOCK`, `ADR-0003-SENSITIVE-DATA-SURFACES`.
- Dependencias: `SCDK-M116`, `SCDK-M117`, `SCDK-M119`, `SCDK-M122`.
- Módulos: `app`, `core:vault`, `feature:vault`.
- Ejecutar tests JVM/instrumentados aplicables y `./gradlew ciVerify`.
- Actualizar trazabilidad y crear `docs/sdd/agent-reports/SCDK-M125.md`.

### API Contract and Expected Behavior (if applies)

El estado restaurable solo contiene identificadores y preferencias no sensibles. La recovery key se
obtiene exclusivamente del almacén transitorio cifrado.

Tras process death, los ViewModels de los editores no se restauran con `SavedStateHandle` ni
`rememberSaveable`; por diseño, los cambios no guardados en pantalla se descartan. Los drafts que
ya se guardaron permanecen en Room y se recuperan mediante el flujo normal de unlock y sync.

### Acceptance Criteria (ACs)

- [ ] `rememberSaveable`, rutas y Bundle no contienen recovery key ni plaintext.
- [ ] Process death siempre conduce a un estado bloqueado antes de renderizar contenido.
- [ ] Una recovery key pendiente continúa disponible después de recrear el proceso.
- [ ] Confirmación y logout eliminan el estado transitorio.
- [ ] Los tests cubren cold start y back stack restaurado.
- [ ] `ciVerify` pasa.
- [ ] Trazabilidad y agent report están actualizados.

---

# SCDK-M126. Desactivar backup y auditar el almacenamiento local de producción

## Main Story (How, I Want, To)

Como usuario, quiero que tokens, material criptográfico y datos del vault no salgan del dispositivo
mediante backup o device transfer.

## Context, Functional Description & Goal

El manifest declara `allowBackup=true` y los XML de backup/data extraction siguen siendo plantillas.

## Steps/Scope

### In Scope

- Establecer `android:allowBackup="false"`.
- Sustituir reglas de plantilla por exclusiones explícitas para cloud backup y device transfer.
- Revisar el manifest merge de release y todos los módulos.
- Verificar que solo la launcher Activity tenga exposición externa necesaria.
- Documentar el inventario local: Room con payloads cifrados y metadata permitida, tokens en
  almacenamiento cifrado, key material envuelto, preferencias no sensibles y recovery transitoria.
- Añadir una verificación automatizada del manifest de release.
- Añadir test que demuestre que una limpieza de sesión elimina tokens, material de keys, officials,
  drafts y checkpoints según contrato.
- Eliminar comentarios y TODOs de las reglas Android.

### Out of Scope (if applies)

- SQLCipher.
- Cambiar schema Room.
- Migración destructiva.
- Backup manual/export de vault.

## Additional Information and Configuration

- Specs: `SPEC-HARDENING-V1`, `SPEC-STORAGE`, `SPEC-VAULT-SYNC-V2`.
- ADR: `ADR-0003-SENSITIVE-DATA-SURFACES`.
- Dependencias: `SCDK-M112`, `SCDK-M125`.
- Módulos: `app`, `core:auth`, `core:storage`, `core:vault`.
- Ejecutar manifest checks, tests de limpieza y `./gradlew releaseVerify`.
- Actualizar trazabilidad y crear `docs/sdd/agent-reports/SCDK-M126.md`.

### API Contract and Expected Behavior (if applies)

Ni Android cloud backup ni device-to-device transfer pueden exportar datos de SafeCube.

### Acceptance Criteria (ACs)

- [ ] El manifest release efectivo declara backup deshabilitado.
- [ ] Las reglas no contienen plantillas ni TODOs.
- [ ] El inventario de persistencia identifica contenido, protección y borrado.
- [ ] No se añade fallback destructivo de Room.
- [ ] Los tests de limpieza de sesión pasan.
- [ ] `releaseVerify` pasa.
- [ ] Trazabilidad y agent report están actualizados.

---

# SCDK-M127. Proteger screenshots, campos secretos y clipboard

## Main Story (How, I Want, To)

Como usuario, quiero que mis credenciales y secretos no aparezcan en capturas, recents o clipboard
gestionado por la app.

## Context, Functional Description & Goal

La Activity no usa `FLAG_SECURE` y varios campos de password/passphrase se renderizan como texto
ordinario.

## Steps/Scope

### In Scope

- Aplicar `FLAG_SECURE` a `MainActivity`.
- Verificar protección de screenshots y thumbnail de recents.
- Aplicar transformación de password a Login, Signup, confirmación, Create Vault, Unlock Vault,
  Change Passphrase y password del editor.
- No transformar campos que no sean secretos.
- No añadir acciones programáticas de copy.
- Auditar que el código propio no escribe recovery key, passphrase o password en clipboard.
- Añadir tests Compose/Activity sobre campos protegidos y flags.
- Mantener semántica accesible sin exponer el valor como label o content description.
- Mantener paridad inglés/español.

### Out of Scope (if applies)

- Copy-to-clipboard seguro.
- Biometría.
- Rediseño de formularios.
- Bloquear herramientas de accesibilidad del sistema.

## Additional Information and Configuration

- Spec: `SPEC-HARDENING-V1`, requisito `SEC-PRIVACY-001`.
- ADR: `ADR-0003-SENSITIVE-DATA-SURFACES`.
- Dependencias: `SCDK-M112`, `SCDK-M124`.
- Módulos: `app`, `feature:auth`, `feature:vault`.
- Ejecutar tests Compose/instrumentados y `./gradlew ciVerify`.
- Actualizar trazabilidad y crear `docs/sdd/agent-reports/SCDK-M127.md`.

### API Contract and Expected Behavior (if applies)

Android debe rechazar screenshots de la ventana de SafeCube. Los campos secretos muestran
caracteres ocultos sin cambiar su valor de dominio.

### Acceptance Criteria (ACs)

- [x] `FLAG_SECURE` está presente en la ventana activa; `MainActivitySmokeTest` lo verifica en la ventana activa.
- [x] Todos los campos secretos enumerados usan `SecretOutlinedTextField`, que impone el enmascarado; `SensitiveSourcePolicyTest` cubre las seis pantallas.
- [x] No existen escrituras programáticas de secretos al clipboard; `SensitiveSourcePolicyTest` audita el código propio de todos los módulos Android.
- [x] Tests y auditoría no imprimen valores sensibles; las pruebas usan valores sintéticos y no registran su contenido.
- [x] `ciVerify` pasa.
- [x] Trazabilidad y agent report están actualizados.

---

# SCDK-M128. Eliminar logging sensible y validar la configuración R8

> Nota normativa de 2026-08-28: el owner mantiene temporalmente logging HTTP `BODY` solo en builds
> locales debug hasta la Fase 9. El resultado histórico de M128 sigue vigente para release,
> benchmark, errores mostrables y R8. La Fase 9 debe retirar definitivamente el logger debug y
> sustituirlo por observabilidad estructurada y redactada antes de integrar telemetría.

## Main Story (How, I Want, To)

Como security owner, quiero que ninguna build registre tráfico sensible y que la minificación
mantenga solo las reglas estrictamente necesarias.

## Context, Functional Description & Goal

`NetworkClientFactory` activa `HttpLoggingInterceptor.Level.BODY` en debug. `proguard-rules.pro`
mantiene una configuración casi de plantilla y no existe auditoría funcional del APK minificado.

## Steps/Scope

### In Scope

- Eliminar logging de bodies y headers HTTP en release y benchmark. La decisión posterior del
  owner conserva debug hasta la Fase 9.
- Mantener el interceptor exclusivamente en debug hasta Fase 9; release y benchmark no lo instalan.
- Eliminar cuerpos HTTP y mensajes crudos de excepciones de errores persistentes o mostrables.
- Añadir tests que inspeccionen clientes debug y release.
- Auditar `proguard-rules.pro` y eliminar comentarios/reglas de plantilla no aplicables.
- Mantener únicamente reglas necesarias y justificar cada excepción.
- Verificar serialización OpenAPI/Kotlin, Hilt, Room y crypto con build minificada.
- Ejecutar `releaseVerify` y ensamblar el APK release.
- Añadir un smoke test sobre variante minificada/benchmark si es necesario para demostrar arranque,
  DI y serialización.
- No imprimir rutas, cuerpos, tokens ni payloads durante la verificación.

### Out of Scope (if applies)

- Telemetría o crash reporting.
- Publicar mapping files.
- Certificate pinning sin ADR específico.
- Cambiar BASE_URL o contratos OpenAPI.

## Additional Information and Configuration

- Spec: `SPEC-HARDENING-V1`, requisito `SEC-PRIVACY-001`.
- ADR: `ADR-0003-SENSITIVE-DATA-SURFACES`.
- Dependencias: `SCDK-M112`, `SCDK-M113`.
- Módulos: `core:network`, `app`.
- Ejecutar tests de network, `./gradlew releaseVerify` y `:app:assembleRelease`.
- Actualizar trazabilidad y crear `docs/sdd/agent-reports/SCDK-M128.md`.

### API Contract and Expected Behavior (if applies)

Clientes debug y release realizan las mismas llamadas. Release no registra tráfico; por decisión
posterior del owner, debug conserva temporalmente request/response completos hasta la Fase 9.

### Acceptance Criteria (ACs)

- [x] Release y benchmark no usan logging BODY o HEADERS; el cliente OpenAPI generado no incorpora
  un logger autónomo.
- [x] La excepción debug está centralizada en `NetworkClientFactory`, condicionada por
  `BuildConfig.DEBUG` y asignada para retirada definitiva a la Fase 9.
- [x] Los modelos mostrables no contienen bodies o mensajes crudos; los errores remotos conservan solo clasificación, código y campos de validación.
- [x] Todas las reglas R8 adicionales tienen justificación concreta; solo se conserva el `-dontwarn` acotado requerido por anotaciones de Tink/Error Prone.
- [x] El APK release y la variante benchmark minificadas compilan mediante `:app:assembleRelease` y `:app:assembleBenchmark`.
- [x] `releaseVerify` pasa.
- [x] Trazabilidad y agent report están actualizados.

---

# SCDK-M129. Retirar pantallas y rutas placeholder fuera del alcance de v1

## Main Story (How, I Want, To)

Como usuario de la beta, quiero que todas las opciones visibles sean funcionales y no conduzcan a
pantallas dummy.

## Context, Functional Description & Goal

El roadmap excluye carpetas reales y perfil enriquecido. Actualmente existen tab de Folders,
pantalla Profile, texto dummy en Settings y rutas App/Error sin comportamiento de producto.

## Steps/Scope

### In Scope

- Eliminar el tab y la pantalla `VaultFolders`.
- Eliminar la entrada Profile y el módulo `feature:profile` si queda sin responsabilidad real.
- Retirar imports, dependencias Gradle, rutas y back policies asociados.
- Eliminar rutas muertas como `Routes.App` y `Routes.Error` si no tienen consumidor real.
- Sustituir el contenido dummy de Settings por auto-lock, Lock now, cambio de passphrase y logout.
- Eliminar el TODO obsoleto de splash.
- Auditar textos visibles para `dummy`, `placeholder`, `coming soon` y equivalentes.
- Mantener recursos inglés/español.
- Actualizar tests de navegación y smoke test.

### Out of Scope (if applies)

- Implementar carpetas o perfil enriquecido.
- Añadir búsqueda o biometría.
- Rediseñar Settings.

## Additional Information and Configuration

- Specs: `SPEC-HARDENING-V1`, `SPEC-PRODUCT-V1`.
- Dependencias: `SCDK-M122`, `SCDK-M124`.
- Módulos: `app`, `feature:vault`, `feature:profile`, `settings.gradle.kts`.
- Ejecutar `rg` de placeholders, tests de navegación, lint y `./gradlew ciVerify`.
- Actualizar trazabilidad y crear `docs/sdd/agent-reports/SCDK-M129.md`.

### API Contract and Expected Behavior (if applies)

La navegación visible de v1 contiene Welcome/Auth, Vault, editores, Recovery, Unlock y Settings
funcional. No expone rutas de features aplazadas.

### Acceptance Criteria (ACs)

- [ ] No existe acceso visible a Folders o Profile.
- [ ] `feature:profile` se elimina si no conserva responsabilidades.
- [ ] Settings no contiene contenido dummy.
- [ ] No quedan rutas placeholder o sin comportamiento.
- [ ] Inglés y español conservan paridad.
- [ ] Tests, lint y `ciVerify` pasan.
- [ ] Trazabilidad y agent report están actualizados.

---

# SCDK-M131. Implementar quick unlock del vault con Android Keystore

## Main Story (How, I Want, To)

Como usuario, quiero desbloquear localmente mi vault con la seguridad del dispositivo para no
introducir la passphrase en cada apertura sin mantener la KEK activa en memoria.

## Context, Functional Description & Goal

Auto-lock y process death dejan el vault Locked. El MVP necesita un único quick unlock que use
biometría fuerte cuando esté disponible, permita la credencial segura del dispositivo cuando el
sensor no exista o falle y conserve passphrase como fallback.

## Steps/Scope

### In Scope

- Implementar el contrato `SEC-SESSION-002` y la sección de quick unlock de `ADR-0001`.
- Crear una abstracción de quick unlock en `core:vault` y un adapter Android Keystore testeable.
- Generar una clave no exportable por cuenta con autorización por uso mediante biometría fuerte o
  credencial segura del dispositivo.
- Persistir únicamente un envelope autenticado y versionado con la KEK envuelta, aislado por cuenta
  y excluido de backup, device transfer y sync.
- Enrolar solo tras unlock por passphrase y consentimiento explícito.
- Integrar un único prompt del sistema en Unlock y ofrecer acción explícita para usar passphrase.
- Mantener Locked ante cancelación, fallo, invalidación o corrupción, y permitir re-enrolar después
  de passphrase.
- Eliminar alias y envelope en logout, cambio o eliminación local de cuenta; conservarlos en
  auto-lock, Lock now y process death.
- Añadir Settings para activar/desactivar quick unlock y explicar el alcance de la credencial del
  dispositivo.
- Añadir tests JVM con adapter falso y tests instrumentados de prompt, lifecycle y cleanup.

### Out of Scope (if applies)

- PIN propio de SafeCube.
- Login o refresh de cuenta mediante biometría.
- Sincronizar el enrolamiento entre dispositivos.
- Sustituir o recuperar la passphrase.
- Modo biométrico estricto separado de la credencial segura del dispositivo.

## Additional Information and Configuration

- Spec: `SPEC-HARDENING-V1`, requisito `SEC-SESSION-002`.
- ADR: `ADR-0001-VAULT-AUTO-LOCK` ACCEPTED.
- Dependencias: `SCDK-M110`, `SCDK-M119`, `SCDK-M122`.
- Módulos esperados: `app`, `core:vault`, `core:storage`, `feature:vault`.
- Ejecutar tests JVM/instrumentados aplicables y `./gradlew ciVerify`.
- Actualizar trazabilidad y crear `docs/sdd/agent-reports/SCDK-M131.md`.

### API Contract and Expected Behavior (if applies)

No cambia APIs remotas. Quick unlock solo autoriza un unwrap local; la account session debe seguir
válida y cada process death comienza en Locked.

### Acceptance Criteria (ACs)

- [ ] La KEK y passphrase nunca se persisten en claro; el envelope está autenticado y excluido de
  backup/transfer.
- [ ] Biometría fuerte y credencial segura del dispositivo son alternativas de un único prompt.
- [ ] Un dispositivo incompatible usa passphrase y no queda bloqueado fuera del vault.
- [ ] Cancelación, invalidación y corrupción fallan en cerrado y permiten recuperación por
  passphrase.
- [ ] Logout y cambio de cuenta destruyen el enrolamiento; auto-lock y process death solo bloquean.
- [ ] No existe PIN propio ni biometría usada como login backend.
- [ ] Tests, `ciVerify`, trazabilidad y agent report pasan y están actualizados.

---

# SCDK-M132. Evitar la sobrescritura concurrente del cambio de passphrase entre dispositivos

## Bug Summary

Dos dispositivos pueden cambiar simultáneamente la passphrase del mismo vault usando la misma
passphrase base y recibir ambos una respuesta de éxito. El segundo cambio sobrescribe al primero
sin que el cliente detecte que trabajaba con una versión obsoleta del wrapper maestro.

## Context

- Entorno: dev/test, prueba manual con dos dispositivos autenticados contra el mismo backend.
- D1 y D2 desbloquean el vault con `aaa` y abren simultáneamente el flujo de cambio de passphrase.
- D1 cambia `aaa` a `bbb`; D2 cambia después `aaa` a `ccc`.
- El cliente actual se basa en `SCDK-M123`/`SCDK-M124` y ejecuta `PUT /vault/keys/master` sin una
  precondición de versión o mecanismo equivalente definido por el backend.
- El comportamiento correcto queda pendiente de la propuesta de contrato del backend; esta tarea
  registra el bug y fija la coordinación necesaria para su resolución.

## Expected Behavior

El backend debe aceptar como máximo una actualización para una misma versión base del material
maestro. Si otro cliente ya modificó el wrapper, el intento concurrente debe rechazarse de forma
tipada y el cliente no debe mostrar éxito ni conservar una passphrase local que no corresponda con
el estado remoto.

El cliente debe poder reconciliar un conflicto o un estado indeterminado de forma segura, bloquear
el vault cuando no pueda establecer el estado remoto y preservar `kekEncRecovery`, items, drafts y
checkpoints sin recifrarlos ni eliminarlos.

## Actual Behavior

Ambos dispositivos reciben éxito. D1 queda configurado localmente para `bbb` y D2 para `ccc`, aunque
solo una de esas passphrases puede abrir el wrapper remoto que prevaleció. El último `PUT` gana y no
existe una señal de conflicto que permita al cliente invalidar el estado obsoleto.

## Steps to Reproduce

1. En D1, inicializar el vault con passphrase `aaa` y dejarlo desbloqueado.
2. En D2, iniciar sesión y desbloquear el mismo vault con `aaa`.
3. En ambos dispositivos, abrir simultáneamente Settings → Change passphrase.
4. En D1, cambiar `aaa` por `bbb` y confirmar que aparece el mensaje de éxito.
5. Inmediatamente después, en D2, cambiar `aaa` por `ccc` y confirmar que también aparece el
   mensaje de éxito.
6. Comprobar que D1 y D2 conservan passphrases distintas para el mismo vault y que uno de los
   clientes ya no puede desbloquearlo con su passphrase local.

## Impact

- Afecta a usuarios que utilizan el mismo vault desde más de un dispositivo o sesión.
- Severidad alta y bloqueante para cerrar el cambio de passphrase en v1: puede dejar al usuario
  con una credencial local inválida y feedback incorrecto.
- No se ha observado pérdida de items, drafts, recovery key ni datos locales.
- Workaround temporal: no cambiar la passphrase desde dos dispositivos hasta disponer del contrato
  de concurrencia del backend.

## Scope

### In Scope

- Acordar con backend una precondición de versión, `ETag`/`If-Match`, mutation idempotente o
  mecanismo equivalente para `PUT /vault/keys/master`.
- Actualizar el contrato OpenAPI y regenerar/adaptar el cliente de red.
- Exponer un resultado tipado para conflicto por versión obsoleta y distinguirlo de 401, 5xx y
  respuesta perdida.
- Reconciliar el estado remoto antes de declarar éxito o bloquear de forma segura cuando el estado
  quede indeterminado.
- Garantizar que solo cambia `kekEncMaster` y que `kekEncRecovery`, items, drafts y checkpoints
  permanecen byte-for-byte idénticos.
- Añadir tests de dos clientes con la misma versión base, ganador único, cliente perdedor y
  recuperación tras conflicto/respuesta perdida.
- Actualizar specs, ADR, trazabilidad, documentación y `ciVerify` cuando el contrato esté aprobado.

### Out of Scope

- Cambio de passphrase mediante cliente web o enlace enviado por email.
- Confirmación de email, recuperación de passphrase olvidada o recuperación sin recovery key.
- Rotación de KEK o recovery key.
- Recifrado de payloads SecureItem.
- Logout global o invalidación de todas las sesiones autenticadas.

## Root Cause Hypothesis (optional)

El cambio de passphrase se valida contra una caché local que puede estar obsoleta y el endpoint
remoto acepta actualizaciones incondicionales. Sin una condición atómica sobre la versión del
wrapper, dos clientes pueden pasar la verificación local y ejecutar escrituras válidas en secuencia,
produciendo una política de último escritor gana.

## Logs / Evidence

- Prueba manual reproducible con D1/D2: `aaa` → `bbb` y `aaa` → `ccc`.
- Evidencia funcional: ambos dispositivos muestran éxito y mantienen estados locales divergentes.
- Endpoint implicado: `PUT /vault/keys/master`; reconciliación relacionada: `GET /vault/keys`.
- No adjuntar passphrases, wrappers, claves, tokens, payloads ni respuestas sensibles en logs,
  capturas o informes.

## Acceptance Criteria (ACs)

- [ ] El contrato backend/OpenAPI define una condición atómica contra la versión base del wrapper.
- [ ] Para dos cambios concurrentes solo uno puede declararse exitoso.
- [ ] El cliente perdedor recibe un resultado tipado de conflicto y no muestra éxito.
- [ ] Un cliente con caché obsoleta no puede sobrescribir silenciosamente el cambio aceptado.
- [ ] Conflicto, respuesta perdida y reconciliación incierta terminan en un estado seguro y
  explicable.
- [ ] `kekEncRecovery`, items, drafts y checkpoints permanecen sin cambios.
- [ ] Existen tests deterministas de concurrencia, conflicto y respuesta perdida.
- [ ] `ciVerify`, trazabilidad y agent report están actualizados.

---

# SCDK-M130. Crear la matriz de regresión y cerrar la verificación de Fase 7

## Main Story (How, I Want, To)

Como release manager, quiero una suite reproducible de seguridad y resiliencia para demostrar que
la Fase 7 cumple su spec antes de iniciar el rediseño visual.

## Context, Functional Description & Goal

Las cards anteriores añaden pruebas locales, pero falta una evidencia transversal que cubra las
transiciones reales de sesión, lifecycle, recovery y navegación.

## Steps/Scope

### In Scope

- Crear `docs/testing/phase-7-resilience-matrix.md`.
- Mapear cada requisito de `SPEC-HARDENING-V1` a tests y evidencia.
- Añadir pruebas instrumentadas mínimas para expiración de sesión, auto-lock, Lock now, process
  death/cold start, recovery key pendiente, `FLAG_SECURE` y Vault Home tras error retryable.
- Mantener tests de red y respuesta perdida con MockWebServer en JVM.
- Ejecutar la suite Fase 7 en el managed device API 30 existente.
- Integrar la nueva suite en el job instrumentado de PR sin duplicar la lista canónica de gates.
- Ejecutar `./gradlew ciVerify`, `./gradlew releaseVerify` y la suite instrumentada API 30.
- Actualizar spec registry y matriz de trazabilidad con paths exactos.
- Promover `SPEC-HARDENING-V1` a `VERIFIED` solo si todos los ACs tienen evidencia.
- Crear el informe final de fase.

### Out of Scope (if applies)

- E2E contra backend real; pertenece a Fase 10.
- Matriz API 30-36 completa.
- Tests visuales del rediseño de Fase 8.
- Observabilidad de producción.

## Additional Information and Configuration

- Spec: `SPEC-HARDENING-V1`.
- ADRs: `ADR-0001`, `ADR-0002`, `ADR-0003`.
- Dependencias: `SCDK-M113` a `SCDK-M129`, `SCDK-M131`, `SCDK-M132`.
- No usar sleeps ni secretos reales.
- Crear `docs/sdd/agent-reports/SCDK-M130.md`.

### API Contract and Expected Behavior (if applies)

La suite no cambia contratos. Actúa como evidencia ejecutable de las transiciones y políticas
aprobadas.

### Acceptance Criteria (ACs)

- [ ] Cada requisito de `SPEC-HARDENING-V1` tiene test o evidencia manual justificada.
- [ ] Los escenarios instrumentados pasan en el managed device API 30.
- [ ] `ciVerify` y `releaseVerify` pasan.
- [ ] El workflow de PR ejecuta la suite ampliada sin secrets.
- [ ] Registry y trazabilidad contienen paths y resultados exactos.
- [ ] No quedan gaps ocultos; cualquier exclusión tiene follow-up explícito.
- [ ] `SPEC-HARDENING-V1` está en estado `VERIFIED`.
- [ ] El agent report final declara Fase 7 `DONE`, `PARTIAL` o `BLOCKED` con evidencia.
