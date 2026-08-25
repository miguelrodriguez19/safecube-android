package com.miguelrodriguez19.safecube.core.vault.data.quickunlock

import android.content.SharedPreferences
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import java.util.Base64
import java.util.UUID
import org.junit.Assert.assertArrayEquals
import org.junit.Assert.assertTrue
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
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

    @Test
    fun `read envelope rejects malformed base64 and clear artifact reports commit failure`() {
        every { preferences.getString(any(), null) } returns "not-base64"
        assertEquals(QuickUnlockStoredEnvelope.Corrupted, target.readEnvelope(accountId))
        every { preferences.edit() } returns editor
        every { editor.remove(any()) } returns editor
        every { editor.commit() } returns false

        assertFalse(target.clearEnrollmentArtifact(accountId))
    }

    @Test
    fun `read envelope treats absent and empty values as non-enrollment states`() {
        every { preferences.getString(any(), null) } returns null
        assertEquals(QuickUnlockStoredEnvelope.Absent, target.readEnvelope(accountId))
        every { preferences.getString(any(), null) } returns ""

        assertEquals(QuickUnlockStoredEnvelope.Corrupted, target.readEnvelope(accountId))
    }

    @Test
    fun `save and clear account require post commit verification`() {
        val envelope = ByteArray(61) { it.toByte() }.also { it[0] = 0x01 }
        val encoded = Base64.getEncoder().encodeToString(envelope)
        every { preferences.edit() } returns editor
        every { editor.putString(any(), encoded) } returns editor
        every { editor.commit() } returns true
        every { preferences.getString(any(), null) } returns "different"

        assertFalse(target.saveEnvelope(accountId, envelope))

        every { editor.remove(any()) } returns editor
        every { preferences.getString(any(), null) } returns null
        every { preferences.getBoolean(any(), false) } returns true
        assertFalse(target.clearAccount(accountId))
    }

    @Test
    fun `clear enrollment artifact rejects a committed removal that remains readable`() {
        every { preferences.edit() } returns editor
        every { editor.remove(any()) } returns editor
        every { editor.commit() } returns true
        every { preferences.getString(any(), null) } returns "still-present"

        assertFalse(target.clearEnrollmentArtifact(accountId))
    }

    @Test
    fun `mark offer requires persisted marker and clear all handles empty keys`() {
        every { preferences.edit() } returns editor
        every { editor.putBoolean(any(), true) } returns editor
        every { editor.commit() } returns true
        every { preferences.getBoolean(any(), false) } returns false
        assertFalse(target.markOfferSeen(accountId))
        every { preferences.all } returns emptyMap()

        assertTrue(target.clearAll())
        verify(exactly = 0) { editor.remove(any()) }
    }

    @Test
    fun `mark offer seen reports commit failure and clear all removes only quick unlock keys`() {
        every { preferences.edit() } returns editor
        every { editor.putBoolean(any(), true) } returns editor
        every { editor.commit() } returns false
        assertFalse(target.markOfferSeen(accountId))
        every { preferences.all } returnsMany listOf(
            mapOf("quick_unlock.one" to true, "other" to true),
            mapOf("other" to true),
        )
        every { editor.remove(any()) } returns editor
        every { editor.commit() } returns true

        assertTrue(target.clearAll())
        verify(exactly = 1) { editor.remove("quick_unlock.one") }
    }

    @Test
    fun `save envelope and clear all report commit failures`() {
        val envelope = ByteArray(61) { it.toByte() }.also { it[0] = 0x01 }
        every { preferences.edit() } returns editor
        every { editor.putString(any(), any()) } returns editor
        every { editor.commit() } returns false

        assertFalse(target.saveEnvelope(accountId, envelope))

        every { preferences.all } returns mapOf("quick_unlock.one" to true)
        every { editor.remove(any()) } returns editor

        assertFalse(target.clearAll())
    }
}
