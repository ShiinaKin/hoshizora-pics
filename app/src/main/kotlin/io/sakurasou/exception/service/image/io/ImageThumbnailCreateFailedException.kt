package io.sakurasou.exception.service.image.io

import io.sakurasou.exception.ServiceThrowable

/**
 * @author Shiina Kin
 * 2024/10/9 13:58
 */
class ImageThumbnailCreateFailedException : ServiceThrowable() {
    override val code: Int
        get() = 5000
    override val message: String
        get() = "Failed to create image thumbnail"
}