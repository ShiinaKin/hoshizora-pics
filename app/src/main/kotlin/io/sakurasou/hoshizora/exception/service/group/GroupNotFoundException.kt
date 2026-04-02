package io.sakurasou.hoshizora.exception.service.group

import io.sakurasou.hoshizora.exception.ServiceException

/**
 * @author Shiina Kin
 * 2024/9/24 13:59
 */
class GroupNotFoundException : ServiceException() {
    override val code: Int
        get() = 4004
    override val message: String
        get() = "Group Not Found"
}
