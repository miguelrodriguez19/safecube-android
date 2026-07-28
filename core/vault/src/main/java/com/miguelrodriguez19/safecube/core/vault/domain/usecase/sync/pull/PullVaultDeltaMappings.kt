package com.miguelrodriguez19.safecube.core.vault.domain.usecase.sync.pull

import com.miguelrodriguez19.safecube.core.vault.domain.model.VaultKeyMaterial
import com.miguelrodriguez19.safecube.core.vault.domain.model.remote.RemoteSecureItem
import com.miguelrodriguez19.safecube.core.vault.domain.model.secureitem.SecureItem
import com.miguelrodriguez19.safecube.core.vault.domain.model.secureitem.SecureItemSyncState
import com.miguelrodriguez19.safecube.core.vault.domain.model.secureitem.SecureItemType
import java.time.Instant
import java.util.UUID

internal fun VaultKeyMaterial?.accountIdOrNull(): UUID? = this?.accountId

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
    itemRevision = itemRevision,
    changeSequence = changeSequence,
    createdAt = createdAt,
    updatedAt = updatedAt,
    deletedAt = deletedAt,
    syncState = SecureItemSyncState.SYNCED,
    lastSyncedAt = updatedAt,
    lastSyncError = null,
)

internal fun SecureItem.matchesOfficialRemoteState(other: SecureItem): Boolean =
    logicalItemId == other.logicalItemId &&
        remoteItemId == other.remoteItemId &&
        itemType == other.itemType &&
        schemaVersion == other.schemaVersion &&
        displayHint == other.displayHint &&
        payload.contentEquals(other.payload) &&
        payloadVersion == other.payloadVersion &&
        itemRevision == other.itemRevision &&
        changeSequence == other.changeSequence &&
        createdAt == other.createdAt &&
        updatedAt == other.updatedAt &&
        deletedAt == other.deletedAt
