package com.miguelrodriguez19.safecube.core.ui.component

import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.KeyboardActionScope
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text.input.KeyboardActionHandler
import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.foundation.text.input.TextObfuscationMode
import androidx.compose.foundation.text.input.setTextAndPlaceCursorAtEnd
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Visibility
import androidx.compose.material.icons.rounded.VisibilityOff
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedSecureTextField
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.editableText
import androidx.compose.ui.semantics.error
import androidx.compose.ui.semantics.inputText
import androidx.compose.ui.semantics.password
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.stateDescription
import androidx.compose.ui.semantics.text
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.miguelrodriguez19.safecube.core.ui.R
import com.miguelrodriguez19.safecube.core.ui.theme.SafeCubeSpacing
import kotlinx.coroutines.flow.collectLatest

/**
 * Text field for passwords and passphrases.
 *
 * The value is masked by default. When [allowReveal] is true, the reveal affordance is available
 * only while the host lifecycle is RESUMED, and any lifecycle or enabled-state boundary hides it.
 * The secure text-field implementation disables copying, cutting and dragging secret text in
 * both masked and revealed modes.
 */
@Composable
fun SecretOutlinedTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: @Composable (() -> Unit),
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    isError: Boolean = false,
    supportingText: @Composable (() -> Unit)? = null,
    allowReveal: Boolean = false,
    keyboardOptions: KeyboardOptions = KeyboardOptions(
        keyboardType = KeyboardType.Password,
        autoCorrectEnabled = false,
    ),
    keyboardActions: KeyboardActions = KeyboardActions.Default,
    interactionSource: MutableInteractionSource? = null,
) {
    val lifecycleOwner = LocalLifecycleOwner.current
    val currentOnValueChange by rememberUpdatedState(onValueChange)
    val externalValue by rememberUpdatedState(value)
    val textFieldState = remember { TextFieldState(value) }
    val resolvedInteractionSource = interactionSource ?: remember { MutableInteractionSource() }
    var revealRequested by remember { mutableStateOf(false) }
    var lifecycleState by remember(lifecycleOwner) {
        mutableStateOf(lifecycleOwner.lifecycle.currentState)
    }
    val isHostResumed = lifecycleState.isAtLeast(Lifecycle.State.RESUMED)
    val isRevealed = revealRequested && allowReveal && enabled && isHostResumed
    val maskedValue = remember(value) {
        PasswordVisualTransformation().filter(AnnotatedString(value)).text
    }
    val errorLabel = stringResource(R.string.design_system_error)
    val showSecretLabel = stringResource(R.string.design_system_show_secret)
    val hideSecretLabel = stringResource(R.string.design_system_hide_secret)
    val secretVisibilityLabel = stringResource(
        if (isRevealed) {
            R.string.design_system_secret_visible
        } else {
            R.string.design_system_secret_hidden
        },
    )

    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            lifecycleState = lifecycleOwner.lifecycle.currentState
            if (event == Lifecycle.Event.ON_PAUSE ||
                !lifecycleState.isAtLeast(Lifecycle.State.RESUMED)
            ) {
                revealRequested = false
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose {
            revealRequested = false
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }
    LaunchedEffect(allowReveal, enabled) {
        if (!allowReveal || !enabled) {
            revealRequested = false
        }
    }
    LaunchedEffect(textFieldState, value) {
        if (textFieldState.text.toString() != value) {
            textFieldState.setTextAndPlaceCursorAtEnd(value)
        }
    }
    LaunchedEffect(textFieldState) {
        snapshotFlow { textFieldState.text.toString() }
            .collectLatest { currentValue ->
                if (currentValue != externalValue) {
                    currentOnValueChange(currentValue)
                }
            }
    }

    OutlinedSecureTextField(
        state = textFieldState,
        label = { label() },
        isError = isError,
        supportingText = supportingText ?: if (isError) ({ Text(errorLabel) }) else null,
        modifier = modifier
            .heightIn(min = SafeCubeSpacing.TouchTarget)
            .semantics {
                if (!isRevealed) {
                    editableText = maskedValue
                    inputText = maskedValue
                    text = maskedValue
                    password()
                }
                stateDescription = secretVisibilityLabel
                if (isError) {
                    error(errorLabel)
                }
            },
        enabled = enabled,
        keyboardOptions = keyboardOptions,
        onKeyboardAction = keyboardActionHandler(
            keyboardActions = keyboardActions,
            imeAction = keyboardOptions.imeAction,
        ),
        interactionSource = resolvedInteractionSource,
        textObfuscationMode = if (isRevealed) {
            TextObfuscationMode.Visible
        } else {
            TextObfuscationMode.Hidden
        },
        trailingIcon = if (allowReveal) {
            {
                IconButton(
                    onClick = {
                        if (enabled && isHostResumed) {
                            revealRequested = !revealRequested
                        }
                    },
                    enabled = enabled && isHostResumed,
                    modifier = Modifier
                        .size(SafeCubeSpacing.TouchTarget)
                        .semantics { stateDescription = secretVisibilityLabel },
                ) {
                    Icon(
                        imageVector = if (isRevealed) {
                            Icons.Rounded.VisibilityOff
                        } else {
                            Icons.Rounded.Visibility
                        },
                        contentDescription = if (isRevealed) {
                            hideSecretLabel
                        } else {
                            showSecretLabel
                        },
                    )
                }
            }
        } else {
            null
        },
    )
}

private fun keyboardActionHandler(
    keyboardActions: KeyboardActions,
    imeAction: ImeAction,
): KeyboardActionHandler = KeyboardActionHandler { performDefaultAction ->
    val actionScope = object : KeyboardActionScope {
        override fun defaultKeyboardAction(imeAction: ImeAction) {
            performDefaultAction()
        }
    }
    val action: (KeyboardActionScope.() -> Unit)? = when (imeAction) {
        ImeAction.Done -> keyboardActions.onDone
        ImeAction.Go -> keyboardActions.onGo
        ImeAction.Next -> keyboardActions.onNext
        ImeAction.Previous -> keyboardActions.onPrevious
        ImeAction.Search -> keyboardActions.onSearch
        ImeAction.Send -> keyboardActions.onSend
        else -> null
    }
    if (action == null) {
        performDefaultAction()
    } else {
        action.invoke(actionScope)
    }
}
