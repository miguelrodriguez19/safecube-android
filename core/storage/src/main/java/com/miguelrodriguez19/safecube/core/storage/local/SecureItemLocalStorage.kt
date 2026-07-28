package com.miguelrodriguez19.safecube.core.storage.local

import androidx.room.withTransaction
import com.miguelrodriguez19.safecube.core.storage.AppDatabase
import com.miguelrodriguez19.safecube.core.storage.SecureItemDao
import com.miguelrodriguez19.safecube.core.storage.SecureItemDraftDao
import com.miguelrodriguez19.safecube.core.storage.SecureItemEntity
import com.miguelrodriguez19.safecube.core.storage.SecureItemSyncStateDb
import com.miguelrodriguez19.safecube.core.storage.SecureItemSyncCheckpointDao
import com.miguelrodriguez19.safecube.core.storage.SecureItemSyncCheckpointEntity
import com.miguelrodriguez19.safecube.core.vault.domain.model.secureitem.SecureItem
import com.miguelrodriguez19.safecube.core.vault.domain.model.secureitem.SecureItemSyncState
import com.miguelrodriguez19.safecube.core.vault.domain.model.secureitem.SecureItemSyncDraft
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
    private val appDatabase: AppDatabase,
    private val secureItemDao: SecureItemDao,
    private val secureItemDraftDao: SecureItemDraftDao,
    private val secureItemSyncCheckpointDao: SecureItemSyncCheckpointDao,
    private val secureItemDraftEntityMapper: SecureItemDraftEntityMapper,
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

    override suspend fun findByRemoteItemId(remoteItemId: UUID): SecureItem? =
        secureItemDao.findByRemoteItemId(remoteItemId)?.toDomain()

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
            itemRevision = item.itemRevision,
            changeSequence = item.changeSequence,
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
        itemRevision: Long,
        changeSequence: Long,
        lastSyncedAt: Instant,
    ): Boolean = secureItemDao.applyRemoteDelete(
        remoteItemId = remoteItemId,
        deletedAt = deletedAt,
        itemRevision = itemRevision,
        changeSequence = changeSequence,
        syncState = SecureItemSyncStateDb.SYNCED,
        lastSyncedAt = lastSyncedAt
    ) > 0

    override suspend fun getSyncCheckpoint(accountId: UUID): Long? =
        secureItemSyncCheckpointDao.getLastAppliedChangeSequence(accountId)

    override suspend fun updateSyncCheckpoint(
        accountId: UUID,
        lastAppliedChangeSequence: Long,
    ) {
        secureItemSyncCheckpointDao.upsert(
            SecureItemSyncCheckpointEntity(
                accountId = accountId,
                lastAppliedChangeSequence = lastAppliedChangeSequence,
            ),
        )
    }

    override suspend fun officializeDraft(
        item: SecureItem,
        lastSyncedAt: Instant,
    ): Boolean {
        if (item.remoteItemId == null) return false
        return runCatching {
            appDatabase.withTransaction {
                check(applyRemoteUpsert(item, lastSyncedAt))
                check(secureItemDraftDao.delete(item.logicalItemId) > 0)
                true
            }
        }.getOrDefault(false)
    }

    override suspend fun replaceOfficialWithConflictedDraft(
        item: SecureItem,
        draft: SecureItemSyncDraft,
        lastSyncedAt: Instant,
    ): Boolean {
        if (item.remoteItemId == null) return false
        return runCatching {
            appDatabase.withTransaction {
                check(applyRemoteUpsert(item, lastSyncedAt))
                secureItemDraftDao.upsert(secureItemDraftEntityMapper.toEntity(draft))
                true
            }
        }.getOrDefault(false)
    }

    override suspend fun applyRemotePage(
        accountId: UUID,
        items: List<SecureItem>,
        conflictedDrafts: List<SecureItemSyncDraft>,
        draftsToDelete: Set<UUID>,
        lastAppliedChangeSequence: Long,
        lastSyncedAt: Instant,
    ): Boolean {
        if (items.any { it.remoteItemId == null }) return false
        return runCatching {
            appDatabase.withTransaction {
                items.forEach { item ->
                    check(applyRemoteUpsert(item, lastSyncedAt))
                }
                conflictedDrafts.forEach { draft ->
                    secureItemDraftDao.upsert(secureItemDraftEntityMapper.toEntity(draft))
                }
                draftsToDelete.forEach { logicalItemId ->
                    secureItemDraftDao.delete(logicalItemId)
                }
                updateSyncCheckpoint(accountId, lastAppliedChangeSequence)
                true
            }
        }.getOrDefault(false)
    }

    override suspend fun clearAllLocalData() {
        appDatabase.withTransaction {
            secureItemDao.deleteAll()
            secureItemDraftDao.deleteAll()
            secureItemSyncCheckpointDao.deleteAll()
        }
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
    itemRevision = itemRevision,
    changeSequence = changeSequence,
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
    itemRevision = itemRevision,
    changeSequence = changeSequence,
    createdAt = createdAt,
    updatedAt = updatedAt,
    deletedAt = deletedAt,
    syncState = SecureItemSyncStateDb.fromDomain(syncState),
    lastSyncedAt = lastSyncedAt,
    lastSyncError = lastSyncError,
)
