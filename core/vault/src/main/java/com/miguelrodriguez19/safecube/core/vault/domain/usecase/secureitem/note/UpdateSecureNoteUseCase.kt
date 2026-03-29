package com.miguelrodriguez19.safecube.core.vault.domain.usecase.secureitem.note

import com.miguelrodriguez19.safecube.core.vault.domain.model.secureitem.SecureItemType
import com.miguelrodriguez19.safecube.core.vault.domain.model.secureitem.crud.SecureItemCrudError
import com.miguelrodriguez19.safecube.core.vault.domain.model.secureitem.crud.SecureItemMutationResult
import com.miguelrodriguez19.safecube.core.vault.domain.model.secureitem.crud.SecureNoteDraft
import com.miguelrodriguez19.safecube.core.vault.domain.usecase.secureitem.SecureItemMutationCoordinator
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UpdateSecureNoteUseCase @Inject internal constructor(
    private val secureItemMutationCoordinator: SecureItemMutationCoordinator,
    private val noteDraftToContentMapper: NoteDraftToContentMapper,
) {
    suspend operator fun invoke(
        logicalItemId: UUID,
        draft: SecureNoteDraft,
    ): SecureItemMutationResult = try {
        secureItemMutationCoordinator.update(
            logicalItemId = logicalItemId,
            displayHint = draft.displayHint,
            expectedItemType = SecureItemType.NOTE,
            content = noteDraftToContentMapper.map(draft),
        )
    } catch (illegalArgumentException: IllegalArgumentException) {
        SecureItemMutationResult.Error(
            SecureItemCrudError.ValidationError(
                illegalArgumentException.message ?: "Invalid note item.",
            ),
        )
    }
}
