package com.miguelrodriguez19.safecube.app.testsupport

import com.miguelrodriguez19.safecube.core.auth.domain.session.AccountSessionLifecycle
import com.miguelrodriguez19.safecube.core.auth.domain.session.SessionManager
import com.miguelrodriguez19.safecube.core.crypto.domain.port.KdfEngine
import com.miguelrodriguez19.safecube.core.crypto.domain.port.KeyWrapping
import com.miguelrodriguez19.safecube.core.vault.data.quickunlock.QuickUnlockPromptCipherProvider
import com.miguelrodriguez19.safecube.core.vault.domain.quickunlock.QuickUnlockManager
import com.miguelrodriguez19.safecube.core.vault.domain.repository.AutoLockTimeoutRepository
import com.miguelrodriguez19.safecube.core.vault.domain.repository.PendingVaultInitializationRepository
import com.miguelrodriguez19.safecube.core.vault.domain.repository.VaultKeyMaterialLocalRepository
import com.miguelrodriguez19.safecube.core.vault.domain.session.VaultSessionManager
import com.miguelrodriguez19.safecube.app.session.autolock.VaultAutoLockController
import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

/** Debug-only Hilt bridge used by the instrumented local quick-unlock fixture. */
@EntryPoint
@InstallIn(SingletonComponent::class)
interface QuickUnlockInstrumentationEntryPoint {
    fun accountSessionLifecycle(): AccountSessionLifecycle

    fun sessionManager(): SessionManager

    fun kdfEngine(): KdfEngine

    fun keyWrapping(): KeyWrapping

    fun vaultKeyMaterialLocalRepository(): VaultKeyMaterialLocalRepository

    fun vaultSessionManager(): VaultSessionManager

    fun vaultAutoLockController(): VaultAutoLockController

    fun autoLockTimeoutRepository(): AutoLockTimeoutRepository

    fun pendingVaultInitializationRepository(): PendingVaultInitializationRepository

    fun quickUnlockManager(): QuickUnlockManager

    fun quickUnlockPromptCipherProvider(): QuickUnlockPromptCipherProvider
}
