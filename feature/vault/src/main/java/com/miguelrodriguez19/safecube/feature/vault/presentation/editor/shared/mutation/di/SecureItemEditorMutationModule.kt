package com.miguelrodriguez19.safecube.feature.vault.presentation.editor.shared.mutation.di

import com.miguelrodriguez19.safecube.feature.vault.presentation.editor.shared.mutation.SecureItemEditorMutationCoordinator
import com.miguelrodriguez19.safecube.feature.vault.presentation.editor.shared.mutation.contract.SecureItemEditorMutationOperations
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
internal abstract class SecureItemEditorMutationModule {
    @Binds
    abstract fun bindSecureItemEditorMutationOperations(
        coordinator: SecureItemEditorMutationCoordinator,
    ): SecureItemEditorMutationOperations
}
