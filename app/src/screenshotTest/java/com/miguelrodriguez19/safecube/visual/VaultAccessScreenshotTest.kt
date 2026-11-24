package com.miguelrodriguez19.safecube.visual

import android.content.res.Configuration
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.android.tools.screenshot.PreviewTest
import com.miguelrodriguez19.safecube.app.testsupport.visual.VaultAccessVisualCase
import com.miguelrodriguez19.safecube.app.testsupport.visual.VaultAccessVisualSurface

@PreviewTest
@Preview(
    name = "Gate loading · EN light compact",
    group = "Vault access",
    widthDp = 360,
    heightDp = 720,
    locale = "en",
    apiLevel = 36,
    uiMode = Configuration.UI_MODE_NIGHT_NO,
)
@Composable
fun vaultAccessGateLoadingPreview() {
    VaultAccessVisualSurface(VaultAccessVisualCase.GateLoading)
}

@PreviewTest
@Preview(
    name = "Gate retryable · ES dark medium landscape",
    group = "Vault access",
    widthDp = 840,
    heightDp = 560,
    locale = "es",
    apiLevel = 36,
    uiMode = Configuration.UI_MODE_NIGHT_YES,
)
@Composable
fun vaultAccessGateRetryablePreview() {
    VaultAccessVisualSurface(VaultAccessVisualCase.GateRetryable)
}

@PreviewTest
@Preview(
    name = "Gate terminal · EN light compact",
    group = "Vault access",
    widthDp = 360,
    heightDp = 640,
    locale = "en",
    apiLevel = 36,
    uiMode = Configuration.UI_MODE_NIGHT_NO,
)
@Composable
fun vaultAccessGateTerminalPreview() {
    VaultAccessVisualSurface(VaultAccessVisualCase.GateTerminal)
}

@PreviewTest
@Preview(
    name = "Create idle · EN dark compact",
    group = "Vault access",
    widthDp = 360,
    heightDp = 720,
    locale = "en",
    apiLevel = 36,
    uiMode = Configuration.UI_MODE_NIGHT_YES,
)
@Composable
fun vaultAccessCreateIdlePreview() {
    VaultAccessVisualSurface(VaultAccessVisualCase.CreateIdle)
}

@PreviewTest
@Preview(
    name = "Create loading · ES light compact",
    group = "Vault access",
    widthDp = 360,
    heightDp = 720,
    locale = "es",
    apiLevel = 36,
    uiMode = Configuration.UI_MODE_NIGHT_NO,
)
@Composable
fun vaultAccessCreateLoadingPreview() {
    VaultAccessVisualSurface(VaultAccessVisualCase.CreateLoading)
}

@PreviewTest
@Preview(
    name = "Create validation · ES dark compact 200%",
    group = "Vault access",
    widthDp = 320,
    heightDp = 640,
    locale = "es",
    apiLevel = 36,
    uiMode = Configuration.UI_MODE_NIGHT_YES,
    fontScale = 2f,
)
@Composable
fun vaultAccessCreateValidationCompact200Preview() {
    VaultAccessVisualSurface(VaultAccessVisualCase.CreateValidation)
}

@PreviewTest
@Preview(
    name = "Recovery hidden · EN light compact",
    group = "Vault access",
    widthDp = 360,
    heightDp = 800,
    locale = "en",
    apiLevel = 36,
    uiMode = Configuration.UI_MODE_NIGHT_NO,
)
@Composable
fun vaultAccessRecoveryHiddenPreview() {
    VaultAccessVisualSurface(VaultAccessVisualCase.RecoveryHidden)
}

@PreviewTest
@Preview(
    name = "Recovery confirmed · ES dark medium",
    group = "Vault access",
    widthDp = 600,
    heightDp = 840,
    locale = "es",
    apiLevel = 36,
    uiMode = Configuration.UI_MODE_NIGHT_YES,
)
@Composable
fun vaultAccessRecoveryConfirmedPreview() {
    VaultAccessVisualSurface(VaultAccessVisualCase.RecoveryConfirmed)
}

