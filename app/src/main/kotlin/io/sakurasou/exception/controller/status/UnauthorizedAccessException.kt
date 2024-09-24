package io.sakurasou.exception.controller.status

import io.sakurasou.exception.ServiceThrowable

/**
 * @author Shiina Kin
 * 2024/9/12 11:33
 */
class UnauthorizedAccessException : ServiceThrowable() {
    override val code: Int
        get() = 401
    override val message: String
        get() = "Unauthorized Access"
}