# ADR-0001-VAULT-AUTO-LOCK — Auto-lock, ciclo de vida y quick unlock del vault

## Metadata

| Campo              | Valor                                                                                           |
|--------------------|-------------------------------------------------------------------------------------------------|
| ID                 | ADR-0001-VAULT-AUTO-LOCK                                                                        |
| Estado             | ACCEPTED                                                                                        |
| Fecha              | 2026-08-10                                                                                      |
| Owner              | Security owner humano                                                                           |
| Reemplaza          | N/A                                                                                             |
| Specs relacionadas | [SPEC-HARDENING-V1](../../specs/features/hardening-resilience-v1.md), requisitos SEC-SESSION-001 y SEC-SESSION-002 |
| Tasks relacionadas | SCDK-M110, SCDK-M131; dependencia SCDK-M109                                                     |

El owner humano aceptó este ADR el 2026-08-10 y promovió biometría y Android Keystore al MVP. Esta
es la decisión normativa para las implementaciones de auto-lock, lifecycle y quick unlock.

## Contexto

VaultSessionManager ya puede bloquear el vault y eliminar best-effort la KEK de memoria, pero la
aplicación no tiene una política única que conecte ese comportamiento con el lifecycle del proceso.
Una Activity puede recrearse por configuración, navegación o presión del sistema sin que eso
signifique que el proceso haya pasado a background. Además, usar la hora de pared para medir un
timeout permite que cambios manuales o automáticos del reloj alteren el periodo de exposición.

SafeCube separa la sesión autenticada de la sesión desbloqueada del vault. Los tokens pueden
mantener la cuenta autenticada mientras el vault permanece bloqueado, y los items, drafts y
checkpoints cifrados deben continuar disponibles para una sesión posterior de unlock.

La política original exigía passphrase después de cada process death. Eso conserva seguridad, pero
degrada la experiencia en dispositivos sin un sensor biométrico operativo y confunde tres conceptos:
login de cuenta, estado Locked del vault y método usado para autorizar un nuevo unlock. El MVP debe
mantener las dos sesiones separadas y ofrecer un unlock local rápido sin almacenar la passphrase ni
mantener la KEK activa en memoria.

## Problema

Se necesita una política inequívoca para decidir cuándo el vault pasa de desbloqueado a bloqueado,
cómo se mide el plazo, qué ocurre durante una recreación de Activity, qué estado se restaura tras
process death, qué datos se conservan y qué credencial puede autorizar el siguiente unlock. La
decisión debe evitar tanto la permanencia indefinida de la KEK como un tercer PIN propio o la
introducción repetida de la passphrase cuando Android ya ofrece una raíz de confianza local.

## Opciones consideradas

### Opción A — Bloquear en cada Activity.onStop

- Ventajas: implementación local y sencilla.
- Costes/riesgos: una recreación de Activity puede limpiar la KEK aunque el proceso siga en primer
  plano; distintas Activities podrían aplicar reglas distintas.
- Impacto en seguridad: reduce exposición, pero no representa correctamente la frontera del proceso.
- Impacto en migración: requiere corregir una política distribuida entre pantallas.

### Opción B — Medir el plazo con fecha/hora de pared

- Ventajas: API familiar y fácil de inspeccionar.
- Costes/riesgos: cambios de zona horaria, sincronización NTP o ajustes manuales pueden adelantar o
  retrasar el bloqueo.
- Impacto en seguridad: puede prolongar de forma no controlada la vida de la KEK.
- Impacto en migración: ningún beneficio de compatibilidad que compense la semántica inestable.

### Opción C — Lifecycle de proceso y reloj monotónico con opciones cerradas

- Ventajas: distingue background real de recreación de Activity, mide duración transcurrida de forma
  estable y permite una política explícita y testeable.
- Costes/riesgos: requiere observar lifecycle de proceso y coordinar un deadline con el gestor de
  sesión.
