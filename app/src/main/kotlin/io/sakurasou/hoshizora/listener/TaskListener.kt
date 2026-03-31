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
                do {
                    val task = dbQuery { taskDao.takeTask() }
                    if (task != null) {
                        when (task.target) {
                            is ImageTask -> {
                                ImageExecutor.taskChannel.send(task)
                            }
                        }
                        logger.debug { "send task(id ${task.id}) to executor" }
                    }
                } while (task != null)
                delay(15.seconds)
            }
        }
    }
}
