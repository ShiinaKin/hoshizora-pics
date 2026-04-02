package io.sakurasou.hoshizora.exception.service.user

import io.sakurasou.hoshizora.exception.ServiceException

/**
 * @author Shiina Kin
 * 2024/9/25 12:44
 */
class UserDeleteFailedException(
    cause: ServiceException? = null,
) : ServiceException() {
    override val code: Int
        get() = 4000
    override var message: String = "User Delete Failed"

    init {
        message = (message + (cause?.let { ", " + it.message } ?: ""))
    }
}
