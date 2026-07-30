package com.miguelrodriguez19.safecube.buildlogic

import java.io.File
import java.io.IOException
import java.util.Properties

data class AppVersion(
    val versionName: String,
    val versionCode: Int,
)

object AppVersionParser {
    const val VERSION_NAME_KEY = "VERSION_NAME"
    const val VERSION_CODE_KEY = "VERSION_CODE"

    private val semVerPattern = Regex(
        """^(0|[1-9]\d*)\.(0|[1-9]\d*)\.(0|[1-9]\d*)(?:-(?:0|[1-9]\d*|\d*[A-Za-z-][0-9A-Za-z-]*)(?:\.(?:0|[1-9]\d*|\d*[A-Za-z-][0-9A-Za-z-]*))*)?(?:\+(?:[0-9A-Za-z-]+)(?:\.[0-9A-Za-z-]+)*)?$""",
    )

    fun fromFile(file: File): AppVersion {
        require(file.isFile) {
            "Version properties file is missing: ${file.path}"
        }

        val properties = Properties()
        try {
            file.inputStream().use(properties::load)
        } catch (exception: IOException) {
            throw IllegalArgumentException(
                "Unable to read version properties file: ${file.path}",
                exception,
            )
        }

        return fromProperties(properties)
    }

    fun fromProperties(properties: Properties): AppVersion {
        val versionName = requiredValue(properties, VERSION_NAME_KEY)
        require(semVerPattern.matches(versionName)) {
            "$VERSION_NAME_KEY must be a valid SemVer 2.0.0 value: '$versionName'"
        }

        val versionCodeValue = requiredValue(properties, VERSION_CODE_KEY)
        val versionCode = versionCodeValue.toIntOrNull()
        require(versionCode != null && versionCode > 0) {
            "$VERSION_CODE_KEY must be a positive integer: '$versionCodeValue'"
        }

        return AppVersion(versionName = versionName, versionCode = versionCode)
    }

    private fun requiredValue(properties: Properties, key: String): String =
        properties.getProperty(key)?.trim()?.takeIf(String::isNotEmpty)
            ?: throw IllegalArgumentException("$key must be present and non-empty")
}
