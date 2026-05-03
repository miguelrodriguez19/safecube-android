package com.miguelrodriguez19.safecube.core.vault.domain.usecase.sync

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

@Singleton
class VaultSyncExecutionLock @Inject constructor() {
    private val mutex = Mutex()

    private val _isSyncing = MutableStateFlow(false)
    val isSyncing: StateFlow<Boolean> = _isSyncing.asStateFlow()

    suspend fun <T> withLock(
        block: suspend () -> T,
    ): T = mutex.withLock {
        _isSyncing.value = true
        try {
            block()
        } finally {
            _isSyncing.value = false
        }
    }

    fun tryLock(): Boolean {
        val locked = mutex.tryLock()
        if (locked) {
            _isSyncing.value = true
        }
        return locked
    }

    fun unlock() {
        _isSyncing.value = false
        mutex.unlock()
    }
}
