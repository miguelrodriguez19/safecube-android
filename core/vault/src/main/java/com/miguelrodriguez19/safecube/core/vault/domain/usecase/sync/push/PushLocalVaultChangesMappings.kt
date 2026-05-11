package com.miguelrodriguez19.safecube.core.vault.domain.usecase.sync.push

import com.miguelrodriguez19.safecube.core.vault.domain.model.remote.request.RemoteCreateSecureItemRequest
import com.miguelrodriguez19.safecube.core.vault.domain.model.remote.request.RemoteUpdateSecureItemRequest
import com.miguelrodriguez19.safecube.core.vault.domain.model.secureitem.SecureItem
import com.miguelrodriguez19.safecube.core.vault.domain.model.secureitem.SecureItemSyncDraft
import java.time.Instant

internal fun SecureItem.toRemoteCreateRequest(): RemoteCreateSecureItemRequest = RemoteCreateSecureItemRequest(
    itemType = itemType.wireName,
    schemaVersion = schemaVersion,
    displayHint = displayHint,
    payload = payload,
)

internal fun SecureItem.toRemoteUpdateRequest(): RemoteUpdateSecureItemRequest = RemoteUpdateSecureItemRequest(
    itemType = itemType.wireName,
    schemaVersion = schemaVersion,
    displayHint = displayHint,
    payload = payload,
)

internal fun SecureItemSyncDraft.toPublishedUpdateRequest(): RemoteUpdateSecureItemRequest = RemoteUpdateSecureItemRequest(
    itemType = itemType.wireName,
    schemaVersion = schemaVersion,
    displayHint = displayHint,
    payload = payload,
)

internal fun SecureItem.localDeleteTimestamp(): Instant = deletedAt ?: updatedAt
