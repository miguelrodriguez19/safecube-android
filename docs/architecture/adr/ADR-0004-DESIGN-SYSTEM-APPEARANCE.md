# ADR-0004-DESIGN-SYSTEM-APPEARANCE — Arquitectura del design system y apariencia local

## Metadata

| Campo              | Valor                                                      |
|--------------------|------------------------------------------------------------|
| ID                 | `ADR-0004-DESIGN-SYSTEM-APPEARANCE`                        |
| Estado             | `ACCEPTED`                                                 |
| Fecha              | `2026-10-11`                                               |
| Owner              | `maintainer/android-ui`                                    |
| Reemplaza          | `N/A`                                                      |
| Specs relacionadas | [`SPEC-UI-V1`](../../specs/features/ui-brand-motion-v1.md) |
| Tasks relacionadas | `SCDK-M136`, `SCDK-M138`, `SCDK-M145`                      |

## Contexto

SafeCube parte de un tema Material de plantilla ubicado en `app/presentation/theme`, con color
dinámico activado por defecto. `core:ui` contiene recursos compartidos y
`SecretOutlinedTextField`, pero todavía no posee tema, tokens ni modelos de apariencia. Settings
vive en `feature:vault` y las features ya dependen de `core:ui`; ninguna feature depende de `app`.

[`SPEC-UI-V1`](../../specs/features/ui-brand-motion-v1.md) y la
[`guía de marca v1`](../../design/brand-guide-v1.md) fijan identidad, defaults y límites: modo
`System`, color dinámico desactivado, apariencia local por dispositivo, conservación tras logout y
cambio de cuenta, fallback seguro y uso de abstracciones canónicas. La apariencia no es sensible,
pero la política vigente de backup excluye el dominio raíz y no admite excepciones de inclusión.

Este ADR solo define arquitectura y contratos. No mueve código, no crea preferencias ni modifica UI
de producción.

## Problema

Se necesita una arquitectura que permita:

- aplicar la preferencia antes de componer navegación, sin un primer frame con el tema equivocado;
- cambiar apariencia desde Settings sin hacer que `feature:vault` conozca DataStore o dependa de
  `app`;
- mover y ampliar el design system en `core:ui` sin ciclos ni estilos duplicados;
- persistir un ajuste no sensible, local al dispositivo y no ligado a una cuenta;
- degradar de forma determinista en API 30, datos desconocidos o errores de almacenamiento.

## Drivers e invariantes

1. `core:ui` es la única fuente de tema, tokens, componentes y modelos públicos de apariencia.
2. `app` es el composition root y el único owner de la implementación de persistencia.
3. Una feature puede depender de `core:ui`, nunca de `app` ni de una API de almacenamiento.
4. Navegación no se compone hasta resolver la primera configuración o su fallback.
5. La preferencia es por dispositivo, no por sesión, vault o cuenta.
6. La ausencia, corrupción o evolución de datos no puede impedir el arranque.
7. El color dinámico es opt-in y nunca se aplica por debajo de Android 12/API 31.

## Opciones consideradas

### Opción A — Preferences DataStore en `app` con contrato público en `core:ui`

- Ventajas:
    - ofrece actualizaciones atómicas y un `Flow` observable sin lecturas bloqueantes en el hilo
      principal;
    - la implementación y Hilt permanecen en el composition root, mientras las features consumen una
      interfaz estable de `core:ui`;
    - el gate de arranque puede esperar la primera emisión y renderizar directamente el tema
      correcto;
    - admite fallback explícito para corrupción, I/O y valores desconocidos.
- Costes/riesgos:
    - añade DataStore a `app` y requiere un estado de bootstrap previo a navegación;
    - Settings debe representar fallos de escritura sin asumir que el cambio ya fue persistido.
- Impacto en seguridad:
    - almacena solo valores no sensibles y permanece excluido de backup por la política raíz.
- Impacto en migración:
    - no existe preferencia anterior que migrar; la ausencia equivale a defaults.

### Opción B — `SharedPreferences` síncronas en `app`

- Ventajas:
    - integración pequeña y lectura inmediata antes de `setContent`;
    - coincide con mecanismos locales ya usados para otros ajustes.
- Costes/riesgos:
    - puede realizar I/O síncrono durante cold start;
    - requiere construir manualmente observación, consistencia y propagación de errores;
    - consolida una API que Android recomienda sustituir para datos reactivos.
- Impacto en seguridad:
    - equivalente a la opción A para estos datos no sensibles.
- Impacto en migración:
    - no requiere migración inicial, pero una migración futura a DataStore sería probable.

### Opción C — Persistencia en `core:ui` o `feature:vault`

- Ventajas:
    - reduce wiring aparente en `app`.
