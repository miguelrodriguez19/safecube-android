# SafeCube — Guía de marca v1

## Metadata

| Campo | Valor |
|---|---|
| ID | `BRAND-GUIDE-SAFECUBE-V1` |
| Task | `SCDK-M135` |
| Spec normativa | [`SPEC-UI-V1`](../specs/features/ui-brand-motion-v1.md) |
| Roadmap | [`Fase 8`](../roadmap/roadmap--fase-8.md) |
| Estado | `APPROVED` |
| Owner de aprobación | `product-owner` |
| Revisión | `v1.0` |
| Fecha de revisión | `2026-10-11` |
| Alcance | Identidad, activos fuente, paletas, tipografía, lenguaje visual y mockups piloto |

Esta guía es el contrato visual de entrada para `SCDK-M138`. Define decisiones reproducibles, pero
no modifica recursos Android ni implementa el design system. Las rutas de assets que aparecen en
este documento son las rutas canónicas esperadas en el repositorio; un export no sustituye a su
fuente.

## 1. Propósito y límites

SafeCube debe resultar reconocible, confiable y tranquilo en superficies públicas y protegidas,
sin que la apariencia reinterprete rutas, estados o garantías de seguridad. La identidad combina
azul petróleo con acentos cálidos y una composición sobria, cercana y moderna.

Esta guía cubre:

- el logo fuente y sus variantes técnicas y cromáticas;
- paletas clara y oscura con roles Material y contrastes calculados;
- tipografía redistribuible, pesos y fallback;
- lenguaje de formas, elevación, densidad, ilustración e iconografía;
- conceptos de launcher, splash y uso in-app;
- ocho mockups piloto y su checklist de aprobación;
- reglas de reproducibilidad, licencias, privacidad y handoff a M138.

Queda fuera de esta guía alterar la geometría del logo, implementar assets en `res/`, cambiar rutas
o estados funcionales, diseñar todas las pantallas, crear una familia completa de iconos o producir
materiales de marketing. Las preferencias `System`, `Light`, `Dark`, el color dinámico y la
persistencia de apariencia siguen el contrato de [`SPEC-UI-V1`](../specs/features/ui-brand-motion-v1.md)
y su arquitectura se resolverá en M136.

## 2. Personalidad, tono y voz

### Personalidad

| Rasgo | Manifestación visual y verbal |
|---|---|
| Cálida | Superficies respirables, acentos cálidos contenidos y lenguaje humano. |
| Cercana | Explica qué ocurre y qué puede hacer la persona sin sonar infantil ni informal. |
| Confiable | Jerarquía clara, estados explícitos, consistencia y ausencia de adornos que distraigan. |
| Sobria | Color y motion medidos; el contenido y las acciones mandan. |
| Moderna | Material 3 coherente, adaptación al espacio y componentes reutilizables. |

### Voz

- Directa: una instrucción o consecuencia por frase cuando sea posible.
- Tranquila: describe el problema y la recuperación sin alarmismo.
- Transparente: distingue contenido local, sincronización, bloqueo y estado remoto.
- Respetuosa: usa segunda persona clara en inglés y español, sin culpar a la persona.
- Precisa en seguridad: no suaviza advertencias sobre passphrase, recovery key, lock o pérdida de
  disponibilidad.

La interfaz evita jerga de implementación, promesas absolutas y mensajes vagos como “algo salió
mal”. Los errores incluyen contexto y la acción funcional disponible. La severidad nunca se expresa
solo mediante color, icono o un Toast temporal.

Ejemplos de estilo:

- Correcto: “No pudimos sincronizar ahora. Tus cambios locales siguen disponibles. Reintentar”.
- Correcto: “La bóveda está bloqueada. Desbloquéala para continuar”.
- Evitar: “¡Ups! Parece que algo no funcionó”.
- Evitar: “Error 401” como único texto visible.

Todos los textos visibles y las descripciones accesibles se localizan en inglés y español según
[`SPEC-UI-V1`](../specs/features/ui-brand-motion-v1.md). Los mockups de esta guía usan texto
sintético y no fijan una traducción de producción.

## 3. Logo y activos fuente

### 3.1 Fuente maestra

La fuente maestra canónica debe vivir en:

- [`docs/design/assets/logo/master.svg`](assets/logo/master.svg)

El SVG recibido del owner fue inspeccionado como entrada de M135:

