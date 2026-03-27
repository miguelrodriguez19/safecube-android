package com.miguelrodriguez19.safecube.core.vault.data.crypto

import com.miguelrodriguez19.safecube.core.crypto.domain.model.DecryptionRequest
import com.miguelrodriguez19.safecube.core.crypto.domain.model.EncryptionRequest
import com.miguelrodriguez19.safecube.core.crypto.domain.model.EncryptionResult
import com.miguelrodriguez19.safecube.core.crypto.domain.model.KeyUnwrapRequest
import com.miguelrodriguez19.safecube.core.crypto.domain.model.KeyWrapRequest
import com.miguelrodriguez19.safecube.core.crypto.domain.port.CryptoEngine
import com.miguelrodriguez19.safecube.core.crypto.domain.port.KeyWrapping
import com.miguelrodriguez19.safecube.core.crypto.domain.service.SaltGenerator
import com.miguelrodriguez19.safecube.core.vault.domain.codec.SecureItemContentCodec
import com.miguelrodriguez19.safecube.core.vault.domain.codec.SecureItemContentDecodeError
import com.miguelrodriguez19.safecube.core.vault.domain.codec.SecureItemContentDecodeResult
import com.miguelrodriguez19.safecube.core.vault.domain.model.secureitem.EncodedSecureItemContent
import com.miguelrodriguez19.safecube.core.vault.domain.model.secureitem.SecureItem
import com.miguelrodriguez19.safecube.core.vault.domain.model.secureitem.SecureItemType
import com.miguelrodriguez19.safecube.core.vault.domain.model.secureitem.itemcontent.NoteSecureItemContent
import com.miguelrodriguez19.safecube.core.vault.domain.model.secureitem.itemcontent.PasswordSecureItemContent
import com.miguelrodriguez19.safecube.core.vault.domain.service.SecureItemCryptoError
import com.miguelrodriguez19.safecube.core.vault.domain.service.SecureItemDecryptionResult
import com.miguelrodriguez19.safecube.core.vault.domain.service.SecureItemEncryptionResult
import io.mockk.confirmVerified
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.charset.StandardCharsets
import java.time.Instant
import java.util.UUID
import org.junit.Assert.assertArrayEquals
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class VaultItemCipherTest {

    private val contentCodec = mockk<SecureItemContentCodec>()
    private val cryptoEngine = mockk<CryptoEngine>()
    private val keyWrapping = mockk<KeyWrapping>()
    private val secureItemCryptoContextProvider = mockk<SecureItemCryptoContextProvider>()
    private val saltGenerator = mockk<SaltGenerator>()

    private val target = VaultItemCipher(
        contentCodec = contentCodec,
        cryptoEngine = cryptoEngine,
        keyWrapping = keyWrapping,
        secureItemCryptoContextProvider = secureItemCryptoContextProvider,
        secureItemPayloadAadFactory = SecureItemPayloadAadFactory(),
        secureItemPayloadEnvelopeV1Codec = SecureItemPayloadEnvelopeV1Codec(),
        saltGenerator = saltGenerator,
    )

    @Test
    fun `encrypt when vault is locked then returns vault locked error`() {
        val content = PasswordSecureItemContent(
            username = "user",
            password = "secret",
        )
        every { secureItemCryptoContextProvider.get() } returns SecureItemCryptoContextResult.VaultLocked

        val result = target.encrypt(
            logicalItemId = SAMPLE_LOGICAL_ITEM_ID,
            payloadVersion = 1,
            content = content,
        )

        assertEquals(
            SecureItemEncryptionResult.Error(SecureItemCryptoError.VaultLocked),
            result,
        )
        verify(exactly = 1) { secureItemCryptoContextProvider.get() }
        verify(exactly = 0) { contentCodec.encode(any()) }
        verify(exactly = 0) { saltGenerator.generate(any()) }
        verify(exactly = 0) { keyWrapping.wrapKey(any()) }
        verify(exactly = 0) { cryptoEngine.encrypt(any()) }
        confirmVerified(
            secureItemCryptoContextProvider,
            contentCodec,
            saltGenerator,
            keyWrapping,
            cryptoEngine,
        )
    }

    @Test
    fun `decrypt when vault is locked then returns vault locked error`() {
        every { secureItemCryptoContextProvider.get() } returns SecureItemCryptoContextResult.VaultLocked

        val result = target.decrypt(sampleSecureItem(payload = byteArrayOf(1, 2, 3)))

        assertEquals(
            SecureItemDecryptionResult.Error(SecureItemCryptoError.VaultLocked),
            result,
        )
        verify(exactly = 1) { secureItemCryptoContextProvider.get() }
        verify(exactly = 0) { keyWrapping.unwrapKey(any()) }
        verify(exactly = 0) { cryptoEngine.decrypt(any()) }
        verify(exactly = 0) { contentCodec.decode(any(), any(), any()) }
        confirmVerified(
            secureItemCryptoContextProvider,
            keyWrapping,
            cryptoEngine,
            contentCodec,
        )
    }

    @Test
    fun `encrypt when account id is unavailable then returns account id unavailable error`() {
        val content = PasswordSecureItemContent(
            username = "user",
            password = "secret",
        )

        every { secureItemCryptoContextProvider.get() } returns SecureItemCryptoContextResult.AccountIdUnavailable

        val result = target.encrypt(
            logicalItemId = SAMPLE_LOGICAL_ITEM_ID,
            payloadVersion = 1,
            content = content,
        )

        assertEquals(
            SecureItemEncryptionResult.Error(SecureItemCryptoError.AccountIdUnavailable),
            result,
        )
        verify(exactly = 1) { secureItemCryptoContextProvider.get() }
        verify(exactly = 0) { contentCodec.encode(any()) }
        verify(exactly = 0) { saltGenerator.generate(any()) }
        verify(exactly = 0) { keyWrapping.wrapKey(any()) }
        verify(exactly = 0) { cryptoEngine.encrypt(any()) }
        confirmVerified(
            secureItemCryptoContextProvider,
            contentCodec,
            saltGenerator,
            keyWrapping,
            cryptoEngine,
        )
    }

    @Test
    fun `encrypt when vault is unlocked then builds payload envelope and canonical aad`() {
        val content = PasswordSecureItemContent(
            username = "user",
            password = "secret",
        )
        val encodedPayload = "encoded-secret".toByteArray(StandardCharsets.UTF_8)
        val kek = byteArrayOf(11, 12, 13, 14)
        val dek = ByteArray(32) { index -> (index + 1).toByte() }
        val wrappedDek = byteArrayOf(21, 22, 23, 24)
        val iv = byteArrayOf(31, 32, 33, 34, 35, 36, 37, 38, 39, 40, 41, 42)
        val ciphertext = byteArrayOf(51, 52, 53, 54)
        val authTag = byteArrayOf(61, 62, 63, 64, 65, 66, 67, 68, 69, 70, 71, 72, 73, 74, 75, 76)
        var capturedWrapRequest: KeyWrapRequest? = null
        var capturedEncryptionRequest: EncryptionRequest? = null

        every { secureItemCryptoContextProvider.get() } returns SecureItemCryptoContextResult.Available(
            accountId = SAMPLE_ACCOUNT_ID,
            kek = kek,
        )
        every { contentCodec.encode(content) } returns EncodedSecureItemContent(
            itemType = SecureItemType.PASSWORD,
            schemaVersion = 1,
            payload = encodedPayload,
        )
        every { saltGenerator.generate(any()) } returns dek
        every { keyWrapping.wrapKey(any()) } answers {
            capturedWrapRequest = firstArg<KeyWrapRequest>().copy(
                keyToWrap = firstArg<KeyWrapRequest>().keyToWrap.copyOf(),
                wrappingKey = firstArg<KeyWrapRequest>().wrappingKey.copyOf(),
                aad = firstArg<KeyWrapRequest>().aad?.copyOf(),
            )
            wrappedDek
        }
        every { cryptoEngine.encrypt(any()) } answers {
            capturedEncryptionRequest = firstArg<EncryptionRequest>().copy(
                plaintext = firstArg<EncryptionRequest>().plaintext.copyOf(),
                keyMaterial = firstArg<EncryptionRequest>().keyMaterial.copyOf(),
                aad = firstArg<EncryptionRequest>().aad?.copyOf(),
            )
            EncryptionResult(
                ciphertext = ciphertext,
                iv = iv,
                authTag = authTag,
            )
        }

        val result = target.encrypt(
            logicalItemId = SAMPLE_LOGICAL_ITEM_ID,
            payloadVersion = 7,
            content = content,
        )

        val success = result as SecureItemEncryptionResult.Success
        assertEquals(SecureItemType.PASSWORD, success.payload.itemType)
        assertEquals(1, success.payload.schemaVersion)
        assertArrayEquals(
            expectedEnvelope(
                logicalItemId = SAMPLE_LOGICAL_ITEM_ID,
                wrappedDek = byteArrayOf(21, 22, 23, 24),
                nonce = byteArrayOf(31, 32, 33, 34, 35, 36, 37, 38, 39, 40, 41, 42),
                ciphertext = byteArrayOf(51, 52, 53, 54),
                authTag = byteArrayOf(61, 62, 63, 64, 65, 66, 67, 68, 69, 70, 71, 72, 73, 74, 75, 76),
            ),
            success.payload.payload,
        )
        assertArrayEquals(expectedAad(payloadVersion = 7), requireNotNull(capturedWrapRequest).aad)
        assertArrayEquals(expectedAad(payloadVersion = 7), requireNotNull(capturedEncryptionRequest).aad)
        assertArrayEquals(ByteArray(32) { index -> (index + 1).toByte() }, requireNotNull(capturedWrapRequest).keyToWrap)
        assertArrayEquals(ByteArray(32) { index -> (index + 1).toByte() }, requireNotNull(capturedEncryptionRequest).keyMaterial)
        verify(exactly = 1) { secureItemCryptoContextProvider.get() }
        verify(exactly = 1) { contentCodec.encode(content) }
        verify(exactly = 1) { saltGenerator.generate(any()) }
        verify(exactly = 1) { keyWrapping.wrapKey(any()) }
        verify(exactly = 1) { cryptoEngine.encrypt(any()) }
        confirmVerified(
            secureItemCryptoContextProvider,
            contentCodec,
            saltGenerator,
            keyWrapping,
            cryptoEngine,
        )
    }

    @Test
    fun `encrypt when key wrapping fails then returns cryptographic failure`() {
        val content = PasswordSecureItemContent(
            username = "user",
            password = "secret",
        )

        every { secureItemCryptoContextProvider.get() } returns SecureItemCryptoContextResult.Available(
            accountId = SAMPLE_ACCOUNT_ID,
            kek = byteArrayOf(11, 12, 13, 14),
        )
        every { contentCodec.encode(content) } returns EncodedSecureItemContent(
            itemType = SecureItemType.PASSWORD,
            schemaVersion = 1,
            payload = "encoded-secret".toByteArray(StandardCharsets.UTF_8),
        )
        every { saltGenerator.generate(any()) } returns ByteArray(32) { index -> (index + 1).toByte() }
        every { keyWrapping.wrapKey(any()) } throws IllegalStateException("wrap failed")

        val result = target.encrypt(
            logicalItemId = SAMPLE_LOGICAL_ITEM_ID,
            payloadVersion = 7,
            content = content,
        )

        assertEquals(
            SecureItemEncryptionResult.Error(SecureItemCryptoError.CryptographicFailure),
            result,
        )
        verify(exactly = 1) { secureItemCryptoContextProvider.get() }
        verify(exactly = 1) { contentCodec.encode(content) }
        verify(exactly = 1) { saltGenerator.generate(any()) }
        verify(exactly = 1) { keyWrapping.wrapKey(any()) }
        verify(exactly = 0) { cryptoEngine.encrypt(any()) }
        confirmVerified(
            secureItemCryptoContextProvider,
            contentCodec,
            saltGenerator,
            keyWrapping,
            cryptoEngine,
        )
    }

    @Test
    fun `encrypt when wrapped dek exceeds envelope limit then returns cryptographic failure`() {
        val content = PasswordSecureItemContent(
            username = "user",
            password = "secret",
        )

        every { secureItemCryptoContextProvider.get() } returns SecureItemCryptoContextResult.Available(
            accountId = SAMPLE_ACCOUNT_ID,
            kek = byteArrayOf(11, 12, 13, 14),
        )
        every { contentCodec.encode(content) } returns EncodedSecureItemContent(
            itemType = SecureItemType.PASSWORD,
            schemaVersion = 1,
            payload = "encoded-secret".toByteArray(StandardCharsets.UTF_8),
        )
        every { saltGenerator.generate(any()) } returns ByteArray(32) { index -> (index + 1).toByte() }
        every { keyWrapping.wrapKey(any()) } returns ByteArray(UShort.MAX_VALUE.toInt() + 1)

        val result = target.encrypt(
            logicalItemId = SAMPLE_LOGICAL_ITEM_ID,
            payloadVersion = 7,
            content = content,
        )

        assertEquals(
            SecureItemEncryptionResult.Error(SecureItemCryptoError.CryptographicFailure),
            result,
        )
        verify(exactly = 1) { secureItemCryptoContextProvider.get() }
        verify(exactly = 1) { contentCodec.encode(content) }
        verify(exactly = 1) { saltGenerator.generate(any()) }
        verify(exactly = 1) { keyWrapping.wrapKey(any()) }
        verify(exactly = 1) { cryptoEngine.encrypt(any()) }
        confirmVerified(
            secureItemCryptoContextProvider,
            contentCodec,
            saltGenerator,
            keyWrapping,
            cryptoEngine,
        )
    }

    @Test
    fun `encrypt when crypto engine returns invalid nonce then returns cryptographic failure`() {
        val content = PasswordSecureItemContent(
            username = "user",
            password = "secret",
        )

        every { secureItemCryptoContextProvider.get() } returns SecureItemCryptoContextResult.Available(
            accountId = SAMPLE_ACCOUNT_ID,
            kek = byteArrayOf(11, 12, 13, 14),
        )
        every { contentCodec.encode(content) } returns EncodedSecureItemContent(
            itemType = SecureItemType.PASSWORD,
            schemaVersion = 1,
            payload = "encoded-secret".toByteArray(StandardCharsets.UTF_8),
        )
        every { saltGenerator.generate(any()) } returns ByteArray(32) { index -> (index + 1).toByte() }
        every { keyWrapping.wrapKey(any()) } returns byteArrayOf(21, 22, 23, 24)
        every { cryptoEngine.encrypt(any()) } returns EncryptionResult(
            ciphertext = byteArrayOf(51, 52, 53, 54),
            iv = byteArrayOf(1, 2, 3),
            authTag = byteArrayOf(61, 62, 63, 64, 65, 66, 67, 68, 69, 70, 71, 72, 73, 74, 75, 76),
        )

        val result = target.encrypt(
            logicalItemId = SAMPLE_LOGICAL_ITEM_ID,
            payloadVersion = 7,
            content = content,
        )

        assertEquals(
            SecureItemEncryptionResult.Error(SecureItemCryptoError.CryptographicFailure),
            result,
        )
        verify(exactly = 1) { secureItemCryptoContextProvider.get() }
        verify(exactly = 1) { contentCodec.encode(content) }
        verify(exactly = 1) { saltGenerator.generate(any()) }
        verify(exactly = 1) { keyWrapping.wrapKey(any()) }
        verify(exactly = 1) { cryptoEngine.encrypt(any()) }
        confirmVerified(
            secureItemCryptoContextProvider,
            contentCodec,
            saltGenerator,
            keyWrapping,
            cryptoEngine,
        )
    }

    @Test
    fun `encrypt when crypto engine returns invalid auth tag then returns cryptographic failure`() {
        val content = PasswordSecureItemContent(
            username = "user",
            password = "secret",
        )

        every { secureItemCryptoContextProvider.get() } returns SecureItemCryptoContextResult.Available(
            accountId = SAMPLE_ACCOUNT_ID,
            kek = byteArrayOf(11, 12, 13, 14),
        )
        every { contentCodec.encode(content) } returns EncodedSecureItemContent(
            itemType = SecureItemType.PASSWORD,
            schemaVersion = 1,
            payload = "encoded-secret".toByteArray(StandardCharsets.UTF_8),
        )
        every { saltGenerator.generate(any()) } returns ByteArray(32) { index -> (index + 1).toByte() }
        every { keyWrapping.wrapKey(any()) } returns byteArrayOf(21, 22, 23, 24)
        every { cryptoEngine.encrypt(any()) } returns EncryptionResult(
            ciphertext = byteArrayOf(51, 52, 53, 54),
            iv = byteArrayOf(31, 32, 33, 34, 35, 36, 37, 38, 39, 40, 41, 42),
            authTag = byteArrayOf(61, 62, 63),
        )

        val result = target.encrypt(
            logicalItemId = SAMPLE_LOGICAL_ITEM_ID,
            payloadVersion = 7,
            content = content,
        )

        assertEquals(
            SecureItemEncryptionResult.Error(SecureItemCryptoError.CryptographicFailure),
            result,
        )
        verify(exactly = 1) { secureItemCryptoContextProvider.get() }
        verify(exactly = 1) { contentCodec.encode(content) }
        verify(exactly = 1) { saltGenerator.generate(any()) }
        verify(exactly = 1) { keyWrapping.wrapKey(any()) }
        verify(exactly = 1) { cryptoEngine.encrypt(any()) }
        confirmVerified(
            secureItemCryptoContextProvider,
            contentCodec,
            saltGenerator,
            keyWrapping,
            cryptoEngine,
        )
    }

    @Test
    fun `encrypt when crypto engine returns empty ciphertext then returns cryptographic failure`() {
        val content = PasswordSecureItemContent(
            username = "user",
            password = "secret",
        )

        every { secureItemCryptoContextProvider.get() } returns SecureItemCryptoContextResult.Available(
            accountId = SAMPLE_ACCOUNT_ID,
            kek = byteArrayOf(11, 12, 13, 14),
        )
        every { contentCodec.encode(content) } returns EncodedSecureItemContent(
            itemType = SecureItemType.PASSWORD,
            schemaVersion = 1,
            payload = "encoded-secret".toByteArray(StandardCharsets.UTF_8),
        )
        every { saltGenerator.generate(any()) } returns ByteArray(32) { index -> (index + 1).toByte() }
        every { keyWrapping.wrapKey(any()) } returns byteArrayOf(21, 22, 23, 24)
        every { cryptoEngine.encrypt(any()) } returns EncryptionResult(
            ciphertext = byteArrayOf(),
            iv = byteArrayOf(31, 32, 33, 34, 35, 36, 37, 38, 39, 40, 41, 42),
            authTag = byteArrayOf(61, 62, 63, 64, 65, 66, 67, 68, 69, 70, 71, 72, 73, 74, 75, 76),
        )

        val result = target.encrypt(
            logicalItemId = SAMPLE_LOGICAL_ITEM_ID,
            payloadVersion = 7,
            content = content,
        )

        assertEquals(
            SecureItemEncryptionResult.Error(SecureItemCryptoError.CryptographicFailure),
            result,
        )
        verify(exactly = 1) { secureItemCryptoContextProvider.get() }
        verify(exactly = 1) { contentCodec.encode(content) }
        verify(exactly = 1) { saltGenerator.generate(any()) }
        verify(exactly = 1) { keyWrapping.wrapKey(any()) }
        verify(exactly = 1) { cryptoEngine.encrypt(any()) }
        confirmVerified(
            secureItemCryptoContextProvider,
            contentCodec,
            saltGenerator,
            keyWrapping,
            cryptoEngine,
        )
    }

    @Test
    fun `decrypt when payload is valid then unwraps decrypts and decodes content`() {
        val wrappedDek = byteArrayOf(21, 22, 23, 24)
        val nonce = byteArrayOf(31, 32, 33, 34, 35, 36, 37, 38, 39, 40, 41, 42)
        val ciphertext = byteArrayOf(51, 52, 53, 54)
        val authTag = byteArrayOf(61, 62, 63, 64, 65, 66, 67, 68, 69, 70, 71, 72, 73, 74, 75, 76)
        val payload = expectedEnvelope(
            logicalItemId = SAMPLE_LOGICAL_ITEM_ID,
            wrappedDek = wrappedDek,
            nonce = nonce,
            ciphertext = ciphertext,
            authTag = authTag,
        )
        val item = sampleSecureItem(payload = payload)
        val kek = byteArrayOf(11, 12, 13, 14)
        val dek = ByteArray(32) { index -> (index + 1).toByte() }
        val plaintext = """{"noteBody":"hello"}""".toByteArray(StandardCharsets.UTF_8)
        var capturedUnwrapRequest: KeyUnwrapRequest? = null
        var capturedDecryptionRequest: DecryptionRequest? = null
        val decodedContent = NoteSecureItemContent(body = "hello")

        every { secureItemCryptoContextProvider.get() } returns SecureItemCryptoContextResult.Available(
            accountId = SAMPLE_ACCOUNT_ID,
            kek = kek,
        )
        every { keyWrapping.unwrapKey(any()) } answers {
            capturedUnwrapRequest = firstArg<KeyUnwrapRequest>().copy(
                wrappedKey = firstArg<KeyUnwrapRequest>().wrappedKey.copyOf(),
                wrappingKey = firstArg<KeyUnwrapRequest>().wrappingKey.copyOf(),
                aad = firstArg<KeyUnwrapRequest>().aad?.copyOf(),
            )
            dek
        }
        every { cryptoEngine.decrypt(any()) } answers {
            capturedDecryptionRequest = firstArg<DecryptionRequest>().copy(
                ciphertext = firstArg<DecryptionRequest>().ciphertext.copyOf(),
                keyMaterial = firstArg<DecryptionRequest>().keyMaterial.copyOf(),
                iv = firstArg<DecryptionRequest>().iv.copyOf(),
                aad = firstArg<DecryptionRequest>().aad?.copyOf(),
                authTag = firstArg<DecryptionRequest>().authTag.copyOf(),
            )
            plaintext
        }
        every {
            contentCodec.decode(
                itemType = SecureItemType.NOTE.wireName,
                schemaVersion = 1,
                payload = any(),
            )
        } returns SecureItemContentDecodeResult.Success(decodedContent)

        val result = target.decrypt(item)

        assertEquals(SecureItemDecryptionResult.Success(decodedContent), result)
        assertArrayEquals(expectedAad(payloadVersion = item.payloadVersion), requireNotNull(capturedUnwrapRequest).aad)
        assertArrayEquals(expectedAad(payloadVersion = item.payloadVersion), requireNotNull(capturedDecryptionRequest).aad)
        assertArrayEquals(byteArrayOf(21, 22, 23, 24), requireNotNull(capturedUnwrapRequest).wrappedKey)
        assertArrayEquals(byteArrayOf(51, 52, 53, 54), requireNotNull(capturedDecryptionRequest).ciphertext)
        assertArrayEquals(byteArrayOf(31, 32, 33, 34, 35, 36, 37, 38, 39, 40, 41, 42), requireNotNull(capturedDecryptionRequest).iv)
        assertArrayEquals(byteArrayOf(61, 62, 63, 64, 65, 66, 67, 68, 69, 70, 71, 72, 73, 74, 75, 76), requireNotNull(capturedDecryptionRequest).authTag)
        verify(exactly = 1) { secureItemCryptoContextProvider.get() }
        verify(exactly = 1) { keyWrapping.unwrapKey(any()) }
        verify(exactly = 1) { cryptoEngine.decrypt(any()) }
        verify(exactly = 1) { contentCodec.decode(SecureItemType.NOTE.wireName, 1, any()) }
        confirmVerified(
            secureItemCryptoContextProvider,
            keyWrapping,
            cryptoEngine,
            contentCodec,
        )
    }

    @Test
    fun `decrypt when account id is unavailable then returns account id unavailable error`() {
        every { secureItemCryptoContextProvider.get() } returns SecureItemCryptoContextResult.AccountIdUnavailable

        val result = target.decrypt(sampleSecureItem(payload = expectedEnvelope(
            logicalItemId = SAMPLE_LOGICAL_ITEM_ID,
            wrappedDek = byteArrayOf(21, 22, 23, 24),
            nonce = byteArrayOf(31, 32, 33, 34, 35, 36, 37, 38, 39, 40, 41, 42),
            ciphertext = byteArrayOf(51, 52, 53, 54),
            authTag = byteArrayOf(61, 62, 63, 64, 65, 66, 67, 68, 69, 70, 71, 72, 73, 74, 75, 76),
        )))

        assertEquals(
            SecureItemDecryptionResult.Error(SecureItemCryptoError.AccountIdUnavailable),
            result,
        )
        verify(exactly = 1) { secureItemCryptoContextProvider.get() }
        verify(exactly = 0) { keyWrapping.unwrapKey(any()) }
        verify(exactly = 0) { cryptoEngine.decrypt(any()) }
        verify(exactly = 0) { contentCodec.decode(any(), any(), any()) }
        confirmVerified(
            secureItemCryptoContextProvider,
            keyWrapping,
            cryptoEngine,
            contentCodec,
        )
    }

    @Test
    fun `decrypt when payload envelope version is unsupported then returns malformed payload`() {
        val invalidPayload = expectedEnvelope(
            logicalItemId = SAMPLE_LOGICAL_ITEM_ID,
            wrappedDek = byteArrayOf(21, 22, 23, 24),
            nonce = byteArrayOf(31, 32, 33, 34, 35, 36, 37, 38, 39, 40, 41, 42),
            ciphertext = byteArrayOf(51, 52, 53, 54),
            authTag = byteArrayOf(61, 62, 63, 64, 65, 66, 67, 68, 69, 70, 71, 72, 73, 74, 75, 76),
        ).also { it[0] = 0x02 }

        every { secureItemCryptoContextProvider.get() } returns SecureItemCryptoContextResult.Available(
            accountId = SAMPLE_ACCOUNT_ID,
            kek = byteArrayOf(11, 12, 13, 14),
        )

        val result = target.decrypt(sampleSecureItem(payload = invalidPayload))

        assertEquals(
            SecureItemDecryptionResult.Error(SecureItemCryptoError.MalformedPayload),
            result,
        )
        verify(exactly = 1) { secureItemCryptoContextProvider.get() }
        verify(exactly = 0) { keyWrapping.unwrapKey(any()) }
        verify(exactly = 0) { cryptoEngine.decrypt(any()) }
        verify(exactly = 0) { contentCodec.decode(any(), any(), any()) }
        confirmVerified(
            secureItemCryptoContextProvider,
            keyWrapping,
            cryptoEngine,
            contentCodec,
        )
    }

    @Test
    fun `decrypt when payload envelope is too short then returns malformed payload`() {
        every { secureItemCryptoContextProvider.get() } returns SecureItemCryptoContextResult.Available(
            accountId = SAMPLE_ACCOUNT_ID,
            kek = byteArrayOf(11, 12, 13, 14),
        )

        val result = target.decrypt(sampleSecureItem(payload = byteArrayOf(0x01, 0x02)))

        assertEquals(
            SecureItemDecryptionResult.Error(SecureItemCryptoError.MalformedPayload),
            result,
        )
        verify(exactly = 1) { secureItemCryptoContextProvider.get() }
        verify(exactly = 0) { keyWrapping.unwrapKey(any()) }
        verify(exactly = 0) { cryptoEngine.decrypt(any()) }
        verify(exactly = 0) { contentCodec.decode(any(), any(), any()) }
        confirmVerified(
            secureItemCryptoContextProvider,
            keyWrapping,
            cryptoEngine,
            contentCodec,
        )
    }

    @Test
    fun `decrypt when wrapped dek length exceeds payload bounds then returns malformed payload`() {
        val invalidPayload = expectedEnvelope(
            logicalItemId = SAMPLE_LOGICAL_ITEM_ID,
            wrappedDek = byteArrayOf(21, 22, 23, 24),
            nonce = byteArrayOf(31, 32, 33, 34, 35, 36, 37, 38, 39, 40, 41, 42),
            ciphertext = byteArrayOf(51, 52, 53, 54),
            authTag = byteArrayOf(61, 62, 63, 64, 65, 66, 67, 68, 69, 70, 71, 72, 73, 74, 75, 76),
        ).also {
            it[17] = 0x7F
            it[18] = 0x7F
        }

        every { secureItemCryptoContextProvider.get() } returns SecureItemCryptoContextResult.Available(
            accountId = SAMPLE_ACCOUNT_ID,
            kek = byteArrayOf(11, 12, 13, 14),
        )

        val result = target.decrypt(sampleSecureItem(payload = invalidPayload))

        assertEquals(
            SecureItemDecryptionResult.Error(SecureItemCryptoError.MalformedPayload),
            result,
        )
        verify(exactly = 1) { secureItemCryptoContextProvider.get() }
        verify(exactly = 0) { keyWrapping.unwrapKey(any()) }
        verify(exactly = 0) { cryptoEngine.decrypt(any()) }
        verify(exactly = 0) { contentCodec.decode(any(), any(), any()) }
        confirmVerified(
            secureItemCryptoContextProvider,
            keyWrapping,
            cryptoEngine,
            contentCodec,
        )
    }

    @Test
    fun `decrypt when wrapped dek length is zero then returns malformed payload`() {
        val invalidPayload = expectedEnvelope(
            logicalItemId = SAMPLE_LOGICAL_ITEM_ID,
            wrappedDek = byteArrayOf(21, 22, 23, 24),
            nonce = byteArrayOf(31, 32, 33, 34, 35, 36, 37, 38, 39, 40, 41, 42),
            ciphertext = byteArrayOf(51, 52, 53, 54),
            authTag = byteArrayOf(61, 62, 63, 64, 65, 66, 67, 68, 69, 70, 71, 72, 73, 74, 75, 76),
        ).also {
            it[17] = 0x00
            it[18] = 0x00
        }

        every { secureItemCryptoContextProvider.get() } returns SecureItemCryptoContextResult.Available(
            accountId = SAMPLE_ACCOUNT_ID,
            kek = byteArrayOf(11, 12, 13, 14),
        )

        val result = target.decrypt(sampleSecureItem(payload = invalidPayload))

        assertEquals(
            SecureItemDecryptionResult.Error(SecureItemCryptoError.MalformedPayload),
            result,
        )
        verify(exactly = 1) { secureItemCryptoContextProvider.get() }
        verify(exactly = 0) { keyWrapping.unwrapKey(any()) }
        verify(exactly = 0) { cryptoEngine.decrypt(any()) }
        verify(exactly = 0) { contentCodec.decode(any(), any(), any()) }
        confirmVerified(
            secureItemCryptoContextProvider,
            keyWrapping,
            cryptoEngine,
            contentCodec,
        )
    }

    @Test
    fun `decrypt when payload logical item id differs then returns malformed payload without crypto`() {
        val item = sampleSecureItem(
            payload = expectedEnvelope(
                logicalItemId = UUID.fromString("bbbbbbbb-bbbb-bbbb-bbbb-bbbbbbbbbbbb"),
                wrappedDek = byteArrayOf(21, 22, 23, 24),
                nonce = byteArrayOf(31, 32, 33, 34, 35, 36, 37, 38, 39, 40, 41, 42),
                ciphertext = byteArrayOf(51, 52, 53, 54),
                authTag = byteArrayOf(61, 62, 63, 64, 65, 66, 67, 68, 69, 70, 71, 72, 73, 74, 75, 76),
            ),
        )

        every { secureItemCryptoContextProvider.get() } returns SecureItemCryptoContextResult.Available(
            accountId = SAMPLE_ACCOUNT_ID,
            kek = byteArrayOf(11, 12, 13, 14),
        )

        val result = target.decrypt(item)

        assertEquals(
            SecureItemDecryptionResult.Error(SecureItemCryptoError.MalformedPayload),
            result,
        )
        verify(exactly = 1) { secureItemCryptoContextProvider.get() }
        verify(exactly = 0) { keyWrapping.unwrapKey(any()) }
        verify(exactly = 0) { cryptoEngine.decrypt(any()) }
        verify(exactly = 0) { contentCodec.decode(any(), any(), any()) }
        confirmVerified(
            secureItemCryptoContextProvider,
            keyWrapping,
            cryptoEngine,
            contentCodec,
        )
    }

    @Test
    fun `decrypt when crypto engine fails then returns cryptographic failure`() {
        val item = sampleSecureItem(
            payload = expectedEnvelope(
                logicalItemId = SAMPLE_LOGICAL_ITEM_ID,
                wrappedDek = byteArrayOf(21, 22, 23, 24),
                nonce = byteArrayOf(31, 32, 33, 34, 35, 36, 37, 38, 39, 40, 41, 42),
                ciphertext = byteArrayOf(51, 52, 53, 54),
                authTag = byteArrayOf(61, 62, 63, 64, 65, 66, 67, 68, 69, 70, 71, 72, 73, 74, 75, 76),
            ),
        )

        every { secureItemCryptoContextProvider.get() } returns SecureItemCryptoContextResult.Available(
            accountId = SAMPLE_ACCOUNT_ID,
            kek = byteArrayOf(11, 12, 13, 14),
        )
        every { keyWrapping.unwrapKey(any()) } returns ByteArray(32) { index -> (index + 1).toByte() }
        every { cryptoEngine.decrypt(any()) } throws IllegalStateException("authentication failed")

        val result = target.decrypt(item)

        assertEquals(
            SecureItemDecryptionResult.Error(SecureItemCryptoError.CryptographicFailure),
            result,
        )
        verify(exactly = 1) { secureItemCryptoContextProvider.get() }
        verify(exactly = 1) { keyWrapping.unwrapKey(any()) }
        verify(exactly = 1) { cryptoEngine.decrypt(any()) }
        verify(exactly = 0) { contentCodec.decode(any(), any(), any()) }
        confirmVerified(
            secureItemCryptoContextProvider,
            keyWrapping,
            cryptoEngine,
            contentCodec,
        )
    }

    @Test
    fun `decrypt when content codec cannot decode plaintext then returns content decoding failed`() {
        val item = sampleSecureItem(
            payload = expectedEnvelope(
                logicalItemId = SAMPLE_LOGICAL_ITEM_ID,
                wrappedDek = byteArrayOf(21, 22, 23, 24),
                nonce = byteArrayOf(31, 32, 33, 34, 35, 36, 37, 38, 39, 40, 41, 42),
                ciphertext = byteArrayOf(51, 52, 53, 54),
                authTag = byteArrayOf(61, 62, 63, 64, 65, 66, 67, 68, 69, 70, 71, 72, 73, 74, 75, 76),
            ),
            itemType = SecureItemType.PASSWORD,
        )
        val decodeError = SecureItemContentDecodeError.UnsupportedSchemaVersion(
            itemType = SecureItemType.PASSWORD.wireName,
            schemaVersion = 1,
        )

        every { secureItemCryptoContextProvider.get() } returns SecureItemCryptoContextResult.Available(
            accountId = SAMPLE_ACCOUNT_ID,
            kek = byteArrayOf(11, 12, 13, 14),
        )
        every { keyWrapping.unwrapKey(any()) } returns ByteArray(32) { index -> (index + 1).toByte() }
        every { cryptoEngine.decrypt(any()) } returns """{"password":"secret"}""".toByteArray(StandardCharsets.UTF_8)
        every {
            contentCodec.decode(
                itemType = SecureItemType.PASSWORD.wireName,
                schemaVersion = 1,
                payload = any(),
            )
        } returns SecureItemContentDecodeResult.Error(decodeError)

        val result = target.decrypt(item)

        assertTrue(result is SecureItemDecryptionResult.Error)
        assertEquals(
            SecureItemCryptoError.ContentDecodingFailed(decodeError),
            (result as SecureItemDecryptionResult.Error).reason,
        )
        verify(exactly = 1) { secureItemCryptoContextProvider.get() }
        verify(exactly = 1) { keyWrapping.unwrapKey(any()) }
        verify(exactly = 1) { cryptoEngine.decrypt(any()) }
        verify(exactly = 1) { contentCodec.decode(SecureItemType.PASSWORD.wireName, 1, any()) }
        confirmVerified(
            secureItemCryptoContextProvider,
            keyWrapping,
            cryptoEngine,
            contentCodec,
        )
    }
}

