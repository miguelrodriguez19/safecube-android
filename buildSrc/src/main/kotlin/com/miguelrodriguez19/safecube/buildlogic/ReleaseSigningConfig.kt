package com.miguelrodriguez19.safecube.buildlogic

data class ReleaseSigningCredentials(
    val keystorePath: String,
    val storePassword: String,
    val keyAlias: String,
    val keyPassword: String,
)

object ReleaseSigningConfig {
    const val KEYSTORE_PATH = "SAFECUBE_RELEASE_KEYSTORE_PATH"
    const val STORE_PASSWORD = "SAFECUBE_RELEASE_STORE_PASSWORD"
    const val KEY_ALIAS = "SAFECUBE_RELEASE_KEY_ALIAS"
    const val KEY_PASSWORD = "SAFECUBE_RELEASE_KEY_PASSWORD"

    private val variableNames = listOf(
        KEYSTORE_PATH,
        STORE_PASSWORD,
        KEY_ALIAS,
        KEY_PASSWORD,
    )

    fun resolve(environment: Map<String, String>): ReleaseSigningCredentials? {
        val values = variableNames.associateWith { name ->
            environment[name]?.takeIf(String::isNotBlank)
        }
        val configuredNames = values.filterValues { it != null }.keys

        if (configuredNames.isEmpty()) return null

        val missingNames = variableNames.filterNot { it in configuredNames }
        require(missingNames.isEmpty()) {
            "Release signing configuration is partial. Missing environment variables: " +
                missingNames.joinToString()
        }

        return ReleaseSigningCredentials(
            keystorePath = values.getValue(KEYSTORE_PATH)!!,
            storePassword = values.getValue(STORE_PASSWORD)!!,
            keyAlias = values.getValue(KEY_ALIAS)!!,
            keyPassword = values.getValue(KEY_PASSWORD)!!,
        )
    }

    fun requireValid(
        environment: Map<String, String>,
        keystoreExists: (String) -> Boolean,
    ): ReleaseSigningCredentials {
        val credentials = resolve(environment)
            ?: throw IllegalStateException(
                "Release signing configuration is required. Set: " + variableNames.joinToString(),
            )

        check(keystoreExists(credentials.keystorePath)) {
            "$KEYSTORE_PATH does not point to an available keystore file"
        }

        return credentials
    }
}
