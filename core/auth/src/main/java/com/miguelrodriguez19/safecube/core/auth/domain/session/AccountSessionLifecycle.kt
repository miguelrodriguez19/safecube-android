package com.miguelrodriguez19.safecube.core.auth.domain.session

import com.miguelrodriguez19.safecube.core.auth.domain.model.AuthTokens

sealed interface AccountSessionResult {
    data object Success : AccountSessionResult

    data object LocalVaultCleanupFailed : AccountSessionResult
}

interface AccountSessionLifecycle {
    suspend fun activateFreshSession(tokens: AuthTokens): AccountSessionResult

    suspend fun refreshSession(tokens: AuthTokens)

    suspend fun terminateSession(): AccountSessionResult
}