| Propiedad | Valor observado |
|---|---|
| Dimensiones declaradas | `1024.000000pt × 1024.000000pt` |
| Elementos geométricos | `37` elementos `<path>` |
| Fill de entrada | `#000000` |
| SHA-256 de la entrada | `79729c8e195797f6eac3708d6a1a65d953077252d3b13494fdaecacc3760c72a` |

El hash identifica la entrada entregada por el owner; el hash del archivo canónico versionado debe
registrarse en el inventario de assets cuando se incorpore. El master conserva el número, orden y
datos `d` de los 37 paths. Se permiten únicamente cambios documentados de fill/stroke, canvas,
viewBox o transformación de colocación para producir una variante técnica. No se permite editar,
simplificar, redibujar, combinar, deformar ni recolorear la geometría dentro del path.

### 3.2 Variantes canónicas

| Variante | Ruta esperada | Uso |
|---|---|---|
| Master | [`assets/logo/master.svg`](assets/logo/master.svg) | Fuente inmutable y regeneración de todas las variantes. |
| Principal | [`assets/logo/primary.svg`](assets/logo/primary.svg) | Superficies claras y uso in-app principal. |
| Invertida | [`assets/logo/inverted.svg`](assets/logo/inverted.svg) | Superficies oscuras o petróleo con contraste suficiente. |
| Monocroma | [`assets/logo/monochrome.svg`](assets/logo/monochrome.svg) | Grabado, impresión, restricciones de un solo color y fallback. |
| Adaptive foreground | [`assets/logo/adaptive-foreground.svg`](assets/logo/adaptive-foreground.svg) | Capa frontal transparente para launcher adaptativo. |
| Adaptive background | [`assets/logo/adaptive-background.svg`](assets/logo/adaptive-background.svg) | Fondo petróleo o superficie aprobada para launcher. |
| Round | [`assets/logo/round-preview.svg`](assets/logo/round-preview.svg) | Icono redondo de fallback; no añade un contorno ajeno al sistema. |
| Themed monochrome | [`assets/logo/monochrome-themed.svg`](assets/logo/monochrome-themed.svg) | Máscara monocroma tintable por el sistema. |
| Splash | [`assets/logo/splash-light.svg`](assets/logo/splash-light.svg) y [`splash-dark.svg`](assets/logo/splash-dark.svg) | Marca centrada en la transición de sistema. |
| In-app transparent | [`assets/logo/in-app-mark.svg`](assets/logo/in-app-mark.svg) | Welcome, Login, Unlock y superficies de marca dentro de la app. |

Las variantes cromáticas toman sus colores de las paletas de esta guía. La variante monocroma
acepta un único color de foreground y un fondo de contraste; no usa degradados ni sombras
integradas. El themed icon expone solo la máscara necesaria y deja el tintado al sistema.

### 3.3 Área de seguridad y tamaños mínimos

El área de seguridad es `1/8` del ancho total de la marca colocada. Si el ancho visible es `W`, no
se coloca texto, iconografía, borde, fotografía, recorte, componente ni otro logo a menos de `W/8`
de cada lado de la caja de colocación. El cálculo se aplica al canvas de la variante, no al bounding
box accidental de sus paths.

Reglas:

- El logo nunca se recorta ni se estira; se escala proporcionalmente.
- En launcher y splash se reserva también el safe zone del sistema; los exports adaptativos usan una
  región documentada de `66%` de diámetro centrada. El área de seguridad de marca no se usa para
  aumentar artificialmente el mark.
- Tamaño mínimo del mark en uso UI: `24dp` de ancho visible.
- Tamaño mínimo en uso independiente, splash o material de referencia: `32dp` de ancho visible.
- Por debajo de `24dp`, se usa el icono de producto definido por M138, no una versión ilegible del
  logo.
- El tamaño se valida en el tamaño físico final y con font scale/layout del dispositivo; no se
  reduce para compensar un layout incorrecto.

## 4. Usos correctos e incorrectos

### Correctos

- Usar la variante principal sobre `background`, `surface` o fondos que conserven el contraste
  aprobado.
- Usar la variante invertida sobre `primary`, `primaryContainer` oscuro o superficies oscuras.
- Usar la monocroma solo cuando el contexto no admita la pareja principal/invertida.
- Mantener proporción, orientación, paths y área de seguridad.
- Alinear el mark a la retícula del contenedor y proporcionar nombre accesible cuando sea accionable
  o informativo.
- Usar una única presencia de marca dominante por superficie; no repetir el logo como decoración.

### Incorrectos

