import kotlinx.kover.gradle.plugin.dsl.CoverageUnit

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

dependencies {
    kover(project(":core:auth"))
    kover(project(":core:network"))
    kover(project(":core:crypto"))
    kover(project(":core:storage"))
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
                    minBound(75, CoverageUnit.LINE)
                }
                rule("Total branch coverage") {
                    minBound(60, CoverageUnit.BRANCH)
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
        ":core:ui:testDebugUnitTest",
        ":feature:auth:testDebugUnitTest",
        ":feature:profile:testDebugUnitTest",
        ":feature:vault:testDebugUnitTest",
        "koverHtmlReport",
        "koverXmlReport",
        "koverVerify"
    )
}