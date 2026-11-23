package com.miguelrodriguez19.safecube

import androidx.activity.ComponentActivity
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.SemanticsActions
import androidx.compose.ui.semantics.SemanticsProperties
import androidx.compose.ui.semantics.getOrNull
import androidx.compose.ui.test.SemanticsMatcher
import androidx.compose.ui.test.assertCountEquals
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsEnabled
import androidx.compose.ui.test.assertIsFocused
import androidx.compose.ui.test.assertIsNotEnabled
import androidx.compose.ui.test.hasSetTextAction
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollTo
import androidx.compose.ui.test.performSemanticsAction
import androidx.compose.ui.test.performTextInput
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.uiautomator.UiDevice
import com.miguelrodriguez19.safecube.core.ui.R as UiR
import com.miguelrodriguez19.safecube.core.ui.theme.AppearanceMode
import com.miguelrodriguez19.safecube.core.ui.theme.AppearanceSettings
import com.miguelrodriguez19.safecube.core.ui.theme.SafeCubeTheme
import com.miguelrodriguez19.safecube.core.vault.domain.session.VaultLockReason
import com.miguelrodriguez19.safecube.feature.auth.R as AuthR
import com.miguelrodriguez19.safecube.feature.auth.presentation.gate.ui.PostLoginGateScreen
import com.miguelrodriguez19.safecube.feature.vault.R as VaultR
import com.miguelrodriguez19.safecube.feature.vault.presentation.create.action.CreateVaultUiAction
import com.miguelrodriguez19.safecube.feature.vault.presentation.create.state.CreateVaultUiState
import com.miguelrodriguez19.safecube.feature.vault.presentation.quickunlock.QuickUnlockPromptOperation
import com.miguelrodriguez19.safecube.feature.vault.presentation.quickunlock.QuickUnlockPromptRequest
import com.miguelrodriguez19.safecube.feature.vault.presentation.recovery.action.RecoveryKeyUiAction
import com.miguelrodriguez19.safecube.feature.vault.presentation.recovery.state.RecoveryKeyUiState
import com.miguelrodriguez19.safecube.feature.vault.presentation.state.VaultUiOperationState
import com.miguelrodriguez19.safecube.feature.vault.presentation.unlock.action.UnlockVaultUiAction
import com.miguelrodriguez19.safecube.feature.vault.presentation.unlock.state.UnlockVaultUiState
import com.miguelrodriguez19.safecube.feature.vault.testsupport.VaultCreateContent
import com.miguelrodriguez19.safecube.feature.vault.testsupport.VaultRecoveryContent
import com.miguelrodriguez19.safecube.feature.vault.testsupport.VaultUnlockContent
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class VaultAccessSemanticsTest {
    @get:Rule
    val composeRule = createAndroidComposeRule<ComponentActivity>()

    @Test
    fun gateLoadingExposesProgressWithoutRetryAction() {
        var retries = 0

        setThemedContent {
            PostLoginGateScreen(
                isLoading = true,
                messageRes = UiR.string.vault_bootstrap_loading,
                onRetry = { retries++ },
            )
        }

        assertPane(AuthR.string.vault_access_gate_title)
        composeRule.onNodeWithText(targetContext().getString(UiR.string.vault_bootstrap_loading))
            .assertIsDisplayed()
        composeRule.onNodeWithText(targetContext().getString(UiR.string.retry))
            .assertDoesNotExist()
        composeRule.onNodeWithText(
            targetContext().getString(UiR.string.design_system_feedback_retry),
        ).assertDoesNotExist()
        assertEquals(0, retries)
    }

    @Test
    fun gateRetryableFailureDispatchesOneRetry() {
        var retries = 0

        setThemedContent {
            PostLoginGateScreen(
                isLoading = false,
                messageRes = UiR.string.vault_bootstrap_retryable_error,
                onRetry = { retries++ },
            )
        }

        composeRule.onNodeWithText(
            targetContext().getString(UiR.string.design_system_feedback_retry),
        ).assertIsEnabled().performClick()

        assertEquals(1, retries)
    }

    @Test
    fun gateTerminalFailureKeepsExplicitRetryAndDispatchesOnce() {
        var retries = 0

        setThemedContent {
            PostLoginGateScreen(
                isLoading = false,
                messageRes = UiR.string.vault_bootstrap_terminal_error,
                onRetry = { retries++ },
            )
        }

        composeRule.onNodeWithText(targetContext().getString(UiR.string.retry))
            .assertIsDisplayed()
            .performClick()

        assertEquals(1, retries)
    }

    @Test
    fun createEntryPaneLabelsAndMasksPassphraseAndSubmitsOnce() {
        val actions = mutableListOf<CreateVaultUiAction>()

        setThemedContent {
            VaultCreateContent(
                uiState = CreateVaultUiState(passphrase = TEST_NOT_A_SECRET),
                onAction = actions::add,
            )
        }

        assertPane(VaultR.string.vault_access_create_title)
        assertSecretField(
            label = VaultR.string.vault_access_create_passphrase_label,
            marker = TEST_NOT_A_SECRET,
        )
        composeRule.onNodeWithText(targetContext().getString(VaultR.string.vault_access_create_action))
            .performClick()

        assertEquals(listOf(CreateVaultUiAction.Submit), actions)
    }

    @Test
    fun createLoadingDisablesSubmitWithoutDispatchingAction() {
        val actions = mutableListOf<CreateVaultUiAction>()

        setThemedContent {
            VaultCreateContent(
                uiState = CreateVaultUiState(operationState = VaultUiOperationState.Loading),
                onAction = actions::add,
            )
        }

        composeRule.onNodeWithText(
            targetContext().getString(VaultR.string.vault_access_create_loading),
        ).assertIsNotEnabled().performSemanticsAction(SemanticsActions.OnClick) { it() }

        assertTrue(actions.isEmpty())
    }

    @Test
    fun recoveryKeyRemainsHiddenAcrossConfirmationAndContinue() {
        val actions = mutableListOf<RecoveryKeyUiAction>()

        setThemedContent {
            var state by remember {
                mutableStateOf(
                    RecoveryKeyUiState(
                        recoveryKey = DEMO_NOT_A_RECOVERY_KEY,
                        operationState = VaultUiOperationState.Success,
                    ),
                )
            }
            VaultRecoveryContent(
                uiState = state,
                onAction = { action ->
                    actions += action
                    if (action is RecoveryKeyUiAction.ConfirmationChanged) {
                        state = state.copy(isConfirmed = action.isConfirmed)
                    }
                },
            )
        }

        assertPane(VaultR.string.vault_access_recovery_title)
        assertSecretHidden(DEMO_NOT_A_RECOVERY_KEY)
        composeRule.onNodeWithText(
            targetContext().getString(VaultR.string.vault_access_recovery_confirmation),
        ).assertIsDisplayed()
        composeRule.onNodeWithText(
            targetContext().getString(VaultR.string.vault_access_recovery_continue),
        ).assertIsNotEnabled()

        composeRule.onNode(
            SemanticsMatcher.expectValue(SemanticsProperties.Role, Role.Checkbox),
        ).assertIsDisplayed().performClick()
        composeRule.onNodeWithText(
            targetContext().getString(VaultR.string.vault_access_recovery_continue),
        ).assertIsEnabled().performClick()

        assertEquals(
            listOf(
                RecoveryKeyUiAction.ConfirmationChanged(true),
                RecoveryKeyUiAction.Continue,
            ),
            actions,
        )
        assertSecretHidden(DEMO_NOT_A_RECOVERY_KEY)
    }

    @Test
    fun recoveryConfirmationFailureShowsRetryAndKeepsKeyOutOfSemantics() {
        val actions = mutableListOf<RecoveryKeyUiAction>()

        setThemedContent {
            VaultRecoveryContent(
                uiState = RecoveryKeyUiState(
                    recoveryKey = DEMO_NOT_A_RECOVERY_KEY,
                    operationState = VaultUiOperationState.RetryableError,
                    isConfirmed = true,
                    errorMessageRes = UiR.string.vault_recovery_confirmation_failed,
                ),
                onAction = actions::add,
            )
        }

        composeRule.onNodeWithText(
            targetContext().getString(UiR.string.vault_recovery_confirmation_failed),
        ).assertIsDisplayed()
        assertSecretHidden(DEMO_NOT_A_RECOVERY_KEY)
        composeRule.onNodeWithText(
            targetContext().getString(UiR.string.design_system_feedback_retry),
        ).performClick()

        assertEquals(listOf(RecoveryKeyUiAction.Retry), actions)
    }

    @Test
    fun unlockErrorCorrectionKeepsFocusAndMaskedValueAndSubmitsOnce() {
        val actions = mutableListOf<UnlockVaultUiAction>()
        val passphraseField = hasSetTextAction()

        setThemedContent {
            var state by remember {
                mutableStateOf(
                    UnlockVaultUiState(
                        passphraseErrorRes = VaultR.string.vault_access_unlock_passphrase_required,
                    ),
                )
            }
            VaultUnlockContent(
                uiState = state,
                focusEffectsEnabled = true,
                onAction = { action ->
                    actions += action
                    if (action is UnlockVaultUiAction.PassphraseChanged) {
                        state = state.copy(
                            passphrase = action.value,
                            passphraseErrorRes = null,
                        )
                    }
                },
            )
        }

        assertPane(VaultR.string.vault_access_unlock_title)
        composeRule.onNodeWithText(
            targetContext().getString(VaultR.string.vault_access_unlock_field_label),
        ).assertIsDisplayed()
        composeRule.waitUntil(timeoutMillis = 5_000) {
            composeRule.onNode(passphraseField, useUnmergedTree = true)
                .fetchSemanticsNode().config.getOrNull(SemanticsProperties.Focused) == true
        }
        composeRule.onNode(passphraseField, useUnmergedTree = true)
            .assertIsFocused()
            .performTextInput(TEST_NOT_A_SECRET)
        composeRule.waitForIdle()
        composeRule.onNode(passphraseField, useUnmergedTree = true).assertIsFocused()
        assertSecretHidden(TEST_NOT_A_SECRET)
        composeRule.onNodeWithText(
            targetContext().getString(VaultR.string.vault_access_unlock_action),
        ).performClick()

        assertEquals(
            listOf(
                UnlockVaultUiAction.PassphraseChanged(TEST_NOT_A_SECRET),
                UnlockVaultUiAction.Submit,
            ),
            actions,
        )
    }

    @Test
    fun unlockLoadingDisablesSubmitWithoutDispatchingAction() {
        val actions = mutableListOf<UnlockVaultUiAction>()

        setThemedContent {
            VaultUnlockContent(
                uiState = UnlockVaultUiState(operationState = VaultUiOperationState.Loading),
                onAction = actions::add,
            )
        }

        composeRule.onNodeWithText(
            targetContext().getString(VaultR.string.vault_access_unlock_loading),
        ).assertIsNotEnabled().performSemanticsAction(SemanticsActions.OnClick) { it() }

        assertTrue(actions.isEmpty())
    }

    @Test
    fun quickUnlockOfferIgnoresBackAndOutsideAndDeclineButtonDispatchesOnce() {
        val actions = mutableListOf<UnlockVaultUiAction>()
        val offerTitle = targetContext().getString(
            VaultR.string.vault_access_unlock_quick_unlock_offer_title,
        )

        setThemedContent {
            VaultUnlockContent(
                uiState = UnlockVaultUiState(showQuickUnlockOffer = true),
                onAction = actions::add,
            )
        }

        UiDevice.getInstance(InstrumentationRegistry.getInstrumentation()).pressBack()
        composeRule.onNodeWithText(offerTitle).assertIsDisplayed()
        assertTrue(actions.isEmpty())
        tapOutsideDialog()
        composeRule.onNodeWithText(offerTitle).assertIsDisplayed()
        assertTrue(actions.isEmpty())
        composeRule.onNodeWithText(
            targetContext().getString(VaultR.string.vault_access_unlock_quick_unlock_not_now),
        ).performClick()

        assertEquals(listOf(UnlockVaultUiAction.DeclineQuickUnlock), actions)
    }

    @Test
    fun quickUnlockOfferEnableButtonDispatchesOnce() {
        val actions = mutableListOf<UnlockVaultUiAction>()

        setThemedContent {
            VaultUnlockContent(
                uiState = UnlockVaultUiState(showQuickUnlockOffer = true),
                onAction = actions::add,
            )
        }

        composeRule.onNodeWithText(
            targetContext().getString(VaultR.string.vault_access_unlock_quick_unlock_enable),
        ).performClick()

        assertEquals(listOf(UnlockVaultUiAction.EnableQuickUnlock), actions)
    }

    @Test
    fun quickUnlockFallbackKeepsPassphraseEntryAndOffersOneRetryAction() {
        val actions = mutableListOf<UnlockVaultUiAction>()

        setThemedContent {
            VaultUnlockContent(
                uiState = UnlockVaultUiState(
                    errorMessageRes = UiR.string.quick_unlock_error,
                    hasQuickUnlockEnrollment = true,
                    canRetryQuickUnlock = true,
                ),
                onAction = actions::add,
            )
        }

        assertPane(VaultR.string.vault_access_unlock_title)
        assertSecretField(
            label = VaultR.string.vault_access_unlock_field_label,
            marker = null,
        )
        composeRule.onNodeWithText(targetContext().getString(UiR.string.quick_unlock_error))
            .assertIsDisplayed()
        composeRule.onNodeWithText(
            targetContext().getString(VaultR.string.vault_access_unlock_quick_unlock_retry),
        ).performClick()

        assertEquals(listOf(UnlockVaultUiAction.RetryQuickUnlock), actions)
    }

    @Test
    fun bothLockNoticesHaveNoCancelAndAcknowledgeOnce() {
        val actions = mutableListOf<UnlockVaultUiAction>()
        val firstReason = VaultLockReason.RemotePassphraseChanged
        val secondReason = VaultLockReason.PassphraseChangeReconciliationRequired

        setThemedContent {
            var lockReason by remember { mutableStateOf(firstReason) }
            VaultUnlockContent(
                uiState = UnlockVaultUiState(lockReason = lockReason),
                onAction = { action ->
                    actions += action
                    if (action == UnlockVaultUiAction.DismissLockNotice) {
                        lockReason = when (lockReason) {
                            VaultLockReason.RemotePassphraseChanged -> secondReason
                            VaultLockReason.PassphraseChangeReconciliationRequired -> firstReason
                        }
                    }
                },
            )
        }

        val understood = targetContext().getString(VaultR.string.vault_access_unlock_understood)
        composeRule.onNodeWithText(targetContext().getString(UiR.string.vault_lock_remote_passphrase_changed_title))
            .assertIsDisplayed()
        composeRule.onNodeWithText(targetContext().getString(UiR.string.action_cancel))
            .assertDoesNotExist()
        composeRule.onNodeWithText(understood).performClick()
        assertEquals(listOf(UnlockVaultUiAction.DismissLockNotice), actions)

        composeRule.onNodeWithText(
            targetContext().getString(UiR.string.vault_lock_passphrase_reconciliation_title),
        ).assertIsDisplayed()
        composeRule.onNodeWithText(targetContext().getString(UiR.string.action_cancel))
            .assertDoesNotExist()
        composeRule.onNodeWithText(understood).performClick()
        assertEquals(
            listOf(
                UnlockVaultUiAction.DismissLockNotice,
                UnlockVaultUiAction.DismissLockNotice,
            ),
            actions,
        )
    }

    @Test
    fun recoveryWarningsAndActionsRemainReachableInSpanishAtTwoHundredPercent() {
        val configuration = android.content.res.Configuration(targetContext().resources.configuration).apply {
            setLocale(java.util.Locale.forLanguageTag("es"))
            fontScale = 2f
        }
        val spanishContext = targetContext().createConfigurationContext(configuration)
        val actions = mutableListOf<RecoveryKeyUiAction>()
        composeRule.setContent {
            val density = androidx.compose.ui.platform.LocalDensity.current.density
            androidx.compose.runtime.CompositionLocalProvider(
                androidx.compose.ui.platform.LocalContext provides spanishContext,
                androidx.compose.ui.platform.LocalConfiguration provides configuration,
                androidx.compose.ui.platform.LocalDensity provides androidx.compose.ui.unit.Density(density, 2f),
            ) {
                SafeCubeTheme(appearance = AppearanceSettings(mode = AppearanceMode.Dark)) {
                    VaultRecoveryContent(
                        uiState = RecoveryKeyUiState(
                            recoveryKey = DEMO_NOT_A_RECOVERY_KEY,
                            operationState = VaultUiOperationState.Success,
                            isConfirmed = true,
                        ),
                        onAction = actions::add,
                    )
                }
            }
        }
        composeRule.onNodeWithText(spanishContext.getString(VaultR.string.vault_access_recovery_warning_message))
            .performScrollTo().assertIsDisplayed()
        composeRule.onNodeWithText(spanishContext.getString(UiR.string.recovery_key_m142_show))
            .performScrollTo().assertIsDisplayed().assertIsEnabled()
        assertSecretHidden(DEMO_NOT_A_RECOVERY_KEY)
        composeRule.onNodeWithText(spanishContext.getString(VaultR.string.vault_access_recovery_confirmation))
            .performScrollTo().assertIsDisplayed()
        composeRule.onNodeWithText(spanishContext.getString(VaultR.string.vault_access_recovery_continue))
            .performScrollTo().assertIsEnabled().performClick()
        assertEquals(listOf(RecoveryKeyUiAction.Continue), actions)
    }

    private fun assertSecretField(label: Int, marker: String?) {
        composeRule.onNodeWithText(targetContext().getString(label)).assertIsDisplayed()
        composeRule.onNode(hasSetTextAction()).assertExists()
        composeRule.onNode(
            SemanticsMatcher.keyIsDefined(SemanticsProperties.Password),
            useUnmergedTree = true,
        ).assertExists()
        marker?.let(::assertSecretHidden)
    }

    private fun assertSecretHidden(marker: String) {
        val plaintext = containsPlaintext(marker)
        composeRule.onAllNodes(plaintext).assertCountEquals(0)
        composeRule.onAllNodes(plaintext, useUnmergedTree = true).assertCountEquals(0)
    }

    private fun containsPlaintext(value: String) = SemanticsMatcher("secret plaintext") { node ->
        val config = node.config
        config.getOrNull(SemanticsProperties.EditableText).toString().contains(value) ||
            config.getOrNull(SemanticsProperties.InputText).toString().contains(value) ||
            config.getOrNull(SemanticsProperties.Text).toString().contains(value)
    }

    private fun assertPane(titleRes: Int) {
        val expected = targetContext().getString(titleRes)
        composeRule.onNode(
            SemanticsMatcher.expectValue(SemanticsProperties.PaneTitle, expected),
        ).assertExists()
        composeRule.onNode(
            androidx.compose.ui.test.hasText(expected) and
                SemanticsMatcher.keyIsDefined(SemanticsProperties.Heading),
        ).assertExists()
    }

    private fun setThemedContent(content: @Composable () -> Unit) {
        composeRule.setContent {
            SafeCubeTheme(
                appearance = AppearanceSettings(
                    mode = AppearanceMode.Light,
                    dynamicColorEnabled = false,
                ),
                content = content,
            )
        }
    }

    private fun tapOutsideDialog() {
        val device = UiDevice.getInstance(InstrumentationRegistry.getInstrumentation())
        device.click(device.displayWidth / 2, device.displayHeight / 12)
        composeRule.waitForIdle()
    }

    private fun targetContext() =
        InstrumentationRegistry.getInstrumentation().targetContext

    private companion object {
        const val TEST_NOT_A_SECRET = "TEST-NOT-A-SECRET"
        const val DEMO_NOT_A_RECOVERY_KEY = "DEMO-NOT-A-RECOVERY-KEY"
    }
}
