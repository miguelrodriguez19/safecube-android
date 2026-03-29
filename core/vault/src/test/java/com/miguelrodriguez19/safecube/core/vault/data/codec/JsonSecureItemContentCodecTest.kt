package com.miguelrodriguez19.safecube.core.vault.data.codec

import com.miguelrodriguez19.safecube.core.vault.domain.codec.SecureItemContentDecodeError
import com.miguelrodriguez19.safecube.core.vault.domain.codec.SecureItemContentDecodeResult
import com.miguelrodriguez19.safecube.core.vault.domain.model.secureitem.itemcontent.NoteSecureItemContent
import com.miguelrodriguez19.safecube.core.vault.domain.model.secureitem.itemcontent.PasswordSecureItemContent
import com.miguelrodriguez19.safecube.core.vault.domain.model.secureitem.itemcontent.PasswordTotpSecureItemContent
import com.miguelrodriguez19.safecube.core.vault.domain.model.secureitem.SecureItemType
import com.miguelrodriguez19.safecube.core.vault.domain.model.secureitem.itemcontent.PasswordWebsiteSecureItemContent
import java.nio.charset.StandardCharsets
import org.junit.Assert.assertArrayEquals
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class JsonSecureItemContentCodecTest {
    private val codec = JsonSecureItemContentCodec(
        passwordSecureItemContentJsonAdapter = PasswordSecureItemContentJsonAdapter(),
        noteSecureItemContentJsonAdapter = NoteSecureItemContentJsonAdapter(),
    )

    @Test
    fun `encode password content serializes deterministic payload from model`() {
        val encoded = codec.encode(
            PasswordSecureItemContent(
                username = "alice",
                email = "alice@example.com",
                password = "s3cr3t",
                website = PasswordWebsiteSecureItemContent(
                    url = "https://example.com",
                    domain = "example.com",
                ),
                notes = "personal",
                totp = PasswordTotpSecureItemContent(
                    secret = "BASE32SECRET",
                    issuer = "Example",
                    accountName = "alice@example.com",
                ),
            ),
        )

        assertEquals(SecureItemType.PASSWORD, encoded.itemType)
        assertEquals(1, encoded.schemaVersion)
        assertArrayEquals(
            """{"username":"alice","email":"alice@example.com","password":"s3cr3t","website":{"url":"https://example.com","domain":"example.com"},"notes":"personal","totp":{"secret":"BASE32SECRET","issuer":"Example","accountName":"alice@example.com"}}"""
                .toByteArray(StandardCharsets.UTF_8),
            encoded.payload,
        )
    }

    @Test
    fun `encode password omits null optional fields`() {
        val encoded = codec.encode(
            PasswordSecureItemContent(
                email = "alice@example.com",
                password = "s3cr3t",
            ),
        )

        assertArrayEquals(
            """{"email":"alice@example.com","password":"s3cr3t"}""".toByteArray(StandardCharsets.UTF_8),
            encoded.payload,
        )
    }

    @Test
    fun `encode note content serializes note body under canonical field name`() {
        val encoded = codec.encode(
            NoteSecureItemContent(
                body = "private text",
            ),
        )

        assertEquals(SecureItemType.NOTE, encoded.itemType)
        assertEquals(1, encoded.schemaVersion)
        assertArrayEquals(
            """{"body":"private text"}""".toByteArray(StandardCharsets.UTF_8),
            encoded.payload,
        )
    }

    @Test
    fun `decode password content rehydrates enriched domain model`() {
        val result = codec.decode(
            itemType = "PASSWORD",
            schemaVersion = 1,
            payload = """{"username":"alice","email":"alice@example.com","password":"s3cr3t","website":{"url":"https://example.com","domain":"example.com"},"notes":"personal","totp":{"secret":"BASE32SECRET","issuer":"Example","accountName":"alice@example.com"}}"""
                .toByteArray(StandardCharsets.UTF_8),
        )

        assertEquals(
            SecureItemContentDecodeResult.Success(
                PasswordSecureItemContent(
                    username = "alice",
                    email = "alice@example.com",
                    password = "s3cr3t",
                    website = PasswordWebsiteSecureItemContent(
                        url = "https://example.com",
                        domain = "example.com",
                    ),
                    notes = "personal",
                    totp = PasswordTotpSecureItemContent(
                        secret = "BASE32SECRET",
                        issuer = "Example",
                        accountName = "alice@example.com",
                    ),
                ),
            ),
            result,
        )
    }

    @Test
    fun `decode note content rehydrates domain model`() {
        val result = codec.decode(
            itemType = "NOTE",
            schemaVersion = 1,
            payload = """{"body":"private text"}""".toByteArray(StandardCharsets.UTF_8),
        )

        assertEquals(
            SecureItemContentDecodeResult.Success(
                NoteSecureItemContent(
                    body = "private text",
                ),
            ),
            result,
        )
    }

    @Test
    fun `decode rejects unsupported item type`() {
        val result = codec.decode(
            itemType = "CARD",
            schemaVersion = 1,
            payload = """{"holder":"Alice"}""".toByteArray(StandardCharsets.UTF_8),
        )

        assertEquals(
            SecureItemContentDecodeResult.Error(
                SecureItemContentDecodeError.UnsupportedItemType("CARD"),
            ),
            result,
        )
    }

    @Test
    fun `decode rejects unsupported schema version`() {
        val result = codec.decode(
            itemType = "PASSWORD",
            schemaVersion = 2,
            payload = """{"email":"alice@example.com","password":"s3cr3t"}""".toByteArray(StandardCharsets.UTF_8),
        )

        assertEquals(
            SecureItemContentDecodeResult.Error(
                SecureItemContentDecodeError.UnsupportedSchemaVersion(
                    itemType = "PASSWORD",
                    schemaVersion = 2,
                ),
            ),
            result,
        )
    }

    @Test
    fun `decode rejects unsupported note schema version`() {
        val result = codec.decode(
            itemType = "NOTE",
            schemaVersion = 2,
            payload = """{"noteBody":"private text"}""".toByteArray(StandardCharsets.UTF_8),
        )

        assertEquals(
            SecureItemContentDecodeResult.Error(
                SecureItemContentDecodeError.UnsupportedSchemaVersion(
                    itemType = "NOTE",
                    schemaVersion = 2,
                ),
            ),
            result,
        )
    }

    @Test
    fun `decode rejects malformed json payload`() {
        val result = codec.decode(
            itemType = "NOTE",
            schemaVersion = 1,
            payload = """{"noteBody":""".toByteArray(StandardCharsets.UTF_8),
        )

        assertTrue(result is SecureItemContentDecodeResult.Error)
        assertTrue((result as SecureItemContentDecodeResult.Error).reason is SecureItemContentDecodeError.InvalidPayload)
    }

    @Test
    fun `decode rejects unknown password fields`() {
        val result = codec.decode(
            itemType = "PASSWORD",
            schemaVersion = 1,
            payload = """{"email":"alice@example.com","password":"s3cr3t","extra":"boom"}"""
                .toByteArray(StandardCharsets.UTF_8),
        )

        assertTrue(result is SecureItemContentDecodeResult.Error)
        assertTrue((result as SecureItemContentDecodeResult.Error).reason is SecureItemContentDecodeError.InvalidPayload)
    }

    @Test
    fun `decode rejects unknown note fields`() {
        val result = codec.decode(
            itemType = "NOTE",
            schemaVersion = 1,
            payload = """{"noteBody":"private text","extra":"boom"}""".toByteArray(StandardCharsets.UTF_8),
        )

        assertTrue(result is SecureItemContentDecodeResult.Error)
        assertTrue((result as SecureItemContentDecodeResult.Error).reason is SecureItemContentDecodeError.InvalidPayload)
    }

    @Test
    fun `decode rejects password without username or email`() {
        val result = codec.decode(
            itemType = "PASSWORD",
            schemaVersion = 1,
            payload = """{"password":"s3cr3t"}""".toByteArray(StandardCharsets.UTF_8),
        )

        assertEquals(
            SecureItemContentDecodeResult.Error(
                SecureItemContentDecodeError.InvalidPayload(
                    "at least one of username or email must be present.",
                ),
            ),
            result,
        )
    }

    @Test
    fun `decode roundtrip preserves password payload bytes`() {
        val encoded = codec.encode(
            PasswordSecureItemContent(
                email = "alice@example.com",
                password = "s3cr3t",
                website = PasswordWebsiteSecureItemContent(domain = "example.com"),
            ),
        )

        val decoded = codec.decode(
            itemType = encoded.itemType.wireName,
            schemaVersion = encoded.schemaVersion,
            payload = encoded.payload,
        )

        assertTrue(decoded is SecureItemContentDecodeResult.Success)
        val reEncoded = codec.encode((decoded as SecureItemContentDecodeResult.Success).content)
        assertArrayEquals(encoded.payload, reEncoded.payload)
    }
}
