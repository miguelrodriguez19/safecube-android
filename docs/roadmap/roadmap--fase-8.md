# FASE 8 — Identidad de Marca, UI & Motion

## Objetivo de la fase

Construir una experiencia visual coherente, accesible y reconocible sobre los flujos funcionales y
de seguridad estabilizados en la Fase 7. SafeCube debe comunicar confianza sin resultar frío,
explicar los estados del vault sin abrumar y mantener una interacción consistente en autenticación,
inicialización, unlock, vault, editores y Settings.

La fase entrega una identidad de marca versionada, un design system real en `core:ui`, una guía de
motion sutil, layouts adaptativos para teléfonos y evidencia reproducible de accesibilidad y
regresión visual. El rediseño no puede cambiar contratos de dominio, crypto, storage, red, sync ni
las políticas de retry y navegación segura aprobadas en fases anteriores.

## Condiciones de entrada

- La Fase 7 debe estar cerrada mediante `SCDK-M130`, con su estado real y gaps documentados.
- `SCDK-M129` debe haber retirado las rutas y superficies placeholder ajenas a v1, de forma que el
  inventario visual se realice sobre el alcance público definitivo.
- Los estados observables, errores, retries, session lifecycle, auto-lock, quick unlock y cambio de
  passphrase de `SPEC-HARDENING-V1` son entradas funcionales y no se reinterpretan en esta fase.
- `SCDK-M134` debe producir `SPEC-UI-V1` en estado `APPROVED` antes de comenzar las cards que
  modifican UI de producción.
- El owner debe proporcionar el SVG fuente del logo antes de ejecutar `SCDK-M135`.
- La identidad y los mockups piloto requieren aprobación humana antes de escalar el rediseño al
  resto de pantallas.

El refinamiento documental, el inventario y la preparación de los activos pueden avanzar mientras
termina la Fase 7. Ninguna card puede editar a la vez las mismas pantallas o recursos que una card
de Fase 7 todavía activa.

## Estado de partida verificado

- El tema actual vive en `app` y conserva los colores morados del template de Material 3.
- El color dinámico está activado por defecto en Android 12 o superior, por lo que SafeCube no
  mantiene hoy una identidad cromática estable.
- La tipografía solo personaliza `bodyLarge` y usa la familia por defecto del sistema.
- No existen tokens canónicos de spacing, shapes, elevation, iconografía o motion.
- `core:ui` centraliza strings y un campo secreto, pero todavía no es un design system consumible
  por todas las features.
- Las pantallas usan directamente componentes Material, dimensiones locales y scaffolds distintos.
- Existen literales visuales y `contentDescription` no localizadas en algunas superficies.
- Los estados funcionales de Fase 7 están modelados, pero su presentación visual no es consistente
  entre auth, bootstrap, vault, editores y Settings.
- La navegación pública queda reducida a `Bóveda` y `Ajustes`; la bottom navigation debe poder
  crecer a una tercera sección post-MVP sin rediseñar su contrato.
- Hay tests JVM de ViewModels y pruebas instrumentadas funcionales, pero no existe catálogo de
  previews, golden tests, gate de regresión visual ni matriz transversal de accesibilidad.
- Inglés y español disponen de recursos compartidos, aunque falta una revisión integral de paridad,
  tono y comportamiento con textos largos.
- El APK debe seguir soportando API 30-36 y target API 36, sin asumir orientación, proporción o
  tamaño fijo de pantalla.

## Decisiones de planificación

- La personalidad de SafeCube será cálida, cercana, confiable, sobria y moderna. La interfaz tendrá
  densidad equilibrada: no estará sobrecargada ni dependerá de grandes espacios vacíos sin función.
- La dirección cromática partirá de azul petróleo con acentos cálidos y definirá paletas propias
  completas para tema claro y oscuro.
- El lenguaje de formas será suave y contenido, con radios moderados y sin apariencia infantil.
- El logo SVG existente conserva su geometría. Se permiten adaptaciones cromáticas, monocromas,
  ópticas y técnicas para launcher, themed icon, splash y superficies in-app.
- Se elegirá una única familia tipográfica libre, empaquetada con la app, con licencia, fallback y
  comportamiento de escalado documentados.
- La iconografía funcional usará una familia Material coherente. Solo la marca y símbolos que no
  tengan equivalente adecuado recibirán activos propios.
- SafeCube ofrecerá los modos de tema `System`, `Light` y `Dark`.
- Material You será opcional y estará desactivado por defecto. En API 30 la opción seguirá visible,
  pero deshabilitada y con una explicación localizada de que requiere Android 12 o superior.
- Las preferencias de apariencia serán no sensibles, locales al dispositivo, independientes de la
  cuenta y persistirán después de logout. Borrar los datos de la app restaura los defaults.
- `core:ui` será dueño de tema, tokens y componentes públicos; `app` aplicará el tema y será dueño
  de la persistencia de apariencia. Las features no persistirán preferencias visuales.
- Se conserva la jerarquía y bottom navigation `Bóveda / Ajustes`, preparada para una tercera tab.
  No se añaden rutas ni funcionalidades para justificar el rediseño.
- El motion será sutil y funcional: explicará navegación, jerarquía o cambio de estado. Todas las
  animaciones respetarán la escala de animación del sistema; escala `0` implica transición
  inmediata sin perder contenido ni acciones.
- La fase soportará teléfonos en ventanas compactas y medias, portrait y landscape. Una experiencia
  específica para tablets, foldables expandidos o desktop queda fuera de alcance.
- El gate de accesibilidad será `AA pragmático`: contraste aplicable, targets mínimos de 48dp,
  semántica, orden de foco, TalkBack en flujos críticos, font scale hasta 200 % y movimiento
  reducido. Una auditoría WCAG completa se registrará como mejora posterior.
- Inglés y español mantendrán paridad y una voz cercana, directa y tranquila, evitando jerga
  innecesaria sin ocultar las consecuencias de seguridad.
- La guía, decisiones, licencias, activos fuente y mockups aprobados se versionarán en Git. Una
  herramienta externa nunca será la única fuente de verdad.
- Los golden tests cubrirán componentes y estados críticos. La semántica Compose y una matriz manual
  cubrirán las combinaciones cuyo coste no justifique mantener una referencia visual.
- Compose Preview Screenshot Testing será la primera opción para golden tests. La versión compatible
  quedará fijada y la integración se aislará tras tareas Gradle canónicas porque la herramienta es
  experimental.
- Ningún screenshot, preview, test, informe o fixture contendrá secretos, datos de una cuenta real,
  tokens, recovery keys reales, payloads ni identificadores sensibles.

