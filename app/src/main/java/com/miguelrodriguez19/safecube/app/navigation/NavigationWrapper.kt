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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.ui.NavDisplay
import com.miguelrodriguez19.safecube.core.auth.domain.model.SessionState
import com.miguelrodriguez19.safecube.feature.auth.presentation.login.ui.LoginScreen
import com.miguelrodriguez19.safecube.feature.auth.presentation.signup.ui.SignupScreen
import com.miguelrodriguez19.safecube.feature.auth.presentation.welcome.ui.WelcomeScreen
import com.miguelrodriguez19.safecube.feature.profile.presentation.profile.ui.ProfileScreen
import com.miguelrodriguez19.safecube.feature.vault.presentation.create.ui.CreateVaultScreen
import com.miguelrodriguez19.safecube.feature.vault.presentation.recovery.ui.RecoveryKeyScreen
import com.miguelrodriguez19.safecube.feature.vault.presentation.settings.ui.SettingsScreen
import com.miguelrodriguez19.safecube.feature.vault.presentation.unlock.ui.UnlockVaultScreen
import com.miguelrodriguez19.safecube.feature.vault.presentation.folders.ui.VaultFoldersScreen
import com.miguelrodriguez19.safecube.feature.vault.presentation.home.ui.VaultScreen
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
    val vaultSessionManager = remember(entryPoint) { entryPoint.vaultSessionManager() }
    val sessionState by sessionManager.sessionState.collectAsState()
    val coroutineScope = rememberCoroutineScope()
    var lastBackPressTimestamp by rememberSaveable { mutableLongStateOf(0L) }
    var pendingRecoveryKey by rememberSaveable { mutableStateOf<String?>(null) }

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
                    onLoginSuccess = { setRoot(Routes.PostLoginGate) },
                )
            }
            entry<Routes.Signup> {
                SignupScreen(
                    onLogin = { setRoot(Routes.Login) },
                    onSignupSuccess = { setRoot(Routes.PostLoginGate) },
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
                            vaultSessionManager.onLogout()
                            sessionManager.forceLogout()
                        }
                    },
                )
            }
            entry<Routes.Profile> {
                ProfileScreen(onBackToSettings = { backStack.add(Routes.Settings) })
            }
            entry<Routes.CreateVault> {
                CreateVaultScreen(
                    onRecoveryKey = { recoveryKeyBase64 ->
                        pendingRecoveryKey = recoveryKeyBase64
                        replaceCurrent(Routes.RecoveryKey)
                    },
                    onVaultAlreadyExists = { replaceCurrent(Routes.UnlockVault) },
                )
            }
            entry<Routes.RecoveryKey> {
                RecoveryKeyScreen(
                    recoveryKeyBase64 = pendingRecoveryKey,
                    onUnlockVault = {
                        pendingRecoveryKey = null
                        replaceCurrent(Routes.UnlockVault)
                    },
                )
            }
            entry<Routes.UnlockVault> {
                UnlockVaultScreen(
                    onApp = { setRoot(Routes.Vault) },
                )
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
