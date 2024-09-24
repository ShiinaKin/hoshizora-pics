package io.sakurasou.exception.service.permission

import io.sakurasou.exception.ServiceThrowable

/**
 * @author ShiinaKin
 * 2024/9/18 17:40
 */
class PermissionNotFoundException: ServiceThrowable() {
    override val code: Int
        get() = 4004
    override val message: String
        get() = "Permission Not Found"
}