private val SAMPLE_ACCOUNT_ID: UUID = UUID.fromString("aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa")
private val SAMPLE_LOGICAL_ITEM_ID: UUID = UUID.fromString("11111111-1111-1111-1111-111111111111")

private fun sampleSecureItem(
    payload: ByteArray,
    itemType: SecureItemType = SecureItemType.NOTE,
): SecureItem = SecureItem(
    logicalItemId = SAMPLE_LOGICAL_ITEM_ID,
    remoteItemId = null,
    itemType = itemType,
    schemaVersion = 1,
    displayHint = "Example",
    payload = payload,
    payloadVersion = 7,
    createdAt = Instant.parse("2026-03-25T09:00:00Z"),
    updatedAt = Instant.parse("2026-03-25T10:00:00Z"),
)

private fun expectedAad(payloadVersion: Long): ByteArray = buildString {
    append("accountId:")
    append(SAMPLE_ACCOUNT_ID)
    append("|logicalItemId:")
    append(SAMPLE_LOGICAL_ITEM_ID)
    append("|payloadVersion:")
    append(payloadVersion)
}.toByteArray(StandardCharsets.UTF_8)

private fun expectedEnvelope(
    logicalItemId: UUID,
    wrappedDek: ByteArray,
    nonce: ByteArray,
    ciphertext: ByteArray,
    authTag: ByteArray,
): ByteArray {
    val output = ByteArray(1 + 16 + 2 + wrappedDek.size + nonce.size + ciphertext.size + authTag.size)
    var offset = 0

    output[offset] = 0x01
    offset += 1

    ByteBuffer.allocate(16)
        .order(ByteOrder.BIG_ENDIAN)
        .putLong(logicalItemId.mostSignificantBits)
        .putLong(logicalItemId.leastSignificantBits)
        .array()
        .copyInto(output, destinationOffset = offset)
    offset += 16

    output[offset] = (wrappedDek.size ushr 8).toByte()
    output[offset + 1] = wrappedDek.size.toByte()
    offset += 2

    wrappedDek.copyInto(output, destinationOffset = offset)
    offset += wrappedDek.size

    nonce.copyInto(output, destinationOffset = offset)
    offset += nonce.size

    ciphertext.copyInto(output, destinationOffset = offset)
    offset += ciphertext.size

    authTag.copyInto(output, destinationOffset = offset)

    return output
}
