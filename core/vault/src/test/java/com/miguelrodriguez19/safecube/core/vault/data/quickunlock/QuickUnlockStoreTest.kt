package com.miguelrodriguez19.safecube.core.vault.data.quickunlock

import android.content.SharedPreferences
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import java.util.Base64
import java.util.UUID
import org.junit.Assert.assertArrayEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class QuickUnlockStoreTest {
    private val preferences = mockk<SharedPreferences>()
    private val editor = mockk<SharedPreferences.Editor>()
    private val target = QuickUnlockStore(preferences)
    private val accountId = UUID.fromString("00000000-0000-4000-8000-000000000001")

    @Test
    fun `readEnvelope decodes only the per account persisted artifact`() {
        val envelope = ByteArray(61) { it.toByte() }.also { it[0] = 0x01 }
        every { preferences.getString(any(), null) } returns Base64.getEncoder().encodeToString(envelope)

        val result = target.readEnvelope(accountId) as QuickUnlockStoredEnvelope.Present

        assertArrayEquals(envelope, result.value)
    }

    @Test
    fun `saveEnvelope commits and verifies stored binary artifact`() {
        val envelope = ByteArray(61) { it.toByte() }.also { it[0] = 0x01 }
        val encoded = Base64.getEncoder().encodeToString(envelope)
        every { preferences.edit() } returns editor
        every { editor.putString(any(), encoded) } returns editor
        every { editor.commit() } returns true
        every { preferences.getString(any(), null) } returns encoded

        val result = target.saveEnvelope(accountId, envelope)

        assertTrue(result)
    }

    @Test
    fun `clear enrollment artifact preserves offer marker`() {
        every { preferences.edit() } returns editor
        every { editor.remove(any()) } returns editor
        every { editor.commit() } returns true
        every { preferences.getString(any(), null) } returns null
        every { preferences.getBoolean(any(), false) } returns true

        val result = target.clearEnrollmentArtifact(accountId)

        assertTrue(result)
        verify(exactly = 1) { editor.remove(any()) }
    }

    @Test
    fun `clear account removes artifact and offer marker`() {
        every { preferences.edit() } returns editor
        every { editor.remove(any()) } returns editor
        every { editor.commit() } returns true
        every { preferences.getString(any(), null) } returns null
        every { preferences.getBoolean(any(), false) } returns false

        val result = target.clearAccount(accountId)

        assertTrue(result)
        verify(exactly = 2) { editor.remove(any()) }
    }
}
