package io.sakurasou.hoshizora.executor

import io.sakurasou.hoshizora.di.inject
import io.sakurasou.hoshizora.model.dao.task.TaskDao
import io.sakurasou.hoshizora.model.entity.Task
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.cancel
import kotlinx.coroutines.channels.Channel

/**
 * @author Shiina Kin
 * 2026/3/31 20:48
 */
abstract class Executor {
    protected val taskDao by inject<TaskDao>()

    companion object {
        const val MAX_WORKER_SIZE = 32
        const val MAX_WAITING_QUEUE_SIZE = 128
    }

    internal abstract val executeScope: CoroutineScope
    internal abstract val taskChannel: Channel<Task>

    fun shutdown() {
        taskChannel.close()
        executeScope.cancel()
    }
}
