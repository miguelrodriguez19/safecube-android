package com.miguelrodriguez19.safecube.feature.vault.presentation.create

data class CreateVaultUiState(
    val passphrase: String = "",
    val passphraseErrorRes: Int? = null,
    val isLoading: Boolean = false,
    val errorMessageRes: Int? = null,
    val recoveryKeyBase64: String? = null,
    val navigateToUnlock: Boolean = false,
)
