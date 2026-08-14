# SPEC-HARDENING-V1 — Contrato de hardening, seguridad y resiliencia de SafeCube v1

## Metadata

| Campo              | Valor                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                              |
|--------------------|------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| ID                 | SPEC-HARDENING-V1                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                  |
| Estado             | APPROVED                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                           |
| Owner              | Maintainer / Security owner humano                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                 |
| Fecha              | 2026-08-07                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                         |
| Última revisión    | 2026-08-11                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                         |
| Reemplaza          | N/A                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                |
| Dependencias       | [SPEC-PRODUCT-V1](../product/v1-product-brief.md), [SPEC-AUTH-CONTRACT](../../architecture/openapi-auth-contract-integration.md), [SPEC-OPENAPI-AUTH](../../architecture/openapi-auth-contract-integration.md), [SPEC-CRYPTO-V1](../../architecture/crypto-v1.md), [SPEC-SECURE-ITEM-PAYLOAD-V1](../../architecture/secure-item-payload-v1.md), [SPEC-VAULT-SYNC-V2](../../architecture/vault-sync-versioning-v2.md), [SPEC-OPENAPI-VAULT-KEY-MATERIAL](../../architecture/openapi-vault-key-material-contract-integration.md), [SPEC-OPENAPI-VAULT-ITEMS](../../architecture/openapi-vault-items-contract-integration.md), [SPEC-STORAGE](../../architecture/storage_decision.md) |
| Tasks relacionadas | SCDK-M109–SCDK-M131                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                  |

La spec fue creada inicialmente en REVIEW y el owner humano la ha promovido a APPROVED. Es fuente
normativa para las tareas posteriores; los ADRs relacionados siguen su propio ciclo de aprobación.

## Problema y contexto

La Fase 7 endurece flujos ya construidos en las fases 0–5, pero el roadmap no era suficiente como
contrato observable para decidir si un fallo debe reintentarse, conservar contenido local, bloquear
el vault o terminar la sesión. Los contratos canónicos existentes definen producto, auth, crypto,
payload, storage y sync por separado; esta spec define únicamente las invariantes transversales
que deben respetar esos contratos cuando ocurren fallos, cambios de lifecycle o exposición en
plataforma.

La spec no sustituye los contratos enlazados ni decide detalles técnicos que la Fase 7 ha reservado
a los ADRs de SCDK-M110, SCDK-M111 y SCDK-M112. Esos ADRs deberán estar ACCEPTED antes de
implementar sus decisiones concretas.

## Objetivos

- OBJ-HARDENING-001: convertir los fallos esperables de auth, vault, sync, storage y crypto en
  estados y acciones observables.
- OBJ-HARDENING-002: conservar confidencialidad, integridad, idempotencia y datos cifrados ante
  errores, process death y operaciones cuyo resultado remoto sea incierto.
- OBJ-HARDENING-003: proporcionar un contrato verificable y trazable para las tareas posteriores
  de la Fase 7.

## No objetivos

- Cambiar APIs, esquemas de storage, primitives criptográficas o el protocolo de sync existentes.
- Elegir detalles de implementación reservados a ADR-0001-VAULT-AUTO-LOCK,
  ADR-0002-PASSPHRASE-REWRAP y ADR-0003-SENSITIVE-DATA-SURFACES.
- Rediseñar visualmente las pantallas o introducir búsqueda, carpetas o background sync.
- Añadir un PIN propio de SafeCube, sustituir la passphrase como credencial raíz o usar biometría
  como autenticación de la cuenta backend.
- Añadir soporte para cuentas locales simultáneas, adjuntos, rich text o merge semántico de
  secretos.

## Actores y casos de uso

| Actor               | Caso de uso                                   | Resultado esperado                                                                                               |
|---------------------|-----------------------------------------------|------------------------------------------------------------------------------------------------------------------|
| Usuario autenticado | Cargar o sincronizar datos                    | Ve un estado inequívoco; el contenido local disponible no desaparece por un fallo remoto reintentable.           |
| Usuario             | Reintentar una operación                      | Repite una operación segura y acotada, conservando su identidad cuando sea una mutación.                         |
| Usuario             | Desbloquear o crear el vault                  | El cliente distingue Create de Unlock y no toma una decisión destructiva durante una respuesta lenta o perdida.  |
| Usuario enrolado    | Desbloquear localmente el vault               | Usa un único prompt del sistema con biometría fuerte o credencial segura del dispositivo; la passphrase queda como fallback y recuperación. |
| Usuario             | Cambiar la passphrase                         | Se reenvuelve la misma KEK; no se recifran items ni se cambia su identidad criptográfica.                        |
| Sistema de sesión   | Recibir expiración o fallo de refresh         | Mantiene la sesión ante fallos transitorios y termina de forma segura ante un refresh definitivamente rechazado. |
| Sistema operativo   | Enviar la app a background o matar el proceso | La KEK se descarta al bloquear el vault o morir el proceso; el arranque exige desbloqueo de nuevo.               |

## Lenguaje e invariantes transversales

- Contenido local disponible significa datos ya leídos y válidos desde la source of truth local,
  aunque exista una actualización remota en curso o fallida.
