# 🎯 VISIÓN GENERAL — SafeCube Mobile v1

Objetivo realista:

> Poder autenticarse, desbloquear vault, crear/editar secretos cifrados, y sincronizarlos con el
> backend.

---

## 🗺 ROADMAP ALTO NIVEL

El desarrollo funcional inicial se completa en las fases 0-5. La salida a `v1.0.0` se organiza
como un programa de release independiente en las fases 6-10.

---

### 🟢 FASE 0 — Bootstrap (COMPLETADA)

✔ Proyecto modular
✔ core:ui
✔ Estructura base
✔ Testing base

Terreno preparado.

---

### 🟢 FASE 1 — Fundaciones Técnicas (COMPLETADA)

Antes de UI bonita o features grandes.

Incluye:

1. 📱 Navigation configured
2. 🔐 Session & Auth foundation
3. 🌐 Network layer real
4. 🗄 Local persistence base
5. 🔑 Crypto abstraction (sin implementación compleja aún)

Resultado esperado:

> App puede loguearse y guardar algo localmente sin UI compleja.

---

### 🟢 FASE 2 — Auth Flow Completo (COMPLETADA)

Construir:

* Register
* Login
* Token storage seguro
* Refresh automático
* Logout

Separar claramente:

* Login
* Vault unlock

Resultado:

> Usuario autenticado pero vault aún bloqueado.

---

### 🟢 FASE 3 — Crypto Real + Vault Unlock (COMPLETADA)

Aquí empieza lo interesante.

Implementar:

* KDF
* Master key derivation
* Vault unlock flow
* Memory-only key handling
* Recovery key generation (solo MVP básico)

Resultado:

> Usuario puede desbloquear vault y cifrar/descifrar localmente.

---

### 🟢 FASE 4 — Vault CRUD Offline-First (COMPLETADA)

Implementar:

* Crear SecureItem
* Editar SecureItem
* Borrado lógico
* Persistencia local (Room)
* Mapeo cifrado

Sin sync todavía.

Resultado:

> Vault funcional 100% offline.

---

### 🟢 FASE 5 — Sync Multi-Device v2 (COMPLETADA)

Implementar:

* Pull incremental por `changeSequence`
* Push cambios locales
* Drafts locales y resolución explícita de conflictos
* Idempotencia y control CAS mediante `itemRevision`
* Soft delete sync
* Aislamiento local por cuenta

Resultado:

> Multi-device real.

---

## 🚀 PROGRAMA DE SALIDA A v1.0.0

La antigua fase 6, `Hardening & UX`, se divide porque mezcla infraestructura de entrega, seguridad,
experiencia de usuario, operación y validación final. Todas las fases siguientes persiguen una única
meta:

> Publicar un APK `v1.0.0` firmado, observable, resiliente y usable mediante GitHub Releases.

La primera distribución será una beta abierta: cualquier persona podrá descargar e instalar el APK
desde GitHub Releases. Por tanto, firma, privacidad, migraciones, compatibilidad y soporte de
incidencias se tratarán con estándar de release pública.

La publicación en Google Play queda fuera de este programa inicial.

### Punto de partida

- No existen workflows de CI/CD ni tags de versión.
- `versionName` está fijado manualmente como `1.0`.
- La build `release` usa todavía el keystore de debug.
- La aplicación permite backup y sus reglas siguen siendo las plantillas por defecto.
- `verifyCoverage` supera los umbrales actuales.
- `lintDebug` bloquea la build por errores y avisos de localización.
- Las pruebas instrumentadas y de UI todavía no representan los flujos reales del producto.

---

### 🟡 FASE 6 — Release Engineering & Quality Gates

Construir la infraestructura transversal antes de continuar con cambios funcionales y visuales:

* CI en GitHub Actions para pull requests y `main`
* Tests, cobertura, lint, build y contrato OpenAPI como quality gates
* Build `release` reproducible y minificada
* Keystore de release real gestionado mediante secrets
* Versionado SemVer con una única fuente de verdad
* Tags `vX.Y.Z` y prereleases `vX.Y.Z-rc.N`
* GitHub Release con APK firmado, checksum y release notes
* Gestión segura de dependencias y secretos

Nice-to-have:

* `CHANGELOG.md` generado automáticamente a partir de una convención de commits
* SBOM y attestations del artefacto publicado

Resultado:

> Cada cambio se valida automáticamente y un tag puede producir un APK firmado y trazable.

---

### 🟡 FASE 7 — Hardening, Seguridad & Resiliencia

Endurecer los flujos existentes sin ampliar innecesariamente el producto:

* Estados consistentes de loading, vacío, error y retry
* Matriz explícita de errores reintentables y definitivos
* Manejo usable de red lenta, offline y respuestas perdidas
* Expiración de sesión, refresh fallido y navegación segura a login
* Auto-lock al pasar a background y política configurable de inactividad
* Cambio de passphrase mediante rewrap de la `KEK`
* Edge cases de crypto, payload corrupto, proceso destruido y estado local inconsistente
* Revisión de backup, screenshots, logs, portapapeles, almacenamiento y configuración R8
* Eliminación u ocultación de pantallas y opciones placeholder que no pertenezcan a `v1`

