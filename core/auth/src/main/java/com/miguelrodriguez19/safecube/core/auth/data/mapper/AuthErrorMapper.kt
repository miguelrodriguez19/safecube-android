package com.miguelrodriguez19.safecube.core.auth.data.mapper

import com.miguelrodriguez19.safecube.core.auth.domain.model.AuthError
import com.miguelrodriguez19.safecube.core.auth.domain.model.AuthOperation
import com.miguelrodriguez19.safecube.core.network.domain.model.NetworkFailure
import com.miguelrodriguez19.safecube.core.network.domain.model.NetworkFailureClassifier
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonObject

@Singleton
class AuthErrorMapper @Inject constructor() {
    private val json = Json {
        ignoreUnknownKeys = true
    }

    fun map(
        statusCode: Int,
        errorBody: String?,
        operation: AuthOperation,
    ): AuthError = map(
        failure = NetworkFailureClassifier.fromHttpStatus(statusCode),
        validationFields = parseValidationFields(errorBody),
        operation = operation,
    )

    fun map(
        failure: NetworkFailure,
        operation: AuthOperation,
    ): AuthError = map(
        failure = failure,
        operation = operation,
        validationFields = emptySet(),
    )

    private fun map(
        failure: NetworkFailure,
        operation: AuthOperation,
        validationFields: Set<String>,
    ): AuthError = when (failure.statusCode) {
        400 -> AuthError.ValidationFailed(
            fields = validationFields,
        )

        401 -> AuthError.InvalidCredentials
        403 -> AuthError.Forbidden
        409 -> {
            if (operation == AuthOperation.SIGNUP) {
                AuthError.AccountAlreadyExists
            } else {
                AuthError.Conflict(failure = failure)
            }
        }

        else -> AuthError.Unknown(code = failure.statusCode, failure = failure)
    }

    private fun parseValidationFields(errorBody: String?): Set<String> {
        if (errorBody.isNullOrBlank()) return emptySet()

        val root = runCatching {
            json.parseToJsonElement(errorBody)
        }.getOrNull() as? JsonObject ?: return emptySet()

        return (root["fields"] as? JsonObject)?.keys.orEmpty()
    }
}
