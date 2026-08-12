# ADR-0003-SENSITIVE-DATA-SURFACES — Política de exposición y persistencia de datos sensibles

## Metadata

| Campo              | Valor                                                                    |
|--------------------|--------------------------------------------------------------------------|
| ID                 | `ADR-0003-SENSITIVE-DATA-SURFACES`                                       |
| Estado             | `ACCEPTED`                                                               |
| Fecha              | `2026-08-12`                                                             |
| Owner              | Security owner humano                                                    |
| Reemplaza          | `N/A`                                                                    |
| Specs relacionadas | `SPEC-HARDENING-V1`, `SPEC-PRODUCT-V1`, `SPEC-CRYPTO-V1`, `SPEC-STORAGE` |
| Tasks relacionadas | `SCDK-M112` (dependencia: `SCDK-M109`)                                   |

El owner humano aceptó este ADR el 2026-08-12. Esta es la decisión normativa para las
implementaciones de exposición y persistencia de datos sensibles.

## Contexto

SafeCube trata passphrases, recovery keys, material de claves, payloads y parte del estado de
navegación como datos sensibles. `SPEC-HARDENING-V1` exige una política única para que esos datos
no lleguen a backups, capturas, logs, clipboard ni estado restaurable.

La inspección del estado actual encontró varias superficies pendientes de endurecer: el manifest
mantiene `android:allowBackup="true"`, las reglas de backup son plantillas sin exclusiones
efectivas, `core:network` instala `HttpLoggingInterceptor` con nivel `BODY` en debug, la Activity
no aplica `FLAG_SECURE` y la navegación mantiene la recovery key en `rememberSaveable` y en
argumentos de navegación. La pantalla de creación también transporta la recovery key mediante un
evento de UI y la pantalla de recovery la mantiene en estado de presentación.

La pantalla debe poder mostrar temporalmente una recovery key para que el usuario la guarde. Esta
necesidad no autoriza persistirla en claro ni convertirla en un argumento serializable. También
existe un caso acotado: si el proceso muere durante una inicialización de vault, la aplicación debe
poder reconciliar el mismo intento sin regenerar material ni perder la recovery key original.

## Problema

Hay que fijar una política transversal y verificable para todas las build types y todas las rutas
de la Activity, incluyendo el único registro transitorio cifrado que puede sobrevivir a process
death durante una inicialización pendiente.

## Opciones consideradas

### Opción A — Confiar en el cifrado del dispositivo y permitir backup

- Ventajas: menor configuración de manifest y compatibilidad con restauración automática.
- Costes/riesgos: expone datos cifrados, estado de navegación y futuros errores de configuración a
  canales de backup o device transfer.
- Impacto en seguridad: no proporciona una frontera única contra restauración accidental en otro
  dispositivo o cuenta.
- Impacto en migración: hace ambiguo qué datos pueden reaparecer después de una reinstalación.

### Opción B — Proteger solo pantallas y logs considerados sensibles

- Ventajas: reduce el impacto visual y permite excepciones por pantalla.
- Costes/riesgos: una navegación, diálogo o recents preview puede mostrar una superficie que no
  estaba clasificada correctamente.
- Impacto en seguridad: deja huecos en Activity, rutas nuevas y build types de diagnóstico.
- Impacto en migración: cada feature tendría que mantener una lista de excepciones.

### Opción C — Deny-by-default en plataforma, UI, logging y estado

- Ventajas: una política de Activity y build global reduce omisiones; la única excepción tiene
  envelope cifrado y ciclo de vida observable.
- Costes/riesgos: elimina backup y copy actions de v1, y puede bloquear la finalización si una
  eliminación segura no puede verificarse.
- Impacto en seguridad: evita que una superficie nueva herede exposición por defecto.
- Impacto en migración: no exige migrar items ni cambiar Room, payloads o APIs.

### Opción D — Persistir el intento pendiente en plaintext o Bundle

