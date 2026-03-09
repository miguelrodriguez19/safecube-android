package com.miguelrodriguez19.safecube.core.crypto

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Test

class SaltGeneratorTest {
    private val generator = SaltGenerator()

    @Test
    fun generate_returnsRequestedLength() {
        val salt = generator.generate(lengthBytes = 32)

        assertEquals(32, salt.size)
    }

    @Test(expected = IllegalArgumentException::class)
    fun generate_throwsForNonPositiveLength() {
        generator.generate(lengthBytes = 0)
    }

    @Test
    fun generate_returnsDifferentSaltForSubsequentCalls() {
        val first = generator.generate(lengthBytes = 32)
        val second = generator.generate(lengthBytes = 32)

        assertFalse(first.contentEquals(second))
    }
}