## Mejoras recomendadas posteriores

Estas mejoras no bloquean la Fase 8, pero cualquier gap descubierto debe registrarse con evidencia
y una tarea de seguimiento:

- experiencia dedicada para tablets, foldables, ventanas expandidas y desktop;
- soporte RTL y nuevos idiomas;
- auditoría completa WCAG 2.2 AA o aspiración AAA;
- switch access, control por voz y modos de alto contraste adicionales;
- pruebas formales de usabilidad con personas externas;
- haptics como lenguaje transversal de feedback;
- personalización adicional de densidad, tamaño o contraste.

## Orden de implementación

1. Contrato normativo e inventario: `SCDK-M134`.
2. Identidad visual y arquitectura: `SCDK-M135`–`SCDK-M136`.
3. Infraestructura de verificación: `SCDK-M137`.
4. Fundaciones del design system: `SCDK-M138`–`SCDK-M139`.
5. Shell y navegación visual: `SCDK-M140`.
6. Rediseño por flujos: `SCDK-M141`–`SCDK-M145`.
7. Auditoría transversal: `SCDK-M146`.
8. Matriz de regresión y cierre: `SCDK-M147`.

---

# SCDK-M134. Definir el contrato normativo de UI, marca y accesibilidad

## Main Story (How, I Want, To)

Como maintainer, quiero una especificación normativa de experiencia visual para que el rediseño de
SafeCube se implemente contra requisitos observables y no contra decisiones implícitas de cada
pantalla o agente.

## Context, Functional Description & Goal

La Fase 8 solo está resumida en el roadmap de alto nivel. Las specs actuales regulan producto,
crypto, sync, storage, seguridad y resiliencia, pero no establecen identidad, design system,
accesibilidad, motion, adaptación de layouts ni regresión visual.

El resultado único es `SPEC-UI-V1` aprobada, acompañada por un inventario de rutas y estados que
delimite exactamente qué debe diseñarse y verificarse.

## Steps/Scope

### In Scope

- Crear `docs/specs/features/ui-brand-motion-v1.md` con ID `SPEC-UI-V1`.
- Crear inicialmente la spec en estado `REVIEW`; solo el owner humano puede promoverla a
  `APPROVED`.
- Inventariar todas las superficies públicas después de Fase 7: Welcome, Login, Signup,
  PostLoginGate, Create Vault, Recovery Key, Unlock, quick unlock, Vault Home, editores de password
  y nota, Change Passphrase, Settings y diálogos globales.
- Crear una matriz pantalla × estado que incluya, cuando aplique: initial loading, content, empty,
  mutating, retryable error, terminal error, locked, offline, draft, conflict y success.
- Definir requisitos estables para identidad, apariencia, design system, componentes, feedback,
  navegación visual, motion, layouts adaptativos, accesibilidad, localización, privacidad visual y
  regresión.
- Definir como flujos críticos accesibles: Login, Unlock, creación de un item, retry manual de sync
  y Settings.
- Definir qué combinaciones requieren golden, test semántico, prueba instrumentada o evidencia
  manual.
- Enlazar `SPEC-PRODUCT-V1`, `SPEC-HARDENING-V1`, `ADR-0001`, `ADR-0002` y `ADR-0003` sin duplicar
  sus contratos.
- Registrar la spec en `docs/sdd/spec-registry.md` y sus requisitos en
  `docs/sdd/traceability-matrix.md`.

### Out of Scope (if applies)

- Modificar UI o código de runtime.
- Elegir valores finales de tokens o producir activos gráficos.
- Cambiar comportamiento de dominio, rutas, crypto, storage, red o sync.
- Añadir funcionalidades fuera del alcance de v1.

## Additional Information and Configuration

- Dependencia: cierre de Fase 7 y alcance público fijado por `SCDK-M129`/`SCDK-M130`.
- La spec no puede quedar `APPROVED` con decisiones críticas, requisitos no verificables o estados
  visuales sin ownership.
- Los requisitos de accesibilidad deben distinguir gates bloqueantes de recomendaciones futuras.
- Crear `docs/sdd/agent-reports/SCDK-M134.md`.

### API Contract and Expected Behavior (if applies)

No cambia APIs de runtime. `SPEC-UI-V1` será la fuente normativa de `SCDK-M135`–`SCDK-M147` y
establecerá el contrato observable entre estados de presentación y representación visual.

### Acceptance Criteria (ACs)

- [ ] Existe `SPEC-UI-V1` con objetivos, no objetivos y requisitos identificables.
- [ ] Todas las rutas públicas de v1 aparecen en el inventario y en la matriz de estados.
- [ ] Cada requisito tiene estrategia de verificación y evidencia esperada.
- [ ] Los gates bloqueantes y las mejoras recomendadas están diferenciados.
- [ ] La spec preserva los estados y contratos aprobados en Fase 7.
- [ ] No quedan decisiones críticas marcadas como `TBD`.
- [ ] El owner humano ha promovido la spec a `APPROVED`.
- [ ] Registry, trazabilidad y agent report están actualizados.

---

# SCDK-M135. Crear la identidad visual y guía de marca

## Main Story (How, I Want, To)

Como product owner, quiero una identidad visual versionada para que SafeCube sea reconocible,
coherente y reproducible en launcher, splash, pantallas y materiales futuros.

## Context, Functional Description & Goal

La app utiliza todavía el launcher, colores y tipografía de plantilla. Existe un logo vectorizado
cuya geometría se considera final, pero faltan variantes, paletas, licencia tipográfica, reglas de
uso y una validación visual de los flujos representativos.

El resultado único es una guía de marca aprobada con activos fuente reutilizables y mockups piloto
que permitan implementar el design system sin reinterpretaciones.

## Steps/Scope

### In Scope

- Incorporar el SVG fuente proporcionado por el owner y preservar su geometría.
- Crear variantes principal, invertida y monocroma con área de seguridad y tamaños mínimos.
- Preparar concepto y exports para adaptive launcher icon, round icon, monochrome themed icon,
  splash y uso in-app.
- Definir paletas clara y oscura basadas en azul petróleo y acentos cálidos.
- Verificar contraste de las combinaciones funcionales que se convertirán en tokens.
- Seleccionar una familia tipográfica open-source, documentar licencia, pesos incluidos y fallback.
- Definir lenguaje de formas, elevación, densidad, ilustración e iconografía funcional.
- Especificar usos correctos e incorrectos del logo, color, tipografía e iconos.
- Crear mockups de referencia para Welcome/Login, Unlock y Vault en estados representativos.
- Incluir al menos una variante clara y oscura de los flujos piloto.
- Guardar guía, SVG fuente, licencias y exports de referencia bajo `docs/design` o el directorio de
  assets aprobado por la spec.

