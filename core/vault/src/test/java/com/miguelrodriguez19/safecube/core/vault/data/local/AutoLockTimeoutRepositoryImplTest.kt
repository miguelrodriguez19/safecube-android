package com.miguelrodriguez19.safecube.core.vault.data.local

import android.content.SharedPreferences
import com.miguelrodriguez19.safecube.core.vault.domain.model.AutoLockTimeout
import io.mockk.Runs
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.mockk.verify
import org.junit.Assert.assertEquals
import org.junit.Test

class AutoLockTimeoutRepositoryImplTest {
    private val preferences = mockk<SharedPreferences>(relaxed = true)
    private val editor = mockk<SharedPreferences.Editor>(relaxed = true)

    private val target = AutoLockTimeoutRepositoryImpl(preferences)


    @Test
    fun `unknown persisted value falls back to immediately`() {
        every { preferences.getString("auto_lock_timeout", null) } returns "never"

        assertEquals(AutoLockTimeout.Immediately, target.timeout.value)
    }

    @Test
    fun `missing persisted value falls back to immediately`() {
        every { preferences.getString("auto_lock_timeout", null) } returns null

        assertEquals(AutoLockTimeout.Immediately, target.timeout.value)
    }

    @Test
    fun `set timeout persists approved value and updates observable state`() {
        every { preferences.getString("auto_lock_timeout", null) } returns null
        every { preferences.edit() } returns editor
        every {
            editor.putString(
                "auto_lock_timeout",
                AutoLockTimeout.FiveMinutes.storedValue
            )
        } returns editor
        every { editor.apply() } just Runs

        target.setTimeout(AutoLockTimeout.FiveMinutes)

        assertEquals(AutoLockTimeout.FiveMinutes, target.timeout.value)
        verify { editor.putString("auto_lock_timeout", "5_minutes") }
        verify { editor.apply() }
    }
}
