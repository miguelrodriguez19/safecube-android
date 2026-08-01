package com.miguelrodriguez19.safecube.buildlogic

import java.io.File
import java.io.IOException
import java.util.Properties

data class AppVersion(
    val versionName: String,
    val versionCode: Int,
)

object AppVersionComparator {
    fun requireIncrease(previous: AppVersion, current: AppVersion) {
        require(compareVersionNames(current.versionName, previous.versionName) > 0) {
            "VERSION_NAME must be greater than the base version: '${previous.versionName}' -> '${current.versionName}'"
        }
        require(current.versionCode > previous.versionCode) {
            "VERSION_CODE must be greater than the base version: ${previous.versionCode} -> ${current.versionCode}"
        }
    }

    private fun compareVersionNames(current: String, previous: String): Int {
        val currentVersion = ParsedSemVer.from(current)
        val previousVersion = ParsedSemVer.from(previous)

        return compareValuesBy(
            currentVersion,
            previousVersion,
            ParsedSemVer::major,
            ParsedSemVer::minor,
            ParsedSemVer::patch,
        ).takeIf { it != 0 }
            ?: comparePrerelease(currentVersion.prerelease, previousVersion.prerelease)
    }

    private fun comparePrerelease(current: List<String>?, previous: List<String>?): Int = when {
        current == null && previous == null -> 0
        current == null -> 1
        previous == null -> -1
        else -> current.zip(previous)
            .asSequence()
            .map { (currentIdentifier, previousIdentifier) ->
                comparePrereleaseIdentifier(currentIdentifier, previousIdentifier)
            }
            .firstOrNull { it != 0 }
            ?: current.size.compareTo(previous.size)
    }

    private fun comparePrereleaseIdentifier(current: String, previous: String): Int {
        val currentNumber = current.toIntOrNull()
        val previousNumber = previous.toIntOrNull()

        return when {
            currentNumber != null && previousNumber != null -> currentNumber.compareTo(previousNumber)
            currentNumber != null -> -1
            previousNumber != null -> 1
            else -> current.compareTo(previous)
        }
    }

    private data class ParsedSemVer(
        val major: Int,
        val minor: Int,
        val patch: Int,
        val prerelease: List<String>?,
    ) {
        companion object {
            fun from(value: String): ParsedSemVer {
                val withoutBuildMetadata = value.substringBefore('+')
                val (core, prerelease) = withoutBuildMetadata.split('-', limit = 2).let { parts ->
                    parts.first() to parts.getOrNull(1)?.split('.')
                }
                val (major, minor, patch) = core.split('.').map(String::toInt)

                return ParsedSemVer(major, minor, patch, prerelease)
            }
        }
    }
}

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