- Impacto en seguridad: limita la permanencia de la KEK y bloquea de forma determinista al alcanzar
  el plazo.
- Impacto en migración: puede adoptar Immediately como fallback sin modificar payloads ni sesiones
  existentes.

### Opción D — No bloquear automáticamente o cerrar también la sesión de cuenta

- Ventajas: la primera opción reduce fricción; la segunda simplifica el modelo de estados.
- Costes/riesgos: Never deja la KEK en memoria sin límite; logout por background obliga a
  autenticarse de nuevo y mezcla dos políticas independientes.
- Impacto en seguridad: Never no es aceptable para v1; logout automático no aporta una protección
  proporcional y puede provocar limpiezas de cuenta no solicitadas.
- Impacto en migración: ambas opciones contradicen el producto y los contratos de sesión/local
  cleanup.

### Opción E — Quick unlock con un PIN propio de SafeCube

- Ventajas: funciona sin sensor biométrico y puede ser más corto que la passphrase.
- Costes/riesgos: añade una tercera credencial, tiene baja entropía, requiere una política propia de
  intentos y puede ser atacado offline si su diseño no queda anclado a hardware.
- Impacto en seguridad: crea una falsa separación si el usuario reutiliza el PIN del dispositivo y
  desplaza validación sensible hacia la aplicación.
- Impacto en migración: añade UI, persistencia y recuperación específicas sin aprovechar la
  credencial segura que el sistema ya gestiona.

### Opción F — Quick unlock con Android Keystore y prompt del sistema

- Ventajas: presenta un único control de unlock, permite biometría fuerte o credencial segura del
  dispositivo, mantiene la passphrase como recuperación y usa una clave no exportable para envolver
  la KEK.
- Costes/riesgos: confiar en la credencial del dispositivo amplía el threat model a quien conozca
  ese PIN, patrón o password; la disponibilidad y nivel hardware varían entre dispositivos.
- Impacto en seguridad: la KEK activa sigue siendo memory-only; solo persiste cifrada y cada unwrap
  requiere autorización del sistema.
- Impacto en migración: exige un envelope local versionado, exclusión de backup y manejo explícito
  de invalidación, pero no cambia backend, KDF, payloads ni sync.

## Decisión

SafeCube adopta las opciones C y F con las siguientes invariantes implementables. La opción E queda
rechazada para v1: no existe PIN propio de SafeCube.

### 1. Sesiones independientes

- Account session representa la autenticación de cuenta y conserva los tokens mientras sigan siendo
  válidos. Auto-lock no la termina.
- Vault session representa exclusivamente si la KEK está disponible para operar con el vault:
  Locked o Unlocked.
- Auto-lock, process death y Lock now cambian Vault session a Locked, no ejecutan logout
  automático ni cambian el timeout de sesión del backend.
- Si existe account session válida pero el vault está Locked, la raíz protegida de navegación es
  Unlock. Si no existe account session válida, la raíz es Login.

### 2. Métodos de unlock y enrolamiento local

- Passphrase es la credencial raíz del vault y el fallback obligatorio. No se persiste, no se
  sustituye por biometría y sigue siendo necesaria para enrolar de nuevo un dispositivo cuando el
  quick unlock no está disponible.
- Quick unlock es opcional y solo se activa después de una account session válida y un unlock
  exitoso mediante passphrase. No inicia ni refresca la account session.
- El usuario percibe una única operación Unlock. El prompt del sistema admite como alternativas
  biometría fuerte o la credencial segura del dispositivo. SafeCube no crea, solicita, valida ni
  almacena un PIN propio.
- La implementación genera una clave de wrapping no exportable en Android Keystore, asociada a la
  cuenta local y autorizada por uso. Con ella envuelve la KEK mediante cifrado autenticado y
  persiste únicamente un envelope local versionado y los metadatos no secretos imprescindibles.
