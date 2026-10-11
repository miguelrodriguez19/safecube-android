# SPEC-UI-V1 — Contrato de UI, marca, motion y accesibilidad de SafeCube v1

## Metadata

| Campo              | Valor                                                                                                                                                                                                                                                                                                          |
|--------------------|----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| ID                 | `SPEC-UI-V1`                                                                                                                                                                                                                                                                                                   |
| Estado             | `APPROVED`                                                                                                                                                                                                                                                                                                     |
| Owner              | `maintainer/product-design`                                                                                                                                                                                                                                                                                    |
| Fecha              | `2026-09-26`                                                                                                                                                                                                                                                                                                   |
| Última revisión    | `2026-10-11`                                                                                                                                                                                                                                                                                                   |
| Reemplaza          | `N/A`                                                                                                                                                                                                                                                                                                          |
| Dependencias       | [SPEC-PRODUCT-V1](../product/v1-product-brief.md), [SPEC-HARDENING-V1](./hardening-resilience-v1.md), [ADR-0001](../../architecture/adr/ADR-0001-VAULT-AUTO-LOCK.md), [ADR-0002](../../architecture/adr/ADR-0002-PASSPHRASE-REWRAP.md), [ADR-0003](../../architecture/adr/ADR-0003-SENSITIVE-DATA-SURFACES.md) |
| Tasks relacionadas | `SCDK-M134`–`SCDK-M147`                                                                                                                                                                                                                                                                                        |

## Problema y contexto

SafeCube terminó la Fase 7 con rutas, estados, seguridad y recuperación verificadas, pero carece de
un contrato visual transversal. El tema actual procede de la plantilla, las features componen
Material 3 directamente y no existen tokens, catálogo de componentes, reglas de motion, cobertura
visual ni una matriz de accesibilidad.

Esta spec define el resultado observable de Fase 8. No sustituye ni reinterpreta los contratos de
producto y hardening: las rutas, prioridades de navegación, retries, locks, quick unlock, cambio de
passphrase, drafts, conflictos, limpieza y protección de datos sensibles permanecen bajo sus fuentes
normativas existentes.

## Objetivos

- `OBJ-UI-001`: ofrecer una experiencia coherente, reconocible, accesible y verificable sobre todos
  los flujos públicos de SafeCube v1.
- `OBJ-UI-002`: convertir los estados estabilizados en Fase 7 en representaciones visuales con
  ownership y evidencia definidos.
- `OBJ-UI-003`: permitir que cada card de Fase 8 implemente UI sin decidir localmente identidad,
  componentes, motion, adaptación, accesibilidad, localización o privacidad visual.

## No objetivos

- Modificar comportamiento de dominio, rutas, crypto, storage, red, sync o lifecycle.
- Elegir en esta spec valores finales de tokens, tipografía concreta o activos gráficos.
- Añadir rutas, tabs, campos, acciones o funcionalidades ajenas al alcance v1.
- Diseñar una experiencia dedicada para tablets, foldables expandidos o desktop.
- Certificar WCAG 2.2 AA/AAA o cubrir exhaustivamente RTL, switch access o control por voz.

## Actores y casos de uso

| Actor                                   | Caso de uso                              | Resultado esperado                                                                                 |
|-----------------------------------------|------------------------------------------|----------------------------------------------------------------------------------------------------|
| Persona nueva                           | Descubrir SafeCube, crear cuenta y vault | Distingue credenciales de cuenta, passphrase y recovery key y completa el flujo sin ambigüedad.    |
| Persona recurrente                      | Iniciar sesión y desbloquear el vault    | Comprende lock, quick unlock, fallback y errores sin exponer secretos.                             |
| Persona con contenido                   | Consultar y editar items                 | Diferencia contenido local, sync, draft y conflicto y conserva control durante fallos.             |
| Persona que usa ayudas de accesibilidad | Completar flujos críticos                | Puede operar con TalkBack, foco predecible, targets suficientes, texto al 200 % y motion reducido. |
| Maintainer                              | Implementar y verificar una pantalla     | Consume contratos compartidos y aporta la evidencia asignada sin reinterpretar Fase 7.             |

## Requisitos normativos

### Requisitos funcionales

- `FR-UI-001` — Inventario y ownership: toda ruta o superficie pública de v1 debe aparecer en el
  inventario y en la matriz de estados de esta spec. Cada combinación aplicable debe tener un owner
  de implementación y verificación; una combinación no aplicable debe declararse `N/A`.
