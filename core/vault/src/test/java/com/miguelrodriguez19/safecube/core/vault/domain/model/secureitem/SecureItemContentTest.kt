package com.miguelrodriguez19.safecube.core.vault.domain.model.secureitem

import com.miguelrodriguez19.safecube.core.vault.domain.model.secureitem.itemcontent.NoteSecureItemContent
import com.miguelrodriguez19.safecube.core.vault.domain.model.secureitem.itemcontent.PasswordSecureItemContent
import com.miguelrodriguez19.safecube.core.vault.domain.model.secureitem.itemcontent.PasswordTotpSecureItemContent
import com.miguelrodriguez19.safecube.core.vault.domain.model.secureitem.itemcontent.PasswordWebsiteSecureItemContent
import org.junit.Assert.assertEquals
import org.junit.Assert.assertThrows
import org.junit.Test

class SecureItemContentTest {
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
    fun `password totp secure item content when secret is blank then throws illegal argument exception`() {
        assertThrows(IllegalArgumentException::class.java) {
            PasswordTotpSecureItemContent(secret = " ")
        }
    }

    @Test
    fun `note secure item content when body is blank then throws illegal argument exception`() {
        assertThrows(IllegalArgumentException::class.java) {
            NoteSecureItemContent(body = " ")
        }
    }
}
