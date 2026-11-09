package com.miguelrodriguez19.safecube

import android.content.Context
import androidx.activity.ComponentActivity
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.focusable
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.MotionDurationScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.SemanticsProperties
import androidx.compose.ui.test.SemanticsMatcher
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsFocused
import androidx.compose.ui.test.assertIsNotSelected
import androidx.compose.ui.test.assertIsSelected
import androidx.compose.ui.test.assertCountEquals
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.onAllNodesWithTag
import androidx.compose.ui.test.assert
import androidx.compose.ui.test.performClick
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.dp
import androidx.navigation3.runtime.NavEntry
import androidx.navigation3.runtime.NavKey
import com.miguelrodriguez19.safecube.app.presentation.navigation.host.SafeCubeNavigationHost
import com.miguelrodriguez19.safecube.app.presentation.navigation.route.Routes
import com.miguelrodriguez19.safecube.app.presentation.ui.SplashGateScreen
import com.miguelrodriguez19.safecube.core.auth.domain.model.SessionState
import com.miguelrodriguez19.safecube.core.auth.domain.model.SessionTerminationReason
import com.miguelrodriguez19.safecube.core.ui.component.SafeCubeScaffold
import com.miguelrodriguez19.safecube.core.ui.component.SafeCubeTopAppBar
import com.miguelrodriguez19.safecube.core.ui.theme.AppearanceMode
import com.miguelrodriguez19.safecube.core.ui.theme.AppearanceSettings
import com.miguelrodriguez19.safecube.core.ui.theme.SafeCubeTheme
import com.miguelrodriguez19.safecube.core.vault.domain.model.VaultState
import com.miguelrodriguez19.safecube.feature.vault.presentation.shared.navigation.AppTab
import com.miguelrodriguez19.safecube.feature.vault.presentation.shared.navigation.NavigationBar
import java.util.concurrent.atomic.AtomicBoolean
import java.util.concurrent.atomic.AtomicReference
import kotlin.coroutines.CoroutineContext
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry

@RunWith(AndroidJUnit4::class)
class SafeCubeShellSemanticsTest {
    private val motionDurationScale = MutableMotionDurationScale()
    private val composedDestinations = mutableSetOf<String>()

    @get:Rule
    val composeRule = createComposeRule(effectContext = motionDurationScale)

    @Test
    fun navigationBarExposesLocalizedLabelsSelectionAndCallbacks() {
        val selectedTabs = mutableListOf<AppTab>()
        val selectedTab = mutableStateOf(AppTab.Vault)
        val tabRole = SemanticsMatcher.expectValue(SemanticsProperties.Role, Role.Tab)

        composeRule.setContent {
            SafeCubeTheme(
                appearance = AppearanceSettings(
                    mode = AppearanceMode.Light,
                    dynamicColorEnabled = false,
                ),
            ) {
                NavigationBar(
                    selectedTab = selectedTab.value,
                    onTabSelected = {
                        selectedTabs += it
                        selectedTab.value = it
                    },
                )
            }
        }

        composeRule.onNodeWithText("Vault")
            .assert(tabRole)
            .assertIsSelected()
            .performClick()
        composeRule.onNodeWithText("Settings")
            .assert(tabRole)
            .assertIsNotSelected()
            .performClick()
            .assertIsSelected()
            .performClick()
        composeRule.onNodeWithText("Vault").assertIsNotSelected()

        assertEquals(listOf(AppTab.Settings), selectedTabs)
        composeRule.onAllNodes(tabRole).assertCountEquals(AppTab.entries.size)
    }

    @Test
    fun scaffoldOwnsSafeDrawingAndContentSpacingAtTwoHundredPercentFontScale() {
        composeRule.setContent {
            CompositionDensity(fontScale = 2f) {
                SafeCubeTheme(
                    appearance = AppearanceSettings(
                        mode = AppearanceMode.Light,
                        dynamicColorEnabled = false,
                    ),
                ) {
                    SafeCubeScaffold(
                        modifier = Modifier.testTag(SCAFFOLD_TAG),
                        topBar = {
                            SafeCubeTopAppBar(
                                title = "Synthetic shell",
                                modifier = Modifier.testTag(TOP_BAR_TAG),
                            )
                        },
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .testTag(CONTENT_TAG)
                                .verticalScroll(rememberScrollState()),
                            verticalArrangement = Arrangement.spacedBy(16.dp),
                        ) {
                            Text("Synthetic shell content that remains available at large text")
                            Text("Synthetic action remains in the scrollable content")
                        }
                    }
                }
            }
        }

