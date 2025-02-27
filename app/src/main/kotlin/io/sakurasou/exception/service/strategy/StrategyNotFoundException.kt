package io.sakurasou.exception.service.strategy

import io.sakurasou.exception.ServiceThrowable

/**
 * @author Shiina Kin
 * 2024/9/23 08:22
 */
class StrategyNotFoundException : ServiceThrowable() {
    override val code: Int
        get() = 4004
    override val message: String
        get() = "Strategy Not Found"
}