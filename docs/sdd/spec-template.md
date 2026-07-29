# SPEC-<DOMAIN>-<SLUG> — <Título>

## Metadata

| Campo              | Valor                  |
|--------------------|------------------------|
| ID                 | `SPEC-<DOMAIN>-<SLUG>` |
| Estado             | `DRAFT`                |
| Owner              | `<persona o equipo>`   |
| Fecha              | `<YYYY-MM-DD>`         |
| Última revisión    | `<YYYY-MM-DD>`         |
| Reemplaza          | `<SPEC-ID o N/A>`      |
| Dependencias       | `<SPEC/ADR IDs o N/A>` |
| Tasks relacionadas | `<Trello IDs o N/A>`   |

## Problema y contexto

Describe el problema observable, el estado actual y por qué este cambio es necesario.

## Objetivos

- OBJ-001: <resultado que debe conseguirse>

## No objetivos

- <comportamiento explícitamente fuera del alcance>

## Actores y casos de uso

| Actor     | Caso de uso | Resultado esperado |
|-----------|-------------|--------------------|
| `<actor>` | `<acción>`  | `<resultado>`      |

## Requisitos funcionales

- `FR-<DOMAIN>-001`: <requisito observable y verificable>.

## Requisitos no funcionales

- `NFR-<DOMAIN>-001`: <rendimiento, disponibilidad, compatibilidad o mantenibilidad>.

## Interfaces y contratos

Describe APIs, eventos, rutas, formatos, headers, versiones y compatibilidad. Enlaza el contrato
OpenAPI o documento canónico cuando exista.

## Modelo de datos y persistencia

Describe entidades, ownership, source of truth, migraciones, índices, retención y borrado.

## Seguridad, privacidad y zero-knowledge

Describe datos sensibles, límites de confianza, cifrado, logging prohibido, autorización y
consecuencias de fallo.

## Estados, errores, retry e idempotencia

| Estado/errores | Causa     | Comportamiento | Retry                |
|----------------|-----------|----------------|----------------------|
| `<estado>`     | `<causa>` | `<respuesta>`  | `sí/no/condicionado` |

## Observabilidad

### Permitido

- `<evento o atributo no sensible>`

### Prohibido

- `<secreto, payload, identificador sensible o PII>`

## Compatibilidad, migraciones y rollout

Describe versiones compatibles, migraciones, rollback, feature flags y condiciones de rollout.

## Test matrix

| Requisito         | Unit      | Integration | Instrumented/E2E | Manual    |
|-------------------|-----------|-------------|------------------|-----------|
| `FR-<DOMAIN>-001` | `<sí/no>` | `<sí/no>`   | `<sí/no>`        | `<sí/no>` |

## Acceptance Criteria

- [ ] `AC-001`: <resultado verificable>.
- [ ] `AC-002`: <resultado verificable>.

## Trazabilidad

- Código esperado: `<paths o módulos>`.
- Tests esperados: `<paths o nombres>`.
- ADRs: `<ADR IDs>`.
- Tasks: `<Trello IDs>`.
- Evidencia final: `<agent report o link>`.

Una spec no puede pasar a `APPROVED` con placeholders críticos, decisiones pendientes o criterios
que no puedan observarse mediante test, inspección o evidencia manual definida.