- Cambiar la geometría, redibujar, rotar, inclinar, reflejar, comprimir o aplicar perspectiva.
- Añadir contornos, bevel, glow, sombras internas, degradados o filtros al mark.
- Aplicar colores fuera de las paletas aprobadas o usar el logo sobre un fondo que reduzca el
  contraste.
- Encerrar el logo en una pastilla, escudo o círculo no definido por la variante correspondiente.
- Invadir el área `W/8`, recortar el mark o colocarlo junto a otro logo sin separación.
- Usar el logo para comunicar por sí solo loading, error, offline, draft, conflicto o éxito.
- Usar un mockup o screenshot de producción como fuente maestra de un asset.

### Matriz de uso por elemento

| Elemento | Uso correcto | Uso incorrecto |
|---|---|---|
| Logo | Variante aprobada, proporción intacta, clearspace `W/8` y fondo de contraste conocido. | Redibujar, deformar, rotar, recortar, añadir efectos o usarlo como indicador de estado. |
| Color | Roles Material exactos, tema correspondiente y contraste de la sección 5; el estado también tiene texto/icono. | Hex ad hoc, color dinámico por defecto, degradado de marca, contraste no verificado o estado indicado solo con color. |
| Tipografía | Manrope 400/500/600/700, fallback `sans-serif`, jerarquía semántica y escalado hasta 200 %. | Peso simulado sin necesidad, mayúsculas sostenidas en párrafos, tracking que reduzca legibilidad, texto rasterizado o reducción de tamaño para evitar wrapping. |
| Iconografía | Familia Material coherente, tamaño/peso alineados y label o `stateDescription` cuando comunica acción/estado. | Mezclar estilos, usar el logo como icono de acción, icono sin nombre accesible o depender solo de forma/color. |

## 5. Paletas y contraste

Los siguientes valores son exactos y son la entrada normativa para los tokens de M138. `on*` se
usa únicamente sobre su rol asociado o sobre una combinación expresamente listada. `outline` es un
rol de borde/indicador gráfico: no se usa como color de texto de cuerpo. El color dinámico del
sistema no sustituye estos valores por defecto.

### 5.1 Tema claro

| Rol Material | Hex | Uso |
|---|---|---|
| `primary` | `#006A6A` | Acción y énfasis principal. |
| `onPrimary` | `#FFFFFF` | Contenido sobre `primary`. |
| `primaryContainer` | `#9CF1EF` | Contenedor de énfasis principal. |
| `onPrimaryContainer` | `#002020` | Contenido sobre `primaryContainer`. |
| `secondary` | `#4A6363` | Acción o énfasis secundario. |
| `onSecondary` | `#FFFFFF` | Contenido sobre `secondary`. |
| `secondaryContainer` | `#CCE8E7` | Contenedor secundario. |
| `onSecondaryContainer` | `#051F1F` | Contenido sobre `secondaryContainer`. |
| `tertiary` | `#8B4A20` | Acento cálido y comunicación auxiliar. |
| `onTertiary` | `#FFFFFF` | Contenido sobre `tertiary`. |
| `tertiaryContainer` | `#FFDCC7` | Contenedor del acento cálido. |
| `onTertiaryContainer` | `#321200` | Contenido sobre `tertiaryContainer`. |
| `error` | `#BA1A1A` | Error y acción correctiva. |
| `onError` | `#FFFFFF` | Contenido sobre `error`. |
| `background` | `#F5FAF9` | Fondo raíz. |
| `onBackground` | `#171D1D` | Contenido sobre `background`. |
| `surface` | `#F5FAF9` | Superficie base; igual a `background` en v1. |
| `onSurface` | `#171D1D` | Contenido sobre `surface`. |
| `surfaceVariant` | `#DAE5E4` | Superficie alternativa y separación visual. |
| `onSurfaceVariant` | `#3F4948` | Contenido sobre `surfaceVariant` y metadata. |
| `outline` | `#6F7978` | Borde y affordance gráfico. |

### 5.2 Tema oscuro

