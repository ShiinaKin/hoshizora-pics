package io.sakurasou.exception

/**
 * @author Shiina Kin
 * 2024/9/12 11:08
 */
class PrincipalNotExistException : Throwable() {
    override val message: String
        get() = "Principal not exist"
}