package com.miguelrodriguez19.safecube.feature.vault.presentation.create.state

data class CreateVaultUiState(
    val passphrase: String = "",
    val passphraseErrorRes: Int? = null,
    val isLoading: Boolean = false,
    val errorMessageRes: Int? = null,
)
