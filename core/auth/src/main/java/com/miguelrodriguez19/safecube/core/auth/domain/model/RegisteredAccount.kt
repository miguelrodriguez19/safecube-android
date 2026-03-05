package com.miguelrodriguez19.safecube.core.auth.domain.model

import java.time.OffsetDateTime
import java.util.UUID

data class RegisteredAccount(
    val accountId: UUID?,
    val createdAt: OffsetDateTime?,
)
