package io.sakurasou.hoshizora.model.task

import io.github.oshai.kotlinlogging.KLogger
import io.github.oshai.kotlinlogging.KotlinLogging
import io.sakurasou.hoshizora.model.entity.Strategy
import io.sakurasou.hoshizora.model.group.ImageType
import io.sakurasou.hoshizora.model.task.ImageTask.Operation.DELETE_IMAGE
import io.sakurasou.hoshizora.model.task.ImageTask.Operation.DELETE_THUMBNAIL
import io.sakurasou.hoshizora.model.task.ImageTask.Operation.PERSIST_THUMBNAIL
import io.sakurasou.hoshizora.model.task.ImageTask.Operation.REPERSIST_THUMBNAIL
import io.sakurasou.hoshizora.util.ImageUtils
import javax.imageio.ImageIO

/**
 * @author Shiina Kin
 * 2024/12/18 18:38
 */

private const val THUMBNAIL_HEIGHT = 256
private const val THUMBNAIL_QUALITY = 0.9

sealed class ImageTask(
    opImageID: Long,
    operation: Operation,
) : Task(TaskType.IMAGE, opImageID.toString(), operation.toString()) {
    protected val logger: KLogger
        get() = KotlinLogging.logger {}

    enum class Operation {
        PERSIST_THUMBNAIL,
        REPERSIST_THUMBNAIL,
        DELETE_IMAGE,
        DELETE_THUMBNAIL,
    }

    abstract suspend fun execute()

    suspend fun start(done: suspend () -> Unit) {
        execute()
        done()
    }
}

class PersistImageThumbnailTask(
    opImageID: Long,
    private val strategy: Strategy,
    private val relativePath: String,
) : ImageTask(opImageID, PERSIST_THUMBNAIL) {
    override suspend fun execute() {
        val fileName = relativePath.substringAfterLast('/')
        val rawImagePath = "${strategy.config.uploadFolder}/$relativePath"
        strategy.config
            .fetch(rawImagePath)
            .inputStream()
            .use { rawImageInputStream ->
                val rawImage = ImageIO.read(rawImageInputStream)
                val imageType = ImageType.valueOf(fileName.substringAfterLast('.').uppercase())
                val thumbnailBytes =
                    ImageUtils.transformImageByHeight(rawImage, imageType, THUMBNAIL_HEIGHT, THUMBNAIL_QUALITY)
                ImageUtils.saveThumbnail(strategy, thumbnailBytes, relativePath)
            }
        logger.debug { "persist thumbnail $fileName in strategy(id ${strategy.id})" }
    }
}

class DeleteImageTask(
    opImageID: Long,
    private val strategy: Strategy,
    private val relativePath: String,
) : ImageTask(opImageID, DELETE_IMAGE) {
    override suspend fun execute() {
        val filePath = "${strategy.config.uploadFolder}/$relativePath"
        strategy.config.delete(filePath)
        val fileName = relativePath.substringAfterLast('/')
        logger.debug { "delete image $fileName from strategy(id ${strategy.id})" }
    }
}

class DeleteThumbnailTask(
    opImageID: Long,
    private val strategy: Strategy,
    private val relativePath: String,
) : ImageTask(opImageID, DELETE_THUMBNAIL) {
    override suspend fun execute() {
        val filePath = "${strategy.config.uploadFolder}/$relativePath"
        strategy.config.delete(filePath)
        val fileName = relativePath.substringAfterLast('/')
        logger.debug { "delete thumbnail $fileName from strategy(id ${strategy.id})" }
    }
}
