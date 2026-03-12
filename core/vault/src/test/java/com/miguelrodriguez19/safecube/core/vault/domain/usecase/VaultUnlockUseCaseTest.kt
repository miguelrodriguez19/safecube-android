package com.miguelrodriguez19.safecube.core.vault.domain.usecase

import android.content.SharedPreferences
import com.miguelrodriguez19.safecube.core.crypto.EncryptionRequest
import com.miguelrodriguez19.safecube.core.crypto.KdfEngine
import com.miguelrodriguez19.safecube.core.crypto.KdfRequest
import com.miguelrodriguez19.safecube.core.crypto.internal.AesGcmCryptoEngine
import com.miguelrodriguez19.safecube.core.vault.data.local.VaultKeyMaterialCache
import com.miguelrodriguez19.safecube.core.vault.domain.crypto.KeyWrapEnvelopeCodec
import com.miguelrodriguez19.safecube.core.vault.domain.model.VaultKeyMaterial
import com.miguelrodriguez19.safecube.core.vault.domain.model.unlock.VaultUnlockError
import com.miguelrodriguez19.safecube.core.vault.domain.model.unlock.VaultUnlockResult
import org.junit.Assert.assertArrayEquals
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class VaultUnlockUseCaseTest {
    private val cryptoEngine = AesGcmCryptoEngine()
    private val kdfEngine = DeterministicKdfEngine()
    private val keyWrapEnvelopeCodec = KeyWrapEnvelopeCodec()

    @Test
    fun `unlock with passphrase returns unlocked keyring`() {
        val passphrase = "correct-passphrase"
        val recoveryKey = ByteArray(32) { index -> (index + 31).toByte() }
        val kek = ByteArray(32) { index -> (index + 1).toByte() }
        val cache = cacheWithMaterial(
            passphrase = passphrase,
            recoveryKey = recoveryKey,
            kek = kek,
        )
        val useCase = VaultUnlockUseCase(
            vaultKeyMaterialLocalRepository = cache,
            kdfEngine = kdfEngine,
            cryptoEngine = cryptoEngine,
            keyWrapEnvelopeCodec = keyWrapEnvelopeCodec,
        )

        val result = useCase.unlockWithPassphrase(passphrase = passphrase)

        assertTrue(result is VaultUnlockResult.Unlocked)
        val keyring = (result as VaultUnlockResult.Unlocked).keyring
        assertArrayEquals(kek, keyring.kek)
    }

    @Test
    fun `unlock with recovery key returns unlocked keyring`() {
        val passphrase = "correct-passphrase"
        val recoveryKey = ByteArray(32) { index -> (index + 31).toByte() }
        val kek = ByteArray(32) { index -> (index + 1).toByte() }
        val cache = cacheWithMaterial(
            passphrase = passphrase,
            recoveryKey = recoveryKey,
            kek = kek,
        )
        val useCase = VaultUnlockUseCase(
            vaultKeyMaterialLocalRepository = cache,
            kdfEngine = kdfEngine,
            cryptoEngine = cryptoEngine,
            keyWrapEnvelopeCodec = keyWrapEnvelopeCodec,
        )

        val result = useCase.unlockWithRecoveryKey(recoveryKey = recoveryKey)

        assertTrue(result is VaultUnlockResult.Unlocked)
        val keyring = (result as VaultUnlockResult.Unlocked).keyring
        assertArrayEquals(kek, keyring.kek)
    }

    @Test
    fun `unlock with wrong passphrase returns stable invalid credential error`() {
        val passphrase = "correct-passphrase"
        val recoveryKey = ByteArray(32) { index -> (index + 31).toByte() }
        val kek = ByteArray(32) { index -> (index + 1).toByte() }
        val cache = cacheWithMaterial(
            passphrase = passphrase,
            recoveryKey = recoveryKey,
            kek = kek,
        )
        val useCase = VaultUnlockUseCase(
            vaultKeyMaterialLocalRepository = cache,
            kdfEngine = kdfEngine,
            cryptoEngine = cryptoEngine,
            keyWrapEnvelopeCodec = keyWrapEnvelopeCodec,
        )

        val result = useCase.unlockWithPassphrase(passphrase = "wrong-passphrase")

        assertEquals(
            VaultUnlockResult.Error(VaultUnlockError.InvalidCredential),
            result,
        )
    }

    @Test
    fun `unlock with wrong recovery key returns stable invalid credential error`() {
        val passphrase = "correct-passphrase"
        val recoveryKey = ByteArray(32) { index -> (index + 31).toByte() }
        val kek = ByteArray(32) { index -> (index + 1).toByte() }
        val cache = cacheWithMaterial(
            passphrase = passphrase,
            recoveryKey = recoveryKey,
            kek = kek,
        )
        val useCase = VaultUnlockUseCase(
            vaultKeyMaterialLocalRepository = cache,
            kdfEngine = kdfEngine,
            cryptoEngine = cryptoEngine,
            keyWrapEnvelopeCodec = keyWrapEnvelopeCodec,
        )

        val result = useCase.unlockWithRecoveryKey(
            recoveryKey = ByteArray(32) { 7 },
        )

        assertEquals(
            VaultUnlockResult.Error(VaultUnlockError.InvalidCredential),
            result,
        )
    }

    @Test
    fun `unlock returns key material unavailable when cache is empty`() {
        val useCase = VaultUnlockUseCase(
            vaultKeyMaterialLocalRepository = VaultKeyMaterialCache(MinimalInMemorySharedPreferences()),
            kdfEngine = kdfEngine,
            cryptoEngine = cryptoEngine,
            keyWrapEnvelopeCodec = keyWrapEnvelopeCodec,
        )

        val passphraseResult = useCase.unlockWithPassphrase(passphrase = "passphrase")
        val recoveryResult = useCase.unlockWithRecoveryKey(recoveryKey = ByteArray(32) { 1 })

        assertEquals(
            VaultUnlockResult.Error(VaultUnlockError.KeyMaterialUnavailable),
            passphraseResult,
        )
        assertEquals(
            VaultUnlockResult.Error(VaultUnlockError.KeyMaterialUnavailable),
            recoveryResult,
        )
    }

    @Test
    fun `unlock returns invalid cached key material when envelope is malformed`() {
        val passphrase = "correct-passphrase"
        val recoveryKey = ByteArray(32) { index -> (index + 31).toByte() }
        val kek = ByteArray(32) { index -> (index + 1).toByte() }
        val cache = cacheWithMaterial(
            passphrase = passphrase,
            recoveryKey = recoveryKey,
            kek = kek,
            mutateMasterEnvelope = { envelope ->
                envelope.copyOf().also { corrupted ->
                    corrupted[0] = 99
                }
            },
        )
        val useCase = VaultUnlockUseCase(
            vaultKeyMaterialLocalRepository = cache,
            kdfEngine = kdfEngine,
            cryptoEngine = cryptoEngine,
            keyWrapEnvelopeCodec = keyWrapEnvelopeCodec,
        )

        val result = useCase.unlockWithPassphrase(passphrase = passphrase)

        assertEquals(
            VaultUnlockResult.Error(VaultUnlockError.InvalidCachedKeyMaterial),
            result,
        )
    }

    private fun cacheWithMaterial(
        passphrase: String,
        recoveryKey: ByteArray,
        kek: ByteArray,
        mutateMasterEnvelope: (ByteArray) -> ByteArray = { value -> value },
    ): VaultKeyMaterialCache {
        val cache = VaultKeyMaterialCache(MinimalInMemorySharedPreferences())
        val kdfSalt = ByteArray(16) { index -> (index + 3).toByte() }
        val masterKey = kdfEngine.deriveKey(
            request = KdfRequest(
                secret = passphrase.encodeToByteArray(),
                salt = kdfSalt,
                iterations = 3,
                memoryKib = 65536,
                parallelism = 1,
                outputLengthBytes = 32,
            ),
        )
        val wrappedMaster = mutateMasterEnvelope(
            wrapKek(
                kek = kek,
                wrappingKey = masterKey,
            ),
        )
        val wrappedRecovery = wrapKek(
            kek = kek,
            wrappingKey = recoveryKey,
        )
        cache.save(
            vaultKeyMaterial = VaultKeyMaterial(
                kekEncMaster = wrappedMaster,
                kekEncRecovery = wrappedRecovery,
                kdfAlgorithm = "argon2id",
                kdfSalt = kdfSalt,
                kdfMemoryKib = 65536,
                kdfIterations = 3,
                kdfParallelism = 1,
                kdfOutputLen = 32,
                cryptoVersion = "v1",
            ),
        )
        return cache
    }

    private fun wrapKek(
        kek: ByteArray,
        wrappingKey: ByteArray,
    ): ByteArray = keyWrapEnvelopeCodec.encode(
        encryptionResult = cryptoEngine.encrypt(
            request = EncryptionRequest(
                plaintext = kek,
                keyMaterial = wrappingKey,
            ),
        ),
    )
}

