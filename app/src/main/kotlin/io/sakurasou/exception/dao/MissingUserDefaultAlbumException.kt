package io.sakurasou.exception.dao

import io.sakurasou.exception.ServiceThrowable

/**
 * @author Shiina Kin
 * 2024/10/9 13:45
 */
class MissingUserDefaultAlbumException(
    private val userId: Long,
) : ServiceThrowable() {
    override val code: Int
        get() = 5002
    override val message: String
        get() = "UserId: $userId Missing default album"
}
