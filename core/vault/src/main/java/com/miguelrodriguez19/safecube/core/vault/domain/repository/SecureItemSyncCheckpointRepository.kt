package com.miguelrodriguez19.safecube.core.vault.domain.repository

import java.time.Instant
import java.util.UUID

interface SecureItemSyncCheckpointRepository {
    suspend fun getLastPulledAt(accountId: UUID): Instant?

    suspend fun updateLastPulledAt(
        accountId: UUID,
        lastPulledAt: Instant,
    )
}
