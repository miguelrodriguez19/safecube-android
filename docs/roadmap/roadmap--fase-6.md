# FASE 6 — Release Engineering & Quality Gates

## Objetivo de la fase

Construir la infraestructura de integración y entrega continua necesaria para distribuir SafeCube
como beta abierta mediante un APK firmado en GitHub Releases.

Esta fase no publica todavía `v1.0.0`. Deja preparado y validado el mecanismo que utilizará la fase
10 para publicar `v1.0.0-rc.N` y promover el release candidate aceptado a `v1.0.0`.

## Decisiones cerradas

- GitHub Actions es la plataforma de CI/CD.
- GitHub Releases es el canal de distribución; Google Play queda fuera de alcance.
- La beta es abierta y el APK se considera un artefacto público.
- `version.properties` es la única fuente de verdad de `versionName` y `versionCode`.
- `versionName` sigue SemVer 2.0.0 y los tags tienen formato `v<versionName>`.
- `versionCode` es un entero positivo y aumenta en cada APK público.
- Los pull requests nunca reciben secretos de firma y validan una release sin firmar.
- Solo el workflow protegido de release puede construir el APK firmado.
- La publicación nunca reutiliza ni sobrescribe una versión existente.
- JDK 21 es el runtime de CI para Gradle y Android Gradle Plugin.
- Los actions de terceros se fijan por SHA completo y Dependabot mantiene esas referencias.
- El camino crítico termina en la tarea 14.
- Las tareas 15 y 16 son mejoras opcionales y no bloquean la fase.

---

# 01. Documentar la política de versiones y releases

## Main Story (How, I Want, To)

Como maintainer, quiero una política canónica de versionado y publicación para que personas y
agentes automaticen releases sin interpretar de nuevo las reglas del proyecto.

## Context, Functional Description & Goal

SafeCube todavía no tiene tags, changelog ni proceso de release. La aplicación declara
manualmente `versionName = "1.0"` y la futura beta será descargable por cualquier persona desde
GitHub. Antes de automatizar el pipeline debe existir un contrato inmutable que defina qué se
versiona, cómo se identifica un prerelease y cuándo un artefacto puede publicarse.

## Steps/Scope

### In Scope

- Crear `docs/release/release-policy.md`.
- Definir SemVer `MAJOR.MINOR.PATCH[-PRERELEASE][+BUILD]` para `versionName`.
- Definir tags exactos `v<versionName>`, por ejemplo `v1.0.0-rc.1`.
- Definir `versionCode` como contador entero positivo, monotónico e independiente de SemVer.
- Establecer que cada APK público exige un `versionCode` mayor que el último APK publicado.
- Definir releases con sufijo SemVer como prereleases de GitHub y versiones sin sufijo como
  releases estables.
- Definir que tags, releases y APK publicados son inmutables; una corrección crea otra versión.
- Documentar que el canal de v1 es GitHub Releases y que Google Play queda fuera de alcance.
- Documentar la matriz soportada API 30-36 y la condición de beta abierta.
- Añadir el procedimiento de retirada: marcar la release afectada, publicar aviso y corregir en una
  versión nueva, sin reemplazar el APK existente.

### Out of Scope (if applies)

- Crear workflows.
- Generar tags o releases reales.
- Automatizar el changelog.
- Publicar en Google Play.

## Additional Information and Configuration

