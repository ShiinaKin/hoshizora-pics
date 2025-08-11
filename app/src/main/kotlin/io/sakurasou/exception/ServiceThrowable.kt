package io.sakurasou.exception

/**
 * @author Shiina Kin
 * 2024/9/13 14:40
 */
abstract class ServiceThrowable : Throwable() {
    abstract val code: Int
    abstract override val message: String
}
