package com.miguelrodriguez19.safecube.core.vault.domain.usecase.secureitem

import com.miguelrodriguez19.safecube.core.vault.domain.model.secureitem.crud.VaultItemSummary
import com.miguelrodriguez19.safecube.core.vault.domain.repository.SecureItemRepository
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

@Singleton
class ObserveVaultItemSummariesUseCase @Inject constructor(
    private val secureItemRepository: SecureItemRepository,
) {
    operator fun invoke(): Flow<List<VaultItemSummary>> = secureItemRepository.observeActiveItems().map { items ->
        items.map { item ->
            VaultItemSummary(
                logicalItemId = item.logicalItemId,
                itemType = item.itemType,
                displayHint = item.displayHint,
                updatedAt = item.updatedAt,
            )
        }
    }
}
