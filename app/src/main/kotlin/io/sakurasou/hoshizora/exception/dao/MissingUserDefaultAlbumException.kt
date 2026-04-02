package io.sakurasou.hoshizora.exception.dao

import io.sakurasou.hoshizora.exception.ServiceException

/**
 * @author Shiina Kin
 * 2024/10/9 13:45
 */
class MissingUserDefaultAlbumException(
    private val userId: Long,
) : ServiceException() {
    override val code: Int
        get() = 5002
    override val message: String
        get() = "UserId: $userId Missing default album"
}
