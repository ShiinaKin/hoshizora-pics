package io.sakurasou.hoshizora.model.task

import io.sakurasou.hoshizora.model.task.ImageTask.Operation.DELETE_IMAGE
import io.sakurasou.hoshizora.model.task.ImageTask.Operation.DELETE_THUMBNAIL
import io.sakurasou.hoshizora.model.task.ImageTask.Operation.PERSIST_THUMBNAIL
import io.sakurasou.hoshizora.model.task.ImageTask.Operation.REPERSIST_THUMBNAIL

/**
 * @author Shiina Kin
 * 2026/4/1 20:23
 */

private val imageThumbnailWriteOperations =
    setOf(
        PERSIST_THUMBNAIL.name,
        REPERSIST_THUMBNAIL.name,
    )

private val imageImageDeleteOperations =
    setOf(
        DELETE_IMAGE.name,
    )

private val imageThumbnailDeleteOperations =
    setOf(
        DELETE_THUMBNAIL.name,
    )

object TaskConflictPolicy {
    fun shouldRejectIncomingTask(
        taskType: TaskType,
        existingOperation: String,
        incomingOperation: String,
    ): Boolean =
        when (taskType) {
            TaskType.IMAGE -> {
                existingOperation == incomingOperation ||
                    (
                        existingOperation in imageThumbnailWriteOperations &&
                            incomingOperation in imageThumbnailWriteOperations
                    ) ||
                    (
                        existingOperation in imageImageDeleteOperations &&
                            incomingOperation in imageThumbnailWriteOperations
                    )
            }
        }

    fun shouldCancelExistingTask(
        taskType: TaskType,
        existingOperation: String,
        incomingOperation: String,
    ): Boolean =
        when (taskType) {
            TaskType.IMAGE -> {
                incomingOperation in imageImageDeleteOperations &&
                    existingOperation in imageThumbnailWriteOperations
            }
        }
}