private class DeterministicKdfEngine : KdfEngine {
    override fun deriveKey(request: KdfRequest): ByteArray {
        require(request.secret.isNotEmpty())
        require(request.salt.isNotEmpty())
        return ByteArray(request.outputLengthBytes) { index ->
            val secretByte = request.secret[index % request.secret.size].toInt() and 0xFF
            val saltByte = request.salt[index % request.salt.size].toInt() and 0xFF
            val mixed = secretByte xor saltByte xor request.iterations xor request.parallelism
            mixed.toByte()
        }
    }
}

private class MinimalInMemorySharedPreferences : SharedPreferences {
    private val values = LinkedHashMap<String, Any?>()

    override fun getAll(): MutableMap<String, *> = LinkedHashMap(values)

    override fun getString(
        key: String?,
        defValue: String?,
    ): String? = values[key] as? String ?: defValue

    @Suppress("UNCHECKED_CAST")
    override fun getStringSet(
        key: String?,
        defValues: MutableSet<String>?,
    ): MutableSet<String>? = (values[key] as? Set<String>)?.toMutableSet() ?: defValues

    override fun getInt(
        key: String?,
        defValue: Int,
    ): Int = values[key] as? Int ?: defValue

    override fun getLong(
        key: String?,
        defValue: Long,
    ): Long = values[key] as? Long ?: defValue

