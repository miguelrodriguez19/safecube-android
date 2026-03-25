package com.miguelrodriguez19.safecube.core.vault.domain.repository

import com.miguelrodriguez19.safecube.core.vault.domain.model.secureitem.SecureItem
import java.time.Instant
import java.util.UUID
import kotlinx.coroutines.flow.Flow

interface SecureItemRepository {
    fun observeActiveItems(): Flow<List<SecureItem>>

    fun observeItem(logicalItemId: UUID): Flow<SecureItem?>

    suspend fun getItem(logicalItemId: UUID): SecureItem?

    suspend fun insert(item: SecureItem)

    suspend fun update(item: SecureItem)

    suspend fun softDelete(
        logicalItemId: UUID,
        deletedAt: Instant,
    ): Boolean
}
