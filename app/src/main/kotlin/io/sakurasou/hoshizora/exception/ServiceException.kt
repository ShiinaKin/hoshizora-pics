package io.sakurasou.hoshizora.exception

/**
 * @author Shiina Kin
 * 2024/9/13 14:40
 */
abstract class ServiceException : RuntimeException() {
    abstract val code: Int
    abstract override val message: String
}
