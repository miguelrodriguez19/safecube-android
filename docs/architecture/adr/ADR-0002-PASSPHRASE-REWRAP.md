# ADR-0002-PASSPHRASE-REWRAP — Consistencia del cambio de passphrase mediante rewrap

## Metadata

| Campo              | Valor                                                                                                                   |
|--------------------|-------------------------------------------------------------------------------------------------------------------------|
| ID                 | `ADR-0002-PASSPHRASE-REWRAP`                                                                                            |
| Estado             | `ACCEPTED`                                                                                                              |
| Fecha              | `2026-08-27` (addendum CAS; decisión original: `2026-08-11`)                                                          |
| Owner              | Security owner humano                                                                                                   |
| Reemplaza          | `N/A`                                                                                                                   |
| Specs relacionadas | `SPEC-HARDENING-V1`, `SPEC-CRYPTO-V1`, `SPEC-SECURE-ITEM-PAYLOAD-V1`, `SPEC-OPENAPI-VAULT-KEY-MATERIAL`, `SPEC-STORAGE` |
| ADRs relacionados   | `ADR-0001-VAULT-AUTO-LOCK` (quick unlock; M131)                                                                        |
| Tasks relacionadas | `SCDK-M111`, `SCDK-M123`, `SCDK-M124`, `SCDK-M132`; backend `SCDK-B55` (dependencia histórica: `SCDK-M109`)           |

El owner humano aceptó este ADR el 2026-08-11. El addendum de concurrencia de `SCDK-M132`, basado
en la entrega backend `SCDK-B55`, queda aceptado el 2026-08-27 y forma parte de la decisión
normativa para las implementaciones del cambio de passphrase mediante rewrap.

## Contexto

`SPEC-CRYPTO-V1` define que el cambio de passphrase de v1 reenvuelve la misma KEK. El contrato
de material de claves ya expone `PUT /vault/keys/master` y conserva un `kekEncRecovery`
independiente. El contrato de hardening exige, además, que el resultado de una mutación incierta
se reconcilie sin perder acceso a los datos cifrados.

La passphrase no es la identidad criptográfica de los items: deriva una `MASTER_KEY` que
desenvuelve la KEK, y la KEK desenvuelve los DEKs de cada item. Por ello, la operación debe
modificar únicamente el wrapper de la KEK protegido por la nueva passphrase. El orden entre la
confirmación remota y la caché local es parte de la seguridad del flujo.

## Problema

Hay que fijar cómo se verifican las credenciales, cómo se genera el nuevo wrapper, en qué orden se
actualizan servidor y caché local y cómo se recupera una respuesta de red perdida, sin rotar la
KEK ni alterar DEKs, payloads, drafts o checkpoints.

## Opciones consideradas

### Opción A — Rotar la KEK y recifrar todos los items

- Ventajas: permitiría asociar una KEK nueva a la nueva passphrase.
- Costes/riesgos: convierte una operación de credencial en una migración masiva; aumenta la
  superficie de fallos, conflictos y pérdida de acceso.
- Impacto en seguridad: requiere mantener o recuperar muchos DEKs y payloads durante una
  operación sensible.
- Impacto en migración: incompatible con el contrato criptográfico v1 y con la continuidad de
  `payloadVersion`, `itemRevision` y drafts.

### Opción B — Actualizar la caché local antes del servidor

- Ventajas: la siguiente apertura local usaría inmediatamente la nueva passphrase.
- Costes/riesgos: una caída antes de la confirmación remota deja la caché divergente del servidor.
- Impacto en seguridad: puede producir un vault que parece actualizado, aunque el backend conserve
  el wrapper anterior.
- Impacto en migración: exige una reconciliación adicional y no ofrece una fuente de verdad clara.

### Opción C — Rewrap de la misma KEK, remoto primero y reconciliación explícita

- Ventajas: conserva la identidad criptográfica de todos los items y permite decidir el resultado
  mediante `GET /vault/keys` cuando la respuesta de `PUT` no es observable.
- Costes/riesgos: requiere una operación de lectura adicional y una ruta de bloqueo seguro si el
  resultado no puede determinarse.
- Impacto en seguridad: evita aceptar una passphrase local que el servidor no haya confirmado.
- Impacto en migración: no requiere migración de items ni de esquemas.

### Opción D — Reintentar el PUT a ciegas después de cualquier error

- Ventajas: implementación aparentemente sencilla.
- Costes/riesgos: confunde un resultado incierto con uno no aplicado y puede repetir una mutación
  sin conocer el estado remoto.
