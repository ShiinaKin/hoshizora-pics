package io.sakurasou.exception

/**
 * @author Shiina Kin
 * 2024/9/14 08:30
 */
class SignupNotAllowedException : ServiceThrowable() {
    override val code: Int
        get() = 4001
    override val message: String
        get() = "Signup is not allowed"
}