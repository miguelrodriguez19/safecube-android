package com.miguelrodriguez19.safecube

import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.SemanticsActions
import androidx.compose.ui.semantics.SemanticsProperties
import androidx.compose.ui.semantics.getOrNull
import androidx.compose.ui.test.SemanticsMatcher
import androidx.compose.ui.test.assert
import androidx.compose.ui.test.assertCountEquals
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsEnabled
import androidx.compose.ui.test.assertIsNotEnabled
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.junit4.StateRestorationTester
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollTo
import androidx.compose.ui.text.TextLayoutResult
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LifecycleRegistry
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import androidx.test.platform.app.InstrumentationRegistry
import com.miguelrodriguez19.safecube.core.ui.R as UiR
import com.miguelrodriguez19.safecube.core.ui.component.SafeCubeRecoveryKey
import com.miguelrodriguez19.safecube.core.ui.theme.AppearanceMode
import com.miguelrodriguez19.safecube.core.ui.theme.AppearanceSettings
import com.miguelrodriguez19.safecube.core.ui.theme.SafeCubeTheme
import com.miguelrodriguez19.safecube.feature.vault.presentation.recovery.action.RecoveryKeyUiAction
import com.miguelrodriguez19.safecube.feature.vault.presentation.recovery.state.RecoveryKeyUiState
import com.miguelrodriguez19.safecube.feature.vault.presentation.state.VaultUiOperationState
import com.miguelrodriguez19.safecube.feature.vault.testsupport.VaultRecoveryContent
import com.miguelrodriguez19.safecube.feature.vault.R as VaultR
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@LargeTest
@RunWith(AndroidJUnit4::class)
class VaultRecoveryPrivacyTest {
    @get:Rule
    val composeRule = createComposeRule()

    @Test
    fun recoveryKeyIsAbsentFromSemanticsUntilExplicitShowWhileResumed() {
        val owner = TestLifecycleOwner()

        setVaultRecoveryContent(owner)

        assertRecoveryKeyHidden()
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        val showLabel = context.getString(UiR.string.recovery_key_m142_show)
        val showButton = composeRule.onNodeWithText(showLabel)
        showButton.performScrollTo().assertIsDisplayed().assertIsNotEnabled()
        showButton.assert(
            SemanticsMatcher.expectValue(
                SemanticsProperties.StateDescription,
                context.getString(UiR.string.recovery_key_m142_hidden),
            ),
        )

        moveToResumed(owner)
        assertRecoveryKeyHidden()
        showButton.performScrollTo().assertIsEnabled().performClick()

        composeRule.onNodeWithText(SYNTHETIC_RECOVERY_KEY).performScrollTo()
        assertRecoveryKeyVisible()
        composeRule.onNodeWithText(context.getString(UiR.string.recovery_key_m142_hide))
            .performScrollTo()
            .assertIsDisplayed()
            .assert(
                SemanticsMatcher.expectValue(
                    SemanticsProperties.StateDescription,
                    context.getString(UiR.string.recovery_key_m142_visible),
                ),
            )
    }

    @Test
    fun recoveryKeyHidesImmediatelyOnPauseAndStaysHiddenAfterResume() {
        val owner = TestLifecycleOwner()
        moveToResumed(owner)
        val expanded = mutableStateOf(false)
        val presentations = mutableListOf<String>()

        setRecoveryKeyContent(owner, expanded, presentations)
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        composeRule.onNodeWithText(context.getString(UiR.string.recovery_key_m142_show))
            .performClick()
        assertRecoveryKeyVisible()
        assertEquals(listOf(SYNTHETIC_RECOVERY_KEY), presentations)

        moveToPaused(owner)
        assertRecoveryKeyHidden()
        composeRule.onNodeWithText(context.getString(UiR.string.recovery_key_m142_show))
            .assert(
                SemanticsMatcher.expectValue(
                    SemanticsProperties.StateDescription,
                    context.getString(UiR.string.recovery_key_m142_hidden),
                ),
            )

        moveToResumed(owner)
        assertRecoveryKeyHidden()
        assertEquals(listOf(SYNTHETIC_RECOVERY_KEY), presentations)
        composeRule.onNodeWithText(context.getString(UiR.string.recovery_key_m142_show))
            .assertIsEnabled()
    }

