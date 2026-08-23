package com.miguelrodriguez19.safecube.core.vault.domain.repository

import com.miguelrodriguez19.safecube.core.vault.domain.model.AutoLockTimeout
import kotlinx.coroutines.flow.StateFlow

interface AutoLockTimeoutRepository {
    val timeout: StateFlow<AutoLockTimeout>

    fun setTimeout(timeout: AutoLockTimeout)
}
