package com.miguelrodriguez19.safecube.core.auth.data.session

import com.miguelrodriguez19.safecube.core.auth.domain.model.AuthError
import com.miguelrodriguez19.safecube.core.auth.domain.model.AuthResult
import com.miguelrodriguez19.safecube.core.auth.domain.model.SessionTerminationReason
import com.miguelrodriguez19.safecube.core.auth.domain.repository.AuthRepository
import com.miguelrodriguez19.safecube.core.auth.domain.repository.TokenStorage
import com.miguelrodriguez19.safecube.core.auth.domain.session.AccountSessionLifecycle
import com.miguelrodriguez19.safecube.core.network.domain.model.NetworkFailureKind
import com.miguelrodriguez19.safecube.core.network.domain.port.TokenRefreshHandler
import javax.inject.Inject
import javax.inject.Provider
import javax.inject.Singleton

@Singleton
class AuthTokenRefreshHandler @Inject constructor(
    private val authRepositoryProvider: Provider<AuthRepository>,
    private val tokenStorage: TokenStorage,
    private val accountSessionLifecycle: AccountSessionLifecycle,
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
                accountSessionLifecycle.terminateSession(
                    reason = SessionTerminationReason.SessionExpired,
                )
                return null
            }

        return when (val refreshResult = authRepositoryProvider.get().refresh(refreshToken)) {
            is AuthResult.Success -> {
                accountSessionLifecycle.refreshSession(refreshResult.data)
                refreshResult.data.accessToken
            }

            is AuthResult.Error -> {
                if (refreshResult.error.shouldForceLogout()) {
                    accountSessionLifecycle.terminateSession(
                        reason = SessionTerminationReason.RefreshCredentialsRejected,
                    )
                }
                null
            }
        }
    }

    private fun AuthError.shouldForceLogout(): Boolean = when (this) {
        AuthError.InvalidCredentials,
        AuthError.Forbidden,
        AuthError.AccountNotActive,
        is AuthError.ValidationFailed,
            -> true

        is AuthError.Unknown -> failure.kind in DEFINITIVE_REFRESH_FAILURE_KINDS

        AuthError.AccountAlreadyExists,
        is AuthError.Conflict,
            -> false
    }

    private companion object {
        val DEFINITIVE_REFRESH_FAILURE_KINDS = setOf(
            NetworkFailureKind.Unauthorized,
            NetworkFailureKind.Forbidden,
            NetworkFailureKind.Validation,
            NetworkFailureKind.Protocol,
            NetworkFailureKind.MalformedResponse,
        )
    }
}
