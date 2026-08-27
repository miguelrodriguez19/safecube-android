package com.miguelrodriguez19.safecube

import android.app.KeyguardManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.ParcelFileDescriptor
import android.security.keystore.KeyInfo
import android.security.keystore.KeyProperties
import android.view.KeyEvent
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricPrompt
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.core.content.ContextCompat
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.uiautomator.By
import androidx.test.uiautomator.UiDevice
import androidx.test.uiautomator.Until
import com.miguelrodriguez19.safecube.app.entrypoint.MainActivity
import com.miguelrodriguez19.safecube.app.testsupport.QuickUnlockColdStartProbeService
import com.miguelrodriguez19.safecube.app.testsupport.QuickUnlockInstrumentationEntryPoint
import com.miguelrodriguez19.safecube.core.auth.domain.model.AuthTokens
import com.miguelrodriguez19.safecube.core.auth.domain.session.AccountSessionResult
import com.miguelrodriguez19.safecube.core.crypto.domain.model.KdfRequest
import com.miguelrodriguez19.safecube.core.crypto.domain.model.KeyWrapRequest
import com.miguelrodriguez19.safecube.core.vault.domain.model.VaultKeyMaterial
import com.miguelrodriguez19.safecube.core.vault.domain.model.VaultState
import com.miguelrodriguez19.safecube.core.vault.domain.quickunlock.QuickUnlockCompletionResult
import com.miguelrodriguez19.safecube.core.vault.domain.quickunlock.QuickUnlockEnrollmentPreparationResult
import com.miguelrodriguez19.safecube.core.vault.domain.quickunlock.QuickUnlockEnrollmentResult
import com.miguelrodriguez19.safecube.core.vault.domain.quickunlock.QuickUnlockOfferState
import com.miguelrodriguez19.safecube.core.vault.domain.quickunlock.QuickUnlockPreparationResult
import com.miguelrodriguez19.safecube.core.vault.domain.session.QuickUnlockPromptMode
import dagger.hilt.android.EntryPointAccessors
import java.security.KeyStore
import java.security.SecureRandom
import java.util.UUID
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicReference
import javax.crypto.SecretKey
import javax.crypto.SecretKeyFactory
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@LargeTest
@RunWith(AndroidJUnit4::class)
class QuickUnlockDeviceCredentialTest {
    private val composeRule = createAndroidComposeRule<MainActivity>()

    @get:Rule
    val rule = composeRule

    private val instrumentation = InstrumentationRegistry.getInstrumentation()
    private val targetContext = instrumentation.targetContext
    private val device = UiDevice.getInstance(instrumentation)
    private var provisionedPin: CharArray? = null
    private val entryPoint = EntryPointAccessors.fromApplication(
        targetContext.applicationContext,
        QuickUnlockInstrumentationEntryPoint::class.java,
    )

    @After
    fun clearDeviceCredentialAndFixture() {
        provisionedPin?.let(::clearDeviceCredential)
        provisionedPin = null
        entryPoint.quickUnlockManager().clearAllEnrollments()
        entryPoint.vaultKeyMaterialLocalRepository().clear()
        entryPoint.vaultSessionManager().lock()
        entryPoint.sessionManager().forceLogout()
    }

    @Test
    fun deviceCredentialQuickUnlockUsesSingleSystemPromptAndUnlocksWithoutBackend() {
        val pin = provisionDeviceCredential()
        val fixture = createUnlockedFixture()
        enrollWithDeviceCredential(pin)

        entryPoint.vaultSessionManager().lock(QuickUnlockPromptMode.ManualOnly)
        assertEquals(VaultState.Locked, entryPoint.vaultSessionManager().vaultState.value)
        assertEquals(
            QuickUnlockPromptMode.ManualOnly,
            entryPoint.vaultSessionManager().quickUnlockPromptMode(),
        )
        assertEquals(QuickUnlockOfferState.Enrolled, entryPoint.vaultSessionManager().quickUnlockOfferState())

        val operationId = prepareQuickUnlock()
        assertKeyIsNonExportableAndRequiresAuthentication(operationId)
        val unauthenticatedResult = entryPoint.vaultSessionManager().finishQuickUnlock(operationId)
        assertFalse(unauthenticatedResult == QuickUnlockCompletionResult.Unlocked)
        assertEquals(VaultState.Locked, entryPoint.vaultSessionManager().vaultState.value)

        val successfulOperationId = prepareQuickUnlock()
        val result = authenticateWithDeviceCredential(
            operationId = successfulOperationId,
            pin = pin,
            assertNoDuplicateQuickUnlock = true,
        ) {
            entryPoint.vaultSessionManager().finishQuickUnlock(successfulOperationId)
        }

        assertEquals(QuickUnlockCompletionResult.Unlocked, result)
        assertTrue(entryPoint.vaultSessionManager().isUnlocked())
        fixture.zeroize()
    }

