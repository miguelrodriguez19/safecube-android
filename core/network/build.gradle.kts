import com.android.build.api.variant.AndroidComponentsExtension
import org.openapitools.generator.gradle.plugin.tasks.GenerateTask

plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.google.devtools.ksp)
    alias(libs.plugins.hilt.android)
    alias(libs.plugins.openapi.generator)
    alias(libs.plugins.kotlinx.kover)
}

val openApiOutput = layout.buildDirectory.dir("generated/openapi")

android {
    namespace = "com.miguelrodriguez19.safecube.core.network"

    compileSdk = 36

    defaultConfig {
        minSdk = 30

        buildConfigField(
            "String",
            "BASE_URL",
            "\"https://selective-charil-safecube-92eb45c3.koyeb.app/safecube/\""
        )
    }

    buildFeatures {
        buildConfig = true
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
}

extensions.getByType(AndroidComponentsExtension::class.java).apply {
    onVariants { variant ->
        variant.sources.java?.addStaticSourceDirectory(
            "build/generated/openapi/src/main/kotlin"
        )
    }
}

tasks.named<GenerateTask>("openApiGenerate") {
    generatorName.set("kotlin")
    inputSpec.set("$projectDir/openapi/OpenAPI.json")
    outputDir.set(openApiOutput.get().asFile.path)
    cleanupOutput.set(true)

    packageName.set("com.miguelrodriguez19.safecube.core.network.generated")
    apiPackage.set("com.miguelrodriguez19.safecube.core.network.generated.api")
    modelPackage.set("com.miguelrodriguez19.safecube.core.network.generated.model")

    configOptions.set(
        mapOf(
            "library" to "jvm-retrofit2",
            "serializationLibrary" to "kotlinx_serialization",
            "dateLibrary" to "java8",
            "useCoroutines" to "true"
        )
    )

    generateModelTests.set(false)
    generateApiTests.set(false)
    generateModelDocumentation.set(false)
    generateApiDocumentation.set(false)
}

tasks.named("preBuild") {
    dependsOn("openApiGenerate")
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
    }
}

dependencies {
    implementation(libs.androidx.core.ktx)

    implementation(libs.okhttp)
    implementation(libs.okhttp.logging)

    implementation(libs.retrofit)
    implementation(libs.retrofit.converter.kotlinx.serialization)
    implementation(libs.retrofit.converter.scalars)

    implementation(libs.kotlinx.serialization.json)
    implementation(libs.kotlinx.coroutines.core)

    testImplementation(libs.junit)
    testImplementation(libs.mockk)
    testImplementation(libs.okhttp.mockwebserver)

    implementation(libs.hilt.android)
    ksp(libs.hilt.compiler)
}
