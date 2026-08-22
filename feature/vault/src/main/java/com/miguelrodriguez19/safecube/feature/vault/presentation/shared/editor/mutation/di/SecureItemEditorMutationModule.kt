package com.miguelrodriguez19.safecube.feature.vault.presentation.shared.editor.mutation.di

import com.miguelrodriguez19.safecube.feature.vault.presentation.shared.editor.mutation.SecureItemEditorMutationCoordinator
import com.miguelrodriguez19.safecube.feature.vault.presentation.shared.editor.mutation.contract.SecureItemEditorMutationOperations
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
