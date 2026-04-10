package com.miguelrodriguez19.safecube.core.vault.data.local

import android.content.SharedPreferences
import com.miguelrodriguez19.safecube.core.vault.domain.model.VaultKeyMaterial
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import java.util.UUID
import org.junit.Assert.assertArrayEquals
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class VaultKeyMaterialCacheTest {
    private val encryptedPreferences = mockk<SharedPreferences>()
    private val editor = mockk<SharedPreferences.Editor>()
    private val values = linkedMapOf<String, Any?>()
    private val pendingUpdates = linkedMapOf<String, Any?>()
    private val removedKeys = linkedSetOf<String>()
    private var clearAll = false

    init {
        every { encryptedPreferences.edit() } returns editor
        every { encryptedPreferences.getString(any(), any()) } answers {
            values[firstArg<String>()] as? String ?: secondArg()
        }
        every { encryptedPreferences.getInt(any(), any()) } answers {
            values[firstArg<String>()] as? Int ?: secondArg()
        }

        every { editor.putString(any(), any()) } answers {
            pendingUpdates[firstArg<String>()] = secondArg<String?>()
            removedKeys.remove(firstArg())
            editor
        }
        every { editor.putInt(any(), any()) } answers {
            pendingUpdates[firstArg<String>()] = secondArg<Int>()
            removedKeys.remove(firstArg())
            editor
        }
        every { editor.clear() } answers {
            clearAll = true
            pendingUpdates.clear()
            removedKeys.clear()
            editor
        }
        every { editor.remove(any()) } answers {
            removedKeys += firstArg<String>()
            pendingUpdates.remove(firstArg())
            editor
        }
        every { editor.apply() } answers { applyEditorChanges() }
        every { editor.commit() } answers {
            applyEditorChanges()
            true
        }
    }

    @Test
    fun `save and get when key material is cached then returns cached key material`() {
        val target = VaultKeyMaterialCache(encryptedPreferences)
        val expected = sampleVaultKeyMaterial()

        target.save(expected)
        val actual = target.get()

        requireNotNull(actual)
        assertEquals(expected.accountId, actual.accountId)
        assertArrayEquals(expected.kekEncMaster, actual.kekEncMaster)
        assertArrayEquals(expected.kekEncRecovery, actual.kekEncRecovery)
        assertEquals(expected.kdfAlgorithm, actual.kdfAlgorithm)
        assertArrayEquals(expected.kdfSalt, actual.kdfSalt)
        assertEquals(expected.kdfMemoryKib, actual.kdfMemoryKib)
        assertEquals(expected.kdfIterations, actual.kdfIterations)
        assertEquals(expected.kdfParallelism, actual.kdfParallelism)
        assertEquals(expected.kdfOutputLen, actual.kdfOutputLen)
        assertEquals(expected.cryptoVersion, actual.cryptoVersion)
        verify(exactly = 1) { encryptedPreferences.edit() }
    }

    @Test
    fun `clear when cache contains key material then removes it`() {
        val target = VaultKeyMaterialCache(encryptedPreferences)
        target.save(sampleVaultKeyMaterial())

        target.clear()

        assertNull(target.get())
        verify(exactly = 2) { encryptedPreferences.edit() }
        verify(exactly = 1) { editor.clear() }
    }

    @Test
    fun `get when cache is empty then returns null`() {
        val target = VaultKeyMaterialCache(encryptedPreferences)

        val result = target.get()

        assertNull(result)
    }

    @Test
    fun `get when account id is missing then returns null`() {
        val target = VaultKeyMaterialCache(encryptedPreferences)
        target.save(sampleVaultKeyMaterial())
        values.remove("account_id")

        val result = target.get()

        assertNull(result)
    }

    @Test
    fun `get when account id is blank then returns null`() {
        val target = VaultKeyMaterialCache(encryptedPreferences)
        target.save(sampleVaultKeyMaterial())
        values["account_id"] = " "

        val result = target.get()

        assertNull(result)
    }

    @Test
    fun `get when account id is not a uuid then returns null`() {
        val target = VaultKeyMaterialCache(encryptedPreferences)
        target.save(sampleVaultKeyMaterial())
        values["account_id"] = "not-a-uuid"

        val result = target.get()

        assertNull(result)
    }

    @Test
    fun `get when master blob is missing then returns null`() {
        val target = VaultKeyMaterialCache(encryptedPreferences)
        target.save(sampleVaultKeyMaterial())
        values.remove("kek_enc_master")

        val result = target.get()

        assertNull(result)
    }

    @Test
    fun `get when master blob is not valid base64 then returns null`() {
        val target = VaultKeyMaterialCache(encryptedPreferences)
        target.save(sampleVaultKeyMaterial())
        values["kek_enc_master"] = "###invalid-base64###"

        val result = target.get()

        assertNull(result)
    }

    @Test
    fun `get when recovery blob is missing then returns null`() {
        val target = VaultKeyMaterialCache(encryptedPreferences)
        target.save(sampleVaultKeyMaterial())
        values.remove("kek_enc_recovery")

        val result = target.get()

        assertNull(result)
    }

    @Test
    fun `get when recovery blob is not valid base64 then returns null`() {
        val target = VaultKeyMaterialCache(encryptedPreferences)
        target.save(sampleVaultKeyMaterial())
        values["kek_enc_recovery"] = "###invalid-base64###"

        val result = target.get()

        assertNull(result)
    }

    @Test
    fun `get when kdf algorithm is blank then returns null`() {
        val target = VaultKeyMaterialCache(encryptedPreferences)
        target.save(sampleVaultKeyMaterial())
        values["kdf_algorithm"] = " "

        val result = target.get()

        assertNull(result)
    }

    @Test
    fun `get when kdf salt is not valid base64 then returns null`() {
        val target = VaultKeyMaterialCache(encryptedPreferences)
        target.save(sampleVaultKeyMaterial())
        values["kdf_salt"] = "###invalid-base64###"

        val result = target.get()

        assertNull(result)
    }

    @Test
    fun `get when kdf memory is zero then returns null`() {
        val target = VaultKeyMaterialCache(encryptedPreferences)
        target.save(sampleVaultKeyMaterial())
        values["kdf_memory_kib"] = 0

        val result = target.get()

        assertNull(result)
    }

    @Test
    fun `get when kdf iterations is zero then returns null`() {
        val target = VaultKeyMaterialCache(encryptedPreferences)
        target.save(sampleVaultKeyMaterial())
        values["kdf_iterations"] = 0

        val result = target.get()

        assertNull(result)
    }

    @Test
    fun `get when kdf parallelism is zero then returns null`() {
        val target = VaultKeyMaterialCache(encryptedPreferences)
        target.save(sampleVaultKeyMaterial())
        values["kdf_parallelism"] = 0

        val result = target.get()

        assertNull(result)
    }

    @Test
    fun `get when kdf output length is zero then returns null`() {
        val target = VaultKeyMaterialCache(encryptedPreferences)
        target.save(sampleVaultKeyMaterial())
        values["kdf_output_len"] = 0

        val result = target.get()

        assertNull(result)
    }

    @Test
    fun `get when crypto version is blank then returns null`() {
        val target = VaultKeyMaterialCache(encryptedPreferences)
        target.save(sampleVaultKeyMaterial())
        values["crypto_version"] = " "

        val result = target.get()

        assertNull(result)
    }

    private fun sampleVaultKeyMaterial(): VaultKeyMaterial = VaultKeyMaterial(
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
    )

    private fun applyEditorChanges() {
        if (clearAll) {
            values.clear()
            clearAll = false
        }

        removedKeys.forEach(values::remove)
        removedKeys.clear()

        pendingUpdates.forEach { (key, value) ->
            if (value == null) {
                values.remove(key)
            } else {
                values[key] = value
            }
        }
        pendingUpdates.clear()
    }
}
