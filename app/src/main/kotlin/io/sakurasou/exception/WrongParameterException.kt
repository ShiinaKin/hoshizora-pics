package io.sakurasou.exception

/**
 * @author Shiina Kin
 * 2024/9/9 10:38
 */
class WrongParameterException : ServiceThrowable() {
    override val code: Int
        get() = 4000
    override val message: String
        get() = "Wrong parameter"
}