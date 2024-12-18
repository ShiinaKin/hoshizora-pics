package io.sakurasou.exception.service.image.io

import io.sakurasou.exception.ServiceThrowable

/**
 * @author Shiina Kin
 * 2024/9/12 12:57
 */
class ImageThumbnailNotFoundException : ServiceThrowable() {
    override val code: Int
        get() = 4004
    override val message: String
        get() = "Image thumbnail not found, will be generated later"
}