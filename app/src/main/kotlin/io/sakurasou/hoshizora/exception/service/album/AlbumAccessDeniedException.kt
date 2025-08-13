package io.sakurasou.hoshizora.exception.service.album

import io.sakurasou.hoshizora.exception.ServiceThrowable

/**
 * @author Shiina Kin
 * 2024/10/9 13:58
 */
class AlbumAccessDeniedException : ServiceThrowable() {
    override val code: Int
        get() = 4003
    override val message: String
        get() = "Album Access Denied. This album is not yours."
}
