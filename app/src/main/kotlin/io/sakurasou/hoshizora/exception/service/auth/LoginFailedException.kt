package io.sakurasou.hoshizora.exception.service.auth

import io.sakurasou.hoshizora.exception.ServiceThrowable

/**
 * @author ShiinaKin
 * 2024/12/8 04:05
 */
class LoginFailedException : ServiceThrowable() {
    override val code: Int
        get() = 401
    override val message: String
        get() = "Login Failed, please check your username and password"
}
