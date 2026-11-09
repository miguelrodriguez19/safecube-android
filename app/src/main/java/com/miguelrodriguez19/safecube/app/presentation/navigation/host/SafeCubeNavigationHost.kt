package com.miguelrodriguez19.safecube.app.presentation.navigation.host

import androidx.activity.compose.BackHandler
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.ContentTransform
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberUpdatedState
import androidx.lifecycle.viewmodel.navigation3.rememberViewModelStoreNavEntryDecorator
import androidx.navigation3.runtime.NavEntry
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.rememberSaveableStateHolderNavEntryDecorator
import androidx.navigation3.ui.NavDisplay
import androidx.navigation3.scene.Scene
import com.miguelrodriguez19.safecube.app.presentation.navigation.route.Routes
import com.miguelrodriguez19.safecube.app.presentation.ui.SplashGateScreen
import com.miguelrodriguez19.safecube.core.auth.domain.model.SessionState
import com.miguelrodriguez19.safecube.core.ui.theme.SafeCubeMotion
import com.miguelrodriguez19.safecube.core.vault.domain.model.VaultState

/**
 * State-injected host shared by production and deterministic navigation tests.
 * Every retained entry checks the live access state, including entries fading out.
 */
@Composable
internal fun SafeCubeNavigationHost(
    backStack: List<NavKey>,
    sessionState: SessionState,
    vaultState: VaultState,
    onBack: () -> Unit,
    entryProvider: (NavKey) -> NavEntry<NavKey>,
    instantTransition: Boolean = false,
) {
    val currentSession by rememberUpdatedState(sessionState)
    val currentVault by rememberUpdatedState(vaultState)
    val currentRoute = backStack.lastOrNull() as? Routes

    if (shouldGuardRestoredNavigation(sessionState, vaultState, currentRoute)) {
        SplashGateScreen()
        return
    }

    val transition: AnimatedContentTransitionScope<Scene<NavKey>>.() -> ContentTransform = {
        val from = initialState.entries.lastOrNull()?.metadata?.get(ROUTE_METADATA) as? Routes
        val to = targetState.entries.lastOrNull()?.metadata?.get(ROUTE_METADATA) as? Routes
        if (shouldAnimateNavigation(from, to, instantTransition)) {
            fadeIn(
                tween(SafeCubeMotion.Enter.inWholeMilliseconds.toInt(), easing = SafeCubeMotion.EnterEasing),
            ) togetherWith fadeOut(
                tween(SafeCubeMotion.Exit.inWholeMilliseconds.toInt(), easing = SafeCubeMotion.ExitEasing),
            )
        } else {
            EnterTransition.None togetherWith ExitTransition.None
        }
    }

    // Navigation3 disables its handler when the root has no previous entry.
    // Register before NavDisplay so nested screens retain their normal Back priority.
    BackHandler(enabled = backStack.size == 1 && currentRoute == Routes.Vault, onBack = onBack)

    NavDisplay(
        backStack = backStack,
        entryDecorators = listOf(
            rememberSaveableStateHolderNavEntryDecorator(),
            rememberViewModelStoreNavEntryDecorator(),
        ),
        onBack = onBack,
        transitionSpec = transition,
        popTransitionSpec = transition,
        predictivePopTransitionSpec = { transition() },
        entryProvider = { key ->
            val entry = entryProvider(key)
            NavEntry(
                key = key,
                contentKey = entry.contentKey,
                metadata = entry.metadata + (ROUTE_METADATA to key),
            ) {
                // Read remembered state here, never the state captured when this entry was created.
                if (shouldGuardRestoredNavigation(currentSession, currentVault, key as? Routes)) {
                    SplashGateScreen()
                } else {
                    entry.Content()
                }
            }
        },
    )
}

internal fun shouldAnimateNavigation(
    from: Routes?,
    to: Routes?,
    instantTransition: Boolean,
): Boolean = !instantTransition && from != null && to != null &&
    from !in INSTANT_ROUTES && to !in INSTANT_ROUTES

private const val ROUTE_METADATA = "safecube.route"
private val INSTANT_ROUTES = setOf(
    Routes.Splash,
    Routes.PostLoginGate,
    Routes.Welcome,
    Routes.Login,
    Routes.Signup,
    Routes.CreateVault,
    Routes.RecoveryKey,
    Routes.UnlockVault,
)