- `FR-UI-002` — Identidad y apariencia: SafeCube debe aplicar una identidad cálida, cercana,
  confiable, sobria y moderna, basada en azul petróleo y acentos cálidos, con paletas propias clara
  y oscura. Debe ofrecer `System`, `Light` y `Dark`; Material You será independiente, opt-in y
  desactivado por defecto. En API 30 el control seguirá visible, deshabilitado y explicado.
- `FR-UI-003` — Design system y feedback: `core:ui` debe ser dueño del tema, tokens y componentes
  públicos. Estados equivalentes deben usar componentes y lenguaje consistentes; ningún estado
  dependerá exclusivamente de color. Loading debe impedir duplicados y los errores deben colocar
  retry o acción correctiva junto a su contexto.
- `FR-UI-004` — Navegación visual: la jerarquía pública seguirá siendo `Bóveda / Ajustes`, con una
  API capaz de admitir una tercera tab futura sin añadirla ahora. El rediseño debe conservar gates,
  set-root, restauración, back behavior, prioridades seguras y retirada inmediata de contenido al
  bloquearse el vault.

### Requisitos no funcionales

- `NFR-UI-001` — Motion: las animaciones deben ser sutiles y explicar continuidad, jerarquía, cambio
  de estado o resultado. Deben respetar la escala de animación del sistema; con escala `0`, el
  resultado, contenido, foco y acciones deben ser equivalentes e inmediatos.
- `NFR-UI-002` — Adaptación: todas las superficies deben funcionar en Android API 30–36, ventanas
  compactas y medias, portrait y landscape, y font scale hasta 200 %, sin asumir dimensiones o
  proporciones fijas ni ocultar acciones críticas. La rotación debe preservar estado, foco y acceso
  a las acciones; no se bloqueará la orientación de las pantallas.
- `NFR-UI-003` — Accesibilidad: contraste aplicable, targets táctiles mínimos de 48dp o área
  equivalente, roles, labels, headings, state descriptions, orden de foco y feedback accesible son
  gates bloqueantes. Login, Unlock, creación de un item, retry manual de sync y Settings deben poder
  completarse con TalkBack.
- `NFR-UI-004` — Localización: inglés y español deben conservar paridad de recursos, intención y
  consecuencias. La voz será cercana, directa y tranquila, sin jerga innecesaria ni atenuación de
  advertencias de seguridad. Los textos largos no deben truncar acciones críticas.
- `NFR-UI-005` — Regresión: componentes y estados críticos deben tener evidencia reproducible
  mediante golden, test semántico, prueba instrumentada o recorrido manual definido. CI verificará
  las referencias y nunca las regenerará ni aceptará automáticamente.

### Seguridad y privacidad

- `SEC-UI-001` — Privacidad visual: campos secretos, recovery key, contenido de items y estados de
  sesión deben cumplir [ADR-0003](../../architecture/adr/ADR-0003-SENSITIVE-DATA-SURFACES.md).
  Screenshots, previews, goldens, fixtures, diffs, reports y evidencia manual solo usarán datos
  sintéticos inequívocos y nunca secretos, tokens, payloads, recovery keys o identificadores de
  cuentas reales. Lock o lifecycle debe retirar plaintext sin esperar a una animación.

## Límites con contratos existentes

- [SPEC-PRODUCT-V1](../product/v1-product-brief.md) conserva el alcance de producto.
- [SPEC-HARDENING-V1](./hardening-resilience-v1.md) conserva state machines, errores, retries,
  lifecycle, scope y requisitos de seguridad/resiliencia.
- [ADR-0001](../../architecture/adr/ADR-0001-VAULT-AUTO-LOCK.md) conserva auto-lock y quick unlock.
- [ADR-0002](../../architecture/adr/ADR-0002-PASSPHRASE-REWRAP.md) conserva rewrap, CAS y
  reconciliación del cambio de passphrase.
- [ADR-0003](../../architecture/adr/ADR-0003-SENSITIVE-DATA-SURFACES.md) conserva las reglas de
  exposición, persistencia, semántica, screenshots y clipboard de datos sensibles.

Si una implementación visual necesita cambiar cualquiera de esos comportamientos, se detiene y se
tramita una spec/task o ADR separado. Una preferencia estética no autoriza un cambio funcional.