- La realización v1 de `SCDK-M131` fija el envelope como AES-256-GCM, versión 1, con nonce de 96
  bits y tag de 128 bits. El AAD se deriva de la cuenta local y del propósito de KEK, por lo que un
  envelope no puede reutilizarse para otra cuenta. El alias de Keystore se deriva por cuenta y no
  contiene el identificador de cuenta en claro.
- La autorización de la clave es por uso (timeout `0`) y su política admite
  `BIOMETRIC_STRONG | DEVICE_CREDENTIAL`. Unlock y enrolamiento presentan un único prompt del
  sistema para esas alternativas; la operación usa el `Cipher` devuelto autenticado por dicho
  prompt y rechaza callbacks sin ese resultado criptográfico.
- La KEK en claro solo existe en memoria durante Vault session Unlocked. La clave Keystore, el
  envelope y sus nonces nunca se sincronizan, envían al backend, incluyen en backup/device transfer
  ni restauran mediante navegación.
- Si el dispositivo no ofrece biometría fuerte ni credencial segura compatible, no tiene un lock
  screen seguro o Keystore no puede aplicar la política, quick unlock no se ofrece. Unlock continúa
  disponible mediante passphrase.
- Cancelar o fallar el prompt conserva Locked. Una clave invalidada o un envelope corrupto,
  ausente o no soportado falla en cerrado, elimina únicamente el enrolamiento inutilizable y exige
  passphrase para volver a enrolar.
- Logout, cambio de cuenta y eliminación local de cuenta destruyen la clave Keystore y el envelope
  asociados. Auto-lock, Lock now y process death eliminan la KEK activa pero conservan el
  enrolamiento para un unlock posterior.
- Tras un unlock por passphrase, la oferta se muestra una vez por cuenta y requiere consentimiento
  explícito. Settings permite activar, desactivar o re-enrolar posteriormente; cancelar el prompt
  de enrolamiento deja el vault Unlocked, mientras que cancelar o fallar un prompt de unlock deja
  el vault Locked. La corrupción o invalidación borra solo el enrolamiento inutilizable y conserva
  la decisión de oferta para que Settings permita recuperarlo mediante passphrase.

### 3. Opciones de configuración

La configuración local de auto-lock admite exactamente estos valores:

| Valor       | Duración desde el background de proceso     |
|-------------|---------------------------------------------|
| Immediately | 0 segundos; bloqueo al entrar en background |
| 30 seconds  | 30 segundos                                 |
| 1 minute    | 60 segundos                                 |
| 5 minutes   | 300 segundos                                |
| 15 minutes  | 900 segundos                                |

- Immediately es el valor por defecto para instalaciones nuevas, valores ausentes y valores
  persistidos inválidos.
- Never, valores superiores a 15 minutos y cualquier alias de duración indefinida no se ofrecen ni
  se aceptan.
- Cambiar la configuración no es por sí mismo un evento de background ni un motivo para bloquear
  mientras la app está en foreground. El nuevo valor se usa en la siguiente transición real a
  background.
- Si el usuario necesita bloquear antes de esa transición, usa la acción manual Lock now.

### 4. Reloj y deadline

- El plazo se mide exclusivamente con un reloj monotónico de tiempo transcurrido, no con fecha/hora
  de pared, zona horaria ni timestamp remoto.
- Al observar una transición válida de foreground a background se captura t0 con el reloj
  monotónico. Para una opción distinta de Immediately, el deadline es t0 más la duración
  configurada.
- La implementación debe comparar el reloj monotónico al disparar el deadline y también al volver a
  foreground. Si el proceso estuvo suspendido y el callback no se ejecutó, el chequeo al volver
  bloquea antes de exponer contenido protegido.
- El reloj se abstrae para tests deterministas. La fuente Android de producción debe ser una medida
  monotónica de tiempo transcurrido, como SystemClock.elapsedRealtime, nunca currentTimeMillis.

### 5. Lifecycle de proceso

