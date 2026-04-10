package com.miguelrodriguez19.safecube.feature.vault.presentation.home.viewmodel

import com.miguelrodriguez19.safecube.core.vault.domain.model.secureitem.SecureItemType
import com.miguelrodriguez19.safecube.core.vault.domain.model.secureitem.crud.VaultItemSummary
import com.miguelrodriguez19.safecube.core.vault.domain.usecase.secureitem.ObserveVaultItemSummariesUseCase
import com.miguelrodriguez19.safecube.feature.vault.test.MainDispatcherRule
import io.mockk.confirmVerified
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import java.time.Instant
import java.util.UUID

@OptIn(ExperimentalCoroutinesApi::class)
class VaultHomeViewModelTest {
    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val observeVaultItemSummariesUseCase = mockk<ObserveVaultItemSummariesUseCase>()
    private val summariesFlow = MutableStateFlow<List<VaultItemSummary>>(emptyList())

    private val target = VaultHomeViewModel(
        observeVaultItemSummariesUseCase = observeVaultItemSummariesUseCase,
    )

    @Test
    fun `init when summaries flow emits then exposes local vault items`() = runTest {
        every { observeVaultItemSummariesUseCase.invoke() } returns summariesFlow

        summariesFlow.value = listOf(
            VaultItemSummary(
                logicalItemId = UUID.randomUUID(),
                itemType = SecureItemType.PASSWORD,
                displayHint = "Github",
                updatedAt = Instant.now(),
            ),
        )

        advanceUntilIdle()

        assertEquals(1, target.uiState.value.items.size)
        assertEquals("Github", target.uiState.value.items.first().displayHint)
        assertEquals(SecureItemType.PASSWORD, target.uiState.value.items.first().itemType)
        verify(exactly = 1) { observeVaultItemSummariesUseCase.invoke() }
        confirmVerified(observeVaultItemSummariesUseCase)
    }
}

