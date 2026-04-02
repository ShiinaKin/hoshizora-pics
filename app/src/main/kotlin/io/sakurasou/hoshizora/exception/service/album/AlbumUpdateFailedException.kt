package io.sakurasou.hoshizora.exception.service.album

import io.sakurasou.hoshizora.exception.ServiceException

/**
 * @author Shiina Kin
 * 2024/9/25 12:44
 */
class AlbumUpdateFailedException(
    cause: ServiceException? = null,
) : ServiceException() {
    override val code: Int
        get() = 4000
    override var message: String = "Album Update Failed"

    init {
        message = (message + (cause?.let { ", " + it.message } ?: ""))
    }
}
