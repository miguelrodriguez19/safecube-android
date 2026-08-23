package com.miguelrodriguez19.safecube.core.vault.domain.model

enum class AutoLockTimeout(
    val storedValue: String,
    val durationMillis: Long,
) {
    Immediately(
        storedValue = "immediately",
        durationMillis = 0L,
    ),
    ThirtySeconds(
        storedValue = "30_seconds",
        durationMillis = 30_000L,
    ),
    OneMinute(
        storedValue = "1_minute",
        durationMillis = 60_000L,
    ),
    FiveMinutes(
        storedValue = "5_minutes",
        durationMillis = 300_000L,
    ),
    FifteenMinutes(
        storedValue = "15_minutes",
        durationMillis = 900_000L,
    ),
    ;

    companion object {
        fun fromStoredValue(value: String?): AutoLockTimeout =
            entries.firstOrNull { timeout -> timeout.storedValue == value } ?: Immediately
    }
}
