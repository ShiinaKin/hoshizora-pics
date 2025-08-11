package io.sakurasou.exception.service.image.io

import io.sakurasou.exception.ServiceThrowable

/**
 * @author Shiina Kin
 * 2024/9/12 12:57
 */
class ImageFileNotFoundException : ServiceThrowable() {
    override val code: Int
        get() = 4004
    override val message: String
        get() = "Image File Not Found"
}
