package com.miguelrodriguez19.safecube.core.storage.local

import com.miguelrodriguez19.safecube.core.storage.SecureItemSyncCheckpointDao
import com.miguelrodriguez19.safecube.core.storage.SecureItemSyncCheckpointEntity
import com.miguelrodriguez19.safecube.core.vault.domain.repository.SecureItemSyncCheckpointRepository
import java.time.Instant
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SecureItemSyncCheckpointLocalStorage @Inject constructor(
    private val secureItemSyncCheckpointDao: SecureItemSyncCheckpointDao,
) : SecureItemSyncCheckpointRepository {
    override suspend fun getLastPulledAt(accountId: UUID): Instant? =
        secureItemSyncCheckpointDao.getLastPulledAt(accountId)

    override suspend fun updateLastPulledAt(
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