- Impacto en seguridad: no proporciona una política de fail-closed para material maestro
  divergente.
- Impacto en migración: dificulta distinguir el wrapper vigente y complica la recuperación.

## Decisión

Se adopta la Opción C. La siguiente política es normativa.

### 1. Precondiciones y verificación de credenciales

1. La operación requiere el vault desbloqueado y una KEK activa en memoria.
2. La UI debe recibir la passphrase actual, la nueva passphrase y su confirmación. La validación
   local de presencia y coincidencia ocurre antes de cualquier llamada remota; este ADR no añade
   una política de complejidad.
3. La passphrase actual se procesa con el `kdfSalt` y los parámetros KDF v1 existentes. La
   `MASTER_KEY` derivada desenvuelve `kekEncMaster`; la KEK resultante debe compararse en bytes,
   sin registrar el valor, con la KEK activa. Si no coincide o la integridad AEAD falla, el
   resultado es terminal local: no se realiza `PUT`, no se cambia la caché y se informa de
   credencial inválida.
4. Las passphrases convertidas a bytes, las `MASTER_KEY` derivadas, las copias de la KEK y los
   buffers temporales se zeroizan best-effort en bloques `finally`. Ningún valor secreto aparece
   en logs, errores, analytics, navegación o reportes.

### 2. Construcción del nuevo wrapper

1. La nueva `MASTER_KEY` se deriva con el mismo `kdfAlgorithm`, `kdfSalt`, parámetros y longitud
   de salida de v1. No se crea un salt nuevo ni se cambian los parámetros KDF en esta operación.
2. Se envuelve la misma KEK activa con la nueva `MASTER_KEY` usando un nonce criptográficamente
   nuevo y se construye el nuevo `kekEncMaster`.
3. `kekEncRecovery` no se modifica. Tampoco se modifican la KEK efectiva, DEKs, `wrappedDEK`,
   payloads, `payloadVersion`, `itemRevision`, `logicalItemId`, drafts ni checkpoints.
4. El request de `PUT /vault/keys/master` contiene exclusivamente el nuevo `kekEncMaster`.
   Nunca contiene passphrases, `MASTER_KEY`, KEK, DEKs ni plaintext.

### 3. Orden remoto/local

1. El nuevo wrapper se mantiene solo en memoria durante la operación; la caché local no se
   sustituye antes de que el servidor confirme.
2. Un éxito remoto explícito confirma la mutación. Solo después se actualiza la caché local en
   una escritura atómica, conservando la KEK activa y el estado desbloqueado. La operación no se
   declara completamente satisfecha hasta que la caché local contiene el wrapper confirmado.
3. Si la respuesta de éxito no devuelve el material completo, la confirmación puede completarse
   con una lectura posterior de `GET /vault/keys`. El wrapper remoto debe coincidir exactamente
   con el nuevo `kekEncMaster` antes de declararlo confirmado.
4. Si la escritura local falla después de un éxito remoto conocido, no se repite el `PUT` ni se
   genera otra KEK. Se reintenta únicamente la persistencia del wrapper ya confirmado; si el
   estado de la caché sigue sin poder determinarse, se aplica la ruta de resultado indeterminado
   del apartado siguiente.

### 4. Respuesta perdida y reconciliación

Una pérdida de conexión, timeout, respuesta truncada o respuesta ambigua después de iniciar el
`PUT` no demuestra que el cambio no se aplicara. En ese caso:

1. Se ejecuta `GET /vault/keys` antes de mostrar éxito.
2. Si `GET` devuelve exactamente el nuevo `kekEncMaster`, el cambio remoto se considera aplicado;
   se actualiza la caché local y se conserva la KEK activa.
3. Si `GET` devuelve exactamente el wrapper anterior, el cambio se considera no aplicado. No se
   actualiza la caché ni se repite automáticamente el `PUT`; el usuario puede iniciar de forma
   explícita un nuevo intento.
4. Si `GET` no puede completarse, devuelve material inválido o devuelve un wrapper distinto de
   los dos estados comparables, el resultado es indeterminado. Se elimina la caché local del
   material maestro (`kekEncMaster` y metadatos asociados exclusivamente a ese wrapper), se
   zeroiza la KEK y se bloquea el vault. Se conservan `kekEncRecovery`, items cifrados, drafts y
   checkpoints. El siguiente acceso exige reconciliación online y un nuevo desbloqueo.
