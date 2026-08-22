package com.miguelrodriguez19.safecube.feature.vault.presentation.editor.shared.mutation.contract

import com.miguelrodriguez19.safecube.core.vault.domain.model.secureitem.crud.SecureItemMutationResult
import com.miguelrodriguez19.safecube.feature.vault.presentation.editor.shared.mutation.model.SecureItemEditorMutationRequest
import java.util.UUID

internal interface SecureItemEditorMutationGateway {
    suspend fun create(request: SecureItemEditorMutationRequest): SecureItemMutationResult

    suspend fun update(
        logicalItemId: UUID,
        request: SecureItemEditorMutationRequest,
    ): SecureItemMutationResult
}
