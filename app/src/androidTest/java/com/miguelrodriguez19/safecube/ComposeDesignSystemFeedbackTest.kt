package com.miguelrodriguez19.safecube

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.material3.MaterialTheme
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextLayoutResult
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.LiveRegionMode
import androidx.compose.ui.semantics.ProgressBarRangeInfo
import androidx.compose.ui.semantics.SemanticsActions
import androidx.compose.ui.semantics.SemanticsProperties
import androidx.compose.ui.test.SemanticsMatcher
import androidx.compose.ui.test.assert
import androidx.compose.ui.test.assert
import androidx.compose.ui.test.assertCountEquals
import androidx.compose.ui.test.assertHasClickAction
import androidx.compose.ui.test.assertHeightIsAtLeast
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsEnabled
import androidx.compose.ui.test.assertIsFocused
import androidx.compose.ui.test.assertIsNotEnabled
import androidx.compose.ui.test.assertWidthIsAtLeast
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performSemanticsAction
import androidx.compose.ui.test.performScrollTo
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.unit.dp
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.miguelrodriguez19.safecube.core.ui.R
import com.miguelrodriguez19.safecube.core.ui.component.SafeCubeBanner
import com.miguelrodriguez19.safecube.core.ui.component.SafeCubeBannerVariant
import com.miguelrodriguez19.safecube.core.ui.component.SafeCubeConfirmationDialog
import com.miguelrodriguez19.safecube.core.ui.component.SafeCubeEmptyState
import com.miguelrodriguez19.safecube.core.ui.component.SafeCubeProgress
import com.miguelrodriguez19.safecube.core.ui.component.SafeCubeRetryableError
import com.miguelrodriguez19.safecube.core.ui.component.SafeCubeTerminalError
import com.miguelrodriguez19.safecube.core.ui.theme.AppearanceMode
import com.miguelrodriguez19.safecube.core.ui.theme.AppearanceSettings
import com.miguelrodriguez19.safecube.core.ui.theme.SafeCubeTheme
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class ComposeDesignSystemFeedbackTest {
    @get:Rule
    val composeRule = createComposeRule()

    @Test
    fun bannerTextActionPreservesTheContrastOfItsSemanticSurface() {
        var expectedColor = Color.Unspecified
        val layouts = mutableListOf<TextLayoutResult>()
        setFeedbackContent {
            expectedColor = MaterialTheme.colorScheme.onErrorContainer
            SafeCubeBanner(
                message = "Synthetic operation needs attention",
                variant = SafeCubeBannerVariant.Error,
                actionLabel = "Review synthetic operation",
                onAction = {},
            )
        }

        composeRule.onNodeWithText("Review synthetic operation", useUnmergedTree = true)
            .performSemanticsAction(SemanticsActions.GetTextLayoutResult) { action -> action(layouts) }

        assertTrue("Action text must expose its rendered layout", layouts.isNotEmpty())
        layouts.forEach { assertEquals(expectedColor, it.layoutInput.style.color) }
    }

    @Test
    fun bannersExposeLocalizedStateTextAndPoliteLiveRegionWithoutColorOnlyMeaning() {
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        val variants = listOf(
            SafeCubeBannerVariant.Info to context.getString(
                R.string.design_system_feedback_variant_info,
            ),
            SafeCubeBannerVariant.Warning to context.getString(
                R.string.design_system_feedback_variant_warning,
            ),
            SafeCubeBannerVariant.Error to context.getString(
                R.string.design_system_feedback_variant_error,
            ),
            SafeCubeBannerVariant.Offline to context.getString(
                R.string.design_system_feedback_variant_offline,
            ),
            SafeCubeBannerVariant.Draft to context.getString(
                R.string.design_system_feedback_variant_draft,
            ),
            SafeCubeBannerVariant.Conflict to context.getString(
                R.string.design_system_feedback_variant_conflict,
            ),
            SafeCubeBannerVariant.Success to context.getString(
                R.string.design_system_feedback_variant_success,
            ),
        )

        setFeedbackContent {
            Column(modifier = Modifier.verticalScroll(rememberScrollState())) {
                variants.forEach { (variant, _) ->
                    SafeCubeBanner(
                        message = "Synthetic ${variant.name} message",
                        variant = variant,
                    )
                }
            }
        }

        variants.forEach { (_, label) ->
            composeRule.onNodeWithText(label)
                .performScrollTo()
                .assertIsDisplayed()
            composeRule.onNodeWithContentDescription(label)
                .performScrollTo()
                .assertIsDisplayed()
            composeRule.onNode(
                SemanticsMatcher.expectValue(
                    SemanticsProperties.StateDescription,
                    label,
                ),
            ).performScrollTo().assertIsDisplayed()
        }
        composeRule.onAllNodes(
            SemanticsMatcher.expectValue(
                SemanticsProperties.LiveRegion,
                LiveRegionMode.Polite,
            ),
        ).assertCountEquals(variants.size)
    }

    @Test
    fun bannerActionUsesCallbackAndMeetsTouchTarget() {
        var actionCount = 0

        setFeedbackContent {
            SafeCubeBanner(
                message = "Synthetic information",
                variant = SafeCubeBannerVariant.Info,
                actionLabel = "Open",
                onAction = { actionCount++ },
            )
        }

        composeRule.onNodeWithText("Open")
            .assertIsEnabled()
            .assertHasClickAction()
            .assertWidthIsAtLeast(48.dp)
            .assertHeightIsAtLeast(48.dp)
            .performClick()

        assertEquals(1, actionCount)
    }

    @Test
    fun bannerLoadingDisablesActionAndPreventsDuplicateCallback() {
        var actionCount = 0
        val loadingLabel = designSystemLoadingLabel()

        setFeedbackContent {
            SafeCubeBanner(
                message = "Synthetic information",
                variant = SafeCubeBannerVariant.Info,
                actionLabel = "Save",
                onAction = { actionCount++ },
                actionLoading = true,
            )
        }

        composeRule.onNodeWithText("Save")
            .assertIsNotEnabled()
            .assertHasClickAction()
            .performSemanticsAction(SemanticsActions.OnClick)
        composeRule.onNode(
            SemanticsMatcher.expectValue(
                SemanticsProperties.StateDescription,
                loadingLabel,
            ),
        ).assertIsDisplayed()

        assertEquals(0, actionCount)
    }

    @Test
    fun confirmationDialogInvokesConfirmAndDismissActions() {
        var confirmCount = 0
        var dismissCount = 0
        val dismissLabel = InstrumentationRegistry.getInstrumentation().targetContext
            .getString(R.string.action_cancel)

        setFeedbackContent {
            SafeCubeConfirmationDialog(
                title = "Synthetic confirmation",
                message = "Synthetic consequence",
                confirmLabel = "Confirm",
                onConfirm = { confirmCount++ },
                onDismiss = { dismissCount++ },
            )
        }

        composeRule.onNodeWithText("Synthetic confirmation")
            .assertIsDisplayed()
            .assert(SemanticsMatcher.keyIsDefined(SemanticsProperties.Heading))
        composeRule.onNodeWithText("Confirm")
            .assertIsEnabled()
            .assertHasClickAction()
            .assertHeightIsAtLeast(48.dp)
            .performClick()
        composeRule.onNodeWithText(dismissLabel)
            .assertIsEnabled()
            .assertHasClickAction()
            .assertHeightIsAtLeast(48.dp)
            .performClick()

        assertEquals(1, confirmCount)
        assertEquals(1, dismissCount)
    }

    @Test
    fun confirmationDialogLoadingDisablesConfirmAndDismiss() {
        var confirmCount = 0
        var dismissCount = 0
        val loadingLabel = designSystemLoadingLabel()

        setFeedbackContent {
            SafeCubeConfirmationDialog(
                title = "Synthetic confirmation",
                message = "Synthetic consequence",
                confirmLabel = "Confirm",
                dismissLabel = "Cancel",
                onConfirm = { confirmCount++ },
                onDismiss = { dismissCount++ },
                loading = true,
            )
        }

        composeRule.onNodeWithText("Confirm")
            .assertIsNotEnabled()
            .assertHasClickAction()
            .performSemanticsAction(SemanticsActions.OnClick)
        composeRule.onNodeWithText("Cancel")
            .assertIsNotEnabled()
            .assertHasClickAction()
            .performSemanticsAction(SemanticsActions.OnClick)
        composeRule.onNode(
            SemanticsMatcher.expectValue(
                SemanticsProperties.StateDescription,
                loadingLabel,
            ),
        ).assertIsDisplayed()

        assertEquals(0, confirmCount)
        assertEquals(0, dismissCount)
    }

    @Test
    fun progressExposesMessageAndDeterminateRange() {
        setFeedbackContent {
            SafeCubeProgress(
                message = "Uploading synthetic data",
                progress = 0.5f,
            )
        }

        composeRule.onNodeWithText("Uploading synthetic data").assertIsDisplayed()
        composeRule.onNode(
            SemanticsMatcher.expectValue(
                SemanticsProperties.ProgressBarRangeInfo,
                ProgressBarRangeInfo(
                    current = 0.5f,
                    range = 0f..1f,
                    steps = 0,
                ),
            ),
        ).assertIsDisplayed()
    }

    @Test
    fun emptyRetryableAndTerminalStatesExposeHeadingsAndOnlyRetryableRetry() {
        var emptyActionCount = 0
        var retryCount = 0
        var terminalActionCount = 0

        setFeedbackContent {
            Column {
                SafeCubeEmptyState(
                    title = "Synthetic empty",
                    message = "Nothing synthetic is here",
                    actionLabel = "Create item",
                    onAction = { emptyActionCount++ },
                )
                SafeCubeRetryableError(
                    title = "Synthetic retryable error",
                    message = "The synthetic operation can be retried",
                    onRetry = { retryCount++ },
                )
                SafeCubeTerminalError(
                    title = "Synthetic terminal error",
                    message = "The synthetic operation cannot be retried",
                    actionLabel = "Go back",
                    onAction = { terminalActionCount++ },
                )
            }
        }

        composeRule.onNodeWithText("Synthetic empty")
            .assert(SemanticsMatcher.keyIsDefined(SemanticsProperties.Heading))
        composeRule.onNodeWithText("Synthetic retryable error")
            .assert(SemanticsMatcher.keyIsDefined(SemanticsProperties.Heading))
        composeRule.onNodeWithText("Synthetic terminal error")
            .assert(SemanticsMatcher.keyIsDefined(SemanticsProperties.Heading))

        composeRule.onNodeWithText("Create item")
            .assertIsEnabled()
            .assertHeightIsAtLeast(48.dp)
            .performSemanticsAction(SemanticsActions.OnClick)
        composeRule.onNodeWithText("Retry")
            .assertIsEnabled()
            .assertHasClickAction()
            .assertHeightIsAtLeast(48.dp)
            .performSemanticsAction(SemanticsActions.RequestFocus)
            .assertIsFocused()
            .performClick()
        composeRule.onNodeWithText("Go back")
            .assertIsEnabled()
            .assertHeightIsAtLeast(48.dp)
            .performClick()

        composeRule.onAllNodes(hasText("Retry")).assertCountEquals(1)
        assertEquals(1, emptyActionCount)
        assertEquals(1, retryCount)
        assertEquals(1, terminalActionCount)
    }

    @Test
    fun retryingStateDisablesOnlyRetryAction() {
        var retryCount = 0
        val loadingLabel = designSystemLoadingLabel()

        setFeedbackContent {
            SafeCubeRetryableError(
                title = "Synthetic retryable error",
                message = "The synthetic operation is retrying",
                onRetry = { retryCount++ },
                retrying = true,
            )
        }

        composeRule.onNodeWithText("Retry")
            .assertIsNotEnabled()
            .assertHasClickAction()
            .performSemanticsAction(SemanticsActions.OnClick)
        composeRule.onNode(
            SemanticsMatcher.expectValue(
                SemanticsProperties.StateDescription,
                loadingLabel,
            ),
        ).assertIsDisplayed()
        assertEquals(0, retryCount)
    }

    private fun setFeedbackContent(content: @Composable () -> Unit) {
        composeRule.setContent {
            DesignSystemTestKeyboardMode()
            SafeCubeTheme(
                appearance = AppearanceSettings(
                    mode = AppearanceMode.Light,
                    dynamicColorEnabled = false,
                ),
            ) {
                content()
            }
        }
    }

    private fun designSystemLoadingLabel(): String = InstrumentationRegistry
        .getInstrumentation()
        .targetContext
        .getString(R.string.design_system_loading)
}
