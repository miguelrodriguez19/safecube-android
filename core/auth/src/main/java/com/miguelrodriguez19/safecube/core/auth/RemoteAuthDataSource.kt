package com.miguelrodriguez19.safecube.core.auth

import com.miguelrodriguez19.safecube.core.network.generated.api.AuthControllerApi
import com.miguelrodriguez19.safecube.core.network.generated.model.AuthTokensResponse
import com.miguelrodriguez19.safecube.core.network.generated.model.AuthenticateAccountRequest
import com.miguelrodriguez19.safecube.core.network.generated.model.RefreshTokenRequest
import com.miguelrodriguez19.safecube.core.network.generated.model.RegisterAccountRequest
import com.miguelrodriguez19.safecube.core.network.generated.model.RegisterAccountResult
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.CancellationException
import retrofit2.Response

@Singleton
class RemoteAuthDataSource @Inject constructor(
    private val authControllerApi: AuthControllerApi,
) {
    suspend fun register(
        request: RegisterAccountRequest,
    ): NetworkResult<RegisterAccountResult> = execute { authControllerApi.register(request) }

    suspend fun login(
        request: AuthenticateAccountRequest,
    ): NetworkResult<AuthTokensResponse> = execute { authControllerApi.login(request) }

    suspend fun refresh(
        request: RefreshTokenRequest,
    ): NetworkResult<AuthTokensResponse> = execute { authControllerApi.refresh(request) }

    suspend fun logout(): NetworkResult<Unit> = execute { authControllerApi.logout() }

    private suspend fun <T> execute(
        call: suspend () -> Response<T>,
    ): NetworkResult<T> = try {
        val response = call()
        if (response.isSuccessful) {
            NetworkResult.Success(
                httpCode = response.code(),
                body = response.body(),
            )
        } else {
            NetworkResult.HttpError(
                httpCode = response.code(),
                body = response.body(),
                errorBody = runCatching { response.errorBody()?.string() }.getOrNull(),
            )
        }
    } catch (cancellationException: CancellationException) {
        throw cancellationException
    } catch (throwable: Throwable) {
        NetworkResult.Failure(throwable)
    }
}
