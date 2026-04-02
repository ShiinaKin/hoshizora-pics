package io.sakurasou.hoshizora.model.dao.task

import io.sakurasou.hoshizora.model.dto.TaskInsertDTO
import io.sakurasou.hoshizora.model.dto.TaskTransitionStatusDTO
import io.sakurasou.hoshizora.model.entity.Task
import io.sakurasou.hoshizora.model.task.TaskConflictPolicy
import io.sakurasou.hoshizora.model.task.TaskStatus
import io.sakurasou.hoshizora.model.task.TaskType
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import org.jetbrains.exposed.v1.core.SortOrder
import org.jetbrains.exposed.v1.core.alias
import org.jetbrains.exposed.v1.core.and
import org.jetbrains.exposed.v1.core.eq
import org.jetbrains.exposed.v1.core.inList
import org.jetbrains.exposed.v1.core.intLiteral
import org.jetbrains.exposed.v1.core.less
import org.jetbrains.exposed.v1.core.notExists
import org.jetbrains.exposed.v1.core.vendors.ForUpdateOption
import org.jetbrains.exposed.v1.jdbc.Query
import org.jetbrains.exposed.v1.jdbc.andWhere
import org.jetbrains.exposed.v1.jdbc.insertAndGetId
import org.jetbrains.exposed.v1.jdbc.select
import org.jetbrains.exposed.v1.jdbc.selectAll
import org.jetbrains.exposed.v1.jdbc.update
import java.sql.SQLException
import java.sql.SQLIntegrityConstraintViolationException
import kotlin.time.Clock
import kotlin.time.Duration

/**
 * @author ShiinaKin
 * 2024/9/7 14:07
 */
class TaskDaoImpl : TaskDao {
    override fun saveTask(taskInsertDTO: TaskInsertDTO): Long? {
        val taskType = taskInsertDTO.type
        val targetID = taskInsertDTO.targetID
        val incomingOperation = taskInsertDTO.operation
        val activeTasks =
            listTaskRefs(
                type = taskType,
                targetID = targetID,
                statuses = setOf(TaskStatus.PENDING, TaskStatus.PROCESSING),
            )

        if (activeTasks.any {
                TaskConflictPolicy.shouldRejectIncomingTask(
                    taskType = taskType,
                    existingOperation = it.operation,
                    incomingOperation = incomingOperation,
                )
            }
        ) {
            return null
        }

        activeTasks
            .filter {
                it.status == TaskStatus.PENDING &&
                    TaskConflictPolicy.shouldCancelExistingTask(
                        taskType = taskType,
                        existingOperation = it.operation,
                        incomingOperation = incomingOperation,
                    )
            }.forEach {
                transitionTaskStatus(
                    TaskTransitionStatusDTO(
                        id = it.id,
                        oldStatus = TaskStatus.PENDING,
                        expectStatus = TaskStatus.CANCELLED,
                        message = "superseded by $incomingOperation",
                        updateTime = Clock.System.now().toLocalDateTime(TimeZone.UTC),
                    ),
                )
            }

        return saveTaskInner(taskInsertDTO)
    }

    private fun saveTaskInner(taskInsertDTO: TaskInsertDTO): Long? {
        try {
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
        } catch (e: Exception) {
            if (e.isUniqueConstraintViolation()) return null
            throw e
        }
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
            .select(Tasks.id, Tasks.status, Tasks.target, Tasks.message, Tasks.createTime, Tasks.updateTime)
            .where { Tasks.status eq status }
            .forUpdate(ForUpdateOption.ForUpdate)
            .toEntityList()

    private fun listTaskRefs(
        type: TaskType,
        targetID: String,
        statuses: Set<TaskStatus>,
    ): List<TaskRef> =
        Tasks
            .select(Tasks.id, Tasks.status, Tasks.operation)
            .where { Tasks.status inList statuses.toList() }
            .andWhere { Tasks.type eq type }
            .andWhere { Tasks.targetID eq targetID }
            .orderBy(Tasks.createTime to SortOrder.ASC)
            .forUpdate(ForUpdateOption.ForUpdate)
            .map {
                TaskRef(
                    id = it[Tasks.id].value,
                    status = it[Tasks.status],
                    operation = it[Tasks.operation],
                )
            }

    private fun getTask(): Task? =
        Tasks
            .selectAll()
            .where {
                val processingTasks = Tasks.alias("processing_tasks")
                (Tasks.status eq TaskStatus.PENDING) and
                    notExists(
                        processingTasks
                            .select(intLiteral(1))
                            .where { processingTasks[Tasks.type] eq Tasks.type }
                            .andWhere { processingTasks[Tasks.targetID] eq Tasks.targetID }
                            .andWhere { processingTasks[Tasks.status] eq TaskStatus.PROCESSING },
                    )
            }.orderBy(Tasks.createTime to SortOrder.ASC)
            .limit(1)
            .forUpdate(ForUpdateOption.ForUpdate)
            .toEntityList()
            .firstOrNull()

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

    private fun Query.toEntityList(): List<Task> =
        map {
            Task(
                id = it[Tasks.id].value,
                status = it[Tasks.status],
                target = it[Tasks.target],
                message = it[Tasks.message],
                createTime = it[Tasks.createTime],
                updateTime = it[Tasks.updateTime],
            )
        }

    private fun Throwable.isUniqueConstraintViolation(): Boolean {
        var current: Throwable? = this
        while (current != null) {
            when (current) {
                is SQLIntegrityConstraintViolationException -> {
                    return true
                }

                is SQLException -> {
                    if (current.sqlState == POSTGRES_UNIQUE_VIOLATION_SQL_STATE ||
                        current.sqlState == GENERIC_INTEGRITY_VIOLATION_SQL_STATE ||
                        current.errorCode == SQLITE_CONSTRAINT_ERROR_CODE
                    ) {
                        return true
                    }
                }
            }
            val message = current.message?.lowercase()
            if (message != null &&
                (
                    "duplicate key" in message ||
                        "unique constraint" in message ||
                        "unique index" in message
                )
            ) {
                return true
            }
            current = current.cause
        }
        return false
    }

    private data class TaskRef(
        val id: Long,
        val status: TaskStatus,
        val operation: String,
    )

    companion object {
        private const val POSTGRES_UNIQUE_VIOLATION_SQL_STATE = "23505"
        private const val GENERIC_INTEGRITY_VIOLATION_SQL_STATE = "23000"
        private const val SQLITE_CONSTRAINT_ERROR_CODE = 19
    }
}