### Out of Scope (if applies)

- Alterar la geometría base del logo.
- Implementar todavía los activos en recursos Android.
- Diseñar todas las pantallas antes de validar los pilotos.
- Crear una familia completa de iconos propios.
- Producir materiales de marketing o una web pública.

## Additional Information and Configuration

- Spec: `SPEC-UI-V1`.
- Dependencia: `SCDK-M134` y entrega del SVG por el owner.
- Los assets no pueden depender de un documento o enlace externo como única fuente.
- Toda fuente o recurso de terceros debe permitir redistribución dentro del APK y del repositorio.
- Crear `docs/sdd/agent-reports/SCDK-M135.md`.

### API Contract and Expected Behavior (if applies)

La guía define la entrada canónica para los tokens y assets de `SCDK-M138`. Un export puede
regenerarse desde la fuente versionada sin pérdida de información ni dependencia de una cuenta
externa.

### Acceptance Criteria (ACs)

- [ ] El logo fuente y sus variantes están versionados y conservan la geometría aprobada.
- [ ] Existen propuestas utilizables de launcher adaptativo, monocromo y splash.
- [ ] Las paletas clara y oscura cubren roles funcionales y combinaciones de contraste.
- [ ] La tipografía tiene licencia, pesos y fallback documentados.
- [ ] Los mockups piloto cubren Welcome/Login, Unlock y Vault en claro y oscuro.
- [ ] La guía describe tono, formas, iconografía y usos incorrectos.
- [ ] El owner ha aprobado explícitamente identidad y mockups piloto.
- [ ] El agent report enlaza las fuentes, exports y evidencia de aprobación.

---

# SCDK-M136. Definir la arquitectura del design system y la apariencia

## Main Story (How, I Want, To)

Como developer, quiero ownership y dependencias explícitas para que el design system pueda crecer
sin duplicar estilos ni acoplar las features a detalles de persistencia.

## Context, Functional Description & Goal

El tema vive actualmente en `app`, mientras que `core:ui` solo contiene recursos y un componente.
La nueva preferencia de apariencia debe aplicarse antes de renderizar navegación, persistir por
dispositivo y ser editable desde Settings sin trasladar infraestructura a `feature:vault`.

El resultado único es un ADR aceptado que fija módulos, contratos, flujo de datos y límites del
design system.

## Steps/Scope

### In Scope

- Crear el siguiente ADR disponible para design system y apariencia, inicialmente `PROPOSED`.
- Establecer que `core:ui` posee `SafeCubeTheme`, tokens, componentes y modelos públicos de UI.
- Establecer que `app` aplica el tema raíz y posee la persistencia de preferencias por dispositivo.
- Definir `AppearanceMode` con `System`, `Light` y `Dark`.
- Definir una configuración independiente `dynamicColorEnabled`, con default `false`.
- Definir que el color dinámico solo se aplica en Android 12 o superior.
- Definir que API 30 expone la preferencia deshabilitada con explicación localizada.
- Definir el flujo unidireccional desde el repositorio de apariencia hasta el theme root y desde
  Settings hacia la actualización.
- Evitar que las features dependan de `app` o escriban directamente la persistencia.
- Definir arranque sin flash de tema, fallback de valores inválidos y comportamiento tras logout,
  cambio de cuenta y borrado de datos.
- Definir las reglas de consumo: usar tokens/componentes públicos y evitar estilos locales cuando
  exista una abstracción canónica.

### Out of Scope (if applies)

- Implementar el ADR.
- Crear un sistema de preferencias remoto o sincronizado.
- Persistir apariencia por cuenta.
- Añadir una infraestructura genérica de feature flags o Settings no visuales.

## Additional Information and Configuration

- Spec: `SPEC-UI-V1`.
- Dependencias: `SCDK-M134`, `SCDK-M135`.
- La preferencia no es sensible, pero permanece cubierta por la política deny-by-default de backup.
- El ADR debe justificar la persistencia elegida y cómo se prueba el primer frame.
- Crear `docs/sdd/agent-reports/SCDK-M136.md`.

### API Contract and Expected Behavior (if applies)

Contrato mínimo esperado:

- `AppearanceMode.System`, `AppearanceMode.Light`, `AppearanceMode.Dark`.
- `AppearanceSettings(mode, dynamicColorEnabled)`.
- default: modo `System`, color dinámico desactivado.
- logout conserva la preferencia; borrar datos de aplicación restaura el default.
- un valor persistido desconocido se interpreta como default sin bloquear el arranque.

### Acceptance Criteria (ACs)

- [ ] El ADR fija ownership de `core:ui`, `app` y features sin ciclos de dependencias.
- [ ] Tema, color dinámico y persistencia tienen contratos y defaults inequívocos.
- [ ] API 30 y Android 12+ tienen comportamientos definidos.
- [ ] Arranque, logout, cambio de cuenta, dato inválido y borrado local están resueltos.
- [ ] El owner humano ha marcado el ADR como `ACCEPTED`.
- [ ] Spec, trazabilidad y agent report enlazan el ADR.

---

# SCDK-M137. Crear la infraestructura de tests Compose y regresión visual

## Main Story (How, I Want, To)

Como developer, quiero una infraestructura visual determinista para detectar regresiones del
design system sin convertir cada revisión en una comparación manual subjetiva.

## Context, Functional Description & Goal

El proyecto dispone de Compose UI Test para instrumentación, pero no tiene previews canónicos,
goldens, fixtures visuales ni un gate de CI. La herramienta oficial de screenshots es experimental,
por lo que debe quedar fijada, aislada y ser reemplazable sin reescribir las pantallas.

## Steps/Scope

### In Scope

- Añadir la versión compatible y fijada de Compose Preview Screenshot Testing.
- Encapsular su configuración en el módulo mínimo necesario y documentar sus tareas Gradle.
- Crear anotaciones multi-preview para tema claro/oscuro, idiomas y configuraciones seleccionadas.
- Definir fixtures sintéticos y deterministas para auth, vault, items, drafts, sync y errores.
- Prohibir datos reales o material sensible en referencias, reports y diffs.
- Crear un conjunto inicial de previews para validar la infraestructura, no el rediseño final.
- Añadir tests semánticos Compose para roles, labels, estados enabled/disabled y acciones.
- Crear una tarea canónica `verifyPhase8Screenshots` o nombre equivalente documentado.
- Integrar la validación en CI sin regenerar ni aceptar referencias automáticamente.
- Documentar el flujo explícito de actualización y revisión humana de un golden.
- Garantizar que las referencias no dependan de reloj, locale, red, animación o fuentes externas.

