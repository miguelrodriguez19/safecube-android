package com.miguelrodriguez19.safecube.feature.vault.presentation.editor.shared.observation.model

import com.miguelrodriguez19.safecube.core.vault.domain.model.secureitem.crud.SecureItemCrudError
import com.miguelrodriguez19.safecube.core.vault.domain.model.secureitem.crud.SecureItemDetail
import com.miguelrodriguez19.safecube.core.vault.domain.model.secureitem.crud.SecureItemDraftDetail

internal sealed interface SecureItemEditorObservationResult {
    data class Content(
        val officialDetail: SecureItemDetail?,
        val draftDetail: SecureItemDraftDetail?,
    ) : SecureItemEditorObservationResult

    data object NotFound : SecureItemEditorObservationResult

    data class Error(
        val reason: SecureItemCrudError,
    ) : SecureItemEditorObservationResult

    data object InconsistentOfficialDraft : SecureItemEditorObservationResult
}
