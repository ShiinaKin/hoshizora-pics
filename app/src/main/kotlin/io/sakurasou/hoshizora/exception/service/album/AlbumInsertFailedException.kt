package io.sakurasou.hoshizora.exception.service.album

import io.sakurasou.hoshizora.exception.ServiceException

/**
 * @author Shiina Kin
 * 2024/9/25 12:44
 */
class AlbumInsertFailedException(
    cause: ServiceException? = null,
    reason: String? = null,
) : ServiceException() {
    override val code: Int
        get() = 4000
    override var message: String = "Album Insert Failed"

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
