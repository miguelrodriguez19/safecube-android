package com.miguelrodriguez19.safecube.feature.vault.presentation.settings.viewmodel

import com.miguelrodriguez19.safecube.core.vault.domain.model.secureitem.SecureItemDraftSyncStatus
import com.miguelrodriguez19.safecube.core.vault.domain.model.secureitem.SecureItemDraftType
import com.miguelrodriguez19.safecube.core.vault.domain.model.secureitem.SecureItemType
import com.miguelrodriguez19.safecube.core.vault.domain.model.secureitem.crud.VaultItemDraftSummary
import com.miguelrodriguez19.safecube.core.vault.domain.usecase.secureitem.ObserveVaultDraftSummariesUseCase
import io.mockk.every
import io.mockk.mockk
import java.time.Instant
import java.util.UUID
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import kotlinx.coroutines.test.resetMain
import org.junit.After
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class SettingsViewModelTest {
    private val dispatcher = UnconfinedTestDispatcher()
    private val summaries = MutableStateFlow(emptyList<VaultItemDraftSummary>())
    private val observeVaultDraftSummariesUseCase =
        mockk<ObserveVaultDraftSummariesUseCase>()

    @Before
    fun setUp() {
        kotlinx.coroutines.Dispatchers.setMain(dispatcher)
        every { observeVaultDraftSummariesUseCase() } returns summaries
    }

    @After
    fun tearDown() {
        kotlinx.coroutines.Dispatchers.resetMain()
    }

    @Test
    fun `active draft enables destructive logout warning`() = runTest {
        val target = SettingsViewModel(observeVaultDraftSummariesUseCase)

        assertFalse(requireNotNull(target.hasActiveDrafts.first { it != null }))
        summaries.value = listOf(draftSummary())
        assertTrue(requireNotNull(target.hasActiveDrafts.first { it == true }))
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