- Mutación significa cualquier creación, actualización, borrado, bootstrap de vault o cambio de
  material maestro que pueda producir un efecto remoto o persistir un nuevo estado local.
- Retryable significa que existe una repetición segura y acotada, normalmente iniciada por una
  acción explícita y con la misma identidad de operación. No significa retry indefinido.
- Un error de refresh o sync no reemplaza contenido local disponible por una pantalla vacía o de
  error. La única excepción es una transición de seguridad terminal que limpia plaintext y lleva a
  Login o Unlock; los blobs cifrados permanecen sujetos a sus contratos de persistencia.
- Ninguna mutación se reintenta automáticamente creando una nueva identidad, mutationId,
  Idempotency-Key, logicalItemId, KEK, DEK o recovery key.
- Una respuesta perdida deja el resultado como incierto hasta que una lectura o reconciliación
  autorizada lo determine; el cliente no declara éxito solo porque la solicitud se envió.
- Un payload corrupto falla en cerrado: no devuelve plaintext parcial, se conserva cifrado y no se
  elimina automáticamente.

## Requisitos funcionales

### FR-AUTH-002 — Expiración de sesión y refresh fallido

La sesión autenticada y la sesión desbloqueada del vault son estados independientes.

1. Ante una respuesta 401 de una operación protegida, el cliente coordina como máximo un refresh
   efectivo para las solicitudes concurrentes y puede reintentar la operación original una sola vez
   con el token nuevo. Una mutación conserva su identidad y sus cabeceras de idempotencia.
2. Si no existe refresh token, o el refresh responde con rechazo definitivo (400, 401 o 403) o con
   una respuesta de protocolo inválida, la sesión termina localmente: se eliminan tokens, se
   descarta la KEK, se limpia el plaintext visible y la raíz de navegación pasa a Login. La
   limpieza de datos locales
   sigue [SPEC-VAULT-SYNC-V2](../../architecture/vault-sync-versioning-v2.md)
   y no debe convertir un fallo remoto de logout en una sesión parcialmente autenticada.
3. Un fallo de transporte, timeout, 408, 429 o 5xx durante refresh conserva la sesión y el vault en
   el estado que aún sea seguro mantener. Se expone RetryableError o contenido local con una
   indicación retryable y no se navega automáticamente a Login.
4. Un segundo 401 tras el refresh único se considera expiración definitiva y sigue el flujo de
   cierre seguro.

**Criterios observables:** varias respuestas 401 concurrentes producen un único refresh; un refresh
transitorio conserva la sesión; un refresh definitivamente rechazado elimina tokens y KEK, limpia
plaintext y navega a Login; no se crean identidades nuevas.

**Estrategia de test:** tests unitarios de concurrencia y clasificación en core:auth/core:network,
tests de integración con respuestas 401 y refresh transitorio/definitivo, y test instrumentado que
verifique la raíz de navegación y la ausencia de plaintext tras el cierre.

### FR-VAULT-002 — Bootstrap del vault, red lenta y respuesta perdida

El bootstrap debe distinguir un vault inexistente, un vault existente que requiere unlock, un
resultado remoto retryable y material local/remoto inválido.

1. Mientras no exista una respuesta válida de GET /vault/keys, el estado observable es
   InitialLoading si no hay contenido local. Una red lenta no se interpreta como Empty, Unlock ni
   como autorización para crear otro vault.
2. Si el vault ya tiene contenido local válido, el bootstrap o refresh remoto se ejecuta sin
   ocultarlo. Un fallo retryable se presenta como metadato retryable junto a Content.
3. Offline sin material local válido no crea un vault nuevo. El usuario recibe una acción de retry
   o una instrucción de conexión, nunca una decisión implícita basada en un estado desconocido.
4. Si se pierde la respuesta de POST /vault/keys, el cliente conserva el intento de forma cifrada,
   ejecuta una reconciliación con GET /vault/keys antes de declarar éxito y mantiene la misma
   candidata, KEK, recovery key e identidad de operación. No genera una segunda inicialización.
5. Si la reconciliación confirma el material esperado, el cliente puede completar la misma
   operación; si no lo confirma, el estado sigue siendo incierto/reintentable y requiere una
   acción explícita. Si la lectura también falla, el intento cifrado se conserva y no se elimina
   automáticamente.
6. Material de vault corrupto, incoherente o no soportado se clasifica como TerminalError, falla en
   cerrado y conserva los blobs cifrados para una reparación explícita.

**Criterios observables:** una respuesta retrasada mantiene InitialLoading; offline no crea un
vault;
una respuesta perdida provoca reconciliación; reintentar no cambia la recovery key ni crea otra KEK;
el material corrupto nunca produce plaintext.

**Estrategia de test:** tests de integración con servidor simulado para latencia, timeout, conexión
interrumpida y respuesta perdida; tests de idempotencia/cantidad de generaciones; tests de crypto
para material corrupto y tests instrumentados del selector Create/Unlock.

### SEC-SESSION-001 — Auto-lock y eliminación de la KEK en memoria

El auto-lock se aplica al vault, no equivale a logout de la cuenta autenticada. Al alcanzar la
política vigente, ejecutar Lock now o detectar process death, el cliente debe:

- zeroizar best-effort la KEK y sus copias accesibles en memoria;
- cancelar operaciones protegidas que no puedan continuar sin la KEK;
- retirar plaintext de pantallas, editores, buffers y estado transitorio visible;
- establecer Unlock como raíz protegida de navegación;
- conservar tokens, items cifrados, drafts y checkpoints conforme a sus contratos canónicos.

La política no puede ofrecer un modo que mantenga el vault desbloqueado indefinidamente. El timeout
por defecto, las opciones permitidas y la medición temporal están cerrados en
[ADR-0001-VAULT-AUTO-LOCK](../../architecture/adr/ADR-0001-VAULT-AUTO-LOCK.md), actualmente
ACCEPTED.

**Criterios observables:** Lock now, expiración de la política y process death dejan el vault
bloqueado, eliminan la KEK y plaintext observable, y conservan los blobs cifrados; la sesión de
cuenta no se termina por un simple auto-lock.

**Estrategia de test:** tests unitarios del gestor de sesión con reloj controlado, tests de
lifecycle de proceso/Activity, tests instrumentados de background/foreground y verificación de que
la navegación no llega a contenido sin unlock.

### SEC-SESSION-002 — Desbloqueo rápido local protegido por Android Keystore

SafeCube debe ofrecer en el MVP un desbloqueo rápido local opcional sin convertir la sesión de
cuenta en una clave del vault ni persistir la passphrase. El enrolamiento solo puede realizarse con
una account session válida y el vault ya desbloqueado mediante passphrase.

1. La aplicación genera una clave de wrapping no exportable en Android Keystore y exige
   autenticación por uso mediante biometría fuerte o credencial segura del dispositivo. El sistema
   operativo, no SafeCube, valida esa credencial.
2. Solo se persiste un artefacto versionado que contenga la KEK envuelta mediante cifrado
   autenticado y los metadatos no secretos imprescindibles. La KEK activa en claro continúa siendo
   exclusivamente material de memoria y se zeroiza al bloquear o morir el proceso.
3. Biometría y credencial segura del dispositivo son métodos alternativos de una única operación
   Unlock; no son controles consecutivos, no autentican contra el backend y no sustituyen la
   passphrase como credencial raíz y mecanismo de recuperación.
4. SafeCube v1 no define ni almacena un PIN propio. Si el dispositivo no soporta los
   authenticators admitidos, no tiene bloqueo seguro o Android Keystore no puede satisfacer la
   política, el desbloqueo rápido no se ofrece y Unlock usa passphrase.
5. Process death siempre restaura Vault session como Locked. Con una account session válida, el
   usuario puede completar un nuevo unlock mediante el prompt del sistema o elegir explícitamente
   la passphrase; nunca se restaura Unlocked ni se expone contenido antes de autenticar.
6. Cancelar o fallar el prompt mantiene Locked. Una clave Keystore invalidada, un artefacto ausente,
   corrupto o no soportado falla en cerrado, elimina únicamente el enrolamiento local inutilizable
   y exige passphrase para volver a enrolar; no elimina items cifrados ni termina la cuenta.
7. Logout, cambio de cuenta o eliminación local de la cuenta destruyen la clave y el artefacto de
   quick unlock asociados. Auto-lock y Lock now conservan el enrolamiento para permitir el siguiente
   unlock local.
8. La clave y el artefacto son exclusivos del dispositivo y la cuenta, quedan excluidos de backup,
   device transfer, sync, logs y estado de navegación, y nunca se envían al backend.

La semántica completa de enrolamiento, fallback, invalidación y lifecycle está cerrada en
[ADR-0001-VAULT-AUTO-LOCK](../../architecture/adr/ADR-0001-VAULT-AUTO-LOCK.md).

**Criterios observables:** después de auto-lock o process death el primer estado siempre es Locked;
un prompt válido permite unlock sin passphrase; cancelar o invalidar el prompt no muestra plaintext;
un dispositivo sin authenticators compatibles usa passphrase; logout elimina el quick unlock; no
existe PIN propio de SafeCube.

**Estrategia de test:** tests unitarios del coordinador y del envelope local, tests de integración
con un adapter de Keystore falso para éxito, cancelación, invalidación y corrupción, y tests
instrumentados con biometría/credencial del dispositivo, process death, logout y fallback a
passphrase.

### SEC-CRYPTO-002 — Cambio de passphrase mediante rewrap

El cambio de passphrase en v1 reenvuelve la misma KEK; no rota la KEK, no recifra items y no
modifica su identidad criptográfica.

1. Solo está disponible con el vault desbloqueado y una KEK activa. La passphrase actual debe
   desenvolver una KEK igual a la activa; la nueva passphrase y su confirmación deben validarse
   antes de mutar el servidor.
2. Se deriva la nueva MASTER_KEY usando el salt y parámetros KDF v1 existentes y se crea un
   kekEncMaster nuevo con nonce nuevo. La operación remota usa PUT /vault/keys/master según
   [SPEC-OPENAPI-VAULT-KEY-MATERIAL](../../architecture/openapi-vault-key-material-contract-integration.md).
3. La operación no cambia kekEncRecovery, DEKs, payloads, payloadVersion, itemRevision,
   logicalItemId, drafts ni ciphertext. El request no transporta passphrases ni la KEK.