- Ventajas: recuperación sencilla tras recreación o process death.
- Costes/riesgos: Bundle, saved state, navegación y almacenamiento local pueden ser serializados,
  respaldados o inspeccionados fuera del control del vault.
- Impacto en seguridad: contradice la prohibición de plaintext persistente.
- Impacto en migración: crea datos sensibles incompatibles con una política posterior de backup.

### Opción E — Desactivar R8 o aplicar `-keep` globales

- Ventajas: evita inicialmente fallos de reflexión o serialización.
- Costes/riesgos: aumenta el artefacto, oculta contratos reales y conserva código sin relación con
  la necesidad.
- Impacto en seguridad: no protege secretos; amplía la superficie de análisis y no sustituye el
  control de logs o estado.
- Impacto en migración: dificulta endurecer release sin descubrir dependencias implícitas.

## Decisión

Se adopta la Opción C. La siguiente política es normativa.

### 1. Backup, cloud backup y device transfer

1. El manifest efectivo de la aplicación fija `android:allowBackup="false"`.
2. Se mantienen reglas explícitas para las generaciones Android que las consumen: las reglas de
   `dataExtractionRules` excluyen el dominio raíz tanto de `cloud-backup` como de
   `device-transfer`, y las reglas legacy de `full-backup-content` excluyen el dominio raíz.
   No se usan reglas basadas solo en comentarios o defaults de plantilla.
3. No se declara ninguna excepción `include` para Room, DataStore, preferencias, archivos, el
   artefacto de quick unlock ni el registro transitorio de inicialización. El registro transitorio
   debe estar excluido de backup y transferencia incluso si el sistema ignora alguna combinación
   de flags en una versión concreta.
4. La configuración mergeada de debug, release y variantes de prueba debe conservar la misma
   denegación. Ninguna build type puede reactivar backup para facilitar diagnóstico.

### 2. Screenshots, recents y ventanas de la Activity

1. La Activity raíz aplica `FLAG_SECURE` antes de mostrar contenido y lo conserva durante todo su
   ciclo de vida.
2. La protección cubre todas las rutas, estados, diálogos y pantallas de la aplicación. No existe
   un opt-out por feature ni una pantalla de recovery sin protección.
3. Los previews de recents y las capturas de ventanas protegidas no pueden mostrar contenido de
   SafeCube. No se añade una ruta especial de depuración que quite el flag.
4. El contrato se valida en debug, release y pruebas instrumentadas; no se considera suficiente
   inspeccionar solo la Activity en el estado inicial.

### 3. Logging y diagnósticos

1. Ningún build type instala logging HTTP de headers o bodies. En particular, no se permite
   `HttpLoggingInterceptor.Level.BODY`, `HEADERS` ni equivalente propio en debug, release o tests.
2. Queda prohibido registrar, directa o indirectamente, headers, bodies, tokens, passphrases,
   recovery keys, `MASTER_KEY`, KEK, DEK, plaintext, payloads, `displayHint` o IDs de items.
   También se prohíben URLs completas con parámetros, objetos serializados y mensajes de excepción
   que puedan contener esos datos.
3. El logging permitido se limita a eventos estructurados y no sensibles: código fijo de evento,
   categoría coarse de resultado, código HTTP sin body y duración agrupada. No incluye account IDs,
   item IDs, valores de campos ni datos de usuario.
4. Los errores que lleguen a UI se mapean a mensajes de recursos o códigos de estado; no se
   presentan excepciones crudas ni respuestas del backend.
5. La regla aplica a `Log`, `println`, interceptores, serializers, ViewModels, tests y cualquier
   logger de terceros. Los tests usan fixtures sintéticos que no representan secretos reales.

### 4. Clipboard

1. SafeCube no escribe programáticamente passwords, passphrases, recovery keys, payloads ni otros
   secretos al clipboard durante v1.
2. No se añaden acciones de copy-to-clipboard en recovery, unlock, create vault ni detail screens.
3. Una entrada iniciada por el usuario desde el teclado del sistema no se convierte en una
   operación de lectura o persistencia de SafeCube; su contenido tampoco se registra ni se copia a
   otro estado de la aplicación.