- El evento que inicia el plazo es el background del proceso observado mediante lifecycle de
  proceso,
  no Activity.onPause, Activity.onStop ni la recreación de una Activity.
- Una recreación de Activity mientras el proceso sigue en foreground no bloquea el vault ni reinicia
  el deadline.
- Al volver a foreground antes del deadline, se cancela el deadline pendiente y el vault continúa
  Unlocked.
- Si se alcanza o supera el deadline, el vault pasa a Locked antes de que una pantalla o una
  operación protegida vuelva a usar la KEK.
- Con Immediately, la entrada del proceso en background ejecuta el bloqueo sin esperar un intervalo.
- Una nueva entrada en background después de haber vuelto a foreground inicia un nuevo plazo
  completo;
  no se acumula tiempo de foreground.

### 6. Secuencia de bloqueo

Al alcanzar el timeout o ejecutar Lock now, la transición lógica debe:

1. impedir nuevas operaciones que requieran el vault Unlocked;
2. cancelar las operaciones en curso que requieran la KEK;
3. retirar plaintext visible de pantallas, editores y buffers accesibles;
4. zeroizar best-effort la KEK y sus copias accesibles en memoria;
5. establecer Vault session como Locked y Unlock como raíz de navegación.

El bloqueo no elimina tokens, items cifrados, drafts ni checkpoints. Una mutación interrumpida
mantiene su identidad y sigue las reglas de persistencia/reconciliación de SPEC-VAULT-SYNC-V2; no se
crea una nueva mutación para sustituir una operación cancelada.

Lock now está disponible mientras el vault está Unlocked, ejecuta la misma secuencia sin esperar al
deadline y no termina la account session.

### 7. Process death y cold start

- Process death equivale siempre a Vault session Locked, aunque todavía existan tokens persistidos o
  el proceso muera antes de que venza el timeout configurado.
- Un estado restaurable que indique Unlocked nunca puede reconstituir la KEK ni abrir directamente
  contenido protegido. El nuevo proceso debe llevar la navegación a Unlock si la account session
  sigue válida o a Login en caso contrario.
- En Unlock, un enrolamiento local válido permite iniciar un nuevo prompt del sistema; si no existe
  o falla, el usuario puede elegir passphrase. Process death obliga a un nuevo unlock, no
  necesariamente a escribir la passphrase.
- Items cifrados, drafts y checkpoints se conservan para el siguiente unlock. Tokens y su lifecycle
  siguen gobernados por los contratos de auth y sync.
- No se usa WorkManager, un servicio background ni una tarea externa para mantener la política. Si
  el
  proceso muere, la regla de process death garantiza el bloqueo en el siguiente arranque.

### 8. Diferencia entre eventos

| Evento                                | Resultado                                                                                   |
|---------------------------------------|---------------------------------------------------------------------------------------------|
| Cambio de configuración en foreground | Actualiza el valor para la siguiente entrada en background; no bloquea por sí solo.         |
| Recreación de Activity                | No bloquea si el proceso continúa en foreground.                                            |
| Background real del proceso           | Inicia el deadline; Immediately bloquea en ese instante.                                    |
| Regreso antes del deadline            | Cancela el deadline y conserva el vault Unlocked.                                           |
| Deadline alcanzado                    | Ejecuta la secuencia de bloqueo y establece Unlock.                                         |
| Process death                         | Vault Locked siempre; Login o Unlock según account session; quick unlock o passphrase autorizan la nueva apertura. |
| Lock now                              | Ejecuta bloqueo inmediato sin logout.                                                       |
| Logout                                | Termina account session y aplica la limpieza local definida por auth/sync; no es auto-lock. |

## Justificación

La política de proceso evita falsos bloqueos por recreación de Activity y concentra la frontera de
seguridad en el lifecycle que representa la visibilidad global de la aplicación. El reloj monotónico
hace que un plazo de 30 segundos signifique siempre 30 segundos transcurridos, incluso si cambia la
hora del dispositivo. Immediately como default limita la exposición en una instalación nueva y las
opciones cerradas evitan que v1 tenga una configuración indefinida.