| Rol Material | Hex | Uso |
|---|---|---|
| `primary` | `#80D5D3` | Acción y énfasis principal. |
| `onPrimary` | `#003737` | Contenido sobre `primary`. |
| `primaryContainer` | `#00504F` | Contenedor de énfasis principal. |
| `onPrimaryContainer` | `#9CF1EF` | Contenido sobre `primaryContainer`. |
| `secondary` | `#B1CCCC` | Acción o énfasis secundario. |
| `onSecondary` | `#1C3535` | Contenido sobre `secondary`. |
| `secondaryContainer` | `#334B4B` | Contenedor secundario. |
| `onSecondaryContainer` | `#CCE8E7` | Contenido sobre `secondaryContainer`. |
| `tertiary` | `#FFB68C` | Acento cálido y comunicación auxiliar. |
| `onTertiary` | `#512400` | Contenido sobre `tertiary`. |
| `tertiaryContainer` | `#6D330A` | Contenedor del acento cálido. |
| `onTertiaryContainer` | `#FFDCC7` | Contenido sobre `tertiaryContainer`. |
| `error` | `#FFB4AB` | Error y acción correctiva. |
| `onError` | `#690005` | Contenido sobre `error`. |
| `background` | `#0E1515` | Fondo raíz. |
| `onBackground` | `#DEE4E3` | Contenido sobre `background`. |
| `surface` | `#0E1515` | Superficie base; igual a `background` en v1. |
| `onSurface` | `#DEE4E3` | Contenido sobre `surface`. |
| `surfaceVariant` | `#3F4948` | Superficie alternativa y separación visual. |
| `onSurfaceVariant` | `#BEC9C8` | Contenido sobre `surfaceVariant` y metadata. |
| `outline` | `#899392` | Borde y affordance gráfico. |

### 5.3 Tabla WCAG 2.1 de pares aprobados

Los valores se calculan con luminancia relativa sRGB y se redondean a dos decimales. `AA texto`
significa al menos `4.50:1` para texto normal y `3.00:1` para texto grande. `AA gráfico` significa
al menos `3.00:1` para un borde, icono o indicador funcional contra su superficie. Una combinación
no listada requiere una nueva verificación antes de convertirse en token.

| Tema | Foreground | Background | Contraste | Gate |
|---|---|---|---:|---|
| Light | `onPrimary #FFFFFF` | `primary #006A6A` | `6.42:1` | AA texto |
| Light | `onPrimaryContainer #002020` | `primaryContainer #9CF1EF` | `13.23:1` | AA texto |
| Light | `onSecondary #FFFFFF` | `secondary #4A6363` | `6.45:1` | AA texto |
| Light | `onSecondaryContainer #051F1F` | `secondaryContainer #CCE8E7` | `13.31:1` | AA texto |
| Light | `onTertiary #FFFFFF` | `tertiary #8B4A20` | `6.78:1` | AA texto |
| Light | `onTertiaryContainer #321200` | `tertiaryContainer #FFDCC7` | `13.35:1` | AA texto |
| Light | `onError #FFFFFF` | `error #BA1A1A` | `6.46:1` | AA texto |
| Light | `onBackground #171D1D` | `background #F5FAF9` | `16.20:1` | AA texto |
| Light | `onSurface #171D1D` | `surface #F5FAF9` | `16.20:1` | AA texto |
| Light | `onSurfaceVariant #3F4948` | `surfaceVariant #DAE5E4` | `7.22:1` | AA texto |
| Light | `primary #006A6A` | `background/surface #F5FAF9` | `6.09:1` | AA texto |
| Light | `secondary #4A6363` | `background/surface #F5FAF9` | `6.12:1` | AA texto |
| Light | `tertiary #8B4A20` | `background/surface #F5FAF9` | `6.43:1` | AA texto |
| Light | `error #BA1A1A` | `background/surface #F5FAF9` | `6.13:1` | AA texto |
| Light | `onSurfaceVariant #3F4948` | `background/surface #F5FAF9` | `8.82:1` | AA texto |
| Light | `outline #6F7978` | `background/surface #F5FAF9` | `4.26:1` | AA gráfico; no body text |
| Light | `primary #006A6A` | `primaryContainer #9CF1EF` | `4.97:1` | AA texto |
| Light | `secondary #4A6363` | `secondaryContainer #CCE8E7` | `4.99:1` | AA texto |
| Light | `tertiary #8B4A20` | `tertiaryContainer #FFDCC7` | `5.27:1` | AA texto |
| Dark | `onPrimary #003737` | `primary #80D5D3` | `7.73:1` | AA texto |
| Dark | `onPrimaryContainer #9CF1EF` | `primaryContainer #00504F` | `7.19:1` | AA texto |
| Dark | `onSecondary #1C3535` | `secondary #B1CCCC` | `7.69:1` | AA texto |
| Dark | `onSecondaryContainer #CCE8E7` | `secondaryContainer #334B4B` | `7.23:1` | AA texto |
| Dark | `onTertiary #512400` | `tertiary #FFB68C` | `7.69:1` | AA texto |
| Dark | `onTertiaryContainer #FFDCC7` | `tertiaryContainer #6D330A` | `7.67:1` | AA texto |
| Dark | `onError #690005` | `error #FFB4AB` | `7.72:1` | AA texto |
| Dark | `onBackground #DEE4E3` | `background #0E1515` | `14.35:1` | AA texto |
| Dark | `onSurface #DEE4E3` | `surface #0E1515` | `14.35:1` | AA texto |
| Dark | `onSurfaceVariant #BEC9C8` | `surfaceVariant #3F4948` | `5.48:1` | AA texto |
| Dark | `primary #80D5D3` | `background/surface #0E1515` | `10.89:1` | AA texto |
| Dark | `secondary #B1CCCC` | `background/surface #0E1515` | `10.89:1` | AA texto |
| Dark | `tertiary #FFB68C` | `background/surface #0E1515` | `10.84:1` | AA texto |
| Dark | `error #FFB4AB` | `background/surface #0E1515` | `10.88:1` | AA texto |
| Dark | `onSurfaceVariant #BEC9C8` | `background/surface #0E1515` | `10.89:1` | AA texto |
| Dark | `outline #899392` | `background/surface #0E1515` | `5.85:1` | AA gráfico; no body text |
| Dark | `primary #80D5D3` | `primaryContainer #00504F` | `5.48:1` | AA texto |
| Dark | `secondary #B1CCCC` | `secondaryContainer #334B4B` | `5.50:1` | AA texto |
| Dark | `tertiary #FFB68C` | `tertiaryContainer #6D330A` | `5.79:1` | AA texto |

