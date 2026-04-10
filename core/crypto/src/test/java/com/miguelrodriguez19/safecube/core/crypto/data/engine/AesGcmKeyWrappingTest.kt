package com.miguelrodriguez19.safecube.core.crypto.data.engine

import com.miguelrodriguez19.safecube.core.crypto.domain.model.KeyUnwrapRequest
import com.miguelrodriguez19.safecube.core.crypto.domain.model.KeyWrapRequest
import javax.crypto.AEADBadTagException
import org.junit.Assert.assertArrayEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertThrows
import org.junit.Test

class AesGcmKeyWrappingTest {
    private val target = AesGcmKeyWrapping(
        cryptoEngine = AesGcmCryptoEngine(),
    )
    private val wrappingKey = ByteArray(32) { index -> (index + 1).toByte() }
    private val kek = ByteArray(32) { index -> (index + 101).toByte() }
    private val aad = "purpose:key-wrap|version:v1".encodeToByteArray()

    @Test
    fun `wrap and unwrap when request is valid then returns original key`() {
        // Arrange

        // Act
        val wrapped = target.wrapKey(
            request = KeyWrapRequest(
                keyToWrap = kek,
                wrappingKey = wrappingKey,
                aad = aad,
            ),
        )

        val unwrapped = target.unwrapKey(
            request = KeyUnwrapRequest(
                wrappedKey = wrapped,
                wrappingKey = wrappingKey,
                aad = aad,
            ),
        )

        // Assert
        assertFalse(wrapped.contentEquals(kek))
        assertArrayEquals(kek, unwrapped)
    }

    @Test
    fun `unwrap when wrapping key is incorrect then throws bad tag exception`() {
        // Arrange
        val wrapped = target.wrapKey(
            request = KeyWrapRequest(
                keyToWrap = kek,
                wrappingKey = wrappingKey,
                aad = aad,
            ),
        )
        val wrongWrappingKey = wrappingKey.copyOf().also {
            it[it.lastIndex] = (it.last().toInt() xor 0x55).toByte()
        }

        // Act
        // Assert
        assertThrows(AEADBadTagException::class.java) {
            target.unwrapKey(
                request = KeyUnwrapRequest(
                    wrappedKey = wrapped,
                    wrappingKey = wrongWrappingKey,
                    aad = aad,
                ),
            )
        }
    }

    @Test
    fun `wrap key when same key is wrapped twice then returns different envelopes`() {
        // Arrange

        // Act
        val firstWrapped = target.wrapKey(
            request = KeyWrapRequest(
                keyToWrap = kek,
                wrappingKey = wrappingKey,
                aad = aad,
            ),
        )
        val secondWrapped = target.wrapKey(
            request = KeyWrapRequest(
                keyToWrap = kek,
                wrappingKey = wrappingKey,
                aad = aad,
            ),
        )

        // Assert
        assertFalse(firstWrapped.contentEquals(secondWrapped))
    }

    @Test
    fun `unwrap when envelope is malformed then throws illegal argument exception`() {
        // Arrange
        val malformedEnvelope = byteArrayOf(99, 1, 2, 3)

        // Act
        // Assert
        assertThrows(IllegalArgumentException::class.java) {
            target.unwrapKey(
                request = KeyUnwrapRequest(
                    wrappedKey = malformedEnvelope,
                    wrappingKey = wrappingKey,
                    aad = aad,
                ),
            )
        }
    }
}
