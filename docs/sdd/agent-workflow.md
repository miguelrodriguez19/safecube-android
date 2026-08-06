# Workflow de agentes SafeCube

## Entrada obligatoria

El agente debe recibir o localizar:

- Task ID de Trello.
- Spec aprobada o corrección explícita.
- ADRs aplicables.
- Acceptance Criteria.
- Dependencias y paths afectados.

Si falta un dato que no pueda descubrirse leyendo el repositorio, el agente bloquea la tarea y
formula una pregunta concreta.

## Ciclo de ejecución

### 1. Contexto

Leer `AGENTS.md`, `docs/sdd/README.md`, la task, la spec, los ADRs y el código relevante. Registrar
hechos descubiertos sin mezclarlos con supuestos.

### 2. Plan

Presentar archivos, interfaces, flujo de datos, riesgos y tests antes de editar. El plan debe
respetar el scope de la task y señalar cualquier dependencia no satisfecha.

### 3. Implementación

Aplicar el cambio mínimo que satisface la spec. No corregir problemas laterales en la misma task.
Si aparece un cambio de contrato, detenerse y crear o solicitar un ADR. Los cambios se harán desde
una rama nueva (NO nuevo worktree). Las ramas deberán incluir el ID de la tarea. 

### 4. Verificación

Ejecutar primero tests del módulo afectado, después los gates definidos por la task y finalmente los
quality gates globales cuando el coste sea razonable. No ocultar fallos con suppressions o baselines
sin aprobación explícita.

### 5. Trazabilidad

Actualizar la matriz con código, tests y evidencia. Si el cambio es documental, registrar la
justificación `N/A` para código/runtime.

### 6. Handoff

Crear el informe con `docs/sdd/agent-report-template.md`, indicar el estado real y comunicar al
agente padre la siguiente acción.

## Preguntas que se resuelven explorando

- dónde vive un tipo, contrato o test;
- qué módulos y dependencias existen;
- qué comando usa actualmente el proyecto;
- qué estado tiene el código y qué migraciones existen;
- qué documentos canónicos ya cubren el tema.

## Cuándo bloquearse

Bloquear y pedir decisión humana si:

- dos specs o ADRs aceptados contradicen el comportamiento requerido;
- se necesita decidir un producto no descrito;
- el cambio expone secretos, PII o material criptográfico;
- se requiere modificar un contrato compartido sin ADR;
- una migración destructiva no tiene política de rollback;
- el test o acceptance criteria no puede hacerse verificable.

No bloquear por un fallo técnico aislado sin intentar primero diagnóstico y alternativas seguras.

## Subagentes

- El agente padre asigna un único objetivo y comparte spec, ADR y constraints.
- Un subagente no cambia contratos ni amplía scope.
- El agente padre integra conclusiones y conserva la decisión final.
- Dos agentes no deben editar simultáneamente el mismo contrato o archivo de migración.
- Todo subagente devuelve hechos, cambios propuestos, evidencia y bloqueos.

## Handoff mínimo

El mensaje final debe incluir `DONE`, `PARTIAL` o `BLOCKED`, Task ID, spec/ADR, resumen, archivos,
tests, gates, riesgos, gaps y siguiente acción.