    @Test
    fun recoveryKeyReportsPresentationOnlyAfterExplicitShowWhileResumed() {
        val owner = TestLifecycleOwner()
        moveToResumed(owner)
        moveToPaused(owner)
        val expanded = mutableStateOf(false)
        val presentations = mutableListOf<String>()

        setRecoveryKeyContent(owner, expanded, presentations)

        assertRecoveryKeyHidden()
        assertTrue(presentations.isEmpty())

        moveToResumed(owner)
        assertRecoveryKeyHidden()
        assertTrue(presentations.isEmpty())

        composeRule.onNodeWithText(
            InstrumentationRegistry.getInstrumentation().targetContext
                .getString(UiR.string.recovery_key_m142_show),
        ).performClick()

        assertRecoveryKeyVisible()
        assertEquals(listOf(SYNTHETIC_RECOVERY_KEY), presentations)
    }

    @Test
    fun recoveryKeyRevealResetsWhenItsCompositionIsDisposedAndRecreated() {
        val owner = TestLifecycleOwner()
        moveToResumed(owner)
        val compositionVisible = mutableStateOf(true)
        val expanded = mutableStateOf(false)
        val presentations = mutableListOf<String>()
        setRecoveryKeyContent(owner, expanded, presentations, compositionVisible)
        val context = InstrumentationRegistry.getInstrumentation().targetContext

        composeRule.onNodeWithText(context.getString(UiR.string.recovery_key_m142_show))
            .performClick()
        assertRecoveryKeyVisible()
        assertEquals(listOf(SYNTHETIC_RECOVERY_KEY), presentations)

        composeRule.runOnIdle { compositionVisible.value = false }
        assertRecoveryKeyAbsent()
        composeRule.runOnIdle { compositionVisible.value = true }

        assertRecoveryKeyHidden()
        assertEquals(listOf(SYNTHETIC_RECOVERY_KEY), presentations)
        composeRule.onNodeWithText(context.getString(UiR.string.recovery_key_m142_show))
            .assertIsDisplayed()
    }

    @Test
    fun recoveryPresentationHistorySurvivesPauseWithoutReopeningKey() {
        val owner = TestLifecycleOwner()
        moveToResumed(owner)
        val uiState = mutableStateOf(availableRecoveryState())
        val actions = mutableListOf<RecoveryKeyUiAction>()
        setVaultRecoveryContent(owner, uiState, actions)
        val context = InstrumentationRegistry.getInstrumentation().targetContext

        composeRule.onNodeWithText(context.getString(UiR.string.recovery_key_m142_show))
            .performScrollTo().performClick()
        assertRecoveryKeyVisible()
        composeRule.onNode(checkboxMatcher()).performScrollTo().performClick()

        composeRule.runOnIdle {
            uiState.value = uiState.value.copy(operationState = VaultUiOperationState.Loading)
        }
        assertRecoveryKeyAbsent()
        composeRule.runOnIdle {
            uiState.value = uiState.value.copy(operationState = VaultUiOperationState.Success)
        }
        assertRecoveryKeyHidden()

        moveToPaused(owner)
        assertRecoveryKeyHidden()
        moveToResumed(owner)
        assertRecoveryKeyHidden()

        composeRule.onNodeWithText(context.getString(VaultR.string.vault_access_recovery_continue))
            .performScrollTo().performClick()

        assertEquals(
            listOf(
                RecoveryKeyUiAction.ConfirmationChanged(true),
                RecoveryKeyUiAction.Continue,
            ),
            actions,
        )
    }

    @Test
    fun firstRevealStillWorksAfterPauseAndResumeWithoutPriorShow() {
        val owner = TestLifecycleOwner()
        moveToResumed(owner)
        val uiState = mutableStateOf(availableRecoveryState())
        val actions = mutableListOf<RecoveryKeyUiAction>()
        setVaultRecoveryContent(owner, uiState, actions)
        val context = InstrumentationRegistry.getInstrumentation().targetContext

        moveToPaused(owner)
        moveToResumed(owner)
        assertRecoveryKeyHidden()

        composeRule.onNode(checkboxMatcher()).performScrollTo().performClick()
        composeRule.onNodeWithText(context.getString(VaultR.string.vault_access_recovery_continue))
            .performScrollTo().performClick()

        assertEquals(
            listOf(
                RecoveryKeyUiAction.ConfirmationChanged(true),
                RecoveryKeyUiAction.ConfirmationChanged(false),
            ),
            actions,
        )
        composeRule.onNodeWithText(SYNTHETIC_RECOVERY_KEY).performScrollTo().assertIsDisplayed()
        composeRule.onNodeWithText(context.getString(VaultR.string.vault_access_recovery_save_acknowledge))
            .performClick()
        composeRule.onNodeWithText(android.text.Html.fromHtml(context.getString(VaultR.string.vault_access_recovery_reconfirm), android.text.Html.FROM_HTML_MODE_LEGACY).toString())
            .performScrollTo().assertIsDisplayed()
    }

