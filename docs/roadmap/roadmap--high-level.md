# 🎯 VISIÓN GENERAL — SafeCube Mobile v1

Objetivo realista:

> Poder autenticarse, desbloquear vault, crear/editar secretos cifrados, y sincronizarlos con el
> backend.

---

## 🗺 ROADMAP ALTO NIVEL

Lo divido en 6 grandes fases naturales.

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

### 🟢 FASE 2 — Auth Flow Completo

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

### 🟢 FASE 3 — Crypto Real + Vault Unlock

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

### 🟢 FASE 4 — Vault CRUD Offline-First

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

### 🟢 FASE 5 — Sync Incremental

Implementar:

* Pull incremental (since)
* Push cambios locales
* Manejo de conflictos simple
* Soft delete sync

Resultado:

> Multi-device real.

---

### 🟢 FASE 6 — Hardening & UX

* Estados de error elegantes
* Loading states
* Retry
* Manejo de expiración sesión
* Edge cases crypto
* QA serio

Resultado:

> MVP sólido y usable.

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
Sync
   ↓
Hardening
```