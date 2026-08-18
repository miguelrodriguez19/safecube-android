package com.miguelrodriguez19.safecube.feature.auth.presentation.mapper

import androidx.annotation.StringRes
import com.miguelrodriguez19.safecube.core.auth.domain.model.AuthError
import com.miguelrodriguez19.safecube.core.network.domain.model.NetworkFailure
import com.miguelrodriguez19.safecube.core.network.domain.model.RetryDecision
import com.miguelrodriguez19.safecube.core.ui.R as UiR
import com.miguelrodriguez19.safecube.feature.auth.presentation.state.AuthUiOperationState

internal data class AuthUiError(
    @param:StringRes val messageRes: Int? = null,
    val fieldErrors: Map<String, Int> = emptyMap(),
    val operationState: AuthUiOperationState,
)


internal object AuthUiErrorMapper {
    const val EMAIL = "email"
    const val PASSWORD = "password"
    const val CONFIRM_PASSWORD = "confirm_password"

    fun map(error: AuthError): AuthUiError = when (error) {
        is AuthError.ValidationFailed -> {
            val fieldErrors = error.fields
                .mapNotNull { field ->
                    validationFieldToRes(field)?.let { field to it }
                }
                .toMap()
            AuthUiError(
                messageRes = if (fieldErrors.isEmpty()) UiR.string.auth_error_validation_failed else null,
                fieldErrors = fieldErrors,
                operationState = AuthUiOperationState.ValidationError,
            )
        }

        AuthError.InvalidCredentials -> AuthUiError(
            messageRes = UiR.string.auth_error_invalid_credentials,
            operationState = AuthUiOperationState.InvalidCredentials,
        )

        AuthError.Forbidden,
        AuthError.AccountNotActive,
            -> AuthUiError(
            messageRes = UiR.string.auth_error_forbidden,
            operationState = AuthUiOperationState.Forbidden,
        )

        AuthError.AccountAlreadyExists -> AuthUiError(
            messageRes = UiR.string.auth_error_account_exists,
            operationState = AuthUiOperationState.AccountAlreadyExists,
        )

        is AuthError.Conflict -> AuthUiError(
            messageRes = UiR.string.auth_error_conflict,
            operationState = AuthUiOperationState.TerminalError,
        )

        is AuthError.Unknown -> mapUnknown(error.failure)
    }

    private fun mapUnknown(failure: NetworkFailure): AuthUiError = when {
        failure.decision != RetryDecision.Retryable -> AuthUiError(
            messageRes = UiR.string.generic_error,
            operationState = AuthUiOperationState.TerminalError,
        )

        failure.statusCode == 429 || failure.statusCode in 500..599 -> AuthUiError(
            messageRes = UiR.string.auth_error_service_unavailable_retryable,
            operationState = AuthUiOperationState.ServiceUnavailable,
        )

        else -> AuthUiError(
            messageRes = UiR.string.auth_error_network_retryable,
            operationState = AuthUiOperationState.OfflineOrTimeout,
        )
    }

    @StringRes
    private fun validationFieldToRes(fieldName: String): Int? = when (fieldName.lowercase()) {
        EMAIL -> UiR.string.auth_error_email_invalid
        PASSWORD -> UiR.string.auth_error_password_invalid
        CONFIRM_PASSWORD -> UiR.string.auth_error_confirm_password_invalid
        else -> null
    }
}
