package com.miguelrodriguez19.safecube

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsEnabled
import androidx.compose.ui.test.assertIsNotEnabled
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import com.miguelrodriguez19.safecube.app.presentation.theme.SafecubeandroidTheme
import com.miguelrodriguez19.safecube.core.network.domain.model.NetworkFailureClassifier
import com.miguelrodriguez19.safecube.core.vault.domain.model.remote.result.SecureItemRemoteError
import com.miguelrodriguez19.safecube.core.vault.domain.model.secureitem.SecureItemType
import com.miguelrodriguez19.safecube.core.vault.domain.model.sync.VaultSyncError
import com.miguelrodriguez19.safecube.core.vault.domain.model.sync.VaultSyncResult
import com.miguelrodriguez19.safecube.core.vault.domain.model.sync.pull.PullVaultDeltaError
import com.miguelrodriguez19.safecube.feature.vault.presentation.home.state.VaultHomeContentState
import com.miguelrodriguez19.safecube.feature.vault.presentation.home.state.VaultHomeUiState
import com.miguelrodriguez19.safecube.feature.vault.presentation.home.state.VaultItemSummaryUiModel
import com.miguelrodriguez19.safecube.feature.vault.testsupport.Phase7VaultHomeContent
import com.miguelrodriguez19.safecube.feature.vault.presentation.recovery.action.RecoveryKeyUiAction
import com.miguelrodriguez19.safecube.feature.vault.presentation.recovery.state.RecoveryKeyUiState
import com.miguelrodriguez19.safecube.feature.vault.testsupport.Phase7RecoveryKeyContent
import com.miguelrodriguez19.safecube.feature.vault.presentation.state.VaultUiOperationState
import java.time.Instant
import java.util.UUID
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@LargeTest
@RunWith(AndroidJUnit4::class)
class Phase7ResilienceUiStateTest {
    @get:Rule
    val composeRule = createComposeRule()

    @Test
    fun pendingRecoveryKeyRemainsVisibleUntilExplicitConfirmation() {
        val recoveryKey = "test-recovery-key"
        val actions = mutableListOf<RecoveryKeyUiAction>()

        composeRule.setContent {
            SafecubeandroidTheme(dynamicColor = false) {
                Phase7RecoveryKeyContent(
                    uiState = RecoveryKeyUiState(
                        recoveryKey = recoveryKey,
                        operationState = VaultUiOperationState.Success,
                    ),
                    onAction = actions::add,
                )
            }
        }

        composeRule.onNodeWithText(recoveryKey).assertIsDisplayed()
        composeRule.onNodeWithText("Continue").assertIsDisplayed().assertIsNotEnabled()
    }

    @Test
    fun retryableVaultHomeErrorKeepsLocalContentAndOffersRetry() {
        val displayHint = "Local item preserved"
        val retryableError = VaultSyncError.PullFailed(
            PullVaultDeltaError.RemoteListFailed(
                SecureItemRemoteError.HttpError(
                    NetworkFailureClassifier.fromHttpStatus(503),
                ),
            ),
        )

        composeRule.setContent {
            SafecubeandroidTheme(dynamicColor = false) {
                Phase7VaultHomeContent(
                    uiState = VaultHomeUiState(
                        items = listOf(
                            VaultItemSummaryUiModel(
                                logicalItemId = UUID.randomUUID(),
                                displayHint = displayHint,
                                itemType = SecureItemType.PASSWORD,
                                updatedAt = Instant.EPOCH,
                                hasDraft = false,
                                draftType = null,
                                draftSyncStatus = null,
                                lastDraftError = null,
                            ),
                        ),
                        contentState = VaultHomeContentState.Content,
                        lastSyncResult = VaultSyncResult.Error(retryableError),
                        lastSyncError = retryableError,
                    ),
                    onCreatePassword = {},
                    onCreateNote = {},
                    onEditPassword = {},
                    onEditNote = {},
                    onVault = {},
                    onSettings = {},
                    onSyncNow = {},
                )
            }
        }

        composeRule.onNodeWithText(displayHint).assertIsDisplayed()
        composeRule.onNodeWithText("Retry").assertIsDisplayed().assertIsEnabled()
        composeRule
            .onNodeWithText("Sync failed: The sync service is temporarily unavailable.")
            .assertIsDisplayed()
    }
}
