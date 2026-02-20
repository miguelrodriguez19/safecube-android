plugins {
    alias(libs.plugins.android.library)
}

android {
    namespace = "com.miguelrodriguez19.safecube.feature.profile"
    compileSdk {
        version = release(36)
    }

    defaultConfig {
        minSdk = 30
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
}

dependencies {
    implementation(project(":core:auth"))
    implementation(project(":core:storage"))
    implementation(libs.androidx.core.ktx)
}