    @Test
    fun cancelledDeviceCredentialPromptKeepsVaultLocked() {
        val pin = provisionDeviceCredential()
        val fixture = createUnlockedFixture()
        enrollWithDeviceCredential(pin)
        entryPoint.vaultSessionManager().lock(QuickUnlockPromptMode.ManualOnly)

        val operationId = prepareQuickUnlock()
        val callbackReached = cancelSystemPrompt(operationId)

        assertTrue(callbackReached)
        assertEquals(VaultState.Locked, entryPoint.vaultSessionManager().vaultState.value)
        assertFalse(entryPoint.vaultSessionManager().isUnlocked())
        fixture.zeroize()
    }

    @Test
    fun lockPreservesEnrollmentAndLogoutDestroysAliasAndEnvelope() {
        val pin = provisionDeviceCredential()
        val fixture = createUnlockedFixture()
        enrollWithDeviceCredential(pin)

        entryPoint.vaultSessionManager().lock(QuickUnlockPromptMode.ManualOnly)

        assertEquals(QuickUnlockOfferState.Enrolled, entryPoint.vaultSessionManager().quickUnlockOfferState())
        assertTrue(quickUnlockAliases().isNotEmpty())

        val logoutResult = runBlocking {
            entryPoint.accountSessionLifecycle().terminateSession()
        }

        assertEquals(AccountSessionResult.Success, logoutResult)
        assertTrue(quickUnlockAliases().isEmpty())
        assertEquals(
            QuickUnlockOfferState.Available,
            entryPoint.quickUnlockManager().offerState(fixture.accountId),
        )
        fixture.zeroize()
    }

    @Test
    fun freshAccountActivationClearsPreviousAccountQuickUnlockEnrollment() {
        val pin = provisionDeviceCredential()
        val fixture = createUnlockedFixture()
        enrollWithDeviceCredential(pin)

        val result = runBlocking {
            entryPoint.accountSessionLifecycle().activateFreshSession(
                AuthTokens(
                    accessToken = UUID.randomUUID().toString(),
                    refreshToken = UUID.randomUUID().toString(),
                    issuedAt = null,
                ),
            )
        }

        assertEquals(AccountSessionResult.Success, result)
        assertTrue(quickUnlockAliases().isEmpty())
        assertEquals(
            QuickUnlockOfferState.Available,
            entryPoint.quickUnlockManager().offerState(fixture.accountId),
        )
        fixture.zeroize()
    }

    @Test
    fun activityRecreationKeepsLiveSessionAndRemoteProcessStartsLocked() {
        val fixture = createUnlockedFixture()

        composeRule.activityRule.scenario.recreate()

        assertTrue(entryPoint.vaultSessionManager().isUnlocked())
        val coldProcessSnapshot = startColdStartProbe()
        assertFalse(coldProcessSnapshot.isUnlocked)
        assertTrue(coldProcessSnapshot.vaultStateName.isNotBlank())
        fixture.zeroize()
    }

    private fun createUnlockedFixture(): Fixture {
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
        passphraseBytes.fill(0)
        masterKey.fill(0)
        recoveryKey.fill(0)
        kek.fill(0)

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
        entryPoint.sessionManager().onLoginSuccess(
            AuthTokens(
                accessToken = UUID.randomUUID().toString(),
                refreshToken = UUID.randomUUID().toString(),
                issuedAt = null,
            ),
        )
        assertNull(entryPoint.vaultSessionManager().unlockWithPassphrase(passphrase))
        return Fixture(accountId = accountId, salt = salt)
    }

    private fun enrollWithDeviceCredential(pin: String) {
        val operationId = (entryPoint.vaultSessionManager().prepareQuickUnlockEnrollment(true)
            as QuickUnlockEnrollmentPreparationResult.Ready).operationId

        val result = authenticateWithDeviceCredential(operationId, pin) {
            entryPoint.vaultSessionManager().finishQuickUnlockEnrollment(operationId)
        }

        assertEquals(QuickUnlockEnrollmentResult.Enrolled, result)
    }

    private fun prepareQuickUnlock(): String =
        (entryPoint.vaultSessionManager().prepareQuickUnlock() as QuickUnlockPreparationResult.Ready)
            .operationId

