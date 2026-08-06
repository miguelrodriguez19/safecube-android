# Changelog

All notable changes to SafeCube are generated from the Git history using
[Conventional Commits](https://www.conventionalcommits.org/).
## [Unreleased]

### Documentation

- *(SCDK-M105)* Document changelog conventions (`34a1fdf`)

- *(SCDK-M105)* Update release documentation (`350edd6`)

- *(SCDK-M105)* Update release traceability (`c3b2f89`)


### Maintenance

- *(SCDK-M104)* Run dependabot exclusively twice a month (`2953b54`)

- *(SCDK-M104)* Create release's runbook (`c0412d9`)

- *(SCDK-M106)* Add CycloneDX SBOM and provenance attestation (`75486ca`)

- *(SCDK-M106)* Removed verify-release-sbom-test (`be8436c`)

- *(SCDK-M105)* Add deterministic changelog generation (`b8106fc`)

- *(SCDK-M105)* Integrate generated release notes (`e113916`)

## [0.1.7-rc.2] - 2026-08-04

### Features

- *(core:network)* Implement base network client with Retrofit and OkHttp (`a1c244e`)

- *(core:crypto)* Define core cryptography interfaces and data models (`4a283b9`)

- *(app)* Implement multi-module navigation and dependency injection with Hilt (`e9e1319`)

- *(core:auth)* Implement SessionManager and session state tracking (`476f92e`)

- *(core:auth)* Define AuthRepository interface (`ca1ec98`)

- *(core:auth)* Define AuthRepository interface (`65acf75`)

- *(core:auth)* Implement TokenProvider to decouple session and network modules (`fe2ab95`)

- *(core:storage)* Integrate Room database and define initial schema (`032cb27`)

- *(core:crypto)* Implement FakeCryptoEngine and configure Hilt bindings (`618421b`)

- *(core:crypto)* Introduce Vault State abstraction and Post-Login Gate (`63ab31c`)

- *(core:network)* Integrate OpenAPI generator and define backend API spec (`257840f`)

- *(docs:roadmap)* Update Phase 2 roadmap for OpenAPI Generator integration (`b4c86e0`)

- *(core:network)* Integrate OpenAPI auth contract and implement authenticator skeleton (`c6454b9`)

- *(core:auth)* Implement `AuthErrorMapper` and domain error models (`8981454`)

- *(core:auth)* Implement RemoteAuthDataSource and NetworkResult (`358b4c2`)

- *(SC-DK_M-28)* Implement AuthRepository and data models (`fe67540`)

- *(SC-DK_M-29)* Enhance TokenStorage with issuedAt tracking and Hilt integration (`a867ffc`)

- *(SC-DK_M-31)* Implement token refresh logic and `TokenRefreshAuthenticator` (`f84e145`)

- *(feature:auth)* Implement Login and Signup logic with ViewModel and UI state (`d8793da`)

- *(SC-DK_B-34)* Implement centralized session-based navigation routing (`c39bc1f`)

- Update navigation flows and enhance network serialization (`e86eeef`)

- Enhance `FolderTreeToFile` utility and update package structure documentation (`055d13b`)

- *(SCDK-M40)* Implement Argon2 KDF engine and SaltGenerator (`fe08c06`)

- *(SCDK-M41)* Implement `AesGcmCryptoEngine` and update crypto models (`2883051`)

- *(SCDK-M43)* Introduce `:core:vault` module and remote data source (`b283202`)

- *(SCDK-M45)* Implement `VaultInitializeUseCase` and `VaultKeyMaterialDataSource` (`47850f8`)

- *(SCDK-M52)* Reorganize module structure and introduce repository pattern (`3f6476f`)

- *(SCDK-M47)* Implement `VaultSessionManagerImpl` and integrate with `VaultUnlocker` (`70fcea0`)

- *(SCDK-M48)* Implement vault state refreshing and refactor vault feature with MVI pattern (`07420aa`)

- *(SCDK-M54)* Reorganize `:core:network` module and improve architectural layering (`8c83bf4`)

- *(SCDK-M58)* Implement custom Base64 serialization for generated OpenAPI models (`5967561`)

- *(SCDK-M59)* Refine vault state resolution and navigation gate logic (`1fcf590`)

- *(SCDK-M60)* Include `accountId` in vault key material and refresh cache after initialization (`437dc5e`)

- *(SCDK-M62)* Implement secure item domain models and JSON codec (`558dd4f`)

- *(SCDK-M63)* Refactor `SecureItemEntity` and implement Room database migration v1 to v2 (`08cb874`)

- *(SCDK-M64)* Implement local storage and repository for secure items (`ee83bfb`)

- *(SCDK-M65)* Implement `SecureItemCryptoService` and payload v1 envelope (`9b5bfb4`)

- *(SCDK-M66)* Implement secure item CRUD logic and refactor use case structure (`ff2e4ca`)

- *(SCDK-M67)* Implement vault home screen with secure item CRUD operations (`9e7cdb9`)

- *(SCDK_M68)* Refactor vault item management into dedicated editor screens (`11ab493`)

- *(SCDK_M72)* Implement synchronization metadata and per-account checkpoints (`8bf5d6f`)

- *(SCDK-M73)* Implement remote secure item data source and repository (`84904c2`)

- *(SCDK_M74)* Enhance `SecureItem` synchronization and persistence layer (`a713f79`)

- *(SCDK_M75)* Implement `PullVaultDeltaUseCase` for incremental synchronization (`40dce11`)

- *(SCDK_M76)* Implement incremental vault synchronization push strategy (`25e4c71`)

- *(SCDK_M77)* Implement `VaultSyncUseCase` for coordinated push and pull synchronization (`7ca7dc0`)

- *(SCDK_M81)* Implement opportunistic sync trigger on local mutations (`9a23798`)

- *(SCDK_M78)* Implement synchronization UI and status tracking (`bb4fa5d`)

- *(SCDK-M82)* Implement secure item draft storage for local proposals (`dc7db2e`)

- *(SCDK_M83)* Implement secure item draft policy and conflict resolution use cases (`c14633d`)

- *(SCDK-M84)* Implement secure item draft management in mobile UI (`53dceb4`)

- *(SCDK-M88)* Implement targeted opportunistic synchronization for vault items (`1c76703`)

- *(SCDK-M90)* Implement revision-based vault synchronization (`ef5c500`)

- *(SCDK-M90)* Expose vault draft conflict resolution (`9cc1036`)

- *(SCDK-M90)* Add transactional local vault cleanup (`de2a57e`)

- *(SCDK-M90)* Warn before discarding local drafts (`295f020`)

- *(SCDK-M92)* Implement single source of truth for application versioning (`fbb362a`)

- *(SCDK-M93)* Implement secure release signing configuration via environment variables (`02d9e29`)

- *(SCDK-M94)* Resolve lint findings for strings resources (`f868951`)

- *(SCDK-M94)* Removed redundant label in AndroidManifest.xml. (`f0d3c79`)

- *(SCDK-M95)* Create gradle task "ciVerify" (`7d8642c`)

- *(SCDK-M96)* Create instrumented smoke test to verify the app launch (`fe4c62c`)


### Fixes

- *(SCDK-M85)* Upgrade database to version 6 and migrate timestamps to nanosecond precision (`1005c65`)

- *(SCDK-M86)* Implement shared identity resolution for incremental sync (`d862b87`)

- *(SCDK-M90)* Handle vault protocol validation failures (`4c8b0d5`)


### Security

- *(core:auth)* Implement secure token storage using EncryptedSharedPreferences (`017499b`)

- *(roadmap)* Add Phase 3 roadmap for Crypto and Vault Unlock implementation (`b0d1316`)

- Define Crypto v1 client contract and cryptographic architecture (`b45b9ef`)

- *(SCDK-M51)* Update and refine OpenAPI specification (`d4a9299`)

- *(SCDK-M44)* Implement `VaultKeyMaterialCache` for encrypted storage of vault metadata (`46988fd`)

- *(SCDK-M46)* Implement `VaultUnlockUseCase` for passphrase and recovery key flows (`a52e62d`)

- *(roadmap)* Define Phase 6 for release engineering and quality gates (`ee99cd3`)


### Refactors

- *(core:auth)* Reorganize module structure into data and domain layers (`a548ac2`)

- *(SC-K_M-30)* Extract SessionManager interface and update state handling (`3e3ff41`)

- *(SCDK-M53)* Reorganize module structure and introduce `KeyWrapping` abstraction (`95a8463`)

- *(SCDK_M55)* Reorganize feature module structures and implement Action/Event patterns (`f6e5cdf`)

- *(SCDK_M80)* Transition from `OffsetDateTime` to `Instant` for timestamp representation (`5a17f0f`)

- *(SCDK-M87)* Reorder synchronization phases to pull before push in `VaultSyncUseCase` (`09b539a`)

- *(SCDK-M89)* Transition from opportunistic push to reactive sync based on vault dirty state (`85635dc`)

- *(SCDK-M90)* Introduce draft-first vault persistence (`e8c3fd8`)

- *(SCDK-M90)* Implement draft-first mutation lifecycle (`da530d8`)

- *(SCDK-M90)* Centralize account session lifecycle (`e6090da`)


### Documentation

- *(safecube-android)* Add project overview and package structure generation tooling (`c17fe8b`)

- *(safecube-android)* Enhance folder tree generation with path compacting and regex filtering (`dc298ae`)

- Add architecture decision record for local persistence strategy (`bc6b724`)

- Add high-level and Phase 1 technical roadmaps (`7c608e6`)

- *(SC-DK_M-25)* Define architectural constraints and scope for OpenAPI integration (`2d9585e`)

- Update package structure and folder tree script parameters (`59de659`)

- *(SCDK-M42)* Define OpenAPI integration strategy for vault key material (`2c38bb1`)

- *(roadmap)* Introduce Phase 4 for offline-first vault CRUD and mark Phases 2 & 3 as completed (`075d442`)

- *(SCDK-M61)* Define `SecureItem` Payload v1 architecture and client contract (`190d803`)

- *(roadmap)* Introduce Phase 5 for incremental multi-device synchronization (`0e91a60`)

- *(SCDK-M70)* Update docs due to contract's update (`1396554`)

- *(SCDK-M70)* Define Vault Sync v1 client strategy (`5b2f026`)

- *(SCDK_M71)* Define OpenAPI integration strategy for vault items contract (`7d76a91`)

- *(roadmap)* Introduce Phase 8 for opportunistic vault synchronization (`a250e8e`)

- *(architecture)* Introduce local draft model for vault sync conflict resolution (`e276265`)

- *(SCDK-M90)* Document account-scoped vault sequencing (`95de004`)

- *(SCDK-M90)* Align documentation with vault sync v2 (`d9764f9`)

- *(roadmap)* Restructure project roadmap into v1.0.0 release program (`4636cce`)

- Promote SBOM and attestations to release requirements in roadmap (`2222c52`)

- *(SCDK-M91)* Establish spec-driven development documentation foundation (`242ada8`)


### Tests

- *(app)* Configure testing environment and add Compose UI tests (`0302a90`)

- *(SC-DK_M-35)* Implement integration tests for token refresh and authentication flows (`d8006ff`)

- *(SCDK-M49)* Add `AesGcmKeyWrappingTest` for KEK operations (`e16ab07`)

- *(SCDK-M69)* Implement `SecureItemDao` integration tests and configure Robolectric (`6f5f57b`)

- *(SCDK-M69)* Implement `SecureItemDao` integration tests and configure Robolectric (`1c4d5c0`)

- *(SCDK-M56)* Migrate unit tests to MockK and enhance coverage in core:auth (`e174bab`)

- *(SCDK-M56)* Refactor unit tests for better readability and coverage in core:crypto (`7ab1544`)

- *(SCDK-M56)* Refactor unit tests for better readability and coverage in core:network (`b0e0a28`)

- *(SCDK-M56)* Refactor and modernize unit tests with MockK and Given-When-Then naming in core:vault (`a63e8c5`)

- *(SCDK-M56)* Refactor view model tests to use dynamic mock data and improve initialization in feature:vault (`fc315c9`)

- *(SCDK-M56)* Improve code coverage and robustness across core modules (`3fac42c`)

- *(SCDK-M90)* Verify the vault OpenAPI contract (`848c3fb`)

- *(SCDK-M90)* Verify account-scoped vault protocol invariants (`7a17018`)

- *(SCDK-M90)* Verify the vault sync protocol end to end (`f02b8fd`)


### Maintenance

- *(safecube-android)* Initialize Android project (`92880da`)

- *(safecube-android)* Modularize project with core and feature modules (`8fd4ea4`)

- Add KSP plugin and update dependencies in version catalog (`38eb05d`)

- *(core:ui)* Initialize core UI module and integrate with feature modules (`bc4f834`)

- *(app)* Configure release and benchmark build types with ProGuard rules (`4ee45da`)

- *(docs)* Update package structure and add run configuration (`83742fb`)

- Update project scripts and add Phase 2 roadmap (`ee74ea3`)

- *(app)* Enhance gitignore (`e626307`)

- *(SCDK-M38)* Integrate Kover for code coverage and verification (`967c47d`)

- *(SCDK-M43)* Increase minimum code coverage thresholds (`5b81f0e`)

- *(SCDK_M57)* Reorganize app module structure and package naming (`915f892`)

- *(SCDK_M57)* Reorganize app module structure and package naming (`783322b`)

- *(SCDK-M56)* Increase minimum test coverage thresholds (`3359df1`)

- *(SCDK-M56)* Update Kover code coverage thresholds (`480ea90`)

- *(SCDK-M70)* Update OpenAPI contract (`47fc563`)

- *(SCDK-M90)* Ignore macOS metadata files (`bb6ce0f`)

- *(SCDK-M97)* Harden CI verification for hosted runners (`0156464`)

- *(SCDK-M98)* Add new job to GitHub pipeline to run instrumented tests (`adc9609`)

- *(SCDK-M99)* Add dependency-review workflow (`5a958cc`)

- *(SCDK-M100)* Add codeql.yml workflow (`d09c549`)

- *(SCDK-M101)* Implement new gitleaks secret-scan workflow (`c656fa0`)

- *(SCDK-M102)* Create release workflow (`8a863b2`)

- *(SCDK-M103)* Add publish job to release workflow (`d183547`)

- *(SCDK-M103)* Fix release candidate tag creation. (`4557728`)


### Other

- Initial commit (`25b0411`)

- Do not commit (`4134856`)

- Feat/scdk m103  Align quality gates with release train

* chore(SCDK-M103): align quality gates with release train

* chore(SCDK-M103): share Gradle setup and enable build cache (`c17a977`)

<!-- generated by git-cliff; do not edit manually -->
