package com.miguelrodriguez19.safecube.core.auth.data.local

import android.content.SharedPreferences
import io.mockk.Runs
import io.mockk.confirmVerified
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.mockk.verify
import io.mockk.verifySequence
import java.time.OffsetDateTime
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class EncryptedTokenStorageTest {

    private val encryptedPreferences = mockk<SharedPreferences>()
    private val editor = mockk<SharedPreferences.Editor>()

    private val target = EncryptedTokenStorage(encryptedPreferences)

    @Test
    fun `saveTokens when issuedAt is provided then persists access refresh and issuedAt`() {
        val issuedAt = OffsetDateTime.parse("2026-03-05T12:34:56Z")
        mockEditorChain()

        target.saveTokens(
            accessToken = "access-token",
            refreshToken = "refresh-token",
            issuedAt = issuedAt,
        )

        verifySequence {
            encryptedPreferences.edit()
            editor.putString("access_token", "access-token")
            editor.putString("refresh_token", "refresh-token")
            editor.putString("issued_at", issuedAt.toString())
            editor.apply()
        }
        confirmVerified(encryptedPreferences, editor)
    }

    @Test
    fun `saveTokens when issuedAt is null then persists access refresh and null issuedAt`() {
        mockEditorChain()

        target.saveTokens(
            accessToken = "access-token",
            refreshToken = "refresh-token",
            issuedAt = null,
        )

        verifySequence {
            encryptedPreferences.edit()
            editor.putString("access_token", "access-token")
            editor.putString("refresh_token", "refresh-token")
            editor.putString("issued_at", null)
            editor.apply()
        }
        confirmVerified(encryptedPreferences, editor)
    }

    @Test
    fun `getAccessToken when preferences contain value then returns persisted access token`() {
        every { encryptedPreferences.getString("access_token", null) } returns "access-token"

        val result = target.getAccessToken()

        assertEquals("access-token", result)
        verify(exactly = 1) { encryptedPreferences.getString("access_token", null) }
        confirmVerified(encryptedPreferences, editor)
    }

    @Test
    fun `getRefreshToken when preferences contain value then returns persisted refresh token`() {
        every { encryptedPreferences.getString("refresh_token", null) } returns "refresh-token"

        val result = target.getRefreshToken()

        assertEquals("refresh-token", result)
        verify(exactly = 1) { encryptedPreferences.getString("refresh_token", null) }
        confirmVerified(encryptedPreferences, editor)
    }

    @Test
    fun `getIssuedAt when stored value is invalid then returns null`() {
        every { encryptedPreferences.getString("issued_at", null) } returns "invalid-date"

        val result = target.getIssuedAt()

        assertNull(result)
        verify(exactly = 1) { encryptedPreferences.getString("issued_at", null) }
        confirmVerified(encryptedPreferences, editor)
    }

    @Test
    fun `getIssuedAt when stored value is missing then returns null`() {
        every { encryptedPreferences.getString("issued_at", null) } returns null

        val result = target.getIssuedAt()

        assertNull(result)
        verify(exactly = 1) { encryptedPreferences.getString("issued_at", null) }
        confirmVerified(encryptedPreferences, editor)
    }

    @Test
    fun `getIssuedAt when stored value is valid then returns parsed issuedAt`() {
        val issuedAt = OffsetDateTime.parse("2026-03-05T12:34:56Z")
        every { encryptedPreferences.getString("issued_at", null) } returns issuedAt.toString()

        val result = target.getIssuedAt()

        assertEquals(issuedAt, result)
        verify(exactly = 1) { encryptedPreferences.getString("issued_at", null) }
        confirmVerified(encryptedPreferences, editor)
    }

    @Test
    fun `clear when tokens are persisted then removes all stored values`() {
        mockEditorChain()
        every { editor.clear() } returns editor

        target.clear()

        verifySequence {
            encryptedPreferences.edit()
            editor.clear()
            editor.apply()
        }
        confirmVerified(encryptedPreferences, editor)
    }

    private fun mockEditorChain() {
        every { encryptedPreferences.edit() } returns editor
        every { editor.putString(any(), any()) } returns editor
        every { editor.apply() } just Runs
    }
}
