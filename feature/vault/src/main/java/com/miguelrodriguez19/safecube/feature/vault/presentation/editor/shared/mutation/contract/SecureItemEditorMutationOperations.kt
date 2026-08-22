package com.miguelrodriguez19.safecube.feature.vault.presentation.editor.shared.mutation.contract

import com.miguelrodriguez19.safecube.core.vault.domain.model.secureitem.crud.SecureItemMutationResult
import com.miguelrodriguez19.safecube.core.vault.domain.model.sync.draft.DiscardSecureItemDraftResult
import com.miguelrodriguez19.safecube.core.vault.domain.model.sync.draft.PrepareSecureItemDraftForSyncResult
import com.miguelrodriguez19.safecube.feature.vault.presentation.editor.shared.mutation.model.SecureItemEditorMutationRequest
import java.util.UUID

internal interface SecureItemEditorMutationOperations {
    suspend fun save(
        logicalItemId: UUID?,
        request: SecureItemEditorMutationRequest,
    ): SecureItemMutationResult

    suspend fun delete(logicalItemId: UUID): SecureItemMutationResult

    suspend fun publish(logicalItemId: UUID): PrepareSecureItemDraftForSyncResult

    suspend fun discard(logicalItemId: UUID): DiscardSecureItemDraftResult
}
