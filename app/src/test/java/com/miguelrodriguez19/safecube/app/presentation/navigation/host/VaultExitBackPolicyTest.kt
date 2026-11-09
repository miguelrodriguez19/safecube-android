package com.miguelrodriguez19.safecube.app.presentation.navigation.host

import org.junit.Assert.assertEquals
import org.junit.Test

class VaultExitBackPolicyTest {
    @Test
    fun `first back prompts without exiting`() {
        val fixture = PolicyFixture()

        fixture.target.onBack()

        assertEquals(1, fixture.prompts.size)
        assertEquals(0, fixture.exits)
    }

    @Test
    fun `second back without a shown callback repeats prompt without exiting`() {
        val fixture = PolicyFixture()

        fixture.target.onBack()
        fixture.target.onBack()

        assertEquals(2, fixture.prompts.size)
        assertEquals(0, fixture.exits)
    }

    @Test
    fun `back while toast is visible confirms exit and clears confirmation`() {
        val fixture = PolicyFixture()

        fixture.target.onBack()
        fixture.prompts.single().onShown()
        fixture.target.onBack()
        fixture.target.onBack()

        assertEquals(1, fixture.exits)
        assertEquals(2, fixture.prompts.size)
    }

    @Test
    fun `back after toast is hidden repeats prompt without exiting`() {
        val fixture = PolicyFixture()

        fixture.target.onBack()
        fixture.prompts.single().onShown()
        fixture.prompts.single().onHidden()
        fixture.target.onBack()

        assertEquals(2, fixture.prompts.size)
        assertEquals(0, fixture.exits)
    }

    @Test
    fun `reset invalidates pending prompt and its late shown callback`() {
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
    fun `reset invalidates visible confirmation`() {
        val fixture = PolicyFixture()

        fixture.target.onBack()
        fixture.prompts.single().onShown()
        fixture.target.reset()
        fixture.target.onBack()

        assertEquals(2, fixture.prompts.size)
        assertEquals(0, fixture.exits)
    }

    @Test
    fun `stale shown callback cannot authorize exit for a replacement prompt`() {
        val fixture = PolicyFixture()

        fixture.target.onBack()
        val stalePrompt = fixture.prompts.single()
        fixture.target.onBack()
        stalePrompt.onShown()
        fixture.target.onBack()

        assertEquals(3, fixture.prompts.size)
        assertEquals(0, fixture.exits)
    }

    @Test
    fun `stale hidden callback cannot cancel visible replacement confirmation`() {
        val fixture = PolicyFixture()

        fixture.target.onBack()
        val stalePrompt = fixture.prompts.single()
        fixture.target.onBack()
        fixture.prompts.last().onShown()
        stalePrompt.onHidden()
        fixture.target.onBack()

        assertEquals(1, fixture.exits)
        assertEquals(2, fixture.prompts.size)
    }

    private class PolicyFixture {
        val prompts = mutableListOf<PromptCallbacks>()
        var cancellations = 0
        var exits = 0

        val target = VaultExitBackPolicy(
            showPrompt = { onShown, onHidden ->
                prompts += PromptCallbacks(onShown, onHidden)
            },
            cancelPrompt = { cancellations++ },
            onExit = { exits++ },
        )
    }

    private data class PromptCallbacks(
        val onShown: () -> Unit,
        val onHidden: () -> Unit,
    )
}
