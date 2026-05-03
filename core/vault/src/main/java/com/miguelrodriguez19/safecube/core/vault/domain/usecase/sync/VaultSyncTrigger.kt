package com.miguelrodriguez19.safecube.core.vault.domain.usecase.sync

import com.miguelrodriguez19.safecube.core.vault.domain.session.VaultSessionManager
import com.miguelrodriguez19.safecube.core.vault.domain.usecase.sync.push.PushLocalVaultChangesUseCase
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch

@Singleton
class VaultSyncTrigger @Inject constructor(
    private val vaultSessionManager: VaultSessionManager,
    private val pushLocalVaultChangesUseCase: PushLocalVaultChangesUseCase,
    private val vaultSyncExecutionLock: VaultSyncExecutionLock,
) {
    private val triggerScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    fun onLocalMutationStored() {
        if (!vaultSessionManager.isUnlocked()) {
            return
        }
        if (!vaultSyncExecutionLock.tryLock()) {
            return
        }

        triggerScope.launch {
            try {
                pushLocalVaultChangesUseCase()
            } catch (cancellationException: CancellationException) {
                throw cancellationException
            } catch (_: Throwable) {
                Unit
            } finally {
                vaultSyncExecutionLock.unlock()
            }
        }
    }
}