4. La caché local solo se actualiza tras confirmación remota o tras una lectura posterior que
   confirme exactamente el nuevo wrapper. Ante respuesta perdida, se reconcilia con GET /vault/keys;
   si el resultado no puede determinarse, se elimina el material maestro cacheado, se zeroiza la
   KEK, se bloquea el vault y se exige reconciliación online.
5. Una passphrase actual incorrecta es un error terminal de validación local y no produce request
   remoto. Las copias temporales de passphrases, MASTER_KEY y KEK se zeroizan best-effort.

Los detalles de orden de persistencia, reconciliación y limpieza se formalizan en
[ADR-0002-PASSPHRASE-REWRAP](../../architecture/adr/ADR-0002-PASSPHRASE-REWRAP.md), ACCEPTED por
el owner humano.

**Criterios observables:** un cambio exitoso conserva la KEK efectiva y todos los payloads; una
passphrase actual incorrecta no llama al servidor; una respuesta perdida no se declara éxito sin
GET /vault/keys; un resultado incierto bloquea y exige reconciliación sin borrar items.

**Estrategia de test:** tests unitarios de KDF/unwrap/rewrap, tests de contrato del request y
tests de integración para confirmación, respuesta perdida y lectura posterior; comparar que no
cambian los campos de items y drafts.

### SEC-PRIVACY-001 — Superficies sensibles y estado transitorio

La aplicación debe impedir que secretos o material identificable del vault aparezcan en superficies
de plataforma o diagnósticos:

- deshabilitar backup de aplicación, cloud backup y device transfer mediante configuración efectiva
  y reglas explícitas;
- aplicar protección de screenshots y recents a toda la Activity mediante el mecanismo de
  plataforma decidido en ADR-0003-SENSITIVE-DATA-SURFACES;
- prohibir en todos los build types logs de headers, cuerpos HTTP, tokens, passphrases, recovery
  keys, MASTER_KEY, KEK, DEK, plaintext, payloads, displayHint e IDs de items;
- no escribir programáticamente passwords, passphrases ni recovery keys al clipboard en v1;
- aplicar visual transformation a passwords y passphrases;
- no colocar secretos en Bundle, rutas serializables, SavedStateHandle ni rememberSaveable;
- permitir únicamente un registro transitorio cifrado y excluido de backup para una inicialización
  de vault pendiente, con borrado verificable al guardar la recovery key, cerrar sesión o descartar
  de forma segura el intento;
- exigir reglas R8 mínimas y justificadas, sin -keep globales como sustituto de un contrato.

La excepción transitoria no permite almacenar plaintext, recovery key en claro o material de claves
fuera del cifrado definido. Las decisiones de configuración Android, lifecycle de esa excepción y
reglas R8 se cierran en
[ADR-0003-SENSITIVE-DATA-SURFACES](../../architecture/adr/ADR-0003-SENSITIVE-DATA-SURFACES.md),
ACCEPTED por el owner humano.

**Criterios observables:** el manifest/release efectivo excluye backup y transferencia; screenshots
y recents no muestran contenido; los logs de cualquier build no contienen datos prohibidos; la
recovery key no aparece en estado restaurable ni clipboard; el registro transitorio se borra en
cada evento de finalización definido.

**Estrategia de test:** inspección estática de manifest, recursos y configuración R8; tests
instrumentados de screenshot/recents y saved state; tests de logging con fixtures sintéticos no
sensibles; tests de ciclo de vida y borrado del registro cifrado.

## Requisitos no funcionales

### NFR-RESILIENCE-001 — Modelo observable mínimo de operación

Toda operación de lectura, bootstrap o mutación que llegue a UI debe exponer como mínimo uno de
estos estados, con causa y acción segura cuando corresponda:

| Estado         | Significado observable                                                                                                        |
|----------------|-------------------------------------------------------------------------------------------------------------------------------|
| Idle           | No hay operación iniciada ni trabajo pendiente visible.                                                                       |
| InitialLoading | Se está obteniendo la primera respuesta y todavía no existe contenido local renderizable.                                     |
| Content        | Existe contenido local válido. Puede coexistir con refresh/sync retryable; el contenido no se sustituye.                      |
| Empty          | La lectura terminó correctamente y el resultado válido no contiene elementos. No representa loading ni error.                 |
| Mutating       | Una mutación iniciada está en curso; los controles incompatibles están protegidos contra doble envío.                         |
| RetryableError | La operación no concluyó por una causa reintentable; se muestra una acción retry acotada.                                     |
| TerminalError  | La operación no puede resolverse repitiéndola; se muestra una acción de reparación, unlock, login o soporte según el dominio. |

Transiciones mínimas:

~~~
Idle → InitialLoading → Content | Empty | RetryableError | TerminalError
Content → Mutating → Content | RetryableError | TerminalError
Empty → Mutating | InitialLoading | RetryableError | TerminalError
RetryableError → InitialLoading | Mutating     (solo por acción explícita)
TerminalError → estado de reparación      (solo por acción explícita o nueva sesión)
~~~

Una recomposición, recreación de Activity o recepción repetida del mismo evento no inicia un retry
ni una mutación. Cuando existe contenido, un fallo de refresh o sync conserva Content y expone la
información retryable de forma asociada; no lo transforma en Empty.

