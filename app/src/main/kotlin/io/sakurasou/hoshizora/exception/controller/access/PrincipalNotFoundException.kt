package io.sakurasou.hoshizora.exception.controller.access

import io.sakurasou.hoshizora.exception.ServiceException

/**
 * @author Shiina Kin
 * 2024/9/12 11:08
 */
class PrincipalNotFoundException : ServiceException() {
    override val code: Int
        get() = 5001
    override val message: String
        get() = "Principal Not Found"
}
