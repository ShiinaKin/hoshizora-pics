package io.sakurasou.execute.executor.image

import io.github.oshai.kotlinlogging.KotlinLogging
import io.sakurasou.execute.task.image.DeleteImageTask
import io.sakurasou.execute.task.image.DeleteThumbnailTask
import io.sakurasou.execute.task.image.ImageTask
import io.sakurasou.execute.task.image.PersistImageThumbnailTask
import io.sakurasou.execute.task.image.RePersistImageThumbnailTask
import io.sakurasou.model.entity.Strategy
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.Channel
import java.awt.image.BufferedImage

/**
 * @author Shiina Kin
 * 2024/12/18 18:31
 */
object ImageExecutor {
    private val logger = KotlinLogging.logger {}

    private val imageExecuteScope  = CoroutineScope(
        Dispatchers.IO + SupervisorJob() + CoroutineName("ImageExecutor")
    )

    private val taskChannel = Channel<ImageTask>(Channel.UNLIMITED)

    init {
        imageExecuteScope.launch {
            logger.debug { "image executor started" }
            for (task in taskChannel) {
                launch {
                    try {
                        task.execute()
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
            }
        }
    }

    suspend fun persistThumbnail(strategy: Strategy, subFolder: String, fileName: String, image: BufferedImage) {
        taskChannel.send(PersistImageThumbnailTask(strategy, subFolder, fileName, image))
    }

    suspend fun rePersistThumbnail(strategy: Strategy, relativePath: String) {
        taskChannel.send(RePersistImageThumbnailTask(strategy, relativePath))
    }

    suspend fun deleteImage(strategy: Strategy, relativePath: String) {
        taskChannel.send(DeleteImageTask(strategy, relativePath))
    }

    suspend fun deleteThumbnail(strategy: Strategy, relativePath: String) {
        taskChannel.send(DeleteThumbnailTask(strategy, relativePath))
    }

    fun shutdown() {
        taskChannel.close()
        imageExecuteScope.cancel()
    }
}
