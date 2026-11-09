package com.miguelrodriguez19.safecube.app.presentation.navigation.host

/**
 * A prompt is ephemeral and never restored. Only the current toast's visibility callbacks
 * enable exit confirmation; a pending or suppressed toast cannot authorize exit.
 */
internal class VaultExitBackPolicy(
    private val showPrompt: (onShown: () -> Unit, onHidden: () -> Unit) -> Unit,
    private val cancelPrompt: () -> Unit,
    private val onExit: () -> Unit,
) {
    private var visible = false
    private var generation = 0L

    fun onBack() {
        if (visible) {
            reset()
            onExit()
            return
        }

        reset()
        val promptGeneration = generation
        showPrompt(
            {
                if (generation == promptGeneration) visible = true
            },
            {
                if (generation == promptGeneration) reset()
            },
        )
    }

    fun reset() {
        generation++
        visible = false
        cancelPrompt()
    }
}
