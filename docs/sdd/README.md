# Spec-Driven Development en SafeCube

## Propósito

Este sistema hace que la intención del producto, las decisiones técnicas y la implementación sean
trazables. Un agente recibe una tarea acotada, implementa contra una spec aprobada, ejecuta la
verificación y deja evidencia reproducible.

## Tipos de documento

- **Spec:** comportamiento y requisitos que el producto debe cumplir.
- **ADR:** decisión técnica aceptada, sus alternativas y consecuencias.
- **Task card:** unidad de trabajo ejecutable, normalmente reflejada en Trello.
- **Agent report:** evidencia de lo que un agente implementó, verificó y dejó pendiente.
- **Roadmap:** orden y agrupación de trabajo; no sustituye contratos ni specs.

## Jerarquía de autoridad

Cuando dos fuentes discrepan, se aplica este orden:

1. Specs `APPROVED`, `IMPLEMENTING` o `VERIFIED` en `docs/specs`.
2. ADRs `ACCEPTED` en `docs/architecture/adr`.
3. Contratos OpenAPI, crypto y storage registrados como canónicos.
4. Tests de aceptación y estándares de testing.
5. Roadmap.
6. Trello.
7. Implementación existente.

Los documentos bajo `historical` son contexto histórico y nunca una fuente de implementación si
existe un documento canónico posterior.

## Ciclo de vida

```text
DRAFT → REVIEW → APPROVED → IMPLEMENTING → VERIFIED → SUPERSEDED
```

- `DRAFT`: contenido en elaboración; no implementar contra él.
- `REVIEW`: listo para revisión humana y técnica.
- `APPROVED`: fuente normativa para crear tareas.
- `IMPLEMENTING`: existe trabajo activo vinculado a tareas.
- `VERIFIED`: criterios aceptados y evidencia registrada.
- `SUPERSEDED`: reemplazado por una spec posterior; se conserva para trazabilidad.

Una spec aprobada no puede mantener decisiones críticas sin resolver, `TBD` abiertos ni criterios
no verificables.

## Estructura documental

```text
AGENTS.md
docs/
  sdd/
    README.md
    spec-template.md
    spec-registry.md
    task-template.md
    agent-workflow.md
    agent-report-template.md
    agent-reports/
    definition-of-ready-done.md
    traceability-matrix.md
  specs/
    product/
    features/
    contracts/
  architecture/
    adr/
```

Los contratos existentes de `docs/architecture` se registran sin duplicarlos. Las specs futuras
de producto y features viven bajo `docs/specs`.

## Convenciones de IDs

- Specs: `SPEC-<DOMAIN>-<SLUG>`, por ejemplo `SPEC-CRYPTO-V1`.
- ADRs: `ADR-<NUMBER>-<SLUG>`, por ejemplo `ADR-0001-ROOM-STORAGE`.
- Requisitos funcionales: `FR-<DOMAIN>-<NNN>`.
- Requisitos no funcionales: `NFR-<DOMAIN>-<NNN>`.
- Seguridad y privacidad: `SEC-<DOMAIN>-<NNN>`.
- Tasks: identificador real generado por Trello, por ejemplo `SCDK-M91`.

Los IDs de specs, requisitos y ADRs son estables. Los IDs de Trello se generan fuera del
repositorio y se enlazan desde Git.

## Relación con Trello

El requisito original debe existir en Git antes de crear la card. Cada card debe enlazar a:

- una spec;
- un ADR cuando aplique;
- sus acceptance criteria;
- el agent report al completarse.

Trello gestiona estado, prioridad, responsable y progreso. No puede ser la única ubicación de un
requisito o decisión.

## Flujo resumido

1. Crear o actualizar spec.
2. Resolver decisiones con ADR.
3. Revisar y aprobar la spec.
4. Crear una task atómica en Trello.
5. Ejecutar el protocolo de agente.
6. Verificar tests y acceptance criteria.
7. Actualizar registry y matriz de trazabilidad.
8. Dejar el agent report.
