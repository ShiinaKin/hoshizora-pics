package io.sakurasou.hoshizora.execute.task.image

import io.github.oshai.kotlinlogging.KotlinLogging
import io.sakurasou.hoshizora.model.entity.Strategy
import io.sakurasou.hoshizora.model.group.ImageType
import io.sakurasou.hoshizora.util.ImageUtils
import java.awt.image.BufferedImage
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
    private val cleanUp: (opImageId: Long, taskType: KClass<out ImageTask>) -> Unit,
) {
    protected val logger = KotlinLogging.logger {}

    abstract suspend fun execute()

    suspend fun submit() {
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
    private val image: BufferedImage,
) : ImageTask(opImageId, taskType = PersistImageThumbnailTask::class, cleanUp) {
    override suspend fun execute() {
        val relativePath = "$subFolder/$fileName"
        val imageType = ImageType.valueOf(fileName.substringAfterLast('.').uppercase())
        val thumbnailBytes = ImageUtils.transformImageByHeight(image, imageType, THUMBNAIL_HEIGHT, THUMBNAIL_QUALITY)
        ImageUtils.saveThumbnail(strategy, thumbnailBytes, relativePath)
        logger.debug { "persist thumbnail $fileName in strategy(id ${strategy.id})" }
    }
}

class RePersistImageThumbnailTask(
    opImageId: Long,
    cleanUp: (opImageId: Long, taskType: KClass<out ImageTask>) -> Unit,
    private val strategy: Strategy,
    private val relativePath: String,
) : ImageTask(opImageId, taskType = RePersistImageThumbnailTask::class, cleanUp) {
    override suspend fun execute() {
        val fileName = relativePath.substringAfterLast('/')
        strategy.config
            .fetch(relativePath)
            .inputStream()
            .use { rawImageInputStream ->
                val rawImage = ImageIO.read(rawImageInputStream)
                val subFolder = relativePath.substringBeforeLast('/')
                val imageType = ImageType.valueOf(fileName.substringAfterLast('.').uppercase())
                val thumbnailBytes =
                    ImageUtils.transformImageByHeight(rawImage, imageType, THUMBNAIL_HEIGHT, THUMBNAIL_QUALITY)
                ImageUtils.saveThumbnail(strategy, thumbnailBytes, relativePath)
            }
        logger.debug { "re-persist thumbnail $fileName in strategy(id ${strategy.id})" }
    }
}

class DeleteImageTask(
    opImageId: Long,
    cleanUp: (opImageId: Long, taskType: KClass<out ImageTask>) -> Unit,
    private val strategy: Strategy,
    private val relativePath: String,
) : ImageTask(opImageId, taskType = DeleteImageTask::class, cleanUp) {
    override suspend fun execute() {
        val filePath = "${strategy.config.uploadFolder}/$relativePath"
        strategy.config.delete(filePath)
        val fileName = relativePath.substringAfterLast('/')
        logger.debug { "delete image $fileName from strategy(id ${strategy.id})" }
    }
}

class DeleteThumbnailTask(
    opImageId: Long,
    cleanUp: (opImageId: Long, taskType: KClass<out ImageTask>) -> Unit,
    private val strategy: Strategy,
    private val relativePath: String,
) : ImageTask(opImageId, taskType = DeleteThumbnailTask::class, cleanUp) {
    override suspend fun execute() {
        val filePath = "${strategy.config.uploadFolder}/$relativePath"
        strategy.config.delete(filePath)
        val fileName = relativePath.substringAfterLast('/')
        logger.debug { "delete thumbnail $fileName from strategy(id ${strategy.id})" }
    }
}