No se usa `error` como fondo de texto para `error` (`1.00:1`); el par aprobado es siempre
`onError` sobre `error`. Los estados success, offline, draft y conflict deben combinar texto/icono
con un rol aprobado y nunca introducir un verde, gris o ámbar ad hoc sin verificación.

## 6. Tipografía

### 6.1 Familia y licencia

La familia aprobada es **Manrope**, distribuida bajo SIL Open Font License 1.1 por The Manrope
Project Authors. La fuente y su licencia deben estar disponibles localmente en:

- `docs/design/assets/type/manrope/`
- `docs/design/assets/type/manrope/OFL.txt`
- `docs/design/assets/type/manrope/README.md`
- `docs/design/assets/type/manrope/README.upstream.txt`

El archivo de procedencia recibido por M135 contenía `OFL.txt`, documentación upstream, una fuente
variable y fuentes estáticas. El paquete canónico versionado conserva `OFL.txt`,
`README.upstream.txt` y solo los cuatro pesos estáticos seleccionados. El paquete de runtime debe
conservar el aviso de copyright y la licencia OFL junto a la fuente. La fuente no se vende por
separado ni se descarga desde una cuenta o URL en build, test o runtime.

### 6.2 Pesos y fallback

Los pesos estáticos canónicos son:

| Peso | Archivo esperado | Uso principal |
|---:|---|---|
| 400 | `assets/type/manrope/Manrope-Regular.ttf` | Body, labels, ayuda y contenido. |
| 500 | `assets/type/manrope/Manrope-Medium.ttf` | Labels con énfasis y controles. |
| 600 | `assets/type/manrope/Manrope-SemiBold.ttf` | Headings secundarios, botones y navegación. |
| 700 | `assets/type/manrope/Manrope-Bold.ttf` | Títulos y énfasis de alta jerarquía. |

Fallback normativo: `sans-serif` del sistema. Si una plataforma no carga Manrope, conserva el peso
semántico, el tamaño, el line-height y el escalado del sistema; no comprime texto ni sustituye una
acción por un icono para evitar wrapping. M138 definirá las asignaciones exactas de `Typography` y
debe preservar font scale hasta 200 %.

La jerarquía tipográfica es contrastada, no ornamental: un título introduce la tarea, el body
explica la consecuencia y el label nombra la acción. No se usan mayúsculas sostenidas para párrafos
ni tracking negativo que degrade legibilidad.

## 7. Lenguaje visual

### 7.1 Formas

Las formas son suaves y contenidas, no infantiles. La escala de referencia para M138 es `4dp`,
`8dp`, `12dp` y `16dp`; el radio completo se reserva a chips, toggles y controles explícitamente
redondos. Cards y campos no mezclan radios arbitrarios dentro de una misma jerarquía. Los radios no
se usan para ocultar una estructura confusa ni para convertir todos los componentes en pastillas.

