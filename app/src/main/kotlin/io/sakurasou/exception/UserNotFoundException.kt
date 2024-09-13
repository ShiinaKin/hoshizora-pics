package io.sakurasou.exception

/**
 * @author Shiina Kin
 * 2024/9/12 12:57
 */
class UserNotFoundException : ServiceThrowable() {
    override val code: Int
        get() = 404
    override val message: String
        get() = "User not found"
}