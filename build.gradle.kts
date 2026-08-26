import com.miguelrodriguez19.safecube.buildlogic.AppVersionComparator
import com.miguelrodriguez19.safecube.buildlogic.AppVersionParser
import com.miguelrodriguez19.safecube.buildlogic.ReleaseSigningConfig
import kotlinx.kover.gradle.plugin.dsl.CoverageUnit
import org.cyclonedx.model.Component
import org.cyclonedx.model.ExternalReference

plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.android.library) apply false
    alias(libs.plugins.kotlin.serialization) apply false
    alias(libs.plugins.kotlin.compose) apply false
    alias(libs.plugins.google.devtools.ksp) apply false
    alias(libs.plugins.hilt.android) apply false
    alias(libs.plugins.openapi.generator) apply false
    alias(libs.plugins.kotlinx.kover)
    alias(libs.plugins.cyclonedx.bom)
}

val appVersion = AppVersionParser.fromFile(rootProject.file("version.properties"))
val androidModules = listOf(
    ":app",
    ":core:auth",
    ":core:crypto",
    ":core:network",
    ":core:storage",
    ":core:ui",
    ":core:vault",
    ":feature:auth",
    ":feature:profile",
    ":feature:vault",
)

dependencies {
    kover(project(":core:auth"))
    kover(project(":core:network"))
    kover(project(":core:crypto"))
    kover(project(":core:storage"))
    kover(project(":core:vault"))
}

project(":app") {
    tasks.cyclonedxDirectBom {
        includeConfigs = listOf("releaseRuntimeClasspath")
        skipConfigs = listOf("(?i).*test.*", "(?i).*debug.*", "(?i).*benchmark.*")
        projectType = Component.Type.APPLICATION
        includeBomSerialNumber = false
        includeLicenseText = false
        includeMetadataResolution = true
        includeBuildEnvironment = false
        includeBuildSystem = false
        componentGroup = "com.miguelrodriguez19.safecube"
        componentName = "safecube-android"
        componentVersion = appVersion.versionName
        externalReferences = listOf(
            ExternalReference().apply {
                type = ExternalReference.Type.VCS
                url = "https://github.com/miguelrodriguez19/safecube-android"
            }
        )
        jsonOutput.set(rootProject.layout.buildDirectory.file("reports/cyclonedx/safecube-release.cdx.json"))
        xmlOutput.unsetConvention()
    }
}

kover {
    reports {
        filters {
            excludes {
                classes(
                    "*.BuildConfig",
                    "*_Factory*",
                    "*_MembersInjector*",
                    "*_HiltModules*",
                    "*_HiltComponents*",
                    "*_Impl",
                    "*AndroidQuickUnlockKeyStorePlatformImpl",
                    "*Dao_Impl*",
                    "*Database_Impl*",
                    "*ComposableSingletons*",
                    "*.di.*",
                    "*generated*"
                )
            }
        }

        total {
            html {
                onCheck.set(true)
            }
            xml {
                onCheck.set(true)
            }
            verify {
                onCheck.set(true)
                rule("Total line coverage") {
                    minBound(90, CoverageUnit.LINE)
                }
                rule("Total branch coverage") {
                    minBound(85, CoverageUnit.BRANCH)
                }
            }
        }
    }
}

tasks.register("validateVersionBump") {
    group = "verification"
    description = "Validates that the app version is greater than a base version file."
    doLast {
        val baseVersionFilePath = providers.gradleProperty("baseVersionFile").orNull
            ?: error("baseVersionFile Gradle property is required")
        val baseVersion = AppVersionParser.fromFile(file(baseVersionFilePath))

        AppVersionComparator.requireIncrease(previous = baseVersion, current = appVersion)
        println("baseVersionName=${baseVersion.versionName}")
        println("baseVersionCode=${baseVersion.versionCode}")
        println("versionName=${appVersion.versionName}")
        println("versionCode=${appVersion.versionCode}")
    }
}

tasks.register("verifyReleaseSigningConfiguration") {
    group = "verification"
    description = "Verifies the release signing environment and keystore file."
    doLast {
        ReleaseSigningConfig.requireValid(System.getenv()) { keystorePath ->
            File(keystorePath).isFile
        }
    }
}

tasks.register("verifyKover") {
    group = "verification"
    description = "Development quality runner. "
    dependsOn(
        androidModules.map { "$it:testDebugUnitTest" },
        "koverHtmlReport",
        "koverXmlReport",
        "koverVerify"
    )
}

tasks.register("ciVerify") {
    group = "verification"
    description = "Runs the canonical CI quality gates without release secrets."
    dependsOn(
        androidModules.map { "$it:testDebugUnitTest" },
        androidModules.map { "$it:lintDebug" },
        "koverHtmlReport",
        "koverXmlReport",
        "koverVerify",
        ":app:assembleRelease",
    )
}

tasks.register("releaseVerify") {
    group = "verification"
    description = "Runs CI gates and release lint without publishing or signing."
    dependsOn(
        "ciVerify",
        androidModules.map { "$it:lintRelease" },
        ":app:verifyReleaseSecurityManifest",
    )
}
