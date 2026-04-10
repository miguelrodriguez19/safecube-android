package com.miguelrodriguez19.safecube.core.crypto.domain.service

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertThrows
import org.junit.Test

class SaltGeneratorTest {
    private val target = SaltGenerator()

    @Test
    fun `generate when length is positive then returns requested number of bytes`() {
        // Arrange

        // Act
        val salt = target.generate(lengthBytes = 32)

        // Assert
        assertEquals(32, salt.size)
    }

    @Test
    fun `generate when length is not positive then throws illegal argument exception`() {
        // Arrange

        // Act
        // Assert
        assertThrows(IllegalArgumentException::class.java) {
            target.generate(lengthBytes = 0)
        }
    }

    @Test
    fun `generate when called twice then returns different values`() {
        // Arrange

        // Act
        val first = target.generate(lengthBytes = 32)
        val second = target.generate(lengthBytes = 32)

        // Assert
        assertFalse(first.contentEquals(second))
    }
}
