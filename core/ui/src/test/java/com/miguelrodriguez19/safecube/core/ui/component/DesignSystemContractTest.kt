package com.miguelrodriguez19.safecube.core.ui.component

import java.io.File
import javax.xml.parsers.DocumentBuilderFactory
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class DesignSystemContractTest {
    private val repositoryRoot = generateSequence(File(requireNotNull(System.getProperty("user.dir")))) {
        it.parentFile
    }.first { it.resolve("settings.gradle.kts").isFile }
    private val module = repositoryRoot.resolve("core/ui")

    @Test
    fun `component resources preserve English Spanish keys and format arguments`() {
        assertLocaleParity(module.resolve("src/main/res"))
        assertLocaleParity(repositoryRoot.resolve("app/src/debug/res"))
    }

    private fun assertLocaleParity(resources: File) {
        val englishFiles = resources.resolve("values").listFiles().orEmpty()
            .filter { it.name.startsWith("design_system_") && it.extension == "xml" }
        assertTrue("Design system resources must be present", englishFiles.isNotEmpty())

        englishFiles.forEach { englishFile ->
            val spanishFile = resources.resolve("values-es/${englishFile.name}")
            assertTrue("Missing Spanish resource file ${englishFile.name}", spanishFile.isFile)
            val english = strings(englishFile)
            val spanish = strings(spanishFile)
            assertEquals("Locale keys in ${englishFile.name}", english.keys, spanish.keys)
            english.forEach { (name, value) ->
                assertTrue("Empty English resource $name", value.isNotBlank())
                assertTrue("Empty Spanish resource $name", spanish.getValue(name).isNotBlank())
                assertEquals(
                    "Format arguments for $name",
                    FORMAT_ARGUMENT.findAll(value).map { it.value }.sorted().toList(),
                    FORMAT_ARGUMENT.findAll(spanish.getValue(name)).map { it.value }.sorted().toList(),
                )
            }
        }
    }

    @Test
    fun `public components do not import app features or domain services`() {
        val componentDirectory = module.resolve(
            "src/main/java/com/miguelrodriguez19/safecube/core/ui/component",
        )
        val forbidden = Regex(
            "(?m)^import com\\.miguelrodriguez19\\.safecube\\.(app|feature|core\\.(auth|vault|network|storage|crypto))\\.",
        )
        componentDirectory.walkTopDown().filter { it.extension == "kt" }.forEach { source ->
            assertFalse("Forbidden dependency in ${source.name}", forbidden.containsMatchIn(source.readText()))
        }
        val buildFile = module.resolve("build.gradle.kts").readText()
        assertFalse("core:ui must not depend on app or features", Regex(":(app|feature:)").containsMatchIn(buildFile))
    }

    private fun strings(file: File): Map<String, String> {
        val factory = DocumentBuilderFactory.newInstance().apply {
            setFeature("http://apache.org/xml/features/disallow-doctype-decl", true)
        }
        val nodes = factory.newDocumentBuilder().parse(file).getElementsByTagName("string")
        return (0 until nodes.length).mapNotNull { index ->
            val element = nodes.item(index) as org.w3c.dom.Element
            if (element.getAttribute("translatable") == "false") {
                null
            } else {
                element.getAttribute("name") to element.textContent
            }
        }.toMap()
    }

    private companion object {
        val FORMAT_ARGUMENT = Regex("%[0-9]+\\$[a-zA-Z]")
    }
}
