package io.sakurasou.hoshizora.exception.common

import io.sakurasou.hoshizora.exception.ServiceException

/**
 * @author Shiina Kin
 * 2024/9/9 10:38
 */
class FileExtensionNotAllowedException : ServiceException() {
    override val code: Int
        get() = 4001
    override val message: String
        get() = "File extension not allowed"
}