    private fun authenticateWithDeviceCredential(
        operationId: String,
        pin: String,
        assertNoDuplicateQuickUnlock: Boolean = false,
        onSucceeded: () -> Any,
    ): Any {
        val result = AtomicReference<Any>()
        val callbackLatch = CountDownLatch(1)
        launchSystemPrompt(
            operationId = operationId,
            onSucceeded = {
                result.set(onSucceeded())
                callbackLatch.countDown()
            },
            onCancelled = {
                entryPoint.vaultSessionManager().cancelQuickUnlock(operationId)
                callbackLatch.countDown()
            },
        )

        if (assertNoDuplicateQuickUnlock) {
            assertEquals(
                QuickUnlockPreparationResult.OperationInProgress,
                entryPoint.vaultSessionManager().prepareQuickUnlock(),
            )
        }

        enterDeviceCredential(pin)

        assertTrue(
            "System credential prompt did not finish",
            callbackLatch.await(PROMPT_TIMEOUT_SECONDS, TimeUnit.SECONDS),
        )
        return requireNotNull(result.get())
    }

    private fun cancelSystemPrompt(operationId: String): Boolean {
        val callbackLatch = CountDownLatch(1)
        val prompt = launchSystemPrompt(
            operationId = operationId,
            onSucceeded = {
                entryPoint.vaultSessionManager().finishQuickUnlock(operationId)
                callbackLatch.countDown()
            },
            onCancelled = {
                entryPoint.vaultSessionManager().cancelQuickUnlock(operationId)
                callbackLatch.countDown()
            },
        )

        try {
            waitForSystemCredentialPrompt()
            instrumentation.runOnMainSync(prompt::cancelAuthentication)
            return callbackLatch.await(PROMPT_TIMEOUT_SECONDS, TimeUnit.SECONDS)
        } finally {
            instrumentation.runOnMainSync(prompt::cancelAuthentication)
        }
    }

