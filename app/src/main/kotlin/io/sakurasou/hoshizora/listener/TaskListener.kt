package io.sakurasou.hoshizora.listener

import io.github.oshai.kotlinlogging.KotlinLogging
import io.sakurasou.hoshizora.di.inject
import io.sakurasou.hoshizora.executor.ImageExecutor
import io.sakurasou.hoshizora.model.DatabaseSingleton.dbQuery
import io.sakurasou.hoshizora.model.dao.task.TaskDao
import io.sakurasou.hoshizora.model.task.ImageTask
import kotlinx.coroutines.CoroutineName
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.getValue
import kotlin.time.Duration.Companion.minutes
import kotlin.time.Duration.Companion.seconds

/**
 * @author Shiina Kin
 * 2026/3/31 20:44
 */
object TaskListener {
    private val logger = KotlinLogging.logger {}
    private val taskDao by inject<TaskDao>()

    private val taskListenerScope =
        CoroutineScope(
            Dispatchers.IO + SupervisorJob() + CoroutineName("TaskListener"),
        )

    fun startListening() {}

    init {
        taskListenerScope.launch {
            logger.debug { "Task Listener Started" }
            while (true) {
                try {
                    while (true) {
                        val task = dbQuery { taskDao.takeTask() }

                        if (task == null) break

                        when (task.target) {
                            is ImageTask -> {
                                ImageExecutor.taskChannel.send(task)
                            }
                        }
                    }
                } catch (e: Exception) {
                    logger.error(e) { "failed to take task" }
                }
                delay(15.seconds)
            }
        }

        taskListenerScope.launch {
            logger.debug { "Task Cleaner Listener Started" }
            while (true) {
                try {
                    dbQuery {
                        val timeout = 5.minutes
                        taskDao.failTimeoutTask(timeout)
                    }
                } catch (e: Exception) {
                    logger.error(e) { "failed to clean task" }
                }
                delay(30.seconds)
            }
        }
    }

    fun stopListening() {
        taskListenerScope.cancel()
    }
}
