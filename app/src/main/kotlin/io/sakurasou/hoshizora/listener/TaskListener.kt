package io.sakurasou.hoshizora.listener

import io.github.oshai.kotlinlogging.KotlinLogging
import io.sakurasou.hoshizora.di.InstanceCenter
import io.sakurasou.hoshizora.executor.ImageExecutor
import io.sakurasou.hoshizora.model.DatabaseSingleton.dbQuery
import io.sakurasou.hoshizora.model.task.ImageTask
import kotlinx.coroutines.CoroutineName
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.time.Duration.Companion.minutes
import kotlin.time.Duration.Companion.seconds

/**
 * @author Shiina Kin
 * 2026/3/31 20:44
 */
object TaskListener {
    private val logger = KotlinLogging.logger {}
    private val taskDao = InstanceCenter.taskDao

    private val taskListenerScope =
        CoroutineScope(
            Dispatchers.IO + SupervisorJob() + CoroutineName("TaskListener"),
        )

    fun startListening() {}

    init {
        taskListenerScope.launch {
            logger.debug { "Task Listener Started" }
            while (true) {
                while (true) {
                    val task =
                        try {
                            dbQuery { taskDao.takeTask() }
                        } catch (e: Exception) {
                            logger.error(e) { "failed to take task" }
                            break
                        }

                    if (task == null) break

                    when (task.target) {
                        is ImageTask -> {
                            ImageExecutor.taskChannel.send(task)
                        }
                    }
                }
                delay(15.seconds)
            }
        }

        taskListenerScope.launch {
            logger.debug { "Task Cleanner Listener Started" }
            while (true) {
                dbQuery {
                    val timeout = 5.minutes
                    taskDao.failTimeoutTask(timeout)
                }
                delay(30.seconds)
            }
        }
    }
}
