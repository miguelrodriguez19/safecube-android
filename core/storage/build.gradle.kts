plugins {
    alias(libs.plugins.android.library)
}

android {
    namespace = "com.miguelrodriguez19.safecube.core.storage"
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
    implementation(project(":core:crypto"))
    implementation(libs.androidx.core.ktx)
}
