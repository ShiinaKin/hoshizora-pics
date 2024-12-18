package io.sakurasou.util

import io.github.oshai.kotlinlogging.KotlinLogging
import io.sakurasou.exception.service.image.io.ImageFileCreateFailedException
import io.sakurasou.exception.service.image.io.ImageFileNotFoundException
import io.sakurasou.exception.service.image.io.ImageParentFolderCreateFailedException
import io.sakurasou.model.entity.Strategy
import io.sakurasou.model.group.ImageType
import io.sakurasou.model.strategy.LocalStrategy
import io.sakurasou.model.strategy.S3Strategy
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import net.coobird.thumbnailator.Thumbnails
import java.awt.image.BufferedImage
import java.io.ByteArrayOutputStream
import java.io.File

/**
 * @author Shiina Kin
 * 2024/10/12 08:52
 */

object ImageUtils {
    private val logger = KotlinLogging.logger {}

    suspend fun uploadImageAndGetRelativePath(
        strategy: Strategy,
        subFolder: String,
        fileName: String,
        bytes: ByteArray
    ): String {
        return withContext(Dispatchers.IO) {
            val relativePath = "$subFolder/$fileName"
            when (val strategyConfig = strategy.config) {
                is LocalStrategy -> {
                    val uploadFolderStr = strategyConfig.uploadFolder
                    val uploadFolder = File(uploadFolderStr, subFolder)
                    if (!uploadFolder.exists() && !uploadFolder.mkdirs()) {
                        throw ImageParentFolderCreateFailedException()
                    }
                    val uploadFile = File(uploadFolder, fileName)
                    runCatching {
                        uploadFile.writeBytes(bytes)
                    }.onFailure {
                        logger.error(it) { "Failed to save image $fileName at ${uploadFile.absolutePath}" }
                        throw ImageFileCreateFailedException()
                    }.onSuccess {
                        logger.debug { "save image $fileName at ${uploadFile.absolutePath}" }
                    }
                }

                is S3Strategy -> {
                    val filePath = "${strategyConfig.uploadFolder}/$relativePath"
                    S3Utils.uploadImage(filePath, bytes, strategy)
                }
            }
            relativePath
        }
    }

    fun saveThumbnail(
        strategy: Strategy,
        subFolder: String,
        fileName: String,
        thumbnailBytes: ByteArray,
        relativePath: String
    ) {
        when (val strategyConfig = strategy.config) {
            is LocalStrategy -> {
                val thumbnailFolderStr = strategyConfig.thumbnailFolder
                val thumbnailFolder = File(thumbnailFolderStr, subFolder)
                if (!thumbnailFolder.exists() && !thumbnailFolder.mkdirs()) {
                    logger.error { "Failed to create thumbnail folder" }
                    return
                }
                val thumbnailFile = File(thumbnailFolder, fileName)
                runCatching {
                    thumbnailFile.writeBytes(thumbnailBytes)
                }.onFailure {
                    logger.error(it) { "Failed to save thumbnail of image $fileName at ${thumbnailFile.absolutePath}" }
                    return
                }.onSuccess {
                    logger.debug { "save thumbnail of image $fileName at ${thumbnailFile.absolutePath}" }
                }
            }

            is S3Strategy -> {
                val filePath = "${strategyConfig.thumbnailFolder}/$relativePath"
                S3Utils.uploadImage(filePath, thumbnailBytes, strategy)
            }
        }
    }

    suspend fun fetchLocalImage(strategy: Strategy, relativePath: String, isThumbnail: Boolean = false): ByteArray? {
        return withContext(Dispatchers.IO) {
            when (val strategyConfig = strategy.config) {
                is S3Strategy -> throw RuntimeException("Not supported")

                is LocalStrategy -> {
                    val parentFolderStr =
                        if (isThumbnail) strategyConfig.thumbnailFolder else strategyConfig.uploadFolder
                    val uploadFile = File(parentFolderStr, relativePath)
                    if (uploadFile.exists()) uploadFile.readBytes() else null
                }
            }
        }
    }

    suspend fun fetchS3Image(strategy: Strategy, relativePath: String, isThumbnail: Boolean = false): String {
        return withContext(Dispatchers.IO) {
            when (val strategyConfig = strategy.config) {
                is LocalStrategy -> throw RuntimeException("Not supported")

                is S3Strategy -> {
                    val folder = if (isThumbnail) strategyConfig.thumbnailFolder else strategyConfig.uploadFolder
                    val relativePath = "$folder/$relativePath"
                    if (!S3Utils.isImageExist(relativePath, strategy)) {
                        if (isThumbnail) return@withContext ""
                        throw ImageFileNotFoundException()
                    }
                    "${strategyConfig.publicUrl}/$relativePath"
                }
            }
        }
    }

    suspend fun transformImage(rawImage: BufferedImage, targetImageType: ImageType): ByteArray {
        return withContext(Dispatchers.IO) {
            ByteArrayOutputStream().apply {
                Thumbnails.of(rawImage)
                    .outputFormat(targetImageType.name)
                    .toOutputStream(this)
            }.use { it.toByteArray() }
        }
    }

    suspend fun transformImage(rawImage: BufferedImage, targetImageType: ImageType, quality: Double): ByteArray {
        return withContext(Dispatchers.IO) {
            ByteArrayOutputStream().apply {
                Thumbnails.of(rawImage)
                    .outputFormat(targetImageType.name)
                    .outputQuality(quality)
                    .toOutputStream(this)
            }.use { it.toByteArray() }
        }
    }

    private fun transformImage(
        rawImage: BufferedImage,
        targetImageType: ImageType,
        newWidth: Int,
        newHeight: Int,
        quality: Double
    ): ByteArray = ByteArrayOutputStream()
        .apply {
            Thumbnails.of(rawImage)
                .size(newWidth, newHeight)
                .outputFormat(targetImageType.name)
                .outputQuality(quality)
                .toOutputStream(this)
        }.use { it.toByteArray() }

    fun transformImageByWidth(
        rawImage: BufferedImage,
        targetImageType: ImageType,
        newWidth: Int,
        quality: Double
    ): ByteArray {
        val originalWidth = rawImage.width
        val originalHeight = rawImage.height
        val newHeight = (originalHeight * newWidth) / originalWidth
        return transformImage(rawImage, targetImageType, newWidth, newHeight, quality)
    }

    fun transformImageByHeight(
        rawImage: BufferedImage,
        targetImageType: ImageType,
        newHeight: Int,
        quality: Double
    ): ByteArray {
        val originalWidth = rawImage.width
        val originalHeight = rawImage.height
        val newWidth = (originalWidth * newHeight) / originalHeight
        return transformImage(rawImage, targetImageType, newWidth, newHeight, quality)
    }
}