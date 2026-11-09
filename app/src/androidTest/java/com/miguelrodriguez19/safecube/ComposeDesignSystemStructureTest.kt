package com.miguelrodriguez19.safecube

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.rounded.Home
import androidx.compose.material.icons.rounded.Info
import androidx.compose.material.icons.rounded.Settings
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.SemanticsActions
import androidx.compose.ui.semantics.SemanticsProperties
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.test.SemanticsMatcher
import androidx.compose.ui.test.assert
import androidx.compose.ui.test.assertHasClickAction
import androidx.compose.ui.test.assertHasNoClickAction
import androidx.compose.ui.test.assertHeightIsAtLeast
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsEnabled
import androidx.compose.ui.test.assertIsFocused
import androidx.compose.ui.test.assertIsNotEnabled
import androidx.compose.ui.test.assertIsNotSelected
import androidx.compose.ui.test.assertIsSelected
import androidx.compose.ui.test.assertWidthIsAtLeast
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.hasRequestFocusAction
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performSemanticsAction
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.dp
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.miguelrodriguez19.safecube.core.ui.component.SafeCubeBottomNavigation
import com.miguelrodriguez19.safecube.core.ui.component.SafeCubeCard
import com.miguelrodriguez19.safecube.core.ui.component.SafeCubeItemRow
import com.miguelrodriguez19.safecube.core.ui.component.SafeCubeNavigationItem
import com.miguelrodriguez19.safecube.core.ui.component.SafeCubeTopAppBar
import com.miguelrodriguez19.safecube.core.ui.theme.AppearanceMode
import com.miguelrodriguez19.safecube.core.ui.theme.AppearanceSettings
import com.miguelrodriguez19.safecube.core.ui.theme.SafeCubeTheme
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class ComposeDesignSystemStructureTest {
    @get:Rule
    val composeRule = createComposeRule()

    @Test
    fun topAppBarExposesHeadingNavigationLabelFocusAndTarget() {
        var navigationClicks = 0

        composeRule.setContent {
            DesignSystemTestKeyboardMode()
            SafeCubeTheme(
                appearance = AppearanceSettings(
                    mode = AppearanceMode.Light,
                    dynamicColorEnabled = false,
                ),
            ) {
                SafeCubeTopAppBar(
                    title = "Synthetic vault",
                    navigationIcon = Icons.AutoMirrored.Rounded.ArrowBack,
                    navigationContentDescription = "Navigate back",
                    onNavigationClick = { navigationClicks++ },
                )
            }
        }

        composeRule.onNode(
            hasText("Synthetic vault") and SemanticsMatcher.keyIsDefined(SemanticsProperties.Heading),
        ).assertIsDisplayed()

        val navigation = composeRule.onNodeWithContentDescription("Navigate back")
        navigation
            .assertIsEnabled()
            .assertHasClickAction()
            .assertWidthIsAtLeast(48.dp)
            .assertHeightIsAtLeast(48.dp)
            .assert(hasRequestFocusAction())
            .performSemanticsAction(SemanticsActions.RequestFocus)
            .assertIsFocused()
            .performClick()

        assertEquals(1, navigationClicks)
    }

    @Test
    fun topAppBarKeepsLongTitleAvailableAtTwoHundredPercentFontScale() {
        val longTitle = "Synthetic vault title that must wrap without truncating critical context"

        composeRule.setContent {
            DesignSystemTestKeyboardMode()
            CompositionLocalProvider(LocalDensity provides Density(1f, 2f)) {
                SafeCubeTheme(
                    appearance = AppearanceSettings(
                        mode = AppearanceMode.Light,
                        dynamicColorEnabled = false,
                    ),
                ) {
                    SafeCubeTopAppBar(
                        title = longTitle,
                        modifier = Modifier.testTag("largeFontTopAppBar"),
                    )
                }
            }
        }

        composeRule.onNodeWithTag("largeFontTopAppBar").assertIsDisplayed()
        composeRule.onNodeWithText(longTitle).assertIsDisplayed()
    }

    @Test
    fun bottomNavigationExposesTabRolesLabelsSelectionCallbacksDisabledStateAndTargets() {
        val clicks = mutableListOf<String>()
        val tabRole = SemanticsMatcher.expectValue(SemanticsProperties.Role, Role.Tab)

        composeRule.setContent {
            DesignSystemTestKeyboardMode()
            SafeCubeTheme(
                appearance = AppearanceSettings(
                    mode = AppearanceMode.Light,
                    dynamicColorEnabled = false,
                ),
            ) {
                SafeCubeBottomNavigation(
                    items = listOf(
                        SafeCubeNavigationItem("vault", "Vault", Icons.Rounded.Home),
                        SafeCubeNavigationItem("settings", "Settings", Icons.Rounded.Settings),
                        SafeCubeNavigationItem(
                            id = "disabled",
                            label = "Disabled",
                            icon = Icons.Rounded.Info,
                            enabled = false,
                        ),
                    ),
                    selectedItemId = "vault",
                    onItemClick = clicks::add,
                )
            }
        }

        val vault = composeRule.onNode(hasText("Vault") and tabRole)
        vault
            .assertIsSelected()
            .assertIsEnabled()
            .assertHasClickAction()
            .assertWidthIsAtLeast(48.dp)
            .assertHeightIsAtLeast(48.dp)
            .assert(hasRequestFocusAction())
            .performSemanticsAction(SemanticsActions.RequestFocus)
            .assertIsFocused()
            .performClick()

        composeRule.onNode(hasText("Settings") and tabRole)
            .assertIsNotSelected()
            .assertIsEnabled()
            .assertHasClickAction()
            .performClick()

        composeRule.onNode(hasText("Disabled") and tabRole)
            .assertIsNotSelected()
            .assertIsNotEnabled()
            .assertHasClickAction()
            .assertWidthIsAtLeast(48.dp)
            .assertHeightIsAtLeast(48.dp)
            .performSemanticsAction(SemanticsActions.OnClick)

        assertEquals(listOf("vault", "settings"), clicks)
    }

    @Test
    fun cardsAndRowsExposeOnlyRealActionsStateTextAndFocusTargets() {
        val clicks = mutableListOf<String>()

        composeRule.setContent {
            DesignSystemTestKeyboardMode()
            SafeCubeTheme(
                appearance = AppearanceSettings(
                    mode = AppearanceMode.Light,
                    dynamicColorEnabled = false,
                ),
            ) {
                Column(modifier = Modifier.fillMaxSize()) {
                    SafeCubeCard(
                        modifier = Modifier.testTag("enabledCard"),
                        onClick = { clicks += "card" },
                    ) {
                        androidx.compose.material3.Text("Action card")
                    }
                    SafeCubeCard(
                        modifier = Modifier.testTag("staticCard"),
                        onClick = null,
                    ) {
                        androidx.compose.material3.Text("Static card")
                    }
                    SafeCubeCard(
                        modifier = Modifier.testTag("disabledCard"),
                        onClick = { clicks += "disabledCard" },
                        enabled = false,
                    ) {
                        androidx.compose.material3.Text("Disabled card")
                    }
                    SafeCubeItemRow(
                        title = "Enabled item",
                        modifier = Modifier.testTag("enabledRow"),
                        supportingText = "Synthetic supporting text",
                        icon = Icons.Rounded.Home,
                        stateDescription = "Synced",
                        onClick = { clicks += "row" },
                    )
                    SafeCubeItemRow(
                        title = "Disabled item",
                        modifier = Modifier.testTag("disabledRow"),
                        onClick = { clicks += "disabled" },
                        enabled = false,
                    )
                }
            }
        }

        val buttonRole = SemanticsMatcher.expectValue(SemanticsProperties.Role, Role.Button)
        val enabledCard = composeRule.onNodeWithTag("enabledCard")
        enabledCard
            .assert(buttonRole)
            .assertIsEnabled()
            .assertHasClickAction()
            .assertWidthIsAtLeast(48.dp)
            .assertHeightIsAtLeast(48.dp)
            .assert(hasRequestFocusAction())
            .performSemanticsAction(SemanticsActions.RequestFocus)
            .assertIsFocused()
            .performSemanticsAction(SemanticsActions.OnClick)

        composeRule.onNodeWithTag("staticCard")
            .assertHasNoClickAction()

        composeRule.onNodeWithTag("disabledCard")
            .assert(buttonRole)
            .assertIsNotEnabled()
            .assertHasClickAction()
            .assertWidthIsAtLeast(48.dp)
            .assertHeightIsAtLeast(48.dp)
            .performSemanticsAction(SemanticsActions.OnClick)

        val enabledRow = composeRule.onNodeWithTag("enabledRow")
        enabledRow
            .assert(buttonRole)
            .assert(
                SemanticsMatcher.expectValue(
                    SemanticsProperties.Text,
                    listOf(
                        AnnotatedString("Enabled item"),
                        AnnotatedString("Synthetic supporting text"),
                    ),
                ),
            )
            .assert(SemanticsMatcher.expectValue(SemanticsProperties.StateDescription, "Synced"))
            .assertIsEnabled()
            .assertHasClickAction()
            .assertHeightIsAtLeast(48.dp)
            .assertWidthIsAtLeast(48.dp)
            .assert(hasRequestFocusAction())
            .performSemanticsAction(SemanticsActions.RequestFocus)
            .assertIsFocused()
            .performClick()

        composeRule.onNode(
            SemanticsMatcher.expectValue(SemanticsProperties.StateDescription, "Synced"),
        ).assertIsDisplayed()

        composeRule.onNodeWithText("Synthetic supporting text").assertIsDisplayed()

        composeRule.onNodeWithTag("disabledRow")
            .assert(buttonRole)
            .assertIsNotEnabled()
            .assertHasClickAction()
            .assertHeightIsAtLeast(48.dp)
            .performSemanticsAction(SemanticsActions.OnClick)

        assertEquals(listOf("card", "row"), clicks)
    }
}