    override fun getFloat(
        key: String?,
        defValue: Float,
    ): Float = values[key] as? Float ?: defValue

    override fun getBoolean(
        key: String?,
        defValue: Boolean,
    ): Boolean = values[key] as? Boolean ?: defValue

    override fun contains(key: String?): Boolean = key != null && values.containsKey(key)

    override fun edit(): SharedPreferences.Editor = EditorImpl()

    override fun registerOnSharedPreferenceChangeListener(
        listener: SharedPreferences.OnSharedPreferenceChangeListener?,
    ) = Unit

    override fun unregisterOnSharedPreferenceChangeListener(
        listener: SharedPreferences.OnSharedPreferenceChangeListener?,
    ) = Unit

    private inner class EditorImpl : SharedPreferences.Editor {
        private var clearAll = false
        private val updates = LinkedHashMap<String, Any?>()
        private val removedKeys = LinkedHashSet<String>()

        override fun putString(
            key: String?,
            value: String?,
        ): SharedPreferences.Editor = apply {
            if (key == null) return@apply
            updates[key] = value
            removedKeys.remove(key)
        }

        override fun putStringSet(
            key: String?,
            values: MutableSet<String>?,
        ): SharedPreferences.Editor = apply {
            if (key == null) return@apply
            updates[key] = values?.toSet()
            removedKeys.remove(key)
        }

        override fun putInt(
            key: String?,
            value: Int,
        ): SharedPreferences.Editor = apply {
            if (key == null) return@apply
            updates[key] = value
            removedKeys.remove(key)
        }

        override fun putLong(
            key: String?,
            value: Long,
        ): SharedPreferences.Editor = apply {
            if (key == null) return@apply
            updates[key] = value
            removedKeys.remove(key)
        }

        override fun putFloat(
            key: String?,
            value: Float,
        ): SharedPreferences.Editor = apply {
            if (key == null) return@apply
            updates[key] = value
            removedKeys.remove(key)
        }

        override fun putBoolean(
            key: String?,
            value: Boolean,
        ): SharedPreferences.Editor = apply {
            if (key == null) return@apply
            updates[key] = value
            removedKeys.remove(key)
        }

        override fun remove(key: String?): SharedPreferences.Editor = apply {
            if (key == null) return@apply
            removedKeys.add(key)
            updates.remove(key)
        }

        override fun clear(): SharedPreferences.Editor = apply {
            clearAll = true
            updates.clear()
            removedKeys.clear()
        }

        override fun commit(): Boolean {
            applyChanges()
            return true
        }

        override fun apply() {
            applyChanges()
        }

        private fun applyChanges() {
            if (clearAll) {
                values.clear()
                clearAll = false
            }

            for (key in removedKeys) {
                values.remove(key)
            }
            removedKeys.clear()

            for ((key, value) in updates) {
                if (value == null) {
                    values.remove(key)
                } else {
                    values[key] = value
                }
            }
            updates.clear()
        }
    }
}