La resiliencia no debe aplicar retries indiscriminados. Cada operación debe preservar idempotencia,
integridad criptográfica y el modelo draft-first de sync.

Resultado:

> Los fallos esperables se recuperan o se explican sin perder datos ni degradar el modelo de
> seguridad.

---

### 🟡 FASE 8 — Identidad de Marca, UI & Motion

Construir una experiencia coherente sobre los flujos ya estabilizados:

* Identidad visual y guía de marca de SafeCube
* Design system real en `core:ui`
* Tokens de color, tipografía, espaciado, formas, elevación e iconografía
* Rediseño de auth, inicialización, recovery, unlock, vault, editores, sync y settings
* Componentes reutilizables para feedback, formularios, errores y estados vacíos
* Transiciones y animaciones con una guía de motion consistente
* Respeto a la preferencia del sistema de reducir movimiento
* Accesibilidad: semántica, contraste, tamaños táctiles, escalado de fuente y navegación
* Localización completa y revisada en inglés y español
* Adaptación a los tamaños de pantalla soportados
* Tests Compose y validación visual de los estados críticos

Resultado:

> SafeCube tiene una identidad reconocible y una UX consistente, accesible y preparada para una
> release pública.

---

### 🟡 FASE 9 — Observabilidad & Operación

Definir primero un contrato de telemetría seguro y elegir después el backend:

* Crashes y ANR
* Tiempo de arranque, frames lentos y operaciones críticas
* Fallos clasificados de auth, refresh, sync, storage y crypto
* Identificación de versión, build y entorno
* Alertas mínimas y procedimiento de diagnóstico
* Sampling, retención, consentimiento y borrado de datos
* Funcionamiento fail-open: una caída de telemetría nunca bloquea la app

La telemetría nunca debe incluir:

* email u otros datos personales directos
* tokens, claves, passphrases o recovery keys
* payloads cifrados o descifrados
* `displayHint`, IDs de items o contenido del vault
* cuerpos HTTP, cabeceras sensibles o URLs con parámetros

Spike técnico inicial:

1. OpenTelemetry Android -> OTLP/Collector -> backend compatible.
2. SDK Android de Sentry -> GlitchTip.

Para `v1`, si todavía no existe infraestructura operativa propia, se recomienda un servicio alojado
en la UE y mantener la instrumentación desacoplada del proveedor. El autoalojado implica operar
despliegues, actualizaciones, backups, retención y alertas.

Resultado:

> Los fallos de producción pueden detectarse y diagnosticarse sin comprometer secretos ni convertir
> la telemetría en una dependencia del producto.

---

### 🟡 FASE 10 — Release Candidate & v1.0.0

Congelar alcance y validar el producto completo:

* E2E contra una instancia real del backend v2
* Sync concurrente entre dos dispositivos
* Respuesta perdida, retry idempotente, red lenta y offline
* Expiración de tokens y cierre seguro de sesión
* Process death, cold start y restauración de navegación
* Migraciones desde todas las versiones de base de datos publicadas
* Matriz mínima de dispositivos entre API 30 y API 36
* Build minificada, firmada e instalada desde cero
* Pruebas de accesibilidad, rendimiento y seguridad
* Validación real de eventos, redacción y alertas de observabilidad
* Publicación pública inicial de `v1.0.0-rc.1` como beta abierta
* Corrección exclusiva de blockers durante el periodo RC
* Promoción del RC validado a `v1.0.0`

Resultado:

> `v1.0.0` está disponible como APK firmado en GitHub Releases y cumple una definición de release
> reproducible.

---

## ✂️ CORTE FUNCIONAL RECOMENDADO PARA v1

La primera versión debe consolidar la propuesta central ya construida:

* registro y login
* creación y desbloqueo del vault
* recovery key
* CRUD cifrado de passwords y notas
* sync multi-device y resolución de conflictos
* cambio de passphrase
* auto-lock y controles esenciales de seguridad
* experiencia completa de estados, errores y recuperación

Para evitar que `v1.0.0` se convierta en una fase indefinida, se recomienda mover a `v1.1+`:

* desbloqueo biométrico
* búsqueda
* carpetas reales
* perfil enriquecido
* background sync periódico con `WorkManager`
* publicación en Google Play

Los accesos actuales a funcionalidades fuera de alcance deben ocultarse o reemplazarse por una
experiencia honesta; `v1` no debe contener pantallas dummy.

---

## ❓ DECISIONES PENDIENTES ANTES DE DETALLAR LAS FASES

1. Confirmar el corte funcional recomendado o promover biometría, búsqueda o carpetas a `v1`.
2. Elegir entre observabilidad alojada en la UE, autoalojada o solo preparar el contrato de
   telemetría.
3. Decidir si el changelog automatizado es requisito de `v1.0.0` o una mejora posterior.

---

## 🧠 Visualmente el roadmap sería:

```
Bootstrap
   ↓
Foundation
   ↓
Auth
   ↓
Crypto
   ↓
Offline Vault
   ↓
Sync v2
   ↓
Release Engineering
   ↓
Security & Resilience
   ↓
Brand, UI & Motion
   ↓
Observability
   ↓
Release Candidate
   ↓
v1.0.0
```
