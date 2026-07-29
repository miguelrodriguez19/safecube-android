# Architecture Decision Records

Los ADRs registran decisiones técnicas con impacto duradero. Una spec describe qué debe hacer el
sistema; un ADR explica por qué se eligió una solución concreta.

## Cuándo es obligatorio un ADR

Crear o actualizar un ADR para decisiones sobre:

- crypto, key management o zero-knowledge;
- almacenamiento, schema o migraciones;
- contratos de red, OpenAPI o compatibilidad backend;
- seguridad, privacidad u observabilidad sensible;
- límites entre módulos o cambios de arquitectura;
- decisiones con rollback difícil o consecuencias irreversibles;
- trade-offs que un agente futuro no pueda inferir con seguridad.

No es necesario crear un ADR para una refactorización interna que preserve contratos y comportamiento.

## Estados

- `PROPOSED`: en debate; no es normativa.
- `ACCEPTED`: decisión vigente.
- `REJECTED`: propuesta descartada; se conserva el razonamiento.
- `SUPERSEDED`: reemplazada por otro ADR enlazado.

Un ADR `ACCEPTED` no se edita para cambiar la decisión. Se crea un ADR nuevo que enlaza al anterior,
explica el cambio y lo marca como `SUPERSEDED`.

## Convención

```text
ADR-<NUMBER>-<SLUG>.md
```

Usar números secuenciales, títulos descriptivos y enlaces a specs, tareas y ADRs relacionados.
La plantilla normativa es [`ADR-TEMPLATE.md`](./ADR-TEMPLATE.md).
