package io.sakurasou.hoshizora.executor

import io.github.oshai.kotlinlogging.KotlinLogging
import io.sakurasou.hoshizora.model.DatabaseSingleton.dbQuery
import io.sakurasou.hoshizora.model.dto.TaskInsertDTO
import io.sakurasou.hoshizora.model.dto.TaskTransitionStatusDTO
import io.sakurasou.hoshizora.model.entity.Strategy
import io.sakurasou.hoshizora.model.entity.Task
import io.sakurasou.hoshizora.model.task.DeleteImageTask
import io.sakurasou.hoshizora.model.task.DeleteThumbnailTask
import io.sakurasou.hoshizora.model.task.ImageTask
import io.sakurasou.hoshizora.model.task.PersistImageThumbnailTask
import io.sakurasou.hoshizora.model.task.RePersistImageThumbnailTask
import io.sakurasou.hoshizora.model.task.TaskStatus
import kotlinx.coroutines.CoroutineName
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.launch
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import java.awt.image.BufferedImage
import java.util.concurrent.ConcurrentHashMap
import kotlin.reflect.KClass
import kotlin.time.Clock

/**
 * @author Shiina Kin
 * 2024/12/18 18:31
 */
object ImageExecutor : Executor() {
    private val logger = KotlinLogging.logger {}
    private val taskMap = ConcurrentHashMap<Pair<Long, KClass<out ImageTask>>, Unit>()

    override val executeScope =
        CoroutineScope(
            Dispatchers.IO + SupervisorJob() + CoroutineName("ImageExecutor"),
        )

    override val taskChannel = Channel<Task>(Channel.UNLIMITED)

    init {
        executeScope.launch {
            for (task in taskChannel) {
                launch {
                    handle(task)
                }
            }
        }
    }

    private suspend fun handle(task: Task) {
        try {
            val opTarget = task.target as ImageTask
            opTarget.start {
                dbQuery {
                    val taskUpdateDTO =
                        TaskTransitionStatusDTO(
                            id = task.id,
                            oldStatus = TaskStatus.PROCESSING,
                            expectStatus = TaskStatus.DONE,
                            updateTime = Clock.System.now().toLocalDateTime(TimeZone.UTC),
                        )
                    val updateResult = taskDao.transitionTaskStatus(taskUpdateDTO)
                    if (!updateResult) {
                        logger.debug {
                            "task(id ${task.id}) status transition failed, maybe due to concurrent update"
                        }
                    }
                }
            }
        } catch (e: Exception) {
            dbQuery {
                val taskUpdateDTO =
                    TaskTransitionStatusDTO(
                        id = task.id,
                        oldStatus = TaskStatus.PROCESSING,
                        expectStatus = TaskStatus.FAILED,
                        message = e.stackTraceToString(),
                        updateTime = Clock.System.now().toLocalDateTime(TimeZone.UTC),
                    )
                taskDao.transitionTaskStatus(taskUpdateDTO)
            }
        }
    }

    suspend fun persistThumbnail(
        opImageID: Long,
        strategy: Strategy,
        subFolder: String,
        fileName: String,
        image: BufferedImage,
    ) {
        submitTask(opImageID, PersistImageThumbnailTask(opImageID, strategy, subFolder, fileName, image))
    }

    suspend fun rePersistThumbnail(
        opImageID: Long,
        strategy: Strategy,
        relativePath: String,
    ) {
        submitTask(opImageID, RePersistImageThumbnailTask(opImageID, strategy, relativePath))
    }

    suspend fun deleteImage(
        opImageID: Long,
        strategy: Strategy,
        relativePath: String,
    ) {
        submitTask(opImageID, DeleteImageTask(opImageID, strategy, relativePath))
    }

    suspend fun deleteThumbnail(
        opImageID: Long,
        strategy: Strategy,
        relativePath: String,
    ) {
        submitTask(opImageID, DeleteThumbnailTask(opImageID, strategy, relativePath))
    }

    private suspend fun submitTask(
        opImageID: Long,
        task: ImageTask,
    ) {
        dbQuery {
            val now = Clock.System.now().toLocalDateTime(TimeZone.UTC)
            val taskInsertDTO =
                TaskInsertDTO(
                    status = TaskStatus.PENDING,
                    target = task,
                    createTime = now,
                    updateTime = now,
                )

            taskDao.saveTask(taskInsertDTO)?.let {
                logger.debug { "submit task of imageId: $opImageID, taskID: $it" }
            } ?: logger.debug { "skip task of imageId: $opImageID due to task conflict policy" }
        }
    }
}
