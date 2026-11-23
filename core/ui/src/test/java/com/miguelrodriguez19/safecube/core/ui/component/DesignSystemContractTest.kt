package com.miguelrodriguez19.safecube.core.ui.component

import java.io.File
import javax.xml.parsers.DocumentBuilderFactory
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import org.w3c.dom.Element

class DesignSystemContractTest {
    private val repositoryRoot = generateSequence(File(requireNotNull(System.getProperty("user.dir")))) {
        it.parentFile
    }.first { it.resolve("settings.gradle.kts").isFile }
    private val module = repositoryRoot.resolve("core/ui")

    @Test
    fun `component resources preserve English Spanish keys and format arguments`() {
        assertLocaleParity(module.resolve("src/main/res"))
        assertLocaleParity(repositoryRoot.resolve("app/src/main/res"))
        assertLocaleParity(repositoryRoot.resolve("app/src/debug/res"))
        assertLocaleParity(repositoryRoot.resolve("feature/vault/src/main/res"))
        assertLocaleParity(repositoryRoot.resolve("feature/auth/src/main/res"))
    }

    private fun assertLocaleParity(resources: File) {
        val english = strings(resources.resolve("values"))
        val spanish = strings(resources.resolve("values-es"))
        assertTrue("Localized resources must be present in $resources", english.isNotEmpty())
        assertEquals("Locale keys in $resources", english.keys, spanish.keys)
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

    private fun strings(directory: File): Map<String, String> {
        val factory = DocumentBuilderFactory.newInstance().apply {
            setFeature("http://apache.org/xml/features/disallow-doctype-decl", true)
        }
        return directory.listFiles().orEmpty().filter { it.extension == "xml" }.flatMap { file ->
            val nodes = factory.newDocumentBuilder().parse(file).getElementsByTagName("string")
            (0 until nodes.length).mapNotNull { index ->
                val element = nodes.item(index) as Element
                if (element.getAttribute("translatable") == "false") {
                    null
                } else {
                    element.getAttribute("name") to element.textContent
                }
            }
        }.toMap()
    }

    private companion object {
        val FORMAT_ARGUMENT = Regex("%[0-9]+\\$[a-zA-Z]")
    }
}
