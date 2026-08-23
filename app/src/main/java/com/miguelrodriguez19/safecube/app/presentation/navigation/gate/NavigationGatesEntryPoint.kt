package com.miguelrodriguez19.safecube.app.presentation.navigation.gate

import com.miguelrodriguez19.safecube.core.auth.domain.repository.AuthRepository
import com.miguelrodriguez19.safecube.core.auth.domain.session.AccountSessionLifecycle
import com.miguelrodriguez19.safecube.core.auth.domain.session.SessionManager
import com.miguelrodriguez19.safecube.core.vault.domain.session.VaultSessionManager
import com.miguelrodriguez19.safecube.app.session.autolock.VaultAutoLockController
import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@EntryPoint
@InstallIn(SingletonComponent::class)
interface NavigationGatesEntryPoint {
    fun authRepository(): AuthRepository
    fun sessionManager(): SessionManager
    fun accountSessionLifecycle(): AccountSessionLifecycle
    fun vaultSessionManager(): VaultSessionManager
    fun vaultAutoLockController(): VaultAutoLockController
}
