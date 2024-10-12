package io.sakurasou.exception.common

import io.sakurasou.exception.ServiceThrowable

/**
 * @author Shiina Kin
 * 2024/9/9 10:38
 */
class FileExtensionNotAllowedException : ServiceThrowable() {
    override val code: Int
        get() = 4001
    override val message: String
        get() = "File extension not allowed"
}