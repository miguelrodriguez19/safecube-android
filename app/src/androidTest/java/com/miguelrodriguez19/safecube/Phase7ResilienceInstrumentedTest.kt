package com.miguelrodriguez19.safecube

import androidx.compose.ui.test.assertCountEquals
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onAllNodesWithTag
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.lifecycle.Lifecycle
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.uiautomator.UiDevice
import com.miguelrodriguez19.safecube.app.entrypoint.MainActivity
import com.miguelrodriguez19.safecube.app.testsupport.QuickUnlockInstrumentationEntryPoint
import com.miguelrodriguez19.safecube.core.auth.domain.model.AuthTokens
import com.miguelrodriguez19.safecube.core.auth.domain.model.SessionTerminationReason
import com.miguelrodriguez19.safecube.core.crypto.domain.model.KdfRequest
import com.miguelrodriguez19.safecube.core.crypto.domain.model.KeyWrapRequest
import com.miguelrodriguez19.safecube.core.vault.domain.model.AutoLockTimeout
import com.miguelrodriguez19.safecube.core.vault.domain.model.VaultKeyMaterial
import com.miguelrodriguez19.safecube.core.vault.domain.model.VaultState
import com.miguelrodriguez19.safecube.feature.auth.presentation.AuthTestTags
import dagger.hilt.android.EntryPointAccessors
import java.util.UUID
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withTimeout
import org.junit.After
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@LargeTest
@RunWith(AndroidJUnit4::class)
class Phase7ResilienceInstrumentedTest {
    private val composeRule = createAndroidComposeRule<MainActivity>()

    @get:Rule
    val rule = composeRule

    private val instrumentation = InstrumentationRegistry.getInstrumentation()
    private val entryPoint = EntryPointAccessors.fromApplication(
        instrumentation.targetContext.applicationContext,
        QuickUnlockInstrumentationEntryPoint::class.java,
    )

    @After
    fun clearFixture() {
        entryPoint.pendingVaultInitializationRepository().clear()
        entryPoint.vaultKeyMaterialLocalRepository().clear()
        entryPoint.vaultSessionManager().lock()
        entryPoint.sessionManager().forceLogout()
    }

    @Test
    fun terminalSessionExpirationReplacesProtectedStackAndBackCannotRestoreIt() {
        entryPoint.sessionManager().onLoginSuccess(testTokens())
        composeRule.waitUntil(timeoutMillis = UI_TIMEOUT_MILLIS) {
            composeRule
                .onAllNodesWithTag(AuthTestTags.WELCOME_SCREEN)
                .fetchSemanticsNodes()
                .isEmpty()
        }

        entryPoint.sessionManager().forceLogout(SessionTerminationReason.SessionExpired)

        composeRule.onNodeWithTag(AuthTestTags.LOGIN_SCREEN).assertIsDisplayed()
        composeRule
            .onNodeWithText(
                "Your session expired, so we signed you out to keep your account secure. " +
                    "Sign in again to continue.",
            )
            .assertIsDisplayed()

        UiDevice.getInstance(instrumentation).pressBack()
        composeRule.waitForIdle()

        composeRule.onNodeWithTag(AuthTestTags.LOGIN_SCREEN).assertIsDisplayed()
        composeRule.onAllNodesWithText(PROTECTED_PLAINTEXT_SENTINEL).assertCountEquals(0)
    }

    @Test
    fun immediateAutoLockOnBackgroundRequiresASecondUnlockOnForeground() {
        val fixture = createUnlockedFixture()
        entryPoint.autoLockTimeoutRepository().setTimeout(AutoLockTimeout.Immediately)

        composeRule.activityRule.scenario.moveToState(Lifecycle.State.CREATED)
        awaitVaultState(VaultState.Locked)

        composeRule.activityRule.scenario.moveToState(Lifecycle.State.RESUMED)
        val unlockError = entryPoint.vaultSessionManager().unlockWithPassphrase(fixture.passphrase)

        assertTrue(unlockError == null)
        assertTrue(entryPoint.vaultSessionManager().isUnlocked())
        fixture.clear()
    }

    @Test
    fun lockNowLocksVaultWithoutEndingAccountSession() {
        val fixture = createUnlockedFixture()
        entryPoint.sessionManager().onLoginSuccess(testTokens())

        entryPoint.vaultAutoLockController().lockNow()

        assertTrue(entryPoint.sessionManager().isLoggedIn())
        assertTrue(entryPoint.vaultSessionManager().vaultState.value == VaultState.Locked)
        fixture.clear()
    }

    private fun createUnlockedFixture(): UnlockedFixture {
        val accountId = UUID.randomUUID()
        val passphrase = UUID.randomUUID().toString()
        val kek = randomBytes(KEY_LENGTH_BYTES)
        val recoveryKey = randomBytes(KEY_LENGTH_BYTES)
        val salt = randomBytes(SALT_LENGTH_BYTES)
        val passphraseBytes = passphrase.encodeToByteArray()
        val masterKey = entryPoint.kdfEngine().deriveKey(
            KdfRequest(
                secret = passphraseBytes,
                salt = salt,
                iterations = 3,
                memoryKib = 65_536,
                parallelism = 1,
                outputLengthBytes = KEY_LENGTH_BYTES,
            ),
        )
        val masterEnvelope = entryPoint.keyWrapping().wrapKey(
            KeyWrapRequest(keyToWrap = kek, wrappingKey = masterKey),
        )
        val recoveryEnvelope = entryPoint.keyWrapping().wrapKey(
            KeyWrapRequest(keyToWrap = kek, wrappingKey = recoveryKey),
        )

        try {
            entryPoint.vaultKeyMaterialLocalRepository().save(
                VaultKeyMaterial(
                    accountId = accountId,
                    kekEncMaster = masterEnvelope,
                    kekEncRecovery = recoveryEnvelope,
                    kdfAlgorithm = "argon2id",
                    kdfSalt = salt,
                    kdfMemoryKib = 65_536,
                    kdfIterations = 3,
                    kdfParallelism = 1,
                    kdfOutputLen = KEY_LENGTH_BYTES,
                    cryptoVersion = "v1",
                ),
            )
            check(entryPoint.vaultSessionManager().unlockWithPassphrase(passphrase) == null)
        } finally {
            passphraseBytes.fill(0)
            masterKey.fill(0)
            recoveryKey.fill(0)
            kek.fill(0)
        }

        return UnlockedFixture(passphrase.toCharArray())
    }

    private fun awaitVaultState(expected: VaultState) {
        runBlocking {
            withTimeout(UI_TIMEOUT_MILLIS) {
                entryPoint.vaultSessionManager().vaultState.first { it == expected }
            }
        }
    }

    private fun testTokens(): AuthTokens = AuthTokens(
        accessToken = UUID.randomUUID().toString(),
        refreshToken = UUID.randomUUID().toString(),
        issuedAt = null,
    )

    private fun randomBytes(size: Int): ByteArray = ByteArray(size).also {
        java.security.SecureRandom().nextBytes(it)
    }

    private data class UnlockedFixture(
        private val passphraseChars: CharArray,
    ) {
        val passphrase: String
            get() = passphraseChars.concatToString()

        fun clear() {
            passphraseChars.fill('\u0000')
        }
    }

    private companion object {
        const val KEY_LENGTH_BYTES = 32
        const val SALT_LENGTH_BYTES = 16
        const val UI_TIMEOUT_MILLIS = 10_000L
        const val PROTECTED_PLAINTEXT_SENTINEL = "phase-7-protected-content"
    }
}
