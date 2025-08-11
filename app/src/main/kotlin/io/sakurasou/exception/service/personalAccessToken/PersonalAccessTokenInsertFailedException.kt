package io.sakurasou.exception.service.personalAccessToken

import io.sakurasou.exception.ServiceThrowable

/**
 * @author ShiinaKin
 * 2024/11/16 04:42
 */
class PersonalAccessTokenInsertFailedException(
    cause: ServiceThrowable? = null,
    reason: String? = null,
) : ServiceThrowable() {
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
