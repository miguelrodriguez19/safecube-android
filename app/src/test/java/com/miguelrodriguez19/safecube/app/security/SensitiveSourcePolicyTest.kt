package com.miguelrodriguez19.safecube.app.security

import java.io.File
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class SensitiveSourcePolicyTest {
    private val workingDirectory = requireNotNull(System.getProperty("user.dir"))
    private val repositoryRoot = generateSequence(File(workingDirectory).absoluteFile) {
        it.parentFile
    }.firstOrNull { candidate -> candidate.resolve("settings.gradle.kts").isFile }
        ?: error("Unable to locate repository root from $workingDirectory")

    @Test
    fun `main sources do not access clipboard APIs directly`() {
        val forbiddenTokens = listOf(
            "ClipboardManager",
            "LocalClipboardManager",
            "ClipData",
            "setPrimaryClip",
            "primaryClip",
        )

        val findings = mainSourceFiles().flatMap { sourceFile ->
            sourceFile.readLines().withIndex().flatMap { (index, line) ->
                forbiddenTokens
                    .filter(line::contains)
                    .map { token -> "${sourceFile.relativeTo(repositoryRoot)}:${index + 1}:$token" }
            }
        }

        assertTrue(
            "Clipboard access must go through an explicitly approved adapter: ${findings.joinToString()}",
            findings.isEmpty(),
        )
    }

    @Test
    fun `approved secret fields use the mandatory masking component`() {
        val expectedFieldCounts = mapOf(
            "feature/auth/src/main/java/com/miguelrodriguez19/safecube/feature/auth/presentation/login/ui/LoginScreen.kt" to 1,
            "feature/auth/src/main/java/com/miguelrodriguez19/safecube/feature/auth/presentation/signup/ui/SignupScreen.kt" to 2,
            "feature/vault/src/main/java/com/miguelrodriguez19/safecube/feature/vault/presentation/create/ui/CreateVaultScreen.kt" to 1,
            "feature/vault/src/main/java/com/miguelrodriguez19/safecube/feature/vault/presentation/unlock/ui/UnlockVaultScreen.kt" to 1,
            "feature/vault/src/main/java/com/miguelrodriguez19/safecube/feature/vault/presentation/passphrase/ui/ChangePassphraseScreen.kt" to 1,
            "feature/vault/src/main/java/com/miguelrodriguez19/safecube/feature/vault/presentation/editor/password/ui/PasswordEditorScreen.kt" to 1,
        )

        expectedFieldCounts.forEach { (relativePath, expectedCount) ->
            val source = repositoryRoot.resolve(relativePath).readText()
            val actualCount = SECRET_FIELD_CALL.findAll(source).count()

            assertEquals("Unexpected secret field count in $relativePath", expectedCount, actualCount)
        }

        val transformationOwners = mainSourceFiles()
            .filter { sourceFile -> "PasswordVisualTransformation" in sourceFile.readText() }
            .map { sourceFile -> sourceFile.relativeTo(repositoryRoot).invariantSeparatorsPath }

        assertEquals(listOf(SECRET_FIELD_COMPONENT), transformationOwners)
    }

    private fun mainSourceFiles(): List<File> = repositoryRoot.walkTopDown()
        .onEnter { directory -> directory == repositoryRoot || directory.name !in EXCLUDED_DIRECTORIES }
        .filter { candidate ->
            candidate.isFile &&
                candidate.extension in setOf("kt", "java", "xml") &&
                "/src/main/" in "/${candidate.relativeTo(repositoryRoot).invariantSeparatorsPath}"
        }
        .toList()

    private companion object {
        val EXCLUDED_DIRECTORIES = setOf(".git", ".gradle", ".idea", "build")
        val SECRET_FIELD_CALL = Regex("""\bSecretOutlinedTextField\s*\(""")
        const val SECRET_FIELD_COMPONENT =
            "core/ui/src/main/java/com/miguelrodriguez19/safecube/core/ui/component/SecretOutlinedTextField.kt"
    }
}
