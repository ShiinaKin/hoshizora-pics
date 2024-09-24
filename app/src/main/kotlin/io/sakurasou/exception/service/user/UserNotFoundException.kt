package io.sakurasou.exception.service.user

import io.sakurasou.exception.ServiceThrowable

/**
 * @author Shiina Kin
 * 2024/9/12 12:57
 */
class UserNotFoundException : ServiceThrowable() {
    override val code: Int
        get() = 404
    override val message: String
        get() = "User Not Found"
}