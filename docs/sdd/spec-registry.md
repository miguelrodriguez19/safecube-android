# Spec Registry

Este registro identifica las fuentes normativas del proyecto. Una entrada `HISTORICAL` o
`SUPERSEDED` no debe utilizarse para implementar cambios nuevos.

| ID                                     | Título                           | Path                                                                                                                       | Estado       | Owner           | Última revisión | ADRs  | Fases     | Tipo      |
|----------------------------------------|----------------------------------|----------------------------------------------------------------------------------------------------------------------------|--------------|-----------------|-----------------|-------|-----------|-----------|
| `SPEC-HARDENING-V1`                    | Hardening, security and resilience v1 | [`hardening-resilience-v1.md`](../specs/features/hardening-resilience-v1.md)                                         | `APPROVED`   | `maintainer/security` | `2026-08-28`    | [`ADR-0001`](../architecture/adr/ADR-0001-VAULT-AUTO-LOCK.md) `ACCEPTED`; [`ADR-0002`](../architecture/adr/ADR-0002-PASSPHRASE-REWRAP.md) `ACCEPTED`; [`ADR-0003`](../architecture/adr/ADR-0003-SENSITIVE-DATA-SURFACES.md) `ACCEPTED` | F7/F9     | Normativa |
| `SPEC-AUTH-CONTRACT`                   | Auth API contract                | [`openapi-auth-contract-integration.md`](../architecture/openapi-auth-contract-integration.md)                             | `APPROVED`   | `auth`          | `2026-07-29`    | `N/A` | F2        | Normativa |
| `SPEC-CRYPTO-V1`                       | Crypto v1 client contract        | [`crypto-v1.md`](../architecture/crypto-v1.md)                                                                             | `APPROVED`   | `crypto`        | `2026-07-29`    | `N/A` | F3+       | Normativa |
| `SPEC-SECURE-ITEM-PAYLOAD-V1`          | SecureItem payload v1            | [`secure-item-payload-v1.md`](../architecture/secure-item-payload-v1.md)                                                   | `APPROVED`   | `vault`         | `2026-07-29`    | `N/A` | F4+       | Normativa |
| `SPEC-VAULT-SYNC-V2`                   | Vault sync versioning v2         | [`vault-sync-versioning-v2.md`](../architecture/vault-sync-versioning-v2.md)                                               | `APPROVED`   | `vault`         | `2026-07-29`    | `N/A` | F5        | Normativa |
| `SPEC-OPENAPI-AUTH`                    | OpenAPI auth integration         | [`openapi-auth-contract-integration.md`](../architecture/openapi-auth-contract-integration.md)                             | `APPROVED`   | `network`       | `2026-07-29`    | `N/A` | F2        | Normativa |
| `SPEC-OPENAPI-VAULT-ITEMS`             | OpenAPI vault items integration  | [`openapi-vault-items-contract-integration.md`](../architecture/openapi-vault-items-contract-integration.md)               | `APPROVED`   | `network/vault` | `2026-07-29`    | `N/A` | F5        | Normativa |
| `SPEC-OPENAPI-VAULT-KEY-MATERIAL`      | OpenAPI key material integration | [`openapi-vault-key-material-contract-integration.md`](../architecture/openapi-vault-key-material-contract-integration.md) | `APPROVED`   | `network/vault` | `2026-08-27`    | [`ADR-0002`](../architecture/adr/ADR-0002-PASSPHRASE-REWRAP.md) `ACCEPTED` | F3        | Normativa |
| `SPEC-STORAGE`                         | Local persistence strategy       | [`storage_decision.md`](../architecture/storage_decision.md)                                                               | `APPROVED`   | `storage`       | `2026-07-29`    | `N/A` | F1+       | Normativa |
| `SPEC-RELEASE-POLICY`                  | Versioning and release policy    | [`release-policy.md`](../release/release-policy.md)                                                                        | `APPROVED`   | `release`       | `2026-08-06`    | `N/A` | F6+       | Normativa |
| `SPEC-TESTING`                         | Testing standards and setup      | [`testing.md`](../testing/testing.md)                                                                                      | `APPROVED`   | `quality`       | `2026-07-29`    | `N/A` | F1+       | Normativa |
| `SPEC-TESTING-STANDARD`                | Core testing standard            | [`TESTING_STANDARD.md`](../testing/TESTING_STANDARD.md)                                                                    | `APPROVED`   | `quality`       | `2026-07-29`    | `N/A` | F1+       | Normativa |
| `SPEC-PRODUCT-V1`                      | SafeCube v1 product brief        | [`v1-product-brief.md`](../specs/product/v1-product-brief.md)                                                              | `APPROVED`   | `product`       | `2026-08-28`    | [`ADR-0001`](../architecture/adr/ADR-0001-VAULT-AUTO-LOCK.md) `ACCEPTED` | F0-10     | Normativa |
| `SPEC-VAULT-SYNC-V1`                   | Historical timestamp sync        | [`vault-sync-v1.md`](../architecture/historical/vault-sync-v1.md)                                                          | `SUPERSEDED` | `vault`         | `2026-07-29`    | `N/A` | Histórico | Histórica |
| `SPEC-VAULT-CONFLICT-DRAFT-HISTORICAL` | Historical draft resolution      | [`vault-sync-conflict-draft-resolution.md`](../architecture/historical/vault-sync-conflict-draft-resolution.md)            | `SUPERSEDED` | `vault`         | `2026-07-29`    | `N/A` | Histórico | Histórica |

## Reglas del registro

- Cada spec normativa tiene un ID único y un path existente.
- Una spec nueva debe enlazar la spec que reemplaza, si existe.
- Los cambios de estado requieren revisión documental en el mismo pull request.
- Una spec `APPROVED` debe tener criterios de aceptación y trazabilidad a tareas.
- Los documentos históricos conservan contexto, pero sus contratos no se implementan.
