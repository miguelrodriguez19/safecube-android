package com.miguelrodriguez19.safecube

import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.test.assertIsFocused
import androidx.compose.ui.test.junit4.StateRestorationTester
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollTo
import androidx.compose.ui.test.performTextInput
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.miguelrodriguez19.safecube.core.ui.R as UiR
import com.miguelrodriguez19.safecube.core.ui.theme.AppearanceSettings
import com.miguelrodriguez19.safecube.core.ui.theme.SafeCubeTheme
import com.miguelrodriguez19.safecube.feature.auth.presentation.AuthTestTags
import com.miguelrodriguez19.safecube.feature.auth.presentation.login.action.LoginUiAction
import com.miguelrodriguez19.safecube.feature.auth.presentation.login.state.LoginUiState
import com.miguelrodriguez19.safecube.feature.auth.presentation.signup.action.SignupUiAction
import com.miguelrodriguez19.safecube.feature.auth.presentation.signup.state.SignupUiState
import com.miguelrodriguez19.safecube.feature.auth.presentation.state.AuthUiOperationState
import com.miguelrodriguez19.safecube.feature.auth.testsupport.AuthLoginContent
import com.miguelrodriguez19.safecube.feature.auth.testsupport.AuthSignupContent
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class AuthEntryRestorationTest {
    @get:Rule
    val composeRule = createComposeRule()

    @Test
    fun loginRestoresFieldFocusAndHidesRevealAfterCompositionRecreation() {
        val target = StateRestorationTester(composeRule)
        val state = mutableStateOf(LoginUiState(email = "person@example.com"))
        target.setContent {
            SafeCubeTheme(appearance = AppearanceSettings()) {
                AuthLoginContent(
                    uiState = state.value,
                    onAction = { action ->
                        state.value = when (action) {
                            is LoginUiAction.EmailChanged -> state.value.copy(email = action.value)
                            is LoginUiAction.PasswordChanged -> state.value.copy(password = action.value)
                            else -> state.value
                        }
                    },
                    onSignup = {},
                    focusEffectsEnabled = true,
                )
            }
        }
        composeRule.onNodeWithTag(AuthTestTags.LOGIN_PASSWORD)
            .performScrollTo().performClick().performTextInput(SYNTHETIC_INPUT)
        val showLabel = InstrumentationRegistry.getInstrumentation().targetContext
            .getString(UiR.string.design_system_show_secret)
        composeRule.onNodeWithContentDescription(showLabel).performClick()

        target.emulateSavedInstanceStateRestore()

        composeRule.onNodeWithTag(AuthTestTags.LOGIN_PASSWORD).assertIsFocused()
        composeRule.onNodeWithContentDescription(showLabel).assertExists()
        composeRule.runOnIdle {
            assertEquals("person@example.com", state.value.email)
            assertEquals(SYNTHETIC_INPUT, state.value.password)
        }
    }

    @Test
    fun signupRestoresConfirmationFocusWithoutSavingCredentialValues() {
        val target = StateRestorationTester(composeRule)
        val state = mutableStateOf(SignupUiState(email = "person@example.com"))
        target.setContent {
            SafeCubeTheme(appearance = AppearanceSettings()) {
                AuthSignupContent(
                    uiState = state.value,
                    onAction = { action ->
                        state.value = when (action) {
                            is SignupUiAction.EmailChanged -> state.value.copy(email = action.value)
                            is SignupUiAction.PasswordChanged -> state.value.copy(password = action.value)
                            is SignupUiAction.ConfirmPasswordChanged ->
                                state.value.copy(confirmPassword = action.value)
                            else -> state.value
                        }
                    },
                    onLogin = {},
                    focusEffectsEnabled = true,
                )
            }
        }
        composeRule.onNodeWithTag(AuthTestTags.SIGNUP_CONFIRM_PASSWORD)
            .performScrollTo().performClick().performTextInput(SYNTHETIC_INPUT)

        // The external holder models a live ViewModel clearing a secret before recreation.
        composeRule.runOnIdle { state.value = state.value.copy(confirmPassword = "") }
        target.emulateSavedInstanceStateRestore()

        composeRule.onNodeWithTag(AuthTestTags.SIGNUP_CONFIRM_PASSWORD).assertIsFocused()
        composeRule.runOnIdle { assertEquals("", state.value.confirmPassword) }
    }

    @Test
    fun signupRestoresChosenFieldRatherThanReplayingAnExistingValidationError() {
        val target = StateRestorationTester(composeRule)
        target.setContent {
            SafeCubeTheme(appearance = AppearanceSettings()) {
                AuthSignupContent(
                    uiState = SignupUiState(
                        operationState = AuthUiOperationState.ValidationError,
                        emailErrorRes = UiR.string.email_is_required,
                        confirmPasswordErrorRes = UiR.string.confirm_password_is_required,
                    ),
                    onAction = {},
                    onLogin = {},
                    focusEffectsEnabled = true,
                )
            }
        }
        composeRule.onNodeWithTag(AuthTestTags.SIGNUP_CONFIRM_PASSWORD)
            .performScrollTo().performClick().assertIsFocused()

        target.emulateSavedInstanceStateRestore()

        composeRule.onNodeWithTag(AuthTestTags.SIGNUP_CONFIRM_PASSWORD).assertIsFocused()
    }

    private companion object {
        const val SYNTHETIC_INPUT = "synthetic-input-not-a-credential"
    }
}
