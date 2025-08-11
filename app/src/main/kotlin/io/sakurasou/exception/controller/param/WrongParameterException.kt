package io.sakurasou.exception.controller.param

import io.sakurasou.exception.ServiceThrowable

/**
 * @author Shiina Kin
 * 2024/9/9 10:38
 */
class WrongParameterException(
    msg: String? = null,
) : ServiceThrowable() {
    override val code: Int
        get() = 4000
    override var message: String = "Wrong parameter"

    init {
        msg?.let { message += ", $it" }
    }
}
