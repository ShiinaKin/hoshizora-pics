package io.sakurasou.hoshizora.exception.service.album

import io.sakurasou.hoshizora.exception.ServiceException

/**
 * @author Shiina Kin
 * 2024/9/12 12:57
 */
class AlbumNotFoundException : ServiceException() {
    override val code: Int
        get() = 4004
    override val message: String
        get() = "Album Not Found"
}
