package com.miguelrodriguez19.safecube.app.presentation.navigation.host

import org.junit.Assert.assertEquals
import org.junit.Test

class VaultExitBackPolicyTest {
    @Test
    fun `first back at clock zero prompts without exiting`() {
        val fixture = PolicyFixture()

        fixture.target.onBack()

        assertEquals(1, fixture.prompts.size)
        assertEquals(0, fixture.exits)
    }

    @Test
    fun `rapid second back before prompt is shown confirms exit`() {
        val fixture = PolicyFixture()

        fixture.target.onBack()
        fixture.clock.now = 1L
        fixture.target.onBack()

        assertEquals(1, fixture.exits)
    }

    @Test
    fun `back after fallback deadline without callbacks repeats prompt without exiting`() {
        val fixture = PolicyFixture()

        fixture.target.onBack()
        fixture.clock.now = BASE_TIMEOUT_MILLIS + 1L
        fixture.target.onBack()

        assertEquals(2, fixture.prompts.size)
        assertEquals(0, fixture.exits)
    }

    @Test
    fun `back at the exclusive fallback deadline without callbacks repeats prompt`() {
        val fixture = PolicyFixture()

        fixture.target.onBack()
        fixture.clock.now = BASE_TIMEOUT_MILLIS
        fixture.target.onBack()

        assertEquals(2, fixture.prompts.size)
        assertEquals(0, fixture.exits)
    }

    @Test
    fun `recommended timeout longer than base keeps pending confirmation open`() {
        val fixture = PolicyFixture(timeoutMillis = 5_000L)

        fixture.target.onBack()
        fixture.clock.now = BASE_TIMEOUT_MILLIS + 1L
        fixture.target.onBack()

        assertEquals(1, fixture.exits)
    }

    @Test
    fun `visible toast still confirms after the base two second deadline`() {
        val fixture = PolicyFixture()

        fixture.target.onBack()
        fixture.prompts.single().onShown()
        fixture.clock.now = BASE_TIMEOUT_MILLIS + 1L
        fixture.target.onBack()

        assertEquals(1, fixture.exits)
    }

    @Test
    fun `hidden toast cannot confirm exit`() {
        val fixture = PolicyFixture()

        fixture.target.onBack()
        fixture.prompts.single().onShown()
        fixture.prompts.single().onHidden()
        fixture.target.onBack()

        assertEquals(2, fixture.prompts.size)
        assertEquals(0, fixture.exits)
    }

    @Test
    fun `reset on leaving invalidates the pending prompt`() {
        val fixture = PolicyFixture()

        fixture.target.onBack()
        val stalePrompt = fixture.prompts.single()
        val cancellationsBeforeReset = fixture.cancellations
        fixture.target.reset()
        assertEquals(cancellationsBeforeReset + 1, fixture.cancellations)
        stalePrompt.onShown()
        fixture.target.onBack()

        assertEquals(2, fixture.prompts.size)
        assertEquals(0, fixture.exits)
    }

    @Test
    fun `stale prompt shown callback cannot make a new prompt confirm`() {
        val fixture = PolicyFixture()

        fixture.target.onBack()
        val stalePrompt = fixture.prompts.single()
        fixture.target.reset()
        fixture.target.onBack()
        fixture.clock.now = BASE_TIMEOUT_MILLIS + 1L
        stalePrompt.onShown()
        fixture.target.onBack()

        assertEquals(3, fixture.prompts.size)
        assertEquals(0, fixture.exits)
    }

    @Test
    fun `stale prompt hidden callback cannot cancel a new pending prompt`() {
        val fixture = PolicyFixture()

        fixture.target.onBack()
        val stalePrompt = fixture.prompts.single()
        fixture.target.reset()
        fixture.target.onBack()
        stalePrompt.onHidden()
        fixture.clock.now = 1L
        fixture.target.onBack()

        assertEquals(1, fixture.exits)
        assertEquals(2, fixture.prompts.size)
    }

    private class PolicyFixture(
        timeoutMillis: Long = BASE_TIMEOUT_MILLIS,
    ) {
        val clock = FakeClock()
        val prompts = mutableListOf<PromptCallbacks>()
        var cancellations = 0
        var exits = 0

        val target = VaultExitBackPolicy(
            clock = { clock.now },
            timeoutMillis = { timeoutMillis },
            showPrompt = { onShown, onHidden ->
                prompts += PromptCallbacks(onShown, onHidden)
            },
            cancelPrompt = { cancellations++ },
            onExit = { exits++ },
        )
    }

    private class FakeClock(var now: Long = 0L)

    private data class PromptCallbacks(
        val onShown: () -> Unit,
        val onHidden: () -> Unit,
    )

    private companion object {
        const val BASE_TIMEOUT_MILLIS = 2_000L
    }
}
