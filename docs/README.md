# SafeCube - Data Keeper (Mobile)

SafeCube – Data Keeper is a Zero-Knowledge secret manager with mobile as the primary client.

The backend acts only as:
- Identity and session provider
- Storage and synchronization layer for already encrypted data

All encryption and key management happen on the client.

---

## Status

Project pre-MVP phase. The Android vault sync implementation uses the v2 draft-first protocol.

---

## Core Principles

- Zero-Knowledge by design
- Offline-first
- Client-centric cryptography
- Simplicity over overengineering

---

## Tech Stack

- Android (Kotlin + Jetpack Compose)
- Modular architecture
- Secure local storage
- Incremental sync

## Current Architecture References

- [Vault Sync Versioning v2](./architecture/vault-sync-versioning-v2.md): current sync protocol,
  CAS revisions, idempotency and account-scoped cursors.
- [OpenAPI Vault Items Contract](./architecture/openapi-vault-items-contract-integration.md):
  generated contract boundaries and manual refresh procedure.
- [SecureItem Payload v1](./architecture/secure-item-payload-v1.md): client payload envelope and
  AAD contract.
- [Fase 5 v2 roadmap](./roadmap/roadmap--fase-5.md): implemented scope and remaining validation.

Historical sync proposals are kept under
[`architecture/historical`](./architecture/historical) and
[`roadmap/historical`](./roadmap/historical).

## Spec-Driven Development

SafeCube uses versioned specifications and ADRs as the normative source for agent-driven work.

- [SDD manual](./sdd/README.md): authority hierarchy, lifecycle and repository conventions.
- [Spec registry](./sdd/spec-registry.md): canonical and historical documents.
- [Traceability matrix](./sdd/traceability-matrix.md): requirements to tasks, code, tests and
  evidence.
- [Spec template](./sdd/spec-template.md): product and contract requirements.
- [ADR template](./architecture/adr/ADR-TEMPLATE.md): durable technical decisions.
- [Agent workflow](./sdd/agent-workflow.md): execution and handoff protocol.
- [Definition of Ready/Done](./sdd/definition-of-ready-done.md): task quality bar.
- [v1 product brief](./specs/product/v1-product-brief.md): approved product baseline.

The roadmap defines order. GitHub/Trello tracks execution. A new phase must create or update an
approved spec before implementation cards are created.

---

The checked-in OpenAPI snapshot is validated locally; builds do not download it from the network.
