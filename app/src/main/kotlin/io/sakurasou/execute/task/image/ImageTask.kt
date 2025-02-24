package io.sakurasou.execute.task.image

import io.github.oshai.kotlinlogging.KotlinLogging
import io.sakurasou.exception.service.image.io.ImageFileDeleteFailedException
import io.sakurasou.exception.service.image.io.ImageFileNotFoundException
import io.sakurasou.model.entity.Strategy
import io.sakurasou.model.group.ImageType
import io.sakurasou.model.strategy.LocalStrategy
import io.sakurasou.model.strategy.S3Strategy
import io.sakurasou.model.strategy.WebDavStrategy
import io.sakurasou.util.ImageUtils
import io.sakurasou.util.S3Utils
import io.sakurasou.util.WebDavUtils
import java.awt.image.BufferedImage
import java.io.File
import javax.imageio.ImageIO
import kotlin.reflect.KClass

/**
 * @author Shiina Kin
 * 2024/12/18 18:38
 */

private const val THUMBNAIL_HEIGHT = 256
private const val THUMBNAIL_QUALITY = 0.9

sealed class ImageTask(
    val opImageId: Long,
    val taskType: KClass<out ImageTask>,
    private val cleanUp: (opImageId: Long, taskType: KClass<out ImageTask>) -> Unit
) {
    protected val logger = KotlinLogging.logger {}
    abstract fun execute()
    fun submit() {
        execute()
        cleanUp(opImageId, taskType)
    }
}

class PersistImageThumbnailTask(
    opImageId: Long,
    cleanUp: (opImageId: Long, taskType: KClass<out ImageTask>) -> Unit,
    private val strategy: Strategy,
    private val subFolder: String,
    private val fileName: String,
    private val image: BufferedImage
) : ImageTask(opImageId, taskType = PersistImageThumbnailTask::class, cleanUp) {
    override fun execute() {
        val relativePath = "$subFolder/$fileName"
        val imageType = ImageType.valueOf(fileName.substringAfterLast('.').uppercase())
        val thumbnailBytes = ImageUtils.transformImageByHeight(image, imageType, THUMBNAIL_HEIGHT, THUMBNAIL_QUALITY)
        ImageUtils.saveThumbnail(strategy, subFolder, fileName, thumbnailBytes, relativePath)
        logger.debug { "persist thumbnail $fileName in strategy(id ${strategy.id})" }
    }
}

class RePersistImageThumbnailTask(
    opImageId: Long,
    cleanUp: (opImageId: Long, taskType: KClass<out ImageTask>) -> Unit,
    private val strategy: Strategy,
    private val relativePath: String
) : ImageTask(opImageId, taskType = RePersistImageThumbnailTask::class, cleanUp) {
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

            is WebDavStrategy -> {
                val filePath = "${strategyConfig.uploadFolder}/$relativePath"
                val bytes = WebDavUtils.fetchImage(filePath, strategyConfig)
                bytes?.inputStream() ?: throw ImageFileNotFoundException()
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
    opImageId: Long,
    cleanUp: (opImageId: Long, taskType: KClass<out ImageTask>) -> Unit,
    private val strategy: Strategy,
    private val relativePath: String
) : ImageTask(opImageId, taskType = DeleteImageTask::class, cleanUp) {
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

            is WebDavStrategy -> {
                val filePath = "${strategyConfig.uploadFolder}/$relativePath"
                WebDavUtils.deleteImage(filePath, strategyConfig)
            }
        }
        val fileName = relativePath.substringAfterLast('/')
        logger.debug { "delete image $fileName from strategy(id ${strategy.id})" }
    }
}

class DeleteThumbnailTask(
    opImageId: Long,
    cleanUp: (opImageId: Long, taskType: KClass<out ImageTask>) -> Unit,
    private val strategy: Strategy,
    private val relativePath: String
) : ImageTask(opImageId, taskType = DeleteThumbnailTask::class, cleanUp) {
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

            is WebDavStrategy -> {
                var filePath = "${strategyConfig.thumbnailFolder}/$relativePath"
                filePath = WebDavUtils.addThumbnailIdentifierToFileName(filePath)
                WebDavUtils.deleteImage(filePath, strategyConfig)
            }
        }
        val fileName = relativePath.substringAfterLast('/')
        logger.debug { "delete thumbnail $fileName from strategy(id ${strategy.id})" }
    }
}
