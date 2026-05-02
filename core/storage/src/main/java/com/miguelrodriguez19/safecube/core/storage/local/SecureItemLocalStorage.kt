package com.miguelrodriguez19.safecube.core.storage.local

import com.miguelrodriguez19.safecube.core.storage.SecureItemDao
import com.miguelrodriguez19.safecube.core.storage.SecureItemEntity
import com.miguelrodriguez19.safecube.core.storage.SecureItemSyncStateDb
import com.miguelrodriguez19.safecube.core.storage.SecureItemSyncCheckpointDao
import com.miguelrodriguez19.safecube.core.storage.SecureItemSyncCheckpointEntity
import com.miguelrodriguez19.safecube.core.vault.domain.model.secureitem.SecureItem
import com.miguelrodriguez19.safecube.core.vault.domain.model.secureitem.SecureItemSyncState
import com.miguelrodriguez19.safecube.core.vault.domain.model.secureitem.SecureItemType
import com.miguelrodriguez19.safecube.core.vault.domain.repository.SecureItemRepository
import java.time.Instant
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

@Singleton
class SecureItemLocalStorage @Inject constructor(
    private val secureItemDao: SecureItemDao,
    private val secureItemSyncCheckpointDao: SecureItemSyncCheckpointDao,
) : SecureItemRepository {
    override fun observeActiveItems(): Flow<List<SecureItem>> =
        secureItemDao.observeActiveItems().map { items ->
            items.map(SecureItemEntity::toDomain)
        }

    override fun observeItem(logicalItemId: UUID): Flow<SecureItem?> =
        secureItemDao.observeItem(logicalItemId).map { item ->
            item?.toDomain()
        }

    override suspend fun getItem(logicalItemId: UUID): SecureItem? =
        secureItemDao.getItem(logicalItemId)?.toDomain()

    override suspend fun insert(item: SecureItem) {
        secureItemDao.insert(item.toEntity())
    }

    override suspend fun update(item: SecureItem) {
        secureItemDao.update(item.toEntity())
    }

    override suspend fun softDelete(
        logicalItemId: UUID,
        deletedAt: Instant
    ): Boolean = secureItemDao.softDelete(
        logicalItemId = logicalItemId,
        deletedAt = deletedAt
    ) > 0

    override suspend fun getPendingSyncItemsOrdered(): List<SecureItem> =
        secureItemDao.getPendingSyncItemsOrdered().map(SecureItemEntity::toDomain)

    override suspend fun findByRemoteItemId(remoteItemId: UUID): SecureItem? =
        secureItemDao.findByRemoteItemId(remoteItemId)?.toDomain()

    override suspend fun markPendingCreate(logicalItemId: UUID): Boolean =
        secureItemDao.markPendingCreate(
            logicalItemId = logicalItemId
        ) > 0

    override suspend fun markPendingUpdate(logicalItemId: UUID): Boolean =
        secureItemDao.markPendingUpdate(
            logicalItemId = logicalItemId
        ) > 0

    override suspend fun markPendingDelete(
        logicalItemId: UUID,
        deletedAt: Instant,
    ): Boolean = secureItemDao.markPendingDelete(
        logicalItemId = logicalItemId,
        deletedAt = deletedAt,
    ) > 0

    override suspend fun markSynced(
        logicalItemId: UUID,
        remoteItemId: UUID?,
        payloadVersion: Long,
        updatedAt: Instant,
        deletedAt: Instant?,
        lastSyncedAt: Instant,
    ): Boolean = secureItemDao.markSynced(
        logicalItemId = logicalItemId,
        remoteItemId = remoteItemId,
        payloadVersion = payloadVersion,
        updatedAt = updatedAt,
        deletedAt = deletedAt,
        lastSyncedAt = lastSyncedAt
    ) > 0

    override suspend fun markConflict(
        logicalItemId: UUID,
        lastSyncError: String,
    ): Boolean = secureItemDao.markConflict(
        logicalItemId = logicalItemId,
        lastSyncError = lastSyncError
    ) > 0

    override suspend fun applyRemoteUpsert(
        item: SecureItem,
        lastSyncedAt: Instant,
    ): Boolean {
        val remoteItemId = item.remoteItemId ?: return false
        val currentByRemoteId = secureItemDao.findByRemoteItemId(remoteItemId)

        val entity = currentByRemoteId?.copy(
            itemType = item.itemType.wireName,
            schemaVersion = item.schemaVersion,
            displayHint = item.displayHint,
            payload = item.payload,
            payloadVersion = item.payloadVersion,
            updatedAt = item.updatedAt,
            deletedAt = item.deletedAt,
            syncState = SecureItemSyncStateDb.SYNCED,
            lastSyncedAt = lastSyncedAt,
            lastSyncError = null,
        ) ?: item.copy(
            syncState = SecureItemSyncState.SYNCED,
            lastSyncedAt = lastSyncedAt,
            lastSyncError = null,
        ).toEntity()

        secureItemDao.upsert(entity)
        return true
    }

    override suspend fun applyRemoteDelete(
        remoteItemId: UUID,
        deletedAt: Instant,
        lastSyncedAt: Instant,
    ): Boolean = secureItemDao.applyRemoteDelete(
        remoteItemId = remoteItemId,
        deletedAt = deletedAt,
        syncState = SecureItemSyncStateDb.SYNCED,
        lastSyncedAt = lastSyncedAt
    ) > 0

    override suspend fun getSyncCheckpoint(accountId: UUID): Instant? =
        secureItemSyncCheckpointDao.getLastPulledAt(accountId)

    override suspend fun updateSyncCheckpoint(
        accountId: UUID,
        lastPulledAt: Instant,
    ) {
        secureItemSyncCheckpointDao.upsert(
            SecureItemSyncCheckpointEntity(
                accountId = accountId,
                lastPulledAt = lastPulledAt,
            ),
        )
    }
}

private fun SecureItemEntity.toDomain(): SecureItem = SecureItem(
    logicalItemId = logicalItemId,
    remoteItemId = remoteItemId,
    itemType = SecureItemType.fromWireName(itemType)
        ?: error("Unsupported SecureItemType '$itemType' in local storage."),
    schemaVersion = schemaVersion,
    displayHint = displayHint,
    payload = payload,
    payloadVersion = payloadVersion,
    createdAt = createdAt,
    updatedAt = updatedAt,
    deletedAt = deletedAt,
    syncState = syncState.toDomain(),
    lastSyncedAt = lastSyncedAt,
    lastSyncError = lastSyncError,
)

private fun SecureItem.toEntity(): SecureItemEntity = SecureItemEntity(
    logicalItemId = logicalItemId,
    remoteItemId = remoteItemId,
    itemType = itemType.wireName,
    schemaVersion = schemaVersion,
    displayHint = displayHint,
    payload = payload,
    payloadVersion = payloadVersion,
    createdAt = createdAt,
    updatedAt = updatedAt,
    deletedAt = deletedAt,
    syncState = SecureItemSyncStateDb.fromDomain(syncState),
    lastSyncedAt = lastSyncedAt,
    lastSyncError = lastSyncError,
)
