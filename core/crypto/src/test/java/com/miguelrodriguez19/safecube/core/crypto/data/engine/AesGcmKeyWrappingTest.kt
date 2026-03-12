package com.miguelrodriguez19.safecube.core.crypto.data.engine

import com.miguelrodriguez19.safecube.core.crypto.domain.model.KeyUnwrapRequest
import com.miguelrodriguez19.safecube.core.crypto.domain.model.KeyWrapRequest
import javax.crypto.AEADBadTagException
import org.junit.Assert.assertArrayEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertThrows
import org.junit.Test

class AesGcmKeyWrappingTest {
    private val keyWrapping = AesGcmKeyWrapping(
        cryptoEngine = AesGcmCryptoEngine(),
    )
    private val wrappingKey = ByteArray(32) { index -> (index + 1).toByte() }
    private val kek = ByteArray(32) { index -> (index + 101).toByte() }
    private val aad = "purpose:key-wrap|version:v1".encodeToByteArray()

    @Test
    fun wrapThenUnwrap_roundtripSucceeds() {
        val wrapped = keyWrapping.wrapKey(
            request = KeyWrapRequest(
                keyToWrap = kek,
                wrappingKey = wrappingKey,
                aad = aad,
            ),
        )

        assertFalse(wrapped.contentEquals(kek))

        val unwrapped = keyWrapping.unwrapKey(
            request = KeyUnwrapRequest(
                wrappedKey = wrapped,
                wrappingKey = wrappingKey,
                aad = aad,
            ),
        )

        assertArrayEquals(kek, unwrapped)
    }

    @Test
    fun unwrap_failsWhenWrappingKeyIsIncorrect() {
        val wrapped = keyWrapping.wrapKey(
            request = KeyWrapRequest(
                keyToWrap = kek,
                wrappingKey = wrappingKey,
                aad = aad,
            ),
        )
        val wrongWrappingKey = wrappingKey.copyOf().also {
            it[it.lastIndex] = (it.last().toInt() xor 0x55).toByte()
        }

        assertThrows(AEADBadTagException::class.java) {
            keyWrapping.unwrapKey(
                request = KeyUnwrapRequest(
                    wrappedKey = wrapped,
                    wrappingKey = wrongWrappingKey,
                    aad = aad,
                ),
            )
        }
    }

    @Test
    fun wrappingSameKekTwiceProducesDifferentCiphertextAndNonce() {
        val firstWrapped = keyWrapping.wrapKey(
            request = KeyWrapRequest(
                keyToWrap = kek,
                wrappingKey = wrappingKey,
                aad = aad,
            ),
        )
        val secondWrapped = keyWrapping.wrapKey(
            request = KeyWrapRequest(
                keyToWrap = kek,
                wrappingKey = wrappingKey,
                aad = aad,
            ),
        )

        assertFalse(firstWrapped.contentEquals(secondWrapped))
    }

    @Test
    fun unwrap_failsWhenEnvelopeIsMalformed() {
        val malformedEnvelope = byteArrayOf(99, 1, 2, 3)

        assertThrows(IllegalArgumentException::class.java) {
            keyWrapping.unwrapKey(
                request = KeyUnwrapRequest(
                    wrappedKey = malformedEnvelope,
                    wrappingKey = wrappingKey,
                    aad = aad,
                ),
            )
        }
    }
}
