package com.miguelrodriguez19.safecube.feature.vault.presentation.shared.editor.mutation

import com.miguelrodriguez19.safecube.core.vault.domain.model.secureitem.crud.SecureItemMutationResult
import com.miguelrodriguez19.safecube.core.vault.domain.model.sync.draft.DiscardSecureItemDraftResult
import com.miguelrodriguez19.safecube.core.vault.domain.model.sync.draft.PrepareSecureItemDraftForSyncResult
import com.miguelrodriguez19.safecube.core.vault.domain.usecase.secureitem.SoftDeleteSecureItemUseCase
import com.miguelrodriguez19.safecube.core.vault.domain.usecase.sync.draft.DiscardSecureItemDraftUseCase
import com.miguelrodriguez19.safecube.core.vault.domain.usecase.sync.draft.PrepareSecureItemDraftForSyncUseCase
import com.miguelrodriguez19.safecube.feature.vault.presentation.shared.editor.mutation.contract.SecureItemEditorMutationGateway
import com.miguelrodriguez19.safecube.feature.vault.presentation.shared.editor.mutation.model.SecureItemEditorMutationRequest
import java.util.UUID

internal class SecureItemEditorMutationCoordinator(
    private val mutationGateway: SecureItemEditorMutationGateway,
    private val softDeleteSecureItemUseCase: SoftDeleteSecureItemUseCase,
    private val prepareSecureItemDraftForSyncUseCase: PrepareSecureItemDraftForSyncUseCase,
    private val discardSecureItemDraftUseCase: DiscardSecureItemDraftUseCase,
) {
    suspend fun save(
        logicalItemId: UUID?,
        request: SecureItemEditorMutationRequest,
    ): SecureItemMutationResult = if (logicalItemId == null) {
        mutationGateway.create(request)
    } else {
        mutationGateway.update(logicalItemId, request)
    }

    suspend fun delete(logicalItemId: UUID): SecureItemMutationResult =
        softDeleteSecureItemUseCase(logicalItemId)

    suspend fun publish(logicalItemId: UUID): PrepareSecureItemDraftForSyncResult =
        prepareSecureItemDraftForSyncUseCase(logicalItemId)

    suspend fun discard(logicalItemId: UUID): DiscardSecureItemDraftResult =
        discardSecureItemDraftUseCase(logicalItemId)
}
