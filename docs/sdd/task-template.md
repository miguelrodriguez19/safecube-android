# <Trello Task ID> — <Título>

## Metadata

| Campo        | Valor                   |
|--------------|-------------------------|
| Task ID      | `<SCDK-Mxx>`            |
| Spec         | `<SPEC-ID>`             |
| ADR          | `<ADR-ID o N/A>`        |
| Estado       | `TODO`                  |
| Owner/agente | `<nombre>`              |
| Dependencias | `<Task/Spec IDs o N/A>` |

## Main Story

Como `<actor>`, quiero `<acción>`, para `<resultado>`.

## Context and Goal

Resume el contexto de la spec y el resultado único que debe producir esta task.

## Precondiciones

- `<spec aprobada, dependencia o estado requerido>`

## In Scope

- `<cambio concreto>`

## Out of Scope

- `<cambio explícitamente excluido>`

## Archivos y módulos esperados

- `<path o módulo>`

## Interfaces afectadas

- `<API, tipo, schema, ruta o N/A>`

## Casos de error

| Caso     | Comportamiento esperado |
|----------|-------------------------|
| `<caso>` | `<resultado>`           |

## Tests requeridos

- `<test unitario/integración/instrumentado/manual>`

## Acceptance Criteria

- [ ] `AC-001`: `<criterio observable>`.
- [ ] `AC-002`: `<criterio observable>`.

## Evidencia requerida

- Comandos ejecutados y resultado.
- Paths de tests o artefactos.
- Agent report enlazado al cerrar.

## Bloqueos

El agente debe detenerse si falta una spec/ADR, hay una contradicción de contrato o la decisión
requiere autoridad no incluida en la task. Debe registrar la pregunta exacta y no implementar un
fallback inventado.

## Riesgo de regresión

`<alto/medio/bajo>` — `<explicación>`.

## Rollback

`<acción reversible o N/A>`.