Separar Vault session de Account session conserva el modelo de producto y permite seguir autenticado
sin conservar plaintext ni KEK en memoria. Mantener items, drafts y checkpoints cifrados evita
pérdida de datos y deja la recuperación a los contratos existentes. Android Keystore permite
conservar únicamente una KEK envuelta y exigir presencia del usuario en cada unwrap; el prompt del
sistema evita añadir un tercer PIN y ofrece credencial del dispositivo cuando el sensor no existe o
no está operativo.

## Consecuencias

### Positivas

- La KEK no permanece desbloqueada indefinidamente.
- La política es idéntica para todas las pantallas y no depende de Activity concreta.
- Process death, background, configuración y Lock now tienen resultados observables distintos.
- La política se puede probar con un reloj monotónico falso y lifecycle de proceso controlado.
- El desbloqueo cotidiano puede completarse con un único prompt local y continuar funcionando
  offline.
- No se introducen cambios en API, wrapper maestro remoto, payload, sync o datos cifrados del vault.

### Negativas

- Immediately puede requerir unlock frecuente en instalaciones nuevas.
- La observación de lifecycle de proceso y la coordinación de cancelación añaden complejidad en app
  y core:vault.
- Quick unlock añade un envelope local, una clave Keystore por cuenta y manejo explícito de
  compatibilidad, invalidación y cleanup.
- Permitir la credencial segura del dispositivo implica que una persona que la conozca puede
  autorizar el unlock local; la UI debe explicarlo antes del enrolamiento.
- El sistema operativo puede suspender el proceso; por eso el bloqueo se comprueba también en
  foreground y process death siempre prevalece.

## Riesgos y mitigaciones

| Riesgo                                                     | Impacto | Mitigación                                                                        | Señal                                                  |
|------------------------------------------------------------|---------|-----------------------------------------------------------------------------------|--------------------------------------------------------|
| Una Activity recreada provoca un bloqueo falso             | Medio   | Usar lifecycle de proceso y no callbacks de Activity                              | Test de cambio de configuración conserva Vault session |
| El reloj de pared altera el plazo                          | Alto    | Abstraer reloj monotónico y prohibir wall-clock                                   | Test con salto de fecha no cambia deadline             |
| Un callback llega después de process death                 | Alto    | Tratar cada cold start como Locked y descartar estado Unlocked                    | Test de process death exige Unlock                     |
| Una operación usa la KEK durante el bloqueo                | Alto    | Bloquear nuevas operaciones, cancelar protegidas y limpiar plaintext en secuencia | Test de carrera de lock y operación                    |
| Un valor persistido mantiene Never o una duración inválida | Alto    | Validar enum cerrado y mapear ausencia/valor inválido a Immediately               | Test de configuración inválida                         |
| Se persiste KEK o passphrase en claro                      | Crítico | Persistir solo envelope autenticado bajo clave Keystore no exportable              | Inspección de storage/backup y test de envelope        |
| Sensor ausente o averiado bloquea al usuario               | Alto    | Admitir credencial segura del dispositivo y passphrase como fallback               | Test sin biometría y cancelación del prompt            |
| Clave Keystore o envelope quedan inválidos                 | Alto    | Fail-closed, borrar solo enrolamiento local y exigir passphrase                     | Tests de invalidación, corrupción y re-enrolamiento    |
| Logout deja quick unlock utilizable                        | Crítico | Destruir alias y envelope por cuenta antes de completar cleanup local               | Test de logout/cambio de cuenta                        |

## Compatibilidad y migración

- El ADR no cambia endpoints, headers, contratos OpenAPI, primitives crypto, payloads, itemRevision,
  drafts ni checkpoints.
- La implementación añade un envelope local versionado por cuenta. No forma parte de Room como
  source of truth del contenido, no se sincroniza y debe estar excluido de backup y device transfer.
