package io.sakurasou.hoshizora.exception.service.permission

import io.sakurasou.hoshizora.exception.ServiceException

/**
 * @author ShiinaKin
 * 2024/9/18 17:40
 */
class PermissionNotFoundException : ServiceException() {
    override val code: Int
        get() = 4004
    override val message: String
        get() = "Permission Not Found"
}
