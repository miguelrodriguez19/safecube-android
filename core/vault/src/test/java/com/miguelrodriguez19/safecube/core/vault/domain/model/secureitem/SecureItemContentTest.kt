package com.miguelrodriguez19.safecube.core.vault.domain.model.secureitem

import com.miguelrodriguez19.safecube.core.vault.domain.model.secureitem.itemcontent.NoteSecureItemContent
import com.miguelrodriguez19.safecube.core.vault.domain.model.secureitem.itemcontent.PasswordSecureItemContent
import com.miguelrodriguez19.safecube.core.vault.domain.model.secureitem.itemcontent.PasswordTotpSecureItemContent
import com.miguelrodriguez19.safecube.core.vault.domain.model.secureitem.itemcontent.PasswordWebsiteSecureItemContent
import kotlinx.serialization.MissingFieldException
import kotlinx.serialization.json.Json
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotEquals
import org.junit.Assert.assertThrows
import org.junit.Test

class SecureItemContentTest {
    private val json = Json {
        prettyPrint = false
        encodeDefaults = false
        explicitNulls = false
        ignoreUnknownKeys = false
        isLenient = false
    }

    @Test
    fun `password secure item content when username is present then creates content`() {
        val content = PasswordSecureItemContent(
            username = "alice",
            password = "s3cr3t",
        )

        assertEquals("alice", content.username)
        assertEquals(null, content.email)
        assertEquals(SecureItemType.PASSWORD, content.itemType)
    }

    @Test
    fun `password secure item content when email is present then creates content`() {
        val content = PasswordSecureItemContent(
            email = "alice@example.com",
            password = "s3cr3t",
        )

        assertEquals(null, content.username)
        assertEquals("alice@example.com", content.email)
        assertEquals(SecureItemType.PASSWORD, content.itemType)
        assertEquals(1, content.schemaVersion)
    }

    @Test
    fun `password secure item content when necessary values are empty then throw exception`() {
        assertThrows(IllegalArgumentException::class.java) {
            PasswordSecureItemContent(
                username = "alice",
                email = "alice@example.com",
                password = "s3cr3t",
                website = null,
                notes = "personal",
                totp = PasswordTotpSecureItemContent(
                    secret = "",
                    issuer = "Example",
                    accountName = "alice@example.com",
                ),
            )
        }
        assertThrows(IllegalArgumentException::class.java) {
            PasswordSecureItemContent(
                username = "alice",
                email = "alice@example.com",
                password = "s3cr3t",
                website = PasswordWebsiteSecureItemContent(url = "", domain = null),
                notes = "personal",
                totp = null,
            )
        }
        assertThrows(IllegalArgumentException::class.java) {
            PasswordSecureItemContent(
                username = "alice",
                email = "alice@example.com",
                password = "",
                website = null,
                notes = "personal",
                totp = null,
            )
        }
    }

    @Test
    fun `password secure item content when compared across supported variations then equality is stable`() {
        val base = PasswordSecureItemContent(
            username = "alice",
            email = "alice@example.com",
            password = "s3cr3t",
            website = PasswordWebsiteSecureItemContent(
                domain = "example.com",
            ),
            notes = "personal",
            totp = PasswordTotpSecureItemContent(
                secret = "BASE32SECRET",
                issuer = "Example",
                accountName = "alice@example.com",
            ),
        )

        assertEquals(base, base.copy())
        assertNotEquals(base, null)
        assertNotEquals(base, "password")
        assertNotEquals(base, base.copy(username = "bob"))
        assertNotEquals(base, base.copy(email = "bob@example.com"))
        assertNotEquals(base, base.copy(password = "different"))
        assertNotEquals(
            base,
            base.copy(website = PasswordWebsiteSecureItemContent(url = "https://other.com"))
        )
        assertNotEquals(base, base.copy(notes = "work"))
        assertNotEquals(
            base,
            base.copy(
                totp = PasswordTotpSecureItemContent(
                    secret = "OTHERSECRET",
                    issuer = "Example",
                    accountName = "alice@example.com",
                ),
            ),
        )
    }

    @Test
    fun `password secure item content when password is blank then throws illegal argument exception`() {
        assertThrows(IllegalArgumentException::class.java) {
            PasswordSecureItemContent(
                email = "alice@example.com",
                password = " ",
            )
        }
    }

    @Test
    fun `password secure item content when username and email are missing then throws illegal argument exception`() {
        assertThrows(IllegalArgumentException::class.java) {
            PasswordSecureItemContent(
                password = "s3cr3t",
            )
        }
    }

    @Test
    fun `password website secure item content when url and domain are missing then throws illegal argument exception`() {
        assertThrows(IllegalArgumentException::class.java) {
            PasswordWebsiteSecureItemContent()
        }
    }

