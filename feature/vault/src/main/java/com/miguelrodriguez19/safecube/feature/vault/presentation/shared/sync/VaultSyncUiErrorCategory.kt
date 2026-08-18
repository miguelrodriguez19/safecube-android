package com.miguelrodriguez19.safecube.feature.vault.presentation.shared.sync

import com.miguelrodriguez19.safecube.core.network.domain.model.NetworkFailureKind
import com.miguelrodriguez19.safecube.core.vault.domain.model.sync.VaultSyncError
import com.miguelrodriguez19.safecube.core.vault.domain.model.sync.pull.PullVaultDeltaError
import com.miguelrodriguez19.safecube.core.vault.domain.model.sync.push.PushLocalVaultChangesError

enum class VaultSyncUiErrorCategory {
    OfflineOrTimeout,
    ServiceUnavailable,
    SessionRequired,
    Conflict,
    ProtocolIntegrity,
    StorageOrCrypto,
}

fun VaultSyncError.toUiCategory(): VaultSyncUiErrorCategory = when (this) {
    is VaultSyncError.InvalidVaultState -> VaultSyncUiErrorCategory.SessionRequired
    is VaultSyncError.PushFailed -> error.toUiCategory()
    is VaultSyncError.PullFailed -> error.toUiCategory()
}

private fun PushLocalVaultChangesError.toUiCategory(): VaultSyncUiErrorCategory = when (this) {
    is PushLocalVaultChangesError.LocalStateUpdateFailed ->
        VaultSyncUiErrorCategory.StorageOrCrypto

    is PushLocalVaultChangesError.ProtocolIntegrityFailed ->
        VaultSyncUiErrorCategory.ProtocolIntegrity

    is PushLocalVaultChangesError.RemoteFailure -> failure.kind.toUiCategory()
}

private fun PullVaultDeltaError.toUiCategory(): VaultSyncUiErrorCategory = when (this) {
    PullVaultDeltaError.AccountIdUnavailable -> VaultSyncUiErrorCategory.SessionRequired
    is PullVaultDeltaError.RemoteListFailed -> error.failure.kind.toUiCategory()
    is PullVaultDeltaError.RemoteDetailFailed -> error.failure.kind.toUiCategory()
    is PullVaultDeltaError.RemoteDetailMissing -> VaultSyncUiErrorCategory.ProtocolIntegrity
    is PullVaultDeltaError.UnsupportedRemoteItemType -> VaultSyncUiErrorCategory.ProtocolIntegrity
    is PullVaultDeltaError.LocalApplyFailed -> VaultSyncUiErrorCategory.StorageOrCrypto
}

private fun NetworkFailureKind.toUiCategory(): VaultSyncUiErrorCategory = when (this) {
    NetworkFailureKind.Connectivity,
    NetworkFailureKind.Timeout,
        -> VaultSyncUiErrorCategory.OfflineOrTimeout

    NetworkFailureKind.RateLimited,
    NetworkFailureKind.ServerUnavailable,
        -> VaultSyncUiErrorCategory.ServiceUnavailable

    NetworkFailureKind.Unauthorized,
    NetworkFailureKind.Forbidden,
        -> VaultSyncUiErrorCategory.SessionRequired

    NetworkFailureKind.Conflict -> VaultSyncUiErrorCategory.Conflict

    NetworkFailureKind.Validation,
    NetworkFailureKind.Protocol,
    NetworkFailureKind.MalformedResponse,
    NetworkFailureKind.Unknown,
        -> VaultSyncUiErrorCategory.ProtocolIntegrity
}
