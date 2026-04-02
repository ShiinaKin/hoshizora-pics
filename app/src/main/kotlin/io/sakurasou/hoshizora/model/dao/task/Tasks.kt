package io.sakurasou.hoshizora.model.dao.task

import io.sakurasou.hoshizora.model.task.Task
import io.sakurasou.hoshizora.model.task.TaskStatus
import io.sakurasou.hoshizora.model.task.TaskType
import io.sakurasou.hoshizora.plugins.jsonFormat
import org.jetbrains.exposed.v1.core.dao.id.LongIdTable
import org.jetbrains.exposed.v1.core.eq
import org.jetbrains.exposed.v1.core.or
import org.jetbrains.exposed.v1.datetime.datetime
import org.jetbrains.exposed.v1.json.json

/**
 * @author ShiinaKin
 * 2024/9/7 13:47
 */
object Tasks : LongIdTable("tasks") {
    val type = enumeration("type", TaskType::class)
    val status = enumeration("status", TaskStatus::class)
    val targetID = varchar("target_id", 255)
    val operation = varchar("operation", 255)
    val target = json<Task>("target", jsonFormat)
    val message = text("message").nullable()
    val createTime = datetime("create_time")
    val updateTime = datetime("update_time")

    init {
        uniqueIndex(type, status, targetID, operation) {
            (status eq TaskStatus.PENDING) or (status eq TaskStatus.PROCESSING)
        }
    }

    val columnMap =
        mapOf(
            "createTime" to createTime,
            "updateTime" to updateTime,
        )
}
