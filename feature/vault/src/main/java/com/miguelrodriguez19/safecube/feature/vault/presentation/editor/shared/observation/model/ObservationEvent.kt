package com.miguelrodriguez19.safecube.feature.vault.presentation.editor.shared.observation.model

import com.miguelrodriguez19.safecube.core.vault.domain.model.secureitem.crud.ObserveSecureItemDetailResult
import com.miguelrodriguez19.safecube.core.vault.domain.model.secureitem.crud.ObserveSecureItemDraftDetailResult

internal sealed interface ObservationEvent {
    data class Official(
        val result: ObserveSecureItemDetailResult,
    ) : ObservationEvent

    data class Draft(
        val result: ObserveSecureItemDraftDetailResult,
    ) : ObservationEvent
}
