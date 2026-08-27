package com.miguelrodriguez19.safecube.core.vault.data.session

import com.miguelrodriguez19.safecube.core.network.domain.model.NetworkFailure
import com.miguelrodriguez19.safecube.core.network.domain.model.RetryDecision
import com.miguelrodriguez19.safecube.core.vault.domain.model.VaultState
import com.miguelrodriguez19.safecube.core.vault.domain.model.quickunlock.VaultUnlockProvenance
import com.miguelrodriguez19.safecube.core.vault.domain.model.remote.result.VaultKeyMaterialRemoteError
import com.miguelrodriguez19.safecube.core.vault.domain.model.remote.result.VaultKeyMaterialRemoteResult
import com.miguelrodriguez19.safecube.core.vault.domain.model.unlock.VaultUnlockError
import com.miguelrodriguez19.safecube.core.vault.domain.model.unlock.VaultUnlockResult
import com.miguelrodriguez19.safecube.core.vault.domain.repository.VaultKeyMaterialLocalRepository
import com.miguelrodriguez19.safecube.core.vault.domain.repository.VaultKeyMaterialLocalReadResult
import com.miguelrodriguez19.safecube.core.vault.domain.repository.VaultKeyMaterialRemoteRepository
import com.miguelrodriguez19.safecube.core.vault.domain.session.VaultSessionManager
import com.miguelrodriguez19.safecube.core.vault.domain.session.QuickUnlockPromptMode
import com.miguelrodriguez19.safecube.core.vault.domain.quickunlock.QuickUnlockCompletionResult
import com.miguelrodriguez19.safecube.core.vault.domain.quickunlock.QuickUnlockCleanupResult
import com.miguelrodriguez19.safecube.core.vault.domain.quickunlock.QuickUnlockEnrollmentResult
import com.miguelrodriguez19.safecube.core.vault.domain.quickunlock.QuickUnlockEnrollmentPreparationResult
import com.miguelrodriguez19.safecube.core.vault.domain.quickunlock.QuickUnlockManager
import com.miguelrodriguez19.safecube.core.vault.domain.quickunlock.QuickUnlockAccountSessionValidator
import com.miguelrodriguez19.safecube.core.vault.domain.quickunlock.QuickUnlockOfferState
import com.miguelrodriguez19.safecube.core.vault.domain.quickunlock.QuickUnlockPreparationResult
import com.miguelrodriguez19.safecube.core.vault.domain.quickunlock.QuickUnlockStoreResult
import com.miguelrodriguez19.safecube.core.vault.domain.usecase.vault.VaultUnlocker
import java.util.Optional
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.UUID

