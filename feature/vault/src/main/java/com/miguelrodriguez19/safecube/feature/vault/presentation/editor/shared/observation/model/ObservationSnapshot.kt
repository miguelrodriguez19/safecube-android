package com.miguelrodriguez19.safecube.feature.vault.presentation.editor.shared.observation.model

import com.miguelrodriguez19.safecube.core.vault.domain.model.secureitem.SecureItemDraftType
import com.miguelrodriguez19.safecube.core.vault.domain.model.secureitem.crud.ObserveSecureItemDetailResult
import com.miguelrodriguez19.safecube.core.vault.domain.model.secureitem.crud.ObserveSecureItemDraftDetailResult
import com.miguelrodriguez19.safecube.core.vault.domain.model.secureitem.crud.SecureItemCrudError
import com.miguelrodriguez19.safecube.core.vault.domain.model.secureitem.crud.SecureItemDetail
import com.miguelrodriguez19.safecube.core.vault.domain.model.secureitem.crud.SecureItemDraftDetail

internal data class ObservationSnapshot(
    val officialDetail: SecureItemDetail? = null,
    val draftDetail: SecureItemDraftDetail? = null,
    val officialResolved: Boolean = false,
    val draftResolved: Boolean = false,
    val officialNotFound: Boolean = false,
    val draftNotFound: Boolean = false,
    val officialError: SecureItemCrudError? = null,
    val draftError: SecureItemCrudError? = null,
) {
    fun reduce(event: ObservationEvent): ObservationSnapshot = when (event) {
        is ObservationEvent.Official -> reduceOfficial(event.result)
        is ObservationEvent.Draft -> reduceDraft(event.result)
    }

    private fun reduceOfficial(result: ObserveSecureItemDetailResult): ObservationSnapshot = when (result) {
        is ObserveSecureItemDetailResult.Success -> copy(
            officialDetail = result.detail,
            officialResolved = true,
            officialNotFound = false,
            officialError = null,
        )

        is ObserveSecureItemDetailResult.Error -> if (result.reason == SecureItemCrudError.ItemNotFound) {
            copy(
                officialDetail = null,
                officialResolved = true,
                officialNotFound = true,
                officialError = null,
            )
        } else {
            copy(
                officialResolved = true,
                officialError = result.reason,
            )
        }
    }

    private fun reduceDraft(result: ObserveSecureItemDraftDetailResult): ObservationSnapshot = when (result) {
        is ObserveSecureItemDraftDetailResult.Success -> copy(
            draftDetail = result.detail,
            draftResolved = true,
            draftNotFound = false,
            draftError = null,
        )

        ObserveSecureItemDraftDetailResult.NotFound -> copy(
            draftDetail = null,
            draftResolved = true,
            draftNotFound = true,
            draftError = null,
        )

        is ObserveSecureItemDraftDetailResult.Error -> if (result.reason == SecureItemCrudError.ItemNotFound) {
            copy(
                draftDetail = null,
                draftResolved = true,
                draftNotFound = true,
                draftError = null,
            )
        } else {
            copy(
                draftResolved = true,
                draftError = result.reason,
            )
        }
    }

    fun toResult(): SecureItemEditorObservationResult? {
        officialError?.let { return SecureItemEditorObservationResult.Error(it) }
        draftError?.let { return SecureItemEditorObservationResult.Error(it) }

        if (isInconsistent()) {
            return SecureItemEditorObservationResult.InconsistentOfficialDraft
        }
        if (officialDetail != null || draftDetail != null) {
            return SecureItemEditorObservationResult.Content(
                officialDetail = officialDetail,
                draftDetail = draftDetail,
            )
        }
        if (officialResolved && draftResolved && officialNotFound && draftNotFound) {
            return SecureItemEditorObservationResult.NotFound
        }
        return null
    }

    private fun isInconsistent(): Boolean {
        if (
            officialDetail != null && draftDetail != null &&
            (
                officialDetail.logicalItemId != draftDetail.logicalItemId ||
                    officialDetail.itemType != draftDetail.itemType
            )
        ) {
            return true
        }
        return officialResolved && officialDetail == null && draftDetail != null &&
            draftDetail.draftType != SecureItemDraftType.CREATE &&
            !draftDetail.requiresSaveAsNew
    }
}
