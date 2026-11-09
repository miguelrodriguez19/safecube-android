package com.miguelrodriguez19.safecube.core.ui.component

import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.layout.heightIn
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.stateDescription
import com.miguelrodriguez19.safecube.core.ui.R
import com.miguelrodriguez19.safecube.core.ui.theme.SafeCubeSpacing

@Composable
fun SafeCubeTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    isError: Boolean = false,
    supportingText: String? = null,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    keyboardActions: KeyboardActions = KeyboardActions.Default,
    interactionSource: MutableInteractionSource? = null,
) {
    val errorLabel = stringResource(R.string.design_system_error)
    val resolvedSupportingText = supportingText ?: errorLabel.takeIf { isError }
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label) },
        modifier = modifier
            .heightIn(min = SafeCubeSpacing.TouchTarget)
            .semantics {
                if (isError) {
                    stateDescription = resolvedSupportingText ?: errorLabel
                }
            },
        enabled = enabled,
        isError = isError,
        supportingText = resolvedSupportingText?.let { text ->
            { Text(text) }
        },
        keyboardOptions = keyboardOptions,
        keyboardActions = keyboardActions,
        singleLine = true,
        interactionSource = interactionSource,
    )
}