### Out of Scope (if applies)

- Cubrir todavía todas las pantallas.
- Convertir cada combinación de estado en un golden.
- Descargar fuentes durante tests.
- Aceptar automáticamente cambios visuales desde CI.
- Sustituir tests funcionales por comparaciones de píxeles.

## Additional Information and Configuration

- Spec: `SPEC-UI-V1`.
- Dependencias: `SCDK-M134`, `SCDK-M136`.
- La integración debe ser compatible con AGP, Kotlin, Compose BOM y JDK canónicos del proyecto.
- Si la herramienta oficial tiene un bloqueo reproducible, el agente debe detenerse y proponer una
  alternativa mediante decisión explícita; no introducir otra librería de forma oportunista.
- Crear `docs/sdd/agent-reports/SCDK-M137.md`.

### API Contract and Expected Behavior (if applies)

- El comando de actualización modifica referencias solo por invocación local explícita.
- El comando de verificación es read-only respecto a las referencias y falla con un diff útil.
- CI ejecuta exclusivamente verificación.
- Un preview se renderiza desde estado inyectado, sin ViewModel real, red, Keystore o Room.

### Acceptance Criteria (ACs)

- [ ] Existe un comando Gradle canónico que valida referencias visuales.
- [ ] Una diferencia controlada hace fallar el gate y produce evidencia inspeccionable.
- [ ] CI no puede actualizar referencias.
- [ ] Los fixtures son deterministas, sintéticos y no sensibles.
- [ ] Los tests semánticos iniciales pasan junto a los goldens.
- [ ] El flujo de revisión y actualización está documentado.
- [ ] Los gates existentes siguen pasando.
- [ ] Trazabilidad y agent report contienen comandos y paths exactos.

---

# SCDK-M138. Implementar tema, tokens, activos y preferencias de apariencia

## Main Story (How, I Want, To)

Como usuario, quiero que SafeCube respete una apariencia coherente y mi elección de tema para que
la aplicación sea reconocible, legible y estable desde el primer frame.

## Context, Functional Description & Goal

Con la identidad aprobada y el ADR aceptado, deben reemplazarse el tema de plantilla y los estilos
locales de fundación por contratos públicos de `core:ui`. La app debe aplicar las preferencias sin
flash visual ni dependencia de la sesión de cuenta.

## Steps/Scope

### In Scope

- Implementar `SafeCubeTheme` en `core:ui`.
- Implementar paletas propia clara y oscura con todos los roles consumidos.
- Empaquetar la familia tipográfica aprobada, sus licencias y fallback.
- Definir tokens públicos de typography, shapes, spacing, elevation, iconografía y motion.
- Convertir e integrar launcher adaptativo, round icon, monochrome icon y splash.
- Implementar `AppearanceMode` y la configuración de color dinámico según el ADR.
- Persistir preferencias de forma local por dispositivo.
- Aplicar el tema raíz antes de renderizar navegación o contenido.
- Mantener default `System` y Material You desactivado.
- Exponer en API 30 la capacidad no disponible sin intentar aplicar color dinámico.
- Fallar de forma segura al default ante dato ausente, corrupto o desconocido.
- Añadir previews y goldens de tokens y tema.

### Out of Scope (if applies)

- Rediseñar pantallas completas.
- Añadir controles en Settings; pertenecen a `SCDK-M145`.
- Sincronizar preferencias con backend.
- Alterar tema o recursos de diálogos del sistema como el prompt biométrico.

## Additional Information and Configuration

- Spec: `SPEC-UI-V1`; ADR de `SCDK-M136`; guía de `SCDK-M135`.
- Dependencia: `SCDK-M137` para registrar referencias visuales aprobadas.
- Los assets y fuentes deben poder compilar offline desde el repositorio.
- Crear `docs/sdd/agent-reports/SCDK-M138.md`.

### API Contract and Expected Behavior (if applies)

- `System` sigue el tema del sistema sin cambiar la preferencia persistida.
- `Light` y `Dark` fuerzan exclusivamente el esquema solicitado.
- Material You modifica el esquema solo cuando el flag está activo y la plataforma lo soporta.
- Reinicio y logout conservan apariencia; clear app data restaura `System` y marca desactivada.

### Acceptance Criteria (ACs)

- [ ] `core:ui` contiene el theme y tokens públicos aprobados.
- [ ] El tema de plantilla deja de ser la fuente funcional de la app.
- [ ] Temas claro y oscuro cubren todos los roles sin colores hardcodeados de plantilla.
- [ ] La fuente, logo, launcher, themed icon y splash proceden de fuentes versionadas.
- [ ] Apariencia se aplica sin flash observable en cold start.
- [ ] Preferencias válidas, ausentes e inválidas tienen tests deterministas.
- [ ] API 30 y Android 12+ cumplen la política de color dinámico.
- [ ] Goldens, trazabilidad y agent report están actualizados.

---

# SCDK-M139. Construir los componentes reutilizables del design system

## Main Story (How, I Want, To)

Como developer, quiero componentes visuales accesibles y reutilizables para que cada flujo no
reimplemente formularios, feedback, errores o estados sensibles de forma distinta.

## Context, Functional Description & Goal

Las pantallas actuales usan directamente componentes Material y estilos locales. Antes de
rediseñarlas debe existir una capa pequeña y deliberada de primitives SafeCube que incorpore tokens,
semántica y estados de interacción.

## Steps/Scope

### In Scope

- Implementar variantes canónicas de botón principal, secundario, textual y destructivo.
- Implementar campos de texto normales y secretos, incluyendo show/hide, error y supporting text.
- Implementar app bars, navegación inferior, cards e item rows.
- Implementar banners de información, warning, error, offline, draft, conflicto y éxito.
- Implementar diálogos de confirmación, progreso, estado vacío, error reintentable y error terminal.
- Definir loading, disabled, pressed, focused y error para cada componente interactivo.
- Incorporar targets mínimos, roles, labels, state descriptions y focus behavior.
- Evitar comunicar estado únicamente mediante color.
- Crear catálogo de previews deterministas en claro/oscuro y EN/ES donde el texto sea relevante.
- Añadir golden tests para los componentes críticos y tests semánticos para todas las interacciones.

### Out of Scope (if applies)

