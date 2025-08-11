package io.sakurasou.exception.service.image.io.webdav

import io.sakurasou.exception.ServiceThrowable

/**
 * @author Shiina Kin
 * 2025/2/24 13:07
 */
class WebDavException(
    reason: String? = null,
) : ServiceThrowable() {
    override val code: Int
        get() = 5000
    override var message: String = "WebDav Ops Failed"

    init {
        message += if (reason != null) ", $reason" else ""
    }
}