    private fun launchSystemPrompt(
        operationId: String,
        onSucceeded: () -> Unit,
        onCancelled: () -> Unit,
    ): BiometricPrompt {
        val cipherProvider = entryPoint.quickUnlockPromptCipherProvider()
        val cipher = cipherProvider.cipherFor(operationId)
        assertNotNull(cipher)
        val prompt = AtomicReference<BiometricPrompt>()
        instrumentation.runOnMainSync {
            val promptInstance = BiometricPrompt(
                composeRule.activity,
                ContextCompat.getMainExecutor(composeRule.activity),
                object : BiometricPrompt.AuthenticationCallback() {
                    override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                        val authenticatedCipher = result.cryptoObject?.cipher
                        if (authenticatedCipher != null &&
                            cipherProvider.acceptAuthenticatedCipher(operationId, authenticatedCipher)
                        ) {
                            onSucceeded()
                        } else {
                            onCancelled()
                        }
                    }

                    override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {
                        onCancelled()
                    }
                },
            )
            prompt.set(promptInstance)
            promptInstance.authenticate(
                BiometricPrompt.PromptInfo.Builder()
                    .setTitle(targetContext.getString(R.string.app_name))
                    .setAllowedAuthenticators(
                        BiometricManager.Authenticators.BIOMETRIC_STRONG or
                            BiometricManager.Authenticators.DEVICE_CREDENTIAL,
                    )
                    .build(),
                BiometricPrompt.CryptoObject(requireNotNull(cipher)),
            )
        }
        return requireNotNull(prompt.get())
    }

    private fun enterDeviceCredential(pin: String) {
        waitForSystemCredentialPrompt()
        pin.forEach { digit ->
            assertTrue(device.pressKeyCode(KeyEvent.KEYCODE_0 + digit.digitToInt()))
        }
        assertTrue(device.pressKeyCode(KeyEvent.KEYCODE_ENTER))
    }

    private fun waitForSystemCredentialPrompt() {
        val promptTitle = targetContext.getString(R.string.app_name)
        val appeared = device.wait(
            Until.hasObject(By.pkg(SYSTEM_UI_PACKAGE).text(promptTitle)),
            PROMPT_TIMEOUT_MILLIS,
        )
        check(appeared) {
            "System credential prompt did not appear within ${PROMPT_TIMEOUT_SECONDS}s; " +
                "foreground package=${device.currentPackageName ?: "unknown"}"
        }
    }

    private fun provisionDeviceCredential(): String {
        val pin = buildString(PIN_LENGTH) {
            repeat(PIN_LENGTH) { append(SecureRandom().nextInt(10)) }
        }
        provisionedPin = pin.toCharArray()
        executeShellCommand("locksettings set-pin $pin")

        val keyguardManager = targetContext.getSystemService(KeyguardManager::class.java)
        assertTrue(keyguardManager.isDeviceSecure)
        assertEquals(
            BiometricManager.BIOMETRIC_SUCCESS,
            BiometricManager.from(targetContext).canAuthenticate(
                BiometricManager.Authenticators.BIOMETRIC_STRONG or
                    BiometricManager.Authenticators.DEVICE_CREDENTIAL,
            ),
        )
        return pin
    }

    private fun clearDeviceCredential(pin: CharArray) {
        try {
            executeShellCommand("locksettings clear --old ${pin.concatToString()}")
            assertFalse(targetContext.getSystemService(KeyguardManager::class.java).isDeviceSecure)
        } finally {
            pin.fill('\u0000')
        }
    }

    private fun assertKeyIsNonExportableAndRequiresAuthentication(operationId: String) {
        val alias = quickUnlockAliases().single()
        val key = KeyStore.getInstance(ANDROID_KEY_STORE).apply { load(null) }
            .getKey(alias, null) as SecretKey
        val keyInfo = SecretKeyFactory.getInstance(key.algorithm, ANDROID_KEY_STORE)
            .getKeySpec(key, KeyInfo::class.java) as KeyInfo

        assertNull(key.encoded)
        assertTrue(keyInfo.isUserAuthenticationRequired())
        assertTrue(keyInfo.getUserAuthenticationValidityDurationSeconds() <= 0)
        assertEquals(
            KeyProperties.AUTH_BIOMETRIC_STRONG or KeyProperties.AUTH_DEVICE_CREDENTIAL,
            keyInfo.getUserAuthenticationType(),
        )
        assertNotNull(entryPoint.quickUnlockPromptCipherProvider().cipherFor(operationId))
    }

    private fun quickUnlockAliases(): List<String> {
        val aliases = KeyStore.getInstance(ANDROID_KEY_STORE)
            .apply { load(null) }
            .aliases()
        return buildList {
            while (aliases.hasMoreElements()) {
                aliases.nextElement()
                    .takeIf { it.startsWith(QUICK_UNLOCK_ALIAS_PREFIX) }
                    ?.let(::add)
            }
        }
    }

    private fun startColdStartProbe(): ColdStartSnapshot {
        val latch = CountDownLatch(1)
        val result = AtomicReference<ColdStartSnapshot>()
        val resultAction = "${targetContext.packageName}.QUICK_UNLOCK_COLD_START_${UUID.randomUUID()}"
        val receiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context, intent: Intent) {
                if (resultAction == intent.action) {
                    result.set(
                        ColdStartSnapshot(
                            isUnlocked = intent.getBooleanExtra(
                                QuickUnlockColdStartProbeService.EXTRA_IS_UNLOCKED,
                                false,
                            ),
                            vaultStateName = intent.getStringExtra(
                                QuickUnlockColdStartProbeService.EXTRA_VAULT_STATE,
                            ).orEmpty(),
                        ),
                    )
                }
                latch.countDown()
            }
        }
        ContextCompat.registerReceiver(
            targetContext,
            receiver,
            IntentFilter(resultAction),
            ContextCompat.RECEIVER_NOT_EXPORTED,
        )
        val pendingIntent = PendingIntent.getBroadcast(
            targetContext,
            0,
            Intent(resultAction).setPackage(targetContext.packageName),
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_MUTABLE,
        )
        try {
            val intent = Intent(targetContext, QuickUnlockColdStartProbeService::class.java)
                .putExtra(
                    QuickUnlockColdStartProbeService.EXTRA_RESULT_PENDING_INTENT,
                    pendingIntent,
                )
            val component = targetContext.startService(intent)
            assertNotNull(component)
            assertTrue(
                "Remote cold-start probe did not return",
                latch.await(PROBE_TIMEOUT_SECONDS, TimeUnit.SECONDS),
            )
            return requireNotNull(result.get())
        } finally {
            pendingIntent.cancel()
            targetContext.unregisterReceiver(receiver)
        }
    }

    private fun executeShellCommand(command: String) {
        ParcelFileDescriptor.AutoCloseInputStream(
            instrumentation.uiAutomation.executeShellCommand(command),
        ).bufferedReader().use { reader ->
            val output = reader.readText()
            check(!output.contains("error", ignoreCase = true)) {
                "locksettings command reported an error"
            }
        }
    }

    private fun randomBytes(size: Int): ByteArray = ByteArray(size).also(SecureRandom()::nextBytes)

    private data class Fixture(
        val accountId: UUID,
        private val salt: ByteArray,
    ) {
        fun zeroize() {
            salt.fill(0)
        }
    }

    private data class ColdStartSnapshot(
        val isUnlocked: Boolean,
        val vaultStateName: String,
    )

    private companion object {
        const val ANDROID_KEY_STORE = "AndroidKeyStore"
        const val KEY_LENGTH_BYTES = 32
        const val SALT_LENGTH_BYTES = 16
        const val PIN_LENGTH = 6
        const val PROMPT_TIMEOUT_SECONDS = 20L
        const val PROMPT_TIMEOUT_MILLIS = PROMPT_TIMEOUT_SECONDS * 1_000L
        const val PROBE_TIMEOUT_SECONDS = 10L
        const val SYSTEM_UI_PACKAGE = "com.android.systemui"
        const val QUICK_UNLOCK_ALIAS_PREFIX = "safecube.quick_unlock."
    }
}