- Duplicar todo Material 3 bajo wrappers sin valor añadido.
- Crear componentes usados por una única pantalla sin necesidad demostrada.
- Cambiar estados de dominio o mensajes funcionales.
- Crear componentes para features post-MVP.

## Additional Information and Configuration

- Spec: `SPEC-UI-V1`.
- Dependencias: `SCDK-M137`, `SCDK-M138`.
- Las APIs públicas deben aceptar state y callbacks; no resolver navegación, red o repositorios.
- Los campos secretos deben preservar las garantías de `ADR-0003`.
- Crear `docs/sdd/agent-reports/SCDK-M139.md`.

### API Contract and Expected Behavior (if applies)

Los componentes son stateless siempre que resulte práctico. Loading deshabilita acciones duplicadas;
error conserva label y relación con el campo; el contenido secreto permanece enmascarado hasta una
acción explícita y vuelve a ocultarse según el lifecycle aprobado.

### Acceptance Criteria (ACs)

- [ ] Existe un catálogo de componentes y estados soportados.
- [ ] Las pantallas pueden construir formularios y feedback sin estilos locales equivalentes.
- [ ] Los componentes interactivos cumplen target mínimo y semántica definida.
- [ ] Ningún estado funcional depende solo del color.
- [ ] Los campos secretos respetan la política de exposición vigente.
- [ ] Goldens y tests semánticos cubren los componentes críticos.
- [ ] La API pública evita dependencias de features y app.
- [ ] Trazabilidad y agent report están actualizados.

---

# SCDK-M140. Rediseñar el shell, navegación y motion

## Main Story (How, I Want, To)

Como usuario, quiero una estructura visual estable para orientarme entre Bóveda y Ajustes y entender
los cambios de pantalla sin animaciones innecesarias.

## Context, Functional Description & Goal

Después de Fase 7 la navegación pública tiene dos destinos principales. La bottom navigation debe
mantener esa jerarquía, admitir una tercera tab futura y convivir con gates, rutas protegidas,
auto-lock y back behavior ya verificados.

## Steps/Scope

### In Scope

- Aplicar componentes de design system al shell raíz, splash gate, app bars y bottom navigation.
- Mantener los destinos `Bóveda` y `Ajustes` y su selección observable.
- Diseñar la estructura de tabs sin asumir exactamente dos elementos en su API.
- Unificar insets, system bars, márgenes de contenido y jerarquía de acciones.
- Definir motion tokens para entrada/salida, cambio de estado y feedback.
- Aplicar transiciones sutiles solo cuando expresen continuidad o resultado.
- Respetar la escala de animación del sistema, incluida escala `0`.
- Preservar restauración, set-root, deep navigation interna y back behavior de Fase 7.
- Añadir tests de selección, semántica de navegación, back behavior y reduced motion.

### Out of Scope (if applies)

- Añadir carpetas, perfil u otra tab.
- Cambiar rutas o políticas de acceso.
- Reescribir Navigation3.
- Animar contenido sensible de forma que permanezca visible después de lock.

## Additional Information and Configuration

- Spec: `SPEC-UI-V1`.
- Dependencias: `SCDK-M138`, `SCDK-M139`.
- Contratos de navegación de `SCDK-M117`, `SCDK-M125` y `SCDK-M129` son normativos.
- Crear `docs/sdd/agent-reports/SCDK-M140.md`.

### API Contract and Expected Behavior (if applies)

- Bóveda y Ajustes conservan destinos y back stack funcional.
- La navegación expone label y selected state a accesibilidad.
- Con escala de animación `0`, el destino final y el foco son idénticos al camino animado.
- Lock o expiración no deja un frame sensible durante la transición al destino seguro.

### Acceptance Criteria (ACs)

- [ ] La bottom navigation conserva Bóveda/Ajustes y admite una tercera entrada sin cambiar API.
- [ ] App bars, insets y jerarquía son consistentes en las superficies que usan el shell.
- [ ] Las transiciones respetan motion scale, incluida escala `0`.
- [ ] Rutas, gates, set-root y back behavior no cambian funcionalmente.
- [ ] Semántica anuncia destino y selección correctamente.
- [ ] Tests de navegación existentes y nuevos pasan.
- [ ] Goldens, trazabilidad y agent report están actualizados.

---

# SCDK-M141. Rediseñar Welcome, Login y Signup

## Main Story (How, I Want, To)

Como persona nueva o recurrente, quiero entender el valor de SafeCube y completar autenticación con
feedback claro sin confundir login de cuenta con unlock del vault.

## Context, Functional Description & Goal

Welcome conserva literales y una composición de plantilla; Login y Signup ya modelan loading,
validación, errores y retry, pero presentan esos estados mediante controles locales. Este flujo es
la primera impresión de marca y debe ser accesible con teclado, font scale y TalkBack.

## Steps/Scope

### In Scope

- Rediseñar Welcome, Login y Signup con la identidad y componentes aprobados.
- Localizar todos los textos y retirar literales de producción.
- Mantener separación explícita entre contraseña de cuenta y passphrase del vault.
- Aplicar autofill y keyboard options apropiados únicamente a credenciales de cuenta.
- Definir foco inicial, navegación IME, submit y retorno tras error.
- Mostrar validación junto al campo y resumen global solo cuando aporte contexto adicional.
- Conservar email cuando proceda y limpiar passwords según el contrato de Fase 7.
- Representar loading, error reintentable y terminal sin duplicar solicitudes.
- Añadir previews, goldens de estados críticos y tests semánticos Compose.
- Validar EN/ES, claro/oscuro, font scale y ventanas compactas/medias.

### Out of Scope (if applies)

- Cambiar endpoints, validación de dominio o sesión.
- Añadir social login, magic links, forgot password o passkeys.
- Usar biometría para login backend.
- Añadir onboarding multipágina.

## Additional Information and Configuration

- Spec: `SPEC-UI-V1`, `SPEC-HARDENING-V1` y contrato auth.
- Dependencias: `SCDK-M139`, `SCDK-M140`.
- Los tests de ViewModel de Fase 7 siguen siendo evidencia funcional y no se sustituyen.
- Crear `docs/sdd/agent-reports/SCDK-M141.md`.

### API Contract and Expected Behavior (if applies)

El rediseño consume los mismos `UiState`, `UiAction` y `UiEvent`. Submit durante loading no dispara
otra solicitud; retry repite la operación autorizada; errores no muestran bodies ni mensajes raw.

### Acceptance Criteria (ACs)

