package io.sakurasou.util

import io.github.oshai.kotlinlogging.KotlinLogging
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
private val logger = KotlinLogging.logger {}

private const val THUMBNAIL_HEIGHT = 250

object ImageUtils {
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
                        throw RuntimeException("Failed to create upload folder")
                    }
                    val uploadFile = File(uploadFolder, fileName)
                    uploadFile.writeBytes(bytes)
                    logger.trace { "save image $fileName at ${uploadFile.absolutePath}" }
                }

                is S3Strategy -> {
                    val filePath = "${strategyConfig.uploadFolder}/$relativePath"
                    S3Utils.uploadImage(filePath, bytes, strategy)
                }
            }
            relativePath
        }
    }

    suspend fun createAndUploadThumbnail(
        strategy: Strategy,
        subFolder: String,
        fileName: String,
        image: BufferedImage
    ) {
        return withContext(Dispatchers.IO) {
            val relativePath = "$subFolder/$fileName"
            val imageType = ImageType.valueOf(fileName.substringAfterLast('.').uppercase())
            val thumbnailBytes = transformImageByHeight(image, imageType, THUMBNAIL_HEIGHT, 0.5)

            when (val strategyConfig = strategy.config) {
                is LocalStrategy -> {
                    val thumbnailFolderStr = strategyConfig.thumbnailFolder
                    val thumbnailFolder = File(thumbnailFolderStr, subFolder)
                    if (!thumbnailFolder.exists() && !thumbnailFolder.mkdirs()) {
                        logger.error { "Failed to create thumbnail folder" }
                        return@withContext
                    }

                    val thumbnailFile = File(thumbnailFolder, fileName)
                    thumbnailFile.writeBytes(thumbnailBytes)
                    logger.trace { "save thumbnail of image $fileName at ${thumbnailFile.absolutePath}" }
                }

                is S3Strategy -> {
                    val filePath = "${strategyConfig.uploadFolder}/$relativePath"
                    S3Utils.uploadImage(filePath, thumbnailBytes, strategy)
                }
            }
        }
    }

    suspend fun deleteImage(strategy: Strategy, relativePath: String) {
        return withContext(Dispatchers.IO) {
            when (val strategyConfig = strategy.config) {
                is LocalStrategy -> {
                    val uploadFolderStr = strategyConfig.uploadFolder
                    val uploadFile = File(uploadFolderStr, relativePath)

                    if (uploadFile.exists() && !uploadFile.delete()) {
                        throw RuntimeException("Failed to delete image")
                    }
                }

                is S3Strategy -> {
                    val filePath = "${strategyConfig.uploadFolder}/$relativePath"
                    S3Utils.deleteImage(filePath, strategy)
                }
            }
        }
    }

    suspend fun fetchLocalImage(strategy: Strategy, relativePath: String, isThumbnail: Boolean = false): ByteArray {
        return withContext(Dispatchers.IO) {
            when (val strategyConfig = strategy.config) {
                is S3Strategy -> throw RuntimeException("Not supported")

                is LocalStrategy -> {
                    val parentFolderStr =
                        if (isThumbnail) strategyConfig.thumbnailFolder else strategyConfig.uploadFolder
                    val uploadFile = File(parentFolderStr, relativePath)

                    if (!uploadFile.exists()) {
                        throw RuntimeException("Image not found")
                    }
                    uploadFile.readBytes()
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
                    "${strategyConfig.publicUrl}/$folder/$relativePath"
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

    suspend fun transformImage(
        rawImage: BufferedImage,
        targetImageType: ImageType,
        newWidth: Int,
        newHeight: Int,
        quality: Double
    ): ByteArray {
        return withContext(Dispatchers.IO) {
            ByteArrayOutputStream().apply {
                Thumbnails.of(rawImage)
                    .size(newWidth, newHeight)
                    .outputFormat(targetImageType.name)
                    .outputQuality(quality)
                    .toOutputStream(this)
            }.use { it.toByteArray() }
        }
    }

    private fun transformImage(
        rawImage: BufferedImage,
        targetType: String,
        newWidth: Int,
        newHeight: Int,
        quality: Double
    ): ByteArray = ByteArrayOutputStream()
        .apply {
            Thumbnails.of(rawImage)
                .size(newWidth, newHeight)
                .outputFormat(targetType)
                .outputQuality(quality)
                .toOutputStream(this)
        }.use { it.toByteArray() }

    suspend fun transformImageByWidth(
        rawImage: BufferedImage,
        targetImageType: ImageType,
        newWidth: Int,
        quality: Double
    ): ByteArray {
        return withContext(Dispatchers.IO) {
            val originalWidth = rawImage.width
            val originalHeight = rawImage.height
            val newHeight = (originalHeight * newWidth) / originalWidth
            transformImage(rawImage, targetImageType.name.lowercase(), newWidth, newHeight, quality)
        }
    }

    suspend fun transformImageByHeight(
        rawImage: BufferedImage,
        targetImageType: ImageType,
        newHeight: Int,
        quality: Double
    ): ByteArray {
        return withContext(Dispatchers.IO) {
            val originalWidth = rawImage.width
            val originalHeight = rawImage.height
            val newWidth = (originalWidth * newHeight) / originalHeight
            transformImage(rawImage, targetImageType.name.lowercase(), newWidth, newHeight, quality)
        }
    }
}