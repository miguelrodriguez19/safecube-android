package com.miguelrodriguez19.safecube.feature.vault.presentation.quickunlock

import android.content.Context
import android.content.ContextWrapper
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricPrompt
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity
import com.miguelrodriguez19.safecube.core.ui.R as UiR
import com.miguelrodriguez19.safecube.core.vault.data.quickunlock.QuickUnlockPromptCipherProvider
import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.android.EntryPointAccessors
import dagger.hilt.components.SingletonComponent

enum class QuickUnlockPromptOperation {
    Unlock,
    Enrollment,
}

data class QuickUnlockPromptRequest(
    val operationId: String,
    val operation: QuickUnlockPromptOperation,
)

@EntryPoint
@InstallIn(SingletonComponent::class)
interface QuickUnlockPromptEntryPoint {
    fun quickUnlockPromptCipherProvider(): QuickUnlockPromptCipherProvider
}

fun quickUnlockPromptCipherProvider(context: Context): QuickUnlockPromptCipherProvider =
    EntryPointAccessors.fromApplication(
        context.applicationContext,
        QuickUnlockPromptEntryPoint::class.java,
    ).quickUnlockPromptCipherProvider()

fun launchQuickUnlockPrompt(
    activity: FragmentActivity,
    cipherProvider: QuickUnlockPromptCipherProvider,
    request: QuickUnlockPromptRequest,
    onSucceeded: (String) -> Unit,
    onCancelledOrError: (String) -> Unit,
) {
    val cipher = cipherProvider.cipherFor(request.operationId) ?: run {
        onCancelledOrError(request.operationId)
        return
    }
    val promptInfo = BiometricPrompt.PromptInfo.Builder()
        .setTitle(activity.getString(UiR.string.quick_unlock_prompt_title))
        .setDescription(
            activity.getString(
                when (request.operation) {
                    QuickUnlockPromptOperation.Unlock -> UiR.string.quick_unlock_prompt_unlock_description
                    QuickUnlockPromptOperation.Enrollment -> UiR.string.quick_unlock_prompt_enrollment_description
                },
            ),
        )
        .setAllowedAuthenticators(
            BiometricManager.Authenticators.BIOMETRIC_STRONG or
                BiometricManager.Authenticators.DEVICE_CREDENTIAL,
        )
        .build()

    try {
        BiometricPrompt(
            activity,
            ContextCompat.getMainExecutor(activity),
            object : BiometricPrompt.AuthenticationCallback() {
                override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                    if (
                        cipherProvider.acceptAuthenticatedCipher(
                            request.operationId,
                            result.cryptoObject?.cipher,
                        )
                    ) {
                        onSucceeded(request.operationId)
                    } else {
                        onCancelledOrError(request.operationId)
                    }
                }

                override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {
                    onCancelledOrError(request.operationId)
                }
            },
        ).authenticate(promptInfo, BiometricPrompt.CryptoObject(cipher))
    } catch (_: Throwable) {
        onCancelledOrError(request.operationId)
    }
}

fun Context.findFragmentActivity(): FragmentActivity? = when (this) {
    is FragmentActivity -> this
    is ContextWrapper -> baseContext.findFragmentActivity()
    else -> null
}