- [ ] Welcome, Login y Signup usan design system y no contienen literales visibles.
- [ ] Todos los estados funcionales previos siguen siendo alcanzables y explicables.
- [ ] Teclado, IME, foco y autofill de credenciales se comportan de forma coherente.
- [ ] TalkBack permite completar Login sin acciones sin label o foco perdido.
- [ ] EN/ES, claro/oscuro y font scale hasta 200 % no ocultan acciones críticas.
- [ ] Goldens cubren al menos Welcome, Login idle/loading/error y Signup validation.
- [ ] Tests de ViewModel, Compose y navegación aplicables pasan.
- [ ] Trazabilidad y agent report están actualizados.

---

# SCDK-M142. Rediseñar bootstrap, creación, recovery y unlock

## Main Story (How, I Want, To)

Como usuario, quiero crear o desbloquear mi vault entendiendo cada paso y sus consecuencias sin
exponer passphrase, recovery key ni material sensible.

## Context, Functional Description & Goal

PostLoginGate, Create Vault, Recovery Key, Unlock y quick unlock contienen las transiciones más
sensibles del producto. Fase 7 estabilizó su state machine, fallback, process death y seguridad;
Fase 8 debe hacer esos estados claros sin alterar su prioridad ni persistencia.

## Steps/Scope

### In Scope

- Rediseñar PostLoginGate y sus estados de espera, retry y fallo terminal.
- Rediseñar creación del vault como primer paso de un proceso comprensible.
- Rediseñar Recovery Key con jerarquía, warning y confirmación explícita.
- Rediseñar Unlock manteniendo passphrase visible como fallback.
- Integrar visualmente oferta, prompt, cancelación y error de quick unlock.
- Mantener diferenciados account login, vault unlock y recovery.
- Proteger campos y recovery key mediante componentes sensibles aprobados.
- Garantizar que lock o lifecycle retira plaintext visible sin esperar una animación.
- Preservar avisos sanitizados de passphrase remota y reconciliación indeterminada.
- Añadir previews, goldens, tests semánticos y pruebas instrumentadas de los estados críticos.

### Out of Scope (if applies)

- Cambiar KDF, envelopes, Keystore, quick unlock o bootstrap.
- Modificar prioridad de rutas o persistencia de recovery pendiente.
- Añadir PIN propio, export de vault o captura de recovery key.
- Rediseñar el diálogo biométrico del sistema.

## Additional Information and Configuration

- Specs: `SPEC-UI-V1`, `SPEC-HARDENING-V1`, `SPEC-CRYPTO-V1`.
- ADRs: `ADR-0001`, `ADR-0002`, `ADR-0003`.
- Dependencias: `SCDK-M139`, `SCDK-M140`.
- Ningún fixture debe contener material con apariencia de secreto real reutilizable.
- Crear `docs/sdd/agent-reports/SCDK-M142.md`.

### API Contract and Expected Behavior (if applies)

Se conservan state machines, actions y eventos. Cancelar quick unlock deja el vault bloqueado y la
passphrase disponible; confirmar Recovery Key conserva la semántica de borrado; lock interrumpe la
presentación sensible inmediatamente.

### Acceptance Criteria (ACs)

- [ ] Bootstrap, Create, Recovery, Unlock y quick unlock usan el design system.
- [ ] Login, passphrase, recovery y quick unlock se explican como conceptos distintos.
- [ ] Passphrase y recovery key no aparecen en semántica o screenshots cuando están ocultas.
- [ ] Cancelación, fallback, retry, lock y errores remotos conservan el comportamiento previo.
- [ ] TalkBack permite completar Unlock mediante passphrase.
- [ ] Font scale y ventanas soportadas conservan warnings, campos y acciones.
- [ ] Goldens y tests cubren estados sensibles sin datos reales.
- [ ] Tests funcionales y de seguridad existentes siguen pasando.

---

# SCDK-M143. Rediseñar Vault Home, sync, drafts y conflictos

## Main Story (How, I Want, To)

Como usuario, quiero comprender qué datos están disponibles localmente y qué está ocurriendo con la
sincronización para actuar ante offline, drafts o conflictos sin temor a perder cambios.

## Context, Functional Description & Goal

Vault Home combina contenido local, acciones de creación, sync manual, resultados, drafts y
conflictos. La presentación actual prioriza controles genéricos y texto técnico. El rediseño debe
mantener Room como source of truth y hacer visible la diferencia entre disponibilidad local y
estado remoto.

## Steps/Scope

### In Scope

- Rediseñar top bar, acciones de creación, lista e items del vault.
- Diseñar estados initial loading, empty, content y local read error.
- Representar sync idle, syncing, pending, success, retryable error y terminal/protocol error.
- Representar draft create/update/delete y conflicto mediante texto, icono y semántica.
- Mantener contenido local visible durante errores remotos cuando el contrato lo permita.
- Convertir retry y sync manual en acciones claras, sin duplicación durante progreso.
- Mejorar jerarquía de metadata no sensible sin introducir contenido nuevo.
- Mantener feedback accesible y no depender de Toast como único canal para información relevante.
- Añadir previews, goldens y tests semánticos para los estados críticos.
- Validar lista, vacío y feedback con EN/ES, font scale y ventana media.

### Out of Scope (if applies)

- Cambiar sync v2, orden de operaciones, retries o resolución de conflictos.
- Añadir búsqueda, filtros, carpetas o background sync.
- Mostrar previews del contenido secreto en la lista.
- Añadir timestamps o metadata sensible no existente.

## Additional Information and Configuration

- Specs: `SPEC-UI-V1`, `SPEC-HARDENING-V1`, `SPEC-VAULT-SYNC-V2`.
- Dependencias: `SCDK-M139`, `SCDK-M140`.
- Los estados de `VaultHomeUiState` son autoridad funcional; un cambio necesario requiere revisión
  de spec y una task separada.
- Crear `docs/sdd/agent-reports/SCDK-M143.md`.

### API Contract and Expected Behavior (if applies)

La UI sigue observando almacenamiento local y estado de sync por separado. Un error remoto no
reemplaza contenido local; una acción loading se deshabilita; draft y conflicto nunca se codifican
exclusivamente por color.

### Acceptance Criteria (ACs)

- [ ] Initial loading, empty, content y error local tienen representaciones diferenciadas.
- [ ] Sync y disponibilidad local no se presentan como un único estado ambiguo.
- [ ] Drafts y conflictos usan texto, icono y state description accesible.
- [ ] Retry manual es visible solo cuando corresponde y no duplica operaciones.
- [ ] Contenido local permanece visible durante fallos remotos según la spec.
- [ ] TalkBack permite identificar items, estado de sync y ejecutar retry.
- [ ] Goldens cubren vacío, contenido, syncing, error retryable y conflicto/draft.
- [ ] Tests de Vault Home, sync y Compose aplicables pasan.