- Costes/riesgos:
    - acopla el design system a Android storage o convierte una feature en owner de una preferencia
      global;
    - dificulta bootstrap y crea dependencias invertidas o ciclos al intentar aplicar el tema raíz;
    - permite escrituras directas fuera del composition root.
- Impacto en seguridad:
    - dispersa ownership y hace más difícil auditar backup y limpieza.
- Impacto en migración:
    - trasladar después la preferencia global exigiría cambiar consumidores y bindings.

## Decisión

Se adopta la **opción A**: contrato y modelos en `core:ui`, implementación con Preferences DataStore
en `app`, aplicación del tema en el root y consumo unidireccional desde las features.

### Ownership y dependencias

| Módulo      | Posee                                                                                                                                                                                             | No puede poseer                                                                                                  |
|-------------|---------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|------------------------------------------------------------------------------------------------------------------|
| `core:ui`   | `SafeCubeTheme`, paletas, tipografía, formas, spacing, motion tokens, componentes canónicos, `AppearanceMode`, `AppearanceSettings`, contrato `AppearanceRepository` y resultado de actualización | DataStore, Hilt bindings de aplicación, sesión, navegación, reglas de cuenta o dependencias hacia `app`/features |
| `app`       | implementación DataStore, serialización, tratamiento de corrupción/I/O, binding Hilt, bootstrap, theme root y aplicación efectiva por API                                                         | estilos de feature, copias del tema o modelos públicos alternativos                                              |
| `feature:*` | state holders de pantalla y eventos de usuario; Settings observa y solicita cambios mediante `AppearanceRepository`                                                                               | dependencia de `app`, claves DataStore, nombres de fichero, acceso directo a preferencias o un theme root propio |

La dirección permitida es:

```text
feature:* ───────► core:ui ◄────── app
                         contrato    │
                                     ├── DataStore privado
app ─────────────► feature:*         └── theme root / bootstrap
```

No existe ninguna arista `feature:* → app`; `app` proporciona en runtime la implementación del
contrato que las features conocen desde `core:ui`.

### Contrato público mínimo

Los únicos modos v1 son `AppearanceMode.System`, `AppearanceMode.Light` y `AppearanceMode.Dark`. No
existe un modo implícito adicional ni un valor `null` con semántica propia.

```kotlin
enum class AppearanceMode {
    System,
    Light,
    Dark,
}

data class AppearanceSettings(
    val mode: AppearanceMode = AppearanceMode.System,
    val dynamicColorEnabled: Boolean = false,
)

interface AppearanceRepository {
    val settings: Flow<AppearanceSettings>

    suspend fun setMode(mode: AppearanceMode): AppearanceUpdateResult
    suspend fun setDynamicColorEnabled(enabled: Boolean): AppearanceUpdateResult
}

sealed interface AppearanceUpdateResult {
    data object Updated : AppearanceUpdateResult
    data object Failed : AppearanceUpdateResult
}
```

Los nombres de tipo y defaults son contrato. La forma exacta de los packages podrá ajustarse en
SCDK-M138 siempre que conserve ownership, visibilidad y semántica. Las operaciones devuelven éxito
solo después de persistir; ante fallo conservan la última configuración confirmada y Settings
muestra feedback retryable localizado. No se filtran excepciones ni detalles de DataStore a una
feature.

### Persistencia y serialización

- `app` usa un único Preferences DataStore privado con nombre lógico `appearance`.
- Las claves persistidas son `mode` y `dynamic_color_enabled`.
- `mode` usa valores estables explícitos `system`, `light` y `dark`; no se persiste `enum.name`.
- Clave ausente o valor de modo desconocido se interpreta como `System` sin bloquear ni reescribir
  durante el arranque.
- Booleano ausente se interpreta como `false`.
- Una corrupción reemplazable produce preferencias vacías; un error de lectura recuperable emite
  defaults y libera el arranque. El fallo se registra solo como diagnóstico estructurado y sin
  volcar contenido de preferencias.
- Cada setter actualiza únicamente su clave mediante la transacción DataStore y deja que el flujo
  confirmado actualice theme root y Settings.
- No se crea un framework genérico de preferencias ni se reutilizan stores de auth o vault.

### Resolución del tema

| Modo solicitado | Tema efectivo                                                                     |
|-----------------|-----------------------------------------------------------------------------------|
| `System`        | Sigue la configuración clara/oscura vigente del sistema y responde a sus cambios. |
| `Light`         | Fuerza la paleta clara de SafeCube.                                               |
| `Dark`          | Fuerza la paleta oscura de SafeCube.                                              |

`dynamicColorEnabled` es una preferencia independiente y representa intención del usuario:

- API 31 o superior: si vale `true`, `SafeCubeTheme` usa el esquema dinámico claro/oscuro; si vale
  `false`, usa las paletas de marca aprobadas.