**Criterios observables:** cada camino de éxito, vacío, mutación, fallo retryable y fallo terminal
se puede distinguir sin inspeccionar excepciones; no hay loops de retry por recomposición; un
refresh/sync fallido conserva contenido local.

**Estrategia de test:** tests unitarios de la state machine y de idempotencia de eventos, tests de
ViewModel para las siete variantes, y tests Compose/instrumentados de contenido, vacío, retry y
error terminal.

### NFR-RESILIENCE-002 — Clasificación canónica de errores y retry

La clasificación es agnóstica de UI y debe viajar hacia los módulos de dominio como una decisión
sanitizada Retryable o Terminal, con una semántica condicional explícita cuando el contrato del
dominio lo exige. CancellationException y cancelaciones equivalentes no son errores retryables y
no deben convertirse en mensajes de error.

#### Reglas de clasificación

**Retryable:** errores de transporte, timeout, HTTP 408, HTTP 429 y HTTP 5xx. También puede ser
retryable un fallo de storage solo cuando el adaptador lo identifica explícitamente como transitorio
y conserva la atomicidad de la operación.

**Terminal:** validación, credenciales rechazadas, errores de protocolo, respuestas malformadas,
integridad criptográfica fallida, envelope no soportado, payload corrupto, corrupción de schema o
storage no recuperable, y causas desconocidas sin una señal explícita de transitoriedad.

**Condicional:** 401 se resuelve con FR-AUTH-002; 403 es terminal; 400 es validación o protocolo
terminal; 409, 412 y 428 siguen la semántica
de [SPEC-VAULT-SYNC-V2](../../architecture/vault-sync-versioning-v2.md):
conflicto de idempotencia/protocolo terminal, conflicto CAS con resolución explícita y header
obligatorio como fallo de protocolo. Ninguna condición permite un retry indiscriminado.

#### Matriz de retry

| Dominio/operación                                   | Retryable y acción                                                                                                                                 | Terminal o condicional                                                                                                       | Preservación obligatoria                                                                                      |
|-----------------------------------------------------|----------------------------------------------------------------------------------------------------------------------------------------------------|------------------------------------------------------------------------------------------------------------------------------|---------------------------------------------------------------------------------------------------------------|
| Auth: login, signup y operaciones protegidas        | Transporte, timeout, 408, 429, 5xx; retry explícito de la misma operación. Un 401 de operación protegida pasa por el refresh único de FR-AUTH-002. | Validación/credenciales rechazadas, protocolo, respuesta malformada y 403; no retry automático.                              | Campos no sensibles permitidos por UX; nunca tokens ni cuerpos HTTP en modelos UI.                            |
| Refresh de sesión                                   | Transporte, timeout, 408, 429, 5xx; conserva sesión y ofrece retry.                                                                                | Sin refresh token, 400, 401, 403 o protocolo inválido; termina sesión y bloquea vault.                                       | Tokens válidos hasta la decisión terminal; contenido cifrado conforme a sync/storage.                         |
| Vault bootstrap: GET /vault/keys y POST /vault/keys | Transporte, timeout, 408, 429, 5xx; en POST con respuesta perdida primero se reconcilia y luego se repite explícitamente la misma operación.       | Validación, protocolo, material corrupto/no soportado; no crear una segunda inicialización.                                  | Candidata cifrada, KEK, recovery key e identidad de operación; nunca plaintext en estado restaurable.         |
| Sync pull/push                                      | Transporte, timeout, 408, 429, 5xx; retry seguro con el mismo cursor/request.                                                                      | 400, 409, 428 como fallo de protocolo/idempotencia; 412 como conflicto que exige resolución; 403 terminal.                   | Contenido local, draft, payload, mutationId, Idempotency-Key, If-Match y checkpoint según SPEC-VAULT-SYNC-V2. |
| Storage local                                       | Fallo que el adapter marque transitorio y que no haya comprometido la transacción; retry explícito sin duplicar escritura.                         | Schema/migración, constraint, corrupción, integridad o I/O no recuperable/desconocido; reparación explícita.                 | Atomicidad de Room; no borrar datos para ocultar un error ni presentar datos parciales como válidos.          |
| Crypto y payload                                    | No hay retry automático de unwrap/decrypt/encrypt. Solo una reparación, migración o nueva entrada explícita puede volver a intentarlo.             | Passphrase inválida, fallo AEAD, envelope no soportado, integridad criptográfica o payload corrupto; terminal y fail-closed. | Blob oficial/draft cifrado; sin plaintext parcial y sin eliminación automática.                               |

El resultado de una mutación retryable nunca se repite con una identidad nueva. Las operaciones de
lectura pueden reintentarse de forma acotada; las mutaciones solo se repiten automáticamente para
la única repetición posterior a un refresh exitoso cuando FR-AUTH-002 lo permite. Todo retry
restante requiere acción explícita o un mecanismo de reconciliación definido por el contrato.

**Criterios observables:** la matriz clasifica los seis dominios; 408, 429, 5xx, timeout y
transporte son retryables; validación, protocolo, integridad criptográfica y payload corrupto son
terminales; 409/412/428 no entran en un loop genérico; cancelación no aparece como error.

