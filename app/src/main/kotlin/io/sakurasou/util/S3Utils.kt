package io.sakurasou.util

import io.ktor.util.*
import io.sakurasou.model.entity.Strategy
import io.sakurasou.model.strategy.S3Strategy
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.apache.commons.codec.digest.DigestUtils
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider
import software.amazon.awssdk.core.exception.SdkClientException
import software.amazon.awssdk.core.sync.RequestBody
import software.amazon.awssdk.regions.Region
import software.amazon.awssdk.services.s3.S3Client
import software.amazon.awssdk.services.s3.model.PutObjectRequest
import software.amazon.awssdk.services.s3.model.S3Exception
import java.net.URI

/**
 * @author Shiina Kin
 * 2024/10/12 13:56
 */
object S3Utils {
    private val s3ClientMap by lazy { mutableMapOf<Long, S3Client>() }

    suspend fun uploadImage(relativePath: String, imageBytes: ByteArray, strategy: Strategy) {
        withContext(Dispatchers.IO) {
            val strategyConfig = strategy.config as S3Strategy
            runCatching {
                s3ClientMap[strategy.id] ?: run {
                    val s3 = S3Client.builder()
                        .region(Region.of(strategyConfig.region))
                        .endpointOverride(URI(strategyConfig.endpoint))
                        .credentialsProvider(
                            StaticCredentialsProvider.create(
                                AwsBasicCredentials.create(
                                    strategyConfig.accessKey,
                                    strategyConfig.secretKey
                                )
                            )
                        )
                        .forcePathStyle(true)
                        .build()
                    s3ClientMap[strategy.id] = s3
                    s3
                }
            }.onFailure {
                throw RuntimeException("Failed to create S3 client", it)
            }.onSuccess { s3Client ->
                val sha256 = DigestUtils.sha256(imageBytes).encodeBase64()
                val putObject = PutObjectRequest.builder()
                    .bucket(strategyConfig.bucketName)
                    .key(relativePath)
                    .checksumSHA256(sha256)
                    .build()
                runCatching {
                    s3Client.putObject(putObject, RequestBody.fromBytes(imageBytes))
                }.onFailure {
                    when (it) {
                        is SdkClientException -> throw RuntimeException("Failed to upload image to S3", it)
                        is S3Exception -> throw RuntimeException("Failed to upload image to S3", it)
                        else -> throw it
                    }
                }
            }
        }
    }
}