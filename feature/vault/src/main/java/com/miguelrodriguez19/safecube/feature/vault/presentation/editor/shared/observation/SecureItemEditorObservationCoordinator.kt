package com.miguelrodriguez19.safecube.feature.vault.presentation.editor.shared.observation

import com.miguelrodriguez19.safecube.core.vault.domain.usecase.secureitem.ObserveSecureItemDetailUseCase
import com.miguelrodriguez19.safecube.core.vault.domain.usecase.secureitem.ObserveSecureItemDraftDetailUseCase
import com.miguelrodriguez19.safecube.feature.vault.presentation.editor.shared.observation.model.ObservationEvent
import com.miguelrodriguez19.safecube.feature.vault.presentation.editor.shared.observation.model.ObservationSnapshot
import com.miguelrodriguez19.safecube.feature.vault.presentation.editor.shared.observation.model.SecureItemEditorObservationResult
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.mapNotNull
import kotlinx.coroutines.flow.merge
import kotlinx.coroutines.flow.scan

@Singleton
internal class SecureItemEditorObservationCoordinator @Inject constructor(
    private val observeSecureItemDetailUseCase: ObserveSecureItemDetailUseCase,
    private val observeSecureItemDraftDetailUseCase: ObserveSecureItemDraftDetailUseCase,
) {
    fun observe(logicalItemId: UUID): Flow<SecureItemEditorObservationResult> = merge(
        observeSecureItemDetailUseCase(logicalItemId).map { result ->
            ObservationEvent.Official(result)
        },
        observeSecureItemDraftDetailUseCase(logicalItemId).map { result ->
            ObservationEvent.Draft(result)
        },
    )
        .scan(ObservationSnapshot()) { snapshot, event -> snapshot.reduce(event) }
        .mapNotNull { snapshot -> snapshot.toResult() }
}
