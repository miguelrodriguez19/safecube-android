package com.miguelrodriguez19.safecube.core.vault.domain.usecase

import android.content.SharedPreferences
import com.miguelrodriguez19.safecube.core.crypto.CryptoEngine
import com.miguelrodriguez19.safecube.core.crypto.DecryptionRequest
import com.miguelrodriguez19.safecube.core.crypto.EncryptionRequest
import com.miguelrodriguez19.safecube.core.crypto.EncryptionResult
import com.miguelrodriguez19.safecube.core.crypto.KdfEngine
import com.miguelrodriguez19.safecube.core.crypto.KdfRequest
import com.miguelrodriguez19.safecube.core.crypto.SaltGenerator
import com.miguelrodriguez19.safecube.core.network.generated.model.InitVaultKeyMaterialRequest
import com.miguelrodriguez19.safecube.core.network.generated.model.UpdateMasterWrappedKekRequest
import com.miguelrodriguez19.safecube.core.network.generated.model.VaultKeyMaterialResponse
import com.miguelrodriguez19.safecube.core.vault.data.local.VaultKeyMaterialCache
import com.miguelrodriguez19.safecube.core.vault.data.remote.VaultKeyMaterialDataSource
import com.miguelrodriguez19.safecube.core.vault.data.remote.VaultKeyMaterialRemoteError
import com.miguelrodriguez19.safecube.core.vault.data.remote.VaultKeyMaterialRemoteResult
import java.time.OffsetDateTime
import java.util.UUID
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertArrayEquals
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class VaultInitializeUseCaseTest {
    @Test
    fun `initializes vault when get returns vault not initialized`() = runBlocking {
        val dataSource = FakeVaultKeyMaterialDataSource(
            getResult = VaultKeyMaterialRemoteResult.Error(VaultKeyMaterialRemoteError.VaultNotInitialized),
            initResult = VaultKeyMaterialRemoteResult.Success(Unit),
        )
        val cache = VaultKeyMaterialCache(InMemorySharedPreferences())
        val kdfEngine = FakeKdfEngine()
        val cryptoEngine = FakeCryptoEngine()
        val useCase = createUseCase(
            dataSource = dataSource,
            cache = cache,
            kdfEngine = kdfEngine,
            cryptoEngine = cryptoEngine,
        )

        val result = useCase(passphrase = "correct horse battery staple")

        assertTrue(result is VaultInitializeResult.Initialized)
        val recoveryKey = (result as VaultInitializeResult.Initialized).recoveryKey
        assertEquals(32, recoveryKey.size)
        assertEquals(1, dataSource.getCalls)
        assertEquals(1, dataSource.initCalls)
        assertEquals(2, cryptoEngine.encryptRequests.size)

        val initRequest = requireNotNull(dataSource.lastInitRequest)
        assertFalse(recoveryKey.contentEquals(initRequest.kekEncMaster))
        assertFalse(recoveryKey.contentEquals(initRequest.kekEncRecovery))
        assertEquals("argon2id", initRequest.kdfAlgorithm)
        assertEquals("v1", initRequest.cryptoVersion)
        assertEquals(65536, initRequest.kdfMemoryKib)
        assertEquals(3, initRequest.kdfIterations)
        assertEquals(1, initRequest.kdfParallelism)
        assertEquals(32, initRequest.kdfOutputLen)

        val cached = requireNotNull(cache.get())
        assertArrayEquals(initRequest.kekEncMaster, cached.kekEncMaster)
        assertArrayEquals(initRequest.kekEncRecovery, cached.kekEncRecovery)
        assertArrayEquals(initRequest.kdfSalt, cached.kdfSalt)
        assertEquals(initRequest.kdfAlgorithm, cached.kdfAlgorithm)
        assertEquals(initRequest.cryptoVersion, cached.cryptoVersion)

        val lastKdfRequest = requireNotNull(kdfEngine.lastRequest)
        assertEquals(65536, lastKdfRequest.memoryKib)
        assertEquals(3, lastKdfRequest.iterations)
        assertEquals(1, lastKdfRequest.parallelism)
        assertEquals(32, lastKdfRequest.outputLengthBytes)
    }

    @Test
    fun `returns already initialized when get succeeds`() = runBlocking {
        val dataSource = FakeVaultKeyMaterialDataSource(
            getResult = VaultKeyMaterialRemoteResult.Success(existingVaultKeyMaterial()),
            initResult = VaultKeyMaterialRemoteResult.Success(Unit),
        )
        val useCase = createUseCase(dataSource = dataSource)

        val result = useCase(passphrase = "irrelevant")

        assertEquals(VaultInitializeResult.AlreadyInitialized, result)
        assertEquals(1, dataSource.getCalls)
        assertEquals(0, dataSource.initCalls)
    }

    @Test
    fun `returns remote error when get fails for reason other than 404`() = runBlocking {
        val dataSource = FakeVaultKeyMaterialDataSource(
            getResult = VaultKeyMaterialRemoteResult.Error(VaultKeyMaterialRemoteError.Unauthorized),
            initResult = VaultKeyMaterialRemoteResult.Success(Unit),
        )
        val useCase = createUseCase(dataSource = dataSource)

        val result = useCase(passphrase = "irrelevant")

        assertTrue(result is VaultInitializeResult.Error)
        assertEquals(
            VaultInitializeError.Remote(VaultKeyMaterialRemoteError.Unauthorized),
            (result as VaultInitializeResult.Error).reason,
        )
        assertEquals(1, dataSource.getCalls)
        assertEquals(0, dataSource.initCalls)
    }

    @Test
    fun `returns already initialized when init collides with existing vault`() = runBlocking {
        val dataSource = FakeVaultKeyMaterialDataSource(
            getResult = VaultKeyMaterialRemoteResult.Error(VaultKeyMaterialRemoteError.VaultNotInitialized),
            initResult = VaultKeyMaterialRemoteResult.Error(VaultKeyMaterialRemoteError.VaultAlreadyInitialized),
        )
        val cache = VaultKeyMaterialCache(InMemorySharedPreferences())
        val useCase = createUseCase(
            dataSource = dataSource,
            cache = cache,
        )

        val result = useCase(passphrase = "irrelevant")

        assertEquals(VaultInitializeResult.AlreadyInitialized, result)
        assertEquals(1, dataSource.initCalls)
        assertNull(cache.get())
    }

    @Test
    fun `returns remote error when init fails`() = runBlocking {
        val dataSource = FakeVaultKeyMaterialDataSource(
            getResult = VaultKeyMaterialRemoteResult.Error(VaultKeyMaterialRemoteError.VaultNotInitialized),
            initResult = VaultKeyMaterialRemoteResult.Error(VaultKeyMaterialRemoteError.Unauthorized),
        )
        val cache = VaultKeyMaterialCache(InMemorySharedPreferences())
        val useCase = createUseCase(
            dataSource = dataSource,
            cache = cache,
        )

        val result = useCase(passphrase = "irrelevant")

        assertTrue(result is VaultInitializeResult.Error)
        assertEquals(
            VaultInitializeError.Remote(VaultKeyMaterialRemoteError.Unauthorized),
            (result as VaultInitializeResult.Error).reason,
        )
        assertNull(cache.get())
    }

    @Test
    fun `returns crypto error when kdf throws`() = runBlocking {
        val dataSource = FakeVaultKeyMaterialDataSource(
            getResult = VaultKeyMaterialRemoteResult.Error(VaultKeyMaterialRemoteError.VaultNotInitialized),
            initResult = VaultKeyMaterialRemoteResult.Success(Unit),
        )
        val useCase = createUseCase(
            dataSource = dataSource,
            kdfEngine = ThrowingKdfEngine(),
        )

        val result = useCase(passphrase = "irrelevant")

        assertTrue(result is VaultInitializeResult.Error)
        assertTrue((result as VaultInitializeResult.Error).reason is VaultInitializeError.Crypto)
        assertEquals(0, dataSource.initCalls)
    }

    private fun createUseCase(
        dataSource: FakeVaultKeyMaterialDataSource,
        cache: VaultKeyMaterialCache = VaultKeyMaterialCache(InMemorySharedPreferences()),
        kdfEngine: KdfEngine = FakeKdfEngine(),
        cryptoEngine: FakeCryptoEngine = FakeCryptoEngine(),
    ): VaultInitializeUseCase = VaultInitializeUseCase(
        vaultKeyMaterialDataSource = dataSource,
        vaultKeyMaterialCache = cache,
        kdfEngine = kdfEngine,
        cryptoEngine = cryptoEngine,
        saltGenerator = SaltGenerator(),
    )

    private fun existingVaultKeyMaterial(): VaultKeyMaterialResponse = VaultKeyMaterialResponse(
        accountId = UUID.randomUUID(),
        kekEncMaster = byteArrayOf(1, 2, 3),
        kekEncRecovery = byteArrayOf(4, 5, 6),
        kdfAlgorithm = "argon2id",
        kdfSalt = byteArrayOf(7, 8, 9),
        kdfMemoryKib = 65536,
        kdfIterations = 3,
        kdfParallelism = 1,
        kdfOutputLen = 32,
        cryptoVersion = "v1",
        createdAt = OffsetDateTime.parse("2026-03-11T12:10:45Z"),
        updatedAt = OffsetDateTime.parse("2026-03-11T12:11:45Z"),
    )
}

