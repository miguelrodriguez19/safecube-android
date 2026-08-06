# SafeCube — Conventional Commits y changelog

SafeCube adopta [Conventional Commits](https://www.conventionalcommits.org/) para que el historial
sea legible y `git-cliff` pueda producir notas de release deterministas. Esta convención no crea
tags, no decide la versión de `version.properties` y no publica releases.

## Formato

En SafeCube, el encabezado obligatorio de un commit tiene esta forma:

```text
<type>(<taskId>): <description>
```

`<taskId>` debe seguir el patrón `SCDK-M[0-9]+`. Los tipos permitidos para el flujo de trabajo son
`feat`, `fix`, `security`, `refactor`, `docs`, `test`, `perf`, `chore`, `build`, `ci` y `revert`.
Las ramas siguen la forma `<type>/<taskId>--<brief-description>`, por ejemplo
`feat/SCDK-M105--Automate-changelog`.

Ejemplos válidos:

```text
feat(SCDK-M105): add deterministic release notes
fix(SCDK-M105): handle a missing previous tag
security(SCDK-M105): pin the changelog binary
```

Un cambio incompatible conserva este encabezado y debe documentarse en el footer con
`BREAKING CHANGE: <reason>`. La descripción debe ser breve, específica y no terminar en punto.

Los tipos habituales son `feat`, `fix`, `security`, `refactor`, `docs`, `test` y `perf`. Los tipos
de mantenimiento `chore`, `build` y `ci` se reservan para trabajo que no cambia el comportamiento
del producto. Los commits puramente mecánicos de dependencias, merges y preparación de release se
omiten de las notas mediante [`cliff.toml`](../../cliff.toml). Los commits antiguos o que no
cumplan la convención no se reescriben: aparecen en `Other` para que puedan detectarse antes de
publicar.

## Generación local

La versión de `git-cliff` usada por el workflow es `2.13.0`. Instalar esa misma versión con un
gestor local y comprobarla antes de generar:

```bash
cargo install git-cliff --version 2.13.0 --locked
git-cliff --version
```

Regenerar el changelog completo:

```bash
./scripts/generate-changelog.sh
```

Generar únicamente las notas entre dos tags existentes, sin crear ni mover ningún tag:

```bash
./scripts/generate-changelog.sh \
  v0.1.7-rc.2 \
  v0.1.7-rc.3 \
  /tmp/safecube-release-notes.md
```

El script exige que el tag anterior sea ancestro del actual y ejecuta `git-cliff --no-exec`. Por
tanto, solo usa la historia local del rango solicitado y no consulta servicios remotos ni ejecuta
comandos definidos en mensajes o configuración.

## Publicación

`Release Train / publish` obtiene el tag anterior alcanzable, genera el rango
`tagAnterior..tagActual` y pasa el archivo resultante a `gh release create --notes-file`. La
creación del tag permanece exclusivamente en `create-candidate-tag`; una generación fallida de
notas detiene la publicación antes de construir o adjuntar artefactos.
