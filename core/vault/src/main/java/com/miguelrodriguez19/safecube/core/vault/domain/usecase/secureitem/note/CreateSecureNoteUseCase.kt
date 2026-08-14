package com.miguelrodriguez19.safecube.core.vault.domain.usecase.secureitem.note

import com.miguelrodriguez19.safecube.core.vault.domain.model.secureitem.crud.SecureItemCrudError
import com.miguelrodriguez19.safecube.core.vault.domain.model.secureitem.crud.SecureItemMutationResult
import com.miguelrodriguez19.safecube.core.vault.domain.model.secureitem.crud.SecureNoteDraft
import com.miguelrodriguez19.safecube.core.vault.domain.usecase.secureitem.SecureItemDraftMutationCoordinator
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CreateSecureNoteUseCase @Inject internal constructor(
    private val secureItemDraftMutationCoordinator: SecureItemDraftMutationCoordinator,
    private val noteDraftToContentMapper: NoteDraftToContentMapper,
) {
    suspend operator fun invoke(draft: SecureNoteDraft): SecureItemMutationResult = try {
        secureItemDraftMutationCoordinator.create(
            displayHint = draft.displayHint,
            content = noteDraftToContentMapper.map(draft),
        )
        } catch (_: IllegalArgumentException) {
            SecureItemMutationResult.Error(
                SecureItemCrudError.ValidationError("Invalid note item."),
            )
    }
}
