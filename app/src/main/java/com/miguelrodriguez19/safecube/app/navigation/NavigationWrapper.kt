package com.miguelrodriguez19.safecube.app.navigation

import android.app.Activity
import android.os.SystemClock
import android.widget.Toast
import androidx.compose.animation.core.tween
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.ui.NavDisplay
import com.miguelrodriguez19.safecube.core.auth.domain.model.SessionState
import com.miguelrodriguez19.safecube.feature.auth.screens.LoginScreen
import com.miguelrodriguez19.safecube.feature.auth.screens.SignupScreen
import com.miguelrodriguez19.safecube.feature.auth.screens.WelcomeScreen
import com.miguelrodriguez19.safecube.feature.profile.navigation.ProfileScreen
import com.miguelrodriguez19.safecube.feature.vault.navigation.CreateVaultScreen
import com.miguelrodriguez19.safecube.feature.vault.navigation.RecoveryKeyScreen
import com.miguelrodriguez19.safecube.feature.vault.navigation.SettingsScreen
import com.miguelrodriguez19.safecube.feature.vault.navigation.UnlockVaultScreen
import com.miguelrodriguez19.safecube.feature.vault.navigation.VaultFoldersScreen
import com.miguelrodriguez19.safecube.feature.vault.navigation.VaultScreen
import dagger.hilt.android.EntryPointAccessors
import kotlinx.coroutines.launch

