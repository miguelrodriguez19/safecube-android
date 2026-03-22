package com.miguelrodriguez19.safecube.core.vault.domain.usecase

import android.content.SharedPreferences
import com.miguelrodriguez19.safecube.core.crypto.domain.port.KdfEngine
import com.miguelrodriguez19.safecube.core.crypto.domain.model.KdfRequest
import com.miguelrodriguez19.safecube.core.crypto.domain.model.KeyUnwrapRequest
import com.miguelrodriguez19.safecube.core.crypto.domain.model.KeyWrapRequest
import com.miguelrodriguez19.safecube.core.crypto.domain.port.KeyWrapping
import com.miguelrodriguez19.safecube.core.crypto.domain.service.SaltGenerator
import com.miguelrodriguez19.safecube.core.vault.data.local.VaultKeyMaterialCache
import com.miguelrodriguez19.safecube.core.vault.domain.model.VaultKeyMaterial
import com.miguelrodriguez19.safecube.core.vault.domain.model.initialize.VaultInitializeError
import com.miguelrodriguez19.safecube.core.vault.domain.model.initialize.VaultInitializeResult
import com.miguelrodriguez19.safecube.core.vault.domain.model.remote.VaultKeyMaterialRemoteError
import com.miguelrodriguez19.safecube.core.vault.domain.model.remote.VaultKeyMaterialRemoteResult
import com.miguelrodriguez19.safecube.core.vault.domain.repository.VaultKeyMaterialRemoteRepository
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
        val refreshedVaultKeyMaterial = existingVaultKeyMaterial()
        val remoteRepository = FakeVaultKeyMaterialRemoteRepository(
            getResult = VaultKeyMaterialRemoteResult.Error(VaultKeyMaterialRemoteError.VaultNotInitialized),
            refreshedGetResult = VaultKeyMaterialRemoteResult.Success(refreshedVaultKeyMaterial),
            initResult = VaultKeyMaterialRemoteResult.Success(Unit),
        )
        val cache = VaultKeyMaterialCache(InMemorySharedPreferences())
        val kdfEngine = FakeKdfEngine()
        val keyWrapping = FakeKeyWrapping()
        val useCase = createUseCase(
            remoteRepository = remoteRepository,
            cache = cache,
            kdfEngine = kdfEngine,
            keyWrapping = keyWrapping,
        )

        val result = useCase(passphrase = "correct horse battery staple")

        assertTrue(result is VaultInitializeResult.Initialized)
        val recoveryKey = (result as VaultInitializeResult.Initialized).recoveryKey
        assertEquals(32, recoveryKey.size)
        assertEquals(2, remoteRepository.getCalls)
        assertEquals(1, remoteRepository.initCalls)
        assertEquals(2, keyWrapping.wrapRequests.size)

        val initPayload = requireNotNull(remoteRepository.lastInitPayload)
        assertNull(initPayload.accountId)
        assertFalse(recoveryKey.contentEquals(initPayload.kekEncMaster))
        assertFalse(recoveryKey.contentEquals(initPayload.kekEncRecovery))
        assertEquals("argon2id", initPayload.kdfAlgorithm)
        assertEquals("v1", initPayload.cryptoVersion)
        assertEquals(65536, initPayload.kdfMemoryKib)
        assertEquals(3, initPayload.kdfIterations)
        assertEquals(1, initPayload.kdfParallelism)
        assertEquals(32, initPayload.kdfOutputLen)

        val cached = requireNotNull(cache.get())
        assertEquals(refreshedVaultKeyMaterial.accountId, cached.accountId)
        assertArrayEquals(refreshedVaultKeyMaterial.kekEncMaster, cached.kekEncMaster)
        assertArrayEquals(refreshedVaultKeyMaterial.kekEncRecovery, cached.kekEncRecovery)
        assertArrayEquals(refreshedVaultKeyMaterial.kdfSalt, cached.kdfSalt)
        assertEquals(refreshedVaultKeyMaterial.kdfAlgorithm, cached.kdfAlgorithm)
        assertEquals(refreshedVaultKeyMaterial.cryptoVersion, cached.cryptoVersion)

        val lastKdfRequest = requireNotNull(kdfEngine.lastRequest)
        assertEquals(65536, lastKdfRequest.memoryKib)
        assertEquals(3, lastKdfRequest.iterations)
        assertEquals(1, lastKdfRequest.parallelism)
        assertEquals(32, lastKdfRequest.outputLengthBytes)
    }

    @Test
    fun `returns already initialized when get succeeds`() = runBlocking {
        val remoteRepository = FakeVaultKeyMaterialRemoteRepository(
            getResult = VaultKeyMaterialRemoteResult.Success(existingVaultKeyMaterial()),
            initResult = VaultKeyMaterialRemoteResult.Success(Unit),
        )
        val useCase = createUseCase(remoteRepository = remoteRepository)

        val result = useCase(passphrase = "irrelevant")

        assertEquals(VaultInitializeResult.AlreadyInitialized, result)
        assertEquals(1, remoteRepository.getCalls)
        assertEquals(0, remoteRepository.initCalls)
    }

    @Test
    fun `returns remote error when get fails for reason other than 404`() = runBlocking {
        val remoteRepository = FakeVaultKeyMaterialRemoteRepository(
            getResult = VaultKeyMaterialRemoteResult.Error(VaultKeyMaterialRemoteError.Unauthorized),
            initResult = VaultKeyMaterialRemoteResult.Success(Unit),
        )
        val useCase = createUseCase(remoteRepository = remoteRepository)

        val result = useCase(passphrase = "irrelevant")

        assertTrue(result is VaultInitializeResult.Error)
        assertEquals(
            VaultInitializeError.Remote(VaultKeyMaterialRemoteError.Unauthorized),
            (result as VaultInitializeResult.Error).reason,
        )
        assertEquals(1, remoteRepository.getCalls)
        assertEquals(0, remoteRepository.initCalls)
    }

    @Test
    fun `returns already initialized when init collides with existing vault`() = runBlocking {
        val remoteRepository = FakeVaultKeyMaterialRemoteRepository(
            getResult = VaultKeyMaterialRemoteResult.Error(VaultKeyMaterialRemoteError.VaultNotInitialized),
            initResult = VaultKeyMaterialRemoteResult.Error(VaultKeyMaterialRemoteError.VaultAlreadyInitialized),
        )
        val cache = VaultKeyMaterialCache(InMemorySharedPreferences())
        val useCase = createUseCase(
            remoteRepository = remoteRepository,
            cache = cache,
        )

        val result = useCase(passphrase = "irrelevant")

        assertEquals(VaultInitializeResult.AlreadyInitialized, result)
        assertEquals(1, remoteRepository.initCalls)
        assertNull(cache.get())
    }

    @Test
    fun `returns remote error when init fails`() = runBlocking {
        val remoteRepository = FakeVaultKeyMaterialRemoteRepository(
            getResult = VaultKeyMaterialRemoteResult.Error(VaultKeyMaterialRemoteError.VaultNotInitialized),
            initResult = VaultKeyMaterialRemoteResult.Error(VaultKeyMaterialRemoteError.Unauthorized),
        )
        val cache = VaultKeyMaterialCache(InMemorySharedPreferences())
        val useCase = createUseCase(
            remoteRepository = remoteRepository,
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
        val remoteRepository = FakeVaultKeyMaterialRemoteRepository(
            getResult = VaultKeyMaterialRemoteResult.Error(VaultKeyMaterialRemoteError.VaultNotInitialized),
            initResult = VaultKeyMaterialRemoteResult.Success(Unit),
        )
        val useCase = createUseCase(
            remoteRepository = remoteRepository,
            kdfEngine = ThrowingKdfEngine(),
        )

        val result = useCase(passphrase = "irrelevant")

        assertTrue(result is VaultInitializeResult.Error)
        assertTrue((result as VaultInitializeResult.Error).reason is VaultInitializeError.Crypto)
        assertEquals(0, remoteRepository.initCalls)
    }

    private fun createUseCase(
        remoteRepository: FakeVaultKeyMaterialRemoteRepository,
        cache: VaultKeyMaterialCache = VaultKeyMaterialCache(InMemorySharedPreferences()),
        kdfEngine: KdfEngine = FakeKdfEngine(),
        keyWrapping: KeyWrapping = FakeKeyWrapping(),
    ): VaultInitializeUseCase = VaultInitializeUseCase(
        vaultKeyMaterialRemoteRepository = remoteRepository,
        vaultKeyMaterialLocalRepository = cache,
        kdfEngine = kdfEngine,
        keyWrapping = keyWrapping,
        saltGenerator = SaltGenerator(),
    )

    private fun existingVaultKeyMaterial(): VaultKeyMaterial = VaultKeyMaterial(
        accountId = UUID.fromString("4f89ab0e-453f-4be5-b261-95068f2ad6f0"),
        kekEncMaster = byteArrayOf(1, 2, 3),
        kekEncRecovery = byteArrayOf(4, 5, 6),
        kdfAlgorithm = "argon2id",
        kdfSalt = byteArrayOf(7, 8, 9),
        kdfMemoryKib = 65536,
        kdfIterations = 3,
        kdfParallelism = 1,
        kdfOutputLen = 32,
        cryptoVersion = "v1",
    )
}

