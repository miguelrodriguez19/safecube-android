package com.miguelrodriguez19.safecube.core.vault.test

import com.miguelrodriguez19.safecube.core.vault.domain.model.VaultKeyMaterial
import com.miguelrodriguez19.safecube.core.vault.domain.model.VaultState
import com.miguelrodriguez19.safecube.core.vault.domain.model.secureitem.SecureItem
import com.miguelrodriguez19.safecube.core.vault.domain.model.secureitem.SecureItemDraftSyncStatus
import com.miguelrodriguez19.safecube.core.vault.domain.model.secureitem.SecureItemDraftType
import com.miguelrodriguez19.safecube.core.vault.domain.model.secureitem.SecureItemSyncDraft
import com.miguelrodriguez19.safecube.core.vault.domain.model.secureitem.SecureItemSyncState
import com.miguelrodriguez19.safecube.core.vault.domain.model.secureitem.SecureItemType
import com.miguelrodriguez19.safecube.core.vault.domain.model.unlock.VaultUnlockError
import com.miguelrodriguez19.safecube.core.vault.domain.service.EncryptedSecureItemPayload
import com.miguelrodriguez19.safecube.core.vault.domain.session.VaultSessionManager
import java.time.Instant
import java.util.UUID
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

fun testEncryptedPayload(
    itemType: SecureItemType = SecureItemType.NOTE,
    schemaVersion: Int = 1,
    payload: ByteArray = byteArrayOf(1, 2, 3),
): EncryptedSecureItemPayload = EncryptedSecureItemPayload(
    itemType = itemType,
    schemaVersion = schemaVersion,
    payload = payload,
)

fun testSecureItem(
    logicalItemId: UUID = UUID.randomUUID(),
    remoteItemId: UUID? = UUID.randomUUID(),
    itemType: SecureItemType = SecureItemType.NOTE,
    displayHint: String = "Official item",
    payload: ByteArray = byteArrayOf(1, 2, 3),
    payloadVersion: Long = 1,
    itemRevision: Long = 1,
    changeSequence: Long = 1,
    createdAt: Instant = Instant.parse("2024-01-01T00:00:00Z"),
    updatedAt: Instant = createdAt,
    deletedAt: Instant? = null,
    syncState: SecureItemSyncState = SecureItemSyncState.SYNCED,
    lastSyncedAt: Instant? = updatedAt,
    lastSyncError: String? = null,
): SecureItem = SecureItem(
    logicalItemId = logicalItemId,
    remoteItemId = remoteItemId,
    itemType = itemType,
    schemaVersion = 1,
    displayHint = displayHint,
    payload = payload,
    payloadVersion = payloadVersion,
    itemRevision = itemRevision,
    changeSequence = changeSequence,
    createdAt = createdAt,
    updatedAt = updatedAt,
    deletedAt = deletedAt,
    syncState = syncState,
    lastSyncedAt = lastSyncedAt,
    lastSyncError = lastSyncError,
)

fun testSecureItemDraft(
    logicalItemId: UUID = UUID.randomUUID(),
    remoteItemId: UUID? = UUID.randomUUID(),
    itemType: SecureItemType = SecureItemType.NOTE,
    displayHint: String = "Draft item",
    payload: ByteArray = byteArrayOf(4, 5, 6),
    payloadVersion: Long = 2,
    mutationId: UUID = UUID.randomUUID(),
    createdAt: Instant = Instant.parse("2024-01-01T00:00:00Z"),
    updatedAt: Instant = createdAt.plusSeconds(60),
    deletedAt: Instant? = null,
    lastSyncedAt: Instant? = createdAt,
    draftType: SecureItemDraftType = SecureItemDraftType.UPDATE,
    draftSyncStatus: SecureItemDraftSyncStatus = SecureItemDraftSyncStatus.READY_TO_SYNC,
    baseItemRevision: Long? = if (draftType == SecureItemDraftType.CREATE) null else 1,
    lastSyncError: String? = null,
): SecureItemSyncDraft = SecureItemSyncDraft(
    logicalItemId = logicalItemId,
    remoteItemId = remoteItemId,
    itemType = itemType,
    schemaVersion = 1,
    displayHint = displayHint,
    payload = payload,
    payloadVersion = payloadVersion,
    mutationId = mutationId,
    createdAt = createdAt,
    updatedAt = updatedAt,
    deletedAt = deletedAt,
    lastSyncedAt = lastSyncedAt,
    draftType = draftType,
    draftSyncStatus = draftSyncStatus,
    baseItemRevision = baseItemRevision,
    lastSyncError = lastSyncError,
)

fun testVaultKeyMaterial(accountId: UUID = UUID.randomUUID()): VaultKeyMaterial = VaultKeyMaterial(
    accountId = accountId,
    kekEncMaster = byteArrayOf(1),
    kekEncRecovery = byteArrayOf(2),
    kdfAlgorithm = "argon2id",
    kdfSalt = byteArrayOf(3),
    kdfMemoryKib = 1,
    kdfIterations = 1,
    kdfParallelism = 1,
    kdfOutputLen = 32,
    cryptoVersion = "1",
)

class FakeVaultSessionManager(
    initialState: VaultState = VaultState.Unlocked,
) : VaultSessionManager {
    private val mutableVaultState = MutableStateFlow(initialState)

    override val vaultState: StateFlow<VaultState> = mutableVaultState

    fun setState(state: VaultState) {
        mutableVaultState.value = state
    }

    override suspend fun refreshVaultState() = Unit

    override fun isUnlocked(): Boolean = vaultState.value == VaultState.Unlocked

    override fun unlockWithPassphrase(passphrase: String): VaultUnlockError? = null

    override fun unlockWithRecoveryKey(recoveryKey: ByteArray): VaultUnlockError? = null

    override fun lock() {
        mutableVaultState.value = VaultState.Locked
    }
}
