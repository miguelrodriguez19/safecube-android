package com.miguelrodriguez19.safecube

import android.content.res.Configuration
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.autofill.ContentType
import androidx.compose.ui.autofill.FillableData
import androidx.compose.ui.autofill.createFromText
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.semantics.SemanticsActions
import androidx.compose.ui.semantics.SemanticsProperties
import androidx.compose.ui.test.assertCountEquals
import androidx.compose.ui.test.assert
import androidx.compose.ui.test.SemanticsMatcher
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsFocused
import androidx.compose.ui.test.assertIsNotFocused
import androidx.compose.ui.test.assertIsNotEnabled
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onAllNodesWithTag
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performScrollTo
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performImeAction
import androidx.compose.ui.test.performSemanticsAction
import androidx.compose.ui.test.performTextInput
import androidx.compose.ui.unit.Density
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import java.util.Locale
import com.miguelrodriguez19.safecube.core.ui.R as UiR
import com.miguelrodriguez19.safecube.core.ui.theme.AppearanceMode
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
import com.miguelrodriguez19.safecube.feature.auth.presentation.welcome.ui.WelcomeScreen
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class AuthEntrySemanticsTest {
    @get:Rule
    val composeRule = createComposeRule()

    @Test
    fun loginFieldsAcceptAutofillAndKeepSecretMasked() {
        var observedEmail = ""
        var observedPassword = ""
        composeRule.setContent { AuthTestTheme {
            var state by remember { mutableStateOf(LoginUiState()) }
            AuthLoginContent(
                uiState = state,
                focusEffectsEnabled = true,
                onSignup = {},
                onAction = { action ->
                    when (action) {
                        is LoginUiAction.EmailChanged -> {
                            observedEmail = action.value
                            state = state.copy(email = action.value)
                        }
                        is LoginUiAction.PasswordChanged -> {
                            observedPassword = action.value
                            state = state.copy(password = action.value)
                        }
                        else -> Unit
                    }
                },
            )
        } }
        composeRule.onNodeWithTag(AuthTestTags.LOGIN_EMAIL).assertIsNotFocused()
        composeRule.onNodeWithTag(AuthTestTags.LOGIN_PASSWORD).assertIsNotFocused()
        // Combined ContentType instances have no public value-equality contract.
        composeRule.onNodeWithTag(AuthTestTags.LOGIN_EMAIL)
            .assert(SemanticsMatcher.keyIsDefined(SemanticsProperties.ContentType))
        assertEquals(
            ContentType.Password,
            composeRule.onNodeWithTag(AuthTestTags.LOGIN_PASSWORD).fetchSemanticsNode().config[
                SemanticsProperties.ContentType
            ],
        )

        composeRule.onNodeWithTag(AuthTestTags.LOGIN_EMAIL)
            .performSemanticsAction(SemanticsActions.OnFillData) {
                it(requireNotNull(FillableData.createFromText("person@example.com")))
            }
        composeRule.onNodeWithTag(AuthTestTags.LOGIN_PASSWORD)
            .performSemanticsAction(SemanticsActions.OnFillData) {
                it(requireNotNull(FillableData.createFromText("synthetic-password")))
            }

        assertEquals("person@example.com", observedEmail)
        assertEquals("synthetic-password", observedPassword)
        composeRule.onAllNodesWithText("synthetic-password", useUnmergedTree = true).assertCountEquals(0)
        composeRule.onNodeWithTag(AuthTestTags.LOGIN_PASSWORD).assertIsDisplayed()
    }

    @Test
    fun loginImeNextMovesToPasswordAndDoneSubmitsOnce() {
        var submits = 0
        composeRule.setContent { AuthTestTheme {
            var state by remember { mutableStateOf(LoginUiState()) }
            AuthLoginContent(
                uiState = state,
                focusEffectsEnabled = true,
                onSignup = {},
                onAction = { action ->
                    when (action) {
                        is LoginUiAction.EmailChanged -> state = state.copy(email = action.value)
                        is LoginUiAction.PasswordChanged -> state = state.copy(password = action.value)
                        LoginUiAction.Submit -> submits++
                        else -> Unit
                    }
                },
            )
        } }

        composeRule.onNodeWithTag(AuthTestTags.LOGIN_EMAIL).performImeAction()
        composeRule.onNodeWithTag(AuthTestTags.LOGIN_PASSWORD).assertIsFocused().performImeAction()

        assertEquals(1, submits)
    }

    @Test
    fun loginLoadingDisablesSubmitAndRouteSwitch() {
        var submits = 0
        var routeChanges = 0
        composeRule.setContent { AuthTestTheme {
            AuthLoginContent(
                uiState = LoginUiState(operationState = AuthUiOperationState.Loading),
                focusEffectsEnabled = true,
                onSignup = { routeChanges++ },
                onAction = { if (it == LoginUiAction.Submit) submits++ },
            )
        } }

        composeRule.onNodeWithTag(AuthTestTags.LOGIN_SUBMIT).assertIsNotEnabled()
            .performSemanticsAction(SemanticsActions.OnClick) { it() }
        composeRule.onNodeWithTag(AuthTestTags.LOGIN_SIGNUP_ACTION).assertIsNotEnabled()
            .performSemanticsAction(SemanticsActions.OnClick) { it() }
        composeRule.onNodeWithTag(AuthTestTags.LOGIN_PASSWORD).assertIsNotEnabled()
            .performSemanticsAction(SemanticsActions.OnImeAction) { it() }

        assertEquals(0, submits)
        assertEquals(0, routeChanges)
    }

    @Test
    fun loginRetryableStateOffersOnlyRetryAction() {
        var retries = 0
        var submits = 0
        composeRule.setContent { AuthTestTheme {
            AuthLoginContent(
                uiState = LoginUiState(
                    operationState = AuthUiOperationState.OfflineOrTimeout,
                    errorMessageRes = UiR.string.auth_error_network_retryable,
                ),
                focusEffectsEnabled = true,
                onSignup = {},
                onAction = {
                    when (it) {
                        LoginUiAction.Retry -> retries++
                        LoginUiAction.Submit -> submits++
                        else -> Unit
                    }
                },
            )
        } }

        composeRule.onAllNodesWithTag(AuthTestTags.LOGIN_SUBMIT).assertCountEquals(0)
        composeRule.onNodeWithText(targetContext().getString(UiR.string.design_system_feedback_retry))
            .performScrollTo()
            .assertIsDisplayed()
            .performClick()

        assertEquals(1, retries)
        assertEquals(0, submits)
    }

    @Test
    fun loginTerminalStateHasNoRetryAndCorrectedEmailEnablesSubmit() {
        var submits = 0
        composeRule.setContent { AuthTestTheme {
            var state by remember {
                mutableStateOf(LoginUiState(operationState = AuthUiOperationState.TerminalError))
            }
            AuthLoginContent(
                uiState = state,
                focusEffectsEnabled = true,
                onSignup = {},
                onAction = { action ->
                    when (action) {
                        is LoginUiAction.EmailChanged -> state = state.copy(
                            email = action.value,
                            operationState = AuthUiOperationState.Idle,
                        )
                        LoginUiAction.Submit -> submits++
                        else -> Unit
                    }
                },
            )
        } }

        composeRule.onAllNodesWithText(targetContext().getString(UiR.string.design_system_feedback_retry))
            .assertCountEquals(0)
        composeRule.onNodeWithTag(AuthTestTags.LOGIN_EMAIL).performTextInput("person@example.com")
        composeRule.onNodeWithTag(AuthTestTags.LOGIN_SUBMIT).assertIsDisplayed().performClick()

        assertEquals(1, submits)
    }

    @Test
    fun loginValidationFocusesFirstInvalidField() {
        composeRule.setContent { AuthTestTheme {
            AuthLoginContent(
                uiState = LoginUiState(
                    operationState = AuthUiOperationState.ValidationError,
                    emailErrorRes = UiR.string.email_is_required,
                    passwordErrorRes = UiR.string.password_is_required,
                ),
                focusEffectsEnabled = true,
                onSignup = {},
                onAction = {},
            )
        } }

        waitForFocus(AuthTestTags.LOGIN_EMAIL)
        composeRule.onNodeWithTag(AuthTestTags.LOGIN_EMAIL).assertIsFocused()
        assertEquals(
            targetContext().getString(UiR.string.email_is_required),
            composeRule.onNodeWithTag(AuthTestTags.LOGIN_EMAIL).fetchSemanticsNode().config[
                SemanticsProperties.StateDescription
            ],
        )
    }

    @Test
    fun loginInvalidCredentialsReturnsFocusToPassword() {
        composeRule.setContent { AuthTestTheme {
            AuthLoginContent(
                uiState = LoginUiState(operationState = AuthUiOperationState.InvalidCredentials),
                focusEffectsEnabled = true,
                onSignup = {},
                onAction = {},
            )
        } }

        waitForFocus(AuthTestTags.LOGIN_PASSWORD)
        composeRule.onNodeWithTag(AuthTestTags.LOGIN_PASSWORD).assertIsFocused()
    }

    @Test
    fun signupAccountFieldsAcceptAutofill() {
        var observedEmail = ""
        var observedPassword = ""
        var observedConfirmation = ""
        composeRule.setContent { AuthTestTheme {
            var state by remember { mutableStateOf(SignupUiState()) }
            AuthSignupContent(
                uiState = state,
                focusEffectsEnabled = true,
                onLogin = {},
                onAction = { action ->
                    when (action) {
                        is SignupUiAction.EmailChanged -> {
                            observedEmail = action.value
                            state = state.copy(email = action.value)
                        }
                        is SignupUiAction.PasswordChanged -> {
                            observedPassword = action.value
                            state = state.copy(password = action.value)
                        }
                        is SignupUiAction.ConfirmPasswordChanged -> {
                            observedConfirmation = action.value
                            state = state.copy(confirmPassword = action.value)
                        }
                        else -> Unit
                    }
                },
            )
        } }
        composeRule.onNodeWithTag(AuthTestTags.SIGNUP_EMAIL)
            .assert(SemanticsMatcher.keyIsDefined(SemanticsProperties.ContentType))
        assertEquals(
            ContentType.NewPassword,
            composeRule.onNodeWithTag(AuthTestTags.SIGNUP_PASSWORD).fetchSemanticsNode().config[
                SemanticsProperties.ContentType
            ],
        )
        assertEquals(
            ContentType.NewPassword,
            composeRule.onNodeWithTag(AuthTestTags.SIGNUP_CONFIRM_PASSWORD).fetchSemanticsNode().config[
                SemanticsProperties.ContentType
            ],
        )

        composeRule.onNodeWithTag(AuthTestTags.SIGNUP_EMAIL)
            .performSemanticsAction(SemanticsActions.OnFillData) {
                it(requireNotNull(FillableData.createFromText("person@example.com")))
            }
        composeRule.onNodeWithTag(AuthTestTags.SIGNUP_PASSWORD)
            .performSemanticsAction(SemanticsActions.OnFillData) {
                it(requireNotNull(FillableData.createFromText("synthetic-password")))
            }
        composeRule.onNodeWithTag(AuthTestTags.SIGNUP_CONFIRM_PASSWORD)
            .performSemanticsAction(SemanticsActions.OnFillData) {
                it(requireNotNull(FillableData.createFromText("synthetic-password")))
            }

        assertEquals("person@example.com", observedEmail)
        assertEquals("synthetic-password", observedPassword)
        assertEquals("synthetic-password", observedConfirmation)
        composeRule.onAllNodesWithText("synthetic-password", useUnmergedTree = true).assertCountEquals(0)
    }

    @Test
    fun signupImeNextMovesThroughFieldsAndDoneSubmitsOnce() {
        var submits = 0
        composeRule.setContent { AuthTestTheme {
            var state by remember { mutableStateOf(SignupUiState()) }
            AuthSignupContent(
                uiState = state,
                focusEffectsEnabled = true,
                onLogin = {},
                onAction = { action ->
                    when (action) {
                        is SignupUiAction.EmailChanged -> state = state.copy(email = action.value)
                        is SignupUiAction.PasswordChanged -> state = state.copy(password = action.value)
                        is SignupUiAction.ConfirmPasswordChanged -> state = state.copy(confirmPassword = action.value)
                        SignupUiAction.Submit -> submits++
                        else -> Unit
                    }
                },
            )
        } }

        composeRule.onNodeWithTag(AuthTestTags.SIGNUP_EMAIL).performImeAction()
        composeRule.onNodeWithTag(AuthTestTags.SIGNUP_PASSWORD).performImeAction()
        composeRule.onNodeWithTag(AuthTestTags.SIGNUP_CONFIRM_PASSWORD).assertIsFocused().performImeAction()

        assertEquals(1, submits)
    }

    @Test
    fun signupLoadingBlocksSubmitAndLoginNavigation() {
        var submits = 0
        var routeChanges = 0
        composeRule.setContent { AuthTestTheme {
            AuthSignupContent(
                uiState = SignupUiState(operationState = AuthUiOperationState.Loading),
                focusEffectsEnabled = true,
                onLogin = { routeChanges++ },
                onAction = { if (it == SignupUiAction.Submit) submits++ },
            )
        } }

        composeRule.onNodeWithTag(AuthTestTags.SIGNUP_SUBMIT).assertIsNotEnabled()
            .performSemanticsAction(SemanticsActions.OnClick) { it() }
        composeRule.onNodeWithTag(AuthTestTags.SIGNUP_LOGIN_ACTION).assertIsNotEnabled()
            .performSemanticsAction(SemanticsActions.OnClick) { it() }
        composeRule.onNodeWithTag(AuthTestTags.SIGNUP_CONFIRM_PASSWORD).assertIsNotEnabled()
            .performSemanticsAction(SemanticsActions.OnImeAction) { it() }

        assertEquals(0, submits)
        assertEquals(0, routeChanges)
    }

    @Test
    fun loginAndSignupCriticalActionsRemainReachableAtTwoHundredPercentSpanish() {
        composeRule.setContent { AuthTestTheme(fontScale = 2f, localeTag = "es") {
            var showSignup by remember { mutableStateOf(false) }
            if (showSignup) {
                AuthSignupContent(
                    uiState = SignupUiState(),
                    focusEffectsEnabled = true,
                    onLogin = { showSignup = false },
                    onAction = {},
                )
            } else {
                AuthLoginContent(
                    uiState = LoginUiState(),
                    focusEffectsEnabled = true,
                    onSignup = { showSignup = true },
                    onAction = {},
                )
            }
        } }

        composeRule.onNodeWithTag(AuthTestTags.LOGIN_SUBMIT).performScrollTo().assertIsDisplayed()
        composeRule.onNodeWithTag(AuthTestTags.LOGIN_SIGNUP_ACTION)
            .performScrollTo()
            .assertIsDisplayed()
            .performClick()
        composeRule.onNodeWithTag(AuthTestTags.SIGNUP_SUBMIT).performScrollTo().assertIsDisplayed()
        composeRule.onNodeWithTag(AuthTestTags.SIGNUP_LOGIN_ACTION)
            .performScrollTo()
            .assertIsDisplayed()
    }

    @Test
    fun welcomeCriticalActionsRemainReachableAtTwoHundredPercentSpanish() {
        var logins = 0
        var signups = 0
        composeRule.setContent { AuthTestTheme(fontScale = 2f, localeTag = "es") {
            WelcomeScreen(onLogin = { logins++ }, onSignup = { signups++ })
        } }

        composeRule.onNodeWithTag(AuthTestTags.WELCOME_LOGIN_ACTION)
            .performScrollTo().assertIsDisplayed().performClick()
        composeRule.onNodeWithTag(AuthTestTags.WELCOME_SIGNUP_ACTION)
            .performScrollTo().assertIsDisplayed().performClick()

        assertEquals(1, logins)
        assertEquals(1, signups)
    }

    @Test
    fun signupValidationFocusesEmailAndDoesNotStealFocusWhileCorrectingIt() {
        composeRule.setContent { AuthTestTheme {
            var state by remember {
                mutableStateOf(SignupUiState(
                    operationState = AuthUiOperationState.ValidationError,
                    emailErrorRes = UiR.string.email_is_required,
                    passwordErrorRes = UiR.string.password_is_required,
                    confirmPasswordErrorRes = UiR.string.confirm_password_is_required,
                ))
            }
            AuthSignupContent(
                uiState = state,
                focusEffectsEnabled = true,
                onLogin = {},
                onAction = { action ->
                    if (action is SignupUiAction.EmailChanged) {
                        state = state.copy(
                            email = action.value,
                            emailErrorRes = null,
                            operationState = AuthUiOperationState.Idle,
                        )
                    }
                },
            )
        } }

        waitForFocus(AuthTestTags.SIGNUP_EMAIL)
        composeRule.onNodeWithTag(AuthTestTags.SIGNUP_EMAIL).performTextInput("person@example.com")

        composeRule.onNodeWithTag(AuthTestTags.SIGNUP_EMAIL).assertIsFocused()
    }

    @Test
    fun signupExistingAccountOffersLoginWithoutRetry() {
        var logins = 0
        composeRule.setContent { AuthTestTheme {
            AuthSignupContent(
                uiState = SignupUiState(
                    operationState = AuthUiOperationState.AccountAlreadyExists,
                    errorMessageRes = UiR.string.auth_error_account_exists,
                ),
                onAction = {},
                onLogin = { logins++ },
            )
        } }

        composeRule.onAllNodesWithText(targetContext().getString(UiR.string.design_system_feedback_retry))
            .assertCountEquals(0)
        composeRule.onNodeWithText(targetContext().getString(UiR.string.auth_error_account_exists))
            .performScrollTo().assertIsDisplayed()
        composeRule.onNodeWithTag(AuthTestTags.SIGNUP_LOGIN_ACTION).performScrollTo().performClick()

        assertEquals(1, logins)
    }

    @Test
    fun signupServiceErrorOffersOneRetryThenBlocksDuplicatesWhileLoading() {
        var retries = 0
        composeRule.setContent { AuthTestTheme {
            var state by remember {
                mutableStateOf(SignupUiState(
                    operationState = AuthUiOperationState.ServiceUnavailable,
                    errorMessageRes = UiR.string.auth_error_service_unavailable_retryable,
                ))
            }
            AuthSignupContent(
                uiState = state,
                onLogin = {},
                onAction = { action ->
                    if (action == SignupUiAction.Retry) {
                        retries++
                        state = state.copy(operationState = AuthUiOperationState.Loading)
                    }
                },
            )
        } }

        composeRule.onAllNodesWithTag(AuthTestTags.SIGNUP_SUBMIT).assertCountEquals(0)
        composeRule.onNodeWithText(targetContext().getString(UiR.string.design_system_feedback_retry))
            .performScrollTo().performClick()
        composeRule.onNodeWithTag(AuthTestTags.SIGNUP_SUBMIT).assertIsNotEnabled()
            .performSemanticsAction(SemanticsActions.OnClick) { it() }

        assertEquals(1, retries)
    }

    private fun waitForFocus(tag: String) {
        composeRule.waitUntil(timeoutMillis = 5_000) {
            composeRule.onNodeWithTag(tag).fetchSemanticsNode().config[SemanticsProperties.Focused]
        }
    }

    private fun targetContext() =
        InstrumentationRegistry.getInstrumentation().targetContext
}

@Composable
private fun AuthTestTheme(
    fontScale: Float = 1f,
    localeTag: String = "en",
    content: @Composable () -> Unit,
) {
    val configuration = Configuration(LocalConfiguration.current).apply {
        setLocale(Locale.forLanguageTag(localeTag))
        this.fontScale = fontScale
    }
    val context = LocalContext.current.createConfigurationContext(configuration)
    val density = LocalDensity.current
    androidx.compose.runtime.CompositionLocalProvider(
        LocalConfiguration provides configuration,
        LocalContext provides context,
        LocalDensity provides Density(density.density, fontScale),
    ) {
        SafeCubeTheme(
            appearance = AppearanceSettings(
                mode = AppearanceMode.Light,
                dynamicColorEnabled = false,
            ),
            content = content,
        )
    }
}
