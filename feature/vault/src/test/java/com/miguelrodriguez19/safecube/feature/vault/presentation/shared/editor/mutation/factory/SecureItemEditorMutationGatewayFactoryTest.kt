package com.miguelrodriguez19.safecube.feature.vault.presentation.shared.editor.mutation.factory

import com.miguelrodriguez19.safecube.core.vault.domain.model.secureitem.SecureItemType
import com.miguelrodriguez19.safecube.feature.vault.presentation.noteeditor.mutation.NoteEditorMutationGateway
import com.miguelrodriguez19.safecube.feature.vault.presentation.passwordeditor.mutation.PasswordEditorMutationGateway
import io.mockk.mockk
import org.junit.Assert.assertSame
import org.junit.Assert.assertNull
import org.junit.Test

class SecureItemEditorMutationGatewayFactoryTest {
    private val noteGateway = mockk<NoteEditorMutationGateway>()
    private val passwordGateway = mockk<PasswordEditorMutationGateway>()
    private val target = SecureItemEditorMutationGatewayFactory(
        gateways = mapOf(
            SecureItemType.NOTE to noteGateway,
            SecureItemType.PASSWORD to passwordGateway,
        ),
    )

    @Test
    fun gatewayFor_whenNoteType_thenReturnsNoteGateway() {
        val actual = target.gatewayFor(SecureItemType.NOTE)

        assertSame(noteGateway, actual)
    }

    @Test
    fun gatewayFor_whenPasswordType_thenReturnsPasswordGateway() {
        val actual = target.gatewayFor(SecureItemType.PASSWORD)

        assertSame(passwordGateway, actual)
    }

    @Test
    fun gatewayFor_whenTypeHasNoBinding_thenReturnsNull() {
        val target = SecureItemEditorMutationGatewayFactory(emptyMap())

        val actual = target.gatewayFor(SecureItemType.NOTE)

        assertNull(actual)
    }
}
