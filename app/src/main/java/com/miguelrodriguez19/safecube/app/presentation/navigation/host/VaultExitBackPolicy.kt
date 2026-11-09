package com.miguelrodriguez19.safecube.app.presentation.navigation.host

/**
 * A prompt is ephemeral and never restored. Toast visibility is authoritative; the deadline
 * bounds the pending period if Android suppresses the toast and delivers no callbacks.
 */
internal class VaultExitBackPolicy(
    private val clock: () -> Long,
    private val timeoutMillis: () -> Long,
    private val showPrompt: (onShown: () -> Unit, onHidden: () -> Unit) -> Unit,
    private val cancelPrompt: () -> Unit,
    private val onExit: () -> Unit,
) {
    private var pendingDeadline: Long? = null
    private var visible = false
    private var generation = 0L

    fun onBack() {
        val now = clock()
        val deadline = pendingDeadline
        if (deadline != null && (visible || now < deadline)) {
            reset()
            onExit()
            return
        }

        reset()
        val promptGeneration = generation
        pendingDeadline = now + timeoutMillis()
        showPrompt(
            {
                if (generation == promptGeneration && pendingDeadline != null) visible = true
            },
            {
                if (generation == promptGeneration) reset()
            },
        )
    }

    fun reset() {
        generation++
        pendingDeadline = null
        visible = false
        cancelPrompt()
    }
}
