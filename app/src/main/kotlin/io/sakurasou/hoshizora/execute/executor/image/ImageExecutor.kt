package io.sakurasou.hoshizora.execute.executor.image

import io.github.oshai.kotlinlogging.KotlinLogging
import io.sakurasou.hoshizora.execute.task.image.*
import io.sakurasou.hoshizora.model.entity.Strategy
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.Channel
import java.awt.image.BufferedImage
import java.util.concurrent.ConcurrentHashMap
import kotlin.reflect.KClass

/**
 * @author Shiina Kin
 * 2024/12/18 18:31
 */
object ImageExecutor {
    private val logger = KotlinLogging.logger {}
    private val taskMap = ConcurrentHashMap<Pair<Long, KClass<out ImageTask>>, Unit>()

    private val imageExecuteScope =
        CoroutineScope(
            Dispatchers.IO + SupervisorJob() + CoroutineName("ImageExecutor"),
        )

    private val taskChannel = Channel<ImageTask>(Channel.UNLIMITED)

    init {
        imageExecuteScope.launch {
            logger.debug { "image executor started" }
            for (task in taskChannel) {
                launch {
                    try {
                        task.submit()
                    } catch (e: Exception) {
                        logger.error(e) { "image task failed" }
                    }
                }
            }
        }
    }

    suspend fun persistThumbnail(
        opImageId: Long,
        strategy: Strategy,
        subFolder: String,
        fileName: String,
        image: BufferedImage,
    ) {
        submitTask(PersistImageThumbnailTask(opImageId, cleanUp, strategy, subFolder, fileName, image))
    }

    suspend fun rePersistThumbnail(
        opImageId: Long,
        strategy: Strategy,
        relativePath: String,
    ) {
        submitTask(RePersistImageThumbnailTask(opImageId, cleanUp, strategy, relativePath))
    }

    suspend fun deleteImage(
        opImageId: Long,
        strategy: Strategy,
        relativePath: String,
    ) {
        submitTask(DeleteImageTask(opImageId, cleanUp, strategy, relativePath))
    }

    suspend fun deleteThumbnail(
        opImageId: Long,
        strategy: Strategy,
        relativePath: String,
    ) {
        submitTask(DeleteThumbnailTask(opImageId, cleanUp, strategy, relativePath))
    }

    private val cleanUp = { opImageId: Long, taskType: KClass<out ImageTask> ->
        taskMap.remove(opImageId to taskType)!!
    }

    private suspend fun submitTask(task: ImageTask) {
        if (taskMap.putIfAbsent(task.opImageId to task.taskType, Unit) != null) {
            logger.debug { "task of imageId: ${task.opImageId} already exists" }
            return
        }
        taskChannel.send(task)
    }

    fun shutdown() {
        taskChannel.close()
        imageExecuteScope.cancel()
    }
}
