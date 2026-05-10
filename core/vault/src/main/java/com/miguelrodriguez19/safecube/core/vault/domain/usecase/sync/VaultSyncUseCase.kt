package com.miguelrodriguez19.safecube.core.vault.domain.usecase.sync

import com.miguelrodriguez19.safecube.core.vault.domain.model.sync.VaultSyncError
import com.miguelrodriguez19.safecube.core.vault.domain.model.sync.VaultSyncResult
import com.miguelrodriguez19.safecube.core.vault.domain.model.sync.pull.PullVaultDeltaError
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

        val pullOutcome = when (val pullPhaseResult = runPullPhase(limit = pullLimit)) {
            is PullPhaseResult.Success -> pullPhaseResult.outcome
            is PullPhaseResult.Error -> {
                return VaultSyncResult.Error(
                    reason = VaultSyncError.PullFailed(pullPhaseResult.reason),
                )
            }
        }

        return when (val pushPhaseResult = runPushPhase()) {
            is PushPhaseResult.Success -> {
                VaultSyncResult.Success(
                    uploadedCount = pushPhaseResult.outcome.uploadedCount,
                    downloadedCount = pullOutcome.downloadedCount,
                    conflictCount = pushPhaseResult.outcome.conflictCount + pullOutcome.conflictCount,
                )
            }

            is PushPhaseResult.Error -> {
                VaultSyncResult.Error(
                    reason = VaultSyncError.PushFailed(pushPhaseResult.reason),
                    uploadedCount = 0,
                    downloadedCount = pullOutcome.downloadedCount,
                    conflictCount = pullOutcome.conflictCount,
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

    private suspend fun runPullPhase(limit: Int?): PullPhaseResult =
        when (val pullResult = pullVaultDeltaUseCase(limit = limit)) {
            is PullVaultDeltaResult.Success -> PullPhaseResult.Success(
                PullOutcome(
                    downloadedCount = pullResult.appliedUpsertCount + pullResult.appliedDeleteCount,
                    conflictCount = pullResult.skippedDirtyOrConflictCount,
                ),
            )

            is PullVaultDeltaResult.Error -> PullPhaseResult.Error(pullResult.reason)
        }

    private data class PushOutcome(
        val uploadedCount: Int,
        val conflictCount: Int,
    )

    private data class PullOutcome(
        val downloadedCount: Int,
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

    private sealed interface PullPhaseResult {
        data class Success(
            val outcome: PullOutcome,
        ) : PullPhaseResult

        data class Error(
            val reason: PullVaultDeltaError,
        ) : PullPhaseResult
    }
}
