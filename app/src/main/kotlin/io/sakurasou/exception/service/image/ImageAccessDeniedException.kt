package io.sakurasou.exception.service.image

import io.sakurasou.exception.ServiceThrowable

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