package com.miguelrodriguez19.safecube

import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.SemanticsProperties
import androidx.compose.ui.test.SemanticsMatcher
import androidx.compose.ui.test.assertHasClickAction
import androidx.compose.ui.test.assertIsEnabled
import androidx.compose.ui.test.assertIsNotEnabled
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollTo
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.miguelrodriguez19.safecube.app.presentation.theme.SafecubeandroidTheme
import com.miguelrodriguez19.safecube.app.testsupport.visual.VisualRegressionCanaryAction
import com.miguelrodriguez19.safecube.app.testsupport.visual.VisualRegressionCanaryFixtures
import com.miguelrodriguez19.safecube.app.testsupport.visual.VisualRegressionCanarySurface
import com.miguelrodriguez19.safecube.core.ui.R
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
// Keep this class before QuickUnlockDeviceCredentialTest in AndroidJUnitRunner discovery order.
// The API 30 locksettings tests intentionally finish the suite after restoring the device credential.
class ComposeVisualRegressionCanarySemanticsTest {
    @get:Rule
    val composeRule = createComposeRule()

    @Test
    fun visualFixtureExposesRolesLabelsStatesAndActions() {
        val actions = mutableListOf<VisualRegressionCanaryAction>()
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        val login = context.getString(R.string.login)
        val syncing = context.getString(R.string.sync_status_syncing)
        val retry = context.getString(R.string.retry)
        val draft = context.getString(R.string.draft_badge_label)
        val conflict = context.getString(R.string.sync_status_conflict)
        val buttonRole = SemanticsMatcher.expectValue(SemanticsProperties.Role, Role.Button)

        composeRule.setContent {
            SafecubeandroidTheme(dynamicColor = false) {
                VisualRegressionCanarySurface(
                    fixture = VisualRegressionCanaryFixtures.infrastructure,
                    onAction = actions::add,
                )
            }
        }

        composeRule.onNode(hasText(login) and buttonRole)
            .assertIsEnabled()
            .assertHasClickAction()
            .performClick()
        composeRule.onNode(SemanticsMatcher.expectValue(SemanticsProperties.StateDescription, draft))
        composeRule.onNode(SemanticsMatcher.expectValue(SemanticsProperties.StateDescription, conflict))
        composeRule.onNode(hasText(syncing) and buttonRole)
            .assertIsNotEnabled()
            .assertHasClickAction()
        composeRule.onNode(hasText(retry) and buttonRole)
            .performScrollTo()
            .assertIsEnabled()
            .assertHasClickAction()
            .performClick()

        assertEquals(
            listOf(
                VisualRegressionCanaryAction.SubmitAuth,
                VisualRegressionCanaryAction.RetrySync,
            ),
            actions,
        )
    }
}
