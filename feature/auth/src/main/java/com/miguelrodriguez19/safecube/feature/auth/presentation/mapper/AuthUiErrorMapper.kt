package com.miguelrodriguez19.safecube.feature.auth.presentation.mapper

import androidx.annotation.StringRes
import com.miguelrodriguez19.safecube.core.auth.domain.model.AuthError
import com.miguelrodriguez19.safecube.core.ui.R as UiR

internal data class AuthUiError(
    @param:StringRes val messageRes: Int? = null,
    val fieldErrors: Map<String, Int> = emptyMap(),
)


internal object AuthUiErrorMapper {
    const val EMAIL = "email"
    const val PASSWORD = "password"
    const val CONFIRM_PASSWORD = "confirm_password"

    fun map(error: AuthError): AuthUiError = when (error) {
        is AuthError.ValidationFailed -> {
            val fieldErrors = error.fields.orEmpty()
                .mapNotNull { (field, _) ->
                    validationFieldToRes(field)?.let { field to it }
                }
                .toMap()
            AuthUiError(
                messageRes = if (fieldErrors.isEmpty()) UiR.string.auth_error_validation_failed else null,
                fieldErrors = fieldErrors,
            )
        }

        AuthError.InvalidCredentials -> AuthUiError(
            messageRes = UiR.string.auth_error_invalid_credentials,
        )

        AuthError.Forbidden,
        AuthError.AccountNotActive,
            -> AuthUiError(
            messageRes = UiR.string.auth_error_forbidden,
        )

        AuthError.AccountAlreadyExists -> AuthUiError(
            messageRes = UiR.string.auth_error_account_exists,
        )

        is AuthError.Conflict -> AuthUiError(
            messageRes = UiR.string.auth_error_conflict,
        )

        is AuthError.Unknown -> AuthUiError(
            messageRes = UiR.string.generic_error,
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
