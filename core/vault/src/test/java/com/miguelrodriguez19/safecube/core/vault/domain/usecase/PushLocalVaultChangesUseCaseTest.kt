package com.miguelrodriguez19.safecube.core.vault.domain.usecase

import com.miguelrodriguez19.safecube.core.network.domain.model.NetworkFailureClassifier
import com.miguelrodriguez19.safecube.core.vault.domain.model.remote.RemoteSecureItem
import com.miguelrodriguez19.safecube.core.vault.domain.model.remote.result.RemoteCreateSecureItemResult
import com.miguelrodriguez19.safecube.core.vault.domain.model.remote.result.RemoteDeleteSecureItemResult
import com.miguelrodriguez19.safecube.core.vault.domain.model.remote.result.RemoteUpdateSecureItemResult
import com.miguelrodriguez19.safecube.core.vault.domain.model.remote.result.SecureItemRemoteError
import com.miguelrodriguez19.safecube.core.vault.domain.model.remote.result.SecureItemRemoteResult
import com.miguelrodriguez19.safecube.core.vault.domain.model.remote.request.RemoteCreateSecureItemRequest
import com.miguelrodriguez19.safecube.core.vault.domain.model.secureitem.SecureItemDraftType
import com.miguelrodriguez19.safecube.core.vault.domain.model.sync.push.PushLocalVaultChangesError
import com.miguelrodriguez19.safecube.core.vault.domain.model.sync.push.PushLocalVaultChangesResult
import com.miguelrodriguez19.safecube.core.vault.domain.repository.SecureItemDraftRepository
import com.miguelrodriguez19.safecube.core.vault.domain.repository.SecureItemRemoteRepository
import com.miguelrodriguez19.safecube.core.vault.domain.usecase.sync.draft.SecureItemDraftSyncCoordinator
import com.miguelrodriguez19.safecube.core.vault.domain.usecase.sync.push.PushLocalVaultChangesUseCase
import com.miguelrodriguez19.safecube.core.vault.test.testSecureItemDraft
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import java.io.IOException
import java.time.Instant
import java.util.UUID
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertArrayEquals
import org.junit.Assert.assertEquals
import org.junit.Test

class PushLocalVaultChangesUseCaseTest {
    private val secureItemDraftRepository = mockk<SecureItemDraftRepository>()
    private val secureItemRemoteRepository = mockk<SecureItemRemoteRepository>()
    private val secureItemDraftSyncCoordinator = mockk<SecureItemDraftSyncCoordinator>()

    private val target = PushLocalVaultChangesUseCase(
        secureItemDraftRepository = secureItemDraftRepository,
        secureItemRemoteRepository = secureItemRemoteRepository,
        secureItemDraftSyncCoordinator = secureItemDraftSyncCoordinator,
    )

    @Test
    fun `invoke when create draft is uploaded then officializes and counts synced`() = runBlocking {
        val draft = testSecureItemDraft(remoteItemId = null, draftType = com.miguelrodriguez19.safecube.core.vault.domain.model.secureitem.SecureItemDraftType.CREATE)
        val remoteItemId = UUID.randomUUID()
        val createdAt = Instant.parse("2024-03-01T00:00:00Z")
        val remoteResult = RemoteCreateSecureItemResult(
            itemId = remoteItemId,
            mutationId = draft.mutationId,
            payloadVersion = draft.payloadVersion,
            itemRevision = 1,
            changeSequence = 10,
            updatedAt = createdAt,
        )
        coEvery { secureItemDraftRepository.getSyncableDraftsOrdered() } returns listOf(draft)
        coEvery { secureItemRemoteRepository.createVaultItem(any()) } returns SecureItemRemoteResult.Success(
            remoteResult,
        )
        coEvery {
            secureItemDraftSyncCoordinator.officializeCreatedDraft(draft, remoteResult)
        } returns true

        val result = target()

        assertEquals(
            PushLocalVaultChangesResult.Success(
                processedCount = 1,
                syncedCount = 1,
                conflictCount = 0,
                keptPendingCount = 0,
                locallyResolvedDeleteCount = 0,
            ),
            result,
        )
    }