**Estrategia de test:** tabla exhaustiva de tests unitarios en core:network, tests de mapeo en
core:auth y core:vault, integración de sync/storage/crypto y pruebas de contrato para que no se
filtren cuerpos HTTP, excepciones ni secretos a UI.

## Requisitos de lifecycle y alcance

### NFR-LIFECYCLE-001 — Process death, cold start y restauración de navegación

1. Process death siempre descarta la KEK activa en claro, aunque los tokens, el estado de cuenta o
   un artefacto local de quick unlock puedan seguir disponibles. Un cold start nunca navega
   directamente a contenido desbloqueado sin una nueva operación de unlock autorizada mediante
   prompt del sistema o passphrase.
2. Con una sesión válida y vault bloqueado, la raíz es Unlock. Sin sesión válida, la raíz es Login.
   La navegación restaurada solo puede incluir estado no sensible y no puede reconstruir plaintext,
   passphrases, recovery keys o una operación mutante como si hubiera terminado.
3. Items cifrados, drafts y checkpoints se conservan según storage/sync. Una mutación interrumpida
   requiere reconciliación o acción explícita; no se ejecuta automáticamente con una identidad
   nueva.
4. Si el estado local restaurable es inconsistente o no soportado, falla en cerrado y presenta una
   acción terminal de recuperación sin eliminar automáticamente el contenido cifrado.

**Criterios observables:** después de process death siempre aparece Login o Unlock; ningún estado
restaurado contiene secretos; una operación interrumpida no se duplica; el contenido cifrado
permanece disponible para la estrategia de recuperación.

**Estrategia de test:** tests unitarios de decisión de ruta, integración con estados persistidos,
tests instrumentados de process death/cold start y pruebas E2E de restauración de navegación.

### FR-SCOPE-001 — Retirada de placeholders fuera de v1

Toda ruta, pantalla, opción o acción alcanzable en la release v1 debe tener comportamiento cubierto
por SPEC-PRODUCT-V1 o no estar expuesta. No se muestran textos, controles ni rutas dummy,
placeholder, coming soon o equivalentes para capacidades fuera del alcance.

Quedan fuera de la superficie pública de v1, entre otros, carpetas reales, búsqueda avanzada, un
PIN propio de SafeCube, background sync periódico, merge semántico, adjuntos/rich text y soporte
local simultáneo para varias cuentas. El quick unlock mediante biometría fuerte o credencial segura
del dispositivo sí pertenece al MVP y debe cumplir SEC-SESSION-002. La navegación no debe anunciar
una capacidad que no pueda completarse con un contrato aprobado.

**Criterios observables:** el inventario de rutas de release no contiene placeholders ni acciones
muertas; las capacidades fuera de v1 no son alcanzables; la auditoría de textos no encuentra
marcadores de implementación incompleta.

**Estrategia de test:** inventario estático de rutas y recursos, búsqueda automatizada de textos
placeholder, tests de navegación y revisión manual de alcance contra SPEC-PRODUCT-V1.

## Interfaces y contratos

Esta spec no añade endpoints, headers ni modelos de red. Los endpoints y campos existentes siguen
siendo gobernados por:

-
Auth: [SPEC-AUTH-CONTRACT / SPEC-OPENAPI-AUTH](../../architecture/openapi-auth-contract-integration.md).
- Key
  material: [SPEC-OPENAPI-VAULT-KEY-MATERIAL](../../architecture/openapi-vault-key-material-contract-integration.md).
- Items y
  sync: [SPEC-OPENAPI-VAULT-ITEMS](../../architecture/openapi-vault-items-contract-integration.md)
  y [SPEC-VAULT-SYNC-V2](../../architecture/vault-sync-versioning-v2.md).
- Criptografía y payload: [SPEC-CRYPTO-V1](../../architecture/crypto-v1.md) y
  [SPEC-SECURE-ITEM-PAYLOAD-V1](../../architecture/secure-item-payload-v1.md).
- Persistencia: [SPEC-STORAGE](../../architecture/storage_decision.md).
- Alcance de producto: [SPEC-PRODUCT-V1](../product/v1-product-brief.md).

La implementación debe mantener la propiedad de runtime de HTTP/auth en core:network y core:auth;
las features no consumen directamente APIs generadas. Esta spec define el comportamiento ante
fallos y no duplica los esquemas de esos contratos.

## Modelo de datos y persistencia

- Room continúa siendo la source of truth local para items, drafts y checkpoints; no se cambia su
  schema ni se introduce SQLCipher en esta tarea.
- Los payloads, material de claves y registros sensibles permanecen cifrados según los contratos
  canónicos. La KEK activa en claro es material de memoria y process death la descarta siempre. El
  único artefacto persistible para quick unlock es una KEK envuelta por una clave no exportable de
  Android Keystore conforme a SEC-SESSION-002 y ADR-0001.
- Un payload oficial o draft corrupto se conserva cifrado para reparación explícita; no se elimina
  automáticamente ni se reemplaza por un registro vacío.
- Un intento de bootstrap cuyo resultado remoto sea incierto puede conservar un registro
  transitorio cifrado, excluido de backup y con ciclo de borrado definido por SEC-PRIVACY-001.