    @Test
    fun recoveryPresentationHistoryResetsWhenKeyChanges() {
        val owner = TestLifecycleOwner()
        moveToResumed(owner)
        val uiState = mutableStateOf(availableRecoveryState().copy(isConfirmed = true))
        val actions = mutableListOf<RecoveryKeyUiAction>()
        setVaultRecoveryContent(owner, uiState, actions)
        val context = InstrumentationRegistry.getInstrumentation().targetContext

        composeRule.onNodeWithText(context.getString(UiR.string.recovery_key_m142_show))
            .performScrollTo().performClick()
        assertRecoveryKeyVisible()
        composeRule.runOnIdle {
            uiState.value = uiState.value.copy(
                recoveryKey = SECOND_SYNTHETIC_RECOVERY_KEY,
                isConfirmed = true,
            )
        }
        assertRecoveryKeyHidden()

        composeRule.onNodeWithText(context.getString(VaultR.string.vault_access_recovery_continue))
            .performScrollTo().performClick()

        assertEquals(
            listOf(RecoveryKeyUiAction.ConfirmationChanged(false)),
            actions,
        )
        composeRule.onNodeWithText(SECOND_SYNTHETIC_RECOVERY_KEY)
            .performScrollTo().assertIsDisplayed()
        composeRule.onNodeWithText(context.getString(VaultR.string.vault_access_recovery_save_acknowledge))
            .performClick()
        composeRule.onNodeWithText(android.text.Html.fromHtml(context.getString(VaultR.string.vault_access_recovery_reconfirm), android.text.Html.FROM_HTML_MODE_LEGACY).toString())
            .performScrollTo().assertIsDisplayed()
    }

    @Test
    fun recoveryPresentationHistoryResetsAfterCompositionDisposal() {
        val owner = TestLifecycleOwner()
        moveToResumed(owner)
        val uiState = mutableStateOf(availableRecoveryState().copy(isConfirmed = true))
        val actions = mutableListOf<RecoveryKeyUiAction>()
        val compositionVisible = mutableStateOf(true)
        setVaultRecoveryContent(owner, uiState, actions, compositionVisible)
        val context = InstrumentationRegistry.getInstrumentation().targetContext

        composeRule.onNodeWithText(context.getString(UiR.string.recovery_key_m142_show))
            .performScrollTo().performClick()
        assertRecoveryKeyVisible()
        composeRule.runOnIdle { compositionVisible.value = false }
        assertRecoveryKeyAbsent()
        composeRule.runOnIdle { compositionVisible.value = true }
        assertRecoveryKeyHidden()

        composeRule.onNodeWithText(context.getString(VaultR.string.vault_access_recovery_continue))
            .performScrollTo().performClick()

        assertEquals(
            listOf(RecoveryKeyUiAction.ConfirmationChanged(false)),
            actions,
        )
        composeRule.onNodeWithText(SYNTHETIC_RECOVERY_KEY).performScrollTo().assertIsDisplayed()
        composeRule.onNodeWithText(context.getString(VaultR.string.vault_access_recovery_save_acknowledge))
            .performClick()
        composeRule.onNodeWithText(android.text.Html.fromHtml(context.getString(VaultR.string.vault_access_recovery_reconfirm), android.text.Html.FROM_HTML_MODE_LEGACY).toString())
            .performScrollTo().assertIsDisplayed()
    }

    @Test
    fun recoveryPresentationHistoryIsNotRestoredWithSavedInstanceState() {
        val owner = TestLifecycleOwner()
        moveToResumed(owner)
        val state = mutableStateOf(availableRecoveryState().copy(isConfirmed = true))
        val actions = mutableListOf<RecoveryKeyUiAction>()
        val restoration = StateRestorationTester(composeRule)
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        restoration.setContent {
            SafeCubeTheme(appearance = lightAppearance()) {
                CompositionLocalProvider(LocalLifecycleOwner provides owner) {
                    VaultRecoveryContent(
                        uiState = state.value,
                        onAction = { action ->
                            actions += action
                            if (action is RecoveryKeyUiAction.ConfirmationChanged) {
                                state.value = state.value.copy(isConfirmed = action.isConfirmed)
                            }
                        },
                    )
                }
            }
        }
        composeRule.onNodeWithText(context.getString(UiR.string.recovery_key_m142_show))
            .performScrollTo().performClick()
        assertRecoveryKeyVisible()

        restoration.emulateSavedInstanceStateRestore()

        assertRecoveryKeyHidden()
        composeRule.onNodeWithText(context.getString(VaultR.string.vault_access_recovery_continue))
            .performScrollTo().performClick()
        assertEquals(listOf(RecoveryKeyUiAction.ConfirmationChanged(false)), actions)
        assertRecoveryKeyVisible()
    }

