# Definition of Ready / Definition of Done

## Definition of Ready

Una task está lista para asignar cuando:

- existe una spec `APPROVED` o una corrección claramente delimitada;
- el objetivo y los no objetivos están escritos;
- los Acceptance Criteria son observables;
- las dependencias están identificadas y disponibles;
- el impacto en seguridad, storage, red, migraciones y compatibilidad está evaluado;
- existe un plan de tests;
- las decisiones críticas tienen ADR `ACCEPTED`;
- la card tiene un único resultado principal;
- los paths y módulos esperados están identificados;
- la evidencia que se debe entregar está definida.

## Definition of Done

Una task está terminada cuando:

- el scope se ha implementado completamente;
- los tests nuevos y existentes aplicables pasan;
- los Acceptance Criteria tienen evidencia;
- la documentación y matriz de trazabilidad están actualizadas;
- no se han introducido secretos ni datos sensibles en logs, tests o informes;
- pasan los quality gates aplicables;
- no se ha ampliado el scope sin una nueva task;
- el agent report está completo;
- los cambios de contrato tienen ADR y spec actualizados;
- no quedan decisiones ocultas para el siguiente agente;
- el estado de la task y de la spec se puede determinar desde Git/Trello.

## Excepciones

Una task puede cerrarse como `PARTIAL` solo si la parte incompleta está documentada, no deja el
producto en un estado engañoso y existe follow-up explícito. `BLOCKED` requiere una causa externa o
una decisión humana pendiente, no solo dificultad técnica.
