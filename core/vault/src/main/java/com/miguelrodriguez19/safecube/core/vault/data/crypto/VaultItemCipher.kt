package com.miguelrodriguez19.safecube.core.vault.data.crypto

import com.miguelrodriguez19.safecube.core.crypto.domain.model.DecryptionRequest
import com.miguelrodriguez19.safecube.core.crypto.domain.model.EncryptionRequest
import com.miguelrodriguez19.safecube.core.crypto.domain.model.EncryptionResult
import com.miguelrodriguez19.safecube.core.crypto.domain.model.KeyUnwrapRequest
import com.miguelrodriguez19.safecube.core.crypto.domain.model.KeyWrapRequest
import com.miguelrodriguez19.safecube.core.crypto.domain.port.CryptoEngine
import com.miguelrodriguez19.safecube.core.crypto.domain.port.KeyWrapping
import com.miguelrodriguez19.safecube.core.crypto.domain.service.SaltGenerator
import com.miguelrodriguez19.safecube.core.vault.domain.codec.SecureItemContentCodec
import com.miguelrodriguez19.safecube.core.vault.domain.codec.SecureItemContentDecodeResult
import com.miguelrodriguez19.safecube.core.vault.domain.config.VaultCryptoDefaults
import com.miguelrodriguez19.safecube.core.vault.domain.model.secureitem.SecureItem
import com.miguelrodriguez19.safecube.core.vault.domain.model.secureitem.itemcontent.SecureItemContent
import com.miguelrodriguez19.safecube.core.vault.domain.service.EncryptedSecureItemPayload
import com.miguelrodriguez19.safecube.core.vault.domain.service.SecureItemCryptoError
import com.miguelrodriguez19.safecube.core.vault.domain.service.SecureItemCryptoService
import com.miguelrodriguez19.safecube.core.vault.domain.service.SecureItemDecryptionResult
import com.miguelrodriguez19.safecube.core.vault.domain.service.SecureItemEncryptionResult
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
internal class VaultItemCipher @Inject constructor(
    private val contentCodec: SecureItemContentCodec,
    private val cryptoEngine: CryptoEngine,
    private val keyWrapping: KeyWrapping,
    private val secureItemCryptoContextProvider: SecureItemCryptoContextProvider,
    private val secureItemPayloadAadFactory: SecureItemPayloadAadFactory,
    private val secureItemPayloadEnvelopeV1Codec: SecureItemPayloadEnvelopeV1Codec,
    private val saltGenerator: SaltGenerator,
) : SecureItemCryptoService {
    override fun encrypt(
        logicalItemId: java.util.UUID,
        payloadVersion: Long,
        content: SecureItemContent,
    ): SecureItemEncryptionResult {
        require(payloadVersion > 0) { "payloadVersion must be positive." }

        val context = when (val result = secureItemCryptoContextProvider.get()) {
            is SecureItemCryptoContextResult.Available -> result
            SecureItemCryptoContextResult.VaultLocked -> {
                return SecureItemEncryptionResult.Error(SecureItemCryptoError.VaultLocked)
            }
            SecureItemCryptoContextResult.AccountIdUnavailable -> {
                return SecureItemEncryptionResult.Error(SecureItemCryptoError.AccountIdUnavailable)
            }
        }
        val aad = secureItemPayloadAadFactory.create(
            accountId = context.accountId,
            logicalItemId = logicalItemId,
            payloadVersion = payloadVersion,
        )
        val encodedContent = contentCodec.encode(content)
        val dek = saltGenerator.generate(VaultCryptoDefaults.KEY_LENGTH_BYTES)
        val sensitiveBuffers = mutableListOf(context.kek, aad, encodedContent.payload, dek)

        return try {
            val wrappedDek = keyWrapping.wrapKey(
                request = KeyWrapRequest(
                    keyToWrap = dek,
                    wrappingKey = context.kek,
                    aad = aad,
                ),
            )
            sensitiveBuffers += wrappedDek

            val encryptionOutput = cryptoEngine.encrypt(
                request = EncryptionRequest(
                    plaintext = encodedContent.payload,
                    keyMaterial = dek,
                    aad = aad,
                ),
            )
            sensitiveBuffers += encryptionOutput.ciphertext
            sensitiveBuffers += encryptionOutput.iv
            sensitiveBuffers += encryptionOutput.authTag

            SecureItemEncryptionResult.Success(
                payload = EncryptedSecureItemPayload(
                    itemType = encodedContent.itemType,
                    schemaVersion = encodedContent.schemaVersion,
                    payload = secureItemPayloadEnvelopeV1Codec.encode(
                        logicalItemId = logicalItemId,
                        wrappedDek = wrappedDek,
                        nonce = encryptionOutput.iv,
                        ciphertext = encryptionOutput.ciphertext,
                        authTag = encryptionOutput.authTag,
                    ),
                ),
            )
        } catch (_: Exception) {
            SecureItemEncryptionResult.Error(SecureItemCryptoError.CryptographicFailure)
        } finally {
            zeroizeAll(sensitiveBuffers)
        }
    }

    override fun decrypt(item: SecureItem): SecureItemDecryptionResult {
        val context = when (val result = secureItemCryptoContextProvider.get()) {
            is SecureItemCryptoContextResult.Available -> result
            SecureItemCryptoContextResult.VaultLocked -> {
                return SecureItemDecryptionResult.Error(SecureItemCryptoError.VaultLocked)
            }
            SecureItemCryptoContextResult.AccountIdUnavailable -> {
                return SecureItemDecryptionResult.Error(SecureItemCryptoError.AccountIdUnavailable)
            }
        }
        val aad = secureItemPayloadAadFactory.create(
            accountId = context.accountId,
            logicalItemId = item.logicalItemId,
            payloadVersion = item.payloadVersion,
        )
        val sensitiveBuffers = mutableListOf(context.kek, aad)

        return try {
            val decodedEnvelope = secureItemPayloadEnvelopeV1Codec.decode(item.payload)
            sensitiveBuffers += decodedEnvelope.wrappedDek
            sensitiveBuffers += decodedEnvelope.nonce
            sensitiveBuffers += decodedEnvelope.ciphertext
            sensitiveBuffers += decodedEnvelope.authTag
            if (decodedEnvelope.logicalItemId != item.logicalItemId) {
                return SecureItemDecryptionResult.Error(SecureItemCryptoError.MalformedPayload)
            }

            val unwrappedDek = keyWrapping.unwrapKey(
                request = KeyUnwrapRequest(
                    wrappedKey = decodedEnvelope.wrappedDek,
                    wrappingKey = context.kek,
                    aad = aad,
                ),
            )
            sensitiveBuffers += unwrappedDek

            val plaintext = cryptoEngine.decrypt(
                request = DecryptionRequest(
                    ciphertext = decodedEnvelope.ciphertext,
                    keyMaterial = unwrappedDek,
                    iv = decodedEnvelope.nonce,
                    aad = aad,
                    authTag = decodedEnvelope.authTag,
                ),
            )
            sensitiveBuffers += plaintext

            when (
                val decoded = contentCodec.decode(
                    itemType = item.itemType.wireName,
                    schemaVersion = item.schemaVersion,
                    payload = plaintext,
                )
            ) {
                is SecureItemContentDecodeResult.Success -> {
                    SecureItemDecryptionResult.Success(decoded.content)
                }

                is SecureItemContentDecodeResult.Error -> {
                    SecureItemDecryptionResult.Error(
                        SecureItemCryptoError.ContentDecodingFailed(decoded.reason),
                    )
                }
            }
        } catch (_: IllegalArgumentException) {
            SecureItemDecryptionResult.Error(SecureItemCryptoError.MalformedPayload)
        } catch (_: Exception) {
            SecureItemDecryptionResult.Error(SecureItemCryptoError.CryptographicFailure)
        } finally {
            zeroizeAll(sensitiveBuffers)
        }
    }
}

private fun zeroizeAll(buffers: Iterable<ByteArray>) {
    buffers.forEach(ByteArray::fillWithZeros)
}

private fun ByteArray.fillWithZeros() {
    fill(0)
}
