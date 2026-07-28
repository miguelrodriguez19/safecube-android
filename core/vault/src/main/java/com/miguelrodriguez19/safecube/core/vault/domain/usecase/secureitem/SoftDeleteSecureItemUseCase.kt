package com.miguelrodriguez19.safecube.core.vault.domain.usecase.secureitem

import com.miguelrodriguez19.safecube.core.vault.domain.model.secureitem.crud.SecureItemMutationResult
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SoftDeleteSecureItemUseCase @Inject internal constructor(
    private val secureItemDraftMutationCoordinator: SecureItemDraftMutationCoordinator,
) {
    suspend operator fun invoke(logicalItemId: UUID): SecureItemMutationResult =
        secureItemDraftMutationCoordinator.softDelete(logicalItemId)
}
