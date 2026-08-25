# Inventario de almacenamiento local de producción

Este inventario documenta las superficies persistentes de SafeCube Android relevantes para
`SCDK-M126`. La política común es deny-by-default: el manifest efectivo deshabilita backup y las
reglas legacy, cloud y device-transfer excluyen el dominio raíz. No se añade SQLCipher, no se
cambia el schema Room y no se usa una migración destructiva.

| Superficie                                      | Contenido                                                                                                                                                                                                                                                                | Protección                                                                                                                                                                 | Limpieza / ciclo de vida                                                                                                                      | Backup y transferencia                                                               |
|-------------------------------------------------|--------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|----------------------------------------------------------------------------------------------------------------------------------------------------------------------------|-----------------------------------------------------------------------------------------------------------------------------------------------|--------------------------------------------------------------------------------------|
| Room (`core:storage`, `safecube.db`)            | `secure_items`, `secure_items_draft` y `secure_item_sync_checkpoints`. El payload de items y drafts es el envelope binario cifrado; identidad, tipo, versiones, timestamps, `displayHint` no sensible y estado de sync son metadata necesaria para listar y sincronizar. | El payload se cifra en `core:vault` antes de persistirse. Room sigue siendo la source of truth local.                                                                      | `SecureItemLocalStorage.clearAllLocalData()` borra las tres tablas en una única transacción. Un payload corrupto no se borra automáticamente. | Excluido por `android:allowBackup=false` y por las exclusiones explícitas de `root`. |
| Tokens (`core:auth`)                            | Access token, refresh token y `issuedAt`.                                                                                                                                                                                                                                | `EncryptedTokenStorage` usa `EncryptedSharedPreferences` respaldado por `MasterKey` AES-256-GCM.                                                                           | `SessionManagerImpl.forceLogout()` llama a `TokenStorage.clear()`.                                                                            | Excluido por las mismas reglas; no hay `include` de preferencias.                    |
| Material de claves envuelto (`core:vault`)      | `kekEncMaster`, `kekEncRecovery`, salt y parámetros KDF, versión criptográfica y `accountId`.                                                                                                                                                                            | `VaultKeyMaterialCache` usa `EncryptedSharedPreferences`. La KEK activa y claves derivadas solo viven en memoria; `VaultInMemoryKekStore.clear()` las zeroiza best-effort. | `LocalVaultDataCleanerImpl.clear()` limpia el almacén en memoria y la caché persistida antes de limpiar Room.                                 | Excluido por las mismas reglas; no se exporta material de claves.                    |
| Preferencia de auto-lock (`core:vault`)         | Solo el identificador no sensible de una de las cinco opciones aprobadas; un valor desconocido vuelve a `Immediately`.                                                                                                                                                   | `SharedPreferences` normal, sin secretos ni material del vault.                                                                                                            | Se actualiza al cambiar Settings y no se interpreta como actividad del usuario.                                                               | Excluido por la denegación global; no se declara ninguna excepción.                  |
| Quick unlock (`core:vault`, `quick_unlock_preferences.xml`) | Por cuenta: envelope binario v1 de una KEK envuelta, y marcador no secreto de que ya se ofreció la activación. El envelope es AES-256-GCM con nonce de 96 bits, tag de 128 bits y AAD ligado a cuenta y propósito de KEK. | La clave AES de wrapping es no exportable y vive en Android Keystore bajo un alias derivado por cuenta; solo se autoriza por uso mediante biometría fuerte o credencial segura del dispositivo. Ni KEK ni passphrase se persisten en claro. | Auto-lock, Lock now y process death retiran solo la KEK en memoria y preservan el enrolamiento. Corrupción/invalidez elimina únicamente el artefacto inutilizable; logout, cambio o borrado local de cuenta eliminan envelope, marcador y alias. | `android:allowBackup=false` y exclusiones nominales de `quick_unlock_preferences.xml` en legacy backup, cloud backup y device transfer; no se sincroniza. |
| Registro transitorio de recovery (`core:vault`) | Registro serializado del intento de inicialización pendiente, incluyendo el candidato y la recovery key solo dentro del valor almacenado.                                                                                                                                | `PendingVaultInitializationStore` lo guarda en `EncryptedSharedPreferences`; el codec no persiste plaintext separado.                                                      | Se elimina tras confirmación, descarte o logout. Si no puede verificarse el borrado, la operación no se da por completada.                    | Excluido explícitamente: no existe `include` para `sharedpref`, `file` o `root`.     |

## Contrato de limpieza de sesión

La terminación de sesión sigue dos límites coordinados:

1. `LocalVaultDataCleaner` borra KEK en memoria, material de claves, registro transitorio y las
   tablas Room de officials, drafts y checkpoints.
2. Solo si esa limpieza termina correctamente, `AccountSessionLifecycle` llama a
   `SessionManager.forceLogout()`, que elimina los tokens y publica el estado `LoggedOut`.

El auto-lock no termina la sesión autenticada ni elimina estos datos locales. El borrado completo
se reserva para logout, expiración o fallo de integridad de la sesión, conforme al contrato de
`SPEC-HARDENING-V1` y `ADR-0003-SENSITIVE-DATA-SURFACES`.

## Evidencia de código y pruebas

- [Manifest de aplicación](../../app/src/main/AndroidManifest.xml)
  y [reglas legacy](../../app/src/main/res/xml/backup_rules.xml).
- [Reglas cloud/device-transfer](../../app/src/main/res/xml/data_extraction_rules.xml).
- [Verificación del manifest release](../../app/build.gradle.kts), ejecutada por `releaseVerify`.
- [Caché de material de claves](../../core/vault/src/main/java/com/miguelrodriguez19/safecube/core/vault/data/local/VaultKeyMaterialCache.kt).
- [Almacén de tokens](../../core/auth/src/main/java/com/miguelrodriguez19/safecube/core/auth/data/local/EncryptedTokenStorage.kt).
- [Limpieza de vault](../../core/vault/src/main/java/com/miguelrodriguez19/safecube/core/vault/data/session/LocalVaultDataCleanerImpl.kt).
- [Adapter Android Keystore](../../core/vault/src/main/java/com/miguelrodriguez19/safecube/core/vault/data/quickunlock/AndroidKeystoreQuickUnlockAdapter.kt)
  y [store de quick unlock](../../core/vault/src/main/java/com/miguelrodriguez19/safecube/core/vault/data/quickunlock/QuickUnlockStore.kt).
- [Verificación instrumentada de quick unlock](../../app/src/androidTest/java/com/miguelrodriguez19/safecube/QuickUnlockDeviceCredentialTest.kt).
- [Prueba de borrado Room](../../core/storage/src/test/java/com/miguelrodriguez19/safecube/core/storage/local/SecureItemOfficializationIntegrationTest.kt).
- [Prueba de contrato de sesión](../../app/src/test/java/com/miguelrodriguez19/safecube/app/session/AccountSessionLifecycleImplTest.kt).