private class FakeVaultKeyMaterialDataSource(
    private val getResult: VaultKeyMaterialRemoteResult<VaultKeyMaterialResponse>,
    private val initResult: VaultKeyMaterialRemoteResult<Unit>,
) : VaultKeyMaterialDataSource {
    var getCalls: Int = 0
        private set
    var initCalls: Int = 0
        private set
    var lastInitRequest: InitVaultKeyMaterialRequest? = null
        private set

    override suspend fun getKeyMaterial(): VaultKeyMaterialRemoteResult<VaultKeyMaterialResponse> {
        getCalls += 1
        return getResult
    }

    override suspend fun initKeyMaterial(
        request: InitVaultKeyMaterialRequest,
    ): VaultKeyMaterialRemoteResult<Unit> {
        initCalls += 1
        lastInitRequest = request
        return initResult
    }

    override suspend fun updateMasterWrappedKek(
        request: UpdateMasterWrappedKekRequest,
    ): VaultKeyMaterialRemoteResult<Unit> = VaultKeyMaterialRemoteResult.Success(Unit)
}

private class FakeKdfEngine : KdfEngine {
    var lastRequest: KdfRequest? = null
        private set

    override fun deriveKey(request: KdfRequest): ByteArray {
        lastRequest = request
        return ByteArray(32) { index -> (index + 1).toByte() }
    }
}

private class ThrowingKdfEngine : KdfEngine {
    override fun deriveKey(request: KdfRequest): ByteArray {
        throw IllegalStateException("boom")
    }
}

private class FakeCryptoEngine : CryptoEngine {
    val encryptRequests = mutableListOf<EncryptionRequest>()

    override fun encrypt(request: EncryptionRequest): EncryptionResult {
        encryptRequests += request.copy(
            plaintext = request.plaintext.copyOf(),
            keyMaterial = request.keyMaterial.copyOf(),
            aad = request.aad?.copyOf(),
        )
        return EncryptionResult(
            ciphertext = request.plaintext
                .map { value -> (value.toInt() xor 0x5A).toByte() }
                .toByteArray(),
            iv = byteArrayOf(1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12),
            authTag = byteArrayOf(13, 14, 15, 16, 17, 18, 19, 20, 21, 22, 23, 24, 25, 26, 27, 28),
        )
    }

    override fun decrypt(request: DecryptionRequest): ByteArray = byteArrayOf()
}

private class InMemorySharedPreferences : SharedPreferences {
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
