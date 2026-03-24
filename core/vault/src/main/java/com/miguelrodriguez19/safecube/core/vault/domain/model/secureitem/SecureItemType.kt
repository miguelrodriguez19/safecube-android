package com.miguelrodriguez19.safecube.core.vault.domain.model.secureitem

enum class SecureItemType {
    PASSWORD,
    NOTE;

    val wireName: String
        get() = name

    companion object {
        fun fromWireName(value: String): SecureItemType? = entries.firstOrNull { it.wireName == value }
    }
}
