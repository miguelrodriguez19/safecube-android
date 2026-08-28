package com.miguelrodriguez19.safecube.core.network.contract

import java.io.File
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.boolean
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class VaultKeyMaterialOpenApiContractTest {
    private val contract: JsonObject by lazy {
        Json.parseToJsonElement(findOpenApiContract().readText()).jsonObject
    }

    @Test
    fun `get key material exposes strong revision and no store headers`() {
        val get = operation("/vault/keys", "get")

        assertContains(responseCodes(get), "200", "401", "403", "404")
        assertContains(responseHeaders(get, "200"), "ETag", "Cache-Control")
        assertTrue(responseHeader(get, "200", "ETag").requiredBoolean("required"))
        assertTrue(responseHeader(get, "200", "Cache-Control").requiredBoolean("required"))
    }

    @Test
    fun `master wrapper update requires one quoted if match precondition`() {
        val update = operation("/vault/keys/master", "put")
        val parameters = update["parameters"]?.jsonArray.orEmpty().map { it.jsonObject }
        val ifMatchParameters = parameters.filter { it.requiredString("name") == "If-Match" }

        assertEquals(1, ifMatchParameters.size)
        val ifMatch = ifMatchParameters.single()
        assertEquals("header", ifMatch.requiredString("in"))
        assertTrue(ifMatch.requiredBoolean("required"))
        assertEquals(
            "\"master-[1-9][0-9]*\"",
            ifMatch.objectAt("schema").requiredString("pattern"),
        )
        assertTrue(update.objectAt("requestBody").requiredBoolean("required"))
    }

    @Test
    fun `master wrapper update exposes cas outcomes and keeps body minimal`() {
        val update = operation("/vault/keys/master", "put")

        assertContains(responseCodes(update), "200", "400", "401", "403", "404", "412", "428")
        assertContains(responseHeaders(update, "200"), "ETag", "Cache-Control")
        assertTrue(responseHeader(update, "200", "ETag").requiredBoolean("required"))
        assertEquals(
            setOf("newKekEncMaster"),
            schema("UpdateMasterWrappedKekRequest").objectAt("properties").keys,
        )
        assertEquals(
            setOf("newKekEncMaster"),
            schema("UpdateMasterWrappedKekRequest")["required"]
                ?.jsonArray
                .orEmpty()
                .map { it.jsonPrimitive.content }
                .toSet(),
        )
    }

    private fun operation(path: String, method: String): JsonObject =
        contract.objectAt("paths", path, method)

    private fun responseCodes(operation: JsonObject): Set<String> =
        operation.objectAt("responses").keys

    private fun responseHeaders(operation: JsonObject, statusCode: String): Set<String> =
        operation.objectAt("responses", statusCode, "headers").keys

    private fun responseHeader(
        operation: JsonObject,
        statusCode: String,
        name: String,
    ): JsonObject = operation.objectAt("responses", statusCode, "headers", name)

    private fun schema(name: String): JsonObject =
        contract.objectAt("components", "schemas", name)

    private fun JsonObject.objectAt(vararg keys: String): JsonObject =
        keys.fold(this) { current, key ->
            current[key]?.jsonObject
                ?: throw AssertionError(
                    "OpenAPI contract is missing object '${keys.joinToString(".")}'.",
                )
        }

    private fun JsonObject.requiredString(key: String): String =
        this[key]?.jsonPrimitive?.content
            ?: throw AssertionError("OpenAPI contract is missing string '$key'.")

    private fun JsonObject.requiredBoolean(key: String): Boolean =
        this[key]?.jsonPrimitive?.boolean
            ?: throw AssertionError("OpenAPI contract is missing boolean '$key'.")

    private fun assertContains(actual: Set<String>, vararg expected: String) {
        val missing = expected.toSet() - actual
        assertTrue(
            "OpenAPI contract is missing: ${missing.sorted().joinToString()}",
            missing.isEmpty(),
        )
    }

    private fun findOpenApiContract(): File {
        val workingDirectory = File(requireNotNull(System.getProperty("user.dir")))
        return generateSequence(workingDirectory) { it.parentFile }
            .flatMap { directory ->
                sequenceOf(
                    directory.resolve("openapi/OpenAPI.json"),
                    directory.resolve("core/network/openapi/OpenAPI.json"),
                )
            }
            .firstOrNull(File::isFile)
            ?: error("Unable to locate core/network/openapi/OpenAPI.json.")
    }
}
