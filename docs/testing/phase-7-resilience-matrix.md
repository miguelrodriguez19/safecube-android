# Phase 7 resilience regression matrix

## Purpose and scope

This is the executable evidence index for `SPEC-HARDENING-V1` and the Phase 7 closure task
`SCDK-M130`. It covers local deterministic tests only. Real-backend E2E, the API 30–36 device
matrix, visual-regression testing and production observability remain outside Phase 7.

The canonical gates remain defined in root Gradle tasks and `docs/testing/testing.md`. This matrix
does not create a second gate list. The pull-request workflow runs the complete `app` instrumented
source set through `:app:pixel2Api30DebugAndroidTest`, so every test added below is discovered
without a class allowlist or secrets.

## Requirement evidence

| Requirement | JVM / static evidence | Instrumented evidence on API 30 | Result |
| --- | --- | --- | --- |
| `FR-AUTH-002` | `core/auth/src/test/java/com/miguelrodriguez19/safecube/core/auth/data/session/AuthTokenRefreshHandlerTest.kt`; `core/network/src/test/java/com/miguelrodriguez19/safecube/core/network/data/auth/TokenRefreshAuthenticatorFlowIntegrationTest.kt`; `app/src/test/java/com/miguelrodriguez19/safecube/app/session/AccountSessionLifecycleImplTest.kt`; `app/src/test/java/com/miguelrodriguez19/safecube/app/presentation/navigation/host/NavigationSessionCoordinatorTest.kt` | `Phase7ResilienceInstrumentedTest.terminalSessionExpirationReplacesProtectedStackAndBackCannotRestoreIt` verifies Login as the safe root, one sanitized explanation and no protected plaintext after Back. | `VERIFIED` |
| `FR-VAULT-002` | `core/vault/src/test/java/com/miguelrodriguez19/safecube/core/vault/domain/usecase/vault/VaultInitializeUseCaseTest.kt`; `VaultInitializeRecoveryUseCaseTest.kt`; `core/vault/src/test/java/com/miguelrodriguez19/safecube/core/vault/data/local/PendingVaultInitializationStoreTest.kt`; MockWebServer coverage in `RemoteVaultKeyMaterialDataSourceIntegrationTest.kt` | `Phase7ResilienceUiStateTest.pendingRecoveryKeyRemainsVisibleUntilExplicitConfirmation` verifies the pending recovery state remains visible and cannot continue before explicit confirmation. | `VERIFIED` |
| `SEC-SESSION-001` | `app/src/test/java/com/miguelrodriguez19/safecube/app/session/autolock/VaultAutoLockCoordinatorTest.kt`; `core/vault/src/test/java/com/miguelrodriguez19/safecube/core/vault/data/session/VaultSessionManagerImplTest.kt` | `Phase7ResilienceInstrumentedTest.immediateAutoLockOnBackgroundRequiresASecondUnlockOnForeground` drives the real Activity lifecycle; `lockNowLocksVaultWithoutEndingAccountSession` proves Lock now preserves the account session. | `VERIFIED` |
| `SEC-SESSION-002` | `QuickUnlockEnvelopeCodecTest.kt`; `QuickUnlockStoreTest.kt`; `QuickUnlockManagerImplTest.kt`; `AndroidKeystoreQuickUnlockAdapterTest.kt`; `UnlockVaultViewModelTest.kt` | `QuickUnlockDeviceCredentialTest` verifies the real device-credential prompt, non-exportable auth-per-use key, cancellation, logout cleanup and a fresh remote process starting Locked. | `VERIFIED` |
| `SEC-CRYPTO-002` | `ChangeVaultPassphraseUseCaseTest.kt` covers fresh preflight, exact ETag, deterministic two-client CAS, one winner, response-lost reconciliation and unchanged dependent data; `RemoteVaultKeyMaterialDataSourceIntegrationTest.kt` and `VaultKeyMaterialOpenApiContractTest.kt` keep HTTP/OpenAPI behavior under MockWebServer/JVM. | UI lock and one-shot lock-reason projection remain covered by `UnlockVaultViewModelTest.kt`; cryptographic and concurrent network scenarios intentionally stay deterministic on JVM. | `VERIFIED` |
| `SEC-PRIVACY-001` | `app/src/test/java/com/miguelrodriguez19/safecube/app/security/SensitiveSourcePolicyTest.kt`; `NetworkClientFactoryIntegrationTest.kt`; `:app:verifyReleaseSecurityManifest`; pending-record codec/store tests. The accepted Phase 9 follow-up removes the currently approved debug-only raw HTTP logger. | `MainActivitySmokeTest.activeWindowRejectsScreenshotsAndRecentsThumbnails` verifies `FLAG_SECURE`; password masking tests cover visible fields. | `VERIFIED` for the approved Phase 7 contract |
| `NFR-RESILIENCE-001` | ViewModel suites for auth, bootstrap, recovery, home and editors cover Idle/InitialLoading/Content/Empty/Mutating/RetryableError/TerminalError and duplicate-event guards. | `Phase7ResilienceUiStateTest.retryableVaultHomeErrorKeepsLocalContentAndOffersRetry` renders Content together with a sanitized retryable 503 and an explicit Retry action. | `VERIFIED` |
| `NFR-RESILIENCE-002` | `NetworkFailureClassifierTest.kt` exhaustively classifies transport, timeout and HTTP statuses; auth/vault/storage/crypto integration suites verify domain mapping without raw bodies. | The Vault Home instrumented scenario verifies the final retryable projection while preserving content. Terminal navigation is covered by the session-expiration scenario. | `VERIFIED` |
| `NFR-LIFECYCLE-001` | `NavigationGatesTest.kt`; `PostLoginGateViewModelTest.kt`; `NavigationSessionCoordinatorTest.kt`; pending-initialization repository/use-case tests. | `QuickUnlockDeviceCredentialTest.activityRecreationKeepsLiveSessionAndRemoteProcessStartsLocked` distinguishes Activity recreation from a fresh process and proves the fresh process has no active KEK. The remote-process probe is used because force-stopping the main package would also kill the instrumentation runner. | `VERIFIED` |
| `FR-SCOPE-001` | `app/src/test/java/com/miguelrodriguez19/safecube/app/security/V1ScopePolicyTest.kt`; `NavigationBackPolicyTest.kt`; automated route/resource placeholder audit. | `MainActivitySmokeTest.cleanLaunchDoesNotExposeDeferredV1Features`; Profile remains a registered but unpackaged and unreachable deferred module by owner decision. | `VERIFIED` |

