package io.sakurasou.exception.service.strategy

import io.sakurasou.exception.ServiceThrowable

/**
 * @author Shiina Kin
 * 2024/9/25 12:44
 */
class StrategyInsertFailedException(
    cause: ServiceThrowable? = null,
    reason: String? = null
) : ServiceThrowable() {
    override val code: Int
        get() = 4000
    override var message: String = "Strategy Insert Failed"

    init {
        message += if (cause != null) ", ${cause.message}" else if (reason != null) ", $reason" else ""
    }
}