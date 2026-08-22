package com.miguelrodriguez19.safecube.feature.vault.presentation.shared.editor.mutation.factory

import com.miguelrodriguez19.safecube.core.vault.domain.model.secureitem.SecureItemType
import com.miguelrodriguez19.safecube.feature.vault.presentation.shared.editor.mutation.contract.SecureItemEditorMutationGateway
import javax.inject.Inject
import kotlin.jvm.JvmSuppressWildcards

internal class SecureItemEditorMutationGatewayFactory @Inject constructor(
    private val gateways: Map<SecureItemType, @JvmSuppressWildcards SecureItemEditorMutationGateway>,
) {
    fun gatewayFor(itemType: SecureItemType): SecureItemEditorMutationGateway? = gateways[itemType]
}