### 7.2 Elevación

La elevación comunica relación y prioridad, no decoración:

- Nivel 0: background y superficies integradas.
- Nivel 1: card o sección que separa contenido relacionado.
- Nivel 2: menú, bottom sheet o superficie temporal que requiere prioridad.
- Nivel 3: diálogo o superficie modal que bloquea el contexto.

En dark, se prioriza separación tonal de las superficies y un shadow contenido. No se usan sombras
negras duras, glow del logo ni elevación para representar éxito, error, lock o conflicto.

### 7.3 Densidad y espaciado

La densidad es equilibrada: cada agrupación debe tener una relación visible entre label, contenido,
ayuda y acción. La retícula de referencia usa múltiplos de `4dp`, con pasos frecuentes de `8dp`,
`12dp`, `16dp`, `24dp` y `32dp`. Cada control interactivo mantiene al menos `48dp` de área táctil,
incluidos icon buttons. En ventanas compactas se reduce decoración antes que contexto o acciones;
en ventanas medias se amplía la respiración sin crear columnas vacías.

### 7.4 Ilustración

La ilustración es funcional y geométrica: puede evocar cubos, capas protegidas, continuidad o
recuperación con volúmenes simples inspirados en el mark. No compite con formularios ni se usa para
representar secretos. No se introducen personajes, escenas de marketing, gradientes complejos ni
texturas en la v1. Una ilustración no reemplaza un heading, un estado textual o una acción accesible.

### 7.5 Iconografía

La iconografía funcional usa una familia Material coherente, preferentemente Material Symbols Rounded
cuando exista un símbolo adecuado. El peso visual y el tamaño deben seguir la jerarquía del texto;
la marca no se convierte en un icono de acción. Los iconos de estado se acompañan de label, texto o
`stateDescription`; un color o una forma aislada nunca es la única señal. No se crea una familia
completa propia en M135: símbolos exclusivos de SafeCube deben documentarse y derivarse de la
geometría de marca sin parecer controles estándar.

Los mockups usan primitivas SVG inline deterministas como representación esquemática. No fijan los
paths de iconos de producción; M138 debe mapear cada función a Material Symbols Rounded o a un asset
versionado con licencia y semántica equivalentes.

## 8. Conceptos de aplicación de marca

Estos conceptos son entradas para implementación posterior; no son todavía recursos Android.

| Superficie | Concepto normativo |
|---|---|
| Launcher adaptativo | Foreground transparente con el mark centrado dentro de la safe zone del sistema y background `primary` o petróleo oscuro. No se coloca texto en la capa frontal. |
| Round icon | Composición autocontenida de alto contraste, sin doble círculo decorativo y con margen suficiente para máscaras de fabricantes. |
| Themed monochrome | Silueta monocroma tintable, sin color interno ni sombras; el sistema aporta el tintado. |
| Splash | Mark centrado, estático y sobrio sobre la superficie del tema; respeta la transición de sistema y no muestra contenido protegido ni una ruta de producto. |
| Uso in-app | Principal sobre claro, invertida sobre dark/petróleo, con `W/8` de clearspace y nombre accesible cuando corresponda. El logo no comunica loading, error, sync o lock por sí solo. |

Todas las variantes se regeneran desde el master y conservan la geometría. El sistema puede aplicar
la máscara de launcher o el tintado themed; el asset fuente no debe incorporar esas decisiones.

## 9. Mockups piloto

Los ocho mockups de referencia son el mínimo de aprobación de M135. Se guardan bajo
`docs/design/mockups/` usando exclusivamente fixtures sintéticos. Cada nombre de archivo
es una ruta canónica esperada para el export aprobado.

