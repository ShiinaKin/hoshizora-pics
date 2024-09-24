package io.sakurasou.exception

/**
 * @author Shiina Kin
 * 2024/9/24 13:59
 */
class GroupNotExistException : ServiceThrowable() {
    override val code: Int
        get() = 4004
    override val message: String
        get() = "Group Not Exist"
}