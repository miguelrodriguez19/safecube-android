package com.miguelrodriguez19.safecube.core.vault.domain.model.secureitem.itemcontent

import com.miguelrodriguez19.safecube.core.vault.domain.model.secureitem.SecureItemType
import kotlinx.serialization.json.Json
import org.junit.Assert.assertEquals
import org.junit.Assert.assertThrows
import org.junit.Test

class NoteSecureItemContentTest {

    private val targetJson = Json

    @Test
    fun `constructor whenBodyIsValid thenCreatesNoteContentWithExpectedDefaults`() {
        val body = "My secure note body"

        val target = NoteSecureItemContent(body = body)

        assertEquals(body, target.body)
        assertEquals(SecureItemType.NOTE, target.itemType)
        assertEquals(NoteSecureItemContent.NOTE_SCHEMA_VERSION, target.schemaVersion)
    }

    @Test
    fun `constructor when BodyIsBlank then Throws IllegalArgumentException`() {
        val body = "   "

        val exception = assertThrows(IllegalArgumentException::class.java) {
            NoteSecureItemContent(body = body)
        }

        assertEquals("body must not be blank.", exception.message)
    }

    @Test
    fun `serialize And Deserialize when NoteContent Is Valid then Preserves Data And Covers Serializable`() {
        val target = NoteSecureItemContent(body = "Serialized secure note")

        val encoded = targetJson.encodeToString(NoteSecureItemContent.serializer(), target)
        val decoded = targetJson.decodeFromString(NoteSecureItemContent.serializer(), encoded)

        assertEquals(target, decoded)
        assertEquals(SecureItemType.NOTE, decoded.itemType)
        assertEquals(NoteSecureItemContent.NOTE_SCHEMA_VERSION, decoded.schemaVersion)
    }
}