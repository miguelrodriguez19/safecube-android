package com.miguelrodriguez19.safecube

import android.view.WindowManager
import androidx.compose.ui.test.assertCountEquals
import androidx.compose.ui.test.assertHasClickAction
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import androidx.test.platform.app.InstrumentationRegistry
import com.miguelrodriguez19.safecube.app.entrypoint.MainActivity
import com.miguelrodriguez19.safecube.feature.auth.presentation.AuthTestTags
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.rules.RuleChain
import org.junit.rules.TestRule
import org.junit.runner.Description
import org.junit.runner.RunWith
import org.junit.runners.model.Statement

@LargeTest
@RunWith(AndroidJUnit4::class)
class MainActivitySmokeTest {
    private val loggedOutStateRule = LoggedOutStateRule()
    private val composeRule = createAndroidComposeRule<MainActivity>()

    @get:Rule
    val rules: RuleChain = RuleChain
        .outerRule(loggedOutStateRule)
        .around(composeRule)

    @Test
    fun cleanLaunchDisplaysWelcomeAndLoginAction() {
        composeRule
            .onNodeWithTag(AuthTestTags.WELCOME_SCREEN)
            .assertIsDisplayed()
        composeRule
            .onNodeWithTag(AuthTestTags.WELCOME_LOGIN_ACTION)
            .assertIsDisplayed()
            .assertHasClickAction()
    }

    @Test
    fun loginActionNavigatesToLoginForm() {
        composeRule
            .onNodeWithTag(AuthTestTags.WELCOME_LOGIN_ACTION)
            .performClick()

        composeRule
            .onNodeWithTag(AuthTestTags.LOGIN_SCREEN)
            .assertIsDisplayed()
    }

    @Test
    fun activeWindowRejectsScreenshotsAndRecentsThumbnails() {
        assertTrue(
            composeRule.activity.window.attributes.flags and
                WindowManager.LayoutParams.FLAG_SECURE != 0,
        )
    }

    @Test
    fun loginPasswordIsVisuallyMasked() {
        composeRule
            .onNodeWithTag(AuthTestTags.WELCOME_LOGIN_ACTION)
            .performClick()
        composeRule
            .onNodeWithText("Password")
            .performTextInput("test-password")

        composeRule
            .onNodeWithText("•••••••••••••")
            .assertIsDisplayed()
    }

    @Test
    fun signupPasswordsAreVisuallyMasked() {
        composeRule
            .onNodeWithTag(AuthTestTags.WELCOME_LOGIN_ACTION)
            .performClick()
        composeRule
            .onNodeWithText("Go to Signup")
            .performClick()
        composeRule
            .onNodeWithText("Password")
            .performTextInput("test-password")
        composeRule
            .onNodeWithText("Confirm password")
            .performTextInput("test-password")

        composeRule
            .onAllNodesWithText("•••••••••••••")
            .assertCountEquals(2)
    }
}

private class LoggedOutStateRule : TestRule {
    override fun apply(base: Statement, description: Description): Statement =
        object : Statement() {
            override fun evaluate() {
                val targetContext = InstrumentationRegistry
                    .getInstrumentation()
                    .targetContext
                targetContext.deleteSharedPreferences(AUTH_PREFERENCES_NAME)
                base.evaluate()
            }
        }

    private companion object {
        const val AUTH_PREFERENCES_NAME = "auth_encrypted_preferences"
    }
}
