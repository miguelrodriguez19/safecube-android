package com.miguelrodriguez19.safecube.core.storage.local

import com.miguelrodriguez19.safecube.core.storage.SecureItemDraftDao
import com.miguelrodriguez19.safecube.core.vault.domain.model.secureitem.SecureItemSyncDraft
import com.miguelrodriguez19.safecube.core.vault.domain.repository.SecureItemDraftRepository
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

@Singleton
class SecureItemDraftLocalStorage @Inject constructor(
    private val secureItemDraftDao: SecureItemDraftDao,
    private val secureItemDraftEntityMapper: SecureItemDraftEntityMapper,
) : SecureItemDraftRepository {
    override fun observeDrafts(): Flow<List<SecureItemSyncDraft>> =
        secureItemDraftDao.observeDrafts().map { drafts ->
            drafts.map(secureItemDraftEntityMapper::toDomain)
        }

    override fun observeDraft(logicalItemId: UUID): Flow<SecureItemSyncDraft?> =
        secureItemDraftDao.observeDraft(logicalItemId).map { draft ->
            draft?.let(secureItemDraftEntityMapper::toDomain)
        }

    override suspend fun getDraft(logicalItemId: UUID): SecureItemSyncDraft? =
        secureItemDraftDao.getDraft(logicalItemId)?.let(secureItemDraftEntityMapper::toDomain)

    override suspend fun findByRemoteItemId(remoteItemId: UUID): SecureItemSyncDraft? =
        secureItemDraftDao.findByRemoteItemId(remoteItemId)?.let(secureItemDraftEntityMapper::toDomain)

    override suspend fun upsert(draft: SecureItemSyncDraft) {
        secureItemDraftDao.upsert(secureItemDraftEntityMapper.toEntity(draft))
    }

    override suspend fun delete(logicalItemId: UUID): Boolean =
        secureItemDraftDao.delete(logicalItemId) > 0
}
