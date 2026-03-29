package com.miguelrodriguez19.safecube.core.vault.domain.usecase.password

import com.miguelrodriguez19.safecube.core.vault.domain.model.secureitem.crud.SecurePasswordDraft
import com.miguelrodriguez19.safecube.core.vault.domain.model.secureitem.crud.SecurePasswordTotpDraft
import com.miguelrodriguez19.safecube.core.vault.domain.model.secureitem.crud.SecurePasswordWebsiteDraft
import com.miguelrodriguez19.safecube.core.vault.domain.model.secureitem.itemcontent.PasswordSecureItemContent
import com.miguelrodriguez19.safecube.core.vault.domain.model.secureitem.itemcontent.PasswordTotpSecureItemContent
import com.miguelrodriguez19.safecube.core.vault.domain.model.secureitem.itemcontent.PasswordWebsiteSecureItemContent
import com.miguelrodriguez19.safecube.core.vault.domain.usecase.secureitem.password.PasswordDraftToContentMapper
import org.junit.Assert.assertEquals
import org.junit.Test

class PasswordDraftToContentMapperTest {

    private val target = PasswordDraftToContentMapper()

    @Test
    fun `map when draft is valid then maps password content`() {
        val draft = SecurePasswordDraft(
            displayHint = "Github",
            username = "user",
            email = "user@example.com",
            password = "secret",
            website = SecurePasswordWebsiteDraft(
                url = "https://github.com",
                domain = "github.com",
            ),
            notes = "2FA enabled",
            totp = SecurePasswordTotpDraft(
                secret = "totp-secret",
                issuer = "Github",
                accountName = "user@example.com",
            ),
        )

        val result = target.map(draft)
        assertEquals(draft.username, result.username)
        assertEquals(draft.email, result.email)
        assertEquals(draft.password, result.password)
        assertEquals(draft.website?.url, result.website?.url)
        assertEquals(draft.website?.domain, result.website?.domain)
        assertEquals(draft.notes, result.notes)
        assertEquals(draft.totp?.secret, result.totp?.secret)
        assertEquals(draft.totp?.issuer, result.totp?.issuer)
        assertEquals(draft.totp?.accountName, result.totp?.accountName)
    }

    @Test(expected = IllegalArgumentException::class)
    fun `map when draft is invalid then throws validation error`() {
        target.map(
            SecurePasswordDraft(
                displayHint = "Github",
                password = " ",
            ),
        )
    }
}