## Inventario normativo de superficies

| ID de superficie       | Ruta o tipo               | Alcance y estados funcionales preservados                                                          | Owner de Fase 8           |
|------------------------|---------------------------|----------------------------------------------------------------------------------------------------|---------------------------|
| `UI-SPLASH`            | `Routes.Splash`           | Transición de sistema sin contenido interactivo; resuelve sesión sin flash de rutas protegidas.    | `SCDK-M140`               |
| `UI-WELCOME`           | `Routes.Welcome`          | Entrada pública a Login y Signup.                                                                  | `SCDK-M141`               |
| `UI-LOGIN`             | `Routes.Login`            | Idle, validación, loading, retryable/offline, terminal y éxito de autenticación.                   | `SCDK-M141`               |
| `UI-SIGNUP`            | `Routes.Signup`           | Idle, validación, loading, cuenta existente, retryable/offline, terminal y éxito.                  | `SCDK-M141`               |
| `UI-POST-LOGIN`        | `Routes.PostLoginGate`    | Gate transitorio que decide Create, Recovery, Unlock o Home sin exponer contenido protegido.       | `SCDK-M142`               |
| `UI-CREATE-VAULT`      | `Routes.CreateVault`      | Passphrase, mutación, retryable/terminal y navegación a Recovery o Unlock existente.               | `SCDK-M142`               |
| `UI-RECOVERY`          | `Routes.RecoveryKey`      | Carga, recovery disponible, confirmación, retryable/terminal y continuación segura.                | `SCDK-M142`               |
| `UI-UNLOCK`            | `Routes.UnlockVault`      | Vault bloqueado, passphrase fallback, quick unlock, loading, retryable/terminal y éxito.           | `SCDK-M142`               |
| `UI-VAULT-HOME`        | `Routes.Vault`            | Initial loading, empty, content, error local, sync, offline, drafts, conflictos y resultados.      | `SCDK-M143`               |
| `UI-PASSWORD-CREATE`   | `Routes.CreatePassword`   | Editor nuevo, dirty, saving, error, draft y lock.                                                  | `SCDK-M144`               |
| `UI-PASSWORD-EDIT`     | `Routes.EditPassword`     | Loading, editable, saving, not found, draft/conflict, corrupción, storage failure y lock.          | `SCDK-M144`               |
| `UI-NOTE-CREATE`       | `Routes.CreateNote`       | Editor nuevo, dirty, saving, error, draft y lock.                                                  | `SCDK-M144`               |
| `UI-NOTE-EDIT`         | `Routes.EditNote`         | Loading, editable, saving, not found, draft/conflict, corrupción, storage failure y lock.          | `SCDK-M144`               |
| `UI-CHANGE-PASSPHRASE` | `Routes.ChangePassphrase` | Idle, validación, loading, retryable, uncertain, terminal, session required y success.             | `SCDK-M145`               |
| `UI-SETTINGS`          | `Routes.Settings`         | Apariencia, auto-lock, quick unlock, change passphrase, Lock now y logout.                         | `SCDK-M145`               |
| `UI-QUICK-UNLOCK`      | Superficie sin ruta       | Oferta, prompt de sistema, cancelación, error, invalidación y fallback a passphrase.               | `SCDK-M142` / `SCDK-M145` |
| `UI-GLOBAL-DIALOGS`    | Superficies sin ruta      | Forced logout, causa de lock/cambio remoto, reconciliación indeterminada y selección de auto-lock. | `SCDK-M142` / `SCDK-M145` |
| `UI-GLOBAL-FEEDBACK`   | Superficie transversal    | Back/exit y feedback de sync; información relevante no puede depender solo de Toast.               | `SCDK-M140` / `SCDK-M143` |

`Splash` es una transición del sistema, no una pantalla de producto. Quick unlock, diálogos y
feedback global son superficies verificables sin ruta propia y no amplían el grafo de navegación.

## Matriz pantalla × estado

Leyenda:

- `Required` = representación obligatoria.
- `Conditional` = obligatoria cuando el estado funcional indicado se produce.
- `N/A` = no pertenece al contrato de esa superficie. Ninguna celda habilita un estado nuevo.

