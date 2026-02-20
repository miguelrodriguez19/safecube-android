# Storage Decision: Local Persistence Strategy

- Status: Accepted
- Date: 2026-02-20
- Scope: Android app local persistence layer

## Context

The project needs an offline-first local persistence strategy that is stable, testable, and well integrated with the Android ecosystem.

Key constraints:
- Native Android support and long-term maintenance.
- Strong schema management with versioned migrations.
- Good integration with Kotlin coroutines and reactive streams.
- Clean dependency injection setup for modular architecture.

## Decision

We will use **Room** as the local database solution and **Dagger Hilt** for dependency injection.

This means:
- Room is the single source of truth for structured local data.
- DAO APIs will expose `Flow` for reactive reads and `suspend` functions for writes.
- Database creation, DAO provisioning, and repository wiring will be provided through Hilt modules.

## Why this option

- Room is the Android standard for local SQL persistence and is production-proven.
- It provides compile-time query validation and explicit migration support.
- It naturally supports offline-first patterns with local-first reads.
- Hilt reduces boilerplate and provides consistent graph management across modules.

## Alternatives considered

- Raw SQLite: flexible but high boilerplate and higher maintenance cost.
- DataStore only: good for key-value/preferences, not enough for relational/complex data.
- SQLDelight: strong option, but Room gives a simpler path with Android-native conventions for this project phase.

## Consequences

Positive:
- Predictable local persistence architecture.
- Better testability with injectable DB/DAO/repositories.
- Easier onboarding for Android developers.

Tradeoffs:
- Need to manage schema migrations carefully.
- Room annotations and generated code add build-time processing cost.

## Implementation guidelines

- Define entities in data modules with explicit table names.
- Keep DAO interfaces focused and transaction-safe.
- Introduce repository interfaces in domain-facing modules; implementations use DAO.
- Configure Hilt modules for:
  - Room database singleton
  - DAO providers
  - Repository bindings
- Add migration policy:
  - No destructive migrations in production builds.
  - Migration tests required when schema version changes.

## Initial module placement

- `core:storage`: Room database, entities, DAO, migrations, repository implementations.
- `feature:*`: consume repositories through use cases/view models, never direct SQL access.

