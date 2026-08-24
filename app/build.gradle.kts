import com.miguelrodriguez19.safecube.buildlogic.AppVersionParser
import com.miguelrodriguez19.safecube.buildlogic.ReleaseSigningConfig
import org.w3c.dom.Element
import org.w3c.dom.NodeList
import javax.xml.parsers.DocumentBuilderFactory

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

        managedDevices {
            localDevices {
                create("pixel2Api30") {
                    device = "Pixel 2"
                    apiLevel = 30
                    systemImageSource = "aosp-atd"
                    require64Bit = true
                }
            }
        }
    }
}

dependencies {
    implementation(project(":core:auth"))
    implementation(project(":core:network"))
    implementation(project(":core:ui"))
    implementation(project(":core:vault"))
    implementation(project(":feature:auth"))
    implementation(project(":feature:vault"))
    implementation(project(":feature:profile"))

    // Core
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.core.splashscreen)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.lifecycle.viewmodel.ktx)
    implementation(libs.androidx.lifecycle.process)
    implementation(libs.androidx.lifecycle.viewmodel.navigation3)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.ui.graphics)
    implementation(libs.androidx.compose.ui.tooling.preview)
    implementation(libs.androidx.compose.material3)
    implementation(libs.androidx.hilt.navigation.compose)
    implementation(libs.hilt.android)
    ksp(libs.hilt.compiler)

    // Navigation3
    implementation(libs.androidx.navigation3.runtime)
    implementation(libs.androidx.navigation3.ui)
    implementation(libs.kotlinx.serialization.core)

    // Testing
    testImplementation(libs.junit)
    testImplementation(libs.mockk)
    testImplementation(libs.kotlinx.coroutines.test)
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

tasks.register("verifyReleaseSecurityManifest") {
    group = "verification"
    description = "Verifies release backup policy and exported components in the merged manifest."
    dependsOn("processReleaseManifest")
    doLast {
        fun NodeList.elements(): Sequence<Element> =
            (0 until length).asSequence().map { item(it) }.filterIsInstance<Element>()

        val manifestFile = layout.buildDirectory
            .file("intermediates/merged_manifests/release/processReleaseManifest/AndroidManifest.xml")
            .get()
            .asFile
        check(manifestFile.isFile) {
            "Merged release manifest not found at ${manifestFile.absolutePath}"
        }

        val documentBuilderFactory = DocumentBuilderFactory.newInstance().apply {
            isNamespaceAware = true
            setFeature("http://apache.org/xml/features/disallow-doctype-decl", true)
            setFeature("http://xml.org/sax/features/external-general-entities", false)
            setFeature("http://xml.org/sax/features/external-parameter-entities", false)
            isXIncludeAware = false
            isExpandEntityReferences = false
            setAttribute("http://javax.xml.XMLConstants/property/accessExternalDTD", "")
            setAttribute("http://javax.xml.XMLConstants/property/accessExternalSchema", "")
        }
        val document = documentBuilderFactory.newDocumentBuilder().parse(manifestFile)
        val androidNamespace = "http://schemas.android.com/apk/res/android"
        val application = document.documentElement
            .getElementsByTagName("application")
            .elements()
            .singleOrNull()
            ?: error("Merged release manifest must declare exactly one application")

        check(application.getAttributeNS(androidNamespace, "allowBackup") == "false") {
            "Release manifest must set android:allowBackup=false"
        }
        check(application.getAttributeNS(androidNamespace, "dataExtractionRules") ==
            "@xml/data_extraction_rules") {
            "Release manifest must reference @xml/data_extraction_rules"
        }
        check(application.getAttributeNS(androidNamespace, "fullBackupContent") ==
            "@xml/backup_rules") {
            "Release manifest must reference @xml/backup_rules"
        }

        val legacyBackupRules = project.file("src/main/res/xml/backup_rules.xml").readText()
        val dataExtractionRules = project.file("src/main/res/xml/data_extraction_rules.xml").readText()
        val explicitRootExclusion = "<exclude domain=\"root\" path=\".\" />"
        check(explicitRootExclusion in legacyBackupRules) {
            "Legacy backup rules must exclude the root domain"
        }
        check(explicitRootExclusion in dataExtractionRules) {
            "Data extraction rules must exclude the root domain"
        }
        check("<cloud-backup>" in dataExtractionRules && "<device-transfer>" in dataExtractionRules) {
            "Data extraction rules must cover cloud backup and device transfer"
        }
        check("<include" !in legacyBackupRules && "<include" !in dataExtractionRules) {
            "Backup rules must not contain include exceptions"
        }
        check("<!--" !in legacyBackupRules && "<!--" !in dataExtractionRules &&
            "TODO" !in legacyBackupRules && "TODO" !in dataExtractionRules) {
            "Backup rules must not contain template comments or TODOs"
        }

        val exportedComponents = sequenceOf("activity", "activity-alias", "provider", "receiver", "service")
            .flatMap { tagName ->
                application.getElementsByTagName(tagName)
                    .elements()
                    .filter { component ->
                        component.parentNode === application &&
                            component.getAttributeNS(androidNamespace, "exported") == "true"
                    }
            }
            .toList()
        check(exportedComponents.size == 1) {
            "Only the launcher activity may be exported; found ${exportedComponents.size} exported components"
        }
        val launcher = exportedComponents.single()
        check(launcher.tagName == "activity") { "The exported component must be an activity" }
        check(launcher.getAttributeNS(androidNamespace, "name") ==
            "com.miguelrodriguez19.safecube.app.entrypoint.MainActivity") {
            "The only exported activity must be MainActivity"
        }
        check(launcher.getElementsByTagName("intent-filter").elements().any { intentFilter ->
            val hasMainAction = intentFilter.getElementsByTagName("action")
                .elements()
                .any { action ->
                    action.getAttributeNS(androidNamespace, "name") == "android.intent.action.MAIN"
                }
            val hasLauncherCategory = intentFilter.getElementsByTagName("category")
                .elements()
                .any { category ->
                    category.getAttributeNS(androidNamespace, "name") ==
                        "android.intent.category.LAUNCHER"
                }
            hasMainAction && hasLauncherCategory
        }) { "MainActivity must retain the MAIN/LAUNCHER intent filter" }
    }
}
