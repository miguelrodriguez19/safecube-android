package com.miguelrodriguez19.safecube

import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.semantics.SemanticsActions
import androidx.compose.ui.semantics.SemanticsProperties
import androidx.compose.ui.semantics.getOrNull
import androidx.compose.ui.text.TextLayoutResult
import androidx.compose.ui.test.SemanticsMatcher
import androidx.compose.ui.test.assertCountEquals
import androidx.compose.ui.test.assertIsNotEnabled
import androidx.compose.ui.test.assert
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertHeightIsAtLeast
import androidx.compose.ui.test.assertWidthIsAtLeast
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.hasSetTextAction
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performSemanticsAction
import androidx.compose.ui.test.performTextInput
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LifecycleRegistry
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.miguelrodriguez19.safecube.core.ui.R
import com.miguelrodriguez19.safecube.core.ui.component.SecretOutlinedTextField
import com.miguelrodriguez19.safecube.core.ui.theme.AppearanceMode
import com.miguelrodriguez19.safecube.core.ui.theme.AppearanceSettings
import com.miguelrodriguez19.safecube.core.ui.theme.SafeCubeTheme
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class ComposeDesignSystemSecretTest {
    @get:Rule
    val composeRule = createComposeRule()

    @Test
    fun secretErrorPreservesLabelSupportingTextAndVisibilityState() {
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        val hiddenLabel = context.getString(R.string.design_system_secret_hidden)
        val showLabel = context.getString(R.string.design_system_show_secret)
        composeRule.setContent {
            SafeCubeTheme {
                SecretOutlinedTextField(
                    value = SYNTHETIC_SECRET,
                    onValueChange = {},
                    label = { androidx.compose.material3.Text("Synthetic protected field") },
                    isError = true,
                    supportingText = { androidx.compose.material3.Text("Synthetic validation message") },
                    allowReveal = true,
                )
            }
        }

        composeRule.onNodeWithText("Synthetic protected field").assertIsDisplayed()
        composeRule.onNodeWithText("Synthetic validation message").assertIsDisplayed()
        composeRule.onNode(SemanticsMatcher.keyIsDefined(SemanticsProperties.Error)).assertExists()
        composeRule.onNodeWithContentDescription(showLabel)
            .assert(SemanticsMatcher.expectValue(SemanticsProperties.StateDescription, hiddenLabel))
            .assertHeightIsAtLeast(48.dp)
            .assertWidthIsAtLeast(48.dp)
        assertSecretHidden(SYNTHETIC_SECRET)
    }

    @Test
    fun maskedSecretIsAbsentFromMergedAndUnmergedSemanticsWhileEditing() {
        val owner = TestLifecycleOwner()
        moveToResumed(owner)
        val value = mutableStateOf(SYNTHETIC_SECRET)

        setSecretContent(owner, value, allowReveal = false)

        assertSecretHidden(value.value)
        composeRule.onNode(hasSetTextAction(), useUnmergedTree = true)
            .performSemanticsAction(SemanticsActions.RequestFocus)
            .performTextInput("synthetic-input")
        composeRule.runOnIdle {
            assertTrue(value.value.contains("synthetic-input"))
        }
        assertSecretHidden(value.value)

        composeRule.runOnIdle { value.value = "synthetic-replacement" }
        assertSecretHidden(value.value)
        composeRule.onNode(hasSetTextAction(), useUnmergedTree = true)
            .performSemanticsAction(SemanticsActions.RequestFocus)
            .performTextInput("synthetic-input")
        composeRule.runOnIdle {
            assertTrue(value.value.contains("synthetic-input"))
            value.value = SYNTHETIC_SECRET
        }
        assertSecretHidden(value.value)
    }

    @Test
    fun revealIsAccessibleOnlyInResumedAndHidesOnPauseWithoutRestoring() {
        val owner = TestLifecycleOwner()
        val value = mutableStateOf(SYNTHETIC_SECRET)
        val showLabel = InstrumentationRegistry.getInstrumentation().targetContext
            .getString(R.string.design_system_show_secret)

        setSecretContent(owner, value, allowReveal = true)
        composeRule.onNodeWithContentDescription(showLabel)
            .assertIsNotEnabled()
            .performSemanticsAction(SemanticsActions.OnClick)
        assertSecretHidden(value.value)

        moveToResumed(owner)
        assertSecretHidden(value.value)
        composeRule.onNodeWithContentDescription(showLabel).performClick()
        assertSecretPresentInSemantics()
        assertClipboardActionsAbsent()

        moveToPaused(owner)
        assertSecretHidden(value.value)
        moveToResumed(owner)
        assertSecretHidden(value.value)

        composeRule.onNodeWithContentDescription(showLabel).performClick()
        assertSecretPresentInSemantics()
    }

    @Test
    fun disabledAndRecreatedSecretFieldsRemainMasked() {
        val owner = TestLifecycleOwner()
        moveToResumed(owner)
        val value = mutableStateOf(SYNTHETIC_SECRET)
        val enabled = mutableStateOf(true)
        val compositionVisible = mutableStateOf(true)
        val showLabel = InstrumentationRegistry.getInstrumentation().targetContext
            .getString(R.string.design_system_show_secret)

        setSecretContent(
            owner = owner,
            value = value,
            allowReveal = true,
            enabled = enabled,
            compositionVisible = compositionVisible,
        )
        composeRule.onNodeWithContentDescription(showLabel).performClick()
        assertSecretPresentInSemantics()

        composeRule.runOnIdle { enabled.value = false }
        assertSecretHidden(value.value)
        composeRule.onNodeWithContentDescription(showLabel)
            .assertIsNotEnabled()
            .performSemanticsAction(SemanticsActions.OnClick)
        composeRule.runOnIdle { enabled.value = true }
        assertSecretHidden(value.value)

        composeRule.runOnIdle { compositionVisible.value = false }
        composeRule.runOnIdle { compositionVisible.value = true }
        assertSecretHidden(value.value)
    }

    private fun setSecretContent(
        owner: TestLifecycleOwner,
        value: androidx.compose.runtime.MutableState<String>,
        allowReveal: Boolean,
        enabled: androidx.compose.runtime.MutableState<Boolean> = mutableStateOf(true),
        compositionVisible: androidx.compose.runtime.MutableState<Boolean> = mutableStateOf(true),
    ) {
        composeRule.setContent {
            SafeCubeTheme(
                appearance = AppearanceSettings(
                    mode = AppearanceMode.Light,
                    dynamicColorEnabled = false,
                ),
            ) {
                CompositionLocalProvider(LocalLifecycleOwner provides owner) {
                    if (compositionVisible.value) {
                        SecretOutlinedTextField(
                            value = value.value,
                            onValueChange = { value.value = it },
                            label = { androidx.compose.material3.Text("Synthetic secret") },
                            enabled = enabled.value,
                            allowReveal = allowReveal,
                        )
                    }
                }
            }
        }
    }

    private fun assertSecretHidden(currentValue: String) {
        assertSecretAbsentFromSemantics(currentValue)
        assertClipboardActionsAbsent()
        assertHiddenLayoutDoesNotContain(currentValue)
    }

    private fun assertSecretAbsentFromSemantics(currentValue: String) {
        composeRule.onAllNodes(containsPlaintext(currentValue)).assertCountEquals(0)
        composeRule.onAllNodes(
            containsPlaintext(currentValue),
            useUnmergedTree = true,
        ).assertCountEquals(0)
    }

    private fun assertSecretPresentInSemantics() {
        composeRule.onAllNodes(containsPlaintext(SYNTHETIC_SECRET)).assertCountEquals(1)
        composeRule.onAllNodes(
            containsPlaintext(SYNTHETIC_SECRET),
            useUnmergedTree = true,
        ).assertCountEquals(1)
    }

    private fun assertClipboardActionsAbsent() {
        val clipboardActionMatcher = SemanticsMatcher("secret clipboard actions") { node ->
            node.config.contains(SemanticsActions.CopyText) ||
                node.config.contains(SemanticsActions.CutText)
        }
        composeRule.onAllNodes(clipboardActionMatcher).assertCountEquals(0)
        composeRule.onAllNodes(
            clipboardActionMatcher,
            useUnmergedTree = true,
        ).assertCountEquals(0)
    }

    private fun assertHiddenLayoutDoesNotContain(currentValue: String) {
        val layouts = mutableListOf<TextLayoutResult>()
        composeRule.onAllNodes(
            SemanticsMatcher.keyIsDefined(SemanticsActions.GetTextLayoutResult),
            useUnmergedTree = true,
        ).fetchSemanticsNodes().forEach { node ->
            composeRule.runOnIdle {
                node.config[SemanticsActions.GetTextLayoutResult].action?.invoke(layouts)
            }
        }
        assertTrue(layouts.none { layout ->
            layout.layoutInput.text.text.contains(currentValue)
        })
    }

    private fun containsPlaintext(value: String): SemanticsMatcher =
        SemanticsMatcher("secret plaintext in semantics") { node ->
            val config = node.config
            config.getOrNull(SemanticsProperties.EditableText).toString().contains(value) ||
                config.getOrNull(SemanticsProperties.InputText).toString().contains(value) ||
                config.getOrNull(SemanticsProperties.Text).toString().contains(value)
        }

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
        const val SYNTHETIC_SECRET = "fixture-not-a-credential"
    }
}
