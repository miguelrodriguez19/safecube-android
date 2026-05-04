package com.miguelrodriguez19.safecube.core.vault.domain.usecase.secureitem

import com.miguelrodriguez19.safecube.core.vault.domain.model.VaultState
import com.miguelrodriguez19.safecube.core.vault.domain.model.secureitem.SecureItem
import com.miguelrodriguez19.safecube.core.vault.domain.model.secureitem.crud.ObserveSecureItemDetailResult
import com.miguelrodriguez19.safecube.core.vault.domain.model.secureitem.crud.SecureItemCrudError
import com.miguelrodriguez19.safecube.core.vault.domain.model.secureitem.crud.SecureItemDetail
import com.miguelrodriguez19.safecube.core.vault.domain.repository.SecureItemRepository
import com.miguelrodriguez19.safecube.core.vault.domain.service.SecureItemCryptoError
import com.miguelrodriguez19.safecube.core.vault.domain.service.SecureItemCryptoService
import com.miguelrodriguez19.safecube.core.vault.domain.service.SecureItemDecryptionResult
import com.miguelrodriguez19.safecube.core.vault.domain.session.VaultSessionManager
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine

@Singleton
class ObserveSecureItemDetailUseCase @Inject constructor(
    private val secureItemRepository: SecureItemRepository,
    private val secureItemCryptoService: SecureItemCryptoService,
    private val vaultSessionManager: VaultSessionManager,
) {
    operator fun invoke(logicalItemId: UUID): Flow<ObserveSecureItemDetailResult> = combine(
        vaultSessionManager.vaultState,
        secureItemRepository.observeItem(logicalItemId),
    ) { vaultState, item ->
        when {
            item == null || item.deletedAt != null -> {
                ObserveSecureItemDetailResult.Error(SecureItemCrudError.ItemNotFound)
            }

            vaultState != VaultState.Unlocked -> {
                ObserveSecureItemDetailResult.Error(SecureItemCrudError.VaultLocked)
            }

            else -> decryptItem(item)
        }
    }

    private fun decryptItem(item: SecureItem): ObserveSecureItemDetailResult = when (
        val decryptionResult = secureItemCryptoService.decrypt(item)
    ) {
        is SecureItemDecryptionResult.Success -> {
            ObserveSecureItemDetailResult.Success(
                detail = SecureItemDetail(
                    logicalItemId = item.logicalItemId,
                    remoteItemId = item.remoteItemId,
                    itemType = item.itemType,
                    schemaVersion = item.schemaVersion,
                    displayHint = item.displayHint,
                    payloadVersion = item.payloadVersion,
                    createdAt = item.createdAt,
                    updatedAt = item.updatedAt,
                    syncState = item.syncState,
                    lastSyncError = item.lastSyncError,
                    content = decryptionResult.content,
                ),
            )
        }

        is SecureItemDecryptionResult.Error -> {
            ObserveSecureItemDetailResult.Error(mapDecryptionError(decryptionResult.reason))
        }
    }

    private fun mapDecryptionError(error: SecureItemCryptoError): SecureItemCrudError = when (error) {
        SecureItemCryptoError.VaultLocked,
        SecureItemCryptoError.AccountIdUnavailable,
        -> SecureItemCrudError.VaultLocked

        SecureItemCryptoError.MalformedPayload,
        SecureItemCryptoError.CryptographicFailure,
        is SecureItemCryptoError.ContentDecodingFailed,
        -> SecureItemCrudError.CorruptedPayload
    }
}
