package com.miguelrodriguez19.safecube.core.auth.data.mapper

import com.miguelrodriguez19.safecube.core.auth.domain.model.AuthError
import com.miguelrodriguez19.safecube.core.auth.domain.model.AuthOperation
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class AuthErrorMapperTest {
    private val mapper = AuthErrorMapper()

    @Test
    fun `maps 400 into validation failed with parsed fields`() {
        val errorBody = """
            {
              "error": "Validation failed",
              "fields": {
                "email": "invalid",
                "password": "too_short"
              }
            }
        """.trimIndent()

        val error = mapper.map(
            statusCode = 400,
            errorBody = errorBody,
            operation = AuthOperation.LOGIN,
        )

        assertTrue(error is AuthError.ValidationFailed)
        val validation = error as AuthError.ValidationFailed
        assertEquals("Validation failed", validation.message)
        assertEquals("invalid", validation.fields?.get("email"))
        assertEquals("too_short", validation.fields?.get("password"))
    }

    @Test
    fun `maps 400 with invalid body safely`() {
        val error = mapper.map(
            statusCode = 400,
            errorBody = "not-json",
            operation = AuthOperation.LOGIN,
        )

        assertEquals(AuthError.ValidationFailed(fields = null, message = null), error)
    }

    @Test
    fun `maps 400 with blank message and non string fields safely`() {
        val error = mapper.map(
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
            error,
        )
    }

    @Test
    fun `maps 401 into invalid credentials`() {
        val error = mapper.map(
            statusCode = 401,
            errorBody = null,
            operation = AuthOperation.LOGIN,
        )

        assertEquals(AuthError.InvalidCredentials, error)
    }

    @Test
    fun `maps 403 into forbidden`() {
        val error = mapper.map(
            statusCode = 403,
            errorBody = null,
            operation = AuthOperation.LOGIN,
        )

        assertEquals(AuthError.Forbidden, error)
    }

    @Test
    fun `maps 409 into account already exists for signup`() {
        val error = mapper.map(
            statusCode = 409,
            errorBody = """{"error":"Account already exists"}""",
            operation = AuthOperation.SIGNUP,
        )

        assertEquals(AuthError.AccountAlreadyExists, error)
    }

    @Test
    fun `maps 409 into generic conflict for non signup operations`() {
        val error = mapper.map(
            statusCode = 409,
            errorBody = """{"error":"Refresh token conflict"}""",
            operation = AuthOperation.REFRESH,
        )

        assertEquals(AuthError.Conflict(message = "Refresh token conflict"), error)
    }

    @Test
    fun `maps 409 into conflict with null message when body missing`() {
        val error = mapper.map(
            statusCode = 409,
            errorBody = null,
            operation = AuthOperation.REFRESH,
        )

        assertEquals(AuthError.Conflict(message = null), error)
    }

    @Test
    fun `maps unexpected status into unknown`() {
        val error = mapper.map(
            statusCode = 500,
            errorBody = """{"error":"Server exploded"}""",
            operation = AuthOperation.LOGOUT,
        )

        assertEquals(
            AuthError.Unknown(
                code = 500,
                message = "Server exploded",
            ),
            error,
        )
    }

    @Test
    fun `maps unexpected status with non object json into unknown without message`() {
        val error = mapper.map(
            statusCode = 500,
            errorBody = """["boom"]""",
            operation = AuthOperation.LOGOUT,
        )

        assertEquals(
            AuthError.Unknown(
                code = 500,
                message = null,
            ),
            error,
        )
    }
}
