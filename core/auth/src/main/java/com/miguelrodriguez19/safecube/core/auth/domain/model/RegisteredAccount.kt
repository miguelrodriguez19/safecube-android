package com.miguelrodriguez19.safecube.core.auth.domain.model

import java.time.Instant
import java.util.UUID

data class RegisteredAccount(
    val accountId: UUID?,
    val createdAt: Instant?,
)
