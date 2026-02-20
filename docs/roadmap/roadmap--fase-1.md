# 🟢 FASE 1 — Fundaciones Técnicas (Diseño en profundidad)

Objetivo real:

> Tener infraestructura lista para que, cuando construyamos auth + vault, solo conectemos piezas.

No queremos UI bonita.
Queremos sistema operativo interno sólido.

---

# 1️⃣ 📱 Navigation configured

## Qué queremos realmente

No queremos “pantallas”.
Queremos:

* Un único `NavHost`
* Un sistema de rutas tipado
* Separación clara entre:
    * Graph root
    * Feature graphs
* Un punto central de orquestación en `app`

---

## Decisión arquitectónica

### 📌 Navegación vive en `app`

Crear:

```
app/navigation/
  NavigationWrapper.kt
  Routes.kt
```

---

## Qué debe permitir esta navegación

En Fase 1 solo necesitamos:

* Splash
* Welcome
* Login screen
* Signup screen
* Placeholder Home screen

Nada más.

No más rutas aún.

---

## Qué NO queremos todavía

* Múltiples NavHosts.
* Hilt acoplado a navegación.
* Deep links.
* Argumentos complejos.

Simple.

---

# 2️⃣ 🔐 Session & Auth foundation

Aquí es donde empieza lo serio.

Queremos separar 3 cosas:

1. Token storage
2. Session state
3. Refresh logic

---

## Diseño correcto

En `core:auth`:

```
SessionManager
TokenStorage
AuthRepository (interface)
```

---

### 🔹 TokenStorage

Responsabilidad:

* Guardar access token
* Guardar refresh token
* Leer tokens
* Limpiar tokens

Decisión importante:

🔒 Usar EncryptedSharedPreferences

Pero aún no implementamos.
Solo definimos contrato.

---

### 🔹 SessionManager

Responsabilidad:

* Saber si hay sesión activa
* Exponer session state como Flow
* Forzar logout
* Manejar refresh fallido

Debe ser completamente UI-agnóstico.

---

### 🔹 AuthRepository

Interfaz que usará feature:auth.

No debe saber de Retrofit.

---

## Qué queremos al final de esta subfase

Poder hacer:

```kotlin
sessionManager.isLoggedIn()
```

Y que eso sea verdad si hay tokens válidos.

Nada más.

---

# 3️⃣ 🌐 Network layer real

Ya tienes factories.

Ahora necesitamos formalizar:

* Retrofit setup
* JSON serializer
* Interceptor de auth
* Interceptor de logging (debug only)

---

## Diseño correcto

En `core:network`:

```
ApiService (interface)
NetworkClientFactory
AuthInterceptor
```

Pero:

AuthInterceptor no debe depender directamente de SessionManager.

Debe depender de una interfaz:

```
TokenProvider
```

Y SessionManager implementa TokenProvider.

Eso evita acoplamiento fuerte.

---

## Qué NO queremos aún

* Endpoints reales.
* Mappers.
* Error parsing complejo.
* Retry automático.

Solo infraestructura.

---

# 4️⃣ 🗄 Local persistence base

Aquí hay una decisión crítica.

### ¿Room ahora o más adelante?

Mi recomendación:

👉 Configurar Room ahora, aunque no lo usemos.

---

En `core:storage`:

```
AppDatabase
SecureItemEntity (vacío si quieres)
Dao interface
```

No implementamos vault aún.

Solo validamos:

* Room funciona
* Base de datos inicializa
* Tests pasan

---

# 5️⃣ 🔑 Crypto abstraction (ya está casi lista)

Tu `core:crypto` ya está muy bien planteado.

Ahora lo que necesitamos es:

* No implementar AES aún.
* No implementar KDF aún.
* Pero definir:

    * CryptoEngine
    * KdfEngine
    * KeyWrapping

Y que compilen y tengan tests fake.

---

# 🎯 Resultado esperado real al terminar FASE 1

La app podrá:

1. Arrancar.
2. Navegar a Login.
3. Simular login.
4. Guardar tokens.
5. Persistir algo en Room.
6. Exponer session state.
7. Tener crypto interfaces listas.

Sin cifrar aún.
Sin sync.
Sin vault real.

---

# 🧠 Arquitectura final de Fase 1 (mental map)

```
app
  └── navigation
        ↓
feature:auth
        ↓
core:auth
        ↓
core:network
        ↓
core:storage
        ↓
core:crypto (interfaces only)
```

Dependencias siempre hacia abajo.

Nunca hacia arriba.

---

# 🧨 Riesgos que debemos evitar en esta fase

1. Meter lógica de login en feature.
2. Hacer refresh automático demasiado pronto.
3. Implementar crypto real antes de tiempo.
4. Hacer Room dependiente de network.

---

# 🎯 Ahora te pregunto algo muy importante

¿Quieres introducir DI (Hilt) en Fase 1?
- Sí quiero meter Hilt ya.