### 5. Campos sensibles y visualización

1. Todos los campos de password y passphrase usan visual transformation desde su creación y
   durante la edición. Ningún build type muestra esos valores por usar un componente de texto
   genérico.
2. La recovery key puede mostrarse únicamente en el flujo explícito de guardado, dentro de la
   Activity protegida. Su presentación es temporal y no autoriza copy action, log, saved state,
   argumento de navegación ni persistencia en claro.
3. Passphrases y recovery keys se limpian de las estructuras mutables de UI y de ViewModels cuando
   termina el flujo, se cancela, se bloquea el vault o la operación se descarta. El zeroizado de
   buffers es best-effort según `SPEC-CRYPTO-V1`.

### 6. Bundle, navegación y estado restaurable

1. No se colocan secretos ni datos que permitan reconstruirlos en `Bundle`, rutas serializables,
   argumentos de navegación, `SavedStateHandle`, `rememberSaveable` ni mecanismos equivalentes de
   saved state.
2. En particular, la recovery key nunca viaja como argumento de ruta ni como valor restaurable.
   El handoff entre create y recovery se realiza mediante memoria de proceso no serializable y se
   limpia tras confirmar, cancelar o abandonar el flujo.
3. Las rutas solo transportan enums, destinos y parámetros no sensibles estrictamente necesarios.
   No se transportan passphrases, recovery keys, material criptográfico, payloads, plaintext ni
   `displayHint`.
4. El estado efímero en memoria que sea imprescindible para mostrar un campo no se considera
   persistencia, pero debe desaparecer al destruirse el flujo. Ningún `ViewModel` puede optar por
   guardar sus valores sensibles en saved state.

### 7. Única excepción: registro transitorio de inicialización pendiente

Se permite un solo registro cifrado y excluido de backup por cuenta/dispositivo para recuperar una
inicialización de vault que aún no ha alcanzado un estado terminal.

1. El registro se escribe antes de iniciar el `POST /vault/keys` y se mantiene en una única
   operación serializada por cuenta. Contiene únicamente, dentro de un envelope autenticado, el
   candidato de material envuelto, los parámetros KDF/crypto necesarios para reconciliarlo, la
   recovery key necesaria para completar el flujo y el estado de la operación.
2. El registro nunca contiene passphrase, `MASTER_KEY`, KEK ni DEK en claro. Es un blob cifrado con
   una clave no exportable y ligada al dispositivo/cuenta; la clave y el blob no se incluyen en
   cloud backup ni device transfer. La implementación usa los envelopes y parámetros AEAD v1
   canónicos, sin introducir SQLCipher ni otra base de datos en este ADR.
3. La recovery key solo existe como parte del ciphertext persistido y como material efímero en
   memoria durante la pantalla protegida. No se permite duplicarla en columnas auxiliares, logs,
   navegación, Bundle, saved state o clipboard.
4. Tras process death, la aplicación detecta el registro sin restaurar plaintext. Solo después de
   una acción explícita de reanudar y de validar el contexto de cuenta/dispositivo descifra el
   contenido en memoria para reconciliar el mismo intento o mostrar la recovery key en la Activity
   protegida.
5. El reintento reutiliza exactamente el candidato persistido; no regenera recovery key, wrappers,
   salt ni material criptográfico. La lectura posterior sigue la política de bootstrap de
   `SPEC-HARDENING-V1`: candidato confirmado permite completar, vault inexistente permite repetir
   el mismo POST y material remoto distinto obliga a descartar el candidato.
6. El registro se elimina y se verifica ausente antes de considerar finalizado cualquiera de estos
   eventos:
    - el usuario confirma que guardó la recovery key;
    - la sesión se cierra o se hace logout;
    - el usuario descarta de forma segura el intento;
    - la reconciliación determina que el vault remoto ya contiene material distinto.
