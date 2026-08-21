package com.miguelrodriguez19.safecube.feature.vault.presentation.noteeditor.mutation

import com.miguelrodriguez19.safecube.core.vault.domain.model.secureitem.crud.SecureItemCrudError
import com.miguelrodriguez19.safecube.core.vault.domain.model.secureitem.crud.SecureItemMutationResult
import com.miguelrodriguez19.safecube.core.vault.domain.model.secureitem.crud.SecureNoteDraft
import com.miguelrodriguez19.safecube.core.vault.domain.model.secureitem.itemcontent.NoteSecureItemContent
import com.miguelrodriguez19.safecube.core.vault.domain.usecase.secureitem.note.CreateSecureNoteUseCase
import com.miguelrodriguez19.safecube.core.vault.domain.usecase.secureitem.note.UpdateSecureNoteUseCase
import com.miguelrodriguez19.safecube.feature.vault.presentation.shared.editor.mutation.contract.SecureItemEditorMutationGateway
import com.miguelrodriguez19.safecube.feature.vault.presentation.shared.editor.mutation.model.SecureItemEditorMutationRequest
import java.util.UUID
import javax.inject.Inject

internal class NoteEditorMutationGateway @Inject constructor(
    private val createSecureNoteUseCase: CreateSecureNoteUseCase,
    private val updateSecureNoteUseCase: UpdateSecureNoteUseCase,
) : SecureItemEditorMutationGateway {
    override suspend fun create(request: SecureItemEditorMutationRequest): SecureItemMutationResult =
        request.toDraft()?.let { draft ->
            createSecureNoteUseCase(draft)
        } ?: invalidResult()

    override suspend fun update(
        logicalItemId: UUID,
        request: SecureItemEditorMutationRequest,
    ): SecureItemMutationResult = request.toDraft()?.let { draft ->
        updateSecureNoteUseCase(logicalItemId, draft)
    } ?: invalidResult()

    private fun invalidResult(): SecureItemMutationResult =
        SecureItemMutationResult.Error(
            SecureItemCrudError.ValidationError("Invalid secure item."),
        )

    private fun SecureItemEditorMutationRequest.toDraft(): SecureNoteDraft? {
        val noteContent = content as? NoteSecureItemContent ?: return null
        return SecureNoteDraft(
            displayHint = displayHint,
            body = noteContent.body,
        )
    }
}
