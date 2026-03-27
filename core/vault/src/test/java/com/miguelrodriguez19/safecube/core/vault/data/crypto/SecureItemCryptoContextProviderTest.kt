package com.miguelrodriguez19.safecube.core.vault.data.crypto

import com.miguelrodriguez19.safecube.core.vault.data.session.VaultInMemoryKekStore
import com.miguelrodriguez19.safecube.core.vault.domain.model.VaultKeyMaterial
import com.miguelrodriguez19.safecube.core.vault.domain.repository.VaultKeyMaterialLocalRepository
import io.mockk.confirmVerified
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import java.util.UUID
import org.junit.Assert.assertArrayEquals
import org.junit.Assert.assertEquals
import org.junit.Test

class SecureItemCryptoContextProviderTest {

    private val vaultKeyMaterialLocalRepository = mockk<VaultKeyMaterialLocalRepository>()
    private val vaultInMemoryKekStore = VaultInMemoryKekStore()

    private val target = SecureItemCryptoContextProvider(
        vaultKeyMaterialLocalRepository = vaultKeyMaterialLocalRepository,
        vaultInMemoryKekStore = vaultInMemoryKekStore,
    )

    @Test
    fun `get when vault kek is missing then returns vault locked without reading key material`() {
        val result = target.get()

        assertEquals(SecureItemCryptoContextResult.VaultLocked, result)
        verify(exactly = 0) { vaultKeyMaterialLocalRepository.get() }
        confirmVerified(vaultKeyMaterialLocalRepository)
    }

    @Test
    fun `get when account id is unavailable then returns account id unavailable`() {
        vaultInMemoryKekStore.replace(byteArrayOf(1, 2, 3, 4))
        every { vaultKeyMaterialLocalRepository.get() } returns sampleVaultKeyMaterial(accountId = null)

        val result = target.get()

        assertEquals(SecureItemCryptoContextResult.AccountIdUnavailable, result)
        assertArrayEquals(byteArrayOf(1, 2, 3, 4), vaultInMemoryKekStore.snapshot())
        verify(exactly = 1) { vaultKeyMaterialLocalRepository.get() }
        confirmVerified(vaultKeyMaterialLocalRepository)
    }

    @Test
    fun `get when context is available then returns account id and isolated kek snapshot`() {
        vaultInMemoryKekStore.replace(byteArrayOf(5, 6, 7, 8))
        every { vaultKeyMaterialLocalRepository.get() } returns sampleVaultKeyMaterial(accountId = SAMPLE_ACCOUNT_ID)

        val result = target.get()

        result as SecureItemCryptoContextResult.Available
        assertEquals(SAMPLE_ACCOUNT_ID, result.accountId)
        assertArrayEquals(byteArrayOf(5, 6, 7, 8), result.kek)
        result.kek[0] = 99.toByte()
        assertArrayEquals(byteArrayOf(5, 6, 7, 8), vaultInMemoryKekStore.snapshot())
        verify(exactly = 1) { vaultKeyMaterialLocalRepository.get() }
        confirmVerified(vaultKeyMaterialLocalRepository)
    }
}

private val SAMPLE_ACCOUNT_ID: UUID = UUID.fromString("aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa")

private fun sampleVaultKeyMaterial(accountId: UUID?): VaultKeyMaterial = VaultKeyMaterial(
    accountId = accountId,
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