7. La eliminación incluye el ciphertext y, cuando el adapter lo permita, la clave dedicada del
   registro. Se verifica con lectura posterior o transacción equivalente. Si la eliminación no se
   puede confirmar, se limpian las copias en memoria, se bloquea el flujo y se muestra un error
   terminal de cleanup; no se continúa como si el secreto hubiera sido eliminado.
8. Process death por sí solo no elimina el registro: es precisamente la condición que permite
   recuperar una inicialización pendiente. Logout, cancelación explícita y resultado remoto
   incompatible sí son terminales y deben ejecutar el borrado definido arriba.

### 8. R8 y shrinker

1. R8/minification se mantiene habilitado en las variantes release según la configuración canónica
   del proyecto. La ofuscación no se trata como control de secreto ni se desactiva para resolver un
   problema de logging o persistencia.
2. Toda regla custom debe apuntar a una clase o miembro concreto y documentar el motivo verificable:
   reflexión requerida, serializer generado, Room, Hilt u otro boundary real. Las reglas se
   revisan junto con el test release que demuestra la necesidad.
3. No se aceptan `-keep` globales, wildcards de todos los paquetes, `-keep class ** { *; }`,
   `-keep public class *`, desactivación global de shrinking/obfuscation ni reglas equivalentes
   como sustituto de entender el contrato.
4. Las reglas de dependencias de terceros solo se conservan cuando el warning y la superficie
   afectada están identificados; un `-dontwarn` amplio no justifica mantener clases de la
   aplicación.

## Justificación

La denegación de backup y la protección de toda la Activity reducen las superficies de plataforma
que una feature individual podría olvidar. Retirar logging HTTP de todos los build types elimina
la vía más directa de exposición de credenciales y payloads. El bloqueo de clipboard y saved state
evita copias persistentes o restaurables, mientras que la única excepción cifrada conserva la
capacidad funcional de reconciliar una inicialización sin almacenar plaintext.

La excepción está limitada a un estado operacional concreto, excluida de backup y con borrado
verificable. De esta forma no se convierte en un almacén general de secretos ni en un segundo
protocolo de vault.

## Consecuencias

### Positivas

- La política se aplica de forma uniforme a debug, release, recents, navegación y almacenamiento.
- Una nueva pantalla hereda protección de Activity sin tener que recordar un opt-out.
- Los fallos durante bootstrap pueden recuperarse sin regenerar material ni persistir plaintext.
- R8 conserva solo contratos concretos y revisables.

### Negativas

- Se pierde la restauración automática de datos de la aplicación y no se ofrece copy action para
  recovery.
- Un fallo de almacenamiento al borrar el registro puede impedir finalizar el flujo hasta que se
  confirme el cleanup.
- La recuperación del registro transitorio es solo para el mismo contexto de dispositivo/cuenta;
  no es un mecanismo de transferencia entre dispositivos.

## Riesgos y mitigaciones

| Riesgo                                               | Impacto | Mitigación                                                                     | Señal                                                      |
|------------------------------------------------------|---------|--------------------------------------------------------------------------------|------------------------------------------------------------|
| Una variante reintroduce backup o device transfer    | Alto    | `allowBackup=false` más exclusiones explícitas y manifest merge tests          | Inspección de manifest efectivo por build type             |
| Una ventana omite `FLAG_SECURE`                      | Alto    | Flag en Activity raíz, sin opt-out y tests de todas las rutas                  | Test instrumentado de screenshots/recents                  |
| Un logger imprime un objeto sensible                 | Alto    | Sin logger HTTP, allowlist estructurada y fixtures sintéticos                  | Captura de logs en debug/release con búsqueda de patrones  |
| La recovery key reaparece en saved state             | Alto    | Rutas sin secreto, handoff de proceso no serializable y tests de process death | Bundle/SavedState inspection sin valores sensibles         |
| El registro transitorio queda tras terminar el flujo | Alto    | Borrado transaccional, lectura de ausencia y bloqueo ante fallo                | Tests de confirmación, logout, discard y remoto divergente |
| R8 se relaja con un keep global                      | Medio   | Revisión de reglas por clase y test estático de patrones prohibidos            | Release shrink verification                                |

