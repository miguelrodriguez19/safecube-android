package com.miguelrodriguez19.safecube.core.vault.data.session

import com.miguelrodriguez19.safecube.core.vault.domain.model.UnlockedKeyring
import com.miguelrodriguez19.safecube.core.vault.domain.model.VaultState
import com.miguelrodriguez19.safecube.core.vault.domain.model.VaultKeyMaterial
import com.miguelrodriguez19.safecube.core.vault.domain.model.remote.VaultKeyMaterialRemoteError
import com.miguelrodriguez19.safecube.core.vault.domain.model.remote.VaultKeyMaterialRemoteResult
import com.miguelrodriguez19.safecube.core.vault.domain.model.unlock.VaultUnlockError
import com.miguelrodriguez19.safecube.core.vault.domain.model.unlock.VaultUnlockResult
import com.miguelrodriguez19.safecube.core.vault.domain.repository.VaultKeyMaterialLocalRepository
import com.miguelrodriguez19.safecube.core.vault.domain.repository.VaultKeyMaterialRemoteRepository
import com.miguelrodriguez19.safecube.core.vault.domain.usecase.VaultUnlocker
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertArrayEquals
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class VaultSessionManagerImplTest {
    @Test
    fun `starts not initialized when there is no local key material`() {
        val manager = createManager(
            unlocker = FakeVaultUnlocker(),
            initialKeyMaterial = null,
        )

        assertEquals(VaultState.NotInitialized, manager.vaultState.value)
    }

    @Test
    fun `starts locked when local key material exists`() {
        val manager = createManager(
            unlocker = FakeVaultUnlocker(),
            initialKeyMaterial = sampleVaultKeyMaterial(),
        )

        assertEquals(VaultState.Locked, manager.vaultState.value)
    }

    @Test
    fun `refresh vault state uses remote key material and keeps state locked`() = runBlocking {
        val remoteKeyMaterial = sampleVaultKeyMaterial().copy(kekEncMaster = byteArrayOf(9, 9, 9))
        val manager = createManager(
            unlocker = FakeVaultUnlocker(),
            initialKeyMaterial = null,
            remoteResult = VaultKeyMaterialRemoteResult.Success(remoteKeyMaterial),
        )

        manager.refreshVaultState()

        assertEquals(VaultState.Locked, manager.vaultState.value)
    }

    @Test
    fun `refresh vault state sets not initialized when backend returns 404`() = runBlocking {
        val manager = createManager(
            unlocker = FakeVaultUnlocker(),
            initialKeyMaterial = sampleVaultKeyMaterial(),
            remoteResult = VaultKeyMaterialRemoteResult.Error(VaultKeyMaterialRemoteError.VaultNotInitialized),
        )

        manager.refreshVaultState()

        assertEquals(VaultState.NotInitialized, manager.vaultState.value)
    }

    @Test
    fun `refresh vault state keeps locked with cache and unknown without cache on network failure`() = runBlocking {
        val managerWithoutCache = createManager(
            unlocker = FakeVaultUnlocker(),
            initialKeyMaterial = null,
            remoteResult = VaultKeyMaterialRemoteResult.Error(
                VaultKeyMaterialRemoteError.NetworkError(IllegalStateException("offline")),
            ),
        )
        val managerWithCache = createManager(
            unlocker = FakeVaultUnlocker(),
            initialKeyMaterial = sampleVaultKeyMaterial(),
            remoteResult = VaultKeyMaterialRemoteResult.Error(
                VaultKeyMaterialRemoteError.NetworkError(IllegalStateException("offline")),
            ),
        )

        managerWithoutCache.refreshVaultState()
        managerWithCache.refreshVaultState()

        assertEquals(VaultState.Unknown, managerWithoutCache.vaultState.value)
        assertEquals(VaultState.Locked, managerWithCache.vaultState.value)
    }

    @Test
    fun `refresh vault state keeps locked with cache and unknown without cache on http error`() = runBlocking {
        val managerWithoutCache = createManager(
            unlocker = FakeVaultUnlocker(),
            initialKeyMaterial = null,
            remoteResult = VaultKeyMaterialRemoteResult.Error(
                VaultKeyMaterialRemoteError.HttpError(
                    statusCode = 500,
                    errorBody = "server unavailable",
                ),
            ),
        )
        val managerWithCache = createManager(
            unlocker = FakeVaultUnlocker(),
            initialKeyMaterial = sampleVaultKeyMaterial(),
            remoteResult = VaultKeyMaterialRemoteResult.Error(
                VaultKeyMaterialRemoteError.HttpError(
                    statusCode = 500,
                    errorBody = "server unavailable",
                ),
            ),
        )

        managerWithoutCache.refreshVaultState()
        managerWithCache.refreshVaultState()

        assertEquals(VaultState.Unknown, managerWithoutCache.vaultState.value)
        assertEquals(VaultState.Locked, managerWithCache.vaultState.value)
    }

    @Test
    fun `unlock with passphrase stores kek in memory and sets state unlocked`() {
        val expectedKek = byteArrayOf(11, 22, 33, 44)
        val unlocker = FakeVaultUnlocker(
            passphraseResult = VaultUnlockResult.Unlocked(UnlockedKeyring(kek = expectedKek)),
        )
        val manager = createManager(unlocker, sampleVaultKeyMaterial())

        val error = manager.unlockWithPassphrase("correct-passphrase")

        assertNull(error)
        assertEquals(VaultState.Unlocked, manager.vaultState.value)
        assertEquals("correct-passphrase", unlocker.lastPassphrase)
        assertArrayEquals(expectedKek, readInMemoryKek(manager))
    }

    @Test
    fun `unlock with recovery key stores kek in memory and sets state unlocked`() {
        val expectedKek = byteArrayOf(9, 8, 7, 6)
        val expectedRecovery = byteArrayOf(5, 4, 3, 2)
        val unlocker = FakeVaultUnlocker(
            recoveryResult = VaultUnlockResult.Unlocked(UnlockedKeyring(kek = expectedKek)),
        )
        val manager = createManager(unlocker, sampleVaultKeyMaterial())

        val error = manager.unlockWithRecoveryKey(expectedRecovery)

        assertNull(error)
        assertEquals(VaultState.Unlocked, manager.vaultState.value)
        assertArrayEquals(expectedRecovery, unlocker.lastRecoveryKey)
        assertArrayEquals(expectedKek, readInMemoryKek(manager))
    }

    @Test
    fun `unlock invalid credential returns stable error and keeps state locked`() {
        val unlocker = FakeVaultUnlocker(
            passphraseResult = VaultUnlockResult.Error(VaultUnlockError.InvalidCredential),
        )
        val manager = createManager(unlocker, sampleVaultKeyMaterial())

        val error = manager.unlockWithPassphrase("wrong-passphrase")

        assertEquals(VaultUnlockError.InvalidCredential, error)
        assertEquals(VaultState.Locked, manager.vaultState.value)
        assertNull(readInMemoryKek(manager))
    }

    @Test
    fun `unlock key material unavailable sets state not initialized`() {
        val unlocker = FakeVaultUnlocker(
            passphraseResult = VaultUnlockResult.Error(VaultUnlockError.KeyMaterialUnavailable),
        )
        val manager = createManager(unlocker, sampleVaultKeyMaterial())

        val error = manager.unlockWithPassphrase("any")

        assertEquals(VaultUnlockError.KeyMaterialUnavailable, error)
        assertEquals(VaultState.NotInitialized, manager.vaultState.value)
        assertNull(readInMemoryKek(manager))
    }

    @Test
    fun `lock wipes previous kek bytes and clears memory`() {
        val unlocker = FakeVaultUnlocker(
            passphraseResult = VaultUnlockResult.Unlocked(UnlockedKeyring(kek = byteArrayOf(1, 2, 3, 4))),
        )
        val manager = createManager(unlocker, sampleVaultKeyMaterial())
        manager.unlockWithPassphrase("passphrase")
        val leakedReference = requireNotNull(readInMemoryKek(manager))

        manager.lock()

        assertEquals(VaultState.Locked, manager.vaultState.value)
        assertTrue(leakedReference.all { it == 0.toByte() })
        assertNull(readInMemoryKek(manager))
    }

    @Test
    fun `onLogout wipes previous kek bytes and clears memory`() {
        val unlocker = FakeVaultUnlocker(
            passphraseResult = VaultUnlockResult.Unlocked(UnlockedKeyring(kek = byteArrayOf(7, 7, 7, 7))),
        )
        val manager = createManager(unlocker, sampleVaultKeyMaterial())
        manager.unlockWithPassphrase("passphrase")
        val leakedReference = requireNotNull(readInMemoryKek(manager))

        manager.onLogout()

        assertEquals(VaultState.Locked, manager.vaultState.value)
        assertTrue(leakedReference.all { it == 0.toByte() })
        assertNull(readInMemoryKek(manager))
    }

    @Test
    fun `unlock after already unlocked wipes previous kek before replacing`() {
        val firstKek = byteArrayOf(1, 1, 1, 1)
        val secondKek = byteArrayOf(2, 2, 2, 2)
        val unlocker = FakeVaultUnlocker(
            passphraseResult = VaultUnlockResult.Unlocked(UnlockedKeyring(kek = firstKek)),
            recoveryResult = VaultUnlockResult.Unlocked(UnlockedKeyring(kek = secondKek)),
        )
        val manager = createManager(unlocker, sampleVaultKeyMaterial())

        manager.unlockWithPassphrase("passphrase")
        val firstStoredReference = requireNotNull(readInMemoryKek(manager))
        manager.unlockWithRecoveryKey(byteArrayOf(9, 9, 9, 9))

        assertTrue(firstStoredReference.all { it == 0.toByte() })
        assertArrayEquals(secondKek, readInMemoryKek(manager))
    }

    @Suppress("UNCHECKED_CAST")
    private fun readInMemoryKek(manager: VaultSessionManagerImpl): ByteArray? {
        val field = VaultSessionManagerImpl::class.java.getDeclaredField("inMemoryKek")
        field.isAccessible = true
        return field.get(manager) as ByteArray?
    }

    private fun createManager(
        unlocker: VaultUnlocker,
        initialKeyMaterial: VaultKeyMaterial?,
        remoteResult: VaultKeyMaterialRemoteResult<VaultKeyMaterial> = VaultKeyMaterialRemoteResult.Error(
            VaultKeyMaterialRemoteError.NetworkError(IllegalStateException("unused")),
        ),
    ): VaultSessionManagerImpl = VaultSessionManagerImpl(
        vaultUnlocker = unlocker,
        vaultKeyMaterialLocalRepository = FakeVaultKeyMaterialLocalRepository(initialKeyMaterial),
        vaultKeyMaterialRemoteRepository = FakeVaultKeyMaterialRemoteRepository(remoteResult),
    )

    private fun sampleVaultKeyMaterial(): VaultKeyMaterial = VaultKeyMaterial(
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

private class FakeVaultUnlocker(
    private val passphraseResult: VaultUnlockResult = VaultUnlockResult.Error(VaultUnlockError.InvalidCredential),
    private val recoveryResult: VaultUnlockResult = VaultUnlockResult.Error(VaultUnlockError.InvalidCredential),
) : VaultUnlocker {
    var lastPassphrase: String? = null
        private set
    var lastRecoveryKey: ByteArray? = null
        private set

    override fun unlockWithPassphrase(passphrase: String): VaultUnlockResult {
        lastPassphrase = passphrase
        return passphraseResult
    }

    override fun unlockWithRecoveryKey(recoveryKey: ByteArray): VaultUnlockResult {
        lastRecoveryKey = recoveryKey.copyOf()
        return recoveryResult
    }
}

private class FakeVaultKeyMaterialLocalRepository(
    initialKeyMaterial: VaultKeyMaterial?,
) : VaultKeyMaterialLocalRepository {
    private var keyMaterial: VaultKeyMaterial? = initialKeyMaterial

    override fun save(vaultKeyMaterial: VaultKeyMaterial) {
        keyMaterial = vaultKeyMaterial
    }

    override fun get(): VaultKeyMaterial? = keyMaterial

    override fun clear() {
        keyMaterial = null
    }
}

private class FakeVaultKeyMaterialRemoteRepository(
    private val getResult: VaultKeyMaterialRemoteResult<VaultKeyMaterial>,
) : VaultKeyMaterialRemoteRepository {
    override suspend fun getKeyMaterial(): VaultKeyMaterialRemoteResult<VaultKeyMaterial> = getResult

    override suspend fun initKeyMaterial(
        vaultKeyMaterial: VaultKeyMaterial,
    ): VaultKeyMaterialRemoteResult<Unit> = error("Not required in test")

    override suspend fun updateMasterWrappedKek(newKekEncMaster: ByteArray): VaultKeyMaterialRemoteResult<Unit> =
        error("Not required in test")
}
