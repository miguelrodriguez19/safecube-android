package com.miguelrodriguez19.safecube.app.presentation.navigation.host

import androidx.compose.material3.Text
import androidx.navigation3.runtime.NavEntry
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.entryProvider
import com.miguelrodriguez19.safecube.app.presentation.navigation.gate.PostLoginGateRoute
import com.miguelrodriguez19.safecube.app.presentation.navigation.route.Routes
import com.miguelrodriguez19.safecube.app.presentation.ui.SplashGateScreen
import com.miguelrodriguez19.safecube.feature.auth.presentation.login.ui.LoginScreen
import com.miguelrodriguez19.safecube.feature.auth.presentation.signup.ui.SignupScreen
import com.miguelrodriguez19.safecube.feature.auth.presentation.welcome.ui.WelcomeScreen
import com.miguelrodriguez19.safecube.feature.profile.presentation.profile.ui.ProfileScreen
import com.miguelrodriguez19.safecube.feature.vault.presentation.create.ui.CreateVaultScreen
import com.miguelrodriguez19.safecube.feature.vault.presentation.folders.ui.VaultFoldersScreen
import com.miguelrodriguez19.safecube.feature.vault.presentation.home.ui.VaultScreen
import com.miguelrodriguez19.safecube.feature.vault.presentation.editor.note.ui.NoteEditorScreen
import com.miguelrodriguez19.safecube.feature.vault.presentation.editor.password.ui.PasswordEditorScreen
import com.miguelrodriguez19.safecube.feature.vault.presentation.recovery.ui.RecoveryKeyScreen
import com.miguelrodriguez19.safecube.feature.vault.presentation.passphrase.ui.ChangePassphraseScreen
import com.miguelrodriguez19.safecube.feature.vault.presentation.settings.ui.SettingsScreen
import com.miguelrodriguez19.safecube.feature.vault.presentation.unlock.ui.UnlockVaultScreen

internal fun navigationEntryProvider(
    setRoot: (Routes) -> Unit,
    addRoute: (Routes) -> Unit,
    replaceCurrent: (Routes) -> Unit,
    popBackStack: () -> Unit,
    onLogout: () -> Unit,
    onLockNow: () -> Unit,
): (NavKey) -> NavEntry<NavKey> = entryProvider {
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
            onSignup = { addRoute(Routes.Signup) },
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
            onVault = {},
            onCreatePassword = { addRoute(Routes.CreatePassword) },
            onCreateNote = { addRoute(Routes.CreateNote) },
            onEditPassword = { logicalItemId ->
                addRoute(Routes.EditPassword(logicalItemId.toString()))
            },
            onEditNote = { logicalItemId ->
                addRoute(Routes.EditNote(logicalItemId.toString()))
            },
            onVaultFolders = { addRoute(Routes.VaultFolders) },
            onSettings = { addRoute(Routes.Settings) },
        )
    }
    entry<Routes.CreatePassword> {
        PasswordEditorScreen(
            onBack = popBackStack,
            onUnlockVault = { setRoot(Routes.UnlockVault) },
        )
    }
    entry<Routes.EditPassword> { route ->
        PasswordEditorScreen(
            logicalItemId = route.logicalItemId,
            onBack = popBackStack,
            onUnlockVault = { setRoot(Routes.UnlockVault) },
        )
    }
    entry<Routes.CreateNote> {
        NoteEditorScreen(
            onBack = popBackStack,
            onUnlockVault = { setRoot(Routes.UnlockVault) },
        )
    }
    entry<Routes.EditNote> { route ->
        NoteEditorScreen(
            logicalItemId = route.logicalItemId,
            onBack = popBackStack,
            onUnlockVault = { setRoot(Routes.UnlockVault) },
        )
    }
    entry<Routes.VaultFolders> {
        VaultFoldersScreen(
            onVault = { addRoute(Routes.Vault) },
            onVaultFolders = {},
            onSettings = { addRoute(Routes.Settings) },
        )
    }
    entry<Routes.Settings> {
        SettingsScreen(
            onVault = { addRoute(Routes.Vault) },
            onVaultFolders = { addRoute(Routes.VaultFolders) },
            onSettings = {},
            onProfile = { addRoute(Routes.Profile) },
            onLogout = onLogout,
            onLockNow = onLockNow,
            onChangePassphrase = { addRoute(Routes.ChangePassphrase) },
        )
    }
    entry<Routes.ChangePassphrase> {
        ChangePassphraseScreen(
            onBack = popBackStack,
            onUnlockVault = { setRoot(Routes.UnlockVault) },
        )
    }
    entry<Routes.Profile> {
        ProfileScreen(onBackToSettings = { addRoute(Routes.Settings) })
    }
    entry<Routes.CreateVault> {
        CreateVaultScreen(
            onRecoveryKey = {
                replaceCurrent(Routes.RecoveryKey)
            },
            onVaultAlreadyExists = { replaceCurrent(Routes.UnlockVault) },
        )
    }
    entry<Routes.RecoveryKey> {
        RecoveryKeyScreen(
            onUnlockVault = {
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
            onCreateVault = { setRoot(Routes.CreateVault) },
            onRecoveryKey = { setRoot(Routes.RecoveryKey) },
            onUnlockVault = { setRoot(Routes.UnlockVault) },
            onHome = { setRoot(Routes.Vault) },
        )
    }
    entry<Routes.Error> { Text("Error") }
}
