package com.miguelrodriguez19.safecube.core.storage.local

import com.miguelrodriguez19.safecube.core.storage.SecureItemDao
import com.miguelrodriguez19.safecube.core.storage.SecureItemEntity
import com.miguelrodriguez19.safecube.core.vault.domain.model.secureitem.SecureItem
import com.miguelrodriguez19.safecube.core.vault.domain.model.secureitem.SecureItemSyncState
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
        deletedAt = deletedAt,
    ) > 0
}

private fun SecureItemEntity.toDomain(): SecureItem = SecureItem(
    logicalItemId = logicalItemId,
    remoteItemId = remoteItemId,
    itemType = com.miguelrodriguez19.safecube.core.vault.domain.model.secureitem.SecureItemType.fromWireName(itemType)
        ?: error("Unsupported SecureItemType '$itemType' in local storage."),
    schemaVersion = schemaVersion,
    displayHint = displayHint,
    payload = payload,
    payloadVersion = payloadVersion,
    createdAt = createdAt,
    updatedAt = updatedAt,
    deletedAt = deletedAt,
    syncState = SecureItemSyncState.fromStorageValue(syncState)
        ?: error("Unsupported SecureItemSyncState '$syncState' in local storage."),
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
    syncState = syncState.storageValue,
    lastSyncedAt = lastSyncedAt,
    lastSyncError = lastSyncError,
)
