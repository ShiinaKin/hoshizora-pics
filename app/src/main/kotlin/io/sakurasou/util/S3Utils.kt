package io.sakurasou.util

import io.ktor.util.*
import io.sakurasou.exception.service.image.io.s3.S3ClientException
import io.sakurasou.model.entity.Strategy
import io.sakurasou.model.strategy.S3Strategy
import org.apache.commons.codec.digest.DigestUtils
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider
import software.amazon.awssdk.core.sync.RequestBody
import software.amazon.awssdk.regions.Region
import software.amazon.awssdk.services.s3.S3Client
import software.amazon.awssdk.services.s3.model.*
import java.io.InputStream
import java.net.URI

/**
 * @author Shiina Kin
 * 2024/10/12 13:56
 */
object S3Utils {
    private val s3ClientMap by lazy { mutableMapOf<Long, S3Client>() }

    fun uploadImage(uploadPath: String, imageBytes: ByteArray, strategy: Strategy) {
        runCatching {
            val strategyConfig = strategy.config as S3Strategy
            val s3Client = getOrCreateS3Client(strategy, strategyConfig)
            val sha256 = DigestUtils.sha256(imageBytes).encodeBase64()
            val putObject = PutObjectRequest.builder()
                .bucket(strategyConfig.bucketName)
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

    fun deleteImage(relativePath: String, strategy: Strategy) {
        runCatching {
            val strategyConfig = strategy.config as S3Strategy
            val s3Client = getOrCreateS3Client(strategy, strategyConfig)
            val deleteObjectRequest = DeleteObjectRequest.builder()
                .bucket(strategyConfig.bucketName)
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

    fun isImageExist(relativePath: String, strategy: Strategy): Boolean {
        return runCatching {
            val strategyConfig = strategy.config as S3Strategy
            val s3Client = getOrCreateS3Client(strategy, strategyConfig)
            val headObjectRequest = HeadObjectRequest.builder()
                .bucket(strategyConfig.bucketName)
                .key(relativePath)
                .build()
            s3Client.headObject(headObjectRequest)
            true
        }.onFailure {
            when (it) {
                is S3Exception -> {
                    if (it.statusCode() == 404) return false
                    throw S3ClientException(it)
                }

                else -> throw it
            }
        }.getOrThrow()
    }

    fun fetchImage(relativePath: String, strategy: Strategy): InputStream {
        return runCatching {
            val strategyConfig = strategy.config as S3Strategy
            val s3Client = getOrCreateS3Client(strategy, strategyConfig)
            val getObjectRequest = GetObjectRequest.builder()
                .bucket(strategyConfig.bucketName)
                .key(relativePath)
                .build()
            s3Client.getObject(getObjectRequest)
        }.onFailure {
            when (it) {
                is S3Exception -> throw S3ClientException(it)
                else -> throw it
            }
        }.getOrThrow()
    }

    private fun getOrCreateS3Client(
        strategy: Strategy,
        strategyConfig: S3Strategy
    ): S3Client = s3ClientMap[strategy.id] ?: S3Client.builder()
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
        .also { s3ClientMap[strategy.id] = it }
}