| ID | Mockup | Ruta esperada | Tema | Estado representativo | Evidencia |
|---|---|---|---|---|---|
| `MOCK-WELCOME-LIGHT` | Welcome — claro | [`mockups/welcome-light.svg`](mockups/welcome-light.svg) | Light | Content; entrada a Login y Signup. | Golden + revisión manual |
| `MOCK-WELCOME-DARK` | Welcome — oscuro | [`mockups/welcome-dark.svg`](mockups/welcome-dark.svg) | Dark | Content; contraste e identidad invertida. | Golden + revisión manual |
| `MOCK-LOGIN-LIGHT` | Login — claro | [`mockups/login-light.svg`](mockups/login-light.svg) | Light | Idle/validación con `person@example.com` y password enmascarada. | Golden + semántica |
| `MOCK-LOGIN-DARK` | Login — oscuro | [`mockups/login-dark.svg`](mockups/login-dark.svg) | Dark | Retryable/offline con retry contextual y datos sintéticos. | Golden + semántica |
| `MOCK-UNLOCK-LIGHT` | Unlock — claro | [`mockups/unlock-light.svg`](mockups/unlock-light.svg) | Light | Locked con passphrase enmascarada y acción principal. | Golden + semántica |
| `MOCK-UNLOCK-DARK` | Unlock — oscuro | [`mockups/unlock-dark.svg`](mockups/unlock-dark.svg) | Dark | Quick unlock fallback/error y retorno claro a passphrase. | Golden + semántica |
| `MOCK-VAULT-LIGHT` | Vault — claro | [`mockups/vault-light.svg`](mockups/vault-light.svg) | Light | Content, sync manual, feedback transitorio y acción inferior de crear item. | Golden + instrumentada |
| `MOCK-VAULT-DARK` | Vault — oscuro | [`mockups/vault-dark.svg`](mockups/vault-dark.svg) | Dark | Offline/draft/conflict con texto y state description. | Golden + instrumentada |

### Decisiones del piloto Vault

- Sync ocupa la acción superior derecha con target mínimo de `48dp`; crear item es una acción
  flotante inferior derecha, situada sobre la navegación y fuera de su área táctil.
- Las filas no reservan un thumbnail ni un icono decorativo. El tipo se muestra como texto
  secundario breve (`Password` o `Secure note`) porque ayuda a distinguir títulos coincidentes sin
  competir con el nombre; M143 deberá validar si sigue aportando valor con contenido real.
- La ausencia de badge significa que el item está sincronizado. `Draft` y `Conflict` se muestran en
  la línea del título, con texto y `stateDescription`; el acento cálido refuerza el estado, pero no
  es su única señal.
- `Synced just now` representa feedback transitorio después de un sync manual correcto, no una
  cabecera permanente. Debe anunciarse accesiblemente y desaparecer usando el timeout recomendado
  por el sistema; con TalkBack o tiempo extendido debe permanecer lo suficiente para comprenderlo.
- Un aviso de sync pausado sí permanece mientras la condición siga vigente. La recuperación se
  ejecuta desde la acción de sync superior, evitando duplicar `Retry sync` dentro del banner.

### Fixtures permitidos

- Cuenta: `person@example.com`.
- Vault: `Vault Alpha`.
- Items: `Example login`, `Example note`, `Synthetic record`.
- UUIDs y fechas: valores sintéticos reservados para previews.
- Password, passphrase y recovery key: siempre enmascaradas, no operativas y nunca con formato de
  credencial real.

No se capturan pantallas de una cuenta real, payloads, tokens, recovery keys, clipboard, contenido
persistido ni logs. Los mockups no pueden mostrar plaintext protegido ni dejarlo en accesibilidad,
saved state o metadatos de export. Un mockup que revele contenido que debe estar oculto es un fallo
de privacidad, no una referencia aceptable.

### Checklist de aprobación humana

El owner aprobó explícitamente el paquete completo el 11 de octubre de 2026 después de revisar la
guía y los ocho mockups. La aprobación queda registrada como decisión de `maintainer/product-design`
en la conversación de SCDK-M135.

- [x] El owner confirma que `assets/logo/master.svg` corresponde a la geometría aprobada.
- [x] El owner aprueba las variantes principal, invertida y monocroma.
- [x] El owner aprueba clearspace `W/8` y mínimos `24dp`/`32dp`.
- [x] El owner aprueba launcher adaptativo, round, themed monochrome, splash e in-app.
- [x] El owner aprueba los hex exactos de las paletas clara y oscura y la tabla de contraste.
- [x] El owner aprueba Manrope, los pesos 400/500/600/700, fallback `sans-serif` y OFL 1.1.
- [x] El owner aprueba formas, elevación, densidad, ilustración e iconografía.
- [x] El owner aprueba los ocho mockups, en claro y oscuro, con fixtures sintéticos.
- [x] El owner confirma que ningún mockup, export o licencia depende de una cuenta o documento
  externo como única fuente.
- [x] El owner registra fecha, rol de aprobación y enlaces a los ocho exports.

## 10. Reproducibilidad y licencias

### Inventario de fuentes

