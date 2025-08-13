package io.sakurasou.hoshizora.exception.common

import io.sakurasou.hoshizora.exception.ServiceThrowable

/**
 * @author Shiina Kin
 * 2024/9/25 16:58
 */
class UserBannedException : ServiceThrowable() {
    override val code: Int
        get() = 4003
    override val message: String
        get() = "You have been banned"
}
