package io.sakurasou.hoshizora.exception.service.image

import io.sakurasou.hoshizora.exception.ServiceException

/**
 * @author Shiina Kin
 * 2024/10/9 13:58
 */
class ImageAccessDeniedException : ServiceException() {
    override val code: Int
        get() = 4003
    override val message: String
        get() = "Image Access Denied."
}