        composeRule.onNodeWithTag(TOP_BAR_TAG).assertIsDisplayed()
        composeRule.onNodeWithTag(CONTENT_TAG).assertIsDisplayed()
        composeRule.onNodeWithText("Synthetic shell content that remains available at large text")
            .assertIsDisplayed()

        val topBarBounds = composeRule.onNodeWithTag(TOP_BAR_TAG)
            .fetchSemanticsNode()
            .boundsInRoot
        val contentBounds = composeRule.onNodeWithTag(CONTENT_TAG)
            .fetchSemanticsNode()
            .boundsInRoot
        val contentSpacing = with(composeRule.density) { 16.dp.toPx() }
        assertEquals(contentSpacing, contentBounds.top - topBarBounds.bottom, 1f)
        val scaffoldBounds = composeRule.onNodeWithTag(SCAFFOLD_TAG)
            .fetchSemanticsNode().boundsInRoot
        assertEquals(contentSpacing, contentBounds.left - scaffoldBounds.left, 1f)
        assertEquals(contentSpacing, scaffoldBounds.right - contentBounds.right, 1f)
    }

    @Test
    fun splashGateExposesOnlyLocalizedLoadingState() {
        val loadingMessage = InstrumentationRegistry.getInstrumentation().targetContext
            .getString(com.miguelrodriguez19.safecube.R.string.shell_loading_session)

        composeRule.setContent {
            SafeCubeTheme(
                appearance = AppearanceSettings(
                    mode = AppearanceMode.Dark,
                    dynamicColorEnabled = false,
                ),
            ) {
                SplashGateScreen()
            }
        }

        composeRule.onNodeWithText(loadingMessage).assertIsDisplayed()
    }

    @Test
    fun navigationHostDelegatesBackToProvidedCallback() {
        val backCalls = mutableListOf<Unit>()
        val backStack = mutableStateListOf<NavKey>(Routes.Vault, Routes.Settings)
        val activity = AtomicReference<ComponentActivity>()

        composeRule.setContent {
            val context = LocalContext.current
            SideEffect {
                activity.set(context.findComponentActivity())
            }
            SafeCubeTheme {
                SafeCubeNavigationHost(
                    backStack = backStack,
                    sessionState = SessionState.LoggedInVaultLocked,
                    vaultState = VaultState.Unlocked,
                    onBack = { backCalls += Unit },
                    entryProvider = syntheticEntryProvider(),
                    instantTransition = true,
                )
            }
        }

        composeRule.runOnIdle {
            activity.get().onBackPressedDispatcher.onBackPressed()
        }

        assertEquals(1, backCalls.size)
    }

    @Test
    fun navigationHostDispatchesSingletonVaultBackThroughActivityDispatcher() {
        val backCalls = mutableListOf<Unit>()
        val backStack = mutableStateListOf<NavKey>(Routes.Vault)
        val activity = AtomicReference<ComponentActivity>()

        composeRule.setContent {
            val context = LocalContext.current
            SideEffect {
                activity.set(context.findComponentActivity())
            }
            SafeCubeTheme {
                SafeCubeNavigationHost(
                    backStack = backStack,
                    sessionState = SessionState.LoggedInVaultLocked,
                    vaultState = VaultState.Unlocked,
                    onBack = { backCalls += Unit },
                    entryProvider = syntheticEntryProvider(),
                    instantTransition = true,
                )
            }
        }

        composeRule.runOnIdle {
            activity.get().onBackPressedDispatcher.onBackPressed()
        }

        assertEquals(1, backCalls.size)
    }

    @Test
    fun settingsBackReturnsToVaultRootThenRootBackDispatchesAgain() {
        val backCalls = mutableListOf<Unit>()
        val backStack = mutableStateListOf<NavKey>(Routes.Vault, Routes.Settings)
        val activity = AtomicReference<ComponentActivity>()

        composeRule.setContent {
            val context = LocalContext.current
            SideEffect {
                activity.set(context.findComponentActivity())
            }
            SafeCubeTheme {
                SafeCubeNavigationHost(
                    backStack = backStack,
                    sessionState = SessionState.LoggedInVaultLocked,
                    vaultState = VaultState.Unlocked,
                    onBack = {
                        backCalls += Unit
                        if (backStack.lastOrNull() == Routes.Settings) {
                            backStack.removeLastOrNull()
                        }
                    },
                    entryProvider = syntheticEntryProvider(),
                    instantTransition = true,
                )
            }
        }

        composeRule.runOnIdle {
            activity.get().onBackPressedDispatcher.onBackPressed()
        }
        composeRule.waitForIdle()
        composeRule.runOnIdle {
            activity.get().onBackPressedDispatcher.onBackPressed()
        }

        assertEquals(2, backCalls.size)
    }

    @Test
    fun navigationHostRendersThreeSyntheticEntriesThroughInjectedProvider() {
        val providedKeys = mutableListOf<NavKey>()
        val backStack = mutableStateListOf<NavKey>(Routes.Vault)

        composeRule.setContent {
            SafeCubeTheme {
                SafeCubeNavigationHost(
                    backStack = backStack,
                    sessionState = SessionState.LoggedInVaultLocked,
                    vaultState = VaultState.Unlocked,
                    onBack = {},
                    entryProvider = syntheticEntryProvider(onProvided = providedKeys::add),
                    instantTransition = true,
                )
            }
        }

        composeRule.onNodeWithTag(VAULT_DESTINATION_TAG).assertIsDisplayed()
        composeRule.runOnIdle {
            backStack.add(Routes.Settings)
            backStack.add(Routes.CreatePassword)
        }
        composeRule.waitForIdle()

        composeRule.onNodeWithTag(CREATE_DESTINATION_TAG).assertIsDisplayed()
        assertTrue(providedKeys.containsAll(listOf(Routes.Vault, Routes.Settings, Routes.CreatePassword)))
    }

    @Test
    fun finalDestinationAndFocusAreEquivalentAtZeroNormalAndExpandedMotionScale() {
        val backStack = mutableStateListOf<NavKey>(Routes.Vault)
        val observations = mutableListOf<DestinationObservation>()

        composeRule.mainClock.autoAdvance = false
        composeRule.setContent {
            DesignSystemTestKeyboardMode()
            SafeCubeTheme {
                SafeCubeNavigationHost(
                    backStack = backStack,
                    sessionState = SessionState.LoggedInVaultLocked,
                    vaultState = VaultState.Unlocked,
                    onBack = {},
                    entryProvider = syntheticEntryProvider(),
                )
            }
        }

        listOf(0f, 1f, 2f).forEach { scaleFactor ->
            composeRule.runOnIdle {
                motionDurationScale.scaleFactor = scaleFactor
                backStack.clear()
                backStack.add(Routes.Vault)
            }
            composeRule.mainClock.advanceTimeUntil(5_000) {
                VAULT_DESTINATION_TAG in composedDestinations &&
                    SETTINGS_DESTINATION_TAG !in composedDestinations
            }

            composeRule.runOnIdle {
                backStack.add(Routes.Settings)
            }
            composeRule.mainClock.advanceTimeUntil(5_000) {
                SETTINGS_DESTINATION_TAG in composedDestinations &&
                    VAULT_DESTINATION_TAG !in composedDestinations
            }

            composeRule.onNodeWithTag(SETTINGS_DESTINATION_TAG)
                .assertIsDisplayed()
                .assertIsFocused()
            observations += DestinationObservation(
                destinationTag = SETTINGS_DESTINATION_TAG,
                focused = true,
            )
        }

        assertEquals(
            listOf(
                DestinationObservation(SETTINGS_DESTINATION_TAG, true),
                DestinationObservation(SETTINGS_DESTINATION_TAG, true),
                DestinationObservation(SETTINGS_DESTINATION_TAG, true),
            ),
            observations,
        )
    }

    @Test
    fun lockDuringOrdinaryFadeRevokesOutgoingProtectedSentinelOnFirstUpdatedFrame() {
        assertProtectedEntryRevokedDuringFade(
            safeRoute = Routes.UnlockVault,
            nextSession = SessionState.LoggedInVaultLocked,
            nextVault = VaultState.Locked,
        )
    }

    @Test
    fun expiryDuringOrdinaryFadeRevokesOutgoingProtectedSentinelOnFirstUpdatedFrame() {
        assertProtectedEntryRevokedDuringFade(
            safeRoute = Routes.Login,
            nextSession = SessionState.LoggedOut(SessionTerminationReason.SessionExpired),
            nextVault = VaultState.Unlocked,
        )
    }

    private fun assertProtectedEntryRevokedDuringFade(
        safeRoute: Routes,
        nextSession: SessionState,
        nextVault: VaultState,
    ) {
        val backStack = mutableStateListOf<NavKey>(Routes.Vault)
        val sessionState = mutableStateOf<SessionState>(SessionState.LoggedInVaultLocked)
        val vaultState = mutableStateOf<VaultState>(VaultState.Unlocked)
        val disposed = AtomicBoolean(false)

        composeRule.mainClock.autoAdvance = false
        composeRule.setContent {
            DesignSystemTestKeyboardMode()
            SafeCubeTheme {
                SafeCubeNavigationHost(
                    backStack = backStack,
                    sessionState = sessionState.value,
                    vaultState = vaultState.value,
                    onBack = {},
                    entryProvider = syntheticEntryProvider(onProtectedDisposed = disposed),
                    instantTransition = false,
                )
            }
        }
        composeRule.mainClock.advanceTimeUntil(5_000) {
            VAULT_DESTINATION_TAG in composedDestinations
        }
        composeRule.runOnIdle {
            backStack.add(Routes.Settings)
        }
        composeRule.mainClock.advanceTimeByFrame()
        composeRule.onNodeWithTag(PROTECTED_SENTINEL_TAG).assertIsDisplayed()
        composeRule.onNodeWithTag(PROTECTED_SENTINEL_TAG, useUnmergedTree = true)
            .assertIsDisplayed()

        composeRule.runOnIdle {
            sessionState.value = nextSession
            vaultState.value = nextVault
            backStack.add(safeRoute)
        }
        composeRule.mainClock.advanceTimeByFrame()

        composeRule.onNodeWithTag(PROTECTED_SENTINEL_TAG).assertDoesNotExist()
        composeRule.onNodeWithTag(PROTECTED_SENTINEL_TAG, useUnmergedTree = true)
            .assertDoesNotExist()
        assertTrue(disposed.get())
    }

    @Test
    fun currentSafeRouteStillGuardsOutgoingProtectedEntry() {
        val backStack = mutableStateListOf<NavKey>(Routes.Vault)
        val sessionState = mutableStateOf<SessionState>(SessionState.LoggedInVaultLocked)
        val vaultState = mutableStateOf<VaultState>(VaultState.Unlocked)
        val disposed = AtomicBoolean(false)

        composeRule.setContent {
            DesignSystemTestKeyboardMode()
            SafeCubeTheme {
                SafeCubeNavigationHost(
                    backStack = backStack,
                    sessionState = sessionState.value,
                    vaultState = vaultState.value,
                    onBack = {},
                    entryProvider = syntheticEntryProvider(onProtectedDisposed = disposed),
                    instantTransition = true,
                )
            }
        }
        composeRule.onNodeWithTag(PROTECTED_SENTINEL_TAG).assertIsDisplayed()

        composeRule.runOnIdle {
            sessionState.value = SessionState.LoggedOut(SessionTerminationReason.SessionExpired)
            vaultState.value = VaultState.Unlocked
            backStack.add(Routes.Login)
        }
        composeRule.mainClock.advanceTimeByFrame()

        composeRule.onNodeWithTag(PROTECTED_SENTINEL_TAG).assertDoesNotExist()
        composeRule.onNodeWithTag(PROTECTED_SENTINEL_TAG, useUnmergedTree = true)
            .assertDoesNotExist()
        assertTrue(disposed.get())
    }

    @Composable
    private fun CompositionDensity(fontScale: Float, content: @Composable () -> Unit) {
        val density = LocalDensity.current.density
        androidx.compose.runtime.CompositionLocalProvider(
            LocalDensity provides Density(density, fontScale),
            content = content,
        )
    }

    private fun syntheticEntryProvider(
        onProvided: ((NavKey) -> Unit)? = null,
        onProtectedDisposed: AtomicBoolean? = null,
    ): (NavKey) -> NavEntry<NavKey> = { key ->
        onProvided?.invoke(key)
        NavEntry(key = key) {
            when (key) {
                Routes.Vault -> SyntheticDestination(
                    tag = VAULT_DESTINATION_TAG,
                    label = "Synthetic vault",
                    sentinel = onProtectedDisposed,
                )

                Routes.Settings -> SyntheticDestination(
                    tag = SETTINGS_DESTINATION_TAG,
                    label = "Synthetic settings",
                )

                Routes.CreatePassword -> SyntheticDestination(
                    tag = CREATE_DESTINATION_TAG,
                    label = "Synthetic create",
                )

                Routes.Login -> SyntheticDestination(
                    tag = LOGIN_DESTINATION_TAG,
                    label = "Synthetic login",
                )

                Routes.UnlockVault -> SyntheticDestination(
                    tag = UNLOCK_DESTINATION_TAG,
                    label = "Synthetic unlock",
                )

                else -> error("Unexpected synthetic navigation key: $key")
            }
        }
    }

    @Composable
    private fun SyntheticDestination(
        tag: String,
        label: String,
        sentinel: AtomicBoolean? = null,
    ) {
        DisposableEffect(tag) {
            composedDestinations.add(tag)
            onDispose { composedDestinations.remove(tag) }
        }
        val focusRequester = remember { FocusRequester() }
        LaunchedEffect(focusRequester) {
            focusRequester.requestFocus()
        }
        sentinel?.let { protectedSentinel ->
            DisposableEffect(Unit) {
                onDispose { protectedSentinel.set(true) }
            }
        }
        Box(
            modifier = Modifier
                .fillMaxSize()
                .testTag(tag)
                .focusRequester(focusRequester)
                .focusable(),
            contentAlignment = Alignment.Center,
        ) {
            if (sentinel != null) {
                Box(modifier = Modifier.testTag(PROTECTED_SENTINEL_TAG)) {
                    Text(label)
                }
            } else {
                Text(label)
            }
        }
    }

    private fun Context.findComponentActivity(): ComponentActivity =
        generateSequence(this) { context ->
            (context as? android.content.ContextWrapper)?.baseContext
        }.filterIsInstance<ComponentActivity>().first()

    private data class DestinationObservation(
        val destinationTag: String,
        val focused: Boolean,
    )

    private class MutableMotionDurationScale : MotionDurationScale {
        override var scaleFactor: Float = 1f
        override val key: CoroutineContext.Key<*>
            get() = MotionDurationScale.Key
    }

    private companion object {
        const val SCAFFOLD_TAG = "shell-scaffold"
        const val TOP_BAR_TAG = "shell-top-bar"
        const val CONTENT_TAG = "shell-content"
        const val VAULT_DESTINATION_TAG = "synthetic-vault"
        const val SETTINGS_DESTINATION_TAG = "synthetic-settings"
        const val CREATE_DESTINATION_TAG = "synthetic-create"
        const val LOGIN_DESTINATION_TAG = "synthetic-login"
        const val UNLOCK_DESTINATION_TAG = "synthetic-unlock"
        const val PROTECTED_SENTINEL_TAG = "protected-synthetic-sentinel"
    }
}
