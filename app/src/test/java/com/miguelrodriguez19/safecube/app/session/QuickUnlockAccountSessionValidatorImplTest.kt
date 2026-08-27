package com.miguelrodriguez19.safecube.app.session

import com.miguelrodriguez19.safecube.core.auth.domain.session.SessionManager
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import java.util.UUID
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class QuickUnlockAccountSessionValidatorImplTest {
    private val sessionManager = mockk<SessionManager>()
    private val target = QuickUnlockAccountSessionValidatorImpl(sessionManager)

    @Test
    fun `is valid when account session is logged in`() {
        every { sessionManager.isLoggedIn() } returns true

        val result = target.isValid(UUID.randomUUID())

        assertTrue(result)
        verify(exactly = 1) { sessionManager.isLoggedIn() }
    }

    @Test
    fun `is invalid when account session is logged out`() {
        every { sessionManager.isLoggedIn() } returns false

        val result = target.isValid(UUID.randomUUID())

        assertFalse(result)
        verify(exactly = 1) { sessionManager.isLoggedIn() }
    }
}