5. En ningún caso se eliminan automáticamente payloads para resolver la incertidumbre ni se
   declara éxito basándose solo en repetir el request.

### 5. Política de reintentos y errores

- Son reintentables únicamente los fallos transitorios de transporte, timeout, HTTP 408, HTTP 429
  y HTTP 5xx, además de la lectura de reconciliación cuando el resultado siga siendo incierto.
  Los reintentos automáticos, si una capa posterior los habilita, deben conservar la misma
  operación candidata y no crear una identidad criptográfica nueva.
- Una respuesta incierta exige primero `GET /vault/keys`; no se autoriza reemitir el `PUT` a ciegas.
- Son definitivos la passphrase actual incorrecta, validación fallida, protocolo incompatible,
  integridad criptográfica fallida y payload o material de claves corrupto, salvo una acción
  explícita que repare su causa. Un error definitivo no entra en un loop de retry.
- Una mutación nunca se reintenta automáticamente con una KEK, DEK, passphrase o wrapper nuevo.
  Un nuevo intento iniciado por el usuario es una nueva operación y debe repetir las
  precondiciones de este ADR.

### 6. Invariantes de éxito

Después de un cambio confirmado:

- la KEK activa antes y después es la misma;
- solo cambia `kekEncMaster`; `kekEncRecovery` permanece igual;
- todos los items conservan exactamente sus DEKs envueltos, payloads, revisiones, identidades y
  versiones;
- los drafts y checkpoints permanecen disponibles sin recifrado;
- un desbloqueo posterior usa la nueva passphrase, y el desbloqueo por recovery conserva su
  semántica existente.

### 7. Concurrencia optimista CAS con ETag e If-Match — addendum aceptado de SCDK-B55

Para evitar la sobrescritura entre dispositivos, el cambio de passphrase debe usar la revisión
remota que el cliente acaba de verificar. Esta sección es normativa para `SCDK-M132` y no reabre
la decisión de rewrap de la misma KEK.

1. Cada operación comienza con un `GET /vault/keys` fresco. La caché local no puede proporcionar
   por sí sola la base de una nueva mutación. La respuesta debe contener material válido y un
   `ETag` fuerte, no débil, no vacío y conservado literalmente con sus comillas. El `ETag` es
   opaco: Android no lo parsea, reconstruye, normaliza ni sustituye por `*`.
2. El cliente conserva conjuntamente, hasta terminar la operación, `materialBase`,
   `wrapperBase = materialBase.kekEncMaster` y `etagBase = ETag` del mismo `GET`. La passphrase
   actual se verifica contra `wrapperBase` remoto y la KEK desenvuelta se compara con la KEK
   activa antes de construir `wrapperCandidato`.
3. El `PUT /vault/keys/master` envía exactamente un header `If-Match` con el valor literal de
   `etagBase` y el body ya definido, que contiene únicamente `newKekEncMaster`.
   No son válidos un ETag débil, varios valores, una lista de valores, `*` o un ETag tomado de una
   lectura antigua. El wrapper candidato no se persiste antes de la confirmación; si el GET fresco
   demuestra que la caché estaba obsoleta y la credencial remota es válida, se sincroniza primero
   el wrapper base autoritativo.
4. Un `200` confirma la mutación únicamente si devuelve un ETag fuerte nuevo; el cliente captura
   ese ETag como confirmación y persiste solo el nuevo `kekEncMaster`. Como cada operación futura
   empieza con otro GET fresco, el ETag no se reutiliza ni se persiste como precondición futura.
   Si dos clientes usan el mismo ETag base, la operación CAS atómica produce exactamente un
   ganador (`200`) y un perdedor
   (`412 Precondition Failed`). El perdedor recibe un resultado tipado de conflicto, no muestra
   éxito y no repite el `PUT` a ciegas.
5. Un `412`, un timeout, una conexión interrumpida o una respuesta ambigua exige un `GET` de
   reconciliación. Hasta completarlo se conservan `wrapperBase`, `wrapperCandidato` y `etagBase`.
   La reconciliación clasifica el resultado así:

   - Si el wrapper remoto coincide exactamente con `wrapperCandidato`, se considera aplicado:
     se actualiza únicamente `kekEncMaster` en la caché, se confirma la operación y se mantiene la
     KEK y el vault desbloqueados.
   - Si coincide exactamente con `wrapperBase`, no se aplicó: no se muestra éxito, no se repite
     automáticamente el `PUT` y un nuevo intento explícito debe comenzar con otro `GET`.
   - Si es un tercer wrapper, otro dispositivo ganó: tras verificar que el resto del material
     remoto coincide byte-for-byte con la base, se actualiza únicamente `kekEncMaster`, se
     invalida la autoridad de la KEK en memoria, se zeroizan la KEK y las claves derivadas mediante
     lock, y el vault queda bloqueado. Se conserva la sesión autenticada,
     `kekEncRecovery`, items, drafts y checkpoints; el siguiente acceso exige la passphrase vigente
     o la recovery key.

