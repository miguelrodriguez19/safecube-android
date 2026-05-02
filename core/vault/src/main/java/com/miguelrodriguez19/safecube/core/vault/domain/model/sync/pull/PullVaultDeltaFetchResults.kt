package com.miguelrodriguez19.safecube.core.vault.domain.model.sync.pull

import com.miguelrodriguez19.safecube.core.vault.domain.model.remote.RemoteSecureItem
import com.miguelrodriguez19.safecube.core.vault.domain.model.remote.RemoteSecureItemSummary
import java.util.UUID

internal sealed interface SummaryFetchResult {
    data class Success(val summaries: List<RemoteSecureItemSummary>) : SummaryFetchResult
    data class Error(val error: PullVaultDeltaError) : SummaryFetchResult
}

internal sealed interface DetailsFetchResult {
    data class Success(val detailsByItemId: Map<UUID, RemoteSecureItem>) : DetailsFetchResult
    data class Error(val error: PullVaultDeltaError) : DetailsFetchResult
}
