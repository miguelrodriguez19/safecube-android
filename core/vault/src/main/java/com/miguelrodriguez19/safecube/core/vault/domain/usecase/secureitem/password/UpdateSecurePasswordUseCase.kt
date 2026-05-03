package com.miguelrodriguez19.safecube.core.vault.domain.usecase.secureitem.password

import com.miguelrodriguez19.safecube.core.vault.domain.model.secureitem.SecureItemType
import com.miguelrodriguez19.safecube.core.vault.domain.model.secureitem.crud.SecureItemCrudError
import com.miguelrodriguez19.safecube.core.vault.domain.model.secureitem.crud.SecureItemMutationResult
import com.miguelrodriguez19.safecube.core.vault.domain.model.secureitem.crud.SecurePasswordDraft
import com.miguelrodriguez19.safecube.core.vault.domain.usecase.secureitem.SecureItemMutationCoordinator
import com.miguelrodriguez19.safecube.core.vault.domain.usecase.sync.VaultSyncTrigger
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UpdateSecurePasswordUseCase @Inject internal constructor(
    private val secureItemMutationCoordinator: SecureItemMutationCoordinator,
    private val passwordDraftToContentMapper: PasswordDraftToContentMapper,
    private val vaultSyncTrigger: VaultSyncTrigger,
) {
    suspend operator fun invoke(
        logicalItemId: UUID,
        draft: SecurePasswordDraft,
    ): SecureItemMutationResult = try {
        val result = secureItemMutationCoordinator.update(
            logicalItemId = logicalItemId,
            displayHint = draft.displayHint,
            expectedItemType = SecureItemType.PASSWORD,
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
