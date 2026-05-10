package com.miguelrodriguez19.safecube.core.vault.domain.usecase.secureitem

import com.miguelrodriguez19.safecube.core.vault.domain.model.secureitem.crud.SecureItemMutationResult
import com.miguelrodriguez19.safecube.core.vault.domain.usecase.sync.VaultSyncTrigger
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SoftDeleteSecureItemUseCase @Inject internal constructor(
    private val secureItemMutationCoordinator: SecureItemMutationCoordinator,
    private val vaultSyncTrigger: VaultSyncTrigger,
) {
    suspend operator fun invoke(logicalItemId: UUID): SecureItemMutationResult {
        val result = secureItemMutationCoordinator.softDelete(logicalItemId)
        if (result is SecureItemMutationResult.Success) {
            vaultSyncTrigger.onLocalMutationStored(result.item.logicalItemId)
        }
        return result
    }
}
