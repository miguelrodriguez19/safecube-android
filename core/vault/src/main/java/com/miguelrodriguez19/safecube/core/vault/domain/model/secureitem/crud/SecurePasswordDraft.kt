package com.miguelrodriguez19.safecube.core.vault.domain.model.secureitem.crud

data class SecurePasswordDraft(
    val displayHint: String,
    val username: String? = null,
    val email: String? = null,
    val password: String,
    val website: SecurePasswordWebsiteDraft? = null,
    val notes: String? = null,
    val totp: SecurePasswordTotpDraft? = null,
)

data class SecurePasswordWebsiteDraft(
    val url: String? = null,
    val domain: String? = null,
)

data class SecurePasswordTotpDraft(
    val secret: String,
    val issuer: String? = null,
    val accountName: String? = null,
)
