package io.sakurasou.exception.service.album

import io.sakurasou.exception.ServiceThrowable

/**
 * @author Shiina Kin
 * 2024/9/12 12:57
 */
class AlbumNotFoundException : ServiceThrowable() {
    override val code: Int
        get() = 4004
    override val message: String
        get() = "Album Not Found"
}
