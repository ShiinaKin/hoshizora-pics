package io.sakurasou.hoshizora.exception.service.image

import io.sakurasou.hoshizora.exception.ServiceThrowable

/**
 * @author Shiina Kin
 * 2024/10/9 13:58
 */
class ImageAccessDeniedException : ServiceThrowable() {
    override val code: Int
        get() = 4003
    override val message: String
        get() = "Image Access Denied."
}