| Superficie        | initial loading | content  | empty    | mutating | retryable error | terminal error | locked      | offline     | draft       | conflict    | success  |
|-------------------|-----------------|----------|----------|----------|-----------------|----------------|-------------|-------------|-------------|-------------|----------|
| Splash            | Required        | N/A      | N/A      | N/A      | N/A             | N/A            | Conditional | N/A         | N/A         | N/A         | Required |
| Welcome           | N/A             | Required | N/A      | N/A      | N/A             | N/A            | N/A         | N/A         | N/A         | N/A         | N/A      |
| Login             | N/A             | Required | N/A      | Required | Required        | Required       | N/A         | Required    | N/A         | N/A         | Required |
| Signup            | N/A             | Required | N/A      | Required | Required        | Required       | N/A         | Required    | N/A         | N/A         | Required |
| PostLoginGate     | Required        | N/A      | N/A      | Required | Conditional     | Conditional    | Conditional | Conditional | N/A         | N/A         | Required |
| Create Vault      | N/A             | Required | N/A      | Required | Required        | Required       | N/A         | Required    | N/A         | N/A         | Required |
| Recovery Key      | Required        | Required | N/A      | Required | Required        | Required       | N/A         | Conditional | N/A         | N/A         | Required |
| Unlock            | N/A             | Required | N/A      | Required | Required        | Required       | Required    | Conditional | N/A         | N/A         | Required |
| Vault Home        | Required        | Required | Required | Required | Required        | Required       | Conditional | Required    | Required    | Required    | Required |
| Password create   | N/A             | Required | N/A      | Required | Required        | Required       | Conditional | Conditional | Required    | Conditional | Required |
| Password edit     | Required        | Required | N/A      | Required | Required        | Required       | Conditional | Conditional | Required    | Required    | Required |
| Note create       | N/A             | Required | N/A      | Required | Required        | Required       | Conditional | Conditional | Required    | Conditional | Required |
| Note edit         | Required        | Required | N/A      | Required | Required        | Required       | Conditional | Conditional | Required    | Required    | Required |
| Change Passphrase | N/A             | Required | N/A      | Required | Required        | Required       | Required    | Conditional | N/A         | Conditional | Required |
| Settings          | Required        | Required | N/A      | Required | Conditional     | Conditional    | Conditional | N/A         | Conditional | N/A         | Required |
| Quick unlock      | N/A             | Required | N/A      | Required | Conditional     | Conditional    | Required    | N/A         | N/A         | N/A         | Required |
| Global dialogs    | N/A             | Required | N/A      | Required | Conditional     | Required       | Conditional | N/A         | Conditional | Conditional | Required |
| Global feedback   | N/A             | Required | N/A      | Required | Required        | Required       | Conditional | Required    | Required    | Required    | Required |

Ownership funcional de las celdas:

- Login/Signup conservan `AuthUiOperationState`.
- Create, Recovery y Unlock conservan `VaultUiOperationState`.
- Home conserva `VaultHomeUiState`.
- Editores conservan `SecureItemEditorState` y estado de draft.
- Change Passphrase conserva `ChangePassphraseUiOperationState`.
- Settings y quick unlock conservan sus estados de Fase 7. `Conditional` solo exige representar el
  estado cuando esos owners lo emiten.

## Contrato de identidad, apariencia y design system

- La marca será cálida, cercana, confiable, sobria y moderna, con densidad equilibrada, formas
  suaves y radios moderados. El logo fuente conservará su geometría; SCDK-M135 decidirá variantes
  cromáticas, monocromas, ópticas y técnicas.
- SCDK-M135 fijará paletas clara/oscura, tipografía redistribuible, iconografía y activos. Esta spec
  no fija valores ni licencias concretas antes de esa aprobación.
- `core:ui` poseerá tema, tokens y componentes públicos. `app` aplicará el tema raíz y poseerá la
  persistencia local de apariencia. Las features consumirán APIs públicas y no persistirán
  preferencias visuales.
- El default será `AppearanceMode.System` con color dinámico desactivado. Logout y cambio de cuenta
  conservarán apariencia; borrar datos restaurará defaults; valores inválidos fallarán al default.
- Deben existir componentes canónicos para acciones, campos normales/secretos, navegación, cards,
  banners, loading, vacío, offline, draft, conflicto, éxito, error retryable/terminal y diálogos.