---

# SCDK-M144. Rediseñar los editores de password y nota

## Main Story (How, I Want, To)

Como usuario, quiero crear y editar passwords y notas con formularios consistentes para saber qué
se guarda localmente, qué está pendiente y cuándo una acción necesita intervención.

## Context, Functional Description & Goal

Los editores comparten coordinación de lifecycle, observación y mutación, pero mantienen layouts y
controles específicos. Deben converger visualmente sin mover lógica a UI ni debilitar la protección
de campos secretos, drafts o payloads corruptos.

## Steps/Scope

### In Scope

- Rediseñar el scaffold común de editores y sus app bars.
- Aplicar campos canónicos a título, username, password y nota.
- Mantener password enmascarada por defecto con reveal explícito y accesible.
- Diseñar create/edit, dirty, saving, deleting, draft y payload error.
- Unificar acciones Save, Delete, Publish, Discard y Save as new según disponibilidad.
- Mostrar errores junto a su acción o campo sin mensajes raw.
- Conservar estado útil durante errores reintentables según el contrato existente.
- Impedir visualmente doble submit durante mutación.
- Mantener salida segura cuando el vault se bloquea durante una operación.
- Añadir previews, goldens y tests semánticos de ambos tipos de editor.

### Out of Scope (if applies)

- Cambiar payload v1 o añadir campos.
- Añadir generador de passwords, rich text, attachments o autofill service.
- Cambiar officialization, drafts, soft delete o sync.
- Recuperar o eliminar automáticamente payloads corruptos.

## Additional Information and Configuration

- Specs: `SPEC-UI-V1`, `SPEC-HARDENING-V1`, `SPEC-SECURE-ITEM-PAYLOAD-V1`.
- ADR: `ADR-0003`.
- Dependencias: `SCDK-M139`, `SCDK-M140`.
- Crear `docs/sdd/agent-reports/SCDK-M144.md`.

### API Contract and Expected Behavior (if applies)

Los ViewModels y coordinadores existentes conservan ownership. La UI proyecta state y emite actions;
loading bloquea duplicados; lock cancela/abandona presentación sensible; corrupción falla en cerrado
sin borrado automático.

### Acceptance Criteria (ACs)

- [ ] Password y Note comparten scaffold y lenguaje de acciones.
- [ ] Todos los estados de mutación y draft existentes tienen representación observable.
- [ ] Password permanece oculta por defecto y reveal tiene label y state description.
- [ ] Ningún error muestra payload, secreto o mensaje técnico raw.
- [ ] Lock durante operación retira contenido sensible y navega según el contrato vigente.
- [ ] Font scale y landscape permiten alcanzar todas las acciones.
- [ ] Goldens cubren create, edit, validation, mutating y draft/error.
- [ ] Tests de ViewModel, mutación, lifecycle y Compose pasan.

---

# SCDK-M145. Rediseñar Settings y operaciones sensibles

## Main Story (How, I Want, To)

Como usuario, quiero gestionar apariencia y seguridad desde una pantalla ordenada que distinga
preferencias reversibles de acciones sensibles o destructivas.

## Context, Functional Description & Goal

Settings reúne auto-lock, quick unlock, cambio de passphrase, Lock now y logout. La Fase 8 añade
tema y Material You, pero no debe ocultar consecuencias de seguridad ni mezclar preferencias
visuales con acciones destructivas sin jerarquía.

## Steps/Scope

### In Scope

- Rediseñar Settings por secciones: Appearance, Vault security y Account/session.
- Añadir selector de tema `System`, `Light`, `Dark`.
- Añadir control de Material You desactivado por defecto.
- Mostrar en API 30 el control dinámico deshabilitado con explicación localizada.
- Aplicar cambios de apariencia de forma inmediata y persistente.
- Rediseñar auto-lock y quick unlock con estado y explicación accesibles.
- Rediseñar Change Passphrase y su feedback sin alterar el rewrap.
- Jerarquizar Lock now, logout y confirmación cuando existen drafts.
- Integrar forced logout notice y diálogos globales en el design system.
- Añadir previews, goldens, tests semánticos y tests de persistencia de apariencia.

### Out of Scope (if applies)

- Sincronizar apariencia por cuenta.
- Añadir ajustes de idioma, fuente, densidad o color manual.
- Cambiar timeout, quick unlock, passphrase o logout.
- Añadir perfil o pantallas eliminadas en Fase 7.

## Additional Information and Configuration

- Specs: `SPEC-UI-V1`, `SPEC-HARDENING-V1`.
- ADRs: design system de `SCDK-M136`, `ADR-0001`, `ADR-0002`, `ADR-0003`.
- Dependencias: `SCDK-M138`, `SCDK-M139`, `SCDK-M140`.
- Crear `docs/sdd/agent-reports/SCDK-M145.md`.

### API Contract and Expected Behavior (if applies)

- Cambiar tema no reinicia sesión, vault ni navegación.
- Logout conserva apariencia.
- API 30 conserva el flag en `false` y no permite activar Material You.
- Android 12+ aplica color dinámico solo tras opt-in.
- Acciones sensibles mantienen confirmaciones, bloqueo y limpieza existentes.

### Acceptance Criteria (ACs)

- [ ] Settings distingue visual y semánticamente apariencia, seguridad y sesión.
- [ ] Sistema/Claro/Oscuro se aplican y persisten sin reiniciar la app.
- [ ] Material You cumple defaults y comportamiento por versión de Android.
- [ ] Logout conserva la apariencia y no conserva datos que deban limpiarse.
- [ ] Auto-lock, quick unlock, passphrase, Lock now y logout preservan sus contratos.
- [ ] Confirmaciones destructivas explican las consecuencias antes de ejecutar.
- [ ] Goldens cubren Settings y diálogos sensibles en estados representativos.
- [ ] Tests de preferencias, Settings, sesión y Compose pasan.

---

# SCDK-M146. Completar la auditoría adaptativa, accesible y lingüística

## Main Story (How, I Want, To)

Como release manager, quiero una auditoría pragmática y reproducible para asegurar que los flujos
críticos siguen siendo utilizables con tamaños, idiomas y ayudas de accesibilidad habituales.

## Context, Functional Description & Goal

Cada card añade pruebas locales, pero el producto puede fallar transversalmente por contraste,
orden de foco, texto al 200 %, landscape, labels repetidas o copy divergente. Esta card corrige los
gaps bloqueantes y registra mejoras avanzadas sin convertir la fase en una certificación WCAG.

