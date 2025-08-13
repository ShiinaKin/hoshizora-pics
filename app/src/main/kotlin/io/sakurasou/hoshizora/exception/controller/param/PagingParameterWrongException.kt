package io.sakurasou.hoshizora.exception.controller.param

import io.sakurasou.hoshizora.exception.ServiceThrowable

/**
 * @author Shiina Kin
 * 2024/9/23 10:31
 */
class PagingParameterWrongException(
    override val message: String = "Paging Parameter Wrong",
) : ServiceThrowable() {
    override val code: Int
        get() = 4000
}