- API 30: el esquema efectivo siempre es el de marca. Settings mantiene visible el control,
  deshabilitado, refleja el valor almacenado y muestra una explicación EN/ES de que requiere Android
  12. No borra ni modifica la intención persistida por incompatibilidad temporal.

Tipografía, formas, componentes y demás tokens de SafeCube siguen aplicándose aunque el esquema de
color sea dinámico.

### Flujo unidireccional

```text
Preferences DataStore
  → AppAppearanceRepository.settings
  → bootstrap Loading / Ready(AppearanceSettings)
  → SafeCubeTheme(settings)
  → NavigationWrapper

Settings intent
  → SettingsViewModel
  → AppearanceRepository setter
  → transacción DataStore
  → settings Flow
  → theme root + Settings state
```

Settings no aplica un estado optimista global. Puede mostrar mutación en curso, pero el tema cambia
cuando el flujo emite el valor persistido. Esto mantiene una única fuente de verdad y evita que una
escritura fallida deje root y Settings en estados distintos.

### Arranque y primer frame

1. `MainActivity` instala el splash del sistema.
2. Un state holder de `app` comienza en `Loading` y obtiene la primera emisión del repositorio.
3. Mientras está en `Loading`, el splash permanece y `NavigationWrapper` no se compone.
4. La primera lectura válida produce `Ready(settings)`; corrupción, I/O recuperable o dato
   desconocido producen `Ready(defaults)`.
5. El primer frame Compose con navegación ya está envuelto por `SafeCubeTheme(settings)`.

“Sin flash de tema” significa que no existe un frame Compose o de navegación con valores
provisionales antes del tema resuelto. El splash de sistema usa recursos de arranque y actúa como
única superficie previa. El gate nunca espera indefinidamente: una lectura fallida termina en
defaults.

### Ciclo de vida de la preferencia

| Evento                                        | Comportamiento                                                              |
|-----------------------------------------------|-----------------------------------------------------------------------------|
| Logout manual, forzado o expiración de sesión | Conserva apariencia; ninguna limpieza de auth/vault toca DataStore.         |
| Cambio de cuenta                              | Conserva apariencia porque es una preferencia del dispositivo.              |
| Lock, quick unlock o cambio de passphrase     | No modifica apariencia.                                                     |
| Valor persistido desconocido                  | Usa `AppearanceMode.System`; el arranque continúa.                          |
| Booleano ausente                              | Usa `false`.                                                                |
| Corrupción o I/O recuperable                  | Usa defaults, mantiene la app disponible y permite una escritura posterior. |
| Borrado de datos o desinstalación             | El sistema elimina DataStore; el siguiente arranque usa defaults.           |
| Restore/transfer                              | No restaura apariencia: continúa la política deny-by-default sin includes.  |

### Reglas de consumo

- Las features usan `SafeCubeTheme` de forma indirecta desde el root y consumen
  `MaterialTheme`/tokens y componentes públicos de `core:ui`.
- No se duplican colores, tipografías, shapes, elevaciones, spacing o motion si existe un token.
- No se crea un componente local cuando existe una abstracción canónica con la misma semántica.
- Una necesidad específica sin abstracción puede permanecer local si no es reutilizable; si se
  repite o representa lenguaje de producto, se promueve a `core:ui` mediante la task
  correspondiente.
- Hex, estilos y dimensiones ad hoc requieren una justificación visible y no pueden redefinir la
  identidad aprobada.
- Previews y tests reciben `AppearanceSettings` o fakes del contrato; no leen almacenamiento real.

## Justificación

Preferences DataStore ofrece el modelo reactivo que necesita el theme root y permite mantener la
persistencia fuera de las features. El gate explícito convierte su lectura asíncrona en una ventaja:
no bloquea el hilo principal y evita componer navegación con defaults efímeros. SharedPreferences
reduciría código inicial, pero trasladaría I/O y observación manual a una ruta crítica que deberá
evolucionar inmediatamente. Mover persistencia a `core:ui` o `feature:vault` violaría ownership y
haría difícil aplicar el resultado antes de navegación.

## Consecuencias

### Positivas

- Una única fuente de verdad gobierna Settings y el theme root.
- Las features no conocen infraestructura ni dependen de `app`.
- Tema, tokens y componentes pueden crecer en `core:ui` sin duplicación.
- Defaults, API 30 y errores de datos tienen comportamiento determinista.
- El primer frame de navegación es verificable sin sleeps.

### Negativas

- `app` incorpora DataStore, un bootstrap state holder y binding Hilt adicionales.
- El arranque espera una lectura local antes de mostrar navegación.
- Los fallos de escritura requieren feedback explícito y no permiten actualización optimista global.
- `core:ui` expone una interfaz de apariencia además de APIs puramente visuales.

