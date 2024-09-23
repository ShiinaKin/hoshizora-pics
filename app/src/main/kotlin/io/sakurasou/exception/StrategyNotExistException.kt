package io.sakurasou.exception

/**
 * @author Shiina Kin
 * 2024/9/23 08:22
 */
class StrategyNotExistException : ServiceThrowable() {
    override val code: Int
        get() = 4004
    override val message: String
        get() = "Strategy Not Exist"
}