@PreviewTest
@Preview(
    name = "Recovery confirmation error · EN light medium landscape",
    group = "Vault access",
    widthDp = 840,
    heightDp = 560,
    locale = "en",
    apiLevel = 36,
    uiMode = Configuration.UI_MODE_NIGHT_NO,
)
@Composable
fun vaultAccessRecoveryConfirmationErrorPreview() {
    VaultAccessVisualSurface(VaultAccessVisualCase.RecoveryConfirmationError)
}

@PreviewTest
@Preview(
    name = "Unlock idle · EN light compact",
    group = "Vault access",
    widthDp = 360,
    heightDp = 720,
    locale = "en",
    apiLevel = 36,
    uiMode = Configuration.UI_MODE_NIGHT_NO,
)
@Composable
fun vaultAccessUnlockIdlePreview() {
    VaultAccessVisualSurface(VaultAccessVisualCase.UnlockIdle)
}

@PreviewTest
@Preview(
    name = "Unlock loading · ES dark compact",
    group = "Vault access",
    widthDp = 360,
    heightDp = 720,
    locale = "es",
    apiLevel = 36,
    uiMode = Configuration.UI_MODE_NIGHT_YES,
)
@Composable
fun vaultAccessUnlockLoadingPreview() {
    VaultAccessVisualSurface(VaultAccessVisualCase.UnlockLoading)
}

@PreviewTest
@Preview(
    name = "Unlock error · EN light medium landscape",
    group = "Vault access",
    widthDp = 840,
    heightDp = 560,
    locale = "en",
    apiLevel = 36,
    uiMode = Configuration.UI_MODE_NIGHT_NO,
)
@Composable
fun vaultAccessUnlockErrorMediumLandscapePreview() {
    VaultAccessVisualSurface(VaultAccessVisualCase.UnlockError)
}

@PreviewTest
@Preview(
    name = "Quick unlock offer · ES dark medium",
    group = "Vault access",
    widthDp = 600,
    heightDp = 840,
    locale = "es",
    apiLevel = 36,
    uiMode = Configuration.UI_MODE_NIGHT_YES,
)
@Composable
fun vaultAccessQuickUnlockOfferPreview() {
    VaultAccessVisualSurface(VaultAccessVisualCase.UnlockQuickUnlockOffer)
}

@PreviewTest
@Preview(
    name = "Quick unlock fallback · EN dark compact",
    group = "Vault access",
    widthDp = 360,
    heightDp = 800,
    locale = "en",
    apiLevel = 36,
    uiMode = Configuration.UI_MODE_NIGHT_YES,
)
@Composable
fun vaultAccessQuickUnlockFallbackPreview() {
    VaultAccessVisualSurface(VaultAccessVisualCase.UnlockQuickUnlockFallback)
}

@PreviewTest
@Preview(
    name = "Remote passphrase lock notice · ES dark medium landscape",
    group = "Vault access",
    widthDp = 840,
    heightDp = 560,
    locale = "es",
    apiLevel = 36,
    uiMode = Configuration.UI_MODE_NIGHT_YES,
)
@Composable
fun vaultAccessRemotePassphraseNoticePreview() {
    VaultAccessVisualSurface(VaultAccessVisualCase.UnlockRemotePassphraseChanged)
}

@PreviewTest
@Preview(
    name = "Passphrase reconciliation notice · EN light compact 200%",
    group = "Vault access",
    widthDp = 360,
    heightDp = 640,
    locale = "en",
    apiLevel = 36,
    uiMode = Configuration.UI_MODE_NIGHT_NO,
    fontScale = 2f,
)
@Composable
fun vaultAccessPassphraseReconciliationNoticeCompact200Preview() {
    VaultAccessVisualSurface(VaultAccessVisualCase.UnlockPassphraseReconciliationRequired)
}