## Riesgos y mitigaciones

| Riesgo                             | Impacto | Mitigación                                                                                | Señal                                          |
|------------------------------------|---------|-------------------------------------------------------------------------------------------|------------------------------------------------|
| Splash retenido indefinidamente    | Alto    | Toda excepción de lectura se convierte en `Ready(defaults)`; test con repositorio fallido | Test de cold start no llega a navegación       |
| Flash con tema provisional         | Alto    | No componer navegación en `Loading`; prueba del primer frame con valor presembrado        | Golden/semántica inicial usa paleta incorrecta |
| Ciclo `feature → app`              | Alto    | Contrato en `core:ui`, implementación/binding en `app`, gate de dependencias              | Gradle o revisión detecta dependencia a `:app` |
| Valor futuro desconocido           | Medio   | Decoder exhaustivo con fallback `System`, sin `enum.valueOf`                              | Unit test de valor desconocido falla           |
| Fallo de escritura desincroniza UI | Medio   | Emitir solo datos confirmados y devolver `Failed`                                         | Root cambia aunque DataStore falle             |
| API 30 intenta color dinámico      | Medio   | Cálculo efectivo centralizado en `SafeCubeTheme` con gate API 31                          | Test API 30 obtiene esquema dinámico           |
| Preferencia entra en backup        | Medio   | Conservar `allowBackup=false`, exclusión raíz y ausencia de includes                      | Gate de manifest/backup deja de pasar          |

## Compatibilidad y migración

- Compatible con API 30–36.
- No hay datos previos que migrar; el tema plantilla actual no representa una preferencia
  persistida.
- SCDK-M138 moverá tema, colores y tipografía desde `app` a `core:ui`, añadirá contrato,
  implementación, binding y bootstrap.
- SCDK-M145 conectará Settings al contrato sin acceso a DataStore.
- Un rollback puede ignorar el fichero `appearance`; no cambia schema, crypto, auth, vault, red ni
  sync. Una versión posterior vuelve a leer sus claves estables.

## Seguridad y privacidad

- Solo se persisten modo y opt-in de color dinámico; no son secretos ni PII.
- No se almacenan account ID, sesión, tema por cuenta ni datos del vault.
- No se registran valores completos del store, paths privados ni contenido de otras preferencias.
- El fichero sigue cubierto por `allowBackup=false`, exclusión raíz de backup/transfer y ausencia de
  reglas `include`.
- Borrar auth, logout o lock no deben borrar esta preferencia; borrar datos de aplicación sí.

## Tests requeridos

- Unitarios de codec: defaults, los tres modos, booleano, clave ausente y valor desconocido.
- Unitarios del repositorio: emisión inicial, updates atómicos, fallo de lectura, corrupción, fallo
  de escritura y conservación de la última configuración confirmada.
- Unitarios de resolución: System/Light/Dark, dynamic on/off y API 30 frente a API 31+.
- Test de arquitectura: ninguna feature depende de `app` ni referencia DataStore/keys de apariencia.
- Test de bootstrap: `Loading` no compone navegación; éxito y fallo terminan en `Ready`.
- Prueba instrumentada de cold start con preferencia presembrada: el primer frame Compose observado
  usa el tema esperado y no existe frame intermedio. Se sincroniza mediante idling/estado
  observable, nunca mediante sleeps.
- Instrumentada en API 30: control visible y deshabilitado con explicación localizada; tema de
  marca.
- Instrumentada en API 31+: color dinámico solo cuando el opt-in confirmado vale `true`.
- Ciclo de vida: logout y cambio de cuenta conservan; clear app data restaura defaults.
- Gates existentes de manifest y backup continúan pasando sin excepciones de inclusión.

## Enlaces

- Spec: [`SPEC-UI-V1`](../../specs/features/ui-brand-motion-v1.md).
- Marca: [`BRAND-GUIDE-SAFECUBE-V1`](../../design/brand-guide-v1.md).
- ADRs relacionados: [`ADR-0003-SENSITIVE-DATA-SURFACES`](./ADR-0003-SENSITIVE-DATA-SURFACES.md).
- Código actual inspeccionado:
    - `app/src/main/java/com/miguelrodriguez19/safecube/app/presentation/theme/`;
    - `app/src/main/java/com/miguelrodriguez19/safecube/app/entrypoint/MainActivity.kt`;
    -
    `feature/vault/src/main/java/com/miguelrodriguez19/safecube/feature/vault/presentation/settings/`;
    - `core/ui/`;
    - `app/src/main/res/xml/backup_rules.xml` y `data_extraction_rules.xml`.
- Implementación futura: `SCDK-M138`, `SCDK-M145`.