- Instalaciones existentes sin una preferencia de auto-lock adoptan Immediately de forma segura.
- Un valor local fuera del enum cerrado se trata como inválido y se sustituye por Immediately; no se
  migra a Never.
- La preferencia de timeout es configuración local no sensible. Su mecanismo de persistencia y UI se
  implementará en las tareas posteriores sin trasladar secretos al estado restaurable.
- Una instalación existente no queda enrolada automáticamente. Debe completar passphrase y aceptar
  explícitamente el quick unlock antes de crear su clave Keystore y envelope local.
- No hay migración destructiva ni rollback de datos. Revertir la implementación conserva tokens,
  items cifrados, drafts y checkpoints; la ausencia de política vuelve a aplicar Immediately.

## Seguridad y privacidad

- La KEK, plaintext y copias temporales se limpian best-effort conforme a SPEC-CRYPTO-V1 y
  SEC-SESSION-001.
- La passphrase y MASTER_KEY nunca se almacenan para quick unlock. La KEK envuelta solo puede
  persistirse en el envelope local definido por SEC-SESSION-002.
- Tokens no se eliminan por auto-lock, pero tampoco conceden acceso al plaintext sin un nuevo
  unlock.
- Biometría y credencial del dispositivo autorizan una operación criptográfica local; no equivalen
  a login, refresh ni recuperación de cuenta.
- No se registran KEK, tokens, passphrases, plaintext, payloads, deadlines sensibles o contenido del
  vault en logs, tests, reportes o eventos de observabilidad.
- Lock now y timeout no ejecutan cleanup de cuenta; logout mantiene la política ya definida por
  SPEC-VAULT-SYNC-V2.

## Tests requeridos

- Validar enum, default Immediately, ausencia de Never y fallback de valores inválidos.
- Verificar con reloj monotónico controlado que 30 segundos, 1, 5 y 15 minutos bloquean en el
  deadline y que un salto de wall-clock no altera el resultado.
- Verificar que background de proceso inicia el plazo y volver antes del deadline mantiene Unlocked.
- Verificar que la recreación de Activity no bloquea ni reinicia el plazo.
- Verificar que timeout, Immediately y Lock now cancelan operaciones protegidas, limpian plaintext,
  zeroizan la KEK y navegan a Unlock.
- Verificar que tokens, items cifrados, drafts y checkpoints sobreviven al bloqueo.
- Verificar que process death nunca restaura Unlocked y que el cold start elige Login o Unlock
  según account session.
- Verificar enrolamiento tras passphrase, unwrap autorizado por biometría fuerte o credencial del
  dispositivo y fallback a passphrase cuando quick unlock no está disponible.
- Verificar que cancelación, fallo, invalidación o corrupción conservan Locked y no muestran
  plaintext ni eliminan contenido cifrado.
- Verificar que no existe PIN propio, que la KEK/passphrase no aparecen en storage y que el envelope
  y alias se eliminan en logout, cambio o eliminación local de cuenta.
- Verificar en dispositivo sin biometría que la credencial segura permite quick unlock; sin
  credencial segura, solo se ofrece passphrase.
- Verificar que cambiar configuración en foreground no bloquea por sí solo y afecta la siguiente
  transición a background.
- Verificar que no se ejecuta logout, WorkManager ni servicio background como consecuencia del
  auto-lock.

## Enlaces

- Specs: [SPEC-HARDENING-V1](../../specs/features/hardening-resilience-v1.md),
  [SPEC-CRYPTO-V1](../crypto-v1.md), [SPEC-VAULT-SYNC-V2](../vault-sync-versioning-v2.md)
- ADRs: N/A; este es el ADR vigente ACCEPTED
- Código esperado: app, core:vault, core:storage, feature:vault
- Tests esperados: tests de VaultSessionManager, policy/lifecycle, Keystore adapter, navegación,
  quick unlock y process death
- Task: SCDK-M110