    @Test
    fun recoveryConfirmationCheckboxGatesContinueAndForwardsActions() {
        val uiState = mutableStateOf(availableRecoveryState())
        val actions = mutableListOf<RecoveryKeyUiAction>()
        val context = InstrumentationRegistry.getInstrumentation().targetContext

        composeRule.setContent {
            SafeCubeTheme(appearance = lightAppearance()) {
                VaultRecoveryContent(
                    uiState = uiState.value,
                    onAction = { action ->
                        actions += action
                        if (action is RecoveryKeyUiAction.ConfirmationChanged) {
                            uiState.value = uiState.value.copy(isConfirmed = action.isConfirmed)
                        }
                    },
                )
            }
        }

        val continueButton = composeRule.onNodeWithText(
            context.getString(VaultR.string.vault_access_recovery_continue),
        )
        continueButton.performScrollTo().assertIsDisplayed().assertIsNotEnabled()
        composeRule.onNodeWithText(
            context.getString(VaultR.string.vault_access_recovery_confirmation),
        ).performScrollTo().assertIsDisplayed()
        composeRule.onAllNodes(checkboxMatcher()).assertCountEquals(1)
        composeRule.onNodeWithText(context.getString(UiR.string.recovery_key_m142_show))
            .performScrollTo().performClick()
        assertRecoveryKeyVisible()
        composeRule.onNode(checkboxMatcher()).performScrollTo().performClick()

        assertEquals(RecoveryKeyUiAction.ConfirmationChanged(true), actions.single())
        continueButton.performScrollTo().assertIsEnabled().performClick()
        assertEquals(
            listOf(
                RecoveryKeyUiAction.ConfirmationChanged(true),
                RecoveryKeyUiAction.Continue,
            ),
            actions,
        )

        composeRule.onNode(checkboxMatcher()).performScrollTo().performClick()
        assertEquals(RecoveryKeyUiAction.ConfirmationChanged(false), actions.last())
        continueButton.performScrollTo().assertIsNotEnabled()
    }

    private fun setRecoveryKeyContent(
        owner: TestLifecycleOwner,
        expanded: MutableState<Boolean>,
        presentations: MutableList<String>,
        compositionVisible: MutableState<Boolean> = mutableStateOf(true),
    ) {
        composeRule.setContent {
            SafeCubeTheme(appearance = lightAppearance()) {
                CompositionLocalProvider(LocalLifecycleOwner provides owner) {
                    if (compositionVisible.value) {
                        SafeCubeRecoveryKey(
                            value = SYNTHETIC_RECOVERY_KEY,
                            expanded = expanded.value,
                            onExpandedChange = { expanded.value = it },
                            onPresented = { presentations += SYNTHETIC_RECOVERY_KEY },
                        )
                    }
                }
            }
        }
    }

    private fun setVaultRecoveryContent(
        owner: TestLifecycleOwner,
        uiState: MutableState<RecoveryKeyUiState> = mutableStateOf(availableRecoveryState()),
        actions: MutableList<RecoveryKeyUiAction> = mutableListOf(),
        compositionVisible: MutableState<Boolean> = mutableStateOf(true),
    ) {
        composeRule.setContent {
            SafeCubeTheme(appearance = lightAppearance()) {
                CompositionLocalProvider(LocalLifecycleOwner provides owner) {
                    if (compositionVisible.value) {
                        VaultRecoveryContent(
                            uiState = uiState.value,
                            onAction = { action ->
                                actions += action
                                if (action is RecoveryKeyUiAction.ConfirmationChanged) {
                                    uiState.value = uiState.value.copy(isConfirmed = action.isConfirmed)
                                }
                            },
                        )
                    }
                }
            }
        }
    }

    private fun assertRecoveryKeyHidden() {
        composeRule.onAllNodesWithText(
            InstrumentationRegistry.getInstrumentation().targetContext
                .getString(UiR.string.recovery_key_m142_hidden),
        ).assertCountEquals(0)
        assertRecoveryKeyAbsent()
        assertClipboardActionsAbsent()
        assertHiddenLayoutDoesNotContain(SYNTHETIC_RECOVERY_KEY)
    }