@Singleton
internal class VaultSessionManagerImpl @Inject constructor(
    private val vaultUnlocker: VaultUnlocker,
    private val vaultKeyMaterialLocalRepository: VaultKeyMaterialLocalRepository,
    private val vaultKeyMaterialRemoteRepository: VaultKeyMaterialRemoteRepository,
    private val vaultInMemoryKekStore: VaultInMemoryKekStore,
    private val quickUnlockManager: QuickUnlockManager,
    private val accountSessionValidator: Optional<QuickUnlockAccountSessionValidator>,
    private val pendingQuickUnlockEnrollmentStore: PendingQuickUnlockEnrollmentStore =
        PendingQuickUnlockEnrollmentStore(),
) : VaultSessionManager {
    private val state = MutableStateFlow(initialVaultState())
    private val quickUnlockOperationAccounts = mutableMapOf<String, UUID>()
    private var promptMode = QuickUnlockPromptMode.AutomaticOnUnlockEntry

    override val vaultState: StateFlow<VaultState> = state.asStateFlow()

    override fun quickUnlockPromptMode(): QuickUnlockPromptMode = promptMode

    override fun isUnlocked(): Boolean {
        return state.value == VaultState.Unlocked
    }

    override suspend fun refreshVaultState() {
        promptMode = QuickUnlockPromptMode.AutomaticOnUnlockEntry
        clearInMemoryKek()
        state.value = VaultState.InitialLoading
        val localReadResult = readLocalKeyMaterial()

        when (val result = vaultKeyMaterialRemoteRepository.getKeyMaterial()) {
            is VaultKeyMaterialRemoteResult.Success -> {
                runCatching {
                    vaultKeyMaterialLocalRepository.save(result.value)
                }.onSuccess {
                    state.value = VaultState.Locked
                }.onFailure {
                    state.value = VaultState.CorruptedLocalKeyMaterial
                }
            }

            is VaultKeyMaterialRemoteResult.Error -> {
                when (result.error) {
                    VaultKeyMaterialRemoteError.VaultNotInitialized -> {
                        vaultKeyMaterialLocalRepository.clear()
                        state.value = VaultState.NotInitialized
                    }

                    VaultKeyMaterialRemoteError.Unauthorized -> {
                        state.value = VaultState.AuthenticationRequired
                    }

                    VaultKeyMaterialRemoteError.Forbidden,
                    is VaultKeyMaterialRemoteError.HttpError,
                    is VaultKeyMaterialRemoteError.NetworkError,
                        -> {
                        state.value = resolveStateForRemoteFailure(
                            failure = result.error.failure,
                            localReadResult = localReadResult,
                        )
                    }

                    VaultKeyMaterialRemoteError.VaultAlreadyInitialized -> {
                        state.value = VaultState.TerminalRemoteFailure(result.error.failure)
                    }
                }
            }
        }
    }

    override fun unlockWithPassphrase(passphrase: String): VaultUnlockError? {
        val result = vaultUnlocker.unlockWithPassphrase(passphrase)
        return handleUnlockResult(result, VaultUnlockProvenance.Passphrase)
    }

    override fun unlockWithRecoveryKey(recoveryKey: ByteArray): VaultUnlockError? {
        val result = vaultUnlocker.unlockWithRecoveryKey(recoveryKey)
        return handleUnlockResult(result, VaultUnlockProvenance.RecoveryKey)
    }

    override fun quickUnlockOfferState(): QuickUnlockOfferState = activeAccountId()
        ?.let(quickUnlockManager::offerState)
        ?: QuickUnlockOfferState.AccountUnavailable

    override fun markQuickUnlockOfferSeen(): QuickUnlockStoreResult = activeAccountId()
        ?.let(quickUnlockManager::markOfferSeen)
        ?: QuickUnlockStoreResult.AccountUnavailable

    @Synchronized
    override fun prepareQuickUnlockEnrollment(
        consentGranted: Boolean,
    ): QuickUnlockEnrollmentPreparationResult {
        if (state.value != VaultState.Unlocked) return QuickUnlockEnrollmentPreparationResult.RequiresPassphrase
        val accountId = activeAccountId() ?: return QuickUnlockEnrollmentPreparationResult.AccountUnavailable
        if (!hasValidAccountSession(accountId)) return QuickUnlockEnrollmentPreparationResult.SessionInvalid
        return quickUnlockManager.prepareEnrollment(accountId, consentGranted).also { result ->
            if (result is QuickUnlockEnrollmentPreparationResult.Ready) {
                quickUnlockOperationAccounts[result.operationId] = accountId
            }
        }
    }

    @Synchronized
    override fun finishQuickUnlockEnrollment(
        operationId: String,
    ): QuickUnlockEnrollmentResult {
        val expectedAccountId = quickUnlockOperationAccounts.remove(operationId)
            ?: return QuickUnlockEnrollmentResult.AccountUnavailable
        if (state.value != VaultState.Unlocked) {
            quickUnlockManager.cancelUnlock(operationId)
            return QuickUnlockEnrollmentResult.RequiresPassphrase
        }
        val accountId = activeAccountId() ?: run {
            quickUnlockManager.cancelUnlock(operationId)
            return QuickUnlockEnrollmentResult.AccountUnavailable
        }
        if (accountId != expectedAccountId) {
            quickUnlockManager.cancelUnlock(operationId)
            return QuickUnlockEnrollmentResult.AccountChanged
        }
        if (!hasValidAccountSession(accountId)) {
            quickUnlockManager.cancelUnlock(operationId)
            return QuickUnlockEnrollmentResult.SessionInvalid
        }
        return quickUnlockManager.finishEnrollment(accountId, operationId)
    }

    @Synchronized
    override fun prepareQuickUnlock(): QuickUnlockPreparationResult {
        if (state.value != VaultState.Locked) return QuickUnlockPreparationResult.TemporarilyUnavailable
        val accountId = activeAccountId() ?: return QuickUnlockPreparationResult.AccountUnavailable
        if (!hasValidAccountSession(accountId)) return QuickUnlockPreparationResult.SessionInvalid
        return quickUnlockManager.prepareUnlock(accountId).also { result ->
            if (result is QuickUnlockPreparationResult.Ready) {
                quickUnlockOperationAccounts[result.operationId] = accountId
            }
        }
    }

    @Synchronized
    override fun finishQuickUnlock(operationId: String): QuickUnlockCompletionResult {
        val expectedAccountId = quickUnlockOperationAccounts.remove(operationId)
            ?: return QuickUnlockCompletionResult.AccountUnavailable
        if (state.value != VaultState.Locked) {
            quickUnlockManager.cancelUnlock(operationId)
            return QuickUnlockCompletionResult.StaleOperation
        }
        val accountId = activeAccountId() ?: run {
            quickUnlockManager.cancelUnlock(operationId)
            return QuickUnlockCompletionResult.AccountUnavailable
        }
        if (accountId != expectedAccountId) {
            quickUnlockManager.cancelUnlock(operationId)
            return QuickUnlockCompletionResult.AccountChanged
        }
        if (!hasValidAccountSession(accountId)) {
            quickUnlockManager.cancelUnlock(operationId)
            return QuickUnlockCompletionResult.SessionInvalid
        }
        return quickUnlockManager.finishUnlock(accountId, operationId).also { result ->
            if (result == QuickUnlockCompletionResult.Unlocked) {
                state.value = VaultState.Unlocked
            }
        }
    }

    @Synchronized
    override fun cancelQuickUnlock(operationId: String) {
        quickUnlockOperationAccounts.remove(operationId)
        quickUnlockManager.cancelUnlock(operationId)
    }

    @Synchronized
    override fun clearQuickUnlockEnrollment(): QuickUnlockCleanupResult {
        val accountId = activeAccountId() ?: return QuickUnlockCleanupResult.AccountUnavailable
        if (!hasValidAccountSession(accountId)) return QuickUnlockCleanupResult.SessionInvalid
        return quickUnlockManager.clearEnrollment(accountId)
    }

    @Synchronized
    override fun requestQuickUnlockEnrollmentAfterPassphrase(): Boolean {
        val accountId = activeAccountId() ?: return false
        if (state.value != VaultState.Unlocked || !hasValidAccountSession(accountId)) return false
        pendingQuickUnlockEnrollmentStore.request(accountId)
        return true
    }

    @Synchronized
    override fun consumeQuickUnlockEnrollmentAfterPassphrase(): Boolean {
        val accountId = activeAccountId() ?: run {
            pendingQuickUnlockEnrollmentStore.clear()
            return false
        }
        if (!hasValidAccountSession(accountId)) {
            pendingQuickUnlockEnrollmentStore.clear()
            return false
        }
        return pendingQuickUnlockEnrollmentStore.consume(accountId)
    }

    @Synchronized
    override fun clearPendingQuickUnlockEnrollment() {
        pendingQuickUnlockEnrollmentStore.clear()
    }

    @Synchronized
    override fun lock() {
        lock(QuickUnlockPromptMode.AutomaticOnUnlockEntry)
    }

    @Synchronized
    override fun lock(promptMode: QuickUnlockPromptMode) {
        this.promptMode = promptMode
        quickUnlockOperationAccounts.keys.toList().forEach(quickUnlockManager::cancelUnlock)
        quickUnlockOperationAccounts.clear()
        clearInMemoryKek()
        state.value = VaultState.Locked
    }

    private fun handleUnlockResult(
        result: VaultUnlockResult,
        provenance: VaultUnlockProvenance,
    ): VaultUnlockError? = when (result) {
        is VaultUnlockResult.Unlocked -> {
            replaceInMemoryKek(result.keyring.kek, provenance)
            state.value = VaultState.Unlocked
            null
        }

        is VaultUnlockResult.Error -> {
            state.value = when (result.reason) {
                VaultUnlockError.KeyMaterialUnavailable -> when (readLocalKeyMaterial()) {
                    VaultKeyMaterialLocalReadResult.Absent -> VaultState.Locked
                    VaultKeyMaterialLocalReadResult.Corrupted ->
                        VaultState.CorruptedLocalKeyMaterial
                    is VaultKeyMaterialLocalReadResult.Present -> VaultState.Locked
                }

                VaultUnlockError.InvalidCachedKeyMaterial ->
                    VaultState.CorruptedLocalKeyMaterial

                VaultUnlockError.InvalidCredential -> VaultState.Locked
            }
            result.reason
        }
    }

    private fun replaceInMemoryKek(
        newKek: ByteArray,
        provenance: VaultUnlockProvenance,
    ) {
        clearInMemoryKek()
        vaultInMemoryKekStore.replace(newKek, provenance)
    }

    private fun clearInMemoryKek() {
        vaultInMemoryKekStore.clear()
    }

    private fun initialVaultState(): VaultState = VaultState.InitialLoading

    private fun resolveStateForRemoteFailure(
        failure: NetworkFailure,
        localReadResult: VaultKeyMaterialLocalReadResult,
    ): VaultState = when {
        localReadResult is VaultKeyMaterialLocalReadResult.Corrupted ->
            VaultState.CorruptedLocalKeyMaterial

        failure.decision == RetryDecision.Retryable ->
            VaultState.RetryableRemoteFailure(
                failure = failure,
                hasValidLocalKeyMaterial = localReadResult is VaultKeyMaterialLocalReadResult.Present,
            )

        else -> VaultState.TerminalRemoteFailure(failure)
    }

    private fun readLocalKeyMaterial(): VaultKeyMaterialLocalReadResult = runCatching {
        vaultKeyMaterialLocalRepository.read()
    }.getOrDefault(VaultKeyMaterialLocalReadResult.Corrupted)

    private fun activeAccountId(): UUID? = when (val result = readLocalKeyMaterial()) {
        is VaultKeyMaterialLocalReadResult.Present -> result.value.accountId
        VaultKeyMaterialLocalReadResult.Absent,
        VaultKeyMaterialLocalReadResult.Corrupted,
            -> null
    }

    private fun hasValidAccountSession(accountId: UUID): Boolean =
        accountSessionValidator.orElse(null)?.isValid(accountId) == true
}
