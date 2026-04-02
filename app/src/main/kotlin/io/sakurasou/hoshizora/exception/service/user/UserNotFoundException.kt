package io.sakurasou.hoshizora.exception.service.user

import io.sakurasou.hoshizora.exception.ServiceException

/**
 * @author Shiina Kin
 * 2024/9/12 12:57
 */
class UserNotFoundException : ServiceException() {
    override val code: Int
        get() = 404
    override val message: String
        get() = "User Not Found"
}