## Compatibilidad y migración

Este ADR no cambia APIs, Room schema, payload envelope, KDF, AEAD, sync ni la semántica de items.
Las futuras modificaciones se limitan a manifest, recursos de backup, Activity, logging, UI state,
storage del registro transitorio y reglas R8.

La aplicación no puede garantizar el borrado retroactivo de una copia que el sistema haya creado
antes de aplicar esta política; desde la versión endurecida no crea nuevas copias ni restaura
secretos desde rutas no aprobadas. Items, drafts y checkpoints permanecen en Room según
`SPEC-STORAGE`, pero no se exportan mediante backup.

El registro transitorio no sustituye el almacenamiento canónico de items ni el material remoto. Un
rollback de runtime debe conservar el envelope cifrado y no reactivar logging de bodies o backup
por defecto.

## Seguridad y privacidad

- No se escriben secretos, payloads, IDs de items ni datos de usuario en logs, reportes, analytics,
  Bundle, rutas o saved state.
- La recovery key aparece solo durante el flujo explícito de guardado y en una Activity protegida;
  nunca se copia automáticamente ni se persiste en claro.
- El registro permitido es ciphertext autenticado, ligado a dispositivo/cuenta y excluido de
  backup/transfer. No se persiste passphrase, `MASTER_KEY`, KEK o DEK en claro.
- `FLAG_SECURE` y la política de backup son controles de plataforma; no sustituyen zeroize ni
  bloqueo del vault.

## Tests requeridos

- Manifest merge: `android:allowBackup="false"`, `dataExtractionRules` excluye cloud y device
  transfer, `fullBackupContent` excluye el dominio raíz y ninguna variante añade includes.
- Instrumentación: todas las rutas y estados de la Activity conservan `FLAG_SECURE`; recents y
  captura de pantalla no contienen contenido.
- Logging: debug, release y tests no instalan logging de headers/bodies; fixtures sintéticos no
  dejan en logs tokens, passphrases, recovery keys, payloads, `displayHint` ni IDs de items.
- Clipboard: no existe acción de copy ni llamada de escritura con datos sensibles.
- UI/state: password y passphrase usan visual transformation; Bundle, rutas,
  `SavedStateHandle` y `rememberSaveable` no contienen secretos después de recreación o process
  death.
- Registro transitorio: cifrado y excluido de backup; creado antes del POST; reutiliza el mismo
  candidato; recuperable tras process death; borrado verificado tras confirmación, logout,
  discard y material remoto divergente; fallo de borrado bloquea la finalización.
- Seguridad de persistencia: nunca se encuentra plaintext de recovery key, passphrase, MASTER_KEY,
  KEK o DEK en el registro ni en columnas auxiliares.
- R8: release shrink succeeds con reglas mínimas y no contiene patrones globales prohibidos; cada
  regla custom tiene una justificación revisable.

## Enlaces

- Hardening: [SPEC-HARDENING-V1](../../specs/features/hardening-resilience-v1.md), requisito
  [SEC-PRIVACY-001](../../specs/features/hardening-resilience-v1.md#sec-privacy-001--superficies-sensibles-y-estado-transitorio).
- Producto: [SPEC-PRODUCT-V1](../../specs/product/v1-product-brief.md).
- Crypto: [SPEC-CRYPTO-V1](../crypto-v1.md).
- Storage: [SPEC-STORAGE](../storage_decision.md).
- Lifecycle/quick unlock: [ADR-0001-VAULT-AUTO-LOCK](ADR-0001-VAULT-AUTO-LOCK.md).
- Rewrap: [ADR-0002-PASSPHRASE-REWRAP](ADR-0002-PASSPHRASE-REWRAP.md).
- Task: `SCDK-M112`; dependencia: `SCDK-M109`.
