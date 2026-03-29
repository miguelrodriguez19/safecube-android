package com.miguelrodriguez19.safecube.core.vault.domain.usecase.secureitem.password

import com.miguelrodriguez19.safecube.core.vault.domain.model.secureitem.crud.SecurePasswordDraft
import com.miguelrodriguez19.safecube.core.vault.domain.model.secureitem.itemcontent.PasswordSecureItemContent
import com.miguelrodriguez19.safecube.core.vault.domain.model.secureitem.itemcontent.PasswordTotpSecureItemContent
import com.miguelrodriguez19.safecube.core.vault.domain.model.secureitem.itemcontent.PasswordWebsiteSecureItemContent
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PasswordDraftToContentMapper @Inject internal constructor() {
    fun map(draft: SecurePasswordDraft): PasswordSecureItemContent = PasswordSecureItemContent(
        username = draft.username,
        email = draft.email,
        password = draft.password,
        website = draft.website?.let {
            PasswordWebsiteSecureItemContent(
                url = it.url,
                domain = it.domain,
            )
        },
        notes = draft.notes,
        totp = draft.totp?.let {
            PasswordTotpSecureItemContent(
                secret = it.secret,
                issuer = it.issuer,
                accountName = it.accountName,
            )
        },
    )
}
