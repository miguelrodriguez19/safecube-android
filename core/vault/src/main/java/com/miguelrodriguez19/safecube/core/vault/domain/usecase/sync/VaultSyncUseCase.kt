package com.miguelrodriguez19.safecube.core.vault.domain.usecase.sync

import com.miguelrodriguez19.safecube.core.vault.domain.model.VaultState
import com.miguelrodriguez19.safecube.core.vault.domain.model.sync.VaultSyncError
import com.miguelrodriguez19.safecube.core.vault.domain.model.sync.VaultSyncResult
import com.miguelrodriguez19.safecube.core.vault.domain.model.sync.pull.PullVaultDeltaResult
import com.miguelrodriguez19.safecube.core.vault.domain.model.sync.push.PushLocalVaultChangesError
import com.miguelrodriguez19.safecube.core.vault.domain.model.sync.push.PushLocalVaultChangesResult
import com.miguelrodriguez19.safecube.core.vault.domain.session.VaultSessionManager
import com.miguelrodriguez19.safecube.core.vault.domain.usecase.sync.pull.PullVaultDeltaUseCase
import com.miguelrodriguez19.safecube.core.vault.domain.usecase.sync.push.PushLocalVaultChangesUseCase
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class VaultSyncUseCase @Inject constructor(
    private val vaultSessionManager: VaultSessionManager,
    private val pushLocalVaultChangesUseCase: PushLocalVaultChangesUseCase,
    private val pullVaultDeltaUseCase: PullVaultDeltaUseCase,
    private val vaultSyncExecutionLock: VaultSyncExecutionLock,
) {
    suspend operator fun invoke(
        pullLimit: Int? = null,
    ): VaultSyncResult = vaultSyncExecutionLock.withLock {
        executeSync(pullLimit)
    }

    private suspend fun executeSync(pullLimit: Int?): VaultSyncResult {
        validateUnlockedState()?.let { return it }

        val pushOutcome = when (val pushPhaseResult = runPushPhase()) {
            is PushPhaseResult.Success -> pushPhaseResult.outcome
            is PushPhaseResult.Error -> {
                return VaultSyncResult.Error(
                    reason = VaultSyncError.PushFailed(pushPhaseResult.reason)
                )
            }
        }

        return when (val pullResult = pullVaultDeltaUseCase(limit = pullLimit)) {
            is PullVaultDeltaResult.Success -> {
                VaultSyncResult.Success(
                    uploadedCount = pushOutcome.uploadedCount,
                    downloadedCount = pullResult.appliedUpsertCount + pullResult.appliedDeleteCount,
                    conflictCount = pushOutcome.conflictCount + pullResult.skippedDirtyOrConflictCount,
                )
            }

            is PullVaultDeltaResult.Error -> {
                VaultSyncResult.Error(
                    reason = VaultSyncError.PullFailed(pullResult.reason),
                    uploadedCount = pushOutcome.uploadedCount,
                    downloadedCount = 0,
                    conflictCount = pushOutcome.conflictCount,
                )
            }
        }
    }

    private fun validateUnlockedState(): VaultSyncResult.Error? {
        return if (vaultSessionManager.isUnlocked()) {
            null
        } else {
            VaultSyncResult.Error(
                reason = VaultSyncError.InvalidVaultState(vaultSessionManager.vaultState.value),
                uploadedCount = 0,
                downloadedCount = 0,
                conflictCount = 0,
            )
        }
    }

    private suspend fun runPushPhase(): PushPhaseResult =
        when (val pushResult = pushLocalVaultChangesUseCase()) {
            is PushLocalVaultChangesResult.Success -> PushPhaseResult.Success(
                PushOutcome(
                    uploadedCount = pushResult.syncedCount,
                    conflictCount = pushResult.conflictCount,
                ),
            )

            is PushLocalVaultChangesResult.Error -> PushPhaseResult.Error(pushResult.reason)
        }

    private data class PushOutcome(
        val uploadedCount: Int,
        val conflictCount: Int,
    )

    private sealed interface PushPhaseResult {
        data class Success(
            val outcome: PushOutcome,
        ) : PushPhaseResult

        data class Error(
            val reason: PushLocalVaultChangesError,
        ) : PushPhaseResult
    }
}
