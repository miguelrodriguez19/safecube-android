package com.miguelrodriguez19.safecube.core.auth.data.session

import com.miguelrodriguez19.safecube.core.auth.domain.model.AuthError
import com.miguelrodriguez19.safecube.core.auth.domain.model.AuthResult
import com.miguelrodriguez19.safecube.core.auth.domain.repository.AuthRepository
import com.miguelrodriguez19.safecube.core.auth.domain.repository.TokenStorage
import com.miguelrodriguez19.safecube.core.auth.domain.session.SessionManager
import com.miguelrodriguez19.safecube.core.network.TokenRefreshHandler
import javax.inject.Inject
import javax.inject.Provider
import javax.inject.Singleton

@Singleton
class AuthTokenRefreshHandler @Inject constructor(
    private val authRepositoryProvider: Provider<AuthRepository>,
    private val tokenStorage: TokenStorage,
    private val sessionManager: SessionManager,
) : TokenRefreshHandler {

    override suspend fun refreshAccessToken(
        failedAccessToken: String?,
    ): String? {
        val latestAccessToken = tokenStorage.getAccessToken()
            ?.takeIf { it.isNotBlank() }
            ?.takeIf { currentAccessToken ->
                !failedAccessToken.isNullOrBlank() && currentAccessToken != failedAccessToken
            }
        if (!latestAccessToken.isNullOrBlank()) return latestAccessToken

        val refreshToken = tokenStorage.getRefreshToken()
            ?.takeIf { it.isNotBlank() }
            ?: run {
                sessionManager.forceLogout()
                return null
            }

        return when (val refreshResult = authRepositoryProvider.get().refresh(refreshToken)) {
            is AuthResult.Success -> {
                sessionManager.onLoginSuccess(refreshResult.data)
                refreshResult.data.accessToken
            }

            is AuthResult.Error -> {
                if (refreshResult.error.shouldForceLogout()) {
                    sessionManager.forceLogout()
                }
                null
            }
        }
    }

    private fun AuthError.shouldForceLogout(): Boolean = when (this) {
        AuthError.InvalidCredentials,
        AuthError.Forbidden,
        AuthError.AccountNotActive,
        AuthError.AccountAlreadyExists,
        is AuthError.ValidationFailed,
        is AuthError.Conflict -> true

        is AuthError.Unknown -> code in AUTH_FAILURE_CODES
    }

    private companion object {
        val AUTH_FAILURE_CODES = setOf(400, 401, 403, 409)
    }
}
