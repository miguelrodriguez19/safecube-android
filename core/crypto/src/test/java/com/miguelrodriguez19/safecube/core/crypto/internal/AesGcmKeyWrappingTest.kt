package com.miguelrodriguez19.safecube.core.crypto.internal

import com.miguelrodriguez19.safecube.core.crypto.DecryptionRequest
import com.miguelrodriguez19.safecube.core.crypto.EncryptionRequest
import com.miguelrodriguez19.safecube.core.crypto.EncryptionResult
import javax.crypto.AEADBadTagException
import org.junit.Assert.assertArrayEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertThrows
import org.junit.Test

class AesGcmKeyWrappingTest {
    private val engine = AesGcmCryptoEngine()
    private val wrappingKey = ByteArray(32) { index -> (index + 1).toByte() }
    private val kek = ByteArray(32) { index -> (index + 101).toByte() }
    private val aad = "purpose:key-wrap|version:v1".encodeToByteArray()

    @Test
    fun wrapThenUnwrap_roundtripSucceeds() {
        val wrapped = wrapKek(
            kek = kek,
            wrappingKey = wrappingKey,
            aad = aad,
        )

        assertFalse(wrapped.ciphertext.contentEquals(kek))

        val unwrapped = unwrapKek(
            wrapped = wrapped,
            wrappingKey = wrappingKey,
            aad = aad,
        )

        assertArrayEquals(kek, unwrapped)
    }

    @Test
    fun unwrap_failsWhenWrappingKeyIsIncorrect() {
        val wrapped = wrapKek(
            kek = kek,
            wrappingKey = wrappingKey,
            aad = aad,
        )
        val wrongWrappingKey = wrappingKey.copyOf().also {
            it[it.lastIndex] = (it.last().toInt() xor 0x55).toByte()
        }

        assertThrows(AEADBadTagException::class.java) {
            unwrapKek(
                wrapped = wrapped,
                wrappingKey = wrongWrappingKey,
                aad = aad,
            )
        }
    }

    @Test
    fun wrappingSameKekTwiceProducesDifferentCiphertextAndNonce() {
        val firstWrapped = wrapKek(
            kek = kek,
            wrappingKey = wrappingKey,
            aad = aad,
        )
        val secondWrapped = wrapKek(
            kek = kek,
            wrappingKey = wrappingKey,
            aad = aad,
        )

        assertFalse(firstWrapped.iv.contentEquals(secondWrapped.iv))
        val firstPayload = firstWrapped.ciphertext + firstWrapped.authTag
        val secondPayload = secondWrapped.ciphertext + secondWrapped.authTag
        assertFalse(firstPayload.contentEquals(secondPayload))
    }

    private fun wrapKek(
        kek: ByteArray,
        wrappingKey: ByteArray,
        aad: ByteArray?,
    ): EncryptionResult = engine.encrypt(
        request = EncryptionRequest(
            plaintext = kek,
            keyMaterial = wrappingKey,
            aad = aad,
        ),
    )

    private fun unwrapKek(
        wrapped: EncryptionResult,
        wrappingKey: ByteArray,
        aad: ByteArray?,
    ): ByteArray = engine.decrypt(
        request = DecryptionRequest(
            ciphertext = wrapped.ciphertext,
            keyMaterial = wrappingKey,
            iv = wrapped.iv,
            aad = aad,
            authTag = wrapped.authTag,
        ),
    )
}