La arquitectura concreta se define en
[`ADR-0004-DESIGN-SYSTEM-APPEARANCE`](../../architecture/adr/ADR-0004-DESIGN-SYSTEM-APPEARANCE.md),
creado por SCDK-M136. Solo será implementable tras quedar `ACCEPTED`.

## Feedback, errores y navegación visual

- La UI debe distinguir disponibilidad local de estado remoto. Un error de sync no sustituirá
  contenido local cuando el contrato vigente permita conservarlo.
- Retry solo estará disponible para resultados funcionalmente retryable. Una operación en progreso
  deshabilitará acciones duplicadas sin retirar contexto necesario.
- Errores y avisos serán sanitizados, accionables y no dependerán exclusivamente de color, Toast o
  duración temporal. Los errores terminales no ofrecerán una acción que repita una operación no
  autorizada.
- Draft y conflicto usarán texto, icono y `stateDescription`; no se inferirán solo por color.
- Bóveda y Ajustes conservarán sus destinos, selección, back stack y políticas de acceso. No se
  reintroducirán Folders, Profile ni rutas placeholder.

## Motion y layouts adaptativos

- Motion no será decorativo ni bloqueará input. Navegación, cambio de estado y feedback podrán
  animarse cuando la transición explique continuidad o resultado.
- Escala de animación `0` eliminará la espera visual; no cambiará destino, foco, acción disponible
  ni limpieza de datos sensibles. Los tests no dependerán de sleeps o duración de animaciones.
- El layout responderá al espacio disponible en ventanas compactas y medias. Scroll, reflow y
  agrupación preservarán input, contexto y acceso a acciones en portrait, landscape y resize.
- Font scale 200 % no podrá provocar solapamiento crítico, clipping de advertencias o acciones
  inaccesibles. El texto no se reducirá para evitar adaptación.

## Accesibilidad: gates y mejoras posteriores

### Gates bloqueantes de Fase 8

- Contraste AA aplicable para texto, iconos funcionales y estados interactivos.
- Target táctil mínimo de 48dp o área interactiva equivalente sin solapamientos.
- Roles, labels localizados, headings, state descriptions, relación error-campo y orden de foco.
- Feedback relevante anunciado de forma accesible y no exclusivamente temporal.
- Login, Unlock, creación de un item, retry manual de sync y Settings completables con TalkBack.
- Font scale hasta 200 %, ventanas compactas/medias, portrait/landscape y motion scale `0`.
- Campos secretos enmascarados por defecto; reveal con label y estado; contenido oculto ausente de
  semántica y evidencia visual.

### Mejoras recomendadas no bloqueantes

- Auditoría o certificación completa WCAG 2.2 AA y aspiración AAA.
- RTL, nuevos idiomas, switch access, control por voz y alto contraste adicional.
- Experiencia dedicada para tablets, foldables expandidos y desktop.
- Estudios formales de usabilidad y haptics como lenguaje transversal.

SCDK-M146 registrará cada gap avanzado con evidencia y follow-up; no podrá clasificar como mejora
una violación de los gates bloqueantes.

## Localización

- Todo texto visible y toda descripción accesible vivirán en recursos con paridad EN/ES.
- Se revisarán plurales, interpolaciones, textos largos, elipsis y orden gramatical; no se validará
  paridad únicamente comparando nombres de recursos.
- `Password` de cuenta, passphrase del vault, recovery key y quick unlock usarán términos distintos
  y consistentes. Las traducciones mantendrán consecuencias y severidad.
- Fechas o metadata se formatearán mediante APIs locales sin incorporar datos sensibles nuevos.

## Privacidad visual y datos de prueba

- Continúa vigente `FLAG_SECURE` y la política de saved state, semántica, clipboard y lifecycle de
  ADR-0003. La Fase 8 no relaja estas garantías para facilitar previews o tests.
- Los fixtures usarán dominios reservados, nombres ficticios y strings sintéticos claramente no
  reutilizables. No se copiarán datos de cuentas o vaults reales.
- Recovery keys, passphrases y passwords de fixtures no tendrán formato de credencial operativa y
  nunca se adjuntarán a reports, screenshots o artefactos CI.
- Un golden que revele contenido que debería estar oculto será un fallo de seguridad, no una nueva
  referencia aceptable.

## Matriz normativa de verificación

`G` = golden, `S` = test semántico Compose, `I` = prueba instrumentada, `M` = evidencia manual. Las
cards indicadas son dueñas de crear la evidencia; SCDK-M147 la agrega y ejecuta.

