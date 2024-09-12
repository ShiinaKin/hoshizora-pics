package io.sakurasou.exception

/**
 * @author Shiina Kin
 * 2024/9/12 11:33
 */
class UnauthorizedAccessException : Throwable() {
    override val message: String
        get() = "Unauthorized Access"
}