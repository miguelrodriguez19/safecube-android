# SafeCube — Política de versionado y releases

| Spec ID               | Status     | Owner     | Last reviewed | Supersedes | Related ADRs |
|-----------------------|------------|-----------|---------------|------------|--------------|
| `SPEC-RELEASE-POLICY` | `APPROVED` | `release` | `2026-08-04`  | `N/A`      | `N/A`        |

## Propósito y estado

Este documento define el contrato público que deben seguir maintainer, CI/CD y agentes de IA al
versionar y publicar SafeCube.

La política se aplica a partir de su adopción en el repositorio. Cada cambio que se fusiona en
`main` debe ser un candidato de release: ya tiene una versión única, supera los quality gates y no
requiere editar el contenido antes de iniciar la publicación.

## Fuente única de versión

Los valores de versión se declaran exclusivamente en el archivo raíz
[`version.properties`](../../version.properties):

```properties
VERSION_NAME=0.1.1
VERSION_CODE=2
```

`app/build.gradle.kts` lee ambos valores desde ese archivo y no contiene una versión funcional
alternativa. La configuración de Gradle falla si el archivo falta, si falta una propiedad, si una
propiedad está vacía, si `VERSION_NAME` no es SemVer 2.0.0 válido o si `VERSION_CODE` no es un
entero positivo. La configuración no depende de Git ni de variables de entorno.

Para actualizar la versión en cualquier pull request que vaya a fusionarse en `main`:

1. Editar `VERSION_NAME` y `VERSION_CODE` en `version.properties` en el mismo cambio.
2. Elegir un `VERSION_NAME` SemVer válido según las reglas de esta política.
3. Incrementar ambos valores frente a la versión de la base de la pull request. `VERSION_NAME` debe
   tener precedencia SemVer estrictamente mayor; el build metadata por sí solo no constituye un
   incremento. `VERSION_CODE` debe ser estrictamente mayor, también si solo cambia el prerelease.
4. Ejecutar `./gradlew validateVersion` y confirmar que muestra únicamente ambos valores.
5. Ejecutar los quality gates y continuar con el flujo de publicación documentado aquí.

No se debe actualizar la versión directamente en `app/build.gradle.kts`, ni inferirla desde ramas,
commits, Git o variables de entorno.

### Guard de entrega continua

El workflow [`Pull Request Quality`](../../.github/workflows/pull-request-quality.yml) llama al
workflow reutilizable [`Kotlin CI`](../../.github/workflows/kotlin-ci-reusable.yml) y ejecuta
`version-guard` antes de los gates JVM e instrumentados. El guard extrae
`version.properties` de la base de la pull request y ejecuta:

```bash
./gradlew validateVersionBump -PbaseVersionFile=<archivo-base>
```

La tarea valida ambas versiones con el mismo parser de la fuente única y falla si `VERSION_NAME` o
`VERSION_CODE` no aumentan. El check `Pull Request Quality / quality / version-guard` debe ser
obligatorio en la protección de `main`. Así se impide fusionar contenido que no pueda pasar
directamente a la etapa de release.

## Firma de releases

Los APK públicos deben utilizar un keystore de release privado y estable. El keystore y sus
credenciales nunca se versionan ni se incluyen en el repositorio. La configuración de Gradle usa
exclusivamente estas variables de entorno:

```text
SAFECUBE_RELEASE_KEYSTORE_PATH
SAFECUBE_RELEASE_STORE_PASSWORD
SAFECUBE_RELEASE_KEY_ALIAS
SAFECUBE_RELEASE_KEY_PASSWORD
```

Comportamiento obligatorio:

- Sin ninguna variable, `assembleRelease` puede producir un APK release sin firma para CI, pero no
  es un artefacto publicable.
- Con una configuración parcial, Gradle falla durante la configuración e indica los nombres de las
  variables ausentes, sin mostrar valores.
- Con las cuatro variables y un archivo de keystore existente,
  `./gradlew verifyReleaseSigningConfiguration` pasa y la variante release utiliza ese keystore.
- `verifyReleaseSigningConfiguration` falla si falta cualquier variable o si el archivo indicado no
  existe.
- La firma debug solo se utiliza para la variante debug; nunca es un fallback de release.

En local, las variables pueden exportarse solo durante la sesión de trabajo:

```bash
export SAFECUBE_RELEASE_KEYSTORE_PATH="/ruta/privada/safecube-release.jks"
export SAFECUBE_RELEASE_STORE_PASSWORD="<valor-no-versionado>"
export SAFECUBE_RELEASE_KEY_ALIAS="<alias-no-versionado>"
export SAFECUBE_RELEASE_KEY_PASSWORD="<valor-no-versionado>"
./gradlew verifyReleaseSigningConfiguration
./gradlew :app:assembleRelease
```

