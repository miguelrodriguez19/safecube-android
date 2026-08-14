package com.miguelrodriguez19.safecube.core.network.domain.model

import java.io.IOException
import java.net.SocketTimeoutException
import java.util.concurrent.TimeoutException
import kotlinx.coroutines.CancellationException
import kotlinx.serialization.SerializationException
import org.junit.Assert.assertEquals
import org.junit.Assert.assertThrows
import org.junit.Test

class NetworkFailureClassifierTest {
    @Test
    fun `http status matrix is deterministic`() {
        assertFailure(400, NetworkFailureKind.Validation, RetryDecision.Terminal)
        assertFailure(401, NetworkFailureKind.Unauthorized, RetryDecision.Terminal)
        assertFailure(403, NetworkFailureKind.Forbidden, RetryDecision.Terminal)
        assertFailure(404, NetworkFailureKind.Unknown, RetryDecision.Terminal)
        assertFailure(408, NetworkFailureKind.Timeout, RetryDecision.Retryable)
        assertFailure(409, NetworkFailureKind.Conflict, RetryDecision.Terminal)
        assertFailure(412, NetworkFailureKind.Conflict, RetryDecision.Terminal)
        assertFailure(428, NetworkFailureKind.Protocol, RetryDecision.Terminal)
        assertFailure(429, NetworkFailureKind.RateLimited, RetryDecision.Retryable)
        assertFailure(500, NetworkFailureKind.ServerUnavailable, RetryDecision.Retryable)
        assertFailure(503, NetworkFailureKind.ServerUnavailable, RetryDecision.Retryable)
        assertFailure(599, NetworkFailureKind.ServerUnavailable, RetryDecision.Retryable)
    }

    @Test
    fun `transport exceptions are retryable`() {
        assertThrowable(IOException(), NetworkFailureKind.Connectivity)
        assertThrowable(SocketTimeoutException(), NetworkFailureKind.Timeout)
        assertThrowable(TimeoutException(), NetworkFailureKind.Timeout)
    }

    @Test
    fun `malformed responses are terminal`() {
        val failure = NetworkFailureClassifier.fromThrowable(SerializationException("invalid"))

        assertEquals(NetworkFailureKind.MalformedResponse, failure.kind)
        assertEquals(RetryDecision.Terminal, failure.decision)
    }

    @Test
    fun `unexpected exceptions are terminal and sanitized`() {
        val failure = NetworkFailureClassifier.fromThrowable(
            IllegalStateException("sensitive exception detail"),
        )

        assertEquals(NetworkFailureKind.Unknown, failure.kind)
        assertEquals(RetryDecision.Terminal, failure.decision)
        assertEquals(null, failure.statusCode)
    }

    @Test
    fun `cancellation is preserved`() {
        assertThrows(CancellationException::class.java) {
            NetworkFailureClassifier.fromThrowable(CancellationException())
        }
    }

    @Test
    fun `explicit malformed and unknown failures are terminal`() {
        assertEquals(
            NetworkFailure(
                kind = NetworkFailureKind.MalformedResponse,
                decision = RetryDecision.Terminal,
                statusCode = 200,
            ),
            NetworkFailureClassifier.malformedResponse(200),
        )
        assertEquals(
            NetworkFailure(
                kind = NetworkFailureKind.Unknown,
                decision = RetryDecision.Terminal,
                statusCode = 418,
            ),
            NetworkFailureClassifier.unknown(418),
        )
    }

    private fun assertFailure(
        statusCode: Int,
        expectedKind: NetworkFailureKind,
        expectedDecision: RetryDecision,
    ) {
        val failure = NetworkFailureClassifier.fromHttpStatus(statusCode)

        assertEquals(expectedKind, failure.kind)
        assertEquals(expectedDecision, failure.decision)
        assertEquals(statusCode, failure.statusCode)
    }

    private fun assertThrowable(
        throwable: Throwable,
        expectedKind: NetworkFailureKind,
    ) {
        val failure = NetworkFailureClassifier.fromThrowable(throwable)

        assertEquals(expectedKind, failure.kind)
        assertEquals(RetryDecision.Retryable, failure.decision)
        assertEquals(null, failure.statusCode)
    }
}
