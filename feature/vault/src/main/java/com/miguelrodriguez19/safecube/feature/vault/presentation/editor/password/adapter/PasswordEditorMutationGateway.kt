package com.miguelrodriguez19.safecube.feature.vault.presentation.editor.password.adapter

import com.miguelrodriguez19.safecube.core.vault.domain.model.secureitem.crud.SecureItemCrudError
import com.miguelrodriguez19.safecube.core.vault.domain.model.secureitem.crud.SecureItemMutationResult
import com.miguelrodriguez19.safecube.core.vault.domain.model.secureitem.crud.SecurePasswordDraft
import com.miguelrodriguez19.safecube.core.vault.domain.model.secureitem.crud.SecurePasswordWebsiteDraft
import com.miguelrodriguez19.safecube.core.vault.domain.model.secureitem.itemcontent.PasswordSecureItemContent
import com.miguelrodriguez19.safecube.core.vault.domain.usecase.secureitem.password.CreateSecurePasswordUseCase
import com.miguelrodriguez19.safecube.core.vault.domain.usecase.secureitem.password.UpdateSecurePasswordUseCase
import com.miguelrodriguez19.safecube.feature.vault.presentation.editor.shared.mutation.contract.SecureItemEditorMutationGateway
import com.miguelrodriguez19.safecube.feature.vault.presentation.editor.shared.mutation.model.SecureItemEditorMutationRequest
import java.util.UUID
import javax.inject.Inject

internal class PasswordEditorMutationGateway @Inject constructor(
    private val createSecurePasswordUseCase: CreateSecurePasswordUseCase,
    private val updateSecurePasswordUseCase: UpdateSecurePasswordUseCase,
) : SecureItemEditorMutationGateway {
    override suspend fun create(request: SecureItemEditorMutationRequest): SecureItemMutationResult =
        request.toDraft()?.let { draft ->
            createSecurePasswordUseCase(draft)
        } ?: invalidResult()

    override suspend fun update(
        logicalItemId: UUID,
        request: SecureItemEditorMutationRequest,
    ): SecureItemMutationResult = request.toDraft()?.let { draft ->
        updateSecurePasswordUseCase(logicalItemId, draft)
    } ?: invalidResult()

    private fun invalidResult(): SecureItemMutationResult =
        SecureItemMutationResult.Error(
            SecureItemCrudError.ValidationError("Invalid secure item."),
        )

    private fun SecureItemEditorMutationRequest.toDraft(): SecurePasswordDraft? {
        val passwordContent = content as? PasswordSecureItemContent ?: return null
        return SecurePasswordDraft(
            displayHint = displayHint,
            username = passwordContent.username,
            email = passwordContent.email,
            password = passwordContent.password,
            website = passwordContent.website?.let { website ->
                SecurePasswordWebsiteDraft(
                    url = website.url,
                    domain = website.domain,
                )
            },
            notes = passwordContent.notes,
        )
    }
}
