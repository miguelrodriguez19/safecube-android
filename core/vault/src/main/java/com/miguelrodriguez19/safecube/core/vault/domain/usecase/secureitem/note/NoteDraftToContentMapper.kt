package com.miguelrodriguez19.safecube.core.vault.domain.usecase.secureitem.note

import com.miguelrodriguez19.safecube.core.vault.domain.model.secureitem.crud.SecureNoteDraft
import com.miguelrodriguez19.safecube.core.vault.domain.model.secureitem.itemcontent.NoteSecureItemContent
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class NoteDraftToContentMapper @Inject internal constructor() {
    fun map(draft: SecureNoteDraft): NoteSecureItemContent = NoteSecureItemContent(
        body = draft.body,
    )
}
