package com.miguelrodriguez19.safecube.core.vault.domain.usecase.sync.push

import com.miguelrodriguez19.safecube.core.vault.domain.model.remote.request.RemoteCreateSecureItemRequest
import com.miguelrodriguez19.safecube.core.vault.domain.model.remote.request.RemoteDeleteSecureItemRequest
import com.miguelrodriguez19.safecube.core.vault.domain.model.remote.request.RemoteUpdateSecureItemRequest
import com.miguelrodriguez19.safecube.core.vault.domain.model.secureitem.SecureItemSyncDraft

internal fun SecureItemSyncDraft.toRemoteCreateRequest(): RemoteCreateSecureItemRequest = RemoteCreateSecureItemRequest(
    itemType = itemType.wireName,
    schemaVersion = schemaVersion,
    displayHint = displayHint,
    payload = payload,
    payloadVersion = payloadVersion,
    mutationId = mutationId,
)

internal fun SecureItemSyncDraft.toRemoteUpdateRequest(): RemoteUpdateSecureItemRequest = RemoteUpdateSecureItemRequest(
    itemType = itemType.wireName,
    schemaVersion = schemaVersion,
    displayHint = displayHint,
    payload = payload,
    payloadVersion = payloadVersion,
    baseItemRevision = requireNotNull(baseItemRevision) {
        "UPDATE draft requires a base item revision."
    },
    mutationId = mutationId,
)

internal fun SecureItemSyncDraft.toRemoteDeleteRequest(): RemoteDeleteSecureItemRequest =
    RemoteDeleteSecureItemRequest(
        baseItemRevision = requireNotNull(baseItemRevision) {
            "DELETE draft requires a base item revision."
        },
        mutationId = mutationId,
    )
