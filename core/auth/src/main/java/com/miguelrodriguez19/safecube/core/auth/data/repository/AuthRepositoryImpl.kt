package com.miguelrodriguez19.safecube.core.auth.data.repository

import com.miguelrodriguez19.safecube.core.auth.data.mapper.AuthErrorMapper
import com.miguelrodriguez19.safecube.core.auth.data.remote.NetworkResult
import com.miguelrodriguez19.safecube.core.auth.data.remote.RemoteAuthDataSource
import com.miguelrodriguez19.safecube.core.auth.domain.model.AuthError
import com.miguelrodriguez19.safecube.core.auth.domain.model.AuthOperation
import com.miguelrodriguez19.safecube.core.auth.domain.model.AuthResult
import com.miguelrodriguez19.safecube.core.auth.domain.model.AuthTokens
import com.miguelrodriguez19.safecube.core.auth.domain.model.RegisteredAccount
import com.miguelrodriguez19.safecube.core.auth.domain.repository.AuthRepository
import com.miguelrodriguez19.safecube.core.network.generated.model.AuthenticateAccountRequest
import com.miguelrodriguez19.safecube.core.network.generated.model.AuthTokensResponse
import com.miguelrodriguez19.safecube.core.network.generated.model.RefreshTokenRequest
import com.miguelrodriguez19.safecube.core.network.generated.model.RegisterAccountRequest
import com.miguelrodriguez19.safecube.core.network.generated.model.RegisterAccountResult
import com.miguelrodriguez19.safecube.core.network.domain.model.NetworkFailureClassifier
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthRepositoryImpl @Inject constructor(
    private val remoteAuthDataSource: RemoteAuthDataSource,
    private val authErrorMapper: AuthErrorMapper,
) : AuthRepository {

    override suspend fun register(
        email: String,
        password: String,
    ): AuthResult<RegisteredAccount> {
        val result = remoteAuthDataSource.register(
            request = RegisterAccountRequest(
                email = email,
                password = password,
            ),
        )
        return when (result) {
            is NetworkResult.Success -> AuthResult.Success(
                data = result.body.toRegisteredAccount(),
            )

            is NetworkResult.HttpError -> result.toAuthError(
                operation = AuthOperation.SIGNUP,
            )

            is NetworkResult.Failure -> result.toUnknownAuthError()
        }
    }

    override suspend fun login(
        email: String,
        password: String,
    ): AuthResult<AuthTokens> {
        val result = remoteAuthDataSource.login(
            request = AuthenticateAccountRequest(
                email = email,
                password = password,
            ),
        )
        return result.toAuthTokensResult(
            operation = AuthOperation.LOGIN,
        )
    }

    override suspend fun refresh(
        refreshToken: String,
    ): AuthResult<AuthTokens> {
        val result = remoteAuthDataSource.refresh(
            request = RefreshTokenRequest(refreshToken = refreshToken),
        )
        return result.toAuthTokensResult(
            operation = AuthOperation.REFRESH,
        )
    }

    override suspend fun logout(): AuthResult<Unit> {
        val result = remoteAuthDataSource.logout()
        return when (result) {
            is NetworkResult.Success -> AuthResult.Success(Unit)
            is NetworkResult.HttpError -> result.toAuthError(
                operation = AuthOperation.LOGOUT,
            )

            is NetworkResult.Failure -> result.toUnknownAuthError()
        }
    }

    private fun NetworkResult<AuthTokensResponse>.toAuthTokensResult(
        operation: AuthOperation,
    ): AuthResult<AuthTokens> = when (this) {
        is NetworkResult.Success -> {
            val tokens = body?.toAuthTokens()
                ?: return AuthResult.Error(
                    error = AuthError.Unknown(
                        code = httpCode,
                        failure = NetworkFailureClassifier.malformedResponse(httpCode),
                    ),
                )
            AuthResult.Success(tokens)
        }

        is NetworkResult.HttpError -> toAuthError(operation = operation)
        is NetworkResult.Failure -> toUnknownAuthError()
    }

    private fun RegisterAccountResult?.toRegisteredAccount(): RegisteredAccount = RegisteredAccount(
        accountId = this?.accountId,
        createdAt = this?.createdAt,
    )

    private fun AuthTokensResponse.toAuthTokens(): AuthTokens? {
        val safeAccessToken = accessToken
            .takeIf { it.isNotBlank() }
            ?: return null
        val safeRefreshToken = refreshToken
            .takeIf { it.isNotBlank() }
            ?: return null
        return AuthTokens(
            accessToken = safeAccessToken,
            refreshToken = safeRefreshToken,
            issuedAt = issuedAt,
        )
    }

    private fun NetworkResult.HttpError<*>.toAuthError(
        operation: AuthOperation,
    ): AuthResult.Error = AuthResult.Error(
        error = authErrorMapper.map(
            failure = failure,
            operation = operation,
        ),
    )

    private fun NetworkResult.Failure.toUnknownAuthError(): AuthResult.Error = AuthResult.Error(
        error = AuthError.Unknown(
            code = failure.statusCode,
            failure = failure,
        ),
    )
}
