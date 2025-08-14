package io.sakurasou.hoshizora.model.strategy

import io.github.smiley4.schemakenerator.core.annotations.Name
import io.ktor.util.encodeBase64
import io.sakurasou.hoshizora.exception.service.image.io.s3.S3ClientException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import org.apache.commons.codec.digest.DigestUtils
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider
import software.amazon.awssdk.core.sync.RequestBody
import software.amazon.awssdk.regions.Region
import software.amazon.awssdk.services.s3.S3Client
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest
import software.amazon.awssdk.services.s3.model.GetObjectRequest
import software.amazon.awssdk.services.s3.model.HeadObjectRequest
import software.amazon.awssdk.services.s3.model.PutObjectRequest
import software.amazon.awssdk.services.s3.model.S3Exception
import java.net.URI

/**
 * @author Shiina Kin
 * 2024/9/9 09:27
 */
@Serializable
@SerialName("S3")
@Name("S3Strategy")
data class S3Strategy(
    val endpoint: String,
    val bucketName: String,
    val region: String,
    val accessKey: String,
    val secretKey: String,
    override val uploadFolder: String,
    override val thumbnailFolder: String,
    val publicUrl: String,
) : StrategyConfig(StrategyType.S3) {
    companion object {
        private val s3ClientMap by lazy { mutableMapOf<Int, S3Client>() }

        private fun getOrCreateS3Client(strategyConfig: S3Strategy): S3Client {
            val key =
                (
                    strategyConfig.endpoint + strategyConfig.bucketName +
                        strategyConfig.region + strategyConfig.accessKey
                ).hashCode()
            return s3ClientMap[key] ?: S3Client
                .builder()
                .region(Region.of(strategyConfig.region))
                .endpointOverride(URI(strategyConfig.endpoint))
                .credentialsProvider(
                    StaticCredentialsProvider.create(
                        AwsBasicCredentials.create(
                            strategyConfig.accessKey,
                            strategyConfig.secretKey,
                        ),
                    ),
                ).forcePathStyle(true)
                .build()
                .also { s3ClientMap[key] = it }
        }
    }

    override suspend fun upload(
        imageBytes: ByteArray,
        uploadPath: String,
    ) {
        withContext(Dispatchers.IO) {
            runCatching {
                val s3Client = getOrCreateS3Client(this@S3Strategy)
                val sha256 = DigestUtils.sha256(imageBytes).encodeBase64()
                val putObject =
                    PutObjectRequest
                        .builder()
                        .bucket(bucketName)
                        .key(uploadPath)
                        .checksumSHA256(sha256)
                        .build()
                s3Client.putObject(putObject, RequestBody.fromBytes(imageBytes))
            }.onFailure {
                when (it) {
                    is S3Exception -> throw S3ClientException(it)
                    else -> throw it
                }
            }
        }
    }

    override suspend fun delete(relativePath: String) {
        withContext(Dispatchers.IO) {
            runCatching {
                val s3Client = getOrCreateS3Client(this@S3Strategy)
                val deleteObjectRequest =
                    DeleteObjectRequest
                        .builder()
                        .bucket(bucketName)
                        .key(relativePath)
                        .build()
                s3Client.deleteObject(deleteObjectRequest)
            }.onFailure {
                when (it) {
                    is S3Exception -> throw S3ClientException(it)
                    else -> throw it
                }
            }
        }
    }

    override suspend fun fetch(relativePath: String): ByteArray =
        withContext(Dispatchers.IO) {
            runCatching {
                val s3Client = getOrCreateS3Client(this@S3Strategy)
                val getObjectRequest =
                    GetObjectRequest
                        .builder()
                        .bucket(bucketName)
                        .key(relativePath)
                        .build()
                s3Client.getObject(getObjectRequest)
            }.onFailure {
                when (it) {
                    is S3Exception -> throw S3ClientException(it)
                    else -> throw it
                }
            }.getOrThrow()
                .readBytes()
        }

    suspend fun isImageExist(relativePath: String): Boolean {
        return withContext(Dispatchers.IO) {
            runCatching {
                val s3Client = getOrCreateS3Client(this@S3Strategy)
                val headObjectRequest =
                    HeadObjectRequest
                        .builder()
                        .bucket(bucketName)
                        .key(relativePath)
                        .build()
                s3Client.headObject(headObjectRequest)
                true
            }.onFailure {
                when (it) {
                    is S3Exception -> {
                        if (it.statusCode() == 404) return@withContext false
                        throw S3ClientException(it)
                    }

                    else -> throw it
                }
            }.getOrThrow()
        }
    }
}
