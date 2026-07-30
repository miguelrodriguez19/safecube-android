# Matriz de trazabilidad SDD

La matriz relaciona intención, ejecución y evidencia:

```text
Spec → Requirement ID → Trello task → código → test → evidencia
```

`N/A` solo es válido para un documento puramente normativo, y debe explicarse en la columna de
notas. Un gap no se oculta: se registra y genera una tarea posterior.

| Spec                          | Requirement ID    | Trello task | Código                       | Test/evidencia         | Estado        | Notas/gap                                     |
|-------------------------------|-------------------|-------------|------------------------------|------------------------|---------------|-----------------------------------------------|
| `SPEC-CRYPTO-V1`              | `SEC-CRYPTO-001`  | `PENDING`   | `core/crypto`, `core/vault`  | Tests crypto/vault     | `GAP`         | Crear task tras normalizar requisitos         |
| `SPEC-SECURE-ITEM-PAYLOAD-V1` | `FR-VAULT-001`    | `PENDING`   | `core/vault`                 | `VaultItemCipherTest`  | `GAP`         | Crear task tras normalizar requisitos         |
| `SPEC-VAULT-SYNC-V2`          | `FR-SYNC-001`     | `SCDK-M90`  | `core/vault`, `core/storage` | Sync/conflict tests    | `IMPLEMENTED` | Validación backend real pendiente             |
| `SPEC-AUTH-CONTRACT`          | `FR-AUTH-001`     | `PENDING`   | `core/auth`, `core/network`  | Auth integration tests | `GAP`         | Crear task con ID real de Trello              |
| `SPEC-STORAGE`                | `NFR-STORAGE-001` | `PENDING`   | `core/storage`               | Room/migration tests   | `GAP`         | Crear task con ID real de Trello              |
| `SPEC-TESTING`                | `NFR-QUALITY-001` | `PENDING`   | Gradle/Kover/tests           | `verifyCoverage`       | `GAP`         | Lint/instrumented gates pendientes            |
| `SPEC-RELEASE-POLICY`         | `NFR-RELEASE-001` | `SCDK-M91`, `SCDK-M92`, `SCDK-M93` | `version.properties`, `buildSrc/src/main/kotlin/com/miguelrodriguez19/safecube/buildlogic/AppVersion.kt`, `buildSrc/src/main/kotlin/com/miguelrodriguez19/safecube/buildlogic/ReleaseSigningConfig.kt`, `app/build.gradle.kts`, `build.gradle.kts`, `docs/release` | `AppVersionTest`, `ReleaseSigningConfigTest`, `validateVersion`, `:app:assembleRelease`, `verifyReleaseSigningConfiguration` | `VERIFIED` | Fuente única, release unsigned sin secrets y firma protegida verificadas |
| `SPEC-PRODUCT-V1`             | `OBJ-V1-001`      | `SCDK-M91`  | `docs/specs/product`         | Product brief review   | `VERIFIED`    | Baseline documental; feature specs pendientes |

## Reglas de mantenimiento

- Sustituir cada marcador `PENDING` por el ID real de Trello cuando se cree la card.
- Añadir fila para cada requisito nuevo antes de implementarlo.
- Añadir paths de código y tests exactos al cerrar una task.
- Marcar `GAP` cuando falta cobertura y enlazar una tarea de seguimiento.
- No marcar `VERIFIED` sin evidencia reproducible.
