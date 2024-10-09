package io.sakurasou.exception.service.album

import io.sakurasou.exception.ServiceThrowable

/**
 * @author Shiina Kin
 * 2024/10/9 13:58
 */
class AlbumAccessDeniedException : ServiceThrowable() {
    override val code: Int
        get() = 400
    override val message: String
        get() = "Album Access Denied. This album is not yours."
}