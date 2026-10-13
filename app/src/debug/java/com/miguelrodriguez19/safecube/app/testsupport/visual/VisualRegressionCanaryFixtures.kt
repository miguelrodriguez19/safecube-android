package com.miguelrodriguez19.safecube.app.testsupport.visual

/**
 * Synthetic, deterministic data used by screenshot previews and semantic tests.
 *
 * These fixtures deliberately contain no password, passphrase, recovery key, token, account ID,
 * vault payload, clock value, random value or environment-derived value.
 */
data class VisualRegressionCanaryFixture(
    val auth: AuthVisualFixture,
    val vault: VaultVisualFixture,
    val sync: SyncVisualFixture,
    val error: ErrorVisualFixture,
)

data class AuthVisualFixture(
    val email: String,
    val submitEnabled: Boolean,
)

data class VaultVisualFixture(
    val items: List<ItemVisualFixture>,
)

data class ItemVisualFixture(
    val title: String,
    val type: ItemVisualType,
    val state: ItemVisualState,
)

enum class ItemVisualType {
    Password,
    Note,
}
enum class ItemVisualState {
    Synced,
    Draft,
    Conflict,
}

data class SyncVisualFixture(
    val actionEnabled: Boolean,
    val isSyncing: Boolean,
)

data class ErrorVisualFixture(
    val visible: Boolean,
    val retryEnabled: Boolean,
)

object VisualRegressionCanaryFixtures {
    val infrastructure = VisualRegressionCanaryFixture(
        auth = AuthVisualFixture(
            email = "visual-user@example.invalid",
            submitEnabled = true,
        ),
        vault = VaultVisualFixture(
            items = listOf(
                ItemVisualFixture(
                    title = "Synthetic mail account",
                    type = ItemVisualType.Password,
                    state = ItemVisualState.Synced,
                ),
                ItemVisualFixture(
                    title = "Synthetic secure note",
                    type = ItemVisualType.Note,
                    state = ItemVisualState.Draft,
                ),
                ItemVisualFixture(
                    title = "Synthetic conflict sample",
                    type = ItemVisualType.Password,
                    state = ItemVisualState.Conflict,
                ),
            ),
        ),
        sync = SyncVisualFixture(
            actionEnabled = false,
            isSyncing = true,
        ),
        error = ErrorVisualFixture(
            visible = true,
            retryEnabled = true,
        ),
    )
}
