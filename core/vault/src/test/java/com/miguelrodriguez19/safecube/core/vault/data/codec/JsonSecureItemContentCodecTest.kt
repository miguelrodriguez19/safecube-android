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
    private val target = JsonSecureItemContentCodec(
        passwordSecureItemContentJsonAdapter = PasswordSecureItemContentJsonAdapter(),
        noteSecureItemContentJsonAdapter = NoteSecureItemContentJsonAdapter(),
    )

    @Test
    fun `encode when password content is valid then serializes deterministic payload`() {
        val content = PasswordSecureItemContent(
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
        )

        val encoded = target.encode(
            content,
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
    fun `encode when password optional fields are null then omits them`() {
        val content = PasswordSecureItemContent(
            email = "alice@example.com",
            password = "s3cr3t",
        )

        val encoded = target.encode(
            content,
        )

        assertArrayEquals(
            """{"email":"alice@example.com","password":"s3cr3t"}""".toByteArray(StandardCharsets.UTF_8),
            encoded.payload,
        )
    }

    @Test
    fun `encode when note content is valid then serializes canonical payload`() {
        val content = NoteSecureItemContent(
            body = "private text",
        )

        val encoded = target.encode(
            content,
        )

        assertEquals(SecureItemType.NOTE, encoded.itemType)
        assertEquals(1, encoded.schemaVersion)
        assertArrayEquals(
            """{"body":"private text"}""".toByteArray(StandardCharsets.UTF_8),
            encoded.payload,
        )
    }

    @Test
    fun `decode when password payload is valid then rehydrates password content`() {
        val payload =
            """
                {
                    "username":"alice",
                    "email":"alice@example.com",
                    "password":"s3cr3t",
                    "website":{
                        "url":"https://example.com",
                        "domain":"example.com"
                    },
                    "notes":"personal",
                    "totp":{
                        "secret":"BASE32SECRET",
                        "issuer":"Example",
                        "accountName":"alice@example.com"
                    }
                }
                """.trimIndent()
                .toByteArray(StandardCharsets.UTF_8)

        val result = target.decode(
            itemType = "PASSWORD",
            schemaVersion = 1,
            payload = payload,
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
    fun `decode when note payload is valid then rehydrates note content`() {
        val payload = """{"body":"private text"}""".toByteArray(StandardCharsets.UTF_8)

        val result = target.decode(
            itemType = "NOTE",
            schemaVersion = 1,
            payload = payload,
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
    fun `decode when item type is unsupported then returns unsupported item type error`() {
        val payload = """{"holder":"Alice"}""".toByteArray(StandardCharsets.UTF_8)

        val result = target.decode(
            itemType = "CARD",
            schemaVersion = 1,
            payload = payload,
        )

        assertEquals(
            SecureItemContentDecodeResult.Error(
                SecureItemContentDecodeError.UnsupportedItemType("CARD"),
            ),
            result,
        )
    }

    @Test
    fun `decode when password schema version is unsupported then returns unsupported schema version error`() {
        val payload =
            """
                {
                    "email":"alice@example.com",
                    "password":"s3cr3t"
                }
            """.trimIndent().toByteArray(StandardCharsets.UTF_8)

        val result = target.decode(
            itemType = "PASSWORD",
            schemaVersion = 2,
            payload = payload,
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
    fun `decode when note schema version is unsupported then returns unsupported schema version error`() {
        val payload = """{"noteBody":"private text"}""".toByteArray(StandardCharsets.UTF_8)

        val result = target.decode(
            itemType = "NOTE",
            schemaVersion = 2,
            payload = payload,
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
    fun `decode when json payload is malformed then returns invalid payload error`() {
        val payload = """{"noteBody":""".toByteArray(StandardCharsets.UTF_8)

        val result = target.decode(
            itemType = "NOTE",
            schemaVersion = 1,
            payload = payload,
        )

        assertTrue(result is SecureItemContentDecodeResult.Error)
        assertTrue((result as SecureItemContentDecodeResult.Error).reason is SecureItemContentDecodeError.InvalidPayload)
    }

    @Test
    fun `decode when password payload contains unknown fields then returns invalid payload error`() {
        val payload = """{"email":"alice@example.com","password":"s3cr3t","extra":"boom"}"""
            .toByteArray(StandardCharsets.UTF_8)

        val result = target.decode(
            itemType = "PASSWORD",
            schemaVersion = 1,
            payload = payload,
        )

        assertTrue(result is SecureItemContentDecodeResult.Error)
        assertTrue((result as SecureItemContentDecodeResult.Error).reason is SecureItemContentDecodeError.InvalidPayload)
    }

    @Test
    fun `decode when note payload contains unknown fields then returns invalid payload error`() {
        val payload =
            """{"noteBody":"private text","extra":"boom"}""".toByteArray(StandardCharsets.UTF_8)

        val result = target.decode(
            itemType = "NOTE",
            schemaVersion = 1,
            payload = payload,
        )

        assertTrue(result is SecureItemContentDecodeResult.Error)
        assertTrue((result as SecureItemContentDecodeResult.Error).reason is SecureItemContentDecodeError.InvalidPayload)
    }

    @Test
    fun `decode when password payload lacks username and email then returns invalid payload error`() {
        val payload = """{"password":"s3cr3t"}""".toByteArray(StandardCharsets.UTF_8)

        val result = target.decode(
            itemType = "PASSWORD",
            schemaVersion = 1,
            payload = payload,
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
    fun `decode and encode when password payload roundtrips then preserves payload bytes`() {
        val encoded = target.encode(
            PasswordSecureItemContent(
                email = "alice@example.com",
                password = "s3cr3t",
                website = PasswordWebsiteSecureItemContent(domain = "example.com"),
            ),
        )

        val decoded = target.decode(
            itemType = encoded.itemType.wireName,
            schemaVersion = encoded.schemaVersion,
            payload = encoded.payload,
        )

        assertTrue(decoded is SecureItemContentDecodeResult.Success)
        val reEncoded = target.encode((decoded as SecureItemContentDecodeResult.Success).content)
        assertArrayEquals(encoded.payload, reEncoded.payload)
    }
}