| Superficie/contrato              | Combinaciones mínimas                                                                  | Evidencia  | Artefacto esperado                                        | Datos permitidos                           | Owner               |
|----------------------------------|----------------------------------------------------------------------------------------|------------|-----------------------------------------------------------|--------------------------------------------|---------------------|
| Identidad y activos              | Marca, paletas clara/oscura, launcher, splash                                          | G, M       | Guía versionada, exports y contraste documentado          | Marca y colores; sin cuentas               | SCDK-M135           |
| Tema y tokens                    | Light/Dark/System; Material You on/off y API 30 disabled                               | G, S, I    | Previews/goldens y tests de persistencia/primer frame     | Fixtures de tokens                         | SCDK-M138           |
| Componentes                      | Default, loading, disabled, focus, error, offline, draft, conflict, success            | G, S       | Catálogo, referencias y assertions semánticas             | Texto sintético localizado                 | SCDK-M139           |
| Splash/shell/navigation          | Splash, Bóveda/Ajustes selected, back y motion `0`                                     | G, S, I    | Goldens y tests de navegación/reduced motion              | Destinos sin contenido secreto             | SCDK-M140           |
| Welcome/Login/Signup             | Welcome; Login idle/loading/retryable/terminal; Signup validation                      | G, S, I, M | Goldens, tests de foco/IME y recorrido TalkBack Login     | `person@example.com`; password enmascarada | SCDK-M141/SCDK-M146 |
| Bootstrap/Create/Recovery/Unlock | Gate loading; create; recovery warning/confirm; locked; quick-unlock fallback/error    | G, S, I, M | Goldens, semántica, lifecycle y recorrido TalkBack Unlock | Claves y passphrases ficticias ocultas     | SCDK-M142/SCDK-M146 |
| Vault Home                       | initial, empty, content, syncing, offline/retryable, terminal, draft, conflict         | G, S, I, M | Goldens, semántica y recorrido TalkBack retry             | Títulos y UUID sintéticos                  | SCDK-M143/SCDK-M146 |
| Password/Note create/edit        | editable, dirty, saving, retryable/terminal, draft/conflict, locked                    | G, S, I, M | Goldens, semántica y recorrido TalkBack de creación       | Contenido ficticio; password oculta        | SCDK-M144/SCDK-M146 |
| Settings/Change Passphrase       | secciones, apariencia, quick unlock, auto-lock, mutating, uncertain, success, diálogos | G, S, I, M | Goldens, persistencia, seguridad y TalkBack Settings      | Preferencias y mensajes sintéticos         | SCDK-M145/SCDK-M146 |
| Adaptación transversal           | EN/ES, compact/medium, portrait/landscape, font 100/200 %, resize                      | G, S, I, M | Matriz de auditoría con findings reproducibles            | Fixtures sintéticos comunes                | SCDK-M146           |
| Cierre de Fase 8                 | Requisitos completos, claro/oscuro, motion `0`, no secretos                            | G, S, I, M | `phase-8-ui-matrix.md`, gates y reports                   | Solo fixtures aprobados                    | SCDK-M147           |

Compose Preview Screenshot Testing será la primera opción para `G`, con versión compatible fijada
por SCDK-M137 y tareas separadas de update/verify. Si existe un bloqueo reproducible, se requerirá
una decisión explícita antes de sustituir la herramienta. Los goldens no reemplazan tests
funcionales.

## Estrategia de verificación por requisito

