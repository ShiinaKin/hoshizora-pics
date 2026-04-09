package io.sakurasou.hoshizora.exception.native

import io.sakurasou.hoshizora.exception.ServiceException

/**
 * @author Shiina Kin
 * 2026/4/9 22:00
 */
class ImageOperationException(
    override val message: String,
) : ServiceException() {
    override val code: Int
        get() = 9001
}