- No se persisten passphrases, MASTER_KEY, KEK o DEK en claro, recovery keys ni plaintext en estado
  de navegación, saved state, logs o almacenamiento transitorio en claro. El artefacto de quick
  unlock no es una excepción para material en claro y queda excluido de backup y transferencia.

## Seguridad, privacidad y zero-knowledge

El fallo de cualquier validación criptográfica o del payload es fail-closed. Los contratos de
crypto y payload continúan gobernando algoritmos, AAD, envelopes, zeroize best-effort y formato;
esta spec añade las consecuencias de lifecycle y error.

La sesión autenticada no concede acceso al plaintext: después de process death, auto-lock o
limpieza terminal de sesión siempre es necesario ejecutar un nuevo unlock mediante passphrase o,
si existe un enrolamiento local válido, mediante biometría fuerte o credencial segura del
dispositivo. Ningún log, error de UI, reporte de agente, test fixture o captura puede contener
secretos, payloads ni identificadores sensibles.

## Observabilidad

### Permitido

- Estado observable de operación y transición (Idle, InitialLoading, Content, Empty, Mutating,
  RetryableError, TerminalError).
- Dominio lógico (auth, refresh, vault-bootstrap, sync, storage, crypto), clase de status HTTP sin
  cuerpo, categoría de error sanitizada y si existe una acción retryable.
- Conteo acotado de intentos, versión de app/build y resultado de una reconciliación sin material
  de datos.

### Prohibido

- Tokens, headers de autorización, cookies, passphrases, recovery keys, MASTER_KEY, KEK, DEK.
- Plaintext, payloads cifrados o descifrados, displayHint, IDs de items, cuerpos HTTP, URLs con
  parámetros sensibles y contenido de excepciones.

La instrumentación de observabilidad no puede bloquear una operación ni cambiar su clasificación.
Telemetría y crash reporting quedan fuera de esta spec y se regirán por el contrato específico de
la Fase 9.

## Compatibilidad, migraciones y rollout

- No hay cambios de API, protocolo, schema Room, envelope, KDF, AEAD, payload o semántica base de
  sync en SCDK-M109.
- El quick unlock requiere persistencia local versionada y excluida de backup, pero no cambia el
  wrapper maestro remoto ni permite sincronizar claves de Android Keystore entre dispositivos.
- Las implementaciones posteriores deben ser compatibles con los datos v1 existentes y no pueden
  eliminar payloads para recuperarse de un error.
- Esta spec está APPROVED tras revisión del owner humano y no contiene decisiones críticas abiertas.
  Cada decisión técnica reservada solo puede pasar a runtime cuando su ADR concreto esté ACCEPTED;
  ADR-0001, ADR-0002 y ADR-0003 ya cumplen esa condición.
- Un rollback de runtime debe conservar blobs cifrados, drafts y checkpoints; cualquier migración
  futura con impacto destructivo requiere su propio ADR y política de rollback.

## Test matrix

| Requisito          | Unit                                        | Integration                                        | Instrumented/E2E                                 | Manual                                 |
|--------------------|---------------------------------------------|----------------------------------------------------|--------------------------------------------------|----------------------------------------|
| FR-AUTH-002        | Sí: refresh, concurrencia y clasificación   | Sí: 401, refresh transitorio/definitivo            | Sí: navegación Login/Unlock y limpieza visible   | Sí: expiración real controlada         |
| FR-VAULT-002       | Sí: state machine e identidades             | Sí: latencia, offline, timeout y respuesta perdida | Sí: Create/Unlock y recovery                     | Sí: red lenta y reconexión             |
| SEC-SESSION-001    | Sí: reloj y zeroize observable por contrato | Sí: operaciones canceladas                         | Sí: background, Lock now, process death          | Sí: política de timeout aceptada       |
| SEC-SESSION-002    | Sí: enrolamiento, fallback y envelope local | Sí: adapter Keystore, corrupción e invalidación    | Sí: prompt, process death, logout y passphrase   | Sí: matriz con/sin sensor o lock seguro |
| SEC-CRYPTO-002     | Sí: rewrap y passphrase inválida            | Sí: PUT, GET de reconciliación y respuesta perdida | Sí: bloqueo seguro ante resultado incierto       | Sí: comprobar que items siguen iguales |
| SEC-PRIVACY-001    | Sí: no persistencia de secretos en modelos  | Sí: ciclo del registro transitorio                 | Sí: screenshot, recents, saved state y clipboard | Sí: inspección de logs/build release   |
| NFR-RESILIENCE-001 | Sí: siete estados y transiciones            | N/A                                                | Sí: estados de Compose y no loops                | Sí: revisión de UX de estados          |
| NFR-RESILIENCE-002 | Sí: tabla exhaustiva por dominio            | Sí: mapeos de capas                                | Sí: retry/terminal y contenido local             | Sí: matriz de escenarios               |
| NFR-LIFECYCLE-001  | Sí: decisión de ruta segura                 | Sí: cold start con estados persistidos             | Sí: process death y restauración                 | Sí: matriz de dispositivos             |
| FR-SCOPE-001       | Sí: inventario de rutas/acciones            | N/A                                                | Sí: navegación sin placeholders                  | Sí: auditoría de alcance v1            |