    @Test
    fun `retry after uncertain create reuses the persisted mutation and payload`() = runBlocking {
        val draft = testSecureItemDraft(
            remoteItemId = null,
            draftType = SecureItemDraftType.CREATE,
        )
        val requests = mutableListOf<RemoteCreateSecureItemRequest>()
        val remoteResult = RemoteCreateSecureItemResult(
            itemId = UUID.randomUUID(),
            mutationId = draft.mutationId,
            payloadVersion = draft.payloadVersion,
            itemRevision = 1,
            changeSequence = 10,
            updatedAt = draft.updatedAt,
        )
        coEvery { secureItemDraftRepository.getSyncableDraftsOrdered() } returns listOf(draft)
        coEvery {
            secureItemRemoteRepository.createVaultItem(capture(requests))
        } returnsMany listOf(
            SecureItemRemoteResult.Error(SecureItemRemoteError.NetworkError(IOException())),
            SecureItemRemoteResult.Success(remoteResult),
        )
        coEvery {
            secureItemDraftSyncCoordinator.officializeCreatedDraft(draft, remoteResult)
        } returns true

        val firstResult = target()
        val secondResult = target()

        assertEquals(
            PushLocalVaultChangesResult.Error(
                PushLocalVaultChangesError.RemoteFailure(
                    logicalItemId = draft.logicalItemId,
                    operation = "CREATE_REMOTE",
                    failure = NetworkFailureClassifier.fromThrowable(IOException()),
                ),
            ),
            firstResult,
        )
        assertEquals(
            PushLocalVaultChangesResult.Success(1, 1, 0, 0, 0),
            secondResult,
        )
        assertEquals(2, requests.size)
        assertEquals(requests[0].mutationId, requests[1].mutationId)
        assertEquals(requests[0].payloadVersion, requests[1].payloadVersion)
        assertArrayEquals(requests[0].payload, requests[1].payload)
        coVerify(exactly = 1) {
            secureItemDraftSyncCoordinator.officializeCreatedDraft(draft, remoteResult)
        }
    }

    @Test
    fun `invoke when update conflicts then keeps draft as conflict and counts it`() = runBlocking {
        val remoteItemId = UUID.randomUUID()
        val draft = testSecureItemDraft(remoteItemId = remoteItemId)
        val remoteOfficial = RemoteSecureItem(
            itemId = remoteItemId,
            itemType = draft.itemType.wireName,
            schemaVersion = 1,
            displayHint = "Remote official",
            payload = byteArrayOf(9, 9, 9),
            payloadVersion = 3,
            itemRevision = 6,
            changeSequence = 11,
            updatedAt = Instant.parse("2024-03-01T01:00:00Z"),
            deletedAt = null,
        )
        coEvery { secureItemDraftRepository.getSyncableDraftsOrdered() } returns listOf(draft)
        coEvery { secureItemRemoteRepository.updateVaultItem(remoteItemId, any()) } returns SecureItemRemoteResult.Error(
            SecureItemRemoteError.PreconditionFailed,
        )
        coEvery { secureItemRemoteRepository.getVaultItem(remoteItemId) } returns SecureItemRemoteResult.Success(remoteOfficial)
        coEvery {
            secureItemDraftSyncCoordinator.replaceOfficialWithRemoteAndConflictedDraft(
                draft = draft,
                remoteItem = any(),
                lastSyncedAt = remoteOfficial.updatedAt,
                lastSyncError = "Update conflicted with backend state.",
            )
        } returns true

        val result = target()

        assertEquals(
            PushLocalVaultChangesResult.Success(
                processedCount = 1,
                syncedCount = 0,
                conflictCount = 1,
                keptPendingCount = 0,
                locallyResolvedDeleteCount = 0,
            ),
            result,
        )
    }

