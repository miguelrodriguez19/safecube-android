package com.miguelrodriguez19.safecube.feature.vault.presentation.noteeditor.mutation

import com.miguelrodriguez19.safecube.core.vault.domain.model.secureitem.crud.SecureItemMutationResult
import com.miguelrodriguez19.safecube.core.vault.domain.model.secureitem.itemcontent.SecureItemContent
import com.miguelrodriguez19.safecube.core.vault.domain.model.sync.draft.DiscardSecureItemDraftResult
import com.miguelrodriguez19.safecube.core.vault.domain.model.sync.draft.PrepareSecureItemDraftForSyncResult
import com.miguelrodriguez19.safecube.core.vault.domain.usecase.secureitem.SoftDeleteSecureItemUseCase
import com.miguelrodriguez19.safecube.core.vault.domain.usecase.sync.draft.DiscardSecureItemDraftUseCase
import com.miguelrodriguez19.safecube.core.vault.domain.usecase.sync.draft.PrepareSecureItemDraftForSyncUseCase
import com.miguelrodriguez19.safecube.feature.vault.presentation.shared.editor.mutation.SecureItemEditorMutationCoordinator
import com.miguelrodriguez19.safecube.feature.vault.presentation.shared.editor.mutation.model.SecureItemEditorMutationRequest
import java.util.UUID
import javax.inject.Inject

internal class NoteEditorMutationCoordinator @Inject constructor(
    private val noteEditorMutationGateway: NoteEditorMutationGateway,
    softDeleteSecureItemUseCase: SoftDeleteSecureItemUseCase,
    prepareSecureItemDraftForSyncUseCase: PrepareSecureItemDraftForSyncUseCase,
    discardSecureItemDraftUseCase: DiscardSecureItemDraftUseCase,
) {
    private val delegate = SecureItemEditorMutationCoordinator(
        mutationGateway = noteEditorMutationGateway,
        softDeleteSecureItemUseCase = softDeleteSecureItemUseCase,
        prepareSecureItemDraftForSyncUseCase = prepareSecureItemDraftForSyncUseCase,
        discardSecureItemDraftUseCase = discardSecureItemDraftUseCase,
    )

    suspend fun save(
        logicalItemId: UUID?,
        displayHint: String,
        content: SecureItemContent,
    ): SecureItemMutationResult = delegate.save(
        logicalItemId = logicalItemId,
        request = SecureItemEditorMutationRequest(displayHint, content),
    )

    suspend fun delete(logicalItemId: UUID): SecureItemMutationResult = delegate.delete(logicalItemId)

    suspend fun publish(logicalItemId: UUID): PrepareSecureItemDraftForSyncResult =
        delegate.publish(logicalItemId)

    suspend fun discard(logicalItemId: UUID): DiscardSecureItemDraftResult =
        delegate.discard(logicalItemId)
}
