package io.sakurasou.exception

/**
 * @author ShiinaKin
 * 2024/9/18 17:40
 */
class RoleNotExistException: ServiceThrowable() {
    override val code: Int
        get() = 4004
    override val message: String
        get() = "Role Not Exist"
}