    @Test
    fun `invoke when create is rejected as invalid then keeps draft and stops push`() = runBlocking {
        val draft = testSecureItemDraft(
            remoteItemId = null,
            draftType = com.miguelrodriguez19.safecube.core.vault.domain.model.secureitem.SecureItemDraftType.CREATE,
        )
        coEvery { secureItemDraftRepository.getSyncableDraftsOrdered() } returns listOf(draft)
        coEvery {
            secureItemRemoteRepository.createVaultItem(any())
        } returns SecureItemRemoteResult.Error(
            SecureItemRemoteError.ValidationFailed(
                fields = mapOf("payloadVersion" to "must be positive"),
            ),
        )

        val result = target()

        assertEquals(
            PushLocalVaultChangesResult.Error(
                PushLocalVaultChangesError.ProtocolIntegrityFailed(
                    logicalItemId = draft.logicalItemId,
                    operation = "CREATE_VALIDATION",
                ),
            ),
            result,
        )
        coVerify(exactly = 0) {
            secureItemDraftSyncCoordinator.officializeCreatedDraft(any(), any())
        }
    }

    @Test
    fun `invoke returns empty success when no drafts are syncable`() = runBlocking {
        coEvery { secureItemDraftRepository.getSyncableDraftsOrdered() } returns emptyList()

        assertEquals(
            PushLocalVaultChangesResult.Success(
                processedCount = 0,
                syncedCount = 0,
                conflictCount = 0,
                keptPendingCount = 0,
                locallyResolvedDeleteCount = 0,
            ),
            target(),
        )
    }

    @Test
    fun `successful update and delete responses are officialized`() = runBlocking {
        val update = testSecureItemDraft(draftType = SecureItemDraftType.UPDATE)
        val delete = testSecureItemDraft(
            draftType = SecureItemDraftType.DELETE,
            deletedAt = NOW,
        )
        val updateResult = updateResult(update)
        val deleteResult = deleteResult(delete)
        coEvery { secureItemDraftRepository.getSyncableDraftsOrdered() } returns listOf(update, delete)
        coEvery {
            secureItemRemoteRepository.updateVaultItem(requireNotNull(update.remoteItemId), any())
        } returns SecureItemRemoteResult.Success(updateResult)
        coEvery {
            secureItemRemoteRepository.deleteVaultItem(requireNotNull(delete.remoteItemId), any())
        } returns SecureItemRemoteResult.Success(deleteResult)
        coEvery {
            secureItemDraftSyncCoordinator.officializeUpdatedDraft(update, updateResult)
        } returns true
        coEvery {
            secureItemDraftSyncCoordinator.officializeDeletedDraft(delete, deleteResult)
        } returns true

        assertEquals(
            PushLocalVaultChangesResult.Success(
                processedCount = 2,
                syncedCount = 2,
                conflictCount = 0,
                keptPendingCount = 0,
                locallyResolvedDeleteCount = 0,
            ),
            target(),
        )
    }

    @Test
    fun `inconsistent successful responses stop push as integrity failures`() = runBlocking {
        val scenarios = listOf(
            SecureItemDraftType.CREATE to "CREATE_RESPONSE",
            SecureItemDraftType.UPDATE to "UPDATE_RESPONSE",
            SecureItemDraftType.DELETE to "DELETE_RESPONSE",
        )

        scenarios.forEach { (type, operation) ->
            val harness = harness(type)
            when (type) {
                SecureItemDraftType.CREATE -> {
                    val result = createResult(harness.draft)
                    coEvery {
                        harness.remote.createVaultItem(any())
                    } returns SecureItemRemoteResult.Success(result)
                    coEvery {
                        harness.coordinator.officializeCreatedDraft(harness.draft, result)
                    } returns false
                }

                SecureItemDraftType.UPDATE -> {
                    val result = updateResult(harness.draft)
                    coEvery {
                        harness.remote.updateVaultItem(requireNotNull(harness.draft.remoteItemId), any())
                    } returns SecureItemRemoteResult.Success(result)
                    coEvery {
                        harness.coordinator.officializeUpdatedDraft(harness.draft, result)
                    } returns false
                }

                SecureItemDraftType.DELETE -> {
                    val result = deleteResult(harness.draft)
                    coEvery {
                        harness.remote.deleteVaultItem(requireNotNull(harness.draft.remoteItemId), any())
                    } returns SecureItemRemoteResult.Success(result)
                    coEvery {
                        harness.coordinator.officializeDeletedDraft(harness.draft, result)
                    } returns false
                }
            }

            assertProtocolFailure(harness.target(), harness.draft, operation)
        }
    }

