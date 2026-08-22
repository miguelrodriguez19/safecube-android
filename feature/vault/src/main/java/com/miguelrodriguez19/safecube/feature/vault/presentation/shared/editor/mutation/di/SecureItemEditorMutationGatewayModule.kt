package com.miguelrodriguez19.safecube.feature.vault.presentation.shared.editor.mutation.di

import com.miguelrodriguez19.safecube.core.vault.domain.model.secureitem.SecureItemType
import com.miguelrodriguez19.safecube.feature.vault.presentation.noteeditor.mutation.NoteEditorMutationGateway
import com.miguelrodriguez19.safecube.feature.vault.presentation.passwordeditor.mutation.PasswordEditorMutationGateway
import com.miguelrodriguez19.safecube.feature.vault.presentation.shared.editor.mutation.contract.SecureItemEditorMutationGateway
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dagger.multibindings.IntoMap

@Module
@InstallIn(SingletonComponent::class)
internal abstract class SecureItemEditorMutationGatewayModule {
    @Binds
    @IntoMap
    @SecureItemTypeKey(SecureItemType.NOTE)
    abstract fun bindNoteEditorMutationGateway(
        gateway: NoteEditorMutationGateway,
    ): SecureItemEditorMutationGateway

    @Binds
    @IntoMap
    @SecureItemTypeKey(SecureItemType.PASSWORD)
    abstract fun bindPasswordEditorMutationGateway(
        gateway: PasswordEditorMutationGateway,
    ): SecureItemEditorMutationGateway
}
