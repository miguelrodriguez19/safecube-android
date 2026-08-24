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
val generatedNetworkPackage = "com.miguelrodriguez19.safecube.core.network.generated"
val generatedApiPackage = "$generatedNetworkPackage.api"
val generatedModelPackage = "$generatedNetworkPackage.model"

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

    packageName.set(generatedNetworkPackage)
    apiPackage.set(generatedApiPackage)
    modelPackage.set(generatedModelPackage)

    configOptions.set(
        mapOf(
            "library" to "jvm-retrofit2",
            "serializationLibrary" to "kotlinx_serialization",
            "dateLibrary" to "java8",
            "useCoroutines" to "true"
        )
    )

    typeMappings.set(
        mapOf(
            "DateTime" to "java.time.Instant",
        ),
    )

    importMappings.set(
        mapOf(
            "DateTime" to "java.time.Instant",
        ),
    )

    generateModelTests.set(false)
    generateApiTests.set(false)
    generateModelDocumentation.set(false)
    generateApiDocumentation.set(false)
}

tasks.register("postProcessOpenApiGeneratedModels") {
    dependsOn("openApiGenerate")
    doLast {
        val generatedApiClient = file(
            "${openApiOutput.get().asFile.path}/src/main/kotlin/${generatedNetworkPackage.replace('.', '/')}/infrastructure/ApiClient.kt",
        )
        if (generatedApiClient.isFile) {
            val originalContent = generatedApiClient.readText()
            val loggingInterceptorImport = "import okhttp3.logging.HttpLoggingInterceptor\n"
            val loggingProperty = "    var logger: ((String) -> Unit)? = null\n"
            val loggingSetup = """    private val defaultClientBuilder: OkHttpClient.Builder by lazy {
        OkHttpClient()
            .newBuilder()
            .addInterceptor(HttpLoggingInterceptor { message -> logger?.invoke(message) }
                .apply { level = HttpLoggingInterceptor.Level.BODY }
            )
    }"""
            val loggingSetter = """    fun setLogger(logger: (String) -> Unit): ApiClient {
        this.logger = logger
        return this
    }
"""
            val sanitizedContent = originalContent
                .replace(loggingInterceptorImport, "")
                .replace(loggingProperty, "")
                .replace(loggingSetup, """    private val defaultClientBuilder: OkHttpClient.Builder by lazy {
        OkHttpClient().newBuilder()
    }""")
                .replace(loggingSetter, "")
            check(
                !sanitizedContent.contains("HttpLoggingInterceptor") &&
                    !sanitizedContent.contains("okhttp3.logging") &&
                    !sanitizedContent.contains("Level.BODY") &&
                    !sanitizedContent.contains("Level.HEADERS"),
            ) {
                "Generated OpenAPI ApiClient still contains HTTP logging"
            }
            if (sanitizedContent != originalContent) {
                generatedApiClient.writeText(sanitizedContent)
            }
        }

        val generatedModelDir = file(
            "${openApiOutput.get().asFile.path}/src/main/kotlin/${generatedModelPackage.replace('.', '/')}",
        )
        if (!generatedModelDir.exists()) return@doLast

        val byteArrayValueRegex = Regex("""^\s*val\s+\w+:\s+kotlin\.ByteArray\b.*$""")

        generatedModelDir
            .walkTopDown()
            .filter { it.isFile && it.extension == "kt" }
            .forEach { modelFile ->
                val originalContent = modelFile.readText()
                if (!originalContent.contains("kotlin.ByteArray")) return@forEach

                val deDuplicatedLines = buildList<String> {
                    originalContent.lines().forEach { line ->
                        val isContextualLine = line.trim() == "@Contextual"
                        val previousIsContextual = lastOrNull()?.trim() == "@Contextual"
                        if (isContextualLine && previousIsContextual) return@forEach
                        add(line)
                    }
                }

                val normalizedLines = deDuplicatedLines.toMutableList()
                var index = 0
                while (index < normalizedLines.lastIndex) {
                    val serialLine = normalizedLines[index]
                    val valueLine = normalizedLines[index + 1]

                    val hasSerialName = serialLine.contains("@SerialName(value =")
                    val hasByteArrayValue = byteArrayValueRegex.matches(valueLine)

                    if (hasSerialName && hasByteArrayValue) {
                        val hasContextualSameLine = serialLine.contains("@Contextual")
                        val previousNonEmptyLine = (index - 1 downTo 0)
                            .asSequence()
                            .map { normalizedLines[it] }
                            .firstOrNull { it.isNotBlank() }
                        val hasContextualPreviousLine = previousNonEmptyLine?.trim() == "@Contextual"

                        if (!hasContextualSameLine && !hasContextualPreviousLine) {
                            val indentation = serialLine.takeWhile { it == ' ' || it == '\t' }
                            normalizedLines.add(index, "${indentation}@Contextual")
                            index += 1
                        }
                    }

                    index += 1
                }

                var updatedContent = normalizedLines.joinToString(separator = "\n")
                if (updatedContent != originalContent &&
                    !updatedContent.contains("import kotlinx.serialization.Contextual")
                ) {
                    updatedContent = updatedContent.replace(
                        oldValue = "import kotlinx.serialization.SerialName",
                        newValue = "import kotlinx.serialization.SerialName\nimport kotlinx.serialization.Contextual",
                    )
                }

                if (updatedContent != originalContent) {
                    modelFile.writeText(updatedContent)
                }
            }
    }
}

tasks.named("preBuild") {
    dependsOn("postProcessOpenApiGeneratedModels")
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
