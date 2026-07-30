import com.miguelrodriguez19.safecube.buildlogic.AppVersionParser
import com.miguelrodriguez19.safecube.buildlogic.ReleaseSigningConfig
import kotlinx.kover.gradle.plugin.dsl.CoverageUnit
import java.io.File

plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.android.library) apply false
    alias(libs.plugins.kotlin.serialization) apply false
    alias(libs.plugins.kotlin.compose) apply false
    alias(libs.plugins.google.devtools.ksp) apply false
    alias(libs.plugins.hilt.android) apply false
    alias(libs.plugins.openapi.generator) apply false
    alias(libs.plugins.kotlinx.kover)
}

val appVersion = AppVersionParser.fromFile(rootProject.file("version.properties"))

dependencies {
    kover(project(":core:auth"))
    kover(project(":core:network"))
    kover(project(":core:crypto"))
    kover(project(":core:storage"))
    kover(project(":core:vault"))
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

tasks.register("verifyCoverage") {
    group = "verification"
    description = "Runs unit tests, generates Kover reports, and verifies coverage thresholds."
    dependsOn(
        ":app:testDebugUnitTest",
        ":core:auth:testDebugUnitTest",
        ":core:network:testDebugUnitTest",
        ":core:crypto:testDebugUnitTest",
        ":core:storage:testDebugUnitTest",
        ":core:vault:testDebugUnitTest",
        ":core:ui:testDebugUnitTest",
        ":feature:auth:testDebugUnitTest",
        ":feature:profile:testDebugUnitTest",
        ":feature:vault:testDebugUnitTest",
        "koverHtmlReport",
        "koverXmlReport",
        "koverVerify"
    )
}

tasks.register("validateVersion") {
    group = "verification"
    description = "Validates and prints the Android application version."
    doLast {
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
