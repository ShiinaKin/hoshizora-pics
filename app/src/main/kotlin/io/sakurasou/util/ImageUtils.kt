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

object ImageUtils {
    suspend fun uploadImageAndGetRelativePath(
        strategy: Strategy,
        subFolder: String,
        fileName: String,
        bytes: ByteArray
    ): String {
        return withContext(Dispatchers.IO) {
            val strategyConfig = strategy.config
            val relativePath = "$subFolder/$fileName"
            when (strategyConfig) {
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
                    S3Utils.uploadImage(relativePath, bytes, strategy)
                }
            }
            relativePath
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
        quality: Double
    ): ByteArray {
        return withContext(Dispatchers.IO) {
            val originalWidth = rawImage.width
            val originalHeight = rawImage.height
            val newHeight = (originalHeight * newWidth) / originalWidth
            ByteArrayOutputStream().apply {
                Thumbnails.of(rawImage)
                    .size(newWidth, newHeight)
                    .outputFormat(targetImageType.name)
                    .outputQuality(quality)
                    .toOutputStream(this)
            }.use { it.toByteArray() }
        }
    }
}