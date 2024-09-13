package io.sakurasou.exception

/**
 * @author Shiina Kin
 * 2024/9/12 11:08
 */
class PrincipalNotExistException : ServiceThrowable() {
    override val code: Int
        get() = 5001
    override val message: String
        get() = "Principal not exist"
}