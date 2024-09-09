package io.sakurasou.exception

/**
 * @author Shiina Kin
 * 2024/9/9 10:38
 */
class WrongParameterException: Throwable() {
    override val message: String
        get() = "Wrong parameter"
}