    @Test
    fun `definitive protocol errors stop push for every mutation type`() = runBlocking {
        val scenarios = listOf(
            Triple(SecureItemDraftType.CREATE, SecureItemRemoteError.IdempotencyConflict, "CREATE_IDEMPOTENCY"),
            Triple(SecureItemDraftType.CREATE, SecureItemRemoteError.PreconditionFailed, "CREATE_PRECONDITION"),
            Triple(SecureItemDraftType.CREATE, SecureItemRemoteError.PreconditionRequired, "CREATE_PRECONDITION"),
            Triple(
                SecureItemDraftType.CREATE,
                SecureItemRemoteError.ValidationFailed(emptyMap()),
                "CREATE_VALIDATION",
            ),
            Triple(SecureItemDraftType.UPDATE, SecureItemRemoteError.IdempotencyConflict, "UPDATE_PROTOCOL"),
            Triple(SecureItemDraftType.UPDATE, SecureItemRemoteError.PreconditionRequired, "UPDATE_PROTOCOL"),
            Triple(
                SecureItemDraftType.UPDATE,
                SecureItemRemoteError.ValidationFailed(emptyMap()),
                "UPDATE_VALIDATION",
            ),
            Triple(SecureItemDraftType.DELETE, SecureItemRemoteError.IdempotencyConflict, "DELETE_PROTOCOL"),
            Triple(SecureItemDraftType.DELETE, SecureItemRemoteError.PreconditionRequired, "DELETE_PROTOCOL"),
            Triple(
                SecureItemDraftType.DELETE,
                SecureItemRemoteError.ValidationFailed(emptyMap()),
                "DELETE_VALIDATION",
            ),
        )

        scenarios.forEach { (type, error, operation) ->
            val harness = harness(type)
            harness.remoteReturns(error)

            assertProtocolFailure(harness.target(), harness.draft, operation)
        }
    }

    @Test
    fun `retryable remote errors expose retry decision and keep every draft unchanged`() = runBlocking {
        val errors = listOf(
            SecureItemRemoteError.HttpError(
                failure = NetworkFailureClassifier.fromHttpStatus(408),
            ),
            SecureItemRemoteError.HttpError(
                failure = NetworkFailureClassifier.fromHttpStatus(429),
            ),
            SecureItemRemoteError.HttpError(
                failure = NetworkFailureClassifier.fromHttpStatus(503),
            ),
            SecureItemRemoteError.NetworkError(IOException("offline")),
        )

        SecureItemDraftType.entries.forEach { type ->
            errors.forEach { error ->
                val harness = harness(type)
                harness.remoteReturns(error)

                assertEquals(
                    PushLocalVaultChangesResult.Error(
                        PushLocalVaultChangesError.RemoteFailure(
                            logicalItemId = harness.draft.logicalItemId,
                            operation = remoteOperation(type),
                            failure = error.failure,
                        ),
                    ),
                    harness.target(),
                )
            }
        }
    }