## Steps/Scope

### In Scope

- Auditar contraste de texto, iconos y estados interactivos contra el nivel AA aplicable.
- Verificar targets táctiles mínimos de 48dp o área interactiva equivalente.
- Verificar roles, labels, headings, state descriptions, live feedback y orden de foco.
- Recorrer con TalkBack Login, Unlock, creación de item, retry de sync y Settings.
- Verificar font scale 100 %, intermedio y 200 % sin pérdida de acciones críticas.
- Verificar ventanas compactas/medias, portrait y landscape usando espacio disponible.
- Verificar cambios de tamaño sin perder input o contexto de navegación.
- Verificar escala de animación `0` y una escala aumentada sin timeouts ligados a motion.
- Auditar paridad de recursos EN/ES, plurales, textos largos, elipsis y literales.
- Revisar el tono cercano y directo sin suavizar advertencias de seguridad.
- Corregir findings dentro de los contratos existentes.
- Registrar como follow-up los gaps avanzados que no bloquean los flujos críticos.

### Out of Scope (if applies)

- Certificación formal WCAG 2.2 AA/AAA.
- Tablet/foldable/desktop dedicados.
- RTL, nuevos idiomas, switch access o voice control exhaustivos.
- Estudios de usabilidad con participantes externos.
- Cambiar comportamiento funcional para resolver una preferencia estética.

## Additional Information and Configuration

- Spec: `SPEC-UI-V1`.
- Dependencias: `SCDK-M141`–`SCDK-M145`.
- Usar herramientas automáticas como apoyo, no como sustituto del recorrido manual crítico.
- No capturar ni adjuntar datos sensibles en la evidencia.
- Crear `docs/sdd/agent-reports/SCDK-M146.md`.

### API Contract and Expected Behavior (if applies)

No cambia contratos de dominio. Un finding que requiera modificar comportamiento, ruta o estado se
detiene y se convierte en una task/spec separada. Los findings puramente visuales o semánticos se
corrigen en esta card.

### Acceptance Criteria (ACs)

- [ ] Las combinaciones funcionales auditadas cumplen contraste AA aplicable.
- [ ] Controles interactivos cumplen el target mínimo y tienen semántica accionable.
- [ ] Los cinco flujos críticos pueden completarse con TalkBack.
- [ ] Font scale hasta 200 % no oculta acciones ni fuerza solapamientos críticos.
- [ ] Compact/medium, portrait/landscape y resize conservan input y navegación.
- [ ] Escala de animación `0` conserva resultado, foco y feedback.
- [ ] Inglés y español tienen paridad, plurales correctos y cero literales visibles.
- [ ] Gaps avanzados quedan registrados como follow-up con prioridad y evidencia.

---

# SCDK-M147. Crear la matriz de regresión y cerrar la verificación de Fase 8

## Main Story (How, I Want, To)

Como release manager, quiero una matriz reproducible de UI para demostrar que la identidad, los
flujos y los gates de Fase 8 cumplen su spec antes de iniciar observabilidad de producción.

## Context, Functional Description & Goal

Las cards anteriores generan design assets, componentes, goldens y tests locales. Falta relacionar
cada requisito con evidencia, ejecutar la combinación canónica de gates y declarar de forma honesta
si la fase está DONE, PARTIAL o BLOCKED.

## Steps/Scope

### In Scope

- Crear `docs/testing/phase-8-ui-matrix.md`.
- Mapear cada requisito de `SPEC-UI-V1` a código, golden, test semántico, test instrumentado o
  evidencia manual justificada.
- Cubrir como goldens mínimos: componentes base, Welcome/Login, Unlock, Vault empty/content/error,
  un editor y Settings.
- Validar los estados críticos con tema claro/oscuro y paleta de marca.
- Validar opt-in de Material You en una API soportada y estado deshabilitado en API 30.
- Validar EN/ES, font scale hasta 200 % y motion scale `0` según la matriz, sin crear el producto
  cartesiano completo cuando no aporte evidencia.
- Ejecutar tests Compose, `verifyPhase8Screenshots`, lint y tests de módulos afectados.
- Ejecutar `./gradlew ciVerify`, `./gradlew releaseVerify` y la suite instrumentada canónica.
- Verificar que los reportes y artefactos no contienen datos sensibles.
- Actualizar spec registry y matriz de trazabilidad con paths y comandos exactos.
- Promover `SPEC-UI-V1` a `VERIFIED` solo si todos sus ACs tienen evidencia.
- Crear el informe final de fase.

### Out of Scope (if applies)

- E2E contra backend real y matriz completa API 30-36; pertenecen a Fase 10.
- Observabilidad, telemetría o retirada del logger debug; pertenecen a Fase 9.
- Aprobar automáticamente cambios de golden.
- Ocultar gaps mediante exclusiones, baselines o evidencia manual genérica.

## Additional Information and Configuration

- Spec: `SPEC-UI-V1`.
- Dependencias: `SCDK-M135`–`SCDK-M146`.
- Specs de producto y hardening siguen siendo gates de no regresión funcional y de seguridad.
- No usar sleeps, red real, cuentas reales ni secretos en la suite.
- Crear `docs/sdd/agent-reports/SCDK-M147.md`.

### API Contract and Expected Behavior (if applies)

La suite no cambia contratos. Actúa como evidencia ejecutable y falla cuando una referencia visual,
semántica accesible o flujo crítico deja de cumplir la spec. Actualizar una referencia requiere una
decisión revisada, no solo regenerar el archivo.

### Acceptance Criteria (ACs)

- [ ] Cada requisito de `SPEC-UI-V1` tiene evidencia reproducible o gap explícito.
- [ ] Los goldens mínimos y tests semánticos pasan en el entorno canónico.
- [ ] Tema, Material You, idiomas, font scale y reduced motion están cubiertos según la matriz.
- [ ] Los recorridos TalkBack críticos tienen evidencia manual reproducible.
- [ ] `ciVerify`, `releaseVerify` y la suite instrumentada pasan.
- [ ] CI valida y nunca regenera referencias visuales.
- [ ] Registry y trazabilidad contienen paths, comandos y resultados exactos.
- [ ] No existen secretos ni datos sensibles en referencias, diffs, reports o informes.
- [ ] `SPEC-UI-V1` está `VERIFIED` o los gaps impiden explícitamente esa promoción.
- [ ] El agent report final declara Fase 8 `DONE`, `PARTIAL` o `BLOCKED` con siguiente acción.
