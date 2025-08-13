package io.sakurasou.hoshizora.exception.controller.access

import io.sakurasou.hoshizora.exception.ServiceThrowable

/**
 * @author Shiina Kin
 * 2024/9/12 11:08
 */
class PrincipalNotFoundException : ServiceThrowable() {
    override val code: Int
        get() = 5001
    override val message: String
        get() = "Principal Not Found"
}
