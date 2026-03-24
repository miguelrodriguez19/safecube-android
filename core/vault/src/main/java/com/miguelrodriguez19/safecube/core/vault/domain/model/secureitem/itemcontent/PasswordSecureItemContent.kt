package com.miguelrodriguez19.safecube.core.vault.domain.model.secureitem.itemcontent

import com.miguelrodriguez19.safecube.core.vault.domain.model.secureitem.SecureItemType
import kotlinx.serialization.Serializable

@Serializable
data class PasswordSecureItemContent(
    val username: String? = null,
    val email: String? = null,
    val password: String,
    val website: PasswordWebsiteSecureItemContent? = null,
    val notes: String? = null,
    val totp: PasswordTotpSecureItemContent? = null,
) : SecureItemContent {
    init {
        require(password.isNotBlank()) { "password must not be blank." }
        require(!username.isNullOrBlank() || !email.isNullOrBlank()) {
            "at least one of username or email must be present."
        }
    }

    override val itemType: SecureItemType = SecureItemType.PASSWORD
    override val schemaVersion: Int = PASSWORD_SCHEMA_VERSION

    companion object {
        const val PASSWORD_SCHEMA_VERSION: Int = 1
    }
}

@Serializable
data class PasswordTotpSecureItemContent(
    val secret: String,
    val issuer: String? = null,
    val accountName: String? = null,
) {
    init {
        require(secret.isNotBlank()) { "secret must not be blank." }
    }
}

@Serializable
data class PasswordWebsiteSecureItemContent(
    val url: String? = null,
    val domain: String? = null,
) {
    init {
        require(!url.isNullOrBlank() || !domain.isNullOrBlank()) {
            "at least one of url or domain must be present."
        }
    }
}