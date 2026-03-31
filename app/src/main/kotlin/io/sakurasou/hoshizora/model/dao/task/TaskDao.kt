package io.sakurasou.hoshizora.model.dao.task

import io.sakurasou.hoshizora.model.dto.TaskInsertDTO
import io.sakurasou.hoshizora.model.dto.TaskTransitionStatusDTO
import io.sakurasou.hoshizora.model.dto.TaskUpdateDTO
import io.sakurasou.hoshizora.model.entity.Task
import io.sakurasou.hoshizora.model.entity.TaskStatus

/**
 * @author ShiinaKin
 * 2024/9/7 14:07
 */
interface TaskDao {
    fun saveTask(taskInsertDTO: TaskInsertDTO): Long

    fun transitionTaskStatus(taskTransitionStatusDTO: TaskTransitionStatusDTO): Boolean

    fun takeTask(): Task?

    fun listTaskByStatus(status: TaskStatus): List<Task>
}
