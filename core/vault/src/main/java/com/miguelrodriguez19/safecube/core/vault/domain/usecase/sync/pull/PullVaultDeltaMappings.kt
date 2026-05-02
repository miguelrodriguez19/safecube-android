package com.miguelrodriguez19.safecube.core.vault.domain.usecase.sync.pull

import com.miguelrodriguez19.safecube.core.vault.domain.model.VaultKeyMaterial
import com.miguelrodriguez19.safecube.core.vault.domain.model.remote.RemoteSecureItem
import com.miguelrodriguez19.safecube.core.vault.domain.model.remote.RemoteSecureItemSummary
import com.miguelrodriguez19.safecube.core.vault.domain.model.secureitem.SecureItem
import com.miguelrodriguez19.safecube.core.vault.domain.model.secureitem.SecureItemSyncState
import com.miguelrodriguez19.safecube.core.vault.domain.model.secureitem.SecureItemType
import java.time.Instant
import java.util.UUID

internal fun VaultKeyMaterial?.accountIdOrNull(): UUID? = this?.accountId

internal fun SecureItemSyncState.blocksRemotePullOverwrite(): Boolean = when (this) {
    SecureItemSyncState.SYNCED -> false
    SecureItemSyncState.PENDING_CREATE,
    SecureItemSyncState.PENDING_UPDATE,
    SecureItemSyncState.PENDING_DELETE,
    SecureItemSyncState.CONFLICT,
    -> true
}

internal fun List<RemoteSecureItemSummary>.deduplicateByItemIdKeepingLatest(): List<RemoteSecureItemSummary> = this
    .groupBy(RemoteSecureItemSummary::itemId)
    .values
    .map(::pickLatestSummary)

private fun pickLatestSummary(candidates: List<RemoteSecureItemSummary>): RemoteSecureItemSummary =
    candidates.reduce { current, candidate ->
        when {
            candidate.updatedAt > current.updatedAt -> candidate
            candidate.updatedAt < current.updatedAt -> current
            candidate.deletedAt != null && current.deletedAt == null -> candidate
            else -> current
        }
    }

internal fun RemoteSecureItem.toLocalSecureItem(
    logicalItemId: UUID,
    itemType: SecureItemType,
    createdAt: Instant,
): SecureItem = SecureItem(
    logicalItemId = logicalItemId,
    remoteItemId = itemId,
    itemType = itemType,
    schemaVersion = schemaVersion,
    displayHint = displayHint,
    payload = payload,
    payloadVersion = payloadVersion,
    createdAt = createdAt,
    updatedAt = updatedAt,
    deletedAt = deletedAt,
    syncState = SecureItemSyncState.SYNCED,
    lastSyncedAt = updatedAt,
    lastSyncError = null,
)