    @Test
    fun `create item not found is a terminal remote failure`() = runBlocking {
        val harness = harness(SecureItemDraftType.CREATE)
        harness.remoteReturns(SecureItemRemoteError.ItemNotFound)

        assertEquals(
            PushLocalVaultChangesResult.Error(
                PushLocalVaultChangesError.RemoteFailure(
                    logicalItemId = harness.draft.logicalItemId,
                    operation = "CREATE_REMOTE",
                    failure = NetworkFailureClassifier.unknown(404),
                ),
            ),
            harness.target(),
        )
    }

    @Test
    fun `missing remote id is a local state failure for update and delete`() = runBlocking {
        listOf(
            SecureItemDraftType.UPDATE to "UPDATE_MISSING_REMOTE_ID",
            SecureItemDraftType.DELETE to "DELETE_MISSING_REMOTE_ID",
        ).forEach { (type, operation) ->
            val harness = harness(type, remoteItemId = null)

            assertEquals(
                PushLocalVaultChangesResult.Error(
                    PushLocalVaultChangesError.LocalStateUpdateFailed(
                        logicalItemId = harness.draft.logicalItemId,
                        operation = operation,
                    ),
                ),
                harness.target(),
            )
        }
    }

    @Test
    fun `update item not found preserves explicit save as new conflict`() = runBlocking {
        listOf(true, false).forEach { marked ->
            val harness = harness(SecureItemDraftType.UPDATE)
            harness.remoteReturns(SecureItemRemoteError.ItemNotFound)
            coEvery {
                harness.coordinator.markDraftConflict(
                    harness.draft.logicalItemId,
                    "Item was deleted remotely while local draft existed.",
                )
            } returns marked

            val result = harness.target()
            if (marked) {
                assertEquals(1, (result as PushLocalVaultChangesResult.Success).conflictCount)
            } else {
                assertEquals(
                    PushLocalVaultChangesResult.Error(
                        PushLocalVaultChangesError.LocalStateUpdateFailed(
                            harness.draft.logicalItemId,
                            "UPDATE_REMOTE_DELETE_RESOLUTION",
                        ),
                    ),
                    result,
                )
            }
        }
    }

    @Test
    fun `delete item not found resolves only when local tombstone can be finalized`() = runBlocking {
        listOf(true, false).forEach { resolved ->
            val harness = harness(SecureItemDraftType.DELETE)
            harness.remoteReturns(SecureItemRemoteError.ItemNotFound)
            coEvery {
                harness.coordinator.resolveAlreadyDeletedDraft(harness.draft)
            } returns resolved

            val result = harness.target()
            if (resolved) {
                assertEquals(1, (result as PushLocalVaultChangesResult.Success).syncedCount)
            } else {
                assertEquals(
                    PushLocalVaultChangesResult.Error(
                        PushLocalVaultChangesError.LocalStateUpdateFailed(
                            logicalItemId = harness.draft.logicalItemId,
                            operation = "DELETE_ALREADY_DELETED_RESOLUTION",
                        ),
                    ),
                    result,
                )
            }
        }
    }

    @Test
    fun `stale update handles missing remote snapshot and local persistence failures`() = runBlocking {
        val fetchFailure = harness(SecureItemDraftType.UPDATE)
        fetchFailure.remoteReturns(SecureItemRemoteError.PreconditionFailed)
        coEvery {
            fetchFailure.remote.getVaultItem(requireNotNull(fetchFailure.draft.remoteItemId))
        } returns SecureItemRemoteResult.Error(SecureItemRemoteError.NetworkError(IOException()))
        assertEquals(
            PushLocalVaultChangesResult.Error(
                PushLocalVaultChangesError.RemoteFailure(
                    logicalItemId = fetchFailure.draft.logicalItemId,
                    operation = "UPDATE_CONFLICT_RECONCILIATION",
                    failure = NetworkFailureClassifier.fromThrowable(IOException()),
                ),
            ),
            fetchFailure.target(),
        )

        val markFailure = harness(SecureItemDraftType.UPDATE)
        markFailure.remoteReturns(SecureItemRemoteError.PreconditionFailed)
        coEvery {
            markFailure.remote.getVaultItem(requireNotNull(markFailure.draft.remoteItemId))
        } returns SecureItemRemoteResult.Error(SecureItemRemoteError.ItemNotFound)
        coEvery {
            markFailure.coordinator.markDraftConflict(any(), any())
        } returns false
        assertEquals(
            PushLocalVaultChangesResult.Error(
                PushLocalVaultChangesError.LocalStateUpdateFailed(
                    markFailure.draft.logicalItemId,
                    "UPDATE_REMOTE_DELETE_RESOLUTION",
                ),
            ),
            markFailure.target(),
        )

        val replaceFailure = conflictHarness(SecureItemDraftType.UPDATE, replacementSucceeds = false)
        assertEquals(
            PushLocalVaultChangesResult.Error(
                PushLocalVaultChangesError.LocalStateUpdateFailed(
                    replaceFailure.draft.logicalItemId,
                    "UPDATE_CONFLICT_RESOLUTION",
                ),
            ),
            replaceFailure.target(),
        )
    }

