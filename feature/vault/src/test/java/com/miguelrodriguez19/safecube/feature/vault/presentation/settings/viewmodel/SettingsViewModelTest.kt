package com.miguelrodriguez19.safecube.feature.vault.presentation.settings.viewmodel

import com.miguelrodriguez19.safecube.core.vault.domain.model.secureitem.SecureItemDraftSyncStatus
import com.miguelrodriguez19.safecube.core.vault.domain.model.secureitem.SecureItemDraftType
import com.miguelrodriguez19.safecube.core.vault.domain.model.secureitem.SecureItemType
import com.miguelrodriguez19.safecube.core.vault.domain.model.AutoLockTimeout
import com.miguelrodriguez19.safecube.core.vault.domain.quickunlock.QuickUnlockCleanupResult
import com.miguelrodriguez19.safecube.core.vault.domain.quickunlock.QuickUnlockEnrollmentPreparationResult
import com.miguelrodriguez19.safecube.core.vault.domain.quickunlock.QuickUnlockEnrollmentResult
import com.miguelrodriguez19.safecube.core.vault.domain.quickunlock.QuickUnlockOfferState
import com.miguelrodriguez19.safecube.core.vault.domain.model.secureitem.crud.VaultItemDraftSummary
import com.miguelrodriguez19.safecube.core.vault.domain.repository.AutoLockTimeoutRepository
import com.miguelrodriguez19.safecube.core.vault.domain.session.VaultSessionManager
import com.miguelrodriguez19.safecube.core.vault.domain.usecase.secureitem.ObserveVaultDraftSummariesUseCase
import com.miguelrodriguez19.safecube.feature.vault.presentation.quickunlock.PendingQuickUnlockEnrollment
import com.miguelrodriguez19.safecube.feature.vault.presentation.quickunlock.QuickUnlockPromptOperation
import com.miguelrodriguez19.safecube.feature.vault.presentation.quickunlock.QuickUnlockPromptRequest
import com.miguelrodriguez19.safecube.feature.vault.presentation.settings.event.SettingsUiEvent
import io.mockk.every
import io.mockk.mockk
import java.time.Instant
import java.util.UUID
import kotlinx.coroutines.async
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import kotlinx.coroutines.test.resetMain
import org.junit.After
import org.junit.Assert.assertFalse
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class SettingsViewModelTest {
    private val dispatcher = UnconfinedTestDispatcher()
    private val summaries = MutableStateFlow(emptyList<VaultItemDraftSummary>())
    private val observeVaultDraftSummariesUseCase =
        mockk<ObserveVaultDraftSummariesUseCase>()
    private val autoLockTimeoutRepository = mockk<AutoLockTimeoutRepository>()
    private val vaultSessionManager = mockk<VaultSessionManager>()
    private val autoLockTimeout = MutableStateFlow(AutoLockTimeout.Immediately)

    private fun target() = SettingsViewModel(
        observeVaultDraftSummariesUseCase = observeVaultDraftSummariesUseCase,
        autoLockTimeoutRepository = autoLockTimeoutRepository,
        vaultSessionManager = vaultSessionManager,
    )

    @Before
    fun setUp() {
        kotlinx.coroutines.Dispatchers.setMain(dispatcher)
        every { observeVaultDraftSummariesUseCase() } returns summaries
        every { autoLockTimeoutRepository.timeout } returns autoLockTimeout
        every { autoLockTimeoutRepository.setTimeout(any()) } answers {
            autoLockTimeout.value = firstArg()
        }
        every { vaultSessionManager.quickUnlockOfferState() } returns QuickUnlockOfferState.Available
    }

    @After
    fun tearDown() {
        kotlinx.coroutines.Dispatchers.resetMain()
        PendingQuickUnlockEnrollment.clear()
    }

    @Test
    fun `active draft enables destructive logout warning`() = runTest {
        val target = target()

        assertFalse(requireNotNull(target.hasActiveDrafts.first { it != null }))
        summaries.value = listOf(draftSummary())
        assertTrue(requireNotNull(target.hasActiveDrafts.first { it == true }))
    }

    @Test
    fun `changing auto lock timeout updates repository without being treated as inactivity`() = runTest {
        val target = target()

        target.setAutoLockTimeout(AutoLockTimeout.FifteenMinutes)

        io.mockk.verify { autoLockTimeoutRepository.setTimeout(AutoLockTimeout.FifteenMinutes) }
        assertEquals(AutoLockTimeout.FifteenMinutes, target.autoLockTimeout.value)
    }

    @Test
    fun `enable quick unlock emits enrollment prompt and exposes enrolled status after success`() = runTest {
        every { vaultSessionManager.prepareQuickUnlockEnrollment(true) } returns
            QuickUnlockEnrollmentPreparationResult.Ready("settings-enrollment")
        every { vaultSessionManager.finishQuickUnlockEnrollment("settings-enrollment") } returns
            QuickUnlockEnrollmentResult.Enrolled
        every { vaultSessionManager.quickUnlockOfferState() } returnsMany listOf(
            QuickUnlockOfferState.Available,
            QuickUnlockOfferState.Enrolled,
        )
        val target = target()
        val event = async { target.events.first() }

        target.enableQuickUnlock()

        assertEquals(
            SettingsUiEvent.LaunchQuickUnlockPrompt(
                QuickUnlockPromptRequest("settings-enrollment", QuickUnlockPromptOperation.Enrollment),
            ),
            event.await(),
        )
        target.onQuickUnlockPromptSucceeded("settings-enrollment")

        assertEquals(QuickUnlockOfferState.Enrolled, target.quickUnlockUiState.value.offerState)
    }

    @Test
    fun `disable quick unlock clears enrollment and refreshes status`() = runTest {
        every { vaultSessionManager.clearQuickUnlockEnrollment() } returns QuickUnlockCleanupResult.Cleared
        every { vaultSessionManager.quickUnlockOfferState() } returnsMany listOf(
            QuickUnlockOfferState.Enrolled,
            QuickUnlockOfferState.Seen,
        )
        val target = target()

        target.disableQuickUnlock()

        io.mockk.verify(exactly = 1) { vaultSessionManager.clearQuickUnlockEnrollment() }
        assertEquals(QuickUnlockOfferState.Seen, target.quickUnlockUiState.value.offerState)
    }

    @Test
    fun `enable requiring passphrase stores process local intent and locks vault`() = runTest {
        every { vaultSessionManager.prepareQuickUnlockEnrollment(true) } returns
            QuickUnlockEnrollmentPreparationResult.RequiresPassphrase
        every { vaultSessionManager.lock() } returns Unit
        val target = target()

        target.enableQuickUnlock()

        io.mockk.verify(exactly = 1) { vaultSessionManager.lock() }
        assertTrue(PendingQuickUnlockEnrollment.consume())
    }

    private fun draftSummary() = VaultItemDraftSummary(
        logicalItemId = UUID.randomUUID(),
        itemType = SecureItemType.NOTE,
        displayHint = "Draft",
        updatedAt = Instant.parse("2026-07-28T10:00:00Z"),
        draftType = SecureItemDraftType.UPDATE,
        draftSyncStatus = SecureItemDraftSyncStatus.READY_TO_SYNC,
        lastSyncError = null,
    )
}