6. Si el `GET` de reconciliación falla o devuelve material inválido, el resultado permanece
   indeterminado: se invalida la caché de `kekEncMaster`, se zeroiza la KEK y las claves derivadas
   mediante lock, se bloquea el vault y se exige conexión para reconciliar. Nunca se eliminan
   `kekEncRecovery`, items, drafts ni checkpoints.
7. Un `400` por precondición malformada y un `428` por ausencia de `If-Match` son errores de
   contrato y no se reintentan automáticamente. Un `404` durante el cambio no permite declarar
   éxito. Los fallos transitorios y de transporte siguen la política de reconciliación de este ADR;
   ninguno autoriza a reemitir el `PUT` sin un `GET` válido.
8. Interacción con `ADR-0001-VAULT-AUTO-LOCK` y `SCDK-M131`: si la reconciliación identifica un
   tercer wrapper o permanece indeterminada, el enrolamiento quick unlock local, exclusivo del
   dispositivo y la cuenta, se invalida best-effort. El vault queda bloqueado y la política de
   desbloqueo queda en `ManualOnly`, sin relanzar automáticamente el prompt del sistema. Si la
   limpieza local se completa, el siguiente desbloqueo exige la passphrase remota vigente o la
   recovery key; un fallo de almacenamiento durante esa limpieza queda registrado como riesgo
   residual y no evita el lock ni autoriza éxito. La sesión autenticada se conserva. Un resultado
   que confirma `wrapperCandidato` o conserva `wrapperBase` no elimina ni invalida el enrolamiento
   quick unlock.
9. Al entrar en Unlock tras un tercer wrapper, el cliente presenta una explicación sanitizada y de
   un solo uso antes de ofrecer quick unlock. La explicación distingue un cambio remoto confirmado
   de una reconciliación indeterminada, aclara que solo se ha bloqueado el vault y que la sesión
   autenticada continúa activa. Esta causa es process-local: no contiene secretos, no se persiste y
   no aparece en auto-lock, `Lock now` ni en un desbloqueo ordinario.

## Justificación

El rewrap mantiene la cadena criptográfica definida por v1 y convierte el cambio de passphrase en
una actualización acotada de material de claves. El orden remoto primero evita que una caché local
sea presentada como verdad antes de la confirmación del backend. La lectura de reconciliación
distingue un cambio aplicado de uno no aplicado; cuando no es posible distinguirlos, eliminar el
wrapper maestro cacheado, zeroizar la KEK y bloquear el vault evita aceptar una relación de
credenciales no demostrada sin destruir los datos cifrados.

## Consecuencias

### Positivas

- No se recifran items ni se rotan KEK o DEKs.
- La respuesta perdida tiene una ruta observable de reconciliación.
- El servidor y la caché local no se tratan como confirmados en órdenes incompatibles.
- La recuperación incierta es fail-closed y conserva el material cifrado recuperable.

### Negativas

- El flujo puede requerir un `GET /vault/keys` adicional.
- Una pérdida de conectividad durante la reconciliación puede bloquear el vault hasta recuperar
  conexión y completar una reconciliación online.
- La actualización local requiere una escritura atómica y tratamiento explícito de errores de
  storage.

## Riesgos y mitigaciones

| Riesgo                                                           | Impacto | Mitigación                                                                 | Señal                                                            |
|------------------------------------------------------------------|---------|----------------------------------------------------------------------------|------------------------------------------------------------------|
| La caché local se actualiza antes de la confirmación remota      | Alto    | Orden remoto primero y persistencia atómica posterior                      | Test de orden y caché inalterada ante rechazo                    |
| La respuesta perdida se interpreta como fallo o éxito sin prueba | Alto    | `GET /vault/keys` y comparación exacta de wrappers                         | Tests de respuesta perdida, wrapper anterior, nuevo y divergente |
| Dos clientes sobrescriben la misma revisión                      | Alto    | CAS atómico con ETag opaco e `If-Match` literal; `412` tipado              | Test determinista de dos clientes y carrera con un solo ganador  |
| Se genera una KEK nueva durante el cambio                        | Alto    | Invariante de igualdad de KEK y tests de DEKs/payloads                     | Snapshot criptográfico antes/después sin registrar secretos      |
| El material maestro incierto queda disponible                    | Alto    | Borrado del wrapper maestro cacheado, zeroize y lock                       | Test de estado Locked y ausencia de caché maestra                |
| Un retry crea una mutación criptográfica distinta                | Medio   | Reintentar solo estados transitorios y no regenerar la operación candidata | Test de retry con misma operación y nonce                        |