    @Test
    fun `stale delete stores conflict or reports each local resolution failure`() = runBlocking {
        val success = conflictHarness(SecureItemDraftType.DELETE, replacementSucceeds = true)
        assertEquals(1, (success.target() as PushLocalVaultChangesResult.Success).conflictCount)

        val replaceFailure = conflictHarness(SecureItemDraftType.DELETE, replacementSucceeds = false)
        assertEquals(
            PushLocalVaultChangesResult.Error(
                PushLocalVaultChangesError.LocalStateUpdateFailed(
                    replaceFailure.draft.logicalItemId,
                    "DELETE_CONFLICT_RESOLUTION",
                ),
            ),
            replaceFailure.target(),
        )

        val fetchFailure = harness(SecureItemDraftType.DELETE)
        fetchFailure.remoteReturns(SecureItemRemoteError.PreconditionFailed)
        coEvery {
            fetchFailure.remote.getVaultItem(requireNotNull(fetchFailure.draft.remoteItemId))
        } returns SecureItemRemoteResult.Error(SecureItemRemoteError.ItemNotFound)
        coEvery { fetchFailure.coordinator.markDraftConflict(any(), any()) } returns false
        assertEquals(
            PushLocalVaultChangesResult.Error(
                PushLocalVaultChangesError.LocalStateUpdateFailed(
                    fetchFailure.draft.logicalItemId,
                    "DELETE_CONFLICT",
                ),
            ),
            fetchFailure.target(),
        )
    }

    private fun harness(
        type: SecureItemDraftType,
        remoteItemId: UUID? = if (type == SecureItemDraftType.CREATE) null else UUID.randomUUID(),
    ): Harness {
        val draftRepository = mockk<SecureItemDraftRepository>()
        val remoteRepository = mockk<SecureItemRemoteRepository>()
        val coordinator = mockk<SecureItemDraftSyncCoordinator>()
        val draft = testSecureItemDraft(
            remoteItemId = remoteItemId,
            draftType = type,
            deletedAt = NOW.takeIf { type == SecureItemDraftType.DELETE },
        )
        coEvery { draftRepository.getSyncableDraftsOrdered() } returns listOf(draft)
        return Harness(
            draft = draft,
            remote = remoteRepository,
            coordinator = coordinator,
            target = PushLocalVaultChangesUseCase(draftRepository, remoteRepository, coordinator),
        )
    }

    private fun conflictHarness(
        type: SecureItemDraftType,
        replacementSucceeds: Boolean,
    ): Harness {
        val harness = harness(type)
        harness.remoteReturns(SecureItemRemoteError.PreconditionFailed)
        val remote = remoteItem(harness.draft)
        coEvery {
            harness.remote.getVaultItem(requireNotNull(harness.draft.remoteItemId))
        } returns SecureItemRemoteResult.Success(remote)
        coEvery {
            harness.coordinator.replaceOfficialWithRemoteAndConflictedDraft(
                draft = harness.draft,
                remoteItem = any(),
                lastSyncedAt = remote.updatedAt,
                lastSyncError = any(),
            )
        } returns replacementSucceeds
        return harness
    }

