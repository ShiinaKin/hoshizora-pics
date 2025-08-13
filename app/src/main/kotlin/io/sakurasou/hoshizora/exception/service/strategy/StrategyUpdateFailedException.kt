package io.sakurasou.hoshizora.exception.service.strategy

import io.sakurasou.hoshizora.exception.ServiceThrowable

/**
 * @author Shiina Kin
 * 2024/9/25 12:44
 */
class StrategyUpdateFailedException(
    cause: ServiceThrowable? = null,
) : ServiceThrowable() {
    override val code: Int
        get() = 4000
    override var message: String = "Strategy Update Failed"

    init {
        message = (message + (cause?.let { ", " + it.message } ?: ""))
    }
}
