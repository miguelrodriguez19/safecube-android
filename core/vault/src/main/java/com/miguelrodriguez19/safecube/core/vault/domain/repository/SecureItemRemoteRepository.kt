package com.miguelrodriguez19.safecube.core.vault.domain.repository

import com.miguelrodriguez19.safecube.core.vault.domain.model.remote.request.RemoteCreateSecureItemRequest
import com.miguelrodriguez19.safecube.core.vault.domain.model.remote.result.RemoteCreateSecureItemResult
import com.miguelrodriguez19.safecube.core.vault.domain.model.remote.result.RemoteDeleteSecureItemResult
import com.miguelrodriguez19.safecube.core.vault.domain.model.remote.RemoteListVaultItemsRequestParams
import com.miguelrodriguez19.safecube.core.vault.domain.model.remote.RemoteSecureItem
import com.miguelrodriguez19.safecube.core.vault.domain.model.remote.RemoteSecureItemChangesPage
import com.miguelrodriguez19.safecube.core.vault.domain.model.remote.RemoteSecureItemSummary
import com.miguelrodriguez19.safecube.core.vault.domain.model.remote.request.RemoteUpdateSecureItemRequest
import com.miguelrodriguez19.safecube.core.vault.domain.model.remote.request.RemoteDeleteSecureItemRequest
import com.miguelrodriguez19.safecube.core.vault.domain.model.remote.result.RemoteUpdateSecureItemResult
import com.miguelrodriguez19.safecube.core.vault.domain.model.remote.result.SecureItemRemoteResult
import java.util.UUID

interface SecureItemRemoteRepository {
    suspend fun listVaultItems(
        requestParams: RemoteListVaultItemsRequestParams,
    ): SecureItemRemoteResult<List<RemoteSecureItemSummary>>

    suspend fun getVaultItem(remoteItemId: UUID): SecureItemRemoteResult<RemoteSecureItem>

    suspend fun listVaultItemChanges(
        after: Long,
        limit: Int,
    ): SecureItemRemoteResult<RemoteSecureItemChangesPage>

    suspend fun createVaultItem(
        request: RemoteCreateSecureItemRequest,
    ): SecureItemRemoteResult<RemoteCreateSecureItemResult>

    suspend fun updateVaultItem(
        remoteItemId: UUID,
        request: RemoteUpdateSecureItemRequest,
    ): SecureItemRemoteResult<RemoteUpdateSecureItemResult>

    suspend fun deleteVaultItem(
        remoteItemId: UUID,
        request: RemoteDeleteSecureItemRequest,
    ): SecureItemRemoteResult<RemoteDeleteSecureItemResult>
}
