package io.sakurasou.hoshizora.exception.service.strategy

import io.sakurasou.hoshizora.exception.ServiceException

/**
 * @author Shiina Kin
 * 2024/9/23 08:22
 */
class StrategyNotFoundException : ServiceException() {
    override val code: Int
        get() = 4004
    override val message: String
        get() = "Strategy Not Found"
}
