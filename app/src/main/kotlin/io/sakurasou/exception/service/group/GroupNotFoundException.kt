package io.sakurasou.exception.service.group

import io.sakurasou.exception.ServiceThrowable

/**
 * @author Shiina Kin
 * 2024/9/24 13:59
 */
class GroupNotFoundException : ServiceThrowable() {
    override val code: Int
        get() = 4004
    override val message: String
        get() = "Group Not Found"
}
