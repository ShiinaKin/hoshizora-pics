package io.sakurasou.execute.task.image

import io.github.oshai.kotlinlogging.KotlinLogging
import io.sakurasou.exception.service.image.io.ImageFileDeleteFailedException
import io.sakurasou.exception.service.image.io.ImageFileNotFoundException
import io.sakurasou.model.entity.Strategy
import io.sakurasou.model.group.ImageType
import io.sakurasou.model.strategy.LocalStrategy
import io.sakurasou.model.strategy.S3Strategy
import io.sakurasou.util.ImageUtils
import io.sakurasou.util.S3Utils
import java.awt.image.BufferedImage
import java.io.File
import javax.imageio.ImageIO

/**
 * @author Shiina Kin
 * 2024/12/18 18:38
 */

private const val THUMBNAIL_HEIGHT = 256
private const val THUMBNAIL_QUALITY = 0.9

sealed class ImageTask() {
    protected val logger = KotlinLogging.logger {}
    abstract fun execute()
}

class PersistImageThumbnailTask(
    private val strategy: Strategy,
    private val subFolder: String,
    private val fileName: String,
    private val image: BufferedImage
) : ImageTask() {
    override fun execute() {
        val relativePath = "$subFolder/$fileName"
        val imageType = ImageType.valueOf(fileName.substringAfterLast('.').uppercase())
        val thumbnailBytes = ImageUtils.transformImageByHeight(image, imageType, THUMBNAIL_HEIGHT, THUMBNAIL_QUALITY)
        ImageUtils.saveThumbnail(strategy, subFolder, fileName, thumbnailBytes, relativePath)
        logger.debug { "persist thumbnail $fileName in strategy(id ${strategy.id})" }
    }
}

class RePersistImageThumbnailTask(
    private val strategy: Strategy,
    private val relativePath: String
) : ImageTask() {
    override fun execute() {
        val fileName = relativePath.substringAfterLast('/')
        when (val strategyConfig = strategy.config) {
            is LocalStrategy -> {
                val uploadFolderStr = strategyConfig.uploadFolder
                val uploadFile = File(uploadFolderStr, relativePath)
                if (!uploadFile.exists()) throw ImageFileNotFoundException()
                uploadFile.inputStream()
            }

            is S3Strategy -> {
                val filePath = "${strategyConfig.uploadFolder}/$relativePath"
                S3Utils.fetchImage(filePath, strategy)
            }
        }.use { rawImageInputStream ->
            val rawImage = ImageIO.read(rawImageInputStream)
            val subFolder = relativePath.substringBeforeLast('/')
            val imageType = ImageType.valueOf(fileName.substringAfterLast('.').uppercase())
            val thumbnailBytes =
                ImageUtils.transformImageByHeight(rawImage, imageType, THUMBNAIL_HEIGHT, THUMBNAIL_QUALITY)
            ImageUtils.saveThumbnail(strategy, subFolder, fileName, thumbnailBytes, relativePath)
        }
        logger.debug { "re-persist thumbnail $fileName in strategy(id ${strategy.id})" }
    }
}

class DeleteImageTask(
    private val strategy: Strategy,
    private val relativePath: String
) : ImageTask() {
    override fun execute() {
        when (val strategyConfig = strategy.config) {
            is LocalStrategy -> {
                val uploadFolderStr = strategyConfig.uploadFolder
                val uploadFile = File(uploadFolderStr, relativePath)

                if (uploadFile.exists() && !uploadFile.delete()) {
                    logger.error { "Failed to delete image at $uploadFolderStr/$relativePath" }
                    throw ImageFileDeleteFailedException()
                }
            }

            is S3Strategy -> {
                val filePath = "${strategyConfig.uploadFolder}/$relativePath"
                S3Utils.deleteImage(filePath, strategy)
            }
        }
        val fileName = relativePath.substringAfterLast('/')
        logger.debug { "delete image $fileName from strategy(id ${strategy.id})" }
    }
}

class DeleteThumbnailTask(
    private val strategy: Strategy,
    private val relativePath: String
) : ImageTask() {
    override fun execute() {
        when (val strategyConfig = strategy.config) {
            is LocalStrategy -> {
                val thumbnailFolder = strategyConfig.thumbnailFolder
                val thumbnailFile = File(thumbnailFolder, relativePath)

                if (thumbnailFile.exists() && !thumbnailFile.delete()) {
                    logger.error { "Failed to delete thumbnail at $thumbnailFolder/$relativePath" }
                }
            }

            is S3Strategy -> {
                val filePath = "${strategyConfig.thumbnailFolder}/$relativePath"
                S3Utils.deleteImage(filePath, strategy)
            }
        }
        val fileName = relativePath.substringAfterLast('/')
        logger.debug { "delete thumbnail $fileName from strategy(id ${strategy.id})" }
    }
}