| Requisito    | Estrategia                                                                         | Evidencia esperada                                    | Owners                          |
|--------------|------------------------------------------------------------------------------------|-------------------------------------------------------|---------------------------------|
| `FR-UI-001`  | Auditoría de rutas, superficies y matriz; ausencia de celdas sin clasificación     | Esta spec y matriz final SCDK-M147                    | SCDK-M134, SCDK-M147            |
| `FR-UI-002`  | Revisión humana, contraste, goldens de tema y tests de preferencias                | Guía SCDK-M135, ADR SCDK-M136, assets/tests SCDK-M138 | SCDK-M135, SCDK-M136, SCDK-M138 |
| `FR-UI-003`  | Catálogo, goldens, tests semánticos y consumo por features                         | Componentes SCDK-M139 y rediseños SCDK-M141–M145      | SCDK-M139–M145                  |
| `FR-UI-004`  | Tests de navegación, back, gates, selección y lock                                 | Tests existentes de Fase 7 más SCDK-M140/SCDK-M147    | SCDK-M140, SCDK-M147            |
| `NFR-UI-001` | Tests con motion scale `0` y recorrido con escala aumentada                        | Tests SCDK-M140 y auditoría SCDK-M146                 | SCDK-M140, SCDK-M146            |
| `NFR-UI-002` | Previews/tests compact/medium, portrait/landscape, font 200 % y resize             | Auditoría SCDK-M146 y matriz SCDK-M147                | SCDK-M146, SCDK-M147            |
| `NFR-UI-003` | Tests semánticos, scanner/inspección y cinco recorridos TalkBack                   | Evidencia SCDK-M139–M146 agregada por SCDK-M147       | SCDK-M139–M147                  |
| `NFR-UI-004` | Paridad de recursos y revisión EN/ES con textos largos                             | Auditoría lingüística SCDK-M146                       | SCDK-M141–M146                  |
| `NFR-UI-005` | Gate screenshot read-only, semántica, instrumentación y matriz manual              | Infraestructura SCDK-M137 y cierre SCDK-M147          | SCDK-M137, SCDK-M147            |
| `SEC-UI-001` | Revisión de fixtures/artefactos, tests de contenido oculto y no regresión ADR-0003 | Tests de seguridad y auditoría de artefactos          | SCDK-M137–M147                  |

## Compatibilidad y rollout

- Fase 8 soporta min/target definidos por el proyecto, actualmente API 30–36.
- SCDK-M135–M147 no pueden comenzar a modificar UI de producción hasta que esta spec esté
  `APPROVED`.
- SCDK-M136 debe quedar `ACCEPTED` antes de implementar ownership o persistencia de apariencia.
- Los rediseños se integrarán por flujo conservando tests funcionales de Fase 7 como gates de no
  regresión. No habrá migraciones de storage, crypto, red o sync.
- SCDK-M147 promoverá esta spec a `VERIFIED` solo con todos los requisitos evidenciados y sin gaps
  bloqueantes. Los gaps avanzados quedarán como follow-ups explícitos.

## Acceptance Criteria

- [x] `AC-UI-001`: existe `SPEC-UI-V1` con objetivos, no objetivos y requisitos identificables.
- [x] `AC-UI-002`: todas las rutas públicas v1 y superficies auxiliares aparecen en el inventario y
  en la matriz de estados.
- [x] `AC-UI-003`: cada requisito tiene estrategia de verificación, evidencia esperada y owner.
- [x] `AC-UI-004`: gates bloqueantes y mejoras recomendadas están diferenciados.
- [x] `AC-UI-005`: la spec preserva por referencia los estados y contratos verificados en Fase 7.
- [x] `AC-UI-006`: no quedan decisiones críticas ni estados sin ownership.
- [x] `AC-UI-007`: el owner humano ha promovido esta spec a `APPROVED` el `2026-10-10`.
- [x] `AC-UI-008`: registry, trazabilidad y agent report están actualizados para estado `APPROVED`.

## Trazabilidad

- Código de referencia, no modificado por SCDK-M134: `Routes.kt`, state holders de auth, vault home,
  editores, passphrase, Settings y quick unlock.
- Código/runtime de SCDK-M134: `N/A`; es una tarea normativa.
- Tests de SCDK-M134: validación estática de inventario, matrices, IDs, enlaces, referencias
  obligatorias y ausencia de decisiones abiertas.
- ADRs: `ADR-0001`, `ADR-0002`, `ADR-0003` y
  [`ADR-0004-DESIGN-SYSTEM-APPEARANCE`](../../architecture/adr/ADR-0004-DESIGN-SYSTEM-APPEARANCE.md)
  (`ACCEPTED`, SCDK-M136).
- Tasks: `SCDK-M134`–`SCDK-M147`.
- Evidencia de SCDK-M134: [agent report](../../sdd/agent-reports/SCDK-M134.md).
- Evidencia final de fase: `docs/testing/phase-8-ui-matrix.md`, propiedad de `SCDK-M147`.

Esta spec no puede pasar a `APPROVED` sin revisión explícita del owner. No puede pasar a
`VERIFIED` mientras exista un requisito sin evidencia o un gate bloqueante incumplido.
