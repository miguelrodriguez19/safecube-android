# Prevención de secretos

## Propósito

El workflow [`Secret scan`](../../.github/workflows/secret-scan.yml) ejecuta Gitleaks antes de
cualquier workflow de publicación. Analiza el rango de commits relevante en pull requests y pushes a
`main`; las ejecuciones manuales analizan el historial disponible. El check exigible en la protección
de rama es:

```text
Secret scan / gitleaks
```

El checkout conserva el historial completo y Gitleaks se ejecuta con redacción. El workflow no
publica reportes, resúmenes ni comentarios de findings: solo informa la regla y localización que
Gitleaks pueda mostrar redactadas en el log.

## Fixture de control

[`tests/fixtures/gitleaks/allowed-synthetic-secret.txt`](../../tests/fixtures/gitleaks/allowed-synthetic-secret.txt)
contiene el único patrón ficticio versionado para comprobar el scanner. La allowlist de
[`.gitleaks.toml`](../../.gitleaks.toml) exige a la vez esa ruta concreta y ese valor sintético;
no se permite ninguna ruta, regla o valor global.

El workflow ejecuta `scripts/verify-gitleaks-fixture.sh` tras el escaneo normal. El script crea un
segundo patrón sintético temporal fuera del repositorio, verifica que Gitleaks falla y comprueba
que ni la salida ni el reporte temporal contienen el valor sin redactar. Los archivos temporales se
eliminan al terminar.

## Activación manual de GitHub

Un administrador del repositorio debe abrir **Settings → Security → Advanced Security** y, cuando
estén disponibles para el plan y visibilidad del repositorio, activar:

1. **Secret scanning**.
2. **Push protection** para bloquear el push antes de que alcance el historial remoto.

Estas protecciones de GitHub complementan el workflow; no sustituyen el check obligatorio de la
pull request. Si la opción no está disponible, registrar la limitación en la configuración de
seguridad del repositorio y conservar Gitleaks como gate de merge.

## Respuesta ante un finding real

1. Detener el merge y cualquier publicación; tratar el finding como una posible exposición.
2. Revocar o rotar la credencial en su proveedor, sin copiar su valor a issues, logs, comentarios ni
   el agent report. Si era un secreto de release, actualizarlo únicamente en el Environment protegido
   de GitHub.
3. Eliminar el valor del código y sustituirlo por una referencia a un secret gestionado. Crear una
   card de incidente para registrar el alcance, la rotación y el seguimiento.
4. Volver a ejecutar los checks. Un falso positivo solo puede añadirse como allowlist específica,
   revisada y documentada; nunca como exclusión global.

La reescritura de historial y la rotación de una credencial real se gestionan como incidente fuera
del alcance de esta tarea.
