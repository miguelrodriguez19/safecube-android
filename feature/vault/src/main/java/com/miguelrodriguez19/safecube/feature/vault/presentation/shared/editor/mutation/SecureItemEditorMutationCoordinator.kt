package com.miguelrodriguez19.safecube.feature.vault.presentation.shared.editor.mutation

import com.miguelrodriguez19.safecube.core.vault.domain.model.secureitem.crud.SecureItemCrudError
import com.miguelrodriguez19.safecube.core.vault.domain.model.secureitem.crud.SecureItemMutationResult
import com.miguelrodriguez19.safecube.core.vault.domain.model.sync.draft.DiscardSecureItemDraftResult
import com.miguelrodriguez19.safecube.core.vault.domain.model.sync.draft.PrepareSecureItemDraftForSyncResult
import com.miguelrodriguez19.safecube.core.vault.domain.usecase.secureitem.SoftDeleteSecureItemUseCase
import com.miguelrodriguez19.safecube.core.vault.domain.usecase.sync.draft.DiscardSecureItemDraftUseCase
import com.miguelrodriguez19.safecube.core.vault.domain.usecase.sync.draft.PrepareSecureItemDraftForSyncUseCase
import com.miguelrodriguez19.safecube.feature.vault.presentation.shared.editor.mutation.contract.SecureItemEditorMutationOperations
import com.miguelrodriguez19.safecube.feature.vault.presentation.shared.editor.mutation.factory.SecureItemEditorMutationGatewayFactory
import com.miguelrodriguez19.safecube.feature.vault.presentation.shared.editor.mutation.model.SecureItemEditorMutationRequest
import java.util.UUID
import javax.inject.Inject

internal class SecureItemEditorMutationCoordinator @Inject constructor(
    private val mutationGatewayFactory: SecureItemEditorMutationGatewayFactory,
    private val softDeleteSecureItemUseCase: SoftDeleteSecureItemUseCase,
    private val prepareSecureItemDraftForSyncUseCase: PrepareSecureItemDraftForSyncUseCase,
    private val discardSecureItemDraftUseCase: DiscardSecureItemDraftUseCase,
): SecureItemEditorMutationOperations {
    override suspend fun save(
        logicalItemId: UUID?,
        request: SecureItemEditorMutationRequest,
    ): SecureItemMutationResult {
        val mutationGateway = mutationGatewayFactory.gatewayFor(request.content.itemType)
            ?: return unsupportedItemTypeResult()

        return if (logicalItemId == null) {
            mutationGateway.create(request)
        } else {
            mutationGateway.update(logicalItemId, request)
        }
    }

    override suspend fun delete(logicalItemId: UUID): SecureItemMutationResult =
        softDeleteSecureItemUseCase(logicalItemId)

    override suspend fun publish(logicalItemId: UUID): PrepareSecureItemDraftForSyncResult =
        prepareSecureItemDraftForSyncUseCase(logicalItemId)

    override suspend fun discard(logicalItemId: UUID): DiscardSecureItemDraftResult =
        discardSecureItemDraftUseCase(logicalItemId)

    private fun unsupportedItemTypeResult(): SecureItemMutationResult =
        SecureItemMutationResult.Error(
            SecureItemCrudError.ValidationError("Unsupported secure item type."),
        )
}