private class FakeVaultKeyMaterialRemoteRepository(
    private val getResult: VaultKeyMaterialRemoteResult<VaultKeyMaterial>,
    private val refreshedGetResult: VaultKeyMaterialRemoteResult<VaultKeyMaterial> = getResult,
    private val initResult: VaultKeyMaterialRemoteResult<Unit>,
) : VaultKeyMaterialRemoteRepository {
    var getCalls: Int = 0
        private set
    var initCalls: Int = 0
        private set
    var lastInitPayload: VaultKeyMaterial? = null
        private set

    override suspend fun getKeyMaterial(): VaultKeyMaterialRemoteResult<VaultKeyMaterial> {
        getCalls += 1
        return if (getCalls == 1) getResult else refreshedGetResult
    }

    override suspend fun initKeyMaterial(
        vaultKeyMaterial: VaultKeyMaterial,
    ): VaultKeyMaterialRemoteResult<Unit> {
        initCalls += 1
        lastInitPayload = vaultKeyMaterial
        return initResult
    }

    override suspend fun updateMasterWrappedKek(
        newKekEncMaster: ByteArray,
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

private class FakeKeyWrapping : KeyWrapping {
    val wrapRequests = mutableListOf<KeyWrapRequest>()

    override fun wrapKey(request: KeyWrapRequest): ByteArray {
        wrapRequests += request.copy(
            keyToWrap = request.keyToWrap.copyOf(),
            wrappingKey = request.wrappingKey.copyOf(),
            aad = request.aad?.copyOf(),
        )
        return byteArrayOf(1) + request.keyToWrap.mapIndexed { index, value ->
            (value.toInt() xor request.wrappingKey[index % request.wrappingKey.size].toInt()).toByte()
        }.toByteArray()
    }

    override fun unwrapKey(request: KeyUnwrapRequest): ByteArray = byteArrayOf()
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
