package io.sakurasou.exception.service.album

import io.sakurasou.exception.ServiceThrowable

/**
 * @author Shiina Kin
 * 2024/9/25 12:44
 */
class AlbumUpdateFailedException(
    cause: ServiceThrowable? = null
) : ServiceThrowable() {
    override val code: Int
        get() = 4000
    override var message: String = "Album Update Failed"

    init {
        message = (message + (cause?.let { ", " + it.message } ?: ""))
    }
}