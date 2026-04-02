package io.sakurasou.hoshizora.exception.controller.param

import io.sakurasou.hoshizora.exception.ServiceException

/**
 * @author Shiina Kin
 * 2024/9/9 10:38
 */
class WrongParameterException(
    msg: String? = null,
) : ServiceException() {
    override val code: Int
        get() = 4000
    override var message: String = "Wrong parameter"

    init {
        msg?.let { message += ", $it" }
    }
}