- Esta tarea no modifica `build.gradle.kts`.
- Usar como referencia normativa [Semantic Versioning 2.0.0](https://semver.org/).
- Dependencia: ninguna.

### API Contract and Expected Behavior (if applies)

No cambia APIs de runtime. El contrato público de release queda formado por:

- `versionName`: SemVer válido.
- `versionCode`: entero positivo monotónico.
- tag: `v<versionName>`.
- APK: `safecube-<versionName>.apk`.
- checksum: `safecube-<versionName>.apk.sha256`.

### Acceptance Criteria (ACs)

- Existe `docs/release/release-policy.md`.
- La política resuelve versiones estables, prereleases, hotfixes y retirada de una release.
- Queda explícito que un artefacto publicado no se reemplaza.
- Queda explícito que `versionCode` debe aumentar aunque solo cambie el prerelease.
- Una persona puede determinar el siguiente tag y los nombres de artefacto sin consultar código.

---

# 02. Crear una única fuente de verdad para la versión Android

## Main Story (How, I Want, To)

Como developer, quiero declarar la versión una sola vez para evitar discrepancias entre Gradle, el
APK, el tag y GitHub Releases.

## Context, Functional Description & Goal

`app/build.gradle.kts` contiene actualmente `versionCode = 1` y `versionName = "1.0"`. El pipeline
necesita leer y validar los mismos valores de forma determinista. La versión inicial de desarrollo
se fijará como `0.1.0` con `versionCode = 1`; no implica publicar esa versión.

## Steps/Scope

### In Scope

- Crear `version.properties` en la raíz con:
  - `VERSION_NAME=0.1.0`
  - `VERSION_CODE=1`
- Leer ambos valores desde `app/build.gradle.kts`.
- Eliminar los literales actuales de `defaultConfig`.
- Fallar durante la configuración si falta el archivo, una propiedad o existe una propiedad vacía.
- Validar `VERSION_NAME` contra SemVer 2.0.0 completo.
- Validar que `VERSION_CODE` sea un entero mayor que cero.
- Añadir una tarea raíz `validateVersion` que muestre únicamente versión y código, sin secretos.
- Encapsular el parser y la validación en
  `buildSrc/src/main/kotlin/com/miguelrodriguez19/safecube/buildlogic/AppVersion.kt`.
- Añadir tests JUnit en
  `buildSrc/src/test/kotlin/com/miguelrodriguez19/safecube/buildlogic/AppVersionTest.kt` para:
  - versión estable válida;
  - prerelease válido;
  - build metadata válida;
  - SemVer inválido;
  - `versionCode` inválido.
- Documentar en `docs/release/release-policy.md` cómo actualizar ambos valores.

### Out of Scope (if applies)

- Incrementar versiones automáticamente.
- Crear tags.
- Inferir la versión desde el nombre de rama o número de commits.
- Cambiar `applicationId`.

## Additional Information and Configuration

- No usar variables de entorno como fuente primaria de versión.
- Gradle debe seguir siendo configurable sin Git instalado.
- Dependencia: tarea 01.

### API Contract and Expected Behavior (if applies)

```properties
VERSION_NAME=0.1.0
VERSION_CODE=1
```

`./gradlew validateVersion` devuelve código `0` solo si ambas propiedades son válidas.

### Acceptance Criteria (ACs)

- `versionName` y `versionCode` del APK proceden exclusivamente de `version.properties`.
- `./gradlew validateVersion` pasa con la configuración versionada.
- Un SemVer o código inválido detiene la build con un mensaje accionable.
- `rg "versionName\\s*="` y `rg "versionCode\\s*="` no encuentran otro valor funcional hardcodeado
  para la app.
- `./gradlew :app:assembleDebug` sigue funcionando.

---

# 03. Sustituir la firma debug por configuración segura de release

## Main Story (How, I Want, To)

Como release manager, quiero firmar los APK públicos con una clave exclusiva de producción sin
guardar el keystore ni sus credenciales en Git.

## Context, Functional Description & Goal

La build `release` usa actualmente `~/.android/debug.keystore` y contraseñas conocidas. Un APK
público debe usar una identidad de firma estable y privada. Los pull requests deben poder compilar
la variante release sin acceder a secretos, pero la publicación debe fallar si la firma real no
está disponible.

## Steps/Scope

### In Scope

- Eliminar el signing config que usa el keystore de debug.
- Leer exclusivamente estas variables para firma pública:
  - `SAFECUBE_RELEASE_KEYSTORE_PATH`
  - `SAFECUBE_RELEASE_STORE_PASSWORD`
  - `SAFECUBE_RELEASE_KEY_ALIAS`
  - `SAFECUBE_RELEASE_KEY_PASSWORD`
- Configurar `signingConfigs.release` solo cuando las cuatro variables estén presentes.
- Fallar con un error explícito si la configuración está parcialmente definida.
- Permitir `assembleRelease` sin firma cuando ninguna variable está definida.
- Crear `verifyReleaseSigningConfiguration`, que falle si falta cualquier variable o archivo.
- Añadir patrones de keystore (`*.jks`, `*.keystore`) al `.gitignore`.
- Documentar cómo generar, respaldar y rotar el keystore sin incluir valores reales.
- Documentar los cuatro nombres de secrets que utilizará GitHub.

### Out of Scope (if applies)

- Generar o custodiar el keystore real.
- Subir secrets a GitHub.
- Firmar con la debug key como fallback.
- Implementar Play App Signing.

## Additional Information and Configuration

- La clave real debe generarse fuera del repositorio y mantenerse en al menos dos backups cifrados.
- No imprimir rutas sensibles, alias, passwords ni contenido base64 en logs.
- Referencia: [firma de apps Android](https://developer.android.com/build/building-cmdline#sign_cmdline).
- Dependencias: tareas 01 y 02.

### API Contract and Expected Behavior (if applies)

- Sin variables: `assembleRelease` produce una variante no publicable y
  `verifyReleaseSigningConfiguration` falla.
- Con las cuatro variables: `assembleRelease` produce un APK firmado y
  `verifyReleaseSigningConfiguration` pasa.
- Con configuración parcial: la configuración Gradle falla indicando qué nombre falta.

### Acceptance Criteria (ACs)

- No existe ninguna referencia funcional a `debug.keystore` dentro de la firma release.
- No hay passwords ni keystores versionados.
- La build debug continúa usando la firma debug estándar.
- La build release sin secrets puede compilar para CI.
- La verificación de firma falla en cerrado antes de una publicación.
- Los tests cubren configuración completa, ausente y parcial.

---

# 04. Dejar Android Lint sin errores ni baseline

## Main Story (How, I Want, To)

Como developer, quiero que Android Lint sea un quality gate real para impedir que deuda conocida se
incorpore al pipeline como una excepción permanente.

## Context, Functional Description & Goal

La línea base actual de `lintDebug` falla. El primer bloqueo conocido contiene 13 errores y 9
warnings en `core:ui`, principalmente traducciones ausentes, acentos, plurales y elipsis. Al
resolverlos pueden aparecer findings de otros módulos que también deben corregirse.

## Steps/Scope

### In Scope

- Ejecutar `./gradlew lintDebug` y conservar el reporte inicial.
- Corregir todos los errores de lint en todos los módulos.
- Corregir los warnings accionables de recursos, accesibilidad y seguridad.
- Completar la paridad de recursos inglés/español.
- Usar `<plurals>` donde la cantidad cambie la gramática.
- Sustituir cadenas hardcodeadas detectadas por recursos cuando corresponda.
- Añadir `tools:locale` al catálogo por defecto si lint lo necesita.
- Volver a ejecutar lint desde una build limpia.

### Out of Scope (if applies)

- Crear `lint-baseline.xml`.
- Desactivar checks globalmente.
- Añadir `tools:ignore`, `@SuppressLint` o `abortOnError = false` para ocultar deuda existente.
- Rediseñar pantallas o cambiar copy más allá de lo necesario para corregir los findings.

## Additional Information and Configuration

- Una supresión solo es admisible si se documenta como falso positivo específico y se acompaña de
  test; no se permiten supresiones de módulo o categoría.
- Dependencia: ninguna; debe completarse antes de la tarea 05.

### API Contract and Expected Behavior (if applies)

No cambia APIs. `./gradlew lintDebug` pasa con cero errores.

### Acceptance Criteria (ACs)

- `./gradlew lintDebug` termina correctamente.
- No se añade ningún baseline.
- Inglés y español contienen las mismas keys traducibles.
- No quedan textos dummy o de plantilla afectados por los findings corregidos.
- `git diff` no contiene una desactivación global de lint.

---

# 05. Crear comandos Gradle canónicos para CI y release

## Main Story (How, I Want, To)

Como agente de CI, quiero ejecutar un único comando versionado para aplicar siempre los mismos
quality gates localmente y en GitHub Actions.

## Context, Functional Description & Goal

El proyecto ya dispone de `verifyCoverage`, pero lint, validación de versión, contrato OpenAPI y
ensamblado release se invocan por separado. Duplicar listas de comandos entre documentación y YAML
provocaría divergencias.

## Steps/Scope

### In Scope

- Crear una tarea raíz `ciVerify`.
- Hacer que `ciVerify` dependa de:
  - `validateVersion`;
  - `verifyCoverage`;
  - `lintDebug`;
  - el test de contrato `VaultSyncOpenApiContractTest`;
  - `:app:assembleRelease`.
- Garantizar que el contrato OpenAPI se ejecuta dentro del gate incluso si cambia la agregación de
  cobertura.
- Crear una tarea raíz `releaseVerify`.
- Hacer que `releaseVerify` dependa de `ciVerify` y `lintRelease`.
- Mantener `verifyReleaseSigningConfiguration` fuera de `ciVerify` y añadirlo al flujo público de
  release.
- Actualizar `docs/testing/testing.md` con ambos comandos y sus diferencias.
- Evitar ejecutar dos veces la misma suite dentro de una invocación Gradle.

### Out of Scope (if applies)

- Ejecutar emuladores.
- Publicar artefactos.
- Añadir lógica específica de GitHub dentro de Gradle.
- Usar `clean` como dependencia obligatoria.

## Additional Information and Configuration

- `ciVerify` debe funcionar sin secrets y ensamblar una release no firmada.
- `releaseVerify` valida código release pero tampoco publica.
- Dependencias: tareas 02, 03 y 04.

### API Contract and Expected Behavior (if applies)

```bash
./gradlew ciVerify
./gradlew releaseVerify
```

Ambos comandos deben devolver un exit code distinto de cero ante cualquier gate fallido.

### Acceptance Criteria (ACs)

- `./gradlew ciVerify` pasa localmente sin secrets.
- `./gradlew releaseVerify` pasa localmente sin publicar ni firmar.
- Borrar o romper deliberadamente el contrato OpenAPI hace fallar `ciVerify`.
- Un fallo de cobertura o lint hace fallar ambos comandos.
- La documentación deja de mantener una lista manual diferente a las dependencias Gradle.

---

# 06. Reemplazar los instrumented tests de plantilla por un smoke test real

## Main Story (How, I Want, To)

Como release manager, quiero un smoke test instrumentado mínimo para saber que el APK arranca y
permite entrar al flujo de autenticación en un dispositivo Android real.

## Context, Functional Description & Goal

`MainActivityComposeTest` todavía intenta renderizar `Greeting("Android")`, símbolo que ya no
representa la aplicación, y `ExampleInstrumentedTest` solo comprueba el package name. Ninguno
protege el arranque real de SafeCube.

## Steps/Scope

### In Scope

- Eliminar o reemplazar los dos tests instrumentados de plantilla.
- Usar `createAndroidComposeRule<MainActivity>()`.
- Añadir test tags o semántica estable únicamente donde sea necesario para no depender del copy.
- Cubrir un arranque limpio y verificar que aparece la pantalla Welcome.
- Cubrir la navegación Welcome -> Login sin realizar llamadas reales al backend.
- Asegurar aislamiento entre tests y estado local limpio.
- Ejecutar los tests en API 30 como baseline de compatibilidad mínima.
- Documentar el comando local.

### Out of Scope (if applies)

- Login real.
- Levantar backend o MockWebServer en instrumentación.
- CRUD, crypto o sync E2E.
- Matriz API 30-36 completa; pertenece a la fase 10.
- Screenshot testing.

## Additional Information and Configuration

- Los selectores deben usar semantics/test tags estables; no usar sleeps.
- Los tests no pueden depender del idioma configurado en el dispositivo.
- Dependencia: tarea 04 para disponer de recursos coherentes.

### API Contract and Expected Behavior (if applies)

El test arranca `MainActivity` desde estado sin sesión y comprueba:

1. Welcome visible.
2. Acción Login disponible.
3. Click en Login navega al formulario de autenticación.

### Acceptance Criteria (ACs)

- No quedan referencias a `Greeting` ni a `Hello Android!`.
- Los tests pasan en un emulador API 30.
- Los tests no acceden a Internet.
- No utilizan esperas temporales arbitrarias.
- Un fallo real de arranque o navegación hace fallar la suite.

---

# 07. Implementar el workflow principal de integración continua

## Main Story (How, I Want, To)

Como maintainer, quiero validar cada pull request y cada cambio de `main` para impedir merges que
rompan los quality gates.

## Context, Functional Description & Goal

El repositorio no tiene GitHub Actions. El workflow debe reutilizar `ciVerify`, operar sin secrets y
ser seguro para pull requests externos propios de una beta abierta.

## Steps/Scope

### In Scope

- Crear `.github/workflows/ci.yml`.
- Ejecutarlo en:
  - `pull_request`;
  - push a `main`;
  - `workflow_dispatch`.
- Usar Ubuntu y JDK 21 Temurin.
- Configurar Gradle mediante `gradle/actions/setup-gradle`.
- Ejecutar `./gradlew --no-daemon ciVerify`.
- Conceder únicamente `contents: read`.
- Añadir concurrency por workflow/ref y cancelar ejecuciones obsoletas.
- Fijar todos los actions por SHA completo y dejar comentario con la versión humana.
- Subir reportes de tests, lint y cobertura con `if: always()`.
- No subir el APK release sin firmar.
- Documentar el nombre exacto del check que debe marcarse como required en branch protection.

### Out of Scope (if applies)

- Instrumented tests; se añaden en la tarea 08.
- Firma y publicación.
- Uso de runners self-hosted.
- Secrets en eventos `pull_request`.

## Additional Information and Configuration

- El workflow no debe depender del estado de caches para pasar.
- No usar `pull_request_target` para compilar código de contribuciones.
- Dependencia: tarea 05.

### API Contract and Expected Behavior (if applies)

El check `CI / verify` debe ser determinista:

- éxito: todos los gates pasan;
- fallo: al menos un gate falla y sus reportes quedan disponibles;
- cancelado: existe una ejecución posterior para la misma ref.

### Acceptance Criteria (ACs)

- Un pull request dispara `CI / verify`.
- Un push a `main` dispara el mismo comando.
- El workflow funciona sin secrets de repositorio.
- Los permisos del token están declarados y son read-only.
- Los reportes se conservan también ante fallo.
- La documentación indica cómo activar el required check en GitHub.

---

# 08. Añadir el smoke test instrumentado al CI

## Main Story (How, I Want, To)

Como maintainer, quiero ejecutar el smoke test en un emulador para detectar problemas de arranque
que los tests JVM y el ensamblado no pueden descubrir.

## Context, Functional Description & Goal

La tarea 06 crea una suite instrumentada mínima. Debe ejecutarse como job separado para que su
coste, logs y resultado no oculten los gates JVM.

## Steps/Scope

### In Scope

- Configurar un Gradle Managed Device llamado `pixel2Api30` con device `Pixel 2`, API 30,
  system image `aosp-atd` y arquitectura x86_64.
- Añadir al CI un job `instrumented-smoke`.
- Ejecutarlo en pull requests, push a `main` y `workflow_dispatch`.
- Habilitar KVM en el runner de forma explícita.
- Ejecutar únicamente la suite smoke de `app`.
- Desactivar animaciones durante tests.
- Subir resultados, logcat y screenshots de fallo con `if: failure()`.
- Establecer timeout del job para evitar runners bloqueados.
- Documentar el check para branch protection.

### Out of Scope (if applies)

- Sharding.
- Firebase Test Lab.
- Runners macOS.
- APIs adicionales a API 30.
- Tests E2E con backend.

## Additional Information and Configuration

- No descargar ni ejecutar actions de emulador sin fijarlos por SHA.
- Preferir Gradle Managed Devices para que la definición del dispositivo viva en el repositorio.
- Dependencias: tareas 06 y 07.

### API Contract and Expected Behavior (if applies)

El check `CI / instrumented-smoke` pasa solo si SafeCube arranca y completa el flujo Welcome ->
Login en API 30.

### Acceptance Criteria (ACs)

- El job arranca un dispositivo desde estado limpio.
- El smoke test se ejecuta en GitHub Actions.
- Un fallo conserva evidencia suficiente para diagnosticarlo.
- El job tiene timeout y no solicita secrets.
- El check puede configurarse como obligatorio antes de merge.

---

# 09. Automatizar actualizaciones y revisión de dependencias

## Main Story (How, I Want, To)

Como maintainer, quiero recibir actualizaciones controladas y bloquear dependencias vulnerables
para reducir riesgo de supply chain sin revisar versiones manualmente.

## Context, Functional Description & Goal

El proyecto usa Gradle Version Catalog y GitHub Actions, pero no existe configuración de Dependabot
ni revisión de dependencias en pull requests.

## Steps/Scope

### In Scope

- Crear `.github/dependabot.yml`.
- Configurar ecosistemas:
  - `gradle`;
  - `github-actions`.
- Programar revisión semanal.
- Limitar el número de PRs abiertos por ecosistema.
- Agrupar actualizaciones patch/minor compatibles y mantener majors separadas.
- Crear `.github/workflows/dependency-review.yml`.
- Ejecutar dependency review en pull requests.
- Fallar ante vulnerabilidades high o critical.
- Mantener permisos mínimos `contents: read`.
- Documentar que Dependabot no hace auto-merge.

### Out of Scope (if applies)

- Actualizar dependencias existentes dentro de esta card.
- Auto-merge.
- Renovate.
- Definir una política legal completa de licencias.

## Additional Information and Configuration

- Las PRs de Dependabot deben ejecutar el mismo CI sin requerir secrets.
- Referencia: [Dependabot y GitHub Actions](https://docs.github.com/en/code-security/reference/supply-chain-security/dependabot-on-actions).
- Dependencia: tarea 07.

### API Contract and Expected Behavior (if applies)

- Dependabot abre PRs semanales según configuración.
- Dependency Review bloquea únicamente severidad high/critical.
- Toda actualización sigue pasando por los required checks.

### Acceptance Criteria (ACs)

- La configuración de ambos ecosistemas es válida para GitHub.
- Las actualizaciones major no se mezclan con minor/patch.
- Ninguna PR se fusiona automáticamente.
- El workflow usa permisos read-only.
- Una dependencia vulnerable high/critical hace fallar el check.

---

# 10. Añadir análisis estático de seguridad con CodeQL

## Main Story (How, I Want, To)

Como maintainer, quiero analizar Java/Kotlin automáticamente para detectar vulnerabilidades antes
de distribuir una beta abierta.

## Context, Functional Description & Goal

Los tests y Android Lint no sustituyen el análisis de flujos de datos de seguridad. CodeQL debe
ejecutarse de forma independiente y con la configuración soportada por GitHub.

## Steps/Scope

### In Scope

- Crear `.github/workflows/codeql.yml`.
- Configurar el lenguaje `java-kotlin`.
- Ejecutar en:
  - pull requests contra `main`;
  - push a `main`;
  - schedule semanal;
  - `workflow_dispatch`.
- Usar el modo de build adecuado para Gradle/Android y verificar que analiza fuentes Kotlin.
- Declarar los permisos mínimos exigidos por CodeQL.
- Añadir concurrency para cancelar análisis obsoletos de la misma ref.
- Fijar las actions por SHA completo.
- Documentar el nombre del check y dónde consultar alertas.

### Out of Scope (if applies)

- Corregir hallazgos no relacionados que requieran rediseño funcional; se trackean como nuevas
  cards.
- Añadir SonarQube.
- Analizar el backend.

## Additional Information and Configuration

- No conceder permisos de escritura distintos a `security-events: write`.
- Dependencia: tarea 07.

### API Contract and Expected Behavior (if applies)

CodeQL analiza Java/Kotlin y publica resultados en GitHub Security. Un error de configuración o
build hace fallar el workflow.

### Acceptance Criteria (ACs)

- CodeQL completa un análisis de `main`.
- Las fuentes Kotlin de los módulos Android aparecen en el análisis.
- El workflow se ejecuta semanalmente y en pull requests.
- Los permisos están limitados a los requeridos.
- El check puede hacerse obligatorio en branch protection.

---

# 11. Añadir prevención de secretos en el repositorio

## Main Story (How, I Want, To)

Como security owner, quiero detectar credenciales antes de merge para impedir que claves de firma,
tokens o passwords terminen en el historial público.

## Context, Functional Description & Goal

La fase introduce secrets de firma y workflows con permisos de publicación. `.gitignore` evita
accidentes locales, pero no detecta secretos pegados en código, documentación o YAML.

## Steps/Scope

### In Scope

- Añadir configuración versionada de Gitleaks.
- Crear `.github/workflows/secret-scan.yml`.
- Escanear historial relevante en pull requests, push a `main` y ejecución manual.
- Usar checkout con historial suficiente.
- Activar redacción para que un finding no imprima el secreto.
- Fijar el scanner/action por SHA completo.
- Añadir allowlist solo para fixtures falsos concretos y documentados.
- Añadir un test controlado que demuestre que el workflow detecta un patrón ficticio.
- Documentar como paso manual la activación de GitHub Secret Scanning y Push Protection si están
  disponibles para el repositorio.

### Out of Scope (if applies)

- Guardar secretos reales como fixtures.
- Reescribir historial Git.
- Rotar una credencial real encontrada; requeriría una card de incidente inmediata.

## Additional Information and Configuration

- El scanner debe ejecutarse antes de cualquier job de publicación.
- La salida nunca debe mostrar el valor detectado.
- Dependencias: tareas 03 y 07.

### API Contract and Expected Behavior (if applies)

- Sin findings: check correcto.
- Con patrón de secreto no permitido: check fallido y localización del archivo, con valor redactado.

### Acceptance Criteria (ACs)

- El workflow detecta el fixture ficticio de prueba.
- El valor del fixture aparece redactado en logs.
- Eliminar el fixture devuelve el workflow a verde.
- No existe una allowlist global o excesivamente amplia.
- El runbook explica cómo responder y rotar si aparece un secreto real.

---

# 12. Construir y verificar el APK firmado en un workflow de release

## Main Story (How, I Want, To)

Como release manager, quiero producir un APK firmado y verificable desde un tag SemVer sin exponer
la clave privada.

## Context, Functional Description & Goal

CI valida releases sin firma. La distribución pública necesita un workflow separado, protegido y
con acceso explícito a la identidad de firma. Esta tarea construye el artefacto, pero todavía no
crea la GitHub Release.

## Steps/Scope

### In Scope

- Crear `.github/workflows/release.yml`.
- Disparar por tags `v*.*.*` y mediante `workflow_dispatch` en modo dry-run.
- Usar un GitHub Environment llamado `release`.
- Configurar permisos read-only en el job de build.
- Validar que el tag sea exactamente `v${VERSION_NAME}`.
- Decodificar `SAFECUBE_RELEASE_KEYSTORE_BASE64` dentro de `$RUNNER_TEMP`.
- Exportar la ruta temporal y los otros tres secrets con los nombres definidos en la tarea 03.
- Ejecutar:
  - `releaseVerify`;
  - `verifyReleaseSigningConfiguration`;
  - `:app:assembleRelease`.
- Localizar un único APK release.
- Verificar su firma con `apksigner verify --verbose --print-certs`.
- Renombrarlo a `safecube-<versionName>.apk`.
- Generar `safecube-<versionName>.apk.sha256`.
- Subir ambos como workflow artifact.
- Borrar el keystore temporal en un step `if: always()`.

### Out of Scope (if applies)

- Crear la GitHub Release.
- Publicar en Play Store.
- Exponer secrets a pull requests.
- Generar la clave de firma.

## Additional Information and Configuration

- Secrets del environment:
  - `SAFECUBE_RELEASE_KEYSTORE_BASE64`
  - `SAFECUBE_RELEASE_STORE_PASSWORD`
  - `SAFECUBE_RELEASE_KEY_ALIAS`
  - `SAFECUBE_RELEASE_KEY_PASSWORD`
- El dry-run exige el environment y produce artefactos, pero no publica.
- Dependencias: tareas 02, 03, 05, 07 y 11.

### API Contract and Expected Behavior (if applies)

Para un tag `v1.0.0-rc.1` y `VERSION_NAME=1.0.0-rc.1`:

```text
safecube-1.0.0-rc.1.apk
safecube-1.0.0-rc.1.apk.sha256
```

Un tag distinto de la versión declarada detiene el workflow antes de usar la clave.

### Acceptance Criteria (ACs)

- El dry-run produce un APK firmado y un checksum.
- `apksigner` valida el artefacto.
- El nombre del APK coincide con `VERSION_NAME`.
- El keystore solo existe en el directorio temporal del runner.
- Los logs no contienen material sensible.
- Una versión/tag inconsistente hace fallar el workflow.

---

# 13. Publicar automáticamente el APK en GitHub Releases

## Main Story (How, I Want, To)

Como usuario de la beta abierta, quiero descargar un APK y su checksum desde una release pública
identificada por una versión inmutable.

## Context, Functional Description & Goal

La tarea 12 genera y verifica los artefactos. Falta un job de publicación con permisos separados
para reducir el alcance del token que puede escribir en el repositorio.

## Steps/Scope

### In Scope

- Añadir a `release.yml` un job `publish` dependiente del job de build.
- No ejecutar `publish` durante `workflow_dispatch` dry-run.
- Conceder `contents: write` únicamente a este job.
- Descargar exactamente el workflow artifact producido por el job anterior.
- Comprobar de nuevo el checksum antes de publicar.
- Crear la release con GitHub CLI y `GITHUB_TOKEN`.
- Adjuntar APK y `.sha256`.
- Usar release notes generadas por GitHub.
- Marcar automáticamente como prerelease cualquier SemVer con sufijo.
- Publicar como estable una versión sin sufijo.
- Fallar si ya existe una release o asset para el mismo tag; nunca sobrescribir.
- Mostrar en el texto de la release el comando de verificación SHA-256.

### Out of Scope (if applies)

- Changelog versionado; tarea opcional 15.
- SBOM y attestations; tarea opcional 16.
- Promover automáticamente un RC a estable.
- Google Play.

## Additional Information and Configuration

- El build job conserva permisos read-only; solo `publish` recibe `contents: write`.
- El workflow debe usar el commit apuntado por el tag, no reconstruir desde `main`.
- Dependencia: tarea 12.

### API Contract and Expected Behavior (if applies)

- `v1.0.0-rc.1` crea una GitHub prerelease.
- `v1.0.0` crea una GitHub release estable.
- Ambos publican APK y checksum con nombres deterministas.

### Acceptance Criteria (ACs)

- Un tag SemVer válido crea una release con los dos assets.
- El checksum descargado valida el APK descargado.
- Los prereleases aparecen marcados correctamente.
- Reejecutar la publicación no reemplaza assets ni modifica la release.
- Ningún job diferente de `publish` tiene `contents: write`.

---

# 14. Crear el runbook de release y validar una RC real

> **Revisión de alcance (2026-08-05):** el release train evolucionó después de redactar la card.
> Cada merge a `main` crea automáticamente su tag, `publish` siempre forma parte de `Release Train`
> y su `workflow_dispatch` es una recuperación publicable. Se descarta un workflow firmado de
> dry-run porque duplicaría el camino real y podría divergir. La política y el runbook versionados
> son la autoridad vigente.

## Main Story (How, I Want, To)

Como maintainer, quiero un checklist reproducible y evidencia de una RC real para poder operar el
pipeline sin depender de conocimiento implícito.

## Context, Functional Description & Goal

Los workflows automatizan pasos, pero la custodia de la firma, configuración del environment,
branch protection, incremento de versión y validación del APK requieren acciones humanas
controladas.

## Steps/Scope

### In Scope

- Crear `docs/release/release-runbook.md`.
- Documentar configuración inicial:
  - GitHub Environment `release`;
  - secrets requeridos;
  - reviewers del environment;
  - required checks de branch protection;
  - Secret Scanning/Push Protection.
- Documentar el release train vigente:
  - actualizar `VERSION_NAME` y `VERSION_CODE`;
  - abrir y validar PR;
  - observar la creación automática del tag protegido;
  - revisar y aprobar el Environment;
  - observar build;
  - verificar APK, firma y SHA-256;
  - instalar el APK en un dispositivo limpio.
- Documentar prerelease, release estable, hotfix y retirada.
- Documentar backup y recuperación del keystore.
- Documentar qué hacer ante fallo después de crear el tag, sin moverlo ni sobrescribir assets.
- Registrar el resultado de una RC real sin incluir secretos.
- Reutilizar evidencia de una RC existente si satisface todos los checks; no publicar una versión
  únicamente para cerrar la card.

### Out of Scope (if applies)

- Publicar `v1.0.0-rc.1`; pertenece a la fase 10.
- Mantener un workflow paralelo de dry-run firmado.
- Automatizar rollback.

## Additional Information and Configuration

- Las operaciones de configuración de GitHub que no sean versionables se marcan explícitamente
  como pasos manuales.
- Dependencias: tareas 07-13.

### API Contract and Expected Behavior (if applies)

El runbook describe el release train real. Una RC aprobada ejecuta build, firma, checksum y
publicación inmutable; la evidencia posterior confirma descarga e instalación.

### Acceptance Criteria (ACs)

- El runbook permite operar una release desde una sesión nueva.
- Una RC real termina correctamente con el Environment protegido.
- El APK descargado pasa `apksigner` y checksum.
- El APK se instala y arranca en un dispositivo API soportada.
- Tag, commit, versión, APK y checksum mantienen trazabilidad exacta.
- Fase 6 puede declararse completada sin ejecutar tareas opcionales.

---

# 15. OPCIONAL — Automatizar CHANGELOG mediante Conventional Commits

> **Estado de implementación (2026-08-06):** implementada como complemento del release train de
> SCDK-M104 y de los assets SBOM/provenance de SCDK-M106. `publish` solo lee tags y consume las
> notas generadas; la creación de tags sigue aislada en `create-candidate-tag`.

## Main Story (How, I Want, To)

Como maintainer, quiero generar un changelog consistente para reducir el trabajo manual al preparar
cada release.

## Context, Functional Description & Goal

El historial ya utiliza mayoritariamente prefijos como `feat`, `fix`, `refactor`, `test` y `docs`.
Esta mejora no debe asumir control sobre tags ni publicar releases; complementa el workflow
existente.

## Steps/Scope

### In Scope

- Adoptar Conventional Commits como formato documentado.
- Configurar `git-cliff` mediante un archivo versionado.
- Crear `CHANGELOG.md`.
- Agrupar al menos breaking changes, features, fixes y security.
- Excluir commits puramente mecánicos cuando no aporten valor al usuario.
- Generar notas para el rango desde el tag anterior hasta el tag actual.
- Añadir un comando local reproducible.
- Integrar las notas generadas en el job `publish`.
- Fijar cualquier action o binario descargado mediante versión y checksum/SHA.

### Out of Scope (if applies)

- Crear commits o tags automáticamente.
- Modificar una release ya publicada.
- Convertir automáticamente todo el historial antiguo.
- Sustituir SemVer o `version.properties`.

## Additional Information and Configuration

- La decisión pendiente sobre changelog queda resuelta para el release train actual.
- Dependencias: `SCDK-M91`, `SCDK-M92` y `SCDK-M103`; complementa `SCDK-M104` y `SCDK-M106`.

### API Contract and Expected Behavior (if applies)

Para un tag nuevo, la herramienta produce Markdown determinista usando únicamente commits
alcanzables entre tags.

### Acceptance Criteria (ACs)

- Dos ejecuciones sobre el mismo rango producen el mismo resultado.
- Features, fixes y breaking changes quedan separados.
- El workflow no crea ni mueve tags.
- Las notas de GitHub Release usan el contenido generado.
- Un commit que incumple la convención se detecta antes de release o queda en una sección explícita.

---

# 16. OPCIONAL — Añadir SBOM y provenance attestation al APK

> **Decisión de implementación (2026-08-05):** el SBOM forma parte del job publicable y es un gate
> porque debe acompañar a cada release sin datos sensibles. La provenance se genera después de
> publicar en un job aislado y `continue-on-error`, de modo que una limitación del plan o del
> servicio de attestations no bloquea ni altera los artefactos base.

## Main Story (How, I Want, To)

Como consumidor de una beta pública, quiero verificar el origen y las dependencias del APK para
mejorar la transparencia de la supply chain.

## Context, Functional Description & Goal

GitHub permite asociar attestations de procedencia a binarios y SBOMs. Esta mejora es posterior al
pipeline mínimo y no debe bloquear una release si el plan actual del repositorio no soporta la
funcionalidad.

## Steps/Scope

### In Scope

- Generar un SBOM CycloneDX JSON para la variante release.
- Revisar que el SBOM no contenga rutas locales, secrets ni credenciales.
- Adjuntar el SBOM a GitHub Release.
- Añadir `actions/attest` fijado por SHA.
- Conceder únicamente en el job de attestation:
  - `id-token: write`;
  - `attestations: write`;
  - `contents: read`.
- Generar provenance sobre el APK exacto que se publica.
- Documentar verificación mediante `gh attestation verify`.
- Documentar el comportamiento si el repositorio/plan no admite attestations.

### Out of Scope (if applies)

- Reclamar un nivel SLSA no demostrado.
- Firmar el APK con Sigstore en sustitución de la firma Android.
- Hacer pública información sensible.
- Bloquear el pipeline base por indisponibilidad temporal del servicio de attestations.

## Additional Information and Configuration

- Referencia: [Artifact attestations de GitHub](https://docs.github.com/en/actions/how-tos/secure-your-work/use-artifact-attestations/use-artifact-attestations).
- Esta card es opcional.
- Dependencia: tarea 13.

### API Contract and Expected Behavior (if applies)

Cada release incluye:

```text
safecube-<versionName>.apk
safecube-<versionName>.apk.sha256
safecube-<versionName>.cdx.json
```

La attestation referencia el digest del mismo APK publicado.

### Acceptance Criteria (ACs)

- El SBOM es válido según CycloneDX.
- El SBOM se adjunta a la release.
- `gh attestation verify` valida el APK contra el repositorio.
- Los permisos elevados solo existen en el job de attestation.
- La firma Android continúa validándose de forma independiente.
