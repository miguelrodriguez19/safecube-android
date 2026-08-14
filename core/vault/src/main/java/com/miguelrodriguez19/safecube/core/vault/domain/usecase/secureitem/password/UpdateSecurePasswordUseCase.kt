package com.miguelrodriguez19.safecube.core.vault.domain.usecase.secureitem.password

import com.miguelrodriguez19.safecube.core.vault.domain.model.secureitem.SecureItemType
import com.miguelrodriguez19.safecube.core.vault.domain.model.secureitem.crud.SecureItemCrudError
import com.miguelrodriguez19.safecube.core.vault.domain.model.secureitem.crud.SecureItemMutationResult
import com.miguelrodriguez19.safecube.core.vault.domain.model.secureitem.crud.SecurePasswordDraft
import com.miguelrodriguez19.safecube.core.vault.domain.usecase.secureitem.SecureItemDraftMutationCoordinator
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UpdateSecurePasswordUseCase @Inject internal constructor(
    private val secureItemDraftMutationCoordinator: SecureItemDraftMutationCoordinator,
    private val passwordDraftToContentMapper: PasswordDraftToContentMapper,
) {
    suspend operator fun invoke(
        logicalItemId: UUID,
        draft: SecurePasswordDraft,
    ): SecureItemMutationResult = try {
        secureItemDraftMutationCoordinator.update(
            logicalItemId = logicalItemId,
            displayHint = draft.displayHint,
            expectedItemType = SecureItemType.PASSWORD,
            content = passwordDraftToContentMapper.map(draft),
        )
        } catch (_: IllegalArgumentException) {
            SecureItemMutationResult.Error(
                SecureItemCrudError.ValidationError("Invalid password item."),
            )
    }
}
