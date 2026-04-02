package io.sakurasou.hoshizora.model.entity

import io.sakurasou.hoshizora.model.task.Task
import io.sakurasou.hoshizora.model.task.TaskStatus
import io.sakurasou.hoshizora.model.task.TaskType
import kotlinx.datetime.LocalDateTime
import kotlinx.serialization.Serializable

/**
 * @author Shiina Kin
 * 2026/3/31 19:49
 */
@Serializable
data class Task(
    val id: Long,
    val type: TaskType,
    val status: TaskStatus,
    val targetID: String,
    val operation: String,
    val target: Task,
    val message: String?,
    val createTime: LocalDateTime,
    val updateTime: LocalDateTime,
) {
    constructor(
        id: Long,
        status: TaskStatus,
        target: Task,
        message: String? = null,
        createTime: LocalDateTime,
        updateTime: LocalDateTime,
    ) : this(id, target.taskType, status, target.opTargetID, target.operation, target, message, createTime, updateTime)
}