## Compatibilidad y migración

La decisión usa el endpoint existente `PUT /vault/keys/master` y el `GET /vault/keys` existente;
no cambia APIs, esquemas de storage, `payloadVersion` ni el formato de los items. Los wrappers
v1 existentes siguen siendo válidos hasta que un cambio confirmado sustituya `kekEncMaster`.

No hay migración de datos. No se rota la KEK ni se recifran payloads. Un rollback de la aplicación
no revierte un cambio remoto de passphrase; la aplicación debe seguir tratando el wrapper remoto
confirmado como autoridad y nunca sobrescribirlo con una caché antigua sin reconciliación.

## Seguridad y privacidad

- Passphrases, `MASTER_KEY`, KEK, DEKs, plaintext y buffers derivados son secretos efímeros y no
  se escriben en logs, errores, analytics, snapshots, saved state ni reportes.
- El request solo transporta el wrapper cifrado de la KEK. No se envían credenciales raíz ni
  material criptográfico en claro.
- El borrado ante resultado indeterminado afecta al material maestro cacheado, no a los items,
  drafts, checkpoints ni al wrapper de recovery cifrado.
- El zeroizado es best-effort y debe cubrir todas las copias mutables bajo control de la
  implementación.

## Tests requeridos

- Unitario: passphrase actual válida e inválida; comparación de la KEK desenvuelta con la activa;
  mismos salt y parámetros KDF; nonce nuevo; misma KEK antes/después; zeroizado best-effort.
- Contract test: `PUT /vault/keys/master` contiene únicamente `kekEncMaster` y nunca passphrases o
  claves.
- Integración: éxito remoto seguido de persistencia local; `GET` de confirmación exacta; respuesta
  perdida con wrapper nuevo, wrapper anterior, wrapper divergente, material inválido y transporte
  no disponible.
- Integración de invariantes: `kekEncRecovery`, DEKs, `wrappedDEK`, payloads, `payloadVersion`,
  `itemRevision`, `logicalItemId`, drafts y checkpoints son idénticos antes y después.
- Error handling: 408, 429, 5xx, timeout y transporte son reintentables según su contexto;
  validación, protocolo, integridad y credencial incorrecta son terminales; nunca se reintenta una
  mutación con una identidad criptográfica nueva.
- Concurrencia: dos clientes con el mismo ETag producen un único `200` y un `412`; se cubren
  revisión obsoleta secuencial, carrera real, conflicto y reconciliación con candidato, base y
  tercer wrapper, incluido el fallo de reconciliación.
- Seguridad: ante resultado remoto indeterminado, caché maestra ausente, KEK zeroizada y vault
  bloqueado, sin borrar datos cifrados.

## Enlaces

- Spec de hardening: [SPEC-HARDENING-V1](../../specs/features/hardening-resilience-v1.md),
  requisito [SEC-CRYPTO-002](../../specs/features/hardening-resilience-v1.md#sec-crypto-002--cambio-de-passphrase-mediante-rewrap).
- Crypto: [SPEC-CRYPTO-V1](../crypto-v1.md).
- Payload: [SPEC-SECURE-ITEM-PAYLOAD-V1](../secure-item-payload-v1.md).
- Contrato de
  claves: [SPEC-OPENAPI-VAULT-KEY-MATERIAL](../openapi-vault-key-material-contract-integration.md).
- Storage: [SPEC-STORAGE](../storage_decision.md).
- Lifecycle y quick unlock: [ADR-0001-VAULT-AUTO-LOCK](./ADR-0001-VAULT-AUTO-LOCK.md), implementado
  en `SCDK-M131`.
- Tasks: `SCDK-M111`, `SCDK-M123`, `SCDK-M124` y `SCDK-M132`; entrega backend relacionada:
  `SCDK-B55`; dependencia histórica: `SCDK-M109`.