## Acceptance Criteria

- [ ] AC-HARDENING-001: existe esta spec con ID SPEC-HARDENING-V1 y estado inicial REVIEW.
- [ ] AC-HARDENING-002: el modelo observable contiene exactamente como mínimo Idle, InitialLoading,
  Content, Empty, Mutating, RetryableError y TerminalError, con transiciones y semántica de
  contenido local verificables.
- [ ] AC-HARDENING-003: la matriz cubre auth, refresh, vault bootstrap, sync, storage y crypto;
  clasifica transporte, timeout, 408, 429, 5xx, validación, protocolo, integridad y payload
  corrupto sin depender de cuerpos HTTP.
- [ ] AC-HARDENING-004: un error de refresh o sync no oculta contenido local válido; los retries de
  mutaciones conservan la identidad y no generan KEK, DEK, draft, mutationId o recovery key nuevos.
- [ ] AC-HARDENING-005: payloads corruptos fallan en cerrado, conservan su blob cifrado y no se
  eliminan automáticamente.
- [ ] AC-HARDENING-006: expiración terminal, auto-lock y process death eliminan la KEK en memoria,
  limpian plaintext y obligan a pasar por Login o Unlock según corresponda.
- [ ] AC-HARDENING-006A: el quick unlock del MVP usa una clave no exportable de Android Keystore,
  acepta biometría fuerte o credencial segura del dispositivo como alternativas, conserva la
  passphrase como fallback, no define PIN propio y nunca restaura Unlocked tras process death.
- [ ] AC-HARDENING-007: el cambio de passphrase se define como rewrap de la misma KEK, sin
  modificar DEKs, payloads, revisiones, drafts o identidades de items.
- [ ] AC-HARDENING-008: backups, screenshots, recents, logs, clipboard y estado transitorio tienen
  controles observables y no contienen secretos.
- [ ] AC-HARDENING-009: process death, cold start y restauración de navegación están definidos sin
  restaurar plaintext ni abrir el vault sin unlock.
- [ ] AC-HARDENING-010: las rutas y opciones placeholder fuera de v1 no son alcanzables en la
  superficie pública de la release.
- [ ] AC-HARDENING-011: todos los requisitos tienen criterios observables, estrategia de test,
  trazabilidad y enlaces a los contratos canónicos sin duplicarlos.
- [x] AC-HARDENING-012: el owner humano ha revisado y promovido esta spec a APPROVED; ningún agente
  puede marcar por sí solo este criterio.

## Trazabilidad

| Requirement ID     | Tareas planificadas                       | Código esperado                                          | Tests/evidencia esperados                                           |
|--------------------|-------------------------------------------|----------------------------------------------------------|---------------------------------------------------------------------|
| NFR-RESILIENCE-001 | SCDK-M117–SCDK-M121                       | core:vault, feature:auth, feature:vault, app             | State machine, ViewModels y tests instrumentados de estados         |
| NFR-RESILIENCE-002 | SCDK-M113–SCDK-M121                       | core:network, core:auth, core:vault, core:storage        | SCDK-M113: matriz unitaria y mapeos sanitizados; integración de errores y ejecución de retry seguro en tareas posteriores |
| FR-AUTH-002        | SCDK-M114, SCDK-M118                      | core:auth, core:network, app, feature:auth               | Refresh concurrente, cierre terminal y navegación                   |
| FR-VAULT-002       | SCDK-M116, SCDK-M117, SCDK-M119           | core:vault, feature:vault, app                           | Bootstrap, respuesta perdida, reconciliación y recovery             |
| SEC-SESSION-001    | SCDK-M110, SCDK-M122, SCDK-M125           | app, core:vault, feature:vault                           | Auto-lock, zeroize, lifecycle y process death                       |
| SEC-SESSION-002    | SCDK-M110, SCDK-M131                      | app, core:vault, core:storage, feature:vault             | Android Keystore, prompt del sistema, fallback y process death      |
| SEC-CRYPTO-002     | SCDK-M111, SCDK-M123, SCDK-M124           | core:vault, feature:vault                                | [ADR-0002](../../architecture/adr/ADR-0002-PASSPHRASE-REWRAP.md) ACCEPTED; rewrap, invariantes de items y resultado incierto |
| SEC-PRIVACY-001    | SCDK-M112, SCDK-M124, SCDK-M126–SCDK-M128 | app, core:auth, core:storage, core:vault, features       | [ADR-0003](../../architecture/adr/ADR-0003-SENSITIVE-DATA-SURFACES.md) ACCEPTED; manifest, R8, logs, screenshots, saved state y clipboard            |
| NFR-LIFECYCLE-001  | SCDK-M125, SCDK-M131                       | app, core:vault, feature:vault                           | Process death, cold start y rutas seguras                           |
| FR-SCOPE-001       | SCDK-M129                                 | app, feature:vault, feature:profile, settings.gradle.kts | Inventario de rutas, navegación y auditoría de placeholders         |

Código/runtime es N/A para SCDK-M109: esta tarea solo crea el contrato normativo. La evidencia de
esta revisión se registra
en [docs/sdd/agent-reports/SCDK-M109.md](../../sdd/agent-reports/SCDK-M109.md).