    @Test
    fun `password website secure item content when compared across supported variations then equality is stable`() {
        val base = PasswordWebsiteSecureItemContent(
            url = "https://example.com",
            domain = "example.com",
        )

        assertEquals(base, base.copy())
        assertNotEquals(base, null)
        assertNotEquals(base, "website")
        assertNotEquals(base, base.copy(url = "https://other.com"))
        assertNotEquals(base, base.copy(domain = "other.com"))
        assertEquals(
            PasswordWebsiteSecureItemContent(url = "https://example.com"),
            PasswordWebsiteSecureItemContent(url = "https://example.com"),
        )
        assertEquals(
            PasswordWebsiteSecureItemContent(domain = "example.com"),
            PasswordWebsiteSecureItemContent(domain = "example.com"),
        )
    }

    @Test
    fun `password totp secure item content when secret is blank then throws illegal argument exception`() {
        assertThrows(IllegalArgumentException::class.java) {
            PasswordTotpSecureItemContent(secret = " ")
        }
    }

    @Test
    fun `password totp secure item content when compared across supported variations then equality is stable`() {
        val base = PasswordTotpSecureItemContent(
            secret = "BASE32SECRET",
            issuer = "Example",
            accountName = "alice@example.com",
        )

        assertEquals(base, base.copy())
        assertNotEquals(base, null)
        assertNotEquals(base, "totp")
        assertNotEquals(base, base.copy(secret = "OTHERSECRET"))
        assertNotEquals(base, base.copy(issuer = "Other"))
        assertNotEquals(base, base.copy(accountName = "bob@example.com"))
    }

    @Test
    fun `password totp secure item content when optional values are omitted then serializes and deserializes with null defaults`() {
        val content = PasswordTotpSecureItemContent(secret = "BASE32SECRET")

        val payload = json.encodeToString(content)
        val decoded = json.decodeFromString<PasswordTotpSecureItemContent>(payload)

        assertEquals("""{"secret":"BASE32SECRET"}""", payload)
        assertEquals(content, decoded)
        assertEquals(null, decoded.issuer)
        assertEquals(null, decoded.accountName)
    }

    @Test
    fun `password totp secure item content when issuer and account name are present then serializes and deserializes them`() {
        val content = PasswordTotpSecureItemContent(
            secret = "BASE32SECRET",
            issuer = "Example",
            accountName = "alice@example.com",
        )

        val payload = json.encodeToString(content)
        val decoded = json.decodeFromString<PasswordTotpSecureItemContent>(payload)

        assertEquals(
            """{"secret":"BASE32SECRET","issuer":"Example","accountName":"alice@example.com"}""",
            payload,
        )
        assertEquals(content, decoded)
    }

    @Test
    fun `password totp secure item content when only issuer is present then serializes and deserializes it`() {
        val content = PasswordTotpSecureItemContent(
            secret = "BASE32SECRET",
            issuer = "Example",
        )

        val payload = json.encodeToString(content)
        val decoded = json.decodeFromString<PasswordTotpSecureItemContent>(payload)

        assertEquals("""{"secret":"BASE32SECRET","issuer":"Example"}""", payload)
        assertEquals(content, decoded)
        assertEquals(null, decoded.accountName)
    }

    @Test
    fun `password totp secure item content when only account name is present then serializes and deserializes it`() {
        val content = PasswordTotpSecureItemContent(
            secret = "BASE32SECRET",
            accountName = "alice@example.com",
        )

        val payload = json.encodeToString(content)
        val decoded = json.decodeFromString<PasswordTotpSecureItemContent>(payload)

        assertEquals("""{"secret":"BASE32SECRET","accountName":"alice@example.com"}""", payload)
        assertEquals(content, decoded)
        assertEquals(null, decoded.issuer)
    }

    @Test
    fun `password totp secure item content when required secret field is missing during decode then throws missing field exception`() {
        assertThrows(MissingFieldException::class.java) {
            json.decodeFromString<PasswordTotpSecureItemContent>("""{"issuer":"Example"}""")
        }
    }

    @Test
    fun `password totp secure item content when decoded secret is blank then throws illegal argument exception`() {
        assertThrows(IllegalArgumentException::class.java) {
            json.decodeFromString<PasswordTotpSecureItemContent>("""{"secret":" "}""")
        }
    }

    @Test
    fun `password totp secure item content when secret is null then throws null pointer exception`() {
        @Suppress("NULL_FOR_NONNULL_TYPE")
        assertThrows(NullPointerException::class.java) {
            PasswordTotpSecureItemContent(
                secret = null as String,
                issuer = "Example",
                accountName = "alice@example.com",
            )
        }
    }

    @Test
    fun `note secure item content when body is blank then throws illegal argument exception`() {
        assertThrows(IllegalArgumentException::class.java) {
            NoteSecureItemContent(body = " ")
        }
    }

    @Test
    fun `note secure item content when body is valid then exposes canonical metadata`() {
        val content = NoteSecureItemContent(body = "private text")

        assertEquals("private text", content.body)
        assertEquals(SecureItemType.NOTE, content.itemType)
        assertEquals(1, content.schemaVersion)
    }

    @Test
    fun `note secure item content when compared across supported variations then equality is stable`() {
        val base = NoteSecureItemContent(body = "private text")

        assertEquals(base, base.copy())
        assertNotEquals(base, null)
        assertNotEquals(base, "note")
        assertNotEquals(base, base.copy(body = "other"))
    }
}
