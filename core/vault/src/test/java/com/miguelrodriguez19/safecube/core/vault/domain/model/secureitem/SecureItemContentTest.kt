package com.miguelrodriguez19.safecube.core.vault.domain.model.secureitem

import com.miguelrodriguez19.safecube.core.vault.domain.model.secureitem.itemcontent.NoteSecureItemContent
import com.miguelrodriguez19.safecube.core.vault.domain.model.secureitem.itemcontent.PasswordSecureItemContent
import com.miguelrodriguez19.safecube.core.vault.domain.model.secureitem.itemcontent.PasswordTotpSecureItemContent
import com.miguelrodriguez19.safecube.core.vault.domain.model.secureitem.itemcontent.PasswordWebsiteSecureItemContent
import org.junit.Assert.assertEquals
import org.junit.Test

class SecureItemContentTest {
    @Test
    fun `creates password content with username only`() {
        val content = PasswordSecureItemContent(
            username = "alice",
            password = "s3cr3t",
        )

        assertEquals("alice", content.username)
        assertEquals(null, content.email)
        assertEquals(SecureItemType.PASSWORD, content.itemType)
    }

    @Test
    fun `creates password content with email only`() {
        val content = PasswordSecureItemContent(
            email = "alice@example.com",
            password = "s3cr3t",
        )

        assertEquals(null, content.username)
        assertEquals("alice@example.com", content.email)
        assertEquals(SecureItemType.PASSWORD, content.itemType)
    }

    @Test(expected = IllegalArgumentException::class)
    fun `rejects password content with blank password`() {
        PasswordSecureItemContent(
            email = "alice@example.com",
            password = " ",
        )
    }

    @Test(expected = IllegalArgumentException::class)
    fun `rejects password content without username or email`() {
        PasswordSecureItemContent(
            password = "s3cr3t",
        )
    }

    @Test(expected = IllegalArgumentException::class)
    fun `rejects website content without url or domain`() {
        PasswordWebsiteSecureItemContent()
    }

    @Test(expected = IllegalArgumentException::class)
    fun `rejects totp content with blank secret`() {
        PasswordTotpSecureItemContent(secret = " ")
    }

    @Test(expected = IllegalArgumentException::class)
    fun `rejects note content with blank body`() {
        NoteSecureItemContent(body = " ")
    }
}
