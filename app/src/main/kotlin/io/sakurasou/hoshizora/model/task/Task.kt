package io.sakurasou.hoshizora.model.task

import kotlinx.serialization.Serializable

/**
 * @author Shiina Kin
 * 2026/3/31 19:58
 */

@Serializable
sealed class Task(
    val taskType: TaskType,
    val opTargetID: String,
    val operation: String,
)

@Serializable
enum class TaskType {
    IMAGE,
}

@Serializable
enum class TaskStatus {
    PENDING,
    PROCESSING,
    DONE,
    FAILED,
    CANCELLED,
}
