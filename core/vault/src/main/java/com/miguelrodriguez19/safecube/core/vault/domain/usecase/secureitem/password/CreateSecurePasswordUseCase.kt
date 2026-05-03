package com.miguelrodriguez19.safecube.core.vault.domain.usecase.secureitem.password

import com.miguelrodriguez19.safecube.core.vault.domain.model.secureitem.crud.SecureItemCrudError
import com.miguelrodriguez19.safecube.core.vault.domain.model.secureitem.crud.SecureItemMutationResult
import com.miguelrodriguez19.safecube.core.vault.domain.model.secureitem.crud.SecurePasswordDraft
import com.miguelrodriguez19.safecube.core.vault.domain.usecase.secureitem.SecureItemMutationCoordinator
import com.miguelrodriguez19.safecube.core.vault.domain.usecase.sync.VaultSyncTrigger
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CreateSecurePasswordUseCase @Inject internal constructor(
    private val secureItemMutationCoordinator: SecureItemMutationCoordinator,
    private val passwordDraftToContentMapper: PasswordDraftToContentMapper,
    private val vaultSyncTrigger: VaultSyncTrigger,
) {
    suspend operator fun invoke(draft: SecurePasswordDraft): SecureItemMutationResult = try {
        val result = secureItemMutationCoordinator.create(
            displayHint = draft.displayHint,
            content = passwordDraftToContentMapper.map(draft),
        )
        if (result is SecureItemMutationResult.Success) {
            vaultSyncTrigger.onLocalMutationStored()
        }
        result
    } catch (illegalArgumentException: IllegalArgumentException) {
        SecureItemMutationResult.Error(
            SecureItemCrudError.ValidationError(
                illegalArgumentException.message ?: "Invalid password item.",
            ),
        )
    }
}
