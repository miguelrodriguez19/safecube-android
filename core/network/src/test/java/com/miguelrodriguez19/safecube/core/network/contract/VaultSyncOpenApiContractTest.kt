package com.miguelrodriguez19.safecube.core.network.contract

import java.io.File
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import org.junit.Assert.assertTrue
import org.junit.Test

class VaultSyncOpenApiContractTest {
    private val contract: JsonObject by lazy {
        Json.parseToJsonElement(findOpenApiContract().readText()).jsonObject
    }

    @Test
    fun `mutations require concurrency and idempotency headers`() {
        val create = operation("/vault/items", "post")
        val update = operation("/vault/items/{itemId}", "put")
        val delete = operation("/vault/items/{itemId}", "delete")

        assertContains(parameterNames(create), "Idempotency-Key")
        assertContains(parameterNames(update), "Idempotency-Key", "If-Match")
        assertContains(parameterNames(delete), "Idempotency-Key", "If-Match")

        assertContains(responseCodes(create), "201", "400", "409")
        assertContains(responseCodes(update), "200", "400", "409", "412", "428")
        assertContains(responseCodes(delete), "200", "400", "409", "412", "428")

        assertContains(responseHeaders(create, "201"), "ETag")
        assertContains(responseHeaders(update, "200"), "ETag")
        assertContains(responseHeaders(delete, "200"), "ETag")
        assertContains(
            responseHeaders(operation("/vault/items/{itemId}", "get"), "200"),
            "ETag",
        )
    }

    @Test
    fun `mutation schemas separate payload version from server revisions`() {
        assertContains(requiredProperties("CreateSecureItemRequest"), "payloadVersion")
        assertContains(requiredProperties("UpdateSecureItemRequest"), "payloadVersion")

        val commonResultFields = arrayOf(
            "mutationId",
            "payloadVersion",
            "itemRevision",
            "changeSequence",
        )
        assertContains(
            requiredProperties("CreateSecureItemResult"),
            *commonResultFields,
            "updatedAt",
        )
        assertContains(
            requiredProperties("UpdateSecureItemResult"),
            *commonResultFields,
            "updatedAt",
        )
        assertContains(
            requiredProperties("DeleteSecureItemResult"),
            *commonResultFields,
            "deletedAt",
        )
    }

    @Test
    fun `changes endpoint exposes complete snapshots tombstones and cursor metadata`() {
        val changes = operation("/vault/items/changes", "get")
        assertContains(parameterNames(changes), "after", "limit")
        assertContains(responseCodes(changes), "200", "400")

        assertContains(
            requiredProperties("SecureItemChangeResponse"),
            "itemId",
            "itemType",
            "schemaVersion",
            "displayHint",
            "payload",
            "payloadVersion",
            "itemRevision",
            "changeSequence",
            "updatedAt",
        )
        assertContains(propertyNames("SecureItemChangeResponse"), "deletedAt")
        assertContains(
            requiredProperties("ListSecureItemChangesResponse"),
            "items",
            "nextCursor",
            "hasMore",
        )
        assertContains(propertyNames("ErrorResponse"), "error", "fields")
    }

    private fun operation(path: String, method: String): JsonObject =
        contract.objectAt("paths", path, method)

    private fun parameterNames(operation: JsonObject): Set<String> =
        operation["parameters"]
            ?.jsonArray
            .orEmpty()
            .map { it.jsonObject.requiredString("name") }
            .toSet()

    private fun responseCodes(operation: JsonObject): Set<String> =
        operation.objectAt("responses").keys

    private fun responseHeaders(operation: JsonObject, statusCode: String): Set<String> =
        operation.objectAt("responses", statusCode, "headers").keys

    private fun requiredProperties(schemaName: String): Set<String> =
        schema(schemaName)["required"]
            ?.jsonArray
            .orEmpty()
            .map { it.jsonPrimitive.content }
            .toSet()

    private fun propertyNames(schemaName: String): Set<String> =
        schema(schemaName).objectAt("properties").keys

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