| Recurso | Fuente local o ruta canónica | Regla de distribución |
|---|---|---|
| Logo | `assets/logo/master.svg` | Geometría propiedad/proporcionada por el owner; no redistribuir fuera del proyecto sin autorización. |
| Variantes de logo | `assets/logo/` | Derivadas del master; cada export conserva un registro de origen. |
| Manrope | `assets/type/manrope/` | OFL 1.1; incluir `OFL.txt` y aviso de copyright junto a los TTF. |
| Mockups | `mockups/` | Fixtures sintéticos; no son fuente de assets ni contienen datos reales. |

Cada export debe poder regenerarse desde una fuente versionada local, sin login, plugin privado,
documento externo o URL como única dependencia. El registro de assets de la implementación debe
contener, como mínimo, ruta, origen, checksum SHA-256, herramienta/versión de export, fecha y
variante. Una variante que no pueda rastrearse hasta el master se rechaza.

La licencia OFL permite empaquetar y redistribuir Manrope con el APK y el repositorio, siempre que
se mantengan el aviso y la licencia, no se venda la fuente por separado y las versiones modificadas
conserven la licencia y respeten los nombres reservados. La guía y los mockups no relicencian el
logo del owner ni presentan una licencia de terceros que no exista.

## 11. Privacidad visual y accesibilidad

- Los mockups, goldens y previews usan solo los fixtures de esta guía y no contienen secretos,
  tokens, payloads, recovery keys reales, identificadores reales ni datos de cuenta.
- Campos secretos permanecen enmascarados por defecto; reveal, foco y estado se expresan mediante
  label y semántica accesibles.
- El logo, color, icono y motion no reemplazan headings, labels, estado anunciado o acción textual.
- Las parejas de contraste se validan contra la tabla anterior; los bordes usan `outline` solo como
  elemento gráfico y no como body text.
- Los conceptos de splash y launcher nunca muestran una ruta protegida, contenido del vault ni
  información que permita inferir una sesión.
- M138 y las tareas de UI deben conservar los gates de [`SPEC-UI-V1`](../specs/features/ui-brand-motion-v1.md):
  targets de 48dp, TalkBack en Login/Unlock/creación/retry de sync/Settings, font scale 200 %,
  orden de foco y motion scale `0`.

## 12. Handoff a SCDK-M138

M138 consume esta guía como entrada canónica para tokens y assets:

1. Convertir los roles Material y sus hex exactos en tokens light/dark sin sustituirlos por color
   dinámico por defecto.
2. Incorporar Manrope 400/500/600/700, OFL 1.1 y fallback `sans-serif`, manteniendo el escalado
   hasta 200 %.
3. Mapear la escala de formas, elevación, densidad, iconografía y spacing a APIs de `core:ui`.
4. Consumir únicamente las variantes de logo versionadas y conservar master, paths, clearspace y
   mínimos.
5. Crear previews/goldens de los ocho mockups con los mismos fixtures y contrastes.
6. Mantener separados los conceptos de launcher/splash de la implementación Android, que queda
   fuera de M135.

M138 no puede corregir una divergencia visual cambiando un color, un peso tipográfico o una forma
localmente en una feature. Cualquier cambio de identidad debe actualizar esta guía, repetir la
revisión de contraste/evidencia y obtener una nueva aprobación del owner.

## 13. Estado y criterios de salida

Estado actual: `APPROVED` desde el 11 de octubre de 2026.

El owner completó el checklist de la sección 9 y aprobó identidad y mockups piloto explícitamente.
La guía queda habilitada como entrada canónica para las tareas posteriores. Los cambios de recursos
Android siguen perteneciendo al alcance explícito de M138.

### Criterios de aceptación de M135

- [x] Master y variantes se versionan bajo `docs/design/assets/logo/` sin cambiar la geometría.
- [x] Existen conceptos utilizables para adaptive launcher, round, themed monochrome, splash e in-app.
- [x] Las paletas light/dark y los pares de contraste están documentados exactamente.
- [x] Manrope, licencia OFL 1.1, pesos y fallback están documentados bajo la ruta canónica.
- [x] Los ocho mockups cubren Welcome/Login, Unlock y Vault en light/dark.
- [x] Tono, formas, iconografía y usos incorrectos están definidos.
- [x] No hay dependencia externa como única fuente ni datos sensibles en evidencia.
- [x] El owner aprueba identidad y mockups; la evidencia queda registrada antes de cerrar M135.
