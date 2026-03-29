package com.miguelrodriguez19.safecube.core.vault.domain.usecase.secureitem

import java.time.Instant
import javax.inject.Inject
import javax.inject.Singleton

internal interface CurrentInstantProvider {
    fun now(): Instant
}

@Singleton
internal class SystemCurrentInstantProvider @Inject constructor() : CurrentInstantProvider {
    override fun now(): Instant = Instant.now()
}
