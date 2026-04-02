package io.sakurasou.hoshizora.exception.service.group

import io.sakurasou.hoshizora.exception.ServiceException

/**
 * @author Shiina Kin
 * 2024/9/25 12:44
 */
class GroupUpdateFailedException(
    cause: ServiceException? = null,
    reason: String? = null,
) : ServiceException() {
    override val code: Int
        get() = 4000
    override var message: String = "Group Update Failed"

    init {
        message +=
            if (cause != null) {
                ", ${cause.message}"
            } else if (reason != null) {
                ", $reason"
            } else {
                ""
            }
    }
}