En GitHub, el job protegido `Release Train / publish` usa el Environment `release`. Ese Environment
debe tener exactamente estos cuatro secrets:

```text
SAFECUBE_RELEASE_KEYSTORE_BASE64
SAFECUBE_RELEASE_STORE_PASSWORD
SAFECUBE_RELEASE_KEY_ALIAS
SAFECUBE_RELEASE_KEY_PASSWORD
```

El workflow descodifica el primero únicamente en `$RUNNER_TEMP`, exporta su ruta como
`SAFECUBE_RELEASE_KEYSTORE_PATH` y pasa a Gradle los otros tres valores con los nombres de entorno
del contrato anterior. Nunca persiste el keystore fuera del runner ni imprime secretos. Los pull
requests y los workflows de CI no reciben esos secrets.

### Creación y publicación protegida del candidato

El workflow versionado [`Release Train`](../../.github/workflows/release.yml) se ejecuta tras cada
push a `main`. La protección de la rama debe impedir pushes directos y exigir todos los quality
gates de la pull request. El workflow no repite ese assessment completo, pero vuelve a validar que
`VERSION_NAME` y `VERSION_CODE` aumentaron antes de crear una identidad pública.

`Release Train / create-candidate-tag` es un job sin secrets de firma. Recibe `contents: write`
únicamente para crear el tag ligero e inmutable `v<versionName>` sobre el SHA exacto de `main`. Si
el tag ya apunta a ese SHA, una reejecución puede continuar; si apunta a otro objeto o commit,
falla y nunca lo mueve. Las ejecuciones del Release Train se serializan para que dos commits de
`main` no compitan por el estado de publicación. La consulta de una referencia distingue un `404`
—tag todavía ausente— de cualquier otro error de GitHub; solo el primer caso permite crearla. Si
otra ejecución crea el tag entre consulta y escritura, el job vuelve a comprobar su tipo y SHA
antes de aceptar la operación como idempotente.

`Release Train / publish` depende del tag y siempre forma parte de la ejecución. Usa el Environment
`release`, por lo que queda pendiente hasta que un reviewer autoriza el deployment. Es el único job
que accede a la identidad de firma. Tras la aprobación:

1. hace checkout del tag exacto y comprueba que apunta al SHA del workflow;
2. rechaza una GitHub Release ya existente;
3. descodifica el keystore solo dentro de `$RUNNER_TEMP`;
4. ejecuta `releaseVerify`, `verifyReleaseSigningConfiguration` y `:app:assembleRelease`;
5. valida el APK con `apksigner verify --verbose --print-certs`;
6. genera y comprueba `safecube-<versionName>.apk.sha256`;
7. conserva ambos archivos como workflow artifact y los adjunta a una GitHub Release inmutable.

El job usa `gh release create --verify-tag`, notas generadas por GitHub y una instrucción
`sha256sum -c` para el usuario. Una versión con sufijo SemVer se publica como prerelease y una
versión sin sufijo como estable. Si ya existe la release, falla sin modificar assets ni metadata.
El keystore temporal se elimina con `if: always()`.

`workflow_dispatch` permite recuperar manualmente una publicación fallida del commit actual de
`main`. No es un dry-run: también pasa por `publish`, exige aprobación y falla si la release ya
existe. Seleccionar otra rama o un tag en la ejecución manual es inválido.

### Generación, backup y rotación

El keystore se genera fuera del repositorio, en una máquina controlada por el maintainer. El
comando debe ejecutarse de forma interactiva para no dejar credenciales en el historial del shell:

```bash
keytool -genkeypair -v \
  -keystore safecube-release.jks \
  -alias <alias-de-release> \
  -keyalg RSA \
  -keysize 4096 \
  -validity <dias-de-validez>
```

Después de generarlo:

1. Verificar la identidad y el fingerprint del certificado con `keytool -list -v`.
2. Guardar el keystore en al menos dos backups cifrados e independientes.
3. Custodiar las credenciales fuera de Git y cargarlas únicamente como secrets protegidos del
   Environment de release.
4. Probar la restauración de un backup sin publicar ningún APK.

