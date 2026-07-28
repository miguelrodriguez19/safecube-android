package com.miguelrodriguez19.safecube.core.vault.domain.usecase.secureitem

import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

interface SecureItemMutationIdGenerator {
    fun generate(): UUID
}

@Singleton
class RandomSecureItemMutationIdGenerator @Inject constructor() : SecureItemMutationIdGenerator {
    override fun generate(): UUID = UUID.randomUUID()
}