## Mandatory Phase 7 scenarios

| Scenario | Exact automated evidence |
| --- | --- |
| Session expiration and clean back stack | `app/src/androidTest/java/com/miguelrodriguez19/safecube/Phase7ResilienceInstrumentedTest.kt` → `terminalSessionExpirationReplacesProtectedStackAndBackCannotRestoreIt` |
| Auto-lock on background and later unlock | Same file → `immediateAutoLockOnBackgroundRequiresASecondUnlockOnForeground` |
| Lock now | Same file → `lockNowLocksVaultWithoutEndingAccountSession` |
| Process death / cold start Locked | `app/src/androidTest/java/com/miguelrodriguez19/safecube/QuickUnlockDeviceCredentialTest.kt` → `activityRecreationKeepsLiveSessionAndRemoteProcessStartsLocked` |
| Pending recovery key | `app/src/androidTest/java/com/miguelrodriguez19/safecube/Phase7ResilienceUiStateTest.kt` → `pendingRecoveryKeyRemainsVisibleUntilExplicitConfirmation` |
| `FLAG_SECURE` | `app/src/androidTest/java/com/miguelrodriguez19/safecube/MainActivitySmokeTest.kt` → `activeWindowRejectsScreenshotsAndRecentsThumbnails` |
| Vault Home retains content after retryable error | `Phase7ResilienceUiStateTest.kt` → `retryableVaultHomeErrorKeepsLocalContentAndOffersRetry` |
| Lost HTTP response and reconciliation | `core/vault/src/test/java/com/miguelrodriguez19/safecube/core/vault/domain/usecase/vault/VaultInitializeUseCaseTest.kt` and `ChangeVaultPassphraseUseCaseTest.kt`; remote adapters remain MockWebServer JVM tests. |

## Pull-request integration

- `.github/workflows/pull-request-quality.yml` enables the reusable workflow's instrumented job.
- `.github/workflows/kotlin-ci-reusable.yml` invokes the complete canonical managed-device task
  `:app:pixel2Api30DebugAndroidTest` with a 24-minute internal deadline inside the 30-minute job.
- No secret, backend credential, external account or real payload is needed.
- No test class allowlist exists, so this matrix cannot drift from the source-set contents.

## Exclusions and follow-up

| Exclusion | Reason | Follow-up |
| --- | --- | --- |
| Real-backend E2E | Would require external state and credentials; not a Phase 7 regression primitive. | Phase 10 |
| API 31–36 instrumented matrix | M130 explicitly closes on the existing API 30 managed device. | Release/device-matrix task after Phase 8 |
| Visual regression | Functional semantics are asserted; pixel-level redesign belongs to Phase 8. | Phase 8 |
| Production observability and removal of debug HTTP BODY logging | The debug-only exception remains normative until structured redacted observability replaces it. | Phase 9 / `ADR-0003-SENSITIVE-DATA-SURFACES` |

## Reproduction commands

```bash
./gradlew --no-daemon :app:pixel2Api30DebugAndroidTest \
  -Pandroid.testoptions.manageddevices.emulator.gpu=swiftshader_indirect
./gradlew ciVerify
./gradlew releaseVerify
```

Execution results are recorded in `docs/sdd/agent-reports/SCDK-M130.md`; generated HTML/XML and
managed-device logs remain build artifacts and are not committed.
