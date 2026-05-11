package com.miguelrodriguez19.safecube.core.vault.domain.usecase.secureitem.password

import com.miguelrodriguez19.safecube.core.vault.domain.model.secureitem.crud.SecureItemCrudError
import com.miguelrodriguez19.safecube.core.vault.domain.model.secureitem.crud.SecureItemMutationResult
import com.miguelrodriguez19.safecube.core.vault.domain.model.secureitem.crud.SecurePasswordDraft
import com.miguelrodriguez19.safecube.core.vault.domain.usecase.secureitem.SecureItemMutationCoordinator
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CreateSecurePasswordUseCase @Inject internal constructor(
    private val secureItemMutationCoordinator: SecureItemMutationCoordinator,
    private val passwordDraftToContentMapper: PasswordDraftToContentMapper,
) {
    suspend operator fun invoke(draft: SecurePasswordDraft): SecureItemMutationResult = try {
        secureItemMutationCoordinator.create(
            displayHint = draft.displayHint,
            content = passwordDraftToContentMapper.map(draft),
        )
    } catch (illegalArgumentException: IllegalArgumentException) {
        SecureItemMutationResult.Error(
            SecureItemCrudError.ValidationError(
                illegalArgumentException.message ?: "Invalid password item.",
            ),
        )
    }
}
