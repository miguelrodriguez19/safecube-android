# SafeCube Agent Instructions

## Authority and context

Before changing anything, read this file and the documentation nearest to the affected module.
The normative order is:

1. `APPROVED` specs in `docs/specs` and registered canonical contracts.
2. `ACCEPTED` ADRs in `docs/architecture/adr`.
3. OpenAPI, crypto and storage contracts.
4. Acceptance tests and test standards.
5. Roadmaps.
6. Trello cards and their summaries.
7. Existing implementation.

Trello tracks execution. It is never the only place where a requirement or decision exists.
Historical documents are context only and must not be used as implementation authority when a
newer canonical document exists.

## Required workflow

For every task:

1. Read this file and the relevant SDD manual.
2. Locate the linked spec, ADR and task acceptance criteria.
3. Inspect the current repository state; do not assume the task description matches the code.
4. Separate discovered facts, assumptions and decisions.
5. Present an implementation plan before editing.
6. Implement only the task scope and preserve unrelated user changes.
7. Add or update tests and documentation required by the spec.
8. Run the narrowest relevant checks, then the project quality gates when practical.
9. Update traceability and leave an agent report using the repository template.
10. Tell the parent agent what changed, what was verified and what remains unresolved.

If a requirement is missing, contradictory or security-critical and cannot be resolved from the
repository, stop and ask the human owner. Do not invent product behavior.

## Change boundaries

- Do not change crypto, OpenAPI, storage schemas, sync semantics or security behavior without an
  applicable `ACCEPTED` ADR or an explicit task that creates one.
- Do not broaden a task because an adjacent improvement is attractive; create a follow-up task.
- Do not expose passphrases, recovery keys, KEK/DEK material, tokens, payload contents or sensitive
  request/response data in logs, tests, screenshots or agent reports.
- Do not run destructive commands such as `git reset --hard`, broad deletion or history rewrites.
- Do not commit unless the task explicitly requests a commit.
- Do not create tags, releases, external messages or infrastructure resources unless the task
  explicitly includes that side effect.

## Repository conventions

- Search with `rg` or `rg --files`.
- Use `apply_patch` for tracked file edits.
- Keep business logic in the appropriate module; do not move logic into UI or `app` for convenience.
- Follow Kotlin official style and the existing test naming convention.
- Use Arrange/Act/Assert tests without section comments.
- Prefer deterministic tests and explicit interaction verification.
- Keep user-visible text in resources and preserve English/Spanish parity.
- Link documentation with repository-relative Markdown links.

## Verification and handoff

At minimum, report:

- `status`: `DONE`, `PARTIAL` or `BLOCKED`;
- Trello task ID;
- spec and ADR IDs used;
- changed files and behavior;
- tests and quality gates executed with results;
- assumptions and decisions made;
- risks, gaps and recommended next action.

Use `docs/sdd/agent-report-template.md`. A task is not complete merely because compilation passes:
its acceptance criteria and traceability must also be evidenced.
