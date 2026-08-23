package com.miguelrodriguez19.safecube.core.vault.data.local

import android.content.SharedPreferences
import com.miguelrodriguez19.safecube.core.vault.domain.model.AutoLockTimeout
import com.miguelrodriguez19.safecube.core.vault.domain.repository.AutoLockTimeoutRepository
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

internal const val AUTO_LOCK_PREFERENCES_NAME = "vault_auto_lock_preferences"

@Singleton
internal class AutoLockTimeoutRepositoryImpl @Inject constructor(
    @param:AutoLockPreferences private val preferences: SharedPreferences,
) : AutoLockTimeoutRepository {
    private val mutableTimeout = MutableStateFlow(
        AutoLockTimeout.fromStoredValue(
            preferences.getString(KEY_TIMEOUT, null),
        ),
    )

    override val timeout: StateFlow<AutoLockTimeout> = mutableTimeout.asStateFlow()

    override fun setTimeout(timeout: AutoLockTimeout) {
        preferences.edit()
            .putString(KEY_TIMEOUT, timeout.storedValue)
            .apply()
        mutableTimeout.value = timeout
    }

    private companion object {
        const val KEY_TIMEOUT = "auto_lock_timeout"
    }
}
