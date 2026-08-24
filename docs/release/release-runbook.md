# SafeCube — Runbook de release

## Objetivo

Este runbook permite validar, publicar, comprobar y retirar una release de SafeCube desde una
sesión nueva. Complementa la [política de releases](release-policy.md), que es el contrato
normativo. No contiene secretos ni sustituye las protecciones configuradas manualmente en GitHub.

El modelo actual es un release train:

- toda pull request a `main` incrementa `VERSION_NAME` y `VERSION_CODE` y supera los checks;
- cada merge a `main` ejecuta `Release Train`, valida el incremento y crea automáticamente el tag
  inmutable `v<VERSION_NAME>`;
- `Release Train / publish` siempre existe, usa el Environment `release` y espera aprobación antes
  de acceder a la firma y publicar;
- `workflow_dispatch` de `Release Train` es una recuperación publicable, no un dry-run.

## Roles y herramientas

Se necesita una persona con permisos de mantenimiento para preparar la versión y otra persona
autorizada para revisar el deployment cuando el equipo lo permita. Para las comprobaciones locales:

- Git y GitHub CLI (`gh`);
- JDK 21 y Android SDK;
- `apksigner` de Android Build Tools;
- `adb` y un dispositivo limpio con Android API 30-36;
- `sha256sum` en Linux o `shasum` en macOS;
- `git-cliff` v2.13.0 para regenerar notas localmente;
- `jq` y una versión reciente de GitHub CLI con `gh attestation`.

## Configuración inicial manual en GitHub

Estas operaciones no son versionables. Deben revisarse después de cambios de plan, ownership o
permisos de GitHub.

### Environment `release`

En **Settings → Environments**, crear `release` y configurar:

1. Required reviewers: seleccionar los responsables de autorizar publicaciones.
2. Activar **Prevent self-review** cuando haya otra persona que pueda aprobar.
3. Limitar las deployment branches a `main`. Ambos workflows protegidos se despachan desde
   `main`; el checkout posterior de un tag no cambia la ref del deployment.
4. No permitir bypass administrativo como operación rutinaria.
5. Crear exclusivamente estos secrets del Environment:

   ```text
   SAFECUBE_RELEASE_KEYSTORE_BASE64
   SAFECUBE_RELEASE_STORE_PASSWORD
   SAFECUBE_RELEASE_KEY_ALIAS
   SAFECUBE_RELEASE_KEY_PASSWORD
   ```

Los secrets no se crean con valores aleatorios independientes: representan el keystore real, su
password, el alias real y el password de la clave. Si `keytool` no solicitó un password de clave
distinto, `SAFECUBE_RELEASE_KEY_PASSWORD` tiene el mismo valor que
`SAFECUBE_RELEASE_STORE_PASSWORD`.

Para generar el Base64 sin saltos de línea en macOS:

```bash
base64 -i /ruta/privada/safecube-release.jks | tr -d '\n'
```

Pegar el resultado únicamente como valor de `SAFECUBE_RELEASE_KEYSTORE_BASE64`; no guardarlo en el
repositorio, documentación, logs ni historial del shell.