@Composable
fun NavigationWrapper() {
    val backStack = rememberNavBackStack(Routes.Splash)
    val context = LocalContext.current
    val activity = remember(context) { context as? Activity }
    val entryPoint = remember(context) {
        EntryPointAccessors.fromApplication(
            context.applicationContext,
            NavigationGatesEntryPoint::class.java,
        )
    }
    val authRepository = remember(entryPoint) { entryPoint.authRepository() }
    val sessionManager = remember(entryPoint) { entryPoint.sessionManager() }
    val sessionState by sessionManager.sessionState.collectAsState()
    val coroutineScope = rememberCoroutineScope()
    var lastBackPressTimestamp by rememberSaveable { mutableLongStateOf(0L) }

    fun setRoot(route: Routes) {
        if (backStack.size == 1 && backStack.lastOrNull() == route) return
        while (backStack.isNotEmpty()) {
            backStack.removeLastOrNull()
        }
        backStack.add(route)
    }

    fun replaceCurrent(route: Routes) {
        backStack.removeLastOrNull()
        backStack.add(route)
    }

    fun moveToVaultFromAppSection() {
        while (backStack.isNotEmpty() && backStack.lastOrNull() != Routes.Vault) {

            backStack.removeLastOrNull()
        }
        if (backStack.lastOrNull() != Routes.Vault) {
            backStack.add(Routes.Vault)
        }
    }

    LaunchedEffect(sessionState) {
        when (sessionState) {
            SessionState.LoggedOut -> {
                if (backStack.lastOrNull() !in LOGGED_OUT_ROUTES) {
                    setRoot(Routes.Welcome)
                }
            }

            SessionState.LoggedInVaultLocked -> {
                if (backStack.lastOrNull() in PRE_AUTH_ROUTES) {
                    setRoot(Routes.PostLoginGate)
                }
            }
        }
    }

    NavDisplay(
        backStack = backStack,
        onBack = {
            when (backStack.lastOrNull()) {
                Routes.VaultFolders,
                Routes.Settings,
                Routes.App,
                -> moveToVaultFromAppSection()

                Routes.Profile -> {
                    while (backStack.isNotEmpty() && backStack.lastOrNull() != Routes.Settings) {
                        backStack.removeLastOrNull()
                    }
                    if (backStack.lastOrNull() != Routes.Settings) {
                        backStack.add(Routes.Settings)
                    }
                }

                Routes.Vault -> {
                    val now = SystemClock.elapsedRealtime()
                    if (now - lastBackPressTimestamp <= 1500L) {
                        activity?.finish()
                    } else {
                        lastBackPressTimestamp = now
                        Toast.makeText(
                            context,
                            "Pulsa de nuevo para cerrar",
                            Toast.LENGTH_SHORT,
                        ).show()
                    }
                }

                Routes.Welcome,
                Routes.Login,
                Routes.Splash,
                Routes.PostLoginGate,
                Routes.UnlockVault,
                Routes.CreateVault,
                -> Unit

                else -> backStack.removeLastOrNull()
            }
        },
        entryProvider = entryProvider {
            entry<Routes.Splash> {
                SplashGateScreen()
            }
            entry<Routes.Welcome> {
                WelcomeScreen(
                    onLogin = { setRoot(Routes.Login) },
                    onSignup = { setRoot(Routes.Signup) },
                )
            }
            entry<Routes.Login> {
                LoginScreen(
                    onSignup = { backStack.add(Routes.Signup) },
                    onLoginSuccess = { },
                )
            }
            entry<Routes.Signup> {
                SignupScreen(
                    onLogin = { setRoot(Routes.Login) },
                    onSignupSuccess = { },
                )
            }
            entry<Routes.Vault> {
                VaultScreen(
                    onVault = { },
                    onVaultFolders = { backStack.add(Routes.VaultFolders) },
                    onSettings = { backStack.add(Routes.Settings) },
                )
            }
            entry<Routes.VaultFolders> {
                VaultFoldersScreen(
                    onVault = { backStack.add(Routes.Vault) },
                    onVaultFolders = { },
                    onSettings = { backStack.add(Routes.Settings) },
                )
            }
            entry<Routes.Settings> {
                SettingsScreen(
                    onVault = { backStack.add(Routes.Vault) },
                    onVaultFolders = { backStack.add(Routes.VaultFolders) },
                    onSettings = { },
                    onProfile = { backStack.add(Routes.Profile) },
                    onLogout = {
                        coroutineScope.launch {
                            authRepository.logout()
                            sessionManager.forceLogout()
                        }
                    },
                )
            }
            entry<Routes.Profile> {
                ProfileScreen(onBackToSettings = { backStack.add(Routes.Settings) })
            }
            entry<Routes.CreateVault> {
                CreateVaultScreen(onRecoveryKey = { replaceCurrent(Routes.RecoveryKey) })
            }
            entry<Routes.RecoveryKey> {
                RecoveryKeyScreen(onUnlockVault = { replaceCurrent(Routes.UnlockVault) })
            }
            entry<Routes.UnlockVault> {
                UnlockVaultScreen(onApp = { setRoot(Routes.Vault) })
            }
            entry<Routes.PostLoginGate> {
                PostLoginGateRoute(
                    onCreateVault = { replaceCurrent(Routes.CreateVault) },
                    onUnlockVault = { replaceCurrent(Routes.UnlockVault) },
                    onHome = { setRoot(Routes.Vault) },
                )
            }
            entry<Routes.Error> { Text("Error") }
        },
        transitionSpec = {
            slideInHorizontally(
                initialOffsetX = { it },
                animationSpec = tween(250),
            ) togetherWith slideOutHorizontally(
                targetOffsetX = { -it },
                animationSpec = tween(250),
            )
        },
        popTransitionSpec = {
            slideInHorizontally(
                initialOffsetX = { -it },
                animationSpec = tween(250),
            ) togetherWith slideOutHorizontally(
                targetOffsetX = { it },
                animationSpec = tween(250),
            )
        },
        predictivePopTransitionSpec = {
            slideInHorizontally(
                initialOffsetX = { -it },
                animationSpec = tween(250),
            ) togetherWith slideOutHorizontally(
                targetOffsetX = { it },
                animationSpec = tween(250),
            )
        },
    )
}

private val PRE_AUTH_ROUTES = setOf(
    Routes.Splash,
    Routes.Welcome,
    Routes.Login,
    Routes.Signup,
)

private val LOGGED_OUT_ROUTES = setOf(
    Routes.Welcome,
    Routes.Login,
    Routes.Signup,
)
