package com.miguelrodriguez19.safecube.core.auth.data.mapper

import com.miguelrodriguez19.safecube.core.auth.domain.model.AuthError
import com.miguelrodriguez19.safecube.core.auth.domain.model.AuthOperation
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class AuthErrorMapperTest {

    private val target = AuthErrorMapper()

    @Test
    fun `map when status code is 400 and body has validation fields then returns validation failed`() {
        val errorBody = """
            {
              "error": "Validation failed",
              "fields": {
                "email": "invalid",
                "password": "too_short"
              }
            }
        """.trimIndent()

        val result = target.map(
            statusCode = 400,
            errorBody = errorBody,
            operation = AuthOperation.LOGIN,
        )

        assertTrue(result is AuthError.ValidationFailed)
        val validation = result as AuthError.ValidationFailed
        assertEquals(setOf("email", "password"), validation.fields)
    }

    @Test
    fun `map when status code is 400 and body is invalid then returns validation failed without details`() {
        val result = target.map(
            statusCode = 400,
            errorBody = "not-json",
            operation = AuthOperation.LOGIN,
        )

        assertEquals(AuthError.ValidationFailed(fields = null, message = null), result)
    }

    @Test
    fun `map when status code is 400 and body is blank then returns validation failed without details`() {
        val result = target.map(
            statusCode = 400,
            errorBody = " ",
            operation = AuthOperation.LOGIN,
        )

        assertEquals(AuthError.ValidationFailed(fields = null, message = null), result)
    }

    @Test
    fun `map when status code is 400 and fields are not strings then sanitizes parsed values`() {
        val result = target.map(
            statusCode = 400,
            errorBody = """{"error":"   ","fields":{"email":12,"password":"   "}}""",
            operation = AuthOperation.LOGIN,
        )

        assertEquals(
            AuthError.ValidationFailed(
                fields = mapOf(
                    "email" to "12",
                    "password" to "   ",
                ),
                message = null,
            ),
            result,
        )
    }

    @Test
    fun `map when status code is 400 and fields object is empty then returns validation failed without fields`() {
        val result = target.map(
            statusCode = 400,
            errorBody = """{"error":"Validation failed","fields":{}}""",
            operation = AuthOperation.LOGIN,
        )

        assertEquals(
            AuthError.ValidationFailed(
                fields = null,
                message = "Validation failed",
            ),
            result,
        )
    }

    @Test
    fun `map when status code is 401 then returns invalid credentials`() {
        val result = target.map(
            statusCode = 401,
            errorBody = null,
            operation = AuthOperation.LOGIN,
        )

        assertEquals(AuthError.InvalidCredentials, result)
    }

    @Test
    fun `map when status code is 403 then returns forbidden`() {
        val result = target.map(
            statusCode = 403,
            errorBody = null,
            operation = AuthOperation.LOGIN,
        )

        assertEquals(AuthError.Forbidden, result)
    }

    @Test
    fun `map when status code is 409 during signup then returns account already exists`() {
        val result = target.map(
            statusCode = 409,
            errorBody = """{"error":"Account already exists"}""",
            operation = AuthOperation.SIGNUP,
        )

        assertEquals(AuthError.AccountAlreadyExists, result)
    }

    @Test
    fun `map when status code is 409 outside signup then returns conflict`() {
        val result = target.map(
            statusCode = 409,
            errorBody = """{"error":"Refresh token conflict"}""",
            operation = AuthOperation.REFRESH,
        )

        assertEquals(AuthError.Conflict(message = "Refresh token conflict"), result)
    }

    @Test
    fun `map when status code is 409 and body is missing then returns conflict without message`() {
        val result = target.map(
            statusCode = 409,
            errorBody = null,
            operation = AuthOperation.REFRESH,
        )

        assertEquals(AuthError.Conflict(message = null), result)
    }

    @Test
    fun `map when status code is 409 and body message is blank then returns conflict without message`() {
        val result = target.map(
            statusCode = 409,
            errorBody = """{"error":" "}""",
            operation = AuthOperation.REFRESH,
        )

        assertEquals(AuthError.Conflict(message = null), result)
    }

    @Test
    fun `map when status code is unexpected and body has message then returns unknown with code and message`() {
        val result = target.map(
            statusCode = 500,
            errorBody = """{"error":"Server exploded"}""",
            operation = AuthOperation.LOGOUT,
        )

        assertEquals(
            AuthError.Unknown(
                code = 500,
                message = "Server exploded",
            ),
            result,
        )
    }

    @Test
    fun `map when status code is unexpected and body is not an object then returns unknown without message`() {
        val result = target.map(
            statusCode = 500,
            errorBody = """["boom"]""",
            operation = AuthOperation.LOGOUT,
        )

        assertEquals(
            AuthError.Unknown(
                code = 500,
                message = null,
            ),
            result,
        )
    }

    @Test
    fun `map when status code is unexpected and body is missing then returns unknown without message`() {
        val result = target.map(
            statusCode = 500,
            errorBody = null,
            operation = AuthOperation.LOGOUT,
        )

        assertEquals(
            AuthError.Unknown(
                code = 500,
                message = null,
            ),
            result,
        )
    }
}
