package io.sakurasou.hoshizora.exception.controller.status

import io.sakurasou.hoshizora.exception.ServiceException

/**
 * @author Shiina Kin
 * 2024/9/12 11:33
 */
class UnauthorizedAccessException : ServiceException() {
    override val code: Int
        get() = 401
    override val message: String
        get() = "Unauthorized Access"
}
