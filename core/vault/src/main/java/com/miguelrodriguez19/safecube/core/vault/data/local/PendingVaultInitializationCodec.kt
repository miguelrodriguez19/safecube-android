package com.miguelrodriguez19.safecube.core.vault.data.local

import com.miguelrodriguez19.safecube.core.vault.domain.model.VaultKeyMaterial
import com.miguelrodriguez19.safecube.core.vault.domain.model.initialize.PendingVaultInitialization
import com.miguelrodriguez19.safecube.core.vault.domain.model.initialize.PendingVaultInitializationState
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.io.DataInputStream
import java.io.DataOutputStream
import java.nio.charset.StandardCharsets
import java.util.Base64
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PendingVaultInitializationCodec @Inject constructor() {
    fun encode(value: PendingVaultInitialization): String {
        val bytes = ByteArrayOutputStream().use { output ->
            DataOutputStream(output).use { data ->
                data.writeByte(FORMAT_VERSION.toInt())
                data.writeByte(value.state.ordinal)
                writeNullableUuid(data, value.candidate.accountId)
                writeBytes(data, value.candidate.kekEncMaster)
                writeBytes(data, value.candidate.kekEncRecovery)
                writeString(data, value.candidate.kdfAlgorithm)
                writeBytes(data, value.candidate.kdfSalt)
                data.writeInt(value.candidate.kdfMemoryKib)
                data.writeInt(value.candidate.kdfIterations)
                data.writeInt(value.candidate.kdfParallelism)
                data.writeInt(value.candidate.kdfOutputLen)
                writeString(data, value.candidate.cryptoVersion)
                writeBytes(data, value.recoveryKey)
            }
            output.toByteArray()
        }
        return Base64.getEncoder().encodeToString(bytes)
    }

    fun decode(encoded: String): PendingVaultInitialization? = runCatching {
        val bytes = Base64.getDecoder().decode(encoded)
        require(bytes.size <= MAX_RECORD_BYTES) { "Pending initialization record is too large." }

        DataInputStream(ByteArrayInputStream(bytes)).use { data ->
            require(data.readUnsignedByte().toByte() == FORMAT_VERSION) {
                "Pending initialization record version is unsupported."
            }
            val state = when (data.readUnsignedByte()) {
                PendingVaultInitializationState.AwaitingRemoteConfirmation.ordinal ->
                    PendingVaultInitializationState.AwaitingRemoteConfirmation
                PendingVaultInitializationState.RemoteConfirmed.ordinal ->
                    PendingVaultInitializationState.RemoteConfirmed
                else -> error("Pending initialization record state is unsupported.")
            }
            val candidate = VaultKeyMaterial(
                accountId = readNullableUuid(data),
                kekEncMaster = readBytes(data),
                kekEncRecovery = readBytes(data),
                kdfAlgorithm = readString(data),
                kdfSalt = readBytes(data),
                kdfMemoryKib = data.readInt().also(::requirePositive),
                kdfIterations = data.readInt().also(::requirePositive),
                kdfParallelism = data.readInt().also(::requirePositive),
                kdfOutputLen = data.readInt().also(::requirePositive),
                cryptoVersion = readString(data),
            )
            val recoveryKey = readBytes(data)
            require(data.available() == 0) { "Pending initialization record has trailing bytes." }
            PendingVaultInitialization(
                candidate = candidate,
                recoveryKey = recoveryKey,
                state = state,
            )
        }
    }.getOrNull()

    private fun writeNullableUuid(data: DataOutputStream, value: UUID?) {
        if (value == null) {
            data.writeBoolean(false)
        } else {
            data.writeBoolean(true)
            data.writeLong(value.mostSignificantBits)
            data.writeLong(value.leastSignificantBits)
        }
    }

    private fun readNullableUuid(data: DataInputStream): UUID? = if (data.readBoolean()) {
        UUID(data.readLong(), data.readLong())
    } else {
        null
    }

    private fun writeBytes(data: DataOutputStream, value: ByteArray) {
        require(value.isNotEmpty()) { "Pending initialization byte fields must not be empty." }
        require(value.size <= MAX_FIELD_BYTES) { "Pending initialization byte field is too large." }
        data.writeInt(value.size)
        data.write(value)
    }

    private fun readBytes(data: DataInputStream): ByteArray {
        val length = data.readInt()
        require(length in 1..MAX_FIELD_BYTES) { "Pending initialization byte field is invalid." }
        return ByteArray(length).also(data::readFully)
    }

    private fun writeString(data: DataOutputStream, value: String) {
        val bytes = value.toByteArray(StandardCharsets.UTF_8)
        require(bytes.isNotEmpty()) { "Pending initialization string fields must not be empty." }
        require(bytes.size <= MAX_STRING_BYTES) { "Pending initialization string field is too large." }
        data.writeInt(bytes.size)
        data.write(bytes)
    }

    private fun readString(data: DataInputStream): String {
        val length = data.readInt()
        require(length in 1..MAX_STRING_BYTES) { "Pending initialization string field is invalid." }
        return ByteArray(length).also(data::readFully).toString(StandardCharsets.UTF_8)
    }

    private fun requirePositive(value: Int) {
        require(value > 0) { "Pending initialization numeric fields must be positive." }
    }

    private companion object {
        const val FORMAT_VERSION: Byte = 0x01
        const val MAX_FIELD_BYTES = 4096
        const val MAX_STRING_BYTES = 256
        const val MAX_RECORD_BYTES = 32 * 1024
    }
}
