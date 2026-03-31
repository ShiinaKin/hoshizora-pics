package io.sakurasou.hoshizora.model.task

import io.sakurasou.hoshizora.model.entity.TaskType
import kotlinx.serialization.Serializable

/**
 * @author Shiina Kin
 * 2026/3/31 19:58
 */

@Serializable
sealed class Task(
    val type: TaskType,
    val opTargetID: String,
    val operation: String,
)
