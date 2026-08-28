package com.miguelrodriguez19.safecube.app.presentation.navigation.host

import androidx.annotation.StringRes
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.miguelrodriguez19.safecube.core.ui.R as UiR

internal enum class ForcedLogoutNotice {
    SessionExpired,
    RefreshCredentialsRejected,
    LocalIntegrityFailure,
}

@Composable
internal fun ForcedLogoutNoticeDialog(
    notice: ForcedLogoutNotice,
    onAcknowledged: () -> Unit,
) {
    AlertDialog(
        onDismissRequest = onAcknowledged,
        title = {
            Text(stringResource(UiR.string.session_terminated_title))
        },
        text = {
            Text(stringResource(notice.messageRes()))
        },
        confirmButton = {
            TextButton(onClick = onAcknowledged) {
                Text(stringResource(UiR.string.session_sign_in_again))
            }
        },
    )
}

@StringRes
private fun ForcedLogoutNotice.messageRes(): Int = when (this) {
    ForcedLogoutNotice.SessionExpired -> UiR.string.session_expired_message
    ForcedLogoutNotice.RefreshCredentialsRejected ->
        UiR.string.session_refresh_rejected_message

    ForcedLogoutNotice.LocalIntegrityFailure -> UiR.string.session_local_integrity_message
}
