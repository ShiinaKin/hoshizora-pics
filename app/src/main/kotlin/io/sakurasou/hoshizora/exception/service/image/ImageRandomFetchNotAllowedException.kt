package io.sakurasou.hoshizora.exception.service.image

import io.sakurasou.hoshizora.exception.ServiceException

/**
 * @author Shiina Kin
 * 2026/4/19 00:00
 */
class ImageRandomFetchNotAllowedException : ServiceException() {
    override val code: Int
        get() = 4005
    override val message: String
        get() = "Private images cannot be fetched randomly."
}
