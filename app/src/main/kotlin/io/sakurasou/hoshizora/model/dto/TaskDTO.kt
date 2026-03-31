package io.sakurasou.hoshizora.model.dto

import io.sakurasou.hoshizora.model.entity.TaskStatus
import io.sakurasou.hoshizora.model.entity.TaskType
import io.sakurasou.hoshizora.model.task.Task
import kotlinx.datetime.LocalDateTime
import kotlinx.serialization.Serializable

/**
 * @author Shiina Kin
 * 2026/3/31 19:49
 */
@Serializable
data class TaskInsertDTO(
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
        status: TaskStatus,
        target: Task,
        message: String? = null,
        createTime: LocalDateTime,
        updateTime: LocalDateTime,
    ) : this(target.type, status, target.opTargetID, target.operation, target, message, createTime, updateTime)
}

@Serializable
data class TaskUpdateDTO(
    val id: Long,
    val status: TaskStatus,
    val message: String? = null,
    val updateTime: LocalDateTime,
)

@Serializable
data class TaskTransitionStatusDTO(
    val id: Long,
    val oldStatus: TaskStatus,
    val expectStatus: TaskStatus,
    val message: String? = null,
    val updateTime: LocalDateTime,
)
