package io.sakurasou.hoshizora.model.dao.task

import io.sakurasou.hoshizora.model.dto.TaskInsertDTO
import io.sakurasou.hoshizora.model.dto.TaskTransitionStatusDTO
import io.sakurasou.hoshizora.model.entity.Task
import io.sakurasou.hoshizora.model.entity.TaskStatus
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import org.jetbrains.exposed.v1.core.SortOrder
import org.jetbrains.exposed.v1.core.and
import org.jetbrains.exposed.v1.core.eq
import org.jetbrains.exposed.v1.core.less
import org.jetbrains.exposed.v1.core.vendors.ForUpdateOption
import org.jetbrains.exposed.v1.jdbc.andWhere
import org.jetbrains.exposed.v1.jdbc.insertAndGetId
import org.jetbrains.exposed.v1.jdbc.select
import org.jetbrains.exposed.v1.jdbc.selectAll
import org.jetbrains.exposed.v1.jdbc.update
import kotlin.time.Clock
import kotlin.time.Duration

/**
 * @author ShiinaKin
 * 2024/9/7 14:07
 */
class TaskDaoImpl : TaskDao {
    override fun saveTask(taskInsertDTO: TaskInsertDTO): Long {
        val entityID =
            Tasks.insertAndGetId {
                it[type] = taskInsertDTO.type
                it[status] = taskInsertDTO.status
                it[targetID] = taskInsertDTO.targetID
                it[operation] = taskInsertDTO.operation
                it[target] = taskInsertDTO.target
                it[message] = taskInsertDTO.message
                it[createTime] = taskInsertDTO.createTime
                it[updateTime] = taskInsertDTO.updateTime
            }
        return entityID.value
    }

    override fun transitionTaskStatus(taskTransitionStatusDTO: TaskTransitionStatusDTO): Boolean =
        transitionTaskStatusInner(taskTransitionStatusDTO) != 0

    private fun transitionTaskStatusInner(taskTransitionStatusDTO: TaskTransitionStatusDTO): Int {
        val oldStatus = taskTransitionStatusDTO.oldStatus
        val expectStatus = taskTransitionStatusDTO.expectStatus
        if (oldStatus == expectStatus ||
            oldStatus == TaskStatus.DONE ||
            oldStatus == TaskStatus.FAILED ||
            oldStatus == TaskStatus.CANCELLED ||
            (oldStatus == TaskStatus.PENDING && expectStatus == TaskStatus.DONE) ||
            (oldStatus == TaskStatus.PENDING && expectStatus == TaskStatus.FAILED) ||
            (oldStatus == TaskStatus.PROCESSING && expectStatus == TaskStatus.PENDING)
        ) {
            return 0
        }

        return Tasks.update({
            (Tasks.id eq taskTransitionStatusDTO.id) and
                (Tasks.status eq taskTransitionStatusDTO.oldStatus)
        }) {
            it[status] = taskTransitionStatusDTO.expectStatus
            it[message] = taskTransitionStatusDTO.message
            it[updateTime] = taskTransitionStatusDTO.updateTime
        }
    }

    override fun failTimeoutTask(timeout: Duration) {
        listOutOfTimeTask(timeout).forEach { taskID ->
            val taskUpdateDTO =
                TaskTransitionStatusDTO(
                    id = taskID,
                    oldStatus = TaskStatus.PROCESSING,
                    expectStatus = TaskStatus.FAILED,
                    message = "task timeout: $timeout",
                    updateTime = Clock.System.now().toLocalDateTime(TimeZone.UTC),
                )
            transitionTaskStatus(taskUpdateDTO)
        }
    }

    override fun takeTask(): Task? =
        getTask()?.also { task ->
            val taskUpdateDTO =
                TaskTransitionStatusDTO(
                    id = task.id,
                    oldStatus = TaskStatus.PENDING,
                    expectStatus = TaskStatus.PROCESSING,
                    updateTime = Clock.System.now().toLocalDateTime(TimeZone.UTC),
                )
            val updateResult = transitionTaskStatus(taskUpdateDTO)
            if (!updateResult) {
                return null
            }
        }

    override fun listTaskByStatus(status: TaskStatus): List<Task> =
        Tasks
            .select(Tasks.id, Tasks.status, Tasks.target, Tasks.createTime, Tasks.updateTime)
            .where { Tasks.status eq status }
            .forUpdate(ForUpdateOption.ForUpdate)
            .map {
                Task(
                    id = it[Tasks.id].value,
                    status = it[Tasks.status],
                    target = it[Tasks.target],
                    message = it[Tasks.message],
                    createTime = it[Tasks.createTime],
                    updateTime = it[Tasks.updateTime],
                )
            }

    private fun getTask(): Task? =
        Tasks
            .selectAll()
            .where { Tasks.status eq TaskStatus.PENDING }
            .orderBy(Tasks.createTime to SortOrder.ASC)
            .limit(1)
            .forUpdate(ForUpdateOption.ForUpdate)
            .map {
                Task(
                    id = it[Tasks.id].value,
                    status = it[Tasks.status],
                    target = it[Tasks.target],
                    message = it[Tasks.message],
                    createTime = it[Tasks.createTime],
                    updateTime = it[Tasks.updateTime],
                )
            }.firstOrNull()

    private fun listOutOfTimeTask(timeout: Duration): List<Long> {
        val cond =
            Clock.System
                .now()
                .let { it - timeout }
                .toLocalDateTime(TimeZone.UTC)
        return Tasks
            .select(Tasks.id)
            .where { Tasks.status eq TaskStatus.PROCESSING }
            .andWhere { Tasks.updateTime less cond }
            .orderBy(Tasks.updateTime to SortOrder.ASC)
            .forUpdate(ForUpdateOption.ForUpdate)
            .map {
                it[Tasks.id].value
            }
    }
}
