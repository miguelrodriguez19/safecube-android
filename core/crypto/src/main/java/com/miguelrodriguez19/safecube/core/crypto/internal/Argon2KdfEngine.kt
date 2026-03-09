package com.miguelrodriguez19.safecube.core.crypto.internal

import com.miguelrodriguez19.safecube.core.crypto.KdfEngine
import com.miguelrodriguez19.safecube.core.crypto.KdfRequest
import org.bouncycastle.crypto.generators.Argon2BytesGenerator
import org.bouncycastle.crypto.params.Argon2Parameters
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class Argon2KdfEngine @Inject constructor() : KdfEngine {

    override fun deriveKey(request: KdfRequest): ByteArray {
        require(request.secret.isNotEmpty()) { "KdfRequest.secret must not be empty." }
        require(request.salt.isNotEmpty()) { "KdfRequest.salt must not be empty." }
        require(request.outputLengthBytes > 0) { "KdfRequest.outputLengthBytes must be > 0." }
        require(request.iterations > 0) { "KdfRequest.iterations must be > 0." }
        require(request.memoryKib > 0) { "KdfRequest.memoryKib must be > 0." }
        require(request.parallelism > 0) { "KdfRequest.parallelism must be > 0." }

        val secretCopy = request.secret.copyOf()
        val contextInfoCopy = request.contextInfo?.copyOf()

        return try {
            val builder = Argon2Parameters.Builder(Argon2Parameters.ARGON2_id)
                .withSalt(request.salt)
                .withIterations(request.iterations)
                .withMemoryAsKB(request.memoryKib)
                .withParallelism(request.parallelism)

            if (contextInfoCopy != null) {
                builder.withAdditional(contextInfoCopy)
            }

            val parameters = builder.build()
            val generator = Argon2BytesGenerator()
            generator.init(parameters)

            ByteArray(request.outputLengthBytes).also { output ->
                generator.generateBytes(secretCopy, output, 0, output.size)
            }
        } finally {
            secretCopy.fill(0)
            contextInfoCopy?.fill(0)
        }
    }
}
