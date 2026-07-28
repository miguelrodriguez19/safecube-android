package com.miguelrodriguez19.safecube.core.vault.domain.usecase.secureitem

import com.miguelrodriguez19.safecube.core.vault.domain.model.VaultState
import com.miguelrodriguez19.safecube.core.vault.domain.model.secureitem.SecureItem
import com.miguelrodriguez19.safecube.core.vault.domain.model.secureitem.SecureItemSyncDraft
import com.miguelrodriguez19.safecube.core.vault.domain.model.secureitem.SecureItemSyncState
import com.miguelrodriguez19.safecube.core.vault.domain.model.secureitem.crud.ObserveSecureItemDraftDetailResult
import com.miguelrodriguez19.safecube.core.vault.domain.model.secureitem.crud.SecureItemCrudError
import com.miguelrodriguez19.safecube.core.vault.domain.model.secureitem.crud.SecureItemDraftDetail
import com.miguelrodriguez19.safecube.core.vault.domain.repository.SecureItemDraftRepository
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
class ObserveSecureItemDraftDetailUseCase @Inject constructor(
    private val secureItemDraftRepository: SecureItemDraftRepository,
    private val secureItemRepository: SecureItemRepository,
    private val secureItemCryptoService: SecureItemCryptoService,
    private val vaultSessionManager: VaultSessionManager,
) {
    operator fun invoke(logicalItemId: UUID): Flow<ObserveSecureItemDraftDetailResult> = combine(
        vaultSessionManager.vaultState,
        secureItemDraftRepository.observeDraft(logicalItemId),
        secureItemRepository.observeItem(logicalItemId),
    ) { vaultState, draft, official ->
        when {
            draft == null -> ObserveSecureItemDraftDetailResult.NotFound
            vaultState != VaultState.Unlocked -> ObserveSecureItemDraftDetailResult.Error(
                SecureItemCrudError.VaultLocked
            )

            else -> decryptDraft(
                draft = draft,
                requiresSaveAsNew = draft.draftType ==
                    com.miguelrodriguez19.safecube.core.vault.domain.model.secureitem.SecureItemDraftType.UPDATE &&
                    official?.deletedAt != null,
            )
        }
    }

    private fun decryptDraft(
        draft: SecureItemSyncDraft,
        requiresSaveAsNew: Boolean,
    ): ObserveSecureItemDraftDetailResult {
        return when (val decryptionResult = secureItemCryptoService.decrypt(draft.toSecureItem())) {
            is SecureItemDecryptionResult.Success -> {
                ObserveSecureItemDraftDetailResult.Success(
                    detail = SecureItemDraftDetail(
                        logicalItemId = draft.logicalItemId,
                        remoteItemId = draft.remoteItemId,
                        draftType = draft.draftType,
                        draftSyncStatus = draft.draftSyncStatus,
                        itemType = draft.itemType,
                        displayHint = draft.displayHint,
                        payloadVersion = draft.payloadVersion,
                        updatedAt = draft.updatedAt,
                        lastSyncError = draft.lastSyncError,
                        content = decryptionResult.content,
                        requiresSaveAsNew = requiresSaveAsNew,
                    ),
                )
            }

            is SecureItemDecryptionResult.Error -> {
                ObserveSecureItemDraftDetailResult.Error(mapDecryptionError(decryptionResult.reason))
            }
        }
    }

    private fun mapDecryptionError(error: SecureItemCryptoError): SecureItemCrudError =
        when (error) {
            SecureItemCryptoError.VaultLocked,
            SecureItemCryptoError.AccountIdUnavailable,
                -> SecureItemCrudError.VaultLocked

            SecureItemCryptoError.MalformedPayload,
            SecureItemCryptoError.CryptographicFailure,
            is SecureItemCryptoError.ContentDecodingFailed,
                -> SecureItemCrudError.CorruptedPayload
        }
}

private fun SecureItemSyncDraft.toSecureItem(): SecureItem = SecureItem(
    logicalItemId = logicalItemId,
    remoteItemId = remoteItemId,
    itemType = itemType,
    schemaVersion = schemaVersion,
    displayHint = displayHint,
    payload = payload,
    payloadVersion = payloadVersion,
    createdAt = createdAt,
    updatedAt = updatedAt,
    deletedAt = deletedAt,
    syncState = SecureItemSyncState.SYNCED,
    lastSyncedAt = lastSyncedAt,
    lastSyncError = lastSyncError,
)