Los secrets de un Environment protegido no están disponibles para el job hasta que se aprueba el
deployment. GitHub documenta los reviewers, la prevención de autoaprobación y las reglas de rama en
[Deployments and environments](https://docs.github.com/en/actions/reference/workflows-and-actions/deployments-and-environments).

### Protección de `main`

En **Settings → Rules → Rulesets** o **Branches**, exigir:

1. Pull request antes de merge; bloquear pushes directos.
2. Al menos una aprobación, descartar aprobaciones obsoletas y resolver conversaciones.
3. Rama actualizada antes del merge.
4. Bloqueo de force-push y borrado de `main`.
5. Aplicación a administradores y bypass de emergencia nominal y auditado.
6. Estos required checks, con sus nombres exactos:

   ```text
   Pull Request Quality / quality / version-guard
   Pull Request Quality / quality / verify
   Pull Request Quality / quality / instrumented-smoke
   Dependency Review / dependency-review
   CodeQL / Analyze (java-kotlin)
   Secret scan / gitleaks
   ```

Crear además un ruleset de tags para `refs/tags/v*` que impida actualizaciones y borrados. No se
debe restringir la creación de forma que bloquee `Release Train / create-candidate-tag`; si se usa
una lista de bypass, limitarla al actor de GitHub Actions necesario para esa creación y probarla con
un candidato real. El script del workflow vuelve a verificar tipo y SHA, pero el ruleset añade una
barrera del servidor frente a movimientos o borrados manuales.

### Controles de seguridad

En **Settings → Security → Advanced Security**:

1. habilitar **Dependency graph**;
2. habilitar **Dependabot alerts** y **Dependabot version updates**;
3. habilitar **Secret scanning** y **Push protection** si están disponibles para el repositorio;
4. restringir los bypass de Push Protection y revisar cada excepción.

La disponibilidad depende de la visibilidad y el plan del repositorio. El procedimiento vigente de
GitHub está en [Enabling push protection for your repository](https://docs.github.com/en/code-security/secret-scanning/enabling-secret-scanning-features/enabling-push-protection-for-your-repository).

Gitleaks sigue siendo un required check aunque GitHub Secret Scanning esté activo: cubren patrones
y momentos diferentes. Ante un hallazgo se sigue el
[runbook de respuesta a secretos](../security/secret-scanning.md).

## Custodia, backup y recuperación del keystore

El keystore de release es la identidad de actualización de la aplicación. Perderlo o reemplazarlo
puede impedir que una APK nueva actualice instalaciones existentes.

### Backup inicial

1. Mantener el original fuera del repositorio y de carpetas sincronizadas sin cifrado.
2. Crear al menos dos copias cifradas e independientes, por ejemplo una imagen de disco cifrada en
   un almacenamiento local controlado y otra copia offline en una ubicación distinta.
3. Guardar las credenciales en un password manager distinto de los soportes del keystore.
4. Registrar fuera del repositorio el alias, la fecha de expiración y los fingerprints del
   certificado obtenidos interactivamente:

   ```bash
   keytool -list -v -keystore /ruta/privada/safecube-release.jks
   ```

5. Probar cada backup: restaurarlo en un directorio temporal, ejecutar `keytool -list -v` y
   comprobar que los fingerprints coinciden. Borrar después la copia temporal.
6. Repetir la prueba de restauración periódicamente y antes de cualquier cambio de custodia.

### Recuperación

1. Detener publicaciones y no generar una clave nueva.
2. Restaurar una copia cifrada en una máquina controlada.
3. Comparar alias y fingerprints con el registro de custodia.
4. Regenerar solo el Base64 del mismo keystore y actualizar el secret correspondiente.
5. Validar la identidad recuperada en la siguiente RC real antes de reanudar el train habitual.

Si ninguna copia válida existe o hay sospecha de exposición, tratarlo como incidente de firma. No
rotar ni publicar hasta definir un plan compatible con las instalaciones existentes.

## Validación operativa de una RC real

SafeCube no mantiene un camino de build firmado que termine antes de publicar. La validación
operativa se realiza sobre una RC real del release train: el merge crea el tag y la aprobación del
Environment autoriza el build, las verificaciones y la GitHub prerelease. Esto evita duplicar un
segundo workflow que podría divergir del proceso realmente publicable.

### Revisar las notas antes de aprobar `publish`

El workflow instala `git-cliff` v2.13.0 desde el release oficial y verifica el SHA-256 del tarball
antes de usarlo. `publish` resuelve el tag anterior alcanzable, genera únicamente el rango
`tagAnterior..tagActual` y detiene el job si el rango no es válido o el binario no coincide. No crea,
mueve ni borra tags.

La misma revisión puede hacerse localmente antes de aprobar el deployment:

```bash
./scripts/generate-changelog.sh \
  v0.1.7-rc.2 \
  v0.1.7-rc.3 \
  /tmp/safecube-release-notes.md
less /tmp/safecube-release-notes.md
```

Confirmar que breaking changes, Features, Fixes y Security aparecen separadas y que cualquier
commit no convencional queda bajo `Other`. Si las notas no son correctas, no aprobar `publish`:
corregir la historia en una nueva pull request o abrir una tarea de mantenimiento. No editar la
GitHub Release después de publicarla como forma de corregir el proceso.

### Ejecutar y observar

1. Anotar el SHA fusionado, `VERSION_NAME` y `VERSION_CODE`.
2. Abrir **Actions → Release Train** y comprobar que `create-candidate-tag` termina en verde.
3. Confirmar que `v<VERSION_NAME>` apunta exactamente al SHA fusionado.
4. Revisar la ejecución y aprobar `Release Train / publish` en el Environment `release` solo cuando
   se quiera distribuir esa RC.
5. Confirmar que pasan el build firmado, `apksigner`, SHA-256, generación/verificación CycloneDX,
   subida del workflow artifact y creación de la GitHub prerelease.
6. Descargar los assets públicos `safecube-<VERSION_NAME>.apk` y
   `safecube-<VERSION_NAME>.apk.sha256` y `safecube-<VERSION_NAME>.cdx.json` desde la release.
7. Revisar el job posterior `attest-provenance`. Un fallo queda visible pero no invalida la release
   base porque la attestation es best-effort.

El artifact de la ejecución puede descargarse adicionalmente con GitHub CLI:

```bash
gh run download <RUN_ID> \
  --name "safecube-<VERSION_NAME>" \
  --dir /ruta/temporal/safecube-release
```

### Verificar checksum y firma después de publicar

Desde el directorio descargado:

```bash
# Linux
sha256sum --check "safecube-<VERSION_NAME>.apk.sha256"

# macOS
shasum -a 256 --check "safecube-<VERSION_NAME>.apk.sha256"
```

Localizar `apksigner` en la versión de Build Tools instalada y ejecutar:

```bash
"$ANDROID_HOME/build-tools/<BUILD_TOOLS_VERSION>/apksigner" \
  verify --verbose --print-certs "safecube-<VERSION_NAME>.apk"
```

La verificación debe terminar correctamente y el fingerprint del certificado debe coincidir con
el registro de custodia. El certificado es información pública; el keystore y sus passwords no lo
son. La referencia del comando y sus verificaciones está en
[`apksigner`](https://developer.android.com/tools/apksigner).

### Verificar SBOM y provenance

Comprobar primero que el SBOM publicado corresponde a la versión y tiene estructura CycloneDX:

```bash
jq -e --arg version "<VERSION_NAME>" '
  .bomFormat == "CycloneDX" and
  .specVersion == "1.6" and
  .metadata.component.name == "safecube-android" and
  .metadata.component.version == $version and
  (.components | type == "array") and
  (.dependencies | type == "array")
' "safecube-<VERSION_NAME>.cdx.json"
```

La comprobación completa que utiliza el workflow puede repetirse desde un checkout del tag:

```bash
scripts/verify-release-sbom.sh \
  "safecube-<VERSION_NAME>.cdx.json" \
  "<VERSION_NAME>"
```

Si `attest-provenance` terminó correctamente, verificar el APK descargado contra este repositorio:

```bash
gh attestation verify \
  "safecube-<VERSION_NAME>.apk" \
  -R miguelrodriguez19/safecube-android
```

El digest mostrado debe coincidir con el APK validado por el archivo `.sha256`. La provenance
complementa la firma Android: no sustituye `apksigner`, no firma el APK para Android y no demuestra
por sí sola un nivel SLSA.

Si GitHub indica que attestations no están soportadas por el plan, visibilidad o hosting, registrar
el resultado del job y continuar usando APK, firma Android, checksum y SBOM. No reejecutar ni
reemplazar una release solo para obtener la attestation. En repositorios públicos GitHub.com la
funcionalidad está disponible normalmente; repositorios privados/internos pueden requerir
Enterprise Cloud y GitHub Enterprise Server no la soporta.

### Instalar y arrancar en un dispositivo limpio

Usar un emulador recién creado o un dispositivo de pruebas sin datos que conservar, dentro de API
30-36. Una instalación debug desde Android Studio utiliza otra firma aunque tenga el mismo package
name; hay que desinstalarla antes. El siguiente comando elimina también los datos locales:

```bash
adb uninstall com.miguelrodriguez19.safecube || true
adb install "safecube-<VERSION_NAME>.apk"
adb shell am start -n \
  com.miguelrodriguez19.safecube/.app.entrypoint.MainActivity
```

Confirmar manualmente que SafeCube arranca y completa Welcome → Login. No usar `adb install -r`
para esta prueba limpia. Un error de actualización por firmas incompatibles no indica que los
package names difieran: suele significar que la instalación anterior era debug o estaba firmada
con otra clave.

### Registrar la evidencia

Añadir al informe de SCDK-M104 o al registro operativo equivalente:

| Campo | Evidencia no sensible |
| --- | --- |
| Run ID y SHA | `<run-id>` / `<sha>` |
| Reviewer y fecha | `<usuario>` / `<UTC>` |
| Tag y GitHub Release | `v<versión>` / prerelease |
| Artifact | `safecube-<versión>` |
| Checksum | `PASS` |
| Firma/fingerprint esperado | `PASS` sin passwords ni keystore |
| CycloneDX SBOM | `PASS`, versión y estructura correctas |
| Provenance | `PASS` o `UNAVAILABLE` con motivo no sensible |
| Dispositivo/API | `<modelo>` / `<API>` |
| Arranque Welcome → Login | `PASS` |
| Tag apunta al SHA fusionado | `PASS` |

Nunca copiar valores de secrets, Base64, passwords, rutas privadas ni el keystore al registro.

## Flujo normal de candidato

1. Partir de `main` y elegir el siguiente candidato del train, normalmente `X.Y.Z-rc.N`.
2. Incrementar en la misma PR `VERSION_NAME` y el contador Android `VERSION_CODE` en
   [`version.properties`](../../version.properties).
3. Ejecutar como mínimo:

   ```bash
   ./gradlew ciVerify
   ```

4. Abrir la PR y esperar todos los required checks y reviews.
5. Hacer merge. No crear el tag manualmente: `Release Train / create-candidate-tag` crea
   `v<VERSION_NAME>` sobre el SHA fusionado.
6. Revisar el candidato y aprobar `Release Train / publish` en el Environment `release` cuando se
   quiera distribuir esa RC.
7. Confirmar build, firma, checksum, SBOM y creación de la GitHub prerelease; revisar por separado
   el resultado best-effort de provenance.
8. Descargar los tres assets públicos y repetir checksum, firma, SBOM e instalación limpia.

Se pueden acumular candidatos pendientes de aprobación. Cada uno conserva su tag y su ejecución;
no se mueve un tag para incluir cambios posteriores.

## Release estable

La estable se construye como una versión nueva, no como un renombrado de una RC:

1. Abrir una PR que cambie `X.Y.Z-rc.N` a `X.Y.Z` y vuelva a incrementar `VERSION_CODE`.
2. Pasar los mismos checks y hacer merge.
3. El train crea `vX.Y.Z`; tras aprobar `publish`, GitHub la publica sin marca de prerelease.
4. Verificar assets e instalación, incluida una actualización desde la RC anterior firmada con la
   misma identidad cuando esa ruta sea relevante.

## Hotfix

1. Crear la corrección desde el `main` actual; no modificar el tag afectado.
2. Elegir una versión SemVer superior acorde al alcance, incrementar siempre `VERSION_CODE` y pasar
   una PR completa.
3. Publicar mediante el mismo release train.
4. Enlazar la versión afectada en las notas y explicar la actualización requerida.

Un hotfix no autoriza bypass de checks, reutilización de versión ni sustitución de assets.

## Fallos y recuperación

| Momento | Acción segura |
| --- | --- |
| Antes del merge | Corregir la misma PR; todavía no existe identidad pública. |
| Tag creado, `publish` pendiente | Inspeccionar el SHA. Aprobar solo si es el candidato correcto; si no, preparar una versión nueva sin mover ni borrar el tag. |
| Error de secrets, runner o red antes de crear la release | Corregir la configuración y reejecutar el mismo run solo si el tag sigue apuntando al mismo SHA y no existe release. |
| Error reproducible de build, firma, SBOM o app después del tag | Corregir mediante otra PR con `VERSION_NAME` y `VERSION_CODE` nuevos. |
| GitHub Release creada total o parcialmente | No reemplazar assets ni reusar el tag. Tratarlo como incidente, marcar la metadata como retirada y publicar una versión nueva. |
| Fallo de `attest-provenance` después de publicar | Conservar la release; registrar si fue plan, servicio o configuración y corregir en una card posterior. No sobrescribir assets. |
| Ejecución automática perdida para el `main` actual | Usar `workflow_dispatch` de **Release Train** sabiendo que sí crea/verifica el tag y sí publica tras aprobación. |

Nunca forzar, mover ni borrar el tag para “arreglar” una ejecución. Nunca sobrescribir APK o
checksum. Una reejecución solo es válida para el mismo SHA y antes de que exista la GitHub Release.

## Retirada

1. Detener la recomendación de descarga y evaluar si el problema exige aviso de seguridad.
2. Anotar la metadata de la GitHub Release como retirada, con motivo, alcance y versión segura.
3. Mantener tag, APK y checksum originales como evidencia; no ocultarlos ni sustituirlos.
4. Corregir en una versión SemVer nueva con `VERSION_CODE` mayor.
5. Publicar por el train normal y enlazar ambas versiones.

La retirada no es rollback automático y no reduce la versión instalada en dispositivos.

## Cierre de la validación

SCDK-M104 puede marcarse `DONE` cuando el runbook sea operable desde una sesión nueva y una RC real
haya completado el Environment protegido, build firmado, publicación inmutable, checksum, firma,
instalación y arranque en una API soportada. Si una RC anterior ya aporta esa evidencia, debe
registrarse su run ID, tag y SHA; no se crea otra publicación únicamente para cerrar la card.
