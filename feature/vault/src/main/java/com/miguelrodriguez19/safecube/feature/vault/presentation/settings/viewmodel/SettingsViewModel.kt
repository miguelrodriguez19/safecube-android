package com.miguelrodriguez19.safecube.feature.vault.presentation.settings.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.miguelrodriguez19.safecube.core.vault.domain.model.AutoLockTimeout
import com.miguelrodriguez19.safecube.core.vault.domain.repository.AutoLockTimeoutRepository
import com.miguelrodriguez19.safecube.core.vault.domain.usecase.secureitem.ObserveVaultDraftSummariesUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

@HiltViewModel
class SettingsViewModel @Inject constructor(
    observeVaultDraftSummariesUseCase: ObserveVaultDraftSummariesUseCase,
    private val autoLockTimeoutRepository: AutoLockTimeoutRepository,
) : ViewModel() {
    val hasActiveDrafts: StateFlow<Boolean?> = observeVaultDraftSummariesUseCase()
        .map { drafts -> drafts.isNotEmpty() }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = null,
        )

    val autoLockTimeout: StateFlow<AutoLockTimeout> = autoLockTimeoutRepository.timeout

    fun setAutoLockTimeout(timeout: AutoLockTimeout) {
        autoLockTimeoutRepository.setTimeout(timeout)
    }
}
