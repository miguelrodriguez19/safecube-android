import com.miguelrodriguez19.safecube.buildlogic.AppVersionParser
import com.miguelrodriguez19.safecube.buildlogic.ReleaseSigningConfig

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.google.devtools.ksp)
    alias(libs.plugins.hilt.android)
}

val appVersion = AppVersionParser.fromFile(rootProject.file("version.properties"))
val releaseSigningCredentials = ReleaseSigningConfig.resolve(System.getenv())

android {
    namespace = "com.miguelrodriguez19.safecube"
    compileSdk {
        version = release(36)
    }

    defaultConfig {
        applicationId = "com.miguelrodriguez19.safecube"
        minSdk = 30
        targetSdk = 36
        versionCode = appVersion.versionCode
        versionName = appVersion.versionName

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        testInstrumentationRunnerArguments["clearPackageData"] = "true"
    }

    signingConfigs {
        getByName("debug")

        releaseSigningCredentials?.let { credentials ->
            create("release") {
                storeFile = file(credentials.keystorePath)
                storePassword = credentials.storePassword
                keyAlias = credentials.keyAlias
                keyPassword = credentials.keyPassword
            }
        }
    }

    buildTypes {
        release {
            if (releaseSigningCredentials != null) {
                signingConfig = signingConfigs.getByName("release")
            }

            isMinifyEnabled = true
            isShrinkResources = true

            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )

            isDebuggable = false
            isProfileable = true
        }

        create("benchmark") {
            initWith(getByName("release"))
            isDebuggable = false
            isProfileable = true
            matchingFallbacks += listOf("release")
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    buildFeatures {
        compose = true
    }
    testOptions {
        animationsDisabled = true
        execution = "ANDROIDX_TEST_ORCHESTRATOR"
        unitTests.isIncludeAndroidResources = true
    }
}

dependencies {
    implementation(project(":core:auth"))
    implementation(project(":core:network"))
    implementation(project(":core:vault"))
    implementation(project(":feature:auth"))
    implementation(project(":feature:vault"))
    implementation(project(":feature:profile"))

    // Core
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.core.splashscreen)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.ui.graphics)
    implementation(libs.androidx.compose.ui.tooling.preview)
    implementation(libs.androidx.compose.material3)
    implementation(libs.hilt.android)
    ksp(libs.hilt.compiler)

    // Navigation3
    implementation(libs.androidx.navigation3.runtime)
    implementation(libs.androidx.navigation3.ui)
    implementation(libs.kotlinx.serialization.core)

    // Testing
    testImplementation(libs.junit)
    testImplementation(libs.mockk)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.test.runner)
    androidTestImplementation(libs.androidx.test.rules)
    androidTestUtil(libs.androidx.test.orchestrator)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.compose.ui.test.junit4)
    debugImplementation(libs.androidx.compose.ui.tooling)
    debugImplementation(libs.androidx.compose.ui.test.manifest)
}
