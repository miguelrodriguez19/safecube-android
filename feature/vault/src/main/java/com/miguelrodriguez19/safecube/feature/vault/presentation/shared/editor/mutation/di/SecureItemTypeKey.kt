package com.miguelrodriguez19.safecube.feature.vault.presentation.shared.editor.mutation.di

import com.miguelrodriguez19.safecube.core.vault.domain.model.secureitem.SecureItemType
import dagger.MapKey

@MapKey
@Retention(AnnotationRetention.BINARY)
internal annotation class SecureItemTypeKey(
    val value: SecureItemType,
)
