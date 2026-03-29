package com.miguelrodriguez19.safecube.core.vault.domain.usecase.secureitem

import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

internal interface SecureItemIdGenerator {
    fun generate(): UUID
}

@Singleton
internal class RandomSecureItemIdGenerator @Inject constructor() : SecureItemIdGenerator {
    override fun generate(): UUID = UUID.randomUUID()
}
