package com.miguelrodriguez19.safecube.core.vault.domain.usecase.note

import com.miguelrodriguez19.safecube.core.vault.domain.model.secureitem.crud.SecureNoteDraft
import com.miguelrodriguez19.safecube.core.vault.domain.model.secureitem.itemcontent.NoteSecureItemContent
import com.miguelrodriguez19.safecube.core.vault.domain.usecase.secureitem.note.NoteDraftToContentMapper
import org.junit.Assert.assertEquals
import org.junit.Test

class NoteDraftToContentMapperTest {

    private val target = NoteDraftToContentMapper()

    @Test
    fun `map when draft is valid then maps note content`() {
        val draft = SecureNoteDraft(
            displayHint = "API key",
            body = "secret body",
        )
        val result = target.map(
            draft,
        )

        assertEquals(NoteSecureItemContent::class, result::class)
        assertEquals(result.body, draft.body)
    }

    @Test(expected = IllegalArgumentException::class)
    fun `map when draft is invalid then throws validation error`() {
        target.map(
            SecureNoteDraft(
                displayHint = "API key",
                body = " ",
            )
        )
    }
}