La rotación debe planificarse antes de que expire el certificado o si existe sospecha de exposición.
La nueva clave se genera y respalda con el mismo procedimiento, se actualizan los cuatro secrets y
se valida con `verifyReleaseSigningConfiguration`. Si la identidad de firma cambia, los APK
existentes pueden no aceptar una actualización directa; la release nueva debe documentar la
acción requerida para el usuario. Nunca se sustituye un APK publicado ni se versiona la clave
anterior.

## Canal y audiencia de distribución

- La distribución de v1 se realiza mediante GitHub Releases.
- La primera distribución es una beta abierta: cualquier persona puede descargar e instalar el APK.
- Google Play queda fuera de alcance de esta política inicial.
- La matriz de dispositivos soportada para v1 comprende Android API 30 hasta API 36.
- Un artefacto público debe poder asociarse de forma inequívoca con su commit, tag, versión y
  checksum.
- La publicación de una beta abierta exige el mismo cuidado de firma, privacidad, migraciones y
  compatibilidad que una release pública estable.

## Modelo de versión

### `versionName`

`versionName` debe ser un número [Semantic Versioning 2.0.0](https://semver.org/) válido con la
forma:

```text
MAJOR.MINOR.PATCH[-PRERELEASE][+BUILD]
```

Reglas:

- `MAJOR`, `MINOR` y `PATCH` son enteros no negativos sin ceros iniciales.
- `PRERELEASE` es opcional y contiene identificadores separados por puntos.
- `BUILD` es opcional y contiene metadatos separados por puntos.
- La precedencia entre versiones se determina según SemVer; el build metadata no cambia la
  precedencia.
- Una versión publicada no se reutiliza para otro contenido binario, aunque cambie únicamente el
  build metadata.
- La versión que aparece en el APK, en el tag y en la GitHub Release debe coincidir exactamente,
  incluyendo mayúsculas, guiones y metadatos.

Ejemplos válidos:

```text
0.1.0
1.0.0-beta.1
1.0.0-rc.1
1.0.0
1.0.1+android.1
```

Ejemplos inválidos:

```text
1.0
v1.0.0
01.0.0
1.0.0-
1.0.0..rc
```

### `versionCode`

`versionCode` es un entero positivo utilizado por Android para ordenar instalaciones y upgrades.
Es independiente de SemVer y no se calcula a partir de `MAJOR`, `MINOR` o `PATCH`.

Reglas obligatorias:

- Cada candidato de release debe usar un `versionCode` mayor que el de su base; por tanto, cada APK
  público también es mayor que el último APK público.
- El valor no se reutiliza nunca, aunque se retire una release.
- Un cambio de prerelease exige aumentar `versionCode`:

  ```text
  1.0.0-beta.1 -> versionCode 10
  1.0.0-beta.2 -> versionCode 11
  1.0.0-rc.1   -> versionCode 12
  1.0.0        -> versionCode 13
  ```

- Un hotfix también exige un nuevo `versionCode`.
- Si un APK ya fue publicado, cualquier recompilación con contenido diferente necesita una nueva
  versión SemVer y un nuevo `versionCode`.
- El valor se declara junto a `versionName` en la fuente única de versión definida por la fase 6.

## Tags de Git

El tag de release es exactamente `v<versionName>` y es sensible a mayúsculas/minúsculas.

Ejemplos:

```text
versionName = 1.0.0-beta.1  -> tag v1.0.0-beta.1
versionName = 1.0.0-rc.1    -> tag v1.0.0-rc.1
versionName = 1.0.0         -> tag v1.0.0
```

Reglas:

- El tag debe apuntar al commit que contiene la versión que se va a construir.
- El workflow de `main` crea el tag automáticamente después de validar el incremento de versión.
- El workflow de release debe rechazar un tag que no coincida exactamente con `versionName`.
- Un tag utilizado para una release pública no se mueve, borra ni reutiliza.
- Corregir un commit después de crear el tag requiere crear otra versión; no se fuerza el tag
  existente.

## Clasificación de releases

### Prerelease

Una versión con `PRERELEASE` es una prerelease de GitHub. Puede utilizar identificadores como:

```text
1.0.0-alpha.1
1.0.0-beta.1
1.0.0-rc.1
```

Las prereleases pueden contener limitaciones conocidas y sirven para validación pública antes de
la versión estable. El `versionCode` sigue aumentando en cada publicación.

### Release estable

Una versión sin `PRERELEASE` es estable:

```text
1.0.0
1.0.1
```

La promoción de `1.0.0-rc.1` a `1.0.0` siempre genera un nuevo APK y un nuevo `versionCode`; no se
renombra ni se reutiliza el binario del RC.

### Hotfix

Un hotfix compatible incrementa `PATCH`:

```text
1.0.0 -> 1.0.1
```

El hotfix se publica con un nuevo tag, nuevo APK, nuevo checksum y `versionCode` superior. Si el
cambio rompe compatibilidad, se aplica la regla correspondiente de `MINOR` o `MAJOR` de SemVer.

## Artefactos públicos

Cada GitHub Release debe publicar como mínimo:

```text
safecube-<versionName>.apk
safecube-<versionName>.apk.sha256
```

Ejemplo:

```text
safecube-1.0.0-rc.1.apk
safecube-1.0.0-rc.1.apk.sha256
```

El checksum debe corresponder al APK exacto adjunto en la misma release y utilizar SHA-256. El
workflow debe verificar la firma Android antes de publicar el archivo.

El APK público debe ser:

- construido desde el commit del tag;
- firmado con el keystore de release, nunca con la debug key;
- minificado según la configuración de release;
- instalable en la matriz soportada;
- acompañado por un checksum verificable fuera de GitHub Actions.

## Flujo de publicación

1. Elegir la versión objetivo del release train y actualizar `VERSION_NAME` y `VERSION_CODE` en
   `version.properties` en la pull request. Cada integración usa el siguiente sufijo `-rc.N`;
   la promoción estable elimina el sufijo y vuelve a aumentar `VERSION_CODE`.
2. Ejecutar `./gradlew validateVersion` y `validateVersionBump` contra la versión base, y confirmar
   que ambos valores aumentan.
3. Pasar `Pull Request Quality / quality / version-guard` y los demás checks requeridos.
4. Fusionar el cambio en `main`.
5. Dejar que `create-candidate-tag` cree el tag exacto `v<versionName>` sobre el commit fusionado.
6. Revisar el deployment pendiente y aprobar `publish` cuando se autorice distribuir el candidato.
7. Dejar que `publish` valide tag, versión, firma, build y checksum, y cree la GitHub Release como
   prerelease o estable según el sufijo SemVer.
8. Descargar el APK publicado y verificar su checksum y firma como comprobación posterior.

La publicación no debe depender de cambios manuales en el APK después del workflow. Si cualquier
verificación falla, el workflow se detiene antes de publicar.

## Inmutabilidad y retirada

### Inmutabilidad

Después de publicar una versión:

- no se reemplaza el APK;
- no se reemplaza ni regenera el checksum publicado;
- no se mueve, borra ni reutiliza el tag;
- no se reutiliza el `versionCode`;
- no se publica otro contenido bajo el mismo `versionName`.

La corrección de cualquier problema siempre crea una versión nueva. Esto conserva la trazabilidad
entre el APK descargado, su checksum, el tag y el commit fuente.

La metadata descriptiva de GitHub puede recibir una anotación de retirada, pero esa anotación no
debe alterar, sustituir ni ocultar los artefactos originales.

### Retirada de una release

Si se descubre un problema de seguridad, integridad o funcionamiento:

1. Marcar la GitHub Release afectada como retirada y explicar claramente el motivo, el alcance y la
   versión recomendada.
2. Mantener disponibles el tag, APK y checksum originales para preservar evidencia y trazabilidad;
   no sustituirlos.
3. Si existe riesgo activo, comunicar la retirada en la documentación y canales del proyecto.
4. Corregir el problema en un nuevo commit.
5. Publicar una nueva versión con SemVer y `versionCode` superior.
6. Enlazar desde la nueva release la versión retirada y describir la migración o acción requerida.

Una retirada no autoriza a reutilizar la versión afectada ni a disminuir el `versionCode`.

## Contrato público resumido

| Elemento        | Regla                                                 |
|-----------------|-------------------------------------------------------|
| `versionName`   | SemVer 2.0.0 válido                                   |
| `versionCode`   | Entero positivo, monotónico e independiente de SemVer |
| Tag             | `v<versionName>` exacto                               |
| APK             | `safecube-<versionName>.apk`                          |
| Checksum        | `safecube-<versionName>.apk.sha256` con SHA-256       |
| Prerelease      | `versionName` contiene `-PRERELEASE`                  |
| Estable         | `versionName` no contiene `-PRERELEASE`               |
| Canal v1        | GitHub Releases, beta abierta                         |
| Google Play     | Fuera de alcance inicial                              |
| APIs soportadas | Android API 30-36                                     |
| Firma pública   | Keystore privado mediante secrets de release          |

## Fuera de alcance de esta política

- Incrementar versiones automáticamente.
- Generar tags o releases reales.
- Automatizar el changelog.
- Publicar en Google Play.
- Definir telemetría u observabilidad.