    private fun assertRecoveryKeyAbsent() {
        composeRule.onAllNodes(containsPlaintext(SYNTHETIC_RECOVERY_KEY))
            .assertCountEquals(0)
        composeRule.onAllNodes(
            containsPlaintext(SYNTHETIC_RECOVERY_KEY),
            useUnmergedTree = true,
        ).assertCountEquals(0)
    }

    private fun assertRecoveryKeyVisible() {
        composeRule.onNodeWithText(SYNTHETIC_RECOVERY_KEY).assertIsDisplayed()
        composeRule.onAllNodes(containsPlaintext(SYNTHETIC_RECOVERY_KEY))
            .assertCountEquals(1)
        composeRule.onAllNodes(
            containsPlaintext(SYNTHETIC_RECOVERY_KEY),
            useUnmergedTree = true,
        ).assertCountEquals(1)
        assertClipboardActionsAbsent()
    }

    private fun assertClipboardActionsAbsent() {
        val clipboardAction = SemanticsMatcher("recovery key clipboard action") { node ->
            node.config.contains(SemanticsActions.CopyText) ||
                node.config.contains(SemanticsActions.CutText)
        }
        composeRule.onAllNodes(clipboardAction).assertCountEquals(0)
        composeRule.onAllNodes(clipboardAction, useUnmergedTree = true).assertCountEquals(0)
    }

    private fun assertHiddenLayoutDoesNotContain(value: String) {
        val layouts = mutableListOf<TextLayoutResult>()
        composeRule.onAllNodes(
            SemanticsMatcher.keyIsDefined(SemanticsActions.GetTextLayoutResult),
            useUnmergedTree = true,
        ).fetchSemanticsNodes().forEach { node ->
            composeRule.runOnIdle {
                node.config[SemanticsActions.GetTextLayoutResult].action?.invoke(layouts)
            }
        }
        assertTrue(layouts.none { layout -> layout.layoutInput.text.text.contains(value) })
    }

    private fun containsPlaintext(value: String): SemanticsMatcher =
        SemanticsMatcher("recovery key plaintext in semantics") { node ->
            val config = node.config
            listOf(
                config.getOrNull(SemanticsProperties.Text),
                config.getOrNull(SemanticsProperties.EditableText),
                config.getOrNull(SemanticsProperties.InputText),
                config.getOrNull(SemanticsProperties.ContentDescription),
            ).any { it?.toString()?.contains(value) == true }
        }

    private fun checkboxMatcher(): SemanticsMatcher =
        SemanticsMatcher("recovery confirmation checkbox") { node ->
            node.config.getOrNull(SemanticsProperties.Role) == Role.Checkbox &&
                node.config.contains(SemanticsActions.OnClick)
        }

    private fun lightAppearance() = AppearanceSettings(
        mode = AppearanceMode.Light,
        dynamicColorEnabled = false,
    )

    private fun availableRecoveryState() = RecoveryKeyUiState(
        recoveryKey = SYNTHETIC_RECOVERY_KEY,
        operationState = VaultUiOperationState.Success,
    )

    private fun moveToResumed(owner: TestLifecycleOwner) {
        InstrumentationRegistry.getInstrumentation().runOnMainSync {
            if (owner.lifecycle.currentState == Lifecycle.State.INITIALIZED) {
                owner.registry.handleLifecycleEvent(Lifecycle.Event.ON_CREATE)
            }
            if (owner.lifecycle.currentState == Lifecycle.State.CREATED) {
                owner.registry.handleLifecycleEvent(Lifecycle.Event.ON_START)
            }
            if (owner.lifecycle.currentState == Lifecycle.State.STARTED) {
                owner.registry.handleLifecycleEvent(Lifecycle.Event.ON_RESUME)
            }
        }
    }

    private fun moveToPaused(owner: TestLifecycleOwner) {
        InstrumentationRegistry.getInstrumentation().runOnMainSync {
            owner.registry.handleLifecycleEvent(Lifecycle.Event.ON_PAUSE)
        }
    }

    private class TestLifecycleOwner : LifecycleOwner {
        val registry = LifecycleRegistry(this)

        override val lifecycle: Lifecycle
            get() = registry
    }

    private companion object {
        const val SYNTHETIC_RECOVERY_KEY = "DEMO-NOT-A-RECOVERY-KEY"
        const val SECOND_SYNTHETIC_RECOVERY_KEY = "DEMO-NOT-A-RECOVERY-KEY-2"
    }
}
