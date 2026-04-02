package io.sakurasou.hoshizora.model.dao.task

import io.sakurasou.hoshizora.model.dto.TaskInsertDTO
import io.sakurasou.hoshizora.model.dto.TaskTransitionStatusDTO
import io.sakurasou.hoshizora.model.entity.Task
import io.sakurasou.hoshizora.model.task.TaskStatus
import kotlin.time.Duration

/**
 * @author ShiinaKin
 * 2024/9/7 14:07
 */
interface TaskDao {
    fun saveTask(taskInsertDTO: TaskInsertDTO): Long?

    fun failTimeoutTask(timeout: Duration)

    fun transitionTaskStatus(taskTransitionStatusDTO: TaskTransitionStatusDTO): Boolean

    fun takeTask(): Task?

    fun checkTaskStatusByID(
        taskID: Long,
        expectStatus: TaskStatus,
    ): Boolean

    fun listTaskByStatus(status: TaskStatus): List<Task>
}
