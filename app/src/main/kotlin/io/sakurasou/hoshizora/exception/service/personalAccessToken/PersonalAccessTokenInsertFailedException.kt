package io.sakurasou.hoshizora.exception.service.personalAccessToken

import io.sakurasou.hoshizora.exception.ServiceException

/**
 * @author ShiinaKin
 * 2024/11/16 04:42
 */
class PersonalAccessTokenInsertFailedException(
    cause: ServiceException? = null,
    reason: String? = null,
) : ServiceException() {
    override val code: Int
        get() = 4000
    override var message: String = "PersonalAccessToken Insert Failed"

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