    private fun remoteOperation(type: SecureItemDraftType): String = when (type) {
        SecureItemDraftType.CREATE -> "CREATE_REMOTE"
        SecureItemDraftType.UPDATE -> "UPDATE_REMOTE"
        SecureItemDraftType.DELETE -> "DELETE_REMOTE"
    }

    private fun Harness.remoteReturns(error: SecureItemRemoteError) {
        when (draft.draftType) {
            SecureItemDraftType.CREATE -> coEvery {
                remote.createVaultItem(any())
            } returns SecureItemRemoteResult.Error(error)

            SecureItemDraftType.UPDATE -> coEvery {
                remote.updateVaultItem(requireNotNull(draft.remoteItemId), any())
            } returns SecureItemRemoteResult.Error(error)

            SecureItemDraftType.DELETE -> coEvery {
                remote.deleteVaultItem(requireNotNull(draft.remoteItemId), any())
            } returns SecureItemRemoteResult.Error(error)
        }
    }

    private suspend fun assertProtocolFailure(
        result: PushLocalVaultChangesResult,
        draft: com.miguelrodriguez19.safecube.core.vault.domain.model.secureitem.SecureItemSyncDraft,
        operation: String,
    ) {
        assertEquals(
            PushLocalVaultChangesResult.Error(
                PushLocalVaultChangesError.ProtocolIntegrityFailed(
                    logicalItemId = draft.logicalItemId,
                    operation = operation,
                ),
            ),
            result,
        )
    }

    private fun createResult(
        draft: com.miguelrodriguez19.safecube.core.vault.domain.model.secureitem.SecureItemSyncDraft,
    ) = RemoteCreateSecureItemResult(
        itemId = UUID.randomUUID(),
        mutationId = draft.mutationId,
        payloadVersion = draft.payloadVersion,
        itemRevision = 1,
        changeSequence = 10,
        updatedAt = NOW,
    )

    private fun updateResult(
        draft: com.miguelrodriguez19.safecube.core.vault.domain.model.secureitem.SecureItemSyncDraft,
    ) = RemoteUpdateSecureItemResult(
        itemId = requireNotNull(draft.remoteItemId),
        mutationId = draft.mutationId,
        payloadVersion = draft.payloadVersion,
        itemRevision = 2,
        changeSequence = 11,
        updatedAt = NOW,
    )

    private fun deleteResult(
        draft: com.miguelrodriguez19.safecube.core.vault.domain.model.secureitem.SecureItemSyncDraft,
    ) = RemoteDeleteSecureItemResult(
        itemId = requireNotNull(draft.remoteItemId),
        mutationId = draft.mutationId,
        payloadVersion = draft.payloadVersion,
        itemRevision = 2,
        changeSequence = 11,
        deletedAt = NOW,
    )

    private fun remoteItem(
        draft: com.miguelrodriguez19.safecube.core.vault.domain.model.secureitem.SecureItemSyncDraft,
    ) = RemoteSecureItem(
        itemId = requireNotNull(draft.remoteItemId),
        itemType = draft.itemType.wireName,
        schemaVersion = draft.schemaVersion,
        displayHint = "Remote",
        payload = byteArrayOf(9),
        payloadVersion = draft.payloadVersion + 1,
        itemRevision = 3,
        changeSequence = 20,
        updatedAt = NOW,
        deletedAt = null,
    )

    private data class Harness(
        val draft: com.miguelrodriguez19.safecube.core.vault.domain.model.secureitem.SecureItemSyncDraft,
        val remote: SecureItemRemoteRepository,
        val coordinator: SecureItemDraftSyncCoordinator,
        val target: PushLocalVaultChangesUseCase,
    )

    private companion object {
        val NOW: Instant = Instant.parse("2024-03-01T02:00:00Z")
    }
}
