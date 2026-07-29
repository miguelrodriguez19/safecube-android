# Core Network Testing Standard

| Spec ID                 | Status     | Owner     | Last reviewed | Supersedes | Related ADRs |
|-------------------------|------------|-----------|---------------|------------|--------------|
| `SPEC-TESTING-STANDARD` | `APPROVED` | `quality` | `2026-07-29`  | `N/A`      | `N/A`        |

## Scope

This standard applies to all unit tests.

## Test Types

- Unit tests: validate one class in isolation, using MockK for collaborators.
- Integration tests: validate HTTP behavior with `MockWebServer` and real OkHttp/Retrofit wiring.

## Naming

Use `function_whenCondition_thenExpectedResult` semantics in backtick test names.

Examples:

- `authenticate retries request with refreshed token when refresh succeeds`
- `createOkHttpClient keeps provided auth components`

## Structure

Follow Arrange / Act / Assert in this order:

1. Arrange: test inputs, mocks, expected collaborators.
2. Act: execute one public method.
3. Assert: state + output + interaction verification.

> Never add comments dividing sections, the code must be declarative enough

## General Tests Rules

- The test subject variable must be called `target`
- Define just one time the collaborators. As fields.
- Data must not be fixed. E.g.:
    - Avoid the use of `UUID.fromString(...)`, use instead
      `UUID.randomUUID()`
    - Avoid the use of `Instant.parse(...)`, use instead
      `Instant.now()`, play adding or subtracting days or other chrono units.

## Unit Tests Rules

- File must end with `Test.kt`.
- Use MockK for interfaces/ports and side-effect collaborators.
- Must be self-contained
- Getter methods or similar are not mocked; that object must know how to resolve them. E.g.
  `AuthResult.email()` shouldn't be stubbed.
- Prefer strict verification (`verify(exactly = N)`, `coVerify(exactly = N)`).
- Assert non-interactions for guarded paths (for example, non-401 auth responses).

## Integration Tests Rules

- File must end with `IntegrationTest.kt`.
- Use `MockWebServer` for endpoint and retry flow assertions.
- Assert request path, headers, and payload when relevant.
- Keep integration tests deterministic and single-scenario.
