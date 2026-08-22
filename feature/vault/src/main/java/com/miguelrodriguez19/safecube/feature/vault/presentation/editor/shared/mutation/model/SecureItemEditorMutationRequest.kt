package com.miguelrodriguez19.safecube.feature.vault.presentation.editor.shared.mutation.model

import com.miguelrodriguez19.safecube.core.vault.domain.model.secureitem.crud.SecureItemCrudError
import com.miguelrodriguez19.safecube.core.vault.domain.model.secureitem.crud.SecureItemMutationResult
import com.miguelrodriguez19.safecube.core.vault.domain.model.secureitem.itemcontent.SecureItemContent

internal data class SecureItemEditorMutationRequest(
    val displayHint: String,
    val content: SecureItemContent,
) {
    internal fun invalidResult(): SecureItemMutationResult =
        SecureItemMutationResult.Error(
            SecureItemCrudError.ValidationError("Invalid secure item."),
        )
}
