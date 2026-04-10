package com.miguelrodriguez19.safecube.core.vault.data.codec

import com.miguelrodriguez19.safecube.core.vault.domain.model.secureitem.itemcontent.NoteSecureItemContent
import com.miguelrodriguez19.safecube.core.vault.domain.model.secureitem.itemcontent.PasswordSecureItemContent
import kotlinx.serialization.json.Json
import org.junit.Assert.assertFalse
import org.junit.Assert.assertThrows
import org.junit.Assert.assertTrue
import org.junit.Test

class SecureItemContentJsonAdapterTest {
    private val json = Json {
        prettyPrint = false
        encodeDefaults = false
        explicitNulls = false
        ignoreUnknownKeys = false
        isLenient = false
    }

    private val passwordAdapter = PasswordSecureItemContentJsonAdapter()
    private val noteAdapter = NoteSecureItemContentJsonAdapter()

    @Test
    fun `password adapter when content type matches then can encode`() {
        val result = passwordAdapter.canEncode(
            PasswordSecureItemContent(
                username = "alice",
                password = "s3cr3t",
            ),
        )

        assertTrue(result)
    }

    @Test
    fun `password adapter when content type does not match then rejects encoding`() {
        val content = NoteSecureItemContent(body = "private text")

        assertFalse(passwordAdapter.canEncode(content))
        assertThrows(IllegalArgumentException::class.java) {
            passwordAdapter.encode(content, json)
        }
    }

    @Test
    fun `note adapter when content type matches then can encode`() {
        val result = noteAdapter.canEncode(
            NoteSecureItemContent(body = "private text"),
        )

        assertTrue(result)
    }

    @Test
    fun `note adapter when content type does not match then rejects encoding`() {
        val content = PasswordSecureItemContent(
            username = "alice",
            password = "s3cr3t",
        )

        assertFalse(noteAdapter.canEncode(content))
        assertThrows(IllegalArgumentException::class.java) {
            noteAdapter.encode(content, json)
        }
    }
}
