package io.sakurasou.hoshizora.exception.service.user

import io.sakurasou.hoshizora.exception.ServiceThrowable

/**
 * @author Shiina Kin
 * 2024/9/25 12:44
 */
class UserUpdateFailedException(
    cause: ServiceThrowable? = null,
) : ServiceThrowable() {
    override val code: Int
        get() = 4000
    override var message: String = "User Update Failed"

    init {
        message = (message + (cause?.let { ", " + it.message } ?: ""))
    }
}
