package com.miguelrodriguez19.safecube.core.auth

import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.contentOrNull

@Singleton
class AuthErrorMapper @Inject constructor() {
    private val json = Json {
        ignoreUnknownKeys = true
    }

    fun map(
        statusCode: Int,
        errorBody: String?,
        operation: AuthOperation,
    ): AuthError {
        val parsed = parseErrorBody(errorBody)

        return when (statusCode) {
            400 -> AuthError.ValidationFailed(
                fields = parsed?.fields,
                message = parsed?.message,
            )

            401 -> AuthError.InvalidCredentials
            403 -> AuthError.Forbidden
            409 -> {
                if (operation == AuthOperation.SIGNUP) {
                    AuthError.AccountAlreadyExists
                } else {
                    AuthError.Conflict(message = parsed?.message)
                }
            }

            else -> AuthError.Unknown(
                code = statusCode,
                message = parsed?.message,
            )
        }
    }

    private fun parseErrorBody(errorBody: String?): ParsedErrorBody? {
        if (errorBody.isNullOrBlank()) return null

        val root = runCatching {
            json.parseToJsonElement(errorBody)
        }.getOrNull() as? JsonObject ?: return null

        val message = root["error"].extractString()
            ?.takeIf { it.isNotBlank() }
        val fields = (root["fields"] as? JsonObject)
            ?.mapNotNull { (key, value) ->
                value.extractString()?.let { key to it }
            }
            ?.toMap()
            ?.takeIf { it.isNotEmpty() }

        return ParsedErrorBody(
            message = message,
            fields = fields,
        )
    }

    private fun JsonElement?.extractString(): String? =
        (this as? JsonPrimitive)?.contentOrNull

    private data class ParsedErrorBody(
        val message: String?,
        val fields: Map<String, String>?,
    )
}
