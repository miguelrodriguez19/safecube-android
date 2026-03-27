package com.miguelrodriguez19.safecube.core.vault.data.crypto

import java.util.UUID
import org.junit.Assert.assertArrayEquals
import org.junit.Assert.assertEquals
import org.junit.Assert.assertThrows
import org.junit.Test

class SecureItemPayloadEnvelopeV1CodecTest {

    private val target = SecureItemPayloadEnvelopeV1Codec()

    @Test
    fun `encode when fields are valid then returns envelope v1 payload`() {
        val result = target.encode(
            logicalItemId = SAMPLE_LOGICAL_ITEM_ID,
            wrappedDek = byteArrayOf(21, 22, 23, 24),
            nonce = byteArrayOf(31, 32, 33, 34, 35, 36, 37, 38, 39, 40, 41, 42),
            ciphertext = byteArrayOf(51, 52, 53, 54),
            authTag = byteArrayOf(61, 62, 63, 64, 65, 66, 67, 68, 69, 70, 71, 72, 73, 74, 75, 76),
        )

        assertArrayEquals(
            byteArrayOf(
                0x01,
                0x11,
                0x11,
                0x11,
                0x11,
                0x11,
                0x11,
                0x11,
                0x11,
                0x11,
                0x11,
                0x11,
                0x11,
                0x11,
                0x11,
                0x11,
                0x11,
                0x00,
                0x04,
                21,
                22,
                23,
                24,
                31,
                32,
                33,
                34,
                35,
                36,
                37,
                38,
                39,
                40,
                41,
                42,
                51,
                52,
                53,
                54,
                61,
                62,
                63,
                64,
                65,
                66,
                67,
                68,
                69,
                70,
                71,
                72,
                73,
                74,
                75,
                76,
            ),
            result,
        )
    }

    @Test
    fun `decode when payload is valid then returns parsed envelope`() {
        val payload = target.encode(
            logicalItemId = SAMPLE_LOGICAL_ITEM_ID,
            wrappedDek = byteArrayOf(21, 22, 23, 24),
            nonce = byteArrayOf(31, 32, 33, 34, 35, 36, 37, 38, 39, 40, 41, 42),
            ciphertext = byteArrayOf(51, 52, 53, 54),
            authTag = byteArrayOf(61, 62, 63, 64, 65, 66, 67, 68, 69, 70, 71, 72, 73, 74, 75, 76),
        )

        val result = target.decode(payload)

        assertEquals(SAMPLE_LOGICAL_ITEM_ID, result.logicalItemId)
        assertArrayEquals(byteArrayOf(21, 22, 23, 24), result.wrappedDek)
        assertArrayEquals(byteArrayOf(31, 32, 33, 34, 35, 36, 37, 38, 39, 40, 41, 42), result.nonce)
        assertArrayEquals(byteArrayOf(51, 52, 53, 54), result.ciphertext)
        assertArrayEquals(byteArrayOf(61, 62, 63, 64, 65, 66, 67, 68, 69, 70, 71, 72, 73, 74, 75, 76), result.authTag)
    }

    @Test
    fun `encode when nonce length is invalid then throws illegal argument exception`() {
        val error = assertThrows(IllegalArgumentException::class.java) {
            target.encode(
                logicalItemId = SAMPLE_LOGICAL_ITEM_ID,
                wrappedDek = byteArrayOf(21, 22, 23, 24),
                nonce = byteArrayOf(31, 32, 33),
                ciphertext = byteArrayOf(51, 52, 53, 54),
                authTag = byteArrayOf(61, 62, 63, 64, 65, 66, 67, 68, 69, 70, 71, 72, 73, 74, 75, 76),
            )
        }

        assertEquals("nonce must be exactly 12 bytes.", error.message)
    }

    @Test
    fun `decode when payload envelope version is unsupported then throws illegal argument exception`() {
        val payload = target.encode(
            logicalItemId = SAMPLE_LOGICAL_ITEM_ID,
            wrappedDek = byteArrayOf(21, 22, 23, 24),
            nonce = byteArrayOf(31, 32, 33, 34, 35, 36, 37, 38, 39, 40, 41, 42),
            ciphertext = byteArrayOf(51, 52, 53, 54),
            authTag = byteArrayOf(61, 62, 63, 64, 65, 66, 67, 68, 69, 70, 71, 72, 73, 74, 75, 76),
        ).also { it[0] = 0x02 }

        val error = assertThrows(IllegalArgumentException::class.java) {
            target.decode(payload)
        }

        assertEquals("SecureItem payload envelope version is not supported.", error.message)
    }
}

private val SAMPLE_LOGICAL_ITEM_ID: UUID = UUID.fromString("11111111-1111-1111-1111-111111111111")
