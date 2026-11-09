package com.miguelrodriguez19.safecube

import androidx.compose.foundation.layout.Column
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Sync
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.SemanticsActions
import androidx.compose.ui.semantics.SemanticsProperties
import androidx.compose.ui.test.assertHasClickAction
import androidx.compose.ui.test.assertHeightIsAtLeast
import androidx.compose.ui.test.assertCountEquals
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.assertIsEnabled
import androidx.compose.ui.test.assertIsFocused
import androidx.compose.ui.test.assertIsNotEnabled
import androidx.compose.ui.test.assertWidthIsAtLeast
import androidx.compose.ui.test.hasSetTextAction
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.SemanticsMatcher
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performSemanticsAction
import androidx.compose.ui.test.performTextInput
import androidx.compose.ui.unit.dp
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.miguelrodriguez19.safecube.core.ui.component.SafeCubeButton
import com.miguelrodriguez19.safecube.core.ui.component.SafeCubeButtonVariant
import com.miguelrodriguez19.safecube.core.ui.component.SafeCubeIconButton
import com.miguelrodriguez19.safecube.core.ui.component.SafeCubeTextField
import com.miguelrodriguez19.safecube.core.ui.component.SecretOutlinedTextField
import androidx.test.platform.app.InstrumentationRegistry
import com.miguelrodriguez19.safecube.core.ui.R
import com.miguelrodriguez19.safecube.core.ui.theme.AppearanceMode
import com.miguelrodriguez19.safecube.core.ui.theme.AppearanceSettings
import com.miguelrodriguez19.safecube.core.ui.theme.SafeCubeTheme
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class ComposeDesignSystemActionsTest {
    @get:Rule
    val composeRule = createComposeRule()

    @Test
    fun errorFieldsProvideVisibleFeedbackEvenWithoutSupportingText() {
        val errorLabel = InstrumentationRegistry.getInstrumentation().targetContext
            .getString(R.string.design_system_error)
        setDesignSystemContent {
            Column {
                SafeCubeTextField(
                    value = "",
                    onValueChange = {},
                    label = "Synthetic normal field",
                    isError = true,
                )
                SecretOutlinedTextField(
                    value = "",
                    onValueChange = {},
                    label = { androidx.compose.material3.Text("Synthetic secret field") },
                    isError = true,
                )
            }
        }

        val errors = composeRule.onAllNodesWithText(errorLabel, useUnmergedTree = true)
        errors.assertCountEquals(2)
        errors[0].assertIsDisplayed()
        errors[1].assertIsDisplayed()
        composeRule.onNodeWithText("Synthetic normal field").assertIsDisplayed()
        composeRule.onNodeWithText("Synthetic secret field").assertIsDisplayed()
    }

    @Test
    fun buttonsExposeRolesTargetsAndCallbacksForEveryVariant() {
        val clicks = mutableListOf<String>()
        setDesignSystemContent {
            Column {
                SafeCubeButton(
                    text = "Primary action",
                    onClick = { clicks += "primary" },
                )
                SafeCubeButton(
                    text = "Secondary action",
                    onClick = { clicks += "secondary" },
                    variant = SafeCubeButtonVariant.Secondary,
                )
                SafeCubeButton(
                    text = "Text action",
                    onClick = { clicks += "text" },
                    variant = SafeCubeButtonVariant.Text,
                )
                SafeCubeButton(
                    text = "Destructive action",
                    onClick = { clicks += "destructive" },
                    variant = SafeCubeButtonVariant.Destructive,
                )
                SafeCubeIconButton(
                    imageVector = Icons.Rounded.Sync,
                    contentDescription = "Sync synthetic data",
                    onClick = { clicks += "icon" },
                )
            }
        }

        val buttonRole = SemanticsMatcher.expectValue(SemanticsProperties.Role, Role.Button)
        listOf(
            "Primary action",
            "Secondary action",
            "Text action",
            "Destructive action",
        ).forEach { label ->
            composeRule.onNode(hasText(label) and buttonRole)
                .assertIsEnabled()
                .assertHasClickAction()
                .assertHeightIsAtLeast(48.dp)
                .performClick()
        }
        composeRule.onNodeWithContentDescription("Sync synthetic data")
            .assertIsEnabled()
            .assertHasClickAction()
            .assertWidthIsAtLeast(48.dp)
            .assertHeightIsAtLeast(48.dp)
            .performClick()

        assertEquals(
            listOf("primary", "secondary", "text", "destructive", "icon"),
            clicks,
        )
    }

    @Test
    fun disabledAndLoadingActionsRejectDirectSemanticsClicks() {
        var disabledCount = 0
        var loadingCount = 0
        var iconCount = 0
        setDesignSystemContent {
            Column {
                SafeCubeButton(
                    text = "Disabled action",
                    onClick = { disabledCount++ },
                    enabled = false,
                )
                SafeCubeButton(
                    text = "Loading action",
                    onClick = { loadingCount++ },
                    loading = true,
                )
                SafeCubeIconButton(
                    imageVector = Icons.Rounded.Sync,
                    contentDescription = "Loading synthetic sync",
                    onClick = { iconCount++ },
                    loading = true,
                )
            }
        }

        composeRule.onNodeWithText("Disabled action")
            .assertIsNotEnabled()
            .assertHasClickAction()
            .performSemanticsAction(SemanticsActions.OnClick)
        composeRule.onNodeWithText("Loading action")
            .assertIsNotEnabled()
            .assertHasClickAction()
            .performSemanticsAction(SemanticsActions.OnClick)
        composeRule.onNodeWithContentDescription("Loading synthetic sync")
            .assertIsNotEnabled()
            .assertHasClickAction()
            .performSemanticsAction(SemanticsActions.OnClick)

        assertEquals(0, disabledCount)
        assertEquals(0, loadingCount)
        assertEquals(0, iconCount)
    }

    @Test
    fun textFieldSupportsEditingFocusErrorAndSupportingText() {
        val value = mutableStateOf("")
        setDesignSystemContent {
            SafeCubeTextField(
                value = value.value,
                onValueChange = { value.value = it },
                label = "Synthetic reference",
                isError = true,
                supportingText = "Synthetic validation message",
            )
        }

        composeRule.onNode(hasSetTextAction())
            .assertIsEnabled()
            .assertHeightIsAtLeast(48.dp)
            .performSemanticsAction(SemanticsActions.RequestFocus)
            .assertIsFocused()
            .performTextInput("synthetic-value")
        composeRule.onNodeWithText("Synthetic validation message").assertExists()
        composeRule.onNode(
            SemanticsMatcher.expectValue(
                SemanticsProperties.StateDescription,
                "Synthetic validation message",
            ),
        ).assertExists()

        assertEquals("synthetic-value", value.value)
    }

    private fun setDesignSystemContent(content: @Composable () -> Unit) {
        composeRule.setContent {
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
}
